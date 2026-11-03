package com.example.data.model

data class QuranAudioReciter(
    val id: String,
    val name: String,
    val style: String,
    val cdnSubpath: String
) {
    /**
     * Constructs standard EveryAyah / Quran.com CDN audio URL:
     * e.g., https://everyayah.com/data/Alafasy_128kbps/001001.mp3
     */
    fun getAyahAudioUrl(surahNumber: Int, ayahNumberInSurah: Int): String {
        val surahStr = surahNumber.toString().padStart(3, '0')
        val ayahStr = ayahNumberInSurah.toString().padStart(3, '0')
        return "https://everyayah.com/data/$cdnSubpath/$surahStr$ayahStr.mp3"
    }

    companion object {
        val MISHARY = QuranAudioReciter(
            id = "alafasy",
            name = "Mishary Rashid Alafasy",
            style = "Murattal (Kuwait)",
            cdnSubpath = "Alafasy_128kbps"
        )

        val ABDUL_BASIT = QuranAudioReciter(
            id = "abdul_basit",
            name = "Abdul Basit Abdul Samad",
            style = "Mujawwad (Egypt)",
            cdnSubpath = "Abdul_Basit_Mujawwad_128kbps"
        )

        val SUDAIS = QuranAudioReciter(
            id = "sudais",
            name = "Abdur-Rahman As-Sudais",
            style = "Murattal (Makkah Imam)",
            cdnSubpath = "Abdurrahmaan_As-Sudais_192kbps"
        )

        val ALL_RECITERS = listOf(MISHARY, ABDUL_BASIT, SUDAIS)

        fun fromId(id: String): QuranAudioReciter =
            ALL_RECITERS.find { it.id == id } ?: MISHARY
    }
}
