package com.example.ui.screens.hajj

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
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
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.HajjUmrahData
import com.example.data.model.PilgrimageStep
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldLight
import com.example.ui.viewmodel.DeenUiState
import com.example.ui.viewmodel.DeenViewModel
import com.example.ui.viewmodel.MainTab

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HajjUmrahScreen(
    state: DeenUiState,
    viewModel: DeenViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.selectTab(MainTab.DASHBOARD)
    }

    val hajjState = state.hajjUmrahState
    val activeTab = hajjState.selectedMainTab

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Hajj & Umrah Companion",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = EmeraldPrimary.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "الحج والعمرة",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = FontFamily.Serif,
                                        fontWeight = FontWeight.Bold,
                                        color = EmeraldPrimary
                                    ),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Step-by-Step Rituals • Tawaf & Sa'i Counters • Du'as",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.selectTab(MainTab.DASHBOARD) },
                        modifier = Modifier.testTag("hajj_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Home"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = modifier.testTag("hajj_umrah_screen")
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Scrollable Tab Row
            ScrollableTabRow(
                selectedTabIndex = activeTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = EmeraldPrimary,
                edgePadding = 16.dp,
                modifier = Modifier.testTag("hajj_tab_row")
            ) {
                Tab(
                    selected = activeTab == 0,
                    onClick = { viewModel.setHajjUmrahMainTab(0) },
                    text = { Text("Umrah Guide", fontWeight = if (activeTab == 0) FontWeight.Bold else FontWeight.Normal) },
                    icon = { Icon(Icons.Default.Explore, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    modifier = Modifier.testTag("tab_umrah_guide")
                )
                Tab(
                    selected = activeTab == 1,
                    onClick = { viewModel.setHajjUmrahMainTab(1) },
                    text = { Text("Hajj Days", fontWeight = if (activeTab == 1) FontWeight.Bold else FontWeight.Normal) },
                    icon = { Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    modifier = Modifier.testTag("tab_hajj_days")
                )
                Tab(
                    selected = activeTab == 2,
                    onClick = { viewModel.setHajjUmrahMainTab(2) },
                    text = { Text("Tawaf Counter (${hajjState.tawafState.completedLaps}/7)", fontWeight = if (activeTab == 2) FontWeight.Bold else FontWeight.Normal) },
                    icon = { Icon(Icons.Default.RotateRight, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    modifier = Modifier.testTag("tab_tawaf_counter")
                )
                Tab(
                    selected = activeTab == 3,
                    onClick = { viewModel.setHajjUmrahMainTab(3) },
                    text = { Text("Sa'i Counter (${hajjState.saiState.completedRounds}/7)", fontWeight = if (activeTab == 3) FontWeight.Bold else FontWeight.Normal) },
                    icon = { Icon(Icons.Default.DirectionsWalk, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    modifier = Modifier.testTag("tab_sai_counter")
                )
                Tab(
                    selected = activeTab == 4,
                    onClick = { viewModel.setHajjUmrahMainTab(4) },
                    text = { Text("Talbiyah & Du'as", fontWeight = if (activeTab == 4) FontWeight.Bold else FontWeight.Normal) },
                    icon = { Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    modifier = Modifier.testTag("tab_talbiyah")
                )
                Tab(
                    selected = activeTab == 5,
                    onClick = { viewModel.setHajjUmrahMainTab(5) },
                    text = { Text("Checklist", fontWeight = if (activeTab == 5) FontWeight.Bold else FontWeight.Normal) },
                    icon = { Icon(Icons.Default.Checklist, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    modifier = Modifier.testTag("tab_checklist")
                )
            }

            // Tab Content
            when (activeTab) {
                0 -> PilgrimageStepsList(
                    steps = HajjUmrahData.UMRAH_STEPS,
                    onStepClick = { viewModel.selectHajjUmrahStepDetail(it) }
                )
                1 -> PilgrimageStepsList(
                    steps = HajjUmrahData.HAJJ_DAYS,
                    onStepClick = { viewModel.selectHajjUmrahStepDetail(it) }
                )
                2 -> TawafCounterContent(
                    tawafState = hajjState.tawafState,
                    onIncrementLap = { viewModel.incrementTawafLap() },
                    onReset = { viewModel.resetTawafTracker() }
                )
                3 -> SaiCounterContent(
                    saiState = hajjState.saiState,
                    onIncrementRound = { viewModel.incrementSaiRound() },
                    onReset = { viewModel.resetSaiTracker() }
                )
                4 -> TalbiyahAndDuasContent(
                    isPlaying = hajjState.isPlayingTalbiyah,
                    onTogglePlay = { viewModel.toggleTalbiyahPlayback() }
                )
                5 -> HajjUmrahChecklistContent(
                    checklist = hajjState.checklist,
                    onToggleItem = { viewModel.toggleHajjUmrahChecklistItem(it) }
                )
            }
        }
    }

    // Step Detail Modal Dialog
    if (hajjState.selectedStepDetail != null) {
        PilgrimageStepDetailDialog(
            step = hajjState.selectedStepDetail,
            onDismiss = { viewModel.selectHajjUmrahStepDetail(null) }
        )
    }
}

// ==========================================
// 1. STEPS LIST (UMRAH & HAJJ DAYS)
// ==========================================
@Composable
private fun PilgrimageStepsList(
    steps: List<PilgrimageStep>,
    onStepClick: (PilgrimageStep) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        items(steps) { step ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onStepClick(step) }
                    .testTag("step_card_${step.id}"),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = EmeraldPrimary.copy(alpha = 0.12f),
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(text = step.iconEmoji, fontSize = 20.sp)
                        }
                    }

                    Spacer(Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = step.title,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = step.arabicTitle,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldPrimary
                                )
                            )
                        }

                        Spacer(Modifier.height(2.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Place,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = step.location,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(Modifier.height(4.dp))

                        Text(
                            text = step.summary,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp, lineHeight = 16.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2
                        )
                    }

                    Spacer(Modifier.width(8.dp))

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Details",
                        tint = MaterialTheme.colorScheme.outlineVariant
                    )
                }
            }
        }
    }
}

// ==========================================
// 2. TAWAF 7-LAP COUNTER
// ==========================================
@Composable
private fun TawafCounterContent(
    tawafState: com.example.data.model.TawafTrackerState,
    onIncrementLap: () -> Unit,
    onReset: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Circular Progress Banner
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(200.dp)
                    .padding(16.dp)
            ) {
                CircularProgressIndicator(
                    progress = { (tawafState.completedLaps.toFloat() / 7f).coerceIn(0f, 1f) },
                    modifier = Modifier.fillMaxSize(),
                    color = EmeraldPrimary,
                    strokeWidth = 12.dp,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "LAP",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 2.sp
                        )
                    )
                    Text(
                        text = if (tawafState.isFinished) "7/7" else "${tawafState.currentLap} / 7",
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = if (tawafState.isFinished) GoldAccent else EmeraldPrimary
                        ),
                        modifier = Modifier.testTag("tawaf_lap_text")
                    )
                    Text(
                        text = if (tawafState.isFinished) "Tawaf Complete! ✓" else "${7 - tawafState.completedLaps} laps remaining",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = if (tawafState.isFinished) GoldAccent else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            // Guidance Card for Current Lap
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Essential Tawaf Instructions:",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "• Start/End each lap at the Black Stone (Hajar al-Aswad) line.\n" +
                                "• Point towards the Black Stone and recite: Bismillahi Allahu Akbar.\n" +
                                "• Between Yemeni Corner & Black Stone recite: Rabbana atina fid-dunya hasanatan wa fil-akhirati hasanatan wa qina 'adhaban-nar.",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp, lineHeight = 16.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (tawafState.isFinished) {
                Spacer(Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = EmeraldPrimary.copy(alpha = 0.12f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "🎉 All 7 Laps Completed Mabroor!",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = EmeraldPrimary)
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Now proceed behind Maqam Ibrahim to pray 2 light Rak'ahs, then drink Zamzam water before heading to Sa'i.",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        // Action Buttons: Complete Lap & Reset
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = onReset,
                modifier = Modifier.weight(1f).testTag("tawaf_reset_button"),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("Reset")
            }

            Button(
                onClick = onIncrementLap,
                enabled = !tawafState.isFinished,
                modifier = Modifier.weight(2f).testTag("tawaf_lap_button"),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
            ) {
                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text(
                    text = if (tawafState.isFinished) "Finished (7/7)" else "Complete Lap ${tawafState.currentLap}",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// ==========================================
// 3. SA'I 7-ROUND COUNTER
// ==========================================
@Composable
private fun SaiCounterContent(
    saiState: com.example.data.model.SaiTrackerState,
    onIncrementRound: () -> Unit,
    onReset: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Circular Progress Indicator
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(200.dp)
                    .padding(16.dp)
            ) {
                CircularProgressIndicator(
                    progress = { (saiState.completedRounds.toFloat() / 7f).coerceIn(0f, 1f) },
                    modifier = Modifier.fillMaxSize(),
                    color = EmeraldPrimary,
                    strokeWidth = 12.dp,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "ROUND",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 2.sp
                        )
                    )
                    Text(
                        text = if (saiState.isFinished) "7/7" else "${saiState.currentRound} / 7",
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = if (saiState.isFinished) GoldAccent else EmeraldPrimary
                        ),
                        modifier = Modifier.testTag("sai_round_text")
                    )
                    Text(
                        text = if (saiState.isFinished) "Concluded at Marwah ✓" else "Heading to ${if (saiState.currentRound % 2 != 0) "Marwah" else "Safa"}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = if (saiState.isFinished) GoldAccent else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            // Station Tracker Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = EmeraldPrimary.copy(alpha = 0.12f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (saiState.currentRound % 2 != 0) "Safa ➔ Marwah" else "Marwah ➔ Safa",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = EmeraldPrimary)
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // Guidance Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Sa'i Rules & Reminders:",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "• Safa to Marwah is 1 round; Marwah back to Safa is round 2.\n" +
                                "• 7th round finishes on Mount Marwah.\n" +
                                "• Men jog lightly between the green light markers; women walk normally.",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp, lineHeight = 16.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Action Buttons: Complete Round & Reset
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = onReset,
                modifier = Modifier.weight(1f).testTag("sai_reset_button"),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("Reset")
            }

            Button(
                onClick = onIncrementRound,
                enabled = !saiState.isFinished,
                modifier = Modifier.weight(2f).testTag("sai_round_button"),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
            ) {
                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text(
                    text = if (saiState.isFinished) "Finished at Marwah (7/7)" else "Arrived at ${if (saiState.currentRound % 2 != 0) "Marwah" else "Safa"}",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// ==========================================
// 4. TALBIYAH & DU'AS CONTENT
// ==========================================
@Composable
private fun TalbiyahAndDuasContent(
    isPlaying: Boolean,
    onTogglePlay: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        // Talbiyah Hero Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "The Sacred Talbiyah",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = EmeraldPrimary
                        )

                        IconButton(
                            onClick = onTogglePlay,
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(EmeraldPrimary.copy(alpha = 0.12f))
                                .size(36.dp)
                                .testTag("talbiyah_audio_toggle")
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = "Play Talbiyah",
                                tint = EmeraldPrimary
                            )
                        }
                    }

                    Spacer(Modifier.height(10.dp))

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = EmeraldPrimary.copy(alpha = 0.05f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "لَبَّيْكَ اللَّهُمَّ لَبَّيْكَ، لَبَّيْكَ لاَ شَرِيكَ لَكَ لَبَّيْكَ، إِنَّ الْحَمْدَ وَالنِّعْمَةَ لَكَ وَالْمُلْكَ، لاَ شَرِيكَ لَكَ",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Right,
                                lineHeight = 26.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(14.dp)
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = "Labbayka Allahumma labbayk, labbayka la sharika laka labbayk, innal-hamda wan-ni'mata laka wal-mulk, la sharika lak.",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = "\"Here I am at Your service, O Allah, here I am. Here I am, You have no partner, here I am. Truly all praise, grace, and sovereignty belong to You. You have no partner.\" (Sahih al-Bukhari #1549)",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Additional Du'a Cards
        item {
            DuaSnippetCard(
                title = "Du'a Between Yemeni Corner & Black Stone",
                arabic = "رَبَّنَا آتِنَا فِي الدُّنْيَا حَسَنَةً وَفِي الآخِرَةِ حَسَنَةً وَقِنَا عَذَابَ النَّارِ",
                translation = "Our Lord! Give us in this world that which is good and in the Hereafter that which is good, and save us from the punishment of the Fire. (Surah Al-Baqarah 2:201)",
                source = "Abu Dawud #1892"
            )
        }

        item {
            DuaSnippetCard(
                title = "Atop Mount Safa and Marwah",
                arabic = "لاَ إِلَهَ إِلاَّ اللَّهُ وَحْدَهُ لاَ شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ، وَهُوَ عَلَى كُلِّ شَىْءٍ قَدِيرٌ",
                translation = "There is no God but Allah alone, with no partner. His is the sovereignty and His is all praise, and He has power over all things. (Sahih Muslim #1218)",
                source = "Sahih Muslim #1218"
            )
        }

        item {
            DuaSnippetCard(
                title = "When Drinking Zamzam Water",
                arabic = "اللَّهُمَّ إِنِّي أَسْأَلُكَ عِلْمًا نَافِعًا، وَرِزْقًا وَاسِعًا، وَشِفَاءً مِنْ كُلِّ دَاءٍ",
                translation = "O Allah, I ask You for beneficial knowledge, abundant provision, and cure from every ailment.",
                source = "Mustadrak al-Hakim #1739"
            )
        }
    }
}

@Composable
private fun DuaSnippetCard(
    title: String,
    arabic: String,
    translation: String,
    source: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = EmeraldPrimary
            )
            Spacer(Modifier.height(8.dp))
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = EmeraldPrimary.copy(alpha = 0.05f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = arabic,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Right,
                        lineHeight = 24.sp
                    ),
                    modifier = Modifier.padding(10.dp)
                )
            }
            Spacer(Modifier.height(6.dp))
            Text(
                text = translation,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

// ==========================================
// 5. CHECKLIST CONTENT
// ==========================================
@Composable
private fun HajjUmrahChecklistContent(
    checklist: List<com.example.data.model.HajjUmrahChecklistItem>,
    onToggleItem: (String) -> Unit
) {
    val completedCount = checklist.count { it.isCompleted }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = EmeraldPrimary.copy(alpha = 0.08f))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Pilgrimage Packing & Prep",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = EmeraldPrimary
                        )
                        Text(
                            text = "$completedCount of ${checklist.size} items ready",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Text(
                        text = "${((completedCount.toFloat() / checklist.size.toFloat()) * 100).toInt()}%",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = EmeraldPrimary)
                    )
                }
            }
        }

        items(checklist, key = { it.id }) { item ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { onToggleItem(item.id) },
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = item.isCompleted,
                        onCheckedChange = { onToggleItem(item.id) },
                        colors = CheckboxDefaults.colors(checkedColor = EmeraldPrimary)
                    )

                    Spacer(Modifier.width(8.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Text(
                                    text = item.category,
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                textDecoration = if (item.isCompleted) androidx.compose.ui.text.style.TextDecoration.LineThrough else null
                            ),
                            color = if (item.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = item.description,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// 6. STEP DETAIL MODAL DIALOG
// ==========================================
@Composable
private fun PilgrimageStepDetailDialog(
    step: PilgrimageStep,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = step.iconEmoji, fontSize = 24.sp)
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Text(
                                text = step.title,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = step.location,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                color = EmeraldPrimary
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(Modifier.height(10.dp))

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Text(
                            text = "Detailed Instructions:",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    items(step.detailedInstructions) { instr ->
                        Row(verticalAlignment = Alignment.Top) {
                            Text(text = "• ", fontWeight = FontWeight.Bold, color = EmeraldPrimary)
                            Text(
                                text = instr,
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, lineHeight = 17.sp),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    if (step.authenticDuas.isNotEmpty()) {
                        item {
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = "Authentic Supplications:",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = EmeraldPrimary
                            )
                        }

                        items(step.authenticDuas) { dua ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = EmeraldPrimary.copy(alpha = 0.06f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = dua.title,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Text(
                                        text = dua.arabic,
                                        style = MaterialTheme.typography.bodyLarge.copy(
                                            fontFamily = FontFamily.Serif,
                                            textAlign = TextAlign.Right,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Text(
                                        text = dua.translation,
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp)
                                    )
                                }
                            }
                        }
                    }

                    if (step.prohibitionsAndMistakes.isNotEmpty()) {
                        item {
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = "Mistakes & Prohibitions to Avoid:",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                            )
                        }

                        items(step.prohibitionsAndMistakes) { mistake ->
                            Row(verticalAlignment = Alignment.Top) {
                                Text(text = "⚠️ ", fontSize = 12.sp)
                                Text(
                                    text = mistake,
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp, lineHeight = 16.sp),
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
