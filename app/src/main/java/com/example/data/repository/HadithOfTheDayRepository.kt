package com.example.data.repository

import android.content.Context
import com.example.data.local.DeenDatabase
import com.example.data.local.HadithEntity
import com.example.data.seed.HadithSeedData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Calendar

/**
 * Model representing the Hadith of the Day with full spiritual metadata.
 */
data class DailyHadithItem(
    val collection: String,
    val collectionName: String,
    val bookNumber: Int,
    val chapterName: String,
    val hadithNumber: Int,
    val arabicText: String,
    val translation: String,
    val narrator: String,
    val grade: String,
    val referenceText: String,
    val themeTopic: String,
    val practicalLesson: String,
    val isFavorite: Boolean = false,
    val isRead: Boolean = false,
    val isMemorized: Boolean = false
)

fun HadithEntity.toDailyHadithItem(): DailyHadithItem {
    val collectionName = when (collection.lowercase()) {
        "bukhari" -> "Sahih al-Bukhari"
        "muslim" -> "Sahih Muslim"
        "nawawi" -> "40 Hadith Nawawi"
        "tirmidhi" -> "Jami` at-Tirmidhi"
        "abudawud" -> "Sunan Abi Dawud"
        "nasai" -> "Sunan an-Nasa'i"
        else -> collection.replaceFirstChar { it.uppercase() }
    }
    val reference = "$collectionName, Hadith #$hadithNumber"
    val lesson = HadithOfTheDayRepository.derivePracticalLesson(this)
    val topic = chapterName.ifBlank { "Prophetic Guidance" }

    return DailyHadithItem(
        collection = collection,
        collectionName = collectionName,
        bookNumber = bookNumber,
        chapterName = chapterName,
        hadithNumber = hadithNumber,
        arabicText = arabicText,
        translation = translation,
        narrator = narrator,
        grade = grade.ifBlank { "Sahih" },
        referenceText = reference,
        themeTopic = topic,
        practicalLesson = lesson,
        isFavorite = isFavorite,
        isRead = isRead,
        isMemorized = isMemorized
    )
}

/**
 * Local Repository providing curated Daily Hadiths from local Room storage and seed dataset.
 * Includes deterministic day-of-year rotation, interactive previous/next browsing, and reflection insights.
 */
object HadithOfTheDayRepository {

    /**
     * Curated list of high-impact spiritual Hadiths emphasizing ethics, character,
     * worship, kindness, sincerity, and inner peace.
     */
    val CURATED_HADITHS: List<HadithEntity> = HadithSeedData.ALL_HADITHS

    /**
     * Retrieve the Hadith of the Day deterministically from Room database or curated seed list.
     */
    suspend fun getTodayHadith(context: Context, dayOffset: Int = 0): DailyHadithItem = withContext(Dispatchers.IO) {
        val list = try {
            val db = DeenDatabase.getDatabase(context)
            val dao = db.hadithDao()
            val all = dao.getAllHadithsSync()
            if (all.isNotEmpty()) all else CURATED_HADITHS
        } catch (_: Exception) {
            CURATED_HADITHS
        }

        val safeList = if (list.isNotEmpty()) list else CURATED_HADITHS
        val calendar = Calendar.getInstance()
        val dayOfYear = calendar.get(Calendar.DAY_OF_YEAR)
        val year = calendar.get(Calendar.YEAR)
        val dayHash = (dayOfYear + year * 365 + dayOffset)
        val index = Math.floorMod(dayHash, safeList.size)

        safeList[index].toDailyHadithItem()
    }

    /**
     * Synchronous fallback getter for quick initial UI state render.
     */
    fun getTodayHadithSync(dayOffset: Int = 0): DailyHadithItem {
        val list = CURATED_HADITHS
        val calendar = Calendar.getInstance()
        val dayOfYear = calendar.get(Calendar.DAY_OF_YEAR)
        val year = calendar.get(Calendar.YEAR)
        val dayHash = (dayOfYear + year * 365 + dayOffset)
        val index = Math.floorMod(dayHash, list.size)
        return list[index].toDailyHadithItem()
    }

    /**
     * Derive an actionable daily reflection lesson based on the hadith topic and wording.
     */
    fun derivePracticalLesson(hadith: HadithEntity): String {
        val text = hadith.translation.lowercase()
        val chapter = hadith.chapterName.lowercase()

        return when {
            text.contains("intention") || chapter.contains("intention") || chapter.contains("sincerity") ->
                "Purify your intentions before undertaking tasks today. Remind yourself that every deed performed sincerely for Allah becomes worship."

            text.contains("angry") || text.contains("anger") ->
                "Exercise self-control and patience when provoked. Seek refuge in Allah from Satan and pause before responding in moments of frustration."

            text.contains("neighbor") || text.contains("brother") || text.contains("loves for his brother") ->
                "Practice active compassion: wish for those around you the exact ease, joy, and blessings you desire for your own heart."

            text.contains("smile") || text.contains("charity") || text.contains("clean") ->
                "Even a warm smile, gentle word, or removing harm from a pathway counts as continuous charity (Sadaqah) today."

            text.contains("tongue") || text.contains("silent") || text.contains("speech") ->
                "Guard your tongue consciously today. Speak only what builds, encourages, and brings barakah, or observe noble silence."

            text.contains("knowledge") ->
                "Dedicate 10 intentional minutes today to study a verse of the Quran or a Hadith; Allah facilitates a path to Paradise for the seeker."

            text.contains("sujud") || text.contains("prayer") || text.contains("closest") ->
                "Pour your heart out in Sujud during your prayers today; it is the most intimate station between the servant and the Creator."

            text.contains("fasting") || text.contains("suhoor") ->
                "Fasting shields the heart and soul. Treasure every moment of restraint and make sincere dua prior to breaking your fast."

            text.contains("mother") || text.contains("family") || text.contains("women") ->
                "Honor your parents and family members with gentle speech, gratitude, and a helpful gesture without waiting to be asked."

            text.contains("ihsan") || text.contains("excellence") ->
                "Strive for Ihsan (excellence) in your work, prayer, and family interactions today, knowing that Allah observes all things with love."

            else ->
                "Reflect deeply on this Prophetic guidance today and incorporate its wisdom into your speech and conduct."
        }
    }
}
