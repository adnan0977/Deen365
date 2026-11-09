package com.example.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.DhikrItemEntity
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.EmeraldPrimaryVariant
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldLight
import com.example.ui.viewmodel.DeenUiState
import com.example.ui.viewmodel.DeenViewModel
import com.example.ui.viewmodel.MainTab

/**
 * Dedicated 'Daily Dhikr' module card on the dashboard providing:
 * - A set of recommended authentic daily tasbih invocations (SubhanAllah, Alhamdulillah, Allahu Akbar, Astaghfirullah, Salawat, etc.)
 * - Recommended counts (33, 100, 1000)
 * - An interactive live counter with haptic pulse & sound
 * - Circular progress indicator tracking user daily progress
 * - Quick quick-add presets (+1, +10, +33) and reset
 * - Shortcut link to the full Tasbih counter screen
 */
@Composable
fun DailyDhikrDashboardCard(
    state: DeenUiState,
    viewModel: DeenViewModel,
    modifier: Modifier = Modifier
) {
    val items = state.dhikrItems
    val activeId = state.selectedDhikrId.ifBlank { items.firstOrNull()?.id ?: "subhanallah" }
    val currentDhikr = items.find { it.id == activeId } ?: items.firstOrNull() ?: return

    val currentLog = state.todayDhikrLogs[currentDhikr.id]
    val count = currentLog?.count ?: 0
    val goal = currentLog?.goal ?: currentDhikr.dailyGoal
    val progressFraction = if (goal > 0) (count.toFloat() / goal).coerceIn(0f, 1f) else 0f
    val isCompleted = count >= goal && goal > 0

    var tapScale by remember { mutableFloatStateOf(1f) }
    val animatedScale by animateFloatAsState(
        targetValue = tapScale,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "dhikr_counter_scale"
    )

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
        border = BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.28f)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("daily_dhikr_dashboard_card")
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header: Title, Category Badge & Link to Full Tasbih
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = EmeraldPrimary.copy(alpha = 0.12f),
                        border = BorderStroke(0.6.dp, EmeraldPrimary.copy(alpha = 0.25f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text("📿", fontSize = 13.sp)
                            Text(
                                text = "DAILY DHIKR & TASBIH",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 0.8.sp,
                                    color = EmeraldPrimary
                                )
                            )
                        }
                    }

                    if (isCompleted) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = GoldAccent.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "Goal Reached! ✨",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = GoldAccent,
                                    fontSize = 10.sp
                                ),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    } else if (count > 0 && count % 33 == 0) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = EmeraldPrimary.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "33 Milestone! ✨",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldPrimary,
                                    fontSize = 10.sp
                                ),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }
                }

                TextButton(
                    onClick = { viewModel.setTab(MainTab.TASBIH) },
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                    modifier = Modifier.testTag("open_tasbih_screen_button")
                ) {
                    Text(
                        text = "Full Tasbih",
                        color = EmeraldPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = EmeraldPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Quick Invocations Carousel
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(items) { item ->
                    val isSelected = item.id == currentDhikr.id
                    val itemLog = state.todayDhikrLogs[item.id]
                    val itemCount = itemLog?.count ?: 0
                    val itemGoal = itemLog?.goal ?: item.dailyGoal
                    val itemDone = itemCount >= itemGoal && itemGoal > 0

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        border = if (isSelected) null else BorderStroke(0.6.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { viewModel.selectDhikr(item.id) }
                            .testTag("dhikr_chip_${item.id}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = item.transliteration,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            )
                            Surface(
                                shape = CircleShape,
                                color = if (isSelected) Color.White.copy(alpha = 0.25f) else if (itemDone) GoldAccent.copy(alpha = 0.2f) else EmeraldPrimary.copy(alpha = 0.12f)
                            ) {
                                Text(
                                    text = "$itemCount/$itemGoal",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else if (itemDone) GoldAccent else EmeraldPrimary,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Main Interactive Dhikr Display & Counter Area
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = EmeraldPrimary.copy(alpha = 0.05f),
                border = BorderStroke(0.8.dp, EmeraldPrimary.copy(alpha = 0.18f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Arabic Text
                    Text(
                        text = currentDhikr.arabicText,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        ),
                        color = EmeraldPrimary,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Transliteration & Meaning
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = currentDhikr.transliteration,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = GoldAccent,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "\"${currentDhikr.meaning}\"",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    // Counter Component Row: Big Tap Button + Progress Circle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Circular Progress with Count
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(90.dp)
                        ) {
                            CircularProgressIndicator(
                                progress = { progressFraction },
                                modifier = Modifier.fillMaxSize(),
                                color = if (isCompleted) GoldAccent else EmeraldPrimary,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                                strokeWidth = 7.dp,
                                strokeCap = StrokeCap.Round
                            )
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "$count",
                                    style = MaterialTheme.typography.headlineSmall.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 22.sp
                                    ),
                                    color = if (isCompleted) GoldAccent else EmeraldPrimary
                                )
                                Text(
                                    text = "of $goal",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Big Interactive Tap Button
                        Surface(
                            shape = CircleShape,
                            color = if (isCompleted) GoldAccent else EmeraldPrimary,
                            shadowElevation = 4.dp,
                            modifier = Modifier
                                .size(84.dp)
                                .scale(animatedScale)
                                .clip(CircleShape)
                                .clickable {
                                    tapScale = 0.9f
                                    viewModel.incrementDhikr(currentDhikr.id, 1)
                                    // Reset scale shortly
                                    tapScale = 1f
                                }
                                .testTag("dhikr_tap_counter_button")
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Default.TouchApp,
                                        contentDescription = "Count Tasbih",
                                        tint = if (isCompleted) Color.Black else Color.White,
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Text(
                                        text = "+1",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 14.sp,
                                        color = if (isCompleted) Color.Black else Color.White
                                    )
                                }
                            }
                        }
                    }

                    // Recommended Count Shortcuts (+10, +33, Reset)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.incrementDhikr(currentDhikr.id, 10) },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("dhikr_add_10_button")
                        ) {
                            Text("+10", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        OutlinedButton(
                            onClick = { viewModel.incrementDhikr(currentDhikr.id, 33) },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("dhikr_add_33_button")
                        ) {
                            Text("+33", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        IconButton(
                            onClick = { viewModel.resetDhikrToday(currentDhikr.id) },
                            modifier = Modifier
                                .size(32.dp)
                                .testTag("dhikr_reset_today_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Reset Count",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            // Prophetic Virtue Note
            if (currentDhikr.virtue.isNotBlank()) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = GoldAccent.copy(alpha = 0.1f),
                    border = BorderStroke(0.5.dp, GoldAccent.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("✨", fontSize = 13.sp)
                        Text(
                            text = currentDhikr.virtue,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.sp,
                                lineHeight = 16.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}
