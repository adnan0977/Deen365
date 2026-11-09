package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.ui.util.adaptiveFocusable
import com.example.ui.util.rememberDeviceScreenProfile
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import com.example.ui.components.LearnDuaDialog
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.HadithEntity
import com.example.data.seed.DuaItem
import com.example.data.seed.DuaSeedData
import com.example.data.seed.HadithSeedData
import com.example.data.seed.TasbihPreset
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldLight
import com.example.ui.viewmodel.DeenUiState
import com.example.ui.viewmodel.DeenViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HadithAndDuaScreen(
    state: DeenUiState,
    viewModel: DeenViewModel,
    modifier: Modifier = Modifier
) {
    var selectedSection by remember(state.hadithDuaActiveSubTab) { mutableIntStateOf(state.hadithDuaActiveSubTab) }
    var isHadithDrawerOpen by remember { mutableStateOf(false) }

    // Intercept back navigation when browsing Hadith chapters or reader
    BackHandler(enabled = selectedSection == 0 && (state.selectedHadithChapterNumber != null || state.selectedHadithCollectionDetail != null)) {
        if (state.selectedHadithChapterNumber != null) {
            viewModel.selectHadithChapterNumber(null)
        } else if (state.selectedHadithCollectionDetail != null) {
            viewModel.selectHadithCollectionDetail(null)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Only show the header and TabRow when not in the full-screen Hadith module
        if (selectedSection != 0) {
            // Screen Header with Settings Icon on Top Right
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Hadith, Duas & Dhikr",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                    Text(
                        text = "Prophetic traditions, daily fortress & tasbih",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    IconButton(
                        onClick = { viewModel.openGoToHadithDialog() },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .testTag("hadith_goto_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.NearMe,
                            contentDescription = "Go to Hadith",
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = { viewModel.openGharibGlossary() },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .testTag("hadith_glossary_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Translate,
                            contentDescription = "Glossary",
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = { viewModel.openImamBiographies() },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .testTag("hadith_imams_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = "Imams",
                            tint = GoldAccent,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = { viewModel.openKitabBabBrowser() },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .testTag("hadith_kitab_browser_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Checklist,
                            contentDescription = "Kitab & Bab Progress",
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = { viewModel.openSettings() },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .testTag("hadith_screen_settings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Top Sub-Tabs
            TabRow(
                selectedTabIndex = selectedSection,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                Tab(
                    selected = selectedSection == 0,
                    onClick = {
                        selectedSection = 0
                        viewModel.setHadithDuaSubTab(0)
                    },
                    text = { Text("Hadiths", fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                    icon = { Icon(Icons.Default.LibraryBooks, contentDescription = null, modifier = Modifier.size(16.dp)) }
                )
                Tab(
                    selected = selectedSection == 1,
                    onClick = {
                        selectedSection = 1
                        viewModel.setHadithDuaSubTab(1)
                    },
                    text = { Text("Duas & Azkar", fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                    icon = { Icon(Icons.Default.Favorite, contentDescription = null, modifier = Modifier.size(16.dp)) }
                )
                Tab(
                    selected = selectedSection == 2,
                    onClick = {
                        selectedSection = 2
                        viewModel.setHadithDuaSubTab(2)
                    },
                    text = { Text("Namaz Guide", fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                    icon = { Icon(Icons.Default.Accessibility, contentDescription = null, modifier = Modifier.size(16.dp)) }
                )
                Tab(
                    selected = selectedSection == 3,
                    onClick = {
                        selectedSection = 3
                        viewModel.setHadithDuaSubTab(3)
                    },
                    text = { Text("Tasbih", fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                    icon = { Icon(Icons.Default.Fingerprint, contentDescription = null, modifier = Modifier.size(16.dp)) }
                )
            }
        }

        when (selectedSection) {
            0 -> HadithSectionView(
                state = state,
                viewModel = viewModel,
                onOpenDrawer = { isHadithDrawerOpen = true }
            )
            1 -> DuasSectionView(state = state, viewModel = viewModel)
            2 -> NamazGuideView(state = state, viewModel = viewModel)
            3 -> DhikrTrackingScreen(state = state, viewModel = viewModel)
        }
    }

    if (state.selectedLearningDua != null) {
        LearnDuaDialog(
            dua = state.selectedLearningDua!!,
            isPlayingAudio = state.playingAudioId == state.selectedLearningDua!!.id,
            onPlayAudio = {
                viewModel.toggleRecitation(
                    state.selectedLearningDua!!.id,
                    state.selectedLearningDua!!.arabicText,
                    state.selectedLearningDua!!.transliteration
                )
            },
            onAskAi = {
                val duaToAsk = state.selectedLearningDua
                viewModel.openLearnDua(null)
                if (duaToAsk != null) {
                    viewModel.askAiAboutDua(duaToAsk)
                }
            },
            onDismiss = { viewModel.openLearnDua(null) }
        )
    }

    com.example.ui.screens.hadith.GharibHadithGlossaryDialog(
        isOpen = state.isGharibGlossaryOpen,
        searchQuery = state.gharibSearchQuery,
        selectedCategory = state.selectedGharibCategory,
        onSearchQueryChange = { viewModel.setGharibSearchQuery(it) },
        onCategoryChange = { viewModel.setGharibCategory(it) },
        onJumpToHadith = { coll, num -> viewModel.jumpFromGharibToHadith(coll, num) },
        onDismiss = { viewModel.closeGharibGlossary() }
    )

    com.example.ui.screens.hadith.ImamBiographiesDialog(
        isOpen = state.isImamBiographiesOpen,
        onDismiss = { viewModel.closeImamBiographies() }
    )

    com.example.ui.screens.hadith.NarratorBioDialog(
        narrator = state.selectedNarratorDetail,
        onDismiss = { viewModel.closeNarratorDetail() }
    )

    com.example.ui.screens.hadith.GoToHadithDialog(
        isOpen = state.isGoToHadithDialogOpen,
        onJump = { viewModel.jumpToHadithCoordinate(it) },
        onDismiss = { viewModel.closeGoToHadithDialog() }
    )

    com.example.ui.screens.hadith.HadithKitabBrowserDialog(
        isOpen = state.isKitabBabBrowserOpen,
        completedBookIds = state.completedHadithBookIds,
        collectionProgressList = state.collectionProgressList,
        onToggleBookCompleted = { viewModel.toggleHadithBookCompleted(it) },
        onMarkCollectionCompleted = { coll, completed -> viewModel.setCollectionCompleted(coll, completed) },
        onClearAllProgress = { viewModel.clearAllKitabProgress() },
        onSelectBook = { book ->
            viewModel.closeKitabBabBrowser()
            viewModel.setHadithCollectionBrowserFilter(book.collectionId)
        },
        onDismiss = { viewModel.closeKitabBabBrowser() }
    )

    com.example.ui.screens.hadith.MarkHadithReadTutorialDialog(
        isOpen = state.isMarkReadTutorialOpen,
        onDismiss = { viewModel.closeMarkReadTutorial() }
    )

    if (state.isVisualSanadDialogOpen) {
        com.example.ui.screens.hadith.VisualSanadChainDialog(
            chain = state.selectedSanadChain,
            onDismiss = { viewModel.closeVisualSanad() }
        )
    }

    if (state.isComparativeFiqhOpen) {
        com.example.ui.screens.fiqh.ComparativeFiqhDialog(
            initialTopicId = state.selectedComparativeFiqhTopicId,
            onDismiss = { viewModel.closeComparativeFiqh() }
        )
    }

    if (isHadithDrawerOpen) {
        ModalBottomSheet(
            onDismissRequest = { isHadithDrawerOpen = false },
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
                    .navigationBarsPadding(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Islamic Sunnah & Hadith Suite",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            selectedSection = 0
                            viewModel.setHadithDuaSubTab(0)
                            isHadithDrawerOpen = false
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(Icons.Default.LibraryBooks, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text(
                            text = "Hadith Collection (Active)",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            selectedSection = 1
                            viewModel.setHadithDuaSubTab(1)
                            isHadithDrawerOpen = false
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(Icons.Default.Favorite, contentDescription = null, tint = EmeraldPrimary)
                        Text("Duas & Fortress of the Muslim", fontWeight = FontWeight.Medium)
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            selectedSection = 2
                            viewModel.setHadithDuaSubTab(2)
                            isHadithDrawerOpen = false
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(Icons.Default.Accessibility, contentDescription = null, tint = EmeraldPrimary)
                        Text("Namaz & Prayer Guide", fontWeight = FontWeight.Medium)
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            selectedSection = 3
                            viewModel.setHadithDuaSubTab(3)
                            isHadithDrawerOpen = false
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(Icons.Default.Fingerprint, contentDescription = null, tint = GoldAccent)
                        Text("Digital Tasbih Counter", fontWeight = FontWeight.Medium)
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(onClick = {
                        isHadithDrawerOpen = false
                        viewModel.openKitabBabBrowser()
                    }) {
                        Text("Kitab Tracker", color = EmeraldPrimary)
                    }
                    TextButton(onClick = {
                        isHadithDrawerOpen = false
                        viewModel.openGharibGlossary()
                    }) {
                        Text("Gharib Glossary", color = EmeraldPrimary)
                    }
                    TextButton(onClick = {
                        isHadithDrawerOpen = false
                        viewModel.openSettings()
                    }) {
                        Text("Settings", color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}

@Composable
fun HadithSectionView(
    state: DeenUiState,
    viewModel: DeenViewModel,
    onOpenDrawer: () -> Unit
) {
    val allHadithList = if (state.allHadiths.isNotEmpty()) state.allHadiths else HadithSeedData.ALL_HADITHS
    val context = LocalContext.current

    val selectedColl = state.selectedHadithCollectionDetail
    val selectedChapter = state.selectedHadithChapterNumber

    if (selectedChapter != null && selectedColl != null) {
        val chapterHadiths = remember(allHadithList, selectedColl, selectedChapter) {
            val list = allHadithList.filter {
                it.collection.equals(selectedColl, ignoreCase = true) && it.bookNumber == selectedChapter
            }
            if (list.isNotEmpty()) list else {
                HadithSeedData.getFallbackHadiths(selectedColl, selectedChapter)
            }
        }
        val collTitle = when (selectedColl.lowercase()) {
            "bukhari" -> "Sahih Bukhari"
            "muslim" -> "Sahih Muslim"
            "nasai" -> "Sunan an-Nasai"
            "abudawud" -> "Abu Dawood"
            "tirmidhi" -> "Jamiat Tirmidhi"
            "ibnmajah" -> "Sunan Ibn Majah"
            "malik" -> "Muwatta Malik"
            "nawawi" -> "40 Hadith Nawawi"
            else -> selectedColl.replaceFirstChar { it.uppercase() }
        }

        val activeHadiths = if (state.currentHadiths.isNotEmpty()) {
            state.currentHadiths.filter {
                it.collection.equals(selectedColl, ignoreCase = true) && it.bookNumber == selectedChapter
            }.ifEmpty { chapterHadiths }
        } else {
            chapterHadiths
        }

        com.example.ui.screens.hadith.HadithReaderDetailView(
            collectionId = selectedColl,
            collectionTitle = collTitle,
            chapterNumber = selectedChapter,
            hadiths = activeHadiths,
            isLoading = state.isLoadingHadiths,
            onBack = { viewModel.selectHadithChapterNumber(null) },
            onToggleBookmark = { viewModel.toggleFavoriteHadith(it) },
            onToggleRead = { viewModel.toggleHadithStudied(it) },
            onCopyHadith = { hadith ->
                val text = "${hadith.arabicText}\n\n${hadith.translation}\n- [${hadith.collection.uppercase()} #${hadith.hadithNumber}]"
                val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                clipboard.setPrimaryClip(android.content.ClipData.newPlainText("Hadith", text))
                android.widget.Toast.makeText(context, "Hadith copied to clipboard", android.widget.Toast.LENGTH_SHORT).show()
            },
            onShareHadith = { hadith ->
                val sendIntent = android.content.Intent().apply {
                    action = android.content.Intent.ACTION_SEND
                    putExtra(android.content.Intent.EXTRA_TEXT, "${hadith.arabicText}\n\n${hadith.translation}\n- [${hadith.collection.uppercase()} #${hadith.hadithNumber}]")
                    type = "text/plain"
                }
                context.startActivity(android.content.Intent.createChooser(sendIntent, "Share Hadith"))
            },
            onOpenGlossary = { viewModel.openGharibGlossary() },
            onOpenTutorial = { viewModel.openMarkReadTutorial() },
            onOpenSanadChain = { hadith -> viewModel.openVisualSanad(hadith.collection, hadith.hadithNumber) },
            onOpenComparativeFiqh = { viewModel.openComparativeFiqh() }
        )
        return
    }

    if (selectedColl != null) {
        val collTitle = when (selectedColl.lowercase()) {
            "bukhari" -> "Sahih Bukhari"
            "muslim" -> "Sahih Muslim"
            "nasai" -> "Sunan an-Nasai"
            "abudawud" -> "Abu Dawood"
            "tirmidhi" -> "Jamiat Tirmidhi"
            "ibnmajah" -> "Sunan Ibn Majah"
            "malik" -> "Muwatta Malik"
            "nawawi" -> "40 Hadith Nawawi"
            else -> selectedColl.replaceFirstChar { it.uppercase() }
        }

        com.example.ui.screens.hadith.HadithChaptersListView(
            collectionId = selectedColl,
            collectionTitle = collTitle,
            onBack = { viewModel.selectHadithCollectionDetail(null) },
            onSelectChapter = { chapterNum -> viewModel.selectHadithChapterNumber(chapterNum) }
        )
        return
    }

    if (state.hadithSearchQuery.isBlank()) {
        val firstHadith = allHadithList.firstOrNull()
        com.example.ui.screens.hadith.HadithCollectionHubView(
            readingStreak = state.hadithReadingStreak,
            dailyHadithRead = firstHadith?.isRead ?: false,
            onToggleDailyHadithRead = {
                firstHadith?.let { viewModel.toggleHadithStudied(it) }
            },
            onSelectBook = { collId, _ ->
                viewModel.selectHadithCollectionDetail(collId)
            },
            onSelectTopic = { topicQuery ->
                viewModel.setHadithSearchQuery(topicQuery)
                viewModel.selectHadithCollectionDetail("bukhari")
            },
            onOpenGlossary = { viewModel.openGharibGlossary() },
            onOpenKitabTracker = { viewModel.openKitabBabBrowser() },
            onOpenGoToHadith = { viewModel.openGoToHadithDialog() },
            onOpenMenu = onOpenDrawer
        )
        return
    }

    // Filtered hadiths based on search keyword, category, narrator, collection, and bookmarks
    val displayedHadiths = remember(
        allHadithList,
        state.hadithSearchQuery,
        state.selectedHadithCategory,
        state.selectedHadithNarrator,
        state.hadithBrowserCollection,
        state.hadithFavoritesOnly,
        state.hadithStudiedOnly,
        state.hadithMemorizedOnly
    ) {
        allHadithList.filter { h ->
            // 1. Keyword search (filters by translation, narrator, arabicText, chapterName, hadithNumber, or collection)
            val matchesKeyword = if (state.hadithSearchQuery.isBlank()) true else {
                val q = state.hadithSearchQuery.trim()
                h.translation.contains(q, ignoreCase = true) ||
                h.narrator.contains(q, ignoreCase = true) ||
                h.chapterName.contains(q, ignoreCase = true) ||
                h.arabicText.contains(q) ||
                h.hadithNumber.toString() == q ||
                h.collection.contains(q, ignoreCase = true)
            }

            // 2. Category filter
            val matchesCategory = if (state.selectedHadithCategory == "All") true else {
                h.chapterName.contains(state.selectedHadithCategory, ignoreCase = true) ||
                state.selectedHadithCategory.contains(h.chapterName, ignoreCase = true)
            }

            // 3. Narrator filter
            val matchesNarrator = if (state.selectedHadithNarrator == "All") true else {
                h.narrator.contains(state.selectedHadithNarrator, ignoreCase = true)
            }

            // 4. Collection filter
            val matchesCollection = if (state.hadithBrowserCollection == "All") true else {
                h.collection.equals(state.hadithBrowserCollection, ignoreCase = true)
            }

            // 5. Bookmarks / Favorites toggle
            val matchesBookmark = if (!state.hadithFavoritesOnly) true else h.isFavorite

            // 6. Studied / Memorized filters
            val matchesStudied = if (!state.hadithStudiedOnly) true else h.isRead
            val matchesMemorized = if (!state.hadithMemorizedOnly) true else h.isMemorized

            matchesKeyword && matchesCategory && matchesNarrator && matchesCollection && matchesBookmark && matchesStudied && matchesMemorized
        }
    }

    val totalBookmarked = allHadithList.count { it.isFavorite }
    val totalStudied = allHadithList.count { it.isRead }
    val totalMemorized = allHadithList.count { it.isMemorized }

    val hasActiveFilters = state.hadithSearchQuery.isNotBlank() ||
            state.selectedHadithCategory != "All" ||
            state.selectedHadithNarrator != "All" ||
            state.hadithBrowserCollection != "All" ||
            state.hadithFavoritesOnly ||
            state.hadithStudiedOnly ||
            state.hadithMemorizedOnly

    val popularKeywords = listOf(
        "Intentions", "Patience", "Prayer", "Charity", "Fasting",
        "Mercy", "Smile", "Parents", "Knowledge", "Forgiveness"
    )

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 340.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 0. Hadith & Dua AI Companion
        item(span = { GridItemSpan(maxLineSpan) }) {
            com.example.ui.screens.ai.HadithDuaAiCompanionView(
                result = state.hadithDuaAiResult,
                isLoading = state.isLoadingHadithDuaAi,
                onSearch = { query ->
                    viewModel.searchHadithDuaCompanion(query)
                }
            )
        }

        // 0.5. Kitab Completion Tracker Banner (Room Database)
        item(span = { GridItemSpan(maxLineSpan) }) {
            val allBooks = com.example.data.seed.HadithKitabProgressData.PROMINENT_BOOKS
            val completedCount = allBooks.count { state.completedHadithBookIds.contains(it.id) }
            val fraction = if (allBooks.isNotEmpty()) completedCount.toFloat() / allBooks.size.toFloat() else 0f

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.openKitabBabBrowser() }
                    .testTag("hadith_kitab_progress_summary_card")
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.MenuBook, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(18.dp))
                            Text(
                                text = "Kitab Completion Tracker",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = EmeraldPrimary.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "Room DB",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold, color = EmeraldPrimary),
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "$completedCount/${allBooks.size} Chapters (${(fraction * 100).toInt()}%)",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = EmeraldPrimary)
                        )
                    }

                    LinearProgressIndicator(
                        progress = { fraction },
                        color = EmeraldPrimary,
                        trackColor = EmeraldPrimary.copy(alpha = 0.2f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val bukhariDone = allBooks.count { it.collectionId.equals("bukhari", ignoreCase = true) && state.completedHadithBookIds.contains(it.id) }
                        val bukhariTotal = allBooks.count { it.collectionId.equals("bukhari", ignoreCase = true) }
                        val muslimDone = allBooks.count { it.collectionId.equals("muslim", ignoreCase = true) && state.completedHadithBookIds.contains(it.id) }
                        val muslimTotal = allBooks.count { it.collectionId.equals("muslim", ignoreCase = true) }
                        val nawawiDone = allBooks.count { it.collectionId.equals("nawawi", ignoreCase = true) && state.completedHadithBookIds.contains(it.id) }
                        val nawawiTotal = allBooks.count { it.collectionId.equals("nawawi", ignoreCase = true) }

                        Text(
                            text = "Bukhari: $bukhariDone/$bukhariTotal • Muslim: $muslimDone/$muslimTotal • Nawawi: $nawawiDone/$nawawiTotal",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Manage →",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = EmeraldPrimary)
                        )
                    }
                }
            }
        }

        // 1. Search Bar with Live Clear and Filter Icon
        item(span = { GridItemSpan(maxLineSpan) }) {
            OutlinedTextField(
                value = state.hadithSearchQuery,
                onValueChange = { viewModel.setHadithSearchQuery(it) },
                placeholder = { Text("Search hadiths, narrators, topics, keywords...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = EmeraldPrimary) },
                trailingIcon = {
                    if (state.hadithSearchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setHadithSearchQuery("") }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear Search")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = EmeraldPrimary,
                    cursorColor = EmeraldPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("hadith_search_input")
            )
        }

        // 2. Quick Keyword Suggestion Chips
        item(span = { GridItemSpan(maxLineSpan) }) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Quick Topics & Keywords",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(popularKeywords) { kw ->
                        val isSelected = state.hadithSearchQuery.equals(kw, ignoreCase = true)
                        SuggestionChip(
                            onClick = {
                                if (isSelected) viewModel.setHadithSearchQuery("")
                                else viewModel.setHadithSearchQuery(kw)
                            },
                            label = { Text(kw, style = MaterialTheme.typography.labelSmall) },
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = if (isSelected) EmeraldPrimary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                labelColor = if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.onSurface
                            ),
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary) else null,
                            modifier = Modifier.testTag("keyword_chip_${kw.lowercase()}")
                        )
                    }
                }
            }
        }

        // 3. User Bookmarks & Learning Progress Filter Bar
        item(span = { GridItemSpan(maxLineSpan) }) {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Bookmarking Toggle for User Favorites
                item {
                    FilterChip(
                        selected = state.hadithFavoritesOnly,
                        onClick = { viewModel.toggleHadithFavoritesOnly() },
                        label = {
                            Text(
                                text = "Bookmarked ($totalBookmarked)",
                                fontWeight = if (state.hadithFavoritesOnly) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 12.sp
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = if (state.hadithFavoritesOnly) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
                                contentDescription = "User Bookmarks",
                                tint = if (state.hadithFavoritesOnly) GoldAccent else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = GoldAccent.copy(alpha = 0.22f),
                            selectedLabelColor = GoldAccent
                        ),
                        border = if (state.hadithFavoritesOnly) androidx.compose.foundation.BorderStroke(1.dp, GoldAccent) else null,
                        modifier = Modifier.testTag("bookmark_filter_chip")
                    )
                }

                // Studied Filter
                item {
                    FilterChip(
                        selected = state.hadithStudiedOnly,
                        onClick = { viewModel.setHadithStudiedFilter(!state.hadithStudiedOnly) },
                        label = { Text("Studied ($totalStudied)", fontSize = 12.sp) },
                        leadingIcon = {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(14.dp))
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EmeraldPrimary.copy(alpha = 0.18f),
                            selectedLabelColor = EmeraldPrimary
                        ),
                        modifier = Modifier.testTag("studied_filter_chip")
                    )
                }

                // Memorized Filter
                item {
                    FilterChip(
                        selected = state.hadithMemorizedOnly,
                        onClick = { viewModel.setHadithMemorizedFilter(!state.hadithMemorizedOnly) },
                        label = { Text("Memorized ($totalMemorized)", fontSize = 12.sp) },
                        leadingIcon = {
                            Icon(Icons.Default.Star, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(14.dp))
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = GoldAccent.copy(alpha = 0.22f),
                            selectedLabelColor = GoldAccent
                        ),
                        modifier = Modifier.testTag("memorized_filter_chip")
                    )
                }

                // Gharib al-Hadith Glossary Button
                item {
                    FilledTonalButton(
                        onClick = { viewModel.openGharibGlossary() },
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = GoldAccent.copy(alpha = 0.18f),
                            contentColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier
                            .height(32.dp)
                            .testTag("quick_gharib_glossary_btn")
                    ) {
                        Icon(Icons.Default.AutoStories, contentDescription = null, modifier = Modifier.size(14.dp), tint = GoldAccent)
                        Spacer(Modifier.width(4.dp))
                        Text("Gharib Glossary", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // 4. Category / Topic Filters
        item(span = { GridItemSpan(maxLineSpan) }) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Default.Category, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(16.dp))
                    Text(
                        text = "Filter by Category / Topic",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(state.hadithBrowserCategories) { cat ->
                        val isSelected = state.selectedHadithCategory == cat
                        val count = if (cat == "All") allHadithList.size else allHadithList.count { it.chapterName.contains(cat, ignoreCase = true) || cat.contains(it.chapterName, ignoreCase = true) }
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.setHadithCategoryFilter(cat) },
                            label = {
                                Text(
                                    text = "$cat ($count)",
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = EmeraldPrimary,
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier.testTag("category_chip_${cat.replace(" ", "_")}")
                        )
                    }
                }
            }
        }

        // 5. Narrator Filters
        item(span = { GridItemSpan(maxLineSpan) }) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(16.dp))
                    Text(
                        text = "Filter by Narrator (Companion)",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(state.hadithBrowserNarrators) { narrator ->
                        val isSelected = state.selectedHadithNarrator == narrator
                        val count = if (narrator == "All") allHadithList.size else allHadithList.count { it.narrator.contains(narrator, ignoreCase = true) }
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.setHadithNarratorFilter(narrator) },
                            label = {
                                Text(
                                    text = "$narrator ($count)",
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = GoldAccent,
                                selectedLabelColor = Color.Black
                            ),
                            modifier = Modifier.testTag("narrator_chip_${narrator.replace(" ", "_")}")
                        )
                    }
                }
            }
        }

        // 6. Collection Selector
        item(span = { GridItemSpan(maxLineSpan) }) {
            val collections = listOf(
                "All" to "All Collections",
                "bukhari" to "Sahih al-Bukhari",
                "muslim" to "Sahih Muslim",
                "nawawi" to "40 Hadith Nawawi",
                "tirmidhi" to "Jami` at-Tirmidhi",
                "abudawud" to "Sunan Abi Dawud",
                "nasai" to "Sunan an-Nasa'i"
            )

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Default.MenuBook, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(16.dp))
                    Text(
                        text = "Collection",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(collections) { (id, name) ->
                        val isSelected = state.hadithBrowserCollection == id
                        AssistChip(
                            onClick = { viewModel.setHadithCollectionBrowserFilter(id) },
                            label = { Text(name, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = if (isSelected) EmeraldPrimary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant
                            ),
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary) else null,
                            modifier = Modifier.testTag("collection_chip_$id")
                        )
                    }
                }
            }
        }

        // 7. Active Filters Indicator & Results Count
        item(span = { GridItemSpan(maxLineSpan) }) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Showing ${displayedHadiths.size} of ${allHadithList.size} Hadiths",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (hasActiveFilters) {
                    TextButton(
                        onClick = { viewModel.clearHadithFilters() },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.testTag("clear_hadith_filters_btn")
                    ) {
                        Icon(Icons.Default.FilterAltOff, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.Red)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Clear All Filters", style = MaterialTheme.typography.labelSmall.copy(color = Color.Red, fontWeight = FontWeight.Bold))
                    }
                }
            }
        }

        // 8. Hadiths Grid / List
        if (state.isLoadingHadiths && displayedHadiths.isEmpty()) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Box(modifier = Modifier.fillMaxWidth().padding(48.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = EmeraldPrimary)
                }
            }
        } else if (displayedHadiths.isEmpty()) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier.size(56.dp).clip(CircleShape).background(EmeraldPrimary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.SearchOff, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(30.dp))
                        }
                        Text(
                            text = "No Hadiths Found",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (state.hadithSearchQuery.isNotBlank())
                                "No results match query \"${state.hadithSearchQuery}\" with current filters."
                            else if (state.hadithFavoritesOnly)
                                "You haven't bookmarked any Hadiths yet. Tap the bookmark icon on any Hadith card to save your favorites!"
                            else
                                "No Hadiths found for the selected category or narrator.",
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Button(
                            onClick = { viewModel.clearHadithFilters() },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Reset All Filters")
                        }
                    }
                }
            }
        } else {
            items(displayedHadiths) { hadith ->
                HadithCard(
                    hadith = hadith,
                    isPremium = state.coinsState.isPremium,
                    onBookmark = { viewModel.toggleFavoriteHadith(hadith) },
                    onToggleStudied = { viewModel.toggleHadithStudied(hadith) },
                    onToggleMemorized = { viewModel.toggleHadithMemorized(hadith) },
                    onAskAi = { viewModel.askAiAboutHadith(hadith) },
                    onOpenCoinsHub = { viewModel.openCoinsHub() }
                )
            }
        }
    }
}

@Composable
fun HadithCard(
    hadith: HadithEntity,
    isPremium: Boolean = true,
    onBookmark: () -> Unit,
    onToggleStudied: () -> Unit,
    onToggleMemorized: () -> Unit,
    onAskAi: () -> Unit,
    onOpenCoinsHub: () -> Unit = {}
) {
    val context = LocalContext.current

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (hadith.isFavorite) GoldAccent.copy(alpha = 0.7f)
            else if (hadith.isMemorized) GoldAccent.copy(alpha = 0.5f)
            else if (hadith.isRead) EmeraldPrimary.copy(alpha = 0.5f)
            else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .adaptiveFocusable(shape = RoundedCornerShape(18.dp))
            .testTag("hadith_${hadith.collection}_${hadith.hadithNumber}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Collection & Hadith #, Category Badge, Bookmark and Share
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
                        shape = RoundedCornerShape(8.dp),
                        color = GoldAccent.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "${hadith.collection.uppercase()} #${hadith.hadithNumber}",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    if (hadith.chapterName.isNotBlank()) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = EmeraldPrimary.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = hadith.chapterName,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold, fontSize = 10.sp),
                                color = EmeraldPrimary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                // Top Actions: Bookmarking Toggle for User Favorites + Share
                Row(horizontalArrangement = Arrangement.spacedBy(2.dp), verticalAlignment = Alignment.CenterVertically) {
                    // Bookmarking toggle button
                    IconButton(
                        onClick = onBookmark,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("bookmark_hadith_${hadith.collection}_${hadith.hadithNumber}")
                    ) {
                        Icon(
                            imageVector = if (hadith.isFavorite) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = if (hadith.isFavorite) "Remove Bookmark" else "Bookmark Hadith",
                            tint = if (hadith.isFavorite) GoldAccent else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Share button
                    IconButton(
                        onClick = {
                            val shareContent = "📜 Hadith • ${hadith.collection.uppercase()} #${hadith.hadithNumber} (${hadith.chapterName})\n\n${hadith.arabicText}\n\nNarrated by: ${hadith.narrator}\n\"${hadith.translation}\"\n\nGrade: ${hadith.grade}\nShared via Deen365"
                            val sendIntent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(android.content.Intent.EXTRA_TEXT, shareContent)
                            }
                            context.startActivity(android.content.Intent.createChooser(sendIntent, "Share Hadith"))
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("share_hadith_${hadith.collection}_${hadith.hadithNumber}")
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "Share Hadith", tint = EmeraldPrimary, modifier = Modifier.size(17.dp))
                    }
                }
            }

            // Arabic Text
            if (hadith.arabicText.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = hadith.arabicText,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Right,
                            lineHeight = 32.sp
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            // Narrator Row with Icon
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(Icons.Default.Person, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(14.dp))
                Text(
                    text = hadith.narrator,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = EmeraldPrimary
                )
                Text(
                    text = "• ${hadith.grade}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(6.dp))

            // Translation (Gated behind Premium)
            if (isPremium) {
                Text(
                    text = hadith.translation,
                    style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
                    color = MaterialTheme.colorScheme.onSurface
                )
            } else {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = GoldAccent.copy(alpha = 0.12f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent.copy(alpha = 0.35f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenCoinsHub() }
                        .testTag("locked_hadith_translation_${hadith.collection}_${hadith.hadithNumber}")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(16.dp))
                        Text(
                            text = "Translation locked for PRO • Tap to unlock with Deen Coins",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = GoldAccent),
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "Unlock",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, color = EmeraldPrimary)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Bottom Learning Actions Row: Studied, Memorized, Reflect AI
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    // Mark as Studied
                    FilledTonalButton(
                        onClick = onToggleStudied,
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = if (hadith.isRead) EmeraldPrimary.copy(alpha = 0.18f) else MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (hadith.isRead) EmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.testTag("toggle_studied_${hadith.hadithNumber}")
                    ) {
                        Icon(
                            imageVector = if (hadith.isRead) Icons.Default.CheckCircle else Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (hadith.isRead) "Studied (+24)" else "Mark Read", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    // Mark as Memorized
                    FilledTonalButton(
                        onClick = onToggleMemorized,
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = if (hadith.isMemorized) GoldAccent.copy(alpha = 0.22f) else MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (hadith.isMemorized) GoldAccent else MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.testTag("toggle_memorized_${hadith.hadithNumber}")
                    ) {
                        Icon(
                            imageVector = if (hadith.isMemorized) Icons.Default.Star else Icons.Outlined.StarBorder,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (hadith.isMemorized) "Memorized" else "Memorize", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                TextButton(
                    onClick = onAskAi,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(14.dp), tint = GoldAccent)
                    Spacer(modifier = Modifier.width(2.dp))
                    Text("Reflect", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                }
            }
        }
    }
}

@Composable
fun DuasSectionView(
    state: DeenUiState,
    viewModel: DeenViewModel
) {
    com.example.ui.screens.dua.DuaLibraryScreen(
        state = state,
        viewModel = viewModel
    )
}


@Composable
fun TasbihSectionView(
    state: DeenUiState,
    viewModel: DeenViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Dhikr Presets
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Select Dhikr",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(state.tasbihPresets) { preset ->
                    val isSelected = state.selectedTasbih.id == preset.id
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.selectTasbihPreset(preset) },
                        label = { Text(preset.transliteration) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = state.selectedTasbih.arabic,
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            )
            Text(
                text = state.selectedTasbih.meaning,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Big Interactive Tap Bead Circle
        Box(
            modifier = Modifier
                .size(240.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(GoldLight.copy(alpha = 0.35f), EmeraldPrimary)
                    )
                )
                .border(4.dp, GoldAccent, CircleShape)
                .clickable { viewModel.incrementTasbih() }
                .testTag("tasbih_tap_button"),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${state.tasbihCount}",
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                )
                Text(
                    text = "/ ${state.selectedTasbih.targetCount}",
                    style = MaterialTheme.typography.titleMedium,
                    color = GoldLight
                )
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.Black.copy(alpha = 0.25f)
                ) {
                    Text(
                        text = "Lap: ${state.tasbihLap} • Total: ${state.totalTasbihCount}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "TAP TO COUNT",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = GoldLight
                )
            }
        }

        // Bottom Reset & Total Row
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 80.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                onClick = { viewModel.resetTasbih() },
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Reset")
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.DoneAll, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Total Session: ${state.totalTasbihCount}",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}
