package com.wordscape.audio

enum class AmbientTrack(
    val displayName: String,
    val fileName: String,
    val loopable: Boolean = true
) {
    NATURE_AMBIENCE("Nature Ambience", "nature_ambience"),
    FOREST_SOUNDS("Forest Sounds", "forest_sounds"),
    OCEAN_WAVES("Ocean Waves", "ocean_waves"),
    WIND_GENTLE("Gentle Wind", "wind_gentle"),
    NIGHT_CRICKETS("Night Crickets", "night_crickets"),
    MAGICAL_WORLD("Magical World", "magical_world"),
    RAIN_SOFT("Soft Rain", "rain_soft");
}
