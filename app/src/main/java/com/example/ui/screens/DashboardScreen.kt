package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.remote.AiCategory
import com.example.data.repository.DailyDuaItem
import com.example.data.repository.DuaOfTheDayRepository
import com.example.domain.CityPreset
import com.example.domain.PrayerTimeCalculator
import com.example.ui.components.UnlockProFeatureDialog
import com.example.ui.theme.*
import com.example.ui.viewmodel.DeenUiState
import com.example.ui.viewmodel.DeenViewModel
import com.example.ui.viewmodel.MainTab

@Composable
fun DashboardScreen(
    state: DeenUiState,
    viewModel: DeenViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showCityDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App Header
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Top Row: App Title & Hijri Date (Left) + Coins Pill & User Profile (Right)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Deen365",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                        Text(
                            text = "${state.hijriDate.day} ${state.hijriDate.monthName} ${state.hijriDate.year} AH • ${state.hijriDate.gregorianFormatted}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Deen Coins Header Widget Pill
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (state.coinsState.isPremium) GoldAccent.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { viewModel.openCoinsHub() }
                                .testTag("dashboard_coins_pill")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text("🪙", fontSize = 13.sp)
                                Text(
                                    text = "${state.coinsState.balance}",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = if (state.coinsState.isPremium) GoldAccent else MaterialTheme.colorScheme.onSurface
                                )
                                if (state.coinsState.isPremium) {
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = GoldAccent
                                    ) {
                                        Text(
                                            text = "${state.coinsState.premiumRemainingHours}h",
                                            color = Color.Black,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Black,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // User Account Profile Button
                        IconButton(
                            onClick = { viewModel.openUserAccountDialog() },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(if (state.userProfile?.role == "subscriber") GoldAccent.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
                                .testTag("dashboard_user_account_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "User Account",
                                tint = if (state.userProfile?.role == "subscriber") GoldAccent else MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                // Dedicated, Elegantly Aligned Location Bar
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { showCityDialog = true }
                        .testTag("dashboard_location_bar")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                                modifier = Modifier.size(32.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = "Location",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            Column {
                                Text(
                                    text = "${state.selectedCity.name}, ${state.selectedCity.country}",
                                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1
                                )
                                Text(
                                    text = "Auto-calculated prayer times for this location",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = "Change",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "Change City",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }

        // Celebratory Reward Banner if coins earned
        if (state.coinsState.recentRewardMessage != null) {
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = GoldAccent.copy(alpha = 0.18f)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent),
                    modifier = Modifier.fillMaxWidth().testTag("reward_banner")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("✨", fontSize = 18.sp)
                            Text(
                                text = state.coinsState.recentRewardMessage ?: "",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                        }
                        IconButton(
                            onClick = { viewModel.clearRecentCoinRewardMessage() },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Dismiss", modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }

        // Hero Next Prayer & Fasting Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("hero_prayer_card")
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(EmeraldPrimary, EmeraldPrimaryVariant)
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = GoldAccent.copy(alpha = 0.2f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent.copy(alpha = 0.5f))
                            ) {
                                Text(
                                    text = "NEXT PRAYER",
                                    color = GoldLight,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (state.isFastedToday) GoldAccent else Color.White.copy(alpha = 0.18f),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable { viewModel.toggleFasting() }
                                        .testTag("dashboard_fasting_toggle_pill")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (state.isFastedToday) Icons.Default.Check else Icons.Default.NightsStay,
                                            contentDescription = "Sawm Toggle",
                                            tint = if (state.isFastedToday) EmeraldPrimaryVariant else Color.White,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Text(
                                            text = if (state.isFastedToday) "Sawm: ON" else "Sawm: OFF",
                                            color = if (state.isFastedToday) EmeraldPrimaryVariant else Color.White,
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                                        )
                                    }
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = if (state.prayerSchedule.isFastingActive) Icons.Default.NightsStay else Icons.Default.WbSunny,
                                        contentDescription = "Fasting State",
                                        tint = GoldLight,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = if (state.prayerSchedule.isFastingActive) "Iftar in ${state.prayerSchedule.timeRemainingIftar}" else "Iftar: ${state.prayerSchedule.iftar}",
                                        color = Color.White.copy(alpha = 0.9f),
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Column {
                                Text(
                                    text = state.prayerSchedule.nextPrayerName,
                                    style = MaterialTheme.typography.headlineLarge.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White
                                    )
                                )
                                Text(
                                    text = "in ${state.prayerSchedule.timeRemainingNextPrayer}",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = GoldLight
                                )
                            }

                            Button(
                                onClick = { viewModel.setTab(MainTab.NAMAZ_FASTING) },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = GoldAccent,
                                    contentColor = EmeraldPrimaryVariant
                                ),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                            ) {
                                Text("Timetable", fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Divider(color = Color.White.copy(alpha = 0.15f))
                        Spacer(modifier = Modifier.height(12.dp))

                        // Compact 5 Prayers Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            PrayerMiniBadge("Fajr", state.prayerSchedule.fajr, state.prayerRecord.fajr) { viewModel.togglePrayer("Fajr") }
                            PrayerMiniBadge("Dhuhr", state.prayerSchedule.dhuhr, state.prayerRecord.dhuhr) { viewModel.togglePrayer("Dhuhr") }
                            PrayerMiniBadge("Asr", state.prayerSchedule.asr, state.prayerRecord.asr) { viewModel.togglePrayer("Asr") }
                            PrayerMiniBadge("Maghrib", state.prayerSchedule.maghrib, state.prayerRecord.maghrib) { viewModel.togglePrayer("Maghrib") }
                            PrayerMiniBadge("Isha", state.prayerSchedule.isha, state.prayerRecord.isha) { viewModel.togglePrayer("Isha") }
                        }
                    }
                }
            }
        }

        // AI Personalized Spiritual Advisor Card
        item {
            com.example.ui.screens.ai.PersonalizedGuidanceCard(
                guidance = state.personalizedGuidanceResult,
                isLoading = state.isLoadingPersonalizedGuidance,
                completedPrayersCount = state.prayerRecord.completedCount,
                prayerStreak = state.prayerStats.currentStreakDays,
                fastingStreak = state.fastingStreak,
                isFastingToday = state.isFastingToday,
                onRefreshGuidance = { viewModel.generatePersonalizedSpiritualGuidance() }
            )
        }

        // All Modules Reorderable Grid
        item {
            val allModulesMap = remember {
                com.example.ui.model.DefaultHomeModules.ALL_MODULES.associateBy { it.id }
            }
            val orderedModules = remember(state.homeGridModuleIds) {
                state.homeGridModuleIds.mapNotNull { allModulesMap[it] }
            }

            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "All Modules",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = EmeraldPrimary.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "${orderedModules.size}",
                                    color = EmeraldPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = if (state.isHomeGridReorderingMode) "Tap arrows to move modules on home" else "Customizable Spiritual Sanctuary",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        if (state.isHomeGridReorderingMode) {
                            TextButton(
                                onClick = { viewModel.resetHomeGridOrder() },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text("Reset", fontSize = 11.sp)
                            }
                        }

                        Button(
                            onClick = { viewModel.toggleHomeGridReorderMode() },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (state.isHomeGridReorderingMode) GoldAccent else MaterialTheme.colorScheme.surfaceVariant
                            ),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("toggle_reorder_grid_button")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (state.isHomeGridReorderingMode) Icons.Default.Check else Icons.Default.SwapVert,
                                    contentDescription = "Reorder",
                                    tint = if (state.isHomeGridReorderingMode) Color.Black else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (state.isHomeGridReorderingMode) "Done" else "Move",
                                    fontSize = 12.sp,
                                    color = if (state.isHomeGridReorderingMode) Color.Black else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (state.isHomeGridReorderingMode) {
                    // Reorder Mode List
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        orderedModules.forEachIndexed { index, module ->
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)),
                                border = null,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(
                                            shape = CircleShape,
                                            color = EmeraldPrimary.copy(alpha = 0.15f),
                                            modifier = Modifier.size(26.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text(
                                                    text = "#${index + 1}",
                                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = EmeraldPrimary)
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = module.title,
                                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                            )
                                            Text(
                                                text = module.subtitle,
                                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        IconButton(
                                            onClick = { viewModel.moveHomeGridModuleUp(module.id) },
                                            enabled = index > 0,
                                            modifier = Modifier.size(36.dp).testTag("move_up_${module.id}")
                                        ) {
                                            Icon(
                                                Icons.Default.KeyboardArrowUp,
                                                contentDescription = "Move Up",
                                                tint = if (index > 0) EmeraldPrimary else MaterialTheme.colorScheme.outlineVariant
                                            )
                                        }

                                        IconButton(
                                            onClick = { viewModel.moveHomeGridModuleDown(module.id) },
                                            enabled = index < orderedModules.size - 1,
                                            modifier = Modifier.size(36.dp).testTag("move_down_${module.id}")
                                        ) {
                                            Icon(
                                                Icons.Default.KeyboardArrowDown,
                                                contentDescription = "Move Down",
                                                tint = if (index < orderedModules.size - 1) EmeraldPrimary else MaterialTheme.colorScheme.outlineVariant
                                            )
                                        }

                                        if (state.userProfile?.role == "admin") {
                                            val isPro = state.proModuleIds.contains(module.id) || module.isPro
                                            FilterChip(
                                                selected = isPro,
                                                onClick = { viewModel.toggleModuleProStatus(module.id, !isPro) },
                                                label = { Text(if (isPro) "PRO" else "FREE", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                                                modifier = Modifier.padding(start = 4.dp).testTag("reorder_pro_toggle_${module.id}")
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // Regular 2-Column Grid of All Modules
                    val chunked = orderedModules.chunked(2)
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        chunked.forEach { rowModules ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                rowModules.forEach { module ->
                                    val icon = when (module.id) {
                                        "quran" -> Icons.Default.MenuBook
                                        "namaz" -> Icons.Default.Schedule
                                        "ai_guide" -> Icons.Default.AutoAwesome
                                        "fasting" -> Icons.Default.NightsStay
                                        "community" -> Icons.Default.Groups
                                        "learning" -> Icons.Default.School
                                        "sahaba" -> Icons.Default.Stars
                                        "tasbih" -> Icons.Default.Fingerprint
                                        "hadith_duas" -> Icons.Default.LibraryBooks
                                        "qibla" -> Icons.Default.Explore
                                        "mosques" -> Icons.Default.Mosque
                                        "videos" -> Icons.Default.VideoLibrary
                                        "rewards" -> Icons.Default.Stars
                                        "heritage" -> Icons.Default.HistoryEdu
                                        "books" -> Icons.Default.AutoStories
                                        else -> Icons.Default.Widgets
                                    }

                                    val isProModule = module.isPro || state.proModuleIds.contains(module.id)
                                    val effectiveBadge = if (isProModule) "PRO" else module.badgeText

                                    HomeGridModuleCard(
                                        title = module.title,
                                        subtitle = module.subtitle,
                                        icon = icon,
                                        isFeatured = module.isFeatured,
                                        badgeText = effectiveBadge,
                                        modifier = Modifier.weight(1f),
                                        onClick = {
                                            if (isProModule && !viewModel.isModuleUnlocked(module)) {
                                                viewModel.showProModuleUnlockPrompt(module)
                                            } else if (module.id == "rewards") {
                                                viewModel.openCoinsHub()
                                            } else if (module.id == "ai_guide") {
                                                viewModel.openDeenAiSheet()
                                            } else {
                                                viewModel.setTab(module.targetTab)
                                            }
                                        }
                                    )
                                }
                                if (rowModules.size == 1) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(14.dp))

            // 🌟 Featured Dua of the Day Card (Fetched from local JSON resource)
            val context = LocalContext.current
            val todayDua = remember { DuaOfTheDayRepository.getTodayDua(context) }
            val isPlayingDua = state.playingAudioId == todayDua.id

            DuaOfTheDayHeroCard(
                dua = todayDua,
                isPlaying = isPlayingDua,
                onPlayAudio = {
                    viewModel.playAudio(todayDua.id, todayDua.arabic, todayDua.transliteration)
                },
                onExploreAllDuas = {
                    viewModel.openDuaLibrary()
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Islamic Video Lectures Hero Banner
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                border = androidx.compose.foundation.BorderStroke(0.5.dp, EmeraldPrimary.copy(alpha = 0.2f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.setTab(MainTab.VIDEOS) }
                    .testTag("islamic_videos_hero_banner")
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = EmeraldPrimary.copy(alpha = 0.15f),
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.VideoLibrary, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(24.dp))
                            }
                        }

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = "Islamic Video Lectures",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = GoldAccent.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = "YouTube",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = GoldAccent,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Lectures by Mufti Menk, Nouman Ali Khan, Omar Suleiman & topics",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = EmeraldPrimary)
                }
            }
        }

        // 'Daily Quranic Reflection' Widget (Fetches & displays random Ayah with translation, Room cached)
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                border = androidx.compose.foundation.BorderStroke(0.5.dp, GoldAccent.copy(alpha = 0.25f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("daily_quranic_reflection_widget")
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    // Widget Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "Reflection",
                                tint = GoldAccent,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "DAILY QURANIC REFLECTION",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = GoldAccent
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        ) {
                            Text(
                                text = "Room DB Cached",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (state.isLoadingReflection) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(32.dp),
                                strokeWidth = 3.dp
                            )
                        }
                    } else {
                        state.dailyQuranicReflection?.let { reflection ->
                            // Surah reference and Theme pill
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant
                                ) {
                                    Text(
                                        text = "${reflection.surahName} (${reflection.surahNumber}:${reflection.ayahNumber})",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }

                                Text(
                                    text = reflection.theme,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Arabic Ayah Script
                            Text(
                                text = reflection.arabicText,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    textAlign = TextAlign.Right,
                                    lineHeight = 36.sp
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // English Translation
                            Text(
                                text = "\"${reflection.translation}\"",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = 22.sp
                            )

                            if (reflection.conciseExplanation.isNotBlank()) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Icon(Icons.Default.Lightbulb, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(16.dp))
                                            Text(
                                                text = "Explanation & Reflection",
                                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                        Text(
                                            text = reflection.conciseExplanation,
                                            style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp),
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 3
                                        )

                                        if (reflection.dailyLifeApplication.isNotBlank()) {
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(14.dp))
                                                Text(
                                                    text = "Daily Action: ${reflection.dailyLifeApplication}",
                                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                                                    color = EmeraldPrimary,
                                                    maxLines = 2
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        } ?: run {
                            Text(
                                text = "Loading today's Quranic reflection...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
                    Spacer(modifier = Modifier.height(10.dp))

                    // Widget Action Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            // Shuffle Button
                            FilledTonalButton(
                                onClick = { viewModel.refreshDailyQuranicReflection() },
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("refresh_random_ayah_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Shuffle,
                                    contentDescription = "Shuffle",
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Shuffle", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                            }

                            // Share Reflection Button
                            OutlinedButton(
                                onClick = {
                                    state.dailyQuranicReflection?.let {
                                        viewModel.shareDailyReflection(context, it)
                                    }
                                },
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("share_daily_reflection_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Share",
                                    tint = EmeraldPrimary,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Share", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = EmeraldPrimary))
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            // Read Full Reflection Dialog Button
                            FilledTonalButton(
                                onClick = {
                                    state.dailyQuranicReflection?.let {
                                        viewModel.openDailyReflectionDetails(it)
                                    }
                                },
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("read_reflection_details_btn")
                            ) {
                                Text("Read Full", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                            }

                            // Reflect with AI
                            Button(
                                onClick = {
                                    state.dailyQuranicReflection?.let {
                                        viewModel.askAiAboutDailyReflection(it)
                                    } ?: viewModel.setTab(MainTab.AI_GUIDE)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("reflect_with_ai_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = "Reflect with AI",
                                    tint = GoldLight,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "AI",
                                    color = Color.White,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Daily Tracker: Prayers & Fasting
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Daily Salah Tracker",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${state.prayerRecord.completedCount} of 5 prayers offered today",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Fasting quick toggle
                        FilterChip(
                            selected = state.isFastedToday,
                            onClick = { viewModel.toggleFasting() },
                            label = { Text(if (state.isFastedToday) "Fasting: Yes" else "Fasting: No") },
                            leadingIcon = {
                                Icon(
                                    imageVector = if (state.isFastedToday) Icons.Default.Check else Icons.Outlined.NightsStay,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = GoldAccent,
                                selectedLabelColor = EmeraldPrimaryVariant
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        PrayerTrackerChip("Fajr", state.prayerRecord.fajr) { viewModel.togglePrayer("Fajr") }
                        PrayerTrackerChip("Dhuhr", state.prayerRecord.dhuhr) { viewModel.togglePrayer("Dhuhr") }
                        PrayerTrackerChip("Asr", state.prayerRecord.asr) { viewModel.togglePrayer("Asr") }
                        PrayerTrackerChip("Maghrib", state.prayerRecord.maghrib) { viewModel.togglePrayer("Maghrib") }
                        PrayerTrackerChip("Isha", state.prayerRecord.isha) { viewModel.togglePrayer("Isha") }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = if (state.userProfile != null) Icons.Default.CloudDone else Icons.Default.CloudQueue,
                                contentDescription = null,
                                tint = if (state.userProfile != null) EmeraldPrimary else GoldAccent,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = if (state.userProfile != null) "Multi-Device Synced" else "Local Tracker",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        TextButton(
                            onClick = { viewModel.setTab(com.example.ui.viewmodel.MainTab.NAMAZ) },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.testTag("dashboard_view_prayer_history")
                        ) {
                            Text(
                                text = "View History & Sync",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = EmeraldPrimary
                            )
                            Spacer(Modifier.width(2.dp))
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = null,
                                modifier = Modifier.size(12.dp),
                                tint = EmeraldPrimary
                            )
                        }
                    }
                }
            }
        }
    }

    if (showCityDialog) {
        CitySelectionDialog(
            currentCity = state.selectedCity,
            onDismiss = { showCityDialog = false },
            onSelect = {
                viewModel.selectCity(it)
                showCityDialog = false
            }
        )
    }

    if (state.proModuleUnlockPrompt != null) {
        val promptModule = state.proModuleUnlockPrompt
        UnlockProFeatureDialog(
            module = promptModule,
            coinBalance = state.coinsState.balance,
            onUnlockWithCoins = {
                viewModel.purchasePremiumWithCoins(24)
                viewModel.dismissProModuleUnlockPrompt()
            },
            onOpenCoinsHub = {
                viewModel.dismissProModuleUnlockPrompt()
                viewModel.openCoinsHub()
            },
            onDismiss = { viewModel.dismissProModuleUnlockPrompt() }
        )
    }
}

@Composable
fun PrayerMiniBadge(
    name: String,
    time: String,
    completed: Boolean,
    onToggle: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onToggle() }
            .padding(horizontal = 4.dp, vertical = 2.dp)
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.8f)
        )
        Text(
            text = time.replace(" AM", "").replace(" PM", ""),
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = if (completed) GoldLight else Color.White
        )
        Spacer(modifier = Modifier.height(2.dp))
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(if (completed) GoldLight else Color.White.copy(alpha = 0.3f))
        )
    }
}

@Composable
fun QuickActionCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isFeatured: Boolean = false,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = if (isFeatured) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
        border = null,
        tonalElevation = 0.dp,
        modifier = modifier
            .height(78.dp)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isFeatured) GoldAccent else MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = if (isFeatured) EmeraldPrimaryVariant else MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun PrayerTrackerChip(
    name: String,
    checked: Boolean,
    onToggle: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable { onToggle() }
            .padding(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(
                    if (checked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                )
                .border(
                    1.dp,
                    if (checked) GoldAccent else Color.Transparent,
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (checked) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "$name completed",
                    tint = GoldLight,
                    modifier = Modifier.size(20.dp)
                )
            } else {
                Text(
                    text = name.take(1),
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = name,
            style = MaterialTheme.typography.labelSmall,
            color = if (checked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun CitySelectionDialog(
    currentCity: CityPreset,
    onDismiss: () -> Unit,
    onSelect: (CityPreset) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Select City for Prayer Times",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().heightIn(max = 350.dp)
            ) {
                items(PrayerTimeCalculator.POPULAR_CITIES.size) { index ->
                    val city = PrayerTimeCalculator.POPULAR_CITIES[index]
                    val isSelected = city.name == currentCity.name
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color.Transparent)
                            .clickable { onSelect(city) }
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = city.name,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            )
                            Text(
                                text = city.country,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
fun DuaOfTheDayHeroCard(
    dua: DailyDuaItem,
    isPlaying: Boolean,
    onPlayAudio: () -> Unit,
    onExploreAllDuas: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, GoldAccent.copy(alpha = 0.25f)),
        modifier = modifier.fillMaxWidth().testTag("dua_of_the_day_card")
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Top Row: Badge & Category
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = GoldAccent.copy(alpha = 0.18f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.Stars, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(15.dp))
                        Text(
                            text = "DUA OF THE DAY",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.sp,
                                color = EmeraldPrimary
                            )
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = EmeraldPrimary.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = dua.category,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = EmeraldPrimary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Text(
                text = dua.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            // Arabic text box
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = EmeraldPrimary.copy(alpha = 0.06f),
                border = androidx.compose.foundation.BorderStroke(0.8.dp, EmeraldPrimary.copy(alpha = 0.2f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = dua.arabic,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 34.sp
                        ),
                        textAlign = TextAlign.Right,
                        color = EmeraldPrimary,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Transliteration
            Text(
                text = dua.transliteration,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Medium,
                    lineHeight = 18.sp
                ),
                color = GoldAccent
            )

            // Translation
            Text(
                text = "\"${dua.translation}\"",
                style = MaterialTheme.typography.bodyMedium.copy(
                    lineHeight = 22.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )

            // Hadith Reference & Virtue
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Reference: ${dua.reference}",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp, fontWeight = FontWeight.Bold),
                    color = EmeraldPrimary
                )
            }

            if (dua.virtue.isNotBlank()) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "💡 Virtue: ${dua.virtue}",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }

            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilledTonalButton(
                    onClick = onPlayAudio,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = if (isPlaying) GoldAccent else EmeraldPrimary.copy(alpha = 0.15f),
                        contentColor = if (isPlaying) Color.Black else EmeraldPrimary
                    ),
                    modifier = Modifier.testTag("play_dua_audio_button")
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.PauseCircle else Icons.Default.PlayCircle,
                        contentDescription = "Recite Dua",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (isPlaying) "Playing" else "Listen Recitation", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    IconButton(
                        onClick = {
                            val clip = ClipData.newPlainText("Dua", "${dua.title}\n\n${dua.arabic}\n\n${dua.transliteration}\n\n${dua.translation}\n\n(${dua.reference})")
                            val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            cm.setPrimaryClip(clip)
                            Toast.makeText(context, "Dua copied to clipboard!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.testTag("copy_dua_btn")
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy Dua", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
                    }

                    TextButton(
                        onClick = onExploreAllDuas,
                        modifier = Modifier.testTag("explore_all_duas_btn")
                    ) {
                        Text("All Duas", color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun HomeGridModuleCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isFeatured: Boolean = false,
    badgeText: String? = null,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isFeatured) GoldAccent.copy(alpha = 0.12f)
            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
        ),
        border = null,
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .testTag("home_module_${title.lowercase().replace(" ", "_")}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isFeatured) GoldAccent else EmeraldPrimary.copy(alpha = 0.15f),
                    modifier = Modifier.size(38.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = title,
                            tint = if (isFeatured) Color.Black else EmeraldPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                if (badgeText != null) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (badgeText == "PRO") GoldAccent else EmeraldPrimary.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = badgeText,
                            color = if (badgeText == "PRO") Color.Black else EmeraldPrimary,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
    }
}

