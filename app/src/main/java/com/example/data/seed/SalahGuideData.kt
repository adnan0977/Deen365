package com.example.data.seed

data class SalahStep(
    val stepNumber: Int,
    val postureNameArabic: String,
    val postureNameEnglish: String,
    val postureDescription: String,
    val arabicRecitation: String,
    val transliteration: String,
    val translation: String,
    val explanation: String
)

object SalahGuideData {

    val STEPS = listOf(
        SalahStep(
            stepNumber = 1,
            postureNameArabic = "تَكْبِيرَةُ الإِحْرَامِ",
            postureNameEnglish = "Takbirat al-Ihram (Opening Takbir)",
            postureDescription = "Stand straight facing the Qibla with feet shoulder-width apart. Raise both hands to earlobe level (palms facing Qibla) and declare Allah's greatness.",
            arabicRecitation = "اللَّهُ أَكْبَرُ",
            transliteration = "Allahu Akbar",
            translation = "Allah is the Greatest.",
            explanation = "This Takbir marks the sacred boundary. By uttering 'Allahu Akbar', you cast the worldly universe behind your back and enter the divine audience of your Creator."
        ),
        SalahStep(
            stepNumber = 2,
            postureNameArabic = "دُعَاءُ الاسْتِفْتَاحِ (الثَّنَاءُ)",
            postureNameEnglish = "Qiyam & Thana (Opening Supplication)",
            postureDescription = "Place your right hand over your left hand on the chest/navel. Cast your gaze humbly upon the spot of your prostration (Sujud).",
            arabicRecitation = "سُبْحَانَكَ اللَّهُمَّ وَبِحَمْدِكَ، وَتَبَارَكَ اسْمُكَ، وَتَعَالَى جَدُّكَ، وَلاَ إِلَهَ غَيْرُكَ",
            transliteration = "Subhanaka Allahumma wa bihamdika, wa tabarakas-muka, wa ta'ala jadduka, wa la ilaha ghayruk.",
            translation = "Glory be to You, O Allah, and all praise is Yours. Blessed is Your Name, exalted is Your Majesty, and there is no deity worthy of worship besides You.",
            explanation = "Thana prepares your mind with absolute awe and reverence before reciting the words of Allah in the Holy Quran."
        ),
        SalahStep(
            stepNumber = 3,
            postureNameArabic = "الاسْتِعَاذَةُ وَالتَّسْمِيَةُ",
            postureNameEnglish = "Ta'awwudh & Tasmiyah",
            postureDescription = "Recite quietly to seek divine refuge from Satan's whisperings before recitation.",
            arabicRecitation = "أَعُوذُ بِاللَّهِ مِنَ الشَّيْطَانِ الرَّجِيمِ • بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
            transliteration = "A'oodhu billahi minash-shaytanir-rajeem. Bismillahir-Rahmanir-Raheem.",
            translation = "I seek refuge in Allah from Satan the outcast. In the Name of Allah, the Entirely Merciful, the Especially Merciful.",
            explanation = "Guards your heart against distractions, stray thoughts, and waswas during prayer."
        ),
        SalahStep(
            stepNumber = 4,
            postureNameArabic = "سُورَةُ الْفَاتِحَةِ",
            postureNameEnglish = "Surah Al-Fatihah (The Core Pillar)",
            postureDescription = "Recite Surah Al-Fatihah with thoughtful pauses between each ayah. It is an obligatory pillar in every Rak'ah.",
            arabicRecitation = "الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ • الرَّحْمَٰنِ الرَّحِيمِ • مَالِكِ يَوْمِ الدِّينِ • إِيَّاكَ نَعْبُدُ وَإِيَّاكَ نَسْتَعِينُ • اهْدِنَا الصِّرَاطَ الْمُسْتَقِيمَ • صِرَاطَ الَّذِينَ أَنْعَمْتَ عَلَيْهِمْ غَيْرِ الْمَغْضُوبِ عَلَيْهِمْ وَلَا الضَّالِّينَ • آمِينَ",
            transliteration = "Alhamdu lillaahi Rabbil 'aalameen. Ar-Rahmaanir-Raheem. Maaliki Yawmid-Deen. Iyyaaka na'budu wa iyyaaka nasta'een. Ihdinas-Siraatal-Mustaqeem. Siraatal-ladheena an'amta 'alayhim ghayril-maghdoobi 'alayhim wa lad-daalleen. Ameen.",
            translation = "All praise is due to Allah, Lord of the worlds. The Entirely Merciful, the Especially Merciful. Sovereign of the Day of Recompense. You alone we worship and You alone we ask for help. Guide us to the straight path. The path of those upon whom You have bestowed favor, not of those who have evoked anger or of those who are astray. Ameen.",
            explanation = "The Prophet ﷺ narrated that Allah says: 'I have divided the prayer between Myself and My servant into two halves, and My servant shall have what he asked for.' Every ayah is answered directly by Allah."
        ),
        SalahStep(
            stepNumber = 5,
            postureNameArabic = "قِرَاءَةُ السُّورَةِ",
            postureNameEnglish = "Recitation of Additional Surah (e.g. Al-Ikhlas)",
            postureDescription = "Recite any short Surah or selected verses from the Quran in the first two Rak'ahs.",
            arabicRecitation = "قُلْ هُوَ اللَّهُ أَحَدٌ • اللَّهُ الصَّمَدُ • لَمْ يَلِدْ وَلَمْ يُولَدْ • وَلَمْ يَكُنْ لَهُ كُفُوًا أَحَدٌ",
            transliteration = "Qul Huwal-laahu Ahad. Allahus-Samad. Lam yalid wa lam yoolad. Wa lam yakul-lahoo kufuwan ahad.",
            translation = "Say, 'He is Allah, [who is] One, Allah, the Eternal Refuge. He neither begets nor is born, Nor is there to Him any equivalent.'",
            explanation = "Reciting Surah Al-Ikhlas affirms the uncompromised oneness of Allah (Tawhid). The Prophet ﷺ described it as equivalent to one-third of the Quran in reward."
        ),
        SalahStep(
            stepNumber = 6,
            postureNameArabic = "الرُّكُوعُ",
            postureNameEnglish = "Ruku' (The Bowing Posture)",
            postureDescription = "Say 'Allahu Akbar' and bend at the waist until your back is flat horizontal. Rest hands firmly on knees with fingers spread, looking down at your feet.",
            arabicRecitation = "سُبْحَانَ رَبِّيَ الْعَظِيمِ (ثلاث مرات)",
            transliteration = "Subhana Rabbiyal 'Adheem (Recite 3 times)",
            translation = "Glory be to my Lord, the Magnificent.",
            explanation = "Ruku represents physical humility before the supreme majesty of Allah. Straighten the back so that if water were poured upon it, it would remain level."
        ),
        SalahStep(
            stepNumber = 7,
            postureNameArabic = "الاعْتِدَالُ مِنَ الرُّكُوعِ (الْقَوْمَةُ)",
            postureNameEnglish = "Qawmah / I'tidal (Standing from Bowing)",
            postureDescription = "Rise back to complete upright standing with hands hanging naturally or resting on chest, declaring praise to Allah.",
            arabicRecitation = "سَمِعَ اللَّهُ لِمَنْ حَمِدَهُ • رَبَّنَا وَلَكَ الْحَمْدُ، حَمْدًا كَثِيرًا طَيِّبًا مُبَارَكًا فِيهِ",
            transliteration = "Sami'a Allahu liman hamidah. Rabbana wa lakal-hamd, hamdan katheeran tayyiban mubarakan feeh.",
            translation = "Allah hears whoever praises Him. Our Lord, and to You belongs all praise, abundant, pure, and blessed praise.",
            explanation = "Stand fully erect and pause for a calm moment. Over 30 angels race to record the prayer of whoever adds 'hamdan katheeran tayyiban mubarakan feeh'."
        ),
        SalahStep(
            stepNumber = 8,
            postureNameArabic = "السُّجُودُ الأَوَّلُ",
            postureNameEnglish = "First Sujud (The Prostration)",
            postureDescription = "Say 'Allahu Akbar' and descend to prostrate on 7 limbs: forehead with nose, both palms, both knees, and tips of both feet with toes facing Qibla.",
            arabicRecitation = "سُبْحَانَ رَبِّيَ الأَعْلَى (ثلاث مرات)",
            transliteration = "Subhana Rabbiyal A'la (Recite 3 times)",
            translation = "Glory be to my Lord, the Most High.",
            explanation = "Sujud is the highest spiritual peak of Islam. The Prophet ﷺ said: 'The closest that a servant ever comes to his Lord is when he is in prostration, so make abundant supplication in it.'"
        ),
        SalahStep(
            stepNumber = 9,
            postureNameArabic = "الْجَلْسَةُ بَيْنَ السَّجْدَتَيْنِ",
            postureNameEnglish = "Jalsah (Sitting Between the Two Prostrations)",
            postureDescription = "Rise from Sujud saying 'Allahu Akbar' and sit upright calmly on your left foot with the right foot upright. Place hands on your thighs.",
            arabicRecitation = "رَبِّ اغْفِرْ لِي، رَبِّ اغْفِرْ لِي • اللَّهُمَّ اغْفِرْ لِي وَارْحَمْنِي وَعَافِنِي وَاهْدِنِي وَارْزُقْنِي",
            transliteration = "Rabbighfir lee, Rabbighfir lee. Allahummagh-fir lee, warhamnee, wa 'aafinee, wahdinee, warzuqnee.",
            translation = "O my Lord, forgive me. O my Lord, forgive me. O Allah, forgive me, have mercy on me, grant me well-being, guide me, and provide for me.",
            explanation = "Do not rush between prostrations. Sit with complete serenity until every bone returns to its resting joint before descending to the second Sujud."
        ),
        SalahStep(
            stepNumber = 10,
            postureNameArabic = "السُّجُودُ الثَّانِي",
            postureNameEnglish = "Second Sujud",
            postureDescription = "Prostrate a second time exactly like the first on all 7 points of contact, exalting the Most High.",
            arabicRecitation = "سُبْحَانَ رَبِّيَ الأَعْلَى (ثلاث مرات)",
            transliteration = "Subhana Rabbiyal A'la (Recite 3 times)",
            translation = "Glory be to my Lord, the Most High.",
            explanation = "Completes the first unit (Rak'ah) of prayer. Rise saying 'Allahu Akbar' to begin the next Rak'ah."
        ),
        SalahStep(
            stepNumber = 11,
            postureNameArabic = "التَّشَهُّدُ (التَّحِيَّاتُ)",
            postureNameEnglish = "Tashahhud (The Testimony of Faith)",
            postureDescription = "Sit calmly on your left foot with right foot upright. Point your right index finger upward toward the Qibla during the testimony of Tawhid.",
            arabicRecitation = "التَّحِيَّاتُ لِلَّهِ وَالصَّلَوَاتُ وَالطَّيِّبَاتُ، السَّلاَمُ عَلَيْكَ أَيُّهَا النَّبِيُّ وَرَحْمَةُ اللَّهِ وَبَرَكَاتُهُ، السَّلاَمُ عَلَيْنَا وَعَلَى عِبَادِ اللَّهِ الصَّالِحِينَ، أَشْهَدُ أَنْ لاَ إِلَهَ إِلاَّ اللَّهُ وَأَشْهَدُ أَنَّ مُحَمَّدًا عَبْدُهُ وَرَسُولُهُ",
            transliteration = "At-tahiyyatu lillahi was-salawatu wat-tayyibat. As-salamu 'alayka ayyuhan-Nabiyyu wa rahmatullahi wa barakatuh. As-salamu 'alayna wa 'ala 'ibadillahis-saliheen. Ash-hadu alla ilaha illallah, wa ash-hadu anna Muhammadan 'abduhoo wa rasooluh.",
            translation = "All compliments, prayers, and pure words belong to Allah. Peace be upon you, O Prophet, and the mercy of Allah and His blessings. Peace be upon us and upon the righteous servants of Allah. I bear witness that there is no deity worthy of worship except Allah, and I bear witness that Muhammad is His servant and Messenger.",
            explanation = "Reenacts the celestial conversation of the Mi'raj (Ascension). Pointing the index finger symbolizes the indivisible Oneness of Allah."
        ),
        SalahStep(
            stepNumber = 12,
            postureNameArabic = "الصَّلَاةُ الإِبْرَاهِيمِيَّةُ",
            postureNameEnglish = "Durood Ibrahim (Salawat on the Prophet ﷺ)",
            postureDescription = "Recite while seated in the final sitting (Tashahhud Akhir) sending blessings upon the Prophet Muhammad ﷺ and Prophet Ibrahim AS.",
            arabicRecitation = "اللَّهُمَّ صَلِّ عَلَى مُحَمَّدٍ وَعَلَى آلِ مُحَمَّدٍ كَمَا صَلَّيْتَ عَلَى إِبْرَاهِيمَ وَعَلَى آلِ إِبْرَاهِيمَ إِنَّكَ حَمِيدٌ مَجِيدٌ • اللَّهُمَّ بَارِكْ عَلَى مُحَمَّدٍ وَعَلَى آلِ مُحَمَّدٍ كَمَا بَارَكْتَ عَلَى إِبْرَاهِيمَ وَعَلَى آلِ إِبْرَاهِيمَ إِنَّكَ حَمِيدٌ مَجِيدٌ",
            transliteration = "Allahumma salli 'ala Muhammadin wa 'ala aali Muhammad, kama sallayta 'ala Ibraheema wa 'ala aali Ibraheem, innaka Hameedum-Majeed. Allahumma barik 'ala Muhammadin wa 'ala aali Muhammad, kama barakta 'ala Ibraheema wa 'ala aali Ibraheem, innaka Hameedum-Majeed.",
            translation = "O Allah, send prayers upon Muhammad and upon the family of Muhammad, as You sent prayers upon Ibrahim and upon the family of Ibrahim; indeed, You are Praiseworthy and Glorious. O Allah, bless Muhammad and the family of Muhammad, as You blessed Ibrahim and the family of Ibrahim; indeed, You are Praiseworthy and Glorious.",
            explanation = "The highest form of Durood taught by the Prophet ﷺ himself when his companions asked him how they should pray for him."
        ),
        SalahStep(
            stepNumber = 13,
            postureNameArabic = "الدُّعَاءُ وَالتَّسْلِيمُ",
            postureNameEnglish = "Final Dua & Taslim (Concluding the Prayer)",
            postureDescription = "Make personal supplication, then turn your face to the right saying 'As-salamu alaykum wa rahmatullah', then turn to the left saying the same.",
            arabicRecitation = "رَبَّنَا آتِنَا فِي الدُّنْيَا حَسَنَةً وَفِي الآخِرَةِ حَسَنَةً وَقِنَا عَذَابَ النَّارِ • السَّلاَمُ عَلَيْكُمْ وَرَحْمَةُ اللَّهِ",
            transliteration = "Rabbana aatina fid-dunya hasanatan wa fil-aakhirati hasanatan wa qina 'adhaban-nar. As-salamu 'alaykum wa rahmatullah.",
            translation = "Our Lord, give us in this world that which is good and in the Hereafter that which is good, and save us from the punishment of the Fire. Peace and the mercy of Allah be upon you.",
            explanation = "The Taslim conveys greetings of peace to the recording angels on your right and left shoulders and to your fellow believers, concluding the sacred prayer."
        )
    )
}
