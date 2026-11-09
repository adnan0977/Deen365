package com.example.data.model

/**
 * Islamic Baby Names & Meanings Dictionary Models & Database.
 */

enum class BabyGender {
    ALL,
    BOY,
    GIRL,
    UNISEX
}

data class BabyNameItem(
    val id: String,
    val name: String,
    val arabic: String,
    val gender: BabyGender,
    val meaning: String,
    val origin: String,
    val significance: String,
    val isQuranic: Boolean = false
)

data class BabyNamesState(
    val searchQuery: String = "",
    val selectedGender: BabyGender = BabyGender.ALL,
    val selectedLetter: String = "All", // "All", "A", "B", ...
    val favoriteIds: Set<String> = emptySet(),
    val selectedNameDetail: BabyNameItem? = null
)

object BabyNamesData {

    val SUNNAH_ETIQUETTES = listOf(
        "Give children meaningful, noble names: The Prophet (ﷺ) said: 'On the Day of Resurrection you will be called by your names and your fathers' names, so make your names good.' (Abu Dawud #4948)",
        "Beloved names to Allah: 'The names dearest to Allah are Abdullah and Abdur-Rahman.' (Sahih Muslim #2132)",
        "Naming after Prophets & Righteous: It is commendable Sunnah to name after prophets (e.g. Ibrahim, Musa, Isa, Muhammad) and noble companions (Sahaba)",
        "Tahneek on birth: Chewing a piece of sweet date and gently rubbing it on the newborn's palate as the Prophet (ﷺ) did",
        "Aqiqah on the 7th Day: Sacrificing one sheep for a girl, two for a boy, shaving the baby's hair, and giving its weight in silver to charity"
    )

    val ALL_NAMES = listOf(
        BabyNameItem(
            id = "name_muhammad",
            name = "Muhammad",
            arabic = "مُحَمَّد",
            gender = BabyGender.BOY,
            meaning = "Praiseworthy, Commended, Praised in character",
            origin = "Arabic",
            significance = "The name of the final Messenger of Allah, Prophet Muhammad (ﷺ).",
            isQuranic = true
        ),
        BabyNameItem(
            id = "name_ahmad",
            name = "Ahmad",
            arabic = "أَحْمَد",
            gender = BabyGender.BOY,
            meaning = "Much praised, Most laudable",
            origin = "Arabic",
            significance = "Prophesied celestial name of Prophet Muhammad (ﷺ) in Surah As-Saff (61:6).",
            isQuranic = true
        ),
        BabyNameItem(
            id = "name_abdullah",
            name = "Abdullah",
            arabic = "عَبْدُ اللَّهِ",
            gender = BabyGender.BOY,
            meaning = "Servant of Allah",
            origin = "Arabic",
            significance = "Explicitly cited by the Prophet (ﷺ) as one of the two most beloved names to Allah.",
            isQuranic = true
        ),
        BabyNameItem(
            id = "name_ibrahim",
            name = "Ibrahim",
            arabic = "إِبْرَاهِيم",
            gender = BabyGender.BOY,
            meaning = "Father of many nations, Intimate Friend of Allah",
            origin = "Arabic / Semitic",
            significance = "The patriarch prophet Khalilullah (Friend of Allah), builder of the Kaaba.",
            isQuranic = true
        ),
        BabyNameItem(
            id = "name_ali",
            name = "Ali",
            arabic = "عَلِيّ",
            gender = BabyGender.BOY,
            meaning = "Elevated, High, Noble, Exalted",
            origin = "Arabic",
            significance = "Ali ibn Abi Talib (RA), cousin and son-in-law of the Prophet, 4th Rightly Guided Caliph.",
            isQuranic = false
        ),
        BabyNameItem(
            id = "name_umar",
            name = "Umar",
            arabic = "عُمَر",
            gender = BabyGender.BOY,
            meaning = "Flourishing, Long-lived, Populous",
            origin = "Arabic",
            significance = "Umar ibn al-Khattab (RA), Al-Farooq (The Distinguisher between truth and falsehood).",
            isQuranic = false
        ),
        BabyNameItem(
            id = "name_hamza",
            name = "Hamza",
            arabic = "حَمْزَة",
            gender = BabyGender.BOY,
            meaning = "Lion, Steadfast, Strong",
            origin = "Arabic",
            significance = "Hamza ibn Abdul-Muttalib (RA), beloved uncle of the Prophet, 'Lion of Allah'.",
            isQuranic = false
        ),
        BabyNameItem(
            id = "name_zayd",
            name = "Zayd",
            arabic = "زَيْد",
            gender = BabyGender.BOY,
            meaning = "Growth, Abundance, Increase",
            origin = "Arabic",
            significance = "Zayd ibn Harithah (RA), the only companion mentioned explicitly by name in the Quran (33:37).",
            isQuranic = true
        ),
        BabyNameItem(
            id = "name_rayan",
            name = "Rayan",
            arabic = "رَيَّان",
            gender = BabyGender.BOY,
            meaning = "Lush, Luxuriant, Quenched of thirst",
            origin = "Arabic",
            significance = "Ar-Rayyan is the dedicated gate of Paradise reserved exclusively for those who fasted (Bukhari #1896).",
            isQuranic = false
        ),
        BabyNameItem(
            id = "name_fatima",
            name = "Fatima",
            arabic = "فَاطِمَة",
            gender = BabyGender.GIRL,
            meaning = "One who weans, Abstaining",
            origin = "Arabic",
            significance = "Fatima az-Zahra (RA), leader of the women of Paradise and daughter of the Prophet (ﷺ).",
            isQuranic = false
        ),
        BabyNameItem(
            id = "name_aisha",
            name = "Aisha",
            arabic = "عَائِشَة",
            gender = BabyGender.GIRL,
            meaning = "Living, Prosperous, Full of life",
            origin = "Arabic",
            significance = "Aisha bint Abi Bakr (RA), Mother of the Believers and immense scholar of Hadith.",
            isQuranic = false
        ),
        BabyNameItem(
            id = "name_khadijah",
            name = "Khadijah",
            arabic = "خَدِيجَة",
            gender = BabyGender.GIRL,
            meaning = "Early baby, Trustworthy, Pure",
            origin = "Arabic",
            significance = "Khadijah bint Khuwaylid (RA), first believer in Islam, foremost wife of the Prophet (ﷺ).",
            isQuranic = false
        ),
        BabyNameItem(
            id = "name_maryam",
            name = "Maryam",
            arabic = "مَرْيَم",
            gender = BabyGender.GIRL,
            meaning = "Pious worshipper, Beloved, Devout servant",
            origin = "Arabic / Hebrew",
            significance = "Maryam (Mary), mother of Prophet Isa (Jesus), the only woman named directly in the Quran with a dedicated Surah.",
            isQuranic = true
        ),
        BabyNameItem(
            id = "name_asma",
            name = "Asma",
            arabic = "أَسْمَاء",
            gender = BabyGender.GIRL,
            meaning = "Exalted, Prestigious, Beautiful attributes",
            origin = "Arabic",
            significance = "Asma bint Abi Bakr (RA), 'Dhat an-Nitaqayn' (She of the Two Waistbelts), hero of the Hijrah.",
            isQuranic = false
        ),
        BabyNameItem(
            id = "name_safiyyah",
            name = "Safiyyah",
            arabic = "صَفِيَّة",
            gender = BabyGender.GIRL,
            meaning = "Pure, Chosen friend, Sincere companion",
            origin = "Arabic",
            significance = "Safiyyah bint Huyayy (RA), Mother of the Believers, and Safiyyah bint Abdul-Muttalib, aunt of the Prophet.",
            isQuranic = false
        ),
        BabyNameItem(
            id = "name_noor",
            name = "Noor",
            arabic = "نُور",
            gender = BabyGender.UNISEX,
            meaning = "Divine Light, Illumination, Radiance",
            origin = "Arabic",
            significance = "One of the divine names and the title of Surah An-Nur (24:35) 'Allah is the Light of the heavens and earth'.",
            isQuranic = true
        ),
        BabyNameItem(
            id = "name_zayn",
            name = "Zayn",
            arabic = "زَيْن",
            gender = BabyGender.BOY,
            meaning = "Grace, Beauty, Excellence, Ornament",
            origin = "Arabic",
            significance = "Derived from Ziyana, signifying aesthetic and moral beauty in Islam.",
            isQuranic = false
        ),
        BabyNameItem(
            id = "name_iman",
            name = "Iman",
            arabic = "إِيمَان",
            gender = BabyGender.UNISEX,
            meaning = "Faith, Certainty, Belief in Allah",
            origin = "Arabic",
            significance = "The core foundation of Islamic conviction.",
            isQuranic = true
        )
    )

    fun filter(query: String, gender: BabyGender, letter: String): List<BabyNameItem> {
        return ALL_NAMES.filter { item ->
            val matchesQuery = query.isBlank() ||
                    item.name.contains(query, ignoreCase = true) ||
                    item.meaning.contains(query, ignoreCase = true) ||
                    item.arabic.contains(query)

            val matchesGender = gender == BabyGender.ALL || item.gender == gender || item.gender == BabyGender.UNISEX

            val matchesLetter = letter == "All" || item.name.startsWith(letter, ignoreCase = true)

            matchesQuery && matchesGender && matchesLetter
        }
    }
}
