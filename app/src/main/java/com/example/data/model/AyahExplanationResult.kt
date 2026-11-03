package com.example.data.model

/**
 * Structured explanation result for a specific Quranic verse (Ayah),
 * encompassing revelation context (Asbab al-Nuzul), classic Tafsir interpretation,
 * and concrete daily practical applications.
 */
data class AyahExplanationResult(
    val surahNumber: Int,
    val ayahNumber: Int,
    val surahName: String,
    val arabicText: String,
    val translation: String,
    val revelationContext: String,
    val classicalInterpretation: String,
    val practicalActionSteps: List<String>,
    val recommendedDua: String = "",
    val spiritualTheme: String = ""
)
