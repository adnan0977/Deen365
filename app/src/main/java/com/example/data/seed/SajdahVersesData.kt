package com.example.data.seed

data class SajdahVerse(
    val id: Int,
    val surahNumber: Int,
    val surahName: String,
    val ayahNumber: Int,
    val juzNumber: Int,
    val arabicVerseText: String,
    val englishTranslation: String,
    val fiqhRuling: String, // "Wajib (Hanafi) / Sunnah Mu'akkadah (Jumhur)"
    val sajdaSupplicationArabic: String,
    val sajdaSupplicationTranslation: String
)

object SajdahVersesData {
    const val DEFAULT_SAJDA_DUA_ARABIC = "سَجَدَ وَجْهِي لِلَّذِي خَلَقَهُ وَصَوَّرَهُ، وَشَقَّ سَمْعَهُ وَبَصَرَهُ بِحَوْلِهِ وَقُوَّتِهِ، فَتَبَارَكَ اللَّهُ أَحْسَنُ الْخَالِقِينَ"
    const val DEFAULT_SAJDA_DUA_TRANSLATION = "My face has prostrated to the One Who created it and formed it, and brought forth its hearing and sight by His might and power. Blessed is Allah, the Best of Creators."

    val ALL_SAJDAH_VERSES: List<SajdahVerse> = listOf(
        SajdahVerse(1, 7, "Al-A'raf", 206, 9, "إِنَّ الَّذِينَ عِندَ رَبِّكَ لَا يَسْتَكْبِرُونَ عَنْ عِبَادَتِهِ وَيُسَبِّحُونَهُ وَلَهُ يَسْجُدُونَ ۩", "Indeed, those who are near your Lord are not prevented by arrogance from His worship, and they exalt Him, and to Him they prostrate.", "Wajib (Hanafi) / Sunnah (Jumhur)", DEFAULT_SAJDA_DUA_ARABIC, DEFAULT_SAJDA_DUA_TRANSLATION),
        SajdahVerse(2, 13, "Ar-Ra'd", 15, 13, "وَلِلَّهِ يَسْجُدُ مَن فِي السَّمَاوَاتِ وَالْأَرْضِ طَوْعًا وَكَرْهًا وَظِلَالُهُم بِالْغُدُوِّ وَالْآصَالِ ۩", "And to Allah prostrates whoever is within the heavens and the earth, willingly or by compulsion, and their shadows [as well] in the mornings and the afternoons.", "Wajib (Hanafi) / Sunnah (Jumhur)", DEFAULT_SAJDA_DUA_ARABIC, DEFAULT_SAJDA_DUA_TRANSLATION),
        SajdahVerse(3, 16, "An-Nahl", 50, 14, "يَخَافُونَ رَبَّهُم مِّن فَوْقِهِمْ وَيَفْعَلُونَ مَا يُؤْمَرُونَ ۩", "They fear their Lord above them, and they do what they are commanded.", "Wajib (Hanafi) / Sunnah (Jumhur)", DEFAULT_SAJDA_DUA_ARABIC, DEFAULT_SAJDA_DUA_TRANSLATION),
        SajdahVerse(4, 17, "Al-Isra", 109, 15, "وَيَخِرُّونَ لِلْأَذْقَانِ يَبْكُونَ وَيَزِيدُهُمْ خُشُوعًا ۩", "And they fall upon their faces weeping, and the Quran increases them in humble submission.", "Wajib (Hanafi) / Sunnah (Jumhur)", DEFAULT_SAJDA_DUA_ARABIC, DEFAULT_SAJDA_DUA_TRANSLATION),
        SajdahVerse(5, 19, "Maryam", 58, 16, "إِذَا تُتْلَىٰ عَلَيْهِمْ آيَاتُ الرَّحْمَٰنِ خَرُّوا سُجَّدًا وَبُكِيًّا ۩", "When the verses of the Most Merciful were recited to them, they fell in prostration and weeping.", "Wajib (Hanafi) / Sunnah (Jumhur)", DEFAULT_SAJDA_DUA_ARABIC, DEFAULT_SAJDA_DUA_TRANSLATION),
        SajdahVerse(6, 22, "Al-Hajj", 18, 17, "أَلَمْ تَرَ أَنَّ اللَّهَ يَسْجُدُ لَهُ مَن فِي السَّمَاوَاتِ وَمَن فِي الْأَرْضِ... ۩", "Do you not see that to Allah prostrates whoever is in the heavens and whoever is on the earth...", "Wajib (Hanafi) / Sunnah (Jumhur)", DEFAULT_SAJDA_DUA_ARABIC, DEFAULT_SAJDA_DUA_TRANSLATION),
        SajdahVerse(7, 22, "Al-Hajj", 77, 17, "يَا أَيُّهَا الَّذِينَ آمَنُوا ارْكَعُوا وَاسْجُدُوا وَاعْبُدُوا رَبَّكُمْ وَافْعَلُوا الْخَيْرَ لَعَلَّكُمْ تُفْلِحُونَ ۩", "O you who have believed, bow and prostrate and worship your Lord and do good - that you may succeed.", "Sunnah (Shafi'i/Hanbali)", DEFAULT_SAJDA_DUA_ARABIC, DEFAULT_SAJDA_DUA_TRANSLATION),
        SajdahVerse(8, 25, "Al-Furqan", 60, 19, "وَإِذَا قِيلَ لَهُمُ اسْجُدُوا لِلرَّحْمَٰنِ قَالُوا وَمَا الرَّحْمَٰنُ... ۩", "And when it is said to them, 'Prostrate to the Most Merciful,' they say, 'And what is the Most Merciful?'...", "Wajib (Hanafi) / Sunnah (Jumhur)", DEFAULT_SAJDA_DUA_ARABIC, DEFAULT_SAJDA_DUA_TRANSLATION),
        SajdahVerse(9, 27, "An-Naml", 26, 19, "اللَّهُ لَا إِلَٰهَ إِلَّا هُوَ رَبُّ الْعَرْشِ الْعَظِيمِ ۩", "Allah - there is no deity except Him, Lord of the Great Throne.", "Wajib (Hanafi) / Sunnah (Jumhur)", DEFAULT_SAJDA_DUA_ARABIC, DEFAULT_SAJDA_DUA_TRANSLATION),
        SajdahVerse(10, 32, "As-Sajdah", 15, 21, "إِنَّمَا يُؤْمِنُ بِآيَاتِنَا الَّذِينَ إِذَا ذُكِّرُوا بِهَا خَرُّوا سُجَّدًا وَسَبَّحُوا بِحَمْدِ رَبِّهِمْ وَهُمْ لَا يَسْتَكْبِرُونَ ۩", "Only those believe in Our verses who, when they are reminded by them, fall down in prostration...", "Wajib (Hanafi) / Sunnah (Jumhur)", DEFAULT_SAJDA_DUA_ARABIC, DEFAULT_SAJDA_DUA_TRANSLATION),
        SajdahVerse(11, 38, "Sad", 24, 23, "وَظَنَّ دَاوُودُ أَنَّمَا فَتَنَّاهُ فَاسْتَغْفَرَ رَبَّهُ وَخَرَّ رَاكِعًا وَأَنَابَ ۩", "And David became certain that We had tried him, and he asked forgiveness of his Lord and fell down bowing [in prostration] and turned in repentance.", "Sajdah Shukr (Shafi'i) / Wajib (Hanafi)", DEFAULT_SAJDA_DUA_ARABIC, DEFAULT_SAJDA_DUA_TRANSLATION),
        SajdahVerse(12, 41, "Fussilat", 38, 24, "فَإِنِ اسْتَكْبَرُوا فَالَّذِينَ عِندَ رَبِّكَ يُسَبِّحُونَ لَهُ بِاللَّيْلِ وَالنَّهَارِ وَهُمْ لَا يَسْأَمُونَ ۩", "But if they are arrogant - then those who are near your Lord exalt Him by night and by day, and they do not become weary.", "Wajib (Hanafi) / Sunnah (Jumhur)", DEFAULT_SAJDA_DUA_ARABIC, DEFAULT_SAJDA_DUA_TRANSLATION),
        SajdahVerse(13, 53, "An-Najm", 62, 27, "فَاسْجُدُوا لِلَّهِ وَاعْبُدُوا ۩", "So prostrate to Allah and worship [Him].", "Wajib (Hanafi) / Sunnah (Jumhur)", DEFAULT_SAJDA_DUA_ARABIC, DEFAULT_SAJDA_DUA_TRANSLATION),
        SajdahVerse(14, 84, "Al-Inshiqaq", 21, 30, "وَإِذَا قُرِئَ عَلَيْهِمُ الْقُرْآنُ لَا يَسْجُدُونَ ۩", "And when the Quran is recited to them, they do not prostrate.", "Wajib (Hanafi) / Sunnah (Jumhur)", DEFAULT_SAJDA_DUA_ARABIC, DEFAULT_SAJDA_DUA_TRANSLATION),
        SajdahVerse(15, 96, "Al-'Alaq", 19, 30, "كَلَّا لَا تُطِعْهُ وَاسْجُدْ وَاقْتَرِب ۩", "No! Do not obey him. But prostrate and draw near [to Allah].", "Wajib (Hanafi) / Sunnah (Jumhur)", DEFAULT_SAJDA_DUA_ARABIC, DEFAULT_SAJDA_DUA_TRANSLATION)
    )
}
