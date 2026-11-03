package com.example.data.seed

import com.example.data.local.HadithEntity

data class HadithCollectionMeta(
    val id: String,
    val name: String,
    val arabicName: String,
    val totalHadith: String,
    val description: String
)

data class HadithChapterMeta(
    val bookNumber: Int,
    val chapterName: String,
    val hadithCount: Int
)

object HadithSeedData {

    val COLLECTIONS = listOf(
        HadithCollectionMeta("bukhari", "Sahih al-Bukhari", "صحيح البخاري", "7,563", "The most authentic book of Hadith compiled by Imam al-Bukhari"),
        HadithCollectionMeta("muslim", "Sahih Muslim", "صحيح مسلم", "7,500", "Second most authentic collection compiled by Imam Muslim ibn al-Hajjaj"),
        HadithCollectionMeta("nawawi", "40 Hadith Nawawi", "الأربعون النووية", "42", "The essential 42 hadiths summarizing the pillars and foundations of Islam"),
        HadithCollectionMeta("tirmidhi", "Jami` at-Tirmidhi", "جامع الترمذي", "3,956", "Renowned collection by Imam at-Tirmidhi with commentary on legal rulings"),
        HadithCollectionMeta("abudawud", "Sunan Abi Dawud", "سنن أبي داود", "5,274", "Fundamental collection focusing primarily on Islamic legal traditions"),
        HadithCollectionMeta("nasai", "Sunan an-Nasa'i", "سنن النسائي", "5,761", "Celebrated for its rigorous standards of chain authentication")
    )

    val BUKHARI_CHAPTERS = listOf(
        HadithChapterMeta(1, "Revelation (Bad' al-Wahy)", 7),
        HadithChapterMeta(2, "Belief (Kitab al-Iman)", 53),
        HadithChapterMeta(3, "Knowledge (Kitab al-'Ilm)", 77),
        HadithChapterMeta(8, "Prayers (Kitab al-Salah)", 140),
        HadithChapterMeta(24, "Obligatory Charity (Zakat)", 118),
        HadithChapterMeta(31, "Fasting (Kitab al-Sawm)", 117),
        HadithChapterMeta(78, "Good Manners and Form (Kitab al-Adab)", 180)
    )

    val MUSLIM_CHAPTERS = listOf(
        HadithChapterMeta(1, "The Book of Faith (Kitab al-Iman)", 92),
        HadithChapterMeta(4, "The Book of Prayers (Kitab as-Salah)", 175),
        HadithChapterMeta(13, "The Book of Fasting (Kitab as-Siyam)", 88),
        HadithChapterMeta(35, "The Book of Dhikr, Dua & Repentance", 102)
    )

    val NAWAWI_CHAPTERS = listOf(
        HadithChapterMeta(1, "The 42 Essential Hadiths", 42)
    )

    val TIRMIDHI_CHAPTERS = listOf(
        HadithChapterMeta(1, "The Book of Purification (Taharah)", 65),
        HadithChapterMeta(27, "The Book of Righteousness and Relations (Birr)", 90),
        HadithChapterMeta(48, "The Book of Supplications (Da'awat)", 120)
    )

    val ABUDAWUD_CHAPTERS = listOf(
        HadithChapterMeta(1, "Purification (Kitab al-Taharah)", 80),
        HadithChapterMeta(2, "Prayer (Kitab al-Salah)", 150),
        HadithChapterMeta(14, "Fasting (Kitab al-Sawm)", 70)
    )

    val NASAI_CHAPTERS = listOf(
        HadithChapterMeta(1, "The Book of Purification", 95),
        HadithChapterMeta(5, "The Book of Salah", 160),
        HadithChapterMeta(22, "The Book of Fasting", 85)
    )

    fun getFallbackHadiths(collection: String, bookNumber: Int): List<HadithEntity> {
        return when {
            collection == "nawawi" -> listOf(
                HadithEntity(
                    collection = "nawawi",
                    bookNumber = 1,
                    chapterName = "Actions by Intentions",
                    hadithNumber = 1,
                    arabicText = "إِنَّمَا الأَعْمَالُ بِالنِّيَّاتِ وَإِنَّمَا لِكُلِّ امْرِئٍ مَا نَوَى",
                    translation = "Actions are according to intentions, and everyone will get what was intended. So whoever emigrated for Allah and His Messenger, his emigration is for Allah and His Messenger.",
                    narrator = "Umar ibn al-Khattab (RA)",
                    grade = "Sahih (Bukhari & Muslim)"
                ),
                HadithEntity(
                    collection = "nawawi",
                    bookNumber = 1,
                    chapterName = "Islam, Iman, and Ihsan",
                    hadithNumber = 2,
                    arabicText = "أَنْ تَعْبُدَ اللَّهَ كَأَنَّكَ تَرَاهُ، فَإِنْ لَمْ تَكُنْ تَرَاهُ فَإِنَّهُ يَرَاكَ",
                    translation = "Ihsan is to worship Allah as though you see Him, and if you cannot see Him, then indeed He sees you.",
                    narrator = "Umar ibn al-Khattab (RA) - Hadith Jibril",
                    grade = "Sahih (Muslim)"
                ),
                HadithEntity(
                    collection = "nawawi",
                    bookNumber = 1,
                    chapterName = "Leaving What Does Not Concern You",
                    hadithNumber = 12,
                    arabicText = "مِنْ حُسْنِ إِسْلاَمِ الْمَرْءِ تَرْكُهُ مَا لاَ يَعْنِيهِ",
                    translation = "Part of the excellence of one's Islam is that he leaves that which does not concern him.",
                    narrator = "Abu Hurairah (RA)",
                    grade = "Hasan (Tirmidhi)"
                ),
                HadithEntity(
                    collection = "nawawi",
                    bookNumber = 1,
                    chapterName = "Loving for Your Brother",
                    hadithNumber = 13,
                    arabicText = "لاَ يُؤْمِنُ أَحَدُكُمْ حَتَّى يُحِبَّ لأَخِيهِ مَا يُحِبُّ لِنَفْسِهِ",
                    translation = "None of you truly believes until he loves for his brother what he loves for himself.",
                    narrator = "Anas ibn Malik (RA)",
                    grade = "Sahih (Bukhari & Muslim)"
                ),
                HadithEntity(
                    collection = "nawawi",
                    bookNumber = 1,
                    chapterName = "Good Speech or Silence",
                    hadithNumber = 15,
                    arabicText = "مَنْ كَانَ يُؤْمِنُ بِاللَّهِ وَالْيَوْمِ الآخِرِ فَلْيَقُلْ خَيْرًا أَوْ لِيَصْمُتْ",
                    translation = "Whoever believes in Allah and the Last Day, let him speak good or remain silent. And whoever believes in Allah and the Last Day, let him honor his neighbor.",
                    narrator = "Abu Hurairah (RA)",
                    grade = "Sahih (Bukhari & Muslim)"
                ),
                HadithEntity(
                    collection = "nawawi",
                    bookNumber = 1,
                    chapterName = "Do Not Become Angry",
                    hadithNumber = 16,
                    arabicText = "لاَ تَغْضَبْ، فَرَدَّدَ مِرَارًا، قَالَ: لاَ تَغْضَبْ",
                    translation = "A man said to the Prophet (pbuh): 'Advise me.' The Prophet said: 'Do not become angry.' The man repeated his request several times, and each time the Prophet said: 'Do not become angry.'",
                    narrator = "Abu Hurairah (RA)",
                    grade = "Sahih al-Bukhari"
                ),
                HadithEntity(
                    collection = "nawawi",
                    bookNumber = 1,
                    chapterName = "Excellence in Everything",
                    hadithNumber = 17,
                    arabicText = "إِنَّ اللَّهَ كَتَبَ الإِحْسَانَ عَلَى كُلِّ شَيْءٍ",
                    translation = "Verily Allah has prescribed Ihsan (proficiency, kindness, excellence) in all things.",
                    narrator = "Shaddad ibn Aws (RA)",
                    grade = "Sahih Muslim"
                )
            )

            collection == "bukhari" && bookNumber == 1 -> listOf(
                HadithEntity(
                    collection = "bukhari",
                    bookNumber = 1,
                    chapterName = "Revelation",
                    hadithNumber = 1,
                    arabicText = "سَمِعْتُ عُمَرَ بْنَ الْخَطَّابِ رَضِيَ اللَّهُ عَنْهُ عَلَى الْمِنْبَرِ قَالَ: سَمِعْتُ رَسُولَ اللَّهِ ﷺ يَقُولُ: إِنَّمَا الأَعْمَالُ بِالنِّيَّاتِ",
                    translation = "I heard Allah's Messenger (pbuh) saying: 'The reward of deeds depends upon the intentions and every person will get the reward according to what he has intended.'",
                    narrator = "Umar bin Al-Khattab (RA)",
                    grade = "Sahih al-Bukhari"
                ),
                HadithEntity(
                    collection = "bukhari",
                    bookNumber = 1,
                    chapterName = "Revelation",
                    hadithNumber = 2,
                    arabicText = "أَنَّ عَائِشَةَ أُمَّ الْمُؤْمِنِينَ رَضِيَ اللَّهُ عَنْهَا أَخْبَرَتْهُ أَنَّ الْحَارِثَ بْنَ هِشَامٍ رَضِيَ اللَّهُ عَنْهُ سَأَلَ رَسُولَ اللَّهِ ﷺ: يَا رَسُولَ اللَّهِ، كَيْفَ يَأْتِيكَ الْوَحْيُ؟",
                    translation = "Al-Harith bin Hisham asked Allah's Messenger (pbuh): 'O Allah's Messenger! How is the Divine Inspiration revealed to you?' Allah's Messenger replied: 'Sometimes it is (revealed) like the ringing of a bell and that is the hardest of all for me...'",
                    narrator = "Aisha (RA)",
                    grade = "Sahih al-Bukhari"
                ),
                HadithEntity(
                    collection = "bukhari",
                    bookNumber = 1,
                    chapterName = "Revelation",
                    hadithNumber = 3,
                    arabicText = "اقْرَأْ بِاسْمِ رَبِّكَ الَّذِي خَلَقَ، خَلَقَ الإِنْسَانَ مِنْ عَلَقٍ، اقْرَأْ وَرَبُّكَ الأَكْرَمُ",
                    translation = "The angel said: 'Recite in the name of your Lord who created. Created man from a clinging substance. Recite, and your Lord is the most Generous.'",
                    narrator = "Aisha (RA)",
                    grade = "Sahih al-Bukhari"
                )
            )

            collection == "bukhari" && bookNumber == 2 -> listOf(
                HadithEntity(
                    collection = "bukhari",
                    bookNumber = 2,
                    chapterName = "Belief (Iman)",
                    hadithNumber = 8,
                    arabicText = "بُنِيَ الإِسْلاَمُ عَلَى خَمْسٍ: شَهَادَةِ أَنْ لاَ إِلَهَ إِلاَّ اللَّهُ وَأَنَّ مُحَمَّدًا رَسُولُ اللَّهِ، وَإِقَامِ الصَّلاَةِ، وَإِيتَاءِ الزَّكَاةِ، وَالْحَجِّ، وَصَوْمِ رَمَضَانَ",
                    translation = "Islam is based on five principles: To testify that none has the right to be worshipped but Allah and Muhammad is Allah's Messenger; to offer the prayers dutifully and perfectly; to pay Zakat; to perform Hajj; and to observe fast during the month of Ramadan.",
                    narrator = "Ibn Umar (RA)",
                    grade = "Sahih al-Bukhari"
                ),
                HadithEntity(
                    collection = "bukhari",
                    bookNumber = 2,
                    chapterName = "Belief (Iman)",
                    hadithNumber = 9,
                    arabicText = "الْمُسْلِمُ مَنْ سَلِمَ الْمُسْلِمُونَ مِنْ لِسَانِهِ وَيَدِهِ",
                    translation = "A true Muslim is the one from whose tongue and hands the Muslims are safe.",
                    narrator = "Abdullah bin Amr (RA)",
                    grade = "Sahih al-Bukhari"
                )
            )

            collection == "bukhari" && bookNumber == 31 -> listOf(
                HadithEntity(
                    collection = "bukhari",
                    bookNumber = 31,
                    chapterName = "Fasting (Sawm)",
                    hadithNumber = 1894,
                    arabicText = "قَالَ رَسُولُ اللَّهِ ﷺ: قَالَ اللَّهُ: كُلُّ عَمَلِ ابْنِ آدَمَ لَهُ إِلاَّ الصِّيَامَ فَإِنَّهُ لِي وَأَنَا أَجْزِي بِهِ",
                    translation = "Allah's Messenger (pbuh) said: 'Allah said: All the deeds of the son of Adam are for him, except fasting, for it is for Me and I will give reward for it.'",
                    narrator = "Abu Hurairah (RA)",
                    grade = "Sahih al-Bukhari"
                ),
                HadithEntity(
                    collection = "bukhari",
                    bookNumber = 31,
                    chapterName = "Fasting (Sawm)",
                    hadithNumber = 1923,
                    arabicText = "تَسَحَّرُوا فَإِنَّ فِي السَّحُورِ بَرَكَةً",
                    translation = "The Prophet (pbuh) said: 'Take Suhoor (meal before dawn), for there is blessing in Suhoor.'",
                    narrator = "Anas bin Malik (RA)",
                    grade = "Sahih al-Bukhari"
                )
            )

            collection == "muslim" -> listOf(
                HadithEntity(
                    collection = "muslim",
                    bookNumber = bookNumber,
                    chapterName = "Book of Faith & Devotion",
                    hadithNumber = 1,
                    arabicText = "الدِّينُ النَّصِيحَةُ، قُلْنَا: لِمَنْ؟ قَالَ: لِلَّهِ وَلِكِتَابِهِ وَلِرَسُولِهِ وَلأَئِمَّةِ الْمُسْلِمِينَ وَعَامَّتِهِمْ",
                    translation = "The Prophet (pbuh) said: 'Religion is sincerity and good counsel.' We asked: 'To whom?' He replied: 'To Allah, His Book, His Messenger, the leaders of the Muslims, and their common folk.'",
                    narrator = "Tamim ad-Dari (RA)",
                    grade = "Sahih Muslim"
                ),
                HadithEntity(
                    collection = "muslim",
                    bookNumber = bookNumber,
                    chapterName = "Dhikr & Repentance",
                    hadithNumber = 2699,
                    arabicText = "مَنْ سَلَكَ طَرِيقًا يَلْتَمِسُ فِيهِ عِلْمًا، سَهَّلَ اللَّهُ لَهُ بِهِ طَرِيقًا إِلَى الْجَنَّةِ",
                    translation = "Whoever treads a path seeking knowledge, Allah will make easy for him by it a path to Paradise.",
                    narrator = "Abu Hurairah (RA)",
                    grade = "Sahih Muslim"
                )
            )

            collection == "tirmidhi" -> listOf(
                HadithEntity(
                    collection = "tirmidhi",
                    bookNumber = bookNumber,
                    chapterName = "Supplications & Character",
                    hadithNumber = 1987,
                    arabicText = "أَكْمَلُ الْمُؤْمِنِينَ إِيمَانًا أَحْسَنُهُمْ خُلُقًا، وَخِيَارُكُمْ خِيَارُكُمْ لِنِسَائِهِمْ",
                    translation = "The most complete of believers in faith are those with the best character, and the best of you are the best to their families.",
                    narrator = "Abu Hurairah (RA)",
                    grade = "Hasan Sahih (Tirmidhi)"
                ),
                HadithEntity(
                    collection = "tirmidhi",
                    bookNumber = bookNumber,
                    chapterName = "Supplications & Character",
                    hadithNumber = 3373,
                    arabicText = "الدُّعَاءُ هُوَ الْعِبَادَةُ",
                    translation = "The Prophet (pbuh) said: 'Supplication (Dua) is the very essence of worship.'",
                    narrator = "An-Nu'man ibn Bashir (RA)",
                    grade = "Sahih (Tirmidhi)"
                )
            )

            collection == "abudawud" -> listOf(
                HadithEntity(
                    collection = "abudawud",
                    bookNumber = bookNumber,
                    chapterName = "Legal Traditions",
                    hadithNumber = 1522,
                    arabicText = "إِنَّ اللَّهَ حَيِيٌّ كَرِيمٌ يَسْتَحْيِي إِذَا رَفَعَ الرَّجُلُ إِلَيْهِ يَدَيْهِ أَنْ يَرُدَّهُمَا صِفْرًا خَائِبَتَيْنِ",
                    translation = "Indeed, your Lord is Generous and Shy. If His servant raises his hands to Him in supplication, He is shy to return them empty and disappointed.",
                    narrator = "Salman al-Farsi (RA)",
                    grade = "Sahih (Abu Dawud)"
                )
            )

            collection == "nasai" -> listOf(
                HadithEntity(
                    collection = "nasai",
                    bookNumber = bookNumber,
                    chapterName = "Prayer & Devotion",
                    hadithNumber = 1300,
                    arabicText = "أَقْرَبُ مَا يَكُونُ الْعَبْدُ مِنْ رَبِّهِ وَهُوَ سَاجِدٌ فَأَكْثِرُوا الدُّعَاءَ",
                    translation = "The closest that a servant comes to his Lord is when he is prostrating in Sujud, so supplicate abundantly therein.",
                    narrator = "Abu Hurairah (RA)",
                    grade = "Sahih (an-Nasa'i & Muslim)"
                )
            )

            else -> listOf(
                HadithEntity(
                    collection = collection,
                    bookNumber = bookNumber,
                    chapterName = "Chapter $bookNumber",
                    hadithNumber = 1,
                    arabicText = "خَيْرُكُمْ مَنْ تَعَلَّمَ الْقُرْآنَ وَعَلَّمَهُ",
                    translation = "The best among you are those who learn the Quran and teach it to others.",
                    narrator = "Uthman bin Affan (RA)",
                    grade = "Sahih"
                )
            )
        }
    }

    val CATEGORIES = listOf(
        "All",
        "Faith & Sincerity",
        "Prayer & Purification",
        "Fasting & Ramadan",
        "Zakat & Charity",
        "Good Character & Manners",
        "Supplications & Dhikr",
        "Knowledge & Wisdom",
        "Repentance & Mercy",
        "Family & Compassion"
    )

    val PROMINENT_NARRATORS = listOf(
        "All",
        "Abu Hurairah",
        "Umar bin Al-Khattab",
        "Aisha",
        "Anas bin Malik",
        "Abdullah ibn Umar",
        "Ibn Abbas",
        "Ali ibn Abi Talib",
        "Abdullah ibn Mas'ud",
        "Uthman bin Affan",
        "Abu Bakr as-Siddiq",
        "Jabir ibn Abdullah"
    )

    val ALL_HADITHS: List<HadithEntity> = listOf(
        // Bukhari
        HadithEntity(
            collection = "bukhari",
            bookNumber = 1,
            chapterName = "Faith & Sincerity",
            hadithNumber = 1,
            arabicText = "إِنَّمَا الأَعْمَالُ بِالنِّيَّاتِ، وَإِنَّمَا لِكُلِّ امْرِئٍ مَا نَوَى",
            translation = "The reward of deeds depends upon the intentions and every person will get the reward according to what he has intended.",
            narrator = "Umar bin Al-Khattab (RA)",
            grade = "Sahih al-Bukhari"
        ),
        HadithEntity(
            collection = "bukhari",
            bookNumber = 1,
            chapterName = "Knowledge & Wisdom",
            hadithNumber = 2,
            arabicText = "سَأَلَ رَسُولَ اللَّهِ ﷺ: يَا رَسُولَ اللَّهِ، كَيْفَ يَأْتِيكَ الْوَحْيُ؟ فَقَالَ: أَحْيَانًا يَأْتِينِي مِثْلَ صَلْصَلَةِ الْجَرَسِ",
            translation = "Al-Harith bin Hisham asked Allah's Messenger (pbuh): 'O Allah's Messenger! How is the Divine Inspiration revealed to you?' He replied: 'Sometimes it comes like the ringing of a bell and that is the hardest of all for me.'",
            narrator = "Aisha (RA)",
            grade = "Sahih al-Bukhari"
        ),
        HadithEntity(
            collection = "bukhari",
            bookNumber = 2,
            chapterName = "Faith & Sincerity",
            hadithNumber = 8,
            arabicText = "بُنِيَ الإِسْلاَمُ عَلَى خَمْسٍ: شَهَادَةِ أَنْ لاَ إِلَهَ إِلاَّ اللَّهُ وَأَنَّ مُحَمَّدًا رَسُولُ اللَّهِ، وَإِقَامِ الصَّلاَةِ، وَإِيتَاءِ الزَّكَاةِ، وَالْحَجِّ، وَصَوْمِ رَمَضَانَ",
            translation = "Islam is based on five principles: To testify that none has the right to be worshipped but Allah and Muhammad is Allah's Messenger; to establish prayer; to pay Zakat; to perform Hajj; and to observe fast during Ramadan.",
            narrator = "Abdullah ibn Umar (RA)",
            grade = "Sahih al-Bukhari"
        ),
        HadithEntity(
            collection = "bukhari",
            bookNumber = 2,
            chapterName = "Good Character & Manners",
            hadithNumber = 9,
            arabicText = "الْمُسْلِمُ مَنْ سَلِمَ الْمُسْلِمُونَ مِنْ لِسَانِهِ وَيَدِهِ",
            translation = "A true Muslim is the one from whose tongue and hands other Muslims are safe.",
            narrator = "Abdullah bin Amr (RA)",
            grade = "Sahih al-Bukhari"
        ),
        HadithEntity(
            collection = "bukhari",
            bookNumber = 3,
            chapterName = "Knowledge & Wisdom",
            hadithNumber = 68,
            arabicText = "يَسِّرُوا وَلاَ تُعَسِّرُوا، وَبَشِّرُوا وَلاَ تُنَفِّرُوا",
            translation = "Make things easy for people and do not make things difficult; give glad tidings and do not drive people away.",
            narrator = "Anas bin Malik (RA)",
            grade = "Sahih al-Bukhari"
        ),
        HadithEntity(
            collection = "bukhari",
            bookNumber = 8,
            chapterName = "Prayer & Purification",
            hadithNumber = 505,
            arabicText = "أَرَأَيْتُمْ لَوْ أَنَّ نَهَرًا بِبَابِ أَحَدِكُمْ يَغْتَسِلُ فِيهِ كُلَّ يَوْمٍ خَمْسًا، مَا تَقُولُونَ ذَلِكَ يُبْقِي مِنْ دَرَنِهِ؟ قَالُوا لاَ يُبْقِي مِنْ دَرَنِهِ شَيْئًا. قَالَ فَذَلِكَ مِثْلُ الصَّلَوَاتِ الْخَمْسِ",
            translation = "If there was a river at the door of one of you in which he bathed five times a day, would any dirt remain on him? They said: 'None.' The Prophet said: 'That is the example of the five daily prayers, by which Allah wipes away sins.'",
            narrator = "Abu Hurairah (RA)",
            grade = "Sahih al-Bukhari"
        ),
        HadithEntity(
            collection = "bukhari",
            bookNumber = 24,
            chapterName = "Zakat & Charity",
            hadithNumber = 1421,
            arabicText = "كُلُّ سُلاَمَى مِنَ النَّاسِ عَلَيْهِ صَدَقَةٌ، كُلَّ يَوْمٍ تَطْلُعُ فِيهِ الشَّمْسُ تَعْدِلُ بَيْنَ الاثْنَيْنِ صَدَقَةٌ، وَتُعِينُ الرَّجُلَ فِي دَابَّتِهِ صَدَقَةٌ",
            translation = "Every joint of a person must perform a charity each day the sun rises: to judge justly between two people is charity, to help a man with his mount is charity, a good word is charity, every step towards prayer is charity, and removing an obstacle from the path is charity.",
            narrator = "Abu Hurairah (RA)",
            grade = "Sahih al-Bukhari"
        ),
        HadithEntity(
            collection = "bukhari",
            bookNumber = 31,
            chapterName = "Fasting & Ramadan",
            hadithNumber = 1894,
            arabicText = "قَالَ اللَّهُ: كُلُّ عَمَلِ ابْنِ آدَمَ لَهُ إِلاَّ الصِّيَامَ فَإِنَّهُ لِي وَأَنَا أَجْزِي بِهِ، وَالصِّيَامُ جُنَّةٌ",
            translation = "Allah said: 'All the deeds of the son of Adam are for him, except fasting; for it is for Me and I will give reward for it.' Fasting is a protective shield.",
            narrator = "Abu Hurairah (RA)",
            grade = "Sahih al-Bukhari"
        ),
        HadithEntity(
            collection = "bukhari",
            bookNumber = 31,
            chapterName = "Fasting & Ramadan",
            hadithNumber = 1901,
            arabicText = "مَنْ صَامَ رَمَضَانَ إِيمَانًا وَاحْتِسَابًا غُفِرَ لَهُ مَا تَقَدَّمَ مِنْ ذَنْبِهِ",
            translation = "Whoever observes fasts during the month of Ramadan out of sincere faith and hoping for Allah's reward, all his previous sins will be forgiven.",
            narrator = "Abu Hurairah (RA)",
            grade = "Sahih al-Bukhari"
        ),
        HadithEntity(
            collection = "bukhari",
            bookNumber = 78,
            chapterName = "Good Character & Manners",
            hadithNumber = 6011,
            arabicText = "خَيْرُكُمْ إِسْلاَمًا أَحَاسِنُكُمْ أَخْلاَقًا",
            translation = "The best among you in Islam are those with the most excellent character and manners.",
            narrator = "Abdullah ibn Amr (RA)",
            grade = "Sahih al-Bukhari"
        ),
        HadithEntity(
            collection = "bukhari",
            bookNumber = 78,
            chapterName = "Family & Compassion",
            hadithNumber = 5971,
            arabicText = "جَاءَ رَجُلٌ إِلَى رَسُولِ اللَّهِ ﷺ فَقَالَ: يَا رَسُولَ اللَّهِ، مَنْ أَحَقُّ النَّاسِ بِحُسْنِ صَحَابَتِي؟ قَالَ: أُمُّكَ، قَالَ: ثُمَّ مَنْ؟ قَالَ: أُمُّكَ، قَالَ: ثُمَّ مَنْ؟ قَالَ: أُمُّكَ، قَالَ: ثُمَّ مَنْ؟ قَالَ: ثُمَّ أَبُوكَ",
            translation = "A man asked the Messenger of Allah: 'Who among the people is most deserving of my fine companionship?' He replied: 'Your mother.' The man asked: 'Then who?' He said: 'Your mother.' The man asked: 'Then who?' He said: 'Your mother.' The man asked: 'Then who?' He said: 'Then your father.'",
            narrator = "Abu Hurairah (RA)",
            grade = "Sahih al-Bukhari"
        ),

        // Muslim
        HadithEntity(
            collection = "muslim",
            bookNumber = 1,
            chapterName = "Faith & Sincerity",
            hadithNumber = 55,
            arabicText = "الدِّينُ النَّصِيحَةُ، قُلْنَا: لِمَنْ؟ قَالَ: لِلَّهِ وَلِكِتَابِهِ وَلِرَسُولِهِ وَلأَئِمَّةِ الْمُسْلِمِينَ وَعَامَّتِهِمْ",
            translation = "The Prophet (pbuh) said: 'Religion is sincerity.' We asked: 'To whom?' He replied: 'To Allah, His Book, His Messenger, and to the leaders of the Muslims and their common folk.'",
            narrator = "Tamim ad-Dari (RA)",
            grade = "Sahih Muslim"
        ),
        HadithEntity(
            collection = "muslim",
            bookNumber = 2,
            chapterName = "Prayer & Purification",
            hadithNumber = 223,
            arabicText = "الطُّهُورُ شَطْرُ الإِيمَانِ، وَالْحَمْدُ لِلَّهِ تَمْلأُ الْمِيزَانَ، وَسُبْحَانَ اللَّهِ وَالْحَمْدُ لِلَّهِ تَمْلآنِ مَا بَيْنَ السَّمَاوَاتِ وَالأَرْضِ",
            translation = "Purity is half of faith. 'Alhamdulillah' (Praise be to Allah) fills the scale. 'SubhanAllah' and 'Alhamdulillah' fill up the space between the heavens and the earth. Prayer is light, charity is proof, and patience is illumination.",
            narrator = "Abu Malik al-Ash'ari (RA)",
            grade = "Sahih Muslim"
        ),
        HadithEntity(
            collection = "muslim",
            bookNumber = 35,
            chapterName = "Knowledge & Wisdom",
            hadithNumber = 2699,
            arabicText = "مَنْ سَلَكَ طَرِيقًا يَلْتَمِسُ فِيهِ عِلْمًا، سَهَّلَ اللَّهُ لَهُ بِهِ طَرِيقًا إِلَى الْجَنَّةِ",
            translation = "Whoever travels a path in search of knowledge, Allah will make easy for him by it a path to Paradise.",
            narrator = "Abu Hurairah (RA)",
            grade = "Sahih Muslim"
        ),
        HadithEntity(
            collection = "muslim",
            bookNumber = 35,
            chapterName = "Supplications & Dhikr",
            hadithNumber = 2704,
            arabicText = "مَنْ صَلَّى عَلَيَّ وَاحِدَةً، صَلَّى اللَّهُ عَلَيْهِ بِهَا عَشْرًا",
            translation = "Whoever sends blessings upon me once, Allah will send blessings upon him tenfold.",
            narrator = "Abu Hurairah (RA)",
            grade = "Sahih Muslim"
        ),
        HadithEntity(
            collection = "muslim",
            bookNumber = 35,
            chapterName = "Repentance & Mercy",
            hadithNumber = 2749,
            arabicText = "إِنَّ اللَّهَ تَعَالَى يَبْسُطُ يَدَهُ بِاللَّيْلِ لِيَتُوبَ مُسِيءُ النَّهَارِ، وَيَبْسُطُ يَدَهُ بِالنَّهَارِ لِيَتُوبَ مُسِيءُ اللَّيْلِ، حَتَّى تَطْلُعَ الشَّمْسُ مِنْ مَغْرِبِهَا",
            translation = "Allah the Almighty stretches out His Hand at night so that those who sinned during the day may repent, and stretches out His Hand during the day so that those who sinned at night may repent, until the sun rises from the west.",
            narrator = "Abu Musa al-Ash'ari (RA)",
            grade = "Sahih Muslim"
        ),
        HadithEntity(
            collection = "muslim",
            bookNumber = 45,
            chapterName = "Good Character & Manners",
            hadithNumber = 2588,
            arabicText = "إِنَّ الرِّفْقَ لاَ يَكُونُ فِي شَيْءٍ إِلاَّ زَانَهُ، وَلاَ يُنْزَعُ مِنْ شَيْءٍ إِلاَّ شَانَهُ",
            translation = "Gentleness is not present in anything except that it beautifies it, and is not removed from anything except that it tarnishes it.",
            narrator = "Aisha (RA)",
            grade = "Sahih Muslim"
        ),

        // Nawawi 40
        HadithEntity(
            collection = "nawawi",
            bookNumber = 1,
            chapterName = "Faith & Sincerity",
            hadithNumber = 2,
            arabicText = "أَنْ تَعْبُدَ اللَّهَ كَأَنَّكَ تَرَاهُ، فَإِنْ لَمْ تَكُنْ تَرَاهُ فَإِنَّهُ يَرَاكَ",
            translation = "Ihsan is that you worship Allah as if you see Him; and if you cannot see Him, then surely He sees you.",
            narrator = "Umar bin Al-Khattab (RA)",
            grade = "Sahih (Muslim)"
        ),
        HadithEntity(
            collection = "nawawi",
            bookNumber = 1,
            chapterName = "Good Character & Manners",
            hadithNumber = 12,
            arabicText = "مِنْ حُسْنِ إِسْلاَمِ الْمَرْءِ تَرْكُهُ مَا لاَ يَعْنِيهِ",
            translation = "Part of the perfection of a person's Islam is his leaving that which does not concern him.",
            narrator = "Abu Hurairah (RA)",
            grade = "Hasan (Tirmidhi)"
        ),
        HadithEntity(
            collection = "nawawi",
            bookNumber = 1,
            chapterName = "Good Character & Manners",
            hadithNumber = 13,
            arabicText = "لاَ يُؤْمِنُ أَحَدُكُمْ حَتَّى يُحِبَّ لأَخِيهِ مَا يُحِبُّ لِنَفْسِهِ",
            translation = "None of you truly believes until he loves for his brother what he loves for himself.",
            narrator = "Anas bin Malik (RA)",
            grade = "Sahih (Bukhari & Muslim)"
        ),
        HadithEntity(
            collection = "nawawi",
            bookNumber = 1,
            chapterName = "Good Character & Manners",
            hadithNumber = 15,
            arabicText = "مَنْ كَانَ يُؤْمِنُ بِاللَّهِ وَالْيَوْمِ الآخِرِ فَلْيَقُلْ خَيْرًا أَوْ لِيَصْمُتْ",
            translation = "Whoever believes in Allah and the Last Day should speak good or remain silent.",
            narrator = "Abu Hurairah (RA)",
            grade = "Sahih (Bukhari & Muslim)"
        ),
        HadithEntity(
            collection = "nawawi",
            bookNumber = 1,
            chapterName = "Good Character & Manners",
            hadithNumber = 16,
            arabicText = "لاَ تَغْضَبْ، فَرَدَّدَ مِرَارًا، قَالَ: لاَ تَغْضَبْ",
            translation = "A man asked the Prophet (pbuh): 'Advise me.' He said: 'Do not become angry.' The man asked repeatedly and the Prophet replied: 'Do not become angry.'",
            narrator = "Abu Hurairah (RA)",
            grade = "Sahih al-Bukhari"
        ),
        HadithEntity(
            collection = "nawawi",
            bookNumber = 1,
            chapterName = "Knowledge & Wisdom",
            hadithNumber = 19,
            arabicText = "احْفَظِ اللَّهَ يَحْفَظْكَ، احْفَظِ اللَّهَ تَجِدْهُ تُجَاهَكَ، إِذَا سَأَلْتَ فَاسْأَلِ اللَّهَ، وَإِذَا اسْتَعَنْتَ فَاسْتَعِنْ بِاللَّهِ",
            translation = "Be mindful of Allah and Allah will protect you. Be mindful of Allah and you will find Him in front of you. When you ask, ask Allah, and when you seek assistance, seek assistance from Allah.",
            narrator = "Ibn Abbas (RA)",
            grade = "Sahih (Tirmidhi)"
        ),

        // Jami` at-Tirmidhi
        HadithEntity(
            collection = "tirmidhi",
            bookNumber = 27,
            chapterName = "Good Character & Manners",
            hadithNumber = 1956,
            arabicText = "تَبَسُّمُكَ فِي وَجْهِ أَخِيكَ لَكَ صَدَقَةٌ",
            translation = "Your smiling in the face of your brother is charity for you.",
            narrator = "Abu Dharr al-Ghifari (RA)",
            grade = "Hasan (Tirmidhi)"
        ),
        HadithEntity(
            collection = "tirmidhi",
            bookNumber = 27,
            chapterName = "Family & Compassion",
            hadithNumber = 1987,
            arabicText = "أَكْمَلُ الْمُؤْمِنِينَ إِيمَانًا أَحْسَنُهُمْ خُلُقًا، وَخِيَارُكُمْ خِيَارُكُمْ لِنِسَائِهِمْ",
            translation = "The most complete believers in faith are those who have the best character, and the best of you are those who are best to their women and families.",
            narrator = "Abu Hurairah (RA)",
            grade = "Hasan Sahih (Tirmidhi)"
        ),
        HadithEntity(
            collection = "tirmidhi",
            bookNumber = 48,
            chapterName = "Supplications & Dhikr",
            hadithNumber = 3373,
            arabicText = "الدُّعَاءُ هُوَ الْعِبَادَةُ",
            translation = "The Prophet (pbuh) said: 'Supplication (Dua) is the very essence of worship.'",
            narrator = "An-Nu'man ibn Bashir (RA)",
            grade = "Sahih (Tirmidhi)"
        ),
        HadithEntity(
            collection = "tirmidhi",
            bookNumber = 48,
            chapterName = "Knowledge & Wisdom",
            hadithNumber = 3540,
            arabicText = "اتَّقِ اللَّهِ حَيْثُمَا كُنْتَ، وَأَتْبِعِ السَّيِّئَةَ الْحَسَنَةَ تَمْحُهَا، وَخَالِقِ النَّاسَ بِخُلُقٍ حَسَنٍ",
            translation = "Fear Allah wherever you may be; follow up a bad deed with a good deed and it will wipe it out; and behave towards people with good character.",
            narrator = "Mu'adh ibn Jabal (RA)",
            grade = "Hasan (Tirmidhi)"
        ),

        // Sunan Abi Dawud
        HadithEntity(
            collection = "abudawud",
            bookNumber = 2,
            chapterName = "Supplications & Dhikr",
            hadithNumber = 1488,
            arabicText = "إِنَّ اللَّهَ حَيِيٌّ كَرِيمٌ يَسْتَحْيِي إِذَا رَفَعَ الرَّجُلُ إِلَيْهِ يَدَيْهِ أَنْ يَرُدَّهُمَا صِفْرًا خَائِبَتَيْنِ",
            translation = "Indeed, your Lord is Modest and Generous. He is shy to turn away His servant's hands empty and unfulfilled when he raises them in prayer to Him.",
            narrator = "Salman al-Farsi (RA)",
            grade = "Sahih (Abu Dawud)"
        ),
        HadithEntity(
            collection = "abudawud",
            bookNumber = 36,
            chapterName = "Knowledge & Wisdom",
            hadithNumber = 3641,
            arabicText = "خَيْرُكُمْ مَنْ تَعَلَّمَ الْقُرْآنَ وَعَلَّمَهُ",
            translation = "The best among you are those who learn the Quran and teach it to others.",
            narrator = "Uthman bin Affan (RA)",
            grade = "Sahih (Abu Dawud)"
        ),
        HadithEntity(
            collection = "abudawud",
            bookNumber = 43,
            chapterName = "Good Character & Manners",
            hadithNumber = 4941,
            arabicText = "الرَّاحِمُونَ يَرْحَمُهُمُ الرَّحْمَنُ، ارْحَمُوا مَنْ فِي الأَرْضِ يَرْحَمْكُمْ مَنْ فِي السَّمَاءِ",
            translation = "The merciful will be shown mercy by the Most Merciful. Show mercy to those on the earth, and the One in the heavens will show mercy to you.",
            narrator = "Abdullah ibn Amr (RA)",
            grade = "Sahih (Abu Dawud & Tirmidhi)"
        ),

        // Sunan an-Nasa'i
        HadithEntity(
            collection = "nasai",
            bookNumber = 5,
            chapterName = "Prayer & Purification",
            hadithNumber = 1300,
            arabicText = "أَقْرَبُ مَا يَكُونُ الْعَبْدُ مِنْ رَبِّهِ وَهُوَ سَاجِدٌ فَأَكْثِرُوا الدُّعَاءَ",
            translation = "The closest a servant comes to his Lord is while he is in prostration (Sujud), so make abundant supplication therein.",
            narrator = "Abu Hurairah (RA)",
            grade = "Sahih (an-Nasa'i & Muslim)"
        ),
        HadithEntity(
            collection = "nasai",
            bookNumber = 22,
            chapterName = "Fasting & Ramadan",
            hadithNumber = 2222,
            arabicText = "عَلَيْكَ بِالصَّوْمِ فَإِنَّهُ لاَ عِدْلَ لَهُ",
            translation = "Abu Umamah said: I said, 'O Messenger of Allah, tell me of an action by which Allah will benefit me.' He said: 'Adhere to fasting, for there is nothing equal to it.'",
            narrator = "Abu Umamah al-Bahili (RA)",
            grade = "Sahih (an-Nasa'i)"
        ),
        HadithEntity(
            collection = "nasai",
            bookNumber = 25,
            chapterName = "Family & Compassion",
            hadithNumber = 3104,
            arabicText = "الْزَمْهَا فَإِنَّ الْجَنَّةَ تَحْتَ رِجْلَيْهَا",
            translation = "A man came asking to join the campaign, and the Prophet asked: 'Do you have a mother?' He said: 'Yes.' The Prophet said: 'Stay with her, for Paradise is at her feet.'",
            narrator = "Mu'awiyah ibn Jahimah (RA)",
            grade = "Hasan Sahih (an-Nasa'i)"
        )
    )
}
