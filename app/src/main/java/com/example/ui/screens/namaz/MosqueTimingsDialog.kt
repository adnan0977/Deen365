package com.example.ui.screens.namaz

import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.domain.MosquePrayerTiming
import com.example.notification.AlertSoundMode
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun MosqueTimingsDialog(
    isOpen: Boolean,
    mosqueName: String,
    isGlobalMosqueActive: Boolean,
    timings: List<MosquePrayerTiming>,
    onDismiss: () -> Unit,
    onSaveMosqueName: (String) -> Unit,
    onToggleGlobalMode: (Boolean) -> Unit,
    onUpdateTiming: (prayerId: String, manualTime: String, isActive: Boolean, preAlarmMins: Int, soundMode: AlertSoundMode) -> Unit,
    onResetAll: () -> Unit,
    onTestAlarm: (prayerId: String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    if (!isOpen) return

    val context = LocalContext.current
    var editingMosqueName by remember(mosqueName) { mutableStateOf(mosqueName) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.9f)
                .clip(RoundedCornerShape(24.dp))
                .testTag("mosque_timings_dialog"),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
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
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Mosque,
                                    contentDescription = null,
                                    tint = EmeraldPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Column {
                            Text(
                                text = "Local Mosque Timings",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Set manual Iqamah & congregation alarms",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_mosque_dialog_btn")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

                // Mosque Name Input
                OutlinedTextField(
                    value = editingMosqueName,
                    onValueChange = {
                        editingMosqueName = it
                        onSaveMosqueName(it)
                    },
                    label = { Text("Masjid / Mosque Name") },
                    leadingIcon = { Icon(Icons.Default.LocationCity, contentDescription = null, tint = EmeraldPrimary) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("mosque_name_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                // Master Global Switch Card
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (isGlobalMosqueActive) EmeraldPrimary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isGlobalMosqueActive) EmeraldPrimary.copy(alpha = 0.5f) else Color.Transparent
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Use Mosque Times for All Prayers",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (isGlobalMosqueActive) "Active: Alarms sound at manual Iqamah times" else "Currently using astronomical calculation",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = isGlobalMosqueActive,
                            onCheckedChange = { active ->
                                onToggleGlobalMode(active)
                                Toast.makeText(context, if (active) "Switched all alarms to Mosque timings" else "Switched to calculated prayer times", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.testTag("global_mosque_switch"),
                            colors = SwitchDefaults.colors(checkedThumbColor = EmeraldPrimary)
                        )
                    }
                }

                Text(
                    text = "Configure Prayers Individually:",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // List of Prayer Timings
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(timings, key = { it.prayerId }) { timing ->
                        MosqueTimingRow(
                            timing = timing,
                            onTimeClick = {
                                showTimePicker(context, timing.manualMosqueTime) { newTime ->
                                    onUpdateTiming(
                                        timing.prayerId,
                                        newTime,
                                        timing.isMosqueTimingActive,
                                        timing.preAlarmMinutes,
                                        timing.alarmSoundMode
                                    )
                                }
                            },
                            onToggleActive = { active ->
                                onUpdateTiming(
                                    timing.prayerId,
                                    timing.manualMosqueTime,
                                    active,
                                    timing.preAlarmMinutes,
                                    timing.alarmSoundMode
                                )
                            },
                            onPreAlarmChange = { mins ->
                                onUpdateTiming(
                                    timing.prayerId,
                                    timing.manualMosqueTime,
                                    timing.isMosqueTimingActive,
                                    mins,
                                    timing.alarmSoundMode
                                )
                            },
                            onSoundModeChange = { mode ->
                                onUpdateTiming(
                                    timing.prayerId,
                                    timing.manualMosqueTime,
                                    timing.isMosqueTimingActive,
                                    timing.preAlarmMinutes,
                                    mode
                                )
                            },
                            onTestAlarm = {
                                onTestAlarm(timing.prayerId)
                                Toast.makeText(context, "Testing ${timing.displayName} mosque alarm (${timing.manualMosqueTime})", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }

                // Footer Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = onResetAll,
                        modifier = Modifier.testTag("reset_mosque_timings_btn")
                    ) {
                        Text("Reset to Defaults", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                    }

                    Button(
                        onClick = {
                            Toast.makeText(context, "Mosque timings and alarms activated!", Toast.LENGTH_SHORT).show()
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("save_mosque_dialog_btn")
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Save & Close", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun MosqueTimingRow(
    timing: MosquePrayerTiming,
    onTimeClick: () -> Unit,
    onToggleActive: (Boolean) -> Unit,
    onPreAlarmChange: (Int) -> Unit,
    onSoundModeChange: (AlertSoundMode) -> Unit,
    onTestAlarm: () -> Unit = {}
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (timing.isMosqueTimingActive) EmeraldPrimary.copy(alpha = 0.06f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        ),
        border = if (timing.isMosqueTimingActive) {
            androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.35f))
        } else null,
        modifier = Modifier.fillMaxWidth().testTag("mosque_row_${timing.prayerId}")
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = timing.displayName,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = timing.arabicName,
                            style = MaterialTheme.typography.bodySmall.copy(color = EmeraldPrimary, fontWeight = FontWeight.SemiBold)
                        )
                    }
                    Text(
                        text = if (timing.isMosqueTimingActive) {
                            "Calculated: ${timing.defaultCalculatedTime} • Alarm: Iqamah"
                        } else {
                            "Calculated: ${timing.defaultCalculatedTime} • Alarm: Azan"
                        },
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Interactive Manual Time Button
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (timing.isMosqueTimingActive) EmeraldPrimary else MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (timing.isMosqueTimingActive) EmeraldPrimary else MaterialTheme.colorScheme.outlineVariant
                    ),
                    modifier = Modifier
                        .clickable { onTimeClick() }
                        .testTag("time_picker_btn_${timing.prayerId}")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = null,
                            tint = if (timing.isMosqueTimingActive) Color.White else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = timing.manualMosqueTime,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (timing.isMosqueTimingActive) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Time",
                            tint = if (timing.isMosqueTimingActive) Color.White.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }

            // Controls Row: Mode Toggle & Walking Pre-Alarm
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Timing Switch Pill
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
                        .padding(2.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    FilterChip(
                        selected = !timing.isMosqueTimingActive,
                        onClick = { onToggleActive(false) },
                        label = { Text("Azan", fontSize = 10.sp) },
                        modifier = Modifier.height(28.dp)
                    )
                    FilterChip(
                        selected = timing.isMosqueTimingActive,
                        onClick = { onToggleActive(true) },
                        label = { Text("Mosque", fontSize = 10.sp) },
                        modifier = Modifier.height(28.dp),
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = EmeraldPrimary, selectedLabelColor = Color.White)
                    )
                }

                // Pre-Alarm Walking Reminder Chips
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Text("Walk:", style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant))
                    listOf(0, 5, 10, 15, 20).forEach { mins ->
                        val isSelected = timing.preAlarmMinutes == mins
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (isSelected) GoldAccent else MaterialTheme.colorScheme.surface,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .clickable { onPreAlarmChange(mins) }
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (mins == 0) "Off" else "${mins}m",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }

                    // Sound Toggle Icon
                    IconButton(
                        onClick = {
                            val nextMode = when (timing.alarmSoundMode) {
                                AlertSoundMode.FULL_ADHAN -> AlertSoundMode.SOFT_CHIME
                                AlertSoundMode.SOFT_CHIME -> AlertSoundMode.SILENT_VIBRATION
                                AlertSoundMode.SILENT_VIBRATION -> AlertSoundMode.FULL_ADHAN
                            }
                            onSoundModeChange(nextMode)
                        },
                        modifier = Modifier.size(28.dp).testTag("sound_toggle_${timing.prayerId}")
                    ) {
                        Icon(
                            imageVector = when (timing.alarmSoundMode) {
                                AlertSoundMode.FULL_ADHAN -> Icons.AutoMirrored.Filled.VolumeUp
                                AlertSoundMode.SOFT_CHIME -> Icons.Default.NotificationsActive
                                AlertSoundMode.SILENT_VIBRATION -> Icons.Default.Vibration
                            },
                            contentDescription = "Alert Sound: ${timing.alarmSoundMode.title}",
                            tint = if (timing.isMosqueTimingActive) EmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Test Alarm Button
                    IconButton(
                        onClick = onTestAlarm,
                        modifier = Modifier.size(28.dp).testTag("test_mosque_alarm_${timing.prayerId}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Test Mosque Alarm",
                            tint = GoldAccent,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

private fun showTimePicker(
    context: android.content.Context,
    currentTimeStr: String,
    onTimeSelected: (String) -> Unit
) {
    val cal = Calendar.getInstance()
    try {
        val sdf = SimpleDateFormat("hh:mm a", Locale.US)
        val d = sdf.parse(currentTimeStr.trim())
        if (d != null) cal.time = d
    } catch (_: Exception) {}

    val hour = cal.get(Calendar.HOUR_OF_DAY)
    val minute = cal.get(Calendar.MINUTE)

    TimePickerDialog(
        context,
        { _, selectedHour, selectedMinute ->
            val setCal = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, selectedHour)
                set(Calendar.MINUTE, selectedMinute)
            }
            val formatted = SimpleDateFormat("hh:mm a", Locale.US).format(setCal.time)
            onTimeSelected(formatted)
        },
        hour,
        minute,
        false
    ).show()
}
