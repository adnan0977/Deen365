package com.example.ui.screens.namaz

import android.Manifest
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.AsrJuristicMethod
import com.example.domain.PrayerSchedule
import com.example.domain.PrayerTimeCalculator
import com.example.notification.AlertSoundMode
import com.example.ui.theme.EmeraldAccent
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.EmeraldPrimaryVariant
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldLight
import com.example.ui.util.adaptiveFocusable
import com.example.ui.viewmodel.DeenUiState
import com.example.ui.viewmodel.DeenViewModel
import com.example.ui.viewmodel.MainTab
import kotlinx.coroutines.delay

/**
 * Modern Prayer Times Module featuring:
 * 1. Location API integration to fetch local GPS coordinates with reverse geocoding & manual fallback.
 * 2. Real-time dynamic live countdown ticker to the next prayer (ticking every second).
 * 3. Complete daily Namaz timetable (Fajr, Sunrise, Dhuhr, Asr, Maghrib, Isha, Qiyam).
 * 4. Current prayer active window indicator & circular interval progress gauge.
 * 5. Notification Adhan sound controls & prayer completion tracking.
 */
@Composable
fun PrayerTimesModuleView(
    state: DeenUiState,
    viewModel: DeenViewModel,
    onOpenCityPicker: () -> Unit,
    onOpenMethodPicker: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val schedule = state.prayerSchedule
    val isDetecting = state.isDetectingLocation
    val isGpsActive = state.isUsingDeviceLocation

    // 1. Android Location API Permission Launcher (FINE & COARSE)
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (fineGranted || coarseGranted) {
            Toast.makeText(context, "Fetching local GPS coordinates for prayer times...", Toast.LENGTH_SHORT).show()
            viewModel.detectAndApplyDeviceLocation(context)
        } else {
            Toast.makeText(context, "Location permission denied. Using ${state.selectedCity.name}.", Toast.LENGTH_LONG).show()
        }
    }

    // 2. Notification Permission Launcher (API 33+)
    val notifPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            viewModel.toggleGlobalPrayerAlerts(true, context)
            Toast.makeText(context, "Adhan prayer alerts enabled!", Toast.LENGTH_SHORT).show()
        } else {
            viewModel.toggleGlobalPrayerAlerts(false, context)
            Toast.makeText(context, "Notification permission is required for Adhan alarms.", Toast.LENGTH_SHORT).show()
        }
    }

    // 3. Live 1-Second Countdown Ticker Clock
    var currentTimeMs by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(schedule) {
        while (true) {
            currentTimeMs = System.currentTimeMillis()
            delay(1000L)
        }
    }

    val liveCountdown = remember(schedule, currentTimeMs) {
        PrayerTimeCalculator.getLiveCountdown(schedule, currentTimeMs)
    }

    val coordinatesStr = remember(state.selectedCity.latitude, state.selectedCity.longitude) {
        PrayerTimeCalculator.formatCoordinates(state.selectedCity.latitude, state.selectedCity.longitude)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("prayer_times_module_view"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // ==========================================
        // SECTION 1: Location API & Coordinates Card
        // ==========================================
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(
                1.dp,
                if (isGpsActive) EmeraldPrimary.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("prayer_location_card")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Top Row: Location Status Pill & Quick Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Status Badge (GPS vs City Preset)
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isGpsActive) EmeraldPrimary.copy(alpha = 0.14f) else GoldAccent.copy(alpha = 0.16f),
                        border = BorderStroke(
                            0.5.dp,
                            if (isGpsActive) EmeraldPrimary.copy(alpha = 0.4f) else GoldAccent.copy(alpha = 0.4f)
                        ),
                        modifier = Modifier.testTag("location_status_badge")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Icon(
                                imageVector = if (isGpsActive) Icons.Default.GpsFixed else Icons.Default.LocationCity,
                                contentDescription = null,
                                tint = if (isGpsActive) EmeraldPrimary else GoldAccent,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = if (isDetecting) "Acquiring GPS..." else if (isGpsActive) "Live GPS Coordinates" else "Selected City Preset",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = if (isGpsActive) EmeraldPrimary else GoldAccent
                                )
                            )
                        }
                    }

                    // Action buttons: "Detect GPS" and "Change City"
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Button(
                            onClick = {
                                locationPermissionLauncher.launch(
                                    arrayOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    )
                                )
                            },
                            enabled = !isDetecting,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isGpsActive) EmeraldPrimary else MaterialTheme.colorScheme.primaryContainer,
                                contentColor = if (isGpsActive) Color.White else MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("btn_detect_gps_coordinates")
                        ) {
                            if (isDetecting) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(14.dp),
                                    strokeWidth = 2.dp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(Modifier.width(4.dp))
                                Text("Locating...", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            } else {
                                Icon(Icons.Default.MyLocation, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Auto GPS", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        OutlinedButton(
                            onClick = onOpenCityPicker,
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("btn_open_city_picker")
                        ) {
                            Icon(Icons.Default.EditLocation, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("City", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                // Middle: Locality & Geocoded Coordinates Display
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = EmeraldPrimary.copy(alpha = 0.12f),
                        modifier = Modifier.size(42.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (isGpsActive) Icons.Default.SatelliteAlt else Icons.Default.Place,
                                contentDescription = "Location Pin",
                                tint = EmeraldPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${state.selectedCity.name}, ${state.selectedCity.country}",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Coordinates: $coordinatesStr • UTC ${if (state.selectedCity.timezone >= 0) "+${state.selectedCity.timezone}" else state.selectedCity.timezone}",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Bottom strip: Calculation Method & Juristic Asr Pill
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenMethodPicker() }
                        .testTag("calculation_method_summary_pill")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.Tune, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(13.dp))
                            Text(
                                text = "${state.selectedCalculationMethod.title.split(",").firstOrNull() ?: state.selectedCalculationMethod.name} • ${state.asrJuristicMethod.displayName.split(" ").first()}",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = "Settings",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = EmeraldPrimary)
                        )
                    }
                }
            }
        }

        // ========================================================
        // SECTION 2: Real-Time Live Countdown Card to Next Prayer
        // ========================================================
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("live_prayer_countdown_card")
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(EmeraldPrimary, EmeraldPrimaryVariant, Color(0xFF0F5132))
                        )
                    )
                    .padding(20.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Header: Active prayer & Upcoming Label
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = GoldAccent.copy(alpha = 0.25f),
                            border = BorderStroke(1.dp, GoldAccent.copy(alpha = 0.6f))
                        ) {
                            Text(
                                text = "NEXT PRAYER COUNTDOWN",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 1.sp,
                                    color = GoldLight
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }

                        // Current active prayer indicator
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color.White.copy(alpha = 0.16f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .clip(CircleShape)
                                        .background(GoldAccent)
                                )
                                Text(
                                    text = "${liveCountdown.currentPrayerName} is Active",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }
                        }
                    }

                    // Middle: Next Prayer Title & Digital Ticker
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = liveCountdown.nextPrayerName,
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                            )
                            Text(
                                text = "Starts at ${liveCountdown.nextPrayerTimeFormatted}",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = GoldLight,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }

                        // Digital 3-box Countdown Ticker: [HH] [MM] [SS]
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CountdownBox(value = "%02d".format(liveCountdown.hours), label = "HRS")
                            Text(":", color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                            CountdownBox(value = "%02d".format(liveCountdown.minutes), label = "MIN")
                            Text(":", color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                            CountdownBox(value = "%02d".format(liveCountdown.seconds), label = "SEC", isHighlight = true)
                        }
                    }

                    // Progress bar showing elapsed interval
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        LinearProgressIndicator(
                            progress = { liveCountdown.progressFraction },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = GoldAccent,
                            trackColor = Color.White.copy(alpha = 0.2f)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Elapsed: ${(liveCountdown.progressFraction * 100).toInt()}%",
                                style = MaterialTheme.typography.labelSmall.copy(color = Color.White.copy(alpha = 0.8f), fontSize = 10.sp)
                            )
                            Text(
                                text = "Window closes at ${liveCountdown.nextPrayerTimeFormatted}",
                                style = MaterialTheme.typography.labelSmall.copy(color = GoldLight, fontSize = 10.sp)
                            )
                        }
                    }

                    // Fasting (Sawm) Sub-strip
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.Black.copy(alpha = 0.18f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = if (liveCountdown.isFastingActive) Icons.Default.WbSunny else Icons.Default.NightsStay,
                                    contentDescription = null,
                                    tint = GoldAccent,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = if (liveCountdown.isFastingActive) "Sawm (Fasting) Ongoing" else "Night / Suhoor Window",
                                    style = MaterialTheme.typography.labelMedium.copy(color = Color.White, fontWeight = FontWeight.Bold)
                                )
                            }
                            Text(
                                text = "${liveCountdown.fastingCountdownLabel} ${liveCountdown.fastingCountdownHMS}",
                                style = MaterialTheme.typography.labelMedium.copy(color = GoldLight, fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }
            }
        }

        // ==========================================
        // SECTION 3: Full Daily Namaz Timetable
        // ==========================================
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("daily_namaz_timetable_card")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Table Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Daily Namaz Timetable",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "5 Obligatory Salah & Sunnah Times",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = EmeraldPrimary.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = "${state.prayerRecord.completedCount}/5 Offered Today",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = EmeraldPrimary,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f))

                // Build full prayers list
                val prayerItems = remember(schedule, state.prayerRecord, state.prayerAlertsEnabled, liveCountdown) {
                    listOf(
                        PrayerRowItem(
                            id = "fajr",
                            name = "Fajr",
                            arabic = "الفجر",
                            time = schedule.fajr,
                            rakats = "2 Sunnah • 2 Fard",
                            icon = Icons.Default.WbTwilight,
                            isFard = true,
                            isCompleted = state.prayerRecord.fajr,
                            isAlertEnabled = state.prayerAlertsEnabled && state.notifyFajr,
                            isCurrent = liveCountdown.currentPrayerName.equals("Fajr", ignoreCase = true),
                            isNext = liveCountdown.nextPrayerName.equals("Fajr", ignoreCase = true)
                        ),
                        PrayerRowItem(
                            id = "sunrise",
                            name = "Sunrise (Shuruq)",
                            arabic = "الشروق",
                            time = schedule.sunrise,
                            rakats = "Fajr ends • Ishraq follows",
                            icon = Icons.Default.WbSunny,
                            isFard = false,
                            isCompleted = false,
                            isAlertEnabled = state.prayerAlertsEnabled && state.notifySunrise,
                            isCurrent = liveCountdown.currentPrayerName.equals("Duha", ignoreCase = true) || liveCountdown.currentPrayerName.contains("Ishraq", ignoreCase = true),
                            isNext = liveCountdown.nextPrayerName.equals("Sunrise", ignoreCase = true)
                        ),
                        PrayerRowItem(
                            id = "dhuhr",
                            name = "Dhuhr",
                            arabic = "الظهر",
                            time = schedule.dhuhr,
                            rakats = "4 Sunnah • 4 Fard • 2 Sunnah",
                            icon = Icons.Default.WbSunny,
                            isFard = true,
                            isCompleted = state.prayerRecord.dhuhr,
                            isAlertEnabled = state.prayerAlertsEnabled && state.notifyDhuhr,
                            isCurrent = liveCountdown.currentPrayerName.equals("Dhuhr", ignoreCase = true),
                            isNext = liveCountdown.nextPrayerName.equals("Dhuhr", ignoreCase = true)
                        ),
                        PrayerRowItem(
                            id = "asr",
                            name = "Asr",
                            arabic = "العصر",
                            time = schedule.asr,
                            rakats = "4 Fard (${state.asrJuristicMethod.displayName.split(" ").first()})",
                            icon = Icons.Default.Brightness5,
                            isFard = true,
                            isCompleted = state.prayerRecord.asr,
                            isAlertEnabled = state.prayerAlertsEnabled && state.notifyAsr,
                            isCurrent = liveCountdown.currentPrayerName.equals("Asr", ignoreCase = true),
                            isNext = liveCountdown.nextPrayerName.equals("Asr", ignoreCase = true)
                        ),
                        PrayerRowItem(
                            id = "maghrib",
                            name = "Maghrib (Iftar)",
                            arabic = "المغرب",
                            time = schedule.maghrib,
                            rakats = "3 Fard • 2 Sunnah",
                            icon = Icons.Default.WbTwilight,
                            isFard = true,
                            isCompleted = state.prayerRecord.maghrib,
                            isAlertEnabled = state.prayerAlertsEnabled && state.notifyMaghrib,
                            isCurrent = liveCountdown.currentPrayerName.equals("Maghrib", ignoreCase = true),
                            isNext = liveCountdown.nextPrayerName.equals("Maghrib", ignoreCase = true)
                        ),
                        PrayerRowItem(
                            id = "isha",
                            name = "Isha & Witr",
                            arabic = "العشاء",
                            time = schedule.isha,
                            rakats = "4 Fard • 2 Sunnah • 3 Witr",
                            icon = Icons.Default.Bedtime,
                            isFard = true,
                            isCompleted = state.prayerRecord.isha,
                            isAlertEnabled = state.prayerAlertsEnabled && state.notifyIsha,
                            isCurrent = liveCountdown.currentPrayerName.equals("Isha", ignoreCase = true),
                            isNext = liveCountdown.nextPrayerName.equals("Isha", ignoreCase = true)
                        )
                    )
                }

                // Render each row
                prayerItems.forEach { item ->
                    val mosqueTiming = state.mosqueTimings.find { it.prayerId.equals(item.name, ignoreCase = true) }
                    PrayerItemRow(
                        item = item,
                        mosqueTiming = mosqueTiming,
                        onTogglePrayer = {
                            if (item.isFard) {
                                viewModel.togglePrayer(item.name)
                            }
                        },
                        onToggleAlert = { enabled ->
                            viewModel.toggleIndividualPrayerAlert(item.name, enabled, context)
                        },
                        onOpenMosqueSettings = {
                            viewModel.openMosqueTimingsDialog(true)
                        }
                    )
                }
            }
        }

        // ==========================================
        // SECTION 4: Quick Controls & Adhan Alarms
        // ==========================================
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("prayer_settings_quick_card")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Adhan Alarms Header & Switch
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
                            modifier = Modifier.size(34.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.VolumeUp, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(18.dp))
                            }
                        }
                        Column {
                            Text("Scheduled Adhan Audio Alarms", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                            Text("Play full Adhan or soft chime at prayer times", style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                        modifier = Modifier.testTag("switch_global_prayer_alerts")
                    )
                }

                // Asr Juristic School Switcher (Standard vs Hanafi)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Asr Juristic Calculation:",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(
                            AsrJuristicMethod.STANDARD to "Standard (Shafi'i)",
                            AsrJuristicMethod.HANAFI to "Hanafi"
                        ).forEach { (method, label) ->
                            val isSelected = state.asrJuristicMethod == method
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.selectAsrJuristicMethod(method) },
                                label = { Text(label, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = EmeraldPrimary.copy(alpha = 0.18f),
                                    selectedLabelColor = EmeraldPrimary
                                ),
                                modifier = Modifier.testTag("asr_method_chip_${method.name.lowercase()}")
                            )
                        }
                    }
                }

                // Quick Action Buttons: Test Notification & Qibla Shortcut
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            viewModel.triggerTestPrayerNotification(context, liveCountdown.nextPrayerName)
                            Toast.makeText(context, "Test alert triggered for ${liveCountdown.nextPrayerName}!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_test_prayer_notification"),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Test Notification", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { viewModel.setTab(MainTab.QIBLA) },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_open_qibla_compass"),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(Icons.Default.Explore, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Qibla Compass", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun CountdownBox(
    value: String,
    label: String,
    isHighlight: Boolean = false
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (isHighlight) GoldAccent.copy(alpha = 0.22f) else Color.White.copy(alpha = 0.14f),
        border = BorderStroke(1.dp, if (isHighlight) GoldAccent else Color.White.copy(alpha = 0.25f)),
        modifier = Modifier.widthIn(min = 44.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    color = if (isHighlight) GoldLight else Color.White
                )
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 8.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isHighlight) GoldLight else Color.White.copy(alpha = 0.75f)
                )
            )
        }
    }
}

private data class PrayerRowItem(
    val id: String,
    val name: String,
    val arabic: String,
    val time: String,
    val rakats: String,
    val icon: ImageVector,
    val isFard: Boolean,
    val isCompleted: Boolean,
    val isAlertEnabled: Boolean,
    val isCurrent: Boolean,
    val isNext: Boolean
)

@Composable
private fun PrayerItemRow(
    item: PrayerRowItem,
    mosqueTiming: com.example.domain.MosquePrayerTiming?,
    onTogglePrayer: () -> Unit,
    onToggleAlert: (Boolean) -> Unit,
    onOpenMosqueSettings: () -> Unit
) {
    val isMosqueActive = mosqueTiming?.isMosqueTimingActive == true
    val displayTime = if (isMosqueActive && mosqueTiming != null) mosqueTiming.manualMosqueTime else item.time

    val containerColor = when {
        item.isCurrent -> EmeraldPrimary.copy(alpha = 0.12f)
        item.isNext -> GoldAccent.copy(alpha = 0.12f)
        else -> Color.Transparent
    }

    val borderColor = when {
        item.isCurrent -> EmeraldPrimary
        item.isNext -> GoldAccent
        else -> Color.Transparent
    }

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = containerColor,
        border = if (item.isCurrent || item.isNext) BorderStroke(1.dp, borderColor) else null,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("prayer_row_${item.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Icon & Prayer details
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                Surface(
                    shape = CircleShape,
                    color = when {
                        item.isCurrent -> EmeraldPrimary
                        item.isNext -> GoldAccent
                        item.isCompleted -> EmeraldAccent
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    },
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.name,
                            tint = when {
                                item.isCurrent || item.isCompleted -> Color.White
                                item.isNext -> Color.Black
                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = item.name,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = if (item.isCurrent || item.isNext) FontWeight.ExtraBold else FontWeight.SemiBold
                            ),
                            color = when {
                                item.isCurrent -> EmeraldPrimary
                                item.isNext -> MaterialTheme.colorScheme.onSurface
                                else -> MaterialTheme.colorScheme.onSurface
                            }
                        )

                        Text(
                            text = item.arabic,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        )

                        if (item.isCurrent) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = EmeraldPrimary
                            ) {
                                Text(
                                    text = "ACTIVE",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color.White,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Black
                                    ),
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        } else if (item.isNext) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = GoldAccent
                            ) {
                                Text(
                                    text = "NEXT",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color.Black,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Black
                                    ),
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                    }

                    Text(
                        text = item.rakats,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Right: Time, Adhan Alert, and Tracker Checkbox
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = displayTime,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = if (item.isCurrent || item.isNext) FontWeight.ExtraBold else FontWeight.Bold
                        ),
                        color = if (item.isCurrent) EmeraldPrimary else MaterialTheme.colorScheme.onSurface
                    )

                    if (isMosqueActive) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = EmeraldPrimary.copy(alpha = 0.15f),
                            modifier = Modifier
                                .clickable { onOpenMosqueSettings() }
                                .testTag("mosque_time_tag_${item.id}")
                        ) {
                            Text(
                                text = "🕌 Iqamah: $displayTime",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldPrimary,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }
                }

                // Alert Sound Toggle
                IconButton(
                    onClick = { onToggleAlert(!item.isAlertEnabled) },
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("bell_${item.id}")
                        .testTag("btn_alert_${item.id}")
                ) {
                    Icon(
                        imageVector = if (item.isAlertEnabled) Icons.Default.NotificationsActive else Icons.Default.NotificationsOff,
                        contentDescription = "Adhan Alert",
                        tint = if (item.isAlertEnabled) GoldAccent else MaterialTheme.colorScheme.outlineVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Fard Prayer completion tracker checkbox
                if (item.isFard) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (item.isCompleted) EmeraldPrimary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        border = if (item.isCompleted) null else BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onTogglePrayer() }
                            .testTag("check_${item.id}")
                            .testTag("chk_prayer_${item.id}")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            if (item.isCompleted) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Offered",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                } else {
                    Spacer(modifier = Modifier.size(32.dp))
                }
            }
        }
    }
}
