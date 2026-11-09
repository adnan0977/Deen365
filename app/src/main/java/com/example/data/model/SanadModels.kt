package com.example.data.model

enum class NarratorRole(val titleEnglish: String, val titleArabic: String) {
    PROPHET("The Messenger of Allah ﷺ", "رسول الله ﷺ"),
    COMPANION("Companion (Sahabi)", "صحابي جليل"),
    SENIOR_TABII("Senior Successor (Kibar at-Tabi'in)", "كبار التابعين"),
    TABII("Successor (Tabi'i)", "تابعي"),
    ATBA_TABII("Successor of the Successors (Atba' at-Tabi'in)", "أتباع التابعين"),
    SHEIKH_COLLECTOR("Direct Teacher of Compiler (Sheikh)", "شيخ المصنف"),
    COMPILER("Master Compiler (Al-Musannif)", "المصنف الإمام")
}

/**
 * Islamic Hadith transmission verb methodologies classified in Ilm al-Mustalah.
 */
enum class TransmissionMethod(
    val arabicTerm: String,
    val englishTerm: String,
    val rank: String,
    val description: String
) {
    SAMA(
        arabicTerm = "السماع من لفظ الشيخ (حَدَّثَنَا / سَمِعْتُ)",
        englishTerm = "Direct Oral Hearing (Sama')",
        rank = "Highest Level (Darajah 'Ulya)",
        description = "The narrator directly and audibly heard the text articulated by the Sheikh."
    ),
    QIRAAH(
        arabicTerm = "القراءة والعرض على الشيخ (أَخْبَرَنَا)",
        englishTerm = "Recitation to Sheikh ('Ard / Qira'ah)",
        rank = "Direct Equivalent to Hearing",
        description = "The student recited the hadith directly before the Sheikh, who listened and verified its accuracy."
    ),
    IJAZAH(
        arabicTerm = "الإجازة المعتمدة (أَجَازَ لِي)",
        englishTerm = "Scholarly License (Ijazah)",
        rank = "Formal Authorization",
        description = "The Sheikh granted specific or general transmission authorization for certified books."
    ),
    MUKATABAH(
        arabicTerm = "المكاتبة والتدوين (كَتَبَ إِلَيَّ)",
        englishTerm = "Written Transmission (Mukatabah)",
        rank = "Authentic Written Record",
        description = "The Sheikh wrote down the Hadith with his own hand and sent it to the student with authenticated verification."
    ),
    ANANAH(
        arabicTerm = "العنعنة (عَنْ)",
        englishTerm = "Ascribed Transmission ('An)",
        rank = "Connected if Meeting is Proven",
        description = "Transmitted using 'An. Deemed fully connected (Muttasil) when contemporary existence and physical meeting are verified without Tadlis."
    ),
    WIJADAH(
        arabicTerm = "الوجادة الموثقة (وَجَدْتُ بِخَطِّ)",
        englishTerm = "Manuscript Discovery (Wijadah)",
        rank = "Documentary Evidence",
        description = "Found in an authenticated, verified original codex of a recognized Imam."
    )
}

/**
 * Visual badge tiers for scholarly grading.
 */
enum class ScholarlyGradeTier(val label: String, val arabicLabel: String) {
    GOLD("Infallible / Ultimate Uprightness", "مقام النبوة والصحابة العدول"),
    EMERALD("Highest Trustworthiness (Thiqah Thabit)", "ثقة ثبت حجة إمام"),
    AMBER("Trustworthy & Upright (Thiqah / Saduq)", "ثقة مأمون صدوق"),
    PURPLE("Master Hadith Compiler (Al-Musannif)", "أئمة ودواوين السنة")
}

/**
 * Classical Jarh wa Ta'dil verdict from foremost Hadith scholars.
 */
data class NarratorEvaluation(
    val scholarName: String,
    val verdictArabic: String,
    val verdictEnglish: String,
    val sourceBook: String
)

/**
 * A directional, interactive transmission link connecting two consecutive narrators.
 */
data class TransmissionLink(
    val linkId: String,
    val fromNodeId: String, // Receiver / Student
    val toNodeId: String, // Source / Sheikh
    val formulaArabic: String, // e.g. "حَدَّثَنَا", "أَخْبَرَنَا", "عَنْ", "سَمِعْتُ"
    val formulaEnglish: String,
    val method: TransmissionMethod = TransmissionMethod.SAMA,
    val isDirectHearingProven: Boolean = true,
    val historicalMeetingCity: String = "Madinah al-Munawwarah",
    val continuityNotes: String = "Physical meeting and contemporary companionship historically established."
)

/**
 * Single narrator node in the Sanad chain according to Sunnah.com taxonomy.
 */
data class SanadNode(
    val nodeId: String,
    val nameEnglish: String,
    val nameArabic: String,
    val role: NarratorRole,
    val generationLevel: Int, // 1 = Prophet, 2 = Sahaba, 3 = Tabi'in, 4 = Atba', 5 = Teachers, 6 = Compiler
    val scholarlyGrading: String, // e.g. "Thiqah Thabit", "Sahabi 'Adl", "Amir al-Mu'minin fi al-Hadith"
    val transmissionFormula: String, // e.g. "حَدَّثَنَا (Haddathana)", "أَخْبَرَنَا (Akhbarana)", "عَنْ ('An)"
    val yearPassedAH: Int,
    val placeOfResidence: String,
    val studentTeacherCount: String,
    val briefBiography: String,
    val isFocalPoint: Boolean = false,

    // Sunnah.com Enhanced Scholarship Fields
    val sunnahNarratorId: Int? = null,
    val tabaqah: String = "",
    val kunya: String = "",
    val gradeTier: ScholarlyGradeTier = ScholarlyGradeTier.EMERALD,
    val evaluations: List<NarratorEvaluation> = emptyList(),
    val notableTeachers: List<String> = emptyList(),
    val notableStudents: List<String> = emptyList(),
    val totalHadithsNarrated: Int = 0,
    val birthYearAH: Int? = null,
    val originCity: String = ""
)

/**
 * Complete Sanad transmission chain representing a Sunnah.com Hadith narration.
 */
data class SanadChain(
    val chainId: String,
    val hadithNumber: Int,
    val collectionId: String,
    val collectionName: String,
    val chapterTitle: String,
    val nodes: List<SanadNode>,
    val chainGrade: String = "Sahih Muttasil (Unbroken Authentic Chain)",
    val scholarsCommentary: String,

    // Sunnah.com Deep Transmission Links
    val links: List<TransmissionLink> = emptyList(),
    val sunnahHadithUrn: String? = null,
    val authenticityRating: String = "Sahih",
    val isContinuityVerified: Boolean = true,
    val spanYears: Int = 245
)
