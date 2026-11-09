package com.example.data.model

import java.util.UUID

enum class MessageSender {
    USER,
    AI
}

data class SpiritualChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val sender: MessageSender,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val arabicSnippet: String? = null,
    val reference: String? = null,
    val translation: String? = null,
    val recommendedDua: String? = null,
    val practicalTakeaways: List<String> = emptyList(),
    val isStreaming: Boolean = false,
    val isError: Boolean = false
)
