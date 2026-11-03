package com.example.data.seed

import com.example.data.model.BookCategory
import com.example.data.model.BookChapter
import com.example.data.model.IslamicBook

object IslamicBookSeedData {
    val FOUNDATIONAL_BOOKS: List<IslamicBook> = listOf(
        IslamicBook(
            id = "sealed_nectar",
            title = "The Sealed Nectar",
            arabicTitle = "الرحيق المختوم",
            author = "Safiur Rahman al-Mubarakpuri",
            translator = "Darussalam Editorial Board",
            category = BookCategory.SEERAH,
            summary = "The award-winning biography of Prophet Muhammad (ﷺ), tracing pre-Islamic Arabia, the Makkan trials, the migration to Madinah, and the triumph of truth.",
            coverEmoji = "🌺",
            publishedYear = "1979 CE",
            chapters = listOf(
                BookChapter(
                    chapterNumber = 1,
                    titleEnglish = "Location and Nature of Arab Tribes",
                    titleArabic = "موقع العرب وأقوامها",
                    textContent = """
The Arabian Peninsula is bounded by the Red Sea and the Sinai on the west, the Arabian Gulf and southern Iraq on the east, the Arabian Sea on the south, and the Levant on the north. This strategic geographic position ensured that while surrounded by mighty empires like the Romans and Persians, Arabia retained natural isolation and liberty.

The lineage of the Arabs is divided into three distinct groups:
1. Arab Ba'idah (The Extinct Arabs): Ancient tribes whose history is largely lost, including 'Ad, Thamud, and Tasm.
2. Arab 'Aribah (The Pure Arabs): Originating from the cradle of Yemen and descending from Qahtan.
3. Arab Musta'ribah (The Arabized Arabs): Descending from Prophet Ismail (AS) through 'Adnan.

Prophet Ibrahim (AS) brought his infant son Ismail and noble wife Hajar to the barren valley of Makkah near the foundations of the House. When their water was exhausted, Hajar ran between the hills of Safa and Marwah in desperate prayer until Angel Jibril struck the earth and the pure, miraculous spring of Zamzam burst forth. The Jurhum tribe settled beside them, and Ismail grew up among them, learning the Arabic language and marrying from their daughters.
                    """.trimIndent()
                ),
                BookChapter(
                    chapterNumber = 2,
                    titleEnglish = "Lineage and Noble Birth of Muhammad (ﷺ)",
                    titleArabic = "نسب النبي ومولده الشريف",
                    textContent = """
The family of the Prophet Muhammad (ﷺ) is known as the Hashimite family after his great-grandfather Hashim ibn Abd Manaf. Hashim was honored with providing food and water to the pilgrims in Makkah. 

Abd al-Muttalib, the Prophet's grandfather, rediscovering the buried well of Zamzam after a divine dream, was the chief of Quraysh. His son Abdullah married Aminah bint Wahb, the most noble woman of Banu Zuhrah.

While Aminah was pregnant, Abdullah journeyed on trade to Gaza and passed away on the return journey in Yathrib (Madinah). Muhammad (ﷺ) was born an orphan on Monday, the 12th of Rabi' al-Awwal in the Year of the Elephant (570 CE). 

His grandfather Abd al-Muttalib carried the newborn baby into the Holy Kaaba, gave thanks to Allah, and named him 'Muhammad'—a name virtually unheard of in Arabia, meaning 'The Much-Praised One'. Halimah as-Sa'diyyah of Banu Sa'd fostered him in the pure desert air, witnessing extraordinary blessings flood her barren pastures from the moment the blessed child entered her tent.
                    """.trimIndent()
                ),
                BookChapter(
                    chapterNumber = 3,
                    titleEnglish = "The Descent of Divine Revelation at Cave Hira",
                    titleArabic = "نزول الوحي في غار حراء",
                    textContent = """
When Prophet Muhammad (ﷺ) approached the age of forty, solitude became beloved to his pure heart. He would retreat with provisions of water and barley bread to Cave Hira atop the Mountain of Light (Jabal an-Nur), contemplating the majesty of the Creator and the spiritual degradation of idol worship.

In the sacred month of Ramadan, Angel Jibril descended upon him in radiant reality, commanding: 'Read!' (Iqra!).
The Prophet replied with trembling humility: 'I cannot read' (Ma ana bi-qari').
The Angel seized him and pressed him tightly until he reached his utmost endurance, then released him and repeated: 'Read!'
On the third embrace, the Angel recited the opening verses of Surah Al-'Alaq:
'Read in the name of your Lord who created—created man from a clinging clot. Read, and your Lord is the Most Generous—who taught by the pen, taught man that which he knew not.'

Trembling with awe, the Messenger of Allah returned home to his noble wife Khadijah (RA), crying: 'Cover me! Cover me!' (Zammiluni! Zammiluni!). She wrapped him tenderly, soothing his beating heart: 'Never! By Allah, Allah will never forsake you. You uphold family ties, you bear the burdens of the weak, you honor the guest, and you assist those afflicted by justice.'
                    """.trimIndent()
                )
            )
        ),
        IslamicBook(
            id = "purification_soul",
            title = "Purification of the Soul (Tazkiyat an-Nafs)",
            arabicTitle = "تزكية النفس",
            author = "Imam Ibn al-Qayyim & Ibn Rajab",
            translator = "Ahmad Farid",
            category = BookCategory.SPIRITUALITY,
            summary = "A masterwork diagnosing the spiritual diseases of the heart—envy, pride, greed, and heedlessness—and prescribing authentic remedies of repentance, remembrance, and sincere devotion.",
            coverEmoji = "💎",
            publishedYear = "Classical (8th Century AH)",
            chapters = listOf(
                BookChapter(
                    chapterNumber = 1,
                    titleEnglish = "The Types of Hearts",
                    titleArabic = "أقسام القلوب",
                    textContent = """
As the heart is described by Allah in the Holy Quran, it falls into three major categories:

1. The Sound and Healthy Heart (Al-Qalb as-Saleem):
This is the only heart whose owner will be saved on the Day of Resurrection, as Allah states: 'The Day when neither wealth nor children will benefit, except one who comes to Allah with a sound heart' (26:88-89). It is a heart preserved from every lust that opposes Allah's commands and every doubt that contradicts His revelation. It loves only for Allah, hates only for Allah, and finds its total tranquility in His remembrance.

2. The Dead Heart (Al-Qalb al-Mayyit):
This is a heart devoid of life. It knows not its Lord, nor does it worship Him according to His pleasure. Instead, it is subjugated to fleeting lusts, idolizing personal ego and worldly gain. If it loves, it loves for its whim; if it gives, it gives for its vanity.

3. The Sick and Ailing Heart (Al-Qalb al-Mareed):
This heart possesses life, yet harbors a disease. It contains two pulling currents: the love of Allah and faith, and the venom of pride, envy, ostentation (Riya), and worldly obsession. Whichever of the two impulses dominates determines its ultimate destiny.
                    """.trimIndent()
                ),
                BookChapter(
                    chapterNumber = 2,
                    titleEnglish = "Remedies for Spiritual Hardness",
                    titleArabic = "أدوية قسوة القلب",
                    textContent = """
The human heart rusts just as iron rusts when touched by moisture. Its polishing is through two essential deeds: the abundant Remembrance of Allah (Dhikr) and sincere Seeking of Forgiveness (Istighfar).

The classical scholars prescribed four proven remedies for softening a hardened heart:
1. Reciting the Holy Quran with Contemplation (Tadabbur): Reading one verse with reflection is superior to reading the entire book heedlessly without understanding.
2. An Empty Stomach and Moderation in Food: Satiety numbs the spiritual faculties and breeds lethargy in worship.
3. Night Prayer (Qiyam al-Layl): Standing in peaceful vigil when the world sleeps connects the spirit directly to the divine presence.
4. Supplicating at Dawn (Dua at Suhur): The time when Allah descends to the lowest heaven, asking: 'Who is calling upon Me that I may answer him?'
                    """.trimIndent()
                )
            )
        ),
        IslamicBook(
            id = "beginning_guidance",
            title = "The Beginning of Guidance",
            arabicTitle = "بداية الهداية",
            author = "Imam Abu Hamid al-Ghazali",
            translator = "Mashhad Al-Allaf",
            category = BookCategory.CLASSICAL_AQEEDAH,
            summary = "A foundational roadmap detailing the daily spiritual discipline of the believer from waking at Fajr to retiring at night, guarding the seven limbs from disobedience.",
            coverEmoji = "🌅",
            publishedYear = "1100 CE",
            chapters = listOf(
                BookChapter(
                    chapterNumber = 1,
                    titleEnglish = "The Protocol of Awakening and Morning Devotion",
                    titleArabic = "آداب الاستيقاظ وصلاة الصبح",
                    textContent = """
Strive to awaken before the break of dawn. Let the very first utterance on your tongue be the remembrance of your Creator:
'Alhamdu lillahil-ladhi ahyana ba'da ma amatana wa ilayhin-nushur' (Praise be to Allah who brought us back to life after having caused us to die, and unto Him is the final resurrection).

When you put on your garments, intend to conceal the nakedness that Allah commanded you to cover, and prepare yourself to stand reverently before your Lord in the Fajr prayer. 

Perform your ablution (Wudu) mindfully:
- With each washing of the face, pray that Allah illuminates your face on the day faces are darkened.
- With washing your hands, pray that your book of deeds is placed in your right hand.
- With wiping your head, pray for the shade of Allah's Throne on the Day when there is no shade except His.
                    """.trimIndent()
                ),
                BookChapter(
                    chapterNumber = 2,
                    titleEnglish = "Guarding the Seven Limbs from Sin",
                    titleArabic = "حفظ الجوارح السبعة",
                    textContent = """
Your body parts are a sacred trust (Amanah) gifted by Allah. On the Day of Judgment, your tongue, eyes, ears, hands, feet, stomach, and private parts will bear witness to how they were employed.

1. The Eye: Created to look upon the marvelous wonders of Allah's creation and read His scripture; guard it from looking at forbidden things or viewing Muslims with scorn.
2. The Ear: Created to listen to the Quran, prophetic wisdom, and beneficial science; protect it from backbiting (Gheebah) and slander, for the listener is a partner of the speaker in sin.
3. The Tongue: The swiftest limb to cast people into the Fire; train it in truthfulness, silence unless speaking good, and unceasing glorification of Allah.
                    """.trimIndent()
                )
            )
        ),
        IslamicBook(
            id = "dont_be_sad",
            title = "Don't Be Sad (La Tahzan)",
            arabicTitle = "لا تحزن",
            author = "Dr. 'Aidh ibn Abdullah al-Qarni",
            translator = "Faisal Shafeeq",
            category = BookCategory.CONTEMPORARY,
            summary = "An inspiring contemporary classic offering spiritual solace, practical optimism, and Quranic wisdom to overcome anxiety, grief, and despair in modern life.",
            coverEmoji = "☀️",
            publishedYear = "2001 CE",
            chapters = listOf(
                BookChapter(
                    chapterNumber = 1,
                    titleEnglish = "Live in the Boundaries of Today",
                    titleArabic = "عش في حدود يومك",
                    textContent = """
When morning arrives, do not await the evening; live as though this single day is your entire lifespan. Yesterday is gone with all its errors and sorrows, never to return. Tomorrow is an unborn child in the realm of the unseen; why exhaust yourself borrowing distress from an imaginary future?

Dedicate the hours of today to excellence in prayer, sincerity in work, kindness to parents, and gratitude for breaths drawn without pain. 

Remember the profound words: 'Sufficient unto the day is the blessing thereof.' By dividing life into single manageable days, mountains of worry dissolve into single footsteps of peace.
                    """.trimIndent()
                ),
                BookChapter(
                    chapterNumber = 2,
                    titleEnglish = "Verily, with Hardship Comes Ease",
                    titleArabic = "إن مع العسر يسرا",
                    textContent = """
The darkest hour of the night is that which precedes the break of dawn. When ropes become excessively tight, they snap; when hunger peaks, nourishment arrives; when despair knocks, divine relief is closest.

Consider the prophets of Allah:
- Ibrahim in the center of the roaring catapult fire found it transformed into cool peace.
- Yunus in the triple darkness of the night, the deep sea, and the belly of the whale was delivered through: 'La ilaha illa anta, subhanaka inni kuntu minaz-zalimeen'.
- Muhammad (ﷺ) in Cave Thawr comforted Abu Bakr: 'Do not grieve; indeed, Allah is with us.'

Never doubt that relief is paired with distress just as dawn is paired with night.
                    """.trimIndent()
                )
            )
        ),
        IslamicBook(
            id = "reclaim_heart",
            title = "Reclaim Your Heart",
            arabicTitle = "استعادة قلبك",
            author = "Yasmin Mogahed",
            translator = "Original English Edition",
            category = BookCategory.CONTEMPORARY,
            summary = "A luminous spiritual manual on breaking free from worldly attachments, healing emotional wounds, finding authentic love in Allah, and maintaining inner peace in a turbulent world.",
            coverEmoji = "🕊️",
            publishedYear = "2012 CE",
            chapters = listOf(
                BookChapter(
                    chapterNumber = 1,
                    titleEnglish = "Attachments: The Ocean and the Ship",
                    titleArabic = "التعلق والدنيا",
                    textContent = """
This worldly life (Dunya) is like an endless ocean, and our hearts are like ships sailing across its waters. So long as the water remains outside the ship, the vessel glides gracefully toward its destination. But the moment water enters the ship, it begins to sink.

Most of our heartbreak, anxiety, and depression arises not from living in the world, but from letting the world settle inside our hearts. When we attach our happiness, identity, and security to temporary things—people, titles, money, compliments—we anchor our souls to that which is bound to perish.

Keep the Dunya in your hands, but keep only Allah in your heart. When Allah is your sole anchor, no worldly storm can sink your ship.
                    """.trimIndent()
                ),
                BookChapter(
                    chapterNumber = 2,
                    titleEnglish = "Why Do Good People Suffer?",
                    titleArabic = "حكمة الابتلاء",
                    textContent = """
Gold is placed inside scorching flames not to destroy it, but to burn away impurities until only radiant, flawless metal remains. 

Allah tests those whom He loves not to punish them, but to awaken them from heedlessness, to draw them onto their knees in intimate prayer, and to wash away sins before the great accounting of the Hereafter.

A trial that brings you closer to Allah is a blessing in disguise; whereas an ease that pulls you away from Allah is a hidden affliction.
                    """.trimIndent()
                )
            )
        )
    )
}
