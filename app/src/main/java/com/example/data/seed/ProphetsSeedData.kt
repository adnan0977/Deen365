package com.example.data.seed

data class ProphetStory(
    val id: Int,
    val nameEnglish: String,
    val nameArabic: String,
    val title: String,
    val era: String,
    val quranicMentionsCount: Int,
    val keySurahs: List<String>,
    val summary: String,
    val majorTrials: List<String>,
    val miracles: List<String>,
    val spiritualLessons: List<String>,
    val keyAyahReference: String,
    val keyAyahArabic: String,
    val keyAyahTranslation: String
)

object ProphetsSeedData {
    val ALL_PROPHETS: List<ProphetStory> = listOf(
        ProphetStory(
            id = 1,
            nameEnglish = "Adam (AS)",
            nameArabic = "آدم",
            title = "Father of Mankind & First Prophet",
            era = "Beginning of Creation",
            quranicMentionsCount = 25,
            keySurahs = listOf("Al-Baqarah", "Al-A'raf", "Ta-Ha"),
            summary = "Created from clay by Allah's divine hand, blessed with knowledge of all names, and tested in Paradise before being sent as Allah's vicegerent (Khalifah) on Earth.",
            majorTrials = listOf(
                "Iblis's refusal to prostrate out of arrogance",
                "Whisperings of Satan to approach the forbidden tree",
                "Descent to Earth and seeking sincere repentance (Tawbah)"
            ),
            miracles = listOf(
                "Created directly from clay without parents",
                "Endowed with universal knowledge of all names",
                "Angels commanded to prostrate to him out of respect"
            ),
            spiritualLessons = listOf(
                "Human dignity stems from knowledge and God-consciousness",
                "Repentance immediately after sin restores closeness to Allah",
                "Iblis's downfall was arrogance (Kibr); humility is the salvation of the soul"
            ),
            keyAyahReference = "Surah Al-Baqarah (2:37)",
            keyAyahArabic = "فَتَلَقَّىٰ آدَمُ مِن رَّبِّهِ كَلِمَاتٍ فَتَابَ عَلَيْهِ ۚ إِنَّهُ هُوَ التَّوَّابُ الرَّحِيمُ",
            keyAyahTranslation = "Then Adam received from his Lord [some] words, and He accepted his repentance. Indeed, it is He who is the Accepting of Repentance, the Merciful."
        ),
        ProphetStory(
            id = 2,
            nameEnglish = "Idris (AS)",
            nameArabic = "إدريس",
            title = "Enoch - The Man of Truth & Wisdom",
            era = "Between Adam and Nuh",
            quranicMentionsCount = 2,
            keySurahs = listOf("Maryam", "Al-Anbiya"),
            summary = "Renowned for his truthfulness, patience, devotion to writing, astronomy, and tailoring, whom Allah raised to a lofty station.",
            majorTrials = listOf(
                "Reforming humanity when idolatry first began creeping into society",
                "Constant struggle against ignorance and heedlessness"
            ),
            miracles = listOf(
                "First to write with a pen and teach calculation",
                "Raised to an exalted celestial station (Makanun 'Aliyya)"
            ),
            spiritualLessons = listOf(
                "Pursuit of beneficial sacred and worldly knowledge is an act of worship",
                "Patience in preaching the truth elevates one's rank with Allah"
            ),
            keyAyahReference = "Surah Maryam (19:56-57)",
            keyAyahArabic = "وَاذْكُرْ فِي الْكِتَابِ إِدْرِيسَ ۚ إِنَّهُ كَانَ صِدِّيقًا نَّبِيًّا ۝ وَرَفَعْنَاهُ مَكَانًا عَلِيًّا",
            keyAyahTranslation = "And mention in the Book, Idris. Indeed, he was a man of truth and a prophet. And We raised him to a high station."
        ),
        ProphetStory(
            id = 3,
            nameEnglish = "Nuh (AS)",
            nameArabic = "نوح",
            title = "Noah - The Grateful Servant & Ark Builder",
            era = "Pre-Flood Antiquity",
            quranicMentionsCount = 43,
            keySurahs = listOf("Nuh", "Hud", "Al-Mu'minun", "Ash-Shu'ara"),
            summary = "Called his people day and night for 950 years with boundless patience, built the Great Ark upon Allah's revelation, and preserved the righteous from the global deluge.",
            majorTrials = listOf(
                "950 years of relentless mockery, rejection, and abuse",
                "Rejection of the message by his own wife and son (Yam)",
                "Building a giant vessel in an arid desert with no water in sight"
            ),
            miracles = listOf(
                "Construction of the Ark under divine inspiration and angel guidance",
                "The miraculous gushing of the oven (Tannur) signaling the flood",
                "Preservation of believers and animals through global tempest"
            ),
            spiritualLessons = listOf(
                "Success in da'wah is measured by steadfastness, not sheer numbers",
                "Family ties do not supersede faith; salvation is strictly individual",
                "True gratitude to Allah sustains the heart through centuries of trial"
            ),
            keyAyahReference = "Surah Al-Isra (17:3)",
            keyAyahArabic = "ذُرِّيَّةَ مَنْ حَمَلْنَا مَعَ نُوحٍ ۚ إِنَّهُ كَانَ عَبْدًا شَكُورًا",
            keyAyahTranslation = "O descendants of those We carried [in the ship] with Noah. Indeed, he was a grateful servant."
        ),
        ProphetStory(
            id = 4,
            nameEnglish = "Hud (AS)",
            nameArabic = "هود",
            title = "Prophet to the Mighty People of 'Ad",
            era = "Southern Arabia (Al-Ahqaf)",
            quranicMentionsCount = 7,
            keySurahs = listOf("Hud", "Al-A'raf", "Ash-Shu'ara"),
            summary = "Sent to the gigantic, prosperous people of 'Ad who built towering pillars in Iram. He warned them against tyranny and idolatry.",
            majorTrials = listOf(
                "Accusations of foolishness and lying by the arrogant chieftains of 'Ad",
                "The pride of 'Ad boasting: 'Who is greater than us in strength?'"
            ),
            miracles = listOf(
                "Deliverance of Hud and believers while a devastating furious wind storm (Sarsar) wiped out 'Ad for 7 nights and 8 days"
            ),
            spiritualLessons = listOf(
                "Material grandeur, tall skyscrapers, and physical strength cannot shield against Allah's justice",
                "Trust in Allah (Tawakkul) removes fear of powerful oppressors"
            ),
            keyAyahReference = "Surah Hud (11:56)",
            keyAyahArabic = "إِنِّي تَوَكَّلْتُ عَلَى اللَّهِ رَبِّي وَرَبِّكُم ۚ مَّا مِن دَابَّةٍ إِلَّا هُوَ آخِذٌ بِنَاصِيَتِهَا",
            keyAyahTranslation = "Indeed, I have relied upon Allah, my Lord and your Lord. There is no creature but that He holds its forelock."
        ),
        ProphetStory(
            id = 5,
            nameEnglish = "Salih (AS)",
            nameArabic = "صالح",
            title = "Prophet to Thamud & Sign of the She-Camel",
            era = "Al-Hijr (Mada'in Salih)",
            quranicMentionsCount = 9,
            keySurahs = listOf("Al-A'raf", "Hud", "Ash-Shu'ara", "Ash-Shams"),
            summary = "Sent to the stone-carving craftsmen of Thamud. Granted the miraculous She-Camel as a divine sign, which the corrupt chieftains hamstrung in defiance.",
            majorTrials = listOf(
                "Conspiracy of nine corrupt oligarchs plotting his assassination",
                "The slaughtering of Allah's sacred She-Camel"
            ),
            miracles = listOf(
                "A living, pregnant She-Camel brought forth from a solid boulder",
                "The camel drinking all water on alternating days while producing abundant milk"
            ),
            spiritualLessons = listOf(
                "Sanctity of divine covenants; transgressing sacred boundaries brings ruin",
                "Silence in the face of corrupt ringleaders implicates the collective"
            ),
            keyAyahReference = "Surah Ash-Shams (91:13)",
            keyAyahArabic = "فَقَالَ لَهُمْ رَسُولُ اللَّهِ نَاقَةَ اللَّهِ وَسُقْيَاهَا",
            keyAyahTranslation = "And the messenger of Allah said to them, '[Do not harm] the she-camel of Allah or [prevent her from] her drink.'"
        ),
        ProphetStory(
            id = 6,
            nameEnglish = "Ibrahim (AS)",
            nameArabic = "إبراهيم",
            title = "Khalilullah - Friend of Allah & Patriarch of Monotheism",
            era = "Mesopotamia, Levant & Makkah",
            quranicMentionsCount = 69,
            keySurahs = listOf("Al-Baqarah", "Ibrahim", "Al-An'am", "As-Saffat"),
            summary = "Champion of pure monotheism (Tawhid). Smashed the idols of Babylon, survived the tyrant Nimrod's blazing fire, settled his family in barren Makkah, rebuilt the Holy Kaaba, and willingly prepared to sacrifice his son Ismail in supreme submission.",
            majorTrials = listOf(
                "Rejection and exile by his father Azar and his tribe",
                "Being hurled into a roaring inferno by catapult",
                "Leaving baby Ismail and Hajar in the waterless Makkan valley",
                "The supreme test of sacrificing his beloved son"
            ),
            miracles = listOf(
                "The blazing fire transformed into coolness and peace ('Kuni Bardan wa Salama')",
                "Revival of four birds after scattering them on hilltops",
                "The miraculous bubbling spring of Zamzam for his family"
            ),
            spiritualLessons = listOf(
                "True Islam is complete surrender of the heart, ego, and desires to Allah",
                "Single-handed courage for truth can make one person an entire nation (Ummah)",
                "The foundations of Hajj embody Abrahamic sacrifice and sincerity"
            ),
            keyAyahReference = "Surah An-Nahl (16:120)",
            keyAyahArabic = "إِنَّ إِبْرَاهِيمَ كَانَ أُمَّةً قَانِتًا لِّلَّهِ حَنِيفًا وَلَمْ يَكُ مِنَ الْمُشْرِكِينَ",
            keyAyahTranslation = "Indeed, Abraham was a comprehensive leader, devoutly obedient to Allah, inclining toward truth, and he was not of those who associate others with Allah."
        ),
        ProphetStory(
            id = 7,
            nameEnglish = "Ismail (AS)",
            nameArabic = "إسماعيل",
            title = "The Forbearing Son & Patriarch of the Arabs",
            era = "Makkah",
            quranicMentionsCount = 12,
            keySurahs = listOf("As-Saffat", "Al-Baqarah", "Maryam"),
            summary = "Firstborn son of Ibrahim, who demonstrated heroic obedience when informed of the divine sacrificial command, helped his father build the Kaaba, and ancestor of Prophet Muhammad (ﷺ).",
            majorTrials = listOf(
                "Infancy in the desolate desert before Zamzam sprang",
                "Readiness to surrender his life upon Allah's vision"
            ),
            miracles = listOf(
                "The emergence of the perennial Zamzam well beneath his infant feet",
                "The divine ransom of a magnificent ram from Paradise"
            ),
            spiritualLessons = listOf(
                "Filial obedience and mutual consultation in worship and sacrifice",
                "Steadfastness in prayer and commanding one's household to prayer"
            ),
            keyAyahReference = "Surah As-Saffat (37:102)",
            keyAyahArabic = "قَالَ يَا أَبَتِ افْعَلْ مَا تُؤْمَرُ ۖ سَتَجِدُنِي إِن شَاءَ اللَّهُ مِنَ الصَّابِرِينَ",
            keyAyahTranslation = "He said, 'O my father, do as you are commanded. You will find me, if Allah wills, of the steadfast.'"
        ),
        ProphetStory(
            id = 8,
            nameEnglish = "Yusuf (AS)",
            nameArabic = "يوسف",
            title = "The Truthful & Paragon of Chastity and Forgiveness",
            era = "Canaan & Ancient Egypt",
            quranicMentionsCount = 27,
            keySurahs = listOf("Yusuf"),
            summary = "Endowed with half of human beauty and dream interpretation. Thrown into a dry well by jealous brothers, sold into slavery in Egypt, falsely imprisoned for chastity, and eventually rose to Grand Vizier who saved millions from famine and forgave his brothers.",
            majorTrials = listOf(
                "Betrayal and abandonment by his ten older brothers",
                "Seduction attempt by the Aziz's wife (Zulaikha) and women of the city",
                "Years of unjust dungeon imprisonment in Egypt"
            ),
            miracles = listOf(
                "God-given mastery of the interpretation of visions and dreams (Ta'wil al-Ahadith)",
                "Restoration of his father Yaqub's blindness with his shirt"
            ),
            spiritualLessons = listOf(
                "Patience in adversity (Sabr Jameel) leads inevitably to divine elevation",
                "Chastity in secret is among the greatest spiritual jewels",
                "Magnanimous forgiveness heals long-standing family fractures"
            ),
            keyAyahReference = "Surah Yusuf (12:90)",
            keyAyahArabic = "إِنَّهُ مَن يَتَّقِ وَيَصْبِرْ فَإِنَّ اللَّهَ لَا يُضِيعُ أَجْرَ الْمُحْسِنِينَ",
            keyAyahTranslation = "Indeed, he who fears Allah and is patient, then indeed, Allah does not allow the reward of those who do good to be lost."
        ),
        ProphetStory(
            id = 9,
            nameEnglish = "Musa (AS)",
            nameArabic = "موسى",
            title = "Kalimullah - The One Spoken to by Allah",
            era = "Ancient Egypt & Sinai",
            quranicMentionsCount = 136,
            keySurahs = listOf("Al-Baqarah", "Al-Qasas", "Ta-Ha", "Al-A'raf"),
            summary = "The most frequently mentioned prophet in the Quran. Raised in Pharaoh's palace as an infant in a floating basket, called at Mount Tur, confronted Pharaoh with miracles, split the Red Sea, and received the Torah on Mount Sinai.",
            majorTrials = listOf(
                "Escaping the infant slaughter decree of tyrannical Pharaoh",
                "Fleeing to Madyan after the accidental killing of an Egyptian",
                "Standing directly before the megalomaniac Pharaoh",
                "Forty years wandering in the desert with the rebellious Children of Israel"
            ),
            miracles = listOf(
                "The wooden staff turning into a serpent devouring sorcerers' illusions",
                "The radiant shining hand without disease (Yad Bayda)",
                "The miraculous cleaving of the Red Sea into towering walls of water",
                "Water gushing from the rock in twelve springs for the twelve tribes"
            ),
            spiritualLessons = listOf(
                "No tyrant on earth is too powerful for Allah to dismantle",
                "Divine speech comforts the frightened believer before formidable trials",
                "Reverence for divine law and persistence in the face of communal stubbornness"
            ),
            keyAyahReference = "Surah Ash-Shu'ara (26:62)",
            keyAyahArabic = "قَالَ كَلَّا ۖ إِنَّ مَعِيَ رَبِّي سَيَهْدِينِ",
            keyAyahTranslation = "[Moses] said, 'No! Indeed, with me is my Lord; He will guide me.'"
        ),
        ProphetStory(
            id = 10,
            nameEnglish = "Dawud (AS)",
            nameArabic = "داود",
            title = "David - The Righteous King & Psalmist",
            era = "Kingdom of Israel",
            quranicMentionsCount = 16,
            keySurahs = listOf("Sad", "Al-Anbiya", "Saba"),
            summary = "Defeated Goliath (Jalut) as a youth. Blessed with an angelic melodic voice reciting the Zabur, the submission of mountains and birds in glorification, mastery over iron, and just governance.",
            majorTrials = listOf(
                "Facing the armored giant Goliath with only a sling",
                "The dispute of the two litigants over ninety-nine ewes reminding him of vigilance"
            ),
            miracles = listOf(
                "Iron rendered as pliable as wax in his bare hands without fire",
                "Mountains and birds echoing his morning and evening glorifications",
                "The divine Zabur (Psalms) revealed to his heart"
            ),
            spiritualLessons = listOf(
                "Spiritual devotion and political leadership can be harmoniously integrated",
                "Earning one's sustenance through honest craftsmanship is loved by Allah",
                "Fasting on alternate days (the Fast of Dawud) is the most beloved voluntary fast"
            ),
            keyAyahReference = "Surah Sad (38:17)",
            keyAyahArabic = "وَاذْكُرْ عَبْدَنَا دَاوُودَ ذَا الْأَيْدِ ۖ إِنَّهُ أَوَّابٌ",
            keyAyahTranslation = "And remember Our servant, David, the possessor of strength; indeed, he was one who repeatedly turned back [to Allah]."
        ),
        ProphetStory(
            id = 11,
            nameEnglish = "Sulayman (AS)",
            nameArabic = "سليمان",
            title = "Solomon - The Grateful Monarch of All Realms",
            era = "Kingdom of Jerusalem",
            quranicMentionsCount = 17,
            keySurahs = listOf("An-Naml", "Saba", "Sad"),
            summary = "Son of Dawud, bestowed with a kingdom unmatched in human history. Understood the speech of animals and birds, commanded the wind, had armies of Jinn and men, and converted the Queen of Sheba (Bilqis) to monotheism.",
            majorTrials = listOf(
                "Ensuring unprecedented imperial authority never caused a speck of arrogance",
                "The test of the horses that distracted his attention momentarily before sunset"
            ),
            miracles = listOf(
                "Understanding the language of ants, birds (Hudhud), and beasts",
                "Harnessing the wind to travel a month's journey in a single morning",
                "Subjugation of Jinn to build sanctified palaces, basins, and sanctuaries"
            ),
            spiritualLessons = listOf(
                "Enormous wealth and authority must be channeled into humble gratitude ('This is from the favor of my Lord')",
                "Attention to the smallest creatures of Allah (the ant in the valley) reflects godly mercy"
            ),
            keyAyahReference = "Surah An-Naml (27:40)",
            keyAyahArabic = "قَالَ هَٰذَا مِن فَضْلِ رَبِّي لِيَبْلُوَنِي أَأَشْكُرُ أَمْ أَكْفُرُ",
            keyAyahTranslation = "He said, 'This is from the favor of my Lord to test me whether I will be grateful or ungrateful.'"
        ),
        ProphetStory(
            id = 12,
            nameEnglish = "Isa (AS)",
            nameArabic = "عيسى",
            title = "Jesus - Ruhullah & Masih, Word of Allah",
            era = "1st Century CE, Judea & Galilee",
            quranicMentionsCount = 25,
            keySurahs = listOf("Ali 'Imran", "Maryam", "Al-Ma'idah", "An-Nisa"),
            summary = "Born miraculously of the virgin Maryam without a father. Spoke in the cradle to defend his mother's honor, healed the blind and leper, raised the dead by Allah's leave, brought the Injeel, and was raised alive to the heavens.",
            majorTrials = listOf(
                "Slander and accusations hurled at his pure mother Maryam",
                "Vehement plots by corrupt rabbis and Roman authorities to crucify him",
                "Corruptions attributing divinity to him against his monotheistic teachings"
            ),
            miracles = listOf(
                "Miraculous virgin birth without a biological father",
                "Speaking articulately as an infant in the cradle defending Maryam",
                "Fashioning birds from clay and breathing life into them by Allah's leave",
                "Healing the born-blind and the leper, and resurrecting the dead by Allah's leave",
                "Descent of the Heavenly Table of food (Al-Ma'idah)"
            ),
            spiritualLessons = listOf(
                "Jesus was a revered human prophet and messenger of Allah, not a divine partner",
                "Detachment from worldly extravagance (Zuhd) purifies spiritual sight",
                "Spiritual revival must accompany outward ritualism"
            ),
            keyAyahReference = "Surah Maryam (19:30)",
            keyAyahArabic = "قَالَ إِنِّي عَبْدُ اللَّهِ آتَانِيَ الْكِتَابَ وَجَعَلَنِي نَبِيًّا",
            keyAyahTranslation = "[Jesus] said, 'Indeed, I am the servant of Allah. He has given me the Scripture and made me a prophet.'"
        ),
        ProphetStory(
            id = 13,
            nameEnglish = "Muhammad (ﷺ)",
            nameArabic = "محمد",
            title = "Khatam an-Nabiyyin - Mercy to all the Worlds",
            era = "570 - 632 CE, Makkah & Madinah",
            quranicMentionsCount = 4,
            keySurahs = listOf("Muhammad", "Al-Fath", "Al-Ahzab", "Al-Anbiya", "An-Najm"),
            summary = "The final seal of all prophets and messengers. Born an orphan, known as Al-Amin (the trustworthy), received the Holy Quran at Cave Hira, endured 13 years of Makkan persecution, undertook the Night Journey (Isra & Mi'raj), established the fraternal society of Madinah, and unified Arabia under the light of Tawhid.",
            majorTrials = listOf(
                "Orphanhood in childhood losing parents and grandfather",
                "Three years of cruel siege and starvation in the Boycott of Shi'b Abi Talib",
                "Rejection, stoning, and bleeding at Ta'if followed by personal heartbreak",
                "Military confrontations at Badr, Uhud, and the Trench defending the community"
            ),
            miracles = listOf(
                "The Eternal Living Miracle: The Inimitable Holy Quran",
                "The Splitting of the Moon witnessed across continents",
                "The Night Journey (Al-Isra) and Ascension beyond the 7 Heavens (Al-Mi'raj)",
                "Water flowing like fountains between his blessed fingers quenching thousands",
                "The weeping palm tree trunk longing for his touch"
            ),
            spiritualLessons = listOf(
                "Sublime character, mercy, and compassion are the greatest arguments for faith",
                "Forgiving enemies upon triumph (the Conquest of Makkah) transforms the world",
                "Universal guidance valid for every era, civilization, and soul until the Last Day"
            ),
            keyAyahReference = "Surah Al-Anbiya (21:107)",
            keyAyahArabic = "وَمَا أَرْسَلْنَاكَ إِلَّا رَحْمَةً لِّلْعَالَمِينَ",
            keyAyahTranslation = "And We have not sent you, [O Muhammad], except as a mercy to all the worlds."
        )
    )
}
