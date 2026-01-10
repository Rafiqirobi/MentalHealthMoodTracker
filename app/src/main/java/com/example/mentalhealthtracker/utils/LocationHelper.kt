package com.example.mentalhealthtracker.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Looper
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.example.mentalhealthtracker.data.PlaceType
import com.example.mentalhealthtracker.data.StressReliefPlace
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.IOException
import java.util.*
import kotlin.coroutines.resume

/**
 * Helper class for location-related operations
 * Handles GPS, location permissions, and geocoding
 */
class LocationHelper(private val context: Context) {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private val locationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    /**
     * Check if location services are enabled
     */
    fun isLocationEnabled(): Boolean {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    /**
     * Check if location permission is granted
     */
    fun hasLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Get current location
     * @return Current location or null if unavailable
     */
    suspend fun getCurrentLocation(): Location? = suspendCancellableCoroutine { continuation ->
        if (!hasLocationPermission()) {
            continuation.resume(null)
            return@suspendCancellableCoroutine
        }

        if (!isLocationEnabled()) {
            continuation.resume(null)
            return@suspendCancellableCoroutine
        }

        try {
            val locationRequest = LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                10000L // 10 seconds
            ).apply {
                setWaitForAccurateLocation(false)
                setMaxUpdateDelayMillis(5000L) // 5 seconds
                setMinUpdateIntervalMillis(5000L)
            }.build()

            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    fusedLocationClient.removeLocationUpdates(this)
                    val location = locationResult.lastLocation
                    if (continuation.isActive) {
                        continuation.resume(location)
                    }
                }
            }

            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )

            // Fallback to last known location if no update within timeout
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (continuation.isActive && location != null) {
                    continuation.resume(location)
                }
            }.addOnFailureListener {
                if (continuation.isActive) {
                    continuation.resume(null)
                }
            }

            // Cancel location updates if coroutine is cancelled
            continuation.invokeOnCancellation {
                fusedLocationClient.removeLocationUpdates(locationCallback)
            }

        } catch (e: SecurityException) {
            continuation.resume(null)
        } catch (e: Exception) {
            continuation.resume(null)
        }
    }

    /**
     * Get address from coordinates using Geocoder
     * @param latitude Latitude coordinate
     * @param longitude Longitude coordinate
     * @return Address string or null if unavailable
     */
    suspend fun getAddressFromLocation(latitude: Double, longitude: Double): String? {
        return try {
            val geocoder = Geocoder(context, Locale.getDefault())

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // For Android 13 and above
                suspendCancellableCoroutine { continuation ->
                    geocoder.getFromLocation(
                        latitude,
                        longitude,
                        1
                    ) { addresses ->
                        val address = addresses.firstOrNull()?.let { formatAddress(it) }
                        continuation.resume(address)
                    }
                }
            } else {
                // For older Android versions
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                addresses?.firstOrNull()?.let { formatAddress(it) }
            }
        } catch (e: IOException) {
            null
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Format address object to readable string
     */
    private fun formatAddress(address: Address): String {
        val parts = mutableListOf<String>()

        // Add street address
        if (address.thoroughfare != null) {
            parts.add(address.thoroughfare)
        }

        // Add city
        if (address.locality != null) {
            parts.add(address.locality)
        } else if (address.subAdminArea != null) {
            parts.add(address.subAdminArea)
        }

        // Add state/province
        if (address.adminArea != null) {
            parts.add(address.adminArea)
        }

        return if (parts.isNotEmpty()) {
            parts.joinToString(", ")
        } else {
            "Unknown location"
        }
    }

    /**
     * Calculate distance between two locations in meters
     * @return Distance in meters
     */
    fun calculateDistance(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double
    ): Float {
        val results = FloatArray(1)
        Location.distanceBetween(lat1, lon1, lat2, lon2, results)
        return results[0]
    }

    /**
     * Format distance to human-readable string
     * @param distanceInMeters Distance in meters
     * @return Formatted distance string
     */
    fun formatDistance(distanceInMeters: Float): String {
        return when {
            distanceInMeters < 1000 -> {
                "${distanceInMeters.toInt()} m"
            }
            else -> {
                val km = distanceInMeters / 1000
                "%.1f km".format(km)
            }
        }
    }

    /**
     * Create mock stress relief places for testing
     * In production, this would use Google Places API
     */
    fun getMockStressReliefPlaces(
        currentLatitude: Double,
        currentLongitude: Double
    ): List<StressReliefPlace> {
        // Mock places near the current location
        return listOf(
            StressReliefPlace(
                name = "Central Park",
                type = PlaceType.PARK,
                latitude = currentLatitude + 0.01,
                longitude = currentLongitude + 0.01,
                distance = calculateDistance(
                    currentLatitude,
                    currentLongitude,
                    currentLatitude + 0.01,
                    currentLongitude + 0.01
                ),
                address = "123 Park Avenue",
                rating = 4.5f,
                isOpen = true
            ),
            StressReliefPlace(
                name = "Peaceful Gardens",
                type = PlaceType.GARDEN,
                latitude = currentLatitude - 0.02,
                longitude = currentLongitude + 0.015,
                distance = calculateDistance(
                    currentLatitude,
                    currentLongitude,
                    currentLatitude - 0.02,
                    currentLongitude + 0.015
                ),
                address = "456 Garden Street",
                rating = 4.8f,
                isOpen = true
            ),
            StressReliefPlace(
                name = "Quiet Corner Cafe",
                type = PlaceType.CAFE,
                latitude = currentLatitude + 0.005,
                longitude = currentLongitude - 0.008,
                distance = calculateDistance(
                    currentLatitude,
                    currentLongitude,
                    currentLatitude + 0.005,
                    currentLongitude - 0.008
                ),
                address = "789 Main Street",
                rating = 4.3f,
                isOpen = true
            ),
            StressReliefPlace(
                name = "City Library",
                type = PlaceType.LIBRARY,
                latitude = currentLatitude - 0.01,
                longitude = currentLongitude - 0.01,
                distance = calculateDistance(
                    currentLatitude,
                    currentLongitude,
                    currentLatitude - 0.01,
                    currentLongitude - 0.01
                ),
                address = "321 Book Lane",
                rating = 4.6f,
                isOpen = true
            ),
            StressReliefPlace(
                name = "Nature Trail Head",
                type = PlaceType.HIKING_TRAIL,
                latitude = currentLatitude + 0.03,
                longitude = currentLongitude - 0.02,
                distance = calculateDistance(
                    currentLatitude,
                    currentLongitude,
                    currentLatitude + 0.03,
                    currentLongitude - 0.02
                ),
                address = "555 Trail Road",
                rating = 4.7f,
                isOpen = true
            )
        ).sortedBy { it.distance }
    }

    /**
     * Check if coordinates are valid
     */
    fun isValidCoordinate(latitude: Double, longitude: Double): Boolean {
        return latitude in -90.0..90.0 && longitude in -180.0..180.0
    }

    companion object {
        const val LOCATION_PERMISSION_REQUEST_CODE = 1001

        /**
         * Array of required location permissions
         */
        val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }
}