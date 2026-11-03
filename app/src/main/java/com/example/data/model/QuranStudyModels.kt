package com.example.data.model

data class WordByWordItem(
    val arabic: String,
    val transliteration: String,
    val translation: String
)

data class TafsirItem(
    val surahNumber: Int,
    val ayahNumber: Int,
    val author: String,
    val title: String,
    val summary: String,
    val detailedText: String,
    val keyLessons: List<String>
)

enum class QuranReadingMode(val displayName: String) {
    TRANSLATION_LIST("Translation List"),
    MUSHAF_PAGE("Mushaf Page")
}

data class TranslationEdition(
    val id: String,
    val name: String,
    val author: String,
    val language: String = "English"
) {
    companion object {
        val SAHIH_INTERNATIONAL = TranslationEdition("en.sahih", "Sahih International", "Saheeh International", "English")
        val CLEAR_QURAN = TranslationEdition("en.khattab", "The Clear Quran", "Dr. Mustafa Khattab", "English")
        val YUSUF_ALI = TranslationEdition("en.yusufali", "Yusuf Ali", "Abdullah Yusuf Ali", "English")
        val PICKTHALL = TranslationEdition("en.pickthall", "Pickthall", "Marmaduke Pickthall", "English")
        val TAQI_USMANI = TranslationEdition("en.usmani", "Taqi Usmani", "Mufti Taqi Usmani", "English")
        val URDU_MAUDUDI = TranslationEdition("ur.maududi", "Tafheem-ul-Quran (Urdu)", "Sayyid Abul A'la Maududi", "Urdu")
        val FRENCH_HAMIDULLAH = TranslationEdition("fr.hamidullah", "Le Coran (French)", "Muhammad Hamidullah", "French")
        val INDONESIAN_KEMENAG = TranslationEdition("id.kemenag", "Kemenag (Indonesian)", "Kementerian Agama RI", "Indonesian")

        val ALL_EDITIONS = listOf(
            CLEAR_QURAN,
            SAHIH_INTERNATIONAL,
            YUSUF_ALI,
            PICKTHALL,
            TAQI_USMANI,
            URDU_MAUDUDI,
            FRENCH_HAMIDULLAH,
            INDONESIAN_KEMENAG
        )

        fun getById(id: String): TranslationEdition {
            return ALL_EDITIONS.firstOrNull { it.id == id } ?: CLEAR_QURAN
        }
    }
}

data class QuranHighlightColor(
    val hex: String,
    val name: String,
    val category: String,
    val description: String
) {
    companion object {
        val EMERALD = QuranHighlightColor("#10B981", "Emerald", "Reflection & Tadabbur", "Heart softeners and deep contemplation")
        val GOLD = QuranHighlightColor("#F59E0B", "Amber Gold", "Wisdom & Rulings", "Commands, prohibitions, and divine wisdom")
        val SKY_BLUE = QuranHighlightColor("#0EA5E9", "Sky Blue", "Dua & Supplication", "Prophetic prayers and supplications")
        val ROSE = QuranHighlightColor("#F43F5E", "Rose Coral", "Warning & Reminder", "Reminders of the Akhirah and accountability")
        val PURPLE = QuranHighlightColor("#8B5CF6", "Amethyst", "Prophetic History", "Stories of the Prophets and nations")

        val ALL_COLORS = listOf(EMERALD, GOLD, SKY_BLUE, ROSE, PURPLE)

        fun getByHex(hex: String): QuranHighlightColor {
            return ALL_COLORS.firstOrNull { it.hex.equals(hex, ignoreCase = true) } ?: EMERALD
        }
    }
}
