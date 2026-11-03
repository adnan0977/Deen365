package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room Entity representing a canonical Hadith Book/Collection (e.g. Sahih al-Bukhari, Sahih Muslim).
 * Stores collection metadata, author information, and chapter/hadith volume counters.
 */
@Entity(tableName = "hadith_books")
data class HadithBookEntity(
    @PrimaryKey
    val id: String,                 // Unique collection key: "bukhari", "muslim", "nasai", "abudawud", "tirmidhi", "ibnmajah", "malik", "nawawi"
    val titleEnglish: String,       // e.g. "Sahih al-Bukhari"
    val titleArabic: String,        // e.g. "صحيح البخاري"
    val authorName: String,         // e.g. "Imam Muhammad al-Bukhari"
    val deathYearHijri: Int,        // e.g. 256
    val totalHadithCount: Int,      // e.g. 7563
    val totalChaptersCount: Int,    // e.g. 97
    val badgeLetter: String,        // e.g. "B"
    val badgeColorHex: String,      // e.g. "#E25A53"
    val description: String = ""
)
