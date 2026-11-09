package com.example.data.repository

import android.content.Context
import com.example.data.local.DeenDatabase
import com.example.data.local.DuaEntity
import com.example.data.seed.DuaSeedData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.util.Calendar

data class DailyDuaItem(
    val id: String,
    val title: String,
    val arabic: String,
    val transliteration: String,
    val translation: String,
    val reference: String,
    val category: String,
    val virtue: String,
    val recommendedCount: Int = 1,
    val isFavorite: Boolean = false,
    val isMemorized: Boolean = false,
    val practiceCount: Int = 0
)

fun DuaEntity.toDailyDuaItem(): DailyDuaItem = DailyDuaItem(
    id = id,
    title = title,
    arabic = arabicText,
    transliteration = transliteration,
    translation = translation,
    reference = reference,
    category = category,
    virtue = explanation,
    recommendedCount = recommendedCount,
    isFavorite = isFavorite,
    isMemorized = isMemorized,
    practiceCount = practiceCount
)

object DuaOfTheDayRepository {
    private var cachedDuas: List<DailyDuaItem>? = null

    /**
     * Suspending fetch that queries the local Room database (DeenDatabase.duaDao)
     * for authentic supplications, ensuring seeding has occurred.
     */
    suspend fun getTodayDuaFromDatabase(context: Context, dayOffset: Int = 0): DuaEntity = withContext(Dispatchers.IO) {
        val db = DeenDatabase.getDatabase(context)
        val dao = db.duaDao()
        val count = dao.getDuasCount()
        if (count == 0) {
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
            dao.insertNewDuas(entities)
        }
        val allDuas = dao.getAllDuasDirect()
        val list = if (allDuas.isNotEmpty()) allDuas else DuaSeedData.DUAS.map { item ->
            DuaEntity(
                id = item.id,
                title = item.title,
                category = item.category,
                arabicText = item.arabicText,
                transliteration = item.transliteration,
                translation = item.translation,
                explanation = item.explanation,
                reference = item.reference,
                recommendedCount = item.recommendedCount
            )
        }
        val calendar = Calendar.getInstance()
        val dayOfYear = calendar.get(Calendar.DAY_OF_YEAR)
        val year = calendar.get(Calendar.YEAR)
        val dayHash = (dayOfYear + year * 365 + dayOffset)
        val index = Math.floorMod(dayHash, list.size)
        list[index]
    }

    /**
     * Synchronous getter that fetches the authentic supplication for today.
     * Tries the local Room database first, falling back to DuaSeedData or asset JSON.
     */
    fun getTodayDua(context: Context, dayOffset: Int = 0): DailyDuaItem {
        try {
            // Attempt fast query from Room database in background IO
            return runBlocking(Dispatchers.IO) {
                getTodayDuaFromDatabase(context, dayOffset).toDailyDuaItem()
            }
        } catch (_: Exception) {
            // Fallback to in-memory authentic DuaSeedData
            val list = DuaSeedData.DUAS
            val calendar = Calendar.getInstance()
            val dayOfYear = calendar.get(Calendar.DAY_OF_YEAR)
            val year = calendar.get(Calendar.YEAR)
            val dayHash = (dayOfYear + year * 365 + dayOffset)
            val index = Math.floorMod(dayHash, list.size)
            val item = list[index]
            return DailyDuaItem(
                id = item.id,
                title = item.title,
                arabic = item.arabicText,
                transliteration = item.transliteration,
                translation = item.translation,
                reference = item.reference,
                category = item.category,
                virtue = item.explanation,
                recommendedCount = item.recommendedCount
            )
        }
    }

    fun loadAllDuas(context: Context): List<DailyDuaItem> {
        cachedDuas?.let { return it }

        return try {
            val db = DeenDatabase.getDatabase(context)
            val list = runBlocking(Dispatchers.IO) {
                val dbList = db.duaDao().getAllDuasDirect()
                if (dbList.isNotEmpty()) {
                    dbList.map { it.toDailyDuaItem() }
                } else {
                    DuaSeedData.DUAS.map { item ->
                        DailyDuaItem(
                            id = item.id,
                            title = item.title,
                            arabic = item.arabicText,
                            transliteration = item.transliteration,
                            translation = item.translation,
                            reference = item.reference,
                            category = item.category,
                            virtue = item.explanation,
                            recommendedCount = item.recommendedCount
                        )
                    }
                }
            }
            cachedDuas = list
            list
        } catch (_: Exception) {
            val list = DuaSeedData.DUAS.map { item ->
                DailyDuaItem(
                    id = item.id,
                    title = item.title,
                    arabic = item.arabicText,
                    transliteration = item.transliteration,
                    translation = item.translation,
                    reference = item.reference,
                    category = item.category,
                    virtue = item.explanation,
                    recommendedCount = item.recommendedCount
                )
            }
            cachedDuas = list
            list
        }
    }
}
