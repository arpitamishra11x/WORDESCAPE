package com.wordscape.domain

import com.wordscape.data.models.UserProgress
import com.wordscape.data.models.Word
import com.wordscape.data.repository.WordRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class LearningEngine @Inject constructor(
    private val wordRepository: WordRepository
) {
    fun determineNextWord(progress: UserProgress): Flow<Word?> {
        return wordRepository.getNextWord(progress.currentWorldId, progress.currentSceneIndex)
    }

    fun calculateDifficulty(wordsLearned: Int): DifficultyLevel {
        return when {
            wordsLearned < 3 -> DifficultyLevel.FullWord
            wordsLearned < 8 -> DifficultyLevel.LongerWord
            wordsLearned < 15 -> DifficultyLevel.MissingLetters
            wordsLearned < 25 -> DifficultyLevel.AudioOnly
            else -> DifficultyLevel.SpellingFromImage
        }
    }

    fun generateLetterPositions(word: String, screenWidth: Float, screenHeight: Float): List<Pair<Float, Float>> {
        val count = word.length
        val positions = mutableListOf<Pair<Float, Float>>()
        
        // Randomly distribute letters around the scene, keeping spacing
        val margin = 80f
        val startY = screenHeight * 0.45f
        val endY = screenHeight * 0.75f
        val startX = margin
        val endX = screenWidth - margin

        for (i in 0 until count) {
            var posX: Float
            var posY: Float
            var tooClose: Boolean
            var attempts = 0
            
            do {
                posX = Random.nextFloat() * (endX - startX) + startX
                posY = Random.nextFloat() * (endY - startY) + startY
                tooClose = false
                attempts++
                
                // Check distance to already generated positions to prevent overlapping
                for (pos in positions) {
                    val dist = kotlin.math.hypot(posX - pos.first, posY - pos.second)
                    if (dist < 150f) {
                        tooClose = true
                        break
                    }
                }
            } while (tooClose && attempts < 50)
            
            positions.add(Pair(posX, posY))
        }
        return positions
    }

    fun checkLetterPlacement(letter: Char, slotIndex: Int, word: String): Boolean {
        if (slotIndex < 0 || slotIndex >= word.length) return false
        return word[slotIndex].equals(letter, ignoreCase = true)
    }

    fun isWordComplete(placedLetters: Map<Int, Char>, word: String): Boolean {
        if (placedLetters.size < word.length) return false
        for (i in word.indices) {
            val placedChar = placedLetters[i] ?: return false
            if (!word[i].equals(placedChar, ignoreCase = true)) {
                return false
            }
        }
        return true
    }

    fun generateMissingLetterChallenge(word: String): Pair<String, List<Int>> {
        // Returns the word with some characters replaced by blanks, and the list of blank indices
        // e.g. for "CAT" returns ("C_T", listOf(1))
        val blankIndices = mutableListOf<Int>()
        val sb = StringBuilder(word)
        
        val blankCount = when {
            word.length <= 3 -> 1
            word.length <= 5 -> 2
            else -> 3
        }

        while (blankIndices.size < blankCount) {
            val idx = Random.nextInt(word.length)
            if (idx !in blankIndices) {
                blankIndices.add(idx)
                sb.setCharAt(idx, '_')
            }
        }
        
        return Pair(sb.toString(), blankIndices.sorted())
    }
}
