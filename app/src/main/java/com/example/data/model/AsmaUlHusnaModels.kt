package com.example.data.model

/**
 * Representation of one of the 99 Beautiful Names of Allah (Asma-ul-Husna)
 * with authentic Arabic calligraphy, transliteration, English meaning,
 * explanation, Quranic reference, and dhikr benefits.
 */
data class AllahName(
    val number: Int,
    val arabic: String,
    val transliteration: String,
    val meaning: String,
    val explanation: String,
    val quranicReference: String,
    val benefits: String
)

object AsmaUlHusnaData {
    val ALL_99_NAMES = listOf(
        AllahName(
            1, "الرَّحْمَٰنُ", "Ar-Rahman", "The Most Gracious",
            "The One who has plenty of mercy for the believers and the blasphemers in this world and exclusively for the believers in the hereafter.",
            "Surah Al-Fatiha 1:1, Surah Ar-Rahman 55:1",
            "Recitation softens the heart and brings peace into one's home."
        ),
        AllahName(
            2, "الرَّحِيمُ", "Ar-Raheem", "The Most Merciful",
            "The One who has plenty of mercy for the believers.",
            "Surah Al-Baqarah 2:143, Surah Al-Fatiha 1:3",
            "Reminds the believer of Allah's continuous tender care and divine forgiveness."
        ),
        AllahName(
            3, "الْمَلِكُ", "Al-Malik", "The King / Sovereign",
            "The Sovereign Lord, The Absolute Ruler of the entire universe without partners or peers.",
            "Surah Al-Mu'minun 23:116, Surah Al-Hashr 59:23",
            "Inspires reliance solely on Allah rather than temporary worldly rulers."
        ),
        AllahName(
            4, "الْقُدُّوسُ", "Al-Quddus", "The Most Holy",
            "The Most Sacred, free from all imperfections, deficiencies, and any resemblance to creation.",
            "Surah Al-Jumu'ah 62:1, Surah Al-Hashr 59:23",
            "Purifies the soul from arrogance, malice, and spiritual sicknesses."
        ),
        AllahName(
            5, "السَّلَامُ", "As-Salam", "The Source of Peace",
            "The One who is free from every imperfection and the granter of peace, safety, and security to creation.",
            "Surah Al-Hashr 59:23",
            "Recited for inner tranquility, relieving anxiety, and spreading peace."
        ),
        AllahName(
            6, "الْمُؤْمِنُ", "Al-Mu'min", "The Guardian of Faith",
            "The One who witnessed for Himself that no one is God but Him, and who affirms truthfulness to His prophets.",
            "Surah Al-Hashr 59:23",
            "Instills unwavering faith, certainty, and safety from fears."
        ),
        AllahName(
            7, "الْمُهَيْمِنُ", "Al-Muhaymin", "The Protector / Overseer",
            "The Overseer who watches over, preserves, and determines the affairs of all things.",
            "Surah Al-Hashr 59:23",
            "Awakens mindful consciousness (Muraqabah) that Allah sees all secret deeds."
        ),
        AllahName(
            8, "الْعَزِيزُ", "Al-Aziz", "The Almighty",
            "The Defeater who is not defeated, The Invincible whose command cannot be resisted.",
            "Surah Al-Imran 3:62, Surah Al-Hashr 59:23",
            "Strengthens the heart with dignity and resilience against oppression."
        ),
        AllahName(
            9, "الْجَبَّارُ", "Al-Jabbar", "The Compeller / Restorer",
            "The One who mends the broken, restores the brokenhearted, and compels everything to His will.",
            "Surah Al-Hashr 59:23",
            "A comfort for those grieving or suffering heartbreak; Allah repairs all fractures."
        ),
        AllahName(
            10, "الْمُتَكَبِّرُ", "Al-Mutakabbir", "The Supreme / Majestic",
            "The Possessor of all greatness and majesty, far above the attributes of creation.",
            "Surah Al-Hashr 59:23",
            "Humbleness before the Supreme Majesty of the Lord of the worlds."
        ),
        AllahName(
            11, "الْخَالِقُ", "Al-Khaliq", "The Creator",
            "The One who brings everything from non-existence into existence with preordained measures.",
            "Surah Al-Hashr 59:24, Surah Al-An'am 6:102",
            "Reflect upon the marvelous precision of nature and the human body."
        ),
        AllahName(
            12, "الْبَارِئُ", "Al-Bari'", "The Evolver / Maker",
            "The Maker who creates with perfect harmony and distinction without prior model.",
            "Surah Al-Hashr 59:24",
            "Relieves distress and facilitates physical and mental wellness."
        ),
        AllahName(
            13, "الْمُصَوِّرُ", "Al-Musawwir", "The Fashioner of Forms",
            "The One who designs and shapes each creation with a unique, beautiful countenance.",
            "Surah Al-Hashr 59:24, Surah Al-Infitar 82:8",
            "Inspires gratitude for one's physical form and artistic contemplation."
        ),
        AllahName(
            14, "الْغَفَّارُ", "Al-Ghaffar", "The Perpetual Forgiver",
            "The Forgiver who continuously veils sins and forgives the errors of His servants repeatedly.",
            "Surah Nuh 71:10, Surah Sad 38:66",
            "Erases shame and invites persistent repentance (Tawbah)."
        ),
        AllahName(
            15, "الْقَهَّارُ", "Al-Qahhar", "The All-Subduing",
            "The Subduer who has absolute sovereignty over all creation; nothing escapes His grasp.",
            "Surah Ar-Ra'd 13:16, Surah Az-Zumar 39:4",
            "Subdues evil desires and breaks arrogance within the ego (Nafs)."
        ),
        AllahName(
            16, "الْوَهَّابُ", "Al-Wahhab", "The Supreme Bestower",
            "The Giver who bestows gifts continually and abundantly without asking for anything in return.",
            "Surah Al-Imran 3:8, Surah Sad 38:9",
            "Du'a for righteous children, beneficial knowledge, and spiritual openings."
        ),
        AllahName(
            17, "الرَّزَّاقُ", "Ar-Razzaq", "The Provider",
            "The Provider who creates livelihood and sustained sustenance for every living creature.",
            "Surah Adh-Dhariyat 51:58",
            "Eradicates fear of financial poverty and opens doors of lawful provision (Halal Rizq)."
        ),
        AllahName(
            18, "الْفَتَّاحُ", "Al-Fattah", "The Supreme Opener",
            "The Opener of the gates of mercy, knowledge, victory, and relief from difficulties.",
            "Surah Saba 34:26",
            "Recited when facing locked doors, exams, career struggles, or complex decisions."
        ),
        AllahName(
            19, "الْعَلِيمُ", "Al-Alim", "The All-Knowing",
            "The One whose knowledge encompasses the past, present, future, hidden, and manifest perfectly.",
            "Surah Al-Baqarah 2:158, Surah Al-Hadid 57:3",
            "Increases illumination in understanding sacred knowledge and wisdom."
        ),
        AllahName(
            20, "الْقَابِضُ", "Al-Qabid", "The Withholder / Constrictor",
            "The One who withholds sustenance and souls in according with His divine wisdom.",
            "Surah Al-Baqarah 2:245",
            "Teaches patience during times of constriction and hardship."
        ),
        AllahName(
            21, "الْبَاسِطُ", "Al-Basit", "The Expander / Extender",
            "The One who expands sustenance, joy, and mercy to whomsoever He wills.",
            "Surah Al-Baqarah 2:245",
            "Brings relief, joy, and generous expansion after distress."
        ),
        AllahName(
            22, "الْخَافِضُ", "Al-Khafid", "The Abaser",
            "The One who lowers the arrogant, the unjust, and the deniers of truth.",
            "Sunan at-Tirmidhi #3507",
            "Protection against tyranny and oppressive pride."
        ),
        AllahName(
            23, "الرَّافِعُ", "Ar-Rafi", "The Exalter",
            "The One who exalts the humble, the righteous, and the seekers of truth.",
            "Surah Al-An'am 6:165, Surah Al-Mujadila 58:11",
            "Elevates status in both worlds through humility and piety."
        ),
        AllahName(
            24, "الْمُعِزُّ", "Al-Mu'izz", "The Bestower of Honor",
            "The One who honors whom He wills with faith, obedience, and eternal dignity.",
            "Surah Al-Imran 3:26",
            "Shields against public humiliation and builds lasting dignity."
        ),
        AllahName(
            25, "الْمُذِلُّ", "Al-Mudhill", "The Dishonorer",
            "The One who humiliates the stubbornly disobedient by stripping away false pride.",
            "Surah Al-Imran 3:26",
            "Protection from enemies of truth and deceitful schemers."
        ),
        AllahName(
            26, "السَّمِيعُ", "As-Sami'", "The All-Hearing",
            "The One who hears all sounds, whispers, supplications, and thoughts across existence.",
            "Surah Al-Baqarah 2:256, Surah As-Shora 42:11",
            "Strengthens certainty that no sincere prayer or heartfelt whisper goes unheard."
        ),
        AllahName(
            27, "الْبَصِيرُ", "Al-Basir", "The All-Seeing",
            "The One who sees all that exists, even a black ant on a black stone in the pitch dark of night.",
            "Surah Al-Hujurat 49:18, Surah Al-Hadid 57:4",
            "Instills piety and sincerity in solitary moments away from public view."
        ),
        AllahName(
            28, "الْحَكَمُ", "Al-Hakam", "The Supreme Judge",
            "The Impartial Judge whose decree cannot be overturned and whose justice is absolute.",
            "Surah Al-An'am 6:114",
            "Comfort in knowing all worldly injustices will be settled with perfection."
        ),
        AllahName(
            29, "الْعَدْلُ", "Al-Adl", "The Utterly Just",
            "The One who is purely just, never acting with unfairness, bias, or cruelty.",
            "Surah Al-An'am 6:115",
            "Encourages practicing justice and fair dealing in personal and business matters."
        ),
        AllahName(
            30, "اللَّطِيفُ", "Al-Latif", "The Subtle / Most Kind",
            "The One who knows the delicate subtleties of all matters and acts with infinite gentleness.",
            "Surah Al-Mulk 67:14, Surah Ash-Shura 42:19",
            "Brings unseen miraculous ease and delivers relief from unexpected sources."
        ),
        AllahName(
            31, "الْخَبِيرُ", "Al-Khabir", "The All-Aware",
            "The One who knows the inner realities, hidden motivations, and minute secrets of all things.",
            "Surah Al-An'am 6:18, Surah Al-Mulk 67:14",
            "Aids in self-reflection and purifying intentions from subtle hypocrisy."
        ),
        AllahName(
            32, "الْحَلِيمُ", "Al-Halim", "The Most Forbearing",
            "The One who is patient and does not hasten punishment upon those who err.",
            "Surah Al-Baqarah 2:225, Surah Al-Isra 17:44",
            "Teaches patience, anger management, and tolerance towards others."
        ),
        AllahName(
            33, "الْعَظِيمُ", "Al-Azim", "The Magnificent",
            "The Tremendous whose greatness is beyond human comprehension and capacity.",
            "Surah Al-Baqarah 2:255 (Ayat al-Kursi), Surah Ash-Shura 42:4",
            "Recited during Ruku' in Salah: 'Subhana Rabbiyal-Azim'."
        ),
        AllahName(
            34, "الْغَفُورُ", "Al-Ghafur", "The All-Forgiving",
            "The Forgiving One who covers sins completely and grants boundless clemency.",
            "Surah Al-Baqarah 2:173, Surah Al-Hijr 15:49",
            "Removes guilt and grants hope of salvation to the remorseful sinner."
        ),
        AllahName(
            35, "الشَّكُورُ", "Ash-Shakur", "The Most Appreciative",
            "The One who rewards a tiny good deed with abundant and everlasting blessings.",
            "Surah Fatir 35:30, Surah At-Taghabun 64:17",
            "Motivates doing small hidden deeds, knowing no good deed is wasted."
        ),
        AllahName(
            36, "الْعَلِيُّ", "Al-Aliyy", "The Most High",
            "The Exalted One who is above all creation in status, power, and essence.",
            "Surah Al-Baqarah 2:255, Surah Al-A'la 87:1",
            "Recited during Sujood in Salah: 'Subhana Rabbiyal-A'la'."
        ),
        AllahName(
            37, "الْكَبِيرُ", "Al-Kabir", "The Most Great",
            "The Greatest whose grandeur outshines all conceivable notions of majesty.",
            "Surah Ar-Ra'd 13:9, Surah Al-Hajj 22:62",
            "Remembered in every prayer transition: 'Allahu Akbar'."
        ),
        AllahName(
            38, "الْحَفِيظُ", "Al-Hafiz", "The Preserver",
            "The Protector who preserves creation from destruction and guards all deeds for reckoning.",
            "Surah Hud 11:57, Surah Saba 34:21",
            "Powerful shield for travel, family protection, and preservation from harm."
        ),
        AllahName(
            39, "الْمُقِيتُ", "Al-Muqit", "The Sustainer",
            "The Nourisher who supplies physical, intellectual, and spiritual fuel to all creation.",
            "Surah An-Nisa 4:85",
            "Reminds us that nourishment for the soul is as essential as food for the body."
        ),
        AllahName(
            40, "الْحَسِيبُ", "Al-Hasib", "The Reckoner",
            "The Accounter who is sufficient for His servants and calculates all deeds with precision.",
            "Surah An-Nisa 4:6, Surah Al-Ahzab 33:39",
            "Recitation: 'Hasbiyallahu la ilaha illa Huwa' (Allah is sufficient for me)."
        ),
        AllahName(
            41, "الْجَلِيلُ", "Al-Jalil", "The Majestic",
            "The One who is attributed with greatness of power and glory of status.",
            "Surah Ar-Rahman 55:27",
            "Inspires reverence and deep awe in worship."
        ),
        AllahName(
            42, "الْكَرِيمُ", "Al-Karim", "The Most Generous",
            "The Bountiful who gives without obligation and pardons with magnanimity.",
            "Surah Al-Infitar 82:6, Surah An-Naml 27:40",
            "Fosters generosity, hospitality, and trust in divine bounty."
        ),
        AllahName(
            43, "الرَّقِيبُ", "Ar-Raqib", "The Watchful",
            "The Ever-Watchful from whom not even an atom's weight escapes observation.",
            "Surah An-Nisa 4:1, Surah Al-Ahzab 33:52",
            "Cultivates mindfulness in public and private life."
        ),
        AllahName(
            44, "الْمُجِيبُ", "Al-Mujib", "The Responsive",
            "The One who responds to every supplication and call from those in need.",
            "Surah Hud 11:61",
            "Strengthens persistence in Du'a with certainty of divine response."
        ),
        AllahName(
            45, "الْوَاسِعُ", "Al-Wasi'", "The All-Encompassing",
            "The Boundless whose knowledge, mercy, and dominion encompass all existence.",
            "Surah Al-Baqarah 2:115, Surah Al-Baqarah 2:268",
            "Expands narrow minds and brings comfort in times of constriction."
        ),
        AllahName(
            46, "الْحَكِيمُ", "Al-Hakim", "The All-Wise",
            "The One who is possessor of absolute wisdom; every decree has a perfect purpose.",
            "Surah Al-Baqarah 2:129, Surah Al-An'am 6:18",
            "Acceptance of life's trials knowing Allah's divine plan is flawless."
        ),
        AllahName(
            47, "الْوَدُودُ", "Al-Wadud", "The Loving",
            "The Source of all pure affection who loves His righteous servants and is loved by them.",
            "Surah Hud 11:90, Surah Al-Buruj 85:14",
            "Fills the heart with warm reciprocal divine love and empathy for creation."
        ),
        AllahName(
            48, "الْمَجِيدُ", "Al-Majid", "The All-Glorious",
            "The Majestic whose deeds are glorious and whose essence is exalted above all.",
            "Surah Hud 11:73, Surah Al-Buruj 85:15",
            "Recited in Durood Ibrahimiyyah during Tashahhud."
        ),
        AllahName(
            49, "الْبَاعِثُ", "Al-Ba'ith", "The Resurrector",
            "The One who raises the dead from their graves on the Day of Resurrection.",
            "Surah Al-Hajj 22:7, Surah Yasin 36:52",
            "Awakens urgency to prepare for the eternal life hereafter."
        ),
        AllahName(
            50, "الشَّهِيدُ", "Ash-Shahid", "The Witness",
            "The Witness who observes all events, actions, and spoken words directly.",
            "Surah An-Nisa 4:79, Surah Al-Hajj 22:17",
            "Guarantees that honest testimony and upright conduct will be rewarded."
        ),
        AllahName(
            51, "الْحَقُّ", "Al-Haqq", "The Absolute Truth",
            "The One whose existence is unchangeable, real, and undeniable reality.",
            "Surah Al-Hajj 22:6, Surah Taha 20:114",
            "Gives courage to stand with truth even against personal interests."
        ),
        AllahName(
            52, "الْوَكِيلُ", "Al-Wakil", "The Trustworthy Trustee",
            "The Supreme Trustee upon whom all matters are entrusted and who manages them flawlessly.",
            "Surah Al-Imran 3:173, Surah Al-An'am 6:102",
            "Say: 'Hasbunallahu wa ni'mal-Wakil' (Allah is sufficient for us and He is the best Disposer of affairs)."
        ),
        AllahName(
            53, "الْقَوِيُّ", "Al-Qawiyy", "The All-Strong",
            "The Possessor of inexhaustible strength and power that never weakens.",
            "Surah Al-Hajj 22:40, Surah Ash-Shura 42:19",
            "Inspires strength when feeling physically, emotionally, or socially vulnerable."
        ),
        AllahName(
            54, "الْمَتِينُ", "Al-Matin", "The Firm",
            "The Steadfast whose firmness is unshakeable and whose resolve knows no fatigue.",
            "Surah Adh-Dhariyat 51:58",
            "Grants steadfastness (Istiqamah) on the Straight Path."
        ),
        AllahName(
            55, "الْوَلِيُّ", "Al-Waliyy", "The Protecting Friend",
            "The Near Guardian and intimate Friend of the righteous believers.",
            "Surah Al-Baqarah 2:257, Surah Ash-Shura 42:28",
            "Assures the believer that they are never alone in this world."
        ),
        AllahName(
            56, "الْحَمِيدُ", "Al-Hamid", "The All-Praiseworthy",
            "The One who deserves all praise, adoration, and gratitude for who He is and what He gives.",
            "Surah Ibrahim 14:1, Surah Al-Hajj 22:64",
            "Continuous praise through 'Alhamdulillah' under all circumstances."
        ),
        AllahName(
            57, "الْمُحْصِي", "Al-Muhsi", "The Accounter of All",
            "The One who tallies and records the count of every particle and action in the universe.",
            "Surah Maryam 19:94, Surah Al-Jinn 72:28",
            "Reminds us that not even a sigh, smile, or teardrop is forgotten."
        ),
        AllahName(
            58, "الْمُبْدِئُ", "Al-Mubdi'", "The Originator",
            "The One who started creation out of nothing without previous blueprint.",
            "Surah Al-Buruj 85:13, Surah Al-Anbiya 21:104",
            "Fosters originality and fresh starts in life and projects."
        ),
        AllahName(
            59, "الْمُعِيدُ", "Al-Mu'id", "The Restorer",
            "The One who restores creation back after death just as He originated it.",
            "Surah Al-Buruj 85:13, Surah Ar-Rum 30:27",
            "Comfort in knowing that what was lost in this world can be restored manifolds."
        ),
        AllahName(
            60, "الْمُحْيِي", "Al-Muhyi", "The Giver of Life",
            "The One who gives life to dead matter and revives dead hearts with faith.",
            "Surah Ar-Rum 30:50, Surah Fussilat 41:39",
            "Revitalizes spiritually cold hearts and physical fatigue."
        ),
        AllahName(
            61, "الْمُمِيتُ", "Al-Mumit", "The Creator of Death",
            "The One who decrees death when life reaches its appointed term.",
            "Surah Al-Anbiya 21:35, Surah Al-Mu'minun 23:80",
            "Reminder to remember the destroyer of pleasures and prepare for the grave."
        ),
        AllahName(
            62, "الْحَيُّ", "Al-Hayy", "The Ever-Living",
            "The Eternally Alive who does not die, sleep, or succumb to drowsiness.",
            "Surah Al-Baqarah 2:255, Surah Al-Furqan 25:58",
            "Key component of Ism Allah al-A'zam (The Greatest Name of Allah)."
        ),
        AllahName(
            63, "الْقَيُّومُ", "Al-Qayyum", "The Self-Sustaining",
            "The Self-Existing One by whom all things subsist and are maintained.",
            "Surah Al-Baqarah 2:255, Surah Taha 20:111",
            "Du'a: 'Ya Hayyu Ya Qayyum bi-rahmatika astagheeth'."
        ),
        AllahName(
            64, "الْوَاجِدُ", "Al-Wajid", "The Finder / Resourceful",
            "The One who lacks nothing and finds whatever He wills without effort.",
            "Sunan at-Tirmidhi #3507",
            "Fosters self-sufficiency through contentment in Allah."
        ),
        AllahName(
            65, "الْمَاجِدُ", "Al-Majid", "The Illustrious",
            "The Glorious whose nobility and magnificence are boundless.",
            "Sunan at-Tirmidhi #3507",
            "Inspires noble character, courtesy, and high aspirations."
        ),
        AllahName(
            66, "الْوَاحِدُ", "Al-Wahid", "The One",
            "The Unique One who has no second, partner, equal, or likeness.",
            "Surah Al-Baqarah 2:163, Surah Al-Ikhlas 112:1",
            "Pure Tawhid (Islamic Monotheism)."
        ),
        AllahName(
            67, "الْأَحَدُ", "Al-Ahad", "The Indivisible One",
            "The Singular who cannot be partitioned or divided.",
            "Surah Al-Ikhlas 112:1",
            "The ultimate declaration of Bilal's steadfastness under torture: 'Ahad! Ahad!'."
        ),
        AllahName(
            68, "الصَّمَدُ", "As-Samad", "The Eternal Refuge",
            "The Self-Sufficient Master upon whom all creation depends while He depends on none.",
            "Surah Al-Ikhlas 112:2",
            "Turning only to Allah for every need, big or small."
        ),
        AllahName(
            69, "الْقَادِرُ", "Al-Qadir", "The All-Capable",
            "The One who has power to do anything He wills without limitation.",
            "Surah Al-Baqarah 2:20, Surah Al-An'am 6:65",
            "Overcomes feelings of helplessness and despair."
        ),
        AllahName(
            70, "الْمُقْتَدِرُ", "Al-Muqtadir", "The Determiner",
            "The Omnipotent who implements His decrees with absolute mastery.",
            "Surah Al-Qamar 54:42, Surah Al-Kahf 18:45",
            "Peace of mind in knowing the universe operates under sovereign control."
        ),
        AllahName(
            71, "الْمُقَدِّمُ", "Al-Muqaddim", "The Advancer",
            "The One who brings forward what He wills in rank, time, and precedence.",
            "Surah Qaf 50:28",
            "Prayer to be granted advancement in faith and righteous deeds."
        ),
        AllahName(
            72, "الْمُؤَخِّرُ", "Al-Mu'akhkhir", "The Delayer",
            "The One who sets back and delays things according to divine wisdom.",
            "Surah Ibrahim 14:42",
            "Contentment when desired outcomes are delayed for a wiser season."
        ),
        AllahName(
            73, "الْأَوَّلُ", "Al-Awwal", "The Very First",
            "The First who existed before everything, with no beginning to His existence.",
            "Surah Al-Hadid 57:3",
            "Puts worldly origins into profound cosmic perspective."
        ),
        AllahName(
            74, "الْآخِرُ", "Al-Akhir", "The Very Last",
            "The Last who remains after everything perishes, with no end to His existence.",
            "Surah Al-Hadid 57:3",
            "Anchors the heart in the only permanence that will never vanish."
        ),
        AllahName(
            75, "الظَّاهِرُ", "Az-Zahir", "The Manifest",
            "The Outward whose existence is clear, evident, and demonstrated through all creation.",
            "Surah Al-Hadid 57:3",
            "Seeing the signs of Allah in the heavens and the earth."
        ),
        AllahName(
            76, "الْبَاطِنُ", "Al-Batin", "The Hidden",
            "The Inward who is concealed from physical sight and knows all hidden depths.",
            "Surah Al-Hadid 57:3",
            "Reverence for the unseen world and the secrets of the heart."
        ),
        AllahName(
            77, "الْوَالِي", "Al-Wali", "The Patron / Governor",
            "The Sole Ruler who manages, protects, and governs the universe and all therein.",
            "Surah Ar-Ra'd 13:11",
            "Security under the divine patronage of the Lord of all realms."
        ),
        AllahName(
            78, "الْمُتَعَالِي", "Al-Muta'ali", "The Supremely Exalted",
            "The Exalted far above any likeness or comparison to the defects of creation.",
            "Surah Ar-Ra'd 13:9",
            "Cleanses theology from anthropomorphism and lower concepts."
        ),
        AllahName(
            79, "الْبَرُّ", "Al-Barr", "The Source of All Goodness",
            "The Benign, Most Kind, and Generous benefactor whose goodness reaches all.",
            "Surah At-Tur 52:28",
            "Inspires piety, filial devotion to parents, and kindness to people."
        ),
        AllahName(
            80, "التَّوَّابُ", "At-Tawwab", "The Relenting Accepter of Repentance",
            "The One who continually turns back towards those who repent and accepts their remorse.",
            "Surah Al-Baqarah 2:37, Surah An-Nasr 110:3",
            "Assures that no door of repentance is ever closed while breath remains."
        ),
        AllahName(
            81, "الْمُنْتَقِمُ", "Al-Muntaqim", "The Avenger",
            "The Victorious who rightly avenges the oppressed by punishing persistent oppressors.",
            "Surah As-Sajdah 32:22, Surah Az-Zukhruf 43:41",
            "Consolation to victims of injustice and genocide."
        ),
        AllahName(
            82, "الْعَفُوُّ", "Al-Afuww", "The Supreme Pardoner",
            "The Pardoner who completely wipes away the sin as if it never occurred.",
            "Surah An-Nisa 4:99, Surah Al-Hajj 22:60",
            "Special Laylat al-Qadr Du'a: 'Allahumma innaka 'Afuwwun tuhibbul-'afwa fa'fu 'anni'."
        ),
        AllahName(
            83, "الرَّءُوفُ", "Ar-Ra'uf", "The Most Compassionate",
            "The Most Tender and Gentle whose pity for His servants exceeds all bounds.",
            "Surah Al-Baqarah 2:207, Surah Al-Hadid 57:9",
            "Draws soft compassion and tenderness into marital and family life."
        ),
        AllahName(
            84, "مَالِكُ الْمُلْكِ", "Malik-ul-Mulk", "Owner of All Sovereignty",
            "The Master of the Kingdom who bestows and strips dominion as He pleases.",
            "Surah Al-Imran 3:26",
            "Total release of worldly control into the hands of the true Sovereign."
        ),
        AllahName(
            85, "ذُو الْجَلَالِ وَالْإِكْرَامِ", "Dhul-Jalali wal-Ikram", "Lord of Majesty and Generosity",
            "The Possessor of absolute majesty and honor who is the source of all honor.",
            "Surah Ar-Rahman 55:27, Surah Ar-Rahman 55:78",
            "The Prophet (ﷺ) urged: 'Persist with Dhul-Jalali wal-Ikram in supplication!'"
        ),
        AllahName(
            86, "الْمُقْسِطُ", "Al-Muqsit", "The Equitable",
            "The One who is fair in all judgments and awards full justice to all.",
            "Surah Al-Imran 3:18",
            "Commitment to honesty and equity in contracts and relationships."
        ),
        AllahName(
            87, "الْجَامِعُ", "Al-Jami'", "The Gatherer",
            "The Gatherer who will assemble all creation on the Day of Resurrection without fail.",
            "Surah Al-Imran 3:9, Surah An-Nisa 4:140",
            "Du'a for reuniting lost items, scattered families, and severed ties."
        ),
        AllahName(
            88, "الْغَنِيُّ", "Al-Ghaniyy", "The Self-Sufficient / Rich",
            "The Rich who is completely free from any need while all need Him.",
            "Surah Al-Baqarah 2:267, Surah Fatir 35:15",
            "Cures greed and frees the worshiper from needing favors from humans."
        ),
        AllahName(
            89, "الْمُغْنِي", "Al-Mughni", "The Enricher",
            "The One who enriches whom He wills with both spiritual and worldly wealth.",
            "Surah An-Najm 53:48",
            "Bestows true richness: richness of the soul (Ghina an-Nafs)."
        ),
        AllahName(
            90, "الْمَانِعُ", "Al-Mani'", "The Withholder / Preventer",
            "The Protector who prevents harm and withholds what is not beneficial for His servants.",
            "Sunan at-Tirmidhi #3507",
            "Gratitude when things don't go our way, knowing Allah prevented harm."
        ),
        AllahName(
            91, "الضَّارُّ", "Ad-Darr", "The Distressor",
            "The One who creates adversity to test, purify, and guide His servants.",
            "Surah Al-An'am 6:17",
            "Understanding that adversity carries hidden spiritual purification."
        ),
        AllahName(
            92, "النَّافِعُ", "An-Nafi'", "The Bestower of Benefit",
            "The One who brings benefit, profit, and wellness to whom He wills.",
            "Surah Al-An'am 6:17",
            "Looking to Allah as the true author of all health, success, and benefit."
        ),
        AllahName(
            93, "النُّورُ", "An-Nur", "The Light",
            "The Light of the heavens and the earth who illuminates the universe and hearts.",
            "Surah An-Nur 24:35",
            "Illuminates the grave and grants radiant clarity in spiritual confusion."
        ),
        AllahName(
            94, "الْهَادِي", "Al-Hadi", "The Guide",
            "The Guide who leads hearts to truth, righteousness, and the Straight Path.",
            "Surah Al-Hajj 22:54, Surah Al-Furqan 25:31",
            "Essential supplication for daily guidance: 'Ihdinas-Sirat al-Mustaqim'."
        ),
        AllahName(
            95, "الْبَدِيعُ", "Al-Badi'", "The Incomparable Originator",
            "The Originator who created the heavens and the earth in breathtaking, unparalleled beauty.",
            "Surah Al-Baqarah 2:117, Surah Al-An'am 6:101",
            "Wonderment at the sublime craftsmanship of the universe."
        ),
        AllahName(
            96, "الْبَاقِي", "Al-Baqi", "The Everlasting",
            "The Ever-Enduring One whose existence has no end when all transient things decay.",
            "Surah Ar-Rahman 55:27",
            "Detachment from temporary materialism and attachment to the Everlasting."
        ),
        AllahName(
            97, "الْوَارِثُ", "Al-Warith", "The Ultimate Inheritor",
            "The Inheritor who outlives all creation and to whom all dominion returns.",
            "Surah Al-Hijr 15:23, Surah Al-Anbiya 21:89",
            "Du'a of Prophet Zakariyya: 'Rabbi la tadharni fardan wa Anta Khayrul-Warithin'."
        ),
        AllahName(
            98, "الرَّشِيدُ", "Ar-Rashid", "The Guide to the Right Path",
            "The Infallible Teacher whose guidance is right, direct, and unerring.",
            "Surah Al-Kahf 18:10",
            "Recitation when facing critical crossroads in life and career."
        ),
        AllahName(
            99, "الصَّبُورُ", "As-Sabur", "The Most Patient",
            "The Patient One who does not hasten action out of time or anger.",
            "Sunan at-Tirmidhi #3507",
            "The ultimate inspiration to bear all life's trials with dignified patience (Sabr Jameel)."
        )
    )

    fun getFeaturedName(dayOfYear: Int = java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_YEAR)): AllahName {
        val index = ((dayOfYear - 1).coerceAtLeast(0)) % ALL_99_NAMES.size
        return ALL_99_NAMES.getOrElse(index) { ALL_99_NAMES[0] }
    }

    fun getPreviewNames(count: Int = 6): List<AllahName> {
        return ALL_99_NAMES.take(count)
    }

    fun getMemorizedPercentage(memorizedCount: Int): Int {
        if (ALL_99_NAMES.isEmpty()) return 0
        return ((memorizedCount.toFloat() / ALL_99_NAMES.size) * 100).toInt().coerceIn(0, 100)
    }
}

data class AsmaUlHusnaState(
    val memorizedIds: Set<Int> = emptySet(),
    val favoriteIds: Set<Int> = emptySet(),
    val searchQuery: String = "",
    val activeFilterTab: Int = 0, // 0: All, 1: Memorized, 2: Favorites
    val selectedNameForDetail: AllahName? = null,
    val isDetailDialogOpen: Boolean = false,
    val nameDhikrCounters: Map<Int, Int> = emptyMap(),
    val currentlyPlayingNumber: Int? = null
)
