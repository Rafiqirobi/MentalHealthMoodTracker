package com.example.mentalhealthtracker.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.mentalhealthtracker.R
import com.example.mentalhealthtracker.data.MoodEntry
import com.example.mentalhealthtracker.data.MoodLevel
import com.example.mentalhealthtracker.databinding.ActivityAddMoodBinding
import com.example.mentalhealthtracker.utils.LocationHelper
import com.example.mentalhealthtracker.utils.PreferencesManager
import com.example.mentalhealthtracker.viewmodel.MoodViewModel
import kotlinx.coroutines.launch

class AddMoodActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddMoodBinding
    private lateinit var viewModel: MoodViewModel
    private lateinit var locationHelper: LocationHelper
    private lateinit var preferencesManager: PreferencesManager

    private var selectedMoodLevel: Int? = null
    private var currentLatitude: Double? = null
    private var currentLongitude: Double? = null
    private var locationName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddMoodBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }

        // Initialize
        viewModel = ViewModelProvider(this)[MoodViewModel::class.java]
        locationHelper = LocationHelper(this)
        preferencesManager = PreferencesManager(this)

        setupMoodSelection()
        setupSaveButton()
        setupLocation()
        observeViewModel()
    }

    private fun setupMoodSelection() {
        val moodContainers = listOf(
            binding.mood1,
            binding.mood2,
            binding.mood3,
            binding.mood4,
            binding.mood5
        )

        moodContainers.forEachIndexed { index, container ->
            container.setOnClickListener {
                selectMood(index + 1, moodContainers)
            }
        }
    }

    private fun selectMood(level: Int, containers: List<View>) {
        selectedMoodLevel = level

        // Update UI for all mood containers
        containers.forEachIndexed { index, container ->
            if (index + 1 == level) {
                container.setBackgroundResource(R.drawable.bg_mood_selected)
            } else {
                container.setBackgroundResource(R.drawable.bg_mood_unselected)
            }
        }

        // Enable save button
        binding.saveMoodButton.isEnabled = true
    }

    private fun setupSaveButton() {
        binding.saveMoodButton.setOnClickListener {
            saveMood()
        }
    }

    private fun setupLocation() {
        if (!preferencesManager.isLocationTrackingEnabled) {
            binding.locationTextView.text = getString(R.string.location_unavailable)
            return
        }

        if (!locationHelper.hasLocationPermission()) {
            requestLocationPermission()
            return
        }

        if (!locationHelper.isLocationEnabled()) {
            binding.locationTextView.text = "Location services disabled"
            return
        }

        getCurrentLocation()
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            LocationHelper.REQUIRED_PERMISSIONS,
            LocationHelper.LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    private fun getCurrentLocation() {
        binding.locationTextView.text = getString(R.string.getting_location)

        lifecycleScope.launch {
            try {
                val location = locationHelper.getCurrentLocation()

                if (location != null) {
                    currentLatitude = location.latitude
                    currentLongitude = location.longitude

                    // Get address
                    locationName = locationHelper.getAddressFromLocation(
                        location.latitude,
                        location.longitude
                    )

                    binding.locationTextView.text = "📍 ${locationName ?: "Location captured"}"
                } else {
                    binding.locationTextView.text = getString(R.string.location_unavailable)
                }
            } catch (e: Exception) {
                binding.locationTextView.text = getString(R.string.location_unavailable)
            }
        }
    }

    private fun saveMood() {
        val moodLevel = selectedMoodLevel
        if (moodLevel == null) {
            Toast.makeText(this, getString(R.string.please_select_mood), Toast.LENGTH_SHORT).show()
            return
        }

        val journal = binding.journalEditText.text?.toString()
        val triggers = binding.triggersEditText.text?.toString()

        val moodEntry = MoodEntry(
            moodLevel = moodLevel,
            moodEmoji = MoodLevel.fromValue(moodLevel).emoji,
            journalEntry = journal?.takeIf { it.isNotBlank() },
            triggers = triggers?.takeIf { it.isNotBlank() },
            latitude = currentLatitude,
            longitude = currentLongitude,
            locationName = locationName
        )

        viewModel.insertMood(moodEntry)
    }

    private fun observeViewModel() {
        viewModel.successMessage.observe(this) { message ->
            message?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                viewModel.clearSuccessMessage()
                finish() // Return to dashboard
            }
        }

        viewModel.errorMessage.observe(this) { message ->
            message?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                viewModel.clearErrorMessage()
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.saveMoodButton.isEnabled = !isLoading && selectedMoodLevel != null
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
                getCurrentLocation()
            } else {
                binding.locationTextView.text = getString(R.string.location_permission_denied)
            }
        }
    }
}