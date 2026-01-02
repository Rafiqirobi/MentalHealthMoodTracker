package com.example.mentalhealthtracker.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mentalhealthtracker.data.StressReliefPlace
import com.example.mentalhealthtracker.databinding.ActivityStressReliefPlacesBinding
import com.example.mentalhealthtracker.ui.adapters.StressReliefPlaceAdapter
import com.example.mentalhealthtracker.utils.LocationHelper
import kotlinx.coroutines.launch

class StressReliefPlacesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStressReliefPlacesBinding
    private lateinit var locationHelper: LocationHelper
    private lateinit var adapter: StressReliefPlaceAdapter

    companion object {
        private const val TAG = "StressReliefPlaces"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStressReliefPlacesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }

        locationHelper = LocationHelper(this)

        setupRecyclerView()
        checkPermissionsAndLoadPlaces()
    }

    private fun setupRecyclerView() {
        adapter = StressReliefPlaceAdapter { place ->
            openDirections(place)
        }

        binding.placesRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.placesRecyclerView.adapter = adapter
    }

    private fun checkPermissionsAndLoadPlaces() {
        Log.d(TAG, "Checking permissions and loading places")

        when {
            !locationHelper.hasLocationPermission() -> {
                Log.d(TAG, "Location permission not granted")
                showPermissionRequired()
            }
            !locationHelper.isLocationEnabled() -> {
                Log.d(TAG, "Location services disabled")
                showLocationServicesDisabled()
            }
            else -> {
                Log.d(TAG, "Permissions OK, loading places")
                loadStressReliefPlaces()
            }
        }
    }

    private fun showPermissionRequired() {
        binding.loadingLayout.visibility = View.GONE
        binding.placesRecyclerView.visibility = View.GONE
        binding.emptyStateLayout.visibility = View.VISIBLE

        AlertDialog.Builder(this)
            .setTitle("Location Permission Required")
            .setMessage("This app needs location permission to find stress relief places near you. Your location data is only used locally and never shared.")
            .setPositiveButton("Grant Permission") { _, _ ->
                requestLocationPermission()
            }
            .setNegativeButton("Cancel") { _, _ ->
                finish()
            }
            .show()

        binding.retryButton.setOnClickListener {
            requestLocationPermission()
        }
    }

    private fun showLocationServicesDisabled() {
        binding.loadingLayout.visibility = View.GONE
        binding.placesRecyclerView.visibility = View.GONE
        binding.emptyStateLayout.visibility = View.VISIBLE

        AlertDialog.Builder(this)
            .setTitle("Location Services Disabled")
            .setMessage("Please enable location services in your device settings to find nearby stress relief places.")
            .setPositiveButton("Open Settings") { _, _ ->
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
            .setNegativeButton("Cancel") { _, _ ->
                finish()
            }
            .show()

        binding.retryButton.setOnClickListener {
            if (locationHelper.isLocationEnabled()) {
                loadStressReliefPlaces()
            } else {
                Toast.makeText(this, "Location services still disabled", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Show rationale
            AlertDialog.Builder(this)
                .setTitle("Location Permission Needed")
                .setMessage("This app needs location access to show you nearby parks, gardens, and peaceful places that can help with stress relief.")
                .setPositiveButton("OK") { _, _ ->
                    ActivityCompat.requestPermissions(
                        this,
                        LocationHelper.REQUIRED_PERMISSIONS,
                        LocationHelper.LOCATION_PERMISSION_REQUEST_CODE
                    )
                }
                .setNegativeButton("Cancel", null)
                .show()
        } else {
            ActivityCompat.requestPermissions(
                this,
                LocationHelper.REQUIRED_PERMISSIONS,
                LocationHelper.LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun loadStressReliefPlaces() {
        Log.d(TAG, "Starting to load stress relief places")
        showLoading()

        lifecycleScope.launch {
            try {
                Log.d(TAG, "Getting current location...")
                val location = locationHelper.getCurrentLocation()

                if (location != null) {
                    Log.d(TAG, "Location obtained: ${location.latitude}, ${location.longitude}")

                    // Get mock places (in production, you'd use Google Places API)
                    val places = locationHelper.getMockStressReliefPlaces(
                        location.latitude,
                        location.longitude
                    )

                    Log.d(TAG, "Found ${places.size} places")

                    if (places.isEmpty()) {
                        showEmptyState()
                    } else {
                        showPlaces(places)
                    }
                } else {
                    Log.e(TAG, "Failed to get location")
                    showError("Unable to get your location. Please make sure location services are enabled.")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading places", e)
                showError("Error loading places: ${e.message}")
            }
        }
    }

    private fun showLoading() {
        binding.loadingLayout.visibility = View.VISIBLE
        binding.placesRecyclerView.visibility = View.GONE
        binding.emptyStateLayout.visibility = View.GONE
    }

    private fun showPlaces(places: List<StressReliefPlace>) {
        binding.loadingLayout.visibility = View.GONE
        binding.placesRecyclerView.visibility = View.VISIBLE
        binding.emptyStateLayout.visibility = View.GONE

        adapter.submitList(places)
        Toast.makeText(this, "Found ${places.size} places nearby", Toast.LENGTH_SHORT).show()
    }

    private fun showEmptyState() {
        binding.loadingLayout.visibility = View.GONE
        binding.placesRecyclerView.visibility = View.GONE
        binding.emptyStateLayout.visibility = View.VISIBLE

        binding.retryButton.setOnClickListener {
            checkPermissionsAndLoadPlaces()
        }
    }

    private fun showError(message: String) {
        binding.loadingLayout.visibility = View.GONE
        binding.placesRecyclerView.visibility = View.GONE
        binding.emptyStateLayout.visibility = View.VISIBLE

        Toast.makeText(this, message, Toast.LENGTH_LONG).show()

        binding.retryButton.setOnClickListener {
            checkPermissionsAndLoadPlaces()
        }
    }

    private fun openDirections(place: StressReliefPlace) {
        try {
            // Open Google Maps with directions
            val uri = Uri.parse(
                "https://www.google.com/maps/dir/?api=1&destination=${place.latitude},${place.longitude}"
            )
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.setPackage("com.google.android.apps.maps")

            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            } else {
                // Google Maps not installed, open in browser
                val browserIntent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(browserIntent)
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error opening directions: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == LocationHelper.LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Location permission granted")
                Toast.makeText(this, "Permission granted! Loading places...", Toast.LENGTH_SHORT).show()
                checkPermissionsAndLoadPlaces()
            } else {
                Log.d(TAG, "Location permission denied")

                // Check if user selected "Don't ask again"
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    // User selected "Don't ask again", show dialog to go to settings
                    AlertDialog.Builder(this)
                        .setTitle("Permission Required")
                        .setMessage("You've denied location permission. To use this feature, please enable location permission in app settings.")
                        .setPositiveButton("Open Settings") { _, _ ->
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            val uri = Uri.fromParts("package", packageName, null)
                            intent.data = uri
                            startActivity(intent)
                        }
                        .setNegativeButton("Cancel") { _, _ ->
                            finish()
                        }
                        .show()
                } else {
                    Toast.makeText(
                        this,
                        "Location permission is required to find nearby places",
                        Toast.LENGTH_LONG
                    ).show()
                    showEmptyState()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Check if location was enabled while in settings
        if (locationHelper.hasLocationPermission() && locationHelper.isLocationEnabled()) {
            if (adapter.itemCount == 0) {
                loadStressReliefPlaces()
            }
        }
    }
}