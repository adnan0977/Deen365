package com.example.data.repository

import com.example.data.local.DuaDao
import com.example.data.local.DuaEntity
import com.example.data.seed.DuaSeedData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DuaRepository(
    private val duaDao: DuaDao,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {

    init {
        scope.launch {
            seedDuasIfEmpty()
        }
    }

    suspend fun seedDuasIfEmpty() = withContext(Dispatchers.IO) {
        val entities = DuaSeedData.DUAS.map { item ->
            DuaEntity(
                id = item.id,
                title = item.title,
                category = item.category,
                arabicText = item.arabicText,
                transliteration = item.transliteration,
                translation = item.translation,
                explanation = item.explanation,
                reference = item.reference,
                recommendedCount = item.recommendedCount,
                isFavorite = false,
                isMemorized = false,
                practiceCount = 0,
                lastPracticedTimestamp = 0L,
                customFolder = "",
                userNote = ""
            )
        }
        duaDao.insertNewDuas(entities)
    }

    suspend fun updateCustomFolder(id: String, folder: String) = withContext(Dispatchers.IO) {
        duaDao.updateCustomFolder(id, folder)
    }

    suspend fun updateUserNote(id: String, note: String) = withContext(Dispatchers.IO) {
        duaDao.updateUserNote(id, note)
    }

    fun getCustomFolders(): Flow<List<String>> = duaDao.getCustomFolders()

    fun getAllDuas(): Flow<List<DuaEntity>> = duaDao.getAllDuas()

    fun getDuasByCategory(category: String): Flow<List<DuaEntity>> {
        return if (category == "All") {
            duaDao.getAllDuas()
        } else {
            duaDao.getDuasByCategory(category)
        }
    }

    fun getFavoriteDuas(): Flow<List<DuaEntity>> = duaDao.getFavoriteDuas()

    fun getMemorizedDuas(): Flow<List<DuaEntity>> = duaDao.getMemorizedDuas()

    fun searchDuas(query: String): Flow<List<DuaEntity>> = duaDao.searchDuas(query)

    suspend fun toggleFavorite(id: String, currentFavorite: Boolean) = withContext(Dispatchers.IO) {
        duaDao.updateFavorite(id, !currentFavorite)
    }

    suspend fun toggleMemorized(id: String, currentMemorized: Boolean) = withContext(Dispatchers.IO) {
        duaDao.updateMemorized(id, !currentMemorized, System.currentTimeMillis())
    }

    suspend fun incrementPractice(id: String, currentCount: Int) = withContext(Dispatchers.IO) {
        duaDao.updatePracticeCount(id, currentCount + 1, System.currentTimeMillis())
    }

    suspend fun resetPractice(id: String) = withContext(Dispatchers.IO) {
        duaDao.resetPracticeCount(id)
    }

    fun getMemorizedCount(): Flow<Int> = duaDao.getMemorizedDuasCount()

    fun getFavoriteCount(): Flow<Int> = duaDao.getFavoriteDuasCount()

    suspend fun getDailyDuaFromDb(dayOffset: Int = 0): DuaEntity? = withContext(Dispatchers.IO) {
        val count = duaDao.getDuasCount()
        if (count == 0) {
            seedDuasIfEmpty()
        }
        val allDuas = duaDao.getAllDuasDirect()
        if (allDuas.isNotEmpty()) {
            val calendar = java.util.Calendar.getInstance()
            val dayOfYear = calendar.get(java.util.Calendar.DAY_OF_YEAR)
            val year = calendar.get(java.util.Calendar.YEAR)
            val dayHash = (dayOfYear + year * 365 + dayOffset)
            val index = Math.floorMod(dayHash, allDuas.size)
            allDuas[index]
        } else {
            null
        }
    }
}
