package com.example.data.seed

import com.example.data.local.HadithBookEntity
import com.example.data.local.HadithChapterEntity

object HadithSchemaSeedData {

    val DEFAULT_BOOKS = listOf(
        HadithBookEntity(
            id = "bukhari",
            titleEnglish = "Sahih al-Bukhari",
            titleArabic = "صحيح البخاري",
            authorName = "Imam Muhammad al-Bukhari",
            deathYearHijri = 256,
            totalHadithCount = 7563,
            totalChaptersCount = 97,
            badgeLetter = "B",
            badgeColorHex = "#E25A53",
            description = "The most authentic book of Hadith after the Holy Quran, compiled by Imam al-Bukhari with stringent verification conditions."
        ),
        HadithBookEntity(
            id = "muslim",
            titleEnglish = "Sahih Muslim",
            titleArabic = "صحيح مسلم",
            authorName = "Imam Muslim ibn al-Hajjaj",
            deathYearHijri = 261,
            totalHadithCount = 3033,
            totalChaptersCount = 56,
            badgeLetter = "M",
            badgeColorHex = "#E05688",
            description = "Renowned for its rigorous thematic organization, sanad corroboration, and theological authenticity."
        ),
        HadithBookEntity(
            id = "nasai",
            titleEnglish = "Sunan an-Nasai",
            titleArabic = "سنن النسائي",
            authorName = "Imam Ahmad an-Nasai",
            deathYearHijri = 303,
            totalHadithCount = 5758,
            totalChaptersCount = 52,
            badgeLetter = "N",
            badgeColorHex = "#3F51B5",
            description = "Famed for its precision in legal jurisprudence (Fiqh) and subtle narration scrutiny (Tahqiq)."
        ),
        HadithBookEntity(
            id = "abudawud",
            titleEnglish = "Sunan Abu Dawood",
            titleArabic = "سنن أبي داود",
            authorName = "Imam Abu Dawood as-Sijistani",
            deathYearHijri = 275,
            totalHadithCount = 5274,
            totalChaptersCount = 43,
            badgeLetter = "AD",
            badgeColorHex = "#4C79F2",
            description = "Premier canonical reference for Ahkam (legal rulings) and juristic Sunnah across all schools of thought."
        ),
        HadithBookEntity(
            id = "tirmidhi",
            titleEnglish = "Jamiat Tirmidhi",
            titleArabic = "جامع الترمذي",
            authorName = "Imam Muhammad at-Tirmidhi",
            deathYearHijri = 279,
            totalHadithCount = 3960,
            totalChaptersCount = 49,
            badgeLetter = "T",
            badgeColorHex = "#4FC3F7",
            description = "Notable for grading each tradition (Sahih, Hasan, Gharib) and comparing opinions of classical Jurists."
        ),
        HadithBookEntity(
            id = "ibnmajah",
            titleEnglish = "Sunan Ibn Majah",
            titleArabic = "سنن ابن ماجه",
            authorName = "Imam Ibn Majah al-Qazwini",
            deathYearHijri = 273,
            totalHadithCount = 4341,
            totalChaptersCount = 37,
            badgeLetter = "IM",
            badgeColorHex = "#389B82",
            description = "One of the Kutub al-Sittah (Six Major Books) with rare and beneficial supporting narrations."
        ),
        HadithBookEntity(
            id = "malik",
            titleEnglish = "Muwatta Malik",
            titleArabic = "موطأ مالك",
            authorName = "Imam Malik ibn Anas",
            deathYearHijri = 179,
            totalHadithCount = 1858,
            totalChaptersCount = 61,
            badgeLetter = "M",
            badgeColorHex = "#E67E22",
            description = "The earliest surviving text of prophetic Sunnah and living traditions of the people of Medina."
        ),
        HadithBookEntity(
            id = "nawawi",
            titleEnglish = "40 Hadith an-Nawawi",
            titleArabic = "الأربعون النووية",
            authorName = "Imam Yahya ibn Sharaf an-Nawawi",
            deathYearHijri = 676,
            totalHadithCount = 42,
            totalChaptersCount = 1,
            badgeLetter = "N",
            badgeColorHex = "#8B5CF6",
            description = "A concise, universally memorized masterpiece encapsulating the comprehensive foundations of Islam."
        )
    )

    val DEFAULT_CHAPTERS = listOf(
        // Bukhari
        HadithChapterEntity("bukhari_1", "bukhari", 1, "1. Revelation", "بدء الوحي", "Hadith No: 1-7", 7, "How the divine revelation began"),
        HadithChapterEntity("bukhari_2", "bukhari", 2, "2. Belief", "كتاب الإيمان", "Hadith No: 8-58", 51, "Branches and nature of faith"),
        HadithChapterEntity("bukhari_3", "bukhari", 3, "3. Knowledge", "كتاب العلم", "Hadith No: 59-134", 76, "Virtues of learning and teaching"),
        HadithChapterEntity("bukhari_4", "bukhari", 4, "4. Ablutions (Wudu')", "كتاب الوضوء", "Hadith No: 135-247", 113, "Rules of ritual purification"),
        HadithChapterEntity("bukhari_5", "bukhari", 5, "5. Bathing (Ghusl)", "كتاب الغسل", "Hadith No: 248-293", 46, "Full ritual purification"),
        HadithChapterEntity("bukhari_6", "bukhari", 6, "6. Menstrual Periods", "كتاب الحيض", "Hadith No: 294-333", 40, "Jurisprudence of menses"),
        HadithChapterEntity("bukhari_7", "bukhari", 7, "7. Tayammum", "كتاب التيمم", "Hadith No: 334-348", 15, "Dry ablution with clean dust"),
        HadithChapterEntity("bukhari_8", "bukhari", 8, "8. Prayers (Salat)", "كتاب الصلاة", "Hadith No: 349-520", 172, "Daily obligatory prayers"),
        HadithChapterEntity("bukhari_24", "bukhari", 24, "24. Obligatory Charity (Zakat)", "كتاب الزكاة", "Hadith No: 1395-1512", 118, "Rules and spirit of Zakat"),
        HadithChapterEntity("bukhari_31", "bukhari", 31, "31. Fasting (Sawm)", "كتاب الصوم", "Hadith No: 1891-2007", 117, "Fasting in Ramadan and voluntary fasts"),

        // Muslim
        HadithChapterEntity("muslim_1", "muslim", 1, "1. Faith (Kitab al-Iman)", "كتاب الإيمان", "Hadith No: 1-92", 92, "The creed and belief"),
        HadithChapterEntity("muslim_2", "muslim", 2, "2. Purification (Taharah)", "كتاب الطهارة", "Hadith No: 93-222", 130, "Cleanliness in Islam"),
        HadithChapterEntity("muslim_4", "muslim", 4, "4. Prayers (Kitab as-Salah)", "كتاب الصلاة", "Hadith No: 397-571", 175, "Establishment of prayer"),
        HadithChapterEntity("muslim_13", "muslim", 13, "13. Fasting (Kitab as-Siyam)", "كتاب الصيام", "Hadith No: 1079-1167", 89, "Rules and blessings of fasting"),
        HadithChapterEntity("muslim_35", "muslim", 35, "35. Supplications & Repentance", "كتاب الذكر والدعاء والتوبة", "Hadith No: 2675-2740", 66, "Remembrance and istighfar"),

        // Nasai
        HadithChapterEntity("nasai_1", "nasai", 1, "1. Purification", "كتاب الطهارة", "Hadith No: 1-95", 95, "Purification rules"),
        HadithChapterEntity("nasai_5", "nasai", 5, "5. The Book of Salah", "كتاب الصلاة", "Hadith No: 448-608", 161, "Timings and Sunnah of Salah"),
        HadithChapterEntity("nasai_22", "nasai", 22, "22. The Book of Fasting", "كتاب الصيام", "Hadith No: 2156-2240", 85, "Observing fasts"),

        // Abu Dawood
        HadithChapterEntity("abudawud_1", "abudawud", 1, "1. Purification (Kitab al-Taharah)", "كتاب الطهارة", "Hadith No: 1-80", 80, "Purity essentials"),
        HadithChapterEntity("abudawud_2", "abudawud", 2, "2. Prayer (Kitab al-Salah)", "كتاب الصلاة", "Hadith No: 391-540", 150, "Detailed rules of prayer"),
        HadithChapterEntity("abudawud_14", "abudawud", 14, "14. Fasting (Kitab al-Sawm)", "كتاب الصوم", "Hadith No: 2313-2382", 70, "Rules of fasting"),

        // Tirmidhi
        HadithChapterEntity("tirmidhi_1", "tirmidhi", 1, "1. The Book of Purification", "كتاب الطهارة", "Hadith No: 1-65", 65, "Taharah traditions"),
        HadithChapterEntity("tirmidhi_27", "tirmidhi", 27, "27. Righteousness and Relations", "كتاب البر والصلة", "Hadith No: 1897-1986", 90, "Good conduct and family relations"),
        HadithChapterEntity("tirmidhi_48", "tirmidhi", 48, "48. Supplications (Da'awat)", "كتاب الدعوات", "Hadith No: 3369-3488", 120, "Prophetic invocations"),

        // Ibn Majah
        HadithChapterEntity("ibnmajah_1", "ibnmajah", 1, "1. The Book of the Sunnah", "كتاب المقدمة", "Hadith No: 1-266", 266, "Adherence to the Sunnah"),
        HadithChapterEntity("ibnmajah_2", "ibnmajah", 2, "2. The Book of Purification", "كتاب الطهارة وسننها", "Hadith No: 267-666", 400, "Purification rules"),
        HadithChapterEntity("ibnmajah_7", "ibnmajah", 7, "7. The Book of Fasting", "كتاب الصيام", "Hadith No: 1638-1778", 141, "Virtues of fasting"),

        // Nawawi
        HadithChapterEntity("nawawi_1", "nawawi", 1, "1. The Forty Hadith of Imam An-Nawawi", "الأربعون النووية", "Hadith No: 1-42", 42, "The fundamental core traditions")
    )
}
