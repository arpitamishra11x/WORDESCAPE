package com.wordscape.data.models

data class WordChallenge(
    val id: Int,
    val wordId: Int,
    val sceneId: String,
    val challengeType: String, // Maps to DifficultyLevel string representation
    val letterPositions: List<Pair<Float, Float>>? = null,
    val hintText: String? = null,
    val isCompleted: Boolean = false
)
