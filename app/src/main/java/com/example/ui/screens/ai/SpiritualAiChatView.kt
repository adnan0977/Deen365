package com.example.ui.screens.ai

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MessageSender
import com.example.data.model.SpiritualChatMessage
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.EmeraldPrimaryVariant
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldLight
import com.example.ui.viewmodel.DeenUiState
import com.example.ui.viewmodel.DeenViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SpiritualAiChatView(
    state: DeenUiState,
    viewModel: DeenViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    var showClearDialog by remember { mutableStateOf(false) }

    val suggestedQuestions = remember {
        listOf(
            "🤲 How can I overcome anxiety and worry using Quranic wisdom?",
            "🕋 How do I attain Khushoo and deep presence in my Salah?",
            "📖 Explain the spiritual depths of Surah Ash-Sharh (94:5-6)",
            "🌙 What are the special virtues of Tahajjud night prayer?",
            "✨ Which master Dua is recommended for sincere repentance?",
            "🛡️ What are the authentic Sunnah morning adhkar for protection?",
            "🕊️ How can I practice Sabr (patience) during trials?"
        )
    }

    // Auto-scroll to bottom on message list update or streaming tokens
    LaunchedEffect(state.chatMessages.size, state.isChatStreaming) {
        if (state.chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(state.chatMessages.lastIndex)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Engine Status & Clear Bar
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            border = androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Pulsing active dot
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF10B981))
                    )
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "Firebase Gemini 3.8 Flash",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = GoldAccent.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "AI Logic",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Black),
                                    color = EmeraldPrimary,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                        Text(
                            text = "Spiritually grounded in Quran & Sahih Sunnah",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(
                    onClick = { showClearDialog = true },
                    modifier = Modifier.testTag("ai_chat_clear_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Restart Chat",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Chat Message Stream
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Suggestion chips at the top of chat
            item {
                Column(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                    Text(
                        text = "Suggested Questions",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(suggestedQuestions) { prompt ->
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                border = androidx.compose.foundation.BorderStroke(0.5.dp, EmeraldPrimary.copy(alpha = 0.2f)),
                                modifier = Modifier
                                    .clickable {
                                        viewModel.sendChatMessage(prompt)
                                    }
                                    .testTag("suggested_prompt_chip")
                            ) {
                                Text(
                                    text = prompt,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Render Messages
            items(state.chatMessages, key = { it.id }) { message ->
                ChatMessageBubble(
                    message = message,
                    isPlaying = state.playingAudioId == message.id,
                    onPlayAudio = {
                        val arabic = message.arabicSnippet ?: ""
                        if (arabic.isNotBlank()) {
                            viewModel.playAudio(message.id, arabic, message.translation ?: "")
                        }
                    },
                    onCopy = {
                        val clip = ClipData.newPlainText("Deen AI Response", message.text)
                        val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        cm.setPrimaryClip(clip)
                        Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                    },
                    onShare = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, "${message.text}\n\n— Shared from Deen365 Spiritual Companion")
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share Spiritual Guidance"))
                    }
                )
            }

            // Error & Retry Banner
            if (state.chatErrorMessage != null) {
                item {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth().testTag("ai_chat_error_card")
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                                Text(
                                    text = state.chatErrorMessage,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                            Button(
                                onClick = { viewModel.retryLastChatMessage() },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text("Retry", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Bottom Chat Input Dock
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            border = androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = state.chatInputText,
                    onValueChange = { viewModel.setChatInputText(it) },
                    placeholder = {
                        Text(
                            "Ask a question on faith, salah, fasting, or life...",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp)
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("ai_chat_input_field"),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EmeraldPrimary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(onSend = {
                        if (state.chatInputText.isNotBlank()) {
                            keyboardController?.hide()
                            viewModel.sendChatMessage()
                        }
                    }),
                    maxLines = 4
                )

                // Send Button
                FilledIconButton(
                    onClick = {
                        if (state.chatInputText.isNotBlank()) {
                            keyboardController?.hide()
                            viewModel.sendChatMessage()
                        }
                    },
                    enabled = state.chatInputText.isNotBlank() && !state.isChatLoading,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = EmeraldPrimary,
                        contentColor = Color.White,
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                    ),
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("ai_chat_send_button")
                ) {
                    if (state.isChatLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = Color.White
                        )
                    } else {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send Message",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }

    // Confirmation dialog for clearing chat
    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("Restart Spiritual Chat?") },
            text = { Text("This will clear the current conversation and start fresh with your Deen AI companion.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showClearDialog = false
                        viewModel.clearChatHistory()
                    }
                ) {
                    Text("Restart", color = EmeraldPrimary, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ChatMessageBubble(
    message: SpiritualChatMessage,
    isPlaying: Boolean,
    onPlayAudio: () -> Unit,
    onCopy: () -> Unit,
    onShare: () -> Unit
) {
    val isUser = message.sender == MessageSender.USER
    val timeFormatter = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }
    val formattedTime = remember(message.timestamp) { timeFormatter.format(Date(message.timestamp)) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .testTag(if (isUser) "user_message_bubble" else "ai_message_bubble"),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            // AI Avatar
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(EmeraldPrimary, EmeraldPrimaryVariant)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "Deen AI",
                    tint = GoldLight,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Column(
            horizontalAlignment = if (isUser) Alignment.End else Alignment.Start,
            modifier = Modifier.widthIn(max = 320.dp)
        ) {
            if (!isUser) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(bottom = 3.dp, start = 4.dp)
                ) {
                    Text(
                        text = "Deen AI",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = EmeraldPrimary
                    )
                    Text(
                        text = "• $formattedTime",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Surface(
                shape = if (isUser) {
                    RoundedCornerShape(18.dp, 18.dp, 4.dp, 18.dp)
                } else {
                    RoundedCornerShape(4.dp, 18.dp, 18.dp, 18.dp)
                },
                color = if (isUser) {
                    EmeraldPrimary
                } else {
                    MaterialTheme.colorScheme.surface
                },
                border = if (isUser) null else androidx.compose.foundation.BorderStroke(
                    0.8.dp,
                    if (message.isError) MaterialTheme.colorScheme.error.copy(alpha = 0.5f)
                    else EmeraldPrimary.copy(alpha = 0.15f)
                ),
                shadowElevation = if (isUser) 1.dp else 2.dp
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (isUser) {
                        Text(
                            text = message.text,
                            style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
                            color = Color.White
                        )
                    } else {
                        // AI Formatted Content
                        if (message.text.isBlank() && message.isStreaming) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.padding(vertical = 4.dp)
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = EmeraldPrimary
                                )
                                Text(
                                    text = "Reflecting upon Quran & Sunnah...",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        } else {
                            Text(
                                text = message.text,
                                style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        // Quranic Arabic Snippet Highlight if present
                        if (!message.arabicSnippet.isNullOrBlank()) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = EmeraldPrimary.copy(alpha = 0.08f),
                                border = androidx.compose.foundation.BorderStroke(0.6.dp, GoldAccent.copy(alpha = 0.3f)),
                                modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = message.arabicSnippet,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                            lineHeight = 30.sp
                                        ),
                                        textAlign = TextAlign.Right,
                                        color = EmeraldPrimary,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    if (!message.translation.isNullOrBlank()) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "\"${message.translation}\"",
                                            style = MaterialTheme.typography.bodySmall.copy(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic),
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    if (!message.reference.isNullOrBlank()) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "— ${message.reference}",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = GoldAccent
                                        )
                                    }
                                }
                            }
                        }

                        // Action Tools: Listen, Copy, Share
                        if (!message.isStreaming && message.text.isNotBlank()) {
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                                modifier = Modifier.padding(top = 4.dp)
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (!message.arabicSnippet.isNullOrBlank()) {
                                    TextButton(
                                        onClick = onPlayAudio,
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (isPlaying) Icons.Default.PauseCircle else Icons.Default.VolumeUp,
                                            contentDescription = "Listen",
                                            modifier = Modifier.size(16.dp),
                                            tint = EmeraldPrimary
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (isPlaying) "Playing" else "Listen",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = EmeraldPrimary
                                        )
                                    }
                                } else {
                                    Spacer(modifier = Modifier.width(1.dp))
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    IconButton(
                                        onClick = onCopy,
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ContentCopy,
                                            contentDescription = "Copy message",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    IconButton(
                                        onClick = onShare,
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Share,
                                            contentDescription = "Share message",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (isUser) {
                Text(
                    text = formattedTime,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp, end = 4.dp)
                )
            }
        }
    }
}
