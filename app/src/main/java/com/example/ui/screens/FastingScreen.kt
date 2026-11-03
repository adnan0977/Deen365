package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.seed.FastingGuideData
import com.example.domain.IslamicCalendarHelper
import androidx.compose.foundation.clickable
import com.example.ui.components.FastingHistoryLogView
import com.example.ui.components.FastingMonthProgressCard
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldLight
import com.example.ui.viewmodel.DeenUiState
import com.example.ui.viewmodel.DeenViewModel

@Composable
fun FastingScreen(
    state: DeenUiState,
    viewModel: DeenViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Calendar & Timings, 1: Fasting Tracker, 2: Duas & Niyyah, 3: Fiqh & Compensation
    var hijriOffsetDays by remember { mutableIntStateOf(0) } // Location-based manual moon sighting adjustment (-2 to +2 days)

    val todayHijri = remember(hijriOffsetDays) {
        IslamicCalendarHelper.getTodayHijriDate(hijriOffsetDays)
    }

    val calendarDays = remember(hijriOffsetDays) {
        IslamicCalendarHelper.getMonthDays(hijriOffsetDays)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Tab Header for Fasting
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = EmeraldPrimary,
            modifier = Modifier.fillMaxWidth().testTag("fasting_top_tabs")
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Calendar", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(18.dp)) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Assistant", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp)) }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text("Sawm Tracker", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp)) }
            )
            Tab(
                selected = selectedTab == 3,
                onClick = { selectedTab = 3 },
                text = { Text("History Log", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.DateRange, contentDescription = null, modifier = Modifier.size(18.dp)) }
            )
            Tab(
                selected = selectedTab == 4,
                onClick = { selectedTab = 4 },
                text = { Text("Duas & Intentions", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.VolunteerActivism, contentDescription = null, modifier = Modifier.size(18.dp)) }
            )
            Tab(
                selected = selectedTab == 5,
                onClick = { selectedTab = 5 },
                text = { Text("Fiqh & Rules", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(18.dp)) }
            )
        }

        when (selectedTab) {
            0 -> {
                // Calendar & Accurate Suhoor/Iftar Timings
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Hijri Date Banner with Moon Adjustment
                    item {
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = EmeraldPrimary),
                            modifier = Modifier.fillMaxWidth().testTag("fasting_hijri_banner")
                        ) {
                            Column(
                                modifier = Modifier.padding(18.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "TODAY'S ISLAMIC DATE",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = GoldLight,
                                                letterSpacing = 1.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                        Text(
                                            text = "${todayHijri.day} ${todayHijri.monthName} ${todayHijri.year} AH",
                                            style = MaterialTheme.typography.headlineSmall.copy(
                                                fontWeight = FontWeight.ExtraBold,
                                                color = Color.White
                                            )
                                        )
                                        Text(
                                            text = "${todayHijri.dayOfWeek}, ${todayHijri.gregorianFormatted}",
                                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.White.copy(alpha = 0.85f))
                                        )
                                    }

                                    Surface(
                                        shape = CircleShape,
                                        color = GoldAccent,
                                        modifier = Modifier.size(48.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                Icons.Default.NightsStay,
                                                contentDescription = null,
                                                tint = Color.Black,
                                                modifier = Modifier.size(26.dp)
                                            )
                                        }
                                    }
                                }

                                // Moon Sighting Location Offset
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Location Moon Offset: ${if (hijriOffsetDays > 0) "+$hijriOffsetDays" else hijriOffsetDays} day(s)",
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = Color.White.copy(alpha = 0.9f))
                                    )
                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        FilledTonalIconButton(
                                            onClick = { if (hijriOffsetDays > -2) hijriOffsetDays-- },
                                            colors = IconButtonDefaults.filledTonalIconButtonColors(
                                                containerColor = Color.White.copy(alpha = 0.2f),
                                                contentColor = Color.White
                                            ),
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(Icons.Default.Remove, contentDescription = "Decrease day", modifier = Modifier.size(16.dp))
                                        }
                                        FilledTonalIconButton(
                                            onClick = { if (hijriOffsetDays < 2) hijriOffsetDays++ },
                                            colors = IconButtonDefaults.filledTonalIconButtonColors(
                                                containerColor = Color.White.copy(alpha = 0.2f),
                                                contentColor = Color.White
                                            ),
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(Icons.Default.Add, contentDescription = "Increase day", modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Suhoor End & Iftar Timetable Cards
                    item {
                        Card(
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            modifier = Modifier.fillMaxWidth()
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
                                    Text(
                                        text = "Fasting Timings • ${state.selectedCity.name}",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = EmeraldPrimary.copy(alpha = 0.15f)
                                    ) {
                                        Text(
                                            text = state.selectedCalculationMethod.title,
                                            style = MaterialTheme.typography.labelSmall.copy(color = EmeraldPrimary, fontWeight = FontWeight.Bold),
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(14.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(14.dp),
                                            verticalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Text("Imsak / Suhoor End", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Text(
                                                text = state.prayerSchedule.suhoorEnd,
                                                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold, color = EmeraldPrimary)
                                            )
                                            Text("Stop eating at Fajr dawn", style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(14.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(14.dp),
                                            verticalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Text("Iftar / Fast Break", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Text(
                                                text = state.prayerSchedule.iftar,
                                                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold, color = GoldAccent)
                                            )
                                            Text("Break fast at sunset", style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Horizontal Scrolling Calendar Strip of Upcoming Days
                    item {
                        Text(
                            text = "Hijri & English Dual Calendar",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    item {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(calendarDays) { day ->
                                val isToday = day.gregorianFormatted == todayHijri.gregorianFormatted
                                Card(
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isToday) EmeraldPrimary else MaterialTheme.colorScheme.surface
                                    ),
                                    border = if (day.isSunnahFast) androidx.compose.foundation.BorderStroke(1.5.dp, GoldAccent) else null,
                                    modifier = Modifier.width(120.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(
                                            text = day.dayOfWeek,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = if (isToday) GoldLight else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        )
                                        Text(
                                            text = "${day.day}",
                                            style = MaterialTheme.typography.titleLarge.copy(
                                                fontWeight = FontWeight.ExtraBold,
                                                color = if (isToday) Color.White else MaterialTheme.colorScheme.onSurface
                                            )
                                        )
                                        Text(
                                            text = day.monthName.take(7),
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = if (isToday) Color.White.copy(alpha = 0.9f) else EmeraldPrimary,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                        Text(
                                            text = day.gregorianFormatted.substringBefore(","),
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontSize = 10.sp,
                                                color = if (isToday) Color.White.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        )

                                        if (day.fastingOccasion != null) {
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = if (isToday) GoldAccent else GoldAccent.copy(alpha = 0.2f),
                                                modifier = Modifier.padding(top = 4.dp)
                                            ) {
                                                Text(
                                                    text = if (day.isWhiteDay) "White Day" else "Sunnah Fast",
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        fontSize = 9.sp,
                                                        color = if (isToday) Color.Black else GoldAccent,
                                                        fontWeight = FontWeight.Bold
                                                    ),
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
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

            1 -> {
                // Interactive Sawm / Fasting Tracker
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        FastingMonthProgressCard(
                            state = state,
                            viewModel = viewModel
                        )
                    }

                    // Banner to Full Fasting History Log
                    item {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = GoldAccent.copy(alpha = 0.12f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent.copy(alpha = 0.35f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedTab = 2 }
                                .testTag("banner_to_fasting_history_log")
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = GoldAccent,
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(Icons.Default.DateRange, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(20.dp))
                                        }
                                    }
                                    Column {
                                        Text(
                                            text = "Fasting History & Log View",
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "Review completed fasts over previous weeks & months",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Icon(Icons.Default.ChevronRight, contentDescription = "Open History", tint = GoldAccent)
                            }
                        }
                    }

                    // Sunnah Fasting Opportunities
                    item {
                        Text(
                            text = "Sunnah Fasting Schedule & Virtues",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    items(FastingGuideData.SUNNAH_FASTS) { fast ->
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(fast.name, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = EmeraldPrimary)
                                    Text(fast.arabicName, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = GoldAccent)
                                }
                                Text("Schedule: ${fast.schedule}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
                                Text("Reward: ${fast.reward}", style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("Hadith: ${fast.reference}", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = EmeraldPrimary)
                            }
                        }
                    }
                }
            }

            1 -> {
                // Fasting Assistant (3-Phase Guide, Sunnah Calendar & Ramadan Milestones)
                com.example.ui.screens.fasting.FastingAssistantView(
                    state = state,
                    viewModel = viewModel
                )
            }

            2 -> {
                // Sawm Tracker & Fasting Month Progress
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        FastingMonthProgressCard(
                            state = state,
                            viewModel = viewModel
                        )
                    }
                    item {
                        FastingHistoryLogView(
                            state = state,
                            viewModel = viewModel
                        )
                    }
                }
            }

            3 -> {
                // Dedicated Fasting History Log View (Previous Weeks & Months)
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        FastingHistoryLogView(
                            state = state,
                            viewModel = viewModel
                        )
                    }
                }
            }

            4 -> {
                // Duas & Intentions Section
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        Text(
                            text = "Authentic Fasting Supplications & Intentions",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    items(FastingGuideData.DUAS) { dua ->
                        val isPlaying = state.playingAudioId == dua.id
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            border = if (isPlaying) androidx.compose.foundation.BorderStroke(1.5.dp, GoldAccent) else null,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(dua.title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.primary)
                                        Text(dua.occasion, style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    IconButton(onClick = { viewModel.playAudio(dua.id, dua.arabicText) }) {
                                        Icon(
                                            imageVector = if (isPlaying) Icons.Default.PauseCircle else Icons.Default.PlayCircle,
                                            contentDescription = "Play Audio",
                                            tint = if (isPlaying) GoldAccent else EmeraldPrimary,
                                            modifier = Modifier.size(32.dp)
                                        )
                                    }
                                }

                                Text(
                                    text = dua.arabicText,
                                    style = MaterialTheme.typography.headlineSmall.copy(
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        lineHeight = 32.sp
                                    ),
                                    textAlign = TextAlign.Right,
                                    color = EmeraldPrimary,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Text(
                                    text = dua.transliteration,
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                    color = GoldAccent
                                )

                                Text(
                                    text = "\"${dua.translation}\"",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Text(
                                    text = "Reference: ${dua.reference}",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            5 -> {
                // Fiqh, Rules, Qada, Fidyah & Kaffarah
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        Text(
                            text = "Making Up Missed Fasts: Qada, Fidyah & Kaffarah",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    items(FastingGuideData.COMPENSATION_RULES) { rule ->
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(rule.type, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = EmeraldPrimary)
                                    Text(rule.arabicTerm, style = MaterialTheme.typography.bodySmall, color = GoldAccent)
                                }
                                Text("Applicable to: ${rule.whenApplicable}", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium), color = MaterialTheme.colorScheme.onSurface)
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Requirement: ${rule.requirement}", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(8.dp))
                                }
                                Text("Evidence: ${rule.reference}", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = EmeraldPrimary)
                            }
                        }
                    }

                    item {
                        Text(
                            text = "Days When Fasting is Forbidden (Haram / Makruh)",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    items(FastingGuideData.PROHIBITED_FASTING_DAYS) { (title, desc) ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.25f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.error)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(desc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }
    }
}
