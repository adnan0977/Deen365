package com.example.data.model

/**
 * Stages and steps of Umrah and Hajj pilgrimages.
 */
data class PilgrimageStep(
    val id: String,
    val title: String,
    val arabicTitle: String,
    val location: String,
    val summary: String,
    val detailedInstructions: List<String>,
    val authenticDuas: List<AuthenticDuaSnippet>,
    val prohibitionsAndMistakes: List<String>,
    val iconEmoji: String,
    val isHajjOnly: Boolean = false,
    val hajjDay: String? = null // e.g. "8th Dhul Hijjah (Yawm at-Tarwiyah)"
)

data class AuthenticDuaSnippet(
    val title: String,
    val arabic: String,
    val transliteration: String,
    val translation: String,
    val source: String
)

data class HajjUmrahChecklistItem(
    val id: String,
    val category: String, // "Documentation", "Ihram & Clothing", "Medical & Hygiene", "Spiritual Prep"
    val title: String,
    val description: String,
    val isCompleted: Boolean = false
)

object HajjUmrahData {
    val UMRAH_STEPS = listOf(
        PilgrimageStep(
            id = "umrah_ihram",
            title = "1. Entering Ihram at the Miqat",
            arabicTitle = "الإِحْرَام",
            location = "Miqat (e.g. Dhul Hulayfah / Yalamlam / Qarn al-Manazil)",
            summary = "Perform Ghusl, put on two white unstitched sheets (for men) or modest clothing (for women), make Niyyah for Umrah, and begin the Talbiyah.",
            detailedInstructions = listOf(
                "Perform full ritual bath (Ghusl) and trim nails/hair before assuming Ihram.",
                "Men wear two white unstitched cloths (Rida over shoulders, Izar around waist). Women wear regular loose modest clothing without face veil (Niqab) or gloves.",
                "Pray 2 Rak'ahs Sunnah of Ihram if at a masjid at the Miqat.",
                "Pronounce the intention (Niyyah): 'Labbayk Allahumma 'Umratan' (Here I am, O Allah, for 'Umrah).",
                "Continuously recite the Talbiyah aloud (men) or softly (women) until arriving at the Kaaba for Tawaf."
            ),
            authenticDuas = listOf(
                AuthenticDuaSnippet(
                    title = "Intention for Umrah (Niyyah)",
                    arabic = "لَبَّيْكَ اللَّهُمَّ عُمْرَةً",
                    transliteration = "Labbayk Allahumma 'Umratan",
                    translation = "Here I am, O Allah, answering Your call with an 'Umrah.",
                    source = "Sahih Muslim #1218"
                ),
                AuthenticDuaSnippet(
                    title = "The Prophetic Talbiyah",
                    arabic = "لَبَّيْكَ اللَّهُمَّ لَبَّيْكَ، لَبَّيْكَ لاَ شَرِيكَ لَكَ لَبَّيْكَ، إِنَّ الْحَمْدَ وَالنِّعْمَةَ لَكَ وَالْمُلْكَ، لاَ شَرِيكَ لَكَ",
                    transliteration = "Labbayka Allahumma labbayk, labbayka la sharika laka labbayk, innal-hamda wan-ni'mata laka wal-mulk, la sharika lak.",
                    translation = "Here I am at Your service, O Allah, here I am. Here I am, You have no partner, here I am. Truly all praise, grace, and sovereignty belong to You. You have no partner.",
                    source = "Sahih al-Bukhari #1549 • Sahih Muslim #1184"
                )
            ),
            prohibitionsAndMistakes = listOf(
                "Do not apply perfumes or scented soaps after assuming Ihram.",
                "Do not cut hair, shave, or clip nails until after Sa'i is complete.",
                "Men must not wear stitched garments (shirts, underwear, trousers) or cover the head.",
                "Do not pass the Miqat boundary on plane or ground without having entered Ihram."
            ),
            iconEmoji = "🕊️"
        ),
        PilgrimageStep(
            id = "umrah_tawaf",
            title = "2. Tawaf al-Umrah (7 Laps)",
            arabicTitle = "طَوَاف الْعُمْرَة",
            location = "Masjid al-Haram, Holy Kaaba",
            summary = "Perform 7 anti-clockwise circuits around the Kaaba starting from the Black Stone (Hajar al-Aswad) with the Kaaba on your left.",
            detailedInstructions = listOf(
                "Must be in a state of Wudu (ritual purity) throughout the entire Tawaf.",
                "Men practice Idtiba' (uncovering right shoulder by placing the Rida under the right armpit and over the left shoulder) during all 7 circuits of Tawaf al-Umrah.",
                "Begin each lap at the Black Stone (Hajar al-Aswad) line. Point right hand towards it and recite: 'Bismillahi Allahu Akbar'.",
                "Men jog with short brisk steps (Raml) during the first 3 circuits, then walk normally for the remaining 4.",
                "Between the Yemeni Corner (Rukn al-Yamani) and the Black Stone, recite the Sunnah Quranic du'a.",
                "Upon completing 7 full circuits, cover the right shoulder, proceed to Maqam Ibrahim, and pray 2 light Rak'ahs (Surah Al-Kafirun in 1st, Surah Al-Ikhlas in 2nd).",
                "Drink Zamzam water until satisfied and make supplication."
            ),
            authenticDuas = listOf(
                AuthenticDuaSnippet(
                    title = "At the Start of Each Circuit (Hajar al-Aswad)",
                    arabic = "بِسْمِ اللَّهِ وَاللَّهُ أَكْبَرُ",
                    transliteration = "Bismillahi wallahu Akbar",
                    translation = "In the name of Allah, and Allah is the Greatest.",
                    source = "Sahih al-Bukhari #1612"
                ),
                AuthenticDuaSnippet(
                    title = "Between Rukn Yamani & Black Stone",
                    arabic = "رَبَّنَا آتِنَا فِي الدُّنْيَا حَسَنَةً وَفِي الآخِرَةِ حَسَنَةً وَقِنَا عَذَابَ النَّارِ",
                    transliteration = "Rabbana atina fid-dunya hasanatan wa fil-akhirati hasanatan wa qina 'adhaban-nar.",
                    translation = "Our Lord! Give us in this world that which is good and in the Hereafter that which is good, and save us from the punishment of the Fire.",
                    source = "Surah Al-Baqarah 2:201 • Sunan Abi Dawud #1892"
                )
            ),
            prohibitionsAndMistakes = listOf(
                "Do not push or shove to kiss the Black Stone; pointing from a distance is completely valid and Sunnah.",
                "Do not make Tawaf inside the Hijr Ismail (Hateem); you must circle outside it, as it is part of the Kaaba.",
                "Do not make specific invented du'as for each lap; any personal authentic supplication from the heart is encouraged."
            ),
            iconEmoji = "🕋"
        ),
        PilgrimageStep(
            id = "umrah_sai",
            title = "3. Sa'i Between Safa & Marwah (7 Rounds)",
            arabicTitle = "السَّعْي",
            location = "Mas'a (Between Mount Safa and Mount Marwah)",
            summary = "Walk 7 rounds between Safa and Marwah starting at Safa and ending at Marwah (Safa to Marwah is 1 round; Marwah to Safa is round 2).",
            detailedInstructions = listOf(
                "Ascend Mount Safa, face the Kaaba, raise hands in du'a, recite Surah Al-Baqarah 2:158 and the Prophetic Takbir three times.",
                "Walk towards Mount Marwah. Men run briskly between the two green light markers, while women maintain normal walking pace.",
                "Upon reaching Marwah (end of 1st round), face the Kaaba, glorify Allah, and make du'a just as done at Safa.",
                "Walk back to Safa for round 2. Repeat until you complete 7 rounds, concluding on Mount Marwah."
            ),
            authenticDuas = listOf(
                AuthenticDuaSnippet(
                    title = "Quranic recitation ascending Safa",
                    arabic = "إِنَّ الصَّفَا وَالْمَرْوَةَ مِن شَعَائِرِ اللَّهِ ۖ أَبْدَأُ بِمَا بَدَأَ اللَّهُ بِهِ",
                    transliteration = "Innas-Safa wal-Marwata min sha'a'irillah. Abda'u bima bada'allahu bih.",
                    translation = "Indeed, As-Safa and Al-Marwah are among the symbols of Allah. I begin with that which Allah began with.",
                    source = "Sahih Muslim #1218"
                ),
                AuthenticDuaSnippet(
                    title = "Supplication atop Safa and Marwah",
                    arabic = "لاَ إِلَهَ إِلاَّ اللَّهُ وَحْدَهُ لاَ شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ، وَهُوَ عَلَى كُلِّ شَىْءٍ قَدِيرٌ، لاَ إِلَهَ إِلاَّ اللَّهُ وَحْدَهُ، أَنْجَزَ وَعْدَهُ، وَنَصَرَ عَبْدَهُ، وَهَزَمَ الأَحْزَابَ وَحْدَهُ",
                    transliteration = "La ilaha illallahu wahdahu la sharika lah, lahul-mulku wa lahul-hamdu, wa Huwa 'ala kulli shay'in qadir. La ilaha illallahu wahdah, anjaza wa'dah, wa nasara 'abdah, wa hazamal-ahzaba wahdah.",
                    translation = "There is no true deity except Allah alone, with no partner. His is the sovereignty and His is all praise, and He has power over all things. None has the right to be worshiped but Allah alone. He fulfilled His promise, granted victory to His servant, and defeated the allied clans alone.",
                    source = "Sahih Muslim #1218"
                )
            ),
            prohibitionsAndMistakes = listOf(
                "Counting Safa to Marwah and back to Safa as one round is a major error; Safa to Marwah is Round 1, Marwah to Safa is Round 2.",
                "Wudu is highly recommended for Sa'i, but unlike Tawaf, if wudu breaks during Sa'i it remains valid."
            ),
            iconEmoji = "⛰️"
        ),
        PilgrimageStep(
            id = "umrah_halq",
            title = "4. Halq or Taqsir (Shaving or Trimming Hair)",
            arabicTitle = "الْحَلْق أَو التَّقْصِير",
            location = "Barber shops around the Grand Mosque",
            summary = "Men shave the entire head (Halq - most rewarded) or trim hair equally all around (Taqsir). Women trim one fingertip length (approx 1 inch) from the end of their hair.",
            detailedInstructions = listOf(
                "For men: Halq (shaving head clean) is thrice blessed by the Prophet (ﷺ) over trimming. If trimming (Taqsir), hair must be cut from all sides of the head equally.",
                "For women: Gather hair and cut the length of a fingertip (about 1 to 2 cm) from the ends. Women must never shave their heads.",
                "Upon cutting hair, the state of Ihram is lifted completely and all normal permissible activities resume. Your Umrah is complete (Mabroor)!"
            ),
            authenticDuas = listOf(
                AuthenticDuaSnippet(
                    title = "Prophetic Prayer for those who shave",
                    arabic = "اللَّهُمَّ اغْفِرْ لِلْمُحَلِّقِينَ، قَالُوا: وَلِلْمُقَصِّرِينَ؟ قَالَ: اللَّهُمَّ اغْفِرْ لِلْمُحَلِّقِينَ، قَالُوا: وَلِلْمُقَصِّرِينَ؟ قَالَ: وَلِلْمُقَصِّرِينَ",
                    transliteration = "Allahummaghfir lil-muhalliqeen (three times)... wa lil-muqassireen.",
                    translation = "The Messenger of Allah (ﷺ) prayed: 'O Allah, forgive those who shave their heads!' They asked: 'And those who cut short their hair?' He repeated his prayer three times for those who shave, then on the fourth time added: 'And those who cut short their hair.'",
                    source = "Sahih al-Bukhari #1727 • Sahih Muslim #1301"
                )
            ),
            prohibitionsAndMistakes = listOf(
                "Do not merely snip two or three stray hairs; Taqsir requires cutting evenly from across the head.",
                "Do not remove Ihram clothing before the hair is actually cut."
            ),
            iconEmoji = "✂️"
        )
    )

    val HAJJ_DAYS = listOf(
        PilgrimageStep(
            id = "hajj_day_8",
            title = "Day 1 (8th Dhul Hijjah): Yawm at-Tarwiyah",
            arabicTitle = "يَوْم التَّرْوِيَة",
            location = "Mina Valley",
            summary = "Enter Ihram for Hajj from Makkah, move to Mina before Dhuhr, and spend the day and night in worship and prayer.",
            detailedInstructions = listOf(
                "Enter state of Ihram for Hajj from your hotel/residence in Makkah in the morning of the 8th.",
                "Arrive in Mina before Dhuhr. Pray Dhuhr, Asr, Maghrib, Isha, and Fajr of the 9th in Mina.",
                "Shorten 4-Rak'ah prayers to 2 Rak'ahs (Qasr) at their designated times without combining.",
                "Spend the night in Mina engaged in Talbiyah, Quranic recitation, and remembrance."
            ),
            authenticDuas = listOf(
                AuthenticDuaSnippet(
                    title = "Intention for Hajj",
                    arabic = "لَبَّيْكَ اللَّهُمَّ حَجًّا",
                    transliteration = "Labbayk Allahumma Hajjan",
                    translation = "Here I am, O Allah, answering Your call for Hajj.",
                    source = "Sahih Muslim #1218"
                )
            ),
            prohibitionsAndMistakes = listOf(
                "Avoid combining Dhuhr and Asr in Mina on the 8th; shorten them without combining per the Sunnah."
            ),
            iconEmoji = "⛺",
            isHajjOnly = true,
            hajjDay = "8th Dhul Hijjah"
        ),
        PilgrimageStep(
            id = "hajj_day_9",
            title = "Day 2 (9th Dhul Hijjah): The Day of Arafah & Muzdalifah",
            arabicTitle = "يَوْم عَرَفَة وَمُزْدَلِفَة",
            location = "Plains of Arafat & Muzdalifah",
            summary = "The pinnacle of Hajj ('Al-Hajj 'Arafah'). Stand in heartfelt supplication from Dhuhr to Sunset, then depart after sunset to Muzdalifah for the night.",
            detailedInstructions = listOf(
                "After sunrise on the 9th, travel from Mina to the plains of Arafat.",
                "Listen to the Khutbah at Masjid Namirah if possible, then pray Dhuhr and Asr combined and shortened (2 Rak'ahs each) during Dhuhr time.",
                "Stand in Arafat (Wuquf) engaging in continuous weeping, repentance, and supplication facing the Qibla until sunset.",
                "Do NOT leave Arafat before sunset. Immediately after Maghrib sets, proceed peacefully to Muzdalifah.",
                "At Muzdalifah, pray Maghrib (3 Rak'ahs) and Isha (2 Rak'ahs) combined. Sleep under the open sky until Fajr.",
                "Collect 49 to 70 small pebbles (size of chickpeas) for the Jamarat."
            ),
            authenticDuas = listOf(
                AuthenticDuaSnippet(
                    title = "The Best Supplication on the Day of Arafat",
                    arabic = "لاَ إِلَهَ إِلاَّ اللَّهُ وَحْدَهُ لاَ شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ، وَهُوَ عَلَى كُلِّ شَىْءٍ قَدِيرٌ",
                    transliteration = "La ilaha illallahu wahdahu la sharika lah, lahul-mulku wa lahul-hamdu, wa Huwa 'ala kulli shay'in qadir.",
                    translation = "The Prophet (ﷺ) said: 'The best supplication is the supplication on the day of Arafat, and the best of what I and the prophets before me have said is: None has the right to be worshiped but Allah alone, without partner. His is the sovereignty and His is the praise, and He has power over all things.'",
                    source = "Sunan at-Tirmidhi #3585"
                )
            ),
            prohibitionsAndMistakes = listOf(
                "Do not pray Maghrib in Arafat; the Prophet (ﷺ) delayed Maghrib until arriving in Muzdalifah.",
                "Do not push onto Mount Rahmah; standing anywhere within the marked boundaries of Arafat is completely valid."
            ),
            iconEmoji = "🌄",
            isHajjOnly = true,
            hajjDay = "9th Dhul Hijjah"
        ),
        PilgrimageStep(
            id = "hajj_day_10",
            title = "Day 3 (10th Dhul Hijjah): Yawm an-Nahr (Eid al-Adha)",
            arabicTitle = "يَوْم النَّحْر (عِيد الْأَضْحَى)",
            location = "Muzdalifah -> Mina (Jamarat) -> Makkah (Tawaf al-Ifadah)",
            summary = "Perform Fajr at Muzdalifah, stone the Big Pillar (Jamarat al-Aqaba), sacrifice animal (Hady), shave head (Halq), and perform Tawaf al-Ifadah.",
            detailedInstructions = listOf(
                "Pray Fajr at dawn in Muzdalifah and make du'a at Al-Mash'ar al-Haram until bright twilight.",
                "Depart Muzdalifah to Mina before sunrise.",
                "Stone Jamarat al-Aqabah (the big pillar) with 7 pebbles reciting 'Allahu Akbar' with each throw. Stop reciting Talbiyah with the first pebble.",
                "Offer or confirm sacrificial animal slaughter (Hady/Qurbani).",
                "Shave (Halq) or cut hair (Taqsir). This achieves the first release from Ihram (At-Tahallul al-Asghar) where everything becomes permissible except marital relations.",
                "Proceed to Makkah to perform Tawaf al-Ifadah and Sa'i of Hajj. After this, the complete release from Ihram (At-Tahallul al-Akbar) is achieved."
            ),
            authenticDuas = listOf(
                AuthenticDuaSnippet(
                    title = "Takbir when throwing each pebble at Jamarat",
                    arabic = "اللَّهُ أَكْبَرُ",
                    transliteration = "Allahu Akbar",
                    translation = "Allah is the Greatest.",
                    source = "Sahih Muslim #1299"
                )
            ),
            prohibitionsAndMistakes = listOf(
                "Do not throw shoes or large rocks at the pillars; pebbles must be small like chickpeas.",
                "Do not stone all three pillars on the 10th; only the large pillar (Jamarat al-Aqaba) is stoned on Eid day."
            ),
            iconEmoji = "🕋",
            isHajjOnly = true,
            hajjDay = "10th Dhul Hijjah"
        ),
        PilgrimageStep(
            id = "hajj_day_11_12",
            title = "Days 4 & 5 (11th, 12th, 13th): Ayyam at-Tashreeq",
            arabicTitle = "أَيَّام التَّشْرِيق",
            location = "Mina & Tawaf al-Wada' (Farewell)",
            summary = "Spend the nights in Mina and stone all three Jamarat (Small, Medium, Big) after Dhuhr each day, concluding with the Farewell Tawaf.",
            detailedInstructions = listOf(
                "Spend the nights of the 11th and 12th in Mina.",
                "On each afternoon after the sun passes its zenith (Zawal / Dhuhr time), stone all three pillars starting with the Small Pillar (7 pebbles), making du'a, then Medium Pillar (7 pebbles), making du'a, and finally Big Pillar (7 pebbles).",
                "Pilgrims may depart Mina on the 12th after stoning before sunset (Ta'ajjul), or remain for the 13th for greater reward.",
                "Before leaving Makkah permanently, perform the Farewell Tawaf (Tawaf al-Wada') as your final act."
            ),
            authenticDuas = listOf(
                AuthenticDuaSnippet(
                    title = "Supplication after stoning the Small and Medium Pillars",
                    arabic = "سُبْحَانَ اللَّهِ، وَالْحَمْدُ لِلَّهِ، وَلاَ إِلَهَ إِلاَّ اللَّهُ، وَاللَّهُ أَكْبَرُ",
                    transliteration = "Subhanallahi wal-hamdulillahi wa la ilaha illallahu wallahu Akbar.",
                    translation = "Glory be to Allah, all praise is due to Allah, there is no God but Allah, and Allah is the Greatest.",
                    source = "Sahih al-Bukhari #1752"
                )
            ),
            prohibitionsAndMistakes = listOf(
                "Do not stone the Jamarat before Dhuhr on the days of Tashreeq.",
                "Do not leave Makkah without Tawaf al-Wada', except for menstruating women who are excused by the Prophet (ﷺ)."
            ),
            iconEmoji = "🌙",
            isHajjOnly = true,
            hajjDay = "11th-13th Dhul Hijjah"
        )
    )

    val DEFAULT_CHECKLIST = listOf(
        HajjUmrahChecklistItem("chk_1", "Documentation", "Passport & Umrah/Hajj Visa valid for at least 6 months", "Keep physical passport and digital backup on phone.", false),
        HajjUmrahChecklistItem("chk_2", "Documentation", "Flight tickets & Hotel reservation vouchers in Makkah/Madinah", "Confirmed booking copies.", false),
        HajjUmrahChecklistItem("chk_3", "Documentation", "Vaccination certificates (Meningococcal ACWY, etc.)", "Mandatory entry requirement.", false),
        HajjUmrahChecklistItem("chk_4", "Ihram & Clothing", "Two sets of white unstitched Ihram towels (for men)", "High quality cotton/bamboo to prevent chafing.", false),
        HajjUmrahChecklistItem("chk_5", "Ihram & Clothing", "Ihram belt or waist pouch with secure zipper", "To secure Izar and hold money, phone, and hotel key card.", false),
        HajjUmrahChecklistItem("chk_6", "Ihram & Clothing", "Comfortable walking sandals exposing ankles and top of foot", "Break them in before travel to prevent blisters.", false),
        HajjUmrahChecklistItem("chk_7", "Medical & Hygiene", "Unscented soap, deodorant, and moisturizer for Ihram", "Perfumed items are forbidden while in Ihram.", false),
        HajjUmrahChecklistItem("chk_8", "Medical & Hygiene", "Personal prescription medications and small first-aid kit", "Pain relievers, throat lozenges, band-aids, anti-chafing cream.", false),
        HajjUmrahChecklistItem("chk_9", "Spiritual Prep", "Pocket Dua book or digital bookmarked Du'as", "Personal list of prayers for family, friends, and Ummah.", false),
        HajjUmrahChecklistItem("chk_10", "Spiritual Prep", "Tawaf and Sa'i digital counter", "Used to track the 7 circuits accurately without distraction.", false)
    )
}

/**
 * State for interactive Tawaf counter (7 circuits around the Holy Kaaba).
 */
data class TawafTrackerState(
    val currentLap: Int = 1,
    val completedLaps: Int = 0,
    val isFinished: Boolean = false,
    val lapTimestamps: List<Long> = emptyList()
)

/**
 * State for interactive Sa'i counter (7 rounds between Safa and Marwah).
 */
data class SaiTrackerState(
    val currentRound: Int = 1,
    val completedRounds: Int = 0,
    val currentStation: String = "Safa", // "Safa" -> "Marwah"
    val isFinished: Boolean = false
)

data class HajjUmrahState(
    val selectedMainTab: Int = 0, // 0: Umrah Steps, 1: Hajj Guide, 2: Tawaf Counter, 3: Sa'i Counter, 4: Talbiyah & Duas, 5: Checklist
    val selectedStepDetail: PilgrimageStep? = null,
    val tawafState: TawafTrackerState = TawafTrackerState(),
    val saiState: SaiTrackerState = SaiTrackerState(),
    val checklist: List<HajjUmrahChecklistItem> = HajjUmrahData.DEFAULT_CHECKLIST,
    val isPlayingTalbiyah: Boolean = false
)
