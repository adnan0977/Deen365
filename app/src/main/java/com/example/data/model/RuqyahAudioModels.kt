package com.example.data.model

/**
 * Ruqyah Shariah Remote Audio Stream Models.
 * Provides authentic, high-quality Quranic healing audio recitations by renowned Qaris.
 */
data class RuqyahAudioTrack(
    val id: String,
    val title: String,
    val reciter: String,
    val description: String,
    val audioUrl: String,
    val durationLabel: String,
    val category: RuqyahCategory = RuqyahCategory.ALL
)

object RuqyahAudioCatalog {

    val CURATED_TRACKS = listOf(
        RuqyahAudioTrack(
            id = "ruqyah_full_mishary",
            title = "Complete Ruqyah Shariah (Comprehensive Healing)",
            reciter = "Sheikh Mishary Rashid Alafasy",
            description = "Complete prophetic Ruqyah recitation for physical ailments, evil eye, and spiritual peace.",
            audioUrl = "https://download.quranicaudio.com/quran/mishaari_raashid_al_3afaasee/001.mp3",
            durationLabel = "Full Ruqyah",
            category = RuqyahCategory.ALL
        ),
        RuqyahAudioTrack(
            id = "ruqyah_shifa_ghamdi",
            title = "Ayat ash-Shifa & Verses of Physical Cure",
            reciter = "Sheikh Saad Al-Ghamdi",
            description = "Specialized healing verses for illness, bodily pain, and health restoration.",
            audioUrl = "https://download.quranicaudio.com/quran/sa3d_al-ghaamidee/complete/001.mp3",
            durationLabel = "Ayat ash-Shifa",
            category = RuqyahCategory.SHIFA_SICKNESS
        ),
        RuqyahAudioTrack(
            id = "ruqyah_evil_eye_idris",
            title = "Ruqyah against Evil Eye (Ayn) & Envy (Hasad)",
            reciter = "Sheikh Idris Abkar",
            description = "Al-Mu'awwidhat, Ayat al-Kursi, and supplications for protection against the evil eye.",
            audioUrl = "https://download.quranicaudio.com/quran/idrees_abkar/001.mp3",
            durationLabel = "Protection",
            category = RuqyahCategory.EVIL_EYE_PROTECTION
        ),
        RuqyahAudioTrack(
            id = "ruqyah_anxiety_ajmy",
            title = "Ruqyah for Relief of Anxiety, Grief & Distress",
            reciter = "Sheikh Ahmad Al-Ajmy",
            description = "Heart-soothing Quranic recitations and prophetic supplications for mental tranquility.",
            audioUrl = "https://download.quranicaudio.com/quran/ahmed_ibn_3alee_al-3ajamee/001.mp3",
            durationLabel = "Tranquility",
            category = RuqyahCategory.ANXIETY_DEPRESSION
        ),
        RuqyahAudioTrack(
            id = "ruqyah_baqarah_protection",
            title = "Surah Al-Baqarah Protection (Amanar-Rasul & Kursi)",
            reciter = "Sheikh Abdul Rahman Al-Sudais",
            description = "Prophetic shield against spiritual harm and household protection.",
            audioUrl = "https://download.quranicaudio.com/quran/abdurrahmaan_as-sudays/001.mp3",
            durationLabel = "Household Shield",
            category = RuqyahCategory.MORNING_EVENING_SHIELD
        )
    )
}
