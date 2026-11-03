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

enum class AiCategory(val title: String, val promptPrefix: String) {
    PRACTICE_QNA("Islamic Practices", "Provide authoritative, empathetic, and clear guidance on Islamic practices, rulings, step-by-step instructions, and spiritual advice based strictly on the Holy Quran and authentic Sunnah."),
    NAMAZ_ADVICE("Namaz Guidance", "Provide practical spiritual and psychological advice to attain Khushoo (deep focus) in Namaz."),
    FASTING_GUIDE("Fasting Coach", "Provide spiritual, physical, and mindset advice for fasting and reaping the rewards of Roza."),
    DAILY_REFLECTION("Daily Reflection", "Provide a profound Quranic reflection for today that brings peace and mindful living."),
    HADITH_INSIGHT("Hadith Insight", "Explain an authentic Hadith on character, ethics, and overcoming life challenges."),
    DUA_EXPLORATION("Dua Deep-Dive", "Explain a powerful Dua from the Sunnah, its background, linguistic beauty, and benefits.")
}

data class AiGuidanceResult(
    val category: AiCategory,
    val arabicText: String,
    val reference: String,
    val translation: String,
    val spiritualInsight: String,
    val practicalActionSteps: List<String>,
    val recommendedDua: String,
    val followUpPrompts: List<String>,
    val userQuestion: String = ""
)

object GeminiSpiritualService {
    private const val TAG = "GeminiSpiritualService"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    suspend fun getSpiritualGuidance(
        category: AiCategory,
        userQuery: String
    ): AiGuidanceResult = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            Log.d(TAG, "No active Gemini API key configured, using curated Deen365 offline spiritual repository")
            return@withContext getOfflineSpiritualWisdom(category, userQuery)
        }

        val systemPrompt = """
            You are Deen365 AI, a revered, empathetic, and authentic Islamic spiritual companion.
            Your guidance is strictly grounded in the Holy Quran and authentic Hadiths (Bukhari, Muslim, Tirmidhi, etc.).
            Always answer in a structured JSON format with these exact keys:
            {
              "arabicText": "Arabic script of relevant Quran ayah or Hadith",
              "reference": "Exact reference (e.g. Surah Al-Baqarah 2:286 or Sahih Bukhari 1)",
              "translation": "Accurate, clear English translation",
              "spiritualInsight": "Empathetic, deep spiritual contemplation and contextual wisdom",
              "practicalActionSteps": ["Action 1", "Action 2", "Action 3"],
              "recommendedDua": "Arabic and translation of a related supplication",
              "followUpPrompts": ["Follow-up question 1", "Follow-up question 2"]
            }
            Do not include any markdown fences or extra text outside the JSON object.
        """.trimIndent()

        val fullUserPrompt = "${category.promptPrefix}\nUser request: $userQuery"

        try {
            val jsonPayload = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().put("text", fullUserPrompt))
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
                val responseBody = response.body?.string().orEmpty()
                val jsonResponse = JSONObject(responseBody)
                val candidates = jsonResponse.optJSONArray("candidates")
                val text = candidates?.optJSONObject(0)
                    ?.optJSONObject("content")
                    ?.optJSONArray("parts")
                    ?.optJSONObject(0)
                    ?.optString("text").orEmpty()

                parseGuidanceJson(category, text, userQuery)
            } else {
                Log.w(TAG, "Gemini API call failed with code ${response.code}, falling back to curated wisdom")
                getOfflineSpiritualWisdom(category, userQuery)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error invoking Gemini API: ${e.message}", e)
            getOfflineSpiritualWisdom(category, userQuery)
        }
    }

    private fun parseGuidanceJson(category: AiCategory, rawJson: String, userQuery: String): AiGuidanceResult {
        return try {
            val cleanJson = rawJson.trim().removePrefix("```json").removePrefix("```").removeSuffix("```").trim()
            val obj = JSONObject(cleanJson)

            val actions = mutableListOf<String>()
            val actionsArray = obj.optJSONArray("practicalActionSteps")
            if (actionsArray != null) {
                for (i in 0 until actionsArray.length()) {
                    actions.add(actionsArray.getString(i))
                }
            }

            val followUps = mutableListOf<String>()
            val followUpsArray = obj.optJSONArray("followUpPrompts")
            if (followUpsArray != null) {
                for (i in 0 until followUpsArray.length()) {
                    followUps.add(followUpsArray.getString(i))
                }
            }

            AiGuidanceResult(
                category = category,
                arabicText = obj.optString("arabicText", "فَإِنَّ مَعَ الْعُسْرِ يُسْرًا"),
                reference = obj.optString("reference", "Surah Ash-Sharh (94:5)"),
                translation = obj.optString("translation", "For indeed, with hardship [will be] ease."),
                spiritualInsight = obj.optString("spiritualInsight", "Allah repeats this promise to assure your heart: tests are temporary, and ease is already prepared for you."),
                practicalActionSteps = if (actions.isNotEmpty()) actions else listOf("Take a deep breath and make Dhikr", "Recite HasbunAllahu wa ni'mal wakeel", "Give a small charity (Sadaqah) today"),
                recommendedDua = obj.optString("recommendedDua", "رَبَّنَا آتِنَا فِي الدُّنْيَا حَسَنَةً وَفِي الآخِرَةِ حَسَنَةً وَقِنَا عَذَابَ النَّارِ"),
                followUpPrompts = if (followUps.isNotEmpty()) followUps else listOf("How to maintain patience during trial?", "What did the Prophet (pbuh) say about anxiety?"),
                userQuestion = userQuery
            )
        } catch (e: Exception) {
            getOfflineSpiritualWisdom(category, userQuery)
        }
    }

    /**
     * Fallback repository of verified spiritual wisdom for instant offline responsiveness and Islamic practice queries.
     */
    fun getOfflineSpiritualWisdom(category: AiCategory, query: String): AiGuidanceResult {
        val q = query.lowercase().trim()

        // 1. Sajdah as-Sahw / Forgetfulness in Salah
        if (q.contains("sahw") || q.contains("forget") || q.contains("sajdah") || q.contains("mistake") || q.contains("doubt in prayer") || q.contains("rakat")) {
            return AiGuidanceResult(
                category = AiCategory.NAMAZ_ADVICE,
                arabicText = "إِذَا شَكَّ أَحَدُكُمْ فِي صَلاَتِهِ فَلْيَتَحَرَّ الصَّوَابَ ثُمَّ لِيَسْجُدْ سَجْدَتَيْنِ",
                reference = "Sahih al-Bukhari 401 & Sahih Muslim 571",
                translation = "If any of you has a doubt in his prayer, let him aim at what is correct and complete his prayer accordingly, then perform two prostrations (Sajdah as-Sahw).",
                spiritualInsight = "Forgetfulness is part of human nature, and Allah in His infinite mercy has legislated Sajdah as-Sahw (Prostrations of Forgetfulness) to repair the prayer without needing to repeat it from scratch.\n\n• If you added an extra action by mistake (e.g. 5th Rak'ah): Perform 2 prostrations AFTER the Taslim, then say Salam again.\n• If you omitted an obligatory part (e.g. 1st Tashahhud): Perform 2 prostrations BEFORE the Taslim, then say Salam.\n• If in doubt between 3 or 4 Rak'ahs: Build on certainty (the lesser number, 3), complete the 4th, and do 2 prostrations before Taslim.",
                practicalActionSteps = listOf(
                    "Do not panic if you lose count; base your prayer on the lowest number you are certain of.",
                    "If you missed a Wajib part (like first sitting), continue your prayer and perform 2 Sujuds right before Taslim.",
                    "If you performed an extra bowing or prostration, do Sajdah as-Sahw immediately after concluding the prayer with Salam.",
                    "Remember that Sajdah as-Sahw humbles Satan and elevates your rank before Allah."
                ),
                recommendedDua = "سُبْحَانَ مَنْ لاَ يَنَامُ وَلاَ يَسْهُو • سُبْحَانَ رَبِّيَ الأَعْلَى\n(Glory be to Him Who does not sleep nor forget. Glory be to my Lord, the Most High.)",
                followUpPrompts = listOf(
                    "What are the 14 Pillars (Arkan) of Salah?",
                    "What invalidates prayer completely?"
                ),
                userQuestion = query
            )
        }

        // 2. Forgetful Eating or Drinking While Fasting
        if ((q.contains("fast") || q.contains("sawm") || q.contains("roza")) && (q.contains("eat") || q.contains("drink") || q.contains("forget") || q.contains("water") || q.contains("accident"))) {
            return AiGuidanceResult(
                category = AiCategory.FASTING_GUIDE,
                arabicText = "مَنْ نَسِيَ وَهُوَ صَائِمٌ فَأَكَلَ أَوْ شَرِبَ فَلْيُتِمَّ صَوْمَهُ فَإِنَّمَا أَطْعَمَهُ اللَّهُ وَسَقَاهُ",
                reference = "Sahih al-Bukhari 1933 & Sahih Muslim 1155",
                translation = "Whoever forgets while he is fasting and eats or drinks, let him complete his fast, for it is Allah Who has fed him and given him to drink.",
                spiritualInsight = "SubhanAllah! If you consumed food or water out of genuine forgetfulness, your fast remains 100% VALID! You do not owe any make-up day (Qada) and you do not have to pay any expiation (Kaffarah).\n\nThe moment you remember you are fasting, immediately spit out whatever is in your mouth, rinse gently, and continue fasting with joy and gratitude for Allah's special hospitality.",
                practicalActionSteps = listOf(
                    "Spit out immediately whatever is in your mouth the second you remember.",
                    "Do not stop fasting—your fast is completely valid by consensus of Islamic scholars.",
                    "Thank Allah for His kindness, as the Prophet ﷺ called this food a gift from Allah.",
                    "Make intention to remain attentive throughout the rest of the day."
                ),
                recommendedDua = "الْحَمْدُ لِلَّهِ الَّذِي أَطْعَمَنَا وَسَقَانَا وَجَعَلَنَا مُسْلِمِينَ\n(All praise is due to Allah Who has fed us, given us drink, and made us Muslims.)",
                followUpPrompts = listOf(
                    "What definitely breaks the fast (Mufattirat)?",
                    "Can I use asthma inhalers while fasting?"
                ),
                userQuestion = query
            )
        }

        // 3. Medical Rulings: Inhaler, Eye drops, Injections
        if (q.contains("inhaler") || q.contains("injection") || q.contains("eye drop") || q.contains("ear drop") || q.contains("blood test") || q.contains("medicine") || q.contains("toothpaste")) {
            return AiGuidanceResult(
                category = AiCategory.FASTING_GUIDE,
                arabicText = "يُرِيدُ اللَّهُ بِكُمُ الْيُسْرَ وَلَا يُرِيدُ بِكُمُ الْعُسْرَ",
                reference = "Surah Al-Baqarah (2:185) & Fiqh Council Resolution",
                translation = "Allah intends for you ease and does not intend for you hardship.",
                spiritualInsight = "The majority of modern Islamic jurisprudence councils (including the International Islamic Fiqh Academy) have clarified:\n\n1. Asthma Inhalers: Permissible to use while fasting; the compressed aerosol gas opens the bronchial tubes and does not nourish the stomach.\n2. Eye & Ear Drops: Permissible, as the eye is not a natural passage to the stomach.\n3. Non-nutritive Injections: Antibiotic, insulin, painkiller, and vaccine injections do NOT break the fast. Only nutritive IV drips (glucose) break the fast.\n4. Blood Tests: Taking routine blood samples does not break the fast.\n5. Toothpaste: Permissible if nothing is swallowed.",
                practicalActionSteps = listOf(
                    "Take your asthma inhaler without hesitation if you experience shortness of breath.",
                    "Insulin injections for diabetic patients are permissible and do not break the fast.",
                    "Avoid swallowing water or toothpaste when cleaning teeth.",
                    "If medical illness is severe, utilize the divine exemption to break fast and make it up when healthy."
                ),
                recommendedDua = "اللَّهُمَّ رَبَّ النَّاسِ أَذْهِبِ الْبَاسَ اشْفِ أَنْتَ الشَّافِي لاَ شِفَاءَ إِلاَّ شِفَاؤُكَ شِفَاءً لاَ يُغَادِرُ سَقَمًا\n(O Allah, Lord of mankind, remove the harm and heal, for You are the Healer. There is no healing except Yours.)",
                followUpPrompts = listOf(
                    "Who is exempt from fasting in Ramadan?",
                    "How is Fidyah calculated for chronic illness?"
                ),
                userQuestion = query
            )
        }

        // 4. Tahajjud, Qiyam al-Layl & Witr
        if (q.contains("tahajjud") || q.contains("qiyam") || q.contains("night prayer") || q.contains("witr") || q.contains("wake up")) {
            return AiGuidanceResult(
                category = AiCategory.NAMAZ_ADVICE,
                arabicText = "وَمِنَ اللَّيْلِ فَتَهَجَّدْ بِهِ نَافِلَةً لَّكَ عَسَىٰ أَن يَبْعَثَكَ رَبُّكَ مَقَامًا مَّحْمُودًا",
                reference = "Surah Al-Isra (17:79) & Sahih Muslim 758",
                translation = "And from [part of] the night, pray with it as additional [worship] for you; it is expected that your Lord will resurrect you to a praised station.",
                spiritualInsight = "Tahajjud is the crown of voluntary worship. In the last third of the night, our Lord descends to the lowest heaven in a manner befitting His Majesty, asking: 'Who is calling upon Me, that I may answer him? Who is asking Me, that I may give him? Who is seeking My forgiveness, that I may forgive him?'\n\n• Minimum Tahajjud is 2 Rak'ahs, prayed in units of 2.\n• Conclude with 1 or 3 Rak'ahs of Witr.",
                practicalActionSteps = listOf(
                    "Set an alarm 25 minutes before Fajr adhan to pray 2 Rak'ahs of Tahajjud.",
                    "Make heartfelt personal Dua in Sujud, pouring out all your worries to Allah.",
                    "Conclude with 3 Rak'ahs of Salatul Witr with Dua al-Qunut.",
                    "Avoid heavy dinners and blue screen light before sleeping to make waking effortless."
                ),
                recommendedDua = "اللَّهُمَّ لَكَ الْحَمْدُ أَنْتَ نُورُ السَّمَاوَاتِ وَالأَرْضِ وَمَنْ فِيهِنَّ\n(O Allah, to You belongs all praise; You are the Light of the heavens and the earth and all that is in them.)",
                followUpPrompts = listOf(
                    "How to recite Dua al-Qunut in Witr?",
                    "What did the Prophet ﷺ recite in Tahajjud?"
                ),
                userQuestion = query
            )
        }
        return when (category) {
            AiCategory.DAILY_REFLECTION -> AiGuidanceResult(
                category = category,
                arabicText = "أَلَا بِذِكْرِ اللَّهِ تَطْمَئِنُّ الْقُلُوبُ",
                reference = "Surah Ar-Ra'd (13:28)",
                translation = "Unquestionably, by the remembrance of Allah hearts are assured.",
                spiritualInsight = "The human soul naturally longs for its Creator. When worldly noise and anxiety overwhelm your day, returning to mindful Dhikr resets your inner equilibrium and fills the chest with tranquil peace.",
                practicalActionSteps = listOf(
                    "Set aside 5 quiet minutes after Salah to repeat SubhanAllah, Alhamdulillah, Allahu Akbar 33 times.",
                    "Pause for 30 seconds before every task and consciously say Bismillah.",
                    "Reflect on three personal blessings you experienced in the last 24 hours."
                ),
                recommendedDua = "اللَّهُمَّ أَعِنِّي عَلَى ذِكْرِكَ وَشُكْرِكَ وَحُسْنِ عِبَادَتِكَ\n(O Allah, help me remember You, thank You, and worship You in the best manner.)",
                followUpPrompts = listOf(
                    "How to cultivate presence of heart in Dhikr?",
                    "What are the best times for morning and evening remembrance?"
                )
            )

            AiCategory.HADITH_INSIGHT -> AiGuidanceResult(
                category = category,
                arabicText = "إِنَّمَا الأَعْمَالُ بِالنِّيَّاتِ وَإِنَّمَا لِكُلِّ امْرِئٍ مَا نَوَى",
                reference = "Sahih al-Bukhari 1 (Narrated by Umar ibn al-Khattab RA)",
                translation = "Actions are judged by intentions, and every person will receive what he intended.",
                spiritualInsight = "Intention transforms ordinary daily tasks—working, cooking, resting, speaking kindly to family—into acts of sacred worship. Renewing sincerity removes stress and guarantees divine reward regardless of worldly outcomes.",
                practicalActionSteps = listOf(
                    "Make a silent conscious intention before your next work meeting or study session.",
                    "Purify your intention when giving charity, seeking only the pleasure of Allah.",
                    "Ask Allah to cleanse your heart of showing off (Riya)."
                ),
                recommendedDua = "اللَّهُمَّ إِنِّي أَعُوذُ بِكَ أَنْ أُشْرِكَ بِكَ وَأَنَا أَعْلَمُ، وَأَسْتَغْفِرُكَ لِمَا لاَ أَعْلَمُ\n(O Allah, I seek refuge in You from associating anything with You knowingly, and I ask Your forgiveness for what I do not know.)",
                followUpPrompts = listOf(
                    "How to overcome doubts in intention?",
                    "Can everyday hobbies become acts of worship with good intention?"
                )
            )

            AiCategory.DUA_EXPLORATION -> AiGuidanceResult(
                category = category,
                arabicText = "لا إِلَهَ إِلَّا أَنْتَ سُبْحَانَكَ إِنِّي كُنْتُ مِنَ الظَّالِمِينَ",
                reference = "Surah Al-Anbiya (21:87) - Dua of Prophet Yunus (Jonah)",
                translation = "There is no deity except You; exalted are You. Indeed, I have been of the wrongdoers.",
                spiritualInsight = "Prophet Yunus cried out from the depths of triple darkness (the night, the sea, and the belly of the whale). The Prophet (pbuh) taught that no Muslim supplicates with this Dua for any hardship except that Allah relieves him.",
                practicalActionSteps = listOf(
                    "Recite this supplication whenever feeling trapped, overwhelmed, or burdened.",
                    "Acknowledge your human shortcomings before asking for ease.",
                    "Pair this Dua with two Rak'ahs of Salat al-Hajah (Prayer of Need)."
                ),
                recommendedDua = "يَا حَيُّ يَا قَيُّومُ بِرَحْمَتِكَ أَسْتَغِيثُ، أَصْلِحْ لِي شَأْنِي كُلَّهُ، وَلاَ تَكِلْنِي إِلَى نَفْسِي طَرْفَةَ عَيْنٍ\n(O Ever-Living, O Sustainer, in Your mercy I seek relief. Rectify all my affairs and do not leave me to myself for the blink of an eye.)",
                followUpPrompts = listOf(
                    "What are the best hours for Dua to be answered?",
                    "How to pray during Tahajjud (the last third of the night)?"
                )
            )

            AiCategory.NAMAZ_ADVICE -> AiGuidanceResult(
                category = category,
                arabicText = "قَدْ أَفْلَحَ الْمُؤْمِنُونَ الَّذِينَ هُمْ فِي صَلَاتِهِمْ خَاشِعُونَ",
                reference = "Surah Al-Mu'minun (23:1-2)",
                translation = "Successful indeed are the believers, those who humble themselves in their prayer with Khushoo.",
                spiritualInsight = "Khushoo is not an instant on-off switch; it is a muscle developed before stepping on the prayer mat. Preparing with a mindful Wudu, arriving at the mat 2 minutes early, and understanding the meaning of Surah Al-Fatiha transforms prayer from a chore into a sanctuary.",
                practicalActionSteps = listOf(
                    "Put your phone on silent and place it in another room before beginning Salah.",
                    "Perform Wudu slowly, contemplating sins washing away with each droplet of water.",
                    "Vary the short Surahs recited after Al-Fatiha so your mind remains alert.",
                    "Pause for 3 seconds between each verse of Surah Al-Fatiha to reflect on Allah's answer."
                ),
                recommendedDua = "اللَّهُمَّ أَعِنِّي عَلَى ذِكْرِكَ وَشُكْرِكَ وَحُسْنِ عِبَادَتِكَ\n(O Allah, aid me in remembering You, thanking You, and perfecting my worship.)",
                followUpPrompts = listOf(
                    "What to do when waswas (distracting thoughts) occur in Salah?",
                    "How to wake up consistently for Fajr without snoozing?"
                )
            )

            AiCategory.FASTING_GUIDE -> AiGuidanceResult(
                category = category,
                arabicText = "يَا أَيُّهَا الَّذِينَ آمَنُوا كُتِبَ عَلَيْكُمُ الصِّيَامُ كَمَا كُتِبَ عَلَى الَّذِينَ مِن قَبْلِكُمْ لَعَلَّكُمْ تَتَّقُونَ",
                reference = "Surah Al-Baqarah (2:183)",
                translation = "O you who have believed, decreed upon you is fasting as it was decreed upon those before you that you may become righteous (attain Taqwa).",
                spiritualInsight = "Fasting is a divine training ground for self-mastery. By voluntarily withholding halal food, drink, and desires for Allah's sake during daylight, your soul discovers that it controls the body, not the other way around. Fasting also awakens deep empathy for the hungry.",
                practicalActionSteps = listOf(
                    "Never skip Suhoor (Sehri); the Prophet (pbuh) said: 'Eat Suhoor, for indeed in Suhoor there is blessing.'",
                    "Hydrate generously with water and electrolytes between Iftar and bedtime.",
                    "Guard your tongue and eyes from vain talk and arguments while fasting.",
                    "Make Dua just before breaking your fast at sunset; it is an hour of guaranteed acceptance."
                ),
                recommendedDua = "ذَهَبَ الظَّمَأُ وَابْتَلَّتِ الْعُرُوقُ وَثَبَتَ الأَجْرُ إِنْ شَاءَ اللَّهُ\n(The thirst has gone, the veins are moistened, and the reward is confirmed, if Allah wills.)",
                followUpPrompts = listOf(
                    "What are the sunnah foods to break the fast with?",
                    "How to prepare spiritually in Sha'ban for Ramadan?"
                )
            )

            AiCategory.PRACTICE_QNA -> AiGuidanceResult(
                category = category,
                arabicText = "وَمَا آتَاكُمُ الرَّسُولُ فَخُذُوهُ وَمَا نَهَاكُمْ عَنْهُ فَانتَهُوا",
                reference = "Surah Al-Hashr (59:7)",
                translation = "And whatever the Messenger gives you, take it; and whatever he forbids you from, abstain from it.",
                spiritualInsight = "Every practice in Islam is designed to refine our character, purify our devotion, and anchor our day in Allah's remembrance. When you have questions about prayer, fasting, or purification, refer to the authentic Sunnah with sincerity and humble obedience.",
                practicalActionSteps = listOf(
                    "Learn the essential pillars (Arkan) and conditions (Shuroot) of each worship practice.",
                    "Practice with consistency, even if small; the most beloved deeds to Allah are regular ones.",
                    "Consult authoritative scholars and authentic sources when doubtful."
                ),
                recommendedDua = "رَبِّ زِدْنِي عِلْمًا\n(My Lord, increase me in knowledge. - Surah Taha 20:114)",
                followUpPrompts = listOf(
                    "What are the invalidators of Salah and Sawm?",
                    "How to perform Sujood as-Sahw (Prostration of Forgetfulness)?"
                ),
                userQuestion = query
            )
        }
    }

    suspend fun generateHabitAnalysis(
        userName: String,
        completedPrayers: Int,
        watchedVideosCount: Int,
        totalStudyMinutes: Int,
        spiritualGoal: String
    ): String = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        val prompt = """
            As Deen365 Spiritual Companion, evaluate the Islamic habits for $userName:
            - Daily Salah completed today: $completedPrayers out of 5 prayers
            - Islamic lectures watched: $watchedVideosCount lectures ($totalStudyMinutes total study minutes)
            - Current spiritual goal: "$spiritualGoal"
            
            Provide a warm, personalized 3-part spiritual habit coaching response:
            1. 🌟 Spiritual Diagnosis: Honest and loving evaluation of their consistency.
            2. 📖 Prophetic Encouragement: Quote a relevant Hadith or Quranic verse on consistency in deeds (A'maal).
            3. 🎯 3 Concrete Action Steps: Clear, achievable micro-habits for tomorrow to elevate their Salah and Islamic learning.
        """.trimIndent()

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext getOfflineHabitDiagnosis(userName, completedPrayers, watchedVideosCount, totalStudyMinutes, spiritualGoal)
        }

        try {
            val requestJson = JSONObject().apply {
                val contents = JSONArray().apply {
                    put(JSONObject().apply {
                        put("role", "user")
                        put("parts", JSONArray().apply {
                            put(JSONObject().put("text", prompt))
                        })
                    })
                }
                put("contents", contents)
            }

            val request = Request.Builder()
                .url("$BASE_URL?key=$apiKey")
                .post(requestJson.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = okHttpClient.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (response.isSuccessful) {
                val json = JSONObject(responseBody)
                val candidates = json.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val parts = candidates.getJSONObject(0)
                        .getJSONObject("content")
                        .getJSONArray("parts")
                    if (parts.length() > 0) {
                        return@withContext parts.getJSONObject(0).getString("text")
                    }
                }
            }
            getOfflineHabitDiagnosis(userName, completedPrayers, watchedVideosCount, totalStudyMinutes, spiritualGoal)
        } catch (e: Exception) {
            Log.e(TAG, "Error in habit analysis", e)
            getOfflineHabitDiagnosis(userName, completedPrayers, watchedVideosCount, totalStudyMinutes, spiritualGoal)
        }
    }

    private fun getOfflineHabitDiagnosis(
        userName: String,
        completedPrayers: Int,
        watchedVideosCount: Int,
        totalStudyMinutes: Int,
        spiritualGoal: String
    ): String {
        val prayerStatus = when {
            completedPrayers == 5 -> "Masha'Allah! You have established all 5 daily prayers today. Protecting your Salah on time is the cornerstone of a tranquil heart and divine Barakah."
            completedPrayers >= 3 -> "Alhamdulillah for establishing $completedPrayers prayers today. You have built a solid foundation. Make sincere intention to guard the remaining prayers before sleeping."
            else -> "You have logged $completedPrayers prayer(s) today. Remember, Allah's mercy is vast, and Salah is your direct lifeline to peace in the midst of daily stress."
        }

        return """
            Assalamu Alaikum $userName,
            
            🌟 Spiritual Diagnosis:
            $prayerStatus You have also dedicated $totalStudyMinutes minutes to Islamic lectures ($watchedVideosCount lectures watched), showing an active hunger for sacred knowledge. Aligning knowledge with action is the hallmark of true faith.
            
            📖 Prophetic Wisdom:
            The Messenger of Allah (ﷺ) said:
            "The most beloved of deeds to Allah are those that are most consistent, even if they are small." (Sahih al-Bukhari 6464)
            
            🎯 3 Recommended Action Steps for Tomorrow:
            1. Perform Wudu 10 minutes prior to the Adhan and offer 2 Rak'ahs of Tahiyyatul Wudu with mindful stillness.
            2. Dedicate 15 minutes of your commute or rest time to watch a lecture on Tazkiyah (Purification of the Heart) or Seerah.
            3. Before going to bed, recite Ayatul Kursi and make sincere Istighfar 33 times to renew your spiritual covenant with Allah.
        """.trimIndent()
    }
}
