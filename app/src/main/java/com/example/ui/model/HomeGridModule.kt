package com.example.ui.model

import com.example.ui.viewmodel.MainTab

data class HomeGridModule(
    val id: String,
    val title: String,
    val subtitle: String,
    val targetTab: MainTab,
    val isFeatured: Boolean = false,
    val isPremium: Boolean = false,
    val badgeText: String? = null
) {
    val isPro: Boolean get() = isPremium
}

object DefaultHomeModules {
    val ALL_MODULES = listOf(
        HomeGridModule("quran", "Holy Quran", "114 Surahs & Recitation", MainTab.QURAN),
        HomeGridModule("namaz", "Namaz & Salah", "Prayer Schedule & Guide", MainTab.NAMAZ),
        HomeGridModule("ai_guide", "Deen AI", "Spiritual Companion", MainTab.AI_GUIDE, isFeatured = true, badgeText = "AI"),
        HomeGridModule("fasting", "Fasting / Sawm", "Daily Suhur & Iftar Tracker", MainTab.FASTING),
        HomeGridModule("community", "Ummah Community", "Reflections, Forums & Du'as", MainTab.COMMUNITY, isFeatured = true, badgeText = "Ummah"),
        HomeGridModule("learning", "Learning Academy", "Interactive Quizzes & Tracks", MainTab.INTERACTIVE_LEARNING, badgeText = "Academy"),
        HomeGridModule("sahaba", "Sahaba Biographies", "Ashara Mubashara & Hadiths", MainTab.SAHABA_BIOGRAPHIES, badgeText = "Companions"),
        HomeGridModule("tasbih", "Dhikr & Tasbih", "Digital Counter & Goals", MainTab.TASBIH),
        HomeGridModule("hadith_duas", "Hadith & Duas", "Authentic Hadiths & Prayers", MainTab.HADITH_DUAS),
        HomeGridModule("qibla", "Qibla Compass", "Live Kaaba Heading & Sensor", MainTab.QIBLA),
        HomeGridModule("heritage", "Islamic Heritage", "Prophets, Caliphs & History", MainTab.ISLAMIC_HERITAGE, isFeatured = true, badgeText = "Alim.org"),
        HomeGridModule("books", "Islamic Books", "Classical & Modern Library", MainTab.ISLAMIC_BOOKS, badgeText = "Library"),
        HomeGridModule("mosques", "Mosque Locator", "Nearby Masjids & Directions", MainTab.MOSQUES, isPremium = true, badgeText = "PRO"),
        HomeGridModule("videos", "Islamic Videos", "Khutbahs & Lectures", MainTab.VIDEOS),
        HomeGridModule("rewards", "Deen Coins Hub", "Earn Free Premium & Offers", MainTab.MORE, isFeatured = true, badgeText = "🪙")
    )
}
