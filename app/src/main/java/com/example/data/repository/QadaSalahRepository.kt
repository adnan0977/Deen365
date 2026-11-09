package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

/**
 * Repository maintaining Qada Salah missed prayer counts, calculations,
 * and daily make-up progress in persistent SharedPreferences.
 */
class QadaSalahRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("deen365_qada_prefs", Context.MODE_PRIVATE)

    private val _countsState = MutableStateFlow(loadSavedCounts())
    val countsState: StateFlow<QadaPrayerCounts> = _countsState.asStateFlow()

    private val _dailyStreak = MutableStateFlow(loadDailyStreak())
    val dailyStreak: StateFlow<Int> = _dailyStreak.asStateFlow()

    private val _todayCompleted = MutableStateFlow(loadTodayCompleted())
    val todayCompleted: StateFlow<Int> = _todayCompleted.asStateFlow()

    private fun loadSavedCounts(): QadaPrayerCounts {
        return QadaPrayerCounts(
            fajrRemaining = prefs.getInt("qada_fajr_rem", 120),
            dhuhrRemaining = prefs.getInt("qada_dhuhr_rem", 120),
            asrRemaining = prefs.getInt("qada_asr_rem", 120),
            maghribRemaining = prefs.getInt("qada_maghrib_rem", 120),
            ishaRemaining = prefs.getInt("qada_isha_rem", 120),
            witrRemaining = prefs.getInt("qada_witr_rem", 120),
            fajrCompleted = prefs.getInt("qada_fajr_done", 35),
            dhuhrCompleted = prefs.getInt("qada_dhuhr_done", 42),
            asrCompleted = prefs.getInt("qada_asr_done", 38),
            maghribCompleted = prefs.getInt("qada_maghrib_done", 40),
            ishaCompleted = prefs.getInt("qada_isha_done", 30),
            witrCompleted = prefs.getInt("qada_witr_done", 28)
        )
    }

    private fun loadDailyStreak(): Int {
        return prefs.getInt("qada_daily_streak", 4)
    }

    private fun loadTodayCompleted(): Int {
        val lastDate = prefs.getString("qada_last_log_date", "") ?: ""
        val todayStr = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
        return if (lastDate == todayStr) {
            prefs.getInt("qada_today_count", 3)
        } else {
            0
        }
    }

    suspend fun setTotalDeficit(daysMissed: Int) = withContext(Dispatchers.IO) {
        val newCounts = QadaPrayerCounts(
            fajrRemaining = daysMissed,
            dhuhrRemaining = daysMissed,
            asrRemaining = daysMissed,
            maghribRemaining = daysMissed,
            ishaRemaining = daysMissed,
            witrRemaining = daysMissed,
            fajrCompleted = 0,
            dhuhrCompleted = 0,
            asrCompleted = 0,
            maghribCompleted = 0,
            ishaCompleted = 0,
            witrCompleted = 0
        )
        saveCountsToPrefs(newCounts)
        _countsState.value = newCounts
    }

    suspend fun markPrayerCompleted(prayerType: QadaPrayerType, delta: Int = 1) = withContext(Dispatchers.IO) {
        val curr = _countsState.value
        val updated = when (prayerType) {
            QadaPrayerType.FAJR -> curr.copy(
                fajrRemaining = (curr.fajrRemaining - delta).coerceAtLeast(0),
                fajrCompleted = curr.fajrCompleted + delta
            )
            QadaPrayerType.DHUHR -> curr.copy(
                dhuhrRemaining = (curr.dhuhrRemaining - delta).coerceAtLeast(0),
                dhuhrCompleted = curr.dhuhrCompleted + delta
            )
            QadaPrayerType.ASR -> curr.copy(
                asrRemaining = (curr.asrRemaining - delta).coerceAtLeast(0),
                asrCompleted = curr.asrCompleted + delta
            )
            QadaPrayerType.MAGHRIB -> curr.copy(
                maghribRemaining = (curr.maghribRemaining - delta).coerceAtLeast(0),
                maghribCompleted = curr.maghribCompleted + delta
            )
            QadaPrayerType.ISHA -> curr.copy(
                ishaRemaining = (curr.ishaRemaining - delta).coerceAtLeast(0),
                ishaCompleted = curr.ishaCompleted + delta
            )
            QadaPrayerType.WITR -> curr.copy(
                witrRemaining = (curr.witrRemaining - delta).coerceAtLeast(0),
                witrCompleted = curr.witrCompleted + delta
            )
        }
        saveCountsToPrefs(updated)
        _countsState.value = updated

        // Record daily activity
        val todayStr = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
        val newTodayCount = _todayCompleted.value + delta
        _todayCompleted.value = newTodayCount
        prefs.edit()
            .putString("qada_last_log_date", todayStr)
            .putInt("qada_today_count", newTodayCount)
            .apply()
    }

    suspend fun undoPrayerCompleted(prayerType: QadaPrayerType, delta: Int = 1) = withContext(Dispatchers.IO) {
        val curr = _countsState.value
        val updated = when (prayerType) {
            QadaPrayerType.FAJR -> curr.copy(
                fajrRemaining = curr.fajrRemaining + delta,
                fajrCompleted = (curr.fajrCompleted - delta).coerceAtLeast(0)
            )
            QadaPrayerType.DHUHR -> curr.copy(
                dhuhrRemaining = curr.dhuhrRemaining + delta,
                dhuhrCompleted = (curr.dhuhrCompleted - delta).coerceAtLeast(0)
            )
            QadaPrayerType.ASR -> curr.copy(
                asrRemaining = curr.asrRemaining + delta,
                asrCompleted = (curr.asrCompleted - delta).coerceAtLeast(0)
            )
            QadaPrayerType.MAGHRIB -> curr.copy(
                maghribRemaining = curr.maghribRemaining + delta,
                maghribCompleted = (curr.maghribCompleted - delta).coerceAtLeast(0)
            )
            QadaPrayerType.ISHA -> curr.copy(
                ishaRemaining = curr.ishaRemaining + delta,
                ishaCompleted = (curr.ishaCompleted - delta).coerceAtLeast(0)
            )
            QadaPrayerType.WITR -> curr.copy(
                witrRemaining = curr.witrRemaining + delta,
                witrCompleted = (curr.witrCompleted - delta).coerceAtLeast(0)
            )
        }
        saveCountsToPrefs(updated)
        _countsState.value = updated
    }

    private fun saveCountsToPrefs(counts: QadaPrayerCounts) {
        prefs.edit()
            .putInt("qada_fajr_rem", counts.fajrRemaining)
            .putInt("qada_dhuhr_rem", counts.dhuhrRemaining)
            .putInt("qada_asr_rem", counts.asrRemaining)
            .putInt("qada_maghrib_rem", counts.maghribRemaining)
            .putInt("qada_isha_rem", counts.ishaRemaining)
            .putInt("qada_witr_rem", counts.witrRemaining)
            .putInt("qada_fajr_done", counts.fajrCompleted)
            .putInt("qada_dhuhr_done", counts.dhuhrCompleted)
            .putInt("qada_asr_done", counts.asrCompleted)
            .putInt("qada_maghrib_done", counts.maghribCompleted)
            .putInt("qada_isha_done", counts.ishaCompleted)
            .putInt("qada_witr_done", counts.witrCompleted)
            .apply()
    }
}
