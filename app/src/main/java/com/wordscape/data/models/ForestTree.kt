package com.wordscape.data.models

data class ForestTree(
    val id: Int,
    val wordId: Int,
    val treeType: String, // e.g. "oak", "willow", "cherry_blossom", "magical"
    val growthStage: Int, // 0-4: seed, sprout, sapling, young, mature
    val positionX: Float, // relative 0f - 1f across the screen
    val positionY: Float, // relative 0f - 1f down screen (ground level placement)
    val plantedAt: Long,
    val color: Long // Tint color hex (e.g. 0xFF86EFAC)
)
