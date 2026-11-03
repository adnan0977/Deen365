package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Kitab / Hadith chapter completion records in Room.
 */
@Dao
interface KitabCompletionDao {

    @Query("SELECT * FROM kitab_completion WHERE isCompleted = 1 ORDER BY completedAt DESC")
    fun getAllCompleted(): Flow<List<KitabCompletionEntity>>

    @Query("SELECT * FROM kitab_completion")
    fun getAllRecords(): Flow<List<KitabCompletionEntity>>

    @Query("SELECT bookId FROM kitab_completion WHERE isCompleted = 1")
    fun getCompletedBookIds(): Flow<List<String>>

    @Query("SELECT * FROM kitab_completion WHERE collectionId = :collectionId AND isCompleted = 1")
    fun getCompletedForCollection(collectionId: String): Flow<List<KitabCompletionEntity>>

    @Query("SELECT * FROM kitab_completion WHERE bookId = :bookId LIMIT 1")
    suspend fun getRecordById(bookId: String): KitabCompletionEntity?

    @Query("SELECT COUNT(*) FROM kitab_completion WHERE isCompleted = 1")
    fun getCompletedCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM kitab_completion WHERE collectionId = :collectionId AND isCompleted = 1")
    fun getCompletedCountForCollection(collectionId: String): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(entity: KitabCompletionEntity)

    @Query("DELETE FROM kitab_completion WHERE bookId = :bookId")
    suspend fun deleteByBookId(bookId: String)

    @Query("DELETE FROM kitab_completion WHERE collectionId = :collectionId")
    suspend fun deleteForCollection(collectionId: String)

    @Query("DELETE FROM kitab_completion")
    suspend fun clearAll()
}
