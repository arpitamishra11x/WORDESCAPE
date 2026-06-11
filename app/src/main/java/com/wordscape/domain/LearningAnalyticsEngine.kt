package com.wordscape.domain

import javax.inject.Inject
import javax.inject.Singleton

interface LearningAnalyticsEngine {
    suspend fun trackWordAttempt(wordId: Int, success: Boolean, timeSpentMs: Long)
    suspend fun getAverageCompletionTime(): Long
    suspend fun getMostDifficultWords(): List<Int>
    suspend fun getStreakData(): Pair<Int, Int> // Current streak, longest streak
    suspend fun getLearningVelocity(): Float // words per session
}

@Singleton
class DummyLearningAnalyticsEngine @Inject constructor() : LearningAnalyticsEngine {
    override suspend fun trackWordAttempt(wordId: Int, success: Boolean, timeSpentMs: Long) {
        // Analytics tracking hook
    }

    override suspend fun getAverageCompletionTime(): Long {
        return 12000L // 12 seconds average
    }

    override suspend fun getMostDifficultWords(): List<Int> {
        return emptyList()
    }

    override suspend fun getStreakData(): Pair<Int, Int> {
        return Pair(3, 7) // Dummy streak data
    }

    override suspend fun getLearningVelocity(): Float {
        return 1.5f // 1.5 words per session
    }
}
