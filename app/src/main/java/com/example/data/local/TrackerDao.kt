package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackerDao {
    @Query("SELECT * FROM prayer_records WHERE dateStr = :dateStr LIMIT 1")
    fun getPrayerRecord(dateStr: String): Flow<PrayerRecordEntity?>

    @Query("SELECT * FROM prayer_records WHERE dateStr = :dateStr LIMIT 1")
    suspend fun getPrayerRecordSync(dateStr: String): PrayerRecordEntity?

    @Query("SELECT * FROM prayer_records ORDER BY dateStr DESC")
    fun getAllPrayerRecords(): Flow<List<PrayerRecordEntity>>

    @Query("SELECT * FROM prayer_records ORDER BY dateStr DESC")
    suspend fun getAllPrayerRecordsSync(): List<PrayerRecordEntity>

    @Query("SELECT * FROM prayer_records WHERE isSynced = 0")
    suspend fun getUnsyncedPrayerRecords(): List<PrayerRecordEntity>

    @Query("UPDATE prayer_records SET isSynced = 1, updatedAt = :updatedAt WHERE dateStr = :dateStr")
    suspend fun markPrayerRecordAsSynced(dateStr: String, updatedAt: Long = System.currentTimeMillis())

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdatePrayerRecord(record: PrayerRecordEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdatePrayerRecords(records: List<PrayerRecordEntity>)

    @Query("SELECT * FROM fasting_records WHERE dateStr = :dateStr LIMIT 1")
    fun getFastingRecord(dateStr: String): Flow<FastingRecordEntity?>

    @Query("SELECT * FROM fasting_records WHERE dateStr = :dateStr LIMIT 1")
    suspend fun getFastingRecordSync(dateStr: String): FastingRecordEntity?

    @Query("SELECT * FROM fasting_records ORDER BY dateStr DESC")
    fun getAllFastingRecords(): Flow<List<FastingRecordEntity>>

    @Query("SELECT COUNT(*) FROM fasting_records WHERE isFasted = 1")
    fun getTotalFastedDaysCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM fasting_records WHERE isFasted = 1")
    suspend fun getTotalFastedDaysCountSync(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateFastingRecord(record: FastingRecordEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateFastingRecords(records: List<FastingRecordEntity>)

    @Query("SELECT * FROM fasting_records ORDER BY dateStr DESC")
    suspend fun getAllFastingRecordsSync(): List<FastingRecordEntity>

    @Query("DELETE FROM fasting_records WHERE dateStr = :dateStr")
    suspend fun deleteFastingRecord(dateStr: String)

    @Query("SELECT * FROM fasting_records WHERE isFasted = 1 ORDER BY dateStr DESC")
    fun getCompletedFastingRecords(): Flow<List<FastingRecordEntity>>
}

