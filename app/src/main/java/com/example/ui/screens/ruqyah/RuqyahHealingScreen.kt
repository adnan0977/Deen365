package com.example.ui.screens.ruqyah

import androidx.activity.compose.BackHandler
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.data.model.*
import com.example.ui.components.RuqyahAudioPlayerCard
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldLight
import com.example.ui.viewmodel.DeenUiState
import com.example.ui.viewmodel.DeenViewModel
import com.example.ui.viewmodel.MainTab

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RuqyahHealingScreen(
    state: DeenUiState,
    viewModel: DeenViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.selectTab(MainTab.DASHBOARD)
    }

    val ruqyah = state.ruqyahState
    var showGuideDialog by remember { mutableStateOf(false) }

    val filteredVerses = remember(ruqyah.selectedCategory) {
        if (ruqyah.selectedCategory == RuqyahCategory.ALL) {
            RuqyahData.VERSES
        } else {
            RuqyahData.VERSES.filter { it.category == ruqyah.selectedCategory }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                "Ruqyah & Spiritual Healing",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Surface(shape = RoundedCornerShape(6.dp), color = GoldAccent.copy(alpha = 0.2f)) {
                                Text(
                                    text = "Ash-Shifa",
                                    color = GoldAccent,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Ayat ash-Shifa • Protection from Evil Eye & Illness",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.selectTab(MainTab.DASHBOARD) },
                        modifier = Modifier.testTag("ruqyah_back_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showGuideDialog = true },
                        modifier = Modifier.testTag("ruqyah_guide_button")
                    ) {
                        Icon(Icons.Default.HelpOutline, contentDescription = "How to perform Ruqyah", tint = EmeraldPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 14.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Live Ruqyah Shariah Audio Player Streamer
            item {
                RuqyahAudioPlayerCard(
                    player = viewModel.ruqyahAudioPlayer
                )
            }

            // How-to Banner
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.fillMaxWidth().clickable { showGuideDialog = true }.testTag("ruqyah_prophetic_banner")
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Surface(shape = RoundedCornerShape(6.dp), color = GoldAccent.copy(alpha = 0.25f)) {
                                Text(
                                    text = "PROPHETIC SUNNAH METHOD",
                                    color = GoldLight,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Text(
                                text = "How to Perform Authentic Ruqyah",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color.White)
                            )
                            Text(
                                text = "4 steps: Purification, Cupping Hands, Nafath (gentle blowing) & Wiping the body.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }

                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = GoldLight)
                    }
                }
            }

            // Categories Filter Row
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 2.dp)
                ) {
                    item {
                        FilterChip(
                            selected = ruqyah.selectedCategory == RuqyahCategory.ALL,
                            onClick = { viewModel.setRuqyahCategory(RuqyahCategory.ALL) },
                            label = { Text("All Verses") },
                            modifier = Modifier.testTag("ruqyah_cat_all")
                        )
                    }
                    item {
                        FilterChip(
                            selected = ruqyah.selectedCategory == RuqyahCategory.SHIFA_SICKNESS,
                            onClick = { viewModel.setRuqyahCategory(RuqyahCategory.SHIFA_SICKNESS) },
                            label = { Text("Illness & Physical Pain") },
                            modifier = Modifier.testTag("ruqyah_cat_sickness")
                        )
                    }
                    item {
                        FilterChip(
                            selected = ruqyah.selectedCategory == RuqyahCategory.EVIL_EYE_PROTECTION,
                            onClick = { viewModel.setRuqyahCategory(RuqyahCategory.EVIL_EYE_PROTECTION) },
                            label = { Text("Evil Eye (Ayn) & Magic") },
                            modifier = Modifier.testTag("ruqyah_cat_evil_eye")
                        )
                    }
                    item {
                        FilterChip(
                            selected = ruqyah.selectedCategory == RuqyahCategory.ANXIETY_DEPRESSION,
                            onClick = { viewModel.setRuqyahCategory(RuqyahCategory.ANXIETY_DEPRESSION) },
                            label = { Text("Anxiety & Heartache") },
                            modifier = Modifier.testTag("ruqyah_cat_anxiety")
                        )
                    }
                }
            }

            // Verses List
            items(filteredVerses) { verse ->
                val isPlaying = ruqyah.currentlyPlayingVerseId == verse.id
                val count = ruqyah.recitationCounters.getOrDefault(verse.id, 0)

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isPlaying) GoldAccent else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("ruqyah_verse_${verse.id}")
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Title & Audio Action Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = verse.title,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = verse.reference,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = EmeraldPrimary
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                // Audio Pronounce Button
                                IconButton(
                                    onClick = { viewModel.playRuqyahAudio(verse) },
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(if (isPlaying) GoldAccent else EmeraldPrimary.copy(alpha = 0.15f))
                                        .testTag("play_ruqyah_${verse.id}")
                                ) {
                                    Icon(
                                        imageVector = if (isPlaying) Icons.Default.GraphicEq else Icons.Default.VolumeUp,
                                        contentDescription = "Recite with Audio",
                                        tint = if (isPlaying) Color.Black else EmeraldPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                // Recitation Counter Pill Button
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (count >= verse.recommendedCount) EmeraldPrimary else MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable { viewModel.incrementRuqyahCounter(verse.id) }
                                        .testTag("count_ruqyah_${verse.id}")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (count >= verse.recommendedCount) Icons.Default.Check else Icons.Default.TouchApp,
                                            contentDescription = null,
                                            tint = if (count >= verse.recommendedCount) Color.White else MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Text(
                                            text = "$count / ${verse.recommendedCount}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (count >= verse.recommendedCount) Color.White else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }

                        // Arabic Typography
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = verse.arabic,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    lineHeight = 36.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                textAlign = TextAlign.End,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(14.dp)
                            )
                        }

                        // Transliteration
                        Text(
                            text = verse.transliteration,
                            style = MaterialTheme.typography.bodyMedium.copy(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        // Translation
                        Text(
                            text = verse.translation,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

                        // Hadith Virtue
                        Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("✨", fontSize = 12.sp)
                            Text(
                                text = verse.propheticBenefit,
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }

    // Prophetic Instructions Dialog
    if (showGuideDialog) {
        AlertDialog(
            onDismissRequest = { showGuideDialog = false },
            title = {
                Text(
                    "Authentic Prophetic Ruqyah Method",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = EmeraldPrimary)
                )
            },
            text = {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(RuqyahData.PROPHETIC_STEPS) { step ->
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Surface(shape = CircleShape, color = EmeraldPrimary, modifier = Modifier.size(24.dp)) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text("${step.stepNumber}", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                Text(step.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                            }
                            Text(step.instruction, style = MaterialTheme.typography.bodySmall)
                            Text(step.hadithProof, style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showGuideDialog = false }) {
                    Text("Understood")
                }
            }
        )
    }
}
