package com.example.data.remote

import android.util.Log
import com.example.data.model.MessageSender
import com.example.data.model.SpiritualChatMessage
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.Content
import com.google.firebase.ai.type.TextPart
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout

private const val TAG = "FirebaseSpiritualChat"

object FirebaseSpiritualChatService {

    private const val SYSTEM_INSTRUCTION = """You are Deen AI, a compassionate, reverent, and authentically grounded Islamic spiritual companion.
Your answers are strictly rooted in the Holy Quran and authentic Sunnah (Sahih al-Bukhari, Sahih Muslim, Abu Dawud, Tirmidhi, Nasa'i, Ibn Majah).
When responding:
1. Greet warmly with peace (Assalamu Alaikum) when the user initiates greeting or seeks solace.
2. Quote relevant Quranic verses with exact chapter and verse notation (e.g., [Surah Ash-Sharh 94:5-6]).
3. Cite authentic Hadith sources (e.g., Sahih al-Bukhari 6407).
4. Provide actionable, practical spiritual steps (Salah improvements, Dhikr, mindfulness).
5. Share authentic supplications (Duas) with Arabic, transliteration, and English translation.
6. If scholars hold differing valid opinions, present them with Islamic respect and moderation.
7. Format your response cleanly using markdown (bold titles, bullet points, blockquotes)."""

    private val generativeModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            generationConfig = generationConfig {
                temperature = 0.7f
            },
            systemInstruction = Content("system", listOf(TextPart(SYSTEM_INSTRUCTION)))
        )
    }

    /**
     * Streams the spiritual response for the given user message and conversation history.
     */
    fun streamSpiritualChat(
        history: List<SpiritualChatMessage>,
        userMessage: String
    ): Flow<String> = flow {
        val chatHistory = mutableListOf<Content>()
        val relevantHistory = history.takeLast(8)
        for (msg in relevantHistory) {
            val role = if (msg.sender == MessageSender.USER) "user" else "model"
            if (msg.text.isNotBlank() && !msg.isError) {
                chatHistory.add(Content(role, listOf(TextPart(msg.text))))
            }
        }

        try {
            // Mandated 30-60s timeout for model inference
            withTimeout(60_000L) {
                val chat = generativeModel.startChat(history = chatHistory)
                chat.sendMessageStream(userMessage).collect { chunk ->
                    val chunkText = chunk.text.orEmpty()
                    if (chunkText.isNotEmpty()) {
                        emit(chunkText)
                    }
                }
            }
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "Chat generation timed out", e)
            throw IllegalStateException("The spiritual guidance response timed out after 60 seconds. Please check your network and try again.")
        } catch (e: CancellationException) {
            // Mandated: Always re-throw CancellationException
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Firebase AI generation error", e)
            throw e
        }
    }.flowOn(Dispatchers.IO)

    /**
     * Non-streaming fallback generation with timeout.
     */
    suspend fun getSpiritualChatResponse(
        history: List<SpiritualChatMessage>,
        userMessage: String
    ): String = withContext(Dispatchers.IO) {
        val chatHistory: List<Content> = history.takeLast(8)
            .filter { it.text.isNotBlank() && !it.isError }
            .map { msg: SpiritualChatMessage ->
                val role = if (msg.sender == MessageSender.USER) "user" else "model"
                Content(role, listOf(TextPart(msg.text)))
            }
        try {
            withTimeout(60_000L) {
                val chat = generativeModel.startChat(history = chatHistory)
                val response = chat.sendMessage(userMessage)
                val text = response.text
                if (!text.isNullOrBlank()) {
                    text
                } else {
                    throw IllegalStateException("The model returned an empty response. Please rephrase your question.")
                }
            }
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "Chat response timed out", e)
            throw IllegalStateException("The request timed out after 60 seconds. Please try again.")
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Firebase AI chat failed", e)
            throw e
        }
    }
}
