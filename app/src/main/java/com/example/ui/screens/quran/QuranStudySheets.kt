package com.example.ui.screens.quran

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.AyahEntity
import com.example.data.model.QuranAudioReciter
import com.example.data.model.QuranReadingMode
import com.example.data.model.TafsirItem
import com.example.data.model.TranslationEdition
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TafsirBottomSheet(
    tafsir: TafsirItem?,
    ayah: AyahEntity?,
    surahName: String,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.testTag("tafsir_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Tafsir Commentary",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Surah $surahName • Ayah ${ayah?.ayahNumber ?: 1}",
                        style = MaterialTheme.typography.bodySmall,
                        color = EmeraldPrimary
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close Tafsir")
                }
            }

            if (tafsir != null) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 420.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = GoldAccent.copy(alpha = 0.12f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Source: ${tafsir.author} • ${tafsir.title}",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = GoldAccent,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }

                    item {
                        Text(
                            text = tafsir.summary,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }

                    item {
                        Text(
                            text = tafsir.detailedText,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                lineHeight = 22.sp,
                                fontSize = 14.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.testTag("tafsir_content_text")
                        )
                    }

                    if (tafsir.keyLessons.isNotEmpty()) {
                        item {
                            Text(
                                text = "Key Reflections:",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = EmeraldPrimary
                            )
                        }
                        items(tafsir.keyLessons) { lesson ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text("•", color = GoldAccent, fontWeight = FontWeight.Bold)
                                Text(lesson, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }
                }
            } else {
                Text(
                    text = "No detailed tafsir commentary available for this ayah.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 20.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReciterSelectionSheet(
    selectedReciter: QuranAudioReciter,
    onSelectReciter: (QuranAudioReciter) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.testTag("reciter_selection_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Select Quran Reciter (Qari)",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(QuranAudioReciter.ALL_RECITERS) { reciter ->
                    val isSelected = reciter.id == selectedReciter.id
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) EmeraldPrimary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary) else null,
                        onClick = {
                            onSelectReciter(reciter)
                            onDismiss()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("reciter_item_${reciter.id}")
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            Icons.Default.RecordVoiceOver,
                                            contentDescription = null,
                                            tint = if (isSelected) Color.White else MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }

                                Column {
                                    Text(
                                        text = reciter.name,
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = reciter.style,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            if (isSelected) {
                                Icon(Icons.Default.Check, contentDescription = "Selected", tint = EmeraldPrimary)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranSettingsSheet(
    arabicFontSizeSp: Int,
    onFontSizeChange: (Int) -> Unit,
    currentEdition: TranslationEdition,
    onEditionChange: (TranslationEdition) -> Unit,
    currentReadingMode: QuranReadingMode,
    onReadingModeChange: (QuranReadingMode) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.testTag("quran_settings_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Quran Reading & Study Settings",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            // 1. Reading Mode Switcher (Ayah by Ayah vs Continuous Mushaf)
            Text(
                text = "Reading Mode",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuranReadingMode.values().forEach { mode ->
                    val isSelected = mode == currentReadingMode
                    FilterChip(
                        selected = isSelected,
                        onClick = { onReadingModeChange(mode) },
                        label = { Text(mode.displayName, fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EmeraldPrimary.copy(alpha = 0.15f),
                            selectedLabelColor = EmeraldPrimary
                        ),
                        modifier = Modifier.weight(1f).testTag("reading_mode_${mode.name.lowercase()}")
                    )
                }
            }

            // 2. Arabic Font Size Slider
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Arabic Font Size",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "$arabicFontSizeSp sp",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = EmeraldPrimary
                    )
                }

                Slider(
                    value = arabicFontSizeSp.toFloat(),
                    onValueChange = { onFontSizeChange(it.toInt()) },
                    valueRange = 18f..40f,
                    steps = 11,
                    colors = SliderDefaults.colors(
                        thumbColor = EmeraldPrimary,
                        activeTrackColor = EmeraldPrimary
                    ),
                    modifier = Modifier.testTag("font_size_slider")
                )
            }

            // 3. Translation Edition Selector
            Text(
                text = "English Translation Edition",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
            )
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                TranslationEdition.ALL_EDITIONS.forEach { edition ->
                    val isSelected = edition.id == currentEdition.id
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) EmeraldPrimary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                        onClick = { onEditionChange(edition) },
                        modifier = Modifier.fillMaxWidth().testTag("edition_item_${edition.id}")
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = edition.name,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = edition.author,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            if (isSelected) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = EmeraldPrimary)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
