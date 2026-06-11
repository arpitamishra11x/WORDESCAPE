package com.wordscape.data.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "journal_entries",
    foreignKeys = [
        ForeignKey(
            entity = Word::class,
            parentColumns = ["id"],
            childColumns = ["wordId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class JournalEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val wordId: Int,
    val learnedAt: Long,
    val timesReviewed: Int = 0,
    val masteryLevel: Int = 0,
    val isFavorite: Boolean = false
)
