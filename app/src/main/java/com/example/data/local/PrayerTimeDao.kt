package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PrayerTimeDao {
    @Query("SELECT * FROM daily_prayer_times WHERE dateStr = :dateStr ORDER BY updatedAt DESC LIMIT 1")
    fun getPrayerTimesForDate(dateStr: String): Flow<PrayerTimeEntity?>

    @Query("SELECT * FROM daily_prayer_times WHERE dateStr = :dateStr ORDER BY updatedAt DESC LIMIT 1")
    suspend fun getPrayerTimesForDateSync(dateStr: String): PrayerTimeEntity?

    @Query("SELECT * FROM daily_prayer_times ORDER BY updatedAt DESC")
    fun getAllSavedPrayerTimes(): Flow<List<PrayerTimeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdatePrayerTimes(entity: PrayerTimeEntity)

    @Query("DELETE FROM daily_prayer_times WHERE dateStr < :thresholdDateStr")
    suspend fun deleteOldPrayerTimes(thresholdDateStr: String)
}
