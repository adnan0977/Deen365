package com.example.data.repository

import com.example.data.local.IslamicVideoDao
import com.example.data.local.IslamicVideoEntity
import com.example.data.local.IslamicVideoSeedData
import com.example.data.local.UserHabitDao
import com.example.data.local.UserHabitEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.regex.Pattern

class IslamicVideoRepository(
    private val videoDao: IslamicVideoDao,
    private val habitDao: UserHabitDao,
    private val scope: CoroutineScope
) {
    init {
        scope.launch {
            if (videoDao.getVideoCount() == 0) {
                videoDao.insertVideos(IslamicVideoSeedData.INITIAL_VIDEOS)
            }
            if (habitDao.getUserHabitSync() == null) {
                habitDao.saveUserHabit(UserHabitEntity())
            }
        }
    }

    fun getAllVideos(): Flow<List<IslamicVideoEntity>> = videoDao.getAllVideos()

    fun getVideosBySpeaker(speaker: String): Flow<List<IslamicVideoEntity>> {
        return if (speaker == "All Speakers" || speaker.isBlank()) {
            videoDao.getAllVideos()
        } else {
            videoDao.getVideosBySpeaker(speaker)
        }
    }

    fun getVideosByTopic(topic: String): Flow<List<IslamicVideoEntity>> {
        return if (topic == "All Topics" || topic.isBlank()) {
            videoDao.getAllVideos()
        } else {
            videoDao.getVideosByTopic(topic)
        }
    }

    fun searchVideos(query: String): Flow<List<IslamicVideoEntity>> {
        return videoDao.searchVideos(query)
    }

    suspend fun addVideo(video: IslamicVideoEntity) {
        videoDao.insertVideo(video)
    }

    suspend fun deleteVideo(id: String) {
        videoDao.deleteVideoById(id)
    }

    fun getUserHabitFlow(): Flow<UserHabitEntity?> = habitDao.getUserHabitFlow()

    suspend fun saveUserHabit(habit: UserHabitEntity) {
        habitDao.saveUserHabit(habit)
    }

    suspend fun recordVideoWatched(videoId: String, minutes: Int = 15) {
        val current = habitDao.getUserHabitSync() ?: UserHabitEntity()
        val currentSet = current.getWatchedIdsSet().toMutableSet()
        currentSet.add(videoId)
        val updated = current.copy(
            watchedVideoIdsCsv = currentSet.joinToString(","),
            totalWatchMinutes = current.totalWatchMinutes + minutes
        )
        habitDao.saveUserHabit(updated)
    }

    companion object {
        fun extractYouTubeVideoId(input: String): String? {
            val clean = input.trim()
            if (clean.length in 9..14 && !clean.contains("/") && !clean.contains("?")) {
                return clean
            }
            val patterns = listOf(
                Pattern.compile("(?:v=|/v/|youtu\\.be/|/embed/|/live/|/shorts/)([a-zA-Z0-9_-]{11})"),
                Pattern.compile("^([a-zA-Z0-9_-]{11})$")
            )
            for (p in patterns) {
                val matcher = p.matcher(clean)
                if (matcher.find()) {
                    return matcher.group(1)
                }
            }
            return null
        }
    }
}
