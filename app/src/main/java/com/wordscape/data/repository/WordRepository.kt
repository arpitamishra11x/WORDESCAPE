package com.wordscape.data.repository

import com.wordscape.data.database.WordDao
import com.wordscape.data.models.Category
import com.wordscape.data.models.Word
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WordRepository @Inject constructor(
    private val wordDao: WordDao
) {
    fun getAllWords(): Flow<List<Word>> = wordDao.getAll()

    fun getWordsByCategory(category: Category): Flow<List<Word>> {
        return wordDao.getByCategory(category.name)
    }

    fun getWordsByDifficulty(level: Int): Flow<List<Word>> {
        return wordDao.getByDifficulty(level)
    }

    fun getWordById(id: Int): Flow<Word?> = wordDao.getById(id)

    fun getLearnedWords(): Flow<List<Word>> = wordDao.getLearnedWords()

    fun getLearnedCount(): Flow<Int> = wordDao.getLearnedWordCount()

    suspend fun markWordLearned(wordId: Int) {
        val word = wordDao.getById(wordId).first()
        if (word != null && !word.isLearned) {
            wordDao.update(
                word.copy(
                    isLearned = true,
                    discoveredAt = System.currentTimeMillis(),
                    masteryLevel = 1
                )
            )
        }
    }

    suspend fun updateMastery(wordId: Int, newLevel: Int) {
        val word = wordDao.getById(wordId).first()
        if (word != null) {
            wordDao.update(word.copy(masteryLevel = newLevel.coerceIn(0, 5)))
        }
    }

    suspend fun incrementInteraction(wordId: Int) {
        val word = wordDao.getById(wordId).first()
        if (word != null) {
            wordDao.update(word.copy(interactionCount = word.interactionCount + 1))
        }
    }

    fun getNextWord(worldId: String, currentWordIndex: Int): Flow<Word?> {
        val category = when (worldId.lowercase()) {
            "nature" -> Category.NATURE.name
            "space" -> Category.SPACE.name
            "ocean" -> Category.OCEAN.name
            "dinosaurs" -> Category.DINOSAURS.name
            else -> Category.ANIMALS.name
        }
        // Return a Flow of a single nullable Word based on index
        return kotlinx.coroutines.flow.flow {
            val words = wordDao.getByCategory(category).first()
            if (words.isNotEmpty()) {
                val index = currentWordIndex % words.size
                emit(words[index])
            } else {
                emit(null)
            }
        }
    }

    suspend fun prepopulateIfEmpty() {
        val words = wordDao.getAll().first()
        if (words.isEmpty()) {
            val dummyWords = listOf(
                // Level 1: Animals
                Word(
                    text = "CAT",
                    category = Category.ANIMALS.name,
                    difficultyLevel = 1,
                    funFact = "Cats sleep 12-16 hours per day to save energy!",
                    sceneName = "meadow",
                    animalType = "cat",
                    imageDescription = "A playful little ginger kitten sitting in grass.",
                    pronunciationText = "/kæt/"
                ),
                Word(
                    text = "DOG",
                    category = Category.ANIMALS.name,
                    difficultyLevel = 1,
                    funFact = "Dogs can understand up to 250 words and gestures!",
                    sceneName = "meadow",
                    animalType = "dog",
                    imageDescription = "A friendly golden retriever puppy wagging its tail.",
                    pronunciationText = "/dɒɡ/"
                ),
                Word(
                    text = "OWL",
                    category = Category.ANIMALS.name,
                    difficultyLevel = 1,
                    funFact = "Owls can rotate their heads 270 degrees around!",
                    sceneName = "forest",
                    animalType = "owl",
                    imageDescription = "A cute brown owl with big round eyes sitting on a branch.",
                    pronunciationText = "/aʊl/"
                ),
                Word(
                    text = "FOX",
                    category = Category.ANIMALS.name,
                    difficultyLevel = 1,
                    funFact = "Foxes use their bushy tails to stay warm and balance!",
                    sceneName = "forest",
                    animalType = "fox",
                    imageDescription = "A sleek orange fox sitting among colorful autumn leaves.",
                    pronunciationText = "/fɒks/"
                ),
                Word(
                    text = "BEE",
                    category = Category.ANIMALS.name,
                    difficultyLevel = 1,
                    funFact = "Bees dance to communicate where flowers are located!",
                    sceneName = "meadow",
                    animalType = "bee",
                    imageDescription = "A fuzzy yellow and black bumblebee hovering near a flower.",
                    pronunciationText = "/biː/"
                ),
                // Level 2: Nature
                Word(
                    text = "SUN",
                    category = Category.NATURE.name,
                    difficultyLevel = 2,
                    funFact = "The Sun is actually a giant glowing star at the center of our solar system!",
                    sceneName = "meadow",
                    animalType = null,
                    imageDescription = "A bright golden sun shining rays through fluffy clouds.",
                    pronunciationText = "/sʌn/"
                ),
                Word(
                    text = "CLOUD",
                    category = Category.NATURE.name,
                    difficultyLevel = 2,
                    funFact = "Clouds are made of billions of tiny floating water droplets!",
                    sceneName = "meadow",
                    animalType = null,
                    imageDescription = "Fluffy cloud shapes drifting across a warm sunset sky.",
                    pronunciationText = "/klaʊd/"
                ),
                Word(
                    text = "TREE",
                    category = Category.NATURE.name,
                    difficultyLevel = 2,
                    funFact = "Trees clean the air we breathe and can live for thousands of years!",
                    sceneName = "forest",
                    animalType = null,
                    imageDescription = "A grand oak tree with deep green leaves and thick branches.",
                    pronunciationText = "/triː/"
                ),
                Word(
                    text = "FLOWER",
                    category = Category.NATURE.name,
                    difficultyLevel = 2,
                    funFact = "Some flowers open up in the morning and close at night!",
                    sceneName = "meadow",
                    animalType = null,
                    imageDescription = "A beautiful pink blossom opening its petals in sunlight.",
                    pronunciationText = "/ˈflaʊər/"
                ),
                Word(
                    text = "RIVER",
                    category = Category.NATURE.name,
                    difficultyLevel = 2,
                    funFact = "Rivers flow downhill toward oceans, carrying fresh water!",
                    sceneName = "meadow",
                    animalType = null,
                    imageDescription = "A crystal blue river winding through rolling green hills.",
                    pronunciationText = "/ˈrɪvər/"
                ),
                // Space
                Word(
                    text = "STAR",
                    category = Category.SPACE.name,
                    difficultyLevel = 3,
                    funFact = "Stars look small because they are trillions of miles away!",
                    sceneName = "space",
                    animalType = null,
                    imageDescription = "A sparkling yellow star glowing in the dark night sky.",
                    pronunciationText = "/stɑːr/"
                ),
                Word(
                    text = "MOON",
                    category = Category.SPACE.name,
                    difficultyLevel = 3,
                    funFact = "The Moon does not make its own light; it reflects the Sun!",
                    sceneName = "space",
                    animalType = null,
                    imageDescription = "A silver crescent moon casting soft light.",
                    pronunciationText = "/muːn/"
                )
            )
            wordDao.insertAll(dummyWords)
        }
    }
}
