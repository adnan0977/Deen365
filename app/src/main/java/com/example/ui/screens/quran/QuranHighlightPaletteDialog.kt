package com.example.ui.screens.quran

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FormatPaint
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
import com.example.data.local.AyahEntity
import com.example.data.local.QuranHighlightEntity
import com.example.data.model.QuranHighlightColor
import com.example.ui.theme.EmeraldPrimary

@Composable
fun QuranHighlightPaletteDialog(
    isOpen: Boolean,
    ayah: AyahEntity?,
    surahName: String,
    existingHighlight: QuranHighlightEntity?,
    onSave: (colorHex: String, colorName: String, categoryLabel: String, note: String) -> Unit,
    onRemove: () -> Unit,
    onDismiss: () -> Unit
) {
    if (!isOpen || ayah == null) return

    var selectedColor by remember(existingHighlight) {
        mutableStateOf(
            if (existingHighlight != null) {
                QuranHighlightColor.getByHex(existingHighlight.colorHex)
            } else {
                QuranHighlightColor.EMERALD
            }
        )
    }

    var noteText by remember(existingHighlight) {
        mutableStateOf(existingHighlight?.note ?: "")
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("quran_highlight_palette_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
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
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.FormatPaint,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Column {
                            Text(
                                text = "Highlight Verse",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Surah $surahName • Ayah ${ayah.ayahNumber}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                // Color Selection Circles
                Text(
                    text = "Select Color Theme",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    QuranHighlightColor.ALL_COLORS.forEach { colorItem ->
                        val isSelected = colorItem.hex.equals(selectedColor.hex, ignoreCase = true)
                        val colorParsed = try {
                            Color(android.graphics.Color.parseColor(colorItem.hex))
                        } catch (e: Exception) {
                            EmeraldPrimary
                        }

                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(colorParsed)
                                .then(
                                    if (isSelected) {
                                        Modifier.border(3.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
                                    } else Modifier
                                )
                                .clickable { selectedColor = colorItem }
                                .testTag("color_picker_${colorItem.name.lowercase().replace(" ", "_")}"),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }
                }

                // Selected Color Category Badge & Description
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = try {
                        Color(android.graphics.Color.parseColor(selectedColor.hex)).copy(alpha = 0.12f)
                    } catch (e: Exception) {
                        MaterialTheme.colorScheme.surfaceVariant
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(
                                        try {
                                            Color(android.graphics.Color.parseColor(selectedColor.hex))
                                        } catch (e: Exception) {
                                            EmeraldPrimary
                                        }
                                    )
                            )
                            Text(
                                text = "${selectedColor.name} • ${selectedColor.category}",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = selectedColor.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Optional Personal Reflection / Study Note
                OutlinedTextField(
                    value = noteText,
                    onValueChange = { noteText = it },
                    label = { Text("Personal Tadabbur / Note (Optional)") },
                    placeholder = { Text("Write personal insights or action items...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("highlight_note_input"),
                    minLines = 2,
                    maxLines = 4,
                    shape = RoundedCornerShape(12.dp)
                )

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (existingHighlight != null) {
                        OutlinedButton(
                            onClick = onRemove,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("remove_highlight_btn")
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Remove")
                        }
                    }

                    Button(
                        onClick = {
                            onSave(
                                selectedColor.hex,
                                selectedColor.name,
                                selectedColor.category,
                                noteText
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier
                            .weight(if (existingHighlight != null) 1.5f else 1f)
                            .testTag("save_highlight_btn")
                    ) {
                        Text(
                            text = if (existingHighlight != null) "Update Highlight" else "Apply Highlight",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
