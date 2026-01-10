package com.example.mentalhealthtracker.utils

import android.content.Context
import android.util.Log
import com.example.mentalhealthtracker.data.PlaceType
import com.example.mentalhealthtracker.data.StressReliefPlace
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import kotlinx.coroutines.tasks.await

class PlacesHelper(context: Context) {

    private val placesClient by lazy {
        if (!Places.isInitialized()) {
            Places.initialize(context.applicationContext, getApiKey(context))
        }
        Places.createClient(context)
    }

    private val locationHelper = LocationHelper(context)

    companion object {
        private const val TAG = "PlacesHelper"
        private const val SEARCH_RADIUS_KM = 15.0 // Changed to 15km radius
    }

    private fun getApiKey(context: Context): String {
        return try {
            val appInfo = context.packageManager.getApplicationInfo(
                context.packageName,
                android.content.pm.PackageManager.GET_META_DATA
            )
            appInfo.metaData?.getString("com.google.android.geo.API_KEY") ?: ""
        } catch (e: Exception) {
            Log.e(TAG, "Error getting API key", e)
            ""
        }
    }

    /**
     * Search for nearby stress relief places using Places Autocomplete API
     */
    suspend fun simpleNearbySearch(
        latitude: Double,
        longitude: Double
    ): List<StressReliefPlace> {
        val places = mutableListOf<StressReliefPlace>()

        try {
            Log.d(TAG, "Starting search near: $latitude, $longitude with ${SEARCH_RADIUS_KM}km radius")

            // Simplified search keywords - most common stress relief places
            val searchKeywords = listOf(
                // Water Features
                "beach" to PlaceType.BEACH,
                "waterfall" to PlaceType.WATERFALL,
                "lake" to PlaceType.LAKE,
                "river" to PlaceType.RIVER,

                // Nature & Parks
                "park" to PlaceType.PARK,
                "garden" to PlaceType.GARDEN,
                "botanical garden" to PlaceType.BOTANICAL_GARDEN,
                "hiking trail" to PlaceType.HIKING_TRAIL,
                "mountain" to PlaceType.MOUNTAIN,
                "forest" to PlaceType.FOREST,
                "scenic viewpoint" to PlaceType.SCENIC_VIEW,
                "lookout point" to PlaceType.SCENIC_VIEW,

                // Countryside
                "orchard" to PlaceType.ORCHARD,
                "farm" to PlaceType.FARM,

                // Urban Relaxation
                "cafe" to PlaceType.CAFE,
                "coffee shop" to PlaceType.CAFE,
                "library" to PlaceType.LIBRARY,
                "museum" to PlaceType.MUSEUM,
                "shopping mall" to PlaceType.SHOPPING_MALL,
                "shopping center" to PlaceType.SHOPPING_MALL
            )

            // Create bounds for search area (approximately 15km radius)
            val offset = 0.135 // Roughly 15km in degrees
            val bounds = RectangularBounds.newInstance(
                LatLng(latitude - offset, longitude - offset),
                LatLng(latitude + offset, longitude + offset)
            )

            // Search for all keywords
            for ((keyword, placeType) in searchKeywords) {
                try {
                    val foundPlaces = searchByKeyword(
                        keyword = keyword,
                        placeType = placeType,
                        userLat = latitude,
                        userLon = longitude,
                        bounds = bounds
                    )
                    places.addAll(foundPlaces)

                    Log.d(TAG, "Found ${foundPlaces.size} places for keyword: $keyword")
                } catch (e: Exception) {
                    Log.e(TAG, "Error searching for $keyword", e)
                }
            }

            // Remove duplicates and sort by distance
            val uniquePlaces = places
                .distinctBy { it.name }
                .sortedBy { it.distance }
                .take(40)

            Log.d(TAG, "Total unique places found: ${uniquePlaces.size}")

            return if (uniquePlaces.isEmpty()) {
                Log.w(TAG, "No places found, using mock data")
                locationHelper.getMockStressReliefPlaces(latitude, longitude)
            } else {
                uniquePlaces
            }

        } catch (e: Exception) {
            Log.e(TAG, "Error in simpleNearbySearch", e)
            return locationHelper.getMockStressReliefPlaces(latitude, longitude)
        }
    }

    /**
     * Search for places by keyword
     */
    private suspend fun searchByKeyword(
        keyword: String,
        placeType: PlaceType,
        userLat: Double,
        userLon: Double,
        bounds: RectangularBounds
    ): List<StressReliefPlace> {
        val foundPlaces = mutableListOf<StressReliefPlace>()

        try {
            // Create autocomplete request
            val request = FindAutocompletePredictionsRequest.builder()
                .setQuery(keyword)
                .setLocationBias(bounds)
                .build()

            // Get predictions
            val response = placesClient.findAutocompletePredictions(request).await()

            // Fetch details for each prediction (limit to 3 per keyword)
            for (prediction in response.autocompletePredictions.take(3)) {
                try {
                    val place = fetchPlaceDetails(prediction.placeId)

                    if (place != null && place.latLng != null) {
                        // Calculate distance
                        val distance = locationHelper.calculateDistance(
                            userLat, userLon,
                            place.latLng!!.latitude, place.latLng!!.longitude
                        )

                        // Only include places within 15km
                        if (distance <= SEARCH_RADIUS_KM * 1000) {
                            foundPlaces.add(
                                StressReliefPlace(
                                    name = place.name ?: "Unknown Place",
                                    type = placeType,
                                    latitude = place.latLng!!.latitude,
                                    longitude = place.latLng!!.longitude,
                                    distance = distance,
                                    address = place.address,
                                    rating = place.rating?.toFloat(),
                                    isOpen = null
                                )
                            )
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error fetching place details for ${prediction.placeId}", e)
                }
            }

        } catch (e: Exception) {
            Log.e(TAG, "Error in searchByKeyword for $keyword", e)
        }

        return foundPlaces
    }

    /**
     * Fetch detailed information for a place
     */
    private suspend fun fetchPlaceDetails(placeId: String): Place? {
        return try {
            val placeFields = listOf(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.LAT_LNG,
                Place.Field.ADDRESS,
                Place.Field.RATING
            )

            val request = FetchPlaceRequest.builder(placeId, placeFields).build()
            val response = placesClient.fetchPlace(request).await()

            response.place
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching place details for $placeId", e)
            null
        }
    }
}