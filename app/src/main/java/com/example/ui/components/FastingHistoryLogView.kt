package com.example.ui.components

import androidx.compose.animation.*
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.FastingRecordEntity
import com.example.domain.IslamicCalendarHelper
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.EmeraldPrimaryVariant
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldLight
import com.example.ui.viewmodel.DeenUiState
import com.example.ui.viewmodel.DeenViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun FastingHistoryLogView(
    state: DeenUiState,
    viewModel: DeenViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTimeframe by remember { mutableIntStateOf(0) } // 0: Past 4 Weeks, 1: By Month, 2: All Records
    var selectedCategoryFilter by remember { mutableStateOf("All") } // All, Ramadan, Sunnah, White Days, Qadha
    var showLogDialog by remember { mutableStateOf(false) }
    var recordToDelete by remember { mutableStateOf<FastingRecordEntity?>(null) }
    var selectedMonthKey by remember { mutableStateOf("") }

    val completedRecords = remember(state.fastingHistory) {
        state.fastingHistory.filter { it.isFasted }
    }

    val filteredRecords = remember(completedRecords, selectedCategoryFilter) {
        if (selectedCategoryFilter == "All") {
            completedRecords
        } else {
            completedRecords.filter { record ->
                when (selectedCategoryFilter) {
                    "Ramadan" -> record.fastingType.contains("Ramadan", ignoreCase = true)
                    "Sunnah" -> record.fastingType.contains("Sunnah", ignoreCase = true) || record.fastingType.contains("Mon/Thu", ignoreCase = true)
                    "White Days" -> record.fastingType.contains("White", ignoreCase = true)
                    "Qadha" -> record.isQadha || record.fastingType.contains("Qadha", ignoreCase = true)
                    "Nafilah" -> record.fastingType.contains("Nafilah", ignoreCase = true)
                    else -> true
                }
            }
        }
    }

    // Group records by Month Year (e.g., "October 2026")
    val monthGroups = remember(filteredRecords) {
        val sdfInput = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val sdfMonth = SimpleDateFormat("MMMM yyyy", Locale.US)
        filteredRecords.groupBy { record ->
            try {
                val d = sdfInput.parse(record.dateStr)
                if (d != null) sdfMonth.format(d) else "Previous Fasts"
            } catch (_: Exception) {
                "Previous Fasts"
            }
        }
    }

    // Default selected month
    LaunchedEffect(monthGroups) {
        if (selectedMonthKey.isEmpty() || !monthGroups.containsKey(selectedMonthKey)) {
            selectedMonthKey = monthGroups.keys.firstOrNull() ?: ""
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("fasting_history_log_view"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Summary Metric Card
        FastingHistorySummaryCard(
            stats = state.fastingStats,
            totalCount = completedRecords.size,
            onLogFastClick = { showLogDialog = true }
        )

        // Timeframe Selector Tabs
        TabRow(
            selectedTabIndex = selectedTimeframe,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = EmeraldPrimary,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .testTag("fasting_history_timeframe_tabs")
        ) {
            Tab(
                selected = selectedTimeframe == 0,
                onClick = { selectedTimeframe = 0 },
                text = { Text("Past 4 Weeks", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.DateRange, contentDescription = null, modifier = Modifier.size(16.dp)) }
            )
            Tab(
                selected = selectedTimeframe == 1,
                onClick = { selectedTimeframe = 1 },
                text = { Text("By Month", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(16.dp)) }
            )
            Tab(
                selected = selectedTimeframe == 2,
                onClick = { selectedTimeframe = 2 },
                text = { Text("All Records", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.FormatListBulleted, contentDescription = null, modifier = Modifier.size(16.dp)) }
            )
        }

        // Category Filter Chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("fasting_category_filter_chips")
        ) {
            val categories = listOf("All", "Ramadan", "Sunnah", "White Days", "Qadha", "Nafilah")
            items(categories) { category ->
                val isSelected = selectedCategoryFilter == category
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedCategoryFilter = category },
                    label = { Text(category, fontSize = 12.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = when (category) {
                                "All" -> Icons.Default.AllInclusive
                                "Ramadan" -> Icons.Default.NightsStay
                                "Sunnah" -> Icons.Default.VolunteerActivism
                                "White Days" -> Icons.Default.Brightness5
                                "Qadha" -> Icons.Default.Restore
                                else -> Icons.Default.Star
                            },
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = EmeraldPrimary,
                        selectedLabelColor = Color.White,
                        selectedLeadingIconColor = GoldLight
                    ),
                    shape = RoundedCornerShape(10.dp)
                )
            }
        }

        // Main Tab Content
        when (selectedTimeframe) {
            0 -> {
                // Past 4 Weeks Breakdown
                FastingWeeksProgressView(
                    fastingRecords = completedRecords,
                    onToggleDate = { dateStr -> viewModel.toggleFastingForDate(dateStr) }
                )
            }
            1 -> {
                // Monthly View
                FastingMonthlyBreakdownView(
                    monthGroups = monthGroups,
                    selectedMonth = selectedMonthKey,
                    onSelectMonth = { selectedMonthKey = it },
                    onDeleteRecord = { recordToDelete = it },
                    onToggleDate = { dateStr -> viewModel.toggleFastingForDate(dateStr) }
                )
            }
            2 -> {
                // All Records Chronological List
                FastingChronologicalLogList(
                    monthGroups = monthGroups,
                    totalRecords = filteredRecords.size,
                    onDeleteRecord = { recordToDelete = it },
                    onLogFastClick = { showLogDialog = true }
                )
            }
        }
    }

    // Log Past Fast Dialog
    if (showLogDialog) {
        LogPastFastDialog(
            onDismiss = { showLogDialog = false },
            onSave = { dateStr, type, isQadha, note ->
                viewModel.logPastFast(dateStr, type, isQadha, note)
                showLogDialog = false
            }
        )
    }

    // Delete Confirmation Dialog
    if (recordToDelete != null) {
        AlertDialog(
            onDismissRequest = { recordToDelete = null },
            icon = { Icon(Icons.Default.DeleteOutline, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
            title = { Text("Delete Fast Record") },
            text = {
                Text("Are you sure you want to remove the completed fast recorded on ${recordToDelete?.dateStr} (${recordToDelete?.fastingType})?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        recordToDelete?.let { viewModel.deleteFastRecord(it.dateStr) }
                        recordToDelete = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.testTag("confirm_delete_fast_button")
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { recordToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun FastingHistorySummaryCard(
    stats: com.example.data.repository.TrackerRepository.FastingHistoryStats,
    totalCount: Int,
    onLogFastClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.horizontalGradient(
                listOf(EmeraldPrimary.copy(alpha = 0.5f), GoldAccent.copy(alpha = 0.6f))
            )
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("fasting_history_summary_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header with "+ Log Fast" Action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Fasting History & Log",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Track and review completed fasts over time",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                FilledTonalButton(
                    onClick = onLogFastClick,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = EmeraldPrimary,
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("open_log_fast_dialog_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Log Fast", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Hero Metric Banner: Total Fasts & Streaks
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = EmeraldPrimary.copy(alpha = 0.1f),
                border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.25f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "TOTAL COMPLETED",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "$totalCount",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = EmeraldPrimary
                            )
                        )
                        Text(
                            text = "Fasting Days",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Box(
                        modifier = Modifier
                            .height(44.dp)
                            .width(1.dp)
                            .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "CURRENT STREAK",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                            color = GoldAccent
                        )
                        Text(
                            text = "${stats.currentStreak}d",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = GoldAccent
                            )
                        )
                        Text(
                            text = "Best: ${stats.bestStreak}d",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Box(
                        modifier = Modifier
                            .height(44.dp)
                            .width(1.dp)
                            .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "THIS MONTH",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "${stats.thisMonthFastedCount}",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                        Text(
                            text = "Last: ${stats.lastMonthFastedCount}",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // 4 Category Breakdown Tiles
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CategoryStatTile(
                    title = "Ramadan",
                    count = stats.ramadanFastsCount,
                    icon = Icons.Default.NightsStay,
                    color = EmeraldPrimary,
                    modifier = Modifier.weight(1f)
                )
                CategoryStatTile(
                    title = "Sunnah",
                    count = stats.sunnahFastsCount,
                    icon = Icons.Default.VolunteerActivism,
                    color = GoldAccent,
                    modifier = Modifier.weight(1f)
                )
                CategoryStatTile(
                    title = "White Days",
                    count = stats.whiteDaysCount,
                    icon = Icons.Default.Brightness5,
                    color = Color(0xFF0288D1),
                    modifier = Modifier.weight(1f)
                )
                CategoryStatTile(
                    title = "Qadha",
                    count = stats.qadhaFastsCount,
                    icon = Icons.Default.Restore,
                    color = Color(0xFF7B1FA2),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun CategoryStatTile(
    title: String,
    count: Int,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.12f),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
            Text(
                text = "$count",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = color
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )
        }
    }
}

@Composable
fun FastingWeeksProgressView(
    fastingRecords: List<FastingRecordEntity>,
    onToggleDate: (String) -> Unit
) {
    val recordsMap = remember(fastingRecords) { fastingRecords.associateBy { it.dateStr } }
    val sdfDate = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    val sdfDay = SimpleDateFormat("EEE", Locale.US)
    val sdfDayNum = SimpleDateFormat("d", Locale.US)
    val sdfRange = SimpleDateFormat("MMM d", Locale.US)

    // Compute the past 4 weeks (starting from current week)
    val weeks = remember {
        val list = mutableListOf<WeekData>()
        val cal = Calendar.getInstance()
        // Align to current week's Sunday/Monday
        // Let's create 4 blocks of 7 days
        for (w in 0 until 4) {
            val weekDays = mutableListOf<DayItem>()
            val weekEndCal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -(w * 7)) }
            val weekStartCal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -((w * 7) + 6)) }

            val label = when (w) {
                0 -> "This Week"
                1 -> "Last Week"
                2 -> "2 Weeks Ago"
                else -> "3 Weeks Ago"
            }
            val rangeLabel = "${sdfRange.format(weekStartCal.time)} – ${sdfRange.format(weekEndCal.time)}"

            for (d in 6 downTo 0) {
                val dayCal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -((w * 7) + d)) }
                val dateStr = sdfDate.format(dayCal.time)
                val dayName = sdfDay.format(dayCal.time)
                val dayNum = sdfDayNum.format(dayCal.time)
                val dow = dayCal.get(Calendar.DAY_OF_WEEK)
                val isSunnah = dow == Calendar.MONDAY || dow == Calendar.THURSDAY
                val isToday = (w == 0 && d == 0)
                weekDays.add(
                    DayItem(
                        dateStr = dateStr,
                        dayName = dayName,
                        dayNumber = dayNum,
                        isSunnah = isSunnah,
                        isToday = isToday
                    )
                )
            }
            list.add(WeekData(label, rangeLabel, weekDays))
        }
        list
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        weeks.forEach { week ->
            val completedInWeek = week.days.count { recordsMap.containsKey(it.dateStr) }
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("fasting_week_card_${week.label.replace(" ", "_").lowercase()}")
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
                        Column {
                            Text(
                                text = week.label,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = week.rangeLabel,
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (completedInWeek > 0) EmeraldPrimary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = "$completedInWeek of 7 Days Fasted",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (completedInWeek > 0) EmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    // Progress Bar for week
                    LinearProgressIndicator(
                        progress = { (completedInWeek.toFloat() / 7f).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = EmeraldPrimary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    )

                    // 7-day Bubbles
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        week.days.forEach { day ->
                            val record = recordsMap[day.dateStr]
                            val isFasted = record != null

                            val bubbleColor = when {
                                !isFasted -> MaterialTheme.colorScheme.surfaceVariant
                                record.fastingType.contains("Sunnah", ignoreCase = true) -> GoldAccent
                                record.fastingType.contains("White", ignoreCase = true) -> Color(0xFF0288D1)
                                record.isQadha || record.fastingType.contains("Qadha", ignoreCase = true) -> Color(0xFF7B1FA2)
                                else -> EmeraldPrimary
                            }

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { onToggleDate(day.dateStr) }
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                                    .testTag("week_day_${day.dateStr}")
                            ) {
                                Text(
                                    text = day.dayName.take(2),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.sp,
                                        fontWeight = if (day.isToday) FontWeight.Bold else FontWeight.Normal
                                    ),
                                    color = if (day.isToday) EmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(bubbleColor)
                                        .then(
                                            if (day.isToday && !isFasted) {
                                                Modifier.border(1.5.dp, EmeraldPrimary, CircleShape)
                                            } else if (day.isSunnah && !isFasted) {
                                                Modifier.border(1.dp, GoldAccent.copy(alpha = 0.6f), CircleShape)
                                            } else {
                                                Modifier
                                            }
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isFasted) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Fasted",
                                            tint = if (bubbleColor == GoldAccent) Color.Black else Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    } else {
                                        Text(
                                            text = day.dayNumber,
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                            color = if (day.isToday) EmeraldPrimary else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }

                                if (day.isSunnah) {
                                    Text(
                                        text = "Sunnah",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp, fontWeight = FontWeight.Bold),
                                        color = GoldAccent
                                    )
                                } else {
                                    Text(text = " ", style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

data class DayItem(
    val dateStr: String,
    val dayName: String,
    val dayNumber: String,
    val isSunnah: Boolean,
    val isToday: Boolean
)

data class WeekData(
    val label: String,
    val rangeLabel: String,
    val days: List<DayItem>
)

@Composable
fun FastingMonthlyBreakdownView(
    monthGroups: Map<String, List<FastingRecordEntity>>,
    selectedMonth: String,
    onSelectMonth: (String) -> Unit,
    onDeleteRecord: (FastingRecordEntity) -> Unit,
    onToggleDate: (String) -> Unit
) {
    if (monthGroups.isEmpty()) {
        FastingEmptyStateCard()
        return
    }

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // Month Selector Horizontal Pills
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("fasting_month_pills")
        ) {
            items(monthGroups.keys.toList()) { monthName ->
                val isSelected = selectedMonth == monthName
                val countInMonth = monthGroups[monthName]?.size ?: 0
                FilterChip(
                    selected = isSelected,
                    onClick = { onSelectMonth(monthName) },
                    label = { Text("$monthName ($countInMonth)") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = EmeraldPrimary,
                        selectedLabelColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        // Active Month Card
        val activeRecords = monthGroups[selectedMonth] ?: emptyList()
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth().testTag("active_month_card")
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = selectedMonth,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${activeRecords.size} Completed Fasts Recorded",
                            style = MaterialTheme.typography.bodySmall,
                            color = EmeraldPrimary
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = GoldAccent.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "${activeRecords.size} Days Fasted",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = GoldAccent),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                // List of Records for this Month
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    activeRecords.forEach { record ->
                        FastingRecordItemRow(
                            record = record,
                            onDelete = { onDeleteRecord(record) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FastingChronologicalLogList(
    monthGroups: Map<String, List<FastingRecordEntity>>,
    totalRecords: Int,
    onDeleteRecord: (FastingRecordEntity) -> Unit,
    onLogFastClick: () -> Unit
) {
    if (monthGroups.isEmpty() || totalRecords == 0) {
        FastingEmptyStateCard(onLogFastClick = onLogFastClick)
        return
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.testTag("fasting_history_list")
    ) {
        monthGroups.forEach { (monthName, records) ->
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Month Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = monthName,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "${records.size} ${if (records.size == 1) "fast" else "fasts"}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Month items
                records.forEach { record ->
                    FastingRecordItemRow(
                        record = record,
                        onDelete = { onDeleteRecord(record) }
                    )
                }
            }
        }
    }
}

@Composable
fun FastingRecordItemRow(
    record: FastingRecordEntity,
    onDelete: () -> Unit
) {
    val hijriDate = remember(record.dateStr) {
        IslamicCalendarHelper.getHijriDateForString(record.dateStr)
    }

    val typeColor = when {
        record.fastingType.contains("Sunnah", ignoreCase = true) -> GoldAccent
        record.fastingType.contains("White", ignoreCase = true) -> Color(0xFF0288D1)
        record.isQadha || record.fastingType.contains("Qadha", ignoreCase = true) -> Color(0xFF7B1FA2)
        else -> EmeraldPrimary
    }

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("fasting_history_item_${record.dateStr}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                // Leading Icon Badge
                Surface(
                    shape = CircleShape,
                    color = typeColor.copy(alpha = 0.15f),
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = when {
                                record.fastingType.contains("Sunnah", ignoreCase = true) -> Icons.Default.VolunteerActivism
                                record.fastingType.contains("White", ignoreCase = true) -> Icons.Default.Brightness5
                                record.isQadha || record.fastingType.contains("Qadha", ignoreCase = true) -> Icons.Default.Restore
                                else -> Icons.Default.NightsStay
                            },
                            contentDescription = null,
                            tint = typeColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = record.dateStr,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = typeColor.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = record.fastingType,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = typeColor
                                ),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    if (hijriDate != null) {
                        Text(
                            text = "${hijriDate.day} ${hijriDate.monthName} ${hijriDate.year} AH • ${hijriDate.dayOfWeek}",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (record.note.isNotBlank()) {
                        Text(
                            text = "Note: ${record.note}",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            // Status Badge & Delete Icon
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = EmeraldPrimary.copy(alpha = 0.12f),
                    modifier = Modifier.size(28.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Completed",
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("delete_fast_${record.dateStr}")
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Delete Fast",
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun FastingEmptyStateCard(
    onLogFastClick: (() -> Unit)? = null
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("fasting_empty_state_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = EmeraldPrimary.copy(alpha = 0.1f),
                modifier = Modifier.size(54.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.NightsStay, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(28.dp))
                }
            }

            Text(
                text = "No Fasting Records Found",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "\"Every deed of the son of Adam will be multiplied for him, a good deed having ten times its reward, up to seven hundred times, except fasting, which is for Allah, and He will reward it.\" (Muslim)",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (onLogFastClick != null) {
                Button(
                    onClick = onLogFastClick,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Log Your First Fast")
                }
            }
        }
    }
}

@Composable
fun LogPastFastDialog(
    onDismiss: () -> Unit,
    onSave: (dateStr: String, type: String, isQadha: Boolean, note: String) -> Unit
) {
    val sdf = remember { SimpleDateFormat("yyyy-MM-dd", Locale.US) }
    val todayStr = remember { sdf.format(Date()) }
    val yesterdayStr = remember {
        val c = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }
        sdf.format(c.time)
    }

    var selectedDate by remember { mutableStateOf(todayStr) }
    var selectedType by remember { mutableStateOf("Ramadan") }
    var isQadha by remember { mutableStateOf(false) }
    var noteText by remember { mutableStateOf("") }

    val fastingTypes = listOf("Ramadan", "Sunnah (Mon/Thu)", "White Days", "Qadha", "Nafilah")

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("log_past_fast_dialog"),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.NightsStay, contentDescription = null, tint = EmeraldPrimary)
                Text("Log Completed Fast")
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Select Date",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Quick Date Pills
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedDate == todayStr,
                        onClick = { selectedDate = todayStr },
                        label = { Text("Today", fontSize = 11.sp) }
                    )
                    FilterChip(
                        selected = selectedDate == yesterdayStr,
                        onClick = { selectedDate = yesterdayStr },
                        label = { Text("Yesterday", fontSize = 11.sp) }
                    )
                }

                // Date Text Field
                OutlinedTextField(
                    value = selectedDate,
                    onValueChange = { selectedDate = it },
                    label = { Text("Date (YYYY-MM-DD)") },
                    leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("fast_date_input")
                )

                Text(
                    text = "Fasting Category",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Type Chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("fast_type_selector")
                ) {
                    items(fastingTypes) { type ->
                        FilterChip(
                            selected = selectedType == type,
                            onClick = {
                                selectedType = type
                                if (type == "Qadha") isQadha = true
                            },
                            label = { Text(type, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = EmeraldPrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                // Qadha Checkbox
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isQadha = !isQadha },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isQadha,
                        onCheckedChange = { isQadha = it },
                        colors = CheckboxDefaults.colors(checkedColor = EmeraldPrimary)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Mark as Qadha (Make-up fast)",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                // Note Text Field
                OutlinedTextField(
                    value = noteText,
                    onValueChange = { noteText = it },
                    label = { Text("Notes (optional)") },
                    placeholder = { Text("e.g. Fasted Shawwal, made up travel fast") },
                    maxLines = 2,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("fast_note_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (selectedDate.isNotBlank()) {
                        onSave(selectedDate.trim(), selectedType, isQadha, noteText.trim())
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                modifier = Modifier.testTag("save_fast_dialog_button")
            ) {
                Text("Save Fast")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
