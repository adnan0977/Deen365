package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyReflectionDao {
    @Query("SELECT * FROM daily_reflections ORDER BY timestamp DESC LIMIT 1")
    fun getLatestReflection(): Flow<DailyReflectionEntity?>

    @Query("SELECT * FROM daily_reflections ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestReflectionSync(): DailyReflectionEntity?

    @Query("SELECT * FROM daily_reflections WHERE dateStr = :dateStr LIMIT 1")
    suspend fun getReflectionForDate(dateStr: String): DailyReflectionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReflection(reflection: DailyReflectionEntity)

    @Query("SELECT * FROM daily_reflections ORDER BY timestamp DESC")
    fun getAllReflections(): Flow<List<DailyReflectionEntity>>
}
