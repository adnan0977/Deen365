package com.example.data.repository

import com.example.data.local.DhikrDao
import com.example.data.local.DhikrDailyLogEntity
import com.example.data.local.DhikrItemEntity
import com.example.data.seed.DhikrSeedData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class DhikrRepository(
    private val dhikrDao: DhikrDao,
    private val scope: CoroutineScope
) {
    init {
        seedInitialDhikrIfEmpty()
    }

    private fun seedInitialDhikrIfEmpty() {
        scope.launch {
            try {
                val count = dhikrDao.getDhikrItemCount()
                if (count == 0) {
                    dhikrDao.insertDhikrItems(DhikrSeedData.DEFAULT_ITEMS)
                }
            } catch (_: Exception) {}
        }
    }

    fun getAllDhikrItems(): Flow<List<DhikrItemEntity>> {
        return dhikrDao.getAllDhikrItems()
    }

    fun getTodayLogs(dateStr: String): Flow<Map<String, DhikrDailyLogEntity>> {
        return dhikrDao.getDailyLogsForDate(dateStr).map { list ->
            list.associateBy { it.dhikrId }
        }
    }

    fun getTotalCountForDate(dateStr: String): Flow<Int> {
        return dhikrDao.getTotalDhikrCountForDate(dateStr).map { it ?: 0 }
    }

    suspend fun updateDailyGoal(dhikrId: String, newGoal: Int, todayDateStr: String) {
        val safeGoal = newGoal.coerceAtLeast(1)
        dhikrDao.updateDailyGoal(dhikrId, safeGoal)
        // Also update today's log goal if already recorded
        val existingLog = dhikrDao.getDailyLogSync(dhikrId, todayDateStr)
        if (existingLog != null) {
            dhikrDao.insertOrUpdateDailyLog(existingLog.copy(goal = safeGoal))
        }
    }

    suspend fun incrementDhikr(dhikrId: String, dateStr: String, amount: Int = 1, currentGoal: Int = 100): Int {
        val existing = dhikrDao.getDailyLogSync(dhikrId, dateStr)
        val currentCount = existing?.count ?: 0
        val targetGoal = existing?.goal ?: currentGoal
        val newCount = currentCount + amount
        val updatedLog = DhikrDailyLogEntity(
            id = "${dhikrId}_$dateStr",
            dhikrId = dhikrId,
            dateStr = dateStr,
            count = newCount,
            goal = targetGoal,
            lastUpdatedTimestamp = System.currentTimeMillis()
        )
        dhikrDao.insertOrUpdateDailyLog(updatedLog)
        return newCount
    }

    suspend fun decrementDhikr(dhikrId: String, dateStr: String): Int {
        val existing = dhikrDao.getDailyLogSync(dhikrId, dateStr) ?: return 0
        val newCount = (existing.count - 1).coerceAtLeast(0)
        val updated = existing.copy(count = newCount, lastUpdatedTimestamp = System.currentTimeMillis())
        dhikrDao.insertOrUpdateDailyLog(updated)
        return newCount
    }

    suspend fun resetDhikrToday(dhikrId: String, dateStr: String) {
        dhikrDao.resetDailyLog(dhikrId, dateStr)
    }

    suspend fun addCustomDhikr(
        arabicText: String,
        transliteration: String,
        meaning: String,
        dailyGoal: Int
    ): DhikrItemEntity {
        val id = "custom_${System.currentTimeMillis()}"
        val item = DhikrItemEntity(
            id = id,
            arabicText = arabicText.ifBlank { "ذِكْر" },
            transliteration = transliteration.ifBlank { "Custom Dhikr" },
            meaning = meaning.ifBlank { "Personal remembrance" },
            dailyGoal = dailyGoal.coerceAtLeast(1),
            virtue = "Constant remembrance of Allah brings peace and tranquility to hearts.",
            category = "Custom",
            displayOrder = 99,
            isCustom = true
        )
        dhikrDao.insertDhikrItem(item)
        return item
    }

    suspend fun deleteCustomDhikr(dhikrId: String) {
        dhikrDao.deleteCustomDhikrItem(dhikrId)
    }

    fun getRecentHistoryLogs(limit: Int = 50): Flow<List<DhikrDailyLogEntity>> {
        return dhikrDao.getRecentLogs(limit)
    }
}
