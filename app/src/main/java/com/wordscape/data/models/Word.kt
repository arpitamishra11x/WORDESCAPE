package com.wordscape.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "words")
data class Word(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val text: String,
    val category: String, // Category enum name
    val difficultyLevel: Int, // 1-5
    val funFact: String,
    val sceneName: String, // e.g. "meadow", "forest", "desert"
    val animalType: String?, // nullable, e.g. "cat", "dog"
    val imageDescription: String,
    val pronunciationText: String,
    val isLearned: Boolean = false,
    val masteryLevel: Int = 0, // 0-5
    val interactionCount: Int = 0,
    val discoveredAt: Long? = null
)
