package com.example.mentalhealthtracker.data

enum class PlaceType(val displayName: String, val emoji: String, val searchQuery: String) {
    BEACH("Beach", "🏖️", "Beach or pantai "),
    PARK("Park", "🌳", "taman or park"),
    CAFE("Café", "☕", "kafe or cafe"),
    LIBRARY("Library", "📚", "perpustakaan or library"),
    MUSEUM("Museum", "🖼️", "muzium or museum"),
    SHOPPING_MALL("Shopping Mall", "🛍️", "pusat membeli-belah or shopping mall"),
    OTHER("Other", "📍", "other")
}