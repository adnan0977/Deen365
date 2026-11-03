package com.example.ui.screens.quran

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.example.data.local.AyahEntity
import com.example.data.model.QuranReadingMode
import com.example.data.repository.TafsirRepository
import com.example.data.seed.HizbQuarterData
import com.example.data.seed.RubAlHizbMarker
import com.example.data.seed.SurahMeta
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldLight
import com.example.ui.viewmodel.DeenUiState
import com.example.ui.viewmodel.DeenViewModel

@Composable
fun SurahReaderView(
    surah: SurahMeta,
    ayahs: List<AyahEntity>,
    isLoading: Boolean,
    state: DeenUiState,
    viewModel: DeenViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    // Daily Reading Time Tracker
    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(1000)
            viewModel.incrementReadingTime(1)
        }
    }

    val isPlayingSurah = state.currentPlayingSurah == surah.number && state.isQuranAudioPlaying

    var searchQuery by remember { mutableStateOf("") }

    val filteredAyahs = remember(ayahs, searchQuery) {
        if (searchQuery.isBlank()) ayahs
        else {
            val q = searchQuery.trim()
            ayahs.filter { ayah ->
                ayah.translation.contains(q, ignoreCase = true) ||
                ayah.arabicText.contains(q) ||
                ayah.transliteration.contains(q, ignoreCase = true) ||
                ayah.ayahNumber.toString() == q
            }
        }
    }

    val matchedSurahs = remember(state.surahList, searchQuery) {
        if (searchQuery.isBlank()) emptyList()
        else {
            val q = searchQuery.trim()
            state.surahList.filter { s ->
                s.englishName.contains(q, ignoreCase = true) ||
                s.englishTranslation.contains(q, ignoreCase = true) ||
                s.arabicName.contains(q) ||
                s.number.toString() == q
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Pinned Top Search Bar on Quran Reading Screen
        QuranReaderTopSearchBar(
            searchQuery = searchQuery,
            onSearchQueryChange = { searchQuery = it },
            currentSurah = surah,
            matchedSurahs = matchedSurahs,
            matchedAyahCount = filteredAyahs.size,
            totalAyahCount = ayahs.size,
            onSelectSurah = { selectedSurah ->
                searchQuery = ""
                viewModel.openSurah(selectedSurah)
            },
            onBack = onBack
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            if (state.quranReadingMode == QuranReadingMode.MUSHAF_PAGE) {
                QuranMushafView(
                    surah = surah,
                    ayahs = filteredAyahs,
                    arabicFontSizeSp = state.arabicFontSizeSp,
                    currentPlayingAyahNumber = state.currentPlayingAyahNumber,
                    isQuranAudioPlaying = state.isQuranAudioPlaying,
                    onPlayAyahAudio = { ayah ->
                        viewModel.playSingleAyahAudio(ayah)
                    }
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 120.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header Card
                item {
                    SurahHeaderCard(
                        surah = surah,
                        isPlayingSurah = isPlayingSurah,
                        isTajweedModeEnabled = state.isTajweedModeEnabled,
                        isDualTranslationEnabled = state.isDualTranslationEnabled,
                        onBack = onBack,
                        onToggleFullSurahAudio = {
                            if (isPlayingSurah) {
                                viewModel.pauseQuranAudio()
                            } else {
                                viewModel.playSurahAudio(surah.number, 1)
                            }
                        },
                        onToggleTajweed = { viewModel.toggleTajweedMode() },
                        onToggleDualTranslation = { viewModel.toggleDualTranslation() },
                        onOpenMultiTranslations = { viewModel.openMultiTranslationDialog() },
                        onOpenGoalsHub = { viewModel.openGoalBookmarksDialog() },
                        onOpenReciterPicker = { viewModel.openReciterPicker() },
                        onOpenSettings = { viewModel.openQuranDisplaySettings() },
                        onOpenAsbabAlNuzul = { viewModel.openAsbabAlNuzul(surah.number) },
                        onOpenQuranExplorer = { viewModel.openQuranExplorer("${surah.number}:1") }
                    )
                }

                // Daily Goal Progress Tracker Card in Reader
                item {
                    val readingMins = state.todayReadingSeconds / 60
                    val goalFraction = (readingMins.toFloat() / state.dailyQuranGoalMinutes.coerceAtLeast(1).toFloat()).coerceIn(0f, 1f)
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                        border = androidx.compose.foundation.BorderStroke(0.5.dp, EmeraldPrimary.copy(alpha = 0.3f)),
                        onClick = { viewModel.openGoalBookmarksDialog() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("surah_daily_goal_banner")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = if (state.isGoalCompletedToday) Icons.Default.CheckCircle else Icons.Default.Timer,
                                    contentDescription = null,
                                    tint = if (state.isGoalCompletedToday) GoldAccent else EmeraldPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Column {
                                    Text(
                                        text = "Daily Goal: $readingMins / ${state.dailyQuranGoalMinutes} mins read",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "${state.quranStreakDays} Day Streak 🔥 • Tap to view goals & checkpoints",
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
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (state.isGoalCompletedToday) GoldAccent.copy(alpha = 0.15f) else EmeraldPrimary.copy(alpha = 0.12f)
                                ) {
                                    Text(
                                        text = if (state.isGoalCompletedToday) "Completed!" else "${(goalFraction * 100).toInt()}%",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp,
                                            color = if (state.isGoalCompletedToday) GoldAccent else EmeraldPrimary
                                        ),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }

                if (isLoading && ayahs.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                                Text(
                                    text = "Loading Surah ayahs...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                } else if (filteredAyahs.isEmpty() && ayahs.isNotEmpty()) {
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp)
                                .testTag("no_matching_verses_card")
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SearchOff,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(36.dp)
                                )
                                Text(
                                    text = "No Verses Found in Surah ${surah.englishName}",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "No verses match \"$searchQuery\" in this Surah. Try searching other keywords or choose a matching Surah from the suggestions above.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                                Button(
                                    onClick = { searchQuery = "" },
                                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                                    modifier = Modifier.testTag("clear_filter_btn")
                                ) {
                                    Text("Clear Search & View All Verses")
                                }
                            }
                        }
                    }
                } else {
                    items(filteredAyahs, key = { "${it.surahNumber}_${it.ayahNumber}" }) { ayah ->
                        val marker = remember(ayah.surahNumber, ayah.ayahNumber) {
                            HizbQuarterData.getMarkerForAyah(ayah.surahNumber, ayah.ayahNumber)
                        }
                        if (marker != null) {
                            RubAlHizbMarkerCard(marker = marker)
                        }

                        val words = remember(ayah.ayahNumber) {
                            TafsirRepository.getWordByWordForAyah(ayah.arabicText, ayah.translation)
                        }

                        val isPlayingThisAyah = state.currentPlayingAyahNumber == ayah.ayahNumber && state.isQuranAudioPlaying

                        val highlight = remember(ayah.surahNumber, ayah.ayahNumber, state.quranHighlights) {
                            state.quranHighlights.firstOrNull { it.surahNumber == ayah.surahNumber && it.ayahNumber == ayah.ayahNumber }
                        }

                        val goalBookmark = remember(ayah.surahNumber, ayah.ayahNumber, state.quranGoalBookmarks) {
                            state.quranGoalBookmarks.firstOrNull { it.surahNumber == ayah.surahNumber && it.ayahNumber == ayah.ayahNumber }
                        }

                        val comparisonTranslations = remember(ayah.surahNumber, ayah.ayahNumber, state.comparisonTranslationIds, ayah.translation) {
                            val compareIds = state.comparisonTranslationIds.filter { it != state.selectedTranslationEdition.id }.toSet()
                            com.example.data.repository.QuranMultiTranslationProvider.getTranslationsForAyah(
                                ayah.surahNumber,
                                ayah.ayahNumber,
                                compareIds,
                                ayah.translation
                            )
                        }

                        AyahItemCard(
                            ayah = ayah,
                            arabicFontSize = state.arabicFontSizeSp.toFloat(),
                            words = words,
                            isCurrentlyPlaying = isPlayingThisAyah,
                            isPremium = state.coinsState.isPremium,
                            isTajweedModeEnabled = state.isTajweedModeEnabled,
                            isDualTranslationEnabled = state.isDualTranslationEnabled,
                            primaryEdition = state.selectedTranslationEdition,
                            highlight = highlight,
                            goalBookmark = goalBookmark,
                            comparisonTranslations = comparisonTranslations,
                            onPlayAyahAudio = {
                                viewModel.playSingleAyahAudio(ayah)
                            },
                            onPauseAyahAudio = {
                                viewModel.pauseQuranAudio()
                            },
                            onOpenTafsir = {
                                viewModel.openTafsir(ayah)
                            },
                            onBookmark = {
                                viewModel.toggleBookmarkAyah(ayah)
                            },
                            onOpenHighlightPalette = {
                                viewModel.openHighlightPalette(ayah)
                            },
                            onOpenSetGoalBookmark = {
                                viewModel.openSetGoalBookmarkDialog(ayah)
                            },
                            onToggleGoalCheckpointCompleted = { isCompleted ->
                                goalBookmark?.let { viewModel.toggleGoalBookmarkCompletion(it.id, isCompleted) }
                            },
                            onAskAi = {
                                viewModel.askAiAboutAyah(ayah)
                            },
                            onOpenCoinsHub = {
                                viewModel.openCoinsHub()
                            },
                            onWordClick = { word ->
                                viewModel.openWordGrammar(word.arabic, word.transliteration)
                            },
                            onTajweedClick = {
                                viewModel.openTajweedRulePopup(null, ayah)
                            }
                        )
                    }

                    // Completion Reward Card
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth().testTag("quran_complete_chapter_card")
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = GoldAccent)
                                    Text(
                                        "Completed Surah ${surah.englishName}?",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                }
                                Text(
                                    "Reward: 24 Deen Coins (+24 Hours Pro Access)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = EmeraldPrimary,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Button(
                                    onClick = { viewModel.rewardQuranChapterCompleted(surah.englishName) },
                                    colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = Color.Black),
                                    modifier = Modifier.fillMaxWidth().testTag("mark_surah_read_btn")
                                ) {
                                    Icon(Icons.Default.MonetizationOn, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(Modifier.width(6.dp))
                                    Text("Claim 24 Deen Coins (+1 Day Pro)", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Floating Bottom Audio Controller Dock
        QuranAudioPlayerDock(
            currentSurah = state.currentPlayingSurah,
            currentAyah = state.currentPlayingAyahNumber,
            isPlaying = state.isQuranAudioPlaying,
            playbackSpeed = state.quranPlaybackSpeed,
            selectedReciter = state.selectedReciter,
            totalAyahs = surah.numberOfAyahs,
            onTogglePlayPause = {
                viewModel.togglePlayPauseQuranAudio()
            },
            onNextAyah = { viewModel.nextAyahAudio() },
            onPrevAyah = { viewModel.previousAyahAudio() },
            onStopAudio = { viewModel.stopQuranAudio() },
            onSpeedCycle = {
                val nextSpeed = when (state.quranPlaybackSpeed) {
                    1.0f -> 1.25f
                    1.25f -> 1.5f
                    else -> 1.0f
                }
                viewModel.setQuranPlaybackSpeed(nextSpeed)
            },
            onOpenReciterPicker = { viewModel.openReciterPicker() },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

    // Bottom Sheets
    if (state.isAyahExplainerOpen && state.activeExplainingAyah != null) {
        com.example.ui.screens.ai.QuranAyahExplainerSheet(
            isOpen = true,
            ayah = state.activeExplainingAyah,
            surahName = surah.englishName,
            explanation = state.ayahExplanationResult,
            isLoading = state.isLoadingAyahExplanation,
            onDismiss = { viewModel.closeAyahExplainer() }
        )
    }

    if (state.isTafsirOpen) {
        TafsirBottomSheet(
            tafsir = state.activeTafsirData,
            ayah = state.activeTafsirAyah,
            surahName = surah.englishName,
            onDismiss = { viewModel.closeTafsir() }
        )
    }

    if (state.isReciterPickerOpen) {
        ReciterSelectionSheet(
            selectedReciter = state.selectedReciter,
            onSelectReciter = { viewModel.setReciter(it) },
            onDismiss = { viewModel.closeReciterPicker() }
        )
    }

    if (state.isQuranDisplaySettingsOpen) {
        QuranSettingsSheet(
            arabicFontSizeSp = state.arabicFontSizeSp,
            onFontSizeChange = { viewModel.setArabicFontSize(it) },
            currentEdition = state.selectedTranslationEdition,
            onEditionChange = { viewModel.setTranslationEdition(it) },
            currentReadingMode = state.quranReadingMode,
            onReadingModeChange = { viewModel.setQuranReadingMode(it) },
            onDismiss = { viewModel.closeQuranDisplaySettings() }
        )
    }

    if (state.isWordGrammarDialogOpen) {
        WordGrammarModalDialog(
            detail = state.selectedWordGrammarDetail,
            onDismiss = { viewModel.closeWordGrammar() },
            onPlayWordAudio = { arabic -> viewModel.playWordAudio(arabic, "") }
        )
    }

    if (state.isAsbabAlNuzulDialogOpen) {
        com.example.ui.screens.heritage.AsbabAlNuzulDialog(
            item = state.selectedAsbabAlNuzul,
            onDismiss = { viewModel.closeAsbabAlNuzul() }
        )
    }
}

@Composable
private fun SurahHeaderCard(
    surah: SurahMeta,
    isPlayingSurah: Boolean,
    isTajweedModeEnabled: Boolean,
    isDualTranslationEnabled: Boolean,
    onBack: () -> Unit,
    onToggleFullSurahAudio: () -> Unit,
    onToggleTajweed: () -> Unit,
    onToggleDualTranslation: () -> Unit,
    onOpenMultiTranslations: () -> Unit,
    onOpenGoalsHub: () -> Unit,
    onOpenReciterPicker: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenAsbabAlNuzul: () -> Unit = {},
    onOpenQuranExplorer: () -> Unit = {}
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth().testTag("surah_reader_header")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("surah_reader_back_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Multi-Translations Button
                    IconButton(
                        onClick = onOpenMultiTranslations,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(androidx.compose.foundation.shape.CircleShape)
                            .background(Color.White.copy(alpha = 0.18f))
                            .testTag("surah_multi_translation_header_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Translate,
                            contentDescription = "Translations",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Goal & Study Hub Button
                    IconButton(
                        onClick = onOpenGoalsHub,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(androidx.compose.foundation.shape.CircleShape)
                            .background(GoldAccent.copy(alpha = 0.22f))
                            .testTag("surah_goals_hub_header_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoStories,
                            contentDescription = "Daily Goals Hub",
                            tint = GoldLight,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Quick Tajweed Layer Toggle
                    IconButton(
                        onClick = onToggleTajweed,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(androidx.compose.foundation.shape.CircleShape)
                            .background(if (isTajweedModeEnabled) GoldAccent else Color.White.copy(alpha = 0.18f))
                            .testTag("surah_tajweed_quick_toggle_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = "Toggle Tajweed",
                            tint = if (isTajweedModeEnabled) Color.Black else Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Dual Translation Toggle
                    IconButton(
                        onClick = onToggleDualTranslation,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(androidx.compose.foundation.shape.CircleShape)
                            .background(if (isDualTranslationEnabled) GoldAccent else Color.White.copy(alpha = 0.18f))
                            .testTag("surah_dual_translation_toggle_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.CompareArrows,
                            contentDescription = "Toggle Dual Translation",
                            tint = if (isDualTranslationEnabled) Color.Black else Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Asbab al-Nuzul (Occasions of Revelation) Button
                    IconButton(
                        onClick = onOpenAsbabAlNuzul,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(androidx.compose.foundation.shape.CircleShape)
                            .background(Color.White.copy(alpha = 0.18f))
                            .testTag("surah_asbab_nuzul_header_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.HistoryEdu,
                            contentDescription = "Asbab al-Nuzul (Occasion of Revelation)",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Quran Explorer Button
                    IconButton(
                        onClick = onOpenQuranExplorer,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(androidx.compose.foundation.shape.CircleShape)
                            .background(Color.White.copy(alpha = 0.18f))
                            .testTag("surah_explorer_header_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Explore,
                            contentDescription = "Quran Explorer (Morphology & Roots)",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = onOpenReciterPicker,
                        modifier = Modifier.testTag("reciter_picker_header_btn")
                    ) {
                        Icon(Icons.Default.RecordVoiceOver, contentDescription = "Reciter", tint = GoldLight)
                    }
                    IconButton(
                        onClick = onOpenSettings,
                        modifier = Modifier.testTag("quran_settings_header_btn")
                    ) {
                        Icon(Icons.Default.Tune, contentDescription = "Settings", tint = Color.White)
                    }
                }
            }

            Text(
                text = surah.arabicName,
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = GoldLight
                )
            )
            Text(
                text = "Surah ${surah.englishName} (${surah.englishTranslation})",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )
            Text(
                text = "${surah.revelationType} • ${surah.numberOfAyahs} Verses",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.8f)
            )

            // Full Surah Audio Recitation Button
            FilledTonalButton(
                onClick = onToggleFullSurahAudio,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = GoldAccent,
                    contentColor = Color.Black
                ),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                modifier = Modifier.testTag("play_full_surah_audio_btn")
            ) {
                Icon(
                    imageVector = if (isPlayingSurah) Icons.Default.Pause else Icons.Default.VolumeUp,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = if (isPlayingSurah) "Pause Surah Audio" else "Play Full Surah Recitation",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }

            Text(
                text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
        }
    }
}

@Composable
private fun RubAlHizbMarkerCard(marker: RubAlHizbMarker) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = GoldAccent.copy(alpha = 0.12f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent.copy(alpha = 0.45f)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("rub_marker_${marker.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "۞",
                    fontSize = 22.sp,
                    color = GoldAccent,
                    fontWeight = FontWeight.Bold
                )
                Column {
                    Text(
                        text = "Hizb ${marker.hizbNumber} • ${marker.quarterType}",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = marker.description,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Text(
                text = marker.arabicQuarterTitle,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = EmeraldPrimary
                )
            )
        }
    }
}

