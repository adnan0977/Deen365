package com.example.data.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Representation of individual prayer counts for Qada Salah (missed prayers make-up).
 */
data class QadaPrayerCounts(
    val fajrRemaining: Int = 0,
    val dhuhrRemaining: Int = 0,
    val asrRemaining: Int = 0,
    val maghribRemaining: Int = 0,
    val ishaRemaining: Int = 0,
    val witrRemaining: Int = 0,
    val fajrCompleted: Int = 0,
    val dhuhrCompleted: Int = 0,
    val asrCompleted: Int = 0,
    val maghribCompleted: Int = 0,
    val ishaCompleted: Int = 0,
    val witrCompleted: Int = 0
) {
    val totalRemaining: Int
        get() = fajrRemaining + dhuhrRemaining + asrRemaining + maghribRemaining + ishaRemaining + witrRemaining

    val totalCompleted: Int
        get() = fajrCompleted + dhuhrCompleted + asrCompleted + maghribCompleted + ishaCompleted + witrCompleted

    val totalInitial: Int
        get() = totalRemaining + totalCompleted

    val completionPercentage: Float
        get() = if (totalInitial == 0) 100f else ((totalCompleted.toFloat() / totalInitial.toFloat()) * 100f).coerceIn(0f, 100f)
}

enum class QadaPrayerType(val title: String, val arabicTitle: String, val rakahs: Int, val iconEmoji: String) {
    FAJR("Fajr", "الْفَجْر", 2, "🌅"),
    DHUHR("Dhuhr", "الظُّهْر", 4, "☀️"),
    ASR("Asr", "الْعَصْر", 4, "🌤️"),
    MAGHRIB("Maghrib", "الْمَغْرِب", 3, "🌇"),
    ISHA("Isha", "الْعِشَاء", 4, "🌌"),
    WITR("Witr", "الْوِتْر", 3, "✨")
}

data class QadaCalculationInput(
    val currentAge: Int = 25,
    val pubertyAge: Int = 14,
    val yearsMissed: Double = 3.0,
    val dailyMakeUpTarget: Int = 5 // e.g. 5 prayers per day
)

data class QadaHistoryLog(
    val id: String = System.currentTimeMillis().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val prayerType: QadaPrayerType,
    val countAdded: Int = 1,
    val dateFormatted: String = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault()).format(Date())
)

data class QadaSalahState(
    val counts: QadaPrayerCounts = QadaPrayerCounts(),
    val calculationInput: QadaCalculationInput = QadaCalculationInput(),
    val isSetupWizardOpen: Boolean = false,
    val historyLogs: List<QadaHistoryLog> = emptyList(),
    val dailyStreak: Int = 3,
    val todayCompletedCount: Int = 2
)
