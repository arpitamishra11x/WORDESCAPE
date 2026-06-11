package com.wordscape.data.models

data class World(
    val id: String,
    val name: String,
    val description: String,
    val category: String, // Category enum name
    val scenes: List<String>, // List of Scene IDs
    val isUnlocked: Boolean = false,
    val requiredWordsToUnlock: Int = 0,
    val gradientColors: List<Long>, // Background gradient stops
    val iconName: String
)
