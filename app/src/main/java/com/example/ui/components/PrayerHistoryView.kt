package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.PrayerRecordEntity
import com.example.sync.PrayerSyncStatus
import com.example.ui.theme.EmeraldAccent
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.EmeraldPrimaryVariant
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldLight
import com.example.ui.viewmodel.DeenUiState
import com.example.ui.viewmodel.DeenViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun PrayerHistoryView(
    state: DeenUiState,
    viewModel: DeenViewModel,
    modifier: Modifier = Modifier
) {
    val stats = state.prayerStats
    val records = state.prayerHistory
    val isSyncing = state.isSyncingPrayers
    val userProfile = state.userProfile

    val infiniteTransition = rememberInfiniteTransition(label = "sync_spin")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("prayer_history_view"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Cross-Device Cloud Sync Status Banner
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (userProfile != null) EmeraldPrimary.copy(alpha = 0.35f) else GoldAccent.copy(alpha = 0.4f)
                ),
                modifier = Modifier.fillMaxWidth().testTag("prayer_sync_status_card")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
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
                                color = if (userProfile != null) EmeraldPrimary.copy(alpha = 0.12f) else GoldAccent.copy(alpha = 0.15f),
                                modifier = Modifier.size(42.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = if (userProfile != null) Icons.Default.CloudDone else Icons.Default.CloudQueue,
                                        contentDescription = "Cloud Sync",
                                        tint = if (userProfile != null) EmeraldPrimary else GoldAccent,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }

                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(
                                        text = "Multi-Device Sync",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (userProfile != null) EmeraldPrimary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant
                                    ) {
                                        Text(
                                            text = if (userProfile != null) "Cloud Active" else "Local Only",
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                                            color = if (userProfile != null) EmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Text(
                                    text = if (userProfile != null) {
                                        "Connected: ${userProfile.email}"
                                    } else {
                                        "Sign in to sync prayer records across all your devices"
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Sync Now Button
                        FilledTonalButton(
                            onClick = {
                                if (userProfile != null) {
                                    viewModel.syncPrayerAttendanceNow()
                                } else {
                                    viewModel.openUserAccountDialog()
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = if (userProfile != null) EmeraldPrimary.copy(alpha = 0.15f) else GoldAccent.copy(alpha = 0.2f),
                                contentColor = if (userProfile != null) EmeraldPrimary else MaterialTheme.colorScheme.onSurface
                            ),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("prayer_sync_now_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Sync,
                                contentDescription = "Sync Now",
                                modifier = Modifier
                                    .size(16.dp)
                                    .rotate(if (isSyncing) rotation else 0f)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = if (isSyncing) "Syncing..." else if (userProfile != null) "Sync Now" else "Sign In",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }

                    // Sync Details or Status Message
                    when (val status = state.prayerSyncStatus) {
                        is PrayerSyncStatus.Success -> {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = EmeraldPrimary.copy(alpha = 0.08f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(14.dp))
                                    Text(
                                        text = "Uploaded ${status.uploadedCount} logs • Merged ${status.downloadedCount} from other devices • Just now",
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                        color = EmeraldPrimary
                                    )
                                }
                            }
                        }
                        is PrayerSyncStatus.Error -> {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(14.dp))
                                    Text(
                                        text = status.message,
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                        else -> {
                            if (state.lastPrayerSyncTime > 0) {
                                val timeFormatted = SimpleDateFormat("h:mm a, MMM d", Locale.getDefault()).format(Date(state.lastPrayerSyncTime))
                                Text(
                                    text = "Last synced with Firestore: $timeFormatted",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        // 2. Performance Summary Card (Overall %, Streaks, Totals)
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth().testTag("prayer_performance_summary_card")
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Historical Attendance",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "${String.format(Locale.getDefault(), "%.1f", stats.overallPercentage)}% Consistency",
                                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }

                        Surface(
                            shape = CircleShape,
                            color = EmeraldPrimary,
                            modifier = Modifier.size(54.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "${stats.totalPrayedCount}",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                            }
                        }
                    }

                    // Progress bar
                    LinearProgressIndicator(
                        progress = { (stats.overallPercentage / 100f).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(6.dp)),
                        color = EmeraldPrimary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )

                    // 3 Metric Pills
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        MetricItem(
                            label = "Recorded Days",
                            value = "${stats.totalRecordedDays}",
                            icon = Icons.Default.CalendarToday
                        )
                        MetricItem(
                            label = "Active Streak",
                            value = "${stats.currentStreakDays} Days",
                            icon = Icons.Default.LocalFireDepartment,
                            highlightColor = GoldAccent
                        )
                        MetricItem(
                            label = "5/5 Full Days",
                            value = "${stats.perfectDaysCount}",
                            icon = Icons.Default.WorkspacePremium,
                            highlightColor = EmeraldPrimary
                        )
                    }
                }
            }
        }

        // 3. Per-Prayer Completion Breakdown
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Five Daily Prayers Breakdown",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    PrayerBreakdownBar("Fajr (Dawn)", stats.fajrCount, stats.totalRecordedDays, stats.fajrPercentage)
                    PrayerBreakdownBar("Dhuhr (Noon)", stats.dhuhrCount, stats.totalRecordedDays, stats.dhuhrPercentage)
                    PrayerBreakdownBar("Asr (Afternoon)", stats.asrCount, stats.totalRecordedDays, stats.asrPercentage)
                    PrayerBreakdownBar("Maghrib (Sunset)", stats.maghribCount, stats.totalRecordedDays, stats.maghribPercentage)
                    PrayerBreakdownBar("Isha (Night)", stats.ishaCount, stats.totalRecordedDays, stats.ishaPercentage)
                }
            }
        }

        // 4. Past 7-Days Visual Matrix
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Recent 7-Day Performance Matrix",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    val recent7 = records.take(7).reversed()
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        items(recent7) { record ->
                            DayMatrixColumn(record)
                        }
                    }
                }
            }
        }

        // 5. Daily Attendance History List
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Daily Prayer Attendance Records",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Tap to toggle",
                    style = MaterialTheme.typography.labelSmall,
                    color = EmeraldPrimary
                )
            }
        }

        items(records, key = { it.dateStr }) { record ->
            HistoricalRecordCard(
                record = record,
                onTogglePrayer = { prayerName ->
                    viewModel.toggleHistoricalPrayer(record.dateStr, prayerName)
                }
            )
        }
    }
}

@Composable
private fun MetricItem(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    highlightColor: Color? = null
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Surface(
            shape = CircleShape,
            color = (highlightColor ?: MaterialTheme.colorScheme.primary).copy(alpha = 0.12f),
            modifier = Modifier.size(34.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = highlightColor ?: MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun PrayerBreakdownBar(
    name: String,
    count: Int,
    totalDays: Int,
    percentage: Float
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "$count/$totalDays (${String.format(Locale.getDefault(), "%.0f", percentage)}%)",
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                color = if (percentage >= 80f) EmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        LinearProgressIndicator(
            progress = { (percentage / 100f).coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = if (percentage >= 80f) EmeraldPrimary else GoldAccent,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

@Composable
private fun DayMatrixColumn(record: PrayerRecordEntity) {
    val dayLabel = try {
        val parser = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = parser.parse(record.dateStr)
        if (date != null) SimpleDateFormat("EEE", Locale.getDefault()).format(date) else record.dateStr.takeLast(2)
    } catch (e: Exception) {
        record.dateStr.takeLast(2)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = dayLabel,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // 5 dots for Fajr, Dhuhr, Asr, Maghrib, Isha
        PrayerDot(record.fajr)
        PrayerDot(record.dhuhr)
        PrayerDot(record.asr)
        PrayerDot(record.maghrib)
        PrayerDot(record.isha)

        Text(
            text = "${record.completedCount}/5",
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = if (record.completedCount == 5) EmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
    }
}

@Composable
private fun PrayerDot(isCompleted: Boolean) {
    Box(
        modifier = Modifier
            .size(10.dp)
            .clip(CircleShape)
            .background(if (isCompleted) EmeraldPrimary else MaterialTheme.colorScheme.surfaceVariant)
    )
}

@Composable
private fun HistoricalRecordCard(
    record: PrayerRecordEntity,
    onTogglePrayer: (String) -> Unit
) {
    val isPerfect = record.completedCount == 5

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isPerfect) EmeraldPrimary.copy(alpha = 0.05f) else MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isPerfect) EmeraldPrimary.copy(alpha = 0.3f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth().testTag("prayer_record_card_${record.dateStr}")
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
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = record.dateStr,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (record.deviceModel.isNotBlank()) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = record.deviceModel,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isPerfect) EmeraldPrimary.copy(alpha = 0.15f) else GoldAccent.copy(alpha = 0.18f)
                ) {
                    Text(
                        text = if (isPerfect) "5/5 Full Attendance" else "${record.completedCount}/5 Prayed",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isPerfect) EmeraldPrimary else MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            // Interactive prayer chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                PrayerAttendanceChip("Fajr", record.fajr) { onTogglePrayer("Fajr") }
                PrayerAttendanceChip("Dhuhr", record.dhuhr) { onTogglePrayer("Dhuhr") }
                PrayerAttendanceChip("Asr", record.asr) { onTogglePrayer("Asr") }
                PrayerAttendanceChip("Maghrib", record.maghrib) { onTogglePrayer("Maghrib") }
                PrayerAttendanceChip("Isha", record.isha) { onTogglePrayer("Isha") }
            }
        }
    }
}

@Composable
private fun PrayerAttendanceChip(
    name: String,
    isOffered: Boolean,
    onToggle: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (isOffered) EmeraldPrimary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        modifier = Modifier
            .clickable { onToggle() }
            .testTag("prayer_chip_${name.lowercase()}")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = if (isOffered) Icons.Default.Check else Icons.Default.Close,
                contentDescription = null,
                tint = if (isOffered) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(12.dp)
            )
            Text(
                text = name,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = if (isOffered) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}
