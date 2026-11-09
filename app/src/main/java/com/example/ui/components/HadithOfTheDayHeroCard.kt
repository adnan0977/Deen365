package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.DailyHadithItem
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.EmeraldPrimaryVariant
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldLight

/**
 * High-polish Material 3 Hero Card for the Hadith of the Day.
 * Displays Arabic script, authentic narrator, English translation, grade badge,
 * practical life lesson, audio recitation, interactive daily navigation (Previous/Next),
 * copy/share, and jump to the full Hadith reader.
 */
@Composable
fun HadithOfTheDayHeroCard(
    hadith: DailyHadithItem,
    dayOffset: Int = 0,
    onPreviousDay: () -> Unit,
    onNextDay: () -> Unit,
    onResetToToday: () -> Unit,
    onPlayAudio: () -> Unit,
    onToggleFavorite: () -> Unit,
    onToggleRead: () -> Unit,
    onOpenHadithReader: () -> Unit,
    onAskAi: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
        border = BorderStroke(1.dp, GoldAccent.copy(alpha = 0.32f)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("hadith_of_the_day_card")
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header: Category Pill, Day Offset Badge & Navigation Buttons
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
                        shape = RoundedCornerShape(8.dp),
                        color = EmeraldPrimary.copy(alpha = 0.12f),
                        border = BorderStroke(0.6.dp, EmeraldPrimary.copy(alpha = 0.25f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MenuBook,
                                contentDescription = null,
                                tint = EmeraldPrimary,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "HADITH OF THE DAY",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 0.8.sp,
                                    color = EmeraldPrimary
                                )
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = GoldAccent.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = if (dayOffset == 0) "Today" else if (dayOffset > 0) "+$dayOffset d" else "$dayOffset d",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = GoldAccent,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }

                // Previous, Next, Reset navigation buttons
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    if (dayOffset != 0) {
                        IconButton(
                            onClick = onResetToToday,
                            modifier = Modifier
                                .size(32.dp)
                                .testTag("hadith_reset_today_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Today,
                                contentDescription = "Return to Today",
                                tint = EmeraldPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    IconButton(
                        onClick = onPreviousDay,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("hadith_prev_day_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronLeft,
                            contentDescription = "Previous Day Hadith",
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(
                        onClick = onNextDay,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("hadith_next_day_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Next Day Hadith",
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(
                        onClick = onToggleFavorite,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("hadith_fav_btn")
                    ) {
                        Icon(
                            imageVector = if (hadith.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Save Favorite",
                            tint = if (hadith.isFavorite) Color(0xFFE53935) else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(19.dp)
                        )
                    }
                }
            }

            // Collection & Grade Subtitle Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = hadith.referenceText,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = EmeraldPrimary
                    )
                )

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                ) {
                    Text(
                        text = hadith.grade,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                    )
                }
            }

            // Arabic Text Container
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = EmeraldPrimary.copy(alpha = 0.05f),
                border = BorderStroke(0.8.dp, EmeraldPrimary.copy(alpha = 0.18f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = hadith.arabicText,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 34.sp
                        ),
                        textAlign = TextAlign.Right,
                        color = EmeraldPrimary,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Narrator Tag
            if (hadith.narrator.isNotBlank()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Narrated by:",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = hadith.narrator,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = GoldAccent
                    )
                }
            }

            // English Translation
            Text(
                text = "\"${hadith.translation}\"",
                style = MaterialTheme.typography.bodyMedium.copy(
                    lineHeight = 22.sp,
                    fontStyle = FontStyle.Normal
                ),
                color = MaterialTheme.colorScheme.onSurface
            )

            // Practical Daily Lesson Banner
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = GoldAccent.copy(alpha = 0.12f),
                border = BorderStroke(0.5.dp, GoldAccent.copy(alpha = 0.35f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("💡", fontSize = 14.sp)
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = "Daily Spiritual Reflection",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = EmeraldPrimary
                            )
                        )
                        Text(
                            text = hadith.practicalLesson,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp, lineHeight = 16.sp),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))

            // Action Buttons Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Audio recitation button
                FilledTonalButton(
                    onClick = onPlayAudio,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = EmeraldPrimary.copy(alpha = 0.12f),
                        contentColor = EmeraldPrimary
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("play_hadith_audio_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = "Recite Hadith",
                        modifier = Modifier.size(17.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Listen Audio",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                // Interactive Icons: Mark Studied, Ask AI, Copy, Full Reader
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Mark as Studied / Read
                    IconButton(
                        onClick = onToggleRead,
                        modifier = Modifier
                            .size(34.dp)
                            .testTag("hadith_mark_read_btn")
                    ) {
                        Icon(
                            imageVector = if (hadith.isRead) Icons.Default.CheckCircle else Icons.Outlined.CheckCircle,
                            contentDescription = "Mark Studied",
                            tint = if (hadith.isRead) EmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Ask AI about this Hadith
                    IconButton(
                        onClick = onAskAi,
                        modifier = Modifier
                            .size(34.dp)
                            .testTag("hadith_ask_ai_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Ask Deen AI",
                            tint = GoldAccent,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Copy to clipboard
                    IconButton(
                        onClick = {
                            val clipText = "Hadith of the Day\n${hadith.referenceText}\n\n${hadith.arabicText}\n\nNarrated by: ${hadith.narrator}\n\n\"${hadith.translation}\"\n\nReflect: ${hadith.practicalLesson}\n\nShared via Deen365"
                            val clip = ClipData.newPlainText("Hadith of the Day", clipText)
                            val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            cm.setPrimaryClip(clip)
                            Toast.makeText(context, "Hadith copied to clipboard!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .size(34.dp)
                            .testTag("hadith_copy_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy Hadith",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(19.dp)
                        )
                    }

                    // Open Full Hadith Reader
                    TextButton(
                        onClick = onOpenHadithReader,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("hadith_view_collection_btn")
                    ) {
                        Text(
                            text = "Explore",
                            color = EmeraldPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
