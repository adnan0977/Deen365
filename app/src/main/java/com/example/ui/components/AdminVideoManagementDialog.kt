package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.data.local.IslamicVideoEntity
import com.example.data.local.IslamicVideoSeedData
import com.example.data.repository.IslamicVideoRepository
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminVideoManagementDialog(
    isAdminLoggedIn: Boolean,
    customVideos: List<IslamicVideoEntity>,
    onLoginAdmin: (String) -> Boolean,
    onLogoutAdmin: () -> Unit,
    onAddVideo: (IslamicVideoEntity) -> Unit,
    onDeleteVideo: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var passcode by remember { mutableStateOf("") }
    var loginError by remember { mutableStateOf<String?>(null) }

    // Form inputs for adding a video
    var youtubeInput by remember { mutableStateOf("") }
    var videoTitle by remember { mutableStateOf("") }
    var selectedSpeaker by remember { mutableStateOf(IslamicVideoSeedData.SPEAKERS[1]) }
    var customSpeaker by remember { mutableStateOf("") }
    var selectedTopic by remember { mutableStateOf(IslamicVideoSeedData.TOPICS[1]) }
    var duration by remember { mutableStateOf("20:00") }
    var channelName by remember { mutableStateOf("Islamic Channel") }
    var description by remember { mutableStateOf("") }
    var formError by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    val extractedVideoId = remember(youtubeInput) {
        IslamicVideoRepository.extractYouTubeVideoId(youtubeInput)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.90f)
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
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = EmeraldPrimary.copy(alpha = 0.15f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = "Admin Icon",
                                tint = EmeraldPrimary,
                                modifier = Modifier
                                    .padding(8.dp)
                                    .size(24.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Admin Video Portal",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (isAdminLoggedIn) "Authorized Video Curator" else "Passcode Required",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isAdminLoggedIn) EmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("admin_dialog_close")
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (!isAdminLoggedIn) {
                    // Admin Login Screen
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Lock",
                                    tint = GoldAccent,
                                    modifier = Modifier.size(48.dp)
                                )

                                Text(
                                    text = "Administrator Access",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Text(
                                    text = "Enter the administrator passcode to manually add new YouTube lectures and manage the Islamic video catalog.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                OutlinedTextField(
                                    value = passcode,
                                    onValueChange = {
                                        passcode = it
                                        loginError = null
                                    },
                                    label = { Text("Admin Passcode") },
                                    singleLine = true,
                                    visualTransformation = PasswordVisualTransformation(),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("admin_passcode_input"),
                                    leadingIcon = {
                                        Icon(Icons.Default.VpnKey, contentDescription = null)
                                    }
                                )

                                if (loginError != null) {
                                    Text(
                                        text = loginError ?: "",
                                        color = MaterialTheme.colorScheme.error,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }

                                Text(
                                    text = "Default admin passcode: deen365admin",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = GoldAccent
                                )

                                Button(
                                    onClick = {
                                        val success = onLoginAdmin(passcode)
                                        if (!success) {
                                            loginError = "Incorrect passcode. Please try again."
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("admin_login_button"),
                                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                                ) {
                                    Text("Sign In as Admin")
                                }
                            }
                        }
                    }
                } else {
                    // Admin Dashboard: Add Video + Manage Videos
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Add YouTube Lecture",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.primary
                                )

                                TextButton(
                                    onClick = onLogoutAdmin,
                                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                                ) {
                                    Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Log Out")
                                }
                            }
                        }

                        item {
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    OutlinedTextField(
                                        value = youtubeInput,
                                        onValueChange = {
                                            youtubeInput = it
                                            formError = null
                                            successMessage = null
                                        },
                                        label = { Text("YouTube URL or Video ID") },
                                        placeholder = { Text("e.g. https://youtu.be/wV5-a1cM6f0") },
                                        singleLine = true,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("admin_video_url_input"),
                                        trailingIcon = {
                                            if (extractedVideoId != null) {
                                                Icon(
                                                    Icons.Default.CheckCircle,
                                                    contentDescription = "Valid Video ID",
                                                    tint = EmeraldPrimary
                                                )
                                            }
                                        }
                                    )

                                    if (extractedVideoId != null) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(MaterialTheme.colorScheme.surface)
                                                .padding(8.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            AsyncImage(
                                                model = "https://img.youtube.com/vi/$extractedVideoId/hqdefault.jpg",
                                                contentDescription = "Thumbnail Preview",
                                                modifier = Modifier
                                                    .width(100.dp)
                                                    .height(60.dp)
                                                    .clip(RoundedCornerShape(6.dp)),
                                                contentScale = ContentScale.Crop
                                            )
                                            Column {
                                                Text(
                                                    text = "ID: $extractedVideoId",
                                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                                    color = EmeraldPrimary
                                                )
                                                Text(
                                                    text = "Thumbnail ready",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    }

                                    OutlinedTextField(
                                        value = videoTitle,
                                        onValueChange = { videoTitle = it },
                                        label = { Text("Lecture Title") },
                                        placeholder = { Text("Enter descriptive lecture title") },
                                        modifier = Modifier.fillMaxWidth().testTag("admin_video_title_input")
                                    )

                                    // Speaker Selection
                                    Text(
                                        text = "Speaker / Scholar",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                                    )
                                    var speakerExpanded by remember { mutableStateOf(false) }
                                    ExposedDropdownMenuBox(
                                        expanded = speakerExpanded,
                                        onExpandedChange = { speakerExpanded = !speakerExpanded }
                                    ) {
                                        OutlinedTextField(
                                            value = selectedSpeaker,
                                            onValueChange = {},
                                            readOnly = true,
                                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = speakerExpanded) },
                                            modifier = Modifier.menuAnchor().fillMaxWidth()
                                        )
                                        ExposedDropdownMenu(
                                            expanded = speakerExpanded,
                                            onDismissRequest = { speakerExpanded = false }
                                        ) {
                                            IslamicVideoSeedData.SPEAKERS.filter { it != "All Speakers" }.forEach { speaker ->
                                                DropdownMenuItem(
                                                    text = { Text(speaker) },
                                                    onClick = {
                                                        selectedSpeaker = speaker
                                                        speakerExpanded = false
                                                    }
                                                )
                                            }
                                            DropdownMenuItem(
                                                text = { Text("+ Custom Speaker Name") },
                                                onClick = {
                                                    selectedSpeaker = "Custom"
                                                    speakerExpanded = false
                                                }
                                            )
                                        }
                                    }

                                    if (selectedSpeaker == "Custom") {
                                        OutlinedTextField(
                                            value = customSpeaker,
                                            onValueChange = { customSpeaker = it },
                                            label = { Text("Enter Scholar / Speaker Name") },
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }

                                    // Topic Selection
                                    Text(
                                        text = "Lecture Topic",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                                    )
                                    var topicExpanded by remember { mutableStateOf(false) }
                                    ExposedDropdownMenuBox(
                                        expanded = topicExpanded,
                                        onExpandedChange = { topicExpanded = !topicExpanded }
                                    ) {
                                        OutlinedTextField(
                                            value = selectedTopic,
                                            onValueChange = {},
                                            readOnly = true,
                                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = topicExpanded) },
                                            modifier = Modifier.menuAnchor().fillMaxWidth()
                                        )
                                        ExposedDropdownMenu(
                                            expanded = topicExpanded,
                                            onDismissRequest = { topicExpanded = false }
                                        ) {
                                            IslamicVideoSeedData.TOPICS.filter { it != "All Topics" }.forEach { topic ->
                                                DropdownMenuItem(
                                                    text = { Text(topic) },
                                                    onClick = {
                                                        selectedTopic = topic
                                                        topicExpanded = false
                                                    }
                                                )
                                            }
                                        }
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        OutlinedTextField(
                                            value = duration,
                                            onValueChange = { duration = it },
                                            label = { Text("Duration") },
                                            placeholder = { Text("e.g. 24:15") },
                                            modifier = Modifier.weight(1f)
                                        )
                                        OutlinedTextField(
                                            value = channelName,
                                            onValueChange = { channelName = it },
                                            label = { Text("Channel") },
                                            placeholder = { Text("e.g. Official Channel") },
                                            modifier = Modifier.weight(1f)
                                        )
                                    }

                                    OutlinedTextField(
                                        value = description,
                                        onValueChange = { description = it },
                                        label = { Text("Description & Summary") },
                                        placeholder = { Text("Brief notes on topics covered") },
                                        maxLines = 3,
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    if (formError != null) {
                                        Text(
                                            text = formError ?: "",
                                            color = MaterialTheme.colorScheme.error,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }

                                    if (successMessage != null) {
                                        Text(
                                            text = successMessage ?: "",
                                            color = EmeraldPrimary,
                                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                                        )
                                    }

                                    Button(
                                        onClick = {
                                            val videoId = extractedVideoId
                                            if (videoId == null) {
                                                formError = "Please enter a valid YouTube URL or Video ID."
                                                return@Button
                                            }
                                            if (videoTitle.isBlank()) {
                                                formError = "Please enter a title for the video."
                                                return@Button
                                            }
                                            val finalSpeaker = if (selectedSpeaker == "Custom") {
                                                if (customSpeaker.isBlank()) "Islamic Scholar" else customSpeaker.trim()
                                            } else {
                                                selectedSpeaker
                                            }

                                            val newVideo = IslamicVideoEntity(
                                                id = videoId,
                                                title = videoTitle.trim(),
                                                speakerName = finalSpeaker,
                                                topicName = selectedTopic,
                                                duration = if (duration.isBlank()) "20:00" else duration.trim(),
                                                channelTitle = if (channelName.isBlank()) "Islamic Dawah" else channelName.trim(),
                                                youtubeUrl = "https://www.youtube.com/watch?v=$videoId",
                                                description = if (description.isBlank()) "Curated Islamic lecture by $finalSpeaker." else description.trim(),
                                                thumbnailUrl = "https://img.youtube.com/vi/$videoId/hqdefault.jpg",
                                                isCustomAdded = true,
                                                viewCountFormatted = "Added by Admin"
                                            )
                                            onAddVideo(newVideo)
                                            successMessage = "Lecture '${videoTitle.take(30)}...' added successfully!"
                                            youtubeInput = ""
                                            videoTitle = ""
                                            description = ""
                                            formError = null
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("admin_save_video_button"),
                                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = null)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Publish Video to Deen365")
                                    }
                                }
                            }
                        }

                        // Section: Admin Added Videos Management
                        if (customVideos.isNotEmpty()) {
                            item {
                                Text(
                                    text = "Custom Added Videos (${customVideos.size})",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            items(customVideos, key = { it.id }) { video ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surface
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        AsyncImage(
                                            model = video.thumbnailUrl,
                                            contentDescription = video.title,
                                            modifier = Modifier
                                                .width(80.dp)
                                                .height(50.dp)
                                                .clip(RoundedCornerShape(6.dp)),
                                            contentScale = ContentScale.Crop
                                        )

                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = video.title,
                                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                                maxLines = 1
                                            )
                                            Text(
                                                text = "${video.speakerName} • ${video.topicName}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }

                                        IconButton(
                                            onClick = { onDeleteVideo(video.id) },
                                            colors = IconButtonDefaults.iconButtonColors(contentColor = MaterialTheme.colorScheme.error)
                                        ) {
                                            Icon(Icons.Default.Delete, contentDescription = "Delete Video")
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
}
