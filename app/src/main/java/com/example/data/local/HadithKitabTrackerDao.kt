package com.example.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * Data projection for calculating Kitab completion progress per collection.
 */
data class BookProgressTuple(
    val collectionId: String,
    val completedChapters: Int,
    val totalChapters: Int
)

/**
 * Data Access Object powering the Room Database schema for Hadith collections,
 * books, chapters, and the Kitab Completion Tracker.
 */
@Dao
interface HadithKitabTrackerDao {

    // ==========================================
    // Books / Collections Queries
    // ==========================================

    @Query("SELECT * FROM hadith_books ORDER BY totalHadithCount DESC")
    fun getAllBooks(): Flow<List<HadithBookEntity>>

    @Query("SELECT * FROM hadith_books WHERE id = :id LIMIT 1")
    fun getBookById(id: String): Flow<HadithBookEntity?>

    @Query("SELECT * FROM hadith_books WHERE id = :id LIMIT 1")
    suspend fun getBookByIdSync(id: String): HadithBookEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooks(books: List<HadithBookEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: HadithBookEntity)

    // ==========================================
    // Chapters (Kitab / Bab) Queries
    // ==========================================

    @Query("SELECT * FROM hadith_chapters ORDER BY collectionId ASC, bookNumber ASC")
    fun getAllChapters(): Flow<List<HadithChapterEntity>>

    @Query("SELECT * FROM hadith_chapters WHERE collectionId = :collectionId ORDER BY bookNumber ASC")
    fun getChaptersForCollection(collectionId: String): Flow<List<HadithChapterEntity>>

    @Query("SELECT * FROM hadith_chapters WHERE collectionId = :collectionId ORDER BY bookNumber ASC")
    suspend fun getChaptersForCollectionSync(collectionId: String): List<HadithChapterEntity>

    @Query("SELECT * FROM hadith_chapters WHERE id = :id LIMIT 1")
    fun getChapterById(id: String): Flow<HadithChapterEntity?>

    @Query("SELECT * FROM hadith_chapters WHERE id = :id LIMIT 1")
    suspend fun getChapterByIdSync(id: String): HadithChapterEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChapters(chapters: List<HadithChapterEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateChapter(chapter: HadithChapterEntity)

    // ==========================================
    // Kitab Completion Tracking Queries & Updates
    // ==========================================

    @Query("SELECT * FROM hadith_chapters WHERE isCompleted = 1 ORDER BY completedAt DESC")
    fun getCompletedChapters(): Flow<List<HadithChapterEntity>>

    @Query("SELECT * FROM hadith_chapters WHERE collectionId = :collectionId AND isCompleted = 1 ORDER BY bookNumber ASC")
    fun getCompletedChaptersForCollection(collectionId: String): Flow<List<HadithChapterEntity>>

    @Query("SELECT id FROM hadith_chapters WHERE isCompleted = 1")
    fun getCompletedChapterIds(): Flow<List<String>>

    @Query("SELECT COUNT(*) FROM hadith_chapters WHERE isCompleted = 1")
    fun getCompletedChaptersCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM hadith_chapters WHERE collectionId = :collectionId AND isCompleted = 1")
    fun getCompletedChaptersCountForCollection(collectionId: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM hadith_chapters WHERE collectionId = :collectionId")
    fun getTotalChaptersCountForCollection(collectionId: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM hadith_chapters")
    suspend fun getTotalChaptersCountSync(): Int

    @Query("SELECT COUNT(*) FROM hadith_books")
    suspend fun getTotalBooksCountSync(): Int

    @Query("UPDATE hadith_chapters SET isCompleted = :isCompleted, completedAt = :completedAt WHERE id = :id")
    suspend fun updateChapterCompletion(id: String, isCompleted: Boolean, completedAt: Long?)

    @Query("UPDATE hadith_chapters SET isCompleted = NOT isCompleted, completedAt = :completedAt WHERE id = :id")
    suspend fun toggleChapterCompletion(id: String, completedAt: Long = System.currentTimeMillis())

    @Query("UPDATE hadith_chapters SET isCompleted = :isCompleted, completedAt = :completedAt WHERE collectionId = :collectionId")
    suspend fun setCollectionCompleted(collectionId: String, isCompleted: Boolean, completedAt: Long?)

    @Query("UPDATE hadith_chapters SET isCompleted = 0, completedAt = NULL")
    suspend fun clearAllCompletions()

    // ==========================================
    // Aggregate Progress Query
    // ==========================================

    @Query("SELECT collectionId, SUM(CASE WHEN isCompleted = 1 THEN 1 ELSE 0 END) as completedChapters, COUNT(*) as totalChapters FROM hadith_chapters GROUP BY collectionId")
    fun getBookProgressSummaries(): Flow<List<BookProgressTuple>>
}
