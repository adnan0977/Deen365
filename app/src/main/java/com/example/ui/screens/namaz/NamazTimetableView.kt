package com.example.ui.screens.namaz

import android.Manifest
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.notification.AlertSoundMode
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.viewmodel.DeenUiState
import com.example.ui.viewmodel.DeenViewModel

@Composable
fun NamazTimetableView(
    state: DeenUiState,
    viewModel: DeenViewModel,
    onOpenCityPicker: () -> Unit,
    onOpenMethodPicker: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val notifPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            viewModel.toggleGlobalPrayerAlerts(true, context)
            Toast.makeText(context, "Prayer alerts enabled!", Toast.LENGTH_SHORT).show()
        } else {
            viewModel.toggleGlobalPrayerAlerts(false, context)
            Toast.makeText(context, "Notification permission required.", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Prayer Times Module (Location API GPS coordinates, live countdown ticker, daily Namaz timetable)
        PrayerTimesModuleView(
            state = state,
            viewModel = viewModel,
            onOpenCityPicker = onOpenCityPicker,
            onOpenMethodPicker = onOpenMethodPicker
        )

        // 2. Local Mosque & Iqamah Timings Card
        val activeMosqueCount = state.mosqueTimings.count { it.isMosqueTimingActive }
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (state.isGlobalMosqueActive || activeMosqueCount > 0) EmeraldPrimary.copy(alpha = 0.09f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            ),
            border = if (state.isGlobalMosqueActive || activeMosqueCount > 0) androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.4f)) else null,
            modifier = Modifier.fillMaxWidth().testTag("mosque_timings_card")
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
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
                            color = if (state.isGlobalMosqueActive || activeMosqueCount > 0) EmeraldPrimary else MaterialTheme.colorScheme.surface,
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Mosque,
                                    contentDescription = null,
                                    tint = if (state.isGlobalMosqueActive || activeMosqueCount > 0) Color.White else EmeraldPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = state.mosqueName,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                if (state.isGlobalMosqueActive || activeMosqueCount > 0) {
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = EmeraldPrimary
                                    ) {
                                        Text(
                                            text = if (state.isGlobalMosqueActive) "All 5 Active" else "$activeMosqueCount Active",
                                            style = MaterialTheme.typography.labelSmall.copy(color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold),
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                            }
                            Text(
                                text = if (state.isGlobalMosqueActive) "Alarms ring at your mosque's custom Iqamah timings"
                                else if (activeMosqueCount > 0) "$activeMosqueCount prayer(s) customized with manual Iqamah alarms"
                                else "Using astronomical solar times • Tap to set mosque timings",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Button(
                        onClick = { viewModel.openMosqueTimingsDialog(true) },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (state.isGlobalMosqueActive || activeMosqueCount > 0) EmeraldPrimary else MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                            contentColor = if (state.isGlobalMosqueActive || activeMosqueCount > 0) Color.White else EmeraldPrimary
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("open_mosque_timings_btn")
                    ) {
                        Text("Mosque Alarms", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Row of quick actions: Edit Times & Test Iqamah Alarm
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { viewModel.openMosqueTimingsDialog(true) },
                        modifier = Modifier.weight(1f).testTag("custom_mosque_times_btn"),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(Icons.Default.AccessTime, contentDescription = null, modifier = Modifier.size(13.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Set Mosque Timings", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }

                    OutlinedButton(
                        onClick = {
                            viewModel.triggerTestMosqueAlarm(state.prayerSchedule.nextPrayerName.lowercase(), context)
                            Toast.makeText(context, "Testing ${state.mosqueName} custom Iqamah alarm!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.weight(1f).testTag("test_mosque_iqamah_alarm_btn"),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(13.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Test Iqamah Alarm", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        // 3. Adhan Alerts Configuration Card
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
            modifier = Modifier.fillMaxWidth().testTag("prayer_alerts_card")
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
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
                        Icon(Icons.Default.VolumeUp, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(20.dp))
                        Column {
                            Text("Scheduled Adhan Audio Alarms", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                            Text("Play full Adhan or soft chime at prayer time", style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                        modifier = Modifier.testTag("global_prayer_alerts_switch")
                    )
                }

                if (state.prayerAlertsEnabled) {
                    // Sound Mode Selector
                    Text(
                        text = "Notification Sound:",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            AlertSoundMode.FULL_ADHAN to "Full Adhan",
                            AlertSoundMode.SOFT_CHIME to "Soft Chime",
                            AlertSoundMode.SILENT_VIBRATION to "Vibrate"
                        ).forEach { (mode, title) ->
                            val isSelected = state.alertSoundMode == mode
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { viewModel.setPrayerAlertSoundMode(mode, context) }
                                    .testTag("sound_mode_${mode.name.lowercase()}")
                            ) {
                                Text(
                                    text = title,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(vertical = 6.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }

                    // Reminder Offset Selector
                    Text(
                        text = "Alert Timing:",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(0 to "Exact", 5 to "5m early", 10 to "10m early", 15 to "15m early").forEach { (offset, label) ->
                            val isSelected = state.reminderOffsetMinutes == offset
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.setPrayerReminderOffset(offset, context) },
                                label = { Text(label, fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = EmeraldPrimary.copy(alpha = 0.15f),
                                    selectedLabelColor = EmeraldPrimary
                                ),
                                modifier = Modifier.weight(1f).testTag("offset_chip_${offset}m")
                            )
                        }
                    }

                    // Location-Aware Service Status Pill
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = EmeraldPrimary.copy(alpha = 0.08f),
                        border = androidx.compose.foundation.BorderStroke(0.5.dp, EmeraldPrimary.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth().testTag("scheduler_service_status_pill")
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

                    // Action buttons: Reschedule & Test Alert
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = {
                                viewModel.scheduleLocationBasedPrayerNotifications(context)
                                Toast.makeText(context, "Location-based prayer notifications rescheduled!", Toast.LENGTH_SHORT).show()
                            },
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("reschedule_prayers_btn")
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Reschedule", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                viewModel.triggerTestPrayerNotification(context, state.prayerSchedule.nextPrayerName)
                                Toast.makeText(context, "Test alert sent for ${state.prayerSchedule.nextPrayerName}!", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = Color.Black),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("test_prayer_notif_btn")
                        ) {
                            Icon(Icons.Default.NotificationsActive, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Test Notification", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
