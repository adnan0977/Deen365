package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.Manifest
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import com.example.notification.AlertSoundMode
import com.example.domain.CalculationMethod
import com.example.domain.DarkModeConfig
import com.example.ui.components.AdminVideoManagementDialog
import com.example.ui.components.UserAccountHabitsDialog
import com.example.ui.theme.AppThemeType
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.EmeraldPrimaryVariant
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldLight
import com.example.ui.viewmodel.DeenUiState
import com.example.ui.viewmodel.DeenViewModel
import com.example.ui.viewmodel.MainTab

data class CustomizableTabInfo(
    val tab: MainTab,
    val title: String,
    val description: String,
    val icon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    state: DeenUiState,
    viewModel: DeenViewModel,
    modifier: Modifier = Modifier,
    onBack: () -> Unit = { viewModel.setTab(state.previousTab) }
) {
    BackHandler { onBack() }

    var showAdminDialog by remember { mutableStateOf(false) }
    var showUserDialog by remember { mutableStateOf(false) }
    var showMethodDialog by remember { mutableStateOf(false) }

    val allCustomizableTabs = remember {
        listOf(
            CustomizableTabInfo(MainTab.DASHBOARD, "Home", "Prayer countdowns & daily overview", Icons.Default.Home),
            CustomizableTabInfo(MainTab.QURAN, "Quran", "Holy Quran Surahs, audio & translations", Icons.Default.MenuBook),
            CustomizableTabInfo(MainTab.VIDEOS, "Videos", "Lectures by Scholar & Topic", Icons.Default.VideoLibrary),
            CustomizableTabInfo(MainTab.HADITH_DUAS, "Hadith & Duas", "Prophetic traditions & Hisn al-Muslim", Icons.Default.LibraryBooks),
            CustomizableTabInfo(MainTab.NAMAZ_FASTING, "Namaz", "Timetable, Qibla compass & Sawm tracker", Icons.Default.Schedule),
            CustomizableTabInfo(MainTab.TASBIH, "Dhikr", "Daily goals, tap counter & progress logs", Icons.Default.Fingerprint),
            CustomizableTabInfo(MainTab.QIBLA, "Qibla", "Accurate sensor-based Kaaba direction", Icons.Default.Explore)
        )
    }

    var selectedTabs by remember(state.selectedBottomTabs) {
        mutableStateOf(state.selectedBottomTabs)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top App Bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .testTag("settings_back_button")
                    ) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }

                    Column {
                        Text(
                            text = "Settings",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                        Text(
                            text = "Customize menus, calculations & preferences",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Section: Deen Coins & Subscription Wallet
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, GoldAccent.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth().testTag("settings_coins_wallet_card")
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = GoldAccent,
                                modifier = Modifier.size(38.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = Color.Black, modifier = Modifier.size(22.dp))
                                }
                            }
                            Column {
                                Text(
                                    text = "Deen Coins & Premium Wallet",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (state.coinsState.isPremium) "🌟 Premium Active • ${state.coinsState.premiumRemainingHours}h remaining" else "Free Tier • Earn coins via 5 Namaz & Fasting",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (state.coinsState.isPremium) GoldAccent else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = GoldAccent.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "${state.coinsState.balance} Coins",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.ExtraBold, color = GoldAccent),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { viewModel.openCoinsHub() },
                            colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = Color.Black),
                            modifier = Modifier.weight(1f).testTag("settings_open_coins_hub_btn")
                        ) {
                            Icon(Icons.Default.Storefront, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Store & Quests", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = { viewModel.openAdminOffers() },
                            modifier = Modifier.weight(1f).testTag("settings_open_admin_offers_btn")
                        ) {
                            Icon(Icons.Default.AdminPanelSettings, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Admin Offers", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Section: Audio Reciter / Speaker Selection (User Request Requirement)
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth().testTag("speaker_selection_card")
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = EmeraldPrimary.copy(alpha = 0.15f),
                                modifier = Modifier.size(38.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.RecordVoiceOver, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(20.dp))
                                }
                            }
                            Column {
                                Text(
                                    text = "Audio Reciter / Speaker",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Current: ${state.salahAudioState.selectedReciter.name} (${state.salahAudioState.selectedReciter.title})",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = EmeraldPrimary
                                )
                            }
                        }

                        Button(
                            onClick = { viewModel.openReciterDialog() },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("settings_change_speaker_btn")
                        ) {
                            Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Change", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Text(
                        text = "Customize the voice used for all Namaz recitation guides and Azan playback. Audio streams online and caches locally for offline use.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Section: System-Wide Dark Mode (Three-Way Toggle)
        item {
            val isSystemDark = androidx.compose.foundation.isSystemInDarkTheme()
            val effectiveDark = when (state.darkModeConfig) {
                DarkModeConfig.SYSTEM -> isSystemDark
                DarkModeConfig.LIGHT -> false
                DarkModeConfig.DARK -> true
            }

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (effectiveDark) GoldAccent.copy(alpha = 0.35f) else EmeraldPrimary.copy(alpha = 0.15f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("dark_mode_settings_card")
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Header Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = if (effectiveDark) Color(0xFF1E293B) else EmeraldPrimary.copy(alpha = 0.15f),
                                modifier = Modifier.size(38.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = if (effectiveDark) Icons.Default.DarkMode else Icons.Default.LightMode,
                                        contentDescription = null,
                                        tint = if (effectiveDark) GoldAccent else EmeraldPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Column {
                                Text(
                                    text = "System-Wide Dark Mode",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Three-way configuration for day & night",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Status Badge
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (effectiveDark) GoldAccent.copy(alpha = 0.2f) else EmeraldPrimary.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = if (effectiveDark) "🌙 Midnight Dark" else "☀️ Daylight Light",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (effectiveDark) GoldAccent else EmeraldPrimary
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    // 3-Option Segmented Selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        DarkModeConfig.entries.forEach { configOption ->
                            val isSelected = state.darkModeConfig == configOption
                            val optionIcon = when (configOption) {
                                DarkModeConfig.SYSTEM -> Icons.Default.BrightnessAuto
                                DarkModeConfig.LIGHT -> Icons.Default.LightMode
                                DarkModeConfig.DARK -> Icons.Default.DarkMode
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) {
                                    if (effectiveDark) GoldAccent.copy(alpha = 0.25f) else EmeraldPrimary.copy(alpha = 0.15f)
                                } else {
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                                },
                                border = if (isSelected) {
                                    androidx.compose.foundation.BorderStroke(1.5.dp, if (effectiveDark) GoldAccent else EmeraldPrimary)
                                } else null,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        viewModel.setDarkModeConfig(configOption, isSystemDark)
                                    }
                                    .testTag("dark_mode_option_${configOption.id}")
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 12.dp, horizontal = 6.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = optionIcon,
                                        contentDescription = configOption.displayName,
                                        tint = if (isSelected) {
                                            if (effectiveDark) GoldAccent else EmeraldPrimary
                                        } else {
                                            MaterialTheme.colorScheme.onSurfaceVariant
                                        },
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Text(
                                        text = configOption.displayName,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            fontSize = 11.sp
                                        ),
                                        color = if (isSelected) {
                                            if (effectiveDark) GoldAccent else EmeraldPrimary
                                        } else {
                                            MaterialTheme.colorScheme.onSurface
                                        },
                                        maxLines = 1
                                    )
                                    Text(
                                        text = when (configOption) {
                                            DarkModeConfig.SYSTEM -> if (isSystemDark) "Auto (Dark)" else "Auto (Light)"
                                            DarkModeConfig.LIGHT -> "Always Light"
                                            DarkModeConfig.DARK -> "Always Dark"
                                        },
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }

                    // Qibla & Tasbih Automatic Sync Highlight Card
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = GoldAccent.copy(alpha = 0.2f),
                                modifier = Modifier.size(28.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Sync,
                                        contentDescription = null,
                                        tint = GoldAccent,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Qibla & Tasbih Auto-Sync",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (effectiveDark)
                                        "Active: Qibla compass & Tasbih counter automatically synced to Celestial Midnight variants."
                                    else
                                        "When Dark Mode is active, Qibla compass & Tasbih counter seamlessly transition to Celestial Midnight styling.",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section: App Themes (More Themes - Premium Gated)
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth().testTag("app_themes_card")
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = GoldAccent.copy(alpha = 0.15f),
                                modifier = Modifier.size(38.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Palette, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(20.dp))
                                }
                            }
                            Column {
                                Text(
                                    text = "App Color Themes",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Customize Islamic visual palette",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        if (!state.coinsState.isPremium) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = GoldAccent.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "PRO Feature",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = GoldAccent),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        AppThemeType.values().forEach { theme ->
                            val isSelected = state.selectedThemeType == theme
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) EmeraldPrimary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                                border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, EmeraldPrimary) else null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.setTheme(theme) }
                                    .testTag("theme_item_${theme.name.lowercase()}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Text(
                                                text = theme.displayName,
                                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            if (theme.isPremiumOnly) {
                                                Surface(
                                                    shape = RoundedCornerShape(4.dp),
                                                    color = GoldAccent.copy(alpha = 0.25f)
                                                ) {
                                                    Text(
                                                        text = "PRO",
                                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = GoldAccent),
                                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                    )
                                                }
                                            }
                                        }
                                        Text(
                                            text = theme.subtitle,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    RadioButton(
                                        selected = isSelected,
                                        onClick = { viewModel.setTheme(theme) },
                                        colors = RadioButtonDefaults.colors(selectedColor = EmeraldPrimary)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section 1: Bottom Menu Customizer (User Request Requirement - Premium Gated)
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.horizontalGradient(
                        listOf(EmeraldPrimary.copy(alpha = 0.5f), GoldAccent.copy(alpha = 0.5f))
                    )
                ),
                modifier = Modifier.fillMaxWidth().testTag("bottom_menu_customizer_card")
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = EmeraldPrimary.copy(alpha = 0.15f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DashboardCustomize,
                                    contentDescription = null,
                                    tint = EmeraldPrimary,
                                    modifier = Modifier.padding(8.dp).size(24.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "Bottom Menu Layout",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Choose 4 options to display in bottom bar",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (selectedTabs.size == 4) EmeraldPrimary.copy(alpha = 0.15f) else GoldAccent.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "${selectedTabs.size} / 4 Selected",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (selectedTabs.size == 4) EmeraldPrimary else GoldAccent,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    if (!state.coinsState.isPremium) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = GoldAccent.copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth().clickable { viewModel.openCoinsHub() }
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = GoldAccent)
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Reordering Bottom Menu is a PRO Feature", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface)
                                    Text("Unlock with 24 Deen Coins (or complete 5 Namaz or Fasting today)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Button(
                                    onClick = { viewModel.openCoinsHub() },
                                    colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = Color.Black),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text("Unlock", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    Text(
                        text = "DEEN AI is anchored in the Centre, and More is anchored at the bottom right. Select 4 favorite sections to place around them:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Selection Checklist
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        allCustomizableTabs.forEach { tabInfo ->
                            val isChecked = selectedTabs.contains(tabInfo.tab)

                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isChecked) EmeraldPrimary.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                                ),
                                border = if (isChecked) CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(EmeraldPrimary, GoldAccent))) else null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        if (!state.coinsState.isPremium) {
                                            viewModel.openBottomMenuCustomizer()
                                        } else {
                                            val newTabs = if (isChecked) {
                                                selectedTabs - tabInfo.tab
                                            } else {
                                                if (selectedTabs.size < 4) {
                                                    selectedTabs + tabInfo.tab
                                                } else {
                                                    selectedTabs.take(3) + tabInfo.tab
                                                }
                                            }
                                            selectedTabs = newTabs
                                            viewModel.updateBottomTabs(newTabs)
                                        }
                                    }
                                    .testTag("tab_toggle_${tabInfo.tab.name}")
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(
                                            imageVector = tabInfo.icon,
                                            contentDescription = null,
                                            tint = if (isChecked) EmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(22.dp)
                                        )
                                        Column {
                                            Text(
                                                text = tabInfo.title,
                                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                                color = if (isChecked) EmeraldPrimary else MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = tabInfo.description,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                fontSize = 11.sp
                                            )
                                        }
                                    }

                                    Checkbox(
                                        checked = isChecked,
                                        onCheckedChange = {
                                            val newTabs = if (isChecked) {
                                                selectedTabs - tabInfo.tab
                                            } else {
                                                if (selectedTabs.size < 4) {
                                                    selectedTabs + tabInfo.tab
                                                } else {
                                                    selectedTabs.take(3) + tabInfo.tab
                                                }
                                            }
                                            selectedTabs = newTabs
                                            viewModel.updateBottomTabs(newTabs)
                                        },
                                        colors = CheckboxDefaults.colors(checkedColor = EmeraldPrimary)
                                    )
                                }
                            }
                        }
                    }

                    // Live Bottom Menu Preview
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Live Bottom Navigation Preview:",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            val leftSlots = selectedTabs.take(2)
                            val rightSlots = selectedTabs.drop(2).take(2)

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(MaterialTheme.colorScheme.surface)
                                    .padding(vertical = 8.dp, horizontal = 4.dp),
                                horizontalArrangement = Arrangement.SpaceAround,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                leftSlots.forEach { tab ->
                                    val info = allCustomizableTabs.firstOrNull { it.tab == tab }
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(info?.icon ?: Icons.Default.Star, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(16.dp))
                                        Text(info?.title?.take(6) ?: "", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp))
                                    }
                                }

                                // Center DEEN AI
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Surface(
                                        shape = CircleShape,
                                        color = GoldAccent,
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                                        }
                                    }
                                    Text("Deen AI", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold), color = GoldAccent)
                                }

                                rightSlots.forEach { tab ->
                                    val info = allCustomizableTabs.firstOrNull { it.tab == tab }
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(info?.icon ?: Icons.Default.Star, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(16.dp))
                                        Text(info?.title?.take(6) ?: "", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp))
                                    }
                                }

                                // Far Right MORE
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.Widgets, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(16.dp))
                                    Text("More", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold))
                                }
                            }
                        }
                    }

                    // Reset to Defaults Button
                    OutlinedButton(
                        onClick = {
                            val defaultTabs = listOf(
                                MainTab.DASHBOARD,
                                MainTab.QURAN,
                                MainTab.VIDEOS,
                                MainTab.NAMAZ_FASTING
                            )
                            selectedTabs = defaultTabs
                            viewModel.updateBottomTabs(defaultTabs)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.RestartAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Reset to Default 4 Options")
                    }
                }
            }
        }

        // Section 2: Calculation Method
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth().testTag("calculation_method_settings_card")
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Prayer Calculation Method",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Calculation parameters for Fajr and Isha angles based on geographic standards.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    CalculationMethod.values().forEach { method ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    viewModel.selectCalculationMethod(method)
                                }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(method.title, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
                                Text("Fajr: ${method.fajrAngle}° • Isha: ${method.ishaAngle}°", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            RadioButton(
                                selected = method == state.selectedCalculationMethod,
                                onClick = { viewModel.selectCalculationMethod(method) },
                                colors = RadioButtonDefaults.colors(selectedColor = EmeraldPrimary)
                            )
                        }
                    }
                }
            }
        }

        // Section 3: Prayer Alerts & Notification Settings
        item {
            val context = LocalContext.current
            val notifPermissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission()
            ) { granted ->
                if (granted) {
                    viewModel.toggleGlobalPrayerAlerts(true, context)
                    Toast.makeText(context, "Prayer alerts enabled!", Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.toggleGlobalPrayerAlerts(false, context)
                    Toast.makeText(context, "Notification permission is required for alerts.", Toast.LENGTH_SHORT).show()
                }
            }

            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.25f)),
                modifier = Modifier.fillMaxWidth().testTag("prayer_notifications_settings_card")
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Header & Master Switch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = EmeraldPrimary.copy(alpha = 0.12f),
                                modifier = Modifier.size(38.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.NotificationsActive,
                                        contentDescription = null,
                                        tint = EmeraldPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Column {
                                Text(
                                    text = "Prayer Time Alerts",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (state.prayerAlertsEnabled) "Scheduled for ${state.selectedCity.name}" else "Alerts currently disabled",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Switch(
                            checked = state.prayerAlertsEnabled,
                            onCheckedChange = { checked ->
                                if (checked && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    notifPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                } else {
                                    viewModel.toggleGlobalPrayerAlerts(checked, context)
                                }
                            },
                            modifier = Modifier.testTag("settings_prayer_alerts_toggle")
                        )
                    }

                    if (state.prayerAlertsEnabled && state.nextScheduledAlertText.isNotEmpty()) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = EmeraldPrimary.copy(alpha = 0.08f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.AccessAlarms, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(14.dp))
                                Text(
                                    text = state.nextScheduledAlertText,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                    color = EmeraldPrimary
                                )
                            }
                        }
                    }

                    AnimatedVisibility(visible = state.prayerAlertsEnabled) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(
                                text = "Active Prayer Alerts:",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                            )

                            val prayerToggles = listOf(
                                Triple("Fajr", state.notifyFajr, state.prayerSchedule.fajr),
                                Triple("Sunrise", state.notifySunrise, state.prayerSchedule.sunrise),
                                Triple("Dhuhr", state.notifyDhuhr, state.prayerSchedule.dhuhr),
                                Triple("Asr", state.notifyAsr, state.prayerSchedule.asr),
                                Triple("Maghrib", state.notifyMaghrib, state.prayerSchedule.maghrib),
                                Triple("Isha", state.notifyIsha, state.prayerSchedule.isha)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                prayerToggles.forEach { (name, enabled, time) ->
                                    FilterChip(
                                        selected = enabled,
                                        onClick = { viewModel.toggleIndividualPrayerAlert(name, !enabled, context) },
                                        label = { Text(name, fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = EmeraldPrimary.copy(alpha = 0.15f),
                                            selectedLabelColor = EmeraldPrimary
                                        ),
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }

                            // Reminder Offset
                            Text(
                                text = "Reminder Offset:",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                listOf(0 to "At Adhan", 5 to "5m early", 10 to "10m early", 15 to "15m early").forEach { (offset, label) ->
                                    FilterChip(
                                        selected = state.reminderOffsetMinutes == offset,
                                        onClick = { viewModel.setPrayerReminderOffset(offset, context) },
                                        label = { Text(label, fontSize = 11.sp) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = EmeraldPrimary.copy(alpha = 0.15f),
                                            selectedLabelColor = EmeraldPrimary
                                        ),
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }

                            // Sound Mode
                            Text(
                                text = "Sound Preference:",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                AlertSoundMode.values().forEach { mode ->
                                    FilterChip(
                                        selected = state.alertSoundMode == mode,
                                        onClick = { viewModel.setPrayerAlertSoundMode(mode, context) },
                                        label = { Text(mode.title, fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = GoldAccent.copy(alpha = 0.18f),
                                            selectedLabelColor = EmeraldPrimary
                                        ),
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }

                            // Sunset Fasting Completion Reminder
                            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.NightsStay,
                                        contentDescription = null,
                                        tint = GoldAccent,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Column {
                                        Text(
                                            text = "Sunset Fast Completion Reminder",
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "Daily push notification at sunset (${state.prayerSchedule.iftar}) to break fast & log completed Sawm",
                                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Switch(
                                    checked = state.notifySunsetFasting,
                                    onCheckedChange = { viewModel.toggleSunsetFastingReminder(it, context) },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = EmeraldPrimary
                                    ),
                                    modifier = Modifier.testTag("sunset_fasting_reminder_toggle")
                                )
                            }
                        }
                    }

                    // Location-Aware Service Status Pill
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = EmeraldPrimary.copy(alpha = 0.08f),
                        border = androidx.compose.foundation.BorderStroke(0.5.dp, EmeraldPrimary.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth().testTag("settings_scheduler_status_pill")
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(EmeraldPrimary)
                            )
                            Text(
                                text = state.schedulerServiceStatus,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = EmeraldPrimary,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = {
                                viewModel.scheduleLocationBasedPrayerNotifications(context)
                                Toast.makeText(context, "Location-based prayer notifications rescheduled!", Toast.LENGTH_SHORT).show()
                            },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("settings_reschedule_prayers_btn")
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Reschedule", fontSize = 11.sp)
                        }

                        OutlinedButton(
                            onClick = {
                                viewModel.triggerTestPrayerNotification(context, state.prayerSchedule.nextPrayerName)
                                Toast.makeText(context, "Test adhan alert dispatched!", Toast.LENGTH_SHORT).show()
                            },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("settings_test_prayer_notif_btn")
                        ) {
                            Icon(Icons.Default.NotificationsActive, contentDescription = null, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Test Adhan", fontSize = 11.sp)
                        }

                        OutlinedButton(
                            onClick = {
                                viewModel.triggerTestFastingNotification(context)
                                Toast.makeText(context, "Sunset fasting reminder dispatched!", Toast.LENGTH_SHORT).show()
                            },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("test_sunset_fasting_notification_button")
                        ) {
                            Icon(Icons.Default.NightsStay, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Test Iftar", fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // Section: Custom Mosque Timings & Iqamah Alarms
        item {
            val context = LocalContext.current
            val activeMosqueCount = state.mosqueTimings.count { it.isMosqueTimingActive }
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.25f)),
                modifier = Modifier.fillMaxWidth().testTag("settings_mosque_timings_card")
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = EmeraldPrimary.copy(alpha = 0.12f),
                                modifier = Modifier.size(38.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Mosque,
                                        contentDescription = null,
                                        tint = EmeraldPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Column {
                                Text(
                                    text = "Mosque & Iqamah Alarms",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (state.isGlobalMosqueActive) "All 5 prayer alarms set to ${state.mosqueName} timings"
                                           else if (activeMosqueCount > 0) "$activeMosqueCount prayer(s) using manual Iqamah times"
                                           else "Using astronomical solar calculations",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Button(
                            onClick = { viewModel.openMosqueTimingsDialog(true) },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("settings_configure_mosque_btn")
                        ) {
                            Text("Configure", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.openMosqueTimingsDialog(true) },
                            modifier = Modifier.weight(1f).testTag("settings_edit_mosque_times_btn"),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.AccessTime, contentDescription = null, modifier = Modifier.size(13.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Edit Iqamah Times", fontSize = 11.sp)
                        }

                        OutlinedButton(
                            onClick = {
                                viewModel.triggerTestMosqueAlarm("fajr", context)
                                Toast.makeText(context, "Testing ${state.mosqueName} custom Iqamah alarm!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.weight(1f).testTag("settings_test_mosque_alarm_btn"),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(13.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Test Iqamah Alarm", fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // Section 3: Account & Portals
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Account & Admin Portals",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Button(
                        onClick = { showUserDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                    ) {
                        Icon(Icons.Default.AccountCircle, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("User Profile & AI Habit Analytics")
                    }

                    OutlinedButton(
                        onClick = { showAdminDialog = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.AdminPanelSettings, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Admin Video Portal (Curate Lectures)")
                    }
                }
            }
        }
    }

    if (showAdminDialog) {
        AdminVideoManagementDialog(
            isAdminLoggedIn = state.isAdminLoggedIn,
            customVideos = state.customVideos,
            onLoginAdmin = { viewModel.loginAdmin(it) },
            onLogoutAdmin = { viewModel.logoutAdmin() },
            onAddVideo = { viewModel.addVideoByAdmin(it) },
            onDeleteVideo = { viewModel.deleteVideoByAdmin(it) },
            onDismiss = { showAdminDialog = false }
        )
    }

    if (showUserDialog) {
        UserAccountHabitsDialog(
            userHabit = state.userHabit,
            prayerRecord = state.prayerRecord,
            isGeneratingAiHabit = state.isGeneratingAiHabit,
            aiHabitInsight = state.aiHabitInsight,
            onSaveUserHabit = { viewModel.saveUserHabit(it) },
            onGenerateAiInsight = { viewModel.generateHabitInsightsWithAi() },
            onDismiss = { showUserDialog = false }
        )
    }

    if (state.isMosqueTimingsDialogOpen) {
        com.example.ui.screens.namaz.MosqueTimingsDialog(
            isOpen = true,
            mosqueName = state.mosqueName,
            isGlobalMosqueActive = state.isGlobalMosqueActive,
            timings = state.mosqueTimings,
            onDismiss = { viewModel.openMosqueTimingsDialog(false) },
            onSaveMosqueName = { viewModel.setMosqueName(it) },
            onToggleGlobalMode = { viewModel.toggleGlobalMosqueTiming(it) },
            onUpdateTiming = { id, time, active, preAlarm, soundMode ->
                viewModel.updateMosqueTiming(id, time, active, preAlarm, soundMode)
            },
            onResetAll = { viewModel.resetMosqueTimingsToDefault() },
            onTestAlarm = { viewModel.triggerTestMosqueAlarm(it) }
        )
    }
}
