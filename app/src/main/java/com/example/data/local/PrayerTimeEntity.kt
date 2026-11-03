package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_prayer_times")
data class PrayerTimeEntity(
    @PrimaryKey val id: String, // e.g. "YYYY-MM-DD-lat-lng"
    val dateStr: String,
    val latitude: Double,
    val longitude: Double,
    val cityName: String,
    val calculationMethod: String,
    val fajr: String,
    val sunrise: String,
    val dhuhr: String,
    val asr: String,
    val maghrib: String,
    val isha: String,
    val suhoorEnd: String,
    val iftar: String,
    val updatedAt: Long = System.currentTimeMillis()
)
