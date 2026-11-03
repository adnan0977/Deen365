package com.example.data.seed

data class JuzMeta(
    val juzNumber: Int,
    val arabicName: String,
    val transliteration: String,
    val startSurahNumber: Int,
    val startSurahName: String,
    val startAyahNumber: Int,
    val endSurahNumber: Int,
    val endSurahName: String,
    val endAyahNumber: Int,
    val totalVerses: Int
)

object JuzSeedData {
    val ALL_JUZ: List<JuzMeta> = listOf(
        JuzMeta(1, "الم", "Alif-Lam-Meem", 1, "Al-Fatihah", 1, 2, "Al-Baqarah", 141, 148),
        JuzMeta(2, "سَيَقُولُ", "Sayaqool", 2, "Al-Baqarah", 142, 2, "Al-Baqarah", 252, 111),
        JuzMeta(3, "تِلْكَ الرُّسُلُ", "Tilka'r-Rusul", 2, "Al-Baqarah", 253, 3, "Ali 'Imran", 92, 126),
        JuzMeta(4, "لَنْ تَنَالُوا", "Lan Tanaaloo", 3, "Ali 'Imran", 93, 4, "An-Nisa", 23, 131),
        JuzMeta(5, "وَالْمُحْصَنَاتُ", "Wal-Muhsanat", 4, "An-Nisa", 24, 4, "An-Nisa", 147, 124),
        JuzMeta(6, "لَا يُحِبُّ اللَّهُ", "La Yuhibbullah", 4, "An-Nisa", 148, 5, "Al-Ma'idah", 81, 110),
        JuzMeta(7, "وَإِذَا سَمِعُوا", "Wa Iza Sami'oo", 5, "Al-Ma'idah", 82, 6, "Al-An'am", 110, 149),
        JuzMeta(8, "وَلَوْ أَنَّنَا", "Wa Law Annana", 6, "Al-An'am", 111, 7, "Al-A'raf", 87, 142),
        JuzMeta(9, "قَالَ الْمَلَأُ", "Qal-al-Mala'u", 7, "Al-A'raf", 88, 8, "Al-Anfal", 40, 159),
        JuzMeta(10, "وَاعْلَمُوا", "Wa'lamoo", 8, "Al-Anfal", 41, 9, "At-Tawbah", 92, 127),
        JuzMeta(11, "يَعْتَذِرُونَ", "Ya'taziroon", 9, "At-Tawbah", 93, 11, "Hud", 5, 151),
        JuzMeta(12, "وَمَا مِنْ دَابَّةٍ", "Wa Ma Min Dabbatin", 11, "Hud", 6, 12, "Yusuf", 52, 170),
        JuzMeta(13, "وَمَا أُبَرِّئُ", "Wa Ma Ubarri'u", 12, "Yusuf", 53, 14, "Ibrahim", 52, 154),
        JuzMeta(14, "رُبَمَا", "Rubama", 15, "Al-Hijr", 1, 16, "An-Nahl", 128, 227),
        JuzMeta(15, "سُبْحَانَ الَّذِي", "Subhan-alladhi", 17, "Al-Isra", 1, 18, "Al-Kahf", 74, 185),
        JuzMeta(16, "قَالَ أَلَمْ", "Qala Alam", 18, "Al-Kahf", 75, 20, "Ta-Ha", 135, 269),
        JuzMeta(17, "اقْتَرَبَ لِلنَّاسِ", "Iqtaraba Lin-Nas", 21, "Al-Anbiya", 1, 22, "Al-Hajj", 78, 190),
        JuzMeta(18, "قَدْ أَفْلَحَ", "Qad Aflaha", 23, "Al-Mu'minun", 1, 25, "Al-Furqan", 20, 202),
        JuzMeta(19, "وَقَالَ الَّذِينَ", "Wa Qal-alladhina", 25, "Al-Furqan", 21, 27, "An-Naml", 55, 339),
        JuzMeta(20, "أَمَّنْ خَلَقَ", "Amman Khalaqa", 27, "An-Naml", 56, 29, "Al-'Ankabut", 45, 171),
        JuzMeta(21, "اتْلُ مَا أُوحِيَ", "Utlu Ma Oohiya", 29, "Al-'Ankabut", 46, 33, "Al-Ahzab", 30, 178),
        JuzMeta(22, "وَمَنْ يَقْنُتْ", "Wa Man Yaqnut", 33, "Al-Ahzab", 31, 36, "Ya-Sin", 27, 169),
        JuzMeta(23, "وَمَا لِيَ", "Wa Maliya", 36, "Ya-Sin", 28, 39, "Az-Zumar", 31, 357),
        JuzMeta(24, "فَمَنْ أَظْلَمُ", "Faman Azlamu", 39, "Az-Zumar", 32, 41, "Fussilat", 46, 175),
        JuzMeta(25, "إِلَيْهِ يُرَدُّ", "Ilayhi Yuraddu", 41, "Fussilat", 47, 45, "Al-Jathiyah", 37, 246),
        JuzMeta(26, "حم", "Ha-Meem", 46, "Al-Ahqaf", 1, 51, "Adh-Dhariyat", 30, 195),
        JuzMeta(27, "قَالَ فَمَا خَطْبُكُمْ", "Qala Fama Khatbukum", 51, "Adh-Dhariyat", 31, 57, "Al-Hadid", 29, 399),
        JuzMeta(28, "قَدْ سَمِعَ اللَّهُ", "Qad Sami'allah", 58, "Al-Mujadila", 1, 66, "At-Tahrim", 12, 137),
        JuzMeta(29, "تَبَارَكَ الَّذِي", "Tabarak-alladhi", 67, "Al-Mulk", 1, 77, "Al-Mursalat", 50, 431),
        JuzMeta(30, "عَمَّ", "'Amma", 78, "An-Naba", 1, 114, "An-Nas", 6, 564)
    )
}
