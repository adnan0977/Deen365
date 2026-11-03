package com.example.ui.screens.heritage

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.seed.CaliphProfile
import com.example.data.seed.CaliphsAndSahabaSeedData
import com.example.data.seed.ProphetStory
import com.example.data.seed.ProphetsSeedData
import com.example.data.seed.SahabiProfile
import com.example.data.seed.IslamicHistoryEra
import com.example.domain.SearchCorpusType
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.viewmodel.DeenUiState
import com.example.ui.viewmodel.DeenViewModel
import com.example.ui.viewmodel.MainTab

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IslamicHeritageScreen(
    state: DeenUiState,
    viewModel: DeenViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        if (state.selectedProphetStory != null) {
            viewModel.closeProphetStory()
        } else if (state.selectedCaliphProfile != null) {
            viewModel.closeCaliphProfile()
        } else if (state.selectedSahabiProfile != null) {
            viewModel.closeSahabiProfile()
        } else {
            viewModel.setTab(MainTab.DASHBOARD)
        }
    }

    if (state.selectedProphetStory != null) {
        ProphetStoryDetailView(
            prophet = state.selectedProphetStory,
            onBack = { viewModel.closeProphetStory() },
            modifier = modifier
        )
        return
    }

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Prophets, 1: Caliphs, 2: Sahaba, 3: Eras

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Islamic Heritage",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Alim.org Encyclopedia & Chronicles",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.setTab(MainTab.DASHBOARD) },
                        modifier = Modifier.testTag("heritage_back_button")
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.openAlimThematicIndex() },
                        modifier = Modifier.testTag("heritage_thematic_index_button")
                    ) {
                        Icon(Icons.Default.AutoStories, contentDescription = "Thematic Index", tint = EmeraldPrimary)
                    }

                    IconButton(
                        onClick = { viewModel.openComparativeFiqh() },
                        modifier = Modifier.testTag("heritage_comparative_fiqh_button")
                    ) {
                        Icon(Icons.Default.Balance, contentDescription = "Comparative Fiqh", tint = GoldAccent)
                    }

                    IconButton(
                        onClick = { viewModel.openUnifiedSearch() },
                        modifier = Modifier.testTag("heritage_open_search_button")
                    ) {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = EmeraldPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Tab Row
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = EmeraldPrimary,
                modifier = Modifier.fillMaxWidth().testTag("heritage_tab_row")
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Prophets", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Default.AutoStories, contentDescription = null, modifier = Modifier.size(16.dp)) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("4 Caliphs", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Default.AccountBalance, contentDescription = null, modifier = Modifier.size(16.dp)) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Sahaba", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Default.People, contentDescription = null, modifier = Modifier.size(16.dp)) }
                )
                Tab(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    text = { Text("History Eras", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Default.HistoryEdu, contentDescription = null, modifier = Modifier.size(16.dp)) }
                )
            }

            when (selectedTab) {
                0 -> ProphetsListView(onSelect = { viewModel.openProphetStory(it) })
                1 -> CaliphsListView()
                2 -> SahabaListView()
                3 -> ErasTimelineView()
            }
        }
    }

    // Unified Cross-Corpus Search Dialog
    UnifiedCrossSearchDialog(
        isOpen = state.isUnifiedSearchOpen,
        query = state.unifiedSearchQuery,
        results = state.unifiedSearchResults,
        activeFilter = state.unifiedSearchFilter,
        onQueryChange = { viewModel.updateUnifiedSearchQuery(it) },
        onFilterChange = { viewModel.setUnifiedSearchFilter(it) },
        onSelectResult = { result ->
            viewModel.closeUnifiedSearch()
            if (result.referenceId.startsWith("prophet_")) {
                val pId = result.referenceId.removePrefix("prophet_").toIntOrNull()
                val match = ProphetsSeedData.ALL_PROPHETS.firstOrNull { it.id == pId }
                if (match != null) viewModel.openProphetStory(match)
            } else if (result.referenceId.startsWith("book_")) {
                val bId = result.referenceId.removePrefix("book_")
                val book = state.islamicBooksList.firstOrNull { it.id == bId }
                if (book != null) {
                    viewModel.openBook(book)
                    viewModel.setTab(MainTab.ISLAMIC_BOOKS)
                }
            } else if (result.corpusType == SearchCorpusType.HADITH) {
                viewModel.setTab(MainTab.HADITH_DUAS)
            }
        },
        onDismiss = { viewModel.closeUnifiedSearch() }
    )

    AlimThematicIndexDialog(
        isOpen = state.isAlimThematicIndexOpen,
        onDismiss = { viewModel.closeAlimThematicIndex() },
        onJumpToQuranCoordinate = { coordinate ->
            viewModel.closeAlimThematicIndex()
            viewModel.jumpToVerseCoordinate(coordinate)
            viewModel.setTab(MainTab.QURAN)
        }
    )

    ComparativeFiqhDialog(
        isOpen = state.isComparativeFiqhOpen,
        onDismiss = { viewModel.closeComparativeFiqh() }
    )
}

@Composable
private fun ProphetsListView(onSelect: (ProphetStory) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 48.dp)
    ) {
        items(ProphetsSeedData.ALL_PROPHETS) { prophet ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .clickable { onSelect(prophet) }
                    .testTag("prophet_card_${prophet.id}")
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = EmeraldPrimary.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "#${prophet.id}",
                                    color = EmeraldPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = prophet.nameEnglish,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = prophet.title,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = prophet.summary,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = prophet.nameArabic,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = EmeraldPrimary
                            )
                        )
                        Spacer(Modifier.height(4.dp))
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = EmeraldPrimary)
                    }
                }
            }
        }
    }
}

@Composable
private fun CaliphsListView() {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 48.dp)
    ) {
        items(CaliphsAndSahabaSeedData.RIGHTLY_GUIDED_CALIPHS) { caliph ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth().testTag("caliph_card_${caliph.id}")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = caliph.nameEnglish,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = caliph.title,
                                style = MaterialTheme.typography.bodySmall,
                                color = EmeraldPrimary
                            )
                        }
                        Text(
                            text = caliph.nameArabic,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = EmeraldPrimary
                            )
                        )
                    }

                    Spacer(Modifier.height(6.dp))

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = "Reign: ${caliph.reignYears} | Lifespan: ${caliph.lifespan}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = "Famous Inscription:",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "\"${caliph.famousSpeechQuote}\"",
                        style = MaterialTheme.typography.bodySmall.copy(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = "Major Achievements:",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    caliph.majorAchievements.forEach { ach ->
                        Text(
                            text = "• $ach",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SahabaListView() {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 48.dp)
    ) {
        items(CaliphsAndSahabaSeedData.ASHARA_AND_SAHABA) { sahabi ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth().testTag("sahabi_card_${sahabi.id}")
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = sahabi.nameEnglish,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = sahabi.honorific,
                                style = MaterialTheme.typography.labelSmall,
                                color = GoldAccent
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = EmeraldPrimary.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = sahabi.category,
                                color = EmeraldPrimary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(6.dp))

                    Text(
                        text = sahabi.virtuesAndRole,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = "\"${sahabi.famousQuote}\"",
                        style = MaterialTheme.typography.labelSmall.copy(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun ErasTimelineView() {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 48.dp)
    ) {
        items(CaliphsAndSahabaSeedData.ISLAMIC_ERAS) { era ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth().testTag("era_card_${era.id}")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = era.name,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = EmeraldPrimary
                        ) {
                            Text(
                                text = era.period,
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = "Historical Capital: ${era.capital}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(Modifier.height(6.dp))

                    Text(
                        text = era.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = "Key Epoch Milestones:",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = EmeraldPrimary
                    )
                    era.keyMilestones.forEach { m ->
                        Text(
                            text = "• $m",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
