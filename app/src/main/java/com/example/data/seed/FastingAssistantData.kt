package com.example.data.seed

data class FastingPhaseTip(
    val phaseId: String, // "suhoor", "midday", "iftar"
    val phaseTitle: String,
    val phaseTimeRange: String,
    val iconEmoji: String,
    val keyNutritionGuidelines: List<String>,
    val propheticPractice: String,
    val recommendedSupplication: String,
    val spiritualReflection: String
)

data class SunnahFastDay(
    val title: String,
    val dayDescription: String,
    val rewardHadith: String,
    val nextUpcomingDate: String
)

data class RamadanDayPlan(
    val dayNumber: Int,
    val ashraStage: String, // "Mercy (Days 1-10)", "Forgiveness (Days 11-20)", "Deliverance from Fire (Days 21-30)"
    val spiritualGoal: String,
    val dailyActionDeed: String,
    val reflectionAyah: String
)

object FastingAssistantData {
    val PHASES: List<FastingPhaseTip> = listOf(
        FastingPhaseTip(
            phaseId = "suhoor",
            phaseTitle = "Pre-Fast Suhoor (Nourishment & Hydration)",
            phaseTimeRange = "3:30 AM - 4:45 AM (Before Fajr)",
            iconEmoji = "🥣",
            keyNutritionGuidelines = listOf(
                "Complex carbohydrates with high fiber (oatmeal, whole wheat roti, barley) for sustained 8-hour glucose release.",
                "High-quality protein (boiled eggs, Greek yogurt, lentils) to prevent muscle breakdown and maintain satiety.",
                "Hydrate with 2-3 glasses of water spaced out; avoid salty pickles and caffeinated drinks that trigger thirst."
            ),
            propheticPractice = "The Prophet ﷺ said: 'Take Suhoor, for indeed there is blessing (Barakah) in Suhoor.' Delaying Suhoor until close to Fajr is Sunnah.",
            recommendedSupplication = "نَوَيْتُ صَوْمَ غَدٍ عَنْ أَدَاءِ فَرْضِ رَمَضَانَ (I intend to fast tomorrow in fulfillment of the obligation of Ramadan)",
            spiritualReflection = "Suhoor is not merely physical fuel; standing at dawn opens the divine portal where Allah forgives those who seek His pardon."
        ),
        FastingPhaseTip(
            phaseId = "midday",
            phaseTitle = "Midday Vitality & Spiritual Focus",
            phaseTimeRange = "11:00 AM - 3:30 PM (Peak Fasting Hours)",
            iconEmoji = "☀️",
            keyNutritionGuidelines = listOf(
                "Conserve physical stamina; avoid unnecessary intense heat exposure or heavy exertion.",
                "Take a 15-20 minute restorative Sunnah midday nap (Qaylulah) to refresh brain alertness.",
                "Refresh face with cool water during Dhuhr and Asr Wudu (avoiding swallowing water)."
            ),
            propheticPractice = "The Prophet ﷺ said: 'Fasting is a shield. When one of you is fasting, let him not speak obscenely or act foolishly. If someone insults him, let him say: I am fasting.'",
            recommendedSupplication = "اللَّهُمَّ إِنِّي صَائِمٌ (O Allah, indeed I am fasting)",
            spiritualReflection = "The pangs of hunger are a gentle spiritual reminder of the poor and a training ground for taming the soul's desires."
        ),
        FastingPhaseTip(
            phaseId = "iftar",
            phaseTitle = "Post-Fast Iftar Recovery & Taraweeh Prep",
            phaseTimeRange = "6:30 PM - 9:30 PM (Sunset & Night)",
            iconEmoji = "🌙",
            keyNutritionGuidelines = listOf(
                "Break fast immediately at Maghrib with 1-3 soft dates (Rutab) and a warm glass of water for instant natural fructose absorption.",
                "Eat slowly; avoid heavy deep-fried oily foods immediately. Wait 15 minutes before having the main dinner.",
                "Include light soups, grilled proteins, and electrolyte-rich fluids (coconut water, laban) before Taraweeh prayer."
            ),
            propheticPractice = "The Prophet ﷺ broke his fast with fresh dates before praying; if unavailable, dried dates, or sips of pure water.",
            recommendedSupplication = "ذَهَبَ الظَّمَأُ وَابْتَلَّتِ الْعُرُوقُ وَثَبَتَ الْأَجْرُ إِنْ شَاءَ اللَّهُ (The thirst is gone, the veins are moistened, and the reward is confirmed, if Allah wills)",
            spiritualReflection = "The supplication of the fasting person at the moment of Iftar is never rejected by Allah. Ask for yourself and the Ummah."
        )
    )

    val SUNNAH_DAYS: List<SunnahFastDay> = listOf(
        SunnahFastDay(
            title = "Monday & Thursday Fasting",
            dayDescription = "Weekly Sunnah observed year-round",
            rewardHadith = "The Prophet ﷺ said: 'Deeds are presented to Allah on Mondays and Thursdays, and I love that my deeds be presented while I am fasting.' (Tirmidhi)",
            nextUpcomingDate = "Every Monday & Thursday"
        ),
        SunnahFastDay(
            title = "The White Days (Ayyam al-Beed)",
            dayDescription = "The 13th, 14th, and 15th of every lunar Hijri month",
            rewardHadith = "Fasting three days every month is equivalent to fasting the entire year in reward. (Bukhari & Muslim)",
            nextUpcomingDate = "13th - 15th of each Hijri Month"
        ),
        SunnahFastDay(
            title = "Day of 'Arafah (9th Dhul-Hijjah)",
            dayDescription = "For those not performing Hajj",
            rewardHadith = "Expolidates the sins of the preceding year and the coming year. (Sahih Muslim)",
            nextUpcomingDate = "9th Dhul-Hijjah"
        ),
        SunnahFastDay(
            title = "Day of 'Ashura (10th Muharram)",
            dayDescription = "Along with the 9th or 11th of Muharram",
            rewardHadith = "I hope that Allah will accept it as an expiation for the sins of the previous year. (Sahih Muslim)",
            nextUpcomingDate = "9th - 10th Muharram"
        )
    )

    val RAMADAN_30_DAYS: List<RamadanDayPlan> = listOf(
        RamadanDayPlan(1, "Mercy (Days 1-10)", "Renew intention (Ikhlas) for Allah's sake", "Feed someone fasting or give secret charity", "2:183"),
        RamadanDayPlan(2, "Mercy (Days 1-10)", "Establish complete focus in 5 daily prayers", "Read Juz 2 with meaning", "2:185"),
        RamadanDayPlan(3, "Mercy (Days 1-10)", "Restrain the tongue from negative speech", "Send blessings upon the Prophet 100 times", "33:56"),
        RamadanDayPlan(11, "Forgiveness (Days 11-20)", "Seek heartfelt forgiveness for past sins", "Make 100 Istighfar at Suhoor time", "3:135"),
        RamadanDayPlan(15, "Forgiveness (Days 11-20)", "Reconcile with family and friends", "Call a relative you have not spoken to", "4:1"),
        RamadanDayPlan(21, "Deliverance (Days 21-30)", "Seek Laylat al-Qadr in odd nights", "Recite: Allahumma innaka 'Afuwwun tuhibbul-'afwa fa'fu 'anni", "97:1-5"),
        RamadanDayPlan(27, "Deliverance (Days 21-30)", "Night of Power & intense devotion", "Dedicate the entire night to prayer and tears", "97:3")
    )
}
