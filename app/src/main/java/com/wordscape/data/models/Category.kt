package com.wordscape.data.models

enum class Category(
    val displayName: String,
    val description: String,
    val iconName: String,
    val gradientStart: Long,
    val gradientEnd: Long
) {
    ANIMALS("Animals", "Discover amazing creatures", "pets", 0xFF86EFAC, 0xFF22C55E),
    NATURE("Nature", "Explore the wild forest", "forest", 0xFFA78BFA, 0xFF7C3AED),
    HOME("Home", "Find objects around the house", "home", 0xFF7DD3FC, 0xFF0284C7),
    FOOD("Food", "Tasty treats and fruits", "restaurant", 0xFFFCD34D, 0xFFD97706),
    WEATHER("Weather", "Sky, rain, sun and snow", "wb_sunny", 0xFFF9A8D4, 0xFFDB2777),
    SPACE("Space", "Stars, planets and galaxies", "rocket", 0xFF1E1B4B, 0xFF4C1D95),
    OCEAN("Ocean", "Deep sea mysteries", "water", 0xFF0EA5E9, 0xFF0369A1),
    DINOSAURS("Dinosaurs", "Ancient giant beasts", "cruelty_free", 0xFF84CC16, 0xFF4D7C0F),
    TRANSPORTATION("Transportation", "Things that move", "directions_car", 0xFF64748B, 0xFF334155),
    FANTASY("Fantasy", "Magical worlds and creatures", "auto_awesome", 0xFFEC4899, 0xFFBE185D);

    companion object {
        fun fromName(name: String): Category {
            return entries.firstOrNull { it.name.equals(name, ignoreCase = true) } ?: ANIMALS
        }
    }
}
