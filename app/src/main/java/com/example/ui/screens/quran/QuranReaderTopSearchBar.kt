package com.example.ui.screens.quran

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.seed.SurahMeta
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent

@Composable
fun QuranReaderTopSearchBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    currentSurah: SurahMeta,
    matchedSurahs: List<SurahMeta>,
    matchedAyahCount: Int,
    totalAyahCount: Int,
    onSelectSurah: (SurahMeta) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        shadowElevation = 2.dp,
        modifier = modifier
            .fillMaxWidth()
            .testTag("quran_reader_search_bar")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Top Search Input Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(40.dp)
                        .testTag("quran_reader_top_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to Surah List",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("quran_reader_search_input"),
                    placeholder = {
                        Text(
                            text = "Search in Surah or jump to any Surah...",
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotBlank()) {
                            IconButton(
                                onClick = { onSearchQueryChange("") },
                                modifier = Modifier
                                    .size(36.dp)
                                    .testTag("quran_reader_clear_search_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear search",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EmeraldPrimary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f),
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)
                    )
                )
            }

            // Suggestions / Search Results Sub-row
            AnimatedVisibility(
                visible = searchQuery.isNotBlank(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // In-Surah Match Count Banner
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (matchedAyahCount > 0) EmeraldPrimary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f),
                            modifier = Modifier.testTag("search_match_count_badge")
                        ) {
                            Text(
                                text = if (matchedAyahCount > 0) {
                                    "Surah ${currentSurah.englishName}: $matchedAyahCount of $totalAyahCount verses match"
                                } else {
                                    "No matching verses in Surah ${currentSurah.englishName}"
                                },
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                ),
                                color = if (matchedAyahCount > 0) EmeraldPrimary else MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }

                        TextButton(
                            onClick = { onSearchQueryChange("") },
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Text(
                                text = "Show All",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = EmeraldPrimary
                            )
                        }
                    }

                    // Matching Surahs quick jump chips
                    val otherMatchingSurahs = matchedSurahs.filter { it.number != currentSurah.number }
                    if (otherMatchingSurahs.isNotEmpty()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.MenuBook,
                                contentDescription = null,
                                tint = GoldAccent,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "Jump to Surah:",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(otherMatchingSurahs, key = { it.number }) { matchingSurah ->
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = GoldAccent.copy(alpha = 0.15f),
                                        border = androidx.compose.foundation.BorderStroke(0.5.dp, GoldAccent.copy(alpha = 0.5f)),
                                        onClick = { onSelectSurah(matchingSurah) },
                                        modifier = Modifier.testTag("surah_suggestion_chip_${matchingSurah.number}")
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Text(
                                                text = "${matchingSurah.number}. ${matchingSurah.englishName}",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp),
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = matchingSurah.arabicName,
                                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                                color = GoldAccent
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
}
