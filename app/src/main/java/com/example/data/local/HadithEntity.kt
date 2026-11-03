package com.example.data.local

import androidx.room.Entity

/**
 * Room Entity representing individual Hadith records in the database.
 * Primary key is composite: (collection, hadithNumber).
 */
@Entity(tableName = "hadiths", primaryKeys = ["collection", "hadithNumber"])
data class HadithEntity(
    val collection: String,
    val bookNumber: Int,
    val chapterName: String,
    val hadithNumber: Int,
    val arabicText: String,
    val translation: String,
    val narrator: String,
    val grade: String,
    val isFavorite: Boolean = false,
    val isRead: Boolean = false,
    val isMemorized: Boolean = false,
    val lastStudiedTimestamp: Long = 0L
) {
    val id: Int get() = hadithNumber
}

/**
 * Typealias representing Hadith records in the schema.
 */
typealias HadithRecordEntity = HadithEntity
