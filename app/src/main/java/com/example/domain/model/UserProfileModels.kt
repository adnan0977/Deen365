package com.example.domain.model

/**
 * Spiritual Goals model representing user targets across Deen365 modules.
 * Fully compatible with Firestore serialization (provides no-arg constructor via default values).
 */
data class SpiritualGoals(
    // 1. Quranic Studies Goals
    val dailyQuranAyahsTarget: Int = 10,
    val weeklyQuranJuzTarget: Float = 0.5f,
    val targetSurahKhatamGoal: String = "Surah Al-Baqarah",

    // 2. Hadith Learning Goals
    val weeklyHadithsToStudy: Int = 5,
    val targetHadithCollection: String = "40 Hadith Nawawi",

    // 3. Dua Memorization Goals
    val monthlyDuasToMemorize: Int = 4,
    val targetDuaCategory: String = "Morning & Evening",

    // 4. Understanding Namaz & Salah Consistency Goals
    val dailySalahTarget: Int = 5, // Target 5/5 daily Salah
    val salahMeaningUnderstandingCompleted: Int = 3, // Steps whose Arabic meaning is mastered
    val totalSalahStepsToUnderstand: Int = 8,

    // 5. Fasting (Sawm) Goals
    val monthlyFastingTargetDays: Int = 4, // Mondays & Thursdays or Ayyam al-Beed
    val fastTypeFocus: String = "Monday & Thursday Sunnah",

    // Metadata
    val updatedAt: Long = System.currentTimeMillis()
)

/**
 * Visual Spiritual Badge / Achievement item.
 */
data class SpiritualBadge(
    val id: String,
    val title: String,
    val arabicTitle: String,
    val description: String,
    val iconEmoji: String,
    val category: String, // "Quran", "Hadith", "Dua", "Salah", "Fasting", "Consistency"
    val isUnlocked: Boolean,
    val currentProgress: Int,
    val targetRequired: Int,
    val unit: String,
    val unlockedDateFormatted: String? = null
)

/**
 * Aggregated live progress across all 5 key modules.
 */
data class ModuleProgressSummary(
    // 1. Quranic Studies
    val quranAyahsReadToday: Int,
    val quranAyahsGoal: Int,
    val quranSurahsCompleted: Int,
    val quranProgressPercentage: Float,

    // 2. Hadith Learning
    val hadithsStudiedCount: Int,
    val hadithsStudiedGoal: Int,
    val hadithsMemorizedCount: Int,
    val hadithProgressPercentage: Float,

    // 3. Dua Memorization
    val duasMemorizedCount: Int,
    val duasMemorizedGoal: Int,
    val duasFavoritedCount: Int,
    val duaProgressPercentage: Float,

    // 4. Understanding Namaz
    val salahOfferedToday: Int,
    val salahStreakDays: Int,
    val salahStepsUnderstood: Int,
    val salahProgressPercentage: Float,

    // 5. Fasting Journey
    val fastingDaysThisMonth: Int,
    val fastingGoalDays: Int,
    val fastingStreak: Int,
    val fastingProgressPercentage: Float,

    // Overall Vitality / Deen Index Score (0 to 100)
    val overallSpiritualVitalityScore: Int,
    val spiritualRankTitle: String,
    val unlockedBadgesCount: Int,
    val totalBadgesCount: Int
)

/**
 * Avatar options for Islamic user profiles.
 */
enum class UserAvatar(val id: String, val emoji: String, val title: String) {
    MOSQUE("avatar_mosque", "🕌", "Masjid Seeker"),
    QURAN("avatar_quran", "📖", "Quran Reader"),
    CRESCENT("avatar_crescent", "🌙", "Hilal Believer"),
    TASBIH("avatar_tasbih", "📿", "Devoted Zakir"),
    KAABA("avatar_kaaba", "🕋", "Pilgrim"),
    STAR("avatar_star", "⭐", "Beacon of Light"),
    LANTERN("avatar_lantern", "🏮", "Ramadan Fanous"),
    CALLIGRAPHY("avatar_calligraphy", "✒️", "Sacred Scribe");

    companion object {
        fun fromId(id: String): UserAvatar = entries.find { it.id == id } ?: MOSQUE
    }
}
