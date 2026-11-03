package com.example.data.seed

data class RubAlHizbMarker(
    val id: String,
    val surahNumber: Int,
    val ayahNumber: Int,
    val hizbNumber: Int,
    val quarterType: String, // "Rub' 1/4", "Nisf 1/2", "Thalathat Arba' 3/4", "Full Hizb"
    val arabicQuarterTitle: String,
    val description: String
)

object HizbQuarterData {

    /**
     * Key Rub' al-Hizb markers across prominent Surahs.
     */
    val PROMINENT_MARKERS = listOf(
        RubAlHizbMarker("h1_q1", 1, 1, 1, "Rub' 1/4", "۞ رُبْعُ الْحِزْبِ ١", "Start of Surah Al-Fatihah • Juz 1"),
        RubAlHizbMarker("h1_q2", 2, 26, 1, "Nisf 1/2", "۞ نِصْفُ الْحِزْبِ ١", "Surah Al-Baqarah 2:26 (Innallaha la yastahyi...)"),
        RubAlHizbMarker("h1_q3", 2, 44, 1, "Thalathat Arba' 3/4", "۞ ثَلاَثَةُ أَرْبَاعِ الْحِزْبِ ١", "Surah Al-Baqarah 2:44 (Ata'muroona an-nasa bil-birr...)"),
        RubAlHizbMarker("h1_q4", 2, 60, 1, "Full Hizb", "۞ نِهَايَةُ الْحِزْبِ ١", "Surah Al-Baqarah 2:60 (Wa idhis-tasqa Moosa...)"),
        RubAlHizbMarker("h2_q1", 2, 75, 2, "Rub' 1/4", "۞ رُبْعُ الْحِزْبِ ٢", "Surah Al-Baqarah 2:75 (Afa-tatma'oona...)"),
        RubAlHizbMarker("h2_q2", 2, 92, 2, "Nisf 1/2", "۞ نِصْفُ الْحِزْبِ ٢", "Surah Al-Baqarah 2:92 (Wa laqad ja'akum Moosa...)"),
        RubAlHizbMarker("h2_q3", 2, 106, 2, "Thalathat Arba' 3/4", "۞ ثَلاَثَةُ أَرْبَاعِ الْحِزْبِ ٢", "Surah Al-Baqarah 2:106 (Ma nansakh min aayah...)"),
        RubAlHizbMarker("h2_q4", 2, 142, 2, "Full Hizb", "۞ بَدْءُ الْجُزْءِ ٢ • سَيَقُولُ", "Surah Al-Baqarah 2:142 (Sayaqoolus-sufaha'u...)"),
        RubAlHizbMarker("h3_q1", 2, 177, 3, "Rub' 1/4", "۞ لَيْسَ الْبِرَّ", "Surah Al-Baqarah 2:177 (Laysal-birra an tuwalloo...)"),
        RubAlHizbMarker("h3_q2", 2, 188, 3, "Nisf 1/2", "۞ نِصْفُ الْحِزْبِ ٣", "Surah Al-Baqarah 2:188 (Wa la ta'kuloo amwalakum...)"),
        RubAlHizbMarker("h4_q1", 2, 255, 4, "Rub' 1/4", "۞ آيَةُ الْكُرْسِيِّ", "Surah Al-Baqarah 2:255 (Ayat al-Kursi - The Throne Verse)"),
        RubAlHizbMarker("h5_q1", 3, 1, 5, "Rub' 1/4", "۞ آلُ عِمْرَانَ", "Start of Surah Ali 'Imran 3:1 • Juz 3"),
        RubAlHizbMarker("h6_q1", 4, 1, 7, "Rub' 1/4", "۞ سُورَةُ النِّسَاءِ", "Start of Surah An-Nisa 4:1 • Juz 4"),
        RubAlHizbMarker("h7_q1", 5, 1, 9, "Rub' 1/4", "۞ سُورَةُ الْمَائِدَةِ", "Start of Surah Al-Ma'idah 5:1 • Juz 6"),
        RubAlHizbMarker("h8_q1", 6, 1, 11, "Rub' 1/4", "۞ سُورَةُ الأَنْعَامِ", "Start of Surah Al-An'am 6:1 • Juz 7"),
        RubAlHizbMarker("h9_q1", 9, 1, 19, "Rub' 1/4", "۞ سُورَةُ التَّوْبَةِ", "Start of Surah At-Tawbah 9:1 • Juz 10"),
        RubAlHizbMarker("h10_q1", 18, 1, 29, "Rub' 1/4", "۞ سُورَةُ الْكَهْفِ", "Start of Surah Al-Kahf 18:1 • Juz 15"),
        RubAlHizbMarker("h11_q1", 36, 1, 45, "Rub' 1/4", "۞ سُورَةُ يس", "Start of Surah Ya-Sin 36:1 • Juz 22"),
        RubAlHizbMarker("h12_q1", 55, 1, 53, "Rub' 1/4", "۞ سُورَةُ الرَّحْمَٰنِ", "Start of Surah Ar-Rahman 55:1 • Juz 27"),
        RubAlHizbMarker("h13_q1", 67, 1, 57, "Rub' 1/4", "۞ سُورَةُ الْمُلْكِ", "Start of Surah Al-Mulk 67:1 • Juz 29"),
        RubAlHizbMarker("h14_q1", 78, 1, 59, "Rub' 1/4", "۞ عَمَّ يَتَسَاءَلُونَ", "Start of Juz Amma 78:1 • Juz 30")
    )

    fun getMarkerForAyah(surahNumber: Int, ayahNumber: Int): RubAlHizbMarker? {
        return PROMINENT_MARKERS.firstOrNull { it.surahNumber == surahNumber && it.ayahNumber == ayahNumber }
    }
}
