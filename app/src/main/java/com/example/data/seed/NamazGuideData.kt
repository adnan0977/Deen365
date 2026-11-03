package com.example.data.seed

data class NamazStep(
    val id: String,
    val stepNumber: Int,
    val title: String,
    val postureName: String,
    val postureCategory: String, // "standing", "bowing", "rising", "prostration", "sitting", "turning"
    val arabicText: String,
    val transliteration: String,
    val translation: String,
    val explanation: String,
    val repetition: String = "1x",
    val ruling: String = "Pillar (Rukn)", // "Pillar (Rukn)", "Obligatory (Wajib)", "Sunnah"
    val spiritualBenefit: String,
    val commonMistakes: String
)

data class PrayerGuide(
    val id: String,
    val name: String,
    val arabicName: String,
    val totalRakats: Int,
    val breakdown: String,
    val timeDescription: String,
    val overview: String,
    val stepIds: List<String>
)

object NamazGuideData {

    val STEPS = listOf(
        NamazStep(
            id = "salah_niyyah",
            stepNumber = 1,
            title = "Niyyah (Intention) & Wudu",
            postureName = "Standing (Qiyam)",
            postureCategory = "standing",
            arabicText = "نَوَيْتُ أَنْ أُصَلِّيَ لِلَّهِ تَعَالَى رَكْعَتَيْنِ صَلَاةَ الفَجْرِ مُتَوَجِّهًا إِلَى جِهَةِ الْكَعْبَةِ",
            transliteration = "Nawaytu an usalliya lillahi ta'ala rak'atayn salatal-Fajri mutawajjihan ila jihatil-Ka'bah",
            translation = "I intend to pray two Rak'ahs of Fajr prayer for the sake of Allah the Almighty, facing towards the Kaaba.",
            explanation = "Ensure you are in a state of ritual purity (Wudu), clothes and area clean, facing the Qibla. Intention is formed in the heart. Focus your eyes gently on the spot where your forehead will touch during prostration.",
            repetition = "Once at the start",
            ruling = "Pillar (Rukn) - Heart Intention",
            spiritualBenefit = "Detaches the heart from worldly preoccupations and consecrates your entire state to Allah alone.",
            commonMistakes = "Saying the intention out loud in a loud voice that disturbs fellow worshippers."
        ),
        NamazStep(
            id = "salah_takbir",
            stepNumber = 2,
            title = "Takbirat al-Ihram (Commencing Prayer)",
            postureName = "Raising Hands to Ears / Shoulders",
            postureCategory = "standing",
            arabicText = "اللَّهُ أَكْبَرُ",
            transliteration = "Allahu Akbar",
            translation = "Allah is the Greatest",
            explanation = "Raise both hands until thumb tips touch the earlobes (or hand level with shoulders), fingers naturally spread facing the Qibla, uttering 'Allahu Akbar'. This marks the absolute beginning of Salah; from this moment worldly speaking, eating, and looking away become forbidden.",
            repetition = "1x",
            ruling = "Pillar (Rukn)",
            spiritualBenefit = "Declares that the Creator is greater than every anxiety, ambition, and distraction in existence.",
            commonMistakes = "Rushing the Takbir before raising hands or tossing hands down carelessly."
        ),
        NamazStep(
            id = "salah_thana",
            stepNumber = 3,
            title = "Thana (Opening Glorification - Al-Istiftah)",
            postureName = "Standing with Folded Hands (Qiyam)",
            postureCategory = "standing",
            arabicText = "سُبْحَانَكَ اللَّهُمَّ وَبِحَمْدِكَ، وَتَبَارَكَ اسْمُكَ، وَتَعَالَىٰ جَدُّكَ، وَلَا إِلَٰهَ غَيْرُكَ",
            transliteration = "Subhanaka Allahumma wa bihamdika, wa tabarakasmuka, wa ta'ala jadduka, wa la ilaha ghayruka",
            translation = "Glory be to You, O Allah, and all praise. Blessed is Your Name, exalted is Your Majesty, and there is no deity worthy of worship besides You.",
            explanation = "Fold your right hand firmly over your left hand on your chest (or above the navel). Recite this opening supplication quietly to yourself during the first Rak'ah.",
            repetition = "1x (First Rak'ah only)",
            ruling = "Sunnah Mu'akkadah",
            spiritualBenefit = "Calms the inner self with profound glorification before dialoguing with Allah through the Quran.",
            commonMistakes = "Reciting Thana in the second or third Rak'ahs where it is not prescribed."
        ),
        NamazStep(
            id = "salah_taawwudh",
            stepNumber = 4,
            title = "Ta'awwudh & Tasmiyah (Seeking Refuge)",
            postureName = "Standing (Qiyam)",
            postureCategory = "standing",
            arabicText = "أَعُوذُ بِاللَّهِ مِنَ الشَّيْطَانِ الرَّجِيمِ • بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
            transliteration = "A'udhu billahi minash-shaytanir-rajim. Bismillahir-Rahmanir-Rahim",
            translation = "I seek refuge in Allah from Satan the outcast. In the Name of Allah, the Entirely Merciful, the Especially Merciful.",
            explanation = "Silently recite the seeking of refuge from Satan's whispering (Waswas) and invoke Allah's mercy before reciting the divine words of the Quran.",
            repetition = "1x",
            ruling = "Sunnah",
            spiritualBenefit = "Builds an invincible shield against doubt, wandering thoughts, and spiritual laziness.",
            commonMistakes = "Neglecting to seek refuge, allowing mind wanderings to compromise presence."
        ),
        NamazStep(
            id = "salah_fatiha",
            stepNumber = 5,
            title = "Surah Al-Fatiha (The Opening)",
            postureName = "Standing (Qiyam)",
            postureCategory = "standing",
            arabicText = "الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ • الرَّحْمَٰنِ الرَّحِيمِ • مَالِكِ يَوْمِ الدِّينِ • إِيَّاكَ نَعْبُدُ وَإِيَّاكَ نَسْتَعِينُ • اهْدِنَا الصِّرَاطَ الْمُسْتَقِيمَ • صِرَاطَ الَّذِينَ أَنْعَمْتَ عَلَيْهِمْ غَيْرِ الْمَغْضُوبِ عَلَيْهِمْ وَلَا الضَّالِّينَ",
            transliteration = "Al-hamdu lillahi Rabbil-'alamin. Ar-Rahmanir-Rahim. Maliki Yawmid-Deen. Iyyaka na'budu wa iyyaka nasta'een. Ihdinas-siratal-mustaqeem. Siratalladhina an'amta 'alayhim ghayril-maghdoobi 'alayhim wa lad-daalleen. Ameen.",
            translation = "[All] praise is [due] to Allah, Lord of the worlds. The Entirely Merciful, the Especially Merciful. Sovereign of the Day of Recompense. It is You we worship and You we ask for help. Guide us to the straight path - The path of those upon whom You have bestowed favor, not of those who have evoked [Your] anger or of those who are astray. Ameen.",
            explanation = "The essential pillar of every Rak'ah. The Prophet (pbuh) said: 'There is no prayer for the one who does not recite the Opening of the Book.' Pause between each verse; Allah responds to each phrase directly according to Hadith Qudsi.",
            repetition = "Every Rak'ah",
            ruling = "Pillar (Rukn)",
            spiritualBenefit = "Direct dialogue between servant and Creator summarizing the entirety of Quranic theology.",
            commonMistakes = "Reciting so quickly that words blur together without pause or comprehension."
        ),
        NamazStep(
            id = "salah_surah",
            stepNumber = 6,
            title = "Additional Surah (e.g. Surah Al-Ikhlas)",
            postureName = "Standing (Qiyam)",
            postureCategory = "standing",
            arabicText = "قُلْ هُوَ اللَّهُ أَحَدٌ • اللَّهُ الصَّمَدُ • لَمْ يَلِدْ وَلَمْ يُولَدْ • وَلَمْ يَكُن لَّهُ كُفُوًا أَحَدٌ",
            transliteration = "Qul Huwa Allahu Ahad. Allahus-Samad. Lam yalid wa lam yoolad. Wa lam yakul-lahu kufuwan ahad.",
            translation = "Say, 'He is Allah, [who is] One, Allah, the Eternal Refuge. He neither begets nor is born, Nor is there to Him any equivalent.'",
            explanation = "Recite at least 3 short verses or one complete Surah of the Quran during the first two Rak'ahs. Surah Al-Ikhlas equates to one-third of the Quran in reward.",
            repetition = "Rak'ah 1 and 2",
            ruling = "Sunnah Mu'akkadah",
            spiritualBenefit = "Contemplating the timeless revelation and divine majesty of the Quran.",
            commonMistakes = "Adding an extra Surah in the 3rd and 4th Rak'ahs of Fard prayers."
        ),
        NamazStep(
            id = "salah_ruku",
            stepNumber = 7,
            title = "Ruku' (Bowing Before Allah)",
            postureName = "Bowing with Hands on Knees",
            postureCategory = "bowing",
            arabicText = "سُبْحَانَ رَبِّيَ الْعَظِيمِ",
            transliteration = "Subhana Rabbiyal-'Adheem",
            translation = "Glory be to my Lord, the Magnificent.",
            explanation = "Say 'Allahu Akbar' while bowing. Place your palms firmly upon your kneecaps with fingers spread apart. Keep your back straight horizontal to the ground, head neither drooped nor raised. Remain still in tranquil pause.",
            repetition = "Recite 3 times (odd numbers recommended)",
            ruling = "Pillar (Rukn)",
            spiritualBenefit = "Physical submission humbling human arrogance before the Majesty of the Creator.",
            commonMistakes = "Arching the back, bending knees too much, or not staying still for at least one breath."
        ),
        NamazStep(
            id = "salah_qawmah",
            stepNumber = 8,
            title = "Qawmah / I'tidal (Rising from Bowing)",
            postureName = "Standing Upright with Composure",
            postureCategory = "rising",
            arabicText = "سَمِعَ اللَّهُ لِمَنْ حَمِدَهُ • رَبَّنَا وَلَكَ الْحَمْدُ، حَمْدًا كَثِيرًا طَيِّبًا مُبَارَكًا فِيهِ",
            transliteration = "Sami'a Allahu liman hamidah. Rabbana wa lakal-hamd, hamdan katheeran tayyiban mubarakan feeh.",
            translation = "Allah listens to the one who praises Him. Our Lord, and to You belongs all praise, abundant, pure, and blessed praise.",
            explanation = "Rise smoothly from Ruku' with arms returning to sides (or folded). Stand completely upright until every bone returns to its resting place (Tumani'nah).",
            repetition = "1x",
            ruling = "Pillar (Rukn)",
            spiritualBenefit = "Reaffirming that gratitude to Allah is immediately answered and accepted.",
            commonMistakes = "Diving immediately into prostration without achieving complete upright stillness."
        ),
        NamazStep(
            id = "salah_sujud1",
            stepNumber = 9,
            title = "First Sujud (Prostration - Closest to Allah)",
            postureName = "Prostrating on Seven Limbs",
            postureCategory = "prostration",
            arabicText = "سُبْحَانَ رَبِّيَ الأَعْلَى",
            transliteration = "Subhana Rabbiyal-A'la",
            translation = "Glory be to my Lord, the Most High.",
            explanation = "Say 'Allahu Akbar' and go down into prostration. Seven body parts MUST firmly touch the ground: forehead & nose together, both palms, both knees, and the toes of both feet bent facing Qibla. Keep elbows elevated and away from sides.",
            repetition = "Recite 3 times",
            ruling = "Pillar (Rukn)",
            spiritualBenefit = "The zenith of worship: placing the highest part of the body on the lowest ground in utter surrender.",
            commonMistakes = "Lifting feet off the ground or resting forearms flat like a dog (which is forbidden)."
        ),
        NamazStep(
            id = "salah_jalsah",
            stepNumber = 10,
            title = "Jalsah (Sitting Between Two Prostrations)",
            postureName = "Sitting Upright on Left Foot",
            postureCategory = "sitting",
            arabicText = "رَبِّ اغْفِرْ لِي، رَبِّ اغْفِرْ لِي، وَارْحَمْنِي، وَعَافِنِي، وَاهْدِنِي، وَارْزُقْنِي",
            transliteration = "Rabbighfir li, Rabbighfir li, warhamni, wa 'afini, wahdini, warzuqni",
            translation = "O Lord forgive me, O Lord forgive me, have mercy on me, grant me well-being, guide me, and provide for me.",
            explanation = "Say 'Allahu Akbar' and sit up completely. Sit on your left foot with the right foot upright, toes pointing toward Qibla (Iftirash). Hands resting flat on thighs near the knees. Pause calmly.",
            repetition = "1x with calm pause",
            ruling = "Pillar (Rukn)",
            spiritualBenefit = "A peaceful moment of personal supplication asking for core spiritual and worldly essentials.",
            commonMistakes = "Barely lifting the head from the floor before bouncing into the second Sujud."
        ),
        NamazStep(
            id = "salah_sujud2",
            stepNumber = 11,
            title = "Second Sujud (Completing the Rak'ah)",
            postureName = "Prostrating on Seven Limbs",
            postureCategory = "prostration",
            arabicText = "سُبْحَانَ رَبِّيَ الأَعْلَى",
            transliteration = "Subhana Rabbiyal-A'la",
            translation = "Glory be to my Lord, the Most High.",
            explanation = "Say 'Allahu Akbar' and prostrate a second time in the exact same manner on 7 limbs, reciting the glorification three times. This completes one full Rak'ah (unit) of prayer.",
            repetition = "Recite 3 times",
            ruling = "Pillar (Rukn)",
            spiritualBenefit = "Doubling devotion; reinforcing humility and seeking closeness to the Divine.",
            commonMistakes = "Forgetting to point toes toward the Qibla."
        ),
        NamazStep(
            id = "salah_tashahhud",
            stepNumber = 12,
            title = "Tashahhud (Sitting for Witness of Faith)",
            postureName = "Sitting with Index Finger Raised",
            postureCategory = "sitting",
            arabicText = "التَّحِيَّاتُ لِلَّهِ وَالصَّلَوَاتُ وَالطَّيِّبَاتُ، السَّلاَمُ عَلَيْكَ أَيُّهَا النَّبِيُّ وَرَحْمَةُ اللَّهِ وَبَرَكَاتُهُ، السَّلاَمُ عَلَيْنَا وَعَلَىٰ عِبَادِ اللَّهِ الصَّالِحِينَ، أَشْهَدُ أَنْ لَا إِلَٰهَ إِلَّا اللَّهُ وَأَشْهَدُ أَنَّ مُحَمَّدًا عَبْدُهُ وَرَسُولُهُ",
            transliteration = "At-tahiyyatu lillahi was-salawatu wat-tayyibat. As-salamu 'alayka ayyuhan-Nabiyyu wa rahmatullahi wa barakatuh. As-salamu 'alayna wa 'ala 'ibadillahis-saliheen. Ash-hadu alla ilaha illallah, wa ash-hadu anna Muhammadan 'abduhu wa rasooluh.",
            translation = "All compliments, prayers, and pure words are due to Allah. Peace be upon you, O Prophet, and the mercy of Allah and His blessings. Peace be upon us and upon the righteous servants of Allah. I bear witness that there is no god but Allah, and I bear witness that Muhammad is His servant and messenger.",
            explanation = "Sitting after the 2nd Rak'ah and at the final sitting. Left hand on left knee; right hand in loose fist on right thigh. Raise the right index finger when reciting the Shahada ('Ash-hadu alla ilaha illallah') pointing toward Qibla to affirm Tawhid.",
            repetition = "At the end of Rak'ah 2 and final Rak'ah",
            ruling = "Obligatory (Wajib) / Pillar (Rukn)",
            spiritualBenefit = "Commemorates the celestial conversation of the Mi'raj (Ascension) and renews covenant of faith.",
            commonMistakes = "Leaving the hand limp or vigorously waving the finger inappropriately."
        ),
        NamazStep(
            id = "salah_durood",
            stepNumber = 13,
            title = "Durood Ibrahim (Salawat Upon the Prophet)",
            postureName = "Sitting in Final Tashahhud",
            postureCategory = "sitting",
            arabicText = "اللَّهُمَّ صَلِّ عَلَىٰ مُحَمَّدٍ وَعَلَىٰ آلِ مُحَمَّدٍ كَمَا صَلَّيْتَ عَلَىٰ إِبْرَاهِيمَ وَعَلَىٰ آلِ إِبْرَاهِيمَ إِنَّكَ حَمِيدٌ مَجِيدٌ، اللَّهُمَّ بَارِكْ عَلَىٰ مُحَمَّدٍ وَعَلَىٰ آلِ مُحَمَّدٍ كَمَا بَارَكْتَ عَلَىٰ إِبْرَاهِيمَ وَعَلَىٰ آلِ إِبْرَاهِيمَ إِنَّكَ حَمِيدٌ مَجِيدٌ",
            transliteration = "Allahumma salli 'ala Muhammadin wa 'ala aali Muhammadin kama sallayta 'ala Ibraheema wa 'ala aali Ibraheem, innaka Hameedum Majeed. Allahumma barik 'ala Muhammadin wa 'ala aali Muhammadin kama barakta 'ala Ibraheema wa 'ala aali Ibraheem, innaka Hameedum Majeed.",
            translation = "O Allah, send prayers upon Muhammad and upon the family of Muhammad, as You sent prayers upon Abraham and the family of Abraham; indeed You are Praiseworthy and Glorious. O Allah, bless Muhammad and the family of Muhammad, as You blessed Abraham and the family of Abraham; indeed You are Praiseworthy and Glorious.",
            explanation = "Recited in the final sitting immediately following Tashahhud. It is the greatest invocation of peace and blessings upon the beloved Prophet Muhammad (pbuh) and his spiritual father Abraham (as).",
            repetition = "Final sitting of every prayer",
            ruling = "Sunnah Mu'akkadah / Pillar in Shafi'i",
            spiritualBenefit = "Connects the worshipper to the pure prophetic lineage and grants ten blessings for every salawat.",
            commonMistakes = "Skipping Durood in the final sitting."
        ),
        NamazStep(
            id = "salah_rabbana",
            stepNumber = 14,
            title = "Final Supplication Before Salam",
            postureName = "Sitting in Final Tashahhud",
            postureCategory = "sitting",
            arabicText = "رَبَّنَا آتِنَا فِي الدُّنْيَا حَسَنَةً وَفِي الآخِرَةِ حَسَنَةً وَقِنَا عَذَابَ النَّارِ",
            transliteration = "Rabbana atina fid-dunya hasanah wa fil-akhirati hasanah wa qina 'adhaban-nar",
            translation = "Our Lord, give us in this world [that which is] good and in the Hereafter [that which is] good and protect us from the punishment of the Fire.",
            explanation = "Before the final Salam, you may make heartfelt supplication using Quranic or authentic Prophetic words. This moment just before concluding Salah is highly answered.",
            repetition = "1x before Salam",
            ruling = "Sunnah Mustahabbah",
            spiritualBenefit = "Balances prayers for worldly flourishing, family well-being, and ultimate salvation in the Hereafter.",
            commonMistakes = "Rushing to end the prayer without taking advantage of this golden moment of Dua."
        ),
        NamazStep(
            id = "salah_taslim",
            stepNumber = 15,
            title = "Taslim / As-Salam (Concluding the Prayer)",
            postureName = "Turning Head to Right then Left",
            postureCategory = "turning",
            arabicText = "السَّلاَمُ عَلَيْكُمْ وَرَحْمَةُ اللَّهِ",
            transliteration = "Assalamu 'alaykum wa rahmatullah (Right, then Left)",
            translation = "May the peace and mercy of Allah be upon you.",
            explanation = "Turn your face to the right until the cheek is visible from behind, uttering 'Assalamu 'alaykum wa rahmatullah'. Then turn to the left, repeating the same greeting. You greet the recording angels (Kiraman Katibin) and fellow Muslims.",
            repetition = "Twice (Right, then Left)",
            ruling = "Pillar (Rukn) - First Salam",
            spiritualBenefit = "Returning to worldly life spreading the tranquility, safety, and mercy received in prayer to all around you.",
            commonMistakes = "Nodding the head down like a bow instead of turning it smoothly from cheek to cheek."
        )
    )

    val PRAYERS = listOf(
        PrayerGuide(
            id = "fajr",
            name = "Fajr Prayer",
            arabicName = "صَلَاةُ الفَجْرِ",
            totalRakats = 2,
            breakdown = "2 Sunnah Mu'akkadah + 2 Fard",
            timeDescription = "From true dawn until right before sunrise",
            overview = "The dawn prayer is witnessed by the angels of the night and day. It instills light, barakah, and divine protection throughout the entire day.",
            stepIds = listOf("salah_niyyah", "salah_takbir", "salah_thana", "salah_taawwudh", "salah_fatiha", "salah_surah", "salah_ruku", "salah_qawmah", "salah_sujud1", "salah_jalsah", "salah_sujud2", "salah_tashahhud", "salah_durood", "salah_rabbana", "salah_taslim")
        ),
        PrayerGuide(
            id = "dhuhr",
            name = "Dhuhr Prayer",
            arabicName = "صَلَاةُ الظُّهْرِ",
            totalRakats = 4,
            breakdown = "4 Sunnah + 4 Fard + 2 Sunnah",
            timeDescription = "From when the sun passes its zenith (midday) until the shadow equals an object's height",
            overview = "The midday prayer brings peace during work and worldly busyness, reminding the believer of their eternal purpose in the middle of daily life.",
            stepIds = listOf("salah_niyyah", "salah_takbir", "salah_thana", "salah_taawwudh", "salah_fatiha", "salah_surah", "salah_ruku", "salah_qawmah", "salah_sujud1", "salah_jalsah", "salah_sujud2", "salah_tashahhud", "salah_durood", "salah_rabbana", "salah_taslim")
        ),
        PrayerGuide(
            id = "asr",
            name = "Asr Prayer",
            arabicName = "صَلَاةُ العَصْرِ",
            totalRakats = 4,
            breakdown = "4 Fard",
            timeDescription = "From the end of Dhuhr time until the sun turns pale orange before sunset",
            overview = "Known in the Quran as the 'Middle Prayer' (Salat al-Wusta). Guarding this prayer is given utmost emphasis in Hadith literature.",
            stepIds = listOf("salah_niyyah", "salah_takbir", "salah_thana", "salah_taawwudh", "salah_fatiha", "salah_surah", "salah_ruku", "salah_qawmah", "salah_sujud1", "salah_jalsah", "salah_sujud2", "salah_tashahhud", "salah_durood", "salah_rabbana", "salah_taslim")
        ),
        PrayerGuide(
            id = "maghrib",
            name = "Maghrib Prayer",
            arabicName = "صَلَاةُ المَغْرِبِ",
            totalRakats = 3,
            breakdown = "3 Fard + 2 Sunnah Mu'akkadah",
            timeDescription = "Immediately following the setting of the sun until the twilight glow disappears",
            overview = "The sunset prayer marks the end of daytime and the opening of Iftar during fasting. Contains 3 Fard units (first 2 with extra Surah, 3rd with Al-Fatiha only).",
            stepIds = listOf("salah_niyyah", "salah_takbir", "salah_thana", "salah_taawwudh", "salah_fatiha", "salah_surah", "salah_ruku", "salah_qawmah", "salah_sujud1", "salah_jalsah", "salah_sujud2", "salah_tashahhud", "salah_durood", "salah_rabbana", "salah_taslim")
        ),
        PrayerGuide(
            id = "isha",
            name = "Isha Prayer",
            arabicName = "صَلَاةُ العِشَاءِ",
            totalRakats = 4,
            breakdown = "4 Fard + 2 Sunnah + 3 Witr",
            timeDescription = "From the disappearance of the red evening twilight until midnight / dawn",
            overview = "The night prayer brings peace and forgiveness to conclude the day's record. Praying Isha in congregation brings the reward of praying half the night.",
            stepIds = listOf("salah_niyyah", "salah_takbir", "salah_thana", "salah_taawwudh", "salah_fatiha", "salah_surah", "salah_ruku", "salah_qawmah", "salah_sujud1", "salah_jalsah", "salah_sujud2", "salah_tashahhud", "salah_durood", "salah_rabbana", "salah_taslim")
        ),
        PrayerGuide(
            id = "witr",
            name = "Witr Prayer (with Qunut)",
            arabicName = "صَلَاةُ الوِتْرِ",
            totalRakats = 3,
            breakdown = "3 Rak'ahs (Odd number)",
            timeDescription = "After Isha up until the arrival of dawn (Fajr)",
            overview = "The Prophet (pbuh) said: 'Allah is One (Witr) and loves the odd number.' Highly emphasized Sunnah including the beautiful Qunut supplication in the final Rak'ah.",
            stepIds = listOf("salah_niyyah", "salah_takbir", "salah_thana", "salah_taawwudh", "salah_fatiha", "salah_surah", "salah_ruku", "salah_qawmah", "salah_sujud1", "salah_jalsah", "salah_sujud2", "salah_tashahhud", "salah_durood", "salah_rabbana", "salah_taslim")
        )
    )

    data class PrayerRakatSchedule(
        val prayerName: String,
        val arabicTitle: String,
        val sunnahBefore: String,
        val fard: String,
        val recitationMode: String, // Loud vs Silent
        val sunnahAfter: String,
        val naflWitr: String,
        val totalRakats: Int,
        val timingWindow: String,
        val spiritualSignificance: String
    )

    val RAKAT_SCHEDULE = listOf(
        PrayerRakatSchedule(
            prayerName = "Fajr",
            arabicTitle = "صَلَاةُ الفَجْرِ",
            sunnahBefore = "2 Sunnah Mu'akkadah (Emphasized)",
            fard = "2 Fard (Obligatory)",
            recitationMode = "Loud (Jahri) by Imam / Solo",
            sunnahAfter = "None (Prohibited until sunrise)",
            naflWitr = "-",
            totalRakats = 4,
            timingWindow = "From true dawn (Subh Sadiq) until the moment before sunrise.",
            spiritualSignificance = "The Prophet ﷺ said: 'The two Rak'ahs before Fajr are better than this world and everything in it.' (Muslim 725)."
        ),
        PrayerRakatSchedule(
            prayerName = "Dhuhr",
            arabicTitle = "صَلَاةُ الظُّهْرِ",
            sunnahBefore = "4 Sunnah Mu'akkadah",
            fard = "4 Fard (Obligatory)",
            recitationMode = "Silent (Sirri)",
            sunnahAfter = "2 Sunnah Mu'akkadah",
            naflWitr = "2 Nafl (Voluntary)",
            totalRakats = 12,
            timingWindow = "From when the sun declines past its meridian (Zawal) until an object's shadow equals its length.",
            spiritualSignificance = "The gates of heaven are opened at midday and the Prophet ﷺ loved for righteous deeds to ascend during this hour."
        ),
        PrayerRakatSchedule(
            prayerName = "Asr",
            arabicTitle = "صَلَاةُ العَصْرِ",
            sunnahBefore = "4 Sunnah Ghayr-Mu'akkadah",
            fard = "4 Fard (Obligatory)",
            recitationMode = "Silent (Sirri)",
            sunnahAfter = "None (Disliked until Maghrib)",
            naflWitr = "-",
            totalRakats = 8,
            timingWindow = "From the end of Dhuhr until the sun begins to turn pale/orange before sunset.",
            spiritualSignificance = "The 'Middle Prayer' (Salat al-Wusta). Leaving it brings severe loss of one's good deeds. Angels exchange shifts at this prayer."
        ),
        PrayerRakatSchedule(
            prayerName = "Maghrib",
            arabicTitle = "صَلَاةُ المَغْرِبِ",
            sunnahBefore = "2 Nafl (Recommended)",
            fard = "3 Fard (Obligatory)",
            recitationMode = "Loud in first 2 Rak'ahs, Silent in 3rd",
            sunnahAfter = "2 Sunnah Mu'akkadah",
            naflWitr = "2 Nafl (Awwabin)",
            totalRakats = 7,
            timingWindow = "Immediately from the moment the sun sets beneath the horizon until twilight fades.",
            spiritualSignificance = "Hasten to pray Maghrib promptly; it opens the Iftar hour of accepted supplications."
        ),
        PrayerRakatSchedule(
            prayerName = "Isha",
            arabicTitle = "صَلَاةُ العِشَاءِ",
            sunnahBefore = "4 Sunnah Ghayr-Mu'akkadah",
            fard = "4 Fard (Obligatory)",
            recitationMode = "Loud in first 2 Rak'ahs, Silent in last 2",
            sunnahAfter = "2 Sunnah Mu'akkadah",
            naflWitr = "3 Witr (Wajib/Sunnah) + 2 Nafl",
            totalRakats = 17,
            timingWindow = "From the disappearance of the evening red glow until Islamic midnight (halfway between sunset and dawn).",
            spiritualSignificance = "Praying Isha and Fajr in congregation equals spending the entire night in uninterrupted prayer."
        ),
        PrayerRakatSchedule(
            prayerName = "Jumu'ah (Friday)",
            arabicTitle = "صَلَاةُ الجُمُعَةِ",
            sunnahBefore = "4 Sunnah (Before Khutbah)",
            fard = "2 Fard (In Congregation with Imam)",
            recitationMode = "Loud (Jahri) by Imam",
            sunnahAfter = "4 Sunnah (or 2+2 in Masjid)",
            naflWitr = "2 Nafl",
            totalRakats = 14,
            timingWindow = "Replaces Dhuhr prayer on Fridays during Dhuhr time.",
            spiritualSignificance = "The best day on which the sun rises; sins between two Fridays are expiated for whoever attends attentively."
        )
    )

    data class ForbiddenPrayerTime(
        val title: String,
        val arabicTitle: String,
        val timeFrame: String,
        val explanation: String,
        val exemption: String
    )

    val FORBIDDEN_TIMES = listOf(
        ForbiddenPrayerTime(
            title = "Sunrise (Shuruq)",
            arabicTitle = "شُرُوقُ الشَّمْسِ",
            timeFrame = "From the moment the sun begins rising until it rises about a spear's height (~15–20 minutes after sunrise).",
            explanation = "The Prophet ﷺ forbade praying at sunrise because the sun rises between the two horns of Satan, and idolaters used to prostrate to it.",
            exemption = "Only missed Fajr of that morning if waking up late, or making up obligatory prayers according to some scholars."
        ),
        ForbiddenPrayerTime(
            title = "Exact Zenith / Midday (Istiwā')",
            arabicTitle = "اِسْتِوَاءُ الشَّمْسِ (عِنْدَ الزَّوَالِ)",
            timeFrame = "When the sun reaches its absolute zenith directly overhead, lasting roughly 5 to 10 minutes until it begins tilting westward (Zawal).",
            explanation = "At this exact moment, the gates of Hellfire are fiercely fueled (except on Friday according to Hadith).",
            exemption = "Voluntary prayers on Friday are permitted during this time while waiting for the Jumu'ah sermon."
        ),
        ForbiddenPrayerTime(
            title = "Sunset / Yellowing Sun (Ghurūb)",
            arabicTitle = "غُرُوبُ الشَّمْسِ",
            timeFrame = "From when the sun becomes pale yellow/orange in the late afternoon until it completely sets below the horizon.",
            explanation = "The hypocrite waits until the sun is between Satan's horns and pecks four quick prostrations. Voluntary prayers are strictly disliked.",
            exemption = "If you have not yet prayed the obligatory Asr of that day, you MUST pray it immediately even if the sun is setting."
        )
    )

    data class SujoodSahwCase(
        val category: String,
        val cause: String,
        val example: String,
        val solution: String,
        val timing: String // Before Salam vs After Salam
    )

    val SUJOOD_SAHW_CASES = listOf(
        SujoodSahwCase(
            category = "Addition (Ziyadah)",
            cause = "Unintentionally adding an extra bowing (Ruku'), prostration (Sujud), or an extra Rak'ah.",
            example = "Accidentally praying 5 Rak'ahs in Dhuhr instead of 4, or doing 3 prostrations in a single unit.",
            solution = "Complete the prayer, recite the final Tashahhud and Durood, make the Salam, then perform 2 prostrations of forgetfulness (Sujood as-Sahw) and conclude with a second Salam.",
            timing = "After Salam (Ba'd as-Salam)"
        ),
        SujoodSahwCase(
            category = "Omission (Nuqsan)",
            cause = "Unintentionally omitting an obligatory act (Wajib), such as the first Tashahhud in a 3 or 4 Rak'ah prayer.",
            example = "Standing up directly for the 3rd Rak'ah after the second Sujud of Rak'ah 2 without sitting for Tashahhud.",
            solution = "Do NOT sit back down once fully upright. Continue the prayer. Before reciting the final Salam, perform 2 prostrations of forgetfulness, then recite the Salam.",
            timing = "Before Salam (Qabl as-Salam)"
        ),
        SujoodSahwCase(
            category = "Doubt with Uncertainty (Shakk)",
            cause = "Being uncertain whether you have prayed 2 or 3 Rak'ahs, with no strong feeling either way.",
            example = "During Dhuhr, wondering whether you are in your second or third Rak'ah.",
            solution = "Build on certainty (the lower number: treat it as 2). Pray the remaining Rak'ahs, and perform 2 prostrations of forgetfulness BEFORE making Salam.",
            timing = "Before Salam (Qabl as-Salam)"
        ),
        SujoodSahwCase(
            category = "Doubt with Strong Inclination (Taharrī)",
            cause = "Being uncertain about the count, but having a prevailing, strong confidence towards one number.",
            example = "Thinking: 'I am 80% confident this is my 4th Rak'ah.'",
            solution = "Act upon your prevailing inclination, complete the prayer, say the final Salam, then perform 2 prostrations of forgetfulness and conclude with another Salam.",
            timing = "After Salam (Ba'd as-Salam)"
        )
    )

    val PRAYER_CONDITIONS = listOf(
        Pair("1. Islam & Sanity", "Only a Muslim with mental capability is obligated to pray."),
        Pair("2. Age of Discernment (Tamyiz)", "Children are taught at 7 and commanded at 10 to establish prayer."),
        Pair("3. Ritual Purity (Taharah)", "Purification from minor impurity via Wudu and major impurity via Ghusl."),
        Pair("4. Cleanliness of Clothes, Body & Place", "Ensuring no physical Najāsah (impurities) on body, garments, or prayer rug."),
        Pair("5. Covering the Awrah", "Men: from navel to knee, with shoulders covered. Women: entire body except face and hands."),
        Pair("6. Facing the Qibla (Kaaba in Makkah)", "Directing your chest towards the Sacred House of Allah in Makkah."),
        Pair("7. Arrival of Prayer Time", "Salah is not valid before its entrance; must be prayed within its prescribed window."),
        Pair("8. Heartfelt Intention (Niyyah)", "Resolving in the heart specifically which prayer is being offered for Allah's sake.")
    )

    val PRAYER_PILLARS = listOf(
        Pair("1. Standing (Qiyam)", "Standing upright in obligatory prayer for those physically able."),
        Pair("2. Takbirat al-Ihram", "The opening 'Allahu Akbar' that enters you into the sanctified state of Salah."),
        Pair("3. Recitation of Surah Al-Fatiha", "The Opening Surah must be recited in every single Rak'ah of prayer."),
        Pair("4. Bowing (Ruku')", "Bowing until hands rest on knees with flat back and still head."),
        Pair("5. Straightening from Bowing", "Rising completely upright until all vertebrae return to rest."),
        Pair("6. Prostration on Seven Bones", "Sujud on forehead with nose, both palms, both knees, and tips of both toes."),
        Pair("7. Rising & Sitting Between Sujuds", "Sitting calmly between the two prostrations with upright back."),
        Pair("8. Tranquility & Serenity (Tumaneenah)", "Pausing in each posture until all limbs are still; rushing invalidates Salah."),
        Pair("9. Final Tashahhud Sitting", "Sitting for the final witness of faith and peace upon the Prophet."),
        Pair("10. Concluding Taslim", "Saying 'Assalamu 'alaykum wa rahmatullah' to conclude the prayer.")
    )

    val PRAYER_INVALIDATORS = listOf(
        Pair("Talking Intentionally", "Uttering worldly words or answering someone intentionally breaks the prayer immediately."),
        Pair("Laughing Out Loud (Qahqahah)", "Audible laughter breaks both the Salah and the Wudu according to the Hanafi school, and breaks Salah in all schools."),
        Pair("Eating or Drinking", "Consuming food or drink while in prayer invalidates it."),
        Pair("Excessive Movement", "Frequent, continuous bodily movements not related to the prayer (e.g. fiddling with clothes, turning head repeatedly)."),
        Pair("Breaking of Wudu", "Passing gas, urination, or any occurrence that nullifies ritual purity breaks prayer."),
        Pair("Uncovering of Awrah", "If Awrah is exposed intentionally or for a prolonged period, prayer becomes invalid."),
        Pair("Turning away from Qibla", "Rotating the chest away from the direction of the Kaaba invalidates prayer.")
    )
}
