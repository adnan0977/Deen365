package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "duas")
data class DuaEntity(
    @PrimaryKey val id: String,
    val title: String,
    val category: String,
    val arabicText: String,
    val transliteration: String,
    val translation: String,
    val explanation: String,
    val reference: String,
    val recommendedCount: Int = 1,
    val isFavorite: Boolean = false,
    val isMemorized: Boolean = false,
    val practiceCount: Int = 0,
    val lastPracticedTimestamp: Long = 0L,
    val customFolder: String = "",
    val userNote: String = ""
)
