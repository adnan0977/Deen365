package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AyahDao {
    @Query("SELECT * FROM ayahs WHERE surahNumber = :surahNumber ORDER BY ayahNumber ASC")
    fun getAyahsForSurah(surahNumber: Int): Flow<List<AyahEntity>>

    @Query("SELECT * FROM ayahs WHERE surahNumber = :surahNumber ORDER BY ayahNumber ASC")
    suspend fun getAyahsForSurahSync(surahNumber: Int): List<AyahEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAyahs(ayahs: List<AyahEntity>)

    @Query("UPDATE ayahs SET isBookmarked = :isBookmarked WHERE surahNumber = :surahNumber AND ayahNumber = :ayahNumber")
    suspend fun updateBookmark(surahNumber: Int, ayahNumber: Int, isBookmarked: Boolean)

    @Query("SELECT * FROM ayahs WHERE isBookmarked = 1 ORDER BY surahNumber ASC, ayahNumber ASC")
    fun getBookmarkedAyahs(): Flow<List<AyahEntity>>

    @Query("SELECT COUNT(*) FROM ayahs WHERE surahNumber = :surahNumber")
    suspend fun getAyahCountForSurah(surahNumber: Int): Int
}
