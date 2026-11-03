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
 * Service for the AI Hadith & Dua Companion.
 * Allows users to search by specific themes, emotional states, or life dilemmas,
 * returning authentic Sunnah Hadiths & Duas with verifiable citations and daily explanations.
 */
object GeminiHadithDuaCompanionService {
    private const val TAG = "HadithDuaCompanion"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(45, TimeUnit.SECONDS)
        .readTimeout(45, TimeUnit.SECONDS)
        .build()

    suspend fun findHadithAndDuaForTheme(
        themeOrSituation: String
    ): AiGuidanceResult = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (_: Exception) {
            ""
        }

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            Log.d(TAG, "Using curated offline authentic Hadith & Dua companion repository")
            return@withContext getOfflineHadithDuaForTheme(themeOrSituation)
        }

        val systemPrompt = """
            You are Deen365 Hadith & Dua AI Companion, an authentic Islamic scholar assistant.
            The user is sharing a specific life situation, emotional state, or spiritual question.
            Provide strictly authentic Hadiths (Bukhari, Muslim, Abu Dawud, Tirmidhi, Nasai, Ibn Majah) and authentic Sunnah Duas.
            Respond ONLY in valid JSON matching this exact structure:
            {
              "arabicText": "Arabic text of the primary authentic Hadith or prophetic supplication",
              "reference": "Exact collection and book/number (e.g. Sahih al-Bukhari 6407 or Sahih Muslim 2699)",
              "translation": "Accurate, clear English translation",
              "spiritualInsight": "Empathetic explanation of how this prophetic tradition directly addresses the user's situation and grants inner peace",
              "practicalActionSteps": [
                "Actionable practical step 1",
                "Actionable practical step 2",
                "Actionable practical step 3"
              ],
              "recommendedDua": "Arabic and English translation of the specific Dua for this situation",
              "followUpPrompts": [
                "Related question 1",
                "Related question 2"
              ]
            }
            Do not include markdown code block formatting.
        """.trimIndent()

        try {
            val jsonPayload = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().put("text", "Situation / Theme: $themeOrSituation"))
                        })
                    })
                })
                put("systemInstruction", JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().put("text", systemPrompt))
                    })
                })
                put("generationConfig", JSONObject().apply {
                    put("temperature", 0.6)
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

                parseHadithDuaJson(text, themeOrSituation)
            } else {
                getOfflineHadithDuaForTheme(themeOrSituation)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error invoking Gemini companion: ${e.message}", e)
            getOfflineHadithDuaForTheme(themeOrSituation)
        }
    }

    private fun parseHadithDuaJson(rawJson: String, userQuery: String): AiGuidanceResult {
        return try {
            val clean = rawJson.trim().removePrefix("```json").removePrefix("```").removeSuffix("```").trim()
            val obj = JSONObject(clean)

            val actions = mutableListOf<String>()
            val actionsArr = obj.optJSONArray("practicalActionSteps")
            if (actionsArr != null) {
                for (i in 0 until actionsArr.length()) {
                    actions.add(actionsArr.getString(i))
                }
            }

            val followUps = mutableListOf<String>()
            val followUpsArr = obj.optJSONArray("followUpPrompts")
            if (followUpsArr != null) {
                for (i in 0 until followUpsArr.length()) {
                    followUps.add(followUpsArr.getString(i))
                }
            }

            AiGuidanceResult(
                category = AiCategory.HADITH_INSIGHT,
                arabicText = obj.optString("arabicText", "إِنَّ مَعَ الْعُسْرِ يُسْرًا"),
                reference = obj.optString("reference", "Sahih al-Bukhari"),
                translation = obj.optString("translation", "Verily, with hardship comes ease."),
                spiritualInsight = obj.optString("spiritualInsight", "The Prophet (ﷺ) taught us that every struggle in the believer's life is accompanied by divine compensation and elevation."),
                practicalActionSteps = if (actions.isNotEmpty()) actions else listOf("Perform Wudu", "Recite Istighfar", "Offer 2 Rak'ahs"),
                recommendedDua = obj.optString("recommendedDua", "اللَّهُمَّ رَحْمَتَكَ أَرْجُو فَلَا تَكِلْنِي إِلَى نَفْسِي طَرْفَةَ عَيْنٍ"),
                followUpPrompts = if (followUps.isNotEmpty()) followUps else listOf("How to maintain patience?", "What is the virtue of gratitude?"),
                userQuestion = userQuery
            )
        } catch (_: Exception) {
            getOfflineHadithDuaForTheme(userQuery)
        }
    }

    /**
     * Curated verified Sunnah Hadiths and Duas mapped to specific emotional states and situations.
     */
    fun getOfflineHadithDuaForTheme(query: String): AiGuidanceResult {
        val q = query.lowercase().trim()

        // 1. Anxiety, Stress & Heavy Heart
        if (q.contains("anxiety") || q.contains("stress") || q.contains("worry") || q.contains("overwhelm") || q.contains("sad") || q.contains("depress") || q.contains("panic")) {
            return AiGuidanceResult(
                category = AiCategory.HADITH_INSIGHT,
                arabicText = "مَا يُصِيبُ الْمُسْلِمَ مِنْ نَصَبٍ وَلاَ وَصَبٍ وَلاَ هَمٍّ وَلاَ حَزَنٍ وَلاَ أَذًى وَلاَ غَمٍّ حَتَّى الشَّوْكَةِ يُشَاكُهَا، إِلاَّ كَفَّرَ اللَّهُ بِهَا مِنْ خَطَايَاهُ",
                reference = "Sahih al-Bukhari 5641 & Sahih Muslim 2573",
                translation = "No fatigue, nor disease, nor sorrow, nor sadness, nor hurt, nor distress befalls a Muslim, even if it were the prick he receives from a thorn, but that Allah expiates some of his sins for that.",
                spiritualInsight = "The Messenger of Allah (ﷺ) validated that emotional distress, sadness, and anxiety are genuine human struggles, and he gave the most comforting news: not a single second of your pain is wasted. Even a sigh of exhaustion expiates mistakes and raises your spiritual station.",
                practicalActionSteps = listOf(
                    "Place your right hand over your chest, breathe deeply, and recite the Prophetic Dua for sorrow.",
                    "Drink a glass of water, make fresh Wudu, and let the cool water wash away tension.",
                    "Remember that Allah does not expect perfection from you—only honest turning to Him in humility."
                ),
                recommendedDua = "اللَّهُمَّ إِنِّي عَبْدُكَ ابْنُ عَبْدِكَ ابْنُ أَمَتِكَ نَاصِيَتِي بِيَدِكَ مَاضٍ فِيَّ حُكْمُكَ عَدْلٌ فِيَّ قَضَاؤُكَ أَسْأَلُكَ بِكُلِّ اسْمٍ هُوَ لَكَ سَمَّيْتَ بِهِ نَفْسَكَ أَنْ تَجْعَلَ الْقُرْآنَ رَبِيعَ قَلْبِي وَنُورَ صَدْرِي وَجَلَاءَ حُزْنِي وَذَهَابَ هَمِّي\n(O Allah, make the Quran the spring of my heart, the light of my chest, the banisher of my grief, and the reliever of my anxiety.)",
                followUpPrompts = listOf(
                    "What did the Prophet (pbuh) say about insomnia and restless sleep?",
                    "How to cultivate Tawakkul (trust in Allah) when the future is uncertain?"
                ),
                userQuestion = query
            )
        }

        // 2. Seeking Sustenance (Rizq), Halal Wealth & Debt Relief
        if (q.contains("rizq") || q.contains("money") || q.contains("wealth") || q.contains("job") || q.contains("debt") || q.contains("sustenance") || q.contains("poverty") || q.contains("career")) {
            return AiGuidanceResult(
                category = AiCategory.HADITH_INSIGHT,
                arabicText = "لَوْ أَنَّكُمْ تَتَوَكَّلُونَ عَلَى اللَّهِ حَقَّ تَوَكُّلِهِ لَرَزَقَكُمْ كَمَا يَرْزُقُ الطَّيْرَ تَغْدُو خِمَاصًا وَتَرُوحُ بِطَانًا",
                reference = "Jami` at-Tirmidhi 2344 (Graded Sahih)",
                translation = "If you were to rely upon Allah with the reliance due to Him, He would provide for you just as He provides for the birds: they go out in the morning with empty stomachs and return at dusk full.",
                spiritualInsight = "Notice the profound lesson in this Hadith: birds do not stay asleep in their nests waiting for seeds to drop—they wake up at dawn, fly out, search diligently, and trust Allah. Tawakkul combines striving with total detachment from fear, knowing your Rizq was decreed before the creation of the heavens.",
                practicalActionSteps = listOf(
                    "Abundantly recite Istighfar (Astaghfirullah): the Quran promises that Istighfar opens the heavens with abundant rain and wealth.",
                    "Audit your finances to ensure all income and contracts are 100% Halal and untainted by interest (Riba).",
                    "Give a small charity (Sadaqah) today even if 1 dollar; charity never decreases wealth—it purifies and multiplies it."
                ),
                recommendedDua = "اللَّهُمَّ اكْفِنِي بِحَلَالِكَ عَنْ حَرَامِكَ وَأَغْنِنِي بِفَضْلِكَ عَمَّنْ سِوَاكَ\n(O Allah, suffice me with what is lawful against what is unlawful, and enrich me with Your bounty above all others.) [At-Tirmidhi 3563]",
                followUpPrompts = listOf(
                    "How does keeping good ties of kinship (Silat ar-Rahim) expand Rizq?",
                    "What is the best Dua before a job interview or business meeting?"
                ),
                userQuestion = query
            )
        }

        // 3. Patience in Hardship & Bereavement (Sabr)
        if (q.contains("patience") || q.contains("sabr") || q.contains("loss") || q.contains("death") || q.contains("bereavement") || q.contains("illness") || q.contains("sick") || q.contains("pain")) {
            return AiGuidanceResult(
                category = AiCategory.HADITH_INSIGHT,
                arabicText = "عَجَبًا لأَمْرِ الْمُؤْمِنِ إِنَّ أَمْرَهُ كُلَّهُ خَيْرٌ وَلَيْسَ ذَاكَ لأَحَدٍ إِلاَّ لِلْمُؤْمِنِ إِنْ أَصَابَتْهُ سَرَّاءُ شَكَرَ فَكَانَ خَيْرًا لَهُ وَإِنْ أَصَابَتْهُ ضَرَّاءُ صَبَرَ فَكَانَ خَيْرًا لَهُ",
                reference = "Sahih Muslim 2999",
                translation = "How wonderful is the affair of the believer, for his affairs are all good, and this is for no one except the believer: If something good happens to him, he is thankful, which is good for him; and if something bad happens to him, he is patient, which is good for him.",
                spiritualInsight = "In the eyes of faith, the believer is never defeated. In times of abundance, gratitude elevates your rank; in times of severe trial, beautiful patience (Sabr Jameel) cleanses your sins and prepares a mansion of praise in Paradise.",
                practicalActionSteps = listOf(
                    "Say 'Inna lillahi wa inna ilayhi raji'un' (To Allah we belong and to Him we shall return).",
                    "Do not swallow your tears; weeping out of tender mercy is Sunnah, but keep your tongue moist with praise.",
                    "Look for opportunities to visit an ill person or comfort someone in grief today."
                ),
                recommendedDua = "إِنَّا لِلَّهِ وَإِنَّا إِلَيْهِ رَاجِعُونَ اللَّهُمَّ أْجُرْنِي فِي مُصِيبَتِي وَأَخْلِفْ لِي خَيْرًا مِنْهَا\n(We belong to Allah and to Him we return. O Allah, reward me in my affliction and replace it with something better for me.) [Sahih Muslim 918]",
                followUpPrompts = listOf(
                    "What is the reward of praising Allah during the loss of a loved one?",
                    "How did the Prophet (pbuh) comfort companions in illness?"
                ),
                userQuestion = query
            )
        }

        // 4. Decision Making & Istikhara
        if (q.contains("decision") || q.contains("istikhara") || q.contains("choice") || q.contains("confused") || q.contains("marriage") || q.contains("career move") || q.contains("future")) {
            return AiGuidanceResult(
                category = AiCategory.HADITH_INSIGHT,
                arabicText = "كَانَ رَسُولُ اللَّهِ صلى الله عليه وسلم يُعَلِّمُنَا الاِسْتِخَارَةَ فِي الأُمُورِ كُلِّهَا كَمَا يُعَلِّمُنَا السُّورَةَ مِنَ الْقُرْآنِ",
                reference = "Sahih al-Bukhari 1162",
                translation = "The Messenger of Allah (ﷺ) used to teach us to seek guidance in all matters (Istikhara) just as he would teach us a Surah from the Quran.",
                spiritualInsight = "Istikhara is not a magical crystal ball; it is surrendering your finite human judgment to the Infinite Knowledge of Allah. After praying 2 voluntary Rak'ahs and reciting the Dua of Istikhara, take the most sensible, pious course of action. If it is good, Allah will ease its doors; if not, He will turn it away from you and turn you away from it.",
                practicalActionSteps = listOf(
                    "Offer 2 voluntary Rak'ahs outside of the forbidden prayer times with intention for Istikhara.",
                    "Recite the authentic Dua of Istikhara with complete humility and open heart.",
                    "Consult trusted, pious elders or mentors (Mashwarah), then move forward with peace."
                ),
                recommendedDua = "اللَّهُمَّ إِنِّي أَسْتَخِيرُكَ بِعِلْمِكَ وَأَسْتَقْدِرُكَ بِقُدْرَتِكَ وَأَسْأَلُكَ مِنْ فَضْلِكَ الْعَظِيمِ فَإِنَّكَ تَقْدِرُ وَلَا أَقْدِرُ وَتَعْلَمُ وَلَا أَعْلَمُ وَأَنْتَ عَلَّامُ الْغُيُوبِ",
                followUpPrompts = listOf(
                    "Do I need to see a dream after praying Istikhara?",
                    "How many times can Istikhara be repeated?"
                ),
                userQuestion = query
            )
        }

        // 5. Default Comprehensive Sunnah Companion
        return AiGuidanceResult(
            category = AiCategory.HADITH_INSIGHT,
            arabicText = "اتَّقِ اللَّهَ حَيْثُمَا كُنْتَ، وَأَتْبِعِ السَّيِّئَةَ الْحَسَنَةَ تَمْحُهَا، وَخَالِقِ النَّاسَ بِخُلُقٍ حَسَنٍ",
            reference = "Jami` at-Tirmidhi 1987 (Graded Sahih)",
            translation = "Be conscious of Allah wherever you are, follow up a bad deed with a good deed and it will wipe it out, and behave toward people with good character.",
            spiritualInsight = "This golden advice from the Prophet (ﷺ) encompasses the three dimensions of righteous living: your relationship with Allah (Taqwa), your relationship with yourself (immediate redemption and repentance), and your relationship with others (kindness, patience, and noble manners).",
            practicalActionSteps = listOf(
                "Perform one silent righteous deed today (e.g., charity, Quran recitation, helping parents).",
                "Smile and speak gently to everyone you encounter today—a smile is recognized as charity in the Sunnah.",
                "Whenever a negative thought arises, immediately substitute it with 'Astaghfirullah wa atubu ilayh'."
            ),
            recommendedDua = "اللَّهُمَّ اهْدِنِي لِأَحْسَنِ الأَخْلَاقِ لَا يَهْدِي لِأَحْسَنِهَا إِلَّا أَنْتَ وَاصْرِفْ عَنِّي سَيِّئَهَا لَا يَصْرِفُ عَنِّي سَيِّئَهَا إِلَّا أَنْتَ\n(O Allah, guide me to the best of manners, none guides to the best of them except You; and turn away from me bad manners, none turns them away except You.)",
            followUpPrompts = listOf(
                "What are the heaviest deeds on the Day of Judgment Scale?",
                "How can I maintain consistency in morning and evening Adhkar?"
            ),
            userQuestion = query
        )
    }
}
