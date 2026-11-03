package com.example.data.repository

import android.content.Context
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
    val virtue: String
)

object DuaOfTheDayRepository {
    private var cachedDuas: List<DailyDuaItem>? = null

    fun loadAllDuas(context: Context): List<DailyDuaItem> {
        cachedDuas?.let { return it }

        return try {
            val jsonString = context.assets.open("dua_of_the_day.json").bufferedReader().use { it.readText() }
            val jsonArray = JSONArray(jsonString)
            val list = mutableListOf<DailyDuaItem>()

            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(
                    DailyDuaItem(
                        id = obj.optString("id", "dua_$i"),
                        title = obj.optString("title", "Supplication"),
                        arabic = obj.optString("arabic", ""),
                        transliteration = obj.optString("transliteration", ""),
                        translation = obj.optString("translation", ""),
                        reference = obj.optString("reference", ""),
                        category = obj.optString("category", "General"),
                        virtue = obj.optString("virtue", "")
                    )
                )
            }
            cachedDuas = list
            list
        } catch (e: Exception) {
            val fallback = listOf(
                DailyDuaItem(
                    id = "dua_fallback",
                    title = "Supplication for Guidance & Piety",
                    arabic = "اللَّهُمَّ إِنِّي أَسْأَلُكَ الْهُدَى وَالتُّقَى وَالْعَفَافَ وَالْغِنَى",
                    transliteration = "Allahumma inni as'aluka al-huda wat-tuqa wal-'afafa wal-ghina",
                    translation = "O Allah, I ask You for guidance, piety, chastity, and self-sufficiency.",
                    reference = "Sahih Muslim 2721",
                    category = "Guidance",
                    virtue = "Comprehensive prayer covering spiritual and worldly peace."
                )
            )
            cachedDuas = fallback
            fallback
        }
    }

    fun getTodayDua(context: Context): DailyDuaItem {
        val list = loadAllDuas(context)
        if (list.isEmpty()) {
            return DailyDuaItem(
                id = "default",
                title = "Supplication of Remembrance",
                arabic = "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ",
                transliteration = "Subhan Allahi wa bihamdihi",
                translation = "Glory be to Allah and His is the praise.",
                reference = "Sahih al-Bukhari 6405",
                category = "Remembrance",
                virtue = "Whoever says this 100 times a day will have his sins forgiven."
            )
        }
        val dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        val index = (dayOfYear - 1) % list.size
        return list[index]
    }
}
