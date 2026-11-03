package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dhikr_items")
data class DhikrItemEntity(
    @PrimaryKey
    val id: String, // e.g. "subhanallah", "alhamdulillah", "custom_170000000"
    val arabicText: String,
    val transliteration: String,
    val meaning: String,
    val dailyGoal: Int = 100,
    val virtue: String = "",
    val category: String = "Praise",
    val displayOrder: Int = 0,
    val isCustom: Boolean = false
)

@Entity(tableName = "dhikr_daily_logs")
data class DhikrDailyLogEntity(
    @PrimaryKey
    val id: String, // composite "${dhikrId}_${dateStr}"
    val dhikrId: String,
    val dateStr: String, // "yyyy-MM-dd"
    val count: Int = 0,
    val goal: Int = 100,
    val lastUpdatedTimestamp: Long = System.currentTimeMillis()
)
