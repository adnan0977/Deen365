package com.example.domain

import java.util.Calendar

data class HijriDate(
    val day: Int,
    val monthName: String,
    val monthIndex: Int, // 1 to 12
    val year: Int,
    val gregorianFormatted: String,
    val dayOfWeek: String = "",
    val isSunnahFast: Boolean = false,
    val isWhiteDay: Boolean = false,
    val fastingOccasion: String? = null
)

object IslamicCalendarHelper {
    val ISLAMIC_MONTHS = listOf(
        "Muharram", "Safar", "Rabi' al-Awwal", "Rabi' al-Thani",
        "Jumada al-Awwal", "Jumada al-Thani", "Rajab", "Sha'ban",
        "Ramadan", "Shawwal", "Dhu al-Qi'dah", "Dhu al-Hijjah"
    )

    private val GREGORIAN_MONTHS = listOf(
        "Jan", "Feb", "Mar", "Apr", "May", "Jun",
        "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
    )

    private val DAYS_OF_WEEK = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")

    fun getTodayHijriDate(offsetDays: Int = 0): HijriDate {
        val cal = Calendar.getInstance()
        if (offsetDays != 0) {
            cal.add(Calendar.DAY_OF_YEAR, offsetDays)
        }
        return computeHijriFromCalendar(cal)
    }

    fun getMonthDays(offsetDays: Int = 0): List<HijriDate> {
        val today = Calendar.getInstance()
        val list = mutableListOf<HijriDate>()
        // Return next 30 days starting from today - 2 days
        val startCal = Calendar.getInstance()
        startCal.add(Calendar.DAY_OF_YEAR, -2)

        for (i in 0 until 35) {
            val iterCal = startCal.clone() as Calendar
            iterCal.add(Calendar.DAY_OF_YEAR, i + offsetDays)
            list.add(computeHijriFromCalendar(iterCal))
        }
        return list
    }

    fun getHijriDateForString(dateStr: String): HijriDate? {
        return try {
            val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
            val date = sdf.parse(dateStr) ?: return null
            val cal = Calendar.getInstance().apply { time = date }
            computeHijriFromCalendar(cal)
        } catch (e: Exception) {
            null
        }
    }

    private fun computeHijriFromCalendar(cal: Calendar): HijriDate {
        val gYear = cal.get(Calendar.YEAR)
        val gMonth = cal.get(Calendar.MONTH) // 0-indexed
        val gDay = cal.get(Calendar.DAY_OF_MONTH)
        val dayOfWeekIndex = cal.get(Calendar.DAY_OF_WEEK) - 1 // 0 for Sun
        val dayOfWeek = DAYS_OF_WEEK[dayOfWeekIndex.coerceIn(0, 6)]

        // Astronomical Julian Day Number calculation
        val a = (14 - (gMonth + 1)) / 12
        val y = gYear + 4800 - a
        val m = (gMonth + 1) + 12 * a - 3
        val jdn = gDay + (153 * m + 2) / 5 + 365 * y + y / 4 - y / 100 + y / 400 - 32045

        // Hijri algorithm conversion
        val l = jdn - 1948440 + 10632
        val n = ((l - 1) / 10631).toInt()
        val lPrime = l - 10631 * n + 354
        val j = (((10985 - lPrime) / 5316)).toInt() * (((50 * lPrime) / 17719)).toInt() +
                ((lPrime / 5670)).toInt() * (((43 * lPrime) / 15238)).toInt()
        val lDoublePrime = lPrime - (((30 - j) / 15)).toInt() * (((17719 * j) / 50)).toInt() -
                ((j / 16)).toInt() * (((15238 * j) / 43)).toInt() + 29
        val mHijri = (((24 * lDoublePrime) / 709)).toInt()
        val dHijri = (lDoublePrime - (((709 * mHijri) / 24)).toInt()).coerceIn(1, 30)
        val yHijri = 30 * n + j - 30

        val monthIndex = ((mHijri - 1).coerceIn(0, 11))
        val monthName = ISLAMIC_MONTHS[monthIndex]
        val gregStr = "${GREGORIAN_MONTHS[gMonth]} $gDay, $gYear"

        val isMondayOrThursday = dayOfWeek == "Mon" || dayOfWeek == "Thu"
        val isWhiteDay = dHijri in 13..15
        val isRamadan = monthName == "Ramadan"

        val occasion = when {
            isRamadan -> "Ramadan Fasting"
            monthName == "Muharram" && dHijri == 10 -> "Ashura Fast"
            monthName == "Muharram" && dHijri == 9 -> "Tasu'a (9th Muharram)"
            monthName == "Dhu al-Hijjah" && dHijri == 9 -> "Day of Arafah"
            monthName == "Shawwal" && dHijri in 2..7 -> "Six Days of Shawwal"
            isWhiteDay -> "White Day (Ayyam al-Beed)"
            isMondayOrThursday -> "Sunnah Fast (Mon/Thu)"
            else -> null
        }

        return HijriDate(
            day = dHijri,
            monthName = monthName,
            monthIndex = monthIndex + 1,
            year = yHijri,
            gregorianFormatted = gregStr,
            dayOfWeek = dayOfWeek,
            isSunnahFast = isMondayOrThursday || isWhiteDay || occasion != null,
            isWhiteDay = isWhiteDay,
            fastingOccasion = occasion
        )
    }
}
