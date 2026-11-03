package com.example.data.repository

import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.data.local.AyahDao
import com.example.data.local.AyahEntity
import com.example.data.local.DailyReflectionDao
import com.example.data.local.DailyReflectionEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

data class CuratedVerseReflection(
    val surahNumber: Int,
    val surahName: String,
    val ayahNumber: Int,
    val arabicText: String,
    val translation: String,
    val conciseExplanation: String,
    val reflectionMeaning: String,
    val dailyLifeApplication: String,
    val theme: String
)

class DailyReflectionRepository(
    private val reflectionDao: DailyReflectionDao,
    private val ayahDao: AyahDao,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {
    private val TAG = "DailyReflectionRepo"

    companion object {
        val CURATED_REFLECTIONS = listOf(
            CuratedVerseReflection(
                surahNumber = 2,
                surahName = "Al-Baqarah",
                ayahNumber = 152,
                arabicText = "فَاذْكُرُونِي أَذْكُرْكُمْ وَاشْكُرُوا لِي وَلَا تَكْفُرُونِ",
                translation = "So remember Me; I will remember you. And be grateful to Me and do not deny Me.",
                conciseExplanation = "Allah commands His servants to actively remember Him through worship, praise, and prayer. In return, Allah promises an extraordinary honor: whenever a servant remembers Allah on earth, Allah mentions them with praise among the noble assembly of angels.",
                reflectionMeaning = "In a bustling world where people often feel overlooked, isolated, or anxious, this verse is an intimate anchor. The Supreme Creator of the cosmos pays direct attention to you the moment your heart or lips turn to Him.",
                dailyLifeApplication = "Pause three times today during your daily routine. Take a deep breath and recite 'SubhanAllah wa bihamdihi' with full mindfulness, realizing that Allah is mentioning your name in the heavens at that very moment.",
                theme = "Divine Connection & Remembrance"
            ),
            CuratedVerseReflection(
                surahNumber = 94,
                surahName = "Ash-Sharh",
                ayahNumber = 5,
                arabicText = "فَإِنَّ مَعَ الْعُسْرِ يُسْرًا • إِنَّ مَعَ الْعُسْرِ يُسْرًا",
                translation = "For indeed, with hardship [will be] ease. Indeed, with hardship [will be] ease.",
                conciseExplanation = "In classical Arabic rhetoric, 'Al-'Usr' (the hardship) is definite (referring to a single known difficulty), while 'Yusra' (ease) is indefinite (meaning manifold, diverse avenues of relief). As the companions noted, one hardship can never conquer two eases.",
                reflectionMeaning = "Notice Allah says 'with' (Ma'a) hardship comes ease, not 'after'. Divine mercy, internal resilience, and the seeds of relief are intertwined within the trial itself, waiting to blossom.",
                dailyLifeApplication = "Identify the single biggest stressor in your life today. Write down two unexpected doors of support, wisdom, or relief that have accompanied it, and trust that Allah's solution is already unfolding.",
                theme = "Relief in Hardship"
            ),
            CuratedVerseReflection(
                surahNumber = 13,
                surahName = "Ar-Ra'd",
                ayahNumber = 28,
                arabicText = "الَّذِينَ آمَنُوا وَتَطْمَئِنُّ قُلُوبُهُم بِذِكْرِ اللَّهِ ۗ أَلَا بِذِكْرِ اللَّهِ تَطْمَئِنُّ الْقُلُوبُ",
                translation = "Those who have believed and whose hearts are assured by the remembrance of Allah. Unquestionably, by the remembrance of Allah hearts are assured.",
                conciseExplanation = "The human soul has an innate spiritual hunger that no material luxury, accolade, or entertainment can satisfy. True tranquility (Itmi'nan) settles into the heart only when it aligns with its Creator through constant awareness.",
                reflectionMeaning = "Restlessness and emotional exhaustion are often the heart's cry for spiritual nourishment. Just as a physical body weakens without clean water, a heart withers without the remembrance of Allah.",
                dailyLifeApplication = "Replace 10 minutes of evening phone scrolling with 10 minutes of quiet Istighfar and Salawat. Feel the natural calm and peace settling into your chest before sleep.",
                theme = "Inner Peace & Serenity"
            ),
            CuratedVerseReflection(
                surahNumber = 65,
                surahName = "At-Talaq",
                ayahNumber = 3,
                arabicText = "وَيَرْزُقْهُ مِنْ حَيْثُ لَا يَحْتَسِبُ ۚ وَمَن يَتَوَكَّلْ عَلَى اللَّهِ فَهُوَ حَسْبُهُ",
                translation = "And He will provide for him from where he does not expect. And whoever relies upon Allah - then He is sufficient for him.",
                conciseExplanation = "Tawakkul is the deliberate union of sincere human effort with complete spiritual detachment from relying on the outcome alone. Allah guarantees that the one who places their trust in Al-Wakil (The Ultimate Trustee) will find Him entirely sufficient.",
                reflectionMeaning = "We burn out when we try to bear burdens meant only for God. Doing your best and trusting Allah does not mean life has no obstacles; it means you face them backed by Infinite Power.",
                dailyLifeApplication = "Surrender one anxiety about your career, finances, or family to Allah today. Say: 'HasbiyAllahu la ilaha illa Huwa, 'alayhi tawakkaltu', and focus peacefully on taking the next honest step.",
                theme = "Complete Trust (Tawakkul)"
            ),
            CuratedVerseReflection(
                surahNumber = 39,
                surahName = "Az-Zumar",
                ayahNumber = 53,
                arabicText = "قُلْ يَا عِبَادِيَ الَّذِينَ أَسْرَفُوا عَلَىٰ أَنفُسِهِمْ لَا تَقْنَطُوا مِن رَّحْمَةِ اللَّهِ ۚ إِنَّ اللَّهَ يَغْفِرُ الذُّنُوبَ جَمِيعًا",
                translation = "Say, 'O My servants who have transgressed against themselves [by sinning], do not despair of the mercy of Allah. Indeed, Allah forgives all sins.'",
                conciseExplanation = "Scholars universally describe this as the most hopeful verse in the Quran. Even when addressing those who have excessively sinned, Allah lovingly calls them 'My servants' (Ibadi), opening an unconditional door to sincere repentance.",
                reflectionMeaning = "Shaytan's greatest deception is convincing you that your sins are bigger than Allah's mercy. No matter how many times you stumble, the door of Tawbah is wider than any human shortcoming.",
                dailyLifeApplication = "Do not let guilt hold you back from prayer. Offer two sincere Rak'ahs of Salat al-Tawbah today and ask Allah for a fresh beginning with complete confidence in His forgiving love.",
                theme = "Infinite Mercy & Hope"
            ),
            CuratedVerseReflection(
                surahNumber = 14,
                surahName = "Ibrahim",
                ayahNumber = 7,
                arabicText = "وَإِذْ تَأَذَّنَ رَبُّكُمْ لَئِن شَكَرْتُمْ لَأَزِيدَنَّكُمْ ۖ وَلَئِن كَفَرْتُمْ إِنَّ عَذَابِي لَشَدِيدٌ",
                translation = "And [remember] when your Lord proclaimed, 'If you are grateful, I will surely increase you [in favor]; but if you show ingratitude, indeed, My punishment is severe.'",
                conciseExplanation = "Shukr (gratitude) functions as both a preserver of existing blessings and a catalyst for further barakah. Gratitude in Islam is expressed through the heart's appreciation, the tongue's praise, and the limbs' obedience.",
                reflectionMeaning = "Ingratitude breeds dissatisfaction by obsessing over what is missing. True gratitude reveals that even on our hardest days, the gifts of sight, breath, safety, and faith are immeasurable treasures.",
                dailyLifeApplication = "Express genuine appreciation to at least one person who served or helped you today, and verbally recount five distinct blessings to Allah in your Dua tonight.",
                theme = "Gratitude & Multiplication"
            ),
            CuratedVerseReflection(
                surahNumber = 2,
                surahName = "Al-Baqarah",
                ayahNumber = 186,
                arabicText = "وَإِذَا سَأَلَكَ عِبَادِي عَنِّي فَإِنِّي قَرِيبٌ ۖ أُجِيبُ دَعْوَةَ الدَّاعِ إِذَا دَعَانِ",
                translation = "And when My servants ask you concerning Me, indeed I am near. I respond to the invocation of the supplicant when he calls upon Me.",
                conciseExplanation = "Throughout the Quran, when people ask questions, Allah instructs: 'Say (Qul)...' But in this verse, Allah beautifully omits 'Say' and answers directly: 'Indeed I am near' (Fa-inni Qareeb), emphasizing intimate proximity without intermediaries.",
                reflectionMeaning = "You never need a special credential or formal protocol to speak to Allah. He knows your spoken words and unspoken heartaches alike, and He delights in hearing your humble call.",
                dailyLifeApplication = "In your next Sajdah, spend two uninterrupted minutes asking Allah for your most cherished secret wish in your native tongue, knowing He listens attentively.",
                theme = "Closeness to Allah & Dua"
            ),
            CuratedVerseReflection(
                surahNumber = 3,
                surahName = "Ali 'Imran",
                ayahNumber = 139,
                arabicText = "وَلَا تَهِنُوا وَلَا تَحْزَنُوا وَأَنتُمُ الْأَعْلَوْنَ إِن كُنتُم مُّؤْمِنِينَ",
                translation = "So do not weaken and do not grieve, and you will be superior if you are [true] believers.",
                conciseExplanation = "Revealed following the temporary tactical setback at the Battle of Uhud, this verse restored the psychological morale of the companions by re-anchoring their dignity in faith rather than fleeting worldly status.",
                reflectionMeaning = "External circumstances may challenge your composure, but they cannot strip away your dignity as a believer. True honor is rooted in spiritual conviction and integrity before Allah.",
                dailyLifeApplication = "Stand firm in your moral principles at school or work today. Refuse to compromise honesty or modesty for peer validation.",
                theme = "Courage & Resilience"
            ),
            CuratedVerseReflection(
                surahNumber = 21,
                surahName = "Al-Anbiya",
                ayahNumber = 87,
                arabicText = "لَّا إِلَٰهَ إِلَّا أَنتَ سُبْحَانَكَ إِنِّي كُنتُ مِنَ الظَّالِمِينَ",
                translation = "There is no deity except You; exalted are You. Indeed, I have been of the wrongdoers.",
                conciseExplanation = "The supplication of Prophet Yunus (Jonah) from inside the depths of the whale and ocean. The Prophet ﷺ verified that no distressed believer calls upon Allah with these words except that Allah delivers them from distress.",
                reflectionMeaning = "The pathway out of dark, suffocating situations begins with glorifying Allah's majesty and taking honest ownership of our shortcomings without pride.",
                dailyLifeApplication = "If you feel trapped or weighed down today, repeat this verse (Ayat al-Kareema) slowly 33 times with contemplation, asking Allah for a way out.",
                theme = "Deliverance from Distress"
            ),
            CuratedVerseReflection(
                surahNumber = 2,
                surahName = "Al-Baqarah",
                ayahNumber = 255,
                arabicText = "اللَّهُ لَا إِلَٰهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ ۚ لَا تَأْخُذُهُ سِنَةٌ وَلَا نَوْمٌ",
                translation = "Allah - there is no deity except Him, the Ever-Living, the Sustainer of all existence. Neither drowsiness overtakes Him nor sleep.",
                conciseExplanation = "Ayat al-Kursi (The Throne Verse) is designated by the Prophet ﷺ as the greatest single verse of the Quran, capturing the essence of Divine sovereignty, eternal vigilance, and absolute protection.",
                reflectionMeaning = "The One who upholds galaxies with effortless precision is watching over your life right now. You can rest peacefully knowing that your Protector never tires or sleeps.",
                dailyLifeApplication = "Recite Ayat al-Kursi with full awareness after your Fard prayers today and before going to sleep tonight for Divine tranquility and protection.",
                theme = "Divine Sovereignty & Protection"
            ),
            CuratedVerseReflection(
                surahNumber = 49,
                surahName = "Al-Hujurat",
                ayahNumber = 10,
                arabicText = "إِنَّمَا الْمُؤْمِنُونَ إِخْوَةٌ فَأَصْلِحُوا بَيْنَ أَخَوَيْكُمْ ۚ وَاتَّقُوا اللَّهَ لَعَلَّكُمْ تُرْحَمُونَ",
                translation = "The believers are but brothers, so make settlement between your brothers. And fear Allah that you may receive mercy.",
                conciseExplanation = "The brotherhood of Islam transcends race, border, language, and culture. Allah establishes mutual reconciliation and empathy as conditions for receiving Divine mercy.",
                reflectionMeaning = "Grudges and division weaken our communities and harden hearts. Seeking harmony and forgiving others opens the gates of Allah's forgiveness for our own mistakes.",
                dailyLifeApplication = "Send a warm salam, heartfelt prayer, or message of reconciliation to a family member or friend with whom you have experienced distance or misunderstanding.",
                theme = "Brotherhood & Reconciliation"
            ),
            CuratedVerseReflection(
                surahNumber = 67,
                surahName = "Al-Mulk",
                ayahNumber = 2,
                arabicText = "الَّذِي خَلَقَ الْمَوْتَ وَالْحَيَاةَ لِيَبْلُوَكُمْ أَيُّكُمْ أَحْسَنُ عَمَلًا ۚ وَهُوَ الْعَزِيزُ الْغَفُورُ",
                translation = "[He] who created death and life to test you [as to] which of you is best in deed - and He is the Exalted in Might, the Forgiving.",
                conciseExplanation = "Allah explains the purpose of existence: life and death are not accidental, but an intentional test of character. Fudhail ibn 'Iyad explained 'best in deed' as that which is purest in intention and most aligned with the Sunnah.",
                reflectionMeaning = "Our value before Allah is not measured by social status, followers, or wealth, but by the sincerity and excellence (Ihsan) we bring to every small action.",
                dailyLifeApplication = "Choose one routine task today—such as doing chores, helping a parent, or completing a work task—and do it with utmost excellence solely to please Allah.",
                theme = "Purpose of Life & Excellence"
            )
        )

        fun formatShareText(reflection: DailyReflectionEntity): String {
            return """
                🌟 Daily Quranic Reflection • Deen365
                
                📖 ${reflection.surahName} (${reflection.surahNumber}:${reflection.ayahNumber})
                "${reflection.arabicText}"
                
                Translation:
                "${reflection.translation}"
                
                💡 Concise Explanation:
                ${reflection.conciseExplanation}
                
                ✨ Spiritual Reflection:
                ${reflection.reflectionMeaning}
                
                🌱 Daily Life Application:
                ${reflection.dailyLifeApplication}
                
                ✨ Theme: ${reflection.theme}
                Shared via Deen365 • Sacred Knowledge & Daily Practice
            """.trimIndent()
        }

        fun shareReflection(context: Context, reflection: DailyReflectionEntity) {
            val text = formatShareText(reflection)
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "Daily Quranic Reflection: ${reflection.surahName} (${reflection.surahNumber}:${reflection.ayahNumber})")
                putExtra(Intent.EXTRA_TEXT, text)
            }
            context.startActivity(Intent.createChooser(intent, "Share Quranic Reflection"))
        }
    }

    fun getLatestReflection(): Flow<DailyReflectionEntity?> {
        scope.launch {
            val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val existing = reflectionDao.getReflectionForDate(todayStr)
            if (existing == null) {
                fetchDailyReflection(forceNew = false)
            }
        }
        return reflectionDao.getLatestReflection()
    }

    suspend fun fetchDailyReflection(forceNew: Boolean = false): DailyReflectionEntity = withContext(Dispatchers.IO) {
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        
        // Pick reflection based on day of year for deterministic daily rotation, or random if user forces shuffle
        val calendar = Calendar.getInstance()
        val dayOfYear = calendar.get(Calendar.DAY_OF_YEAR)
        val selectedIndex = if (forceNew) {
            Random.nextInt(CURATED_REFLECTIONS.size)
        } else {
            dayOfYear % CURATED_REFLECTIONS.size
        }
        val curated = CURATED_REFLECTIONS[selectedIndex]

        val reflectionEntity = DailyReflectionEntity(
            id = if (forceNew) "reflection_${System.currentTimeMillis()}" else "today_$todayStr",
            surahNumber = curated.surahNumber,
            surahName = curated.surahName,
            ayahNumber = curated.ayahNumber,
            arabicText = curated.arabicText,
            translation = curated.translation,
            transliteration = "",
            conciseExplanation = curated.conciseExplanation,
            reflectionMeaning = curated.reflectionMeaning,
            dailyLifeApplication = curated.dailyLifeApplication,
            theme = curated.theme,
            dateStr = todayStr,
            isCachedOffline = true,
            timestamp = System.currentTimeMillis()
        )

        reflectionDao.insertReflection(reflectionEntity)

        ayahDao.insertAyahs(
            listOf(
                AyahEntity(
                    surahNumber = curated.surahNumber,
                    ayahNumber = curated.ayahNumber,
                    arabicText = curated.arabicText,
                    translation = curated.translation
                )
            )
        )

        Log.d(TAG, "Cached daily reflection for $todayStr: ${curated.surahName} ${curated.ayahNumber}")
        reflectionEntity
    }
}
