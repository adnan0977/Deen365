package com.example.data.model

/**
 * Quranic Ruqyah & Spiritual Healing (Ayat ash-Shifa & Protection) Models.
 * Authentic healing supplications and Quranic passages according to the Sunnah.
 */

enum class RuqyahCategory {
    ALL,
    SHIFA_SICKNESS,
    EVIL_EYE_PROTECTION,
    ANXIETY_DEPRESSION,
    MORNING_EVENING_SHIELD
}

data class RuqyahVerse(
    val id: String,
    val title: String,
    val category: RuqyahCategory,
    val arabic: String,
    val transliteration: String,
    val translation: String,
    val reference: String,
    val recommendedCount: Int = 3,
    val propheticBenefit: String
)

data class RuqyahPropheticStep(
    val stepNumber: Int,
    val title: String,
    val instruction: String,
    val hadithProof: String
)

data class RuqyahState(
    val selectedCategory: RuqyahCategory = RuqyahCategory.ALL,
    val selectedVerse: RuqyahVerse? = null,
    val currentlyPlayingVerseId: String? = null,
    val recitationCounters: Map<String, Int> = emptyMap(),
    val isPropheticGuideDialogOpen: Boolean = false
)

object RuqyahData {

    val PROPHETIC_STEPS = listOf(
        RuqyahPropheticStep(
            stepNumber = 1,
            title = "Purification & Sincere Niyyah",
            instruction = "Perform Wudu (ablution), face the Qiblah if possible, and establish sincere certainty that cure comes solely from Allah (Ash-Shafi).",
            hadithProof = "Prophet Ibrahim said: 'And when I am ill, it is He who cures me.' (Surah Ash-Shu'ara 26:80)"
        ),
        RuqyahPropheticStep(
            stepNumber = 2,
            title = "Recitation & Cupping Hands",
            instruction = "Cup your hands together before your mouth and chest, recite the prescribed verses (Al-Fatihah, Ayat al-Kursi, Al-Mu'awwidhat) with mindful presence.",
            hadithProof = "Aisha (RA) narrated: 'Whenever the Prophet went to bed every night, he used to cup his hands together and blow over them after reciting Surah Al-Ikhlas, Al-Falaq, and An-Nas.' (Sahih Bukhari #5017)"
        ),
        RuqyahPropheticStep(
            stepNumber = 3,
            title = "Nafath (Gentle Blowing with Moisture)",
            instruction = "Lightly blow into your cupped hands with gentle, dry spitting (Nafath) containing the blessings of the Quranic words.",
            hadithProof = "The Prophet (ﷺ) used to practice Nafath during Ruqyah without emitting saliva. (Sahih Muslim #2192)"
        ),
        RuqyahPropheticStep(
            stepNumber = 4,
            title = "Wiping the Afflicted Area and Body",
            instruction = "Wipe your hands over your head, face, the front of your body, and specifically the place of pain or illness three times.",
            hadithProof = "'He would wipe over whatever he could reach of his body, starting with his head and face.' (Sahih Bukhari #5017)"
        )
    )

    val VERSES = listOf(
        RuqyahVerse(
            id = "ruqyah_fatihah",
            title = "Surah Al-Fatihah (The Mother of the Book & The Cure)",
            category = RuqyahCategory.SHIFA_SICKNESS,
            arabic = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ ۝ الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ ۝ الرَّحْمَٰنِ الرَّحِيمِ ۝ مَالِكِ يَوْمِ الدِّينِ ۝ إِيَّاكَ نَعْبُدُ وَإِيَّاكَ نَسْتَعِينُ ۝ اهْدِنَا الصِّرَاطَ الْمُسْتَقِيمَ ۝ صِرَاطَ الَّذِينَ أَنْعَمْتَ عَلَيْهِمْ غَيْرِ الْمَغْضُوبِ عَلَيْهِمْ وَلَا الضَّالِّينَ",
            transliteration = "Bismillāhir-Raḥmānir-Raḥīm. Al-ḥamdu lillāhi Rabbil-'ālamīn. Ar-Raḥmānir-Raḥīm. Māliki yawmid-dīn. Iyyāka na'budu wa iyyāka nasta'īn. Ihdinaṣ-ṣirāṭal-mustaqīm. Ṣirāṭalla-dhīna an'amta 'alayhim, ghayril-maghḍūbi 'alayhim wa laḍ-ḍāllīn.",
            translation = "In the name of Allah, the Entirely Merciful, the Especially Merciful. All praise is due to Allah, Lord of the worlds. The Entirely Merciful, the Especially Merciful. Sovereign of the Day of Recompense. It is You we worship and You we ask for help. Guide us to the straight path. The path of those upon whom You have bestowed favor, not of those who have evoked anger or of those who are astray.",
            reference = "Surah Al-Fatihah 1:1-7",
            recommendedCount = 7,
            propheticBenefit = "The companion Abu Sa'id al-Khudri cured a chieftain bitten by a scorpion by reciting Al-Fatihah, and the Prophet (ﷺ) said: 'How did you know it was a Ruqyah?' (Sahih Bukhari #2276)."
        ),
        RuqyahVerse(
            id = "ruqyah_kursi",
            title = "Ayat al-Kursi (The Verse of the Throne)",
            category = RuqyahCategory.EVIL_EYE_PROTECTION,
            arabic = "اللَّهُ لَا إِلَٰهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ ۚ لَا تَأْخُذُهُ سِنَةٌ وَلَا نَوْمٌ ۚ لَّهُ مَا فِي السَّمَاوَاتِ وَمَا فِي الْأَرْضِ ۗ مَن ذَا الَّذِي يَشْفَعُ عِندَهُ إِلَّا بِإِذْنِهِ ۚ يَعْلَمُ مَا بَيْنَ أَيْدِيهِمْ وَمَا خَلْفَهُمْ ۖ وَلَا يُحِيطُونَ بِشَيْءٍ مِّنْ عِلْمِهِ إِلَّا بِمَا شَاءَ ۚ وَسِعَ كُرْسِيُّهُ السَّمَاوَاتِ وَالْأَرْضَ ۖ وَلَا يَئُودُهُ حِفْظُهُمَا ۚ وَهُوَ الْعَلِيُّ الْعَظِيمُ",
            transliteration = "Allāhu lā ilāha illā Huwal-Ḥayyul-Qayyūm. Lā ta'khudhuhū sinatuw-wa lā nawm. Lahū mā fis-samāwāti wa mā fil-arḍ. Man dhal-ladhī yashfa'u 'indahū illā bi-idhnih. Ya'lamu mā bayna aydīhim wa mā khalfahum, wa lā yuḥīṭūna bi-shay'im-min 'ilmihī illā bimā shā'. Wasi'a kursiyyuhus-samāwāti wal-arḍ, wa lā ya'ūduhū ḥifẓuhumā, wa Huwal-'Aliyyul-'Aẓīm.",
            translation = "Allah - there is no deity except Him, the Ever-Living, the Sustainer of all existence. Neither drowsiness overtakes Him nor sleep. To Him belongs whatever is in the heavens and whatever is on the earth... His Kursi extends over the heavens and the earth, and their preservation tires Him not. And He is the Most High, the Most Great.",
            reference = "Surah Al-Baqarah 2:255",
            recommendedCount = 3,
            propheticBenefit = "Whoever recites it in the morning or before sleeping is protected by a guardian from Allah, and no devil approaches them until morning (Sahih Bukhari #2311)."
        ),
        RuqyahVerse(
            id = "ruqyah_muawwidhat",
            title = "Al-Mu'awwidhat (Surah Al-Falaq & An-Nas)",
            category = RuqyahCategory.EVIL_EYE_PROTECTION,
            arabic = "قُلْ أَعُوذُ بِرَبِّ الْفَلَقِ ۝ مِن شَرِّ مَا خَلَقَ ۝ وَمِن شَرِّ غَاسِقٍ إِذَا وَقَبَ ۝ وَمِن شَرِّ النَّفَّاثَاتِ فِي الْعُقَدِ ۝ وَمِن شَرِّ حَاسِدٍ إِذَا حَسَدَ ۝ قُلْ أَعُوذُ بِرَبِّ النَّاسِ ۝ مَلِكِ النَّاسِ ۝ إِلَٰهِ النَّاسِ ۝ مِن شَرِّ الْوَسْوَاسِ الْخَنَّاسِ ۝ الَّذِي يُوَسْوِسُ فِي صُدُورِ النَّاسِ ۝ مِنَ الْجِنَّةِ وَالنَّاسِ",
            transliteration = "Qul a'ūdhu bi-Rabbil-falaq. Min sharri mā khalaq... Qul a'ūdhu bi-Rabbin-nās. Malikin-nās. Ilāhin-nās. Min sharril-waswāsil-khannās. Alladhī yuwaswisu fī ṣudūrin-nās. Minal-jinnati wan-nās.",
            translation = "Say: I seek refuge with the Lord of the daybreak from the evil of what He created... Say: I seek refuge with the Lord of mankind, the Sovereign of mankind, the God of mankind, from the evil of the retreating whisperer.",
            reference = "Surah Al-Falaq 113 & Surah An-Nas 114",
            recommendedCount = 3,
            propheticBenefit = "The Prophet (ﷺ) said: 'Recite Qul Huwa Allahu Ahad and Al-Mu'awwidhatayn in the morning and evening three times; it will suffice you against everything.' (Sunan Abi Dawud #5082)."
        ),
        RuqyahVerse(
            id = "ruqyah_sunnah_pain",
            title = "Prophetic Du'a for Bodily Pain",
            category = RuqyahCategory.SHIFA_SICKNESS,
            arabic = "بِسْمِ اللَّهِ (ثَلَاثًا)، أَعُوذُ بِاللَّهِ وَقُدْرَتِهِ مِنْ شَرِّ مَا أَجِدُ وَأُحَاذِرُ (سَبْعًا)",
            transliteration = "Bismillāh (3 times). A'ūdhu billāhi wa qudratihī min sharri mā ajidu wa uḥādhir (7 times).",
            translation = "In the name of Allah (three times). I seek refuge in Allah and His omnipotence from the evil of what I find and dread (seven times).",
            reference = "Sahih Muslim #2202",
            recommendedCount = 7,
            propheticBenefit = "Uthman bin Abi Al-As complained of pain in his body since becoming Muslim, and the Prophet (ﷺ) taught him this formula, after which his pain vanished permanently."
        ),
        RuqyahVerse(
            id = "ruqyah_anxiety",
            title = "Du'a for Alleviating Anxiety, Grief & Distress",
            category = RuqyahCategory.ANXIETY_DEPRESSION,
            arabic = "اللَّهُمَّ إِنِّي عَبْدُكَ، ابْنُ عَبْدِكَ، ابْنُ أَمَتِكَ، نَاصِيَتِي بِيَدِكَ، مَاضٍ فِيَّ حُكْمُكَ، عَدْلٌ فِيَّ قَضَاؤُكَ، أَسْأَلُكَ بِكُلِّ اسْمٍ هُوَ لَكَ سَمَّيْتَ بِهِ نَفْسَكَ، أَوْ أَنْزَلْتَهُ فِي كِتَابِكَ، أَوْ عَلَّمْتَهُ أَحَدًا مِنْ خَلْقِكَ، أَوِ اسْتَأْثَرْتَ بِهِ فِي عِلْمِ الْغَيْبِ عِنْدَكَ، أَنْ تَجْعَلَ الْقُرْآنَ رَبِيعَ قَلْبِي، وَنُورَ صَدْرِي، وَجَلَاءَ حُزْنِي، وَذَهَابَ هَمِّي",
            transliteration = "Allāhumma innī 'abduk, ibnu 'abdik, ibnu amatik, nāṣiyatī bi-yadik, māḍin fiyya ḥukmuk, 'adlun fiyya qaḍā'uk, as'aluka bikullis-min huwa lak, sammayta bihī nafsak, aw anzaltahū fī kitābik, aw 'allamtahū aḥadam-min khalqik, awis-ta'tharta bihī fī 'ilmil-ghaybi 'indak, an taj'alal-Qur'āna rabī'a qalbī, wa nūra ṣadrī, wa jalā'a ḥuznī, wa dhahāba hammī.",
            translation = "O Allah, I am Your servant, son of Your servant, son of Your maidservant. My forelock is in Your hand, Your command over me is forever executed, and Your decree over me is just. I ask You by every name belonging to You... to make the Quran the spring of my heart, the light of my chest, the banisher of my sadness, and the reliever of my distress.",
            reference = "Musnad Ahmad #3712, Sahih Ibn Hibban",
            recommendedCount = 1,
            propheticBenefit = "The Prophet (ﷺ) promised that whoever says this, Allah will remove their grief and replace it with joy."
        )
    )
}
