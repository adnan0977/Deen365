package com.example.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room Entity representing individual Hadith Chapters (Kitab / Bab) within a collection.
 * Includes completion tracking fields (`isCompleted`, `completedAt`, `notes`) to support
 * the Kitab Completion Tracker across all canonical Hadith collections.
 */
@Entity(
    tableName = "hadith_chapters",
    indices = [
        Index(value = ["collectionId"]),
        Index(value = ["collectionId", "bookNumber"], unique = true)
    ]
)
data class HadithChapterEntity(
    @PrimaryKey
    val id: String,                 // Unique key, e.g. "bukhari_1", "muslim_2", "nasai_5"
    val collectionId: String,       // Collection slug, e.g. "bukhari", "muslim"
    val bookNumber: Int,            // Kitab index (1, 2, 3...)
    val titleEnglish: String,       // e.g. "1. Revelation"
    val titleArabic: String,        // e.g. "بدء الوحي"
    val hadithRange: String,        // e.g. "Hadith No: 1-7"
    val hadithCount: Int,           // Total hadiths in this chapter
    val overview: String = "",      // Short summary/theme of the chapter
    val isCompleted: Boolean = false, // Kitab completion status in Room
    val completedAt: Long? = null,  // Timestamp when marked completed
    val notes: String = ""          // User reflections or personal notes
)
