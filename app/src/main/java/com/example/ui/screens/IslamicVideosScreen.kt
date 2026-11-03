package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.example.ui.util.adaptiveFocusable
import com.example.ui.util.rememberDeviceScreenProfile
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.data.local.IslamicVideoEntity
import com.example.data.local.IslamicVideoSeedData
import com.example.ui.components.AdminVideoManagementDialog
import com.example.ui.components.EmbeddedYouTubePlayerView
import com.example.ui.components.UserAccountHabitsDialog
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.EmeraldPrimaryVariant
import com.example.ui.theme.GoldAccent
import com.example.ui.viewmodel.DeenUiState
import com.example.ui.viewmodel.DeenViewModel
import com.example.ui.viewmodel.VideoFilterType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IslamicVideosScreen(
    state: DeenUiState,
    viewModel: DeenViewModel,
    modifier: Modifier = Modifier
) {
    var showAdminDialog by remember { mutableStateOf(false) }
    var showUserDialog by remember { mutableStateOf(false) }
    var activeVideoForPlaying by remember { mutableStateOf<IslamicVideoEntity?>(null) }

    val watchedIds = remember(state.userHabit.watchedVideoIdsCsv) {
        state.userHabit.getWatchedIdsSet()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Screen Header & Action Icons
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Islamic Lectures",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = GoldAccent.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "YouTube",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = GoldAccent,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Authentic reminders filtered by Scholar or Topic",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Admin Portal Button
                        IconButton(
                            onClick = { showAdminDialog = true },
                            modifier = Modifier
                                .testTag("open_admin_portal_button")
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Icon(
                                imageVector = if (state.isAdminLoggedIn) Icons.Default.AdminPanelSettings else Icons.Outlined.AdminPanelSettings,
                                contentDescription = "Admin Portal",
                                tint = if (state.isAdminLoggedIn) EmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // User Profile & AI Habits Button
                        IconButton(
                            onClick = { showUserDialog = true },
                            modifier = Modifier
                                .testTag("open_user_habits_button")
                                .clip(CircleShape)
                                .background(EmeraldPrimary.copy(alpha = 0.15f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "User Account & Habits",
                                tint = EmeraldPrimary
                            )
                        }

                        // Settings Button
                        IconButton(
                            onClick = { viewModel.openSettings() },
                            modifier = Modifier
                                .testTag("open_settings_button_videos")
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Dual Section Switcher: By Speaker vs By Topic
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(6.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Speaker Tab
                        Button(
                            onClick = { viewModel.setVideoFilterType(VideoFilterType.BY_SPEAKER) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("filter_tab_speaker"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (state.videoFilterType == VideoFilterType.BY_SPEAKER) EmeraldPrimary else Color.Transparent,
                                contentColor = if (state.videoFilterType == VideoFilterType.BY_SPEAKER) Color.White else MaterialTheme.colorScheme.onSurface
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "By Speaker",
                                fontWeight = if (state.videoFilterType == VideoFilterType.BY_SPEAKER) FontWeight.Bold else FontWeight.Normal
                            )
                        }

                        // Topic Tab
                        Button(
                            onClick = { viewModel.setVideoFilterType(VideoFilterType.BY_TOPIC) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("filter_tab_topic"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (state.videoFilterType == VideoFilterType.BY_TOPIC) EmeraldPrimary else Color.Transparent,
                                contentColor = if (state.videoFilterType == VideoFilterType.BY_TOPIC) Color.White else MaterialTheme.colorScheme.onSurface
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Category,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "By Topic",
                                fontWeight = if (state.videoFilterType == VideoFilterType.BY_TOPIC) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }

            // Horizontal Chip Carousel based on current filter type
            item {
                if (state.videoFilterType == VideoFilterType.BY_SPEAKER) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth().testTag("speaker_chips_row")
                    ) {
                        items(IslamicVideoSeedData.SPEAKERS) { speaker ->
                            val isSelected = state.selectedSpeakerFilter == speaker
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.selectSpeakerFilter(speaker) },
                                label = { Text(speaker) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = if (speaker == "All Speakers") Icons.Default.Groups else Icons.Default.RecordVoiceOver,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = EmeraldPrimary,
                                    selectedLabelColor = Color.White,
                                    selectedLeadingIconColor = Color.White
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }
                } else {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth().testTag("topic_chips_row")
                    ) {
                        items(IslamicVideoSeedData.TOPICS) { topic ->
                            val isSelected = state.selectedTopicFilter == topic
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.selectTopicFilter(topic) },
                                label = { Text(topic) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = if (topic == "All Topics") Icons.Default.AllInclusive else Icons.Default.Bookmark,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = EmeraldPrimary,
                                    selectedLabelColor = Color.White,
                                    selectedLeadingIconColor = Color.White
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }
                }
            }

            // Search Bar
            item {
                OutlinedTextField(
                    value = state.videoSearchQuery,
                    onValueChange = { viewModel.setVideoSearchQuery(it) },
                    placeholder = { Text("Search by title, scholar, or theme...") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = "Search Videos")
                    },
                    trailingIcon = {
                        if (state.videoSearchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setVideoSearchQuery("") }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear Search")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("video_search_input")
                )
            }

            // Count header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val filterDescription = if (state.videoFilterType == VideoFilterType.BY_SPEAKER) {
                        state.selectedSpeakerFilter
                    } else {
                        state.selectedTopicFilter
                    }
                    Text(
                        text = "$filterDescription (${state.filteredVideos.size})",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    if (state.isAdminLoggedIn) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = EmeraldPrimary.copy(alpha = 0.15f),
                            modifier = Modifier.clickable { showAdminDialog = true }
                        ) {
                            Text(
                                text = "+ Add Video",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = EmeraldPrimary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            // Video Cards List
            if (state.filteredVideos.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.SearchOff,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(48.dp)
                            )
                            Text(
                                text = "No lectures found",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Try clearing your search query or selecting a different speaker/topic.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(state.filteredVideos, key = { it.id }) { video ->
                    val isWatched = watchedIds.contains(video.id)

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                activeVideoForPlaying = video
                                viewModel.recordVideoWatch(video.id)
                            }
                            .testTag("video_card_${video.id}"),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column {
                            // Thumbnail with overlaid badges
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(16f / 9f)
                                    .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
                                    .background(Color.Black)
                            ) {
                                AsyncImage(
                                    model = video.thumbnailUrl,
                                    contentDescription = video.title,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )

                                // Duration pill
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color.Black.copy(alpha = 0.8f),
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .padding(8.dp)
                                ) {
                                    Text(
                                        text = video.duration,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }

                                // Play button overlay
                                Surface(
                                    shape = CircleShape,
                                    color = EmeraldPrimary.copy(alpha = 0.85f),
                                    modifier = Modifier.align(Alignment.Center)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = "Play",
                                        tint = Color.White,
                                        modifier = Modifier
                                            .padding(12.dp)
                                            .size(28.dp)
                                    )
                                }

                                if (isWatched) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = EmeraldPrimaryVariant.copy(alpha = 0.9f),
                                        modifier = Modifier
                                            .align(Alignment.TopStart)
                                            .padding(8.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                tint = GoldAccent,
                                                modifier = Modifier.size(12.dp)
                                            )
                                            Text(
                                                text = "Completed",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                color = Color.White
                                            )
                                        }
                                    }
                                }
                            }

                            // Info details
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = video.title,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = EmeraldPrimary.copy(alpha = 0.12f)
                                    ) {
                                        Text(
                                            text = video.speakerName,
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                            color = EmeraldPrimary,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = GoldAccent.copy(alpha = 0.15f)
                                    ) {
                                        Text(
                                            text = video.topicName,
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                            color = GoldAccent,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.weight(1f))

                                    Text(
                                        text = video.viewCountFormatted,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Text(
                                    text = video.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }

        // In-App Embedded YouTube Player Dialog / Fullsheet
        if (activeVideoForPlaying != null) {
            val video = activeVideoForPlaying!!

            Dialog(
                onDismissRequest = { activeVideoForPlaying = null }
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth(0.98f)
                        .wrapContentHeight()
                        .clip(RoundedCornerShape(20.dp)),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Now Playing in Deen365",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = EmeraldPrimary
                            )
                            IconButton(
                                onClick = { activeVideoForPlaying = null }
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Close Player")
                            }
                        }

                        // Embedded Player
                        EmbeddedYouTubePlayerView(videoId = video.id)

                        Text(
                            text = video.title,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = EmeraldPrimary.copy(alpha = 0.12f)
                            ) {
                                Text(
                                    text = video.speakerName,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = EmeraldPrimary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = GoldAccent.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = video.topicName,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = GoldAccent,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            Text(
                                text = video.channelTitle,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Text(
                            text = video.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Button(
                            onClick = {
                                viewModel.recordVideoWatch(video.id)
                                activeVideoForPlaying = null
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                        ) {
                            Icon(Icons.Default.DoneAll, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Mark as Watched (+15 Mins Deen Study)")
                        }
                    }
                }
            }
        }

        // Admin Portal Dialog
        if (showAdminDialog) {
            AdminVideoManagementDialog(
                isAdminLoggedIn = state.isAdminLoggedIn,
                customVideos = state.customVideos,
                onLoginAdmin = { passcode ->
                    viewModel.loginAdmin(passcode)
                },
                onLogoutAdmin = {
                    viewModel.logoutAdmin()
                },
                onAddVideo = { newVideo ->
                    viewModel.addVideoByAdmin(newVideo)
                },
                onDeleteVideo = { videoId ->
                    viewModel.deleteVideoByAdmin(videoId)
                },
                onDismiss = { showAdminDialog = false }
            )
        }

        // User Account & AI Habits Dialog
        if (showUserDialog) {
            UserAccountHabitsDialog(
                userHabit = state.userHabit,
                prayerRecord = state.prayerRecord,
                isGeneratingAiHabit = state.isGeneratingAiHabit,
                aiHabitInsight = state.aiHabitInsight,
                onSaveUserHabit = { updatedHabit ->
                    viewModel.saveUserHabit(updatedHabit)
                },
                onGenerateAiInsight = {
                    viewModel.generateHabitInsightsWithAi()
                },
                onDismiss = { showUserDialog = false }
            )
        }
    }
}
