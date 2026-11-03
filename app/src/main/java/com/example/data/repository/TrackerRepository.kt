package com.example.data.repository

import com.example.data.local.FastingRecordEntity
import com.example.data.local.PrayerRecordEntity
import com.example.data.local.TrackerDao
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TrackerRepository(private val trackerDao: TrackerDao) {

    fun getTodayDateStr(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    fun getPrayerRecord(dateStr: String = getTodayDateStr()): Flow<PrayerRecordEntity?> {
        return trackerDao.getPrayerRecord(dateStr)
    }

    fun getAllPrayerRecords(): Flow<List<PrayerRecordEntity>> {
        return trackerDao.getAllPrayerRecords()
    }

    suspend fun getAllPrayerRecordsSync(): List<PrayerRecordEntity> {
        return trackerDao.getAllPrayerRecordsSync()
    }

    suspend fun togglePrayer(prayerName: String, dateStr: String = getTodayDateStr()) {
        val current = trackerDao.getPrayerRecordSync(dateStr) ?: PrayerRecordEntity(dateStr = dateStr)
        val now = System.currentTimeMillis()
        val updated = when (prayerName.lowercase()) {
            "fajr" -> current.copy(fajr = !current.fajr, updatedAt = now, isSynced = false)
            "dhuhr" -> current.copy(dhuhr = !current.dhuhr, updatedAt = now, isSynced = false)
            "asr" -> current.copy(asr = !current.asr, updatedAt = now, isSynced = false)
            "maghrib" -> current.copy(maghrib = !current.maghrib, updatedAt = now, isSynced = false)
            "isha" -> current.copy(isha = !current.isha, updatedAt = now, isSynced = false)
            else -> current
        }
        trackerDao.insertOrUpdatePrayerRecord(updated)
    }

    suspend fun savePrayerRecord(record: PrayerRecordEntity) {
        trackerDao.insertOrUpdatePrayerRecord(record.copy(updatedAt = System.currentTimeMillis(), isSynced = false))
    }

    fun calculateHistoricalStats(records: List<PrayerRecordEntity>): com.example.sync.HistoricalPrayerStats {
        if (records.isEmpty()) return com.example.sync.HistoricalPrayerStats()

        val totalDays = records.size
        val totalPossible = totalDays * 5
        var totalPrayed = 0
        var fajr = 0
        var dhuhr = 0
        var asr = 0
        var maghrib = 0
        var isha = 0
        var perfectDays = 0

        // Sorted ascending by date for streak calculation
        val sortedAsc = records.sortedBy { it.dateStr }
        var currentStreak = 0
        var bestStreak = 0
        var runningStreak = 0

        for (r in sortedAsc) {
            if (r.fajr) { fajr++; totalPrayed++ }
            if (r.dhuhr) { dhuhr++; totalPrayed++ }
            if (r.asr) { asr++; totalPrayed++ }
            if (r.maghrib) { maghrib++; totalPrayed++ }
            if (r.isha) { isha++; totalPrayed++ }

            if (r.completedCount == 5) {
                perfectDays++
            }

            if (r.completedCount >= 4) { // Active prayer day
                runningStreak++
                if (runningStreak > bestStreak) bestStreak = runningStreak
            } else {
                runningStreak = 0
            }
        }
        currentStreak = runningStreak

        val overallPct = if (totalPossible > 0) (totalPrayed.toFloat() / totalPossible.toFloat()) * 100f else 0f
        val fajrPct = if (totalDays > 0) (fajr.toFloat() / totalDays.toFloat()) * 100f else 0f
        val dhuhrPct = if (totalDays > 0) (dhuhr.toFloat() / totalDays.toFloat()) * 100f else 0f
        val asrPct = if (totalDays > 0) (asr.toFloat() / totalDays.toFloat()) * 100f else 0f
        val maghribPct = if (totalDays > 0) (maghrib.toFloat() / totalDays.toFloat()) * 100f else 0f
        val ishaPct = if (totalDays > 0) (isha.toFloat() / totalDays.toFloat()) * 100f else 0f

        return com.example.sync.HistoricalPrayerStats(
            totalRecordedDays = totalDays,
            totalPossiblePrayers = totalPossible,
            totalPrayedCount = totalPrayed,
            overallPercentage = overallPct,
            fajrCount = fajr,
            fajrPercentage = fajrPct,
            dhuhrCount = dhuhr,
            dhuhrPercentage = dhuhrPct,
            asrCount = asr,
            asrPercentage = asrPct,
            maghribCount = maghrib,
            maghribPercentage = maghribPct,
            ishaCount = isha,
            ishaPercentage = ishaPct,
            currentStreakDays = currentStreak,
            bestStreakDays = bestStreak,
            perfectDaysCount = perfectDays
        )
    }

    suspend fun seedHistoricalDataIfEmpty() {
        val count = trackerDao.getAllPrayerRecordsSync().size
        if (count < 3) {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val seedList = mutableListOf<PrayerRecordEntity>()
            // Generate last 14 days of realistic prayers
            for (i in 1..14) {
                val cal = java.util.Calendar.getInstance()
                cal.add(java.util.Calendar.DAY_OF_YEAR, -i)
                val date = sdf.format(cal.time)
                // Randomly high compliance reflecting practicing Muslim
                seedList.add(
                    PrayerRecordEntity(
                        dateStr = date,
                        fajr = (i % 5 != 0),
                        dhuhr = true,
                        asr = (i % 4 != 0),
                        maghrib = true,
                        isha = true,
                        updatedAt = cal.timeInMillis,
                        isSynced = false
                    )
                )
            }
            trackerDao.insertOrUpdatePrayerRecords(seedList)
        }
    }

    fun getFastingRecord(dateStr: String = getTodayDateStr()): Flow<FastingRecordEntity?> {
        return trackerDao.getFastingRecord(dateStr)
    }

    fun getAllFastingRecords(): Flow<List<FastingRecordEntity>> {
        return trackerDao.getAllFastingRecords()
    }

    suspend fun getAllFastingRecordsSync(): List<FastingRecordEntity> {
        return trackerDao.getAllFastingRecordsSync()
    }

    fun getTotalFastedDaysCount(): Flow<Int> {
        return trackerDao.getTotalFastedDaysCount()
    }

    suspend fun toggleFasting(dateStr: String = getTodayDateStr(), defaultType: String? = null) {
        val current = trackerDao.getFastingRecordSync(dateStr)
        val newFastedState = if (current != null) !current.isFasted else true
        val resolvedType = defaultType ?: current?.fastingType ?: getDefaultFastingTypeForToday()
        trackerDao.insertOrUpdateFastingRecord(
            FastingRecordEntity(
                dateStr = dateStr,
                isFasted = newFastedState,
                fastingType = resolvedType,
                isQadha = resolvedType.contains("Qadha", ignoreCase = true)
            )
        )
    }

    suspend fun updateFastingType(dateStr: String = getTodayDateStr(), fastingType: String) {
        val current = trackerDao.getFastingRecordSync(dateStr)
        trackerDao.insertOrUpdateFastingRecord(
            FastingRecordEntity(
                dateStr = dateStr,
                isFasted = true,
                fastingType = fastingType,
                isQadha = fastingType.contains("Qadha", ignoreCase = true)
            )
        )
    }

    suspend fun setFastingState(dateStr: String, isFasted: Boolean, fastingType: String) {
        trackerDao.insertOrUpdateFastingRecord(
            FastingRecordEntity(
                dateStr = dateStr,
                isFasted = isFasted,
                fastingType = fastingType,
                isQadha = fastingType.contains("Qadha", ignoreCase = true)
            )
        )
    }

    suspend fun logFastRecord(
        dateStr: String,
        isFasted: Boolean,
        fastingType: String,
        isQadha: Boolean = false,
        note: String = ""
    ) {
        trackerDao.insertOrUpdateFastingRecord(
            FastingRecordEntity(
                dateStr = dateStr,
                isFasted = isFasted,
                fastingType = fastingType,
                isQadha = isQadha,
                note = note
            )
        )
    }

    suspend fun deleteFastRecord(dateStr: String) {
        trackerDao.deleteFastingRecord(dateStr)
    }

    suspend fun seedHistoricalFastingDataIfEmpty() {
        val existing = trackerDao.getAllFastingRecordsSync()
        if (existing.size < 4) {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val seedList = mutableListOf<FastingRecordEntity>()
            // Generate realistic fasts across the past 6 weeks (42 days)
            for (i in 1..42) {
                val cal = java.util.Calendar.getInstance()
                cal.add(java.util.Calendar.DAY_OF_YEAR, -i)
                val dateStr = sdf.format(cal.time)
                val dow = cal.get(java.util.Calendar.DAY_OF_WEEK)
                val dayOfMonth = cal.get(java.util.Calendar.DAY_OF_MONTH)

                // Monday and Thursday Sunnah fasts
                if (dow == java.util.Calendar.MONDAY || dow == java.util.Calendar.THURSDAY) {
                    if (i % 7 != 0) {
                        seedList.add(
                            FastingRecordEntity(
                                dateStr = dateStr,
                                isFasted = true,
                                fastingType = "Sunnah (Mon/Thu)",
                                note = "Sunnah weekly fast"
                            )
                        )
                    }
                } else if (dayOfMonth in 13..15 && i in 10..30) {
                    // White days (13, 14, 15)
                    seedList.add(
                        FastingRecordEntity(
                            dateStr = dateStr,
                            isFasted = true,
                            fastingType = "White Days",
                            note = "Ayyam al-Beed (White Days)"
                        )
                    )
                } else if (i in listOf(8, 22, 36)) {
                    // Qadha makeup fasts
                    seedList.add(
                        FastingRecordEntity(
                            dateStr = dateStr,
                            isFasted = true,
                            fastingType = "Qadha",
                            isQadha = true,
                            note = "Making up missed fast"
                        )
                    )
                }
            }
            trackerDao.insertOrUpdateFastingRecords(seedList)
        }
    }

    fun calculateFastingStats(records: List<FastingRecordEntity>): FastingHistoryStats {
        val completedFasts = records.filter { it.isFasted }
        if (completedFasts.isEmpty()) return FastingHistoryStats()

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val calNow = java.util.Calendar.getInstance()
        val currentYear = calNow.get(java.util.Calendar.YEAR)
        val currentMonth = calNow.get(java.util.Calendar.MONTH)

        calNow.add(java.util.Calendar.MONTH, -1)
        val lastYear = calNow.get(java.util.Calendar.YEAR)
        val lastMonth = calNow.get(java.util.Calendar.MONTH)

        var ramadan = 0
        var sunnah = 0
        var whiteDays = 0
        var qadha = 0
        var nafilah = 0
        var thisMonthCount = 0
        var lastMonthCount = 0

        for (r in completedFasts) {
            when {
                r.fastingType.contains("Ramadan", ignoreCase = true) -> ramadan++
                r.fastingType.contains("Sunnah", ignoreCase = true) || r.fastingType.contains("Mon/Thu", ignoreCase = true) -> sunnah++
                r.fastingType.contains("White", ignoreCase = true) -> whiteDays++
                r.isQadha || r.fastingType.contains("Qadha", ignoreCase = true) -> qadha++
                else -> nafilah++
            }

            try {
                val d = sdf.parse(r.dateStr)
                if (d != null) {
                    val c = java.util.Calendar.getInstance().apply { time = d }
                    val y = c.get(java.util.Calendar.YEAR)
                    val m = c.get(java.util.Calendar.MONTH)
                    if (y == currentYear && m == currentMonth) {
                        thisMonthCount++
                    } else if (y == lastYear && m == lastMonth) {
                        lastMonthCount++
                    }
                }
            } catch (_: Exception) {}
        }

        val sortedAsc = completedFasts.mapNotNull {
            try {
                val parsed = sdf.parse(it.dateStr)
                if (parsed != null) parsed.time to it.dateStr else null
            } catch (_: Exception) { null }
        }.sortedBy { it.first }

        var bestStreak = 0
        var runningStreak = 0
        var prevTime = -1L
        val ONE_DAY_MS = 24 * 60 * 60 * 1000L

        for (entry in sortedAsc) {
            val t: Long = entry.first
            if (prevTime == -1L) {
                runningStreak = 1
            } else {
                val diffDays = ((t - prevTime) / ONE_DAY_MS).toInt()
                if (diffDays == 1) {
                    runningStreak++
                } else if (diffDays > 1) {
                    runningStreak = 1
                }
            }
            if (runningStreak > bestStreak) bestStreak = runningStreak
            prevTime = t
        }

        val todayStr = getTodayDateStr()
        val yesterdayCal = java.util.Calendar.getInstance().apply { add(java.util.Calendar.DAY_OF_YEAR, -1) }
        val yesterdayStr = sdf.format(yesterdayCal.time)
        val hasRecentFast = completedFasts.any { it.dateStr == todayStr || it.dateStr == yesterdayStr }
        val currentStreak = if (hasRecentFast) runningStreak else 0

        return FastingHistoryStats(
            totalFastedDays = completedFasts.size,
            ramadanFastsCount = ramadan,
            sunnahFastsCount = sunnah,
            whiteDaysCount = whiteDays,
            qadhaFastsCount = qadha,
            nafilahFastsCount = nafilah,
            currentStreak = currentStreak,
            bestStreak = bestStreak,
            thisMonthFastedCount = thisMonthCount,
            lastMonthFastedCount = lastMonthCount
        )
    }

    fun getDefaultFastingTypeForToday(): String {
        val cal = java.util.Calendar.getInstance()
        val dayOfWeek = cal.get(java.util.Calendar.DAY_OF_WEEK)
        return when (dayOfWeek) {
            java.util.Calendar.MONDAY, java.util.Calendar.THURSDAY -> "Sunnah (Mon/Thu)"
            else -> "Ramadan"
        }
    }

    fun getPastDays(count: Int = 7): List<DateInfo> {
        val list = mutableListOf<DateInfo>()
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())
        val numFormat = SimpleDateFormat("d", Locale.getDefault())

        for (i in (count - 1) downTo 0) {
            val c = java.util.Calendar.getInstance().apply { add(java.util.Calendar.DAY_OF_YEAR, -i) }
            val dateStr = sdf.format(c.time)
            val dayName = dayFormat.format(c.time)
            val dayNum = numFormat.format(c.time)
            val dow = c.get(java.util.Calendar.DAY_OF_WEEK)
            val isSunnahDay = dow == java.util.Calendar.MONDAY || dow == java.util.Calendar.THURSDAY
            list.add(DateInfo(dateStr, dayName, dayNum, i == 0, isSunnahDay))
        }
        return list
    }

    data class DateInfo(
        val dateStr: String,
        val dayName: String,
        val dayNumber: String,
        val isToday: Boolean,
        val isSunnahDay: Boolean
    )

    data class FastingHistoryStats(
        val totalFastedDays: Int = 0,
        val ramadanFastsCount: Int = 0,
        val sunnahFastsCount: Int = 0,
        val whiteDaysCount: Int = 0,
        val qadhaFastsCount: Int = 0,
        val nafilahFastsCount: Int = 0,
        val currentStreak: Int = 0,
        val bestStreak: Int = 0,
        val thisMonthFastedCount: Int = 0,
        val lastMonthFastedCount: Int = 0
    )
}
