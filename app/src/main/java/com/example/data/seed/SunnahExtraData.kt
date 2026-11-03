package com.example.data.seed

data class HadithTerm(
    val termArabic: String,
    val termTransliteration: String,
    val meaningEnglish: String,
    val technicalDefinition: String,
    val exampleContext: String
)

data class ImamBiography(
    val id: String,
    val name: String,
    val arabicName: String,
    val lifespan: String,
    val birthplace: String,
    val majorWork: String,
    val methodology: String,
    val strictnessCriteria: String,
    val memorableQuote: String
)

data class NarratorDetail(
    val id: String,
    val nameEnglish: String,
    val nameArabic: String,
    val relationToProphet: String,
    val totalNarrationsCount: Int,
    val scholarlyGrading: String, // e.g., "Thiqah Thiqah (Highest Trustworthiness)"
    val briefBiography: String,
    val yearPassedAH: Int
)

object SunnahExtraData {
    val GLOSSARY_TERMS: List<HadithTerm> = listOf(
        HadithTerm(
            termArabic = "مُتَوَاتِر",
            termTransliteration = "Mutawatir",
            meaningEnglish = "Mass-transmitted Hadith",
            technicalDefinition = "A narration transmitted by so vast a number of narrators in every generation that it is inconceivable they could have agreed upon a lie. Yields definitive, certain knowledge (Yaqeen).",
            exampleContext = "The five daily prayer timings and Quranic recitation modes."
        ),
        HadithTerm(
            termArabic = "صَحِيح",
            termTransliteration = "Sahih",
            meaningEnglish = "Authentic / Sound",
            technicalDefinition = "A hadith with an unbroken chain of upright ('Adl) and precise (Dabit) narrators, free from hidden defects ('Illah) and irregularity (Shudhudh).",
            exampleContext = "Found in the Sahihayn of Bukhari and Muslim."
        ),
        HadithTerm(
            termArabic = "حَسَن",
            termTransliteration = "Hasan",
            meaningEnglish = "Good / Fair",
            technicalDefinition = "Meets all criteria of Sahih, except that one narrator's memory/precision is slightly inferior, yet acceptable for legal rulings.",
            exampleContext = "Commonly graded by Imam at-Tirmidhi in his Jami`."
        ),
        HadithTerm(
            termArabic = "ضَعِيف",
            termTransliteration = "Da'if",
            meaningEnglish = "Weak",
            technicalDefinition = "A narration that fails to fulfill one or more conditions of Hasan/Sahih, such as a gap in the chain (Inqita') or a narrator with impaired memory.",
            exampleContext = "Cannot be used as standalone evidence for binding legal obligations."
        ),
        HadithTerm(
            termArabic = "إِسْنَاد",
            termTransliteration = "Isnad / Sanad",
            meaningEnglish = "Chain of Transmission",
            technicalDefinition = "The sequential pedigree of transmitters connecting the compiler back to the Prophet (ﷺ) or companion.",
            exampleContext = "Abdullah ibn al-Mubarak said: 'The Isnad is part of religion; were it not for the Isnad, anyone would say whatever they wished.'"
        ),
        HadithTerm(
            termArabic = "مَتْن",
            termTransliteration = "Matn",
            meaningEnglish = "Actual Text of Hadith",
            technicalDefinition = "The substantive speech, action, tacit approval, or physical description of the Prophet (ﷺ) where the chain ends.",
            exampleContext = "'Actions are judged only by intentions...'"
        ),
        HadithTerm(
            termArabic = "غَرِيبُ الحَدِيث",
            termTransliteration = "Gharib al-Hadith",
            meaningEnglish = "Obscure / Rare Vocabulary",
            technicalDefinition = "Classical Arabic words occurring in prophetic speech that were uncommon in everyday parlance and require specialized lexical commentary.",
            exampleContext = "Analyzed by scholars like Ibn al-Athir in 'An-Nihayah fi Gharib al-Hadith'."
        )
    )

    val IMAM_BIOGRAPHIES: List<ImamBiography> = listOf(
        ImamBiography(
            id = "bukhari",
            name = "Imam Muhammad ibn Isma'il al-Bukhari",
            arabicName = "محمد بن إسماعيل البخاري",
            lifespan = "194 - 256 AH (810 - 870 CE)",
            birthplace = "Bukhara (Modern Uzbekistan)",
            majorWork = "Al-Jami' al-Sahih (Sahih al-Bukhari)",
            methodology = "Required that narrators in a chain not only be contemporaries (Mu'asarah), but have proven physical meeting (Liqa') and unbroken transmission.",
            strictnessCriteria = "Evaluated over 600,000 narrations over 16 years, performing Ghusl and two Rak'ahs of Istikharah prayer before recording every single hadith.",
            memorableQuote = "I did not include in this book any hadith except that I verified its authenticity, and what I omitted of authentic hadiths is far greater."
        ),
        ImamBiography(
            id = "muslim",
            name = "Imam Muslim ibn al-Hajjaj an-Naysaburi",
            arabicName = "مسلم بن الحجاج النيسابوري",
            lifespan = "206 - 261 AH (821 - 875 CE)",
            birthplace = "Nishapur (Khorasan)",
            majorWork = "Al-Musnad al-Sahih (Sahih Muslim)",
            methodology = "Exquisite thematic clustering: grouped all routes (Turuq) and slight text variations of a hadith together in a single place with unbroken chains.",
            strictnessCriteria = "Accepted narrators who were confirmed contemporaries without demanding explicit documented physical meetings, provided no disproof existed.",
            memorableQuote = "If the scholars of hadith were to write traditions for 200 years, their pillar would remain this Musnad."
        ),
        ImamBiography(
            id = "tirmidhi",
            name = "Imam Abu 'Isa Muhammad at-Tirmidhi",
            arabicName = "أبو عيسى محمد بن عيسى الترمذي",
            lifespan = "209 - 279 AH (824 - 892 CE)",
            birthplace = "Termez (Uzbekistan)",
            majorWork = "Jami` at-Tirmidhi & Ash-Shama'il al-Muhammadiyyah",
            methodology = "Pioneered standardized critical grading notes at the foot of each hadith ('Hadith Hasan Sahih Gharib') and cited legal opinions of the Jurists (Fuqaha).",
            strictnessCriteria = "Balanced rigorous authentication with practical Fiqh applications across the four Madhhabs.",
            memorableQuote = "Whoever has this book in his house, it is as though the Prophet ﷺ is residing in his home speaking to him."
        ),
        ImamBiography(
            id = "abudawud",
            name = "Imam Abu Dawud Sulayman ibn al-Ash'ath",
            arabicName = "أبو داود سليمان بن الأشعث السجستاني",
            lifespan = "202 - 275 AH (817 - 889 CE)",
            birthplace = "Sistan (Eastern Iran / Afghanistan)",
            majorWork = "Sunan Abi Dawud",
            methodology = "Focused primarily on Hadiths of Legal Rulings (Ahadith al-Ahkam) governing prayer, transactions, marriage, and criminal law.",
            strictnessCriteria = "Included authentic and acceptable hadiths; if a hadith had severe weakness, he explicitly noted it.",
            memorableQuote = "Out of 500,000 hadiths, four are sufficient for a person's religion: 'Actions are by intentions', 'Part of the perfection of Islam is leaving what does not concern you', 'None of you truly believes until he loves for his brother...', and 'The Halal is clear and the Haram is clear.'"
        )
    )

    val NARRATORS: List<NarratorDetail> = listOf(
        NarratorDetail(
            id = "abu_hurairah",
            nameEnglish = "Abu Hurairah (Abd ar-Rahman ibn Sakhr)",
            nameArabic = "أبو هريرة",
            relationToProphet = "Companion who stayed at the Suffah dedicating his entire life to learning",
            totalNarrationsCount = 5374,
            scholarlyGrading = "Sahabi Jalil - Thiqah Thabit (Peerless Memory blessed by the Prophet's du'a)",
            briefBiography = "Embraced Islam in 7 AH. The Prophet ﷺ spread his cloak and Abu Hurairah gathered it to his chest, after which he never forgot a single word he heard.",
            yearPassedAH = 59
        ),
        NarratorDetail(
            id = "aisha",
            nameEnglish = "Mother of Believers Aisha bint Abi Bakr",
            nameArabic = "عائشة بنت أبي بكر",
            relationToProphet = "Wife of the Prophet ﷺ and daughter of Abu Bakr as-Siddiq",
            totalNarrationsCount = 2210,
            scholarlyGrading = "Faqihat al-Ummah (Foremost Female Jurist and Master Scholar)",
            briefBiography = "Narrated the intimate household habits, night worship, and private manners of the Prophet ﷺ, correcting senior male Sahaba on intricate matters of Fiqh.",
            yearPassedAH = 58
        ),
        NarratorDetail(
            id = "anas",
            nameEnglish = "Anas ibn Malik",
            nameArabic = "أنس بن مالك",
            relationToProphet = "Personal servant and helper of the Prophet ﷺ for ten years in Madinah",
            totalNarrationsCount = 2286,
            scholarlyGrading = "Khadim ar-Rasul - Sahabi Thiqah Adl",
            briefBiography = "Served the Messenger of Allah from age 10 to 20. Remarked: 'He never once asked me why I did what I did, or why I did not do what I failed to do.'",
            yearPassedAH = 93
        ),
        NarratorDetail(
            id = "ibn_umar",
            nameEnglish = "Abdullah ibn Umar",
            nameArabic = "عبد الله بن عمر",
            relationToProphet = "Son of Caliph Umar ibn al-Khattab",
            totalNarrationsCount = 2630,
            scholarlyGrading = "Imam in Sunnah Adherence - Exemplary Piety",
            briefBiography = "Famous for meticulously imitating every single movement, rest, and prayer of the Prophet ﷺ down to the specific trees under which he rested.",
            yearPassedAH = 73
        )
    )
}
