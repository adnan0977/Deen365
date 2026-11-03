package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
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
import androidx.compose.material.icons.automirrored.filled.VolumeUp
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.audio.RepeatMode
import com.example.data.seed.DetailedPrayerPlan
import com.example.data.seed.NamazPrayerProcessData
import com.example.data.seed.PrayerProcessStep
import com.example.data.repository.NamazOfflineRepository
import com.example.ui.theme.EmeraldAccent
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldLight
import com.example.ui.viewmodel.DeenUiState
import com.example.ui.viewmodel.DeenViewModel

@Composable
fun EachNamazLearningView(
    state: DeenUiState,
    viewModel: DeenViewModel,
    modifier: Modifier = Modifier
) {
    val prayers = remember { NamazPrayerProcessData.PRAYERS }
    val currentPrayer = prayers.firstOrNull { it.id == state.selectedPrayerProcessId } ?: prayers[0]
    var showAzanDetails by remember { mutableStateOf(false) }

    // Automatically trigger caching into Room DB and pre-download audio once prayer module is opened
    LaunchedEffect(currentPrayer.id) {
        viewModel.autoCachePrayerIfNeeded(currentPrayer.id)
    }

    val downloadProgress = state.namazDownloadStates[currentPrayer.id]
    val isPrayerDownloaded = state.downloadedPrayerIds.contains(currentPrayer.id) ||
        (downloadProgress != null && downloadProgress.progress >= 1.0f && !downloadProgress.isDownloading)
    val isDownloading = downloadProgress?.isDownloading == true

    val audioState = state.salahAudioState
    val isPlaying = audioState.isPlaying

    val pulseScale by animateFloatAsState(
        targetValue = if (isPlaying) 1.08f else 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(650, easing = FastOutSlowInEasing),
            repeatMode = androidx.compose.animation.core.RepeatMode.Reverse
        ),
        label = "pulse_salah_audio"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Prayer Selection Chips (Fajr, Dhuhr, Asr, Maghrib, Isha)
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth().testTag("prayer_selection_card")
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Learn Each Namaz & Azan",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Step-by-step process, Duas, Ayats & authentic Azan",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = EmeraldPrimary.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "${currentPrayer.totalRakats} Rak'ahs",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = EmeraldPrimary),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(prayers) { prayer ->
                            val isSelected = prayer.id == currentPrayer.id
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.selectPrayerProcess(prayer.id) },
                                label = {
                                    Text(
                                        text = prayer.name.split(" ")[0],
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    )
                                },
                                leadingIcon = if (isSelected) {
                                    { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
                                } else null,
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = EmeraldPrimary,
                                    selectedLabelColor = Color.White
                                ),
                                modifier = Modifier.testTag("prayer_chip_${prayer.id}")
                            )
                        }
                    }
                }
            }
        }

        // 1.5. Inline Offline Ready & Automatic Room DB Download Card
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isPrayerDownloaded) EmeraldPrimary.copy(alpha = 0.10f)
                    else if (isDownloading) EmeraldPrimary.copy(alpha = 0.08f)
                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isPrayerDownloaded) EmeraldPrimary.copy(alpha = 0.55f)
                    else if (isDownloading) EmeraldPrimary.copy(alpha = 0.45f)
                    else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("offline_status_card_${currentPrayer.id}")
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
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = if (isPrayerDownloaded) EmeraldPrimary
                                else if (isDownloading) EmeraldPrimary.copy(alpha = 0.2f)
                                else MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    if (isDownloading) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(18.dp),
                                            strokeWidth = 2.dp,
                                            color = EmeraldPrimary
                                        )
                                    } else if (isPrayerDownloaded) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "Offline Ready",
                                            tint = Color.White,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    } else {
                                        Icon(
                                            imageVector = Icons.Default.CloudDownload,
                                            contentDescription = "Download for Offline",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = if (isPrayerDownloaded) "Offline Ready"
                                        else if (isDownloading) "Saving for Offline Practice..."
                                        else "Available for Offline Learning",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = if (isPrayerDownloaded) EmeraldPrimary else MaterialTheme.colorScheme.onSurface
                                    )

                                    if (isPrayerDownloaded) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = EmeraldPrimary.copy(alpha = 0.18f)
                                        ) {
                                            Text(
                                                text = "Room DB",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontWeight = FontWeight.ExtraBold,
                                                    color = EmeraldPrimary,
                                                    fontSize = 10.sp
                                                ),
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }

                                val subtext = if (isDownloading) {
                                    downloadProgress?.statusMessage ?: "Writing prayer steps to Room & downloading audio..."
                                } else if (isPrayerDownloaded) {
                                    val sizeStr = if (state.totalNamazCachedSizeBytes > 0) {
                                        NamazOfflineRepository.formatStorageSize(state.totalNamazCachedSizeBytes)
                                    } else "Cached"
                                    "100% offline available • $sizeStr on device"
                                } else {
                                    "Auto-saves prayer steps and reciter audio for offline practice"
                                }

                                Text(
                                    text = subtext,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Action / Progress Indicator
                        if (isDownloading) {
                            val percent = ((downloadProgress?.progress ?: 0f) * 100).toInt()
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = EmeraldPrimary.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "$percent%",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = EmeraldPrimary),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        } else if (isPrayerDownloaded) {
                            IconButton(
                                onClick = { viewModel.downloadPrayerForOffline(currentPrayer.id) },
                                modifier = Modifier.size(32.dp).testTag("refresh_download_${currentPrayer.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Re-sync prayer content",
                                    tint = EmeraldPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        } else {
                            FilledTonalButton(
                                onClick = { viewModel.downloadPrayerForOffline(currentPrayer.id) },
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                modifier = Modifier.testTag("download_prayer_btn_${currentPrayer.id}")
                            ) {
                                Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Save", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Linear progress bar during active download
                    if (isDownloading) {
                        LinearProgressIndicator(
                            progress = { downloadProgress?.progress ?: 0f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = EmeraldPrimary,
                            trackColor = EmeraldPrimary.copy(alpha = 0.2f),
                        )
                    }

                    // Download All 5 Prayers Quick Action
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${state.downloadedPrayerIds.size} of 5 Namaz cached for offline",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        if (state.downloadedPrayerIds.size < 5 && !state.isDownloadingAllNamaz) {
                            TextButton(
                                onClick = { viewModel.downloadAllPrayersForOffline() },
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp),
                                modifier = Modifier.testTag("download_all_prayers_btn")
                            ) {
                                Icon(Icons.Default.CloudDownload, contentDescription = null, modifier = Modifier.size(14.dp), tint = EmeraldPrimary)
                                Spacer(Modifier.width(4.dp))
                                Text("Download All 5", fontSize = 11.sp, color = EmeraldPrimary, fontWeight = FontWeight.SemiBold)
                            }
                        } else if (state.isDownloadingAllNamaz) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                CircularProgressIndicator(modifier = Modifier.size(12.dp), strokeWidth = 1.5.dp, color = EmeraldPrimary)
                                Text("Downloading all...", fontSize = 11.sp, color = EmeraldPrimary)
                            }
                        }
                    }
                }
            }
        }

        // 2. Audio Reciter & Repeat Controls Bar
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isPlaying) EmeraldPrimary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isPlaying) EmeraldPrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
                ),
                modifier = Modifier.fillMaxWidth().testTag("salah_audio_controls_card")
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Speaker Info & Change Button
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
                                    .scale(pulseScale)
                                    .clip(CircleShape)
                                    .background(if (isPlaying) EmeraldPrimary else GoldAccent),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isPlaying) Icons.AutoMirrored.Filled.VolumeUp else Icons.Default.RecordVoiceOver,
                                    contentDescription = null,
                                    tint = if (isPlaying) Color.White else Color.Black,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "Reciter / Speaker",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = audioState.selectedReciter.name,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        OutlinedButton(
                            onClick = { viewModel.openReciterDialog() },
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("change_speaker_btn")
                        ) {
                            Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Change Speaker", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))

                    // Repeat Option for Learning
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Learning Repeat Mode:",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            RepeatMode.values().forEach { mode ->
                                val isSelected = audioState.repeatMode == mode
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { viewModel.setSalahRepeatMode(mode) },
                                    label = { Text(mode.displayName, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = GoldAccent.copy(alpha = 0.25f),
                                        selectedLabelColor = EmeraldPrimary
                                    ),
                                    modifier = Modifier.testTag("repeat_chip_${mode.name.lowercase()}")
                                )
                            }
                        }
                    }

                    // Live Playback Status & Stop Button
                    if (isPlaying) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = EmeraldPrimary.copy(alpha = 0.15f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        strokeWidth = 2.dp,
                                        color = EmeraldPrimary
                                    )
                                    Column {
                                        Text(
                                            text = audioState.currentTitle ?: "Reciting...",
                                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                            color = EmeraldPrimary
                                        )
                                        Text(
                                            text = "Repeat iteration ${audioState.currentIteration} (${audioState.repeatMode.displayName})",
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                IconButton(
                                    onClick = { viewModel.stopSalahAudio() },
                                    modifier = Modifier.size(32.dp).testTag("stop_salah_audio_btn")
                                ) {
                                    Icon(Icons.Default.Stop, contentDescription = "Stop", tint = Color.Red)
                                }
                            }
                        }
                    }
                }
            }
        }

        // 3. Azan of That Prayer Section
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth().testTag("azan_card_${currentPrayer.id}")
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
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
                                modifier = Modifier.size(38.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(20.dp))
                                }
                            }
                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = "Azan for ${currentPrayer.name.split(" ")[0]}",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    if (isPrayerDownloaded) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = EmeraldPrimary.copy(alpha = 0.15f)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                                            ) {
                                                Icon(Icons.Default.Check, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(10.dp))
                                                Text("Offline", fontSize = 10.sp, color = EmeraldPrimary, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                                Text(
                                    text = if (currentPrayer.azan.isFajr) "Includes: 'Prayer is better than sleep'" else "Standard Call to Prayer",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        val isPlayingThisAzan = isPlaying && audioState.currentAudioId == currentPrayer.azan.audioId
                        FilledTonalButton(
                            onClick = {
                                if (isPlayingThisAzan) {
                                    viewModel.stopSalahAudio()
                                } else {
                                    val fullArabic = currentPrayer.azan.phrases.joinToString(" • ") { it.arabic }
                                    viewModel.playSalahStepAudio(
                                        audioId = currentPrayer.azan.audioId,
                                        title = "Azan for ${currentPrayer.name.split(" ")[0]}",
                                        arabicText = fullArabic
                                    )
                                }
                            },
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = if (isPlayingThisAzan) Color.Red.copy(alpha = 0.15f) else EmeraldPrimary,
                                contentColor = if (isPlayingThisAzan) Color.Red else Color.White
                            ),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("play_azan_btn_${currentPrayer.id}")
                        ) {
                            Icon(
                                imageVector = if (isPlayingThisAzan) Icons.Default.Stop else Icons.Default.PlayArrow,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (isPlayingThisAzan) "Stop" else "Listen Azan", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Azan Phrases toggle
                    OutlinedButton(
                        onClick = { showAzanDetails = !showAzanDetails },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (showAzanDetails) "Hide Azan Phrases" else "View All Azan Phrases & Dua After Azan")
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = if (showAzanDetails) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null
                        )
                    }

                    AnimatedVisibility(visible = showAzanDetails) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            currentPrayer.azan.phrases.forEachIndexed { idx, phrase ->
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "${idx + 1}. Phrase (${phrase.repeats})",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                color = GoldAccent
                                            )
                                            Text(
                                                text = phrase.arabic,
                                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                                color = MaterialTheme.colorScheme.primary,
                                                textAlign = TextAlign.Right
                                            )
                                        }
                                        Text(
                                            text = phrase.transliteration,
                                            style = MaterialTheme.typography.bodySmall.copy(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic),
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = phrase.translation,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }

                            // Dua After Azan Card
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = GoldAccent.copy(alpha = 0.12f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent.copy(alpha = 0.4f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Dua After Azan (Supplication)",
                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                            color = GoldAccent
                                        )

                                        val isPlayingDua = isPlaying && audioState.currentAudioId == "dua_after_azan"
                                        TextButton(
                                            onClick = {
                                                if (isPlayingDua) {
                                                    viewModel.stopSalahAudio()
                                                } else {
                                                    viewModel.playSalahStepAudio(
                                                        audioId = "dua_after_azan",
                                                        title = "Dua After Azan",
                                                        arabicText = currentPrayer.azan.duaAfterAzanArabic
                                                    )
                                                }
                                            },
                                            contentPadding = PaddingValues(0.dp)
                                        ) {
                                            Icon(
                                                imageVector = if (isPlayingDua) Icons.Default.Stop else Icons.Default.PlayArrow,
                                                contentDescription = null,
                                                tint = EmeraldPrimary,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(Modifier.width(4.dp))
                                            Text("Recite", color = EmeraldPrimary, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                        }
                                    }

                                    Text(
                                        text = currentPrayer.azan.duaAfterAzanArabic,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.primary,
                                        textAlign = TextAlign.Right,
                                        lineHeight = 24.sp,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Text(
                                        text = currentPrayer.azan.duaAfterAzanTransliteration,
                                        style = MaterialTheme.typography.bodySmall.copy(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = currentPrayer.azan.duaAfterAzanTranslation,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 4. Prayer Overview Banner (Rakats, Recitation Rule & Hadith)
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = currentPrayer.arabicTitle,
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Rak'ahs: ${currentPrayer.rakatsSummary}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Recitation: ${currentPrayer.recitationRule}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = EmeraldPrimary.copy(alpha = 0.1f),
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Text(
                            text = currentPrayer.hadithVirtue,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic),
                            color = EmeraldPrimary,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
        }

        // 5. Header for Steps
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Step-by-Step Prayer Process (${currentPrayer.steps.size} Steps)",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Tap speaker to practice",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // 6. Each Step Card (Ayats, Duas, Arabic, Transliteration, Audio)
        items(currentPrayer.steps) { step ->
            PrayerStepCard(
                step = step,
                audioState = audioState,
                isPrayerDownloaded = isPrayerDownloaded,
                onPlayAudio = {
                    viewModel.playSalahStepAudio(
                        audioId = step.audioId,
                        title = step.title,
                        arabicText = step.arabicRecitation
                    )
                },
                onStopAudio = { viewModel.stopSalahAudio() }
            )
        }
    }
}

@Composable
private fun PrayerStepCard(
    step: PrayerProcessStep,
    audioState: com.example.data.audio.SalahAudioPlaybackState,
    isPrayerDownloaded: Boolean = false,
    onPlayAudio: () -> Unit,
    onStopAudio: () -> Unit
) {
    val isPlayingThis = audioState.isPlaying && audioState.currentAudioId == step.audioId

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isPlayingThis) EmeraldAccent else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
        ),
        modifier = Modifier.fillMaxWidth().testTag("salah_step_${step.stepIndex}")
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            // Step Header: Index, Title & Posture
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
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(EmeraldPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${step.stepIndex}",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = Color.White)
                        )
                    }

                    Column {
                        Text(
                            text = step.title,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "Posture: ${step.posture} • ${step.repetitionGuide}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (isPrayerDownloaded) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = EmeraldPrimary.copy(alpha = 0.15f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                                    ) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(9.dp))
                                        Text("Offline", fontSize = 9.sp, color = EmeraldPrimary, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                // Audio Button
                IconButton(
                    onClick = {
                        if (isPlayingThis) onStopAudio() else onPlayAudio()
                    },
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(if (isPlayingThis) Color.Red.copy(alpha = 0.15f) else EmeraldPrimary.copy(alpha = 0.15f))
                        .testTag("play_step_audio_${step.stepIndex}")
                ) {
                    Icon(
                        imageVector = if (isPlayingThis) Icons.Default.Stop else Icons.Default.VolumeUp,
                        contentDescription = "Recite Step",
                        tint = if (isPlayingThis) Color.Red else EmeraldPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Arabic Recitation Text
            if (step.arabicRecitation.isNotBlank()) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = step.arabicRecitation,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                textAlign = TextAlign.Right,
                                lineHeight = 36.sp
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // Transliteration
            if (step.transliteration.isNotBlank()) {
                Text(
                    text = step.transliteration,
                    style = MaterialTheme.typography.bodySmall.copy(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // English Translation
            if (step.englishTranslation.isNotBlank()) {
                Text(
                    text = step.englishTranslation,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Notes / Fiqh details
            if (step.notes.isNotBlank()) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = GoldAccent.copy(alpha = 0.12f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "💡 ${step.notes}",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}
