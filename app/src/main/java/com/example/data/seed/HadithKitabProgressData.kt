package com.example.data.seed

data class HadithKitabItem(
    val id: String,
    val collectionId: String,
    val bookNumber: Int,
    val titleEnglish: String,
    val titleArabic: String,
    val hadithCount: Int,
    val overview: String
)

object HadithKitabProgressData {

    val PROMINENT_BOOKS = listOf(
        // Bukhari
        HadithKitabItem("bukhari_1", "bukhari", 1, "Revelation (Bad' al-Wahy)", "بدء الوحي", 7, "How divine inspiration commenced upon the Prophet (pbuh) and the first revelation in Cave Hira."),
        HadithKitabItem("bukhari_2", "bukhari", 2, "Belief (Al-Iman)", "كتاب الإيمان", 53, "The branches of faith, love of the Prophet, and the pillars of Islam."),
        HadithKitabItem("bukhari_3", "bukhari", 3, "Knowledge (Al-'Ilm)", "كتاب العلم", 77, "The virtues of seeking knowledge, teaching with kindness, and preserving authentic traditions."),
        HadithKitabItem("bukhari_4", "bukhari", 4, "Ablution (Al-Wudu')", "كتاب الوضوء", 111, "Purification rituals, Sunnahs of Wudu, and hygiene essentials."),
        HadithKitabItem("bukhari_8", "bukhari", 8, "Prayers (As-Salah)", "كتاب الصلاة", 149, "Timings, postures, congregation virtues, and mosques."),
        HadithKitabItem("bukhari_24", "bukhari", 24, "Obligatory Charity (Zakat)", "كتاب الزكاة", 117, "Nisab, recipients of Zakat, and warnings against withholding wealth."),
        HadithKitabItem("bukhari_30", "bukhari", 30, "Fasting (As-Sawm)", "كتاب الصوم", 123, "Virtues of Ramadan, Suhoor, Iftar, Laylatul Qadr, and I'tikaf."),
        
        // Muslim
        HadithKitabItem("muslim_1", "muslim", 1, "Faith (Kitab al-Iman)", "كتاب الإيمان", 380, "Foundations of faith, Hadith of Jibreel, and intercession on Judgment Day."),
        HadithKitabItem("muslim_2", "muslim", 2, "Purification (Kitab at-Taharah)", "كتاب الطهارة", 130, "Cleanliness is half of faith, Tayammum, and Sunan al-Fitrah."),
        HadithKitabItem("muslim_4", "muslim", 4, "Prayer (Kitab as-Salah)", "كتاب الصلاة", 300, "Detailed postures, recitation in prayer, and remembrance after Salam."),
        HadithKitabItem("muslim_13", "muslim", 13, "Fasting (Kitab as-Siyam)", "كتاب الصيام", 170, "Observing the crescent moon, voluntary fasts, and virtue of fasting the White Days."),

        // 40 Nawawi
        HadithKitabItem("nawawi_all", "nawawi", 1, "The Forty Hadith of Imam An-Nawawi", "الأربعون النووية", 42, "Foundational traditions encompassing the complete essence and architecture of Islam.")
    )

    fun getBooksForCollection(collectionId: String): List<HadithKitabItem> {
        return if (collectionId == "All") PROMINENT_BOOKS else PROMINENT_BOOKS.filter { it.collectionId.equals(collectionId, ignoreCase = true) }
    }
}
