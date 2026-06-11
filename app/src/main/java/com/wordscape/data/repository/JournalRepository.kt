package com.wordscape.data.repository

import com.wordscape.data.database.JournalDao
import com.wordscape.data.database.WordDao
import com.wordscape.data.models.JournalEntry
import com.wordscape.data.models.Word
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JournalRepository @Inject constructor(
    private val journalDao: JournalDao,
    private val wordDao: WordDao
) {
    fun getJournalEntries(): Flow<List<JournalEntry>> = journalDao.getAll()

    fun getJournalWithWords(): Flow<List<Pair<JournalEntry, Word>>> {
        return journalDao.getAll().combine(wordDao.getAll()) { entries, words ->
            entries.mapNotNull { entry ->
                val word = words.firstOrNull { it.id == entry.wordId }
                if (word != null) Pair(entry, word) else null
            }
        }
    }

    suspend fun addEntry(wordId: Int) {
        val existing = journalDao.getByWordId(wordId).first()
        if (existing == null) {
            journalDao.insert(
                JournalEntry(
                    wordId = wordId,
                    learnedAt = System.currentTimeMillis()
                )
            )
        }
    }

    suspend fun markReviewed(entryId: Int) {
        // Find by entryId. Since DAO has no direct query by entryId, get all and update
        val entry = journalDao.getAll().first().firstOrNull { it.id == entryId }
        if (entry != null) {
            journalDao.update(
                entry.copy(
                    timesReviewed = entry.timesReviewed + 1,
                    masteryLevel = (entry.masteryLevel + 1).coerceAtMost(5)
                )
            )
        }
    }

    suspend fun toggleFavorite(entryId: Int) {
        val entry = journalDao.getAll().first().firstOrNull { it.id == entryId }
        if (entry != null) {
            journalDao.update(entry.copy(isFavorite = !entry.isFavorite))
        }
    }

    fun getEntryCount(): Flow<Int> = journalDao.getEntryCount()
}
