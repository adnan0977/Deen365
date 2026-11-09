package com.example.audio

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class AudioRecitationManager(context: Context) : TextToSpeech.OnInitListener {

    private val TAG = "AudioRecitationManager"
    private var tts: TextToSpeech? = null
    private var isInitialized = false
    private var isArabicSupported = false

    private val _currentPlayingId = MutableStateFlow<String?>(null)
    val currentPlayingId: StateFlow<String?> = _currentPlayingId.asStateFlow()

    init {
        try {
            tts = TextToSpeech(context.applicationContext, this)
        } catch (e: Throwable) {
            Log.w(TAG, "Failed to instantiate TextToSpeech: ${e.message}")
        }
    }

    override fun onInit(status: Int) {
        try {
            if (status == TextToSpeech.SUCCESS) {
                val arabicLocale = Locale("ar")
                val result = tts?.setLanguage(arabicLocale)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.w(TAG, "Arabic TTS language missing or not supported, falling back to default locale")
                    isArabicSupported = false
                    tts?.setLanguage(Locale.US)
                } else {
                    isArabicSupported = true
                }
                tts?.setSpeechRate(0.85f) // Respectful, steady recitation cadence
                tts?.setPitch(1.0f)

                tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        _currentPlayingId.value = utteranceId
                    }

                    override fun onDone(utteranceId: String?) {
                        if (_currentPlayingId.value == utteranceId) {
                            _currentPlayingId.value = null
                        }
                    }

                    override fun onError(utteranceId: String?) {
                        Log.w(TAG, "TTS utterance error on ID: $utteranceId")
                        if (_currentPlayingId.value == utteranceId) {
                            _currentPlayingId.value = null
                        }
                    }
                })
                isInitialized = true
            } else {
                Log.e(TAG, "TextToSpeech initialization failed with status $status")
            }
        } catch (e: Throwable) {
            Log.w(TAG, "Error in TextToSpeech onInit: ${e.message}")
        }
    }

    fun playRecitation(id: String, arabicText: String, fallbackTransliteration: String = "") {
        if (!isInitialized || tts == null) {
            Log.w(TAG, "TTS not initialized yet")
            return
        }

        if (_currentPlayingId.value == id) {
            stop()
            return
        }

        stop()
        _currentPlayingId.value = id

        val textToSpeak = if (isArabicSupported) {
            try { tts?.setLanguage(Locale("ar")) } catch (_: Throwable) {}
            arabicText.replace("•", " ").trim()
        } else {
            try { tts?.setLanguage(Locale.US) } catch (_: Throwable) {}
            if (fallbackTransliteration.isNotBlank()) {
                fallbackTransliteration.replace("•", " ").trim()
            } else {
                arabicText.replace("•", " ").trim()
            }
        }

        try {
            tts?.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null, id)
        } catch (e: Throwable) {
            Log.w(TAG, "TTS speak failed: ${e.message}")
        }
    }

    fun stop() {
        try {
            tts?.stop()
        } catch (_: Throwable) {}
        _currentPlayingId.value = null
    }

    fun isPlaying(id: String): Boolean {
        return _currentPlayingId.value == id
    }

    fun shutdown() {
        stop()
        try {
            tts?.shutdown()
        } catch (_: Throwable) {}
        tts = null
    }
}
