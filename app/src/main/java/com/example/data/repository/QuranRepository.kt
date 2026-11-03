package com.example.data.repository

import android.util.Log
import com.example.data.local.AyahDao
import com.example.data.local.AyahEntity
import com.example.data.local.QuranGoalBookmarkEntity
import com.example.data.local.QuranHighlightEntity
import com.example.data.local.QuranStudyDao
import com.example.data.remote.IslamicApiService
import com.example.data.seed.QuranSeedData
import com.example.data.seed.SurahMeta
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch

class QuranRepository(
    private val ayahDao: AyahDao,
    private val quranStudyDao: QuranStudyDao? = null,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {
    private val TAG = "QuranRepository"

    fun getSurahList(): List<SurahMeta> = QuranSeedData.SURAH_LIST

    fun getAyahsForSurah(surahNumber: Int): Flow<List<AyahEntity>> {
        // Cache-First: On-demand check and fetch
        scope.launch {
            try {
                val existingCount = ayahDao.getAyahCountForSurah(surahNumber)
                if (existingCount == 0) {
                    Log.d(TAG, "No cached ayahs for Surah $surahNumber. Fetching on-demand from API...")
                    val apiAyahs = IslamicApiService.fetchSurahAyahs(surahNumber)
                    if (apiAyahs.isNotEmpty()) {
                        ayahDao.insertAyahs(apiAyahs)
                        Log.d(TAG, "Stored ${apiAyahs.size} ayahs into Room DB for Surah $surahNumber")
                    } else {
                        // Fallback to rich pre-bundled seed ayahs
                        val fallback = QuranSeedData.getFallbackAyahsForSurah(surahNumber)
                        ayahDao.insertAyahs(fallback)
                        Log.d(TAG, "Stored fallback seed ayahs into Room DB for Surah $surahNumber")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching/caching ayahs: ${e.message}", e)
                val fallback = QuranSeedData.getFallbackAyahsForSurah(surahNumber)
                ayahDao.insertAyahs(fallback)
            }
        }
        return ayahDao.getAyahsForSurah(surahNumber)
    }

    suspend fun toggleBookmark(surahNumber: Int, ayahNumber: Int, currentBookmarked: Boolean) {
        ayahDao.updateBookmark(surahNumber, ayahNumber, !currentBookmarked)
    }

    fun getBookmarkedAyahs(): Flow<List<AyahEntity>> {
        return ayahDao.getBookmarkedAyahs()
    }

    // Highlighting
    fun getAllHighlights(): Flow<List<QuranHighlightEntity>> {
        return quranStudyDao?.getAllHighlights() ?: emptyFlow()
    }

    fun getHighlightsForSurah(surahNumber: Int): Flow<List<QuranHighlightEntity>> {
        return quranStudyDao?.getHighlightsForSurah(surahNumber) ?: emptyFlow()
    }

    suspend fun saveHighlight(highlight: QuranHighlightEntity) {
        quranStudyDao?.upsertHighlight(highlight)
    }

    suspend fun deleteHighlight(surahNumber: Int, ayahNumber: Int) {
        quranStudyDao?.deleteHighlight(surahNumber, ayahNumber)
    }

    // Daily Reading Goal Bookmarks
    fun getAllGoalBookmarks(): Flow<List<QuranGoalBookmarkEntity>> {
        return quranStudyDao?.getAllGoalBookmarks() ?: emptyFlow()
    }

    suspend fun saveGoalBookmark(bookmark: QuranGoalBookmarkEntity) {
        quranStudyDao?.upsertGoalBookmark(bookmark)
    }

    suspend fun updateGoalBookmarkCompletion(id: String, isCompleted: Boolean) {
        quranStudyDao?.updateGoalBookmarkCompletion(id, isCompleted)
    }

    suspend fun deleteGoalBookmark(id: String) {
        quranStudyDao?.deleteGoalBookmark(id)
    }
}
