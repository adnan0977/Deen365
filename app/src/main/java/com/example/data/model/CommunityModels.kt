package com.example.data.model

enum class CommunityTopicCategory(val displayName: String, val iconEmoji: String) {
    QURAN_TADABBUR("Quran Reflections", "📖"),
    HADITH_STUDY("Hadith & Sunnah", "📜"),
    DAILY_DUAS("Duas & Dhikr", "🤲"),
    FIQH_QUESTIONS("Salah & Fasting", "🕌"),
    SPIRITUAL_GROWTH("Spiritual Growth", "🌱"),
    DUA_REQUESTS("Mutual Du'a Support", "🕊️")
}

data class CommunityPost(
    val id: String = "",
    val authorId: String = "",
    val authorName: String = "Anonymous Believer",
    val authorRole: String = "member", // "member", "student_of_knowledge", "moderator", "admin"
    val category: CommunityTopicCategory = CommunityTopicCategory.SPIRITUAL_GROWTH,
    val title: String = "",
    val content: String = "",
    val ayahReference: String? = null,
    val hadithReference: String? = null,
    val likesCount: Int = 0,
    val commentsCount: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val isPinned: Boolean = false,
    val isFlagged: Boolean = false,
    val reportCount: Int = 0,
    val likedByUsers: List<String> = emptyList()
)

data class CommunityComment(
    val id: String = "",
    val postId: String = "",
    val authorId: String = "",
    val authorName: String = "Fellow Believer",
    val text: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val likesCount: Int = 0
)

data class DuaRequestItem(
    val id: String = "",
    val requesterId: String = "",
    val requesterName: String = "Anonymous Believer",
    val requestTitle: String = "",
    val requestDetails: String = "",
    val ameenCount: Int = 0,
    val ameenUserIds: List<String> = emptyList(),
    val timestamp: Long = System.currentTimeMillis()
)

data class ContentReportItem(
    val reportId: String = "",
    val contentId: String = "",
    val contentType: String = "post", // "post", "comment"
    val reportedByUserId: String = "",
    val reason: String = "Inappropriate / Disrespectful",
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "pending" // "pending", "reviewed", "dismissed", "deleted"
)
