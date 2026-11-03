package com.example.ui.screens.quran

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Search
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.MorphologySegmentType
import com.example.data.model.QuranExplorerVerse
import com.example.data.model.QuranExplorerWord
import com.example.data.seed.QuranExplorerSeedData
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranExplorerView(
    initialVerseKey: String = "1:1",
    onBack: () -> Unit,
    onPlayWordAudio: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedVerseKey by remember { mutableStateOf(initialVerseKey) }
    var selectedWordForDetail by remember { mutableStateOf<QuranExplorerWord?>(null) }
    var activeTab by remember { mutableIntStateOf(0) } // 0: Word Matrix, 1: Root Analysis, 2: API Schema
    var rootFilter by remember { mutableStateOf<String?>(null) }

    val currentVerse = remember(selectedVerseKey) {
        QuranExplorerSeedData.getVerse(selectedVerseKey)
    }

    val displayedWords = remember(currentVerse, rootFilter) {
        if (rootFilter == null) currentVerse.words
        else currentVerse.words.filter { it.rootArabic == rootFilter || it.root == rootFilter }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top App Bar
        TopAppBar(
            title = {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "Quran Explorer",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = GoldAccent.copy(alpha = 0.25f)
                        ) {
                            Text(
                                text = "Quran.com API v4",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldLight
                                ),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text(
                        text = "Word-by-Word Morphology & Root Concordance",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
                    )
                }
            },
            navigationIcon = {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("quran_explorer_back_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            },
            actions = {
                IconButton(
                    onClick = { onPlayWordAudio(currentVerse.textUthmani) },
                    modifier = Modifier.testTag("play_explorer_verse_audio_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = "Recite Verse",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = EmeraldPrimary,
                titleContentColor = MaterialTheme.colorScheme.onPrimary,
                navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
            )
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(top = 14.dp, bottom = 96.dp)
        ) {
            // Verse Key Selector Quick-Chips
            item {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Select Verse for Morphological Breakdown:",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val sampleVerses = listOf("1:1", "1:2", "1:5", "2:255", "112:1")
                        items(sampleVerses) { key ->
                            val isSelected = selectedVerseKey == key
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    selectedVerseKey = key
                                    rootFilter = null
                                },
                                label = {
                                    val label = when (key) {
                                        "1:1" -> "1:1 Al-Fatiha"
                                        "1:2" -> "1:2 Al-Fatiha"
                                        "1:5" -> "1:5 Al-Fatiha"
                                        "2:255" -> "2:255 Ayat al-Kursi"
                                        "112:1" -> "112:1 Al-Ikhlas"
                                        else -> key
                                    }
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                        )
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = EmeraldPrimary.copy(alpha = 0.15f),
                                    selectedLabelColor = EmeraldPrimary
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = isSelected,
                                    borderColor = if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.outlineVariant
                                ),
                                modifier = Modifier.testTag("explorer_verse_chip_$key")
                            )
                        }
                    }
                }
            }

            // Hero Card: Verse Calligraphy & English Meaning
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.35f)),
                    modifier = Modifier.fillMaxWidth().testTag("explorer_hero_card")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Metadata Bar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = EmeraldPrimary.copy(alpha = 0.12f)
                                ) {
                                    Text(
                                        text = "Verse ${currentVerse.verseKey}",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = EmeraldPrimary
                                        ),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                                Text(
                                    text = "Surah ${currentVerse.surahName}",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant
                                ) {
                                    Text(
                                        text = "${currentVerse.totalWordsCount} Words",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = GoldAccent.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = "${currentVerse.uniqueRootsCount} Roots",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        ),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        // Prominent Arabic Uthmani Calligraphy
                        Text(
                            text = currentVerse.textUthmani,
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontSize = 28.sp,
                                lineHeight = 46.sp,
                                fontWeight = FontWeight.Normal
                            ),
                            textAlign = TextAlign.Right,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.fillMaxWidth().testTag("explorer_arabic_text")
                        )

                        // English Translation
                        Text(
                            text = currentVerse.translationText,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 14.5.sp,
                                lineHeight = 21.sp,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Mode Selector Tabs (Word Matrix vs Root Concordance vs Quran.com API Schema)
            item {
                TabRow(
                    selectedTabIndex = activeTab,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = EmeraldPrimary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .testTag("explorer_mode_tabs")
                ) {
                    Tab(
                        selected = activeTab == 0,
                        onClick = { activeTab = 0 },
                        text = { Text("Word Matrix", fontSize = 11.5.sp, fontWeight = FontWeight.Bold) },
                        icon = { Icon(Icons.Default.GridView, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    )
                    Tab(
                        selected = activeTab == 1,
                        onClick = { activeTab = 1 },
                        text = { Text("Root Concordance", fontSize = 11.5.sp, fontWeight = FontWeight.Bold) },
                        icon = { Icon(Icons.Outlined.Explore, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    )
                    Tab(
                        selected = activeTab == 2,
                        onClick = { activeTab = 2 },
                        text = { Text("API v4 Schema", fontSize = 11.5.sp, fontWeight = FontWeight.Bold) },
                        icon = { Icon(Icons.Outlined.Code, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    )
                }
            }

            // Tab 0: Word Matrix (Cards)
            if (activeTab == 0) {
                item {
                    Text(
                        text = "Word-by-Word Tokens (Tap word to inspect morphology):",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                items(displayedWords) { word ->
                    WordExplorerCard(
                        word = word,
                        onWordClick = { selectedWordForDetail = word },
                        onPlayAudio = { onPlayWordAudio(word.textUthmani) }
                    )
                }
            }

            // Tab 1: Root Concordance
            if (activeTab == 1) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Roots Occurring in Verse ${currentVerse.verseKey}:",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        val verseRoots = remember(currentVerse) {
                            currentVerse.words.mapNotNull {
                                if (it.root.isNotBlank()) it.root to it.rootArabic else null
                            }.distinctBy { it.second }
                        }

                        verseRoots.forEach { (rootLetters, rootArabic) ->
                            val rootEntry = QuranExplorerSeedData.POPULAR_ROOTS.firstOrNull { it.rootArabic == rootArabic }
                            val wordsSharingRoot = currentVerse.words.filter { it.rootArabic == rootArabic }

                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.3f)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("root_card_$rootArabic")
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = EmeraldPrimary
                                        ) {
                                            Text(
                                                text = "جذر: $rootLetters",
                                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                                color = Color.White,
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                            )
                                        }

                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = GoldAccent.copy(alpha = 0.15f),
                                            border = androidx.compose.foundation.BorderStroke(0.5.dp, GoldAccent.copy(alpha = 0.5f))
                                        ) {
                                            Text(
                                                text = "${rootEntry?.totalOccurrencesInQuran ?: 42}x in Quran",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                color = MaterialTheme.colorScheme.onSurface,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                    }

                                    if (rootEntry != null) {
                                        Text(
                                            text = "Root Meaning: ${rootEntry.meaningEnglish}",
                                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }

                                    // Words in this verse derived from this root
                                    Text(
                                        text = "Words in this verse (${wordsSharingRoot.size}):",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold
                                        ),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        wordsSharingRoot.forEach { w ->
                                            Surface(
                                                shape = RoundedCornerShape(8.dp),
                                                color = MaterialTheme.colorScheme.surfaceVariant,
                                                modifier = Modifier.clickable { selectedWordForDetail = w }
                                            ) {
                                                Text(
                                                    text = "${w.textUthmani} (${w.translation})",
                                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                                    color = EmeraldPrimary,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
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

            // Tab 2: Raw Quran.com API v4 Schema
            if (activeTab == 2) {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                        border = androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.fillMaxWidth().testTag("quran_api_schema_card")
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Code,
                                    contentDescription = null,
                                    tint = EmeraldPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Quran.com API v4 Response Format",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = EmeraldPrimary
                                )
                            }

                            Text(
                                text = "GET /api/v4/verses/by_key/${currentVerse.verseKey}?words=true&word_fields=text_uthmani,root,part_of_speech,lemma",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.5.sp,
                                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)

                            // Render JSON-like syntax block
                            val jsonSample = buildString {
                                appendLine("{\n  \"verse\": {\n    \"id\": ${currentVerse.ayahNumber},\n    \"verse_key\": \"${currentVerse.verseKey}\",\n    \"text_uthmani\": \"${currentVerse.textUthmani}\",\n    \"words\": [")
                                currentVerse.words.forEachIndexed { idx, w ->
                                    appendLine("      {\n        \"id\": ${w.id},\n        \"position\": ${w.position},\n        \"text_uthmani\": \"${w.textUthmani}\",\n        \"transliteration\": \"${w.transliteration}\",\n        \"translation\": \"${w.translation}\",\n        \"root\": \"${w.rootArabic}\",\n        \"lemma\": \"${w.lemma}\",\n        \"part_of_speech\": \"${w.partOfSpeechCode}\"\n      }${if (idx < currentVerse.words.size - 1) "," else ""}")
                                }
                                appendLine("    ]\n  }\n}")
                            }

                            Text(
                                text = jsonSample,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 11.sp,
                                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                    lineHeight = 16.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }

    // Detail Bottom Sheet for Selected Word
    if (selectedWordForDetail != null) {
        WordMorphologyDetailSheet(
            word = selectedWordForDetail!!,
            onDismiss = { selectedWordForDetail = null },
            onPlayAudio = { onPlayWordAudio(selectedWordForDetail!!.textUthmani) }
        )
    }
}

@Composable
private fun WordExplorerCard(
    word: QuranExplorerWord,
    onWordClick: () -> Unit,
    onPlayAudio: () -> Unit
) {
    val posBadgeColor = when (word.partOfSpeechCode) {
        "PN" -> Color(0xFF1E88E5) // Blue for Proper Noun
        "N" -> Color(0xFF2E7D32)  // Green for Noun
        "V" -> Color(0xFFD81B60)  // Pink/Ruby for Verb
        "P", "P+N", "P+PN" -> Color(0xFFFB8C00) // Amber for Preposition/Particle
        "PRON" -> Color(0xFF00ACC1) // Teal for Pronoun
        else -> MaterialTheme.colorScheme.primary
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onWordClick() }
            .testTag("explorer_word_card_${word.position}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Position Number & Translation details
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "#${word.position}",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = word.translation,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = word.transliteration,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Part of speech badge
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = posBadgeColor.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = "${word.partOfSpeechCode}: ${word.partOfSpeechArabic}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    color = posBadgeColor
                                ),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        // Root badge
                        if (word.root.isNotBlank()) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = GoldAccent.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "جذر: ${word.root}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    ),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Right: Arabic Calligraphy & Audio Pronounce Button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = word.textUthmani,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = EmeraldPrimary,
                    modifier = Modifier.testTag("word_uthmani_${word.position}")
                )

                IconButton(
                    onClick = onPlayAudio,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(EmeraldPrimary.copy(alpha = 0.1f))
                        .testTag("play_word_${word.position}")
                ) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = "Pronounce Word",
                        tint = EmeraldPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun WordMorphologyDetailSheet(
    word: QuranExplorerWord,
    onDismiss: () -> Unit,
    onPlayAudio: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.85f)
                .testTag("word_morphology_sheet")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Top Header
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
                            shape = CircleShape,
                            color = EmeraldPrimary.copy(alpha = 0.12f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.MenuBook,
                                    contentDescription = null,
                                    tint = EmeraldPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Column {
                            Text(
                                text = "Morphology Breakdown",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Position #${word.position} in Verse ${word.verseKey}",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_morphology_sheet_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                HorizontalDivider(
                    thickness = 0.5.dp,
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                    modifier = Modifier.padding(vertical = 12.dp)
                )

                // Scrollable Content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Big Word Banner
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = EmeraldPrimary.copy(alpha = 0.08f)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = word.textUthmani,
                                style = MaterialTheme.typography.headlineLarge.copy(
                                    fontSize = 36.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = EmeraldPrimary,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "${word.transliteration} • “${word.translation}”",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            FilledTonalButton(
                                onClick = onPlayAudio,
                                modifier = Modifier.padding(top = 4.dp).testTag("sheet_pronounce_btn")
                            ) {
                                Icon(Icons.Default.VolumeUp, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Play Audio Pronunciation")
                            }
                        }
                    }

                    // Morphological Segments (Prefix, Stem, Suffix)
                    if (word.morphologySegments.isNotEmpty()) {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "Quranic Arabic Corpus Segments (الأجزاء الصرفية):",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = EmeraldPrimary
                                )

                                word.morphologySegments.forEach { seg ->
                                    val segColor = when (seg.segmentType) {
                                        MorphologySegmentType.PREFIX -> Color(0xFFFB8C00)
                                        MorphologySegmentType.STEM -> EmeraldPrimary
                                        MorphologySegmentType.SUFFIX -> Color(0xFF1E88E5)
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = MaterialTheme.colorScheme.surface,
                                        border = androidx.compose.foundation.BorderStroke(0.5.dp, segColor.copy(alpha = 0.5f)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(10.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                                Surface(
                                                    shape = RoundedCornerShape(4.dp),
                                                    color = segColor.copy(alpha = 0.15f)
                                                ) {
                                                    Text(
                                                        text = seg.segmentType.displayName,
                                                        style = MaterialTheme.typography.labelSmall.copy(
                                                            fontSize = 9.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = segColor
                                                        ),
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                    )
                                                }
                                                Text(
                                                    text = seg.roleName,
                                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                                Text(
                                                    text = seg.grammaticalDescription,
                                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }

                                            Text(
                                                text = seg.arabicText,
                                                style = MaterialTheme.typography.headlineSmall.copy(
                                                    fontSize = 22.sp,
                                                    fontWeight = FontWeight.Bold
                                                ),
                                                color = segColor
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Root Analysis Card
                    if (word.root.isNotBlank()) {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "Root & Lemma Analysis (الجذر والمعجم):",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = EmeraldPrimary
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = EmeraldPrimary
                                    ) {
                                        Text(
                                            text = "Root: ${word.root}",
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                        )
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = GoldAccent.copy(alpha = 0.15f),
                                        border = androidx.compose.foundation.BorderStroke(0.5.dp, GoldAccent)
                                    ) {
                                        Text(
                                            text = "${word.rootFrequency}x in Holy Quran",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                        )
                                    }
                                }

                                if (word.lemma.isNotBlank()) {
                                    Text(
                                        text = "Lemma / Base Dictionary Entry: ${word.lemma}",
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                if (word.relatedRoots.isNotEmpty()) {
                                    Text(
                                        text = "Related Quranic Derivatives: ${word.relatedRoots.joinToString(" • ")}",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontSize = 11.5.sp,
                                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                        ),
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }

                    // I'rab Grammatical Case Explanation
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "Grammatical Case & I'rab (الإعراب والتوجيه النحوي):",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = EmeraldPrimary
                            )

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.surface
                            ) {
                                Text(
                                    text = word.grammaticalCase,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = EmeraldPrimary
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }

                            Text(
                                text = word.irabExplanation,
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.5.sp, lineHeight = 18.sp),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}
