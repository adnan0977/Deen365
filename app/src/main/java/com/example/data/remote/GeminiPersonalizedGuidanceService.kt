package com.example.data.remote

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/**
 * Service that analyzes live user habits (Salah records, fasting streaks, Quran recitation)
 * to synthesize personalized spiritual reflections, gentle habit reminders, and targeted Duas.
 */
object GeminiPersonalizedGuidanceService {
    private const val TAG = "PersonalGuidanceService"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(45, TimeUnit.SECONDS)
        .readTimeout(45, TimeUnit.SECONDS)
        .build()

    suspend fun generatePersonalizedGuidance(
        telemetrySummary: String,
        userName: String = "Servant of Allah"
    ): AiGuidanceResult = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (_: Exception) {
            ""
        }

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            Log.d(TAG, "Using curated offline spiritual diagnosis")
            return@withContext getOfflinePersonalizedGuidance(telemetrySummary, userName)
        }

        val systemPrompt = """
            You are Deen365 Spiritual Coach AI.
            Analyze the user's real prayer logs, fasting streak, and Quran habits.
            Offer warm, inspiring, and non-judgmental guidance:
            - If they missed prayers: comfort them, encourage sincere Istighfar, and advise them on making up missed prayers with hope.
            - If they are maintaining streaks: congratulate their steadfastness (Istiqaamah) and advise on humility and extra Sunnah/Tahajjud.
            - Recommend a specific daily Quranic verse, authentic Hadith, and targeted Dua for their exact spiritual situation.
            
            Respond ONLY in valid JSON matching:
            {
              "arabicText": "Arabic script of relevant Quran Ayah or Hadith",
              "reference": "Exact reference citation",
              "translation": "Clear English translation",
              "spiritualInsight": "Empathetic diagnosis of their progress and specific encouragement",
              "practicalActionSteps": [
                "Action 1 tailored to their prayer/fasting status",
                "Action 2 tailored to their prayer/fasting status",
                "Action 3 tailored to their prayer/fasting status"
              ],
              "recommendedDua": "Arabic and translation of a custom supplication",
              "followUpPrompts": [
                "Question 1",
                "Question 2"
              ]
            }
            Do not include markdown fences.
        """.trimIndent()

        try {
            val jsonPayload = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().put("text", "User Name: $userName\nTelemetry Summary:\n$telemetrySummary"))
                        })
                    })
                })
                put("systemInstruction", JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().put("text", systemPrompt))
                    })
                })
                put("generationConfig", JSONObject().apply {
                    put("temperature", 0.7)
                    put("responseMimeType", "application/json")
                })
            }

            val request = Request.Builder()
                .url("$BASE_URL?key=$apiKey")
                .post(jsonPayload.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = okHttpClient.newCall(request).execute()
            if (response.isSuccessful) {
                val body = response.body?.string().orEmpty()
                val jsonResponse = JSONObject(body)
                val candidates = jsonResponse.optJSONArray("candidates")
                val text = candidates?.optJSONObject(0)
                    ?.optJSONObject("content")
                    ?.optJSONArray("parts")
                    ?.optJSONObject(0)
                    ?.optString("text").orEmpty()

                parseGuidanceJson(text, telemetrySummary)
            } else {
                getOfflinePersonalizedGuidance(telemetrySummary, userName)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error invoking personal guidance: ${e.message}", e)
            getOfflinePersonalizedGuidance(telemetrySummary, userName)
        }
    }

    private fun parseGuidanceJson(rawJson: String, summary: String): AiGuidanceResult {
        return try {
            val clean = rawJson.trim().removePrefix("```json").removePrefix("```").removeSuffix("```").trim()
            val obj = JSONObject(clean)

            val actions = mutableListOf<String>()
            val arr = obj.optJSONArray("practicalActionSteps")
            if (arr != null) {
                for (i in 0 until arr.length()) {
                    actions.add(arr.getString(i))
                }
            }

            val followUps = mutableListOf<String>()
            val fArr = obj.optJSONArray("followUpPrompts")
            if (fArr != null) {
                for (i in 0 until fArr.length()) {
                    followUps.add(fArr.getString(i))
                }
            }

            AiGuidanceResult(
                category = AiCategory.DAILY_REFLECTION,
                arabicText = obj.optString("arabicText", "يَا أَيُّهَا الَّذِينَ آمَنُوا اسْتَعِينُوا بِالصَّبْرِ وَالصَّلَاةِ"),
                reference = obj.optString("reference", "Surah Al-Baqarah (2:153)"),
                translation = obj.optString("translation", "O you who have believed, seek help through patience and prayer."),
                spiritualInsight = obj.optString("spiritualInsight", "Every day is a fresh opportunity to draw nearer to Allah through consistent worship."),
                practicalActionSteps = if (actions.isNotEmpty()) actions else listOf("Protect Fajr on time tomorrow", "Recite Ayat al-Kursi after every prayer", "Make sincere Dua at sunset"),
                recommendedDua = obj.optString("recommendedDua", "اللَّهُمَّ أَعِنِّي عَلَى ذِكْرِكَ وَشُكْرِكَ وَحُسْنِ عِبَادَتِكَ"),
                followUpPrompts = if (followUps.isNotEmpty()) followUps else listOf("How to wake up easily for Fajr?", "What are the virtues of fasting Mondays and Thursdays?"),
                userQuestion = summary
            )
        } catch (_: Exception) {
            getOfflinePersonalizedGuidance(summary, "Servant of Allah")
        }
    }

    fun getOfflinePersonalizedGuidance(summary: String, userName: String): AiGuidanceResult {
        val s = summary.lowercase()

        // High Streak / All 5 prayers offered
        if (s.contains("5/5") || s.contains("streak: 5") || s.contains("all offered") || s.contains("high streak")) {
            return AiGuidanceResult(
                category = AiCategory.DAILY_REFLECTION,
                arabicText = "وَالَّذِينَ هُمْ عَلَىٰ صَلَوَاتِهِمْ يُحَافِظُونَ • أُولَٰئِكَ هُمُ الْوَارِثُونَ الَّذِينَ يَرِثُونَ الْفِرْدَوْسَ هُمْ فِيهَا خَالِدُونَ",
                reference = "Surah Al-Mu'minun (23:9-11)",
                translation = "And they who carefully maintain their prayers - Those are the inheritors who will inherit Al-Firdaus; they will abide therein eternally.",
                spiritualInsight = "Masha'Allah tabarakAllah, $userName! Your dedication to establishing all five daily prayers and maintaining your streak is the foundation of divine tranquility and Firdaus. When Salah is guarded, your hours, health, and family are blessed with Barakah.",
                practicalActionSteps = listOf(
                    "Add 2 Rak'ahs of Sunnah Ratibah before or after your obligatory prayers today.",
                    "If waking before Fajr, pray 2 Rak'ahs of Tahajjud in the stillness of the night.",
                    "Make intention for Sunnah fasting on Monday and Thursday to elevate your spiritual rank."
                ),
                recommendedDua = "رَبِّ اجْعَلْنِي مُقِيمَ الصَّلَاةِ وَمِن ذُرِّيَّتِي ۚ رَبَّنَا وَتَقَبَّلْ دُعَاءِ\n(My Lord, make me an establisher of prayer, and [many] from my descendants. Our Lord, and accept my supplication.)",
                followUpPrompts = listOf(
                    "What are the 12 Sunnah Ratibah prayers that build a palace in Jannah?",
                    "How to increase Khushoo (mindful presence) in Ruku and Sujud?"
                ),
                userQuestion = summary
            )
        }

        // Fasting active or Sunnah fasting streak
        if (s.contains("fasting: true") || s.contains("fasted") || s.contains("roza")) {
            return AiGuidanceResult(
                category = AiCategory.FASTING_GUIDE,
                arabicText = "كُلُّ عَمَلِ ابْنِ آدَمَ يُضَاعَفُ الْحَسَنَةُ عَشْرُ أَمْثَالِهَا إِلَى سَبْعِمِائَةِ ضِعْفٍ قَالَ اللَّهُ عَزَّ وَجَلَّ إِلاَّ الصَّوْمَ فَإِنَّهُ لِي وَأَنَا أَجْزِي بِهِ",
                reference = "Sahih Muslim 1151",
                translation = "Every deed of the son of Adam is given tenfold to seven hundredfold reward. Allah the Exalted said: 'Except for fasting, for it is done for Me and I shall reward it.'",
                spiritualInsight = "May Allah accept your fast, $userName! Your hunger and thirst are silent declarations that Allah's pleasure matters more to you than physical comfort. The supplication of a fasting person at the moment of Iftar is never rejected.",
                practicalActionSteps = listOf(
                    "Prepare your personal Dua list 10 minutes prior to Maghrib to present your heartfelt requests at Iftar.",
                    "Break your fast on dates and fresh water following the exact Sunnah of the Prophet (ﷺ).",
                    "Guard your tongue from idle speech, anger, or backbiting so the reward of your fast remains whole."
                ),
                recommendedDua = "ذَهَبَ الظَّمَأُ وَابْتَلَّتِ الْعُرُوقُ وَثَبَتَ الأَجْرُ إِنْ شَاءَ اللَّهُ\n(The thirst has gone, the veins are moistened, and the reward is confirmed, if Allah wills.)",
                followUpPrompts = listOf(
                    "What are the 3 White Days (Ayyam al-Beed) each Hijri month?",
                    "How to balance physical energy during work while fasting?"
                ),
                userQuestion = summary
            )
        }

        // Default: Gentle encouragement to renew commitment
        return AiGuidanceResult(
            category = AiCategory.DAILY_REFLECTION,
            arabicText = "قُلْ يَا عِبَادِيَ الَّذِينَ أَسْرَفُوا عَلَىٰ أَنفُسِهِمْ لَا تَقْنَطُوا مِن رَّحْمَةِ اللَّهِ ۚ إِنَّ اللَّهَ يَغْفِرُ الذُّنُوبَ جَمِيعًا",
            reference = "Surah Az-Zumar (39:53)",
            translation = "Say, 'O My servants who have transgressed against themselves, do not despair of the mercy of Allah. Indeed, Allah forgives all sins.'",
            spiritualInsight = "Assalamu Alaikum $userName, if you fell behind on your prayers or missed a Salah today, do not allow Shaytan to whisper despair into your heart. Allah named Himself Al-Ghaffar (The Perpetual Forgiver). A single sincere prostration with regret wipes the slate clean.",
            practicalActionSteps = listOf(
                "Make fresh Wudu with presence of mind, feeling the water carry away tiredness and mistakes.",
                "Immediately offer any missed prayer (Qada) without delaying further.",
                "Recite 'Astaghfirullah wa atubu ilayh' 100 times throughout your day."
            ),
            recommendedDua = "اللَّهُمَّ أَنْتَ رَبِّي لَا إِلَهَ إِلَّا أَنْتَ خَلَقْتَنِي وَأَنَا عَبْدُكَ وَأَنَا عَلَى عَهْدِكَ وَوَعْدِكَ مَا اسْتَطَعْتُ أَعُوذُ بِكَ مِنْ شَرِّ مَا صَنَعْتُ أَبُوءُ لَكَ بِنِعْمَتِكَ عَلَيَّ وَأَبُوءُ لَكَ بِذَنْبِي فَاغْفِرْ لِي فَإِنَّهُ لَا يَغْفِرُ الذُّنُوبَ إِلَّا أَنْتَ",
            followUpPrompts = listOf(
                "How can I set up effective prayer reminders on my phone?",
                "What is the best way to make up missed prayers systematically?"
            ),
            userQuestion = summary
        )
    }
}
