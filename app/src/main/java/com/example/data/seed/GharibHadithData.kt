package com.example.data.seed

data class GharibHadithReference(
    val collection: String,                 // e.g. "Sahih al-Bukhari", "Sahih Muslim"
    val collectionId: String,               // "bukhari", "muslim", "abudawud", "tirmidhi"
    val hadithNumber: Int,                  // Reference hadith number
    val arabicSnippet: String,              // Contextual sentence in Arabic
    val englishContext: String              // Contextual translation and explanation
)

data class GharibGlossaryEntry(
    val id: String,
    val termArabic: String,
    val termTransliteration: String,
    val rootArabic: String,                 // Arabic triliteral/quadriliteral root
    val category: String,                   // "Classical Vocabulary", "Metaphor & Idiom", "Commerce & Fiqh", "Mustalah (Sciences)"
    val briefMeaning: String,               // Concise definition
    val detailedExplanation: String,        // Deep linguistic and contextual explanation from classical lexicographers
    val classicalSource: String,            // e.g. "An-Nihayah fi Gharib al-Hadith (Ibn al-Athir)"
    val references: List<GharibHadithReference>
)

object GharibHadithData {

    val ENTRIES: List<GharibGlossaryEntry> = listOf(
        GharibGlossaryEntry(
            id = "mufarridoon",
            termArabic = "الْمُفَرِّدُونَ",
            termTransliteration = "Al-Mufarridoon",
            rootArabic = "ف - ر - د",
            category = "Classical Vocabulary",
            briefMeaning = "The solitary devotees who constantly remember Allah and outstrip others",
            detailedExplanation = "Classically derived from 'tafarada' (to isolate oneself for devotion). Ibn al-Athir explains in An-Nihayah that they are believers whose hearts are detached from worldly competition, engrossed in the constant Dhikr of Allah so that the burdens of sin are lifted off them.",
            classicalSource = "An-Nihayah fi Gharib al-Hadith (Ibn al-Athir, Vol 3, p. 434)",
            references = listOf(
                GharibHadithReference(
                    collection = "Sahih Muslim",
                    collectionId = "muslim",
                    hadithNumber = 2676,
                    arabicSnippet = "سَبَقَ الْمُفَرِّدُونَ. قَالُوا: وَمَا الْمُفَرِّدُونَ يَا رَسُولَ اللَّهِ؟ قَالَ: الذَّاكِرُونَ اللَّهَ كَثِيرًا وَالذَّاكِرَاتُ",
                    englishContext = "The Prophet (pbuh) said: 'The Mufarridoon have surpassed all.' They asked: 'Who are the Mufarridoon, O Messenger of Allah?' He replied: 'Those men and women who remember Allah abundantly.'"
                )
            )
        ),
        GharibGlossaryEntry(
            id = "ghurr_muhajjaloon",
            termArabic = "الْغُرُّ الْمُحَجَّلُونَ",
            termTransliteration = "Al-Ghurr al-Muhajjaloon",
            rootArabic = "غ - ر - ر / ح - ج - ل",
            category = "Metaphor & Idiom",
            briefMeaning = "Those marked by radiant white blazes on their foreheads and luminous ankles from Wudu",
            detailedExplanation = "'Ghurrah' is originally the bright white star-like patch on the forehead of a noble steed. 'Tahjeel' is the white coloring wrapping around the horse's lower legs. The Prophet (pbuh) used this vivid equestrian idiom to illustrate how his Ummah will be recognized on the Day of Resurrection by radiance emanating from the limbs washed during ablution.",
            classicalSource = "Lisan al-Arab (Ibn Manzur) & An-Nihayah (Ibn al-Athir)",
            references = listOf(
                GharibHadithReference(
                    collection = "Sahih al-Bukhari",
                    collectionId = "bukhari",
                    hadithNumber = 136,
                    arabicSnippet = "إِنَّ أُمَّتِي يُدْعَوْنَ يَوْمَ الْقِيَامَةِ غُرًّا مُحَجَّلِينَ مِنْ آثَارِ الْوُضُوءِ",
                    englishContext = "My nation will be summoned on the Day of Resurrection with radiant white foreheads and white limbs from the luminous traces of Wudu."
                )
            )
        ),
        GharibGlossaryEntry(
            id = "musarrah",
            termArabic = "الْمُصَرَّاة",
            termTransliteration = "Al-Musarrah",
            rootArabic = "ص - ر - ي",
            category = "Commerce & Fiqh",
            briefMeaning = "An animal whose udder is deliberately tied up to deceive buyers about milk volume",
            detailedExplanation = "Derived from 'tasriyah' (gathering and holding back water or milk). In pre-Islamic commerce, deceitful vendors tied up the teats of camels or sheep for several days before market day so the udder appeared overflowing. The Prophet strictly banned this fraud and legislated compensation of a Sa' of dates if returned.",
            classicalSource = "Gharib al-Hadith (Abu Ubayd al-Qasim ibn Sallam)",
            references = listOf(
                GharibHadithReference(
                    collection = "Sahih al-Bukhari",
                    collectionId = "bukhari",
                    hadithNumber = 2148,
                    arabicSnippet = "لاَ تُصَرُّوا الإِبِلَ وَالْغَنَمَ، فَمَنِ ابْتَاعَهَا بَعْدُ فَإِنَّهُ بِخَيْرِ النَّظَرَيْنِ",
                    englishContext = "Do not tie up the udders of camels and sheep (to falsely inflate milk). Whoever buys such an animal has the choice after milking it to keep it or return it with a Sa' of dates."
                )
            )
        ),
        GharibGlossaryEntry(
            id = "hima",
            termArabic = "الْحِمَى",
            termTransliteration = "Al-Hima",
            rootArabic = "ح - م - ي",
            category = "Metaphor & Idiom",
            briefMeaning = "A demarcated, strictly guarded royal sanctuary or private pasture",
            detailedExplanation = "In ancient Arabia, kings and chieftains declared expansive fertile meadows as 'Hima', where unauthorized grazing was punishable by death. In the renowned Hadith of Nu'man ibn Bashir on Halal and Haram, the Prophet utilized this spatial boundary metaphor: Allah's Hima on earth consists of His sacred prohibitions.",
            classicalSource = "An-Nihayah fi Gharib al-Hadith (Ibn al-Athir, Vol 1, p. 444)",
            references = listOf(
                GharibHadithReference(
                    collection = "Sahih al-Bukhari",
                    collectionId = "bukhari",
                    hadithNumber = 52,
                    arabicSnippet = "أَلاَ وَإِنَّ لِكُلِّ مَلِكٍ حِمًى، أَلاَ وَإِنَّ حِمَى اللَّهِ مَحَارِمُهُ",
                    englishContext = "Beware, every sovereign has a sanctuary (Hima), and truly the sanctuary of Allah on earth consists of what He has declared forbidden."
                )
            )
        ),
        GharibGlossaryEntry(
            id = "najash",
            termArabic = "النَّجَش",
            termTransliteration = "An-Najash",
            rootArabic = "ن - ج - ش",
            category = "Commerce & Fiqh",
            briefMeaning = "Artificially bidding up an auction price without intent to buy to entrap others",
            detailedExplanation = "Linguistically signifies provoking, exciting, or driving game toward a trap. In Hadith jurisprudence, it denotes shill bidding or collusive bidding where an accomplice falsely raises prices to swindle genuine purchasers. The contract is sinful and constitutes impermissible deception (Ghashsh).",
            classicalSource = "Ma'alim as-Sunan (Al-Khattabi)",
            references = listOf(
                GharibHadithReference(
                    collection = "Sahih al-Bukhari",
                    collectionId = "bukhari",
                    hadithNumber = 2160,
                    arabicSnippet = "نَهَى النَّبِيُّ صلى الله عليه وسلم عَنِ النَّجَشِ",
                    englishContext = "The Prophet (pbuh) strictly forbade An-Najash (artificial inflated bidding in markets)."
                )
            )
        ),
        GharibGlossaryEntry(
            id = "maghram",
            termArabic = "الْمَغْرَم",
            termTransliteration = "Al-Maghram",
            rootArabic = "غ - ر - م",
            category = "Classical Vocabulary",
            briefMeaning = "Overwhelming debt or financial burden incurred through difficulty",
            detailedExplanation = "Classically distinguished from ordinary lending; Maghram refers to financial obligations, debts, or penalties one is forced into that cause spiritual distress or compromise moral integrity. In his daily Tashahhud supplication, the Prophet frequently sought refuge from 'Al-Ma'tham wal-Maghram' (sin and debilitating debt).",
            classicalSource = "An-Nihayah fi Gharib al-Hadith (Ibn al-Athir)",
            references = listOf(
                GharibHadithReference(
                    collection = "Sahih al-Bukhari",
                    collectionId = "bukhari",
                    hadithNumber = 832,
                    arabicSnippet = "اللَّهُمَّ إِنِّي أَعُوذُ بِكَ مِنَ الْمَأْثَمِ وَالْمَغْرَمِ",
                    englishContext = "O Allah, I seek refuge in You from committing sin and from becoming submerged in debt (Maghram)."
                )
            )
        ),
        GharibGlossaryEntry(
            id = "inah",
            termArabic = "الْعِينَة",
            termTransliteration = "Al-'Inah",
            rootArabic = "ع - ي - ن",
            category = "Commerce & Fiqh",
            briefMeaning = "A disguised usurious buy-back transaction using goods as an artificial conduit",
            detailedExplanation = "Derived from ''Ayn' (the tangible physical commodity). A person sells a commodity on deferred payment at a higher price, then immediately repurchases the identical good for immediate lower cash from the same buyer, thus creating a veiled interest-bearing loan masquerading as a sale.",
            classicalSource = "Al-Majmu' (Imam An-Nawawi) & Sunan Abi Dawud commentary",
            references = listOf(
                GharibHadithReference(
                    collection = "Sunan Abi Dawud",
                    collectionId = "abudawud",
                    hadithNumber = 3462,
                    arabicSnippet = "إِذَا تَبَايَعْتُمْ بِالْعِينَةِ وَأَخَذْتُمْ أَذْنَابَ الْبَقَرِ... سَلَّطَ اللَّهُ عَلَيْكُمْ ذُلاًّ",
                    englishContext = "When you practice 'Inah (disguised usurious sales), cling to the tails of cattle, and abandon striving, Allah will impose humiliation upon you until you return to your faith."
                )
            )
        ),
        GharibGlossaryEntry(
            id = "saeed_tayyib",
            termArabic = "الصَّعِيد الطَّيِّب",
            termTransliteration = "As-Sa'eed at-Tayyib",
            rootArabic = "ص - ع - د",
            category = "Classical Vocabulary",
            briefMeaning = "Pure, elevated soil or natural surface dust of the earth used for Tayammum",
            detailedExplanation = "Classically signifies everything that rises and ascends upon the face of the earth, whether loose dust, clean sand, clay, or stone. The Quran and Hadith mandate its use when water is inaccessible or harmful to touch.",
            classicalSource = "Mu'jam Maqayis al-Lughah (Ibn Faris)",
            references = listOf(
                GharibHadithReference(
                    collection = "Jami` at-Tirmidhi",
                    collectionId = "tirmidhi",
                    hadithNumber = 124,
                    arabicSnippet = "إِنَّ الصَّعِيدَ الطَّيِّبَ طَهُورُ الْمُسْلِمِ وَإِنْ لَمْ يَجِدِ الْمَاءَ عَشْرَ سِنِينَ",
                    englishContext = "Pure clean earth (As-Sa'eed at-Tayyib) serves as purification for a Muslim, even if he does not find water for ten years."
                )
            )
        ),
        GharibGlossaryEntry(
            id = "kalalah",
            termArabic = "الْكَلَالَة",
            termTransliteration = "Al-Kalalah",
            rootArabic = "ك - ل - ل",
            category = "Commerce & Fiqh",
            briefMeaning = "An inheritance state where the deceased leaves behind neither direct ascendants nor descendants",
            detailedExplanation = "Linguistically related to 'Ikleel' (a garland or crown surrounding the head rather than topping it). In inheritance jurisprudence, Kalalah designates an estate where there is neither father, grandfather, son, nor daughter, leaving distant lateral kin (collaterals like brothers or sisters) as heirs.",
            classicalSource = "Gharib al-Hadith (Ibn Qutaybah)",
            references = listOf(
                GharibHadithReference(
                    collection = "Sahih Muslim",
                    collectionId = "muslim",
                    hadithNumber = 1616,
                    arabicSnippet = "مَا سَأَلْتُ النَّبِيَّ صلى الله عليه وسلم عَنْ شَيْءٍ أَكْثَرَ مَا سَأَلْتُهُ عَنِ الْكَلاَلَةِ",
                    englishContext = "Umar ibn al-Khattab (RA) said: 'I never questioned the Prophet about anything as frequently as I questioned him regarding Al-Kalalah.'"
                )
            )
        ),
        GharibGlossaryEntry(
            id = "mutawatir",
            termArabic = "مُتَوَاتِر",
            termTransliteration = "Mutawatir",
            rootArabic = "و - ت - ر",
            category = "Mustalah (Sciences)",
            briefMeaning = "Mass-transmitted tradition yielding definitive epistemological certainty",
            detailedExplanation = "Derived from 'tawatur' (successive, continuous procession like raindrops). A hadith narrated by a multitude in each era such that their conspiracy on falsehood is rational impossible. Yields 'Ilm Qat'i (definitive certainty).",
            classicalSource = "Muqaddimah Ibn al-Salah fi 'Ulum al-Hadith",
            references = listOf(
                GharibHadithReference(
                    collection = "Sahih al-Bukhari",
                    collectionId = "bukhari",
                    hadithNumber = 110,
                    arabicSnippet = "مَنْ كَذَبَ عَلَىَّ مُتَعَمِّدًا فَلْيَتَبَوَّأْ مَقْعَدَهُ مِنَ النَّارِ",
                    englishContext = "'Whoever intentionally attributes a falsehood to me, let him take his seat in the Fire.' Narrated by over seventy Companions, making it prime Mutawatir."
                )
            )
        ),
        GharibGlossaryEntry(
            id = "mudraj",
            termArabic = "مُدْرَج",
            termTransliteration = "Mudraj",
            rootArabic = "د - ر - ج",
            category = "Mustalah (Sciences)",
            briefMeaning = "An interpolated phrase from a narrator blended seamlessly into the Hadith text",
            detailedExplanation = "Classically from 'Idraj' (inserting one document inside another). Occurs when a Companion or Tabi'i pauses to explain a difficult word and subsequent transmitters unwittingly fuse their gloss into the Prophet's speech without attribution.",
            classicalSource = "Fath al-Mugheeth (As-Sakhawi)",
            references = listOf(
                GharibHadithReference(
                    collection = "Sahih al-Bukhari",
                    collectionId = "bukhari",
                    hadithNumber = 3,
                    arabicSnippet = "فَيَتَحَنَّثُ فِيهِ ـ وَهُوَ التَّعَبُّدُ ـ اللَّيَالِيَ ذَوَاتِ الْعَدَدِ",
                    englishContext = "The famous early Hadith of revelation contains Alzuhri's interpolated clarification: 'Wa huwa at-ta'abbud' (and tahannuth means devotional worship)."
                )
            )
        ),
        GharibGlossaryEntry(
            id = "muallal",
            termArabic = "مُعَلَّل",
            termTransliteration = "Mu'allal / Ma'lool",
            rootArabic = "ع - ل - ل",
            category = "Mustalah (Sciences)",
            briefMeaning = "A Hadith containing a subtle, concealed defect while appearing authentic on the surface",
            detailedExplanation = "An 'Illah is an obscure flaw—such as an interrupted link disguised as continuous, or an erroneous attribution of a Mawquf companion statement to the Prophet—that only master verifiers (like Bukhari, Ahmad, and Daraqutni) could pinpoint through exhaustive comparative chain analysis.",
            classicalSource = "'Ilal al-Hadith (Ibn Abi Hatim ar-Razi)",
            references = listOf(
                GharibHadithReference(
                    collection = "Sahih Muslim",
                    collectionId = "muslim",
                    hadithNumber = 1,
                    arabicSnippet = "كِتَابُ التَّمْيِيزِ فِي بَيَانِ عِلَلِ الْحَدِيثِ",
                    englishContext = "Examined meticulously in Imam Muslim's classical treatise Kitab at-Tamyiz on detecting hidden Hadith defects."
                )
            )
        ),
        GharibGlossaryEntry(
            id = "jadhwah",
            termArabic = "الْجَذْوَة",
            termTransliteration = "Al-Jadhwah",
            rootArabic = "ج - ذ - و",
            category = "Classical Vocabulary",
            briefMeaning = "A glowing, thick brand or ember of burning wood retaining deep internal heat",
            detailedExplanation = "Mentioned in Quranic accounts of Musa (AS) at Mount Tur ('او جذوة من النار') and recurring in Hadith traditions depicting trials and faith clinging. Symbolizes intense, deep-seated burning endurance.",
            classicalSource = "An-Nihayah fi Gharib al-Hadith (Ibn al-Athir)",
            references = listOf(
                GharibHadithReference(
                    collection = "Jami` at-Tirmidhi",
                    collectionId = "tirmidhi",
                    hadithNumber = 2260,
                    arabicSnippet = "يَأْتِي عَلَى النَّاسِ زَمَانٌ الصَّابِرُ فِيهِمْ عَلَى دِينِهِ كَالْقَابِضِ عَلَى الْجَمْرِ",
                    englishContext = "A time will come when holding steadfast to one's faith will be like grasping a hot burning ember."
                )
            )
        ),
        GharibGlossaryEntry(
            id = "shadh",
            termArabic = "شَاذّ",
            termTransliteration = "Shadh",
            rootArabic = "ش - ذ - ذ",
            category = "Mustalah (Sciences)",
            briefMeaning = "An anomalous narration where an upright narrator contradicts someone more reliable",
            detailedExplanation = "Shadh denotes isolation from consensus. In Hadith taxonomy, it is a narration reported by an acceptable transmitter which conflicts directly with transmissions of narrators who are more numerous, more precise, or higher in authority.",
            classicalSource = "Nukhbat al-Fikar (Ibn Hajar al-Asqalani)",
            references = listOf(
                GharibHadithReference(
                    collection = "Sunan an-Nasa'i",
                    collectionId = "nasai",
                    hadithNumber = 1264,
                    arabicSnippet = "بَيَانُ رِوَايَةِ الثِّقَةِ الْمُخَالِفَةِ لِمَنْ هُوَ أَوْثَقُ مِنْهُ",
                    englishContext = "Categorized when a single reliable narrator inserts an anomalous addition into Tashahhud not corroborated by the rest of the school."
                )
            )
        ),
        GharibGlossaryEntry(
            id = "gheelah",
            termArabic = "الْغِيلَة",
            termTransliteration = "Al-Gheelah",
            rootArabic = "غ - ي - ل",
            category = "Classical Vocabulary",
            briefMeaning = "Cohabitation with a mother while nursing, or lactation during pregnancy",
            detailedExplanation = "Pre-Islamic Arabs feared that cohabiting with a nursing woman would weaken or harm the infant's development. The Prophet (pbuh) investigated Persian and Roman customs and declared it permissible, noting that it caused no innate harm to children.",
            classicalSource = "An-Nihayah fi Gharib al-Hadith (Ibn al-Athir, Vol 3, p. 402)",
            references = listOf(
                GharibHadithReference(
                    collection = "Sahih Muslim",
                    collectionId = "muslim",
                    hadithNumber = 1442,
                    arabicSnippet = "لَقَدْ هَمَمْتُ أَنْ أَنْهَى عَنِ الْغِيلَةِ، حَتَّى ذَكَرْتُ أَنَّ الرُّومَ وَفَارِسَ يَصْنَعُونَ ذَلِكَ فَلاَ يَضُرُّ أَوْلاَدَهُمْ",
                    englishContext = "The Prophet said: 'I had intended to prohibit Al-Gheelah, until I recalled that the Romans and Persians practice it without any harm befalling their children.'"
                )
            )
        )
    )

    val CATEGORIES = listOf(
        "All Categories",
        "Classical Vocabulary",
        "Metaphor & Idiom",
        "Commerce & Fiqh",
        "Mustalah (Sciences)"
    )

    fun searchGlossary(query: String, category: String = "All Categories"): List<GharibGlossaryEntry> {
        val q = query.trim().lowercase()
        return ENTRIES.filter { entry ->
            val matchesCategory = (category == "All Categories" || entry.category.equals(category, ignoreCase = true))
            val matchesQuery = if (q.isBlank()) true else {
                entry.termArabic.contains(q) ||
                entry.termTransliteration.lowercase().contains(q) ||
                entry.rootArabic.replace(" ", "").contains(q.replace(" ", "")) ||
                entry.briefMeaning.lowercase().contains(q) ||
                entry.detailedExplanation.lowercase().contains(q) ||
                entry.classicalSource.lowercase().contains(q) ||
                entry.references.any { ref ->
                    ref.collection.lowercase().contains(q) ||
                    ref.arabicSnippet.contains(q) ||
                    ref.englishContext.lowercase().contains(q)
                }
            }
            matchesCategory && matchesQuery
        }
    }
}
