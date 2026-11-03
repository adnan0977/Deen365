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
    val isFocalPoint: Boolean = false
)

data class SanadChain(
    val chainId: String,
    val hadithNumber: Int,
    val collectionId: String,
    val collectionName: String,
    val chapterTitle: String,
    val nodes: List<SanadNode>,
    val chainGrade: String = "Sahih Muttasil (Unbroken Authentic Chain)",
    val scholarsCommentary: String
)
