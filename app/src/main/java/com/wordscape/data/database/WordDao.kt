package com.wordscape.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.wordscape.data.models.Word
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {
    @Query("SELECT * FROM words")
    fun getAll(): Flow<List<Word>>

    @Query("SELECT * FROM words WHERE category = :category")
    fun getByCategory(category: String): Flow<List<Word>>

    @Query("SELECT * FROM words WHERE difficultyLevel = :level")
    fun getByDifficulty(level: Int): Flow<List<Word>>

    @Query("SELECT * FROM words WHERE id = :id")
    fun getById(id: Int): Flow<Word?>

    @Query("SELECT * FROM words WHERE isLearned = 1")
    fun getLearnedWords(): Flow<List<Word>>

    @Query("SELECT * FROM words WHERE isLearned = 0")
    fun getUnlearnedWords(): Flow<List<Word>>

    @Query("SELECT COUNT(*) FROM words")
    fun getWordCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM words WHERE isLearned = 1")
    fun getLearnedWordCount(): Flow<Int>

    @Query("SELECT * FROM words WHERE text LIKE :query || '%'")
    fun searchWords(query: String): Flow<List<Word>>

    @Update
    suspend fun update(word: Word)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(words: List<Word>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(word: Word)
}
