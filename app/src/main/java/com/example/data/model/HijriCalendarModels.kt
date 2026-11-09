package com.example.data.model

import java.util.Calendar

/**
 * Islamic Hijri Events, Sacred Occasions & Sunnah Fasting Models.
 */

data class HijriMonthInfo(
    val monthNumber: Int, // 1 to 12
    val englishName: String,
    val arabicName: String,
    val isSacredMonth: Boolean = false, // Al-Ashhur al-Hurum (Dhul Qa'dah, Dhul Hijjah, Muharram, Rajab)
    val totalDays: Int = 30
)

enum class SacredOccasionType {
    RAMADAN_START,
    LAYLAT_AL_QADR,
    EID_AL_FITR,
    DAY_OF_ARAFAH,
    EID_AL_ADHA,
    ASHURA,
    ISLAMIC_NEW_YEAR,
    ISRA_WAL_MIRAJ,
    MID_SHABAN,
    MAWLID_AL_NABI
}

data class SacredOccasion(
    val id: String,
    val title: String,
    val arabicTitle: String,
    val hijriMonth: Int,
    val hijriDay: Int,
    val description: String,
    val virtues: String,
    val type: SacredOccasionType
) {
    /**
     * Approximate days remaining from a given current Hijri month and day.
     */
    fun daysRemaining(currentMonth: Int, currentDay: Int): Int {
        val targetDayOfYear = (hijriMonth - 1) * 30 + hijriDay
        val currentDayOfYear = (currentMonth - 1) * 30 + currentDay
        val diff = targetDayOfYear - currentDayOfYear
        return if (diff >= 0) diff else diff + 354
    }
}

enum class SunnahFastCategory {
    WHITE_DAYS,        // Ayyam al-Beed: 13th, 14th, 15th of lunar month
    MONDAY_THURSDAY,   // Weekly Sunnah
    SHAWWAL_SIX,       // 6 days of Shawwal following Ramadan
    DHUL_HIJJAH_NINE,  // First 9 days of Dhul Hijjah
    ASHURA             // 9th and 10th of Muharram
}

data class SunnahFastDay(
    val id: String,
    val title: String,
    val category: SunnahFastCategory,
    val hijriMonth: Int,
    val hijriDay: Int,
    val gregorianDateFormatted: String,
    val dayOfWeek: String,
    val isFasted: Boolean = false,
    val propheticVirtue: String
)

data class HijriCalendarState(
    val currentHijriYear: Int = 1447,
    val currentHijriMonth: Int = 9, // Ramadan default example
    val currentHijriDay: Int = 15,
    val manualOffsetDays: Int = 0, // -2 to +2 for local moonsighting adjustments
    val selectedViewMonth: Int = 9,
    val completedFastIds: Set<String> = emptySet(),
    val fastingStreakDays: Int = 4,
    val selectedSacredOccasion: SacredOccasion? = null
)

object HijriCalendarData {

    val HIJRI_MONTHS = listOf(
        HijriMonthInfo(1, "Muharram", "الْمُحَرَّم", isSacredMonth = true),
        HijriMonthInfo(2, "Safar", "صَفَر", isSacredMonth = false),
        HijriMonthInfo(3, "Rabi' al-Awwal", "رَبِيع الْأَوَّل", isSacredMonth = false),
        HijriMonthInfo(4, "Rabi' al-Thani", "رَبِيع الثَّانِي", isSacredMonth = false),
        HijriMonthInfo(5, "Jumada al-Ula", "جُمَادَىٰ الْأُولَىٰ", isSacredMonth = false),
        HijriMonthInfo(6, "Jumada al-Akhirah", "جُمَادَىٰ الْآخِرَة", isSacredMonth = false),
        HijriMonthInfo(7, "Rajab", "رَجَب", isSacredMonth = true),
        HijriMonthInfo(8, "Sha'ban", "شَعْبَان", isSacredMonth = false),
        HijriMonthInfo(9, "Ramadan", "رَمَضَان", isSacredMonth = false),
        HijriMonthInfo(10, "Shawwal", "شَوَّال", isSacredMonth = false),
        HijriMonthInfo(11, "Dhul Qa'dah", "ذُو الْقَعْدَة", isSacredMonth = true),
        HijriMonthInfo(12, "Dhul Hijjah", "ذُو الْحِجَّة", isSacredMonth = true)
    )

    val SACRED_OCCASIONS = listOf(
        SacredOccasion(
            id = "islamic_new_year",
            title = "Islamic New Year",
            arabicTitle = "رَأْس السَّنَة الْهِجْرِيَّة",
            hijriMonth = 1,
            hijriDay = 1,
            description = "Commemoration of the historic Hijrah migration of Prophet Muhammad (ﷺ) from Makkah to Madinah.",
            virtues = "Marks the beginning of the blessed sacred month of Muharram.",
            type = SacredOccasionType.ISLAMIC_NEW_YEAR
        ),
        SacredOccasion(
            id = "ashura",
            title = "Day of Ashura",
            arabicTitle = "يَوْم عَاشُورَاء",
            hijriMonth = 1,
            hijriDay = 10,
            description = "The day Allah delivered Prophet Musa (Moses) and the Children of Israel from Pharaoh.",
            virtues = "Fasting Ashura expiates the minor sins of the preceding year (Sahih Muslim #1162).",
            type = SacredOccasionType.ASHURA
        ),
        SacredOccasion(
            id = "isra_miraj",
            title = "Al-Isra' wal-Mi'raj",
            arabicTitle = "الْإِسْرَاء وَالْمِعْرَاج",
            hijriMonth = 7,
            hijriDay = 27,
            description = "The miraculous Night Journey and Heavenly Ascension wherein the 5 daily prayers were ordained.",
            virtues = "Remembrance of the sanctity of Masjid al-Aqsa and divine nearness.",
            type = SacredOccasionType.ISRA_WAL_MIRAJ
        ),
        SacredOccasion(
            id = "mid_shaban",
            title = "Nisfu Sha'ban (Night of Forgiveness)",
            arabicTitle = "لَيْلَة النِّصْف مِن شَعْبَان",
            hijriMonth = 8,
            hijriDay = 15,
            description = "The midpoint night of Sha'ban where divine mercy descends in abundance.",
            virtues = "Prophetic encouragement for extra repentance, night prayers, and fasting.",
            type = SacredOccasionType.MID_SHABAN
        ),
        SacredOccasion(
            id = "ramadan_start",
            title = "First Day of Ramadan",
            arabicTitle = "أَوَّل رَمَضَان الْمُبَارَك",
            hijriMonth = 9,
            hijriDay = 1,
            description = "The commencement of the obligatory month of fasting, revelation of the Quran, and spiritual renewal.",
            virtues = "The gates of Paradise are opened, the gates of Hell are shut, and devils chained (Bukhari #1899).",
            type = SacredOccasionType.RAMADAN_START
        ),
        SacredOccasion(
            id = "laylat_al_qadr",
            title = "Laylat al-Qadr (Night of Decree)",
            arabicTitle = "لَيْلَة الْقَدْر",
            hijriMonth = 9,
            hijriDay = 27,
            description = "The pinnacle night in the odd nights of the last ten days of Ramadan.",
            virtues = "Worship therein is superior to a thousand months (Surah Al-Qadr 97:3).",
            type = SacredOccasionType.LAYLAT_AL_QADR
        ),
        SacredOccasion(
            id = "eid_al_fitr",
            title = "Eid al-Fitr",
            arabicTitle = "عِيد الْفِطْر الْمُبَارَك",
            hijriMonth = 10,
            hijriDay = 1,
            description = "Festival of fast-breaking celebrating the completion of Ramadan with gratitude and charity (Zakat al-Fitr).",
            virtues = "Day of joyful thanksgiving and community prayers across the Ummah.",
            type = SacredOccasionType.EID_AL_FITR
        ),
        SacredOccasion(
            id = "day_of_arafah",
            title = "Day of Arafah",
            arabicTitle = "يَوْم عَرَفَة",
            hijriMonth = 12,
            hijriDay = 9,
            description = "The climax of the Hajj pilgrimage where pilgrims stand on Mount Arafat beseeching Allah.",
            virtues = "Fasting on Arafah expiates sins of the past and coming year for non-pilgrims (Muslim #1162).",
            type = SacredOccasionType.DAY_OF_ARAFAH
        ),
        SacredOccasion(
            id = "eid_al_adha",
            title = "Eid al-Adha",
            arabicTitle = "عِيد الْأَضْحَىٰ الْمُبَارَك",
            hijriMonth = 12,
            hijriDay = 10,
            description = "Festival of the Sacrifice honoring the devotion of Prophet Ibrahim (Abraham) and Ismail (Ishmael).",
            virtues = "Four days of Eid and Tashreeq filled with remembrance of Allah (Takbeerat).",
            type = SacredOccasionType.EID_AL_ADHA
        )
    )

    fun getUpcomingSacredOccasion(currentMonth: Int, currentDay: Int): SacredOccasion {
        return SACRED_OCCASIONS.minByOrNull { it.daysRemaining(currentMonth, currentDay) } ?: SACRED_OCCASIONS[0]
    }

    fun getSunnahFastsForMonth(month: Int): List<SunnahFastDay> {
        val list = mutableListOf<SunnahFastDay>()

        // 1. White Days (13th, 14th, 15th)
        val monthName = HIJRI_MONTHS.getOrNull(month - 1)?.englishName ?: "Month"
        listOf(13, 14, 15).forEach { day ->
            list.add(
                SunnahFastDay(
                    id = "fast_${month}_$day",
                    title = "White Day (Ayyam al-Beed) #$day",
                    category = SunnahFastCategory.WHITE_DAYS,
                    hijriMonth = month,
                    hijriDay = day,
                    gregorianDateFormatted = "$day $monthName",
                    dayOfWeek = "Lunar Midpoint",
                    propheticVirtue = "Fasting 3 days every month is equivalent to fasting the entire lifetime (Bukhari #1975)."
                )
            )
        }

        // 2. Mondays & Thursdays
        listOf(4, 7, 11, 18, 21, 25).forEach { day ->
            val dayName = if (day % 2 == 0) "Thursday" else "Monday"
            list.add(
                SunnahFastDay(
                    id = "fast_${month}_${day}_$dayName",
                    title = "Sunnah $dayName Fast",
                    category = SunnahFastCategory.MONDAY_THURSDAY,
                    hijriMonth = month,
                    hijriDay = day,
                    gregorianDateFormatted = "$day $monthName",
                    dayOfWeek = dayName,
                    propheticVirtue = "Deeds are presented to Allah on Mondays and Thursdays, and I love that my deeds be presented while I am fasting (Tirmidhi #747)."
                )
            )
        }

        return list
    }
}
