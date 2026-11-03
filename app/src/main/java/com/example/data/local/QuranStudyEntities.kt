package com.example.data.local

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "quran_highlights")
data class QuranHighlightEntity(
    @PrimaryKey val id: String, // "${surahNumber}_${ayahNumber}"
    val surahNumber: Int,
    val ayahNumber: Int,
    val surahName: String,
    val colorHex: String,
    val colorName: String,
    val categoryLabel: String,
    val note: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "quran_goal_bookmarks")
data class QuranGoalBookmarkEntity(
    @PrimaryKey val id: String,
    val surahNumber: Int,
    val ayahNumber: Int,
    val surahName: String,
    val goalLabel: String,
    val targetMinutes: Int = 15,
    val note: String = "",
    val isCompleted: Boolean = false,
    val dateString: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Dao
interface QuranStudyDao {
    // Highlights
    @Query("SELECT * FROM quran_highlights ORDER BY timestamp DESC")
    fun getAllHighlights(): Flow<List<QuranHighlightEntity>>

    @Query("SELECT * FROM quran_highlights WHERE surahNumber = :surahNumber ORDER BY ayahNumber ASC")
    fun getHighlightsForSurah(surahNumber: Int): Flow<List<QuranHighlightEntity>>

    @Query("SELECT * FROM quran_highlights WHERE surahNumber = :surahNumber AND ayahNumber = :ayahNumber LIMIT 1")
    suspend fun getHighlight(surahNumber: Int, ayahNumber: Int): QuranHighlightEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertHighlight(highlight: QuranHighlightEntity)

    @Query("DELETE FROM quran_highlights WHERE surahNumber = :surahNumber AND ayahNumber = :ayahNumber")
    suspend fun deleteHighlight(surahNumber: Int, ayahNumber: Int)

    // Daily Reading Goal Bookmarks
    @Query("SELECT * FROM quran_goal_bookmarks ORDER BY timestamp DESC")
    fun getAllGoalBookmarks(): Flow<List<QuranGoalBookmarkEntity>>

    @Query("SELECT * FROM quran_goal_bookmarks WHERE surahNumber = :surahNumber AND ayahNumber = :ayahNumber LIMIT 1")
    suspend fun getGoalBookmark(surahNumber: Int, ayahNumber: Int): QuranGoalBookmarkEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertGoalBookmark(bookmark: QuranGoalBookmarkEntity)

    @Query("UPDATE quran_goal_bookmarks SET isCompleted = :isCompleted WHERE id = :id")
    suspend fun updateGoalBookmarkCompletion(id: String, isCompleted: Boolean)

    @Query("DELETE FROM quran_goal_bookmarks WHERE id = :id")
    suspend fun deleteGoalBookmark(id: String)
}
