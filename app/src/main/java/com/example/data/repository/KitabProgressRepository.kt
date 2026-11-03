package com.example.data.repository

import android.util.Log
import com.example.data.local.HadithBookEntity
import com.example.data.local.HadithChapterEntity
import com.example.data.local.HadithKitabTrackerDao
import com.example.data.local.KitabCompletionDao
import com.example.data.local.KitabCompletionEntity
import com.example.data.seed.HadithKitabItem
import com.example.data.seed.HadithKitabProgressData
import com.example.data.seed.HadithSchemaSeedData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

data class CollectionProgress(
    val collectionId: String,
    val collectionTitle: String,
    val completedBooksCount: Int,
    val totalBooksCount: Int,
    val completedHadithCount: Int,
    val totalHadithCount: Int,
    val progressFraction: Float
)

/**
 * Repository abstracting Room database operations for Hadith collections,
 * books, chapters, and the Kitab completion tracker.
 * Reactive Flow streams allow the UI to automatically update when a user marks a chapter read.
 */
class KitabProgressRepository(
    private val dao: KitabCompletionDao,
    val trackerDao: HadithKitabTrackerDao? = null,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {
    private val TAG = "KitabProgressRepo"

    init {
        trackerDao?.let { tDao ->
            scope.launch {
                try {
                    if (tDao.getTotalBooksCountSync() == 0) {
                        tDao.insertBooks(HadithSchemaSeedData.DEFAULT_BOOKS)
                    }
                    if (tDao.getTotalChaptersCountSync() == 0) {
                        tDao.insertChapters(HadithSchemaSeedData.DEFAULT_CHAPTERS)
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error initializing Hadith schema seeds: ${e.message}", e)
                }
            }
        }
    }

    /**
     * Reactive stream of all completed Kitab entries stored in Room.
     */
    val completedRecords: Flow<List<KitabCompletionEntity>> = dao.getAllCompleted()

    /**
     * Reactive set of completed book IDs for instant O(1) membership lookup.
     */
    val completedBookIds: Flow<Set<String>> = dao.getCompletedBookIds().map { it.toSet() }

    /**
     * Canonical Hadith Books from Room database.
     */
    val allBooks: Flow<List<HadithBookEntity>> = trackerDao?.getAllBooks() ?: emptyFlow()

    /**
     * Canonical Hadith Chapters with completion states from Room database.
     */
    val allChapters: Flow<List<HadithChapterEntity>> = trackerDao?.getAllChapters() ?: emptyFlow()

    /**
     * Stream chapters for a specific Hadith collection (e.g. "bukhari", "muslim").
     */
    fun getChaptersForCollection(collectionId: String): Flow<List<HadithChapterEntity>> {
        return trackerDao?.getChaptersForCollection(collectionId) ?: emptyFlow()
    }

    /**
     * Aggregate progress across collections computed reactively from Room.
     */
    val collectionProgressList: Flow<List<CollectionProgress>> = dao.getCompletedBookIds().map { completedIdsList ->
        val completedSet = completedIdsList.toSet()
        val allBooksList = HadithKitabProgressData.PROMINENT_BOOKS

        val collectionsMeta = listOf(
            Triple("bukhari", "Sahih al-Bukhari", allBooksList.filter { it.collectionId.equals("bukhari", ignoreCase = true) }),
            Triple("muslim", "Sahih Muslim", allBooksList.filter { it.collectionId.equals("muslim", ignoreCase = true) }),
            Triple("nawawi", "40 Hadith an-Nawawi", allBooksList.filter { it.collectionId.equals("nawawi", ignoreCase = true) })
        )

        collectionsMeta.map { (collId, collTitle, booksInColl) ->
            val completedInColl = booksInColl.filter { completedSet.contains(it.id) }
            val completedHadiths = completedInColl.sumOf { it.hadithCount }
            val totalHadiths = booksInColl.sumOf { it.hadithCount }
            val fraction = if (booksInColl.isNotEmpty()) completedInColl.size.toFloat() / booksInColl.size.toFloat() else 0f

            CollectionProgress(
                collectionId = collId,
                collectionTitle = collTitle,
                completedBooksCount = completedInColl.size,
                totalBooksCount = booksInColl.size,
                completedHadithCount = completedHadiths,
                totalHadithCount = totalHadiths,
                progressFraction = fraction
            )
        }
    }

    /**
     * Toggles the completion status of an individual Hadith chapter in Room.
     */
    suspend fun toggleBookCompletion(book: HadithKitabItem): Boolean {
        val existing = dao.getRecordById(book.id)
        val isNowCompleted = if (existing != null && existing.isCompleted) {
            dao.deleteByBookId(book.id)
            false
        } else {
            val entity = KitabCompletionEntity(
                bookId = book.id,
                collectionId = book.collectionId,
                bookNumber = book.bookNumber,
                titleEnglish = book.titleEnglish,
                titleArabic = book.titleArabic,
                hadithCount = book.hadithCount,
                isCompleted = true,
                completedAt = System.currentTimeMillis()
            )
            dao.insertOrUpdate(entity)
            true
        }

        // Sync with hadith_chapters table if present
        trackerDao?.updateChapterCompletion(
            id = book.id,
            isCompleted = isNowCompleted,
            completedAt = if (isNowCompleted) System.currentTimeMillis() else null
        )

        return isNowCompleted
    }

    /**
     * Mark an entire collection as completed or unread in Room.
     */
    suspend fun setCollectionCompleted(collectionId: String, completed: Boolean) {
        val books = HadithKitabProgressData.getBooksForCollection(collectionId)
        val now = System.currentTimeMillis()
        if (completed) {
            books.forEach { book ->
                val entity = KitabCompletionEntity(
                    bookId = book.id,
                    collectionId = book.collectionId,
                    bookNumber = book.bookNumber,
                    titleEnglish = book.titleEnglish,
                    titleArabic = book.titleArabic,
                    hadithCount = book.hadithCount,
                    isCompleted = true,
                    completedAt = now
                )
                dao.insertOrUpdate(entity)
            }
        } else {
            dao.deleteForCollection(collectionId)
        }

        trackerDao?.setCollectionCompleted(
            collectionId = collectionId,
            isCompleted = completed,
            completedAt = if (completed) now else null
        )
    }

    /**
     * Clear all recorded progress in Room.
     */
    suspend fun clearAllProgress() {
        dao.clearAll()
        trackerDao?.clearAllCompletions()
    }
}
