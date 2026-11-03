package com.example.domain

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.EmeraldPrimaryVariant
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldLight

enum class QiblaTheme(
    val id: String,
    val title: String,
    val subtitle: String,
    val dialBackground: List<Color>,
    val ringColor: Color,
    val needlePointerColor: Color,
    val needleSouthColor: Color,
    val kaabaBeaconColor: Color,
    val textColor: Color
) {
    EMERALD_GOLD(
        id = "emerald_gold",
        title = "Emerald & Gold",
        subtitle = "Classic Islamic Heritage",
        dialBackground = listOf(EmeraldPrimary, EmeraldPrimaryVariant),
        ringColor = GoldAccent,
        needlePointerColor = GoldLight,
        needleSouthColor = Color.White.copy(alpha = 0.6f),
        kaabaBeaconColor = GoldAccent,
        textColor = GoldLight
    ),
    CELESTIAL_MIDNIGHT(
        id = "celestial_midnight",
        title = "Celestial Midnight",
        subtitle = "Cosmic Starfield & Glow",
        dialBackground = listOf(Color(0xFF0D1B2A), Color(0xFF1B263B)),
        ringColor = Color(0xFF00E5FF),
        needlePointerColor = Color(0xFF00E5FF),
        needleSouthColor = Color(0xFF778DA9),
        kaabaBeaconColor = Color(0xFF00E5FF),
        textColor = Color(0xFFE0E1DD)
    ),
    EARTH_SATELLITE(
        id = "earth_satellite",
        title = "Earth Satellite",
        subtitle = "Orbital View with Makkah Beacon",
        dialBackground = listOf(Color(0xFF0B3954), Color(0xFF087E8B)),
        ringColor = Color(0xFFFF5A5F),
        needlePointerColor = Color(0xFFFF5A5F),
        needleSouthColor = Color.White.copy(alpha = 0.7f),
        kaabaBeaconColor = Color(0xFFFFD166),
        textColor = Color.White
    ),
    MINIMALIST_OBSIDIAN(
        id = "minimalist_obsidian",
        title = "Minimalist Obsidian",
        subtitle = "Matte Graphite & Hairline Gold",
        dialBackground = listOf(Color(0xFF1A1A1A), Color(0xFF262626)),
        ringColor = GoldAccent.copy(alpha = 0.6f),
        needlePointerColor = GoldAccent,
        needleSouthColor = Color(0xFF888888),
        kaabaBeaconColor = GoldAccent,
        textColor = Color(0xFFEAEAEA)
    );

    companion object {
        fun fromId(id: String): QiblaTheme = entries.find { it.id == id } ?: EMERALD_GOLD
    }
}
