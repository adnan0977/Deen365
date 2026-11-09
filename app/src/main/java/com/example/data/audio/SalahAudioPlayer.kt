package com.example.data.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.Locale

enum class RepeatMode(val displayName: String, val count: Int) {
    ONCE("1x (Normal)", 1),
    THREE_TIMES("3x (Learn)", 3),
    FIVE_TIMES("5x (Memorize)", 5),
    LOOP("Loop (Infinite)", -1)
}

data class ReciterProfile(
    val id: String,
    val name: String,
    val title: String,
    val cdnIdentifier: String,
    val previewUrl: String
)

data class SalahAudioPlaybackState(
    val isPlaying: Boolean = false,
    val currentAudioId: String? = null,
    val currentTitle: String = "",
    val repeatMode: RepeatMode = RepeatMode.THREE_TIMES,
    val currentIteration: Int = 1,
    val selectedReciter: ReciterProfile = SalahAudioPlayer.DEFAULT_RECITER,
    val isBuffering: Boolean = false,
    val errorMessage: String? = null
)

class SalahAudioPlayer private constructor(private val context: Context) {

    private val prefs = context.getSharedPreferences("salah_audio_prefs", Context.MODE_PRIVATE)
    private val scope = CoroutineScope(Dispatchers.IO)
    private val mainHandler = Handler(Looper.getMainLooper())

    private var mediaPlayer: MediaPlayer? = null
    private var textToSpeech: TextToSpeech? = null
    private var isTtsReady = false

    private val _playbackState = MutableStateFlow(
        SalahAudioPlaybackState(
            repeatMode = loadSavedRepeatMode(),
            selectedReciter = loadSavedReciter()
        )
    )
    val playbackState: StateFlow<SalahAudioPlaybackState> = _playbackState.asStateFlow()

    private var currentAudioUrl: String? = null
    private var currentArabicFallbackText: String? = null

    init {
        initTts()
    }

    private fun initTts() {
        try {
            textToSpeech = TextToSpeech(context.applicationContext) { status ->
                try {
                    if (status == TextToSpeech.SUCCESS) {
                        val result = textToSpeech?.setLanguage(Locale("ar"))
                        isTtsReady = (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED)
                    }
                } catch (_: Throwable) {
                    isTtsReady = false
                }
            }
        } catch (_: Throwable) {
            isTtsReady = false
        }
    }

    fun setRepeatMode(mode: RepeatMode) {
        prefs.edit().putString(KEY_REPEAT_MODE, mode.name).apply()
        _playbackState.update { it.copy(repeatMode = mode) }
    }

    fun setSelectedReciter(reciter: ReciterProfile) {
        prefs.edit().putString(KEY_SELECTED_RECITER, reciter.id).apply()
        _playbackState.update { it.copy(selectedReciter = reciter) }
    }

    /**
     * Plays an audio recitation for a prayer step or Azan.
     * Tries:
     * 1. Cached local file
     * 2. Online streaming from Quran/Adhan CDN
     * 3. Fallback to Arabic Text-To-Speech if stream unavailable
     */
    fun playRecitation(
        audioId: String,
        title: String,
        arabicText: String,
        onlineStreamUrl: String? = null,
        localFilePath: String? = null
    ) {
        if (_playbackState.value.isPlaying && _playbackState.value.currentAudioId == audioId) {
            stopAudio()
            return
        }

        stopAudio()

        val reciter = _playbackState.value.selectedReciter
        val streamUrl = onlineStreamUrl ?: getCdnStreamUrl(audioId, reciter)
        currentAudioUrl = streamUrl
        currentArabicFallbackText = arabicText

        _playbackState.update {
            it.copy(
                isPlaying = true,
                currentAudioId = audioId,
                currentTitle = title,
                currentIteration = 1,
                isBuffering = true,
                errorMessage = null
            )
        }

        scope.launch {
            val explicitFile = if (!localFilePath.isNullOrBlank()) File(localFilePath) else null
            val localCachedFile = if (explicitFile != null && explicitFile.exists() && explicitFile.length() > 1024) {
                explicitFile
            } else {
                getCachedAudioFile(audioId, reciter.id)
            }

            if (localCachedFile.exists() && localCachedFile.length() > 1024) {
                // Play directly from local cache without network
                playLocalFile(localCachedFile)
            } else if (!streamUrl.isNullOrEmpty()) {
                // Stream online & cache in background
                playStreamUrl(streamUrl, localCachedFile)
            } else {
                // Fallback to TTS
                playTtsFallback(arabicText)
            }
        }
    }

    private fun playStreamUrl(url: String, cacheTargetFile: File) {
        mainHandler.post {
            try {
                mediaPlayer?.release()
                mediaPlayer = MediaPlayer().apply {
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build()
                    )
                    setDataSource(context, Uri.parse(url))
                    setOnPreparedListener { mp ->
                        _playbackState.update { it.copy(isBuffering = false) }
                        mp.start()
                    }
                    setOnCompletionListener {
                        handlePlaybackIterationComplete()
                    }
                    setOnErrorListener { _, _, _ ->
                        playTtsFallback(currentArabicFallbackText ?: "")
                        true
                    }
                    prepareAsync()
                }

                // Download to cache asynchronously
                scope.launch {
                    downloadToCache(url, cacheTargetFile)
                }
            } catch (e: Exception) {
                playTtsFallback(currentArabicFallbackText ?: "")
            }
        }
    }

    private fun playLocalFile(file: File) {
        mainHandler.post {
            try {
                mediaPlayer?.release()
                mediaPlayer = MediaPlayer().apply {
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build()
                    )
                    setDataSource(file.absolutePath)
                    setOnPreparedListener { mp ->
                        _playbackState.update { it.copy(isBuffering = false) }
                        mp.start()
                    }
                    setOnCompletionListener {
                        handlePlaybackIterationComplete()
                    }
                    setOnErrorListener { _, _, _ ->
                        playTtsFallback(currentArabicFallbackText ?: "")
                        true
                    }
                    prepareAsync()
                }
            } catch (e: Exception) {
                playTtsFallback(currentArabicFallbackText ?: "")
            }
        }
    }

    private fun handlePlaybackIterationComplete() {
        val currentState = _playbackState.value
        val repeatMode = currentState.repeatMode
        val currentIteration = currentState.currentIteration

        val shouldRepeat = when (repeatMode) {
            RepeatMode.ONCE -> false
            RepeatMode.THREE_TIMES -> currentIteration < 3
            RepeatMode.FIVE_TIMES -> currentIteration < 5
            RepeatMode.LOOP -> true
        }

        if (shouldRepeat) {
            _playbackState.update { it.copy(currentIteration = currentIteration + 1) }
            mainHandler.postDelayed({
                mediaPlayer?.seekTo(0)
                mediaPlayer?.start()
            }, 800) // Small natural breathing pause between repetitions
        } else {
            stopAudio()
        }
    }

    private fun playTtsFallback(arabicText: String) {
        mainHandler.post {
            _playbackState.update { it.copy(isBuffering = false) }
            if (isTtsReady && arabicText.isNotBlank()) {
                textToSpeech?.speak(arabicText, TextToSpeech.QUEUE_FLUSH, null, "salah_step_${System.currentTimeMillis()}")
                mainHandler.postDelayed({
                    handlePlaybackIterationComplete()
                }, 4000)
            } else {
                stopAudio()
            }
        }
    }

    fun stopAudio() {
        mainHandler.post {
            try {
                mediaPlayer?.stop()
                mediaPlayer?.release()
                mediaPlayer = null
                textToSpeech?.stop()
            } catch (e: Exception) {
                // Ignore
            }
            _playbackState.update {
                it.copy(
                    isPlaying = false,
                    currentAudioId = null,
                    currentTitle = "",
                    isBuffering = false,
                    currentIteration = 1
                )
            }
        }
    }

    private fun downloadToCache(urlString: String, outputFile: File) {
        try {
            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection
            connection.connectTimeout = 8000
            connection.readTimeout = 8000
            connection.connect()

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val inputStream = connection.inputStream
                val outputStream = FileOutputStream(outputFile)
                val buffer = ByteArray(4096)
                var bytesRead: Int
                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                    outputStream.write(buffer, 0, bytesRead)
                }
                outputStream.close()
                inputStream.close()
            }
        } catch (e: Exception) {
            // Cache write failed; streaming is still fine
        }
    }

    fun getCachedAudioFile(audioId: String, reciterId: String): File {
        val cacheDir = File(context.cacheDir, "salah_audio")
        if (!cacheDir.exists()) cacheDir.mkdirs()
        val safeName = audioId.replace("[^a-zA-Z0-9_-]".toRegex(), "_")
        return File(cacheDir, "${reciterId}_${safeName}.mp3")
    }

    fun getCdnStreamUrl(audioId: String, reciter: ReciterProfile): String {
        return when (audioId.lowercase()) {
            "azan_fajr" -> "https://cdn.aladhan.com/audio/adhans/fajr.mp3"
            "azan_standard", "azan_dhuhr", "azan_asr", "azan_maghrib", "azan_isha" ->
                "https://cdn.aladhan.com/audio/adhans/makkah.mp3"
            "fatiha" -> "https://everyayah.com/data/${reciter.cdnIdentifier}/001001.mp3"
            "ikhlas" -> "https://everyayah.com/data/${reciter.cdnIdentifier}/112001.mp3"
            "falaq" -> "https://everyayah.com/data/${reciter.cdnIdentifier}/113001.mp3"
            "nas" -> "https://everyayah.com/data/${reciter.cdnIdentifier}/114001.mp3"
            "ayat_kursi" -> "https://everyayah.com/data/${reciter.cdnIdentifier}/002255.mp3"
            else -> "https://everyayah.com/data/${reciter.cdnIdentifier}/001001.mp3"
        }
    }

    fun downloadAudioDirectly(urlString: String, outputFile: File): Boolean {
        return try {
            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection
            connection.connectTimeout = 10000
            connection.readTimeout = 10000
            connection.connect()

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val inputStream = connection.inputStream
                val outputStream = FileOutputStream(outputFile)
                val buffer = ByteArray(4096)
                var bytesRead: Int
                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                    outputStream.write(buffer, 0, bytesRead)
                }
                outputStream.close()
                inputStream.close()
                outputFile.exists() && outputFile.length() > 512
            } else {
                false
            }
        } catch (_: Exception) {
            false
        }
    }

    private fun loadSavedRepeatMode(): RepeatMode {
        val name = prefs.getString(KEY_REPEAT_MODE, RepeatMode.THREE_TIMES.name)
        return try {
            RepeatMode.valueOf(name ?: RepeatMode.THREE_TIMES.name)
        } catch (e: Exception) {
            RepeatMode.THREE_TIMES
        }
    }

    private fun loadSavedReciter(): ReciterProfile {
        val id = prefs.getString(KEY_SELECTED_RECITER, DEFAULT_RECITER.id)
        return AVAILABLE_RECITERS.firstOrNull { it.id == id } ?: DEFAULT_RECITER
    }

    companion object {
        private const val KEY_REPEAT_MODE = "audio_repeat_mode"
        private const val KEY_SELECTED_RECITER = "audio_selected_reciter"

        val DEFAULT_RECITER = ReciterProfile(
            id = "alafasy",
            name = "Mishary Rashid Alafasy",
            title = "Imam of Grand Mosque of Kuwait",
            cdnIdentifier = "Alafasy_128kbps",
            previewUrl = "https://everyayah.com/data/Alafasy_128kbps/001001.mp3"
        )

        val AVAILABLE_RECITERS = listOf(
            DEFAULT_RECITER,
            ReciterProfile(
                id = "sudais",
                name = "Abdul Rahman Al-Sudais",
                title = "Chief Imam of Masjid al-Haram, Makkah",
                cdnIdentifier = "Abdurrahmaan_As-Sudais_192kbps",
                previewUrl = "https://everyayah.com/data/Abdurrahmaan_As-Sudais_192kbps/001001.mp3"
            ),
            ReciterProfile(
                id = "ghamdi",
                name = "Saad Al-Ghamdi",
                title = "Renowned Saudi Qari & Imam",
                cdnIdentifier = "Ghamadi_40kbps",
                previewUrl = "https://everyayah.com/data/Ghamadi_40kbps/001001.mp3"
            ),
            ReciterProfile(
                id = "muaiqly",
                name = "Maher Al-Muaiqly",
                title = "Imam of Masjid al-Haram, Makkah",
                cdnIdentifier = "Maher_AlMuaiqly_64kbps",
                previewUrl = "https://everyayah.com/data/Maher_AlMuaiqly_64kbps/001001.mp3"
            )
        )

        @Volatile
        private var INSTANCE: SalahAudioPlayer? = null

        fun getInstance(context: Context): SalahAudioPlayer {
            return INSTANCE ?: synchronized(this) {
                val instance = SalahAudioPlayer(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }
}
