package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.EmeraldPrimaryVariant
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldLight
import com.example.ui.viewmodel.DeenUiState
import com.example.ui.viewmodel.DeenViewModel
import java.util.Calendar

@Composable
fun FastingTrackerCard(
    state: DeenUiState,
    viewModel: DeenViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showDuaSection by remember { mutableStateOf(false) }

    val cal = remember { Calendar.getInstance() }
    val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
    val isSunnahDayToday = dayOfWeek == Calendar.MONDAY || dayOfWeek == Calendar.THURSDAY

    val pastDays = remember(state.fastingHistory) {
        viewModel.getPastDaysForFasting()
    }

    val historyMap = remember(state.fastingHistory) {
        state.fastingHistory.associateBy { it.dateStr }
    }

    val currentFastingType = state.todayFastingRecord?.fastingType ?: if (isSunnahDayToday) "Sunnah (Mon/Thu)" else "Ramadan"

    val fastingTypes = listOf(
        "Ramadan",
        "Sunnah (Mon/Thu)",
        "White Days",
        "Qadha",
        "Nafilah"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("fasting_tracker_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.horizontalGradient(
                listOf(
                    EmeraldPrimary.copy(alpha = if (state.isFastedToday) 0.6f else 0.25f),
                    GoldAccent.copy(alpha = if (state.isFastedToday) 0.7f else 0.25f)
                )
            )
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Row
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
                        shape = RoundedCornerShape(12.dp),
                        color = if (state.isFastedToday) EmeraldPrimary else MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Icon(
                            imageVector = if (state.isFastedToday) Icons.Default.NightsStay else Icons.Outlined.NightsStay,
                            contentDescription = "Fasting Icon",
                            tint = if (state.isFastedToday) GoldLight else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .padding(8.dp)
                                .size(24.dp)
                        )
                    }

                    Column {
                        Text(
                            text = "Fasting Tracker (Sawm)",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (state.isFastedToday) "Recorded as $currentFastingType" else "Track Ramadan & Sunnah fasts",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (state.isFastedToday) EmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Total Fasted Badge
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = GoldAccent.copy(alpha = 0.15f),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(GoldAccent, GoldLight)))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = GoldAccent,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "${state.totalFastedDaysCount} Days",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Primary Toggle Switch Banner
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = if (state.isFastedToday) EmeraldPrimary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                border = if (state.isFastedToday) CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(EmeraldPrimary, GoldAccent))) else null,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.toggleFasting() }
                    .testTag("fasting_today_toggle_container")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "Fasting Today?",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (isSunnahDayToday) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = GoldAccent.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = "Sunnah Day",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = GoldAccent,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Text(
                            text = if (state.isFastedToday) {
                                "Alhamdulillah! Fasting is logged for today."
                            } else if (isSunnahDayToday) {
                                "Today is ${if (dayOfWeek == Calendar.MONDAY) "Monday" else "Thursday"} (Sunnah Fasting). Toggle to record."
                            } else {
                                "Tap switch to record your Sawm for today."
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = if (state.isFastedToday) EmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Switch(
                        checked = state.isFastedToday,
                        onCheckedChange = { viewModel.toggleFasting() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = EmeraldPrimary,
                            uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                            uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        modifier = Modifier.testTag("fasting_today_switch")
                    )
                }
            }

            // Fasting Type Selector Chips
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Fasting Category",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(fastingTypes) { type ->
                        val isSelected = currentFastingType.equals(type, ignoreCase = true) && state.isFastedToday
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                viewModel.setFastingType(type)
                                if (!state.isFastedToday) {
                                    viewModel.toggleFasting(type)
                                }
                            },
                            label = { Text(type) },
                            leadingIcon = {
                                Icon(
                                    imageVector = when (type) {
                                        "Ramadan" -> Icons.Default.NightsStay
                                        "Sunnah (Mon/Thu)" -> Icons.Default.VolunteerActivism
                                        "White Days" -> Icons.Default.Brightness5
                                        "Qadha" -> Icons.Default.Restore
                                        else -> Icons.Default.Star
                                    },
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
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
            }

            // Visual Progress for Fasting Month (Pie / Donut Chart)
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("fasting_card_pie_progress")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Fasting Month Progress",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(EmeraldPrimary)
                            )
                            Text(
                                text = "Completed: ${state.fastingTrackerDaysCount} days",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, fontWeight = FontWeight.SemiBold),
                                color = EmeraldPrimary
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.outline)
                            )
                            val remaining = (state.fastingMonthTotalDays - state.fastingTrackerDaysCount).coerceAtLeast(0)
                            Text(
                                text = "Remaining: $remaining of ${state.fastingMonthTotalDays} days",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Compact Donut/Pie Chart
                    Box(
                        modifier = Modifier.size(76.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        FastingMonthProgressPieChart(
                            completedDays = state.fastingTrackerDaysCount,
                            totalDays = state.fastingMonthTotalDays,
                            isDonut = true,
                            onToggleStyle = {},
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            // 7-Day Fasting History Quick Strip
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Past 7 Days",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Tap day to toggle log",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    pastDays.forEach { dateInfo ->
                        val record = historyMap[dateInfo.dateStr]
                        val isFastedOnDate = record?.isFasted == true

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    viewModel.toggleFastingForDate(dateInfo.dateStr)
                                }
                                .padding(4.dp)
                                .testTag("fasting_day_${dateInfo.dateStr}")
                        ) {
                            Text(
                                text = dateInfo.dayName,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (dateInfo.isToday) FontWeight.Bold else FontWeight.Normal
                                ),
                                color = if (dateInfo.isToday) EmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            isFastedOnDate -> EmeraldPrimary
                                            dateInfo.isToday -> EmeraldPrimary.copy(alpha = 0.15f)
                                            else -> MaterialTheme.colorScheme.surfaceVariant
                                        }
                                    )
                                    .then(
                                        if (dateInfo.isSunnahDay && !isFastedOnDate) {
                                            Modifier.border(1.dp, GoldAccent.copy(alpha = 0.8f), CircleShape)
                                        } else {
                                            Modifier
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isFastedOnDate) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Fasted",
                                        tint = GoldLight,
                                        modifier = Modifier.size(18.dp)
                                    )
                                } else {
                                    Text(
                                        text = dateInfo.dayNumber,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                        color = if (dateInfo.isToday) EmeraldPrimary else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }

                            if (dateInfo.isSunnahDay) {
                                Text(
                                    text = "Sunnah",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                    color = GoldAccent
                                )
                            } else {
                                Text(
                                    text = " ",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp)
                                )
                            }
                        }
                    }
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = EmeraldPrimary.copy(alpha = 0.08f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.setTab(com.example.ui.viewmodel.MainTab.FASTING) }
                        .testTag("view_full_fasting_log_button")
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
                            Icon(Icons.Default.DateRange, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(16.dp))
                            Text(
                                text = "View Full Fasting Log & History",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = EmeraldPrimary
                            )
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(18.dp))
                    }
                }
            }

            // Expandable Fasting Duas (Niyyah & Iftar)
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDuaSection = !showDuaSection }
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MenuBook,
                                contentDescription = null,
                                tint = GoldAccent,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Fasting Duas (Niyyah & Iftar)",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Icon(
                            imageVector = if (showDuaSection) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = "Toggle Duas",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    AnimatedVisibility(visible = showDuaSection) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            // Suhoor Niyyah
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.surface,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Suhoor Intention (Niyyah)",
                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                            color = EmeraldPrimary
                                        )
                                        IconButton(
                                            onClick = {
                                                val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                                cm.setPrimaryClip(ClipData.newPlainText("Niyyah", "وَبِصَوْمِ غَدٍ نَوَيْتُ مِنْ شَهْرِ رَمَضَانَ"))
                                                Toast.makeText(context, "Niyyah copied to clipboard", Toast.LENGTH_SHORT).show()
                                            },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(16.dp))
                                        }
                                    }

                                    Text(
                                        text = "وَبِصَوْمِ غَدٍ نَوَيْتُ مِنْ شَهْرِ رَمَضَانَ",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface,
                                        textAlign = TextAlign.End,
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    Text(
                                        text = "Wa bisawmi ghadin nawaiytu min shahri ramadan",
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                        color = GoldAccent
                                    )

                                    Text(
                                        text = "I intend to keep the fast for tomorrow in the month of Ramadan.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            // Iftar Dua
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.surface,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Dua at Iftar (Breaking Fast)",
                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                            color = GoldAccent
                                        )
                                        IconButton(
                                            onClick = {
                                                val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                                cm.setPrimaryClip(ClipData.newPlainText("Iftar Dua", "ذَهَبَ الظَّمَأُ وَابْتَلَّتِ الْعُرُوقُ وَثَبَتَ الأَجْرُ إِنْ شَاءَ اللَّهُ"))
                                                Toast.makeText(context, "Iftar Dua copied to clipboard", Toast.LENGTH_SHORT).show()
                                            },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(16.dp))
                                        }
                                    }

                                    Text(
                                        text = "ذَهَبَ الظَّمَأُ وَابْتَلَّتِ الْعُرُوقُ وَثَبَتَ الأَجْرُ إِنْ شَاءَ اللَّهُ",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface,
                                        textAlign = TextAlign.End,
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    Text(
                                        text = "Dhahaba az-zama'u wabtallatil-'urooqu wa thabatal-ajru in sha' Allah",
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                        color = EmeraldPrimary
                                    )

                                    Text(
                                        text = "The thirst has gone, the veins are moistened, and the reward is confirmed, if Allah wills. (Abu Dawud)",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
