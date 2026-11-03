package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room Entity tracking the completion status of individual Hadith chapters (Kitab / Bab)
 * across collections (Sahih al-Bukhari, Sahih Muslim, 40 Hadith an-Nawawi, etc.).
 */
@Entity(tableName = "kitab_completion")
data class KitabCompletionEntity(
    @PrimaryKey
    val bookId: String,             // Unique identifier, e.g., "bukhari_1", "muslim_2"
    val collectionId: String,       // Collection slug: "bukhari", "muslim", "nawawi"
    val bookNumber: Int,            // Kitab index
    val titleEnglish: String,       // e.g. "Revelation (Bad' al-Wahy)"
    val titleArabic: String,        // e.g. "بدء الوحي"
    val hadithCount: Int,           // Number of Ahadith in this chapter
    val isCompleted: Boolean = true,
    val completedAt: Long = System.currentTimeMillis(),
    val notes: String = ""
)
