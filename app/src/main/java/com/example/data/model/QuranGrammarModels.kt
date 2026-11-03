package com.example.data.model

data class WordGrammarDetail(
    val arabic: String,
    val transliteration: String,
    val translation: String,
    val root: String,            // e.g. "ر - ح - م"
    val rootArabic: String,      // e.g. "رحم"
    val partOfSpeech: String,    // e.g. "Noun (اسم)" or "Verb (فعل)" or "Particle (حرف)"
    val morphology: String,      // e.g. "Genitive masculine plural adjective"
    val lemma: String,           // e.g. "رَحْمَٰن"
    val occurrenceCount: Int,    // e.g. 560
    val explanation: String,     // Linguistic & theological meaning
    val derivedWords: List<String> = emptyList() // Other Quranic derivatives
)

data class WordAudioPronunciation(
    val wordArabic: String,
    val phoneticAudioDesc: String,
    val syllableBreakdown: String
)
