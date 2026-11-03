package com.example.data.model

data class BookChapter(
    val chapterNumber: Int,
    val titleEnglish: String,
    val titleArabic: String? = null,
    val textContent: String,
    val estimatedReadMinutes: Int = 5
)

enum class BookCategory(val displayName: String) {
    SEERAH("Seerah & Biography"),
    CLASSICAL_AQEEDAH("Classical Aqeedah & Theology"),
    SPIRITUALITY("Spirituality & Tazkiyah"),
    CONTEMPORARY("Contemporary & Heart"),
    FIQH_SUNNAH("Fiqh & Sunnah")
}

data class IslamicBook(
    val id: String,
    val title: String,
    val arabicTitle: String,
    val author: String,
    val translator: String? = null,
    val category: BookCategory,
    val summary: String,
    val coverEmoji: String = "📖",
    val publishedYear: String,
    val chapters: List<BookChapter>,
    val isCrawled: Boolean = false,
    val isFavorite: Boolean = false,
    val currentChapterIndex: Int = 0,
    val readProgressPercentage: Float = 0f
)
