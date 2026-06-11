package com.wordscape.domain

import com.wordscape.data.models.Category
import com.wordscape.data.models.UserProgress
import com.wordscape.data.models.Word
import com.wordscape.data.repository.WordRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

interface WordRecommendationEngine {
    suspend fun getRecommendedWords(userId: String, count: Int): List<Word>
    suspend fun getPersonalizedCategory(userId: String): Category
    suspend fun shouldIntroduceNewDifficulty(progress: UserProgress): Boolean
}

@Singleton
class DummyWordRecommendationEngine @Inject constructor(
    private val wordRepository: WordRepository
) : WordRecommendationEngine {
    
    override suspend fun getRecommendedWords(userId: String, count: Int): List<Word> {
        val words = wordRepository.getAllWords().first()
        val unlearned = words.filter { !it.isLearned }
        return if (unlearned.isNotEmpty()) {
            unlearned.shuffled().take(count)
        } else {
            words.shuffled().take(count)
        }
    }

    override suspend fun getPersonalizedCategory(userId: String): Category {
        return Category.ANIMALS
    }

    override suspend fun shouldIntroduceNewDifficulty(progress: UserProgress): Boolean {
        return progress.totalWordsLearned >= 5
    }
}
