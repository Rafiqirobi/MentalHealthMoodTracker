package com.example.mentalhealthtracker.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class StressReliefPlace(
    val name: String,
    val type: PlaceType,
    val latitude: Double,
    val longitude: Double,
    val distance: Float, // in meters
    val address: String? = null,
    val rating: Float? = null,
    val isOpen: Boolean? = null
) : Parcelable

enum class PlaceType(val displayName: String, val searchQuery: String) {
    PARK("Park", "park"),
    GARDEN("Garden", "garden"),
    NATURE("Nature Spot", "nature reserve"),
    CAFE("Quiet Cafe", "cafe"),
    LIBRARY("Library", "library"),
    MUSEUM("Museum", "museum"),
    BEACH("Beach", "beach"),
    TRAIL("Walking Trail", "hiking trail"),
    BOTANICAL_GARDEN("Botanical Garden", "botanical garden"),
    OTHER("Other", "peaceful place");

    companion object {
        fun getAllSearchQueries(): List<String> {
            return values().map { it.searchQuery }
        }
    }
}