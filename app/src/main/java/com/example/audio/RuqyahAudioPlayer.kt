package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.PlaybackParams
import android.os.Build
import android.util.Log
import com.example.data.model.RuqyahAudioCatalog
import com.example.data.model.RuqyahAudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * Robust, lifecycle-safe Audio Streaming Engine for Ruqyah Shariah recitations.
 * Supports remote HTTP/HTTPS streaming, live buffering updates, seeking, playback rate,
 * and track advancement.
 */
class RuqyahAudioPlayer(private val context: Context) {
    private val TAG = "RuqyahAudioPlayer"
    private val playerScope = CoroutineScope(Dispatchers.Main + Job())

    private var mediaPlayer: MediaPlayer? = null
    private var progressTrackerJob: Job? = null

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _isBuffering = MutableStateFlow(false)
    val isBuffering: StateFlow<Boolean> = _isBuffering.asStateFlow()

    private val _currentPositionMs = MutableStateFlow(0L)
    val currentPositionMs: StateFlow<Long> = _currentPositionMs.asStateFlow()

    private val _durationMs = MutableStateFlow(0L)
    val durationMs: StateFlow<Long> = _durationMs.asStateFlow()

    private val _currentTrack = MutableStateFlow<RuqyahAudioTrack?>(RuqyahAudioCatalog.CURATED_TRACKS.firstOrNull())
    val currentTrack: StateFlow<RuqyahAudioTrack?> = _currentTrack.asStateFlow()

    private val _playbackSpeed = MutableStateFlow(1.0f)
    val playbackSpeed: StateFlow<Float> = _playbackSpeed.asStateFlow()

    private val _isLooping = MutableStateFlow(false)
    val isLooping: StateFlow<Boolean> = _isLooping.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun playTrack(track: RuqyahAudioTrack) {
        _currentTrack.value = track
        streamRemoteUrl(track.audioUrl)
    }

    fun streamRemoteUrl(url: String, title: String? = null, reciter: String? = null) {
        if (title != null && reciter != null) {
            _currentTrack.value = RuqyahAudioTrack(
                id = "custom_${System.currentTimeMillis()}",
                title = title,
                reciter = reciter,
                description = "Custom streaming Ruqyah audio link",
                audioUrl = url,
                durationLabel = "Stream"
            )
        }

        _errorMessage.value = null
        _isBuffering.value = true
        _currentPositionMs.value = 0L

        try {
            stopInternal()

            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(url)
                isLooping = _isLooping.value

                setOnPreparedListener { mp ->
                    _isBuffering.value = false
                    val dur = mp.duration.toLong().coerceAtLeast(0L)
                    _durationMs.value = dur

                    applySpeed(mp, _playbackSpeed.value)
                    mp.start()
                    _isPlaying.value = true
                    startProgressTracker()
                    Log.d(TAG, "Ruqyah audio playback started: duration=$dur ms")
                }

                setOnCompletionListener {
                    _isPlaying.value = false
                    _currentPositionMs.value = _durationMs.value
                    stopProgressTracker()
                    if (!_isLooping.value) {
                        playNextTrack()
                    }
                }

                setOnBufferingUpdateListener { _, percent ->
                    if (percent < 100 && !_isPlaying.value) {
                        _isBuffering.value = true
                    } else if (_isPlaying.value) {
                        _isBuffering.value = false
                    }
                }

                setOnErrorListener { _, what, extra ->
                    Log.e(TAG, "MediaPlayer error: what=$what, extra=$extra")
                    _isBuffering.value = false
                    _isPlaying.value = false
                    _errorMessage.value = "Failed to stream audio (Error $what:$extra). Please check your internet connection."
                    stopProgressTracker()
                    true
                }

                prepareAsync()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error initiating remote playback for URL: $url: ${e.message}", e)
            _isBuffering.value = false
            _isPlaying.value = false
            _errorMessage.value = "Could not stream audio: ${e.message}"
        }
    }

    fun playOrPause() {
        if (_isPlaying.value) {
            pause()
        } else {
            resume()
        }
    }

    fun pause() {
        try {
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.pause()
                _isPlaying.value = false
                stopProgressTracker()
                _currentPositionMs.value = mediaPlayer?.currentPosition?.toLong() ?: _currentPositionMs.value
            }
        } catch (e: Exception) {
            Log.e(TAG, "Pause error: ${e.message}")
        }
    }

    fun resume() {
        try {
            if (mediaPlayer != null) {
                mediaPlayer?.start()
                _isPlaying.value = true
                _errorMessage.value = null
                startProgressTracker()
            } else {
                _currentTrack.value?.let { playTrack(it) }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Resume error: ${e.message}")
            _currentTrack.value?.let { playTrack(it) }
        }
    }

    fun seekTo(positionMs: Long) {
        val target = positionMs.coerceIn(0L, _durationMs.value.coerceAtLeast(1L))
        _currentPositionMs.value = target
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mediaPlayer?.seekTo(target, MediaPlayer.SEEK_CLOSEST)
            } else {
                mediaPlayer?.seekTo(target.toInt())
            }
        } catch (e: Exception) {
            Log.e(TAG, "Seek error: ${e.message}")
        }
    }

    fun skipForward(seconds: Int = 10) {
        val next = (_currentPositionMs.value + (seconds * 1000L)).coerceAtMost(_durationMs.value)
        seekTo(next)
    }

    fun skipBackward(seconds: Int = 10) {
        val prev = (_currentPositionMs.value - (seconds * 1000L)).coerceAtLeast(0L)
        seekTo(prev)
    }

    fun setSpeed(speed: Float) {
        _playbackSpeed.value = speed
        mediaPlayer?.let { applySpeed(it, speed) }
    }

    fun toggleLooping() {
        val newLoop = !_isLooping.value
        _isLooping.value = newLoop
        try {
            mediaPlayer?.isLooping = newLoop
        } catch (e: Exception) {
            Log.e(TAG, "Loop toggle error: ${e.message}")
        }
    }

    fun playNextTrack() {
        val tracks = RuqyahAudioCatalog.CURATED_TRACKS
        val currentIndex = tracks.indexOfFirst { it.id == _currentTrack.value?.id }
        val nextIndex = if (currentIndex != -1 && currentIndex + 1 < tracks.size) currentIndex + 1 else 0
        playTrack(tracks[nextIndex])
    }

    fun playPreviousTrack() {
        val tracks = RuqyahAudioCatalog.CURATED_TRACKS
        val currentIndex = tracks.indexOfFirst { it.id == _currentTrack.value?.id }
        val prevIndex = if (currentIndex > 0) currentIndex - 1 else tracks.size - 1
        playTrack(tracks[prevIndex])
    }

    private fun applySpeed(mp: MediaPlayer, speed: Float) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val params = mp.playbackParams ?: PlaybackParams()
                params.speed = speed
                mp.playbackParams = params
            }
        } catch (e: Exception) {
            Log.w(TAG, "Could not set playback speed to $speed: ${e.message}")
        }
    }

    private fun startProgressTracker() {
        stopProgressTracker()
        progressTrackerJob = playerScope.launch {
            while (isActive && _isPlaying.value) {
                try {
                    val pos = mediaPlayer?.currentPosition?.toLong() ?: 0L
                    _currentPositionMs.value = pos
                    val dur = mediaPlayer?.duration?.toLong() ?: _durationMs.value
                    if (dur > 0L) {
                        _durationMs.value = dur
                    }
                } catch (_: Exception) {}
                delay(250L)
            }
        }
    }

    private fun stopProgressTracker() {
        progressTrackerJob?.cancel()
        progressTrackerJob = null
    }

    private fun stopInternal() {
        stopProgressTracker()
        try {
            mediaPlayer?.stop()
            mediaPlayer?.reset()
            mediaPlayer?.release()
        } catch (e: Exception) {
            Log.w(TAG, "Stop error: ${e.message}")
        } finally {
            mediaPlayer = null
            _isPlaying.value = false
        }
    }

    fun stop() {
        stopInternal()
        _currentPositionMs.value = 0L
        _isBuffering.value = false
        _errorMessage.value = null
    }

    fun shutdown() {
        stop()
    }

    companion object {
        fun formatDuration(millis: Long): String {
            val totalSeconds = (millis / 1000).coerceAtLeast(0L)
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60
            val hours = minutes / 60
            return if (hours > 0) {
                String.format(java.util.Locale.US, "%d:%02d:%02d", hours, minutes % 60, seconds)
            } else {
                String.format(java.util.Locale.US, "%02d:%02d", minutes, seconds)
            }
        }
    }
}
