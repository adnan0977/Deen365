package com.example.ui.screens.khatam

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
fun KhatamPlannerScreen(
    state: DeenUiState,
    viewModel: DeenViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.selectTab(MainTab.DASHBOARD)
    }

    val planner = state.khatamPlannerState
    var showDuaDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                "Khatam Quran Planner",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Surface(shape = RoundedCornerShape(6.dp), color = GoldAccent.copy(alpha = 0.2f)) {
                                Text(
                                    text = "Target",
                                    color = GoldAccent,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Daily Pacing • Micro-habits after each Salah",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.selectTab(MainTab.DASHBOARD) },
                        modifier = Modifier.testTag("khatam_back_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showDuaDialog = true },
                        modifier = Modifier.testTag("khatam_dua_button")
                    ) {
                        Icon(Icons.Default.MenuBook, contentDescription = "Khatam Du'a", tint = GoldAccent)
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
            // Overall Progress Hero Card
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.fillMaxWidth().testTag("khatam_hero_card")
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(shape = RoundedCornerShape(8.dp), color = GoldAccent.copy(alpha = 0.2f)) {
                                Text(
                                    text = "${planner.selectedTargetDays}-DAY TARGET",
                                    color = GoldLight,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("🔥", fontSize = 12.sp)
                                Text(
                                    text = "${planner.streakDays} days streak",
                                    color = Color.White,
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Column {
                                Text(
                                    text = "${planner.completedPages} / ${planner.totalQuranPages} Pages",
                                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold, color = Color.White)
                                )
                                Text(
                                    text = "Currently in Juz ${planner.currentJuz} • ${planner.pagesRemaining} pages remaining",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = GoldLight
                                )
                            }

                            Text(
                                text = "${(planner.completionPercentage * 100).toInt()}%",
                                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Black, color = Color.White)
                            )
                        }

                        LinearProgressIndicator(
                            progress = { planner.completionPercentage },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = GoldAccent,
                            trackColor = Color.White.copy(alpha = 0.25f)
                        )
                    }
                }
            }

            // Quick Pacing Strategy Presets
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Choose Completion Pace",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        KhatamPlannerData.PRESETS.forEach { preset ->
                            val isSelected = planner.selectedTargetDays == preset.targetDays
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.setKhatamTargetDays(preset.targetDays) },
                                label = { Text("${preset.targetDays} Days", fontSize = 11.sp) },
                                modifier = Modifier.weight(1f).testTag("preset_${preset.targetDays}")
                            )
                        }
                    }
                }
            }

            // Today's Prayer Pacing Checklist Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.25f)),
                    modifier = Modifier.fillMaxWidth().testTag("khatam_checklist_card")
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
                                    text = "Today's Prayer Micro-Goals",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "Read ~${"%.1f".format(planner.pagesNeededPerPrayer)} pages after each prayer to stay on track",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Surface(shape = RoundedCornerShape(8.dp), color = EmeraldPrimary.copy(alpha = 0.12f)) {
                                Text(
                                    text = "${planner.todayChecklist.completedCount} / 5 Done",
                                    color = EmeraldPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        // Prayer Rows
                        PrayerGoalRow("Fajr (Morning)", planner.todayChecklist.fajrCompleted) {
                            viewModel.toggleKhatamPrayerGoal("Fajr")
                        }
                        PrayerGoalRow("Dhuhr (Midday)", planner.todayChecklist.dhuhrCompleted) {
                            viewModel.toggleKhatamPrayerGoal("Dhuhr")
                        }
                        PrayerGoalRow("Asr (Afternoon)", planner.todayChecklist.asrCompleted) {
                            viewModel.toggleKhatamPrayerGoal("Asr")
                        }
                        PrayerGoalRow("Maghrib (Sunset)", planner.todayChecklist.maghribCompleted) {
                            viewModel.toggleKhatamPrayerGoal("Maghrib")
                        }
                        PrayerGoalRow("Isha (Night)", planner.todayChecklist.ishaCompleted) {
                            viewModel.toggleKhatamPrayerGoal("Isha")
                        }
                    }
                }
            }

            // Quick Page Logging Actions
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "Quick Page Logger",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { viewModel.addKhatamPages(1) },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                                modifier = Modifier.weight(1f).testTag("khatam_add_1_page")
                            ) {
                                Text("+1 Page", fontSize = 12.sp)
                            }

                            Button(
                                onClick = { viewModel.addKhatamPages(4) },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                                modifier = Modifier.weight(1f).testTag("khatam_add_4_pages")
                            ) {
                                Text("+4 Pgs (1 Salah)", fontSize = 12.sp)
                            }

                            Button(
                                onClick = { viewModel.addKhatamPages(20) },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = Color.Black),
                                modifier = Modifier.weight(1f).testTag("khatam_add_1_juz")
                            ) {
                                Text("+1 Juz", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            IconButton(
                                onClick = { viewModel.addKhatamPages(-1) },
                                modifier = Modifier.size(40.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surface)
                            ) {
                                Icon(Icons.Default.Undo, contentDescription = "Undo", modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }

            // Du'a Khatm al-Quran CTA
            item {
                OutlinedButton(
                    onClick = { showDuaDialog = true },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("open_khatm_dua_button")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(18.dp))
                        Text("View Du'a Khatm al-Quran (Upon Completion)", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // Khatam Du'a Dialog
    if (showDuaDialog) {
        AlertDialog(
            onDismissRequest = { showDuaDialog = false },
            title = {
                Text(
                    "Du'a Khatm al-Quran",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = EmeraldPrimary)
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = KhatamPlannerData.KHATM_AL_QURAN_DUA_ARABIC.trim(),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        textAlign = TextAlign.End,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                    Text(
                        text = KhatamPlannerData.KHATM_AL_QURAN_DUA_TRANSLATION.trim(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showDuaDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
private fun PrayerGoalRow(
    title: String,
    isCompleted: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { onToggle() }
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = if (isCompleted) FontWeight.Bold else FontWeight.Normal,
                color = if (isCompleted) EmeraldPrimary else MaterialTheme.colorScheme.onSurface
            )
        )

        Checkbox(
            checked = isCompleted,
            onCheckedChange = { onToggle() }
        )
    }
}
