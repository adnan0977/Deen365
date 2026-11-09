package com.example.data.repository

import android.util.Log
import com.example.data.local.HadithDao
import com.example.data.local.HadithEntity
import com.example.data.remote.IslamicApiService
import com.example.data.seed.HadithChapterMeta
import com.example.data.seed.HadithCollectionMeta
import com.example.data.seed.HadithSeedData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class HadithRepository(
    private val hadithDao: HadithDao,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {
    private val TAG = "HadithRepository"

    // No upfront bulk database seeding: hadiths are fetched via API on-demand
    // and cached into Room DB only when the user opens each book/chapter.

    val allHadiths: Flow<List<HadithEntity>> = hadithDao.getAllHadiths()

    fun getFavoriteHadithsCount(): Flow<Int> = hadithDao.getFavoriteHadithsCount()

    fun getAllNarrators(): Flow<List<String>> = hadithDao.getAllNarrators()

    fun getAllCategories(): Flow<List<String>> = hadithDao.getAllCategories()

    suspend fun getHadithsForCollection(collection: String): List<HadithEntity> {
        val all = allHadiths.first()
        val filtered = all.filter { it.collection.equals(collection, ignoreCase = true) }
        return if (filtered.isNotEmpty()) filtered else HadithSeedData.ALL_HADITHS.filter { it.collection.equals(collection, ignoreCase = true) }
    }

    fun getCollections(): List<HadithCollectionMeta> = HadithSeedData.COLLECTIONS

    fun getChapters(collection: String): List<HadithChapterMeta> {
        return when (collection.lowercase()) {
            "bukhari" -> HadithSeedData.BUKHARI_CHAPTERS
            "muslim" -> HadithSeedData.MUSLIM_CHAPTERS
            "nawawi" -> HadithSeedData.NAWAWI_CHAPTERS
            "tirmidhi" -> HadithSeedData.TIRMIDHI_CHAPTERS
            "abudawud" -> HadithSeedData.ABUDAWUD_CHAPTERS
            "nasai" -> HadithSeedData.NASAI_CHAPTERS
            else -> listOf(
                HadithChapterMeta(1, "Book of Faith", 50),
                HadithChapterMeta(2, "Book of Prayer", 80),
                HadithChapterMeta(3, "Book of Fasting", 40)
            )
        }
    }

    fun getHadithsForBook(collection: String, bookNumber: Int): Flow<List<HadithEntity>> {
        scope.launch {
            try {
                val count = hadithDao.getHadithCountForBook(collection, bookNumber)
                if (count == 0) {
                    Log.d(TAG, "No cached hadiths for $collection book $bookNumber. Fetching on-demand...")
                    val apiHadiths = IslamicApiService.fetchHadithBook(collection, bookNumber)
                    if (apiHadiths.isNotEmpty()) {
                        hadithDao.insertHadiths(apiHadiths)
                        Log.d(TAG, "Inserted ${apiHadiths.size} hadiths into Room for $collection book $bookNumber")
                    } else {
                        val fallback = HadithSeedData.getFallbackHadiths(collection, bookNumber)
                        hadithDao.insertHadiths(fallback)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching hadith book: ${e.message}", e)
                val fallback = HadithSeedData.getFallbackHadiths(collection, bookNumber)
                hadithDao.insertHadiths(fallback)
            }
        }
        return hadithDao.getHadithsForBook(collection, bookNumber)
    }

    suspend fun toggleFavorite(collection: String, hadithNumber: Int, currentFavorite: Boolean) {
        hadithDao.updateFavorite(collection, hadithNumber, !currentFavorite)
    }

    suspend fun toggleStudied(collection: String, hadithNumber: Int, currentStudied: Boolean) {
        hadithDao.updateStudied(collection, hadithNumber, !currentStudied, System.currentTimeMillis())
    }

    suspend fun toggleMemorized(collection: String, hadithNumber: Int, currentMemorized: Boolean) {
        hadithDao.updateMemorized(collection, hadithNumber, !currentMemorized, System.currentTimeMillis())
    }

    fun getFavoriteHadiths(): Flow<List<HadithEntity>> = hadithDao.getFavoriteHadiths()

    fun getStudiedHadiths(): Flow<List<HadithEntity>> = hadithDao.getStudiedHadiths()

    fun getMemorizedHadiths(): Flow<List<HadithEntity>> = hadithDao.getMemorizedHadiths()

    fun getStudiedCount(): Flow<Int> = hadithDao.getStudiedHadithsCount()

    fun getMemorizedCount(): Flow<Int> = hadithDao.getMemorizedHadithsCount()

    fun searchHadiths(query: String): Flow<List<HadithEntity>> = hadithDao.searchHadiths(query)
}
