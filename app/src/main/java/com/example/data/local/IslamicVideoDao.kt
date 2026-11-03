package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface IslamicVideoDao {
    @Query("SELECT * FROM islamic_videos ORDER BY isCustomAdded DESC, dateAdded DESC")
    fun getAllVideos(): Flow<List<IslamicVideoEntity>>

    @Query("SELECT * FROM islamic_videos WHERE speakerName = :speaker ORDER BY dateAdded DESC")
    fun getVideosBySpeaker(speaker: String): Flow<List<IslamicVideoEntity>>

    @Query("SELECT * FROM islamic_videos WHERE topicName = :topic ORDER BY dateAdded DESC")
    fun getVideosByTopic(topic: String): Flow<List<IslamicVideoEntity>>

    @Query("SELECT * FROM islamic_videos WHERE title LIKE '%' || :query || '%' OR speakerName LIKE '%' || :query || '%' OR topicName LIKE '%' || :query || '%'")
    fun searchVideos(query: String): Flow<List<IslamicVideoEntity>>

    @Query("SELECT * FROM islamic_videos WHERE id = :id LIMIT 1")
    suspend fun getVideoById(id: String): IslamicVideoEntity?

    @Query("SELECT COUNT(*) FROM islamic_videos")
    suspend fun getVideoCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideo(video: IslamicVideoEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideos(videos: List<IslamicVideoEntity>)

    @Query("DELETE FROM islamic_videos WHERE id = :id")
    suspend fun deleteVideoById(id: String)
}
