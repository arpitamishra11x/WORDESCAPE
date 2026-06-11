package com.wordscape.data.models

data class ForestZone(
    val id: Int,
    val name: String,
    val description: String,
    val unlockThreshold: Int, // Number of trees needed to unlock this zone
    val isUnlocked: Boolean = false,
    val biomeType: String // e.g. "temperate", "tropical", "magical", "mystic"
)
