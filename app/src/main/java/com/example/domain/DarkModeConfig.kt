package com.example.domain

/**
 * Three-way Dark Mode configuration options:
 * - SYSTEM: Automatically follows Android device dark/light theme
 * - LIGHT: Force application to use daylight light theme
 * - DARK: Force application to use deep midnight dark theme
 */
enum class DarkModeConfig(
    val id: String,
    val displayName: String,
    val subtitle: String
) {
    SYSTEM(
        id = "system",
        displayName = "System Default",
        subtitle = "Matches device OS appearance"
    ),
    LIGHT(
        id = "light",
        displayName = "Always Light",
        subtitle = "Bright emerald & golden daylight"
    ),
    DARK(
        id = "dark",
        displayName = "Always Dark",
        subtitle = "Deep celestial midnight & battery saving"
    );

    companion object {
        fun fromId(id: String?): DarkModeConfig {
            return entries.find { it.id.equals(id, ignoreCase = true) } ?: SYSTEM
        }
    }
}
