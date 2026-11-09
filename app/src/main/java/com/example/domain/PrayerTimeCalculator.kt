package com.example.domain

import java.util.Calendar
import java.util.TimeZone
import kotlin.math.*

data class PrayerSchedule(
    val cityName: String,
    val fajr: String,
    val sunrise: String,
    val dhuhr: String,
    val asr: String,
    val maghrib: String,
    val isha: String,
    val suhoorEnd: String, // Imsak (10 min before Fajr)
    val iftar: String,     // Sunset (Maghrib)
    val nextPrayerName: String,
    val timeRemainingNextPrayer: String,
    val timeRemainingIftar: String,
    val isFastingActive: Boolean,
    val fajrTimestamp: Long = 0L,
    val sunriseTimestamp: Long = 0L,
    val dhuhrTimestamp: Long = 0L,
    val asrTimestamp: Long = 0L,
    val maghribTimestamp: Long = 0L,
    val ishaTimestamp: Long = 0L,
    val currentPrayerName: String = "Fajr",
    val isUsingGpsLocation: Boolean = false,
    val calculationMethodName: String = "Karachi",
    val asrMethodName: String = "Standard"
)

data class CityPreset(
    val name: String,
    val country: String,
    val latitude: Double,
    val longitude: Double,
    val timezone: Double
)

enum class AsrJuristicMethod(val displayName: String, val shadowFactor: Double) {
    STANDARD("Standard (Shafi'i, Maliki, Hanbali)", 1.0),
    HANAFI("Hanafi (Double Shadow)", 2.0)
}

enum class CalculationMethod(val title: String, val fajrAngle: Double, val ishaAngle: Double) {
    KARACHI("University of Islamic Sciences, Karachi", 18.0, 18.0),
    MWL("Muslim World League", 18.0, 17.0),
    ISNA("Islamic Society of North America", 15.0, 15.0),
    UMM_AL_QURA("Umm al-Qura University, Makkah", 18.5, 19.0),
    EGYPT("Egyptian General Authority of Survey", 19.5, 17.5),
    DUBAI("Dubai Islamic Affairs & Awqaf", 18.2, 18.2),
    KUWAIT("Ministry of Awqaf, Kuwait", 18.0, 17.5),
    QATAR("Ministry of Awqaf, Qatar", 18.0, 18.0)
}

object PrayerTimeCalculator {

    val POPULAR_CITIES = listOf(
        CityPreset("Makkah", "Saudi Arabia", 21.4225, 39.8262, 3.0),
        CityPreset("Madinah", "Saudi Arabia", 24.4672, 39.6111, 3.0),
        CityPreset("Karachi", "Pakistan", 24.8607, 67.0011, 5.0),
        CityPreset("Lahore", "Pakistan", 31.5204, 74.3587, 5.0),
        CityPreset("Islamabad", "Pakistan", 33.6844, 73.0479, 5.0),
        CityPreset("London", "United Kingdom", 51.5074, -0.1278, 0.0),
        CityPreset("New York", "USA", 40.7128, -74.0060, -5.0),
        CityPreset("San Francisco", "USA", 37.7749, -122.4194, -8.0),
        CityPreset("Cairo", "Egypt", 30.0444, 31.2357, 2.0),
        CityPreset("Istanbul", "Turkey", 41.0082, 28.9784, 3.0),
        CityPreset("Dubai", "UAE", 25.2048, 55.2708, 4.0),
        CityPreset("Jakarta", "Indonesia", -6.2088, 106.8456, 7.0),
        CityPreset("Kuala Lumpur", "Malaysia", 3.1390, 101.6869, 8.0),
        CityPreset("Toronto", "Canada", 43.6532, -79.3832, -5.0),
        CityPreset("Sydney", "Australia", -33.8688, 151.2093, 10.0),
        CityPreset("Dhaka", "Bangladesh", 23.8103, 90.4125, 6.0)
    )

    fun calculateSchedule(
        city: CityPreset = POPULAR_CITIES[0],
        method: CalculationMethod = CalculationMethod.KARACHI,
        asrMethod: AsrJuristicMethod = AsrJuristicMethod.STANDARD,
        calendar: Calendar = Calendar.getInstance(),
        isUsingGpsLocation: Boolean = false
    ): PrayerSchedule {
        return calculateScheduleForCoordinates(
            latitude = city.latitude,
            longitude = city.longitude,
            timezone = city.timezone,
            cityName = city.name,
            method = method,
            asrMethod = asrMethod,
            calendar = calendar,
            isUsingGpsLocation = isUsingGpsLocation
        )
    }

    fun calculateScheduleForCoordinates(
        latitude: Double,
        longitude: Double,
        timezone: Double = (calendar.timeZone.rawOffset / 3600000.0),
        cityName: String = "Custom Location",
        method: CalculationMethod = CalculationMethod.KARACHI,
        asrMethod: AsrJuristicMethod = AsrJuristicMethod.STANDARD,
        calendar: Calendar = Calendar.getInstance(),
        isUsingGpsLocation: Boolean = false
    ): PrayerSchedule {
        val dayOfYear = calendar.get(Calendar.DAY_OF_YEAR)
        val lat = latitude
        val lng = longitude
        val tz = timezone

        // Astronomical calculations
        val d = (dayOfYear - 1).toDouble()
        // Solar declination (degrees)
        val decl = 23.45 * sin(Math.toRadians(360.0 / 365.0 * (d - 81.0)))
        // Equation of time (minutes)
        val b = Math.toRadians(360.0 / 365.0 * (d - 81.0))
        val eot = 9.87 * sin(2 * b) - 7.53 * cos(b) - 1.5 * sin(b)

        // Solar noon in hours
        val noon = 12.0 + (tz * 15.0 - lng) / 15.0 - eot / 60.0

        fun hourAngle(angle: Double): Double {
            val phi = Math.toRadians(lat)
            val delta = Math.toRadians(decl)
            val a = Math.toRadians(angle)
            val cosHA = (sin(a) - sin(phi) * sin(delta)) / (cos(phi) * cos(delta))
            return if (cosHA > 1.0) 0.0 else if (cosHA < -1.0) 180.0 else Math.toDegrees(acos(cosHA))
        }

        // Sunrise & Sunset angle is -0.8333°
        val sunHA = hourAngle(-0.8333) / 15.0
        val sunriseHour = noon - sunHA
        val maghribHour = noon + sunHA

        // Fajr
        val fajrHA = hourAngle(-method.fajrAngle) / 15.0
        val fajrHour = noon - fajrHA

        // Isha
        val ishaHA = hourAngle(-method.ishaAngle) / 15.0
        val ishaHour = noon + ishaHA

        // Dhuhr (4 min safety buffer after solar noon)
        val dhuhrHour = noon + (4.0 / 60.0)

        // Asr calculation based on Juristic School (1.0 for Shafi/Hanbali/Maliki, 2.0 for Hanafi)
        val phi = Math.toRadians(lat)
        val delta = Math.toRadians(decl)
        val asrAngle = Math.toDegrees(atan(1.0 / (asrMethod.shadowFactor + tan(abs(phi - delta)))))
        val asrHA = hourAngle(asrAngle) / 15.0
        val asrHour = noon + asrHA

        val suhoorHour = fajrHour - (10.0 / 60.0) // 10 min before Fajr

        fun toTimestamp(hourDouble: Double): Long {
            var h = hourDouble
            var dayOffset = 0
            while (h < 0) { h += 24.0; dayOffset -= 1 }
            while (h >= 24) { h -= 24.0; dayOffset += 1 }
            val totalMinutes = (h * 60).roundToInt()
            val hour = (totalMinutes / 60) % 24
            val min = totalMinutes % 60
            val cal = calendar.clone() as Calendar
            if (dayOffset != 0) cal.add(Calendar.DAY_OF_YEAR, dayOffset)
            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal.set(Calendar.MINUTE, min)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            return cal.timeInMillis
        }

        fun formatTime(hourDouble: Double): String {
            var h = hourDouble
            while (h < 0) h += 24.0
            while (h >= 24) h -= 24.0
            val totalMinutes = (h * 60).roundToInt()
            val hour = (totalMinutes / 60) % 24
            val min = totalMinutes % 60
            val amPm = if (hour >= 12) "PM" else "AM"
            val displayHour = when {
                hour == 0 -> 12
                hour > 12 -> hour - 12
                else -> hour
            }
            return "%d:%02d %s".format(displayHour, min, amPm)
        }

        // Current time in hours
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY) + calendar.get(Calendar.MINUTE) / 60.0 + calendar.get(Calendar.SECOND) / 3600.0

        val prayers = listOf(
            Triple("Fajr", fajrHour, formatTime(fajrHour)),
            Triple("Sunrise", sunriseHour, formatTime(sunriseHour)),
            Triple("Dhuhr", dhuhrHour, formatTime(dhuhrHour)),
            Triple("Asr", asrHour, formatTime(asrHour)),
            Triple("Maghrib", maghribHour, formatTime(maghribHour)),
            Triple("Isha", ishaHour, formatTime(ishaHour))
        )

        var nextName = "Fajr (Tomorrow)"
        var diffHours = (fajrHour + 24.0) - currentHour

        for ((name, hourVal, _) in prayers) {
            if (hourVal > currentHour) {
                nextName = name
                diffHours = hourVal - currentHour
                break
            }
        }

        // Determine current active prayer
        val currentPrayer = when {
            currentHour in fajrHour..sunriseHour -> "Fajr"
            currentHour in sunriseHour..dhuhrHour -> "Duha"
            currentHour in dhuhrHour..asrHour -> "Dhuhr"
            currentHour in asrHour..maghribHour -> "Asr"
            currentHour in maghribHour..ishaHour -> "Maghrib"
            else -> "Isha"
        }

        val remMins = (diffHours * 60).toInt()
        val remH = remMins / 60
        val remM = remMins % 60
        val timeRemainingStr = if (remH > 0) "${remH}h ${remM}m" else "${remM}m"

        // Fasting state (Fasting is active between Suhoor/Fajr and Maghrib)
        val isFastingActive = currentHour >= fajrHour && currentHour < maghribHour
        val iftarDiffHours = if (maghribHour > currentHour) {
            maghribHour - currentHour
        } else {
            (maghribHour + 24.0) - currentHour
        }
        val iftarMins = (iftarDiffHours * 60).toInt()
        val iftarStr = "%dh %dm".format(iftarMins / 60, iftarMins % 60)

        return PrayerSchedule(
            cityName = cityName,
            fajr = formatTime(fajrHour),
            sunrise = formatTime(sunriseHour),
            dhuhr = formatTime(dhuhrHour),
            asr = formatTime(asrHour),
            maghrib = formatTime(maghribHour),
            isha = formatTime(ishaHour),
            suhoorEnd = formatTime(suhoorHour),
            iftar = formatTime(maghribHour),
            nextPrayerName = nextName,
            timeRemainingNextPrayer = timeRemainingStr,
            timeRemainingIftar = iftarStr,
            isFastingActive = isFastingActive,
            fajrTimestamp = toTimestamp(fajrHour),
            sunriseTimestamp = toTimestamp(sunriseHour),
            dhuhrTimestamp = toTimestamp(dhuhrHour),
            asrTimestamp = toTimestamp(asrHour),
            maghribTimestamp = toTimestamp(maghribHour),
            ishaTimestamp = toTimestamp(ishaHour),
            currentPrayerName = currentPrayer,
            isUsingGpsLocation = isUsingGpsLocation,
            calculationMethodName = method.name,
            asrMethodName = asrMethod.name
        )
    }

    /**
     * Formats GPS latitude and longitude into human-readable coordinates (e.g., 24.8607° N, 67.0011° E)
     */
    fun formatCoordinates(latitude: Double, longitude: Double): String {
        val latDir = if (latitude >= 0) "N" else "S"
        val lngDir = if (longitude >= 0) "E" else "W"
        return "%.4f° %s, %.4f° %s".format(abs(latitude), latDir, abs(longitude), lngDir)
    }

    private data class PrayerInterval(
        val nextName: String,
        val nextTimeStr: String,
        val targetMs: Long,
        val startMs: Long,
        val currentName: String,
        val currentStartStr: String
    )

    /**
     * Computes real-time dynamic countdown to the upcoming prayer down to the exact second,
     * including hours, minutes, seconds, interval progress fraction (0f to 1f), and active prayer state.
     */
    fun getLiveCountdown(
        schedule: PrayerSchedule,
        nowMs: Long = System.currentTimeMillis()
    ): LivePrayerCountdown {
        val oneDayMs = 24 * 3600 * 1000L
        val fajr = schedule.fajrTimestamp
        val sunrise = schedule.sunriseTimestamp
        val dhuhr = schedule.dhuhrTimestamp
        val asr = schedule.asrTimestamp
        val maghrib = schedule.maghribTimestamp
        val isha = schedule.ishaTimestamp

        // Safety fallback if timestamps are not yet populated
        if (fajr <= 0L || dhuhr <= 0L || asr <= 0L || maghrib <= 0L || isha <= 0L) {
            return LivePrayerCountdown(
                nextPrayerName = schedule.nextPrayerName.ifBlank { "Next Prayer" },
                nextPrayerTimeFormatted = schedule.dhuhr,
                remainingMillis = 0L,
                hours = 0L,
                minutes = 0L,
                seconds = 0L,
                formattedHMS = "00:00:00",
                progressFraction = 0f,
                currentPrayerName = schedule.currentPrayerName.ifBlank { "Prayer Time" },
                currentPrayerStartTime = "",
                isFastingActive = schedule.isFastingActive,
                fastingCountdownLabel = if (schedule.isFastingActive) "Iftar in" else "Suhoor ends in",
                fastingCountdownHMS = "00:00:00"
            )
        }

        val interval = when {
            nowMs < fajr -> {
                // Before Fajr today: current is previous night's Isha / Tahajjud
                val yesterdayIsha = isha - oneDayMs
                PrayerInterval("Fajr", schedule.fajr, fajr, yesterdayIsha, "Tahajjud / Qiyam", schedule.isha)
            }
            nowMs in fajr until sunrise -> {
                PrayerInterval("Sunrise", schedule.sunrise, sunrise, fajr, "Fajr", schedule.fajr)
            }
            nowMs in sunrise until dhuhr -> {
                PrayerInterval("Dhuhr", schedule.dhuhr, dhuhr, sunrise, "Duha (Ishraq)", schedule.sunrise)
            }
            nowMs in dhuhr until asr -> {
                PrayerInterval("Asr", schedule.asr, asr, dhuhr, "Dhuhr", schedule.dhuhr)
            }
            nowMs in asr until maghrib -> {
                PrayerInterval("Maghrib", schedule.maghrib, maghrib, asr, "Asr", schedule.asr)
            }
            nowMs in maghrib until isha -> {
                PrayerInterval("Isha", schedule.isha, isha, maghrib, "Maghrib", schedule.maghrib)
            }
            else -> {
                // After Isha: next is tomorrow's Fajr
                val tomorrowFajr = fajr + oneDayMs
                PrayerInterval("Fajr (Tomorrow)", schedule.fajr, tomorrowFajr, isha, "Isha", schedule.isha)
            }
        }

        val remainingMs = (interval.targetMs - nowMs).coerceAtLeast(0L)
        val totalSec = remainingMs / 1000L
        val h = totalSec / 3600L
        val m = (totalSec % 3600L) / 60L
        val s = totalSec % 60L
        val hms = "%02d:%02d:%02d".format(h, m, s)

        val totalIntervalMs = (interval.targetMs - interval.startMs).coerceAtLeast(1L)
        val elapsedMs = (nowMs - interval.startMs).coerceAtLeast(0L)
        val fraction = (elapsedMs.toDouble() / totalIntervalMs.toDouble()).toFloat().coerceIn(0f, 1f)

        // Fasting countdown (Suhoor or Iftar)
        val isFasting = nowMs in fajr until maghrib
        val (fastLabel, fastTargetMs) = if (isFasting) {
            "Iftar in" to maghrib
        } else {
            val nextFajr = if (nowMs < fajr) fajr else fajr + oneDayMs
            "Suhoor ends in" to nextFajr
        }
        val fastRemMs = (fastTargetMs - nowMs).coerceAtLeast(0L)
        val fastSec = fastRemMs / 1000L
        val fastH = fastSec / 3600L
        val fastM = (fastSec % 3600L) / 60L
        val fastS = fastSec % 60L
        val fastHms = "%02d:%02d:%02d".format(fastH, fastM, fastS)

        return LivePrayerCountdown(
            nextPrayerName = interval.nextName,
            nextPrayerTimeFormatted = interval.nextTimeStr,
            remainingMillis = remainingMs,
            hours = h,
            minutes = m,
            seconds = s,
            formattedHMS = hms,
            progressFraction = fraction,
            currentPrayerName = interval.currentName,
            currentPrayerStartTime = interval.currentStartStr,
            isFastingActive = isFasting,
            fastingCountdownLabel = fastLabel,
            fastingCountdownHMS = fastHms
        )
    }
}

data class LivePrayerCountdown(
    val nextPrayerName: String,
    val nextPrayerTimeFormatted: String,
    val remainingMillis: Long,
    val hours: Long,
    val minutes: Long,
    val seconds: Long,
    val formattedHMS: String,
    val progressFraction: Float,
    val currentPrayerName: String,
    val currentPrayerStartTime: String,
    val isFastingActive: Boolean,
    val fastingCountdownLabel: String,
    val fastingCountdownHMS: String
)

