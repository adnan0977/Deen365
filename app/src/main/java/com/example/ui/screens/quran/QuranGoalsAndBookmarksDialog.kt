package com.example.ui.screens.quran

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
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.AyahEntity
import com.example.data.local.QuranGoalBookmarkEntity
import com.example.data.local.QuranHighlightEntity
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent

@Composable
fun QuranGoalsAndBookmarksDialog(
    isOpen: Boolean,
    dailyGoalMinutes: Int,
    todayReadingSeconds: Int,
    quranStreakDays: Int,
    goalBookmarks: List<QuranGoalBookmarkEntity>,
    highlights: List<QuranHighlightEntity>,
    bookmarkedAyahs: List<AyahEntity>,
    onChangeDailyGoalMinutes: (Int) -> Unit,
    onToggleGoalCompleted: (id: String, isCompleted: Boolean) -> Unit,
    onDeleteGoalBookmark: (id: String) -> Unit,
    onDeleteHighlight: (surahNumber: Int, ayahNumber: Int) -> Unit,
    onJumpToVerse: (surahNumber: Int, ayahNumber: Int) -> Unit,
    onDismiss: () -> Unit
) {
    if (!isOpen) return

    var selectedTab by remember { mutableStateOf(0) } // 0: Daily Goals, 1: Bookmarks, 2: Highlights
    var highlightFilterCategory by remember { mutableStateOf("All") }

    val readingMinutes = todayReadingSeconds / 60
    val progressFraction = (readingMinutes.toFloat() / dailyGoalMinutes.coerceAtLeast(1).toFloat()).coerceIn(0f, 1f)

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 680.dp)
                .padding(8.dp)
                .testTag("quran_goals_and_bookmarks_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header
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
                            shape = CircleShape,
                            color = EmeraldPrimary.copy(alpha = 0.12f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.AutoStories,
                                    contentDescription = null,
                                    tint = EmeraldPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Column {
                            Text(
                                text = "Study & Goal Hub",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Daily reading tracker, checkpoints & reflections",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                // 3 Tabs
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                    modifier = Modifier.clip(RoundedCornerShape(12.dp)),
                    indicator = {},
                    divider = {}
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Text(
                                "Daily Goals (${goalBookmarks.size})",
                                fontSize = 12.sp,
                                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == 0) EmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        modifier = Modifier.testTag("tab_goals_checkpoints")
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Text(
                                "Bookmarks (${bookmarkedAyahs.size})",
                                fontSize = 12.sp,
                                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == 1) EmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        modifier = Modifier.testTag("tab_bookmarks")
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = {
                            Text(
                                "Highlights (${highlights.size})",
                                fontSize = 12.sp,
                                fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == 2) EmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        modifier = Modifier.testTag("tab_highlights")
                    )
                }

                // Tab Contents
                when (selectedTab) {
                    0 -> {
                        // TAB 0: DAILY READING GOALS & CHECKPOINTS
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f, fill = false),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Goal Progress Banner Card
                            item {
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = EmeraldPrimary.copy(alpha = 0.08f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.3f)),
                                    modifier = Modifier.fillMaxWidth().testTag("daily_goal_progress_card")
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
                                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                Icon(Icons.Default.Whatshot, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(20.dp))
                                                Text(
                                                    text = "$quranStreakDays Days Streak",
                                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                                    color = GoldAccent
                                                )
                                            }

                                            Text(
                                                text = "$readingMinutes / $dailyGoalMinutes mins",
                                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                                color = EmeraldPrimary
                                            )
                                        }

                                        LinearProgressIndicator(
                                            progress = { progressFraction },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(8.dp)
                                                .clip(RoundedCornerShape(4.dp)),
                                            color = EmeraldPrimary,
                                            trackColor = EmeraldPrimary.copy(alpha = 0.2f)
                                        )

                                        // Goal adjustment buttons
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "Target:",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                listOf(10, 15, 20, 30).forEach { mins ->
                                                    val isSelected = dailyGoalMinutes == mins
                                                    Surface(
                                                        shape = RoundedCornerShape(6.dp),
                                                        color = if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.surface,
                                                        border = androidx.compose.foundation.BorderStroke(
                                                            0.5.dp,
                                                            if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.outlineVariant
                                                        ),
                                                        onClick = { onChangeDailyGoalMinutes(mins) },
                                                        modifier = Modifier.testTag("set_goal_minutes_$mins")
                                                    ) {
                                                        Text(
                                                            text = "${mins}m",
                                                            style = MaterialTheme.typography.labelSmall.copy(
                                                                fontWeight = FontWeight.Bold,
                                                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                                            ),
                                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            item {
                                Text(
                                    text = "Active Goal Checkpoints",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            if (goalBookmarks.isEmpty()) {
                                item {
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(20.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.Flag,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.size(32.dp)
                                            )
                                            Text(
                                                text = "No Goal Checkpoints Yet",
                                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                            )
                                            Text(
                                                text = "While reading any Surah, tap the bookmark icon on any ayah and choose 'Set Goal Checkpoint' to establish your reading targets.",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                            )
                                        }
                                    }
                                }
                            } else {
                                items(goalBookmarks, key = { it.id }) { goal ->
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = if (goal.isCompleted) EmeraldPrimary.copy(alpha = 0.06f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                                        border = if (goal.isCompleted) androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.4f)) else null,
                                        modifier = Modifier.fillMaxWidth().testTag("goal_checkpoint_card_${goal.id}")
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(12.dp),
                                            verticalArrangement = Arrangement.spacedBy(6.dp)
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
                                                    Checkbox(
                                                        checked = goal.isCompleted,
                                                        onCheckedChange = { onToggleGoalCompleted(goal.id, goal.isCompleted) },
                                                        colors = CheckboxDefaults.colors(checkedColor = EmeraldPrimary),
                                                        modifier = Modifier.testTag("goal_checkbox_${goal.id}")
                                                    )
                                                    Column {
                                                        Text(
                                                            text = goal.goalLabel,
                                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                                fontWeight = FontWeight.Bold,
                                                                textDecoration = if (goal.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                                                            ),
                                                            color = if (goal.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                                                        )
                                                        Text(
                                                            text = "Surah ${goal.surahName} • Ayah ${goal.ayahNumber} (${goal.targetMinutes}m target)",
                                                            style = MaterialTheme.typography.labelSmall,
                                                            color = EmeraldPrimary
                                                        )
                                                    }
                                                }

                                                IconButton(
                                                    onClick = { onDeleteGoalBookmark(goal.id) },
                                                    modifier = Modifier.size(32.dp).testTag("delete_goal_${goal.id}")
                                                ) {
                                                    Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                                                }
                                            }

                                            if (goal.note.isNotBlank()) {
                                                Text(
                                                    text = "Note: ${goal.note}",
                                                    style = MaterialTheme.typography.bodySmall.copy(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic),
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    modifier = Modifier.padding(start = 40.dp)
                                                )
                                            }

                                            Row(
                                                modifier = Modifier.fillMaxWidth().padding(start = 40.dp),
                                                horizontalArrangement = Arrangement.End
                                            ) {
                                                TextButton(
                                                    onClick = { onJumpToVerse(goal.surahNumber, goal.ayahNumber) },
                                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                                    modifier = Modifier.testTag("jump_to_goal_${goal.id}")
                                                ) {
                                                    Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(14.dp))
                                                    Spacer(Modifier.width(4.dp))
                                                    Text("Jump to Ayah", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    1 -> {
                        // TAB 1: STANDARD SAVED BOOKMARKS
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f, fill = false),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (bookmarkedAyahs.isEmpty()) {
                                item {
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(24.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Icon(
                                                Icons.Outlined.BookmarkBorder,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.size(36.dp)
                                            )
                                            Text(
                                                text = "No Bookmarks Saved",
                                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                            )
                                            Text(
                                                text = "Tap the bookmark icon on any ayah card to save it for quick reference.",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                            )
                                        }
                                    }
                                }
                            } else {
                                items(bookmarkedAyahs) { ayah ->
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                                        modifier = Modifier.fillMaxWidth().testTag("saved_bookmark_item_${ayah.surahNumber}_${ayah.ayahNumber}")
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(12.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = "Surah ${ayah.surahNumber} • Ayah ${ayah.ayahNumber}",
                                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                                    color = EmeraldPrimary
                                                )
                                                Text(
                                                    text = ayah.translation,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    maxLines = 2,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }

                                            TextButton(
                                                onClick = { onJumpToVerse(ayah.surahNumber, ayah.ayahNumber) },
                                                modifier = Modifier.testTag("jump_to_bookmark_${ayah.surahNumber}_${ayah.ayahNumber}")
                                            ) {
                                                Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                                                Spacer(Modifier.width(4.dp))
                                                Text("Read")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    2 -> {
                        // TAB 2: HIGHLIGHTS & NOTES
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f, fill = false),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Category Filter Chips
                            item {
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    val categories = listOf("All", "Reflection & Tadabbur", "Wisdom & Rulings", "Dua & Supplication", "Warning & Reminder", "Prophetic History")
                                    items(categories) { cat ->
                                        val isSelected = highlightFilterCategory == cat
                                        FilterChip(
                                            selected = isSelected,
                                            onClick = { highlightFilterCategory = cat },
                                            label = { Text(cat, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = EmeraldPrimary.copy(alpha = 0.15f),
                                                selectedLabelColor = EmeraldPrimary
                                            ),
                                            modifier = Modifier.testTag("highlight_category_filter_${cat.lowercase().replace(" ", "_")}")
                                        )
                                    }
                                }
                            }

                            val filtered = if (highlightFilterCategory == "All") {
                                highlights
                            } else {
                                highlights.filter { it.categoryLabel.equals(highlightFilterCategory, ignoreCase = true) }
                            }

                            if (filtered.isEmpty()) {
                                item {
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(24.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.FormatPaint,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.size(36.dp)
                                            )
                                            Text(
                                                text = "No Highlights Found",
                                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                            )
                                            Text(
                                                text = "Highlight verses by tapping the paint palette icon on any ayah in the reader to organize your reflections by category.",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                            )
                                        }
                                    }
                                }
                            } else {
                                items(filtered, key = { it.id }) { hl ->
                                    val colorParsed = try {
                                        Color(android.graphics.Color.parseColor(hl.colorHex))
                                    } catch (e: Exception) {
                                        EmeraldPrimary
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = colorParsed.copy(alpha = 0.08f),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, colorParsed.copy(alpha = 0.4f)),
                                        modifier = Modifier.fillMaxWidth().testTag("highlight_card_${hl.id}")
                                    ) {
                                        Row(modifier = Modifier.fillMaxWidth()) {
                                            // Left color ribbon
                                            Box(
                                                modifier = Modifier
                                                    .width(6.dp)
                                                    .fillMaxHeight()
                                                    .background(colorParsed)
                                            )

                                            Column(
                                                modifier = Modifier
                                                    .padding(12.dp)
                                                    .weight(1f),
                                                verticalArrangement = Arrangement.spacedBy(6.dp)
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
                                                        Surface(
                                                            shape = RoundedCornerShape(4.dp),
                                                            color = colorParsed.copy(alpha = 0.2f)
                                                        ) {
                                                            Text(
                                                                text = hl.categoryLabel,
                                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp),
                                                                color = colorParsed,
                                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                            )
                                                        }
                                                        Text(
                                                            text = "Surah ${hl.surahName} • ${hl.ayahNumber}",
                                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                                            color = MaterialTheme.colorScheme.onSurface
                                                        )
                                                    }

                                                    IconButton(
                                                        onClick = { onDeleteHighlight(hl.surahNumber, hl.ayahNumber) },
                                                        modifier = Modifier.size(28.dp).testTag("delete_highlight_${hl.id}")
                                                    ) {
                                                        Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                                                    }
                                                }

                                                if (hl.note.isNotBlank()) {
                                                    Surface(
                                                        shape = RoundedCornerShape(8.dp),
                                                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                                                        modifier = Modifier.fillMaxWidth()
                                                    ) {
                                                        Text(
                                                            text = "“${hl.note}”",
                                                            style = MaterialTheme.typography.bodySmall.copy(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic),
                                                            color = MaterialTheme.colorScheme.onSurface,
                                                            modifier = Modifier.padding(8.dp)
                                                        )
                                                    }
                                                }

                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.End
                                                ) {
                                                    TextButton(
                                                        onClick = { onJumpToVerse(hl.surahNumber, hl.ayahNumber) },
                                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                                        modifier = Modifier.testTag("jump_to_highlight_${hl.id}")
                                                    ) {
                                                        Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(14.dp))
                                                        Spacer(Modifier.width(4.dp))
                                                        Text("Jump to Ayah", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Done Button
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.fillMaxWidth().testTag("close_goals_dialog_btn")
                ) {
                    Text("Close", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
