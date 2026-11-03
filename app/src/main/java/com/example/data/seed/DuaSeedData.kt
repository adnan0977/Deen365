package com.example.data.seed

data class DuaItem(
    val id: String,
    val title: String,
    val category: String,
    val arabicText: String,
    val transliteration: String,
    val translation: String,
    val explanation: String,
    val reference: String,
    val recommendedCount: Int = 1
)

data class TasbihPreset(
    val id: String,
    val arabic: String,
    val transliteration: String,
    val meaning: String,
    val targetCount: Int = 33
)

data class DuaCategoryMeta(
    val id: String,
    val name: String,
    val arabicName: String,
    val iconKey: String,
    val description: String
)

object DuaSeedData {

    val CATEGORY_METAS = listOf(
        DuaCategoryMeta(
            id = "morning_evening",
            name = "Morning & Evening",
            arabicName = "أذكار الصباح والمساء",
            iconKey = "wb_sunny",
            description = "Daily fortress and remembrance for the dawn and twilight"
        ),
        DuaCategoryMeta(
            id = "prayer_purification",
            name = "Prayer & Purification",
            arabicName = "الصلاة والطهور",
            iconKey = "mosque",
            description = "Invocations before, during, and after Salah, Wudu, and Sujood"
        ),
        DuaCategoryMeta(
            id = "anxiety_distress",
            name = "Anxiety, Distress & Hardship",
            arabicName = "الهم والحزن والكرب",
            iconKey = "psychology",
            description = "Relief from distress, overwhelming debts, sadness, and trials"
        ),
        DuaCategoryMeta(
            id = "forgiveness_repentance",
            name = "Forgiveness & Repentance",
            arabicName = "الاستغفار والتوبة",
            iconKey = "favorite",
            description = "Master supplications seeking Allah's vast mercy and wiping away sins"
        ),
        DuaCategoryMeta(
            id = "protection_ruqyah",
            name = "Protection & Ruqyah",
            arabicName = "الحفظ والوقاية والرقى",
            iconKey = "shield",
            description = "Shielding against the evil eye, harm, affliction, and whispers"
        ),
        DuaCategoryMeta(
            id = "health_healing",
            name = "Health & Healing (Shifa)",
            arabicName = "المرض والشفاء",
            iconKey = "healing",
            description = "Duas for physical cure, spiritual recovery, and visiting the sick"
        ),
        DuaCategoryMeta(
            id = "travel_journey",
            name = "Travel & Journey",
            arabicName = "السفر والرواح",
            iconKey = "flight",
            description = "Invocations for boarding vehicles, safe voyages, and return"
        ),
        DuaCategoryMeta(
            id = "food_fasting",
            name = "Food, Drink & Fasting",
            arabicName = "الطعام والصيام",
            iconKey = "restaurant",
            description = "Blessings for nourishment, fasting intentions, and Iftar relief"
        ),
        DuaCategoryMeta(
            id = "home_family",
            name = "Home, Family & Parents",
            arabicName = "البيت والوالدين",
            iconKey = "home",
            description = "Prayers for righteous parents, pious children, and household peace"
        ),
        DuaCategoryMeta(
            id = "sleep_waking",
            name = "Sleep & Waking Up",
            arabicName = "النوم والاستيقاظ",
            iconKey = "bedtime",
            description = "Peaceful sleep, protection from bad dreams, and morning gratitude"
        ),
        DuaCategoryMeta(
            id = "quranic_rabbana",
            name = "Quranic Duas (Rabbana)",
            arabicName = "أدعية القرآن الكريم",
            iconKey = "auto_stories",
            description = "Divine, comprehensive invocations revealed in the Holy Quran"
        ),
        DuaCategoryMeta(
            id = "daily_life",
            name = "Daily Life & Wisdom",
            arabicName = "الحياة اليومية والحكمة",
            iconKey = "explore",
            description = "Beneficial knowledge, marketplace entry, and virtuous character"
        )
    )

    val CATEGORIES = listOf("All") + CATEGORY_METAS.map { it.name }

    val POPULAR_TAGS = listOf(
        "Morning Shield",
        "Bedtime Peace",
        "Anxiety Relief",
        "After Prayer",
        "Health & Cure",
        "Forgiveness",
        "Parents & Family",
        "Travel Safety",
        "Ramadan & Fasting"
    )

    val TASBIH_PRESETS = listOf(
        TasbihPreset("subhanallah", "سُبْحَانَ اللَّهِ", "SubhanAllah", "Glory be to Allah", 33),
        TasbihPreset("alhamdulillah", "الْحَمْدُ لِلَّهِ", "Alhamdulillah", "All praise is due to Allah", 33),
        TasbihPreset("allahuakbar", "اللَّهُ أَكْبَرُ", "Allahu Akbar", "Allah is the Greatest", 34),
        TasbihPreset("astaghfirullah", "أَسْتَغْفِرُ اللَّهَ", "Astaghfirullah", "I seek forgiveness from Allah", 100),
        TasbihPreset("lailahaillallah", "لاَ إِلَهَ إِلاَّ اللَّهُ", "La ilaha illallah", "There is no deity except Allah", 100),
        TasbihPreset("salawat", "اللَّهُمَّ صَلِّ عَلَى مُحَمَّدٍ", "Allahumma salli 'ala Muhammad", "O Allah, send blessings upon Muhammad", 100),
        TasbihPreset("hawqala", "لاَ حَوْلَ وَلاَ قُوَّةَ إِلاَّ بِاللَّهِ", "La hawla wa la quwwata illa billah", "There is no power nor strength except with Allah", 100)
    )

    val DUAS = listOf(
        // 1. Morning & Evening
        DuaItem(
            id = "d1",
            title = "Sayyid al-Istighfar (The Master Supplication for Forgiveness)",
            category = "Forgiveness & Repentance",
            arabicText = "اللَّهُمَّ أَنْتَ رَبِّي لاَ إِلَهَ إِلاَّ أَنْتَ، خَلَقْتَنِي وَأَنَا عَبْدُكَ، وَأَنَا عَلَى عَهْدِكَ وَوَعْدِكَ مَا اسْتَطَعْتُ، أَعُوذُ بِكَ مِنْ شَرِّ مَا صَنَعْتُ، أَبُوءُ لَكَ بِنِعْمَتِكَ عَلَيَّ، وَأَبُوءُ بِذَنْبِي فَاغْفِرْ لِي فَإِنَّهُ لاَ يَغْفِرُ الذُّنُوبَ إِلاَّ أَنْتَ",
            transliteration = "Allahumma anta Rabbi la ilaha illa anta, khalaqtani wa ana 'abduka, wa ana 'ala 'ahdika wa wa'dika mastata'tu, a'oodhu bika min sharri ma sana'tu, aboo'u laka bini'matika 'alayya, wa aboo'u bidhanbi faghfir li fa'innahu la yaghfirudh-dhunooba illa anta.",
            translation = "O Allah, You are my Lord, there is no god but You. You created me and I am Your servant, and I abide by Your covenant and promise as best I can. I seek refuge in You from the evil of what I have done. I acknowledge Your favor upon me and I acknowledge my sin, so forgive me, for none forgives sins except You.",
            explanation = "Described by the Prophet (pbuh) as the most comprehensive supplication for forgiveness. Whoever recites it during the day with firm belief and dies before evening will enter Paradise, and whoever recites it at night with firm belief and dies before morning will enter Paradise.",
            reference = "Sahih al-Bukhari 6306",
            recommendedCount = 1
        ),
        DuaItem(
            id = "d6",
            title = "Morning & Evening Shield Against All Harm",
            category = "Morning & Evening",
            arabicText = "بِسْمِ اللَّهِ الَّذِي لاَ يَضُرُّ مَعَ اسْمِهِ شَيْءٌ فِي الأَرْضِ وَلاَ فِي السَّمَاءِ وَهُوَ السَّمِيعُ الْعَلِيمُ",
            transliteration = "Bismillahilladhi la yadurru ma'as-mihi shay'un fil-ardi wa la fis-samaa'i wa Huwas-Samee'ul-'Aleem.",
            translation = "In the Name of Allah, with Whose Name nothing upon earth or in the heavens can cause harm, and He is the All-Hearing, the All-Knowing.",
            explanation = "The Prophet (pbuh) said: 'Whoever recites this three times in the morning and three times in the evening, nothing will harm him.' It establishes an impenetrable divine shield around the believer.",
            reference = "Jami` at-Tirmidhi 3388 • Sahih",
            recommendedCount = 3
        ),
        DuaItem(
            id = "d14",
            title = "Sufficiency in Allah (HasbiyAllahu)",
            category = "Morning & Evening",
            arabicText = "حَسْبِيَ اللَّهُ لاَ إِلَٰهَ إِلاَّ هُوَ ۖ عَلَيْهِ تَوَكَّلْتُ ۖ وَهُوَ رَبُّ الْعَرْشِ الْعَظِيمِ",
            transliteration = "HasbiyAllahu la ilaha illa Huwa, 'alayhi tawakkaltu wa Huwa Rabbul-'Arshil-'Azeem.",
            translation = "Allah is sufficient for me; there is no deity except Him. Upon Him I rely, and He is the Lord of the Great Throne.",
            explanation = "Abu ad-Darda narrated: 'Whoever recites this seven times in the morning and seven times in the evening, Allah will suffice him for all that concerns him in worldly affairs and the hereafter.'",
            reference = "Sunan Abi Dawud 5081 • Sahih Mauquf",
            recommendedCount = 7
        ),
        DuaItem(
            id = "d15",
            title = "Evening Protection with Allah's Perfect Words",
            category = "Morning & Evening",
            arabicText = "أَعُوذُ بِكَلِمَاتِ اللَّهِ التَّامَّاتِ مِنْ شَرِّ مَا خَلَقَ",
            transliteration = "A'oodhu bi kalimatillahit-tammati min sharri ma khalaq.",
            translation = "I seek refuge in the perfect words of Allah from the evil of what He has created.",
            explanation = "The Prophet (pbuh) said: 'Whoever recites this thrice in the evening, no poisonous sting, harmful insect, or creature will harm him that night.'",
            reference = "Sahih Muslim 2709",
            recommendedCount = 3
        ),
        DuaItem(
            id = "d16",
            title = "The Juwayriyah Formula for Immense Reward",
            category = "Morning & Evening",
            arabicText = "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ عَدَدَ خَلْقِهِ، وَرِضَا نَفْسِهِ، وَزِنَةَ عَرْشِهِ، وَمِدَادَ كَلِمَاتِهِ",
            transliteration = "SubhanAllahi wa bihamdihi 'adada khalqihi, wa rida nafsihi, wa zinata 'arshihi, wa midada kalimatih.",
            translation = "Glory be to Allah and praise be to Him, by the number of His creation, by His pleasure, by the weight of His Throne, and by the ink of His words.",
            explanation = "The Prophet (pbuh) taught this to his wife Juwayriyah, explaining that reciting this 3 times outweighs hours of continuous remembrance from dawn to noon.",
            reference = "Sahih Muslim 2726",
            recommendedCount = 3
        ),

        // 2. Prayer & Purification
        DuaItem(
            id = "d2",
            title = "Ayat al-Kursi (The Throne Verse - Ultimate Protection)",
            category = "Prayer & Purification",
            arabicText = "اللَّهُ لَا إِلَٰهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ ۚ لَا تَأْخُذُهُ سِنَةٌ وَلَا نَوْمٌ ۚ لَّهُ مَا فِي السَّمَاوَاتِ وَمَا فِي الْأَرْضِ ۗ مَن ذَا الَّذِي يَشْفَعُ عِندَهُ إِلَّا بِإِذْنِهِ ۚ يَعْلَمُ مَا بَيْنَ أَيْدِيهِمْ وَمَا خَلْفَهُمْ ۖ وَلَا يُحِيطُونَ بِشَيْءٍ مِّنْ عِلْمِهِ إِلَّا بِمَا شَاءَ ۚ وَسِعَ كُرْسِيُّهُ السَّمَاوَاتِ وَالْأَرْضَ ۖ وَلَا يَئُودُهُ حِفْظُهُمَا ۚ وَهُوَ الْعَلِيُّ الْعَظِيمُ",
            transliteration = "Allahu la ilaha illa Huwal-Hayyul-Qayyum. La ta'khudhuhu sinatuw-wa la nawm. Lahu ma fis-samawati wa ma fil-ard. Man dhal-ladhi yashfa'u 'indahu illa bi-idhnihi. Ya'lamu ma bayna aydihim wa ma khalfahum. Wa la yuhitoona bishay'im-min 'ilmihi illa bima sha'a. Wasi'a kursiyyuhus-samawati wal-arda wa la ya'ooduhu hifzuhuma, wa Huwal-'Aliyyul-'Azeem.",
            translation = "Allah - there is no deity except Him, the Ever-Living, the Sustainer of all existence. Neither drowsiness overtakes Him nor sleep. To Him belongs whatever is in the heavens and whatever is on the earth. Who is it that can intercede with Him except by His permission? He knows what is before them and what will be after them, and they encompass not a thing of His knowledge except for what He wills. His Kursi extends over the heavens and the earth, and their preservation tires Him not. And He is the Most High, the Most Great.",
            explanation = "The Prophet (pbuh) said: 'Whoever recites Ayat al-Kursi immediately after each prescribed prayer, there is nothing standing between him and entering Paradise except death.'",
            reference = "Surah Al-Baqarah 2:255 • Sunan an-Nasa'i as-Sunan al-Kubra 9848",
            recommendedCount = 1
        ),
        DuaItem(
            id = "d7",
            title = "Supplication After Tashahhud (Seeking Refuge from 4 Calamities)",
            category = "Prayer & Purification",
            arabicText = "اللَّهُمَّ إِنِّي أَعُوذُ بِكَ مِنْ عَذَابِ الْقَبْرِ، وَمِنْ عَذَابِ جَهَنَّمَ، وَمِنْ فِتْنَةِ الْمَحْيَا وَالْمَمَاتِ، وَمِنْ شَرِّ فِتْنَةِ الْمَسِيحِ الدَّجَّالِ",
            transliteration = "Allahumma inni a'oodhu bika min 'adhabal-qabr, wa min 'adhabi jahannam, wa min fitnatil-mahya wal-mamat, wa min sharri fitnatil-maseehid-dajjal.",
            translation = "O Allah, I seek refuge in You from the punishment of the grave, from the punishment of Hellfire, from the trials of life and death, and from the evil trial of the False Messiah.",
            explanation = "The Prophet (pbuh) instructed that when anyone completes the final Tashahhud of prayer, before the final Salam, they should seek protection from these four great calamities.",
            reference = "Sahih al-Bukhari 1377 • Sahih Muslim 588",
            recommendedCount = 1
        ),
        DuaItem(
            id = "d17",
            title = "Supplication Between the Two Prostrations (Jalsah)",
            category = "Prayer & Purification",
            arabicText = "رَبِّ اغْفِرْ لِي، وَارْحَمْنِي، وَاهْدِنِي، وَعَافِنِي، وَارْزُقْنِي، وَاجْبُرْنِي، وَارْفَعْنِي",
            transliteration = "Rabbigh-fir li, warhamni, wahdini, wa 'afini, warzuqni, wajburni, warfa'ni.",
            translation = "My Lord, forgive me, have mercy on me, guide me, grant me health and well-being, provide for me, mend my shortcomings, and elevate my status.",
            explanation = "Ibn Abbas narrated that the Messenger of Allah (pbuh) used to say this comprehensive prayer while sitting between the two Sujoods of Salah.",
            reference = "Sunan Abi Dawud 850 • Jami` at-Tirmidhi 284",
            recommendedCount = 1
        ),
        DuaItem(
            id = "d18",
            title = "Dua Upon Entering the Mosque",
            category = "Prayer & Purification",
            arabicText = "اللَّهُمَّ افْتَحْ لِي أَبْوَابَ رَحْمَتِكَ",
            transliteration = "Allahummaf-tah li abwaba rahmatik.",
            translation = "O Allah, open for me the gates of Your mercy.",
            explanation = "Said upon stepping into the mosque with the right foot, preparing the heart to enter a sanctuary dedicated to the worship of Allah.",
            reference = "Sahih Muslim 713",
            recommendedCount = 1
        ),
        DuaItem(
            id = "d19",
            title = "Dua for Istikhara (Divine Guidance in Important Decisions)",
            category = "Prayer & Purification",
            arabicText = "اللَّهُمَّ إِنِّي أَسْتَخِيرُكَ بِعِلْمِكَ، وَأَسْتَقْدِرُكَ بِقُدْرَتِكَ، وَأَسْأَلُكَ مِنْ فَضْلِكَ الْعَظِيمِ، فَإِنَّكَ تَقْدِرُ وَلاَ أَقْدِرُ، وَتَعْلَمُ وَلاَ أَعْلَمُ، وَأَنْتَ عَلاَّمُ الْغُيُوبِ",
            transliteration = "Allahumma inni astakheeruka bi-'ilmika, wa astaqdiruka bi-qudratika, wa as'aluka min fadlikal-'azeem, fa'innaka taqdiru wa la aqdir, wa ta'lamu wa la a'lam, wa Anta 'Allamul-ghuyoob.",
            translation = "O Allah, I consult You through Your knowledge, and I seek strength through Your power, and I ask of Your immense favor; for You have power and I have none, and You know and I do not, and You are the Knower of the unseen.",
            explanation = "Jabir ibn Abdillah said: 'The Prophet (pbuh) used to teach us Istikhara in all matters just as he taught us a Surah from the Quran.'",
            reference = "Sahih al-Bukhari 1162",
            recommendedCount = 1
        ),

        // 3. Anxiety, Distress & Hardship
        DuaItem(
            id = "d5",
            title = "Relief from Anxiety, Sorrow, Laziness & Debt",
            category = "Anxiety, Distress & Hardship",
            arabicText = "اللَّهُمَّ إِنِّي أَعُوذُ بِكَ مِنَ الْهَمِّ وَالْحَزَنِ، وَالْعَجْزِ وَالْكَسَلِ، وَالْبُخْلِ وَالْجُبْنِ، وَضَلَعِ الدَّيْنِ، وَغَلَبَةِ الرِّجَالِ",
            transliteration = "Allahumma inni a'oodhu bika minal-hammi wal-hazani, wal-'ajzi wal-kasali, wal-bukhli wal-jubni, wa dala'id-dayni wa ghalabatir-rijaal.",
            translation = "O Allah, I seek refuge in You from grief and sadness, from weakness and laziness, from miserliness and cowardice, from being overcome by debt and overpowered by men.",
            explanation = "Anas ibn Malik reported that the Prophet (pbuh) used to say this supplication frequently. It systematically cures mental sorrow, character flaws, and external burdens.",
            reference = "Sahih al-Bukhari 2893",
            recommendedCount = 3
        ),
        DuaItem(
            id = "d8",
            title = "Dua of Prophet Yunus (In the Depths of Distress)",
            category = "Anxiety, Distress & Hardship",
            arabicText = "لَّا إِلَٰهَ إِلَّا أَنتَ سُبْحَانَكَ إِنِّي كُنتُ مِنَ الظَّالِمِينَ",
            transliteration = "La ilaha illa Anta subhanaka inni kuntu minaz-zalimeen.",
            translation = "There is no deity except You; exalted are You. Indeed, I have been of the wrongdoers.",
            explanation = "The Prophet (pbuh) stated: 'No Muslim supplicates with this Dua for any need except that Allah responds to him.' Yunus called upon Allah from the belly of the whale and was granted complete deliverance.",
            reference = "Surah Al-Anbiya 21:87 • Jami` at-Tirmidhi 3505",
            recommendedCount = 3
        ),
        DuaItem(
            id = "d20",
            title = "Dua for Ease in Difficult Affairs",
            category = "Anxiety, Distress & Hardship",
            arabicText = "اللَّهُمَّ لاَ سَهْلَ إِلاَّ مَا جَعَلْتَهُ سَهْلاً، وَأَنْتَ تَجْعَلُ الْحَزْنَ إِذَا شِئْتَ سَهْلاً",
            transliteration = "Allahumma la sahla illa ma ja'altahu sahla, wa Anta taj'alul-hazna idha shi'ta sahla.",
            translation = "O Allah, there is no ease except that which You have made easy, and You turn sorrow and difficulty into ease if You will.",
            explanation = "A beloved supplication whenever facing a difficult task, challenging exam, tough interview, or complex obstacle.",
            reference = "Sahih Ibn Hibban 974 • Sahih",
            recommendedCount = 1
        ),
        DuaItem(
            id = "d21",
            title = "Dua of Absolute Reliance in Urgent Need",
            category = "Anxiety, Distress & Hardship",
            arabicText = "يَا حَيُّ يَا قَيُّومُ بِرَحْمَتِكَ أَسْتَغِيثُ، أَصْلِحْ لِي شَأْنِي كُلَّهُ، وَلاَ تَكِلْنِي إِلَى نَفْسِي طَرْفَةَ عَيْنٍ",
            transliteration = "Ya Hayyu Ya Qayyoom, bi-rahmatika astagheeth, aslih li sha'ni kullahu, wa la takilni ila nafsi tarfata 'ayn.",
            translation = "O Ever-Living, O Sustainer of all existence, by Your mercy I seek assistance. Rectify for me all of my affairs, and do not leave me to myself even for the blink of an eye.",
            explanation = "Taught by the Prophet (pbuh) to Fatimah (ra) as a foundational daily prayer for guidance, protection, and humble reliance.",
            reference = "Sunan an-Nasa'i as-Sunan al-Kubra 10330 • Sahih",
            recommendedCount = 3
        ),

        // 4. Forgiveness & Repentance
        DuaItem(
            id = "d22",
            title = "Supplication for Laylatul Qadr & Absolute Pardon",
            category = "Forgiveness & Repentance",
            arabicText = "اللَّهُمَّ إِنَّكَ عَفُوٌّ تُحِبُّ الْعَفْوَ فَاعْفُ عَنِّي",
            transliteration = "Allahumma innaka 'Afuwwun tuhibbul-'afwa fa'fu 'anni.",
            translation = "O Allah, You are Most Forgiving, and You love forgiveness, so forgive and pardon me.",
            explanation = "Aishah (ra) asked the Prophet (pbuh): 'If I know which night is the Night of Decree (Laylatul Qadr), what should I say?' He taught her this concise, profound invocation.",
            reference = "Jami` at-Tirmidhi 3513 • Sahih",
            recommendedCount = 3
        ),
        DuaItem(
            id = "d23",
            title = "Dua of Adam & Eve for Forgiveness and Mercy",
            category = "Forgiveness & Repentance",
            arabicText = "رَبَّنَا ظَلَمْنَا أَنفُسَنَا وَإِن لَّمْ تَغْفِرْ لَنَا وَتَرْحَمْنَا لَنَكُونَنَّ مِنَ الْخَاسِرِينَ",
            transliteration = "Rabbana zalamna anfusana wa il-lam taghfir lana wa tarhamna lanakoonanna minal-khasireen.",
            translation = "Our Lord, we have wronged ourselves, and if You do not forgive us and have mercy upon us, we will surely be among the losers.",
            explanation = "The words inspired by Allah to Adam and Hawwa when seeking repentance. It embodies supreme humility and honesty before the Creator.",
            reference = "Surah Al-A'raf 7:23",
            recommendedCount = 3
        ),

        // 5. Protection & Ruqyah
        DuaItem(
            id = "d24",
            title = "Seeking Protection for Children & Loved Ones",
            category = "Protection & Ruqyah",
            arabicText = "أُعِيذُكَ بِكَلِمَاتِ اللَّهِ التَّامَّةِ مِنْ كُلِّ شَيْطَانٍ وَهَامَّةٍ، وَمِنْ كُلِّ عَيْنٍ لاَمَّةٍ",
            transliteration = "U'eedhuka bi-kalimatillahit-tammati min kulli shaytanin wa hammah, wa min kulli 'aynin lammah.",
            translation = "I seek refuge for you in the perfect words of Allah from every devil and poisonous reptile, and from every harmful envious eye.",
            explanation = "The Prophet (pbuh) used this exact supplication to invoke protection over his beloved grandsons Al-Hasan and Al-Husain, noting that Prophet Ibrahim used it for Ishmael and Isaac.",
            reference = "Sahih al-Bukhari 3371",
            recommendedCount = 3
        ),
        DuaItem(
            id = "d25",
            title = "Protection from Harmful Envy, Calamity & Bad Decrees",
            category = "Protection & Ruqyah",
            arabicText = "اللَّهُمَّ إِنِّي أَعُوذُ بِكَ مِنْ جَهْدِ الْبَلاَءِ، وَدَرَكِ الشَّقَاءِ، وَسُوءِ الْقَضَاءِ، وَشَمَاتَةِ الأَعْدَاءِ",
            transliteration = "Allahumma inni a'oodhu bika min jahdil-bala'i, wa darakish-shaqa'i, wa soo'il-qada'i, wa shamatatil-a'da.",
            translation = "O Allah, I seek refuge in You from the severest trials, from falling into wretchedness, from an evil destiny, and from the malicious joy of enemies.",
            explanation = "Abu Hurairah reported that the Prophet (pbuh) used to seek refuge in Allah from these four painful conditions.",
            reference = "Sahih al-Bukhari 6347 • Sahih Muslim 2707",
            recommendedCount = 1
        ),

        // 6. Health & Healing (Shifa)
        DuaItem(
            id = "d13",
            title = "Supplication for Well-being ('Afiyah) in Dunya & Akhirah",
            category = "Health & Healing (Shifa)",
            arabicText = "اللَّهُمَّ إِنِّي أَسْأَلُكَ الْعَفْوَ وَالْعَافِيَةَ فِي الدُّنْيَا وَالآخِرَةِ",
            transliteration = "Allahumma inni as'alukal-'afwa wal-'afiyata fid-dunya wal-akhirah.",
            translation = "O Allah, I ask You for pardon and well-being in this world and the Hereafter.",
            explanation = "The Prophet (pbuh) said: 'Ask Allah for pardon and well-being ('Afiyah), for after certainty of faith, no one is granted anything better than well-being.'",
            reference = "Sunan Ibn Majah 3848 • Sahih",
            recommendedCount = 3
        ),
        DuaItem(
            id = "d26",
            title = "Prophetic Prayer for Curing Illness (Ruqyah)",
            category = "Health & Healing (Shifa)",
            arabicText = "أَذْهِبِ الْبَاسَ رَبَّ النَّاسِ، اشْفِ وَأَنْتَ الشَّافِي، لاَ شِفَاءَ إِلاَّ شِفَاؤُكَ، شِفَاءً لاَ يُغَادِرُ سَقَمًا",
            transliteration = "Adh-hibil-ba'sa Rabban-nas, ishfi wa Antash-Shafi, la shifa'a illa shifa'uka, shifa'an la yughadiru saqama.",
            translation = "Remove the affliction, O Lord of mankind, and bring about healing, for You are the Healer. There is no healing except Your healing, a healing that leaves behind no ailment.",
            explanation = "Aishah (ra) reported that when someone was ill or in pain, the Prophet (pbuh) placed his right hand over the affected spot and recited this supplication.",
            reference = "Sahih al-Bukhari 5743 • Sahih Muslim 2191",
            recommendedCount = 3
        ),
        DuaItem(
            id = "d27",
            title = "Dua for Relief from Physical Pain (7-fold Remedy)",
            category = "Health & Healing (Shifa)",
            arabicText = "بِسْمِ اللَّهِ (٣x)، أَعُوذُ بِاللَّهِ وَقُدْرَتِهِ مِنْ شَرِّ مَا أَجِدُ وَأُحَاذِرُ (٧x)",
            transliteration = "Bismillah (3x). A'oodhu billahi wa qudratihi min sharri ma ajidu wa uhadhiru (7x).",
            translation = "In the Name of Allah (3 times). I seek refuge in Allah and in His power from the evil of what I feel and what I fear (7 times).",
            explanation = "Uthman ibn Abi al-As complained of severe pain in his body. The Prophet (pbuh) instructed him to place his hand on the painful area, say Bismillah three times, and repeat this supplication seven times. He reported the pain vanished completely.",
            reference = "Sahih Muslim 2202",
            recommendedCount = 7
        ),

        // 7. Travel & Journey
        DuaItem(
            id = "d11",
            title = "Dua for Travelling & Boarding a Vehicle (Safar)",
            category = "Travel & Journey",
            arabicText = "سُبْحَانَ الَّذِي سَخَّرَ لَنَا هَٰذَا وَمَا كُنَّا لَهُ مُقْرِنِينَ وَإِنَّا إِلَىٰ رَبِّنَا لَمُنقَلِبُونَ، اللَّهُمَّ إِنَّا نَسْأَلُكَ فِي سَفَرِنَا هَٰذَا الْبِرَّ وَالتَّقْوَىٰ",
            transliteration = "Subhanal-ladhee sakh-khara lana hadha wa ma kunna lahu muqrineen, wa inna ila Rabbina lamunqaliboon. Allahumma inna nas'aluka fee safarina hadhal-birra wat-taqwa.",
            translation = "Glory to Him who has subjected this to us, for we could never have done so by our own efforts, and surely to our Lord is our return. O Allah, we ask You on this journey of ours for righteousness and piety.",
            explanation = "Recited upon stepping into a car, plane, train, or ship. It grounds the traveler's heart in gratitude and turns every physical transit into an act of remembrance.",
            reference = "Surah Az-Zukhruf 43:13-14 • Sahih Muslim 1342",
            recommendedCount = 1
        ),
        DuaItem(
            id = "d28",
            title = "Dua Upon Leaving the House (Full Divine Escort)",
            category = "Travel & Journey",
            arabicText = "بِسْمِ اللَّهِ، تَوَكَّلْتُ عَلَى اللَّهِ، وَلاَ حَوْلَ وَلاَ قُوَّةَ إِلاَّ بِاللَّهِ",
            transliteration = "Bismillahi, tawakkaltu 'alallahi, wa la hawla wa la quwwata illa billah.",
            translation = "In the Name of Allah, I place my trust in Allah, and there is no power nor strength except with Allah.",
            explanation = "The Prophet (pbuh) said: 'When a person says this upon leaving his home, it is said to him: You are guided, defended, and protected, and the devils turn away from him.'",
            reference = "Sunan Abi Dawud 5095 • Jami` at-Tirmidhi 3426",
            recommendedCount = 1
        ),

        // 8. Food, Drink & Fasting
        DuaItem(
            id = "d3",
            title = "Dua for Breaking the Fast (Iftar)",
            category = "Food, Drink & Fasting",
            arabicText = "ذَهَبَ الظَّمَأُ وَابْتَلَّتِ الْعُرُوقُ وَثَبَتَ الأَجْرُ إِنْ شَاءَ اللَّهُ",
            transliteration = "Dhahabadh-dhama'u wabtallatil-'urooqu wa thabatal-ajru in sha'Allah.",
            translation = "The thirst is gone, the veins are moistened, and the reward is confirmed, if Allah wills.",
            explanation = "Said upon drinking water or eating a date at sunset to break the fast. It expresses heartfelt gratitude for physical relief and asserts eternal divine reward.",
            reference = "Sunan Abi Dawud 2357 • Hasan",
            recommendedCount = 1
        ),
        DuaItem(
            id = "d4",
            title = "Intention for Fasting (Sehri / Suhoor Niyyah)",
            category = "Food, Drink & Fasting",
            arabicText = "وَبِصَوْمِ غَدٍ نَوَيْتُ مِنْ شَهْرِ رَمَضَانَ",
            transliteration = "Wa bisawmi ghadin nawaytu min shahri Ramadan.",
            translation = "And I intend to keep the fast tomorrow for the month of Ramadan.",
            explanation = "Affirms the sincere intention in the heart before the dawn prayer (Fajr) to dedicate the fasting day solely to Allah's pleasure.",
            reference = "Traditional Fiqh formulation for fasting intention",
            recommendedCount = 1
        ),
        DuaItem(
            id = "d29",
            title = "Dua After Finishing a Meal",
            category = "Food, Drink & Fasting",
            arabicText = "الْحَمْدُ لِلَّهِ الَّذِي أَطْعَمَنِي هَٰذَا وَرَزَقَنِيهِ مِنْ غَيْرِ حَوْلٍ مِّنِّي وَلاَ قُوَّةٍ",
            transliteration = "Alhamdu lillahil-ladhi at'amani hadha wa razaqaneehi min ghayri hawlin minni wa la quwwah.",
            translation = "All praise is for Allah who fed me this food and provided it for me without any power or strength on my part.",
            explanation = "The Prophet (pbuh) said: 'Whoever eats food and then says this, all his past minor sins will be forgiven.'",
            reference = "Jami` at-Tirmidhi 3458 • Hasan",
            recommendedCount = 1
        ),

        // 9. Home, Family & Parents
        DuaItem(
            id = "d9",
            title = "Dua for Parents (Mercy & Lifelong Gratitude)",
            category = "Home, Family & Parents",
            arabicText = "رَّبِّ ارْحَمْهُمَا كَمَا رَبَّيَانِي صَغِيرًا",
            transliteration = "Rabbir-hamhuma kama rabbayani sagheera.",
            translation = "My Lord, have mercy upon them both as they brought me up when I was small.",
            explanation = "A direct divine instruction in Surah Al-Isra, reciprocating the tenderness, sleepless nights, and sacrifices our parents endured for us.",
            reference = "Surah Al-Isra 17:24",
            recommendedCount = 3
        ),
        DuaItem(
            id = "d30",
            title = "Dua for a Loving Spouse & Righteous Children",
            category = "Home, Family & Parents",
            arabicText = "رَبَّنَا هَبْ لَنَا مِنْ أَزْوَاجِنَا وَذُرِّيَّاتِنَا قُرَّةَ أَعْيُنٍ وَاجْعَلْنَا لِلْمُتَّقِينَ إِمَامًا",
            transliteration = "Rabbana hab lana min azwajina wa dhurriyyatina qurrata a'yunin waj'alna lil-muttaqeena imama.",
            translation = "Our Lord, grant us from among our spouses and offspring comfort to our eyes and make us an example for the righteous.",
            explanation = "The prayer of the 'Servants of the Most Merciful' (Ibad-ur-Rahman) seeking family harmony, tranquility, and noble leadership in faith.",
            reference = "Surah Al-Furqan 25:74",
            recommendedCount = 3
        ),
        DuaItem(
            id = "d31",
            title = "Dua for a Blessed Home & Dwelling",
            category = "Home, Family & Parents",
            arabicText = "رَّبِّ أَنزِلْنِي مُنزَلًا مُّبَارَكًا وَأَنتَ خَيْرُ الْمُنزِلِينَ",
            transliteration = "Rabbi anzilni munzalan mubarakan wa Anta khayrul-munzileen.",
            translation = "My Lord, let me land at a blessed landing place, and You are the best to accommodate us.",
            explanation = "Taught to Prophet Nuh upon disembarking from the ark. A blessed prayer when moving into a new home, staying in a hotel, or blessing one's current residence.",
            reference = "Surah Al-Mu'minoon 23:29",
            recommendedCount = 1
        ),

        // 10. Sleep & Waking Up
        DuaItem(
            id = "d12",
            title = "Dua When Waking Up in the Morning",
            category = "Sleep & Waking Up",
            arabicText = "الْحَمْدُ لِلَّهِ الَّذِي أَحْيَانَا بَعْدَ مَا أَمَاتَنَا وَإِلَيْهِ النُّشُورُ",
            transliteration = "Alhamdu lillahil-ladhi ahyana ba'da ma amatana wa ilayhin-nushoor.",
            translation = "All praise is for Allah who gave us life after having caused us to die, and unto Him is the resurrection.",
            explanation = "The first words spoken upon opening one's eyes. Sleep is a minor death, and waking is a fresh opportunity to perform good deeds.",
            reference = "Sahih al-Bukhari 6312",
            recommendedCount = 1
        ),
        DuaItem(
            id = "d32",
            title = "Dua Before Going to Sleep (Surrendering the Soul)",
            category = "Sleep & Waking Up",
            arabicText = "بِاسْمِكَ رَبِّي وَضَعْتُ جَنْبِي وَبِكَ أَرْفَعُهُ، إِنْ أَمْسَكْتَ نَفْسِي فَارْحَمْهَا، وَإِنْ أَرْسَلْتَهَا فَاحْفَظْهَا بِمَا تَحْفَظُ بِهِ عِبَادَكَ الصَّالِحِينَ",
            transliteration = "Bismika Rabbi wada'tu janbi wa bika arfa'uh, in amsakta nafsi farhamha, wa in arsaltaha fahfazha bima tahfazu bihi 'ibadakas-saliheen.",
            translation = "In Your Name, my Lord, I lay down my side and in Your Name I raise it up. If You take my soul, have mercy upon it, and if You send it back, protect it with that which You protect Your righteous servants.",
            explanation = "The Prophet (pbuh) instructed that before sleeping, one should dust their bed and recite this supplication with total surrender to Allah.",
            reference = "Sahih al-Bukhari 6320 • Sahih Muslim 2714",
            recommendedCount = 1
        ),

        // 11. Quranic Duas (Rabbana)
        DuaItem(
            id = "d33",
            title = "The Ultimate Quranic Supplication for Dunya & Akhirah",
            category = "Quranic Duas (Rabbana)",
            arabicText = "رَبَّنَا آتِنَا فِي الدُّنْيَا حَسَنَةً وَفِي الآخِرَةِ حَسَنَةً وَقِنَا عَذَابَ النَّارِ",
            transliteration = "Rabbana atina fid-dunya hasanatan wa fil-akhirati hasanatan wa qina 'adhaban-nar.",
            translation = "Our Lord, give us in this world that which is good and in the Hereafter that which is good and protect us from the punishment of the Fire.",
            explanation = "Anas (ra) reported that this was the most frequent supplication made by the Prophet Muhammad (pbuh). It encompasses every blessing of this life and eternal salvation.",
            reference = "Surah Al-Baqarah 2:201 • Sahih al-Bukhari 6389",
            recommendedCount = 3
        ),
        DuaItem(
            id = "d34",
            title = "Dua for Steadfast Faith & Divine Mercy",
            category = "Quranic Duas (Rabbana)",
            arabicText = "رَبَّنَا لَا تُزِغْ قُلُوبَنَا بَعْدَ إِذْ هَدَيْتَنَا وَهَبْ لَنَا مِن لَّدُنكَ رَحْمَةً ۚ إِنَّكَ أَنتَ الْوَهَّابُ",
            transliteration = "Rabbana la tuzigh quloobana ba'da idh hadaytana wa hab lana min ladunka rahmah, innaka Antal-Wahhab.",
            translation = "Our Lord, let not our hearts deviate after You have guided us and grant us from Yourself mercy. Indeed, You are the Bestower.",
            explanation = "Supplication of those firmly grounded in knowledge, asking Allah to safeguard their hearts from doubt, worldly distractions, and deviation.",
            reference = "Surah Ali 'Imran 3:8",
            recommendedCount = 3
        ),
        DuaItem(
            id = "d35",
            title = "Prophet Ibrahim's Prayer for Prayerful Generations",
            category = "Quranic Duas (Rabbana)",
            arabicText = "رَبِّ اجْعَلْنِي مُقِيمَ الصَّلَاةِ وَمِن ذُرِّيَّتِي ۚ رَبَّنَا وَتَقَبَّلْ دُعَاءِ، رَبَّنَا اغْفِرْ لِي وَلِوَالِدَيَّ وَلِلْمُؤْمِنِينَ يَوْمَ يَقُومُ الْحِسَابُ",
            transliteration = "Rabbij-'alni muqeemas-salati wa min dhurriyyati, Rabbana wa taqabbal du'a. Rabbanagh-fir li wa li-walidayya wa lil-mu'mineena yawma yaqoomul-hisab.",
            translation = "My Lord, make me an establisher of prayer, and many from my descendants. Our Lord, and accept my supplication. Our Lord, forgive me and my parents and the believers the Day the account is established.",
            explanation = "Prophet Ibrahim's noble invocation for himself, his offspring, his parents, and the global Ummah of believers on the Day of Judgment.",
            reference = "Surah Ibrahim 14:40-41",
            recommendedCount = 3
        ),

        // 12. Daily Life & Wisdom
        DuaItem(
            id = "d10",
            title = "Dua for Seeking Beneficial Knowledge & Wisdom",
            category = "Daily Life & Wisdom",
            arabicText = "رَّبِّ زِدْنِي عِلْمًا",
            transliteration = "Rabbi zidni 'ilma.",
            translation = "My Lord, increase me in knowledge.",
            explanation = "The only worldly expansion that Allah specifically commanded His Prophet (pbuh) to pray for more of. It encompasses actionable, spiritually uplifting wisdom.",
            reference = "Surah Ta-Ha 20:114",
            recommendedCount = 3
        ),
        DuaItem(
            id = "d36",
            title = "Dua Upon Entering the Market or Shopping Center",
            category = "Daily Life & Wisdom",
            arabicText = "لاَ إِلَهَ إِلاَّ اللَّهُ وَحْدَهُ لاَ شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ، يُحْيِي وَيُمِيتُ، وَهُوَ حَيٌّ لاَ يَمُوتُ، بِيَدِهِ الْخَيْرُ، وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ",
            transliteration = "La ilaha illallahu wahdahu la shareeka lah, lahul-mulku wa lahul-hamdu, yuhyee wa yumeetu, wa Huwa hayyun la yamoot, biyadihil-khayr, wa Huwa 'ala kulli shay'in Qadeer.",
            translation = "There is no deity except Allah alone without partner. To Him belongs all sovereignty and praise. He gives life and causes death, and He is Ever-Living and does not die. In His Hand is all good, and He is over all things competent.",
            explanation = "The Prophet (pbuh) taught that entering the busy worldly market with this statement of pure Tawhid yields one million rewards, erases one million sins, and elevates one's rank in Paradise.",
            reference = "Jami` at-Tirmidhi 3428 • Hasan",
            recommendedCount = 1
        )
    )
}
