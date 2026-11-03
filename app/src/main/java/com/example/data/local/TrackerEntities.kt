package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "prayer_records")
data class PrayerRecordEntity(
    @PrimaryKey val dateStr: String, // format "YYYY-MM-DD"
    val fajr: Boolean = false,
    val dhuhr: Boolean = false,
    val asr: Boolean = false,
    val maghrib: Boolean = false,
    val isha: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false,
    val deviceModel: String = ""
) {
    val completedCount: Int
        get() = (if (fajr) 1 else 0) +
                (if (dhuhr) 1 else 0) +
                (if (asr) 1 else 0) +
                (if (maghrib) 1 else 0) +
                (if (isha) 1 else 0)

    val completionPercentage: Float
        get() = (completedCount.toFloat() / 5f) * 100f
}

@Entity(tableName = "fasting_records")
data class FastingRecordEntity(
    @PrimaryKey val dateStr: String, // format "YYYY-MM-DD"
    val isFasted: Boolean = false,
    val fastingType: String = "Ramadan", // "Ramadan", "Sunnah (Mon/Thu)", "White Days", "Qadha", "Nafilah"
    val isQadha: Boolean = false,
    val note: String = ""
)
