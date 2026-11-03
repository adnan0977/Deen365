package com.example.sync

sealed class PrayerSyncStatus {
    object Idle : PrayerSyncStatus()
    object Syncing : PrayerSyncStatus()
    data class Success(
        val uploadedCount: Int,
        val downloadedCount: Int,
        val timestamp: Long
    ) : PrayerSyncStatus()
    data class Error(val message: String) : PrayerSyncStatus()
}

data class HistoricalPrayerStats(
    val totalRecordedDays: Int = 0,
    val totalPossiblePrayers: Int = 0,
    val totalPrayedCount: Int = 0,
    val overallPercentage: Float = 0f,
    val fajrCount: Int = 0,
    val fajrPercentage: Float = 0f,
    val dhuhrCount: Int = 0,
    val dhuhrPercentage: Float = 0f,
    val asrCount: Int = 0,
    val asrPercentage: Float = 0f,
    val maghribCount: Int = 0,
    val maghribPercentage: Float = 0f,
    val ishaCount: Int = 0,
    val ishaPercentage: Float = 0f,
    val currentStreakDays: Int = 0,
    val bestStreakDays: Int = 0,
    val perfectDaysCount: Int = 0 // Days with 5/5
)
