package com.example.ui.components

import android.Manifest
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.AsrJuristicMethod
import com.example.domain.CalculationMethod
import com.example.domain.CityPreset
import com.example.domain.PrayerSchedule
import com.example.ui.theme.EmeraldAccent
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.EmeraldPrimaryVariant
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldLight
import com.example.ui.util.adaptiveFocusable
import com.example.ui.viewmodel.DeenUiState
import com.example.ui.viewmodel.DeenViewModel

data class PrayerTimeItem(
    val name: String,
    val time: String,
    val arabicName: String,
    val isCompleted: Boolean,
    val isNext: Boolean,
    val isCurrent: Boolean,
    val rakats: String,
    val isAlertEnabled: Boolean = true
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyPrayerScheduleCard(
    state: DeenUiState,
    viewModel: DeenViewModel,
    modifier: Modifier = Modifier,
    onOpenCitySelector: () -> Unit = {},
    onOpenMethodSelector: () -> Unit = {}
) {
    val context = LocalContext.current
    val schedule = state.prayerSchedule
    val isDetecting = state.isDetectingLocation
    val isGpsActive = state.isUsingDeviceLocation

    // Runtime location permission launcher for Device GPS calculation
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (fineGranted || coarseGranted) {
            Toast.makeText(context, "Detecting device location for prayer times...", Toast.LENGTH_SHORT).show()
            viewModel.detectAndApplyDeviceLocation(context)
        } else {
            Toast.makeText(context, "Location permission denied. Using ${state.selectedCity.name}.", Toast.LENGTH_SHORT).show()
        }
    }

    // Runtime notification permission launcher (Android 13+ / API 33)
    val notifPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            viewModel.toggleGlobalPrayerAlerts(true, context)
            Toast.makeText(context, "Prayer alerts enabled for ${state.selectedCity.name}!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Notification permission is required for Adhan alerts.", Toast.LENGTH_SHORT).show()
        }
    }

    val prayerItems = remember(
        schedule,
        state.prayerRecord,
        state.prayerAlertsEnabled,
        state.notifyFajr,
        state.notifySunrise,
        state.notifyDhuhr,
        state.notifyAsr,
        state.notifyMaghrib,
        state.notifyIsha
    ) {
        listOf(
            PrayerTimeItem(
                name = "Fajr",
                time = schedule.fajr,
                arabicName = "الفجر",
                isCompleted = state.prayerRecord.fajr,
                isNext = schedule.nextPrayerName.equals("Fajr", ignoreCase = true),
                isCurrent = schedule.currentPrayerName.equals("Fajr", ignoreCase = true),
                rakats = "2 Fard",
                isAlertEnabled = state.prayerAlertsEnabled && state.notifyFajr
            ),
            PrayerTimeItem(
                name = "Sunrise",
                time = schedule.sunrise,
                arabicName = "الشروق",
                isCompleted = false,
                isNext = schedule.nextPrayerName.equals("Sunrise", ignoreCase = true),
                isCurrent = schedule.currentPrayerName.equals("Duha", ignoreCase = true),
                rakats = "Shuruq",
                isAlertEnabled = state.prayerAlertsEnabled && state.notifySunrise
            ),
            PrayerTimeItem(
                name = "Dhuhr",
                time = schedule.dhuhr,
                arabicName = "الظهر",
                isCompleted = state.prayerRecord.dhuhr,
                isNext = schedule.nextPrayerName.equals("Dhuhr", ignoreCase = true),
                isCurrent = schedule.currentPrayerName.equals("Dhuhr", ignoreCase = true),
                rakats = "4 Fard",
                isAlertEnabled = state.prayerAlertsEnabled && state.notifyDhuhr
            ),
            PrayerTimeItem(
                name = "Asr",
                time = schedule.asr,
                arabicName = "العصر",
                isCompleted = state.prayerRecord.asr,
                isNext = schedule.nextPrayerName.equals("Asr", ignoreCase = true),
                isCurrent = schedule.currentPrayerName.equals("Asr", ignoreCase = true),
                rakats = "4 Fard",
                isAlertEnabled = state.prayerAlertsEnabled && state.notifyAsr
            ),
            PrayerTimeItem(
                name = "Maghrib",
                time = schedule.maghrib,
                arabicName = "المغرب",
                isCompleted = state.prayerRecord.maghrib,
                isNext = schedule.nextPrayerName.equals("Maghrib", ignoreCase = true),
                isCurrent = schedule.currentPrayerName.equals("Maghrib", ignoreCase = true),
                rakats = "3 Fard",
                isAlertEnabled = state.prayerAlertsEnabled && state.notifyMaghrib
            ),
            PrayerTimeItem(
                name = "Isha",
                time = schedule.isha,
                arabicName = "العشاء",
                isCompleted = state.prayerRecord.isha,
                isNext = schedule.nextPrayerName.equals("Isha", ignoreCase = true),
                isCurrent = schedule.currentPrayerName.equals("Isha", ignoreCase = true),
                rakats = "4 Fard",
                isAlertEnabled = state.prayerAlertsEnabled && state.notifyIsha
            )
        )
    }

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isGpsActive) EmeraldPrimary.copy(alpha = 0.4f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        ),
        modifier = modifier
            .fillMaxWidth()
            .adaptiveFocusable(shape = RoundedCornerShape(24.dp))
            .testTag("daily_prayer_schedule_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Bar: Location, GPS status, and Hijri Date
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isGpsActive) EmeraldPrimary.copy(alpha = 0.15f) else GoldAccent.copy(alpha = 0.15f),
                            modifier = Modifier.clickable {
                                locationPermissionLauncher.launch(
                                    arrayOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    )
                                )
                            }.testTag("gps_detect_chip")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = if (isGpsActive) Icons.Default.GpsFixed else Icons.Default.LocationOn,
                                    contentDescription = "Device Location",
                                    tint = if (isGpsActive) EmeraldPrimary else GoldAccent,
                                    modifier = Modifier.size(13.dp)
                                )
                                Text(
                                    text = if (isDetecting) "Locating..." else if (isGpsActive) "GPS Location" else "Selected City",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp,
                                        color = if (isGpsActive) EmeraldPrimary else GoldAccent
                                    )
                                )
                            }
                        }

                        Text(
                            text = state.selectedCity.name,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${state.hijriDate.day} ${state.hijriDate.monthName} ${state.hijriDate.year} AH • ${state.hijriDate.gregorianFormatted}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Location selector, Refresh GPS, & Prayer Alerts buttons
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    IconButton(
                        onClick = {
                            if (!state.prayerAlertsEnabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                notifPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            } else {
                                viewModel.toggleGlobalPrayerAlerts(!state.prayerAlertsEnabled, context)
                                val msg = if (!state.prayerAlertsEnabled) "Prayer alerts enabled" else "Prayer alerts turned off"
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(if (state.prayerAlertsEnabled) EmeraldPrimary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant)
                            .testTag("btn_toggle_prayer_alerts")
                    ) {
                        Icon(
                            imageVector = if (state.prayerAlertsEnabled) Icons.Default.NotificationsActive else Icons.Default.NotificationsOff,
                            contentDescription = "Prayer Alerts",
                            tint = if (state.prayerAlertsEnabled) EmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }

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
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .testTag("btn_detect_gps_location")
                    ) {
                        Icon(
                            imageVector = Icons.Default.MyLocation,
                            contentDescription = "Use Device Location",
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = onOpenCitySelector,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .testTag("btn_open_city_selector")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Change City / Calculation",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Notification Schedule Status Pill
            AnimatedVisibility(visible = state.prayerAlertsEnabled && state.nextScheduledAlertText.isNotEmpty()) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = EmeraldPrimary.copy(alpha = 0.08f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.2f)),
                    modifier = Modifier.fillMaxWidth().testTag("prayer_alerts_status_banner")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = null,
                                tint = EmeraldPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = state.nextScheduledAlertText,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = EmeraldPrimary,
                                maxLines = 1
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = GoldAccent.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = if (state.reminderOffsetMinutes > 0) "${state.reminderOffsetMinutes}m before" else state.alertSoundMode.title,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            // Hero Next Prayer Countdown Banner
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("hero_next_prayer_banner")
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(EmeraldPrimary, EmeraldPrimaryVariant)
                            )
                        )
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = GoldAccent.copy(alpha = 0.25f)
                                ) {
                                    Text(
                                        text = "NEXT PRAYER",
                                        color = GoldLight,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }

                                if (schedule.isFastingActive) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color.White.copy(alpha = 0.15f)
                                    ) {
                                        Text(
                                            text = "Fasting Active",
                                            color = Color.White,
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = schedule.nextPrayerName,
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                            Text(
                                text = "Starts in ${schedule.timeRemainingNextPrayer}",
                                style = MaterialTheme.typography.bodyMedium.copy(color = GoldLight)
                            )
                        }

                        // Right: Suhoor/Iftar countdown summary
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = if (schedule.isFastingActive) "Iftar at ${schedule.iftar}" else "Suhoor ends ${schedule.suhoorEnd}",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                            Text(
                                text = if (schedule.isFastingActive) "${schedule.timeRemainingIftar} remaining" else "Dawn: ${schedule.fajr}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            )
                        }
                    }
                }
            }

            // Horizontal / Grid Prayer Times Timeline
            Text(
                text = "Daily Namaz Timetable",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            // Responsive Row / Wrap of the 6 Prayer Times
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("prayer_schedule_chips_row"),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                prayerItems.forEach { item ->
                    val isFardPrayer = item.name != "Sunrise"
                    val isChecked = item.isCompleted

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = when {
                            item.isNext -> EmeraldPrimary.copy(alpha = 0.15f)
                            item.isCurrent -> GoldAccent.copy(alpha = 0.15f)
                            isChecked -> EmeraldAccent.copy(alpha = 0.12f)
                            else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                        },
                        border = androidx.compose.foundation.BorderStroke(
                            width = if (item.isNext || item.isCurrent) 1.5.dp else 1.dp,
                            color = when {
                                item.isNext -> EmeraldPrimary
                                item.isCurrent -> GoldAccent
                                isChecked -> EmeraldPrimary.copy(alpha = 0.5f)
                                else -> Color.Transparent
                            }
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 2.dp)
                            .clickable(enabled = isFardPrayer) {
                                if (isFardPrayer) {
                                    viewModel.togglePrayer(item.name)
                                }
                            }
                            .adaptiveFocusable(shape = RoundedCornerShape(14.dp))
                            .testTag("prayer_slot_${item.name.lowercase()}")
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Text(
                                    text = item.name,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = if (item.isNext) EmeraldPrimary else MaterialTheme.colorScheme.onSurface
                                    ),
                                    maxLines = 1
                                )
                                if (item.isAlertEnabled) {
                                    Icon(
                                        imageVector = Icons.Default.Notifications,
                                        contentDescription = "Alert active",
                                        tint = if (item.isNext) EmeraldPrimary else GoldAccent,
                                        modifier = Modifier.size(9.dp)
                                    )
                                }
                            }

                            Text(
                                text = item.time,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 11.sp,
                                    color = if (item.isNext) EmeraldPrimary else MaterialTheme.colorScheme.onSurface
                                ),
                                maxLines = 1
                            )

                            if (isFardPrayer) {
                                Icon(
                                    imageVector = if (isChecked) Icons.Default.CheckCircle else Icons.Outlined.CheckCircle,
                                    contentDescription = if (isChecked) "Offered" else "Mark Offered",
                                    tint = if (isChecked) EmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                                    modifier = Modifier.size(16.dp)
                                )
                            } else {
                                Text(
                                    text = item.arabicName,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 9.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Calculation Methodology & Asr School Footer Pill
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenMethodSelector() }
                    .testTag("calculation_method_footer")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Engine: ${state.selectedCalculationMethod.title.split(",").firstOrNull() ?: state.selectedCalculationMethod.name} • ${state.asrJuristicMethod.displayName.split(" ").first()}",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Text(
                        text = "Customize",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = EmeraldPrimary,
                            fontSize = 11.sp
                        )
                    )
                }
            }
        }
    }
}
