package com.example.data.local

import androidx.room.Entity

@Entity(tableName = "ayahs", primaryKeys = ["surahNumber", "ayahNumber"])
data class AyahEntity(
    val surahNumber: Int,
    val ayahNumber: Int,
    val arabicText: String,
    val translation: String,
    val transliteration: String = "",
    val audioUrl: String? = null,
    val isBookmarked: Boolean = false
)
