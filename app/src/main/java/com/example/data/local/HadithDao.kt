package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HadithDao {
    @Query("SELECT * FROM hadiths WHERE collection = :collection AND bookNumber = :bookNumber ORDER BY hadithNumber ASC")
    fun getHadithsForBook(collection: String, bookNumber: Int): Flow<List<HadithEntity>>

    @Query("SELECT * FROM hadiths WHERE collection = :collection AND bookNumber = :bookNumber ORDER BY hadithNumber ASC")
    suspend fun getHadithsForBookSync(collection: String, bookNumber: Int): List<HadithEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHadiths(hadiths: List<HadithEntity>)

    @Query("UPDATE hadiths SET isFavorite = :isFavorite WHERE collection = :collection AND hadithNumber = :hadithNumber")
    suspend fun updateFavorite(collection: String, hadithNumber: Int, isFavorite: Boolean)

    @Query("UPDATE hadiths SET isRead = :isRead, lastStudiedTimestamp = :timestamp WHERE collection = :collection AND hadithNumber = :hadithNumber")
    suspend fun updateStudied(collection: String, hadithNumber: Int, isRead: Boolean, timestamp: Long)

    @Query("UPDATE hadiths SET isMemorized = :isMemorized, lastStudiedTimestamp = :timestamp WHERE collection = :collection AND hadithNumber = :hadithNumber")
    suspend fun updateMemorized(collection: String, hadithNumber: Int, isMemorized: Boolean, timestamp: Long)

    @Query("SELECT * FROM hadiths WHERE isFavorite = 1 ORDER BY collection ASC, hadithNumber ASC")
    fun getFavoriteHadiths(): Flow<List<HadithEntity>>

    @Query("SELECT * FROM hadiths WHERE isMemorized = 1 ORDER BY collection ASC, hadithNumber ASC")
    fun getMemorizedHadiths(): Flow<List<HadithEntity>>

    @Query("SELECT * FROM hadiths WHERE isRead = 1 ORDER BY collection ASC, hadithNumber ASC")
    fun getStudiedHadiths(): Flow<List<HadithEntity>>

    @Query("SELECT COUNT(*) FROM hadiths WHERE isRead = 1")
    fun getStudiedHadithsCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM hadiths WHERE isMemorized = 1")
    fun getMemorizedHadithsCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM hadiths")
    fun getTotalHadithsCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM hadiths WHERE collection = :collection AND bookNumber = :bookNumber")
    suspend fun getHadithCountForBook(collection: String, bookNumber: Int): Int

    @Query("SELECT * FROM hadiths WHERE translation LIKE '%' || :query || '%' OR narrator LIKE '%' || :query || '%' OR arabicText LIKE '%' || :query || '%' OR chapterName LIKE '%' || :query || '%'")
    fun searchHadiths(query: String): Flow<List<HadithEntity>>

    @Query("SELECT * FROM hadiths ORDER BY collection ASC, hadithNumber ASC")
    fun getAllHadiths(): Flow<List<HadithEntity>>

    @Query("SELECT COUNT(*) FROM hadiths WHERE isFavorite = 1")
    fun getFavoriteHadithsCount(): Flow<Int>

    @Query("SELECT DISTINCT narrator FROM hadiths WHERE narrator != '' ORDER BY narrator ASC")
    fun getAllNarrators(): Flow<List<String>>

    @Query("SELECT DISTINCT chapterName FROM hadiths WHERE chapterName != '' ORDER BY chapterName ASC")
    fun getAllCategories(): Flow<List<String>>
}
