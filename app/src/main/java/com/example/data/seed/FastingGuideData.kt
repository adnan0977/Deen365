package com.example.data.seed

data class FastingDua(
    val id: String,
    val title: String,
    val occasion: String,
    val arabicText: String,
    val transliteration: String,
    val translation: String,
    val reference: String
)

data class FastingRuleItem(
    val title: String,
    val ruling: String, // "Permissible", "Invalidates Fast", "Exempted", "Disliked"
    val explanation: String,
    val reference: String = ""
)

data class SunnahFastType(
    val name: String,
    val arabicName: String,
    val schedule: String,
    val reward: String,
    val reference: String
)

object FastingGuideData {

    val DUAS = listOf(
        FastingDua(
            id = "dua_iftar_primary",
            title = "Primary Iftar Supplication (At the Moment of Breaking Fast)",
            occasion = "Recited immediately upon tasting date or water at Maghrib",
            arabicText = "ذَهَبَ الظَّمَأُ وَابْتَلَّتِ الْعُرُوقُ وَثَبَتَ الأَجْرُ إِنْ شَاءَ اللَّهُ",
            transliteration = "Dhahaba adh-dhama'u wabtallatil-'urooqu wa thabatal-ajru in sha Allah",
            translation = "The thirst has gone, the veins are moistened, and the reward is confirmed, if Allah wills.",
            reference = "Sunan Abi Dawud 2357 (Hasan / Sahih)"
        ),
        FastingDua(
            id = "dua_iftar_traditional",
            title = "Traditional Iftar Dua of Trust & Provision",
            occasion = "Recited before or while breaking the fast",
            arabicText = "اللَّهُمَّ إِنِّي لَكَ صُمْتُ وَعَلَى رِزْقِكَ أَفْطَرْتُ",
            transliteration = "Allahumma inni laka sumtu wa 'ala rizqika aftart",
            translation = "O Allah, for You I have fasted, and with Your provision I have broken my fast.",
            reference = "Sunan Abi Dawud 2358"
        ),
        FastingDua(
            id = "dua_niyyah_fast",
            title = "Intention (Niyyah) for Fasting",
            occasion = "Made in the heart at night or before dawn (Fajr)",
            arabicText = "نَوَيْتُ صَوْمَ غَدٍ عَنْ أَدَاءِ فَرْضِ رَمَضَانَ هَذِهِ السَّنَةِ لِلَّهِ تَعَالَى",
            transliteration = "Nawaytu sawma ghadin 'an adaa'i fardi Ramadaana haadhihis-sanati lillaahi Ta'aala",
            translation = "I intend to fast tomorrow to fulfill the obligation of Ramadan this year for the sake of Allah the Almighty.",
            reference = "Formed in the heart (Sahih Muslim 1157)"
        ),
        FastingDua(
            id = "dua_iftar_guest",
            title = "Supplication for the Host Providing Iftar",
            occasion = "Recited by a guest for their host after eating",
            arabicText = "أَفْطَرَ عِنْدَكُمُ الصَّائِمُونَ وَأَكَلَ طَعَامَكُمُ الأَبْرَارُ وَصَلَّتْ عَلَيْكُمُ الْمَلاَئِكَةُ",
            transliteration = "Aftara 'indakumus-saa'imoona wa akala ta'aamakumul-abraaru wa sallat 'alaykumul-malaa'ikah",
            translation = "May fasting people break their fast with you, may the righteous eat your food, and may the angels send blessings upon you.",
            reference = "Sunan Abi Dawud 3854"
        ),
        FastingDua(
            id = "dua_laylatul_qadr",
            title = "Supplication of the Night of Decree (Laylat al-Qadr)",
            occasion = "Recommended during the last ten nights of Ramadan",
            arabicText = "اللَّهُمَّ إِنَّكَ عَفُوٌّ تُحِبُّ الْعَفْوَ فَاعْفُ عَنِّي",
            transliteration = "Allahumma innaka 'afuwwun tuhibbul-'afwa fa'fu 'anni",
            translation = "O Allah, You are Most Forgiving, and You love forgiveness; so forgive me.",
            reference = "Jami' at-Tirmidhi 3513"
        )
    )

    val STEP_BY_STEP_DAY = listOf(
        Pair(
            "1. Suhoor (Pre-Dawn Meal) & Barakah",
            "Eat a nutritious pre-dawn meal before the Fajr adhan. The Prophet ﷺ said: 'Take Suhoor, for indeed in Suhoor there is blessing (Barakah).' (Bukhari 1923). It is Sunnah to delay Suhoor until near the adhan."
        ),
        Pair(
            "2. Heartfelt Intention (Niyyah)",
            "Resolve in your heart to fast for Allah. For obligatory Ramadan fasts, intention must be established before Fajr. For voluntary (Sunnah) fasts, it can be made anytime before Zawwal (midday) if nothing has been eaten."
        ),
        Pair(
            "3. Guarding Tongue, Gaze & Limbs",
            "Fasting is not merely abstaining from food and drink; it is spiritual discipline. The Prophet ﷺ said: 'Whoever does not give up false speech and acting upon it, Allah has no need for his giving up food and drink.' (Bukhari 1903)."
        ),
        Pair(
            "4. The Golden Hour Before Iftar (Dua Window)",
            "The 30–60 minutes leading up to Maghrib are among the most sacred moments of the day. The Prophet ﷺ said: 'The supplication of the fasting person when he breaks his fast is not rejected.' (Ibn Majah 1753)."
        ),
        Pair(
            "5. Prompt Iftar (Sunnah Dates & Water)",
            "Hasten to break the fast as soon as the sun sets (Maghrib). Break your fast with fresh dates (Rutab), dry dates (Tamr), or water before praying Maghrib, reciting 'Dhahaba adh-dhama'u...'"
        ),
        Pair(
            "6. Night Prayers (Taraweeh & Tahajjud)",
            "Follow the fast with night standing. 'Whoever prays during the nights of Ramadan with faith and seeking reward, his past sins will be forgiven.' (Bukhari 2009)."
        )
    )

    val WHAT_BREAKS_FAST = listOf(
        FastingRuleItem(
            title = "Intentional Eating or Drinking",
            ruling = "Invalidates Fast",
            explanation = "Any ingestion of food, drink, or smoke into the body breaks the fast. Requires making up the day (Qada) and sincere repentance.",
            reference = "Surah Al-Baqarah 2:187"
        ),
        FastingRuleItem(
            title = "Deliberate Vomiting",
            ruling = "Invalidates Fast",
            explanation = "Inducing vomit intentionally breaks the fast and requires a make-up day. However, involuntary vomiting does NOT break the fast.",
            reference = "Abu Dawud 2380"
        ),
        FastingRuleItem(
            title = "Nutritive Injections / IV Drips",
            ruling = "Invalidates Fast",
            explanation = "Injections that nourish the body (glucose, nutritional vitamins) serve as a substitute for food and drink, thus breaking the fast.",
            reference = "Contemporary Fiqh Academies"
        ),
        FastingRuleItem(
            title = "Menstruation or Postnatal Bleeding",
            ruling = "Invalidates Fast",
            explanation = "If bleeding begins even a minute before sunset, the fast is broken. The woman is excused from fasting and makes up the day after Ramadan.",
            reference = "Sahih Muslim 335"
        )
    )

    val WHAT_DOES_NOT_BREAK = listOf(
        FastingRuleItem(
            title = "Unintentional / Forgetful Eating or Drinking",
            ruling = "Permissible",
            explanation = "If you forget you are fasting and consume food or water, continue fasting immediately! 'Allah has fed him and given him to drink.' No make-up is needed.",
            reference = "Sahih al-Bukhari 1933"
        ),
        FastingRuleItem(
            title = "Asthma Inhalers & Medical Sprays",
            ruling = "Permissible",
            explanation = "Inhalers vaporize gas to open airways; the minute droplets do not act as nutritional nourishment. Major scholars permit their emergency use while fasting.",
            reference = "Islamic Fiqh Academy"
        ),
        FastingRuleItem(
            title = "Eye Drops, Ear Drops & Contact Lenses",
            ruling = "Permissible",
            explanation = "Applying eye drops or ear drops does not break the fast, even if a subtle taste is perceived in the throat, as the eye is not an opening to the stomach.",
            reference = "Ibn Taymiyyah (Majmu' al-Fatawa)"
        ),
        FastingRuleItem(
            title = "Blood Tests & Involuntary Bleeding",
            ruling = "Permissible",
            explanation = "Routine blood draws for lab testing, nosebleeds, and accidental cuts do not break the fast. (Large-scale blood donations that induce severe weakness are disliked).",
            reference = "Bukhari 1936"
        ),
        FastingRuleItem(
            title = "Toothpaste & Miswak",
            ruling = "Permissible",
            explanation = "Using a Miswak (natural tooth-stick) is Sunnah at all times. Brushing with toothpaste is permissible provided nothing is swallowed.",
            reference = "Tirmidhi 725"
        ),
        FastingRuleItem(
            title = "Rinsing Mouth & Nose for Wudu",
            ruling = "Permissible",
            explanation = "Rinsing the mouth (Madmadah) and nose (Istinshaq) during Wudu is permissible, but avoid exaggerating or sniffing too deeply.",
            reference = "Abu Dawud 142"
        ),
        FastingRuleItem(
            title = "Showering & Swimming for Cooling",
            ruling = "Permissible",
            explanation = "Taking a bath or shower to relieve heat and fatigue is permissible as long as water is not swallowed.",
            reference = "Abu Dawud 2365"
        )
    )

    val EXEMPTIONS = listOf(
        FastingRuleItem(
            title = "The Traveler (Al-Musaafir)",
            ruling = "Exempted",
            explanation = "Travelers on a journey meeting Qasr distance (~80 km / 48 miles) may break their fast and make up the missed days later before next Ramadan.",
            reference = "Surah Al-Baqarah 2:185"
        ),
        FastingRuleItem(
            title = "The Temporarily Sick (Al-Mareed)",
            ruling = "Exempted",
            explanation = "Anyone with an acute illness that fasting would worsen or delay recovery from may break the fast and make up the days when healed.",
            reference = "Surah Al-Baqarah 2:184"
        ),
        FastingRuleItem(
            title = "Pregnant & Nursing Mothers",
            ruling = "Exempted",
            explanation = "If a mother fears for her health or the health and milk supply of her child, she may break her fast and make it up later without sin.",
            reference = "Sunan an-Nasa'i 2274"
        ),
        FastingRuleItem(
            title = "The Elderly & Chronically Ill",
            ruling = "Exempted (Pay Fidyah)",
            explanation = "Those unable to fast due to permanent illness or advanced age are exempt. Instead, they pay Fidyah: feeding one needy person per missed day.",
            reference = "Surah Al-Baqarah 2:184"
        )
    )

    val SUNNAH_FASTS = listOf(
        SunnahFastType(
            name = "Mondays and Thursdays",
            arabicName = "صِيَامُ الاِثْنَيْنِ وَالْخَمِيسِ",
            schedule = "Every week on Monday and Thursday",
            reward = "Deeds are presented to Allah on these two days. The Prophet ﷺ said: 'I love that my deeds be presented while I am fasting.' (Tirmidhi 747)",
            reference = "Jami' at-Tirmidhi 747"
        ),
        SunnahFastType(
            name = "The White Days (Ayyam al-Beed)",
            arabicName = "أَيَّامُ الْبِيضِ (١٣، ١٤، ١٥)",
            schedule = "13th, 14th, and 15th of every Islamic Hijri month",
            reward = "Fasting three days every month equals a lifetime of continuous fasting in reward! (Bukhari 1975)",
            reference = "Sahih al-Bukhari 1975"
        ),
        SunnahFastType(
            name = "Six Days of Shawwal",
            arabicName = "سِتَّةُ أَيَّامٍ مِنْ شَوَّالٍ",
            schedule = "Any 6 days during Shawwal (month after Ramadan)",
            reward = "Whoever fasts Ramadan followed by 6 days of Shawwal, it is as if he fasted the entire year! (Muslim 1164)",
            reference = "Sahih Muslim 1164"
        ),
        SunnahFastType(
            name = "Day of Arafah (9th Dhul Hijjah)",
            arabicName = "يَوْمُ عَرَفَةَ",
            schedule = "9th of Dhul Hijjah (for non-pilgrims)",
            reward = "Expiates the sins of the previous year and the coming year! (Muslim 1162)",
            reference = "Sahih Muslim 1162"
        ),
        SunnahFastType(
            name = "Day of Ashura & 9th Muharram",
            arabicName = "عَاشُورَاءُ وَتَاسُوعَاءُ",
            schedule = "10th of Muharram (preferably paired with 9th)",
            reward = "Expiates the minor sins of the entire past year. (Muslim 1162)",
            reference = "Sahih Muslim 1162"
        )
    )

    data class FastingSpiritualLevel(
        val levelNumber: Int,
        val title: String,
        val arabicTitle: String,
        val description: String,
        val practices: List<String>
    )

    val SPIRITUAL_LEVELS = listOf(
        FastingSpiritualLevel(
            levelNumber = 1,
            title = "The Fast of the Ordinary (Sawm al-Umum)",
            arabicTitle = "صَوْمُ العُمُومِ",
            description = "Abstaining from the physical desires of the stomach and the private parts from dawn until sunset.",
            practices = listOf(
                "Refraining from eating, drinking, and marital relations.",
                "Fulfills the basic legal requirement (Fiqh validity) of the fast.",
                "Minimum baseline obligation; without higher intention, may yield only physical hunger."
            )
        ),
        FastingSpiritualLevel(
            levelNumber = 2,
            title = "The Fast of the Pious (Sawm al-Khusus)",
            arabicTitle = "صَوْمُ الخُصُوصِ",
            description = "Restraining the limbs, eyes, tongue, ears, and hands from all blameworthy actions and idle pursuits.",
            practices = listOf(
                "Lowering the gaze from forbidden sights and vain media distractions.",
                "Guarding the tongue from backbiting (Gheebah), falsehood, insults, and arguing.",
                "Closing the ears to malicious rumors, slander, and frivolous gossip.",
                "Withholding hands and feet from injustice, and eating only moderate Halal food at Iftar."
            )
        ),
        FastingSpiritualLevel(
            levelNumber = 3,
            title = "The Fast of the Elite (Sawm Khusus al-Khusus)",
            arabicTitle = "صَوْمُ خُصُوصِ الخُصُوصِ",
            description = "Fasting of the heart from worldly anxieties, doubts, and any thought that distracts from Allah.",
            practices = listOf(
                "Total absorption of consciousness in divine remembrance and sacred reflection.",
                "Regarding any thought of worldly vanity as a temporary disruption of the fast.",
                "Complete trust (Tawakkul) in Allah's decree and boundless joy in His servitude."
            )
        )
    )

    data class CompensationRule(
        val type: String,
        val arabicTerm: String,
        val whenApplicable: String,
        val requirement: String,
        val reference: String
    )

    val COMPENSATION_RULES = listOf(
        CompensationRule(
            type = "Qada (Make-Up Fasting)",
            arabicTerm = "قَضَاءُ الصِّيَامِ",
            whenApplicable = "For missed days due to travel, temporary illness, menstruation, or post-natal bleeding.",
            requirement = "Fast 1 day for each missed day before the following Ramadan arrives. Days can be fasted continuously or separately.",
            reference = "Surah Al-Baqarah (2:184)"
        ),
        CompensationRule(
            type = "Fidyah (Expiation for Inability)",
            arabicTerm = "فِدْيَةُ الصِّيَامِ",
            whenApplicable = "For elderly believers or chronically ill persons who cannot fast and have no realistic prospect of recovery.",
            requirement = "Feed one needy person (Miskeen) two fulfilling meals or approximately 1.5 kg of staple food (grain/dates) or local monetary equivalent per missed day.",
            reference = "Surah Al-Baqarah (2:184)"
        ),
        CompensationRule(
            type = "Kaffarah (Severe Expiation)",
            arabicTerm = "كَفَّارَةُ الجِمَاعِ فِي رَمَضَانَ",
            whenApplicable = "For deliberately violating the sanctity of an obligatory Ramadan day through consensual intercourse while fasting.",
            requirement = "Free a believing slave (historically); if unable, fast 60 consecutive days without interruption; if unable, feed 60 needy persons.",
            reference = "Sahih al-Bukhari 1936"
        )
    )

    val PROHIBITED_FASTING_DAYS = listOf(
        Pair("Eid al-Fitr (1st Shawwal)", "Strictly forbidden (Haram) to fast on the celebration day of breaking the Ramadan fast. It is a day of joy and feast from Allah."),
        Pair("Eid al-Adha (10th Dhul Hijjah)", "Forbidden to fast on the Day of Sacrifice; believers enjoy the sacrificial feast."),
        Pair("Days of Tashreeq (11th, 12th, 13th Dhul Hijjah)", "Forbidden to fast during the 3 days after Eid al-Adha. The Prophet ﷺ said: 'The days of Tashreeq are days of eating, drinking, and remembering Allah.' (Muslim 1141)"),
        Pair("Singling Out Friday or Saturday Alone", "Disliked (Makruh) to fast only on Friday without fasting Thursday before it or Saturday after it, unless it coincides with a customary date like Arafah or Ashura."),
        Pair("Day of Doubt (Yawm ash-Shakk)", "Disliked to fast the 30th of Sha'ban in anticipation of Ramadan before moon sighting is confirmed.")
    )

    val HEALTH_CONSIDERATIONS = listOf(
        Pair("1. Smart Hydration Schedule", "Drink 2 to 3 liters of water gradually between Iftar and bedtime. Avoid chugging cold water all at once; sip steadily and incorporate hydrating fruits (watermelon, cucumbers, oranges)."),
        Pair("2. Suhoor Nutrient Density", "Focus on complex carbohydrates and slow-burning proteins: whole grain oats, eggs, Greek yogurt, chia seeds, and dates. Avoid salty, processed, or fried foods that cause daytime dehydration."),
        Pair("3. Iftar Blood Sugar Management", "Break the fast with 1 to 3 dates and warm water as taught by the Sunnah. Pray Maghrib first before eating a balanced meal; this prevents glucose spikes, acid reflux, and lethargy."),
        Pair("4. Chronic Conditions & Diabetes", "Believers with Type 1 diabetes, uncontrolled hypertension, or advanced kidney disease should consult their physician. Islam explicitly relieves those whose medical conditions risk severe harm.")
    )
}
