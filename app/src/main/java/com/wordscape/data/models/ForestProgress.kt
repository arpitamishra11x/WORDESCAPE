package com.wordscape.data.models

data class ForestProgress(
    val totalTrees: Int,
    val currentZoneId: Int,
    val unlockedZones: List<Int>,
    val forestLevel: Int, // Calculated based on tree milestones
    val lastTreePlantedAt: Long? = null
) {
    fun getForestLevelName(): String = when {
        totalTrees < 10 -> "Sprout Meadow"
        totalTrees < 50 -> "Whispering Grove"
        totalTrees < 200 -> "Starlight Woodland"
        totalTrees < 500 -> "Enchanted Forest"
        else -> "Fantasy Ecosystem"
    }
}
