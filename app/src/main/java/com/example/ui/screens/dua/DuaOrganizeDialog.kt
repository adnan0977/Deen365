package com.example.ui.screens.dua

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.DuaEntity
import com.example.data.seed.DuaSeedData
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent

@Composable
fun DuaOrganizeDialog(
    dua: DuaEntity,
    existingFolders: List<String>,
    onSave: (folder: String, note: String) -> Unit,
    onToggleFavorite: (Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedFolder by remember { mutableStateOf(dua.customFolder) }
    var userNote by remember { mutableStateOf(dua.userNote) }
    var isFavorite by remember { mutableStateOf(dua.isFavorite) }

    val suggestedFolders = remember(existingFolders) {
        val base = DuaSeedData.POPULAR_TAGS
        (base + existingFolders).distinct()
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("dua_organize_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
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
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(GoldAccent.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.FolderSpecial,
                                contentDescription = null,
                                tint = GoldAccent,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Organize Invocation",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Custom collections & notes",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("close_dua_organize_dialog")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                // Dua Title Brief
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = dua.title,
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 2
                            )
                            Text(
                                text = "Category: ${dua.category}",
                                style = MaterialTheme.typography.labelSmall,
                                color = EmeraldPrimary
                            )
                        }

                        // Bookmark Toggle Button
                        IconButton(
                            onClick = {
                                val next = !isFavorite
                                isFavorite = next
                                onToggleFavorite(dua.isFavorite)
                            },
                            modifier = Modifier
                                .size(40.dp)
                                .testTag("organize_dialog_bookmark_btn")
                        ) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                                contentDescription = if (isFavorite) "Remove Bookmark" else "Bookmark",
                                tint = if (isFavorite) GoldAccent else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }

                // Collection / Folder Field
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Collection / Folder Name",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    OutlinedTextField(
                        value = selectedFolder,
                        onValueChange = { selectedFolder = it },
                        placeholder = { Text("e.g. Daily Routine, Ramadan, Family") },
                        leadingIcon = {
                            Icon(Icons.Default.Folder, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(20.dp))
                        },
                        trailingIcon = {
                            if (selectedFolder.isNotEmpty()) {
                                IconButton(onClick = { selectedFolder = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear", modifier = Modifier.size(18.dp))
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("dua_folder_input")
                    )
                }

                // Suggested Folders Chips
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Quick Suggested Collections",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(suggestedFolders) { folder ->
                            val isSelected = selectedFolder.equals(folder, ignoreCase = true)
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    selectedFolder = if (isSelected) "" else folder
                                },
                                label = { Text(folder, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = EmeraldPrimary.copy(alpha = 0.2f),
                                    selectedLabelColor = EmeraldPrimary
                                ),
                                border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary) else null,
                                modifier = Modifier.testTag("suggested_folder_chip_${folder.replace(" ", "_")}")
                            )
                        }
                    }
                }

                // Personal Note Field
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Personal Note & Intention (Optional)",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    OutlinedTextField(
                        value = userNote,
                        onValueChange = { userNote = it },
                        placeholder = { Text("e.g. Recite 3x after Fajr, my mother's favorite invocation...") },
                        leadingIcon = {
                            Icon(Icons.Default.EditNote, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(20.dp))
                        },
                        maxLines = 3,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("dua_note_input")
                    )
                }

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("cancel_dua_organize_btn")
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            onSave(selectedFolder, userNote)
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                        modifier = Modifier
                            .weight(1.3f)
                            .testTag("save_dua_organize_btn")
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Save Collection", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
