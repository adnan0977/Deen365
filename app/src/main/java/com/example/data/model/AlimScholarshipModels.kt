package com.example.data.model

enum class Madhab(val displayName: String, val arabicName: String, val founder: String, val era: String) {
    HANAFI("Hanafi", "الحنفي", "Imam Abu Hanifa an-Nu'man", "80 - 150 AH"),
    MALIKI("Maliki", "المالكي", "Imam Malik ibn Anas", "93 - 179 AH"),
    SHAFII("Shafi'i", "الشافعي", "Imam Muhammad ibn Idris ash-Shafi'i", "150 - 204 AH"),
    HANBALI("Hanbali", "الحنبلي", "Imam Ahmad ibn Hanbal", "164 - 241 AH")
}

data class MadhabRuling(
    val madhab: Madhab,
    val rulingSummary: String,
    val legalGrade: String, // e.g. "Fard / Obligatory", "Sunnah Mu'akkadah", "Rukn (Pillar)", "Mubah"
    val detailedExplanation: String,
    val primaryDalil: String, // Quran Ayah or Hadith reference
    val scholarlyNote: String
)

data class ComparativeFiqhTopic(
    val topicId: String,
    val title: String,
    val category: String, // "Taharah & Wudu", "Salah (Prayer)", "Sawm (Fasting)", "Zakat & Wealth"
    val coreQuestion: String,
    val consensusPoints: String,
    val rulings: List<MadhabRuling>
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
