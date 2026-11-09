package com.example.data.model

/**
 * Makkah & Madinah 24/7 Live Haramain Streams Models.
 */

enum class HolyMosque {
    MAKKAH_HARAM,
    MADINAH_NABAWI
}

data class HaramainChannel(
    val id: String,
    val mosque: HolyMosque,
    val title: String,
    val arabicTitle: String,
    val channelName: String,
    val broadcastContent: String,
    val description: String,
    val streamWebUrl: String,
    val backupUrl: String,
    val historicalVirtue: String
)

data class HaramainLiveState(
    val selectedMosque: HolyMosque = HolyMosque.MAKKAH_HARAM,
    val isPlaying: Boolean = true,
    val isMuted: Boolean = false,
    val makkahTimeOffsetHours: Int = 3, // UTC+3 Makkah Standard Time
    val lastKnownPrayerName: String = "Isha",
    val activeViewersCount: String = "84.2K Live"
)

object HaramainLiveData {

    val CHANNELS = listOf(
        HaramainChannel(
            id = "makkah_live",
            mosque = HolyMosque.MAKKAH_HARAM,
            title = "Makkah 24/7 Live",
            arabicTitle = "بَثّ مُبَاشِر مِن الْمَسْجِد الْحَرَام",
            channelName = "Saudi Quran TV",
            broadcastContent = "Live Tawaf around the Holy Kaaba & Salah with Quran Recitation",
            description = "Continuous 24/7 broadcast from the Grand Mosque in Makkah al-Mukarramah. Experience the spiritual ambiance of the Kaaba, the Black Stone, and prayers led by the Imams of the Haram.",
            streamWebUrl = "https://www.youtube.com/watch?v=mohhqVz47C8",
            backupUrl = "https://makkahlive.net/",
            historicalVirtue = "One prayer in Masjid al-Haram is better than 100,000 prayers elsewhere (Sunan Ibn Majah #1406)."
        ),
        HaramainChannel(
            id = "madinah_live",
            mosque = HolyMosque.MADINAH_NABAWI,
            title = "Madinah 24/7 Live",
            arabicTitle = "بَثّ مُبَاشِر مِن الْمَسْجِد النَّبَوِيّ",
            channelName = "Saudi Sunnah TV",
            broadcastContent = "Prophet's Mosque Rawdah ash-Sharifah & Green Dome with Hadith Broadcast",
            description = "Continuous 24/7 broadcast from the Prophet's Mosque in Al-Madinah al-Munawwarah. View the Green Dome, the Prophet's Minbar, and send peace upon the Messenger of Allah (ﷺ).",
            streamWebUrl = "https://www.youtube.com/watch?v=J3e3yY2qYwA",
            backupUrl = "https://madinahlive.net/",
            historicalVirtue = "One prayer in my mosque is better than a thousand prayers elsewhere except Masjid al-Haram (Sahih Bukhari #1190)."
        )
    )

    fun getChannel(mosque: HolyMosque): HaramainChannel {
        return CHANNELS.firstOrNull { it.mosque == mosque } ?: CHANNELS[0]
    }
}
