package com.example.data.local

object IslamicVideoSeedData {
    val SPEAKERS = listOf(
        "All Speakers",
        "Mufti Menk",
        "Nouman Ali Khan",
        "Omar Suleiman",
        "Dr. Yasir Qadhi",
        "Dr. Bilal Philips",
        "Dr. Zakir Naik"
    )

    val TOPICS = listOf(
        "All Topics",
        "Tazkiyah & Heart",
        "Quran & Reflection",
        "Salah & Khushoo",
        "Seerah of the Prophet",
        "Duas & Repentance",
        "Youth & Modern Life"
    )

    val INITIAL_VIDEOS = listOf(
        // Mufti Menk
        IslamicVideoEntity(
            id = "B5x380JmD4A",
            title = "Curing Anxiety, Depression and Overthinking with Quran",
            speakerName = "Mufti Menk",
            topicName = "Tazkiyah & Heart",
            duration = "22:15",
            channelTitle = "Mufti Menk Official",
            youtubeUrl = "https://www.youtube.com/watch?v=B5x380JmD4A",
            description = "A deeply comforting lecture on how remembrance of Allah (Dhikr) and reliance upon His wisdom heals a wounded heart and alleviates mental distress.",
            thumbnailUrl = "https://img.youtube.com/vi/B5x380JmD4A/hqdefault.jpg",
            isCustomAdded = false,
            viewCountFormatted = "850K views"
        ),
        IslamicVideoEntity(
            id = "wV5-a1cM6f0",
            title = "The Power of Tahajjud and Sincere Repentance",
            speakerName = "Mufti Menk",
            topicName = "Duas & Repentance",
            duration = "18:40",
            channelTitle = "Mufti Menk Official",
            youtubeUrl = "https://www.youtube.com/watch?v=wV5-a1cM6f0",
            description = "Discover the immense spiritual station of night prayer (Tahajjud) and why Allah descends to the lowest heaven asking who seeks forgiveness.",
            thumbnailUrl = "https://img.youtube.com/vi/wV5-a1cM6f0/hqdefault.jpg",
            isCustomAdded = false,
            viewCountFormatted = "1.2M views"
        ),
        IslamicVideoEntity(
            id = "7E-CRm0i9QY",
            title = "Protecting Your Home and Family in Turbulent Times",
            speakerName = "Mufti Menk",
            topicName = "Youth & Modern Life",
            duration = "29:12",
            channelTitle = "Light Upon Light",
            youtubeUrl = "https://www.youtube.com/watch?v=7E-CRm0i9QY",
            description = "Practical wisdom on cultivating mercy, gentleness, and moral courage within the household amidst modern distractions and digital pressures.",
            thumbnailUrl = "https://img.youtube.com/vi/7E-CRm0i9QY/hqdefault.jpg",
            isCustomAdded = false,
            viewCountFormatted = "430K views"
        ),

        // Nouman Ali Khan
        IslamicVideoEntity(
            id = "qjJd7YqR51k",
            title = "Surah Al-Kahf: Unlocking Hidden Meanings and Lessons",
            speakerName = "Nouman Ali Khan",
            topicName = "Quran & Reflection",
            duration = "34:50",
            channelTitle = "Bayyinah Institute",
            youtubeUrl = "https://www.youtube.com/watch?v=qjJd7YqR51k",
            description = "A linguistic and thematic analysis of the four parables of Surah Al-Kahf and their vital protection against modern Dajjalic trials.",
            thumbnailUrl = "https://img.youtube.com/vi/qjJd7YqR51k/hqdefault.jpg",
            isCustomAdded = false,
            viewCountFormatted = "920K views"
        ),
        IslamicVideoEntity(
            id = "8p6tXl_B1Qo",
            title = "Why Do We Lose Focus in Salah? Finding True Khushoo",
            speakerName = "Nouman Ali Khan",
            topicName = "Salah & Khushoo",
            duration = "24:30",
            channelTitle = "Bayyinah Institute",
            youtubeUrl = "https://www.youtube.com/watch?v=8p6tXl_B1Qo",
            description = "Exploring the psychology of prayer and understanding each word of Al-Fatiha to transform ritual into an intimate conversation with your Creator.",
            thumbnailUrl = "https://img.youtube.com/vi/8p6tXl_B1Qo/hqdefault.jpg",
            isCustomAdded = false,
            viewCountFormatted = "1.5M views"
        ),
        IslamicVideoEntity(
            id = "N7v_R8hF5Xw",
            title = "How the Quran Speaks Directly to Your Doubts",
            speakerName = "Nouman Ali Khan",
            topicName = "Quran & Reflection",
            duration = "21:18",
            channelTitle = "Bayyinah Institute",
            youtubeUrl = "https://www.youtube.com/watch?v=N7v_R8hF5Xw",
            description = "A compassionate explanation of how questioning is natural and how Allah addresses existential curiosities with divine reason and eloquence.",
            thumbnailUrl = "https://img.youtube.com/vi/N7v_R8hF5Xw/hqdefault.jpg",
            isCustomAdded = false,
            viewCountFormatted = "610K views"
        ),

        // Omar Suleiman
        IslamicVideoEntity(
            id = "d8kO9sP_gL4",
            title = "Angels in Your Presence: What Happens When You Make Dua",
            speakerName = "Omar Suleiman",
            topicName = "Duas & Repentance",
            duration = "26:45",
            channelTitle = "Yaqeen Institute",
            youtubeUrl = "https://www.youtube.com/watch?v=d8kO9sP_gL4",
            description = "An inspiring exploration of the unseen world: how angels surround gatherings of Dhikr and say Ameen to the sincere supplication of believers.",
            thumbnailUrl = "https://img.youtube.com/vi/d8kO9sP_gL4/hqdefault.jpg",
            isCustomAdded = false,
            viewCountFormatted = "1.1M views"
        ),
        IslamicVideoEntity(
            id = "Yp8f6N3_R9k",
            title = "The Firsts: Companions Who Sacrificed Everything for Islam",
            speakerName = "Omar Suleiman",
            topicName = "Seerah of the Prophet",
            duration = "38:20",
            channelTitle = "Yaqeen Institute",
            youtubeUrl = "https://www.youtube.com/watch?v=Yp8f6N3_R9k",
            description = "Chronicling the lives of early Sahaba, their steadfastness under torture in Makkah, and their unshakable love for Prophet Muhammad (ﷺ).",
            thumbnailUrl = "https://img.youtube.com/vi/Yp8f6N3_R9k/hqdefault.jpg",
            isCustomAdded = false,
            viewCountFormatted = "780K views"
        ),
        IslamicVideoEntity(
            id = "mX2c8B9_wL4",
            title = "Cleansing the Heart from Envy, Arrogance, and Malice",
            speakerName = "Omar Suleiman",
            topicName = "Tazkiyah & Heart",
            duration = "23:10",
            channelTitle = "Yaqeen Institute",
            youtubeUrl = "https://www.youtube.com/watch?v=mX2c8B9_wL4",
            description = "Diagnosing spiritual diseases of the heart according to Imam Al-Ghazali and the Quranic remedies to achieve a Sound Heart (Qalb Saleem).",
            thumbnailUrl = "https://img.youtube.com/vi/mX2c8B9_wL4/hqdefault.jpg",
            isCustomAdded = false,
            viewCountFormatted = "540K views"
        ),

        // Dr. Yasir Qadhi
        IslamicVideoEntity(
            id = "vo_C8dF6B5g",
            title = "Detailed Seerah: The Miraculous Night of Al-Isra wal-Miraj",
            speakerName = "Dr. Yasir Qadhi",
            topicName = "Seerah of the Prophet",
            duration = "52:14",
            channelTitle = "Yasir Qadhi Lectures",
            youtubeUrl = "https://www.youtube.com/watch?v=vo_C8dF6B5g",
            description = "An authentic historical breakdown of the heavenly journey of Prophet Muhammad (ﷺ), meeting past prophets, and the gift of the five daily prayers.",
            thumbnailUrl = "https://img.youtube.com/vi/vo_C8dF6B5g/hqdefault.jpg",
            isCustomAdded = false,
            viewCountFormatted = "1.8M views"
        ),
        IslamicVideoEntity(
            id = "k9H3m2_P7rU",
            title = "The Architecture of Salah: The Meaning Behind Every Position",
            speakerName = "Dr. Yasir Qadhi",
            topicName = "Salah & Khushoo",
            duration = "31:05",
            channelTitle = "Yasir Qadhi Lectures",
            youtubeUrl = "https://www.youtube.com/watch?v=k9H3m2_P7rU",
            description = "Why do we bow? Why do we place our face upon the dust in Sujood? A theological and spiritual unveiling of the physical dimensions of prayer.",
            thumbnailUrl = "https://img.youtube.com/vi/k9H3m2_P7rU/hqdefault.jpg",
            isCustomAdded = false,
            viewCountFormatted = "950K views"
        ),

        // Dr. Bilal Philips
        IslamicVideoEntity(
            id = "eF1_m9k8L4g",
            title = "Tawheed: The Absolute Foundation of Faith and Purpose",
            speakerName = "Dr. Bilal Philips",
            topicName = "Tazkiyah & Heart",
            duration = "35:40",
            channelTitle = "Islamic Online University",
            youtubeUrl = "https://www.youtube.com/watch?v=eF1_m9k8L4g",
            description = "Understanding the three categories of Tawheed (Ruboobiyyah, Uloohiyyah, Asma wa Sifat) and freeing the modern mind from subtle forms of Shirk.",
            thumbnailUrl = "https://img.youtube.com/vi/eF1_m9k8L4g/hqdefault.jpg",
            isCustomAdded = false,
            viewCountFormatted = "320K views"
        ),

        // Dr. Zakir Naik
        IslamicVideoEntity(
            id = "p9L4k2_wX8e",
            title = "Scientific Miracles in the Holy Quran Explained",
            speakerName = "Dr. Zakir Naik",
            topicName = "Quran & Reflection",
            duration = "44:18",
            channelTitle = "Peace TV",
            youtubeUrl = "https://www.youtube.com/watch?v=p9L4k2_wX8e",
            description = "Examining Quranic verses discussing cosmology, embryology, oceanography, and geology revealed over 1400 years ago.",
            thumbnailUrl = "https://img.youtube.com/vi/p9L4k2_wX8e/hqdefault.jpg",
            isCustomAdded = false,
            viewCountFormatted = "2.4M views"
        )
    )
}
