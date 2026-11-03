package com.example.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.data.seed.AzanPhrase
import org.json.JSONArray
import org.json.JSONObject

@Entity(tableName = "namaz_prayers")
data class NamazPrayerEntity(
    @PrimaryKey val id: String, // "fajr", "dhuhr", "asr", "maghrib", "isha"
    val name: String,
    val arabicTitle: String,
    val rakatsSummary: String,
    val totalRakats: Int,
    val recitationRule: String,
    val bestTime: String,
    val hadithVirtue: String,
    val isDownloaded: Boolean = false,
    val downloadTimestamp: Long = 0L,
    val cachedSizeBytes: Long = 0L,
    val stepCount: Int = 0
)

@Entity(
    tableName = "namaz_steps",
    indices = [Index(value = ["prayerId", "stepIndex"])]
)
data class NamazStepEntity(
    @PrimaryKey val id: String, // e.g., "fajr_0", "fajr_1"
    val prayerId: String,
    val stepIndex: Int,
    val title: String,
    val arabicName: String,
    val posture: String,
    val arabicRecitation: String,
    val transliteration: String,
    val englishTranslation: String,
    val repetitionGuide: String = "1x",
    val audioId: String,
    val notes: String,
    val audioLocalPath: String? = null,
    val isAudioDownloaded: Boolean = false
)

@Entity(tableName = "namaz_azans")
data class NamazAzanEntity(
    @PrimaryKey val prayerId: String, // "fajr", "dhuhr", etc.
    val prayerName: String,
    val isFajr: Boolean = false,
    val audioId: String,
    val phrasesJson: String,
    val duaAfterAzanArabic: String,
    val duaAfterAzanTransliteration: String,
    val duaAfterAzanTranslation: String,
    val audioLocalPath: String? = null,
    val isAudioDownloaded: Boolean = false
) {
    fun parsePhrases(): List<AzanPhrase> {
        val list = mutableListOf<AzanPhrase>()
        try {
            val jsonArray = JSONArray(phrasesJson)
            for (i in 0 until jsonArray.length()) {
                val item = jsonArray.getJSONObject(i)
                list.add(
                    AzanPhrase(
                        arabic = item.optString("arabic", ""),
                        transliteration = item.optString("transliteration", ""),
                        translation = item.optString("translation", ""),
                        repeats = item.optString("repeats", "1x")
                    )
                )
            }
        } catch (_: Exception) {
            // return empty list on malformed json
        }
        return list
    }

    companion object {
        fun phrasesToJson(phrases: List<AzanPhrase>): String {
            val jsonArray = JSONArray()
            for (phrase in phrases) {
                val item = JSONObject().apply {
                    put("arabic", phrase.arabic)
                    put("transliteration", phrase.transliteration)
                    put("translation", phrase.translation)
                    put("repeats", phrase.repeats)
                }
                jsonArray.put(item)
            }
            return jsonArray.toString()
        }
    }
}
