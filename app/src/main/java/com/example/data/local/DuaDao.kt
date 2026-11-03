package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DuaDao {
    @Query("SELECT * FROM duas ORDER BY title ASC")
    fun getAllDuas(): Flow<List<DuaEntity>>

    @Query("SELECT * FROM duas WHERE id = :id LIMIT 1")
    suspend fun getDuaById(id: String): DuaEntity?

    @Query("SELECT * FROM duas WHERE category = :category ORDER BY title ASC")
    fun getDuasByCategory(category: String): Flow<List<DuaEntity>>

    @Query("SELECT * FROM duas WHERE isFavorite = 1 ORDER BY title ASC")
    fun getFavoriteDuas(): Flow<List<DuaEntity>>

    @Query("SELECT * FROM duas WHERE isMemorized = 1 ORDER BY title ASC")
    fun getMemorizedDuas(): Flow<List<DuaEntity>>

    @Query("SELECT * FROM duas WHERE title LIKE '%' || :query || '%' OR translation LIKE '%' || :query || '%' OR arabicText LIKE '%' || :query || '%' OR reference LIKE '%' || :query || '%' OR category LIKE '%' || :query || '%' ORDER BY title ASC")
    fun searchDuas(query: String): Flow<List<DuaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDuas(duas: List<DuaEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertNewDuas(duas: List<DuaEntity>)

    @Query("UPDATE duas SET customFolder = :folder WHERE id = :id")
    suspend fun updateCustomFolder(id: String, folder: String)

    @Query("UPDATE duas SET userNote = :note WHERE id = :id")
    suspend fun updateUserNote(id: String, note: String)

    @Query("SELECT DISTINCT customFolder FROM duas WHERE customFolder != '' ORDER BY customFolder ASC")
    fun getCustomFolders(): Flow<List<String>>

    @Query("UPDATE duas SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavorite(id: String, isFavorite: Boolean)

    @Query("UPDATE duas SET isMemorized = :isMemorized, lastPracticedTimestamp = :timestamp WHERE id = :id")
    suspend fun updateMemorized(id: String, isMemorized: Boolean, timestamp: Long)

    @Query("UPDATE duas SET practiceCount = :count, lastPracticedTimestamp = :timestamp WHERE id = :id")
    suspend fun updatePracticeCount(id: String, count: Int, timestamp: Long)

    @Query("UPDATE duas SET practiceCount = 0 WHERE id = :id")
    suspend fun resetPracticeCount(id: String)

    @Query("SELECT COUNT(*) FROM duas")
    suspend fun getDuasCount(): Int

    @Query("SELECT COUNT(*) FROM duas WHERE isMemorized = 1")
    fun getMemorizedDuasCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM duas WHERE isFavorite = 1")
    fun getFavoriteDuasCount(): Flow<Int>
}
