package com.example.ui.screens

import android.hardware.SensorManager
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.CityPreset
import com.example.domain.PrayerTimeCalculator
import com.example.ui.components.FastingHistoryLogView
import com.example.ui.components.FastingMonthProgressCard
import com.example.ui.components.FastingTrackerCard
import com.example.ui.theme.EmeraldAccent
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.EmeraldPrimaryVariant
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldLight
import com.example.ui.viewmodel.DeenUiState
import com.example.ui.viewmodel.DeenViewModel
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun NamazAndFastingScreen(
    state: DeenUiState,
    viewModel: DeenViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: Timetable & Qibla, 1: Interactive Namaz Guide
    var showCityDialog by remember { mutableStateOf(false) }

    // Activate compass sensors when this screen is active, and deactivate on dispose
    DisposableEffect(selectedTab) {
        if (selectedTab == 0) {
            viewModel.startQiblaCompass()
        } else {
            viewModel.stopQiblaCompass()
        }
        onDispose {
            viewModel.stopQiblaCompass()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Screen Header with Settings Icon on Top Right
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Namaz & Fasting",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
                Text(
                    text = "${state.selectedCity.name} • ${state.hijriDate.monthName} ${state.hijriDate.year} AH",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(
                onClick = { viewModel.openSettings() },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .testTag("namaz_screen_settings_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Top Sub-Tabs
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Timetable", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(16.dp)) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Namaz Guide", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.Accessibility, contentDescription = null, modifier = Modifier.size(16.dp)) }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text("Fasting Guide", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(16.dp)) }
            )
            Tab(
                selected = selectedTab == 3,
                onClick = { selectedTab = 3 },
                text = { Text("Fasting Log", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.NightsStay, contentDescription = null, modifier = Modifier.size(16.dp)) }
            )
        }

        when (selectedTab) {
            0 -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Quick Action Banner to Namaz Guide
                    item {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedTab = 1 }
                                .testTag("banner_to_namaz_guide")
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(38.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(Icons.Default.Accessibility, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                                        }
                                    }
                                    Column {
                                        Text(
                                            text = "Interactive Namaz Guide",
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = "Step-by-step Salah with audio recitation & meanings",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Icon(Icons.Default.ChevronRight, contentDescription = "Open guide", tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }

                    // Quick Action Banner to Fasting Guide
                    item {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = GoldAccent.copy(alpha = 0.12f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent.copy(alpha = 0.4f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedTab = 2 }
                                .testTag("banner_to_fasting_guide")
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = GoldAccent,
                                        modifier = Modifier.size(38.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(Icons.Default.NightsStay, contentDescription = null, tint = EmeraldPrimaryVariant, modifier = Modifier.size(20.dp))
                                        }
                                    }
                                    Column {
                                        Text(
                                            text = "Detailed Fasting Guide",
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                            color = EmeraldPrimaryVariant
                                        )
                                        Text(
                                            text = "Suhoor, Iftar, rules, exemptions, and authentic Duas",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Icon(Icons.Default.ChevronRight, contentDescription = "Open fasting guide", tint = GoldAccent)
                            }
                        }
                    }
        // Screen Title & City Selector
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Namaz & Qibla",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                    Text(
                        text = "Real-time Magnetometer & Accelerometer Qibla Finder",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                FilledTonalButton(
                    onClick = { showCityDialog = true },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(state.selectedCity.name)
                }
            }
        }

        // Qibla Direction Finder (Magnetometer + Accelerometer)
        item {
            QiblaCompassCard(
                state = state,
                onRecalibrate = { viewModel.startQiblaCompass() }
            )
        }

        // Fasting Banner (Suhoor & Iftar)
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth().testTag("fasting_banner")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "SUHOOR ENDS (IMSAK)",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = state.prayerSchedule.suhoorEnd,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                    }

                    Box(
                        modifier = Modifier
                            .height(40.dp)
                            .width(1.dp)
                            .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "IFTAR (MAGHRIB)",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = GoldAccent
                        )
                        Text(
                            text = state.prayerSchedule.iftar,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = GoldAccent
                            )
                        )
                    }
                }
            }
        }

        // Fasting Tracker Toggle & Sawm Logger
        item {
            FastingTrackerCard(state = state, viewModel = viewModel)
        }

        // Today's Timetable List
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Today's Prayer Schedule",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    PrayerScheduleRow("Fajr (Dawn)", state.prayerSchedule.fajr, state.prayerRecord.fajr) { viewModel.togglePrayer("Fajr") }
                    PrayerScheduleRow("Sunrise", state.prayerSchedule.sunrise, false, isNonPrayer = true) {}
                    PrayerScheduleRow("Dhuhr (Noon)", state.prayerSchedule.dhuhr, state.prayerRecord.dhuhr) { viewModel.togglePrayer("Dhuhr") }
                    PrayerScheduleRow("Asr (Afternoon)", state.prayerSchedule.asr, state.prayerRecord.asr) { viewModel.togglePrayer("Asr") }
                    PrayerScheduleRow("Maghrib (Sunset)", state.prayerSchedule.maghrib, state.prayerRecord.maghrib) { viewModel.togglePrayer("Maghrib") }
                    PrayerScheduleRow("Isha (Night)", state.prayerSchedule.isha, state.prayerRecord.isha) { viewModel.togglePrayer("Isha") }
                }
            }
        }

        // Fasting Intentions & Duas
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Fasting Duas (Roza)",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Sehri Niyyah (Intention for Fasting)",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "وَبِصَوْمِ غَدٍ نَوَيْتُ مِنْ شَهْرِ رَمَضَانَ",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Right
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = "Wa bisawmi ghadin nawaytu min shahri Ramadan",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Iftar Dua (Breaking the Fast)",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = GoldAccent
                    )
                    Text(
                        text = "ذَهَبَ الظَّمَأُ وَابْتَلَّتِ الْعُرُوقُ وَثَبَتَ الأَجْرُ إِنْ شَاءَ اللَّهُ",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Right
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = "Dhahabadh-dhama'u wabtallatil-'urooqu wa thabatal-ajru in sha'Allah",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
1 -> {
                NamazGuideView(state = state, viewModel = viewModel)
            }
            2 -> {
                FastingGuideView(state = state, viewModel = viewModel)
            }
            3 -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        FastingHistoryLogView(state = state, viewModel = viewModel)
                    }
                    item {
                        FastingMonthProgressCard(state = state, viewModel = viewModel)
                    }
                    item {
                        FastingTrackerCard(state = state, viewModel = viewModel)
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
}

@Composable
fun QiblaCompassCard(
    state: DeenUiState,
    onRecalibrate: () -> Unit
) {
    val animatedHeading by animateFloatAsState(
        targetValue = state.deviceHeading,
        animationSpec = spring(stiffness = 200f),
        label = "heading_anim"
    )

    val cardBorderColor by animateColorAsState(
        targetValue = if (state.isQiblaAligned) EmeraldAccent else GoldAccent.copy(alpha = 0.4f),
        label = "border_color"
    )

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = androidx.compose.foundation.BorderStroke(2.dp, cardBorderColor),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("qibla_compass_card")
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header with Location & Bearing
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Qibla Direction (Kaaba)",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                    Text(
                        text = "Bearing: ${state.qiblaBearing.toInt()}° from North (${state.selectedCity.name})",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (state.isQiblaAligned) EmeraldAccent.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (state.isQiblaAligned) EmeraldAccent else GoldAccent)
                        )
                        Text(
                            text = if (state.isQiblaAligned) "FACING KAABA" else "${state.deviceHeading.toInt()}°",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (state.isQiblaAligned) EmeraldPrimary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Main Rotating Compass Dial
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .border(
                        width = if (state.isQiblaAligned) 4.dp else 2.dp,
                        color = if (state.isQiblaAligned) EmeraldAccent else GoldAccent,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Compass Canvas Drawing
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val center = Offset(size.width / 2, size.height / 2)
                    val radius = size.minDimension / 2

                    // Rotate entire compass dial based on current device heading
                    rotate(degrees = -animatedHeading, pivot = center) {
                        // Draw 360 degree tick marks
                        for (degree in 0 until 360 step 30) {
                            val angleRad = Math.toRadians((degree - 90).toDouble())
                            val tickLength = if (degree % 90 == 0) 18f else 10f
                            val strokeW = if (degree % 90 == 0) 3f else 1.5f
                            val tickColor = if (degree == 0) Color.Red else Color.Gray

                            val startX = (center.x + (radius - tickLength) * cos(angleRad)).toFloat()
                            val startY = (center.y + (radius - tickLength) * sin(angleRad)).toFloat()
                            val endX = (center.x + radius * cos(angleRad)).toFloat()
                            val endY = (center.y + radius * sin(angleRad)).toFloat()

                            drawLine(
                                color = tickColor,
                                start = Offset(startX, startY),
                                end = Offset(endX, endY),
                                strokeWidth = strokeW,
                                cap = StrokeCap.Round
                            )
                        }

                        // Draw North Pointer Needle (Red)
                        val northAngleRad = Math.toRadians(-90.0)
                        val northX = (center.x + (radius - 30f) * cos(northAngleRad)).toFloat()
                        val northY = (center.y + (radius - 30f) * sin(northAngleRad)).toFloat()
                        drawLine(
                            color = Color.Red,
                            start = center,
                            end = Offset(northX, northY),
                            strokeWidth = 3f,
                            cap = StrokeCap.Round
                        )

                        // Draw Qibla Direction Needle towards Kaaba (Emerald & Gold)
                        val qiblaAngleRad = Math.toRadians((state.qiblaBearing - 90.0))
                        val qiblaX = (center.x + (radius - 20f) * cos(qiblaAngleRad)).toFloat()
                        val qiblaY = (center.y + (radius - 20f) * sin(qiblaAngleRad)).toFloat()

                        // Arrowhead
                        drawLine(
                            color = EmeraldPrimary,
                            start = center,
                            end = Offset(qiblaX, qiblaY),
                            strokeWidth = 7f,
                            cap = StrokeCap.Round
                        )
                    }

                    // Static Top Aiming Indicator (fixed at 12 o'clock where phone top is pointed)
                    val topIndicatorPath = Path().apply {
                        moveTo(center.x, 10f)
                        lineTo(center.x - 12f, 32f)
                        lineTo(center.x + 12f, 32f)
                        close()
                    }
                    drawPath(
                        path = topIndicatorPath,
                        color = if (state.isQiblaAligned) EmeraldAccent else GoldAccent,
                        style = Fill
                    )
                }

                // Central Kaaba Indicator Emblem
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(if (state.isQiblaAligned) EmeraldPrimary else MaterialTheme.colorScheme.primary)
                        .border(
                            2.dp,
                            if (state.isQiblaAligned) GoldLight else GoldAccent,
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (state.isQiblaAligned) Icons.Default.Check else Icons.Default.Explore,
                        contentDescription = "Kaaba",
                        tint = GoldLight,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Guidance Instruction Banner
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = if (state.isQiblaAligned) {
                    EmeraldPrimary
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = when {
                            state.isQiblaAligned -> Icons.Default.CheckCircle
                            state.angleDifferenceToQibla > 0 -> Icons.Default.RotateRight
                            else -> Icons.Default.RotateLeft
                        },
                        contentDescription = null,
                        tint = if (state.isQiblaAligned) GoldLight else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )

                    Column {
                        Text(
                            text = when {
                                state.isQiblaAligned -> "ALHAMDULILLAH! YOU ARE FACING QIBLA"
                                state.angleDifferenceToQibla > 0 -> "Turn ${abs(state.angleDifferenceToQibla).toInt()}° to the Right"
                                else -> "Turn ${abs(state.angleDifferenceToQibla).toInt()}° to the Left"
                            },
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = if (state.isQiblaAligned) Color.White else MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (state.isQiblaAligned) "Align your prayer rug in this exact direction" else "Rotate slowly until the top needle points to Kaaba",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (state.isQiblaAligned) Color.White.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Tilt / Level Warning
            if (!state.isDeviceFlat) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = GoldAccent.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ScreenRotation,
                            contentDescription = "Tilt Warning",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Hold phone flat horizontally for maximum compass precision",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Sensor Telemetry Badges
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = state.qiblaSensorData.sensorTypeUsed,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = when (state.compassAccuracy) {
                        SensorManager.SENSOR_STATUS_ACCURACY_HIGH -> "Accuracy: High (${state.qiblaSensorData.magneticFieldStrength.toInt()} µT)"
                        SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM -> "Medium (${state.qiblaSensorData.magneticFieldStrength.toInt()} µT)"
                        SensorManager.SENSOR_STATUS_ACCURACY_LOW -> "Calibrate (8-motion)"
                        else -> "Calibrate in Figure-8"
                    },
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = if (state.compassAccuracy >= SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM) EmeraldPrimary else GoldAccent
                )
            }
        }
    }
}

@Composable
fun PrayerScheduleRow(
    name: String,
    time: String,
    completed: Boolean,
    isNonPrayer: Boolean = false,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(enabled = !isNonPrayer) { onToggle() }
            .padding(vertical = 8.dp, horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (!isNonPrayer) {
                Checkbox(
                    checked = completed,
                    onCheckedChange = { onToggle() },
                    colors = CheckboxDefaults.colors(
                        checkedColor = EmeraldPrimary,
                        checkmarkColor = GoldLight
                    ),
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.WbSunny,
                    contentDescription = null,
                    tint = GoldAccent,
                    modifier = Modifier.size(24.dp)
                )
            }
            Text(
                text = name,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = if (completed) FontWeight.Bold else FontWeight.Normal
                ),
                color = if (completed) EmeraldPrimary else MaterialTheme.colorScheme.onSurface
            )
        }

        Text(
            text = time,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = if (completed) GoldAccent else MaterialTheme.colorScheme.onSurface
        )
    }
}
