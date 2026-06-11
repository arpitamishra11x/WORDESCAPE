package com.wordscape.domain

import javax.inject.Inject
import javax.inject.Singleton

interface AdaptiveDifficultyEngine {
    suspend fun calculateOptimalDifficulty(userId: String): DifficultyLevel
    suspend fun adjustAfterAttempt(wordId: Int, success: Boolean, timeMs: Long): DifficultyLevel
    suspend fun getConfidenceScore(userId: String): Float // 0f - 1f
}

@Singleton
class DummyAdaptiveDifficultyEngine @Inject constructor() : AdaptiveDifficultyEngine {
    override suspend fun calculateOptimalDifficulty(userId: String): DifficultyLevel {
        return DifficultyLevel.FullWord
    }

    override suspend fun adjustAfterAttempt(wordId: Int, success: Boolean, timeMs: Long): DifficultyLevel {
        return DifficultyLevel.FullWord
    }

    override suspend fun getConfidenceScore(userId: String): Float {
        return 0.85f
    }
}
