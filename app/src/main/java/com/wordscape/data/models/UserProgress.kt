package com.wordscape.data.models

data class UserProgress(
    val totalWordsLearned: Int,
    val currentLevel: Int,
    val currentStreak: Int,
    val longestStreak: Int,
    val totalPlayTime: Long, // in minutes
    val lastPlayedAt: Long? = null,
    val currentWorldId: String = "animals",
    val currentSceneIndex: Int = 0
)
