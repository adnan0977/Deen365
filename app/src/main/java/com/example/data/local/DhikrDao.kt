package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DhikrDao {
    @Query("SELECT * FROM dhikr_items ORDER BY displayOrder ASC, id ASC")
    fun getAllDhikrItems(): Flow<List<DhikrItemEntity>>

    @Query("SELECT * FROM dhikr_items WHERE id = :id LIMIT 1")
    suspend fun getDhikrItemById(id: String): DhikrItemEntity?

    @Query("SELECT COUNT(*) FROM dhikr_items")
    suspend fun getDhikrItemCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDhikrItems(items: List<DhikrItemEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDhikrItem(item: DhikrItemEntity)

    @Query("UPDATE dhikr_items SET dailyGoal = :newGoal WHERE id = :dhikrId")
    suspend fun updateDailyGoal(dhikrId: String, newGoal: Int)

    @Query("DELETE FROM dhikr_items WHERE id = :dhikrId AND isCustom = 1")
    suspend fun deleteCustomDhikrItem(dhikrId: String)

    // Daily Logs
    @Query("SELECT * FROM dhikr_daily_logs WHERE dateStr = :dateStr")
    fun getDailyLogsForDate(dateStr: String): Flow<List<DhikrDailyLogEntity>>

    @Query("SELECT * FROM dhikr_daily_logs WHERE dhikrId = :dhikrId AND dateStr = :dateStr LIMIT 1")
    fun getDailyLog(dhikrId: String, dateStr: String): Flow<DhikrDailyLogEntity?>

    @Query("SELECT * FROM dhikr_daily_logs WHERE dhikrId = :dhikrId AND dateStr = :dateStr LIMIT 1")
    suspend fun getDailyLogSync(dhikrId: String, dateStr: String): DhikrDailyLogEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateDailyLog(log: DhikrDailyLogEntity)

    @Query("SELECT SUM(count) FROM dhikr_daily_logs WHERE dateStr = :dateStr")
    fun getTotalDhikrCountForDate(dateStr: String): Flow<Int?>

    @Query("SELECT * FROM dhikr_daily_logs ORDER BY dateStr DESC, lastUpdatedTimestamp DESC LIMIT :limit")
    fun getRecentLogs(limit: Int = 50): Flow<List<DhikrDailyLogEntity>>

    @Query("DELETE FROM dhikr_daily_logs WHERE dhikrId = :dhikrId AND dateStr = :dateStr")
    suspend fun resetDailyLog(dhikrId: String, dateStr: String)
}
