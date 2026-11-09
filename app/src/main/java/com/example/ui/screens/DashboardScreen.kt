package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.example.data.model.AllahName
import com.example.data.model.AsmaUlHusnaData
import com.example.data.model.*
import com.example.data.remote.AiCategory
import com.example.data.repository.DailyDuaItem
import com.example.data.repository.DuaOfTheDayRepository
import com.example.data.repository.toDailyDuaItem
import com.example.domain.CityPreset
import com.example.domain.PrayerTimeCalculator
import com.example.ui.components.HadithOfTheDayHeroCard
import com.example.ui.components.UnlockProFeatureDialog
import com.example.ui.screens.names.AllahNameDetailDialog
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

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (fineGranted || coarseGranted) {
            Toast.makeText(context, "Detecting GPS location for prayer times...", Toast.LENGTH_SHORT).show()
            viewModel.detectAndApplyDeviceLocation(context)
        } else {
            Toast.makeText(context, "Location permission denied. Using ${state.selectedCity.name}.", Toast.LENGTH_SHORT).show()
        }
    }

    var currentTimeMs by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(state.prayerSchedule) {
        while (true) {
            currentTimeMs = System.currentTimeMillis()
            kotlinx.coroutines.delay(1000L)
        }
    }
    val liveCountdown = remember(state.prayerSchedule, currentTimeMs) {
        PrayerTimeCalculator.getLiveCountdown(state.prayerSchedule, currentTimeMs)
    }

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
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            IconButton(
                                onClick = {
                                    locationPermissionLauncher.launch(
                                        arrayOf(
                                            Manifest.permission.ACCESS_FINE_LOCATION,
                                            Manifest.permission.ACCESS_COARSE_LOCATION
                                        )
                                    )
                                },
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(if (state.isUsingDeviceLocation) EmeraldPrimary.copy(alpha = 0.18f) else MaterialTheme.colorScheme.surfaceVariant)
                                    .testTag("dashboard_gps_btn")
                            ) {
                                Icon(
                                    imageVector = if (state.isUsingDeviceLocation) Icons.Default.GpsFixed else Icons.Default.MyLocation,
                                    contentDescription = "Detect Device Location",
                                    tint = if (state.isUsingDeviceLocation) EmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { showCityDialog = true }
                                    .padding(horizontal = 6.dp, vertical = 4.dp)
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
                                        text = "${liveCountdown.fastingCountdownLabel} ${liveCountdown.fastingCountdownHMS}",
                                        color = Color.White.copy(alpha = 0.9f),
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
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
                                    text = liveCountdown.nextPrayerName,
                                    style = MaterialTheme.typography.headlineLarge.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White
                                    )
                                )
                                Text(
                                    text = "in ${liveCountdown.formattedHMS} (${liveCountdown.nextPrayerTimeFormatted})",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
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

        // Daily Spiritual Reminders & Consistency Card
        item {
            DashboardSpiritualRemindersCard(
                state = state,
                viewModel = viewModel
            )
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

        // Spiritual Vitality & Goals Card
        item {
            DashboardSpiritualProgressCard(
                state = state,
                onOpenProfile = { viewModel.openUserAccountDialog() }
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
                                        "asma_ul_husna" -> Icons.Default.AutoAwesome
                                        "hajj_umrah" -> Icons.Default.Explore
                                        "qada_salah" -> Icons.Default.Update
                                        "zakat" -> Icons.Default.Savings
                                        "mirath" -> Icons.Default.Calculate
                                        "hijri_calendar" -> Icons.Default.CalendarMonth
                                        "khatam_planner" -> Icons.Default.Book
                                        "ruqyah_healing" -> Icons.Default.Healing
                                        "halal_guide" -> Icons.Default.Restaurant
                                        "baby_names" -> Icons.Default.Face
                                        "haramain_live" -> Icons.Default.LiveTv
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

        // 🌟 Dedicated 99 Names of Allah Entry Card with Preview & Link to Audio-Enabled List
        item {
            NamesOfAllahDashboardCard(
                state = state,
                onPlayAudio = { name -> viewModel.playNamePronunciation(name) },
                onSelectName = { name -> viewModel.selectNameForDetail(name) },
                onViewAllNames = { viewModel.setTab(MainTab.ASMA_UL_HUSNA) }
            )
        }

        // 🌟 Dedicated Hijri Events & Sunnah Fasting Card
        item {
            HijriAndFastingDashboardCard(
                state = state,
                onToggleFast = { id -> viewModel.toggleSunnahFast(id) },
                onViewFullCalendar = { viewModel.setTab(MainTab.HIJRI_CALENDAR) }
            )
        }

        // 🌟 Dedicated Islamic Inheritance (Mirath) Quick Card
        item {
            MirathCalculatorDashboardCard(
                state = state,
                onOpenCalculator = { viewModel.setTab(MainTab.INHERITANCE_CALCULATOR) }
            )
        }

        // 🌟 Dedicated Khatam Quran Target Planner Card
        item {
            KhatamPlannerDashboardCard(
                state = state,
                onAddPages = { pgs -> viewModel.addKhatamPages(pgs) },
                onOpenPlanner = { viewModel.setTab(MainTab.KHATAM_PLANNER) }
            )
        }

        // 🌟 Dedicated Ruqyah & Spiritual Healing Card
        item {
            RuqyahHealingDashboardCard(
                state = state,
                onPlayAudio = { verse -> viewModel.playRuqyahAudio(verse) },
                onOpenRuqyah = { viewModel.setTab(MainTab.RUQYAH_HEALING) }
            )
        }

        // 🌟 Dedicated Hadith of the Day Card (Fetched from curated local repository)
        item {
            Spacer(modifier = Modifier.height(14.dp))
            HadithOfTheDayHeroCard(
                hadith = state.todayHadith,
                dayOffset = state.todayHadithDayOffset,
                onPreviousDay = { viewModel.previousDailyHadith() },
                onNextDay = { viewModel.nextDailyHadith() },
                onResetToToday = { viewModel.resetDailyHadithToToday() },
                onPlayAudio = { viewModel.playHadithAudio(state.todayHadith) },
                onToggleFavorite = { viewModel.toggleFavoriteTodayHadith() },
                onToggleRead = { viewModel.toggleReadTodayHadith() },
                onOpenHadithReader = {
                    viewModel.jumpToHadithCoordinate("${state.todayHadith.collection}:${state.todayHadith.hadithNumber}")
                    viewModel.setTab(MainTab.HADITH_DUAS)
                },
                onAskAi = {
                    viewModel.setTab(MainTab.AI_GUIDE)
                    viewModel.sendChatMessage("Can you explain the historical context, narrator background, and practical daily lessons of this Prophetic Hadith: '${state.todayHadith.translation}' (${state.todayHadith.referenceText})?")
                }
            )
        }

        item {
            Spacer(modifier = Modifier.height(14.dp))

            // 🌟 Featured Dua of the Day Card (Fetched from local Room database)
            val context = LocalContext.current
            val todayDua = state.todayDua?.toDailyDuaItem() ?: remember(state.todayDuaDayOffset) { DuaOfTheDayRepository.getTodayDua(context, state.todayDuaDayOffset) }
            val isPlayingDua = state.playingAudioId == todayDua.id

            DuaOfTheDayHeroCard(
                dua = todayDua,
                isPlaying = isPlayingDua,
                onPlayAudio = {
                    viewModel.playAudio(todayDua.id, todayDua.arabic, todayDua.transliteration)
                },
                onExploreAllDuas = {
                    viewModel.openDuaLibrary()
                },
                onNext = { viewModel.nextDailyDua() },
                onPrevious = { viewModel.previousDailyDua() },
                onToggleFavorite = { viewModel.toggleFavoriteDua(todayDua.id, todayDua.isFavorite) },
                onPractice = { viewModel.incrementDuaCount(todayDua.id, todayDua.recommendedCount) },
                onAskAi = {
                    viewModel.setTab(MainTab.AI_GUIDE)
                    viewModel.sendChatMessage("Can you explain the spiritual meaning, hadith context, and virtues of this supplication: '${todayDua.title}' (${todayDua.reference})?")
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

    if (state.asmaUlHusnaState.isDetailDialogOpen && state.asmaUlHusnaState.selectedNameForDetail != null) {
        val selected = state.asmaUlHusnaState.selectedNameForDetail!!
        AllahNameDetailDialog(
            name = selected,
            isMemorized = state.asmaUlHusnaState.memorizedIds.contains(selected.number),
            isFavorite = state.asmaUlHusnaState.favoriteIds.contains(selected.number),
            dhikrCount = state.asmaUlHusnaState.nameDhikrCounters.getOrDefault(selected.number, 0),
            onDismiss = { viewModel.selectNameForDetail(null) },
            onPlayPronounce = { viewModel.playNamePronunciation(selected) },
            onToggleMemorized = { viewModel.toggleNameMemorized(selected.number) },
            onToggleFavorite = { viewModel.toggleNameFavorite(selected.number) },
            onIncrementDhikr = { viewModel.incrementNameDhikr(selected.number) }
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
    onNext: () -> Unit = {},
    onPrevious: () -> Unit = {},
    onToggleFavorite: () -> Unit = {},
    onPractice: () -> Unit = {},
    onAskAi: () -> Unit = {},
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
            // Top Row: Badge, Database indicator & Previous/Next Navigation
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = GoldAccent.copy(alpha = 0.18f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.Stars, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(14.dp))
                            Text(
                                text = "DUA OF THE DAY",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 0.8.sp,
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
                            text = "Room DB",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                            color = EmeraldPrimary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    IconButton(
                        onClick = onPrevious,
                        modifier = Modifier.size(32.dp).testTag("dua_prev_day_btn")
                    ) {
                        Icon(Icons.Default.ChevronLeft, contentDescription = "Previous Dua", tint = EmeraldPrimary, modifier = Modifier.size(20.dp))
                    }
                    IconButton(
                        onClick = onNext,
                        modifier = Modifier.size(32.dp).testTag("dua_next_day_btn")
                    ) {
                        Icon(Icons.Default.ChevronRight, contentDescription = "Next Dua", tint = EmeraldPrimary, modifier = Modifier.size(20.dp))
                    }
                    IconButton(
                        onClick = onToggleFavorite,
                        modifier = Modifier.size(32.dp).testTag("dua_fav_btn")
                    ) {
                        Icon(
                            imageVector = if (dua.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (dua.isFavorite) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Category & Practice Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                ) {
                    Text(
                        text = dua.category,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (dua.practiceCount >= dua.recommendedCount) GoldAccent.copy(alpha = 0.25f) else EmeraldPrimary.copy(alpha = 0.1f),
                    modifier = Modifier.clip(RoundedCornerShape(12.dp)).clickable { onPractice() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = if (dua.practiceCount >= dua.recommendedCount) Icons.Default.CheckCircle else Icons.Default.TouchApp,
                            contentDescription = null,
                            tint = if (dua.practiceCount >= dua.recommendedCount) GoldAccent else EmeraldPrimary,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = "Practice: ${dua.practiceCount}/${dua.recommendedCount}",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp),
                            color = if (dua.practiceCount >= dua.recommendedCount) GoldAccent else EmeraldPrimary
                        )
                    }
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

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

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

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onAskAi,
                        modifier = Modifier.testTag("ask_ai_dua_btn")
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = "Ask Deen AI", tint = GoldAccent, modifier = Modifier.size(20.dp))
                    }

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

@Composable
fun DashboardSpiritualProgressCard(
    state: DeenUiState,
    onOpenProfile: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progress = remember(state) {
        com.example.domain.SpiritualAchievementsEngine.computeProgressSummary(state, state.spiritualGoals)
    }
    val badges = remember(state) {
        com.example.domain.SpiritualAchievementsEngine.computeBadges(state, state.spiritualGoals)
    }
    val unlockedCount = remember(badges) { badges.count { it.isUnlocked } }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onOpenProfile)
            .testTag("dashboard_spiritual_goals_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header: Title & Vitality Pill
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
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = state.userProfile?.avatarId?.let { com.example.domain.model.UserAvatar.fromId(it).emoji } ?: "🌱",
                                fontSize = 20.sp
                            )
                        }
                    }

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "Spiritual Vitality",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = EmeraldPrimary.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = progress.spiritualRankTitle,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = EmeraldPrimary,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = if (state.userProfile != null) "Assalamu Alaikum, ${state.userProfile.displayName}" else "Track daily Ayahs, Hadith, Salah & Fasting",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Gauge Score badge
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = EmeraldPrimary,
                    modifier = Modifier.padding(start = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "${progress.overallSpiritualVitalityScore}%",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            // Linear Progress Bar for overall vitality
            LinearProgressIndicator(
                progress = { (progress.overallSpiritualVitalityScore / 100f).coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = EmeraldPrimary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            // 5 Mini Module Indicators Grid / Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MiniModuleGoalPill(
                    icon = "📖",
                    label = "Quran",
                    progressText = "${progress.quranAyahsReadToday}/${progress.quranAyahsGoal}",
                    fraction = progress.quranProgressPercentage
                )
                MiniModuleGoalPill(
                    icon = "📜",
                    label = "Hadith",
                    progressText = "${progress.hadithsStudiedCount}/${progress.hadithsStudiedGoal}",
                    fraction = progress.hadithProgressPercentage
                )
                MiniModuleGoalPill(
                    icon = "🤲",
                    label = "Dua",
                    progressText = "${progress.duasMemorizedCount}/${progress.duasMemorizedGoal}",
                    fraction = progress.duaProgressPercentage
                )
                MiniModuleGoalPill(
                    icon = "🕌",
                    label = "Salah",
                    progressText = "${progress.salahOfferedToday}/${state.spiritualGoals.dailySalahTarget}",
                    fraction = progress.salahProgressPercentage
                )
                MiniModuleGoalPill(
                    icon = "🌙",
                    label = "Fasting",
                    progressText = "${progress.fastingDaysThisMonth}/${progress.fastingGoalDays}",
                    fraction = progress.fastingProgressPercentage
                )
            }

            // Footer Row: Badges Unlocked & Call to Action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("🏆", fontSize = 13.sp)
                    Text(
                        text = "$unlockedCount/10 Badges Unlocked",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = "Customize Goals",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = EmeraldPrimary
                    )
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = EmeraldPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun MiniModuleGoalPill(
    icon: String,
    label: String,
    progressText: String,
    fraction: Float,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        modifier = modifier.width(62.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(icon, fontSize = 14.sp)
            Text(
                text = label,
                fontSize = 9.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = progressText,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = if (fraction >= 1f) EmeraldPrimary else MaterialTheme.colorScheme.onSurface
            )
            LinearProgressIndicator(
                progress = { fraction.coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .clip(RoundedCornerShape(1.5.dp)),
                color = if (fraction >= 1f) GoldAccent else EmeraldPrimary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }
    }
}

@Composable
fun NamesOfAllahDashboardCard(
    state: DeenUiState,
    onPlayAudio: (AllahName) -> Unit,
    onSelectName: (AllahName) -> Unit,
    onViewAllNames: () -> Unit,
    modifier: Modifier = Modifier
) {
    val featuredName = remember { AsmaUlHusnaData.getFeaturedName() }
    val previewNames = remember { AsmaUlHusnaData.getPreviewNames(8) }
    val memorizedCount = state.asmaUlHusnaState.memorizedIds.size
    val currentlyPlaying = state.asmaUlHusnaState.currentlyPlayingNumber

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent.copy(alpha = 0.35f)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("names_of_allah_dashboard_card")
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header: Title, Subtitle, Audio Badge, and Memorization Pill
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
                        color = GoldAccent.copy(alpha = 0.18f),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = GoldAccent,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "99 Names of Allah",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = EmeraldPrimary.copy(alpha = 0.15f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.VolumeUp,
                                        contentDescription = null,
                                        tint = EmeraldPrimary,
                                        modifier = Modifier.size(11.dp)
                                    )
                                    Text(
                                        text = "Audio",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = EmeraldPrimary,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                        Text(
                            text = "Asmā' Allāh al-Ḥusnā • 99 Divine Attributes",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Memorization Counter Pill
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                    border = androidx.compose.foundation.BorderStroke(0.5.dp, GoldAccent.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "$memorizedCount/99",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = if (memorizedCount > 0) EmeraldPrimary else MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "learned",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Featured Name Highlight
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.2f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectName(featuredName) }
                    .testTag("featured_name_hero_card")
            ) {
                Box(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = GoldAccent.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "FEATURED ATTRIBUTE #${featuredName.number}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    ),
                                    color = GoldAccent,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }

                            val isPlayingThis = currentlyPlaying == featuredName.number
                            IconButton(
                                onClick = { onPlayAudio(featuredName) },
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(if (isPlayingThis) GoldAccent else EmeraldPrimary.copy(alpha = 0.15f))
                                    .testTag("play_featured_name_audio_button")
                            ) {
                                Icon(
                                    imageVector = if (isPlayingThis) Icons.Default.GraphicEq else Icons.Default.VolumeUp,
                                    contentDescription = "Listen to pronunciation",
                                    tint = if (isPlayingThis) Color.Black else EmeraldPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = featuredName.transliteration,
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = featuredName.meaning,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = featuredName.explanation,
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 2
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Text(
                                text = featuredName.arabic,
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                ),
                                textAlign = TextAlign.End
                            )
                        }
                    }
                }
            }

            // Quick Preview Carousel (Horizontal Scroll)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Quick Preview",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Swipe to explore →",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(horizontal = 2.dp)
                ) {
                    items(previewNames) { name ->
                        val isPlayingThis = currentlyPlaying == name.number
                        val isMemorized = state.asmaUlHusnaState.memorizedIds.contains(name.number)

                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isPlayingThis) GoldAccent else if (isMemorized) EmeraldPrimary.copy(alpha = 0.5f) else Color.Transparent
                            ),
                            modifier = Modifier
                                .width(150.dp)
                                .clickable { onSelectName(name) }
                                .testTag("preview_name_card_${name.number}")
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = EmeraldPrimary.copy(alpha = 0.12f),
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = "${name.number}",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 10.sp,
                                                    color = EmeraldPrimary
                                                )
                                            )
                                        }
                                    }

                                    IconButton(
                                        onClick = { onPlayAudio(name) },
                                        modifier = Modifier
                                            .size(26.dp)
                                            .clip(CircleShape)
                                            .background(if (isPlayingThis) GoldAccent else EmeraldPrimary.copy(alpha = 0.15f))
                                            .testTag("play_preview_name_${name.number}")
                                    ) {
                                        Icon(
                                            imageVector = if (isPlayingThis) Icons.Default.GraphicEq else Icons.Default.VolumeUp,
                                            contentDescription = "Pronounce",
                                            tint = if (isPlayingThis) Color.Black else EmeraldPrimary,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }

                                Text(
                                    text = name.arabic,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    ),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Text(
                                    text = name.transliteration,
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1
                                )

                                Text(
                                    text = name.meaning,
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }

            // Dedicated Link Button to Full Audio-Enabled List
            Button(
                onClick = onViewAllNames,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = EmeraldPrimary,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("view_all_99_names_button")
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Explore All 99 Names (Audio-Enabled)",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun HijriAndFastingDashboardCard(
    state: DeenUiState,
    onToggleFast: (String) -> Unit,
    onViewFullCalendar: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cal = state.hijriCalendarState
    val nextOccasion = remember(cal.currentHijriMonth, cal.currentHijriDay) {
        HijriCalendarData.getUpcomingSacredOccasion(cal.currentHijriMonth, cal.currentHijriDay)
    }
    val daysRemaining = nextOccasion.daysRemaining(cal.currentHijriMonth, cal.currentHijriDay)
    val fasts = remember(cal.currentHijriMonth) {
        HijriCalendarData.getSunnahFastsForMonth(cal.currentHijriMonth).take(3)
    }

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.35f)),
        modifier = modifier.fillMaxWidth().testTag("hijri_dashboard_card")
    ) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Surface(shape = CircleShape, color = EmeraldPrimary.copy(alpha = 0.15f), modifier = Modifier.size(40.dp)) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(22.dp))
                        }
                    }
                    Column {
                        Text("Hijri & Sunnah Fasting", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                        Text("${cal.currentHijriDay} ${HijriCalendarData.HIJRI_MONTHS.getOrNull(cal.currentHijriMonth - 1)?.englishName ?: ""} ${cal.currentHijriYear} AH", style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Surface(shape = RoundedCornerShape(12.dp), color = GoldAccent.copy(alpha = 0.2f)) {
                    Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("🔥", fontSize = 11.sp)
                        Text("${cal.fastingStreakDays}d streak", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = GoldAccent)
                    }
                }
            }

            // Next Occasion Hero Banner
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = EmeraldPrimary.copy(alpha = 0.08f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.2f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("NEXT SACRED OCCASION", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = EmeraldPrimary)
                        Text(nextOccasion.title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                        Text(nextOccasion.arabicTitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text("$daysRemaining", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black, color = EmeraldPrimary))
                        Text("days left", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            // Quick Sunnah Fast Checklist
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Upcoming Sunnah Fasts", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                fasts.forEach { fast ->
                    val isDone = cal.completedFastIds.contains(fast.id)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                            .clickable { onToggleFast(fast.id) }
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(fast.title, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold))
                        Icon(
                            imageVector = if (isDone) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                            contentDescription = "Fast Toggle",
                            tint = if (isDone) EmeraldPrimary else MaterialTheme.colorScheme.outlineVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Button
            Button(
                onClick = onViewFullCalendar,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                modifier = Modifier.fillMaxWidth().testTag("view_full_hijri_calendar_button")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Explore 12-Month Calendar & Sacred Days", fontWeight = FontWeight.Bold)
                    Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
fun MirathCalculatorDashboardCard(
    state: DeenUiState,
    onOpenCalculator: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent.copy(alpha = 0.35f)),
        modifier = modifier.fillMaxWidth().testTag("mirath_dashboard_card")
    ) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Surface(shape = CircleShape, color = GoldAccent.copy(alpha = 0.2f), modifier = Modifier.size(40.dp)) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Calculate, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(22.dp))
                        }
                    }
                    Column {
                        Text("Inheritance (Mirath) Calculator", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                        Text("Surah An-Nisa 4:11-12 • 4-Madhab Jurisprudence", style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Surface(shape = RoundedCornerShape(6.dp), color = EmeraldPrimary.copy(alpha = 0.15f)) {
                    Text("4 Madhabs", color = EmeraldPrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                }
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Pre-Distribution Deductions Supported:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                    Text("1. Funeral Costs (Tajheez) → 2. Unpaid Debts/Mahr → 3. Wasiyyah (Max 1/3) → 4. Heirs Shares", style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Button(
                onClick = onOpenCalculator,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                modifier = Modifier.fillMaxWidth().testTag("open_mirath_calculator_button")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Calculate Estate Distribution (Hanafi, Maliki, Shafi'i, Hanbali)", fontWeight = FontWeight.Bold)
                    Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
fun KhatamPlannerDashboardCard(
    state: DeenUiState,
    onAddPages: (Int) -> Unit,
    onOpenPlanner: () -> Unit,
    modifier: Modifier = Modifier
) {
    val planner = state.khatamPlannerState

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.35f)),
        modifier = modifier.fillMaxWidth().testTag("khatam_dashboard_card")
    ) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Surface(shape = CircleShape, color = EmeraldPrimary.copy(alpha = 0.15f), modifier = Modifier.size(40.dp)) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Book, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(22.dp))
                        }
                    }
                    Column {
                        Text("Khatam Quran Planner", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                        Text("Target: ${planner.selectedTargetDays} Days • Juz ${planner.currentJuz}", style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Surface(shape = RoundedCornerShape(6.dp), color = GoldAccent.copy(alpha = 0.2f)) {
                    Text("${(planner.completionPercentage * 100).toInt()}% Done", color = GoldAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                }
            }

            // Progress Bar
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("${planner.completedPages} / ${planner.totalQuranPages} Pages read", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                    Text("${planner.pagesRemaining} left", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                LinearProgressIndicator(
                    progress = { planner.completionPercentage },
                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                    color = EmeraldPrimary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }

            // Quick log actions
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = { onAddPages(1) },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("+1 Page", fontSize = 11.sp)
                }
                OutlinedButton(
                    onClick = { onAddPages(4) },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("+4 Pgs (1 Salah)", fontSize = 11.sp)
                }
                Button(
                    onClick = onOpenPlanner,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                    modifier = Modifier.weight(1f).testTag("dashboard_open_khatam_button")
                ) {
                    Text("Planner →", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun RuqyahHealingDashboardCard(
    state: DeenUiState,
    onPlayAudio: (RuqyahVerse) -> Unit,
    onOpenRuqyah: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sampleVerse = remember { RuqyahData.VERSES[0] } // Al-Fatihah
    val isPlaying = state.ruqyahState.currentlyPlayingVerseId == sampleVerse.id

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent.copy(alpha = 0.35f)),
        modifier = modifier.fillMaxWidth().testTag("ruqyah_dashboard_card")
    ) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Surface(shape = CircleShape, color = GoldAccent.copy(alpha = 0.2f), modifier = Modifier.size(40.dp)) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Healing, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(22.dp))
                        }
                    }
                    Column {
                        Text("Ruqyah & Spiritual Healing", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                        Text("Ayat ash-Shifa • Authentic Prophetic Protection", style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                IconButton(
                    onClick = { onPlayAudio(sampleVerse) },
                    modifier = Modifier.size(36.dp).clip(CircleShape).background(if (isPlaying) GoldAccent else EmeraldPrimary.copy(alpha = 0.15f))
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.GraphicEq else Icons.Default.VolumeUp,
                        contentDescription = "Listen",
                        tint = if (isPlaying) Color.Black else EmeraldPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(sampleVerse.title, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                    Text(sampleVerse.arabic, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), textAlign = TextAlign.End, color = MaterialTheme.colorScheme.primary, modifier = Modifier.fillMaxWidth())
                    Text(sampleVerse.translation, style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp), color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2)
                }
            }

            Button(
                onClick = onOpenRuqyah,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                modifier = Modifier.fillMaxWidth().testTag("open_ruqyah_suite_button")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Explore All Healing Verses & Prophetic Method", fontWeight = FontWeight.Bold)
                    Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
fun DashboardSpiritualRemindersCard(
    state: DeenUiState,
    viewModel: DeenViewModel
) {
    val context = LocalContext.current
    val notifPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            viewModel.toggleGlobalPrayerAlerts(true, context)
            viewModel.toggleQuranReminder(true, context)
            Toast.makeText(context, "Spiritual reminders enabled!", Toast.LENGTH_SHORT).show()
        }
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.22f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("dashboard_spiritual_reminders_card")
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header
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
                        color = EmeraldPrimary.copy(alpha = 0.12f),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = null,
                                tint = EmeraldPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Column {
                        Text(
                            text = "Daily Spiritual Reminders",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Automatic local alerts for Salah & Quran recitation",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(
                    onClick = { viewModel.setTab(MainTab.SETTINGS) },
                    modifier = Modifier.size(32.dp).testTag("dashboard_reminders_settings_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Customize Reminders",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))

            // Row 1: Prayer Time Alerts
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("🕌", fontSize = 18.sp)
                    Column {
                        Text(
                            text = "Prayer Time Alerts",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (state.prayerAlertsEnabled) state.nextScheduledAlertText.ifEmpty { "Active for ${state.selectedCity.name}" } else "Alerts turned off",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = if (state.prayerAlertsEnabled) EmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(
                        onClick = {
                            viewModel.triggerTestPrayerNotification(context, state.prayerSchedule.nextPrayerName)
                            Toast.makeText(context, "Test Adhan alert fired!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.size(28.dp).testTag("dashboard_test_prayer_alert_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "Test Prayer Alert",
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Switch(
                        checked = state.prayerAlertsEnabled,
                        onCheckedChange = { checked ->
                            if (checked && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                                notifPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                            } else {
                                viewModel.toggleGlobalPrayerAlerts(checked, context)
                            }
                        },
                        modifier = Modifier.testTag("dashboard_prayer_alerts_switch")
                    )
                }
            }

            // Row 2: Daily Quran Reading Reminder
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("📖", fontSize = 18.sp)
                    Column {
                        Text(
                            text = "Daily Quran Recitation",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (state.quranReminderSettings.enabled)
                                "${state.quranReminderSettings.formattedTime} • ${state.quranReminderSettings.dailyPagesTarget} pages target"
                            else
                                "Daily reading reminder turned off",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = if (state.quranReminderSettings.enabled) GoldAccent else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(
                        onClick = {
                            viewModel.triggerTestQuranReminder(context)
                            Toast.makeText(context, "Test Quran reminder fired!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.size(28.dp).testTag("dashboard_test_quran_reminder_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = "Test Quran Reminder",
                            tint = GoldAccent,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Switch(
                        checked = state.quranReminderSettings.enabled,
                        onCheckedChange = { checked ->
                            if (checked && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                                notifPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                            } else {
                                viewModel.toggleQuranReminder(checked, context)
                            }
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = GoldAccent
                        ),
                        modifier = Modifier.testTag("dashboard_quran_reminder_switch")
                    )
                }
            }

            // Footer pills & actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (state.quranReminderSettings.notifyFridayKahf) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = GoldAccent.copy(alpha = 0.12f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text("✨", fontSize = 11.sp)
                            Text(
                                text = "Friday Surah Al-Kahf Alert Active",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }

                TextButton(
                    onClick = { viewModel.setTab(MainTab.SETTINGS) },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "Customize Routine →",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = EmeraldPrimary
                    )
                }
            }
        }
    }
}



