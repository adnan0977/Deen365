package com.example.data.seed

import com.example.data.local.DhikrItemEntity

object DhikrSeedData {
    val DEFAULT_ITEMS: List<DhikrItemEntity> = listOf(
        DhikrItemEntity(
            id = "subhanallah",
            arabicText = "سُبْحَانَ اللَّهِ",
            transliteration = "SubhanAllah",
            meaning = "Glory be to Allah",
            dailyGoal = 100,
            virtue = "The Prophet ﷺ said: 'Whoever says SubhanAllah 100 times, a thousand good deeds are recorded for him, or a thousand bad deeds are wiped away.' (Sahih Muslim)",
            category = "Praise",
            displayOrder = 1
        ),
        DhikrItemEntity(
            id = "alhamdulillah",
            arabicText = "الْحَمْدُ لِلَّهِ",
            transliteration = "Alhamdulillah",
            meaning = "All praise is due to Allah",
            dailyGoal = 100,
            virtue = "The Prophet ﷺ said: 'Alhamdulillah fills the scale of good deeds on the Day of Judgment.' (Sahih Muslim 223)",
            category = "Gratitude",
            displayOrder = 2
        ),
        DhikrItemEntity(
            id = "allahuakbar",
            arabicText = "اللَّهُ أَكْبَرُ",
            transliteration = "Allahu Akbar",
            meaning = "Allah is the Greatest",
            dailyGoal = 100,
            virtue = "One of the most beloved phrases to Allah that magnifies the Creator above all earthly concerns.",
            category = "Praise",
            displayOrder = 3
        ),
        DhikrItemEntity(
            id = "astaghfirullah",
            arabicText = "أَسْتَغْفِرُ اللَّهَ",
            transliteration = "Astaghfirullah",
            meaning = "I seek forgiveness from Allah",
            dailyGoal = 100,
            virtue = "The Prophet ﷺ said: 'By Allah, I seek Allah's forgiveness and turn to Him in repentance more than seventy times a day.' (Sahih al-Bukhari 6307)",
            category = "Forgiveness",
            displayOrder = 4
        ),
        DhikrItemEntity(
            id = "lailahaillallah",
            arabicText = "لاَ إِلَهَ إِلاَّ اللَّهُ",
            transliteration = "La ilaha illallah",
            meaning = "There is no deity worthy of worship except Allah",
            dailyGoal = 100,
            virtue = "The Prophet ﷺ said: 'The best remembrance is La ilaha illallah, and the best supplication is Alhamdulillah.' (Sunan al-Tirmidhi 3383)",
            category = "Tawhid",
            displayOrder = 5
        ),
        DhikrItemEntity(
            id = "salawat",
            arabicText = "اللَّهُمَّ صَلِّ عَلَى مُحَمَّدٍ وَعَلَى آلِ مُحَمَّدٍ",
            transliteration = "Allahumma salli 'ala Muhammadin wa 'ala ali Muhammad",
            meaning = "O Allah, send blessings upon Muhammad and upon the family of Muhammad",
            dailyGoal = 100,
            virtue = "The Prophet ﷺ said: 'Whoever sends blessings upon me once, Allah sends blessings upon him tenfold, removes ten sins, and raises him ten degrees.' (Sunan an-Nasa'i 1297)",
            category = "Salawat",
            displayOrder = 6
        ),
        DhikrItemEntity(
            id = "subhanallahi_wa_bihamdihi",
            arabicText = "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ ، سُبْحَانَ اللَّهِ الْعَظِيمِ",
            transliteration = "SubhanAllahi wa bihamdihi, SubhanAllahil Azeem",
            meaning = "Glory be to Allah and His praise, Glory be to Allah the Supreme",
            dailyGoal = 100,
            virtue = "The Prophet ﷺ said: 'Two words are light on the tongue, heavy on the Balance, and beloved to the Most Merciful: SubhanAllahi wa bihamdihi, SubhanAllahil Azeem.' (Sahih al-Bukhari 6682)",
            category = "Praise",
            displayOrder = 7
        ),
        DhikrItemEntity(
            id = "hawqala",
            arabicText = "لاَ حَوْلَ وَلاَ قُوَّةَ إِلاَّ بِاللَّهِ",
            transliteration = "La hawla wa la quwwata illa billah",
            meaning = "There is no power nor strength except with Allah",
            dailyGoal = 100,
            virtue = "The Prophet ﷺ told Abu Musa al-Ash'ari: 'Shall I not guide you to a treasure from the treasures of Paradise? Say: La hawla wa la quwwata illa billah.' (Sahih al-Bukhari 4205)",
            category = "Reliance",
            displayOrder = 8
        ),
        DhikrItemEntity(
            id = "hasbunallah",
            arabicText = "حَسْبُنَا اللَّهُ وَنِعْمَ الْوَكِيلُ",
            transliteration = "HasbunAllahu wa ni'mal wakeel",
            meaning = "Allah is sufficient for us, and He is the best disposer of affairs",
            dailyGoal = 100,
            virtue = "Said by Ibrahim (as) when thrown into the fire, and by Muhammad ﷺ when told the armies had gathered against him. (Sahih al-Bukhari 4563)",
            category = "Reliance",
            displayOrder = 9
        )
    )
}
