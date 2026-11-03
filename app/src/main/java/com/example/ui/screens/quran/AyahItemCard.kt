package com.example.ui.screens.quran

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.FormatPaint
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.AyahEntity
import com.example.data.local.QuranGoalBookmarkEntity
import com.example.data.local.QuranHighlightEntity
import com.example.data.model.TranslationEdition
import com.example.data.model.WordByWordItem
import com.example.data.repository.AyahTranslationComparison
import com.example.data.util.TajweedEngine
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent

@Composable
fun AyahItemCard(
    ayah: AyahEntity,
    arabicFontSize: Float,
    words: List<WordByWordItem>,
    isCurrentlyPlaying: Boolean,
    isPremium: Boolean,
    isTajweedModeEnabled: Boolean = false,
    isDualTranslationEnabled: Boolean = false,
    primaryEdition: TranslationEdition = TranslationEdition.CLEAR_QURAN,
    highlight: QuranHighlightEntity? = null,
    goalBookmark: QuranGoalBookmarkEntity? = null,
    comparisonTranslations: List<AyahTranslationComparison> = emptyList(),
    onPlayAyahAudio: () -> Unit,
    onPauseAyahAudio: () -> Unit,
    onOpenTafsir: () -> Unit,
    onBookmark: () -> Unit,
    onOpenHighlightPalette: () -> Unit = {},
    onOpenSetGoalBookmark: () -> Unit = {},
    onToggleGoalCheckpointCompleted: ((Boolean) -> Unit)? = null,
    onAskAi: () -> Unit,
    onOpenCoinsHub: () -> Unit,
    onWordClick: ((WordByWordItem) -> Unit)? = null,
    onTajweedClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var showWordByWord by remember { mutableStateOf(false) }

    val highlightColorParsed = remember(highlight?.colorHex) {
        highlight?.colorHex?.let {
            try {
                Color(android.graphics.Color.parseColor(it))
            } catch (e: Exception) {
                null
            }
        }
    }

    val cardBgColor = when {
        isCurrentlyPlaying -> EmeraldPrimary.copy(alpha = 0.08f)
        highlightColorParsed != null -> highlightColorParsed.copy(alpha = 0.08f)
        else -> MaterialTheme.colorScheme.surface
    }

    val cardBorder = when {
        isCurrentlyPlaying -> androidx.compose.foundation.BorderStroke(1.5.dp, EmeraldPrimary.copy(alpha = 0.8f))
        highlightColorParsed != null -> androidx.compose.foundation.BorderStroke(1.dp, highlightColorParsed.copy(alpha = 0.5f))
        goalBookmark != null -> androidx.compose.foundation.BorderStroke(1.dp, GoldAccent.copy(alpha = 0.5f))
        else -> androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardBgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = cardBorder,
        modifier = modifier
            .fillMaxWidth()
            .testTag("ayah_${ayah.surahNumber}_${ayah.ayahNumber}")
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            // Optional Left Color Accent Ribbon for Highlight
            if (highlightColorParsed != null) {
                Box(
                    modifier = Modifier
                        .width(5.dp)
                        .fillMaxHeight()
                        .background(highlightColorParsed)
                        .testTag("highlight_ribbon_${ayah.ayahNumber}")
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Goal Checkpoint Header Banner if active
                if (goalBookmark != null) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = GoldAccent.copy(alpha = 0.12f),
                        border = androidx.compose.foundation.BorderStroke(0.5.dp, GoldAccent.copy(alpha = 0.4f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("goal_checkpoint_banner_${ayah.ayahNumber}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Checkbox(
                                    checked = goalBookmark.isCompleted,
                                    onCheckedChange = { onToggleGoalCheckpointCompleted?.invoke(goalBookmark.isCompleted) },
                                    colors = CheckboxDefaults.colors(checkedColor = GoldAccent),
                                    modifier = Modifier
                                        .size(24.dp)
                                        .testTag("goal_checkpoint_check_${ayah.ayahNumber}")
                                )
                                Text(
                                    text = goalBookmark.goalLabel,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        textDecoration = if (goalBookmark.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                                    ),
                                    color = if (goalBookmark.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else GoldAccent
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = MaterialTheme.colorScheme.surface
                            ) {
                                Text(
                                    text = "${goalBookmark.targetMinutes}m Target",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.SemiBold),
                                    color = EmeraldPrimary,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }

                // Highlight Category Pill if highlighted
                if (highlight != null && highlightColorParsed != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = highlightColorParsed.copy(alpha = 0.18f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(highlightColorParsed)
                                )
                                Text(
                                    text = "${highlight.colorName} • ${highlight.categoryLabel}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    ),
                                    color = highlightColorParsed
                                )
                            }
                        }

                        TextButton(
                            onClick = onOpenHighlightPalette,
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 1.dp),
                            modifier = Modifier.testTag("edit_highlight_btn_${ayah.ayahNumber}")
                        ) {
                            Text("Edit Note", fontSize = 11.sp, color = highlightColorParsed)
                        }
                    }
                }

                // Verse Action Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Verse Number Pill
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(if (isCurrentlyPlaying) EmeraldPrimary else MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${ayah.ayahNumber}",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isCurrentlyPlaying) Color.White else MaterialTheme.colorScheme.primary
                                )
                            )
                        }

                        // Play/Pause Verse Audio Button
                        IconButton(
                            onClick = {
                                if (isCurrentlyPlaying) onPauseAyahAudio() else onPlayAyahAudio()
                            },
                            modifier = Modifier.size(34.dp).testTag("play_ayah_${ayah.ayahNumber}_btn")
                        ) {
                            if (isCurrentlyPlaying) {
                                Icon(
                                    imageVector = Icons.Default.GraphicEq,
                                    contentDescription = "Pause Ayah Audio",
                                    tint = EmeraldPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.PlayCircleOutline,
                                    contentDescription = "Play Ayah Audio",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        // Word by Word Toggle
                        TextButton(
                            onClick = { showWordByWord = !showWordByWord },
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                            modifier = Modifier.testTag("wbw_toggle_${ayah.ayahNumber}")
                        ) {
                            Text(
                                text = if (showWordByWord) "Hide WbW" else "WbW",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (showWordByWord) EmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Tafsir Button
                        TextButton(
                            onClick = onOpenTafsir,
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("tafsir_btn_${ayah.ayahNumber}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.MenuBook,
                                contentDescription = "Tafsir",
                                tint = GoldAccent,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                "Tafsir",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = GoldAccent
                                )
                            )
                        }

                        // Highlight / Color Palette Action
                        IconButton(
                            onClick = onOpenHighlightPalette,
                            modifier = Modifier.size(32.dp).testTag("highlight_ayah_${ayah.ayahNumber}")
                        ) {
                            Icon(
                                imageVector = if (highlight != null) Icons.Default.FormatPaint else Icons.Outlined.FormatPaint,
                                contentDescription = "Highlight Verse",
                                tint = highlightColorParsed ?: MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Daily Goal Checkpoint Action
                        IconButton(
                            onClick = onOpenSetGoalBookmark,
                            modifier = Modifier.size(32.dp).testTag("set_goal_ayah_${ayah.ayahNumber}")
                        ) {
                            Icon(
                                imageVector = if (goalBookmark != null) Icons.Default.Flag else Icons.Outlined.Flag,
                                contentDescription = "Set Goal Checkpoint",
                                tint = if (goalBookmark != null) GoldAccent else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Bookmark Action
                        IconButton(
                            onClick = onBookmark,
                            modifier = Modifier.size(32.dp).testTag("bookmark_ayah_${ayah.ayahNumber}")
                        ) {
                            Icon(
                                imageVector = if (ayah.isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                                contentDescription = "Bookmark",
                                tint = if (ayah.isBookmarked) GoldAccent else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Tajweed Rule Inspector Button (when Tajweed mode is on)
                        if (isTajweedModeEnabled) {
                            IconButton(
                                onClick = { onTajweedClick?.invoke() },
                                modifier = Modifier.size(32.dp).testTag("tajweed_rule_ayah_btn_${ayah.ayahNumber}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Palette,
                                    contentDescription = "Tajweed Rules",
                                    tint = EmeraldPrimary,
                                    modifier = Modifier.size(17.dp)
                                )
                            }
                        }
                    }
                }

                // Arabic Verse with dynamic font scaling and Tajweed annotations
                val annotatedArabic = remember(ayah.arabicText, isTajweedModeEnabled) {
                    TajweedEngine.annotateTajweedText(ayah.arabicText, isTajweedModeEnabled)
                }
                Text(
                    text = annotatedArabic,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontSize = arabicFontSize.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Right,
                        lineHeight = (arabicFontSize * 1.6f).sp
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onTajweedClick?.invoke() }
                        .testTag("arabic_text_${ayah.ayahNumber}")
                )

                // Word-by-Word Breakdown Section
                AnimatedVisibility(visible = showWordByWord && words.isNotEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Quran.com Root & Morphology • Tap word for analysis",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = EmeraldPrimary
                                )
                                Icon(
                                    imageVector = Icons.Default.VolumeUp,
                                    contentDescription = null,
                                    tint = EmeraldPrimary,
                                    modifier = Modifier.size(14.dp)
                                )
                            }

                            LazyRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(words) { word ->
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = MaterialTheme.colorScheme.surface,
                                        border = androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
                                        modifier = Modifier
                                            .clickable { onWordClick?.invoke(word) }
                                            .testTag("wbw_chip_${ayah.ayahNumber}_${word.arabic}")
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = word.arabic,
                                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                                color = EmeraldPrimary
                                            )
                                            Text(
                                                text = word.transliteration,
                                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            Text(
                                                text = word.translation,
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.SemiBold
                                                ),
                                                color = MaterialTheme.colorScheme.onSurface,
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                if (ayah.transliteration.isNotBlank()) {
                    Text(
                        text = ayah.transliteration,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            fontSize = 12.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f))

                // Primary Translation Section
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                        ) {
                            Text(
                                text = "${primaryEdition.name} (${primaryEdition.language})",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp),
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Text(
                        text = ayah.translation,
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp, lineHeight = 20.sp),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.testTag("translation_text_${ayah.ayahNumber}")
                    )

                    // User Highlight Note if attached
                    if (highlight != null && highlight.note.isNotBlank() && highlightColorParsed != null) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = highlightColorParsed.copy(alpha = 0.12f),
                            border = androidx.compose.foundation.BorderStroke(0.5.dp, highlightColorParsed.copy(alpha = 0.35f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("highlight_note_card_${ayah.ayahNumber}")
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(Icons.Default.Notes, contentDescription = null, tint = highlightColorParsed, modifier = Modifier.size(14.dp))
                                Text(
                                    text = "“${highlight.note}”",
                                    style = MaterialTheme.typography.bodySmall.copy(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    // Stacked Comparison Translations (when enabled or comparisons selected)
                    if (isDualTranslationEnabled && comparisonTranslations.isNotEmpty()) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("dual_translation_stack_${ayah.ayahNumber}")
                        ) {
                            comparisonTranslations.forEach { comparison ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                                    border = androidx.compose.foundation.BorderStroke(0.5.dp, EmeraldPrimary.copy(alpha = 0.25f)),
                                    modifier = Modifier.fillMaxWidth().testTag("comparison_item_${ayah.ayahNumber}_${comparison.edition.id}")
                                ) {
                                    Column(
                                        modifier = Modifier.padding(8.dp),
                                        verticalArrangement = Arrangement.spacedBy(3.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.CompareArrows,
                                                contentDescription = null,
                                                tint = EmeraldPrimary,
                                                modifier = Modifier.size(13.dp)
                                            )
                                            Text(
                                                text = "${comparison.edition.name} • ${comparison.edition.author}",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = EmeraldPrimary
                                                )
                                            )
                                        }
                                        Text(
                                            text = comparison.translationText,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontSize = 12.sp,
                                                lineHeight = 17.sp,
                                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        )
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
