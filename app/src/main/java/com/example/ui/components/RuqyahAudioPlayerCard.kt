package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.RuqyahAudioPlayer
import com.example.data.model.RuqyahAudioCatalog
import com.example.data.model.RuqyahAudioTrack
import com.example.ui.theme.EmeraldAccent
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.EmeraldPrimaryVariant
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldLight

/**
 * Modern Material 3 Ruqyah Shariah Audio Player Component.
 * Supports remote HTTP/HTTPS streaming, basic playback controls (play, pause, seek),
 * skip +/-10s, speed adjustment, looping, playlist switching, and custom stream URL input.
 */
@Composable
fun RuqyahAudioPlayerCard(
    player: RuqyahAudioPlayer,
    modifier: Modifier = Modifier
) {
    val isPlaying by player.isPlaying.collectAsState()
    val isBuffering by player.isBuffering.collectAsState()
    val currentPosition by player.currentPositionMs.collectAsState()
    val duration by player.durationMs.collectAsState()
    val currentTrack by player.currentTrack.collectAsState()
    val speed by player.playbackSpeed.collectAsState()
    val isLooping by player.isLooping.collectAsState()
    val errorMessage by player.errorMessage.collectAsState()

    var showPlaylistSheet by remember { mutableStateOf(false) }
    var showCustomUrlDialog by remember { mutableStateOf(false) }

    // Seeking state
    var isDraggingSlider by remember { mutableStateOf(false) }
    var dragProgress by remember { mutableFloatStateOf(0f) }

    val effectiveProgress = if (isDraggingSlider) {
        dragProgress
    } else {
        if (duration > 0) (currentPosition.toFloat() / duration.toFloat()).coerceIn(0f, 1f) else 0f
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isPlaying) GoldAccent.copy(alpha = 0.8f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        ),
        modifier = modifier
            .fillMaxWidth()
            .testTag("ruqyah_player_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header: Section label, Streaming indicator, and actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(listOf(EmeraldPrimary, EmeraldPrimaryVariant))
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.GraphicEq else Icons.Default.Headphones,
                            contentDescription = null,
                            tint = GoldLight,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "Ruqyah Shariah Stream",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = EmeraldPrimary.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "LIVE AUDIO",
                                    color = EmeraldPrimary,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Prophetic recitation & spiritual cure",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    // Custom URL stream button
                    IconButton(
                        onClick = { showCustomUrlDialog = true },
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("ruqyah_custom_url_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Link,
                            contentDescription = "Stream Custom URL",
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Playlist button
                    IconButton(
                        onClick = { showPlaylistSheet = !showPlaylistSheet },
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("ruqyah_playlist_toggle_button")
                    ) {
                        Icon(
                            imageVector = if (showPlaylistSheet) Icons.Default.QueueMusic else Icons.Default.PlaylistPlay,
                            contentDescription = "Recitations Playlist",
                            tint = if (showPlaylistSheet) GoldAccent else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            // Current Track Banner
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(EmeraldPrimary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.RecordVoiceOver,
                            contentDescription = null,
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = currentTrack?.title ?: "Select Ruqyah Recitation",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = currentTrack?.reciter ?: "Authentic Sunnah Reciters",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                            color = EmeraldPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    if (isBuffering) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            CircularProgressIndicator(
                                strokeWidth = 2.dp,
                                color = GoldAccent,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Buffering",
                                fontSize = 10.sp,
                                color = GoldAccent,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Error Message (if any)
            if (errorMessage != null) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.7f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.WarningAmber,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = errorMessage ?: "",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.weight(1f)
                        )
                        TextButton(
                            onClick = { player.resume() },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text("Retry", fontSize = 11.sp)
                        }
                    }
                }
            }

            // Progress Slider & Seek Controls
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Slider(
                    value = effectiveProgress,
                    onValueChange = { newVal ->
                        isDraggingSlider = true
                        dragProgress = newVal
                    },
                    onValueChangeFinished = {
                        val targetMs = (dragProgress * duration).toLong()
                        player.seekTo(targetMs)
                        isDraggingSlider = false
                    },
                    colors = SliderDefaults.colors(
                        thumbColor = GoldAccent,
                        activeTrackColor = EmeraldPrimary,
                        inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(26.dp)
                        .testTag("ruqyah_seek_slider")
                )

                // Time indicators
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val displayPos = if (isDraggingSlider) {
                        (dragProgress * duration).toLong()
                    } else {
                        currentPosition
                    }
                    Text(
                        text = RuqyahAudioPlayer.formatDuration(displayPos),
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = if (duration > 0) RuqyahAudioPlayer.formatDuration(duration) else "--:--",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Playback Controls Row: Skip-10, Prev, Play/Pause, Next, Skip+10
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Loop toggle
                IconButton(
                    onClick = { player.toggleLooping() },
                    modifier = Modifier
                        .size(40.dp)
                        .testTag("ruqyah_loop_button")
                ) {
                    Icon(
                        imageVector = if (isLooping) Icons.Default.RepeatOne else Icons.Default.Repeat,
                        contentDescription = if (isLooping) "Repeat Track On" else "Repeat Off",
                        tint = if (isLooping) GoldAccent else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Skip backward 10 seconds
                IconButton(
                    onClick = { player.skipBackward(10) },
                    modifier = Modifier
                        .size(44.dp)
                        .testTag("ruqyah_skip_back_10")
                ) {
                    Icon(
                        imageVector = Icons.Default.Replay10,
                        contentDescription = "Rewind 10 seconds",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(26.dp)
                    )
                }

                // Previous track
                IconButton(
                    onClick = { player.playPreviousTrack() },
                    modifier = Modifier
                        .size(44.dp)
                        .testTag("ruqyah_prev_track")
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipPrevious,
                        contentDescription = "Previous Recitation",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(28.dp)
                    )
                }

                // Main Play/Pause Button
                val playButtonColor by animateColorAsState(
                    targetValue = if (isPlaying) EmeraldPrimary else GoldAccent,
                    label = "playButtonColor"
                )

                FilledIconButton(
                    onClick = { player.playOrPause() },
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = playButtonColor,
                        contentColor = if (isPlaying) Color.White else Color.Black
                    ),
                    modifier = Modifier
                        .size(56.dp)
                        .testTag("ruqyah_play_pause_button")
                ) {
                    if (isBuffering && !isPlaying) {
                        CircularProgressIndicator(
                            strokeWidth = 3.dp,
                            color = Color.Black,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                // Next track
                IconButton(
                    onClick = { player.playNextTrack() },
                    modifier = Modifier
                        .size(44.dp)
                        .testTag("ruqyah_next_track")
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "Next Recitation",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(28.dp)
                    )
                }

                // Skip forward 10 seconds
                IconButton(
                    onClick = { player.skipForward(10) },
                    modifier = Modifier
                        .size(44.dp)
                        .testTag("ruqyah_skip_forward_10")
                ) {
                    Icon(
                        imageVector = Icons.Default.Forward10,
                        contentDescription = "Forward 10 seconds",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(26.dp)
                    )
                }

                // Speed selection dropdown
                SpeedSelectorMenu(
                    currentSpeed = speed,
                    onSelectSpeed = { newSpeed -> player.setSpeed(newSpeed) }
                )
            }

            // Expandable Curated Playlist
            AnimatedVisibility(visible = showPlaylistSheet) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Authentic Sunnah Reciters",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${RuqyahAudioCatalog.CURATED_TRACKS.size} Recitations",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = EmeraldPrimary
                        )
                    }

                    RuqyahAudioCatalog.CURATED_TRACKS.forEach { track ->
                        val isSelected = currentTrack?.id == track.id
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) EmeraldPrimary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, GoldAccent) else null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { player.playTrack(track) }
                                .testTag("ruqyah_track_${track.id}")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = if (isSelected && isPlaying) Icons.Default.GraphicEq else Icons.Default.PlayCircleOutline,
                                        contentDescription = null,
                                        tint = if (isSelected) GoldAccent else EmeraldPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Column {
                                        Text(
                                            text = track.title,
                                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium),
                                            color = if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.onSurface,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = track.reciter,
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = if (isSelected) GoldAccent.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface
                                ) {
                                    Text(
                                        text = track.durationLabel,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (isSelected) GoldAccent else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Custom Remote Stream URL Dialog
    if (showCustomUrlDialog) {
        CustomStreamUrlDialog(
            onDismiss = { showCustomUrlDialog = false },
            onStream = { url, reciter, title ->
                player.streamRemoteUrl(url, title = title.ifEmpty { "Custom Ruqyah Recitation" }, reciter = reciter.ifEmpty { "Online Stream" })
                showCustomUrlDialog = false
            }
        )
    }
}

@Composable
private fun SpeedSelectorMenu(
    currentSpeed: Float,
    onSelectSpeed: (Float) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val speeds = listOf(0.75f, 1.0f, 1.25f, 1.5f)

    Box {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable { expanded = true }
                .testTag("ruqyah_speed_selector")
        ) {
            Text(
                text = "${currentSpeed}x",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            speeds.forEach { s ->
                DropdownMenuItem(
                    text = {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("${s}x Speed")
                            if (currentSpeed == s) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    tint = EmeraldPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    },
                    onClick = {
                        onSelectSpeed(s)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun CustomStreamUrlDialog(
    onDismiss: () -> Unit,
    onStream: (url: String, reciter: String, title: String) -> Unit
) {
    var urlText by remember { mutableStateOf("https://download.quranicaudio.com/quran/mishaari_raashid_al_3afaasee/001.mp3") }
    var reciterText by remember { mutableStateOf("") }
    var titleText by remember { mutableStateOf("") }
    var hasError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.Podcasts, contentDescription = null, tint = EmeraldPrimary)
                Text("Stream Remote Ruqyah URL", style = MaterialTheme.typography.titleMedium)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Enter any remote audio stream URL (MP3, AAC, M4A) for authentic Ruqyah Shariah recitation:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = urlText,
                    onValueChange = {
                        urlText = it
                        hasError = false
                    },
                    label = { Text("Remote Audio URL") },
                    placeholder = { Text("https://example.com/audio.mp3") },
                    singleLine = true,
                    isError = hasError,
                    supportingText = if (hasError) {
                        { Text("Please enter a valid HTTP/HTTPS URL", color = MaterialTheme.colorScheme.error) }
                    } else null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("custom_stream_url_input")
                )

                OutlinedTextField(
                    value = titleText,
                    onValueChange = { titleText = it },
                    label = { Text("Recitation Title (Optional)") },
                    placeholder = { Text("e.g., Ruqyah for Sickness") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = reciterText,
                    onValueChange = { reciterText = it },
                    label = { Text("Qari / Reciter Name (Optional)") },
                    placeholder = { Text("e.g., Sheikh Al-Minshawi") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (urlText.isNotBlank() && (urlText.startsWith("http://") || urlText.startsWith("https://"))) {
                        onStream(urlText.trim(), reciterText.trim(), titleText.trim())
                    } else {
                        hasError = true
                    }
                },
                modifier = Modifier.testTag("custom_stream_submit_button")
            ) {
                Text("Start Streaming")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
