package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.seed.DuaItem
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldLight

@Composable
fun LearnDuaDialog(
    dua: DuaItem,
    isPlayingAudio: Boolean,
    onPlayAudio: () -> Unit,
    onDismiss: () -> Unit
) {
    var repetitionCount by remember { mutableIntStateOf(0) }
    var targetRepetitions by remember { mutableIntStateOf(dua.recommendedCount.coerceAtLeast(3)) }
    var hideTranslationForMemoryTest by remember { mutableStateOf(false) }

    val isCompleted = repetitionCount >= targetRepetitions

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.85f)
                .clip(RoundedCornerShape(24.dp)),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = GoldAccent.copy(alpha = 0.2f),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.School,
                                    contentDescription = null,
                                    tint = GoldAccent,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        Column {
                            Text(
                                text = "Learn & Memorize Dua",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = dua.category,
                                style = MaterialTheme.typography.labelSmall,
                                color = EmeraldPrimary
                            )
                        }
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.testTag("learn_dua_dialog_close")) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Dua Title & Authentic Hadith Citation
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = dua.title,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = EmeraldPrimary.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = "Hadith: ${dua.reference}",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = EmeraldPrimary),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Arabic Script Card with Audio Button
                    item {
                        Card(
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, GoldAccent.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
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
                                    Text(
                                        text = "Recite & Listen",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    FilledTonalButton(
                                        onClick = onPlayAudio,
                                        colors = ButtonDefaults.filledTonalButtonColors(
                                            containerColor = if (isPlayingAudio) GoldAccent else EmeraldPrimary.copy(alpha = 0.15f),
                                            contentColor = if (isPlayingAudio) Color.Black else EmeraldPrimary
                                        ),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (isPlayingAudio) Icons.Default.Pause else Icons.Default.VolumeUp,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(if (isPlayingAudio) "Pause" else "Play Audio", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Text(
                                    text = dua.arabicText,
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Bold,
                                        lineHeight = 36.sp
                                    ),
                                    textAlign = TextAlign.Right,
                                    color = EmeraldPrimary,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Text(
                                    text = dua.transliteration,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Medium,
                                        color = GoldAccent
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )

                                AnimatedVisibility(visible = !hideTranslationForMemoryTest) {
                                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                                        Text(
                                            text = "\"${dua.translation}\"",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = dua.explanation,
                                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Memory Test Toggle
                    item {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = if (hideTranslationForMemoryTest) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = null,
                                        tint = EmeraldPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Column {
                                        Text("Test Recall Mode", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                                        Text("Hide translation to test memory", style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }

                                Switch(
                                    checked = hideTranslationForMemoryTest,
                                    onCheckedChange = { hideTranslationForMemoryTest = it }
                                )
                            }
                        }
                    }

                    // Repetition Counter
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isCompleted) GoldAccent.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                            ),
                            border = if (isCompleted) androidx.compose.foundation.BorderStroke(1.5.dp, GoldAccent) else null,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    text = if (isCompleted) "MashaAllah! Target Completed" else "Memorization Repetitions",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (isCompleted) GoldAccent else MaterialTheme.colorScheme.onSurface
                                )

                                Text(
                                    text = "$repetitionCount / $targetRepetitions",
                                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold, color = EmeraldPrimary)
                                )

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Button(
                                        onClick = { repetitionCount++ },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                                        modifier = Modifier.weight(1f).testTag("dua_tap_repetition_btn")
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = null)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Tap to Recite", fontWeight = FontWeight.Bold)
                                    }

                                    OutlinedButton(
                                        onClick = { repetitionCount = 0 },
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Icon(Icons.Default.Refresh, contentDescription = "Reset")
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
