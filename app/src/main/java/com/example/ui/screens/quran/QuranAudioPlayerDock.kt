package com.example.ui.screens.quran

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.example.data.model.QuranAudioReciter
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent

@Composable
fun QuranAudioPlayerDock(
    currentSurah: Int?,
    currentAyah: Int?,
    isPlaying: Boolean,
    playbackSpeed: Float,
    selectedReciter: QuranAudioReciter,
    totalAyahs: Int,
    onTogglePlayPause: () -> Unit,
    onNextAyah: () -> Unit,
    onPrevAyah: () -> Unit,
    onStopAudio: () -> Unit,
    onSpeedCycle: () -> Unit,
    onOpenReciterPicker: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activeAyah = currentAyah ?: 1

    AnimatedVisibility(
        visible = currentSurah != null && (isPlaying || currentAyah != null),
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it }),
        modifier = modifier
    ) {
        Surface(
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
            color = MaterialTheme.colorScheme.surfaceColorAtElevation(6.dp),
            shadowElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("quran_audio_dock")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Top row: Reciter badge, Verse indicator & speed cycle button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .clickable { onOpenReciterPicker() }
                            .testTag("quran_audio_reciter_pill")
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = EmeraldPrimary.copy(alpha = 0.15f),
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.RecordVoiceOver,
                                    contentDescription = null,
                                    tint = EmeraldPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Column {
                            Text(
                                text = selectedReciter.name,
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = selectedReciter.style,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = EmeraldPrimary.copy(alpha = 0.12f),
                            modifier = Modifier.testTag("audio_dock_verse_counter")
                        ) {
                            Text(
                                text = "Ayah $activeAyah / $totalAyahs",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = EmeraldPrimary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        // Speed Pill
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = GoldAccent.copy(alpha = 0.2f),
                            modifier = Modifier
                                .clickable { onSpeedCycle() }
                                .testTag("audio_dock_speed_button")
                        ) {
                            Text(
                                text = "${playbackSpeed}x",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = GoldAccent,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        IconButton(
                            onClick = onStopAudio,
                            modifier = Modifier.size(28.dp).testTag("audio_dock_close_button")
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Close player", modifier = Modifier.size(16.dp))
                        }
                    }
                }

                // Bottom row: Previous, Play/Pause, Next
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onPrevAyah,
                        enabled = activeAyah > 1,
                        modifier = Modifier.testTag("audio_dock_prev_ayah")
                    ) {
                        Icon(Icons.Default.SkipPrevious, contentDescription = "Previous Ayah")
                    }

                    FilledIconButton(
                        onClick = onTogglePlayPause,
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = EmeraldPrimary,
                            contentColor = Color.White
                        ),
                        modifier = Modifier.size(46.dp).testTag("audio_dock_play_pause_button")
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play"
                        )
                    }

                    IconButton(
                        onClick = onNextAyah,
                        enabled = activeAyah < totalAyahs,
                        modifier = Modifier.testTag("audio_dock_next_ayah")
                    ) {
                        Icon(Icons.Default.SkipNext, contentDescription = "Next Ayah")
                    }
                }
            }
        }
    }
}
