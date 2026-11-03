package com.example.data.remote

import android.util.Log
import com.example.data.local.AyahEntity
import com.example.data.local.HadithEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object IslamicApiService {
    private const val TAG = "IslamicApiService"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    /**
     * Fetches complete Surah with Arabic Uthmani script and English Sahih International translation.
     */
    suspend fun fetchSurahAyahs(surahNumber: Int): List<AyahEntity> = withContext(Dispatchers.IO) {
        val url = "https://api.alquran.cloud/v1/surah/$surahNumber/editions/quran-uthmani,en.sahih"
        try {
            val request = Request.Builder().url(url).build()
            val response = okHttpClient.newCall(request).execute()
            if (response.isSuccessful) {
                val body = response.body?.string().orEmpty()
                val json = JSONObject(body)
                if (json.optString("status") == "OK") {
                    val dataArray = json.getJSONArray("data")
                    val arabicEdition = dataArray.getJSONObject(0)
                    val englishEdition = dataArray.getJSONObject(1)

                    val arabicAyahs = arabicEdition.getJSONArray("ayahs")
                    val englishAyahs = englishEdition.getJSONArray("ayahs")

                    val result = mutableListOf<AyahEntity>()
                    val count = minOf(arabicAyahs.length(), englishAyahs.length())

                    for (i in 0 until count) {
                        val arObj = arabicAyahs.getJSONObject(i)
                        val enObj = englishAyahs.getJSONObject(i)
                        val numInSurah = arObj.getInt("numberInSurah")
                        val arText = arObj.getString("text")
                        val enText = enObj.getString("text")

                        result.add(
                            AyahEntity(
                                surahNumber = surahNumber,
                                ayahNumber = numInSurah,
                                arabicText = arText,
                                translation = enText,
                                transliteration = ""
                            )
                        )
                    }
                    if (result.isNotEmpty()) {
                        Log.d(TAG, "Successfully fetched ${result.size} ayahs for Surah $surahNumber from API")
                        return@withContext result
                    }
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Network call failed for Surah $surahNumber: ${e.message}")
        }
        emptyList()
    }

    /**
     * Fetches Hadiths for a specific collection and chapter from public Hadith CDN.
     */
    suspend fun fetchHadithBook(collection: String, bookNumber: Int): List<HadithEntity> = withContext(Dispatchers.IO) {
        // Formatted hadith endpoint
        val colKey = collection.lowercase()
        val url = "https://cdn.jsdelivr.net/gh/fawazahmed0/hadith-api@1/editions/eng-$colKey/sections/$bookNumber.json"
        try {
            val request = Request.Builder().url(url).build()
            val response = okHttpClient.newCall(request).execute()
            if (response.isSuccessful) {
                val body = response.body?.string().orEmpty()
                val json = JSONObject(body)
                val hadithsArray = json.optJSONArray("hadiths")
                if (hadithsArray != null && hadithsArray.length() > 0) {
                    val result = mutableListOf<HadithEntity>()
                    for (i in 0 until hadithsArray.length()) {
                        val item = hadithsArray.getJSONObject(i)
                        val hadithNumber = item.optInt("hadithnumber", i + 1)
                        val text = item.optString("text", "")
                        if (text.isNotBlank()) {
                            result.add(
                                HadithEntity(
                                    collection = collection,
                                    bookNumber = bookNumber,
                                    chapterName = "Book $bookNumber",
                                    hadithNumber = hadithNumber,
                                    arabicText = "",
                                    translation = text,
                                    narrator = "Narrated by Prophet's Companions",
                                    grade = "Sahih"
                                )
                            )
                        }
                    }
                    if (result.isNotEmpty()) {
                        Log.d(TAG, "Successfully fetched ${result.size} hadiths for $collection book $bookNumber")
                        return@withContext result
                    }
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed fetching hadiths from API for $collection book $bookNumber: ${e.message}")
        }
        emptyList()
    }
}
