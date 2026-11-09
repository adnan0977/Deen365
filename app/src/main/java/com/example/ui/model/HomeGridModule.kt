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
        HomeGridModule("asma_ul_husna", "99 Names of Allah", "Asma-ul-Husna & Audio", MainTab.ASMA_UL_HUSNA, isFeatured = true, badgeText = "99"),
        HomeGridModule("hajj_umrah", "Hajj & Umrah", "Rituals, Tawaf & Sa'i Counter", MainTab.HAJJ_UMRAH, isFeatured = true, badgeText = "Guide"),
        HomeGridModule("qada_salah", "Qada Tracker", "Missed Prayer Make-up Log", MainTab.QADA_SALAH, badgeText = "Qada"),
        HomeGridModule("zakat", "Zakat Calculator", "Live Gold/Silver Nisab & Hadith", MainTab.ZAKAT, isFeatured = true, badgeText = "2.5%"),
        HomeGridModule("mirath", "Inheritance (Mirath)", "4 Madhabs & Debt Deductions", MainTab.INHERITANCE_CALCULATOR, isFeatured = true, badgeText = "Mirath"),
        HomeGridModule("hijri_calendar", "Hijri Calendar", "Sacred Occasions & Sunnah Fasts", MainTab.HIJRI_CALENDAR, isFeatured = true, badgeText = "1447 AH"),
        HomeGridModule("khatam_planner", "Khatam Planner", "Daily Pacing & Salah Targets", MainTab.KHATAM_PLANNER, badgeText = "Khatam"),
        HomeGridModule("ruqyah_healing", "Ruqyah & Shifa", "Healing Verses & Audio", MainTab.RUQYAH_HEALING, isFeatured = true, badgeText = "Shifa"),
        HomeGridModule("halal_guide", "Halal Food Guide", "350+ E-Numbers & Rulings", MainTab.HALAL_GUIDE, badgeText = "Halal"),
        HomeGridModule("baby_names", "Baby Names", "Meanings & Sunnah Etiquettes", MainTab.BABY_NAMES, badgeText = "Names"),
        HomeGridModule("haramain_live", "Haramain Live", "Makkah & Madinah 24/7 Feeds", MainTab.HARAMAIN_LIVE, badgeText = "Live"),
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
