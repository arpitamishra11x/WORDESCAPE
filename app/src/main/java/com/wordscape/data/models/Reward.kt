package com.wordscape.data.models

data class Reward(
    val id: Int,
    val wordId: Int,
    val rewardType: String, // "animal_appear", "scene_transform", "particle_burst", "sound_effect"
    val animationName: String,
    val description: String,
    val isCollected: Boolean = false
)
