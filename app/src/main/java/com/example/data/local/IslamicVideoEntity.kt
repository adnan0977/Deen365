package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "islamic_videos")
data class IslamicVideoEntity(
    @PrimaryKey
    val id: String, // YouTube Video ID (e.g. "wV5-a1cM6f0")
    val title: String,
    val speakerName: String,
    val topicName: String,
    val duration: String,
    val channelTitle: String,
    val youtubeUrl: String,
    val description: String,
    val thumbnailUrl: String = "https://img.youtube.com/vi/$id/hqdefault.jpg",
    val isCustomAdded: Boolean = false,
    val viewCountFormatted: String = "120K views",
    val dateAdded: Long = System.currentTimeMillis()
)
