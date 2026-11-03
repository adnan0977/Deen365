package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_reflections")
data class DailyReflectionEntity(
    @PrimaryKey val id: String, // "today" or unique key
    val surahNumber: Int,
    val surahName: String,
    val ayahNumber: Int,
    val arabicText: String,
    val translation: String,
    val transliteration: String = "",
    val conciseExplanation: String = "",
    val reflectionMeaning: String = "",
    val dailyLifeApplication: String = "",
    val theme: String = "Divine Wisdom & Peace",
    val dateStr: String,
    val isCachedOffline: Boolean = true,
    val timestamp: Long = System.currentTimeMillis()
)
