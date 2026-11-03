package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.domain.DarkModeConfig

enum class AppThemeType(val displayName: String, val subtitle: String, val isPremiumOnly: Boolean = false) {
    EMERALD("Emerald Islamic", "Traditional Islamic rich emerald & royal gold", false),
    MIDNIGHT("Midnight Sapphire", "Deep celestial midnight sapphire & astrolabe gold", true),
    ANDALUSIAN("Desert Sand", "Warm Moorish terracotta, ivory & Alhambra teal", true),
    OBSIDIAN("Royal Kaaba Obsidian", "Jet black, deep charcoal & pure gilded gold leaf", true),
    ROSE_MEDINA("Rose of Medina", "Noble spiritual plum, velvet rose & gold aura", true)
}

// 1. Emerald Schemes
private val EmeraldDarkColorScheme = darkColorScheme(
    primary = GoldAccent,
    onPrimary = EmeraldPrimaryVariant,
    primaryContainer = EmeraldPrimary,
    onPrimaryContainer = GoldGlow,
    secondary = GoldLight,
    onSecondary = EmeraldDarkBackground,
    secondaryContainer = GoldContainer,
    onSecondaryContainer = GoldGlow,
    tertiary = EmeraldAccent,
    onTertiary = Color.White,
    background = EmeraldDarkBackground,
    onBackground = TextPrimaryDark,
    surface = EmeraldDarkSurface,
    onSurface = TextPrimaryDark,
    surfaceVariant = EmeraldDarkCard,
    onSurfaceVariant = TextSecondaryDark,
    outline = Color(0xFF2A4A3C)
)

private val EmeraldLightColorScheme = lightColorScheme(
    primary = EmeraldPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD2EADF),
    onPrimaryContainer = EmeraldPrimaryVariant,
    secondary = GoldAccent,
    onSecondary = Color(0xFF2D2000),
    secondaryContainer = Color(0xFFFFF2D0),
    onSecondaryContainer = Color(0xFF5A4400),
    tertiary = TealAccent,
    onTertiary = Color.White,
    background = SoftSand,
    onBackground = TextPrimaryLight,
    surface = LightSurface,
    onSurface = TextPrimaryLight,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = TextSecondaryLight,
    outline = Color(0xFFD6CFC3)
)

// 2. Midnight Globe Schemes
private val MidnightDarkColorScheme = darkColorScheme(
    primary = MidnightGold,
    onPrimary = MidnightNavyDark,
    primaryContainer = MidnightNavyPrimary,
    onPrimaryContainer = Color.White,
    secondary = MidnightCyan,
    onSecondary = MidnightNavyDark,
    secondaryContainer = Color(0xFF1E3A5F),
    onSecondaryContainer = MidnightCyan,
    tertiary = GoldLight,
    onTertiary = MidnightNavyDark,
    background = MidnightStarfield,
    onBackground = Color(0xFFE2E8F0),
    surface = MidnightSurface,
    onSurface = Color(0xFFF1F5F9),
    surfaceVariant = MidnightCard,
    onSurfaceVariant = Color(0xFF94A3B8),
    outline = Color(0xFF263C63)
)

private val MidnightLightColorScheme = lightColorScheme(
    primary = MidnightNavyPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD8E5F7),
    onPrimaryContainer = MidnightNavyDark,
    secondary = Color(0xFF0284C7),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE0F2FE),
    onSecondaryContainer = Color(0xFF0369A1),
    tertiary = MidnightGold,
    onTertiary = Color.Black,
    background = Color(0xFFF8FAFC),
    onBackground = Color(0xFF0F172A),
    surface = Color.White,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFEEF2F6),
    onSurfaceVariant = Color(0xFF475569),
    outline = Color(0xFFCBD5E1)
)

// 3. Andalusian Moorish Schemes
private val AndalusianDarkColorScheme = darkColorScheme(
    primary = AndalusianCopper,
    onPrimary = Color.White,
    primaryContainer = AndalusianTerracotta,
    onPrimaryContainer = Color(0xFFFFDBCF),
    secondary = AndalusianAlhambraTeal,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF123D37),
    onSecondaryContainer = Color(0xFF7AE0D4),
    tertiary = GoldAccent,
    onTertiary = Color.Black,
    background = AndalusianDarkBg,
    onBackground = Color(0xFFF7ECE8),
    surface = AndalusianDarkSurface,
    onSurface = Color(0xFFF7ECE8),
    surfaceVariant = Color(0xFF3B2A25),
    onSurfaceVariant = Color(0xFFC7B1AA),
    outline = Color(0xFF563D36)
)

private val AndalusianLightColorScheme = lightColorScheme(
    primary = AndalusianTerracotta,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFDBCF),
    onPrimaryContainer = Color(0xFF3B0B00),
    secondary = AndalusianAlhambraTeal,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFBCEEE7),
    onSecondaryContainer = Color(0xFF003833),
    tertiary = AndalusianCopper,
    onTertiary = Color.White,
    background = AndalusianWarmSand,
    onBackground = Color(0xFF231A17),
    surface = Color.White,
    onSurface = Color(0xFF231A17),
    surfaceVariant = AndalusianIvoryCard,
    onSurfaceVariant = Color(0xFF5C4F4B),
    outline = Color(0xFFDCC8BD)
)

// 4. Obsidian Schemes
private val ObsidianDarkColorScheme = darkColorScheme(
    primary = ObsidianGold,
    onPrimary = Color.Black,
    primaryContainer = ObsidianDarkCard,
    onPrimaryContainer = GoldGlow,
    secondary = GoldLight,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF2C240A),
    onSecondaryContainer = ObsidianGold,
    tertiary = GoldAccent,
    onTertiary = Color.Black,
    background = ObsidianBlack,
    onBackground = Color(0xFFF3F4F6),
    surface = ObsidianDarkSurface,
    onSurface = Color(0xFFF3F4F6),
    surfaceVariant = ObsidianDarkCard,
    onSurfaceVariant = Color(0xFF9CA3AF),
    outline = Color(0xFF374151)
)

private val ObsidianLightColorScheme = lightColorScheme(
    primary = Color(0xFF1F2937),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE5E7EB),
    onPrimaryContainer = Color(0xFF111827),
    secondary = ObsidianGold,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFFFEF3C7),
    onSecondaryContainer = Color(0xFF92400E),
    tertiary = Color(0xFFD97706),
    onTertiary = Color.White,
    background = Color(0xFFF9FAFB),
    onBackground = Color(0xFF111827),
    surface = Color.White,
    onSurface = Color(0xFF111827),
    surfaceVariant = Color(0xFFF3F4F6),
    onSurfaceVariant = Color(0xFF4B5563),
    outline = Color(0xFFD1D5DB)
)

// 5. Rose Medina Schemes
private val RoseMedinaDarkColorScheme = darkColorScheme(
    primary = RoseMedinaPink,
    onPrimary = RoseMedinaPlumDark,
    primaryContainer = RoseMedinaPrimary,
    onPrimaryContainer = Color(0xFFFFD8E4),
    secondary = GoldLight,
    onSecondary = RoseMedinaPlumDark,
    secondaryContainer = Color(0xFF381525),
    onSecondaryContainer = RoseMedinaPink,
    tertiary = GoldAccent,
    onTertiary = Color.Black,
    background = RoseMedinaPlumDark,
    onBackground = Color(0xFFFCE7F3),
    surface = RoseMedinaSurface,
    onSurface = Color(0xFFFCE7F3),
    surfaceVariant = Color(0xFF3D1935),
    onSurfaceVariant = Color(0xFFDDA7C8),
    outline = Color(0xFF5B234F)
)

private val RoseMedinaLightColorScheme = lightColorScheme(
    primary = RoseMedinaPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFD8E4),
    onPrimaryContainer = Color(0xFF3E003D),
    secondary = Color(0xFF9D174D),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFCE7F3),
    onSecondaryContainer = Color(0xFF831843),
    tertiary = GoldAccent,
    onTertiary = Color.Black,
    background = Color(0xFFFDF4F8),
    onBackground = Color(0xFF280B25),
    surface = Color.White,
    onSurface = Color(0xFF280B25),
    surfaceVariant = Color(0xFFF9E8F1),
    onSurfaceVariant = Color(0xFF644458),
    outline = Color(0xFFE4C5D6)
)

@Composable
fun MyApplicationTheme(
    darkModeConfig: DarkModeConfig = DarkModeConfig.SYSTEM,
    darkTheme: Boolean = when (darkModeConfig) {
        DarkModeConfig.SYSTEM -> isSystemInDarkTheme()
        DarkModeConfig.LIGHT -> false
        DarkModeConfig.DARK -> true
    },
    themeType: AppThemeType = AppThemeType.EMERALD,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme: ColorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        themeType == AppThemeType.MIDNIGHT -> {
            if (darkTheme) MidnightDarkColorScheme else MidnightLightColorScheme
        }
        themeType == AppThemeType.ANDALUSIAN -> {
            if (darkTheme) AndalusianDarkColorScheme else AndalusianLightColorScheme
        }
        themeType == AppThemeType.OBSIDIAN -> {
            if (darkTheme) ObsidianDarkColorScheme else ObsidianLightColorScheme
        }
        themeType == AppThemeType.ROSE_MEDINA -> {
            if (darkTheme) RoseMedinaDarkColorScheme else RoseMedinaLightColorScheme
        }
        else -> {
            if (darkTheme) EmeraldDarkColorScheme else EmeraldLightColorScheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
