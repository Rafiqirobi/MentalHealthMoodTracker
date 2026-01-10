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

