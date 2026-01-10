package com.example.mentalhealthtracker.data

enum class PlaceType(val displayName: String, val emoji: String) {
    // Water Features
    BEACH("Serene beach", "🏖️"),
    WATERFALL("Waterfall with picnic area", "💧"),
    LAKE("Lake garden", "🏞️"),
    RIVER("Riverfront park", "🌊"),
    HOT_SPRING("Public hot spring", "♨️"),

    // Nature & Parks
    PARK("Public park with walking paths", "🌳"),
    GARDEN("Public garden", "🌺"),
    HIKING_TRAIL("Hiking trail", "🥾"),
    MOUNTAIN("Mountain viewpoint", "⛰️"),
    FOREST("Recreational forest", "🌲"),
    SCENIC_VIEW("Scenic lookout point", "🌅"),

    // Countryside & Agriculture
    TEA_PLANTATION("Tea plantation with cafe", "🍵"),
    ORCHARD("Fruit orchard", "🥭"),
    FARM("Agro-tourism farm", "🚜"),
    BOTANICAL_GARDEN("Botanical Garden", "🌿"),

    // Urban & Community Relaxation
    CAFE("Quiet cafe", "☕"),
    LIBRARY("Public library", "📚"),
    MUSEUM("Art museum or gallery", "🖼️"),
    SHOPPING_MALL("Shopping mall", "🛍️"),
    MOSQUE("Mosque", "🕌"),

    OTHER("Other", "📍")
}