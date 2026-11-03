package com.example.data.seed

data class PrayerProcessStep(
    val stepIndex: Int,
    val title: String,
    val arabicName: String,
    val posture: String,
    val arabicRecitation: String,
    val transliteration: String,
    val englishTranslation: String,
    val repetitionGuide: String = "1x",
    val audioId: String,
    val notes: String
)

data class AzanPhrase(
    val arabic: String,
    val transliteration: String,
    val translation: String,
    val repeats: String
)

data class PrayerAzanData(
    val prayerName: String,
    val isFajr: Boolean = false,
    val audioId: String,
    val phrases: List<AzanPhrase>,
    val duaAfterAzanArabic: String,
    val duaAfterAzanTransliteration: String,
    val duaAfterAzanTranslation: String
)

data class DetailedPrayerPlan(
    val id: String,
    val name: String,
    val arabicTitle: String,
    val rakatsSummary: String,
    val totalRakats: Int,
    val recitationRule: String,
    val bestTime: String,
    val hadithVirtue: String,
    val steps: List<PrayerProcessStep>,
    val azan: PrayerAzanData
)

object NamazPrayerProcessData {

    private val STANDARD_AZAN_PHRASES = listOf(
        AzanPhrase("اللَّهُ أَكْبَرُ، اللَّهُ أَكْبَرُ", "Allahu Akbar, Allahu Akbar", "Allah is the Greatest, Allah is the Greatest", "2x"),
        AzanPhrase("أَشْهَدُ أَنْ لَا إِلَٰهَ إِلَّا اللَّهُ", "Ash-hadu an la ilaha illallah", "I bear witness that there is no deity worthy of worship except Allah", "2x"),
        AzanPhrase("أَشْهَدُ أَنَّ مُحَمَّدًا رَسُولُ اللَّهِ", "Ash-hadu anna Muhammadan Rasoolullah", "I bear witness that Muhammad is the Messenger of Allah", "2x"),
        AzanPhrase("حَيَّ عَلَى الصَّلَاةِ", "Hayya 'alas-Salah", "Hasten to the prayer", "2x"),
        AzanPhrase("حَيَّ عَلَى الْفَلَاحِ", "Hayya 'alal-Falah", "Hasten to success and salvation", "2x"),
        AzanPhrase("اللَّهُ أَكْبَرُ، اللَّهُ أَكْبَرُ", "Allahu Akbar, Allahu Akbar", "Allah is the Greatest, Allah is the Greatest", "1x"),
        AzanPhrase("لَا إِلَٰهَ إِلَّا اللَّهُ", "La ilaha illallah", "There is no deity worthy of worship except Allah", "1x")
    )

    private val FAJR_AZAN_PHRASES = listOf(
        AzanPhrase("اللَّهُ أَكْبَرُ، اللَّهُ أَكْبَرُ", "Allahu Akbar, Allahu Akbar", "Allah is the Greatest, Allah is the Greatest", "2x"),
        AzanPhrase("أَشْهَدُ أَنْ لَا إِلَٰهَ إِلَّا اللَّهُ", "Ash-hadu an la ilaha illallah", "I bear witness that there is no deity worthy of worship except Allah", "2x"),
        AzanPhrase("أَشْهَدُ أَنَّ مُحَمَّدًا رَسُولُ اللَّهِ", "Ash-hadu anna Muhammadan Rasoolullah", "I bear witness that Muhammad is the Messenger of Allah", "2x"),
        AzanPhrase("حَيَّ عَلَى الصَّلَاةِ", "Hayya 'alas-Salah", "Hasten to the prayer", "2x"),
        AzanPhrase("حَيَّ عَلَى الْفَلَاحِ", "Hayya 'alal-Falah", "Hasten to success and salvation", "2x"),
        AzanPhrase("الصَّلَاةُ خَيْرٌ مِنَ النَّوْمِ", "As-Salatu khayrum minan-nawm", "Prayer is better than sleep! (Fajr Special Addition)", "2x"),
        AzanPhrase("اللَّهُ أَكْبَرُ، اللَّهُ أَكْبَرُ", "Allahu Akbar, Allahu Akbar", "Allah is the Greatest, Allah is the Greatest", "1x"),
        AzanPhrase("لَا إِلَٰهَ إِلَّا اللَّهُ", "La ilaha illallah", "There is no deity worthy of worship except Allah", "1x")
    )

    private const val DUA_AFTER_AZAN_AR = "اللَّهُمَّ رَبَّ هَذِهِ الدَّعْوَةِ التَّامَّةِ، وَالصَّلَاةِ الْقَائِمَةِ، آتِ مُحَمَّدًا الْوَسِيلَةَ وَالْفَضِيلَةَ، وَابْعَثْهُ مَقَامًا مَحْمُودًا الَّذِي وَعَدْتَهُ"
    private const val DUA_AFTER_AZAN_TR = "Allahumma Rabba hadhihid-da'watit-tammah, was-salatil-qa'imah, ati Muhammadanil-wasilata wal-fadilah, wab'ath-hu maqamam mahmudanilladhi wa'adtah."
    private const val DUA_AFTER_AZAN_EN = "O Allah, Lord of this perfect call and established prayer, grant Muhammad the intercession and virtue, and raise him to the praised station which You have promised him."

    val PRAYERS = listOf(
        DetailedPrayerPlan(
            id = "fajr",
            name = "Fajr (Dawn Prayer)",
            arabicTitle = "صَلَاةُ الفَجْرِ",
            rakatsSummary = "2 Sunnah Mu'akkadah + 2 Fard",
            totalRakats = 4,
            recitationRule = "Recite Surah Al-Fatiha and Ayats loudly (Jahri) in the 2 Fard Rak'ahs.",
            bestTime = "From true dawn until right before sunrise.",
            hadithVirtue = "The Prophet ﷺ said: 'The two Sunnah Rak'ahs of Fajr are better than this whole world and everything in it.' (Sahih Muslim)",
            steps = getPrayerStepsFor("Fajr", 2),
            azan = PrayerAzanData(
                prayerName = "Fajr",
                isFajr = true,
                audioId = "azan_fajr",
                phrases = FAJR_AZAN_PHRASES,
                duaAfterAzanArabic = DUA_AFTER_AZAN_AR,
                duaAfterAzanTransliteration = DUA_AFTER_AZAN_TR,
                duaAfterAzanTranslation = DUA_AFTER_AZAN_EN
            )
        ),
        DetailedPrayerPlan(
            id = "dhuhr",
            name = "Dhuhr (Noon Prayer)",
            arabicTitle = "صَلَاةُ الظُّهْرِ",
            rakatsSummary = "4 Sunnah + 4 Fard + 2 Sunnah + 2 Nafl",
            totalRakats = 12,
            recitationRule = "Recite all Surahs and Ayats quietly to yourself (Sirri) throughout all 4 Fard Rak'ahs.",
            bestTime = "From the sun passing its zenith until an object's shadow equals its length.",
            hadithVirtue = "Whoever observes the four Rak'ahs before Dhuhr and four after it, Allah will forbid the Fire for him. (Sunan Abu Dawood)",
            steps = getPrayerStepsFor("Dhuhr", 4),
            azan = PrayerAzanData(
                prayerName = "Dhuhr",
                isFajr = false,
                audioId = "azan_standard",
                phrases = STANDARD_AZAN_PHRASES,
                duaAfterAzanArabic = DUA_AFTER_AZAN_AR,
                duaAfterAzanTransliteration = DUA_AFTER_AZAN_TR,
                duaAfterAzanTranslation = DUA_AFTER_AZAN_EN
            )
        ),
        DetailedPrayerPlan(
            id = "asr",
            name = "Asr (Afternoon Prayer)",
            arabicTitle = "صَلَاةُ العَصْرِ",
            rakatsSummary = "4 Sunnah Ghayr Mu'akkadah + 4 Fard",
            totalRakats = 8,
            recitationRule = "Silent recitation (Sirri) in all Rak'ahs.",
            bestTime = "From mid-afternoon until the sun begins to turn pale/orange before sunset.",
            hadithVirtue = "He who observes the two cool prayers (Fajr and Asr) will enter Paradise. (Sahih al-Bukhari)",
            steps = getPrayerStepsFor("Asr", 4),
            azan = PrayerAzanData(
                prayerName = "Asr",
                isFajr = false,
                audioId = "azan_standard",
                phrases = STANDARD_AZAN_PHRASES,
                duaAfterAzanArabic = DUA_AFTER_AZAN_AR,
                duaAfterAzanTransliteration = DUA_AFTER_AZAN_TR,
                duaAfterAzanTranslation = DUA_AFTER_AZAN_EN
            )
        ),
        DetailedPrayerPlan(
            id = "maghrib",
            name = "Maghrib (Sunset Prayer)",
            arabicTitle = "صَلَاةُ المَغْرِبِ",
            rakatsSummary = "3 Fard + 2 Sunnah + 2 Nafl",
            totalRakats = 7,
            recitationRule = "Loud recitation (Jahri) in the first 2 Fard Rak'ahs; silent in the 3rd Rak'ah.",
            bestTime = "Immediately upon complete sunset until the twilight red glow disappears.",
            hadithVirtue = "My Ummah will continue to be upon good as long as they do not delay Maghrib until the stars appear. (Sunan Abu Dawood)",
            steps = getPrayerStepsFor("Maghrib", 3),
            azan = PrayerAzanData(
                prayerName = "Maghrib",
                isFajr = false,
                audioId = "azan_standard",
                phrases = STANDARD_AZAN_PHRASES,
                duaAfterAzanArabic = DUA_AFTER_AZAN_AR,
                duaAfterAzanTransliteration = DUA_AFTER_AZAN_TR,
                duaAfterAzanTranslation = DUA_AFTER_AZAN_EN
            )
        ),
        DetailedPrayerPlan(
            id = "isha",
            name = "Isha (Night Prayer)",
            arabicTitle = "صَلَاةُ العِشَاءِ",
            rakatsSummary = "4 Sunnah + 4 Fard + 2 Sunnah + 2 Nafl + 3 Witr",
            totalRakats = 17,
            recitationRule = "Loud recitation (Jahri) in the first 2 Fard Rak'ahs; silent in the 3rd and 4th Rak'ahs.",
            bestTime = "From total disappearance of twilight until the first third or middle of the night.",
            hadithVirtue = "One who prayed Isha in congregation is like one who stayed up half the night in prayer. (Sahih Muslim)",
            steps = getPrayerStepsFor("Isha", 4),
            azan = PrayerAzanData(
                prayerName = "Isha",
                isFajr = false,
                audioId = "azan_standard",
                phrases = STANDARD_AZAN_PHRASES,
                duaAfterAzanArabic = DUA_AFTER_AZAN_AR,
                duaAfterAzanTransliteration = DUA_AFTER_AZAN_TR,
                duaAfterAzanTranslation = DUA_AFTER_AZAN_EN
            )
        )
    )

    private fun getPrayerStepsFor(prayerName: String, fardCount: Int): List<PrayerProcessStep> {
        return listOf(
            PrayerProcessStep(
                stepIndex = 1,
                title = "Niyyah (Heart's Intention)",
                arabicName = "النيّة",
                posture = "Standing upright facing Qibla",
                arabicRecitation = "نَوَيْتُ أَنْ أُصَلِّيَ فَرْضَ صَلَاةِ $prayerName لِلَّهِ تَعَالَى مُتَوَجِّهًا إِلَى الْكَعْبَةِ",
                transliteration = "Nawaytu an usalliya farda salatil-$prayerName lillahi ta'ala mutawajjihan ilal-Ka'bah",
                englishTranslation = "I intend to offer the $fardCount Fard Rak'ahs of $prayerName prayer for Allah the Almighty, facing the Kaaba.",
                repetitionGuide = "Formed in heart before opening Takbir",
                audioId = "fatiha",
                notes = "Stand facing the Qibla with dignity and humility. Ensure valid Wudu before beginning."
            ),
            PrayerProcessStep(
                stepIndex = 2,
                title = "Takbeerat al-Ihram",
                arabicName = "تكبيرة الإحرام",
                posture = "Raising both hands to earlobes / shoulders",
                arabicRecitation = "اللَّهُ أَكْبَرُ",
                transliteration = "Allahu Akbar",
                englishTranslation = "Allah is the Greatest!",
                repetitionGuide = "1x to enter prayer",
                audioId = "fatiha",
                notes = "With hands raised and fingers naturally spread, pronounce the Takbir to enter the sacred state of Salah."
            ),
            PrayerProcessStep(
                stepIndex = 3,
                title = "Qiyam & Thana (Opening Supplication)",
                arabicName = "دعاء الاستفتاح",
                posture = "Hands folded right over left on chest",
                arabicRecitation = "سُبْحَانَكَ اللَّهُمَّ وَبِحَمْدِكَ، وَتَبَارَكَ اسْمُكَ، وَتَعَالَىٰ جَدُّكَ، وَلَا إِلَٰهَ غَيْرُكَ",
                transliteration = "Subhanak-Allahumma wa bihamdika, wa tabarakasmuka, wa ta'ala jadduka, wa la ilaha ghayruk.",
                englishTranslation = "Glory be to You, O Allah, and all praise. Blessed is Your Name, exalted is Your Majesty, and there is no God besides You.",
                repetitionGuide = "1x in the 1st Rak'ah only",
                audioId = "fatiha",
                notes = "Recited quietly in the first Rak'ah before Surah Al-Fatiha."
            ),
            PrayerProcessStep(
                stepIndex = 4,
                title = "Surah Al-Fatiha (The Opening)",
                arabicName = "سورة الفاتحة",
                posture = "Standing (Qiyam)",
                arabicRecitation = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ ۝ الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ ۝ الرَّحْمَٰنِ الرَّحِيمِ ۝ مَالِكِ يَوْمِ الدِّينِ ۝ إِيَّاكَ نَعْبُدُ وَإِيَّاكَ نَسْتَعِينُ ۝ اهْدِنَا الصِّرَاطَ الْمُسْتَقِيمَ ۝ صِرَاطَ الَّذِينَ أَنْعَمْتَ عَلَيْهِمْ غَيْرِ الْمَغْضُوبِ عَلَيْهِمْ وَلَا الضَّالِّينَ",
                transliteration = "Bismillahir-Rahmanir-Rahim. Alhamdu lillahi Rabbil-'alamin. Ar-Rahmanir-Rahim. Maliki Yawmid-Din. Iyyaka na'budu wa iyyaka nasta'in. Ihdinas-siratal-mustaqim. Siratalladhina an'amta 'alayhim, ghayril-maghdubi 'alayhim walad-dallin. Amin.",
                englishTranslation = "In the name of Allah, the Entirely Merciful, the Especially Merciful. All praise is due to Allah, Lord of the worlds. The Entirely Merciful, the Especially Merciful. Sovereign of the Day of Recompense. It is You we worship and You we ask for help. Guide us to the straight path. The path of those upon whom You have bestowed favor, not of those who have evoked anger or of those who are astray. Ameen.",
                repetitionGuide = "Pillar of every single Rak'ah",
                audioId = "fatiha",
                notes = "Essential pillar. Without Surah Al-Fatiha, the prayer is invalid. Recited in every Rak'ah."
            ),
            PrayerProcessStep(
                stepIndex = 5,
                title = "Quranic Ayat (Surah Al-Ikhlas)",
                arabicName = "سورة الإخلاص",
                posture = "Standing (Qiyam)",
                arabicRecitation = "قُلْ هُوَ اللَّهُ أَحَدٌ ۝ اللَّهُ الصَّمَدُ ۝ لَمْ يَلِدْ وَلَمْ يُولَدْ ۝ وَلَمْ يَكُن لَّهُ كُفُوًا أَحَدٌ",
                transliteration = "Qul Huwal-lahu Ahad. Allahus-Samad. Lam yalid wa lam yulad. Wa lam yakun lahu kufuwan ahad.",
                englishTranslation = "Say: He is Allah, [who is] One. Allah, the Eternal Refuge. He neither begets nor is born, nor is there to Him any equivalent.",
                repetitionGuide = "Recited in Rak'ahs 1 & 2 after Fatiha",
                audioId = "ikhlas",
                notes = "Equal to one third of the Holy Quran in spiritual virtue and reward."
            ),
            PrayerProcessStep(
                stepIndex = 6,
                title = "Ruku (Bowing before Allah)",
                arabicName = "الرُّكُوع",
                posture = "Bowing with back flat, hands on knees",
                arabicRecitation = "سُبْحَانَ رَبِّيَ الْعَظِيمِ",
                transliteration = "Subhana Rabbiyal-'Azeem",
                englishTranslation = "Glory be to my Lord, the Almighty!",
                repetitionGuide = "Recite at least 3x",
                audioId = "fatiha",
                notes = "Keep your spine straight, fingers spread over your knees, and eyes looking at the spot of prostration."
            ),
            PrayerProcessStep(
                stepIndex = 7,
                title = "Qawmah (Rising from Bowing)",
                arabicName = "القَوْمَة",
                posture = "Standing completely erect",
                arabicRecitation = "سَمِعَ اللَّهُ لِمَنْ حَمِدَهُ ۝ رَبَّنَا وَلَكَ الْحَمْدُ، حَمْدًا كَثِيرًا طَيِّبًا مُبَارَكًا فِيهِ",
                transliteration = "Sami' Allahu liman hamidah. Rabbana wa lakal-hamd, hamdan kathiran tayyiban mubarakan feeh.",
                englishTranslation = "Allah hears whoever praises Him. Our Lord, and to You belongs all praise, abundant, pure, and blessed praise.",
                repetitionGuide = "1x while rising & standing",
                audioId = "fatiha",
                notes = "Stand completely straight and pause with tranquility before descending to prostration."
            ),
            PrayerProcessStep(
                stepIndex = 8,
                title = "Sujood (Prostration)",
                arabicName = "السُّجُود",
                posture = "7 limbs touching the ground (forehead+nose, palms, knees, toes)",
                arabicRecitation = "سُبْحَانَ رَبِّيَ الْأَعْلَىٰ",
                transliteration = "Subhana Rabbiyal-A'la",
                englishTranslation = "Glory be to my Lord, the Most High!",
                repetitionGuide = "Recite at least 3x in each Sujood (2 Sujood per Rak'ah)",
                audioId = "fatiha",
                notes = "The closest a servant comes to their Lord is while in prostration. Make heartfelt Duas here."
            ),
            PrayerProcessStep(
                stepIndex = 9,
                title = "Jalsah (Sitting between two Sujoods)",
                arabicName = "الجَلْسَة بَيْنَ السَّجْدَتَيْن",
                posture = "Sitting comfortably on left foot, right foot upright",
                arabicRecitation = "رَبِّ اغْفِرْ لِي، رَبِّ اغْفِرْ لِي، وَارْحَمْنِي، وَعَافِنِي، وَاهْدِنِي، وَارْزُقْنِي",
                transliteration = "Rabbighfir li, Rabbighfir li, warhamni, wa 'afini, wahdini, warzuqni.",
                englishTranslation = "O my Lord forgive me, O my Lord forgive me, have mercy on me, grant me well-being, guide me, and provide for me.",
                repetitionGuide = "1x to 3x between prostrations",
                audioId = "fatiha",
                notes = "Sit calmly with hands resting comfortably on thighs near the knees."
            ),
            PrayerProcessStep(
                stepIndex = 10,
                title = "Tashahhud (Attahiyyat)",
                arabicName = "التَّشَهُّد",
                posture = "Sitting in Qa'dah, index finger raised during testimony",
                arabicRecitation = "التَّحِيَّاتُ لِلَّهِ وَالصَّلَوَاتُ وَالطَّيِّبَاتُ، السَّلَامُ عَلَيْكَ أَيُّهَا النَّبِيُّ وَرَحْمَةُ اللَّهِ وَبَرَكَاتُهُ، السَّلَامُ عَلَيْنَا وَعَلَىٰ عِبَادِ اللَّهِ الصَّالِحِينَ، أَشْهَدُ أَنْ لَا إِلَٰهَ إِلَّا اللَّهُ وَأَشْهَدُ أَنَّ مُحَمَّدًا عَبْدُهُ وَرَسُولُهُ",
                transliteration = "At-tahiyyatu lillahi was-salawatu wat-tayyibat. As-salamu 'alayka ayyuhan-Nabiyyu wa rahmatullahi wa barakatuh. As-salamu 'alayna wa 'ala 'ibadillahis-salihin. Ash-hadu an la ilaha illallah, wa ash-hadu anna Muhammadan 'abduhu wa rasuluh.",
                englishTranslation = "All compliments, prayers and pure words are due to Allah. Peace be upon you, O Prophet, and the mercy of Allah and His blessings. Peace be upon us and upon the righteous servants of Allah. I bear witness that there is no deity worthy of worship except Allah, and I bear witness that Muhammad is His servant and messenger.",
                repetitionGuide = "Recited in sitting after 2nd Rak'ah and final Rak'ah",
                audioId = "fatiha",
                notes = "Raise your right index finger when saying 'Ash-hadu an la ilaha illallah' to affirm the oneness of Allah."
            ),
            PrayerProcessStep(
                stepIndex = 11,
                title = "Salawat (Durood Ibrahim)",
                arabicName = "الصَّلَاةُ الإِبْرَاهِيمِيَّة",
                posture = "Sitting in the final Qa'dah",
                arabicRecitation = "اللَّهُمَّ صَلِّ عَلَىٰ مُحَمَّدٍ وَعَلَىٰ آلِ مُحَمَّدٍ كَمَا صَلَّيْتَ عَلَىٰ إِبْرَاهِيمَ وَعَلَىٰ آلِ إِبْرَاهِيمَ إِنَّكَ حَمِيدٌ مَجِيدٌ، اللَّهُمَّ بَارِكْ عَلَىٰ مُحَمَّدٍ وَعَلَىٰ آلِ مُحَمَّدٍ كَمَا بَارَكْتَ عَلَىٰ إِبْرَاهِيمَ وَعَلَىٰ آلِ إِبْرَاهِيمَ إِنَّكَ حَمِيدٌ مَجِيدٌ",
                transliteration = "Allahumma salli 'ala Muhammadin wa 'ala ali Muhammad, kama sallayta 'ala Ibrahima wa 'ala ali Ibrahim, innaka Hamidun Majid. Allahumma barik 'ala Muhammadin wa 'ala ali Muhammad, kama barakta 'ala Ibrahima wa 'ala ali Ibrahim, innaka Hamidun Majid.",
                englishTranslation = "O Allah, send peace upon Muhammad and upon the family of Muhammad, as You sent peace upon Ibrahim and upon the family of Ibrahim. Indeed, You are Praiseworthy, Majestic. O Allah, bless Muhammad and the family of Muhammad, as You blessed Ibrahim and the family of Ibrahim. Indeed, You are Praiseworthy, Majestic.",
                repetitionGuide = "Recited in the final sitting before Tasleem",
                audioId = "fatiha",
                notes = "Sending blessings upon the Prophet ﷺ in the final sitting is emphasized Sunnah."
            ),
            PrayerProcessStep(
                stepIndex = 12,
                title = "Concluding Dua & Tasleem (Salam)",
                arabicName = "الدعاء والتَّسْلِيم",
                posture = "Turning head to right, then to left",
                arabicRecitation = "رَبَّنَا آتِنَا فِي الدُّنْيَا حَسَنَةً وَفِي الْآخِرَةِ حَسَنَةً وَقِنَا عَذَابَ النَّارِ ۝ السَّلَامُ عَلَيْكُمْ وَرَحْمَةُ اللَّهِ",
                transliteration = "Rabbana atina fid-dunya hasanah wa fil-akhirati hasanah wa qina 'adhaban-nar. As-Salamu 'alaykum wa Rahmatullah.",
                englishTranslation = "Our Lord, grant us good in this world and good in the Hereafter, and protect us from the punishment of the Fire. Peace and the mercy of Allah be upon you.",
                repetitionGuide = "Turn right: Salam 1x; Turn left: Salam 1x",
                audioId = "fatiha",
                notes = "Turn your face to the right shoulder uttering the first Salam, then to the left shoulder uttering the second Salam."
            )
        )
    }
}
