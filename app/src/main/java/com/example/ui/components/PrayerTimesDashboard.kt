package com.example.ui.components

import android.Manifest
import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.CalculationMethod
import com.example.domain.CityPreset
import com.example.domain.PrayerSchedule
import com.example.domain.PrayerTimeCalculator
import com.example.notification.PrayerNotificationSchedulerService
import com.example.ui.screens.namaz.CalculationMethodDialog
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Material 3 Prayer Times Dashboard displaying:
 * 1. Current Live Digital Time (with seconds ticking), Gregorian & Hijri dates.
 * 2. Hero Upcoming Prayer Banner with start time & high-precision dynamic countdown.
 * 3. Daily 5 Prayers Timetable Cards with completion tracking & individual alert toggles.
 * 4. WorkManager Background Service status card with calculation method selection,
 *    on-demand background sync, and test local notification triggers.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrayerTimesDashboard(
    state: DeenUiState,
    viewModel: DeenViewModel,
    modifier: Modifier = Modifier,
    onOpenCitySelector: () -> Unit = {},
    onOpenMethodSelector: () -> Unit = {}
) {
    val context = LocalContext.current
    val schedule = state.prayerSchedule
    val isGpsActive = state.isUsingDeviceLocation
    val isDetecting = state.isDetectingLocation

    var showMethodPicker by remember { mutableStateOf(false) }

    // Live 1-second dynamic clock & countdown ticker
    var currentTimeMs by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            currentTimeMs = System.currentTimeMillis()
            delay(1000L)
        }
    }

    val liveCountdown = remember(schedule, currentTimeMs) {
        PrayerTimeCalculator.getLiveCountdown(schedule, currentTimeMs)
    }

    val formattedCurrentTime = remember(currentTimeMs) {
        SimpleDateFormat("hh:mm:ss a", Locale.getDefault()).format(Date(currentTimeMs))
    }

    val formattedCurrentDate = remember(currentTimeMs) {
        SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault()).format(Date(currentTimeMs))
    }

    // Read WorkManager execution status from SharedPreferences
    val schedulerPrefs = remember(context, currentTimeMs) {
        PrayerNotificationSchedulerService.getPrefs(context)
    }
    val lastSyncSummary = remember(schedulerPrefs, currentTimeMs) {
        schedulerPrefs.getString(
            PrayerNotificationSchedulerService.KEY_LAST_SUMMARY,
            "Periodic WorkManager service active for ${state.selectedCity.name}"
        ) ?: "Periodic WorkManager service active"
    }
    val lastSyncTimestamp = remember(schedulerPrefs, currentTimeMs) {
        schedulerPrefs.getString(
            PrayerNotificationSchedulerService.KEY_LAST_TIMESTAMP,
            "Active"
        ) ?: "Active"
    }

    // Permission launchers
    val locationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fine = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarse = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (fine || coarse) {
            Toast.makeText(context, "Detecting GPS coordinates for prayer times...", Toast.LENGTH_SHORT).show()
            viewModel.detectAndApplyDeviceLocation(context)
        } else {
            Toast.makeText(context, "Location permission denied. Using ${state.selectedCity.name}.", Toast.LENGTH_SHORT).show()
        }
    }

    val notifLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            viewModel.toggleGlobalPrayerAlerts(true, context)
            Toast.makeText(context, "Prayer notifications enabled!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Notification permission required for Adhan alarms.", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("prayer_times_dashboard"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // =========================================================================
        // 1. Current Time & Location Header Bar Card
        // =========================================================================
        ElevatedCard(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("current_time_display")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Top Row: Location chip & Alerts bell
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Single GPS Location chip (auto-detects and refreshes GPS coordinates)
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isGpsActive) EmeraldPrimary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                        border = BorderStroke(
                            1.dp,
                            if (isGpsActive) EmeraldPrimary.copy(alpha = 0.35f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                        ),
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                locationLauncher.launch(
                                    arrayOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    )
                                )
                            }
                            .testTag("dashboard_location_chip")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = if (isGpsActive) Icons.Default.GpsFixed else Icons.Default.LocationOn,
                                contentDescription = "GPS Location",
                                tint = if (isGpsActive) EmeraldPrimary else MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Column {
                                Text(
                                    text = if (isDetecting) "Detecting GPS..." else state.selectedCity.name,
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (isGpsActive) "GPS Location Active • Tap to refresh" else "${state.selectedCity.country} • Tap to fetch GPS",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // Quick Actions Row: GPS Detect, Alerts Toggle & Settings
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        IconButton(
                            onClick = {
                                locationLauncher.launch(
                                    arrayOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    )
                                )
                            },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                .testTag("btn_detect_gps")
                        ) {
                            Icon(
                                imageVector = Icons.Default.MyLocation,
                                contentDescription = "Detect GPS",
                                tint = EmeraldPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        IconButton(
                            onClick = {
                                if (!state.prayerAlertsEnabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    notifLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                } else {
                                    viewModel.toggleGlobalPrayerAlerts(!state.prayerAlertsEnabled, context)
                                    val msg = if (!state.prayerAlertsEnabled) "Adhan prayer alerts enabled" else "Prayer alerts disabled"
                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(
                                    if (state.prayerAlertsEnabled) EmeraldPrimary.copy(alpha = 0.15f)
                                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                )
                                .testTag("btn_toggle_alerts")
                        ) {
                            Icon(
                                imageVector = if (state.prayerAlertsEnabled) Icons.Default.NotificationsActive else Icons.Default.NotificationsOff,
                                contentDescription = "Prayer Alerts",
                                tint = if (state.prayerAlertsEnabled) EmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f))

                // Bottom Row: Active Calculation Method & Quick Method Picker Shortcut
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onOpenMethodSelector() }
                            .padding(vertical = 2.dp, horizontal = 4.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = EmeraldPrimary.copy(alpha = 0.12f),
                            modifier = Modifier.size(26.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Tune,
                                    contentDescription = null,
                                    tint = EmeraldPrimary,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                        Column {
                            Text(
                                text = "CALCULATION METHOD",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.sp,
                                    letterSpacing = 0.5.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = state.selectedCalculationMethod.title,
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                color = EmeraldPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (state.prayerAlertsEnabled) EmeraldPrimary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant,
                        border = BorderStroke(0.5.dp, if (state.prayerAlertsEnabled) EmeraldPrimary.copy(alpha = 0.3f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = if (state.prayerAlertsEnabled) Icons.Default.NotificationsActive else Icons.Default.NotificationsOff,
                                contentDescription = null,
                                tint = if (state.prayerAlertsEnabled) EmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = if (state.prayerAlertsEnabled) "Alerts ON" else "Silent",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (state.prayerAlertsEnabled) EmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }
                }
            }
        }

        // =========================================================================
        // 2. Hero Upcoming Prayer & Countdown Card (Material 3 Card with Gradient)
        // =========================================================================
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("upcoming_prayer_card")
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(EmeraldPrimary, EmeraldPrimaryVariant)
                        )
                    )
                    .padding(22.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Header Row: Next Prayer Pill & Fasting Status
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = GoldAccent.copy(alpha = 0.22f),
                            border = BorderStroke(1.dp, GoldAccent.copy(alpha = 0.6f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccessTimeFilled,
                                    contentDescription = null,
                                    tint = GoldLight,
                                    modifier = Modifier.size(13.dp)
                                )
                                Text(
                                    text = "UPCOMING PRAYER",
                                    color = GoldLight,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        }

                        // Fasting countdown badge
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color.White.copy(alpha = 0.15f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Icon(
                                    imageVector = if (schedule.isFastingActive) Icons.Default.NightsStay else Icons.Default.WbSunny,
                                    contentDescription = null,
                                    tint = GoldLight,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "${liveCountdown.fastingCountdownLabel} ${liveCountdown.fastingCountdownHMS}",
                                    color = Color.White,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        }
                    }

                    // Main Countdown & Next Prayer Info
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = liveCountdown.nextPrayerName.uppercase(Locale.US),
                                    style = MaterialTheme.typography.headlineLarge.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 32.sp
                                    ),
                                    color = Color.White
                                )
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color.White.copy(alpha = 0.18f)
                                ) {
                                    Text(
                                        text = getArabicNameForPrayer(liveCountdown.nextPrayerName),
                                        color = GoldLight,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        ),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "At ${liveCountdown.nextPrayerTimeFormatted} • ${getRakatsForPrayer(liveCountdown.nextPrayerName)}",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = GoldLight
                            )
                        }

                        // Countdown Timer Pill
                        Column(
                            horizontalAlignment = Alignment.End,
                            modifier = Modifier.testTag("next_prayer_countdown")
                        ) {
                            Text(
                                text = "STARTS IN",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp,
                                    fontSize = 10.sp
                                ),
                                color = Color.White.copy(alpha = 0.7f)
                            )
                            Text(
                                text = liveCountdown.formattedHMS,
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 1.sp
                                ),
                                color = Color.White
                            )
                        }
                    }

                    // Window Interval Progress Bar
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Current: ${liveCountdown.currentPrayerName}",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.75f)
                            )
                            Text(
                                text = "${(liveCountdown.progressFraction * 100).toInt()}% elapsed",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = GoldLight
                            )
                        }

                        LinearProgressIndicator(
                            progress = { liveCountdown.progressFraction.coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = GoldAccent,
                            trackColor = Color.White.copy(alpha = 0.2f)
                        )
                    }
                }
            }
        }

        // =========================================================================
        // 3. Five Daily Prayers Grid / Carousel Cards
        // =========================================================================
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Daily Namaz Timetable",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                TextButton(
                    onClick = { viewModel.setTab(MainTab.NAMAZ_FASTING) },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text("Full View", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            val prayerCards = listOf(
                PrayerCardData("Fajr", "الفجر", schedule.fajr, state.prayerRecord.fajr, Icons.Default.WbTwilight, "2 Fard"),
                PrayerCardData("Sunrise", "الشروق", schedule.sunrise, false, Icons.Default.WbSunny, "Shuruq", isSunnahOnly = true),
                PrayerCardData("Dhuhr", "الظهر", schedule.dhuhr, state.prayerRecord.dhuhr, Icons.Default.LightMode, "4 Fard"),
                PrayerCardData("Asr", "العصر", schedule.asr, state.prayerRecord.asr, Icons.Default.WbSunny, "4 Fard"),
                PrayerCardData("Maghrib", "المغرب", schedule.maghrib, state.prayerRecord.maghrib, Icons.Default.WbTwilight, "3 Fard"),
                PrayerCardData("Isha", "العشاء", schedule.isha, state.prayerRecord.isha, Icons.Default.NightsStay, "4 Fard")
            )

            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val isWide = maxWidth >= 520.dp
                val columns = if (isWide) 6 else 3
                val rows = prayerCards.chunked(columns)

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    rows.forEach { rowItems ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowItems.forEach { item ->
                                val isNext = item.name.equals(liveCountdown.nextPrayerName, ignoreCase = true)
                                val isCurrent = item.name.equals(liveCountdown.currentPrayerName, ignoreCase = true)

                                PrayerMiniCard(
                                    data = item,
                                    isNext = isNext,
                                    isCurrent = isCurrent,
                                    onToggle = { if (!item.isSunnahOnly) viewModel.togglePrayer(item.name) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }
        }

        // =========================================================================
        // 4. WorkManager Background Service & Calculation Method Card
        // =========================================================================
        OutlinedCard(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.outlinedCardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            ),
            border = BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.25f)),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("workmanager_status_card")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header: Service Name & Live Pulsing Indicator
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = EmeraldAccent.copy(alpha = 0.2f),
                            modifier = Modifier.size(24.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Surface(
                                    shape = CircleShape,
                                    color = EmeraldAccent,
                                    modifier = Modifier.size(10.dp)
                                ) {}
                            }
                        }

                        Column {
                            Text(
                                text = "WorkManager Background Service",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Recurring 6h Location-Aware Prayer Scheduler",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = EmeraldPrimary.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = "ACTIVE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = EmeraldPrimary,
                                fontSize = 10.sp
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                // Calculation Method Details & Config
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (onOpenMethodSelector != {}) onOpenMethodSelector() else showMethodPicker = true
                            }
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Configured Calculation Method",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = state.selectedCalculationMethod.title,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "Fajr: ${state.selectedCalculationMethod.fajrAngle}° • Isha: ${state.selectedCalculationMethod.ishaAngle}° • ${state.asrJuristicMethod.displayName}",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Change Method",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Summary from background scheduler service
                Text(
                    text = lastSyncSummary,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Action Buttons: Sync WorkManager Now & Test Notification
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilledTonalButton(
                        onClick = {
                            viewModel.refreshPrayerWorkManagerSync(context)
                            Toast.makeText(context, "WorkManager background sync enqueued!", Toast.LENGTH_SHORT).show()
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_sync_workmanager")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sync,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Sync WorkManager", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            viewModel.triggerTestPrayerNotification(context, liveCountdown.nextPrayerName)
                            Toast.makeText(context, "Triggered Adhan alert for ${liveCountdown.nextPrayerName}!", Toast.LENGTH_SHORT).show()
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EmeraldPrimary,
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_test_notification")
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Test Notification", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    if (showMethodPicker) {
        CalculationMethodDialog(
            currentMethod = state.selectedCalculationMethod,
            onSelect = { method ->
                viewModel.selectCalculationMethod(method)
                showMethodPicker = false
                Toast.makeText(context, "Updated to ${method.title}", Toast.LENGTH_SHORT).show()
            },
            onDismiss = { showMethodPicker = false }
        )
    }
}

private data class PrayerCardData(
    val name: String,
    val arabicName: String,
    val time: String,
    val isCompleted: Boolean,
    val icon: ImageVector,
    val rakats: String,
    val isSunnahOnly: Boolean = false
)

@Composable
private fun PrayerMiniCard(
    data: PrayerCardData,
    isNext: Boolean,
    isCurrent: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when {
        isNext -> EmeraldPrimary
        isCurrent -> EmeraldPrimary.copy(alpha = 0.10f)
        data.isCompleted -> EmeraldPrimary.copy(alpha = 0.08f)
        else -> MaterialTheme.colorScheme.surface
    }

    val contentColor = when {
        isNext -> Color.White
        data.isCompleted -> EmeraldPrimary
        else -> MaterialTheme.colorScheme.onSurface
    }

    // Clean, subtle border (never harsh or thick on touch)
    val borderColor = when {
        isNext -> GoldAccent.copy(alpha = 0.65f)
        isCurrent -> EmeraldPrimary.copy(alpha = 0.45f)
        data.isCompleted -> EmeraldPrimary.copy(alpha = 0.35f)
        else -> MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)
    }

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = backgroundColor,
        border = BorderStroke(0.75.dp, borderColor),
        shadowElevation = if (isNext) 2.dp else 0.dp,
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable(
                interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                indication = ripple(bounded = true, color = if (isNext) Color.White else EmeraldPrimary)
            ) { onToggle() }
            .testTag("prayer_card_${data.name.lowercase(Locale.US)}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Top Row: Icon circle badge & status badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = if (isNext) Color.White.copy(alpha = 0.2f) else EmeraldPrimary.copy(alpha = 0.1f),
                    modifier = Modifier.size(24.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = data.icon,
                            contentDescription = null,
                            tint = if (isNext) GoldLight else EmeraldPrimary,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }

                when {
                    isNext -> {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = GoldAccent
                        ) {
                            Text(
                                text = "NEXT",
                                color = EmeraldPrimaryVariant,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }
                    isCurrent -> {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = EmeraldPrimary
                        ) {
                            Text(
                                text = "NOW",
                                color = Color.White,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }
                    data.isCompleted -> {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = EmeraldPrimary.copy(alpha = 0.15f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Offered",
                                    tint = EmeraldPrimary,
                                    modifier = Modifier.size(10.dp)
                                )
                                Text(
                                    text = "OFFERED",
                                    color = EmeraldPrimary,
                                    fontSize = 7.5.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                    }
                    else -> {
                        Text(
                            text = data.rakats,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            maxLines = 1
                        )
                    }
                }
            }

            // Prayer Name: English & Arabic side by side
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = data.name,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = if (isNext) FontWeight.ExtraBold else FontWeight.Bold,
                        fontSize = 12.sp
                    ),
                    color = contentColor,
                    maxLines = 1
                )
                Text(
                    text = data.arabicName,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = if (isNext) Color.White.copy(alpha = 0.85f) else EmeraldPrimary,
                    maxLines = 1
                )
            }

            // Time Pill
            Surface(
                shape = RoundedCornerShape(7.dp),
                color = if (isNext) Color.White.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = data.time,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 11.sp,
                        letterSpacing = 0.2.sp
                    ),
                    color = if (isNext) GoldLight else MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}

private fun getArabicNameForPrayer(name: String): String {
    return when (name.lowercase(Locale.US)) {
        "fajr" -> "الفجر"
        "sunrise", "shuruq" -> "الشروق"
        "dhuhr" -> "الظهر"
        "asr" -> "العصر"
        "maghrib" -> "المغرب"
        "isha" -> "العشاء"
        else -> "الصلاة"
    }
}

private fun getRakatsForPrayer(name: String): String {
    return when (name.lowercase(Locale.US)) {
        "fajr" -> "2 Fard Rak'ats"
        "sunrise", "shuruq" -> "Shuruq Time"
        "dhuhr" -> "4 Fard Rak'ats"
        "asr" -> "4 Fard Rak'ats"
        "maghrib" -> "3 Fard Rak'ats"
        "isha" -> "4 Fard Rak'ats"
        else -> "Fard Prayer"
    }
}
