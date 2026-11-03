package com.example.data.repository

import android.content.Context
import com.example.data.audio.ReciterProfile
import com.example.data.audio.SalahAudioPlayer
import com.example.data.local.NamazAzanEntity
import com.example.data.local.NamazDao
import com.example.data.local.NamazPrayerEntity
import com.example.data.local.NamazStepEntity
import com.example.data.seed.DetailedPrayerPlan
import com.example.data.seed.NamazPrayerProcessData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Locale

data class NamazDownloadProgress(
    val prayerId: String,
    val progress: Float, // 0.0f to 1.0f
    val isDownloading: Boolean = false,
    val statusMessage: String = "",
    val error: String? = null
)

class NamazOfflineRepository(
    private val namazDao: NamazDao,
    private val salahAudioPlayer: SalahAudioPlayer,
    private val context: Context
) {

    private val _downloadStates = MutableStateFlow<Map<String, NamazDownloadProgress>>(emptyMap())
    val downloadStates: StateFlow<Map<String, NamazDownloadProgress>> = _downloadStates.asStateFlow()

    val downloadedPrayerIds: Flow<List<String>> = namazDao.getDownloadedPrayerIds()
    val downloadedCount: Flow<Int> = namazDao.getDownloadedCount()
    val totalCachedSizeBytes: Flow<Long?> = namazDao.getTotalCachedSizeBytes()

    fun getPrayer(prayerId: String): Flow<NamazPrayerEntity?> = namazDao.getPrayer(prayerId)
    fun getPrayerSteps(prayerId: String): Flow<List<NamazStepEntity>> = namazDao.getPrayerSteps(prayerId)
    fun getPrayerAzan(prayerId: String): Flow<NamazAzanEntity?> = namazDao.getPrayerAzan(prayerId)

    suspend fun isPrayerDownloaded(prayerId: String): Boolean {
        return withContext(Dispatchers.IO) {
            namazDao.checkIsPrayerDownloaded(prayerId) > 0
        }
    }

    /**
     * Automatically caches the prayer plan in Room DB and downloads reciter audio files locally.
     * Guaranteed non-blocking background execution with granular progress updates.
     */
    suspend fun cachePrayerForOffline(
        prayerId: String,
        reciter: ReciterProfile = salahAudioPlayer.playbackState.value.selectedReciter
    ): Boolean = withContext(Dispatchers.IO) {
        val prayerPlan = NamazPrayerProcessData.PRAYERS.firstOrNull { it.id.equals(prayerId, ignoreCase = true) }
            ?: return@withContext false

        updateDownloadState(prayerId, 0.05f, true, "Saving ${prayerPlan.name} text & steps into Room DB...")

        try {
            // 1. Convert and save Prayer Entity in Room DB
            val prayerEntity = NamazPrayerEntity(
                id = prayerPlan.id,
                name = prayerPlan.name,
                arabicTitle = prayerPlan.arabicTitle,
                rakatsSummary = prayerPlan.rakatsSummary,
                totalRakats = prayerPlan.totalRakats,
                recitationRule = prayerPlan.recitationRule,
                bestTime = prayerPlan.bestTime,
                hadithVirtue = prayerPlan.hadithVirtue,
                isDownloaded = false,
                downloadTimestamp = 0L,
                cachedSizeBytes = 0L,
                stepCount = prayerPlan.steps.size
            )
            namazDao.insertPrayer(prayerEntity)

            // 2. Convert and save all Prayer Steps in Room DB
            val stepEntities = prayerPlan.steps.mapIndexed { idx, step ->
                NamazStepEntity(
                    id = "${prayerPlan.id}_$idx",
                    prayerId = prayerPlan.id,
                    stepIndex = step.stepIndex,
                    title = step.title,
                    arabicName = step.arabicName,
                    posture = step.posture,
                    arabicRecitation = step.arabicRecitation,
                    transliteration = step.transliteration,
                    englishTranslation = step.englishTranslation,
                    repetitionGuide = step.repetitionGuide,
                    audioId = step.audioId,
                    notes = step.notes,
                    audioLocalPath = null,
                    isAudioDownloaded = false
                )
            }
            namazDao.insertSteps(stepEntities)

            // 3. Convert and save Azan Data in Room DB
            val azanEntity = NamazAzanEntity(
                prayerId = prayerPlan.id,
                prayerName = prayerPlan.azan.prayerName,
                isFajr = prayerPlan.azan.isFajr,
                audioId = prayerPlan.azan.audioId,
                phrasesJson = NamazAzanEntity.phrasesToJson(prayerPlan.azan.phrases),
                duaAfterAzanArabic = prayerPlan.azan.duaAfterAzanArabic,
                duaAfterAzanTransliteration = prayerPlan.azan.duaAfterAzanTransliteration,
                duaAfterAzanTranslation = prayerPlan.azan.duaAfterAzanTranslation,
                audioLocalPath = null,
                isAudioDownloaded = false
            )
            namazDao.insertAzan(azanEntity)

            updateDownloadState(prayerId, 0.20f, true, "Room DB saved. Pre-downloading audio recitations...")

            // 4. Download / Verify Audio for Steps and Azan
            var totalBytes: Long = 0
            val totalItems = stepEntities.size + 1 // steps + 1 azan audio
            var itemsCompleted = 0

            // Azan audio
            val azanTargetFile = salahAudioPlayer.getCachedAudioFile(azanEntity.audioId, reciter.id)
            if (azanTargetFile.exists() && azanTargetFile.length() > 1024) {
                totalBytes += azanTargetFile.length()
                namazDao.updateAzanAudioPath(prayerPlan.id, azanTargetFile.absolutePath, true)
            } else {
                val streamUrl = salahAudioPlayer.getCdnStreamUrl(azanEntity.audioId, reciter)
                val success = salahAudioPlayer.downloadAudioDirectly(streamUrl, azanTargetFile)
                if (success && azanTargetFile.exists()) {
                    totalBytes += azanTargetFile.length()
                    namazDao.updateAzanAudioPath(prayerPlan.id, azanTargetFile.absolutePath, true)
                }
            }
            itemsCompleted++
            val azanFraction = 0.20f + (itemsCompleted.toFloat() / totalItems.toFloat()) * 0.75f
            updateDownloadState(
                prayerId,
                azanFraction,
                true,
                "Downloaded Azan audio (1 of $totalItems)"
            )

            // Steps audio
            for ((idx, step) in stepEntities.withIndex()) {
                val audioFile = salahAudioPlayer.getCachedAudioFile(step.audioId, reciter.id)
                var isDownloaded = false
                if (audioFile.exists() && audioFile.length() > 1024) {
                    totalBytes += audioFile.length()
                    isDownloaded = true
                    namazDao.updateStepAudioPath(step.id, audioFile.absolutePath, true)
                } else {
                    val streamUrl = salahAudioPlayer.getCdnStreamUrl(step.audioId, reciter)
                    val success = salahAudioPlayer.downloadAudioDirectly(streamUrl, audioFile)
                    if (success && audioFile.exists()) {
                        totalBytes += audioFile.length()
                        isDownloaded = true
                        namazDao.updateStepAudioPath(step.id, audioFile.absolutePath, true)
                    }
                }

                itemsCompleted++
                val fraction = 0.20f + (itemsCompleted.toFloat() / totalItems.toFloat()) * 0.75f
                val readableSize = formatStorageSize(totalBytes)
                updateDownloadState(
                    prayerId,
                    fraction,
                    true,
                    "Caching ${step.title} ($itemsCompleted of $totalItems • $readableSize)..."
                )
            }

            // 5. Mark as fully downloaded in Room DB
            namazDao.markPrayerDownloaded(
                prayerId = prayerPlan.id,
                isDownloaded = true,
                timestamp = System.currentTimeMillis(),
                sizeBytes = totalBytes
            )

            val finalReadableSize = formatStorageSize(totalBytes)
            updateDownloadState(
                prayerId = prayerId,
                progress = 1.0f,
                isDownloading = false,
                statusMessage = "Offline Ready • $finalReadableSize cached in Room DB"
            )
            true
        } catch (e: Exception) {
            updateDownloadState(
                prayerId = prayerId,
                progress = 0f,
                isDownloading = false,
                statusMessage = "Error saving offline: ${e.message}",
                error = e.localizedMessage
            )
            false
        }
    }

    suspend fun downloadAllPrayers(reciter: ReciterProfile = salahAudioPlayer.playbackState.value.selectedReciter) {
        withContext(Dispatchers.IO) {
            for (prayer in NamazPrayerProcessData.PRAYERS) {
                cachePrayerForOffline(prayer.id, reciter)
            }
        }
    }

    suspend fun clearPrayerCache(prayerId: String) = withContext(Dispatchers.IO) {
        namazDao.resetPrayerDownloaded(prayerId)
        val current = _downloadStates.value.toMutableMap()
        current.remove(prayerId)
        _downloadStates.value = current
    }

    suspend fun clearAllNamazCache() = withContext(Dispatchers.IO) {
        namazDao.resetAllPrayersDownloaded()
        _downloadStates.value = emptyMap()
    }

    private fun updateDownloadState(
        prayerId: String,
        progress: Float,
        isDownloading: Boolean,
        statusMessage: String,
        error: String? = null
    ) {
        val current = _downloadStates.value.toMutableMap()
        current[prayerId] = NamazDownloadProgress(
            prayerId = prayerId,
            progress = progress,
            isDownloading = isDownloading,
            statusMessage = statusMessage,
            error = error
        )
        _downloadStates.value = current
    }

    companion object {
        fun formatStorageSize(bytes: Long): String {
            if (bytes <= 0) return "0 KB"
            val kb = bytes / 1024.0
            val mb = kb / 1024.0
            return if (mb >= 1.0) {
                String.format(Locale.US, "%.1f MB", mb)
            } else {
                String.format(Locale.US, "%.0f KB", kb)
            }
        }
    }
}
