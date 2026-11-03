package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EmeraldAccent
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.EmeraldPrimaryVariant
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldLight
import com.example.ui.viewmodel.DeenUiState
import com.example.ui.viewmodel.DeenViewModel
import kotlin.math.cos
import kotlin.math.sin

/**
 * Visual Progress Tracker for the Fasting Month (Ramadan / Sawm).
 * Displays completed days vs remaining days in an interactive pie or donut chart,
 * with Ashra milestone breakdown, streak counters, and intuitive logging controls.
 */
@Composable
fun FastingMonthProgressCard(
    state: DeenUiState,
    viewModel: DeenViewModel,
    modifier: Modifier = Modifier
) {
    val completedDays = state.fastingTrackerDaysCount.coerceIn(0, state.fastingMonthTotalDays)
    val totalDays = state.fastingMonthTotalDays
    val remainingDays = (totalDays - completedDays).coerceAtLeast(0)
    val percentage = if (totalDays > 0) (completedDays.toFloat() / totalDays.toFloat() * 100f) else 0f

    var isDonutView by remember { mutableStateOf(true) }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.horizontalGradient(
                listOf(
                    EmeraldPrimary.copy(alpha = 0.5f),
                    GoldAccent.copy(alpha = 0.5f)
                )
            )
        ),
        modifier = modifier
            .fillMaxWidth()
            .testTag("fasting_progress_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Row: Title & Lunar Month Length Selector (29 vs 30 days)
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
                        border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent.copy(alpha = 0.5f)),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.NightsStay,
                                contentDescription = null,
                                tint = GoldAccent,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    Column {
                        Text(
                            text = "Fasting Month Progress",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${state.hijriDate.monthName} ${state.hijriDate.year} AH • Sawm Tracker",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Month Length Switcher (29 or 30 days)
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                        .padding(2.dp)
                        .testTag("fasting_month_length_toggle"),
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    listOf(29, 30).forEach { days ->
                        val isSelected = totalDays == days
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) EmeraldPrimary else Color.Transparent,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { viewModel.setFastingMonthTotalDays(days) }
                        ) {
                            Text(
                                text = "${days}d",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 11.sp
                                ),
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            // Pie Chart Display with Center Cutout and Toggle between Donut and Solid Pie
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                FastingMonthProgressPieChart(
                    completedDays = completedDays,
                    totalDays = totalDays,
                    isDonut = isDonutView,
                    onToggleStyle = { isDonutView = !isDonutView },
                    modifier = Modifier
                        .size(230.dp)
                        .testTag("fasting_progress_pie_chart")
                )
            }

            // Chart View Mode Chip
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                    modifier = Modifier.clickable { isDonutView = !isDonutView }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = if (isDonutView) Icons.Default.PieChartOutline else Icons.Default.PieChart,
                            contentDescription = null,
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = if (isDonutView) "Switch to Solid Pie" else "Switch to Donut View",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Legend / Stat Breakdown Grid (Completed vs Remaining)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Completed Days Legend Card
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = EmeraldPrimary.copy(alpha = 0.12f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.35f)),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("fasting_completed_legend")
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(EmeraldPrimary)
                            )
                            Text(
                                text = "Completed",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = EmeraldPrimary
                            )
                        }

                        Text(
                            text = "$completedDays Days",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = EmeraldPrimary
                            )
                        )

                        Text(
                            text = String.format("%.1f%% of month", percentage),
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Remaining Days Legend Card
                val remainingPercent = 100f - percentage
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("fasting_remaining_legend")
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.outline)
                            )
                            Text(
                                text = "Remaining",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Text(
                            text = "$remainingDays Days",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )

                        Text(
                            text = String.format("%.1f%% to Eid", remainingPercent),
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Quick Stepper & Today's Fast Logging
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Adjust Completed Days",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (state.isFastingToday) "Today's fast is logged" else "Streak: ${state.fastingStreak} consecutive",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = if (state.isFastingToday) EmeraldPrimary else GoldAccent
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Decrement Button with 48dp minimum touch target
                        FilledTonalIconButton(
                            onClick = { viewModel.decrementFastingTrackerDays() },
                            enabled = completedDays > 0,
                            modifier = Modifier
                                .size(48.dp)
                                .testTag("fasting_decrement_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Remove,
                                contentDescription = "Decrease completed days",
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Text(
                            text = "$completedDays",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                            color = EmeraldPrimary,
                            modifier = Modifier.padding(horizontal = 6.dp)
                        )

                        // Increment Button with 48dp minimum touch target
                        FilledTonalIconButton(
                            onClick = { viewModel.incrementFastingTrackerDays() },
                            enabled = completedDays < totalDays,
                            colors = IconButtonDefaults.filledTonalIconButtonColors(
                                containerColor = EmeraldPrimary.copy(alpha = 0.2f),
                                contentColor = EmeraldPrimary
                            ),
                            modifier = Modifier
                                .size(48.dp)
                                .testTag("fasting_increment_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Increase completed days",
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            // Sunset Fasting Completion Push Notification Reminder Banner
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = GoldAccent.copy(alpha = 0.12f),
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent.copy(alpha = 0.35f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("fasting_sunset_reminder_card")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
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
                            color = GoldAccent.copy(alpha = 0.2f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (state.notifySunsetFasting) Icons.Default.NotificationsActive else Icons.Default.NotificationsOff,
                                    contentDescription = null,
                                    tint = if (state.notifySunsetFasting) EmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
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
                                    text = "Sunset Fast Reminder",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = EmeraldPrimary.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = state.prayerSchedule.iftar,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp,
                                            color = EmeraldPrimary
                                        ),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = if (state.notifySunsetFasting) "Daily notification at sunset (${state.prayerSchedule.iftar}) to recite Iftar Dua & log Sawm" else "Sunset notification disabled",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Switch(
                        checked = state.notifySunsetFasting,
                        onCheckedChange = { viewModel.toggleSunsetFastingReminder(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = EmeraldPrimary
                        ),
                        modifier = Modifier.testTag("fasting_sunset_reminder_switch")
                    )
                }
            }

            // Ashra / Phases of the Fasting Month Tracker
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Ramadan Ashra Milestones",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // 3 Ashras
                AshraProgressRow(
                    number = 1,
                    title = "Days 1–10: Mercy (Rehmat)",
                    completedInAshra = completedDays.coerceIn(0, 10),
                    totalInAshra = 10,
                    isActive = completedDays in 1..10
                )

                AshraProgressRow(
                    number = 2,
                    title = "Days 11–20: Forgiveness (Maghfirah)",
                    completedInAshra = (completedDays - 10).coerceIn(0, 10),
                    totalInAshra = 10,
                    isActive = completedDays in 11..20
                )

                val thirdAshraTotal = totalDays - 20
                AshraProgressRow(
                    number = 3,
                    title = "Days 21–$totalDays: Deliverance (Nijat) & Qadr",
                    completedInAshra = (completedDays - 20).coerceIn(0, thirdAshraTotal),
                    totalInAshra = thirdAshraTotal,
                    isActive = completedDays > 20
                )
            }

            // Prophetic Blessing Note
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = GoldAccent.copy(alpha = 0.1f),
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.VolunteerActivism,
                        contentDescription = null,
                        tint = GoldAccent,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "\"Whoever fasts Ramadan out of faith and expectation of reward, his past sins will be forgiven.\" (Sahih al-Bukhari)",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

/**
 * Pure Jetpack Compose Canvas Pie / Donut Chart for Fasting Month.
 */
@Composable
fun FastingMonthProgressPieChart(
    completedDays: Int,
    totalDays: Int,
    isDonut: Boolean,
    onToggleStyle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progressRatio = if (totalDays > 0) (completedDays.toFloat() / totalDays.toFloat()).coerceIn(0f, 1f) else 0f

    val animatedRatio by animateFloatAsState(
        targetValue = progressRatio,
        animationSpec = tween(durationMillis = 850, easing = FastOutSlowInEasing),
        label = "fasting_chart_anim"
    )

    val surfaceVariantColor = MaterialTheme.colorScheme.surfaceVariant
    val outlineColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
    val completedColor = EmeraldPrimary
    val completedAccentColor = GoldAccent

    Box(
        modifier = modifier.clickable { onToggleStyle() },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = if (isDonut) 24.dp.toPx() else 0f
            val padding = strokeWidth / 2f + 8.dp.toPx()
            val diameter = size.minDimension - padding * 2
            val arcSize = Size(diameter, diameter)
            val topLeft = Offset(
                (size.width - diameter) / 2f,
                (size.height - diameter) / 2f
            )
            val center = Offset(size.width / 2f, size.height / 2f)

            val completedSweep = animatedRatio * 360f
            val remainingSweep = 360f - completedSweep

            if (isDonut) {
                // Background Track (Remaining Days)
                drawArc(
                    color = surfaceVariantColor,
                    startAngle = -90f + completedSweep,
                    sweepAngle = remainingSweep,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
                )

                // Completed Days Arc with spiritual gradient
                if (completedSweep > 0.5f) {
                    drawArc(
                        brush = Brush.sweepGradient(
                            listOf(
                                completedColor,
                                completedAccentColor,
                                completedColor
                            ),
                            center = center
                        ),
                        startAngle = -90f,
                        sweepAngle = completedSweep,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                }

                // Decorative Outer Ring
                drawCircle(
                    color = outlineColor.copy(alpha = 0.15f),
                    radius = (diameter / 2f) + (strokeWidth / 2f) + 4.dp.toPx(),
                    center = center,
                    style = Stroke(width = 1.dp.toPx())
                )
            } else {
                // Solid Pie Chart
                // Remaining slice
                drawArc(
                    color = surfaceVariantColor,
                    startAngle = -90f + completedSweep,
                    sweepAngle = remainingSweep,
                    useCenter = true,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Fill
                )

                // Completed slice
                if (completedSweep > 0.5f) {
                    drawArc(
                        brush = Brush.radialGradient(
                            listOf(completedAccentColor, completedColor),
                            center = center,
                            radius = diameter / 2f
                        ),
                        startAngle = -90f,
                        sweepAngle = completedSweep,
                        useCenter = true,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Fill
                    )
                }

                // Divider line at start (-90 deg / 12 o'clock)
                val topAngleRad = Math.toRadians(-90.0)
                val lineEndX = (center.x + (diameter / 2f) * cos(topAngleRad)).toFloat()
                val lineEndY = (center.y + (diameter / 2f) * sin(topAngleRad)).toFloat()
                drawLine(
                    color = Color.White.copy(alpha = 0.7f),
                    start = center,
                    end = Offset(lineEndX, lineEndY),
                    strokeWidth = 2.dp.toPx()
                )

                // Divider line at progress end
                val sweepAngleRad = Math.toRadians((-90.0 + completedSweep))
                val sweepEndX = (center.x + (diameter / 2f) * cos(sweepAngleRad)).toFloat()
                val sweepEndY = (center.y + (diameter / 2f) * sin(sweepAngleRad)).toFloat()
                drawLine(
                    color = Color.White.copy(alpha = 0.7f),
                    start = center,
                    end = Offset(sweepEndX, sweepEndY),
                    strokeWidth = 2.dp.toPx()
                )
            }
        }

        // Center Content Overlay
        if (isDonut) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "$completedDays",
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = EmeraldPrimary,
                        fontSize = 32.sp
                    )
                )
                Text(
                    text = "of $totalDays Days",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = GoldAccent.copy(alpha = 0.2f),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Text(
                        text = "${(progressRatio * 100f).toInt()}% Done",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 11.sp
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }
        } else {
            // Small badge in center for solid pie
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(2.dp, GoldAccent),
                shadowElevation = 4.dp,
                modifier = Modifier.size(64.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "${(progressRatio * 100f).toInt()}%",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = EmeraldPrimary
                        )
                    )
                }
            }
        }
    }
}

/**
 * Ashra Milestone Row showing progress in each of the 3 ten-day blocks of Ramadan.
 */
@Composable
private fun AshraProgressRow(
    number: Int,
    title: String,
    completedInAshra: Int,
    totalInAshra: Int,
    isActive: Boolean
) {
    val isFinished = completedInAshra >= totalInAshra
    val progress = (completedInAshra.toFloat() / totalInAshra.toFloat()).coerceIn(0f, 1f)

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = when {
            isFinished -> EmeraldPrimary.copy(alpha = 0.1f)
            isActive -> GoldAccent.copy(alpha = 0.12f)
            else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        },
        border = when {
            isFinished -> androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.4f))
            isActive -> androidx.compose.foundation.BorderStroke(1.dp, GoldAccent.copy(alpha = 0.6f))
            else -> null
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
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
                        imageVector = when {
                            isFinished -> Icons.Default.CheckCircle
                            isActive -> Icons.Default.PlayCircleFilled
                            else -> Icons.Default.RadioButtonUnchecked
                        },
                        contentDescription = null,
                        tint = when {
                            isFinished -> EmeraldPrimary
                            isActive -> GoldAccent
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    text = "$completedInAshra / $totalInAshra",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = when {
                            isFinished -> EmeraldPrimary
                            isActive -> GoldAccent
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                )
            }

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = if (isFinished) EmeraldPrimary else GoldAccent,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }
    }
}
