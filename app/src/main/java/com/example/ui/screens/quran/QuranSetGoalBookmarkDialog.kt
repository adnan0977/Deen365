package com.example.ui.screens.quran

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.AyahEntity
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent

@Composable
fun QuranSetGoalBookmarkDialog(
    isOpen: Boolean,
    ayah: AyahEntity?,
    surahName: String,
    currentGoalMinutes: Int,
    onSaveGoalBookmark: (goalLabel: String, targetMinutes: Int, note: String) -> Unit,
    onDismiss: () -> Unit
) {
    if (!isOpen || ayah == null) return

    val goalPresets = listOf(
        "Daily 15-Min Milestone",
        "Morning Fajr Target",
        "Evening Tadabbur Goal",
        "Hizb Checkpoint",
        "Juz Khatmah Milestone"
    )

    var selectedGoalLabel by remember { mutableStateOf(goalPresets.first()) }
    var customGoalLabel by remember { mutableStateOf("") }
    var selectedMinutes by remember { mutableStateOf(currentGoalMinutes) }
    var noteText by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("set_goal_bookmark_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
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
                            color = GoldAccent.copy(alpha = 0.15f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Flag,
                                    contentDescription = null,
                                    tint = GoldAccent,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Column {
                            Text(
                                text = "Set Goal Checkpoint",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Surah $surahName • Ayah ${ayah.ayahNumber}",
                                style = MaterialTheme.typography.labelSmall,
                                color = EmeraldPrimary
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Text(
                    text = "Bookmark this ayah as a milestone for your daily Quran reading habits.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Goal Label Presets
                Text(
                    text = "Goal Target Type",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                )

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    goalPresets.take(3).forEach { preset ->
                        val isSelected = selectedGoalLabel == preset && customGoalLabel.isEmpty()
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) EmeraldPrimary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary) else null,
                            onClick = {
                                selectedGoalLabel = preset
                                customGoalLabel = ""
                            },
                            modifier = Modifier.fillMaxWidth().testTag("goal_preset_${preset.replace(" ", "_")}")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = preset,
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                    color = if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.onSurface
                                )
                                if (isSelected) {
                                    Icon(Icons.Default.Flag, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }

                // Custom Title
                OutlinedTextField(
                    value = customGoalLabel,
                    onValueChange = { customGoalLabel = it },
                    label = { Text("Or Custom Goal Name") },
                    placeholder = { Text("e.g., Afternoon Recitation Target") },
                    modifier = Modifier.fillMaxWidth().testTag("custom_goal_label_input"),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp)
                )

                // Target Minutes Selector
                Text(
                    text = "Daily Reading Target Duration",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(10, 15, 20, 30).forEach { mins ->
                        val isSelected = selectedMinutes == mins
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedMinutes = mins },
                            label = { Text("$mins min", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                            leadingIcon = {
                                Icon(Icons.Default.Timer, contentDescription = null, modifier = Modifier.size(14.dp))
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = EmeraldPrimary.copy(alpha = 0.15f),
                                selectedLabelColor = EmeraldPrimary
                            ),
                            modifier = Modifier.weight(1f).testTag("goal_duration_$mins")
                        )
                    }
                }

                // Optional Note
                OutlinedTextField(
                    value = noteText,
                    onValueChange = { noteText = it },
                    label = { Text("Notes / Intentions (Optional)") },
                    placeholder = { Text("Why I want to complete up to this ayah...") },
                    modifier = Modifier.fillMaxWidth().testTag("goal_note_input"),
                    minLines = 2,
                    maxLines = 3,
                    shape = RoundedCornerShape(10.dp)
                )

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            val finalLabel = customGoalLabel.ifBlank { selectedGoalLabel }
                            onSaveGoalBookmark(finalLabel, selectedMinutes, noteText)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.weight(1.5f).testTag("save_goal_checkpoint_btn")
                    ) {
                        Text("Save Checkpoint", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
