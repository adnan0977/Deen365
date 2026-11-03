package com.example.data.repository

import android.util.Log
import com.example.data.local.PrayerTimeDao
import com.example.data.local.PrayerTimeEntity
import com.example.domain.CalculationMethod
import com.example.domain.PrayerSchedule
import com.example.domain.PrayerTimeCalculator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.TimeZone

class PrayerTimesRepository(
    private val prayerTimeDao: PrayerTimeDao
) {
    private val TAG = "PrayerTimesRepo"

    fun getSavedPrayerTimes(dateStr: String): Flow<PrayerTimeEntity?> {
        return prayerTimeDao.getPrayerTimesForDate(dateStr)
    }

    suspend fun getSavedPrayerTimesSync(dateStr: String): PrayerTimeEntity? = withContext(Dispatchers.IO) {
        prayerTimeDao.getPrayerTimesForDateSync(dateStr)
    }

    fun getAllSavedPrayerTimes(): Flow<List<PrayerTimeEntity>> {
        return prayerTimeDao.getAllSavedPrayerTimes()
    }

    suspend fun calculateAndStorePrayerTimes(
        latitude: Double,
        longitude: Double,
        timezone: Double = (TimeZone.getDefault().rawOffset / 3600000.0),
        cityName: String = "Custom Location",
        method: CalculationMethod = CalculationMethod.KARACHI,
        calendar: Calendar = Calendar.getInstance()
    ): PrayerSchedule = withContext(Dispatchers.IO) {
        val schedule = PrayerTimeCalculator.calculateScheduleForCoordinates(
            latitude = latitude,
            longitude = longitude,
            timezone = timezone,
            cityName = cityName,
            method = method,
            calendar = calendar
        )

        val dateStr = "%04d-%02d-%02d".format(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH) + 1,
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        val entityId = "$dateStr-${(latitude * 100).toInt()}-${(longitude * 100).toInt()}"
        val entity = PrayerTimeEntity(
            id = entityId,
            dateStr = dateStr,
            latitude = latitude,
            longitude = longitude,
            cityName = cityName,
            calculationMethod = method.name,
            fajr = schedule.fajr,
            sunrise = schedule.sunrise,
            dhuhr = schedule.dhuhr,
            asr = schedule.asr,
            maghrib = schedule.maghrib,
            isha = schedule.isha,
            suhoorEnd = schedule.suhoorEnd,
            iftar = schedule.iftar,
            updatedAt = System.currentTimeMillis()
        )

        try {
            prayerTimeDao.insertOrUpdatePrayerTimes(entity)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to persist daily prayer times: ${e.message}", e)
        }

        schedule
    }
}
