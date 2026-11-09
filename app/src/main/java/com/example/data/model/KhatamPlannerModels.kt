package com.example.data.model

/**
 * Khatam Quran Target Planner Models.
 * Calculates pacing (pages & ayahs per prayer) and tracks daily progress towards completing the Noble Quran.
 */

data class KhatamPlanPreset(
    val id: String,
    val title: String,
    val targetDays: Int,
    val description: String,
    val pagesPerDay: Double,
    val pagesPerPrayer: Double
)

data class KhatamPrayerChecklist(
    val fajrCompleted: Boolean = false,
    val dhuhrCompleted: Boolean = false,
    val asrCompleted: Boolean = false,
    val maghribCompleted: Boolean = false,
    val ishaCompleted: Boolean = false
) {
    val completedCount: Int get() = listOf(fajrCompleted, dhuhrCompleted, asrCompleted, maghribCompleted, ishaCompleted).count { it }
}

data class KhatamPlannerState(
    val selectedTargetDays: Int = 30, // 30-Day Ramadan Target default
    val totalQuranPages: Int = 604,   // Standard Madinah Mushaf
    val completedPages: Int = 180,     // e.g. ~9 Juz
    val currentReadingPage: Int = 181,
    val todayChecklist: KhatamPrayerChecklist = KhatamPrayerChecklist(),
    val streakDays: Int = 8,
    val targetStartDateMs: Long = System.currentTimeMillis() - (8L * 24 * 3600 * 1000),
    val isKhatamDuaDialogOpen: Boolean = false
) {
    val completionPercentage: Float get() = (completedPages.toFloat() / totalQuranPages.toFloat()).coerceIn(0f, 1f)
    val pagesRemaining: Int get() = (totalQuranPages - completedPages).coerceAtLeast(0)
    val currentJuz: Int get() = ((completedPages / 20) + 1).coerceIn(1, 30)
    val pagesNeededPerDay: Double get() = (pagesRemaining.toDouble() / selectedTargetDays.coerceAtLeast(1).toDouble()).coerceAtLeast(1.0)
    val pagesNeededPerPrayer: Double get() = pagesNeededPerDay / 5.0
}

object KhatamPlannerData {

    val PRESETS = listOf(
        KhatamPlanPreset(
            id = "plan_30",
            title = "30-Day Ramadan Sprint",
            targetDays = 30,
            description = "Complete 1 full Juz daily. Read approximately 4 pages after each of the 5 daily prayers.",
            pagesPerDay = 20.1,
            pagesPerPrayer = 4.0
        ),
        KhatamPlanPreset(
            id = "plan_60",
            title = "60-Day Consistent Journey",
            targetDays = 60,
            description = "Complete half a Juz daily. Read 2 pages after each obligatory prayer.",
            pagesPerDay = 10.0,
            pagesPerPrayer = 2.0
        ),
        KhatamPlanPreset(
            id = "plan_90",
            title = "Quarterly Khatam (90 Days)",
            targetDays = 90,
            description = "Read approximately 7 pages per day (1 to 2 pages per prayer).",
            pagesPerDay = 6.7,
            pagesPerPrayer = 1.3
        ),
        KhatamPlanPreset(
            id = "plan_365",
            title = "Annual Khatam (1 Year)",
            targetDays = 365,
            description = "Read 1.6 pages daily (less than half a page per prayer) to complete the Quran every year.",
            pagesPerDay = 1.65,
            pagesPerPrayer = 0.33
        )
    )

    const val KHATM_AL_QURAN_DUA_ARABIC = """
اللَّهُمَّ ارْحَمْنِي بِالْقُرْآنِ، وَاجْعَلْهُ لِي إِمَامًا وَنُورًا وَهُدًى وَرَحْمَةً،
اللَّهُمَّ ذَكِّرْنِي مِنْهُ مَا نَسِيتُ، وَعَلِّمْنِي مِنْهُ مَا جَهِلْتُ،
وَارْزُقْنِي تِلَاوَتَهُ آنَاءَ اللَّيْلِ وَأَطْرَافَ النَّهَارِ،
وَاجْعَلْهُ لِي حُجَّةً يَا رَبَّ الْعَالَمِينَ.
"""

    const val KHATM_AL_QURAN_DUA_TRANSLATION = """
"O Allah, have mercy upon me through the Quran, and make it for me a leader, a light, guidance, and mercy.
O Allah, remind me from it what I have forgotten, and teach me from it what I was ignorant of,
and grant me its recitation during the hours of the night and the edges of the day,
and make it a proof for me, O Lord of all the worlds."
"""
}
