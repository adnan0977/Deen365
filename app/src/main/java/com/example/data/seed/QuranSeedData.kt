package com.example.data.seed

import com.example.data.local.AyahEntity

data class SurahMeta(
    val number: Int,
    val englishName: String,
    val arabicName: String,
    val englishTranslation: String,
    val revelationType: String, // "Meccan" or "Medinan"
    val numberOfAyahs: Int
)

object QuranSeedData {

    val SURAH_LIST = listOf(
        SurahMeta(1, "Al-Fatihah", "الفاتحة", "The Opening", "Meccan", 7),
        SurahMeta(2, "Al-Baqarah", "البقرة", "The Cow", "Medinan", 286),
        SurahMeta(3, "Ali 'Imran", "آل عمران", "Family of Imran", "Medinan", 200),
        SurahMeta(4, "An-Nisa", "النساء", "The Women", "Medinan", 176),
        SurahMeta(5, "Al-Ma'idah", "المائدة", "The Table Spread", "Medinan", 120),
        SurahMeta(6, "Al-An'am", "الأنعام", "The Cattle", "Meccan", 165),
        SurahMeta(7, "Al-A'raf", "الأعراف", "The Heights", "Meccan", 206),
        SurahMeta(8, "Al-Anfal", "الأنفال", "The Spoils of War", "Medinan", 75),
        SurahMeta(9, "At-Tawbah", "التوبة", "The Repentance", "Medinan", 129),
        SurahMeta(10, "Yunus", "يونس", "Jonah", "Meccan", 109),
        SurahMeta(11, "Hud", "هود", "Hud", "Meccan", 123),
        SurahMeta(12, "Yusuf", "يوسف", "Joseph", "Meccan", 111),
        SurahMeta(13, "Ar-Ra'd", "الرعد", "The Thunder", "Medinan", 43),
        SurahMeta(14, "Ibrahim", "إبراهيم", "Abraham", "Meccan", 52),
        SurahMeta(15, "Al-Hijr", "الحجر", "The Rocky Tract", "Meccan", 99),
        SurahMeta(16, "An-Nahl", "النحل", "The Bee", "Meccan", 128),
        SurahMeta(17, "Al-Isra", "الإسراء", "The Night Journey", "Meccan", 111),
        SurahMeta(18, "Al-Kahf", "الكهف", "The Cave", "Meccan", 110),
        SurahMeta(19, "Maryam", "مريم", "Mary", "Meccan", 98),
        SurahMeta(20, "Taha", "طه", "Ta-Ha", "Meccan", 135),
        SurahMeta(21, "Al-Anbiya", "الأنبياء", "The Prophets", "Meccan", 112),
        SurahMeta(22, "Al-Hajj", "الحج", "The Pilgrimage", "Medinan", 78),
        SurahMeta(23, "Al-Mu'minun", "المؤمنون", "The Believers", "Meccan", 118),
        SurahMeta(24, "An-Nur", "النور", "The Light", "Medinan", 64),
        SurahMeta(25, "Al-Furqan", "الفرقان", "The Criterion", "Meccan", 77),
        SurahMeta(26, "Ash-Shu'ara", "الشعراء", "The Poets", "Meccan", 227),
        SurahMeta(27, "An-Naml", "النمل", "The Ant", "Meccan", 93),
        SurahMeta(28, "Al-Qasas", "القصص", "The Stories", "Meccan", 88),
        SurahMeta(29, "Al-'Ankabut", "العنكبوت", "The Spider", "Meccan", 69),
        SurahMeta(30, "Ar-Rum", "الروم", "The Romans", "Meccan", 60),
        SurahMeta(31, "Luqman", "لقمان", "Luqman", "Meccan", 34),
        SurahMeta(32, "As-Sajdah", "السجدة", "The Prostration", "Meccan", 30),
        SurahMeta(33, "Al-Ahzab", "الأحزاب", "The Combined Forces", "Medinan", 73),
        SurahMeta(34, "Saba", "سبأ", "Sheba", "Meccan", 54),
        SurahMeta(35, "Fatir", "فاطر", "Originator", "Meccan", 45),
        SurahMeta(36, "Ya-Sin", "يس", "Ya-Sin", "Meccan", 83),
        SurahMeta(37, "As-Saffat", "الصافات", "Those Who Set The Ranks", "Meccan", 182),
        SurahMeta(38, "Sad", "ص", "The Letter Sad", "Meccan", 88),
        SurahMeta(39, "Az-Zumar", "الزمر", "The Troops", "Meccan", 75),
        SurahMeta(40, "Ghafir", "غافر", "The Forgiver", "Meccan", 85),
        SurahMeta(41, "Fussilat", "فصلت", "Explained in Detail", "Meccan", 54),
        SurahMeta(42, "Ash-Shura", "الشورى", "The Consultation", "Meccan", 53),
        SurahMeta(43, "Az-Zukhruf", "الزخرف", "The Ornaments of Gold", "Meccan", 89),
        SurahMeta(44, "Ad-Dukhan", "الدخان", "The Smoke", "Meccan", 59),
        SurahMeta(45, "Al-Jathiyah", "الجاثية", "The Crouching", "Meccan", 37),
        SurahMeta(46, "Al-Ahqaf", "الأحقاف", "The Wind-Curved Sandhills", "Meccan", 35),
        SurahMeta(47, "Muhammad", "محمد", "Muhammad", "Medinan", 38),
        SurahMeta(48, "Al-Fath", "الفتح", "The Victory", "Medinan", 29),
        SurahMeta(49, "Al-Hujurat", "الحجرات", "The Rooms", "Medinan", 18),
        SurahMeta(50, "Qaf", "ق", "The Letter Qaf", "Meccan", 45),
        SurahMeta(51, "Adh-Dhariyat", "الذاريات", "The Winnowing Winds", "Meccan", 60),
        SurahMeta(52, "At-Tur", "الطور", "The Mount", "Meccan", 49),
        SurahMeta(53, "An-Najm", "النجم", "The Star", "Meccan", 62),
        SurahMeta(54, "Al-Qamar", "القمر", "The Moon", "Meccan", 55),
        SurahMeta(55, "Ar-Rahman", "الرحمن", "The Beneficent", "Medinan", 78),
        SurahMeta(56, "Al-Waqi'ah", "الواقعة", "The Inevitable", "Meccan", 96),
        SurahMeta(57, "Al-Hadid", "الحديد", "The Iron", "Medinan", 29),
        SurahMeta(58, "Al-Mujadila", "المجادلة", "The Pleading Woman", "Medinan", 22),
        SurahMeta(59, "Al-Hashr", "الحشر", "The Exile", "Medinan", 24),
        SurahMeta(60, "Al-Mumtahanah", "الممتحنة", "She That Is To Be Examined", "Medinan", 13),
        SurahMeta(61, "As-Saff", "الصف", "The Ranks", "Medinan", 14),
        SurahMeta(62, "Al-Jumu'ah", "الجمعة", "The Congregation", "Medinan", 11),
        SurahMeta(63, "Al-Munafiqun", "المنافقون", "The Hypocrites", "Medinan", 11),
        SurahMeta(64, "At-Taghabun", "التغابن", "The Mutual Disillusion", "Medinan", 18),
        SurahMeta(65, "At-Talaq", "الطلاق", "The Divorce", "Medinan", 12),
        SurahMeta(66, "At-Tahrim", "التحريم", "The Prohibition", "Medinan", 12),
        SurahMeta(67, "Al-Mulk", "الملك", "The Sovereignty", "Meccan", 30),
        SurahMeta(68, "Al-Qalam", "القلم", "The Pen", "Meccan", 52),
        SurahMeta(69, "Al-Haqqah", "الحاقة", "The Reality", "Meccan", 52),
        SurahMeta(70, "Al-Ma'arij", "المعارج", "The Ascending Stairways", "Meccan", 44),
        SurahMeta(71, "Nuh", "نوح", "Noah", "Meccan", 28),
        SurahMeta(72, "Al-Jinn", "الجن", "The Jinn", "Meccan", 28),
        SurahMeta(73, "Al-Muzzammil", "المزمل", "The Enshrouded One", "Meccan", 20),
        SurahMeta(74, "Al-Muddaththir", "المدثر", "The Cloaked One", "Meccan", 56),
        SurahMeta(75, "Al-Qiyamah", "القيامة", "The Resurrection", "Meccan", 40),
        SurahMeta(76, "Al-Insan", "الإنسان", "Man", "Medinan", 31),
        SurahMeta(77, "Al-Mursalat", "المرسلات", "The Emissaries", "Meccan", 50),
        SurahMeta(78, "An-Naba", "النبأ", "The Tidings", "Meccan", 40),
        SurahMeta(79, "An-Nazi'at", "النازعات", "Those Who Drag Forth", "Meccan", 46),
        SurahMeta(80, "'Abasa", "عبس", "He Frowned", "Meccan", 42),
        SurahMeta(81, "At-Takwir", "التكوير", "The Overthrowing", "Meccan", 29),
        SurahMeta(82, "Al-Infitar", "الانفطار", "The Cleaving", "Meccan", 19),
        SurahMeta(83, "Al-Mutaffifin", "المطففين", "Defrauding", "Meccan", 36),
        SurahMeta(84, "Al-Inshiqaq", "الانشقاق", "The Splitting Open", "Meccan", 25),
        SurahMeta(85, "Al-Buruj", "البروج", "The Mansions of the Stars", "Meccan", 22),
        SurahMeta(86, "At-Tariq", "الطارق", "The Morning Star", "Meccan", 17),
        SurahMeta(87, "Al-A'la", "الأعلى", "The Most High", "Meccan", 19),
        SurahMeta(88, "Al-Ghashiyah", "الغاشية", "The Overwhelming", "Meccan", 26),
        SurahMeta(89, "Al-Fajr", "الفجر", "The Dawn", "Meccan", 30),
        SurahMeta(90, "Al-Balad", "البلد", "The City", "Meccan", 20),
        SurahMeta(91, "Ash-Shams", "الشمس", "The Sun", "Meccan", 15),
        SurahMeta(92, "Al-Layl", "الليل", "The Night", "Meccan", 21),
        SurahMeta(93, "Ad-Duhaa", "الضحى", "The Morning Hours", "Meccan", 11),
        SurahMeta(94, "Ash-Sharh", "الشرح", "The Relief", "Meccan", 8),
        SurahMeta(95, "At-Tin", "التين", "The Fig", "Meccan", 8),
        SurahMeta(96, "Al-'Alaq", "العلق", "The Clot", "Meccan", 19),
        SurahMeta(97, "Al-Qadr", "القدر", "The Power / Decree", "Meccan", 5),
        SurahMeta(98, "Al-Bayyinah", "البينة", "The Clear Proof", "Medinan", 8),
        SurahMeta(99, "Az-Zalzalah", "الزلزلة", "The Earthquake", "Medinan", 8),
        SurahMeta(100, "Al-'Adiyat", "العاديات", "The Courser", "Meccan", 11),
        SurahMeta(101, "Al-Qari'ah", "القارعة", "The Calamity", "Meccan", 11),
        SurahMeta(102, "At-Takathur", "التكاثر", "Rivalry in World Increase", "Meccan", 8),
        SurahMeta(103, "Al-'Asr", "العصر", "The Declining Day", "Meccan", 3),
        SurahMeta(104, "Al-Humazah", "الهمزة", "The Scorner", "Meccan", 9),
        SurahMeta(105, "Al-Fil", "الفيل", "The Elephant", "Meccan", 5),
        SurahMeta(106, "Quraysh", "قريش", "Quraysh", "Meccan", 4),
        SurahMeta(107, "Al-Ma'un", "الماعون", "Small Kindnesses", "Meccan", 7),
        SurahMeta(108, "Al-Kawthar", "الكوثر", "Abundance", "Meccan", 3),
        SurahMeta(109, "Al-Kafirun", "الكافرون", "The Disbelievers", "Meccan", 6),
        SurahMeta(110, "An-Nasr", "النصر", "Divine Support", "Medinan", 3),
        SurahMeta(111, "Al-Masad", "المسد", "The Palm Fiber", "Meccan", 5),
        SurahMeta(112, "Al-Ikhlas", "الإخلاص", "The Sincerity", "Meccan", 4),
        SurahMeta(113, "Al-Falaq", "الفلق", "The Daybreak", "Meccan", 5),
        SurahMeta(114, "An-Nas", "الناس", "Mankind", "Meccan", 6)
    )

    fun getFallbackAyahsForSurah(surahNumber: Int): List<AyahEntity> {
        return when (surahNumber) {
            1 -> listOf(
                AyahEntity(1, 1, "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ", "In the name of Allah, the Entirely Merciful, the Especially Merciful.", "Bismillaahir Rahmaanir Raheem"),
                AyahEntity(1, 2, "الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ", "[All] praise is [due] to Allah, Lord of the worlds -", "Alhamdu lillaahi Rabbil 'aalameen"),
                AyahEntity(1, 3, "الرَّحْمَٰنِ الرَّحِيمِ", "The Entirely Merciful, the Especially Merciful,", "Ar-Rahmaanir-Raheem"),
                AyahEntity(1, 4, "مَالِكِ يَوْمِ الدِّينِ", "Sovereign of the Day of Recompense.", "Maaliki Yawmid-Deen"),
                AyahEntity(1, 5, "إِيَّاكَ نَعْبُدُ وَإِيَّاكَ نَسْتَعِينُ", "It is You we worship and You we ask for help.", "Iyyaaka na'budu wa lyyaaka nasta'een"),
                AyahEntity(1, 6, "اهْدِنَا الصِّرَاطَ الْمُسْتَقِيمَ", "Guide us to the straight path -", "Ihdinas-Siraatal-Mustaqeem"),
                AyahEntity(1, 7, "صِرَاطَ الَّذِينَ أَنْعَمْتَ عَلَيْهِمْ غَيْرِ الْمَغْضُوبِ عَلَيْهِمْ وَلَا الضَّالِّينَ", "The path of those upon whom You have bestowed favor, not of those who have evoked [Your] anger or of those who are astray.", "Siraatal-lazeena an'amta 'alayhim ghayril-maghdoobi 'alayhim wa lad-daaalleen")
            )
            112 -> listOf(
                AyahEntity(112, 1, "قُلْ هُوَ اللَّهُ أَحَدٌ", "Say, 'He is Allah, [who is] One,", "Qul Huwal Laahu Ahad"),
                AyahEntity(112, 2, "اللَّهُ الصَّمَدُ", "Allah, the Eternal Refuge.", "Allahus Samad"),
                AyahEntity(112, 3, "لَمْ يَلِدْ وَلَمْ يُولَدْ", "He neither begets nor is born,", "Lam yalid wa lam yoolad"),
                AyahEntity(112, 4, "وَلَمْ يَكُنْ لَهُ كُفُوًا أَحَدٌ", "Nor is there to Him any equivalent.'", "Wa lam yakul lahoo kufuwan ahad")
            )
            113 -> listOf(
                AyahEntity(113, 1, "قُلْ أَعُوذُ بِرَبِّ الْفَلَقِ", "Say, 'I seek refuge in the Lord of daybreak", "Qul a'oozu bi rabbil-falaq"),
                AyahEntity(113, 2, "مِنْ شَرِّ مَا خَلَقَ", "From the evil of that which He created", "Min sharri maa khalaq"),
                AyahEntity(113, 3, "وَمِنْ شَرِّ غَاسِقٍ إِذَا وَقَبَ", "And from the evil of darkness when it settles", "Wa min sharri ghaasiqin izaa waqab"),
                AyahEntity(113, 4, "وَمِنْ شَرِّ النَّفَّاثَاتِ فِي الْعُقَدِ", "And from the evil of the blowers in knots", "Wa min sharrin-naffaasaati fil 'uqad"),
                AyahEntity(113, 5, "وَمِنْ شَرِّ حَاسِدٍ إِذَا حَسَدَ", "And from the evil of an envier when he envies.'", "Wa min sharri haasidin izaa hasad")
            )
            114 -> listOf(
                AyahEntity(114, 1, "قُلْ أَعُوذُ بِرَبِّ النَّاسِ", "Say, 'I seek refuge in the Lord of mankind,", "Qul a'oozu bi rabbin naas"),
                AyahEntity(114, 2, "مَلِكِ النَّاسِ", "The Sovereign of mankind.", "Malikin naas"),
                AyahEntity(114, 3, "إِلَٰهِ النَّاسِ", "The God of mankind,", "Ilaahin naas"),
                AyahEntity(114, 4, "مِنْ شَرِّ الْوَسْوَاسِ الْخَنَّاسِ", "From the evil of the retreating whisperer -", "Min sharril waswaasil khannaas"),
                AyahEntity(114, 5, "الَّذِي يُوَسْوِسُ فِي صُدُورِ النَّاسِ", "Who whispers into the breasts of mankind -", "Allazee yuwaswisu fee sudoorin naas"),
                AyahEntity(114, 6, "مِنَ الْجِنَّةِ وَالنَّاسِ", "From among the jinn and mankind.'", "Minal jinnati wannaas")
            )
            67 -> listOf(
                AyahEntity(67, 1, "تَبَارَكَ الَّذِي بِيَدِهِ الْمُلْكُ وَهُوَ عَلَىٰ كُلِّ شَيْءٍ قَدِيرٌ", "Blessed is He in whose hand is dominion, and He is over all things competent -", "Tabaarakal lazee biyadihil mulku wa Huwa 'alaa kulli shai'in Qadeer"),
                AyahEntity(67, 2, "الَّذِي خَلَقَ الْمَوْتَ وَالْحَيَاةَ لِيَبْلُوَكُمْ أَيُّكُمْ أَحْسَنُ عَمَلًا ۚ وَهُوَ الْعَزِيزُ الْغَفُورُ", "[He] who created death and life to test you [as to] which of you is best in deed - and He is the Exalted in Might, the Forgiving -", "Allazee khalaqal mawta walhayaata liyabluwakum ayyukum ahsanu 'amalaa; wa huwal 'Azeezul Ghafoor"),
                AyahEntity(67, 3, "الَّذِي خَلَقَ سَبْعَ سَمَاوَاتٍ طِبَاقًا ۖ مَّا تَرَىٰ فِي خَلْقِ الرَّحْمَٰنِ مِن تَفَاوُتٍ", "[And] who created seven heavens in layers. You see no flaw in the creation of the Most Merciful.", "Allazee khalaqa sab'a samaawaatin tibaaqan maa taraa fee khalqir Rahmaani min tafaawut")
            )
            36 -> listOf(
                AyahEntity(36, 1, "يس", "Ya, Seen.", "Yaa-Seeen"),
                AyahEntity(36, 2, "وَالْقُرْآنِ الْحَكِيمِ", "By the wise Qur'an.", "Wal-Qur-aanil-Hakeem"),
                AyahEntity(36, 3, "إِنَّكَ لَمِنَ الْمُرْسَلِينَ", "Indeed you, [O Muhammad], are from among the messengers,", "Innaka laminal mursaleen"),
                AyahEntity(36, 4, "عَلَىٰ صِرَاطٍ مُّسْتَقِيمٍ", "On a straight path.", "'Alaa Siraatim Mustaqeem"),
                AyahEntity(36, 5, "تَنزِيلَ الْعَزِيزِ الرَّحِيمِ", "[This is] a revelation of the Exalted in Might, the Merciful,", "Tanzeelal 'Azeezir Raheem")
            )
            else -> {
                // Return descriptive seed ayah for immediate offline display if network hasn't loaded yet
                val meta = SURAH_LIST.find { it.number == surahNumber }
                listOf(
                    AyahEntity(
                        surahNumber = surahNumber,
                        ayahNumber = 1,
                        arabicText = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                        translation = "In the name of Allah, the Entirely Merciful, the Especially Merciful. (Surah ${meta?.englishName ?: surahNumber} - Tap to load all ${meta?.numberOfAyahs ?: ""} Ayahs via API or view offline)",
                        transliteration = "Bismillaahir Rahmaanir Raheem"
                    )
                )
            }
        }
    }
}
