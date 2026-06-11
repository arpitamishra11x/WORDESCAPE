package com.wordscape.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.wordscape.data.models.JournalEntry
import com.wordscape.data.models.Word

@Database(entities = [Word::class, JournalEntry::class], version = 1, exportSchema = false)
abstract class WordScapeDatabase : RoomDatabase() {
    abstract fun wordDao(): WordDao
    abstract fun journalDao(): JournalDao

    companion object {
        const val DATABASE_NAME = "wordscape_db"
    }
}
