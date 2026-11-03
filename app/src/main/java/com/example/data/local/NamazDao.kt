package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NamazDao {

    @Query("SELECT * FROM namaz_prayers WHERE id = :prayerId")
    fun getPrayer(prayerId: String): Flow<NamazPrayerEntity?>

    @Query("SELECT * FROM namaz_prayers")
    fun getAllPrayers(): Flow<List<NamazPrayerEntity>>

    @Query("SELECT id FROM namaz_prayers WHERE isDownloaded = 1")
    fun getDownloadedPrayerIds(): Flow<List<String>>

    @Query("SELECT * FROM namaz_steps WHERE prayerId = :prayerId ORDER BY stepIndex ASC")
    fun getPrayerSteps(prayerId: String): Flow<List<NamazStepEntity>>

    @Query("SELECT * FROM namaz_azans WHERE prayerId = :prayerId")
    fun getPrayerAzan(prayerId: String): Flow<NamazAzanEntity?>

    @Query("SELECT COUNT(*) FROM namaz_prayers WHERE id = :prayerId AND isDownloaded = 1")
    suspend fun checkIsPrayerDownloaded(prayerId: String): Int

    @Query("SELECT COUNT(*) FROM namaz_prayers WHERE isDownloaded = 1")
    fun getDownloadedCount(): Flow<Int>

    @Query("SELECT SUM(cachedSizeBytes) FROM namaz_prayers WHERE isDownloaded = 1")
    fun getTotalCachedSizeBytes(): Flow<Long?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrayer(prayer: NamazPrayerEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrayers(prayers: List<NamazPrayerEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSteps(steps: List<NamazStepEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAzan(azan: NamazAzanEntity)

    @Query("UPDATE namaz_steps SET audioLocalPath = :path, isAudioDownloaded = :isDownloaded WHERE id = :stepId")
    suspend fun updateStepAudioPath(stepId: String, path: String, isDownloaded: Boolean)

    @Query("UPDATE namaz_azans SET audioLocalPath = :path, isAudioDownloaded = :isDownloaded WHERE prayerId = :prayerId")
    suspend fun updateAzanAudioPath(prayerId: String, path: String, isDownloaded: Boolean)

    @Query("UPDATE namaz_prayers SET isDownloaded = :isDownloaded, downloadTimestamp = :timestamp, cachedSizeBytes = :sizeBytes WHERE id = :prayerId")
    suspend fun markPrayerDownloaded(prayerId: String, isDownloaded: Boolean, timestamp: Long, sizeBytes: Long)

    @Query("DELETE FROM namaz_steps WHERE prayerId = :prayerId")
    suspend fun deleteSteps(prayerId: String)

    @Query("DELETE FROM namaz_azans WHERE prayerId = :prayerId")
    suspend fun deleteAzan(prayerId: String)

    @Query("UPDATE namaz_prayers SET isDownloaded = 0, cachedSizeBytes = 0 WHERE id = :prayerId")
    suspend fun resetPrayerDownloaded(prayerId: String)

    @Query("UPDATE namaz_prayers SET isDownloaded = 0, cachedSizeBytes = 0")
    suspend fun resetAllPrayersDownloaded()

    @Query("DELETE FROM namaz_steps")
    suspend fun clearAllSteps()

    @Query("DELETE FROM namaz_azans")
    suspend fun clearAllAzans()
}
