package com.example.data.model

enum class Madhab(
    val displayName: String,
    val arabicName: String,
    val founder: String,
    val era: String,
    val geographicCenter: String = "",
    val coreMethodology: String = "",
    val primaryTextbook: String = "",
    val brandColorHex: Long = 0xFF1E88E5
) {
    HANAFI(
        displayName = "Hanafi",
        arabicName = "الحنفي",
        founder = "Imam Abu Hanifa an-Nu'man",
        era = "80 - 150 AH",
        geographicCenter = "Kufa (Iraq) & Ottoman Lands",
        coreMethodology = "Systematic Qiyas (Analogy), Istihsan (Juristic Preference), & Urf (Custom)",
        primaryTextbook = "Al-Hidayah by Al-Marghinani / Radd al-Muhtar",
        brandColorHex = 0xFF1E88E5 // Classic Azure Blue
    ),
    MALIKI(
        displayName = "Maliki",
        arabicName = "المالكي",
        founder = "Imam Malik ibn Anas",
        era = "93 - 179 AH",
        geographicCenter = "Madinah al-Munawwarah & North/West Africa",
        coreMethodology = "'Amal Ahl al-Madinah (Living Tradition of Madinah) & Masalih Mursalah",
        primaryTextbook = "Al-Muwatta / Al-Mudawwanah / Mukhtasar Khalil",
        brandColorHex = 0xFF2E7D32 // Deep Madinan Emerald
    ),
    SHAFII(
        displayName = "Shafi'i",
        arabicName = "الشافعي",
        founder = "Imam Muhammad ibn Idris ash-Shafi'i",
        era = "150 - 204 AH",
        geographicCenter = "Makkah, Baghdad, Egypt & Southeast Asia",
        coreMethodology = "Systematized Usul: Quran, authentic Sunnah, strict Qiyas, & Ijma'",
        primaryTextbook = "Al-Umm / Al-Majmu' / Minhaj at-Talibin by An-Nawawi",
        brandColorHex = 0xFF8E24AA // Deep Royal Purple
    ),
    HANBALI(
        displayName = "Hanbali",
        arabicName = "الحنبلي",
        founder = "Imam Ahmad ibn Hanbal",
        era = "164 - 241 AH",
        geographicCenter = "Baghdad, Damascus & Arabian Peninsula",
        coreMethodology = "Textual priority of Nass (Quran & Hadith), Athar of Sahaba, & avoidance of speculative Ra'y",
        primaryTextbook = "Al-Mughni by Ibn Qudamah / Zad al-Mustaqni'",
        brandColorHex = 0xFFE65100 // Terracotta Amber
    )
}

data class MadhabRuling(
    val madhab: Madhab,
    val rulingSummary: String,
    val legalGrade: String, // e.g. "Fard / Obligatory", "Sunnah Mu'akkadah", "Rukn (Pillar)", "Mubah", "Makruh"
    val detailedExplanation: String,
    val primaryDalil: String, // Quran Ayah or Hadith reference
    val scholarlyNote: String = "",
    val classicalManualRef: String = "",
    val juristicPrinciple: String = ""
)

data class ComparativeFiqhTopic(
    val topicId: String,
    val title: String,
    val category: String, // "Taharah & Wudu", "Salah (Prayer)", "Sawm (Fasting)", "Zakat & Wealth"
    val coreQuestion: String,
    val consensusPoints: String,
    val rulings: List<MadhabRuling>,
    val quickSummaryTable: Map<Madhab, String> = emptyMap(),
    val practicalTakeaway: String = ""
)

data class AsbabAlNuzulItem(
    val id: String,
    val surahNumber: Int,
    val surahName: String,
    val ayahRange: String,
    val title: String,
    val occasionSummary: String,
    val historicalIncident: String,
    val classicalSource: String, // e.g. "Asbab al-Nuzul by Imam al-Wahidi" or "Lubab an-Nuqul by As-Suyuti"
    val spiritualTakeaway: String
)
