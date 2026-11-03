package com.example.domain

import com.example.notification.AlertSoundMode

/**
 * Data model representing manual mosque congregation / Iqamah timings
 * alongside default astronomical solar calculations.
 */
data class MosquePrayerTiming(
    val prayerId: String, // "fajr", "sunrise", "dhuhr", "asr", "maghrib", "isha", "jummah"
    val displayName: String,
    val arabicName: String,
    val defaultCalculatedTime: String,
    val manualMosqueTime: String,
    val isMosqueTimingActive: Boolean = false,
    val isAlarmEnabled: Boolean = true,
    val alarmSoundMode: AlertSoundMode = AlertSoundMode.FULL_ADHAN,
    val preAlarmMinutes: Int = 0 // 0 = disabled, 5 = 5m before, 10 = 10m before, 15 = 15m before
) {
    /**
     * The effective time string that triggers alarms and shows on timetable
     */
    val effectiveTime: String
        get() = if (isMosqueTimingActive && manualMosqueTime.isNotBlank()) manualMosqueTime else defaultCalculatedTime
}
