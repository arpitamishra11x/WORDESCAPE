package com.wordscape.domain

sealed class DifficultyLevel(val level: Int, val name: String, val description: String) {
    object FullWord : DifficultyLevel(1, "Explorer", "Drag all letters to spell the word")
    object LongerWord : DifficultyLevel(2, "Adventurer", "Spell longer words")
    object MissingLetters : DifficultyLevel(3, "Discoverer", "Fill in the missing letters")
    object AudioOnly : DifficultyLevel(4, "Listener", "Build the word from hearing it")
    object SpellingFromImage : DifficultyLevel(5, "Master", "See the object, spell the word")
    
    companion object {
        fun fromLevel(level: Int): DifficultyLevel = when(level) {
            1 -> FullWord
            2 -> LongerWord
            3 -> MissingLetters
            4 -> AudioOnly
            5 -> SpellingFromImage
            else -> FullWord
        }
    }
}
