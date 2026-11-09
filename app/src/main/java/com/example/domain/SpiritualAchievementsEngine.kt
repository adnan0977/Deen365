package com.example.domain

import com.example.domain.model.ModuleProgressSummary
import com.example.domain.model.SpiritualBadge
import com.example.domain.model.SpiritualGoals
import com.example.ui.viewmodel.DeenUiState

object SpiritualAchievementsEngine {

    /**
     * Computes real-time progress across the 5 core modules:
     * 1. Quranic studies
     * 2. Hadith learning
     * 3. Dua memorization
     * 4. Understanding Namaz
     * 5. Fasting (Sawm)
     */
    fun computeProgressSummary(
        state: DeenUiState,
        goals: SpiritualGoals
    ): ModuleProgressSummary {
        // 1. Quranic studies
        // Base from reading history / bookmarks
        val quranAyahsRead = state.quranGoalBookmarks.size.coerceAtLeast(state.completedActionSteps.size * 5)
        val quranGoal = goals.dailyQuranAyahsTarget.coerceAtLeast(1)
        val quranFraction = (quranAyahsRead.toFloat() / quranGoal.toFloat()).coerceIn(0f, 1f)

        // 2. Hadith learning
        val hadithsStudied = state.hadithStudiedCount.coerceAtLeast(state.currentHadiths.count { it.isRead || it.lastStudiedTimestamp > 0L })
        val hadithGoal = goals.weeklyHadithsToStudy.coerceAtLeast(1)
        val hadithFraction = (hadithsStudied.toFloat() / hadithGoal.toFloat()).coerceIn(0f, 1f)

        // 3. Dua memorization
        val duasMemorized = state.duaMemorizedCount.coerceAtLeast(state.duaEntities.count { it.isMemorized })
        val duaGoal = goals.monthlyDuasToMemorize.coerceAtLeast(1)
        val duaFraction = (duasMemorized.toFloat() / duaGoal.toFloat()).coerceIn(0f, 1f)

        // 4. Understanding Namaz
        val salahOffered = state.prayerRecord.completedCount
        val salahGoal = goals.dailySalahTarget.coerceAtLeast(1)
        val salahFraction = (salahOffered.toFloat() / salahGoal.toFloat()).coerceIn(0f, 1f)
        val stepsUnderstood = goals.salahMeaningUnderstandingCompleted

        // 5. Fasting
        val fastingDays = state.totalFastedDaysCount.coerceAtLeast(if (state.isFastedToday) 1 else 0)
        val fastingGoal = goals.monthlyFastingTargetDays.coerceAtLeast(1)
        val fastingFraction = (fastingDays.toFloat() / fastingGoal.toFloat()).coerceIn(0f, 1f)

        // Vitality score (weighted combination)
        val overallScore = ((salahFraction * 0.30f +
                quranFraction * 0.25f +
                hadithFraction * 0.15f +
                duaFraction * 0.15f +
                fastingFraction * 0.15f) * 100f).toInt().coerceIn(5, 100)

        val rankTitle = when {
            overallScore >= 85 -> "Muttaqi (Steadfast)"
            overallScore >= 65 -> "Murid (Committed Seeker)"
            overallScore >= 40 -> "Devoted Believer"
            else -> "Knowledge Seeker"
        }

        val allBadges = computeBadges(state, goals)
        val unlockedCount = allBadges.count { it.isUnlocked }

        return ModuleProgressSummary(
            quranAyahsReadToday = quranAyahsRead,
            quranAyahsGoal = quranGoal,
            quranSurahsCompleted = state.surahList.count { it.number <= 3 },
            quranProgressPercentage = quranFraction,
            hadithsStudiedCount = hadithsStudied,
            hadithsStudiedGoal = hadithGoal,
            hadithsMemorizedCount = state.hadithMemorizedCount,
            hadithProgressPercentage = hadithFraction,
            duasMemorizedCount = duasMemorized,
            duasMemorizedGoal = duaGoal,
            duasFavoritedCount = state.duaFavoriteCount,
            duaProgressPercentage = duaFraction,
            salahOfferedToday = salahOffered,
            salahStreakDays = state.prayerStats.currentStreakDays,
            salahStepsUnderstood = stepsUnderstood,
            salahProgressPercentage = salahFraction,
            fastingDaysThisMonth = fastingDays,
            fastingGoalDays = fastingGoal,
            fastingStreak = state.fastingStreak,
            fastingProgressPercentage = fastingFraction,
            overallSpiritualVitalityScore = overallScore,
            spiritualRankTitle = rankTitle,
            unlockedBadgesCount = unlockedCount,
            totalBadgesCount = allBadges.size
        )
    }

    /**
     * Evaluates and builds the list of 10 spiritual achievements.
     */
    fun computeBadges(
        state: DeenUiState,
        goals: SpiritualGoals
    ): List<SpiritualBadge> {
        val salahOffered = state.prayerRecord.completedCount
        val salahStreak = state.prayerStats.currentStreakDays
        val hadithsStudied = state.hadithStudiedCount
        val hadithsMemorized = state.hadithMemorizedCount
        val duasMemorized = state.duaMemorizedCount
        val fastingDays = state.totalFastedDaysCount
        val tasbihCount = state.totalTasbihCount
        val stepsUnderstood = goals.salahMeaningUnderstandingCompleted

        return listOf(
            SpiritualBadge(
                id = "badge_salah_guardian",
                title = "Salah Guardian",
                arabicTitle = "حافظ على الصلوات",
                description = "Complete all 5 daily obligatory prayers on time.",
                iconEmoji = "🌟",
                category = "Salah",
                isUnlocked = salahOffered >= 5,
                currentProgress = salahOffered,
                targetRequired = 5,
                unit = "prayers",
                unlockedDateFormatted = if (salahOffered >= 5) "Today" else null
            ),
            SpiritualBadge(
                id = "badge_steadfast_musalli",
                title = "Steadfast Musalli",
                arabicTitle = "المداوم على الصلاة",
                description = "Maintain a 7-day consistent prayer streak.",
                iconEmoji = "🛡️",
                category = "Consistency",
                isUnlocked = salahStreak >= 7,
                currentProgress = salahStreak,
                targetRequired = 7,
                unit = "days",
                unlockedDateFormatted = if (salahStreak >= 7) "Active Streak" else null
            ),
            SpiritualBadge(
                id = "badge_quran_seeker",
                title = "Light of Quran",
                arabicTitle = "نور القرآن",
                description = "Read 50 or more verses in the Quran explorer.",
                iconEmoji = "📖",
                category = "Quran",
                isUnlocked = state.quranGoalBookmarks.size >= 10 || state.surahList.isNotEmpty(),
                currentProgress = (state.quranGoalBookmarks.size * 5).coerceAtMost(50),
                targetRequired = 50,
                unit = "ayahs",
                unlockedDateFormatted = if (state.quranGoalBookmarks.size >= 10) "Completed" else null
            ),
            SpiritualBadge(
                id = "badge_hadith_seeker",
                title = "Hadith Seeker",
                arabicTitle = "طالب الحديث",
                description = "Study 5 authentic Hadiths from Imam Nawawi or Sahih collections.",
                iconEmoji = "📜",
                category = "Hadith",
                isUnlocked = hadithsStudied >= 5,
                currentProgress = hadithsStudied,
                targetRequired = 5,
                unit = "hadiths",
                unlockedDateFormatted = if (hadithsStudied >= 5) "Unlocked" else null
            ),
            SpiritualBadge(
                id = "badge_hadith_memorizer",
                title = "Hadith Memorizer",
                arabicTitle = "حافظ السنن",
                description = "Commit 3 or more Prophetic Hadiths to heart.",
                iconEmoji = "🧠",
                category = "Hadith",
                isUnlocked = hadithsMemorized >= 3,
                currentProgress = hadithsMemorized,
                targetRequired = 3,
                unit = "memorized",
                unlockedDateFormatted = if (hadithsMemorized >= 3) "Unlocked" else null
            ),
            SpiritualBadge(
                id = "badge_dua_hisn",
                title = "Shield of Remembrance",
                arabicTitle = "حصن المسلم",
                description = "Memorize 4 essential daily Duas from the Sunnah.",
                iconEmoji = "🤲",
                category = "Dua",
                isUnlocked = duasMemorized >= 4,
                currentProgress = duasMemorized,
                targetRequired = 4,
                unit = "duas",
                unlockedDateFormatted = if (duasMemorized >= 4) "Unlocked" else null
            ),
            SpiritualBadge(
                id = "badge_sawm_devotee",
                title = "Sawm Devotee",
                arabicTitle = "الصائم لله",
                description = "Fast 3 voluntary Sunnah or White Days.",
                iconEmoji = "🌙",
                category = "Fasting",
                isUnlocked = fastingDays >= 3,
                currentProgress = fastingDays,
                targetRequired = 3,
                unit = "days",
                unlockedDateFormatted = if (fastingDays >= 3) "Unlocked" else null
            ),
            SpiritualBadge(
                id = "badge_zakir_tasbih",
                title = "Constant Zakir",
                arabicTitle = "الذاكرون الله كثيراً",
                description = "Complete 500 Tasbih dhikr counts using the digital counter.",
                iconEmoji = "📿",
                category = "Dua",
                isUnlocked = tasbihCount >= 500,
                currentProgress = tasbihCount,
                targetRequired = 500,
                unit = "dhikr",
                unlockedDateFormatted = if (tasbihCount >= 500) "Unlocked" else null
            ),
            SpiritualBadge(
                id = "badge_kaaba_aligned",
                title = "Kaaba Seeker",
                arabicTitle = "متوجه إلى القبلة",
                description = "Align directly to the Qibla using the live sensor compass.",
                iconEmoji = "🕋",
                category = "Salah",
                isUnlocked = state.isQiblaAligned,
                currentProgress = if (state.isQiblaAligned) 1 else 0,
                targetRequired = 1,
                unit = "aligned",
                unlockedDateFormatted = if (state.isQiblaAligned) "Aligned" else null
            ),
            SpiritualBadge(
                id = "badge_khushu_mastery",
                title = "Salah Understanding",
                arabicTitle = "فهم معاني الصلاة",
                description = "Master understanding the Arabic meaning of 5 Salah positions.",
                iconEmoji = "📚",
                category = "Salah",
                isUnlocked = stepsUnderstood >= 5,
                currentProgress = stepsUnderstood,
                targetRequired = 5,
                unit = "steps",
                unlockedDateFormatted = if (stepsUnderstood >= 5) "Mastered" else null
            )
        )
    }
}
