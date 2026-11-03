package com.example.data.seed

data class CaliphProfile(
    val id: String,
    val nameEnglish: String,
    val nameArabic: String,
    val title: String,
    val reignYears: String,
    val lifespan: String,
    val virtues: List<String>,
    val majorAchievements: List<String>,
    val famousSpeechQuote: String,
    val martyrdomOrPassing: String,
    val hadithVirtue: String
)

data class SahabiProfile(
    val id: String,
    val nameEnglish: String,
    val nameArabic: String,
    val honorific: String,
    val category: String, // "Ashara Mubashara", "Mothers of Believers", "Noble Companions"
    val virtuesAndRole: String,
    val prominentAction: String,
    val famousQuote: String
)

data class IslamicHistoryEra(
    val id: String,
    val name: String,
    val period: String,
    val capital: String,
    val keyMilestones: List<String>,
    val description: String
)

object CaliphsAndSahabaSeedData {
    val RIGHTLY_GUIDED_CALIPHS: List<CaliphProfile> = listOf(
        CaliphProfile(
            id = "abu_bakr",
            nameEnglish = "Abu Bakr as-Siddiq (RA)",
            nameArabic = "أبو بكر الصديق",
            title = "As-Siddiq (The Truthful) & Al-Atiq",
            reignYears = "632 - 634 CE (11 - 13 AH)",
            lifespan = "573 - 634 CE (63 years)",
            virtues = listOf(
                "First adult male to accept Islam without hesitation",
                "Sole companion of the Prophet ﷺ during the Hijrah Cave (Thawr)",
                "Spent his entire fortune to ransom and free enslaved Muslims, including Bilal"
            ),
            majorAchievements = listOf(
                "Steered the Ummah with steely resolve upon the passing of the Prophet ﷺ",
                "Decisive victory in the Riddah (Apostasy) wars preserving the faith",
                "Commissioned the historic first single-volume compilation of the Holy Quran under Zaid ibn Thabit"
            ),
            famousSpeechQuote = "O people! I have been given authority over you, though I am not the best among you. If I do well, assist me; and if I do wrong, set me right.",
            martyrdomOrPassing = "Passed away naturally from illness in Madinah; buried beside his beloved Prophet Muhammad ﷺ.",
            hadithVirtue = "'If I were to take an intimate friend (Khalil) other than my Lord, I would have taken Abu Bakr.' (Sahih Bukhari)"
        ),
        CaliphProfile(
            id = "umar",
            nameEnglish = "Umar ibn al-Khattab (RA)",
            nameArabic = "عمر بن الخطاب",
            title = "Al-Faruq (The Distinguisher between Truth and Falsehood)",
            reignYears = "634 - 644 CE (13 - 23 AH)",
            lifespan = "584 - 644 CE (60 years)",
            virtues = listOf(
                "Conversion answered the Prophet's du'a: 'O Allah, strengthen Islam with Umar'",
                "Satan would take another valley whenever Umar walked upon a path",
                "Revered for unmatched justice, personal asceticism, and walking the streets at night"
            ),
            majorAchievements = listOf(
                "Established the Islamic Hijri calendar system dating from the Prophet's migration",
                "Created the first structured civil administration, police (Al-Asas), and public treasury (Bayt al-Mal)",
                "Peacefully received the keys of Jerusalem (Al-Quds) and secured the Pact of Umar guaranteeing Christian religious freedom"
            ),
            famousSpeechQuote = "If a lost sheep dies on the banks of the Euphrates, I fear that Allah will hold Umar accountable for it.",
            martyrdomOrPassing = "Stabbed while leading Fajr prayer in the Prophet's Mosque by Abu Lu'lu'ah; buried beside the Prophet ﷺ and Abu Bakr.",
            hadithVirtue = "'Never does Satan find you taking a path, O Umar, except that he takes another path.' (Sahih Bukhari)"
        ),
        CaliphProfile(
            id = "uthman",
            nameEnglish = "Uthman ibn Affan (RA)",
            nameArabic = "عثمان بن عفان",
            title = "Dhun-Nurayn (Possessor of the Two Lights)",
            reignYears = "644 - 656 CE (23 - 35 AH)",
            lifespan = "576 - 656 CE (80 years)",
            virtues = listOf(
                "Married two daughters of the Prophet ﷺ (Ruqayyah and Umm Kulthum)",
                "Known for legendary modesty; even the angels felt shy before Uthman",
                "Personally financed the Army of Hardship (Tabuk) and purchased the well of Rumah for the public"
            ),
            majorAchievements = listOf(
                "Standardized and distributed the official Mus-haf of the Holy Quran across the Islamic world",
                "Established the first Islamic maritime naval fleet safeguarding Mediterranean coasts",
                "Expanded the Prophet's Mosque in Madinah and Masjid al-Haram in Makkah"
            ),
            famousSpeechQuote = "If our hearts were truly pure, they would never have enough of the words of our Lord (the Quran).",
            martyrdomOrPassing = "Martyred inside his home in Madinah while reciting Surah Al-Baqarah, refusing to shed Muslim blood in self-defense.",
            hadithVirtue = "'Shall I not feel shy before a man whom even the angels feel shy before?' (Sahih Muslim)"
        ),
        CaliphProfile(
            id = "ali",
            nameEnglish = "Ali ibn Abi Talib (RA)",
            nameArabic = "علي بن أبي طالب",
            title = "Asadullah (The Lion of Allah) & Bab Madinat al-Ilm",
            reignYears = "656 - 661 CE (35 - 40 AH)",
            lifespan = "600 - 661 CE (61 years)",
            virtues = listOf(
                "Cousin and son-in-law of the Prophet ﷺ, married to Fatimah az-Zahra",
                "First child to embrace Islam; slept in the Prophet's bed during the night of Hijrah",
                "Standard-bearer in heroic single combats at Badr, Uhud, and the fortress of Khaybar"
            ),
            majorAchievements = listOf(
                "Paragon of eloquence, legal jurisprudence, and Arabic grammar codification",
                "Championed judicial impartiality, even standing before a Qadi equal to a non-Muslim citizen",
                "Preserved the high ethical governance standards recorded in his letters and wisdom"
            ),
            famousSpeechQuote = "Live amongst people in such a manner that if you die they weep over you, and if you live they yearn for your company.",
            martyrdomOrPassing = "Martyred in Kufa during Fajr prayer in Ramadan by Ibn Muljam; buried in Najaf.",
            hadithVirtue = "'You are to me in the position of Aaron to Moses, except that there is no prophet after me.' (Sahih Bukhari)"
        )
    )

    val ASHARA_AND_SAHABA: List<SahabiProfile> = listOf(
        SahabiProfile(
            id = "talhah",
            nameEnglish = "Talhah ibn Ubaydullah (RA)",
            nameArabic = "طلحة بن عبيد الله",
            honorific = "The Living Martyr",
            category = "Ashara Mubashara",
            virtuesAndRole = "Shielded the Prophet ﷺ with his bare hands at Uhud, suffering over 70 wounds and a paralyzed hand.",
            prominentAction = "The Prophet ﷺ remarked: 'Whoever wishes to look at a martyr walking upon the earth, let him look at Talhah.'",
            famousQuote = "We give all wealth so that the religion of Muhammad may prosper."
        ),
        SahabiProfile(
            id = "zubayr",
            nameEnglish = "Az-Zubayr ibn al-Awwam (RA)",
            nameArabic = "الزبير بن العوام",
            honorific = "Disciple (Hawari) of the Prophet",
            category = "Ashara Mubashara",
            virtuesAndRole = "First person to draw a sword for the cause of Islam; cousin of the Prophet ﷺ and nephew of Khadijah.",
            prominentAction = "Braved enemy reconnaissance missions alone during the Battle of the Trench.",
            famousQuote = "The Prophet ﷺ said: 'Every prophet has a disciple, and my disciple is Az-Zubayr.'"
        ),
        SahabiProfile(
            id = "abdurrahman",
            nameEnglish = "Abdur Rahman ibn Awf (RA)",
            nameArabic = "عبد الرحمن بن عوف",
            honorific = "The Generous Merchant of Faith",
            category = "Ashara Mubashara",
            virtuesAndRole = "Arrived in Madinah with nothing, requested only directions to the marketplace, and became a billionaire who financed armies and freed hundreds of slaves.",
            prominentAction = "Bequeathed 400 gold dinars to every surviving veteran of Badr in his will.",
            famousQuote = "Show me where the market is, and Allah will bless honest trade."
        ),
        SahabiProfile(
            id = "saad",
            nameEnglish = "Sa'd ibn Abi Waqqas (RA)",
            nameArabic = "سعد بن أبي وقاص",
            honorific = "The Archer of Islam",
            category = "Ashara Mubashara",
            virtuesAndRole = "Maternal uncle of the Prophet ﷺ whose supplications were guaranteed accepted by the Prophet's du'a.",
            prominentAction = "Commander-in-Chief at the decisive Battle of Al-Qadisiyyah and founder of Kufa.",
            famousQuote = "Shoot, O Sa'd! May my father and mother be sacrificed for you! (Prophetic praise at Uhud)"
        ),
        SahabiProfile(
            id = "abu_ubaydah",
            nameEnglish = "Abu Ubaydah ibn al-Jarrah (RA)",
            nameArabic = "أبو عبيدة بن الجراح",
            honorific = "Trustee of the Ummah (Amin al-Ummah)",
            category = "Ashara Mubashara",
            virtuesAndRole = "Renowned for peerless trustworthiness and humility; pulled helmet links embedded in the Prophet's cheek with his front teeth at Uhud.",
            prominentAction = "Supreme commander in the Levant whose quarters contained only a water skin and a shield.",
            famousQuote = "The Prophet ﷺ said: 'Every nation has an Ameen (trustee), and the trustee of this nation is Abu Ubaydah.'"
        ),
        SahabiProfile(
            id = "khadijah",
            nameEnglish = "Khadijah bint Khuwaylid (RA)",
            nameArabic = "خديجة بنت خويلد",
            honorific = "Mother of the Believers & At-Tahirah",
            category = "Mothers of Believers",
            virtuesAndRole = "First human to believe in the Prophet ﷺ, comforting him when revelation first descended at Cave Hira and supporting Islam with all her wealth.",
            prominentAction = "Received personal greetings of peace (Salam) from Allah Almighty delivered by Angel Jibril.",
            famousQuote = "By Allah, Allah will never disgrace you! You keep good relations with kith and kin, you help the poor, and you serve guests."
        ),
        SahabiProfile(
            id = "aisha",
            nameEnglish = "Aisha bint Abi Bakr (RA)",
            nameArabic = "عائشة بنت أبي بكر",
            honorific = "As-Siddiqah & Scholar of the Ummah",
            category = "Mothers of Believers",
            virtuesAndRole = "Foremost female jurist, scholar, and narrator of prophetic Sunnah (over 2,210 hadiths), teaching senior Sahaba the intricate rulings of Islam.",
            prominentAction = "Her bedroom in Madinah was chosen by Allah as the final resting place of the Messenger of Allah ﷺ.",
            famousQuote = "When asked about the character of the Prophet ﷺ: 'His character was the Quran.'"
        ),
        SahabiProfile(
            id = "bilal",
            nameEnglish = "Bilal ibn Rabah (RA)",
            nameArabic = "بلال بن رباح",
            honorific = "Mu'adhin of the Messenger of Allah",
            category = "Noble Companions",
            virtuesAndRole = "Endured agonizing torture under heavy boulders in the burning desert sand repeating 'Ahad, Ahad' (One, One).",
            prominentAction = "First Mu'adhin in Islamic history; ascended the roof of the Kaaba at the Conquest of Makkah to proclaim the Adhan.",
            famousQuote = "The Prophet ﷺ said: 'O Bilal, I heard the sound of your footsteps before me in Paradise.'"
        ),
        SahabiProfile(
            id = "salman",
            nameEnglish = "Salman al-Farsi (RA)",
            nameArabic = "سلمان الفارسي",
            honorific = "The Seeker of Truth",
            category = "Noble Companions",
            virtuesAndRole = "Traveled across continents from Persia through Syria and Arabia in search of the true prophet, enduring enslavement until freed by the Prophet ﷺ.",
            prominentAction = "Proposed digging the defensive trench (Khandaq) around Madinah that saved the city from the allied siege.",
            famousQuote = "The Prophet ﷺ said: 'Salman is of us, the People of the Household (Ahl al-Bayt).'"
        ),
        SahabiProfile(
            id = "khalid",
            nameEnglish = "Khalid ibn al-Walid (RA)",
            nameArabic = "خالد بن الوليد",
            honorific = "Sayfullah (The Drawn Sword of Allah)",
            category = "Noble Companions",
            virtuesAndRole = "Undefeated military genius in over 100 battles who turned military disaster into victory through master tactical maneuvering.",
            prominentAction = "Broke nine swords in his hand at the Battle of Mu'tah while defending the retreating Muslim vanguard.",
            famousQuote = "There is not a spot on my body the size of a handspan without a spear wound, sword strike, or arrow puncture."
        )
    )

    val ISLAMIC_ERAS: List<IslamicHistoryEra> = listOf(
        IslamicHistoryEra(
            id = "prophetic",
            name = "The Prophetic Dawn",
            period = "610 - 632 CE",
            capital = "Madinah al-Munawwarah",
            keyMilestones = listOf(
                "Descent of the Holy Quran at Cave Hira (610 CE)",
                "The Hijrah (Migration) to Madinah establishing the first Islamic state (622 CE)",
                "The peaceful Conquest of Makkah and cleansing of the Kaaba (630 CE)",
                "The Farewell Pilgrimage delivering the universal charter of human rights (632 CE)"
            ),
            description = "The foundational era where divine revelation transformed fragmented tribal Arabia into a global fraternal brotherhood grounded in monotheism, justice, and compassion."
        ),
        IslamicHistoryEra(
            id = "rashidun",
            name = "The Rashidun Caliphate",
            period = "632 - 661 CE",
            capital = "Madinah & Kufa",
            keyMilestones = listOf(
                "Abu Bakr preserves Islamic unity and compiles the Mus-haf",
                "Umar establishes justice, public welfare institutions, and enters Jerusalem peacefully",
                "Uthman standardizes the Quranic codex distributed to world centers",
                "Ali upholds judicial integrity and deep spiritual wisdom"
            ),
            description = "The golden thirty-year model of consultative Islamic governance (Shura), unmatched social justice, rule of law, and rapid geographic expansion from North Africa to Persia."
        ),
        IslamicHistoryEra(
            id = "umayyad",
            name = "The Umayyad Caliphate",
            period = "661 - 750 CE",
            capital = "Damascus (Dimashq)",
            keyMilestones = listOf(
                "Establishment of Arabic as the official international lingua franca of administration",
                "Construction of architectural marvels: The Dome of the Rock in Jerusalem and Umayyad Mosque in Damascus",
                "The righteous rule of Umar ibn Abd al-Aziz (Umar II) eliminating poverty",
                "Arrival of Islam into Andalusia (Spain) and the Indus Valley (Sindh)"
            ),
            description = "An era of continent-spanning administration connecting Spain to China, monumental Islamic architecture, and standardizing Arabic coinage and state documentation."
        ),
        IslamicHistoryEra(
            id = "abbasid",
            name = "The Abbasid Golden Age",
            period = "750 - 1258 CE",
            capital = "Baghdad (The Round City)",
            keyMilestones = listOf(
                "Establishment of the House of Wisdom (Bayt al-Hikmah) in Baghdad",
                "Compilation of the Sahih Hadith canonical collections (Bukhari, Muslim, Tirmidhi)",
                "Scientific revolutions: Al-Khwarizmi invents Algebra, Ibn al-Haytham pioneers Optics, Ibn Sina (Avicenna) revolutionizes Medicine",
                "Golden Age of Islamic Jurisprudence with the Four Great Imams (Abu Hanifa, Malik, Shafi'i, Ahmad)"
            ),
            description = "A five-century intellectual apex where science, theology, philosophy, astronomy, and mathematics flourished, lighting the way for human civilization."
        ),
        IslamicHistoryEra(
            id = "andalus",
            name = "Islamic Andalusia (Al-Andalus)",
            period = "711 - 1492 CE",
            capital = "Cordoba & Granada",
            keyMilestones = listOf(
                "Cordoba becomes Europe's leading metropolis with 70 libraries and paved street lamps",
                "Construction of the Great Mosque of Cordoba and the Alhambra Palace in Granada",
                "Scholarly giants: Ibn Rushd (Averroes), Ibn Hazm, and Al-Qurtubi",
                "Peaceful coexistence (Convivencia) fostering unprecedented interfaith intellectual collaboration"
            ),
            description = "The dazzling European beacon of learning, botanical agriculture, courtyard poetry, and philosophy that sparked the European Renaissance."
        ),
        IslamicHistoryEra(
            id = "ottoman",
            name = "The Ottoman Caliphate",
            period = "1299 - 1924 CE",
            capital = "Istanbul (Constantinople)",
            keyMilestones = listOf(
                "Conquest of Constantinople by Sultan Mehmed II (Fatih) fulfilling prophetic prophecy (1453 CE)",
                "Golden Age under Suleiman the Magnificent codifying imperial law",
                "Master Sinan constructs timeless architectural gems (Suleymaniye, Selimiye)",
                "Safe sanctuary provided to Jewish refugees expelled during the Spanish Inquisition"
            ),
            description = "A multi-ethnic six-century maritime and continental caliphate bridging Europe, Asia, and Africa with enduring institutional and architectural legacies."
        )
    )
}
