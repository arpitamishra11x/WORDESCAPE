package com.wordscape.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.wordscape.data.database.JournalDao
import com.wordscape.data.database.WordDao
import com.wordscape.data.database.WordScapeDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
        wordRepositoryProvider: Provider<com.wordscape.data.repository.WordRepository>
    ): WordScapeDatabase {
        return Room.databaseBuilder(
            context,
            WordScapeDatabase::class.java,
            WordScapeDatabase.DATABASE_NAME
        )
        .addCallback(object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // Prepopulate database inside a background scope
                CoroutineScope(Dispatchers.IO).launch {
                    wordRepositoryProvider.get().prepopulateIfEmpty()
                }
            }
        })
        .fallbackToDestructiveMigration()
        .build()
    }

    @Provides
    fun provideWordDao(database: WordScapeDatabase): WordDao {
        return database.wordDao()
    }

    @Provides
    fun provideJournalDao(database: WordScapeDatabase): JournalDao {
        return database.journalDao()
    }
}
