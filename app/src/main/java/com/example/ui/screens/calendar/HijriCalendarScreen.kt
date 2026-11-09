package com.example.ui.screens.calendar

import androidx.activity.compose.BackHandler
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldLight
import com.example.ui.viewmodel.DeenUiState
import com.example.ui.viewmodel.DeenViewModel
import com.example.ui.viewmodel.MainTab

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HijriCalendarScreen(
    state: DeenUiState,
    viewModel: DeenViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.selectTab(MainTab.DASHBOARD)
    }

    val calState = state.hijriCalendarState
    val currentMonthInfo = HijriCalendarData.HIJRI_MONTHS.getOrNull(calState.selectedViewMonth - 1)
        ?: HijriCalendarData.HIJRI_MONTHS[8] // Ramadan fallback

    val sunnahFasts = remember(calState.selectedViewMonth) {
        HijriCalendarData.getSunnahFastsForMonth(calState.selectedViewMonth)
    }

    val upcomingOccasion = remember(calState.currentHijriMonth, calState.currentHijriDay) {
        HijriCalendarData.getUpcomingSacredOccasion(calState.currentHijriMonth, calState.currentHijriDay)
    }

    var showOccasionDialog by remember { mutableStateOf<SacredOccasion?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                "Hijri & Sunnah Calendar",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = EmeraldPrimary.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "${calState.currentHijriYear} AH",
                                    color = EmeraldPrimary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Lunar Calendar • Sacred Occasions • Voluntary Fasts",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.selectTab(MainTab.DASHBOARD) },
                        modifier = Modifier.testTag("hijri_back_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.adjustHijriOffset(if (calState.manualOffsetDays >= 2) -2 else calState.manualOffsetDays + 1) },
                        modifier = Modifier.testTag("hijri_moonsighting_button")
                    ) {
                        Icon(Icons.Default.NightsStay, contentDescription = "Moonsighting Offset", tint = GoldAccent)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 14.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Month Switcher Header
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.fillMaxWidth().testTag("hijri_month_card")
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = {
                                    val prev = if (calState.selectedViewMonth <= 1) 12 else calState.selectedViewMonth - 1
                                    viewModel.setHijriViewMonth(prev)
                                },
                                modifier = Modifier.size(36.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.15f))
                            ) {
                                Icon(Icons.Default.ChevronLeft, contentDescription = "Previous Month", tint = Color.White)
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = currentMonthInfo.arabicName,
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = GoldLight)
                                )
                                Text(
                                    text = "${currentMonthInfo.englishName} (${calState.currentHijriYear} AH)",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold, color = Color.White)
                                )
                                if (currentMonthInfo.isSacredMonth) {
                                    Surface(shape = RoundedCornerShape(4.dp), color = GoldAccent) {
                                        Text(
                                            text = "Sacred Month (Shahr Haram)",
                                            color = Color.Black,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Black,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }

                            IconButton(
                                onClick = {
                                    val next = if (calState.selectedViewMonth >= 12) 1 else calState.selectedViewMonth + 1
                                    viewModel.setHijriViewMonth(next)
                                },
                                modifier = Modifier.size(36.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.15f))
                            ) {
                                Icon(Icons.Default.ChevronRight, contentDescription = "Next Month", tint = Color.White)
                            }
                        }

                        // Local Moonsighting offset indicator
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Moonsighting Declaration: ${if (calState.manualOffsetDays >= 0) "+" else ""}${calState.manualOffsetDays} day",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                            TextButton(
                                onClick = { viewModel.adjustHijriOffset(0) },
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("Reset to default", fontSize = 11.sp, color = GoldLight)
                            }
                        }
                    }
                }
            }

            // Upcoming Sacred Occasion Banner
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth().testTag("upcoming_occasion_banner")
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Surface(shape = RoundedCornerShape(6.dp), color = GoldAccent.copy(alpha = 0.2f)) {
                                Text(
                                    text = "NEXT SACRED OCCASION",
                                    color = GoldAccent,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = upcomingOccasion.title,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "${upcomingOccasion.arabicTitle} • ${upcomingOccasion.hijriDay} ${HijriCalendarData.HIJRI_MONTHS[upcomingOccasion.hijriMonth - 1].englishName}",
                                style = MaterialTheme.typography.bodySmall,
                                color = EmeraldPrimary
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            val days = upcomingOccasion.daysRemaining(calState.currentHijriMonth, calState.currentHijriDay)
                            Text(
                                text = "$days",
                                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold, color = EmeraldPrimary)
                            )
                            Text("days left", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            // Sunnah Fasting Habit Tracker for Current Month
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Sunnah Fasting Schedule",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("🔥", fontSize = 12.sp)
                            Text(
                                text = "${calState.fastingStreakDays} day streak",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = GoldAccent)
                            )
                        }
                    }

                    // Fasting checklist items
                    sunnahFasts.forEach { fast ->
                        val isFasted = calState.completedFastIds.contains(fast.id)
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isFasted) EmeraldPrimary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isFasted) EmeraldPrimary else Color.Transparent
                            ),
                            modifier = Modifier.fillMaxWidth().testTag("sunnah_fast_${fast.id}")
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
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
                                        color = if (isFasted) EmeraldPrimary else MaterialTheme.colorScheme.surfaceVariant,
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = "${fast.hijriDay}",
                                                style = MaterialTheme.typography.labelMedium.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isFasted) Color.White else MaterialTheme.colorScheme.onSurface
                                                )
                                            )
                                        }
                                    }

                                    Column {
                                        Text(
                                            text = fast.title,
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                        )
                                        Text(
                                            text = fast.propheticVirtue,
                                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1
                                        )
                                    }
                                }

                                IconButton(
                                    onClick = { viewModel.toggleSunnahFast(fast.id) },
                                    modifier = Modifier.size(36.dp).testTag("toggle_fast_${fast.id}")
                                ) {
                                    Icon(
                                        imageVector = if (isFasted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                        contentDescription = "Toggle Fast",
                                        tint = if (isFasted) EmeraldPrimary else MaterialTheme.colorScheme.outlineVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Sacred Occasions List for Entire Year
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Major Islamic Occasions (1447 AH)",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )

                    HijriCalendarData.SACRED_OCCASIONS.forEach { occ ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth().clickable { showOccasionDialog = occ }
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(occ.title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                                    Text(occ.arabicTitle, style = MaterialTheme.typography.bodySmall, color = EmeraldPrimary)
                                }

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = EmeraldPrimary.copy(alpha = 0.12f)
                                ) {
                                    Text(
                                        text = "${occ.hijriDay} ${HijriCalendarData.HIJRI_MONTHS[occ.hijriMonth - 1].englishName}",
                                        color = EmeraldPrimary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Occasion Details Dialog
    showOccasionDialog?.let { occ ->
        AlertDialog(
            onDismissRequest = { showOccasionDialog = null },
            title = {
                Column {
                    Text(occ.title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    Text(occ.arabicTitle, style = MaterialTheme.typography.titleSmall, color = EmeraldPrimary)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(occ.description, style = MaterialTheme.typography.bodyMedium)
                    Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                    Text("Virtues & Sunnah:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                    Text(occ.virtues, style = MaterialTheme.typography.bodySmall)
                }
            },
            confirmButton = {
                TextButton(onClick = { showOccasionDialog = null }) {
                    Text("Close")
                }
            }
        )
    }
}
