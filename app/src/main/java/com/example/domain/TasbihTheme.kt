package com.example.domain

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.EmeraldPrimaryVariant
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldLight

enum class TasbihTheme(
    val id: String,
    val title: String,
    val subtitle: String,
    val surfaceGradient: List<Color>,
    val beadColor: Color,
    val activeBeadColor: Color,
    val ringColor: Color,
    val counterDigitColor: Color,
    val buttonBackground: Color,
    val buttonContentColor: Color
) {
    EMERALD_GOLD(
        id = "emerald_gold",
        title = "Emerald & Gold",
        subtitle = "Gemstone Beads & Gold Accent",
        surfaceGradient = listOf(EmeraldPrimary, EmeraldPrimaryVariant),
        beadColor = GoldLight.copy(alpha = 0.35f),
        activeBeadColor = GoldAccent,
        ringColor = GoldAccent,
        counterDigitColor = GoldLight,
        buttonBackground = GoldAccent,
        buttonContentColor = Color.Black
    ),
    CELESTIAL_MIDNIGHT(
        id = "celestial_midnight",
        title = "Celestial Midnight",
        subtitle = "Starry Obsidian & Neon Cyan",
        surfaceGradient = listOf(Color(0xFF0F172A), Color(0xFF1E293B)),
        beadColor = Color(0xFF334155),
        activeBeadColor = Color(0xFF38BDF8),
        ringColor = Color(0xFF38BDF8),
        counterDigitColor = Color(0xFFE2E8F0),
        buttonBackground = Color(0xFF38BDF8),
        buttonContentColor = Color(0xFF0F172A)
    ),
    EARTH_SATELLITE(
        id = "earth_satellite",
        title = "Earth Satellite",
        subtitle = "Terrestrial Marble & Coral",
        surfaceGradient = listOf(Color(0xFF132A13), Color(0xFF31572C)),
        beadColor = Color(0xFF4F772D).copy(alpha = 0.5f),
        activeBeadColor = Color(0xFF90A955),
        ringColor = Color(0xFFECF39E),
        counterDigitColor = Color(0xFFECF39E),
        buttonBackground = Color(0xFF90A955),
        buttonContentColor = Color.Black
    ),
    MINIMALIST_OBSIDIAN(
        id = "minimalist_obsidian",
        title = "Minimalist Obsidian",
        subtitle = "Matte Black & Hairline Gold",
        surfaceGradient = listOf(Color(0xFF18181B), Color(0xFF27272A)),
        beadColor = Color(0xFF3F3F46),
        activeBeadColor = GoldAccent,
        ringColor = GoldAccent.copy(alpha = 0.6f),
        counterDigitColor = Color.White,
        buttonBackground = GoldAccent,
        buttonContentColor = Color.Black
    );

    companion object {
        fun fromId(id: String): TasbihTheme = entries.find { it.id == id } ?: EMERALD_GOLD
    }
}
