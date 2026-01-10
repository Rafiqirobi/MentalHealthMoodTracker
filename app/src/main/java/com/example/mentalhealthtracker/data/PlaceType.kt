package com.example.mentalhealthtracker.data

enum class PlaceType(val displayName: String, val emoji: String) {
    // Water Features
    BEACH("Beach", "🏖️"),
    WATERFALL("Waterfall", "💧"),
    LAKE("Lake", "🏞️"),
    RIVER("River", "🌊"),

    // Nature & Parks
    PARK("Park", "🌳"),
    GARDEN("Garden", "🌺"),
    HIKING_TRAIL("Hiking Trail", "🥾"),
    MOUNTAIN("Mountain", "⛰️"),
    FOREST("Forest", "🌲"),
    SCENIC_VIEW("Scenic View", "👁️"),

    // Countryside
    ORCHARD("Orchard", "🍎"),
    FARM("Farm", "🚜"),
    BOTANICAL_GARDEN("Botanical Garden", "🌿"),

    // Urban Relaxation
    CAFE("Café", "☕"),
    LIBRARY("Library", "📚"),
    MUSEUM("Museum", "🖼️"),
    SHOPPING_MALL("Shopping Mall", "🛍️"),

    OTHER("Other", "📍")
}