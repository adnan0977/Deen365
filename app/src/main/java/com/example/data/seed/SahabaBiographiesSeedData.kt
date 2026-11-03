package com.example.data.seed

data class LinkedHadithRef(
    val collection: String,
    val hadithNumber: Int,
    val narrator: String,
    val arabicText: String,
    val translation: String,
    val grade: String,
    val contextDescription: String
)

data class DetailedSahabiBiography(
    val id: String,
    val nameEnglish: String,
    val nameArabic: String,
    val honorificTitle: String,
    val category: String, // "Ashara Mubashara", "Mothers of Believers", "Pioneers & Martyrs"
    val lifespan: String,
    val conversionOrder: String,
    val majorTrials: List<String>,
    val milestonesAndService: List<String>,
    val lifeStoryChronicle: String,
    val famousQuote: String,
    val linkedHadiths: List<LinkedHadithRef>
)

object SahabaBiographiesSeedData {
    val ALL_SAHABA: List<DetailedSahabiBiography> = listOf(
        // 1. Abu Bakr
        DetailedSahabiBiography(
            id = "abu_bakr_detailed",
            nameEnglish = "Abu Bakr as-Siddiq (RA)",
            nameArabic = "أبو بكر الصديق",
            honorificTitle = "As-Siddiq • The Truthful & First Caliph",
            category = "Ashara Mubashara",
            lifespan = "573 - 634 CE (63 years)",
            conversionOrder = "First adult free male to accept Islam without hesitation",
            majorTrials = listOf(
                "Enduring savage physical beatings near the Kaaba in early Makkan da'wah",
                "Ransoming persecuted slaves like Bilal with all his personal wealth",
                "Shielding the Prophet ﷺ in the dark Cave of Thawr during the Hijrah",
                "Holding the Ummah together upon the devastating passing of the Prophet ﷺ"
            ),
            milestonesAndService = listOf(
                "Sole companion honored by Allah in the Quran: 'The second of the two when they were in the cave'",
                "Appointed by the Prophet ﷺ to lead the congregational prayers during his final illness",
                "Commissioned the historic first single-volume codification of the Holy Quran",
                "Preserved the territorial integrity of Islam in the Riddah (Apostasy) wars"
            ),
            lifeStoryChronicle = """
Born Abdullah ibn Abi Quhafah into the noble Banu Taym clan of Quraysh, he was a wealthy, universally respected merchant known for honesty and an upright aversion to idol worship and alcohol even before Islam.

When Muhammad ﷺ received the first divine revelation, he presented Islam to Abu Bakr, who embraced it instantaneously without an ounce of doubt. He earned the immortal title 'As-Siddiq' (The Verifier of Truth) when he unconditionally affirmed the Night Journey and Heavenly Ascension (Al-Isra wal-Mi'raj) while the pagans mocked.

During his two-year caliphate, he embodied humility: he continued milking the goats of widows in his neighborhood and allocated himself only a meager stipend from the public treasury. On his deathbed, he returned whatever surplus remained in his possession to Bayt al-Mal.
            """.trimIndent(),
            famousQuote = "O people! I have been given authority over you, though I am not the best among you. If I do well, assist me; and if I do wrong, set me right.",
            linkedHadiths = listOf(
                LinkedHadithRef(
                    collection = "bukhari",
                    hadithNumber = 3654,
                    narrator = "Abu Sa'id al-Khudri (RA)",
                    arabicText = "إِنَّ مِنْ أَمَنِّ النَّاسِ عَلَىَّ فِي صُحْبَتِهِ وَمَالِهِ أَبَا بَكْرٍ، وَلَوْ كُنْتُ مُتَّخِذًا خَلِيلاً غَيْرَ رَبِّي لاَتَّخَذْتُ أَبَا بَكْرٍ",
                    translation = "The Prophet ﷺ said: 'Of all the people who have favored me most in company and wealth is Abu Bakr. If I were to take an intimate friend (Khalil) other than my Lord, I would have taken Abu Bakr.'",
                    grade = "Sahih al-Bukhari",
                    contextDescription = "Prophetic praise honoring Abu Bakr's unmatched sacrifice of life and fortune."
                ),
                LinkedHadithRef(
                    collection = "muslim",
                    hadithNumber = 2382,
                    narrator = "Abu Hurairah (RA)",
                    arabicText = "مَنْ أَصْبَحَ مِنْكُمُ الْيَوْمَ صَائِمًا؟ قَالَ أَبُو بَكْرٍ أَنَا. قَالَ: فَمَنْ شَيَّعَ مِنْكُمُ الْيَوْمَ جَنَازَةً؟ قَالَ أَبُو بَكْرٍ أَنَا. قَالَ: فَمَنْ أَطْعَمَ مِنْكُمُ الْيَوْمَ مِسْكِينًا؟ قَالَ أَبُو بَكْرٍ أَنَا. قَالَ: فَمَنْ عَادَ مِنْكُمُ الْيَوْمَ مَرِيضًا؟ قَالَ أَبُو بَكْرٍ أَنَا",
                    translation = "The Prophet ﷺ asked: 'Who among you is fasting today?' Abu Bakr said: 'I am.' He asked: 'Who visited a sick person today?' Abu Bakr said: 'I did.' The Prophet ﷺ said: 'These qualities never combine in a person except that he enters Paradise.'",
                    grade = "Sahih Muslim",
                    contextDescription = "Exemplary daily embodiment of righteousness and spontaneous charitable deeds."
                )
            )
        ),

        // 2. Umar ibn al-Khattab
        DetailedSahabiBiography(
            id = "umar_detailed",
            nameEnglish = "Umar ibn al-Khattab (RA)",
            nameArabic = "عمر بن الخطاب",
            honorificTitle = "Al-Faruq • The Distinguisher between Truth and Falsehood",
            category = "Ashara Mubashara",
            lifespan = "584 - 644 CE (60 years)",
            conversionOrder = "40th man to accept Islam, openly declaring it at the Kaaba",
            majorTrials = listOf(
                "Initial intense tribal opposition before his miraculous conversion",
                "Deep sorrow at the passing of the Prophet ﷺ",
                "Bearing the immense accountability of ruling a continent spanning from Egypt to Persia"
            ),
            milestonesAndService = listOf(
                "Established the Islamic Hijri calendar system dating from the Prophet's migration",
                "Created the first structured civil administration, judicial courts, and pensions",
                "Peacefully received the keys of Jerusalem (Al-Quds) and secured the historic Pact of Umar",
                "Instituted regular congregational Taraweeh prayers behind a single Imam in Ramadan"
            ),
            lifeStoryChronicle = """
Known in his youth for physical prowess, wrestling, and commanding eloquence, Umar's conversion in the 6th year of prophethood was the direct answer to the Prophet's du'a: 'O Allah, strengthen Islam with whichever of the two men is more beloved to You: Abu Jahl or Umar ibn al-Khattab.'

Upon converting, he marched to the Kaaba with a column of believers, enabling Muslims to pray publicly for the first time. During the Hijrah, while others migrated secretly out of fear of Makkan assassination, Umar strapped on his sword, walked to the Kaaba, made Tawaf, and proclaimed: 'Whoever wishes his mother to weep over him, let him meet me behind this valley.'

As Caliph, he wore patched garments, slept under a palm tree without guards, and walked the streets of Madinah in disguise at night carrying sacks of flour on his back to feed impoverished families.
            """.trimIndent(),
            famousQuote = "If a lost stray goat dies on the banks of the Euphrates, I fear that Allah will hold Umar accountable for failing to pave the road for it.",
            linkedHadiths = listOf(
                LinkedHadithRef(
                    collection = "bukhari",
                    hadithNumber = 3683,
                    narrator = "Sa'd ibn Abi Waqqas (RA)",
                    arabicText = "وَالَّذِي نَفْسِي بِيَدِهِ، مَا لَقِيَكَ الشَّيْطَانُ سَالِكًا فَجًّا قَطُّ إِلاَّ سَلَكَ فَجًّا غَيْرَ فَجِّكَ",
                    translation = "The Prophet ﷺ said: 'By Him in Whose Hand is my life, never does Satan find you taking a path, O Umar, except that he takes another path than yours!'",
                    grade = "Sahih al-Bukhari",
                    contextDescription = "Umar's fierce spiritual purity and integrity causing evil to flee."
                ),
                LinkedHadithRef(
                    collection = "tirmidhi",
                    hadithNumber = 3686,
                    narrator = "Uqbah ibn 'Amir (RA)",
                    arabicText = "لَوْ كَانَ نَبِيٌّ بَعْدِي لَكَانَ عُمَرَ بْنَ الْخَطَّابِ",
                    translation = "The Prophet ﷺ said: 'If there were to be a prophet after me, it would surely have been Umar ibn al-Khattab.'",
                    grade = "Hasan (Jami` at-Tirmidhi)",
                    contextDescription = "Umar's profound divinely guided intuition (Mulham) and alignment with divine revelation."
                )
            )
        ),

        // 3. Uthman ibn Affan
        DetailedSahabiBiography(
            id = "uthman_detailed",
            nameEnglish = "Uthman ibn Affan (RA)",
            nameArabic = "عثمان بن عفان",
            honorificTitle = "Dhun-Nurayn • Possessor of the Two Lights",
            category = "Ashara Mubashara",
            lifespan = "576 - 656 CE (80 years)",
            conversionOrder = "Among the very first to accept Islam through Abu Bakr",
            majorTrials = listOf(
                "Persecution by his uncle Hakam who bound him in ropes trying to force apostasy",
                "Enduring the two migrations to Abyssinia and Madinah",
                "The 40-day siege of his home in Madinah refusing to shed a drop of Muslim blood in self-defense"
            ),
            milestonesAndService = listOf(
                "Standardized and compiled the official Mus-haf of the Holy Quran sent to world capitals",
                "Personally purchased the Well of Rumah and made it a free public waqf for all residents",
                "Financed the entire Army of Hardship (Jaysh al-'Usrah) for the Tabuk expedition",
                "Constructed the first Islamic naval fleet safeguarding Mediterranean coastlines"
            ),
            lifeStoryChronicle = """
Born into the wealthy Umayyad clan, Uthman was renowned across Arabia for elegance, immense wealth, generosity, and exquisite modesty (Haya). He was bestowed the blessed title 'Dhun-Nurayn' because he married two noble daughters of the Prophet ﷺ: Ruqayyah, and after her passing, Umm Kulthum.

When the Muslim immigrants faced severe water shortages in Madinah, a merchant sold water from the Well of Rumah at exorbitant prices. Uthman bought the well for 20,000 dirhams and made it free for everyone. When Tabuk arrived, he provided 300 laden camels, 50 horses, and poured 1,000 gold dinars into the lap of the Prophet ﷺ.

His crowning legacy was the standardization of the Quranic Mus-haf, commissioning a committee of scribes under Zaid ibn Thabit to produce master codices based on the Quraishi dialect, uniting the Ummah on a single recited text forever.
            """.trimIndent(),
            famousQuote = "If our hearts were truly pure, they would never have enough of the words of our Lord (the Holy Quran).",
            linkedHadiths = listOf(
                LinkedHadithRef(
                    collection = "muslim",
                    hadithNumber = 2401,
                    narrator = "Aisha (RA)",
                    arabicText = "أَلاَ أَسْتَحِي مِنْ رَجُلٍ تَسْتَحِي مِنْهُ الْمَلاَئِكَةُ",
                    translation = "The Prophet ﷺ said regarding Uthman: 'Shall I not feel shy before a man whom even the angels feel shy before?'",
                    grade = "Sahih Muslim",
                    contextDescription = "Uthman's legendary modesty admired by the heavenly host."
                ),
                LinkedHadithRef(
                    collection = "tirmidhi",
                    hadithNumber = 3700,
                    narrator = "Abdullah ibn Umar (RA)",
                    arabicText = "مَا ضَرَّ عُثْمَانَ مَا عَمِلَ بَعْدَ الْيَوْمِ",
                    translation = "When Uthman financed the Army of Tabuk, the Prophet ﷺ turned the gold dinars in his lap and proclaimed twice: 'Nothing that Uthman does after today can ever harm him!'",
                    grade = "Hasan (Jami` at-Tirmidhi)",
                    contextDescription = "Prophetic guarantee of absolute divine pleasure for monumental public generosity."
                )
            )
        ),

        // 4. Ali ibn Abi Talib
        DetailedSahabiBiography(
            id = "ali_detailed",
            nameEnglish = "Ali ibn Abi Talib (RA)",
            nameArabic = "علي بن أبي طالب",
            honorificTitle = "Asadullah • The Lion of Allah & Gate of Knowledge",
            category = "Ashara Mubashara",
            lifespan = "600 - 661 CE (61 years)",
            conversionOrder = "First child to embrace Islam (at age 10)",
            majorTrials = listOf(
                "Sleeping in the bed of the Prophet ﷺ on the night of Hijrah as a target for assassins",
                "Heroic single combats at Badr, Uhud, and the fortress of Khaybar",
                "Navigating painful civil tribulations and factional strife during his caliphate"
            ),
            milestonesAndService = listOf(
                "Cousin and son-in-law of the Prophet ﷺ, married to the Master of Women Fatimah az-Zahra",
                "Father of the Prophet's beloved grandsons Al-Hasan and Al-Husayn",
                "Master jurist, philosopher, and orator whose legal opinions guided the Sahaba",
                "Breached the iron gates of the fortified citadel of Khaybar single-handedly"
            ),
            lifeStoryChronicle = """
Raised directly inside the household of Prophet Muhammad ﷺ from childhood, Ali never bowed to an idol. On the perilous night of the Hijrah, when assassins surrounded the Prophet's house, young Ali fearlessly lay in the Prophet's bed covered by his green cloak, risking his life to return the deposits entrusted to the Prophet by the Makkan pagans.

At the Battle of Khaybar, after successive commanders failed to breach the fortress, the Prophet ﷺ announced: 'Tomorrow I will give the banner to a man who loves Allah and His Messenger, and whom Allah and His Messenger love.' The next morning, he called Ali, cured his inflamed eyes with his blessed saliva, and gave him the banner. Ali conquered the fortress.

His caliphate was an archetype of judicial integrity. Once, he saw his lost armor in the possession of a Christian citizen. Rather than seizing it by executive decree, Caliph Ali took the dispute to the Muslim judge (Qadi Shurayh), who ruled in favor of the Christian due to lack of evidence. Awed by such divine justice under the rule of law, the man embraced Islam on the spot.
            """.trimIndent(),
            famousQuote = "Live amongst people in such a manner that if you die they weep over you, and if you live they yearn for your company.",
            linkedHadiths = listOf(
                LinkedHadithRef(
                    collection = "bukhari",
                    hadithNumber = 3706,
                    narrator = "Sahl ibn Sa'd (RA)",
                    arabicText = "لأُعْطِيَنَّ الرَّايَةَ غَدًا رَجُلاً يَفْتَحُ اللَّهُ عَلَى يَدَيْهِ، يُحِبُّ اللَّهَ وَرَسُولَهُ، وَيُحِبُّهُ اللَّهُ وَرَسُولُهُ",
                    translation = "The Prophet ﷺ said at Khaybar: 'Tomorrow I will give the standard to a man through whose hands Allah will grant victory; a man who loves Allah and His Messenger, and whom Allah and His Messenger love.'",
                    grade = "Sahih al-Bukhari",
                    contextDescription = "Divine and prophetic mutual love affirmed for Ali."
                ),
                LinkedHadithRef(
                    collection = "muslim",
                    hadithNumber = 2404,
                    narrator = "Sa'd ibn Abi Waqqas (RA)",
                    arabicText = "أَمَا تَرْضَى أَنْ تَكُونَ مِنِّي بِمَنْزِلَةِ هَارُونَ مِنْ مُوسَى غَيْرَ أَنَّهُ لاَ نَبِيَّ بَعْدِي",
                    translation = "The Prophet ﷺ said to Ali when leaving him behind to govern Madinah during Tabuk: 'Are you not pleased that you are to me in the station of Aaron to Moses, except that there is no prophet after me?'",
                    grade = "Sahih Muslim",
                    contextDescription = "Honoring Ali's unique station and brotherhood with the Prophet ﷺ."
                )
            )
        ),

        // 5. Talhah ibn Ubaydullah
        DetailedSahabiBiography(
            id = "talhah_detailed",
            nameEnglish = "Talhah ibn Ubaydullah (RA)",
            nameArabic = "طلحة بن عبيد الله",
            honorificTitle = "The Living Martyr • Shield of the Prophet",
            category = "Ashara Mubashara",
            lifespan = "594 - 656 CE (62 years)",
            conversionOrder = "Converted through Abu Bakr among the first eight believers",
            majorTrials = listOf(
                "Bound together with Abu Bakr in ropes by Nawfal ibn Khuwaylid (called Al-Qareenayn)",
                "Heroic shield at Uhud receiving over 70 wounds to save the Prophet's life"
            ),
            milestonesAndService = listOf(
                "Deflected lethal swords and arrows with his bare hands at Uhud, paralyzing his hand",
                "One of the six members of the consultative council (Shura) appointed by Umar",
                "Known as 'Talhat al-Khayr' (Talhah of Goodness) and 'Talhat al-Fayyadh' (The Munificent)"
            ),
            lifeStoryChronicle = """
A prosperous international trader of Taym, he was in Busra (Syria) when a Christian monk asked the caravan: 'Has the Ahmed of Makkah appeared yet? His era has begun.' Talhah hurried back to Makkah, found that Muhammad ﷺ had announced prophethood, and immediately embraced Islam.

At the devastating climax of the Battle of Uhud, when the Muslim archers abandoned their posts and enemy cavalry surrounded the Prophet ﷺ, only a tiny handful stood firm. Talhah threw himself in front of the Messenger of Allah, absorbing flying arrows and sword strikes with his body. He raised his bare hand to catch an arrow aimed directly at the Prophet's face; the arrow severed his tendon, permanently paralyzing his fingers.
            """.trimIndent(),
            famousQuote = "The Prophet ﷺ said of me: 'Whoever wishes to look at a martyr walking upon the face of the earth, let him look at Talhah.'",
            linkedHadiths = listOf(
                LinkedHadithRef(
                    collection = "tirmidhi",
                    hadithNumber = 3738,
                    narrator = "Jabir ibn Abdillah (RA)",
                    arabicText = "مَنْ سَرَّهُ أَنْ يَنْظُرَ إِلَى شَهِيدٍ يَمْشِي عَلَى وَجْهِ الأَرْضِ فَلْيَنْظُرْ إِلَى طَلْحَةَ بْنِ عُبَيْدِ اللَّهِ",
                    translation = "The Prophet ﷺ said: 'Whoever is pleased to look upon a martyr walking on the face of the earth, let him look at Talhah ibn Ubaydullah.'",
                    grade = "Sahih (Jami` at-Tirmidhi)",
                    contextDescription = "Prophetic testimony granting Talhah the status of a living martyr."
                )
            )
        ),

        // 6. Az-Zubayr ibn al-Awwam
        DetailedSahabiBiography(
            id = "zubayr_detailed",
            nameEnglish = "Az-Zubayr ibn al-Awwam (RA)",
            nameArabic = "الزبير بن العوام",
            honorificTitle = "Hawari ar-Rasul • The Disciple of the Prophet",
            category = "Ashara Mubashara",
            lifespan = "594 - 656 CE (64 years)",
            conversionOrder = "Embraced Islam at age 15 through Abu Bakr",
            majorTrials = listOf(
                "Tortured by his uncle wrapped in a reed mat suffocated with smoke to renounce Islam",
                "Undertook dangerous solo reconnaissance behind enemy lines at the Trench"
            ),
            milestonesAndService = listOf(
                "First person in Islamic history to draw a sword for the cause of Allah",
                "Heroic cavalry vanguard in all major campaigns alongside the Prophet ﷺ",
                "Member of Umar's six-man electoral Shura council"
            ),
            lifeStoryChronicle = """
Az-Zubayr was the cousin of the Prophet ﷺ (son of Safiyyah bint Abd al-Muttalib) and nephew of Khadijah. As a teenager in Makkah, a false rumor circulated that the Prophet ﷺ had been captured. Young Zubayr drew his sword and marched through the streets prepared to fight all of Quraysh alone until he found the Prophet ﷺ safe, who smiled and made du'a for him and his sword.

At the Battle of the Trench, when the Banu Qurayzah conspired against the besieged Muslims, the Prophet ﷺ asked three times: 'Who will bring me news of the enemy?' Each time, Zubayr stood up alone and volunteered. The Prophet ﷺ embraced him and declared his everlasting title: 'Every prophet has a disciple, and my disciple is Az-Zubayr.'
            """.trimIndent(),
            famousQuote = "By Allah, my sword was never drawn except to elevate the word of truth.",
            linkedHadiths = listOf(
                LinkedHadithRef(
                    collection = "bukhari",
                    hadithNumber = 2997,
                    narrator = "Jabir ibn Abdillah (RA)",
                    arabicText = "إِنَّ لِكُلِّ نَبِيٍّ حَوَارِيًّا، وَإِنَّ حَوَارِيَّ الزُّبَيْرُ",
                    translation = "The Prophet ﷺ said: 'Indeed, every prophet has a disciple (Hawari), and my disciple is Az-Zubayr.'",
                    grade = "Sahih al-Bukhari",
                    contextDescription = "Bestowal of the exclusive title of Prophetic Disciple."
                )
            )
        ),

        // 7. Bilal ibn Rabah
        DetailedSahabiBiography(
            id = "bilal_detailed",
            nameEnglish = "Bilal ibn Rabah (RA)",
            nameArabic = "بلال بن رباح",
            honorificTitle = "Mu'adhin of the Messenger • Voice of Tawhid",
            category = "Pioneers & Martyrs",
            lifespan = "580 - 640 CE (60 years)",
            conversionOrder = "Among the first seven individuals to proclaim Islam publicly",
            majorTrials = listOf(
                "Crushed under blistering red-hot boulders in the scorching Makkan sand by Umayyah ibn Khalaf",
                "Choking under heavy iron neck-chains dragged through the streets of Makkah"
            ),
            milestonesAndService = listOf(
                "Proclaimed the immortal cry of monotheism: 'Ahad! Ahad!' (One! One!) under torture",
                "Chosen as the very first Mu'adhin (caller to prayer) in Islamic history",
                "Ascended the roof of the Holy Kaaba upon the Conquest of Makkah to call the Adhan",
                "Umar ibn al-Khattab used to say: 'Abu Bakr is our master, and he freed our master (Bilal)'"
            ),
            lifeStoryChronicle = """
Born into slavery in Makkah of Abyssinian descent, Bilal heard the pure teachings of Prophet Muhammad ﷺ regarding human equality and monotheism, embracing the truth with all his soul. His master, Umayyah ibn Khalaf, dragged him out at noon into the boiling desert sands, strapped heavy iron boulders to his bare chest, and demanded he worship the idols Lat and Uzza. Bilal responded only with the iconic defiance: 'Ahad! Ahad!' (God is One! God is One!).

Abu Bakr purchased him and freed him for Allah. In Madinah, when the Adhan was legislated, the Prophet ﷺ selected Bilal for his resonant, melodious voice: 'Rise, O Bilal, and comfort us with the prayer!'

After the Prophet's passing, Bilal was so overcome with tears that he could not bear to finish the Adhan in Madinah. He migrated to Sham (Syria) for Jihad, calling the Adhan only once more when Caliph Umar visited Jerusalem, causing the entire assembly of Sahaba to weep uncontrollably.
            """.trimIndent(),
            famousQuote = "Ahad, Ahad! (The One, The Only God!)",
            linkedHadiths = listOf(
                LinkedHadithRef(
                    collection = "bukhari",
                    hadithNumber = 1149,
                    narrator = "Abu Hurairah (RA)",
                    arabicText = "يَا بِلاَلُ حَدِّثْنِي بِأَرْجَى عَمَلٍ عَمِلْتَهُ فِي الإِسْلاَمِ، فَإِنِّي سَمِعْتُ دَفَّ نَعْلَيْكَ بَيْنَ يَدَىَّ فِي الْجَنَّةِ",
                    translation = "The Prophet ﷺ said to Bilal at Fajr: 'O Bilal, tell me of the most hopeful deed you have done, for I heard the sound of your footsteps before me in Paradise!' Bilal replied: 'Whenever I perform Wudu by day or night, I pray whatever prayer is written for me.'",
                    grade = "Sahih al-Bukhari",
                    contextDescription = "Prophetic confirmation of Bilal's high celestial rank in Jannah."
                )
            )
        ),

        // 8. Khalid ibn al-Walid
        DetailedSahabiBiography(
            id = "khalid_detailed",
            nameEnglish = "Khalid ibn al-Walid (RA)",
            nameArabic = "خالد بن الوليد",
            honorificTitle = "Sayfullah al-Maslul • The Drawn Sword of Allah",
            category = "Pioneers & Martyrs",
            lifespan = "592 - 642 CE (50 years)",
            conversionOrder = "Converted prior to the Conquest of Makkah in 8 AH alongside Amr ibn al-As",
            majorTrials = listOf(
                "Breaking nine swords in hand at the Battle of Mu'tah defending the vanguard",
                "Facing multi-front imperial Byzantine and Sasanian armies simultaneously",
                "Demonstrating supreme humility when relieved of supreme military command by Umar"
            ),
            milestonesAndService = listOf(
                "Undefeated in over 100 historical battles throughout his military career",
                "Named 'The Drawn Sword of Allah' by the Prophet ﷺ",
                "Masterminded the epic forced desert march across the waterless Syrian desert",
                "Decisive architect of the historic Battle of Yarmouk"
            ),
            lifeStoryChronicle = """
A scion of the aristocratic Banu Makhzum clan, Khalid was a tactical prodigy from youth. After embracing Islam in 8 AH, the Prophet ﷺ embraced him and declared: 'Praise be to Allah who guided you; I always knew you had an intellect that would only lead you to good.'

At the Battle of Mu'tah in Jordan, three successive commanders appointed by the Prophet ﷺ were martyred against a Roman force outnumbering Muslims by ten to one. Khalid took the standard, broke nine swords in intense hand-to-hand combat, regrouped the army through brilliant psychological tactical retreat, and brought the soldiers safely back to Madinah.

On his deathbed in Homs, he wept not out of fear of death, but because he desired martyrdom: 'There is not a spot on my body the width of a handspan without a sword strike, spear thrust, or arrow puncture. Yet here I die upon my bed like a camel dies. May the eyes of cowards never sleep!'
            """.trimIndent(),
            famousQuote = "I die upon my bed like a camel dies, though no battle was fought without me being in the thick of it. May the eyes of cowards never taste sleep!",
            linkedHadiths = listOf(
                LinkedHadithRef(
                    collection = "bukhari",
                    hadithNumber = 4261,
                    narrator = "Anas ibn Malik (RA)",
                    arabicText = "أَخَذَ الرَّايَةَ زَيْدٌ فَأُصِيبَ، ثُمَّ أَخَذَ جَعْفَرٌ فَأُصِيبَ... حَتَّى أَخَذَ الرَّايَةَ سَيْفٌ مِنْ سُيُوفِ اللَّهِ، حَتَّى فَتَحَ اللَّهُ عَلَيْهِمْ",
                    translation = "The Prophet ﷺ wept in Madinah describing Mu'tah: 'Zaid took the banner and fell, then Ja'far took it and fell... until a Sword from the Swords of Allah (Khalid) took the banner, and Allah granted them victory!'",
                    grade = "Sahih al-Bukhari",
                    contextDescription = "The Prophet ﷺ conferring the title of 'Sword of Allah' upon Khalid."
                )
            )
        )
    )
}
