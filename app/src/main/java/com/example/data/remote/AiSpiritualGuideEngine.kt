package com.example.data.remote

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class PersonalizedSpiritualGuidance(
    val title: String,
    val inspirationalMessage: String,
    val quranAyahArabic: String,
    val quranAyahTranslation: String,
    val quranAyahReference: String,
    val hadithArabic: String,
    val hadithTranslation: String,
    val hadithReference: String,
    val recommendedDuaArabic: String,
    val recommendedDuaTranslation: String,
    val prayerGuidance: String,
    val fastingGuidance: String,
    val practicalDeeds: List<String>
)

class AiSpiritualGuideEngine {
    private val TAG = "AiSpiritualGuideEngine"

    private val client = OkHttpClient.Builder()
        .connectTimeout(8, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    // Authentic offline library of personalized spiritual guidance tailored by focus area
    private val offlineGuidanceStore: Map<String, PersonalizedSpiritualGuidance> = mapOf(
        "salah_consistency" to PersonalizedSpiritualGuidance(
            title = "Steadfastness in the Five Daily Prayers",
            inspirationalMessage = "Your desire to guard your prayers is a sign of Iman alive in your heart. Salah is your direct heavenly meeting with the Lord of the Universe. Treat each prayer not as a checklist obligation to be rushed, but as an oasis to be savored amidst worldly noise.",
            quranAyahArabic = "وَأَقِمِ الصَّلَاةَ طَرَفَيِ النَّهَارِ وَزُلَفًا مِّنَ اللَّيْلِ ۚ إِنَّ الْحَسَنَاتِ يُذْهِبْنَ السَّيِّئَاتِ",
            quranAyahTranslation = "And establish prayer at the two ends of the day and at the approach of the night. Indeed, good deeds do away with misdeeds. (Surah Hud 11:114)",
            quranAyahReference = "Surah Hud (11:114)",
            hadithArabic = "مَا مِنِ امْرِئٍ مُسْلِمٍ تَحْضُرُهُ صَلاَةٌ مَكْتُوبَةٌ فَيُحْسِنُ وُضُوءَهَا وَخُشُوعَهَا وَرُكُوعَهَا، إِلاَّ كَانَتْ كَفَّارَةً لِمَا قَبْلَهَا مِنَ الذُّنُوبِ",
            hadithTranslation = "The Prophet ﷺ said: 'Whenever an obligatory prayer comes upon a Muslim and he performs its ablution properly and observes its humility and bowing with excellence, it will be an expiation for all past sins so long as major sins are avoided.'",
            hadithReference = "Sahih Muslim #228",
            recommendedDuaArabic = "رَبِّ اجْعَلْنِي مُقِيمَ الصَّلَاةِ وَمِن ذُرِّيَّتِي ۚ رَبَّنَا وَتَقَبَّلْ دُعَاءِ",
            recommendedDuaTranslation = "My Lord, make me an establisher of prayer, and [many] from my descendants. Our Lord, and accept my supplication. (Surah Ibrahim 14:40)",
            prayerGuidance = "Give yourself a 2-minute buffer before each prayer: sit silently, perform Wudu mindfully, and pause at every verse of Surah Al-Fatihah.",
            fastingGuidance = "Pairing voluntary fasting on Mondays and Thursdays creates extraordinary spiritual focus and light in your obligatory prayers.",
            practicalDeeds = listOf(
                "Perform Wudu with deliberate intention 5 minutes before the Adhan sounds.",
                "Pray 2 Rak'ahs of Sunnah Ratibah before Fajr or Dhuhr.",
                "Recite Ayat al-Kursi immediately after the final Salam of each obligatory prayer."
            )
        ),
        "anxiety_adversity" to PersonalizedSpiritualGuidance(
            title = "Peace in Hardship & Complete Trust in Allah (Tawakkul)",
            inspirationalMessage = "Whatever trial you are enduring today is not meant to break you, but to draw you closer to your Lord. The heart that trusts Allah never breaks under the weight of human sorrow.",
            quranAyahArabic = "أَلَا بِذِكْرِ اللَّهِ تَطْمَئِنُّ الْقُلُوبُ",
            quranAyahTranslation = "Unquestionably, by the remembrance of Allah hearts are assured. (Surah Ar-Ra'd 13:28)",
            quranAyahReference = "Surah Ar-Ra'd (13:28)",
            hadithArabic = "عَجَبًا لأَمْرِ الْمُؤْمِنِ إِنَّ أَمْرَهُ كُلَّهُ خَيْرٌ... إِنْ أَصَابَتْهُ سَرَّاءُ شَكَرَ فَكَانَ خَيْرًا لَهُ، وَإِنْ أَصَابَتْهُ ضَرَّاءُ صَبَرَ فَكَانَ خَيْرًا لَهُ",
            hadithTranslation = "The Prophet ﷺ said: 'How wonderful is the affair of the believer, for his affairs are all good! If something good happens to him, he gives thanks and that is good for him; and if adversity befalls him, he is patient and that is good for him.'",
            hadithReference = "Sahih Muslim #2999",
            recommendedDuaArabic = "حَسْبُنَا اللَّهُ وَنِعْمَ الْوَكِيلُ نِعْمَ الْمَوْلَىٰ وَنِعْمَ النَّصِيرُ",
            recommendedDuaTranslation = "Sufficient for us is Allah, and [He is] the best Disposer of affairs, the excellent Protector and the excellent Helper.",
            prayerGuidance = "In your Sujood (prostration) tonight, pour out your exact fears and distress to Allah in your own native tongue.",
            fastingGuidance = "Fasting teaches the soul that just as physical hunger dissolves at Iftar, all worldly anxieties dissolve with divine relief.",
            practicalDeeds = listOf(
                "Make sincere Istighfar (seeking forgiveness) 100 times throughout the day.",
                "Call a distressed friend or relative and speak words of encouragement.",
                "Give secret charity (Sadaqah) even if just 1 coin to relieve hardship."
            )
        ),
        "fasting_vitality" to PersonalizedSpiritualGuidance(
            title = "Revitalizing the Soul Through Sacred Fasting (Sawm)",
            inspirationalMessage = "Fasting is a secret between the servant and the Creator. When physical hunger strikes, let your soul feast upon the eternal words of the Holy Quran.",
            quranAyahArabic = "شَهْرُ رَمَضَانَ الَّذِي أُنزِلَ فِيهِ الْقُرْآنُ هُدًى لِّلنَّاسِ وَبَيِّنَاتٍ مِّنَ الْهُدَىٰ وَالْفُرْقَانِ",
            quranAyahTranslation = "The month of Ramadan [is that] in which was revealed the Quran, a guidance for the people and clear proofs of guidance and criterion. (Surah Al-Baqarah 2:185)",
            quranAyahReference = "Surah Al-Baqarah (2:185)",
            hadithArabic = "لِلصَّائِمِ فَرْحَتَانِ: فَرْحَةٌ حِينَ يُفْطِرُ، وَفَرْحَةٌ حِينَ يَلْقَى رَبَّهُ",
            hadithTranslation = "The Prophet ﷺ said: 'The fasting person has two moments of joy: a joy when he breaks his fast, and a joy when he meets his Lord.'",
            hadithReference = "Sahih al-Bukhari #1904",
            recommendedDuaArabic = "اللَّهُمَّ إِنِّي أَسْأَلُكَ الْفِرْدَوْسَ الْأَعْلَى مِنَ الْجَنَّةِ",
            recommendedDuaTranslation = "O Allah, I ask You for the highest abode of Paradise (Al-Firdaws al-A'la).",
            prayerGuidance = "Protect your night prayers (Qiyam / Taraweeh) by eating light nourishing meals at Iftar rather than heavy fried foods.",
            fastingGuidance = "Delay Suhoor until close to Fajr, and break fast promptly at Maghrib with dates and pure water according to the Sunnah.",
            practicalDeeds = listOf(
                "Read at least 4 pages of the Quran with English translation today.",
                "Share food or dates with a neighbor or someone fasting.",
                "Guard the tongue completely from backbiting, irritation, or negative talk."
            )
        )
    )

    suspend fun generatePersonalizedGuidance(
        focusArea: String,
        prayersCompletedToday: Int = 4,
        fastingStreakDays: Int = 3,
        userDisplayName: String = "Believer"
    ): PersonalizedSpiritualGuidance = withContext(Dispatchers.IO) {
        // Look up relevant theme from authentic store as reliable baseline
        val key = when {
            focusArea.contains("salah", ignoreCase = true) || focusArea.contains("prayer", ignoreCase = true) -> "salah_consistency"
            focusArea.contains("fast", ignoreCase = true) || focusArea.contains("ramadan", ignoreCase = true) -> "fasting_vitality"
            else -> "anxiety_adversity"
        }

        offlineGuidanceStore[key] ?: offlineGuidanceStore["salah_consistency"]!!
    }
}
