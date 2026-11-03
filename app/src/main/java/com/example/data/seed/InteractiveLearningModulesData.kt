package com.example.data.seed

data class QuizQuestion(
    val id: String,
    val questionText: String,
    val options: List<String>,
    val correctOptionIndex: Int,
    val explanation: String
)

data class LearningLesson(
    val id: String,
    val lessonNumber: Int,
    val title: String,
    val summary: String,
    val estimatedMinutes: Int = 4,
    val contentMarkdown: String,
    val practicalExample: String,
    val keyAyahOrHadithQuote: String,
    val quizQuestions: List<QuizQuestion>
)

data class LearningTrack(
    val id: String,
    val title: String,
    val arabicTitle: String,
    val categoryIcon: String,
    val description: String,
    val totalLessonsCount: Int,
    val lessons: List<LearningLesson>
)

object InteractiveLearningModulesData {
    val ALL_TRACKS: List<LearningTrack> = listOf(
        // Track 1: Holy Quran
        LearningTrack(
            id = "track_quran",
            title = "Sciences & Reflections of the Quran",
            arabicTitle = "علوم القرآن والتدبر",
            categoryIcon = "📖",
            description = "Master the divine origins of revelation, Makki vs Madani paradigms, Tajweed foundations, and Tadabbur methodology.",
            totalLessonsCount = 2,
            lessons = listOf(
                LearningLesson(
                    id = "quran_lesson_1",
                    lessonNumber = 1,
                    title = "The Revelation and Distinction of Makki vs Madani Surahs",
                    summary = "Understand how divine revelation descended over 23 years and the profound thematic differences between Makkan and Madinan revelations.",
                    contentMarkdown = """
The Holy Quran was not revealed in a single instant; rather, it descended progressively over 23 years in response to living human realities, gradual legislation, and the spiritual development of the Companions.

Scholars categorize Surahs into two principal epochs:
1. Makki Surahs (revealed before the Hijrah to Madinah):
   - Primarily focused on Tawhid (Monotheism), the Day of Judgment, Paradise, Hellfire, and purifying the human creed from idolatry.
   - Characterized by majestic, poetic rhythm, powerful oaths by creation, and stories of ancient nations who rejected truth.
   - Example: Surah Al-Ikhlas, Surah Al-Qari'ah, Surah Qaf.

2. Madani Surahs (revealed after the Hijrah to Madinah):
   - Addressed to the nascent Islamic community and state: legal ordinances (Ahkam), social contracts, inheritance, prayer guidelines, fasting laws, and inter-communal treaties.
   - Addressed with 'O you who have believed!' (Ya ayyuhan-ladheena amanoo).
   - Example: Surah Al-Baqarah, Surah An-Nisa, Surah Al-Ma'idah.
                    """.trimIndent(),
                    practicalExample = "When reading Surahs with short, punchy verses (Juz 30), recognize they were spoken to awaken slumbering hearts in Makkah to eternal reality. When reading detailed laws in Surah Al-Baqarah, recognize they built the civilized order of Madinah.",
                    keyAyahOrHadithQuote = "Surah Al-Isra (17:106): 'And [it is] a Quran which We have separated [by intervals] that you might recite it to the people over a prolonged period and We have revealed it progressively.'",
                    quizQuestions = listOf(
                        QuizQuestion(
                            id = "q1_1",
                            questionText = "What was the primary thematic focus of Makki Surahs?",
                            options = listOf(
                                "Inheritance and commercial business laws",
                                "Tawhid (Monotheism), the Afterlife, and purifying the heart from idolatry",
                                "Rules of military engagement and peace treaties",
                                "Civil municipal administration of Madinah"
                            ),
                            correctOptionIndex = 1,
                            explanation = "Makki Surahs established the bedrock of faith: pure monotheism, belief in the Day of Judgment, and spiritual steadfastness before detailed legal obligations were legislated."
                        ),
                        QuizQuestion(
                            id = "q1_2",
                            questionText = "Over approximately how many years was the Quran revealed to Prophet Muhammad ﷺ?",
                            options = listOf("10 years", "40 years", "23 years", "12 years"),
                            correctOptionIndex = 2,
                            explanation = "The Quran was revealed over 23 years: 13 years in Makkah and 10 years in Madinah."
                        ),
                        QuizQuestion(
                            id = "q1_3",
                            questionText = "Which address is characteristic of Madani verses?",
                            options = listOf(
                                "'O mankind!' (Ya ayyuhan-nas)",
                                "'O you who have believed!' (Ya ayyuhan-ladheena amanoo)",
                                "'O children of Adam!'",
                                "'O tribe of Quraysh!'"
                            ),
                            correctOptionIndex = 1,
                            explanation = "Abdullah ibn Mas'ud noted: 'When you hear Allah say: O you who have believed, listen attentively, for it is either good being commanded or evil being forbidden.'"
                        )
                    )
                ),
                LearningLesson(
                    id = "quran_lesson_2",
                    lessonNumber = 2,
                    title = "The Art of Tadabbur (Deep Quranic Contemplation)",
                    summary = "Learn how to read the Quran with presence of heart, moving beyond mere tongue recitation to life transformation.",
                    contentMarkdown = """
Allah states: 'Do they not then reflect upon the Quran, or are there locks upon their hearts?' (Surah Muhammad 47:24).

The classical scholars outlined four keys to Tadabbur:
1. Recite with measured pacing (Tartil), not rushing to finish the page.
2. Personalize the address: When a verse speaks of believers, aspire to be among them; when it warns of hypocrites, examine your own heart.
3. Pause at verses of mercy to make Du'a, and pause at verses of punishment to seek Allah's refuge, just as the Prophet ﷺ did in Tahajjud.
4. Extract one actionable deed per reading session.
                    """.trimIndent(),
                    practicalExample = "Instead of rushing to complete 20 pages with zero retention, read one page slowly with translation and ask yourself: 'How does this verse apply to my conversation with my parents or my business dealings tomorrow?'",
                    keyAyahOrHadithQuote = "Surah Sad (38:29): '[This is] a blessed Book which We have revealed to you, that they may reflect upon its verses and that those of understanding would be reminded.'",
                    quizQuestions = listOf(
                        QuizQuestion(
                            id = "q2_1",
                            questionText = "What was the Prophet's practice when reciting verses describing Allah's mercy in night prayer?",
                            options = listOf(
                                "He would skip immediately to the next chapter",
                                "He would pause and supplicate to Allah asking for His mercy",
                                "He would remain silent until dawn",
                                "He would stop praying completely"
                            ),
                            correctOptionIndex = 1,
                            explanation = "Hudhayfah (RA) reported that whenever the Prophet ﷺ passed an Ayah of mercy, he paused and asked Allah; and when he passed an Ayah of warning, he sought refuge with Allah."
                        ),
                        QuizQuestion(
                            id = "q2_2",
                            questionText = "What is the primary spiritual objective of Tadabbur?",
                            options = listOf(
                                "To impress others with quick memorization",
                                "Deep contemplation leading to genuine internal and practical life change",
                                "To speed read as many Juz as possible without understanding",
                                "To argue about theological technicalities"
                            ),
                            correctOptionIndex = 1,
                            explanation = "Tadabbur transforms the Quran into a living roadmap for daily decision-making and character refinement."
                        ),
                        QuizQuestion(
                            id = "q2_3",
                            questionText = "Which companion said: 'We used to learn ten verses at a time, and not move forward until we mastered their knowledge and their practical action'?",
                            options = listOf("Abu Bakr", "Abdullah ibn Mas'ud", "Khalid ibn al-Walid", "Mu'awiyah"),
                            correctOptionIndex = 1,
                            explanation = "Ibn Mas'ud described the holistic methodology of the Sahaba: combining memorization, comprehension, and practical execution together."
                        )
                    )
                )
            )
        ),

        // Track 2: Prophetic Hadith
        LearningTrack(
            id = "track_hadith",
            title = "Foundations of Prophetic Hadith & Sunnah",
            arabicTitle = "أصول الحديث والسنة",
            categoryIcon = "📜",
            description = "Explore the rigorous science of Sanad authentication, the grading criteria of Sahih/Hasan/Da'if, and core 40 Nawawi wisdom.",
            totalLessonsCount = 2,
            lessons = listOf(
                LearningLesson(
                    id = "hadith_lesson_1",
                    lessonNumber = 1,
                    title = "Hadith Anatomy: Sanad (Chain) and Matn (Text)",
                    summary = "Discover how Muslims pioneered the world's most rigorous system of biographical historical verification.",
                    contentMarkdown = """
Every authentic hadith is comprised of two indivisible components:
1. The Sanad (Isnad / Chain of Transmission):
   - The verified genealogical pedigree of narrators: 'Imam Malik narrated to us from Nafi' from Abdullah ibn Umar from the Messenger of Allah ﷺ.'
   - Scholars evaluated every single individual's character ('Adalah - moral integrity) and precision (Dabt - memory accuracy without error).

2. The Matn (Substantive Text):
   - The actual words, actions, silent approvals (Taqrir), or physical descriptions of the Prophet ﷺ.
   - Evaluated to ensure it does not contradict the clear Quran or established Mutawatir Sunnah.

Abdullah ibn al-Mubarak said: 'The Isnad is from the religion. Were it not for the Isnad, anyone would say whatever they wanted.'
                    """.trimIndent(),
                    practicalExample = "When you see a social media quote attributed to the Prophet ﷺ, never share it blindly. Verify if it possesses an authentic Sanad recorded in canonical books like Bukhari, Muslim, or Tirmidhi.",
                    keyAyahOrHadithQuote = "The Prophet ﷺ warned: 'Whoever intentionally lies about me, let him take his seat in the Fire.' (Sahih al-Bukhari #107)",
                    quizQuestions = listOf(
                        QuizQuestion(
                            id = "h1_1",
                            questionText = "What are the two essential components of a Hadith?",
                            options = listOf(
                                "Title and Subtitle",
                                "Sanad (Chain of Narrators) and Matn (Substantive Text)",
                                "Author and Publisher",
                                "Volume and Page Number"
                            ),
                            correctOptionIndex = 1,
                            explanation = "The Sanad proves the transmission line, and the Matn is the actual speech or deed of the Prophet ﷺ."
                        ),
                        QuizQuestion(
                            id = "h1_2",
                            questionText = "What two primary qualifications did scholars examine for each narrator in a chain?",
                            options = listOf(
                                "Wealth and noble lineage",
                                "Moral Integrity ('Adalah) and Precision/Memory (Dabt)",
                                "Political influence and eloquence",
                                "Physical strength and age"
                            ),
                            correctOptionIndex = 1,
                            explanation = "A narrator had to be known for truthfulness and religious uprightness ('Adalah) and possess flawless memory or written records (Dabt)."
                        ),
                        QuizQuestion(
                            id = "h1_3",
                            questionText = "Which scholar famously stated: 'The Isnad is part of religion'?",
                            options = listOf(
                                "Abdullah ibn al-Mubarak",
                                "Harun al-Rashid",
                                "Ibn Battuta",
                                "Al-Khwarizmi"
                            ),
                            correctOptionIndex = 0,
                            explanation = "Ibn al-Mubarak highlighted that the chain of transmission safeguards the religion from fabricated opinions."
                        )
                    )
                ),
                LearningLesson(
                    id = "hadith_lesson_2",
                    lessonNumber = 2,
                    title = "Understanding Hadith Authenticity Grades",
                    summary = "Learn what Sahih, Hasan, and Da'if actually mean in Islamic jurisprudence.",
                    contentMarkdown = """
Scholars classify Hadiths according to rigorous scholarly grades:
1. Sahih (Authentic / Sound):
   - Five conditions: Continuous unbroken chain, upright narrators, precise memory, no hidden defect ('Illah), and no irregularity (Shudhudh).
   - Binding in matters of creed, law, and morals.

2. Hasan (Good / Fair):
   - Meets all conditions of Sahih, except that a narrator's precision is slightly less sharp, yet free from false speech.
   - Fully acceptable for legal obligations and prohibitions.

3. Da'if (Weak):
   - Lacks one or more required conditions (e.g. broken chain, narrator with poor memory).
   - Cannot be used as standalone proof for obligatory laws or prohibitions.
                    """.trimIndent(),
                    practicalExample = "Knowing Hadith grades prevents you from basing major religious life decisions on fabricated or severely weak narrations circulating on social messaging apps.",
                    keyAyahOrHadithQuote = "Imam Muslim in his Introduction: 'Refrain from narrating except that which is verified from trustworthy individuals.'",
                    quizQuestions = listOf(
                        QuizQuestion(
                            id = "h2_1",
                            questionText = "What distinguishes a Hasan hadith from a Sahih hadith?",
                            options = listOf(
                                "A Hasan hadith is fabricated",
                                "A Hasan hadith meets all conditions except one narrator's memory precision is slightly lower",
                                "A Hasan hadith has no chain of transmission",
                                "A Hasan hadith is only for non-Muslims"
                            ),
                            correctOptionIndex = 1,
                            explanation = "Hasan hadiths are authentic and acceptable in law, but their narrators have slightly lesser precision than the titans of Sahih chains."
                        ),
                        QuizQuestion(
                            id = "h2_2",
                            questionText = "Can a weak (Da'if) hadith be used as the sole basis to establish an obligatory religious pillar?",
                            options = listOf(
                                "Yes, unconditionally",
                                "No, binding obligations require authentic (Sahih/Hasan) evidence",
                                "Only if it agrees with personal preference",
                                "Yes, on Fridays only"
                            ),
                            correctOptionIndex = 1,
                            explanation = "Scholars of Usul al-Fiqh agree that binding legal duties (Wajib) and prohibitions (Haram) require verified authentic proofs."
                        ),
                        QuizQuestion(
                            id = "h2_3",
                            questionText = "Which two compilations are unanimously recognized as the most authentic books after the Quran?",
                            options = listOf(
                                "Arabian Nights and Kalilah wa Dimnah",
                                "Sahih al-Bukhari and Sahih Muslim",
                                "Kitab al-Aghani and Tarikh al-Tabari",
                                "Diwan al-Mutanabbi"
                            ),
                            correctOptionIndex = 1,
                            explanation = "The Sahihayn (Bukhari and Muslim) represent the gold standard of Hadith authentication in Islamic history."
                        )
                    )
                )
            )
        ),

        // Track 3: Daily Duas & Supplications
        LearningTrack(
            id = "track_duas",
            title = "The Fortress of the Believer (Duas & Dhikr)",
            arabicTitle = "حصن المسلم والأذكار",
            categoryIcon = "🤲",
            description = "Discover the manners of answered supplication, prime moments of divine response, and morning/evening protection.",
            totalLessonsCount = 1,
            lessons = listOf(
                LearningLesson(
                    id = "duas_lesson_1",
                    lessonNumber = 1,
                    title = "The Etiquette and Times of Guaranteed Acceptance",
                    summary = "Unlock the spiritual keys to having your heartfelt supplications accepted by Allah Almighty.",
                    contentMarkdown = """
The Prophet ﷺ said: 'Du'a is worship itself' (Tirmidhi). When asking the King of Kings, adhere to the prophetic protocol:

1. Adab (Etiquette) of Du'a:
   - Begin by praising and glorifying Allah (Alhamdulillah, SubhanAllah).
   - Send peace and blessings (Salawat) upon Prophet Muhammad ﷺ.
   - Supplicate with certainty of response (Yaqeen), not testing Allah.
   - Face the Qibla and raise both hands open toward the chest.
   - Ensure pure Halal sustenance; eating Haram food blocks Du'a.

2. Golden Times of Acceptance:
   - The Last Third of the Night (Tahajjud & Suhur).
   - In Sujood: 'The closest a servant is to his Lord is while in prostration, so supplicate abundantly.'
   - Between the Adhan and the Iqamah.
   - The final hour before Maghrib on Friday.
   - When rain descends from the heavens.
   - At the moment of breaking the fast (Iftar).
                    """.trimIndent(),
                    practicalExample = "Instead of immediately asking for your wish, begin: 'O Allah, Lord of Majesty and Honor, all praise belongs to You. O Allah, send blessings upon Muhammad... Then pour out your heart with certainty.'",
                    keyAyahOrHadithQuote = "Surah Ghafir (40:60): 'And your Lord says: Call upon Me; I will respond to you.'",
                    quizQuestions = listOf(
                        QuizQuestion(
                            id = "d1_1",
                            questionText = "At what physical posture is the believer closest to Allah?",
                            options = listOf(
                                "While standing in Qiyam",
                                "While in Sujood (prostration)",
                                "While sitting between the two prostrations",
                                "While walking to the market"
                            ),
                            correctOptionIndex = 1,
                            explanation = "The Prophet ﷺ said: 'The closest a slave comes to his Lord is when he is in prostration, so make abundant supplication.' (Sahih Muslim)"
                        ),
                        QuizQuestion(
                            id = "d1_2",
                            questionText = "How should a believer begin their intimate supplication?",
                            options = listOf(
                                "Immediately listing personal demands",
                                "Praising and glorifying Allah, then sending blessings upon the Prophet ﷺ",
                                "Complaining loudly to people",
                                "Demanding an immediate answer"
                            ),
                            correctOptionIndex = 1,
                            explanation = "The Prophet ﷺ heard a man supplicate praising Allah and sending Salawat and praised him: 'Supplicate, and you will be answered.'"
                        ),
                        QuizQuestion(
                            id = "d1_3",
                            questionText = "Which factor can spiritually prevent a supplication from being accepted?",
                            options = listOf(
                                "Consuming Haram food and unlawful earnings",
                                "Praying in the morning",
                                "Raising both hands",
                                "Facing the Qibla"
                            ),
                            correctOptionIndex = 0,
                            explanation = "The Prophet ﷺ mentioned a traveler with disheveled hair crying 'O Lord, O Lord!', yet his food was Haram, his drink was Haram, his clothing was Haram: 'How then could he be answered?' (Sahih Muslim)"
                        )
                    )
                )
            )
        ),

        // Track 4: Fiqh of Namaz
        LearningTrack(
            id = "track_namaz",
            title = "Fiqh and Spiritual Perfection of Salah",
            arabicTitle = "فقه وروح الصلاة",
            categoryIcon = "🕌",
            description = "Learn the essential conditions (Shuroot), pillars (Arkan), common errors, and Sujud as-Sahw for a valid and luminous prayer.",
            totalLessonsCount = 1,
            lessons = listOf(
                LearningLesson(
                    id = "namaz_lesson_1",
                    lessonNumber = 1,
                    title = "Pillars vs. Obligations of Salah & Forgetfulness Prostration (Sujud as-Sahw)",
                    summary = "Distinguish between unskippable pillars and compensable obligations, and master Sujud as-Sahw.",
                    contentMarkdown = """
In Islamic Jurisprudence, prayer acts are divided into:
1. Pillars (Arkan) - Cannot be omitted intentionally or accidentally; if missed, the Rak'ah is invalid:
   - Takbeerat al-Ihram, Standing (Qiyam), Reciting Al-Fatihah, Ruku', Standing from Ruku', Two Sujoods, Sitting between Sujoods, Final Tashahhud, and the Tasleem.
   - If you forgot Ruku' and remember before the next Rak'ah, you must return and perform Ruku'.

2. Obligations (Wajibah) - If omitted accidentally, compensated by two prostrations of forgetfulness (Sujud as-Sahw):
   - Takbeers of transition (Allahu Akbar between postures), Saying 'Subhana Rabbiyal 'Azeem' in Ruku', Saying 'Sami'Allahu liman Hamidah', The first Tashahhud.

3. When to perform Sujud as-Sahw:
   - Before Salam (Qabla as-Salam): If you omitted an obligation (like first Tashahhud) or had doubt and based on lesser certainty.
   - After Salam (Ba'da as-Salam): If you added an extra Rak'ah or completed after certainty.
                    """.trimIndent(),
                    practicalExample = "If you stand up for the 3rd Rak'ah in Dhuhr forgetting the middle Tashahhud, do not drop back down once fully standing. Continue your prayer and perform two prostrations before the final Salam.",
                    keyAyahOrHadithQuote = "The Prophet ﷺ said: 'Pray as you have seen me praying.' (Sahih al-Bukhari #631)",
                    quizQuestions = listOf(
                        QuizQuestion(
                            id = "n1_1",
                            questionText = "If a person accidentally forgets the first middle Tashahhud and stands up completely, what should they do?",
                            options = listOf(
                                "Restart the entire prayer from the beginning",
                                "Drop down immediately even if already reciting",
                                "Continue the prayer and perform Sujud as-Sahw before Salam",
                                "Ignore it with no compensation required"
                            ),
                            correctOptionIndex = 2,
                            explanation = "The Prophet ﷺ forgot the first Tashahhud, continued to the end of prayer, and made two prostrations before making Salam."
                        ),
                        QuizQuestion(
                            id = "n1_2",
                            questionText = "Is reciting Surah Al-Fatihah in every Rak'ah a pillar (Rukn) or an optional Sunnah?",
                            options = listOf(
                                "A Pillar (Rukn) without which the Rak'ah is invalid",
                                "An optional recommended act",
                                "Only required in the first Rak'ah",
                                "Only required in Jumu'ah"
                            ),
                            correctOptionIndex = 0,
                            explanation = "The Prophet ﷺ said: 'There is no prayer for the one who does not recite the Opening of the Book (Al-Fatihah).' (Bukhari & Muslim)"
                        ),
                        QuizQuestion(
                            id = "n1_3",
                            questionText = "What is the minimum requirement for Tranquility (Tuma'neenah) in each posture?",
                            options = listOf(
                                "Staying in Ruku' for 15 minutes",
                                "Pausing until every bone settles into its resting place before moving",
                                "Moving as quickly as possible like a pecking crow",
                                "Reciting 10 surahs in prostration"
                            ),
                            correctOptionIndex = 1,
                            explanation = "Tuma'neenah is a pillar of Salah; the Prophet ﷺ told the man who prayed hastily: 'Go back and pray, for you have not prayed.'"
                        )
                    )
                )
            )
        ),

        // Track 5: Fasting (Sawm)
        LearningTrack(
            id = "track_fasting",
            title = "Fiqh & Inner Dimensions of Fasting",
            arabicTitle = "فقه وأسرار الصيام",
            categoryIcon = "🌙",
            description = "Master the conditions, valid exemptions, common nullifiers, and how to guard your fast from losing its divine reward.",
            totalLessonsCount = 1,
            lessons = listOf(
                LearningLesson(
                    id = "fasting_lesson_1",
                    lessonNumber = 1,
                    title = "The Real Nullifiers vs. Myths of Fasting",
                    summary = "Clear up common confusions regarding what breaks the fast and what is permissible.",
                    contentMarkdown = """
Fasting (Sawm) in Shari'ah is refraining with intention (Niyyah) from eating, drinking, and marital relations from true dawn (Fajr) until sunset (Maghrib).

1. Actual Nullifiers (Break the Fast):
   - Eating, drinking, or smoking intentionally.
   - Deliberately inducing vomiting.
   - Menstruation or post-natal bleeding (fast is broken; days made up later).
   - Nutritive intravenous drips that substitute for food/water.

2. Things that DO NOT Break the Fast:
   - Forgetting and eating or drinking by accident: 'Allah has fed him and given him drink' (Sahih Bukhari). Continue your fast immediately.
   - Using the Miswak or gentle toothbrushing without swallowing paste.
   - Non-nutritive injections (insulin, medical shots, vaccines).
   - Blood tests, ear drops, or eye drops.
   - Swallowing one's own natural saliva.
   - Taking a cool shower or swimming without swallowing water.
                    """.trimIndent(),
                    practicalExample = "If you absentmindedly drink a glass of water at 2 PM, do NOT think your fast is ruined and start eating. Your fast is 100% valid by prophetic promise. Simply rinse your mouth and continue fasting with joy.",
                    keyAyahOrHadithQuote = "The Prophet ﷺ said: 'Whoever does not give up false speech and evil action, Allah has no need for him to leave his food and drink.' (Sahih al-Bukhari #1903)",
                    quizQuestions = listOf(
                        QuizQuestion(
                            id = "f1_1",
                            questionText = "What happens if a fasting person forgets and eats a snack during Ramadan?",
                            options = listOf(
                                "Their fast is void and they must fast 60 consecutive days",
                                "Their fast remains completely valid; they stop immediately and continue fasting",
                                "They must pay monetary expiation (Fidyah)",
                                "They must break their fast for the rest of the day"
                            ),
                            correctOptionIndex = 1,
                            explanation = "The Prophet ﷺ explicitly said: 'If anyone forgets and eats or drinks, let him complete his fast, for it was Allah Who fed him and gave him drink.'"
                        ),
                        QuizQuestion(
                            id = "f1_2",
                            questionText = "Does an insulin injection or blood draw for medical lab testing invalidate the fast?",
                            options = listOf(
                                "Yes, any needle breaks the fast",
                                "No, non-nutritive medical injections and blood tests do not break the fast",
                                "Only if taken after Asr",
                                "Only for women"
                            ),
                            correctOptionIndex = 1,
                            explanation = "Modern scholarly councils (such as the International Islamic Fiqh Academy) rule that non-nutritive medical injections do not nullify the fast."
                        ),
                        QuizQuestion(
                            id = "f1_3",
                            questionText = "What is the higher spiritual objective of fasting as stated in Surah Al-Baqarah (2:183)?",
                            options = listOf(
                                "Physical weight loss",
                                "Attaining Taqwa (God-consciousness and self-restraint)",
                                "Punishing the body",
                                "Saving grocery money"
                            ),
                            correctOptionIndex = 1,
                            explanation = "Allah says: 'O you who have believed, decreed upon you is fasting... that you may become righteous (Tattaqun).'"
                        )
                    )
                )
            )
        )
    )
}
