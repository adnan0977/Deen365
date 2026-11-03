package com.example.data.model

enum class MorphologySegmentType(val displayName: String, val arabicName: String) {
    PREFIX("Prefix / Proclitic", "سابقة / حرف"),
    STEM("Stem / Root Core", "جذع / أصل"),
    SUFFIX("Suffix / Enclitic", "لاحقة / ضمير متصل")
}

data class MorphologySegment(
    val segmentType: MorphologySegmentType,
    val arabicText: String,
    val roleName: String,
    val grammaticalDescription: String
)

data class QuranExplorerWord(
    val id: Int,
    val position: Int,
    val verseKey: String,
    val textUthmani: String,
    val transliteration: String,
    val translation: String,
    val root: String,
    val rootArabic: String,
    val lemma: String,
    val partOfSpeechCode: String,     // e.g. "P+N", "PN", "V", "PRON", "ADJ", "REL"
    val partOfSpeechName: String,     // e.g. "Preposition + Noun"
    val partOfSpeechArabic: String,   // e.g. "حرف جر + اسم مجرور"
    val grammaticalCase: String,      // e.g. "Genitive (مجرور)"
    val morphologySegments: List<MorphologySegment> = emptyList(),
    val rootFrequency: Int = 1,
    val charTypeName: String = "word",
    val audioUrl: String = "",
    val irabExplanation: String = "",
    val relatedRoots: List<String> = emptyList()
)

data class QuranExplorerVerse(
    val verseKey: String,             // e.g. "1:1"
    val surahNumber: Int,
    val surahName: String,
    val ayahNumber: Int,
    val textUthmani: String,
    val translationText: String,
    val juzNumber: Int = 1,
    val hizbNumber: Int = 1,
    val words: List<QuranExplorerWord>
) {
    val totalWordsCount: Int get() = words.count { it.charTypeName == "word" }
    val uniqueRootsCount: Int get() = words.mapNotNull { if (it.rootArabic.isNotBlank()) it.rootArabic else null }.distinct().size
}

data class QuranRootEntry(
    val rootLetters: String,          // e.g. "ر - ح - م"
    val rootArabic: String,           // e.g. "رحم"
    val meaningEnglish: String,       // e.g. "To show mercy, compasssion, womb"
    val totalOccurrencesInQuran: Int, // e.g. 560
    val derivedFormsCount: Int = 12,
    val primaryVerses: List<String> = emptyList() // e.g. ["1:1", "1:3", "2:218"]
)
