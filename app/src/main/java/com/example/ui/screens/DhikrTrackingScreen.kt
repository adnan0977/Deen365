package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.DhikrDailyLogEntity
import com.example.data.local.DhikrItemEntity
import com.example.domain.TasbihTheme
import com.example.ui.theme.EmeraldAccent
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.EmeraldPrimaryVariant
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldLight
import com.example.ui.viewmodel.DeenUiState
import com.example.ui.viewmodel.DeenViewModel
import com.example.ui.viewmodel.MainTab

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DhikrTrackingScreen(
    state: DeenUiState,
    viewModel: DeenViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.setTab(MainTab.DASHBOARD)
    }

    val activeDhikr = remember(state.dhikrItems, state.selectedDhikrId) {
        state.dhikrItems.find { it.id == state.selectedDhikrId }
            ?: state.dhikrItems.firstOrNull()
    }

    val activeLog: DhikrDailyLogEntity? = remember(state.todayDhikrLogs, activeDhikr) {
        activeDhikr?.let { state.todayDhikrLogs[it.id] }
    }

    val currentCount = activeLog?.count ?: 0
    val currentGoal = activeLog?.goal ?: (activeDhikr?.dailyGoal ?: 100)
    val isGoalCompleted = currentCount >= currentGoal && currentGoal > 0
    val progressFraction = if (currentGoal > 0) (currentCount.toFloat() / currentGoal).coerceIn(0f, 1f) else 0f

    val tasbihTheme = if (state.isDarkMode && state.selectedTasbihTheme == TasbihTheme.EMERALD_GOLD) {
        TasbihTheme.CELESTIAL_MIDNIGHT
    } else {
        state.selectedTasbihTheme
    }
    var showGoalDialog by remember { mutableStateOf(false) }
    var showAddCustomDialog by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }

    // Tap pulse animation
    var tapScale by remember { mutableFloatStateOf(1f) }
    val animatedScale by animateFloatAsState(
        targetValue = tapScale,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "tap_scale"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Screen Header with Settings Icon on Top Right
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = GoldAccent.copy(alpha = 0.2f),
                        modifier = Modifier.size(42.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Fingerprint,
                                contentDescription = null,
                                tint = GoldAccent,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    Column {
                        Text(
                            text = "Dhikr & Tasbih Tracker",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                        Text(
                            text = "Daily remembrance goals & live tap counter",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(
                    onClick = { viewModel.openSettings() },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .testTag("dhikr_settings_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Tasbih Theme Selector Row
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                TasbihTheme.entries.forEach { theme ->
                    val isSelected = theme == tasbihTheme
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) GoldAccent.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, GoldAccent) else null,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { viewModel.setTasbihTheme(theme) }
                            .testTag("tasbih_theme_btn_${theme.id}")
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = when (theme) {
                                    TasbihTheme.EMERALD_GOLD -> "Emerald"
                                    TasbihTheme.CELESTIAL_MIDNIGHT -> "Celestial"
                                    TasbihTheme.EARTH_SATELLITE -> "Earth"
                                    TasbihTheme.MINIMALIST_OBSIDIAN -> "Obsidian"
                                },
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 10.sp
                                ),
                                color = if (isSelected) GoldAccent else MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }

        // Daily Progress Summary Card
        item {
            val totalAchievedGoals = remember(state.dhikrItems, state.todayDhikrLogs) {
                state.dhikrItems.count { item ->
                    val log = state.todayDhikrLogs[item.id]
                    val count = log?.count ?: 0
                    val goal = log?.goal ?: item.dailyGoal
                    count >= goal && goal > 0
                }
            }

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.horizontalGradient(
                        listOf(EmeraldPrimary.copy(alpha = 0.4f), GoldAccent.copy(alpha = 0.4f))
                    )
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("dhikr_summary_card")
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
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.TrackChanges,
                                contentDescription = null,
                                tint = EmeraldPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Today's Remembrance",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (totalAchievedGoals > 0) EmeraldPrimary.copy(alpha = 0.15f) else GoldAccent.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "$totalAchievedGoals / ${state.dhikrItems.size} Goals Met",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (totalAchievedGoals > 0) EmeraldPrimary else GoldAccent,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "${state.todayTotalDhikrCount}",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = EmeraldPrimary
                                )
                            )
                            Text(
                                text = "Total Recitations Today",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "${(progressFraction * 100).toInt()}%",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (isGoalCompleted) GoldAccent else MaterialTheme.colorScheme.primary
                                )
                            )
                            Text(
                                text = "Active Dhikr Goal",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    LinearProgressIndicator(
                        progress = { progressFraction },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = if (isGoalCompleted) GoldAccent else EmeraldPrimary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }
            }
        }

        // Horizontal Dhikr Selector Carousel
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Choose Remembrance",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    TextButton(
                        onClick = { showAddCustomDialog = true },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.testTag("add_custom_dhikr_button")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Custom", fontSize = 12.sp)
                    }
                }

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(state.dhikrItems, key = { it.id }) { item ->
                        val isSelected = item.id == state.selectedDhikrId
                        val itemLog = state.todayDhikrLogs[item.id]
                        val itemCount = itemLog?.count ?: 0
                        val itemGoal = itemLog?.goal ?: item.dailyGoal
                        val itemDone = itemCount >= itemGoal && itemGoal > 0

                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                            border = if (isSelected) null else if (itemDone) androidx.compose.foundation.BorderStroke(1.dp, GoldAccent) else null,
                            modifier = Modifier
                                .clickable { viewModel.selectDhikr(item.id) }
                                .testTag("dhikr_chip_${item.id}")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                if (itemDone) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Completed",
                                        tint = if (isSelected) GoldAccent else EmeraldPrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                Column {
                                    Text(
                                        text = item.transliteration,
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                        ),
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "$itemCount / $itemGoal",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                        color = if (isSelected) Color.White.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Active Dhikr Phrase Card
        if (activeDhikr != null) {
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("active_dhikr_card")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Category and Goal Action Bar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = EmeraldPrimary.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = activeDhikr.category,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = EmeraldPrimary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }

                            // Daily Goal Pill with Edit Button
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = GoldAccent.copy(alpha = 0.18f),
                                modifier = Modifier
                                    .clickable { showGoalDialog = true }
                                    .testTag("edit_daily_goal_button")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit Goal",
                                        tint = EmeraldPrimaryVariant,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = "Goal: $currentGoal / day",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = EmeraldPrimaryVariant
                                    )
                                }
                            }
                        }

                        // Arabic Phrase
                        Text(
                            text = activeDhikr.arabicText,
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = EmeraldPrimary
                            ),
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        )

                        // Transliteration & Meaning
                        Text(
                            text = activeDhikr.transliteration,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "\"${activeDhikr.meaning}\"",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )

                        // Hadith / Virtue Excerpt
                        if (activeDhikr.virtue.isNotBlank()) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = GoldAccent,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = activeDhikr.virtue,
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Central Interactive Tap Counter
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Celebration Banner if goal is reached or Milestone badge!
                    val is33Milestone = currentCount > 0 && currentCount % 33 == 0 && !isGoalCompleted
                    if (isGoalCompleted) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = GoldAccent.copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent),
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateContentSize()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.EmojiEvents,
                                    contentDescription = null,
                                    tint = GoldAccent,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Daily Goal Completed! May Allah accept it.",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = EmeraldPrimaryVariant
                                )
                            }
                        }
                    } else if (is33Milestone) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = EmeraldPrimary.copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.35f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateContentSize()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DoneAll,
                                    contentDescription = null,
                                    tint = EmeraldPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "✨ 33-Bead Milestone Reached! ($currentCount recitations)",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = EmeraldPrimary
                                )
                            }
                        }
                    }

                    // Tactile Tap Button with Circular Progress Frame
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(230.dp)
                            .scale(animatedScale)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(tasbihTheme.surfaceGradient)
                            )
                            .border(
                                width = 3.dp,
                                color = tasbihTheme.ringColor,
                                shape = CircleShape
                            )
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = ripple(bounded = true, color = Color.White)
                            ) {
                                tapScale = 0.94f
                                viewModel.incrementDhikr(activeDhikr.id, 1)
                                tapScale = 1f
                            }
                            .testTag("dhikr_tap_counter_button")
                    ) {
                        // Circular Progress Arc & Beads
                        androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize().padding(10.dp)) {
                            drawArc(
                                color = tasbihTheme.beadColor,
                                startAngle = -90f,
                                sweepAngle = 360f,
                                useCenter = false,
                                style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
                            )
                            drawArc(
                                color = tasbihTheme.activeBeadColor,
                                startAngle = -90f,
                                sweepAngle = progressFraction * 360f,
                                useCenter = false,
                                style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
                            )
                        }

                        // Inner Counter Content
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "$currentCount",
                                style = MaterialTheme.typography.displayMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = tasbihTheme.counterDigitColor
                                )
                            )
                            Text(
                                text = "/ $currentGoal GOAL",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp,
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color.Black.copy(alpha = 0.18f)
                            ) {
                                Text(
                                    text = "TAP TO COUNT",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, color = Color.White),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    // Quick Step Controls and Feedback Toggles
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Undo (-1)
                        OutlinedButton(
                            onClick = { viewModel.decrementDhikr(activeDhikr.id) },
                            enabled = currentCount > 0,
                            shape = CircleShape,
                            contentPadding = PaddingValues(0.dp),
                            modifier = Modifier
                                .size(44.dp)
                                .testTag("dhikr_undo_button")
                        ) {
                            Icon(Icons.Default.Undo, contentDescription = "Undo -1", modifier = Modifier.size(18.dp))
                        }

                        // +10 Step Button
                        FilledTonalButton(
                            onClick = { viewModel.incrementDhikr(activeDhikr.id, 10) },
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.testTag("dhikr_step_10_button")
                        ) {
                            Text("+10", fontWeight = FontWeight.Bold)
                        }

                        // +33 Step Button (One full Tasbih set)
                        FilledTonalButton(
                            onClick = { viewModel.incrementDhikr(activeDhikr.id, 33) },
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.testTag("dhikr_step_33_button")
                        ) {
                            Text("+33", fontWeight = FontWeight.Bold)
                        }

                        // Reset
                        OutlinedButton(
                            onClick = { showResetDialog = true },
                            shape = CircleShape,
                            contentPadding = PaddingValues(0.dp),
                            modifier = Modifier
                                .size(44.dp)
                                .testTag("dhikr_reset_button")
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = "Reset Today", modifier = Modifier.size(18.dp))
                        }
                    }

                    // Sound and Haptic toggles
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FilterChip(
                            selected = state.dhikrVibrationEnabled,
                            onClick = { viewModel.toggleDhikrVibration() },
                            label = { Text(if (state.dhikrVibrationEnabled) "Haptic Feedback ON" else "Haptic Feedback OFF", fontSize = 11.sp) },
                            leadingIcon = {
                                Icon(
                                    if (state.dhikrVibrationEnabled) Icons.Default.Vibration else Icons.Default.Smartphone,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        )

                        FilterChip(
                            selected = state.dhikrSoundEnabled,
                            onClick = { viewModel.toggleDhikrSound() },
                            label = { Text("Bead Sound", fontSize = 11.sp) },
                            leadingIcon = {
                                Icon(
                                    if (state.dhikrSoundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        )
                    }
                }
            }
        }

        // Today's All Dhikrs Progress List
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Today's Dhikr Breakdown",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                state.dhikrItems.forEach { item ->
                    val log = state.todayDhikrLogs[item.id]
                    val count = log?.count ?: 0
                    val goal = log?.goal ?: item.dailyGoal
                    val isDone = count >= goal && goal > 0
                    val itemProgress = if (goal > 0) (count.toFloat() / goal).coerceIn(0f, 1f) else 0f

                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        border = if (item.id == state.selectedDhikrId) {
                            androidx.compose.foundation.BorderStroke(1.5.dp, EmeraldPrimary)
                        } else null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.selectDhikr(item.id) }
                            .testTag("dhikr_breakdown_${item.id}")
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
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
                                    if (isDone) {
                                        Icon(
                                            Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = GoldAccent,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Column {
                                        Text(
                                            text = item.transliteration,
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = item.meaning,
                                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "$count / $goal",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = if (isDone) EmeraldPrimary else MaterialTheme.colorScheme.onSurface
                                        )
                                    )
                                    Text(
                                        text = "${(itemProgress * 100).toInt()}%",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            LinearProgressIndicator(
                                progress = { itemProgress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = if (isDone) GoldAccent else EmeraldPrimary,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }

    // Dialog: Edit Daily Count Goal
    if (showGoalDialog && activeDhikr != null) {
        GoalSettingDialog(
            dhikr = activeDhikr,
            currentGoal = currentGoal,
            onDismiss = { showGoalDialog = false },
            onSave = { newGoal ->
                viewModel.updateDhikrDailyGoal(activeDhikr.id, newGoal)
                showGoalDialog = false
            }
        )
    }

    // Dialog: Add Custom Dhikr
    if (showAddCustomDialog) {
        AddCustomDhikrDialog(
            onDismiss = { showAddCustomDialog = false },
            onAdd = { arabic, transliteration, meaning, goal ->
                viewModel.addCustomDhikr(arabic, transliteration, meaning, goal)
                showAddCustomDialog = false
            }
        )
    }

    // Dialog: Confirm Reset Today's Count
    if (showResetDialog && activeDhikr != null) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            icon = { Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
            title = { Text("Reset Today's Count?") },
            text = { Text("This will reset today's recitations for '${activeDhikr.transliteration}' back to 0.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.resetDhikrToday(activeDhikr.id)
                        showResetDialog = false
                    }
                ) {
                    Text("Reset", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun GoalSettingDialog(
    dhikr: DhikrItemEntity,
    currentGoal: Int,
    onDismiss: () -> Unit,
    onSave: (Int) -> Unit
) {
    var goalText by remember { mutableStateOf(currentGoal.toString()) }
    val presetGoals = listOf(33, 100, 300, 500, 1000)

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Set Daily Goal",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Text(
                    text = "Set your target daily recitation count for ${dhikr.transliteration}:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Quick Preset Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    presetGoals.forEach { preset ->
                        FilterChip(
                            selected = goalText == preset.toString(),
                            onClick = { goalText = preset.toString() },
                            label = { Text("$preset", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                OutlinedTextField(
                    value = goalText,
                    onValueChange = { input ->
                        if (input.all { it.isDigit() } && input.length <= 5) {
                            goalText = input
                        }
                    },
                    label = { Text("Daily Target Recitations") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val parsed = goalText.toIntOrNull() ?: currentGoal
                            onSave(parsed.coerceAtLeast(1))
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                    ) {
                        Text("Save Goal")
                    }
                }
            }
        }
    }
}

@Composable
private fun AddCustomDhikrDialog(
    onDismiss: () -> Unit,
    onAdd: (arabic: String, transliteration: String, meaning: String, goal: Int) -> Unit
) {
    var arabicText by remember { mutableStateOf("") }
    var transliteration by remember { mutableStateOf("") }
    var meaning by remember { mutableStateOf("") }
    var dailyGoalText by remember { mutableStateOf("100") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Add Custom Dhikr",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                OutlinedTextField(
                    value = arabicText,
                    onValueChange = { arabicText = it },
                    label = { Text("Arabic Text (optional)") },
                    placeholder = { Text("e.g. سُبْحَانَ اللَّهِ") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = transliteration,
                    onValueChange = { transliteration = it },
                    label = { Text("Transliteration / Name *") },
                    placeholder = { Text("e.g. Ya Hayyu Ya Qayyum") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = meaning,
                    onValueChange = { meaning = it },
                    label = { Text("English Meaning (optional)") },
                    placeholder = { Text("e.g. O Ever-Living, O Sustainer") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = dailyGoalText,
                    onValueChange = { if (it.all { char -> char.isDigit() }) dailyGoalText = it },
                    label = { Text("Daily Target Goal *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val goal = dailyGoalText.toIntOrNull() ?: 100
                            onAdd(arabicText, transliteration, meaning, goal.coerceAtLeast(1))
                        },
                        enabled = transliteration.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                    ) {
                        Text("Add Dhikr")
                    }
                }
            }
        }
    }
}
