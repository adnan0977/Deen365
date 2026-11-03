package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.PlaybackParams
import android.os.Build
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class QuranAudioPlayer(private val context: Context) {
    private val TAG = "QuranAudioPlayer"
    private var mediaPlayer: MediaPlayer? = null

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentPlayingAyah = MutableStateFlow<Int?>(null)
    val currentPlayingAyah: StateFlow<Int?> = _currentPlayingAyah.asStateFlow()

    private var onCompletionCallback: (() -> Unit)? = null
    private var currentSpeed: Float = 1.0f

    fun playAyahAudio(
        url: String,
        ayahNumber: Int,
        speed: Float = currentSpeed,
        onComplete: () -> Unit
    ) {
        currentSpeed = speed
        onCompletionCallback = onComplete

        try {
            stop()
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(url)
                setOnPreparedListener { mp ->
                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            val params = mp.playbackParams
                            params.speed = currentSpeed
                            mp.playbackParams = params
                        }
                        mp.start()
                        _isPlaying.value = true
                        _currentPlayingAyah.value = ayahNumber
                    } catch (e: Exception) {
                        Log.e(TAG, "Error starting playback: ${e.message}")
                    }
                }
                setOnCompletionListener {
                    _isPlaying.value = false
                    _currentPlayingAyah.value = null
                    onCompletionCallback?.invoke()
                }
                setOnErrorListener { _, what, extra ->
                    Log.e(TAG, "MediaPlayer error: what=$what, extra=$extra")
                    _isPlaying.value = false
                    _currentPlayingAyah.value = null
                    true
                }
                prepareAsync()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize player for URL: $url: ${e.message}")
            _isPlaying.value = false
            _currentPlayingAyah.value = null
        }
    }

    fun pause() {
        try {
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.pause()
                _isPlaying.value = false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Pause error: ${e.message}")
        }
    }

    fun resume() {
        try {
            if (mediaPlayer != null && !_isPlaying.value) {
                mediaPlayer?.start()
                _isPlaying.value = true
            }
        } catch (e: Exception) {
            Log.e(TAG, "Resume error: ${e.message}")
        }
    }

    fun stop() {
        try {
            mediaPlayer?.stop()
            mediaPlayer?.reset()
            mediaPlayer?.release()
        } catch (e: Exception) {
            Log.e(TAG, "Stop error: ${e.message}")
        } finally {
            mediaPlayer = null
            _isPlaying.value = false
            _currentPlayingAyah.value = null
        }
    }

    fun setSpeed(speed: Float) {
        currentSpeed = speed
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && mediaPlayer != null && _isPlaying.value) {
                val params = mediaPlayer?.playbackParams ?: PlaybackParams()
                params.speed = speed
                mediaPlayer?.playbackParams = params
            }
        } catch (e: Exception) {
            Log.e(TAG, "Set speed error: ${e.message}")
        }
    }

    fun shutdown() {
        stop()
    }
}
