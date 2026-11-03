package com.example.ui.screens.dua

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.DuaEntity
import com.example.data.seed.DuaCategoryMeta
import com.example.data.seed.DuaSeedData
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.util.adaptiveFocusable
import com.example.ui.viewmodel.DeenUiState
import com.example.ui.viewmodel.DeenViewModel

@Composable
fun DuaLibraryScreen(
    state: DeenUiState,
    viewModel: DeenViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Raw Duas from Room or fallback to Seed
    val rawDuas: List<DuaEntity> = remember(state.duaEntities, state.duasList) {
        if (state.duaEntities.isNotEmpty()) {
            state.duaEntities
        } else {
            DuaSeedData.DUAS.map {
                DuaEntity(
                    id = it.id,
                    title = it.title,
                    category = it.category,
                    arabicText = it.arabicText,
                    transliteration = it.transliteration,
                    translation = it.translation,
                    explanation = it.explanation,
                    reference = it.reference,
                    recommendedCount = it.recommendedCount,
                    isFavorite = false,
                    isMemorized = false,
                    practiceCount = state.duaProgressCounts[it.id] ?: 0,
                    lastPracticedTimestamp = 0L,
                    customFolder = "",
                    userNote = ""
                )
            }
        }
    }

    // Active Custom Folders collected from DB and popular defaults
    val allFolders = remember(rawDuas, state.duaCustomFolders) {
        val fromDuas = rawDuas.map { it.customFolder }.filter { it.isNotBlank() }
        (listOf("All") + (fromDuas + state.duaCustomFolders).distinct()).distinct()
    }

    // Filtered Duas based on search, category, folder, and tab
    val filteredDuas = remember(
        rawDuas,
        state.selectedDuaCategory,
        state.selectedDuaFolder,
        state.duaSearchQuery,
        state.duaFavoritesOnly,
        state.duaMemorizedOnly,
        state.duaViewModeTab
    ) {
        rawDuas.filter { dua ->
            // Search query filter (matches title, translation, transliteration, arabic, reference, category, folder, or note)
            val matchesSearch = if (state.duaSearchQuery.isBlank()) true else {
                val q = state.duaSearchQuery.trim()
                dua.title.contains(q, ignoreCase = true) ||
                dua.translation.contains(q, ignoreCase = true) ||
                dua.transliteration.contains(q, ignoreCase = true) ||
                dua.arabicText.contains(q) ||
                dua.reference.contains(q, ignoreCase = true) ||
                dua.category.contains(q, ignoreCase = true) ||
                dua.customFolder.contains(q, ignoreCase = true) ||
                dua.userNote.contains(q, ignoreCase = true)
            }

            // Tab specific filter
            val matchesTab = when (state.duaViewModeTab) {
                2 -> dua.isFavorite || dua.customFolder.isNotBlank() // Saved & Organized
                else -> true
            }

            // Category filter
            val matchesCategory = if (state.selectedDuaCategory == "All") true else {
                dua.category.equals(state.selectedDuaCategory, ignoreCase = true)
            }

            // Folder filter (for Saved tab or general)
            val matchesFolder = if (state.selectedDuaFolder == "All") true else {
                dua.customFolder.equals(state.selectedDuaFolder, ignoreCase = true)
            }

            // Favorites & Memorized toggles
            val matchesFav = if (!state.duaFavoritesOnly) true else dua.isFavorite
            val matchesMem = if (!state.duaMemorizedOnly) true else dua.isMemorized

            matchesSearch && matchesTab && matchesCategory && matchesFolder && matchesFav && matchesMem
        }
    }

    val totalDuasCount = rawDuas.size
    val bookmarkedCount = rawDuas.count { it.isFavorite }
    val memorizedCount = rawDuas.count { it.isMemorized }
    val organizedInFoldersCount = rawDuas.count { it.customFolder.isNotBlank() }

    val popularSearchKeywords = listOf(
        "Morning", "Anxiety", "Forgiveness", "Healing", "Parents", "Rabbana", "Sleep", "Travel", "Protection", "Food"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // 1. Live Instant Search Bar
        OutlinedTextField(
            value = state.duaSearchQuery,
            onValueChange = { viewModel.setDuaSearchQuery(it) },
            placeholder = { Text("Search invocations, Arabic, English, or reference...") },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Search", tint = EmeraldPrimary)
            },
            trailingIcon = {
                if (state.duaSearchQuery.isNotEmpty()) {
                    IconButton(
                        onClick = { viewModel.setDuaSearchQuery("") },
                        modifier = Modifier.testTag("clear_dua_search_btn")
                    ) {
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
                .testTag("dua_search_field")
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 2. Quick Search Keyword Chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(popularSearchKeywords) { kw ->
                val isSelected = state.duaSearchQuery.equals(kw, ignoreCase = true)
                SuggestionChip(
                    onClick = {
                        if (isSelected) viewModel.setDuaSearchQuery("")
                        else viewModel.setDuaSearchQuery(kw)
                    },
                    label = { Text(kw, style = MaterialTheme.typography.labelSmall) },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = if (isSelected) EmeraldPrimary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        labelColor = if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.onSurface
                    ),
                    border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary) else null,
                    modifier = Modifier.testTag("dua_keyword_chip_${kw.lowercase()}")
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 3. Navigation Modes (Categories Explorer | All Invocations | Saved & Organized)
        TabRow(
            selectedTabIndex = state.duaViewModeTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = EmeraldPrimary
        ) {
            Tab(
                selected = state.duaViewModeTab == 0,
                onClick = { viewModel.setDuaViewModeTab(0) },
                text = { Text("Categories", fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                icon = { Icon(Icons.Default.Category, contentDescription = null, modifier = Modifier.size(16.dp)) },
                modifier = Modifier.testTag("dua_tab_categories")
            )
            Tab(
                selected = state.duaViewModeTab == 1,
                onClick = { viewModel.setDuaViewModeTab(1) },
                text = { Text("All ($totalDuasCount)", fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                icon = { Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(16.dp)) },
                modifier = Modifier.testTag("dua_tab_all")
            )
            Tab(
                selected = state.duaViewModeTab == 2,
                onClick = { viewModel.setDuaViewModeTab(2) },
                text = { Text("Saved ($bookmarkedCount)", fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                icon = { Icon(Icons.Default.Bookmark, contentDescription = null, modifier = Modifier.size(16.dp)) },
                modifier = Modifier.testTag("dua_tab_saved")
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Content Display based on selected tab
        when (state.duaViewModeTab) {
            0 -> {
                // CATEGORIES EXPLORER
                CategoriesExplorerView(
                    categories = DuaSeedData.CATEGORY_METAS,
                    rawDuas = rawDuas,
                    onSelectCategory = { categoryName ->
                        viewModel.openDuaCategory(categoryName)
                    }
                )
            }
            1 -> {
                // ALL INVOCATIONS VIEW
                AllDuasExplorerView(
                    duas = filteredDuas,
                    allCount = totalDuasCount,
                    state = state,
                    viewModel = viewModel
                )
            }
            2 -> {
                // SAVED & ORGANIZED BOOKMARKS VIEW
                SavedAndOrganizedView(
                    savedDuas = filteredDuas,
                    allFolders = allFolders,
                    bookmarkedCount = bookmarkedCount,
                    memorizedCount = memorizedCount,
                    organizedCount = organizedInFoldersCount,
                    state = state,
                    viewModel = viewModel
                )
            }
        }
    }

    // Organize Invocation Dialog
    if (state.selectedDuaForOrganizing != null) {
        val targetDua = state.selectedDuaForOrganizing!!
        DuaOrganizeDialog(
            dua = targetDua,
            existingFolders = state.duaCustomFolders,
            onSave = { folder, note ->
                viewModel.saveDuaCustomFolderAndNote(targetDua.id, folder, note)
                Toast.makeText(context, "Saved to ${if (folder.isBlank()) "My Invocations" else folder}!", Toast.LENGTH_SHORT).show()
            },
            onToggleFavorite = { isFav ->
                viewModel.toggleFavoriteDua(targetDua.id, isFav)
            },
            onDismiss = { viewModel.openDuaOrganizeDialog(null) }
        )
    }
}

// -------------------------------------------------------------
// Sub-View 1: Categories Explorer
// -------------------------------------------------------------
@Composable
private fun CategoriesExplorerView(
    categories: List<DuaCategoryMeta>,
    rawDuas: List<DuaEntity>,
    onSelectCategory: (String) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 160.dp),
        modifier = Modifier
            .fillMaxSize()
            .testTag("dua_categories_grid"),
        contentPadding = PaddingValues(bottom = 96.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            // Header summary banner
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(EmeraldPrimary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Hisn al-Muslim Library",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Browse ${categories.size} verified authentic categories from Quran & Sunnah",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        items(categories) { cat ->
            val count = rawDuas.count { it.category.equals(cat.name, ignoreCase = true) }
            val icon = getCategoryIcon(cat.iconKey)

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectCategory(cat.name) }
                    .adaptiveFocusable(shape = RoundedCornerShape(16.dp))
                    .testTag("category_card_${cat.id}")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(EmeraldPrimary.copy(alpha = 0.14f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = icon, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(20.dp))
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = GoldAccent.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "$count Duas",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Text(
                        text = cat.name,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = cat.arabicName,
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold, color = EmeraldPrimary),
                        textAlign = TextAlign.Right,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        text = cat.description,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, lineHeight = 15.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// Sub-View 2: All Invocations View
// -------------------------------------------------------------
@Composable
private fun AllDuasExplorerView(
    duas: List<DuaEntity>,
    allCount: Int,
    state: DeenUiState,
    viewModel: DeenViewModel
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Horizontal Category Filter Carousel
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(DuaSeedData.CATEGORIES) { cat ->
                val isSelected = state.selectedDuaCategory.equals(cat, ignoreCase = true)
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.selectDuaCategory(cat) },
                    label = { Text(cat, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = EmeraldPrimary,
                        selectedLabelColor = Color.White
                    ),
                    modifier = Modifier.testTag("filter_chip_${cat.replace(" ", "_")}")
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Sub-filters row: Favorites filter, Memorized filter, and Layout view toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilterChip(
                    selected = state.duaFavoritesOnly,
                    onClick = { viewModel.setDuaFavoritesFilter(!state.duaFavoritesOnly) },
                    label = { Text("Favorites", fontSize = 11.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = if (state.duaFavoritesOnly) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = if (state.duaFavoritesOnly) GoldAccent else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    modifier = Modifier.testTag("filter_chip_favorites")
                )

                FilterChip(
                    selected = state.duaMemorizedOnly,
                    onClick = { viewModel.setDuaMemorizedFilter(!state.duaMemorizedOnly) },
                    label = { Text("Memorized", fontSize = 11.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = EmeraldPrimary
                        )
                    },
                    modifier = Modifier.testTag("filter_chip_memorized")
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${duas.size} / $allCount",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(6.dp))
                IconButton(
                    onClick = { viewModel.toggleDuaViewMode() },
                    modifier = Modifier.size(36.dp).testTag("dua_toggle_grid_mode")
                ) {
                    Icon(
                        imageVector = if (state.isDuaGridView) Icons.Default.GridView else Icons.Default.ViewAgenda,
                        contentDescription = "Toggle Grid",
                        tint = EmeraldPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (duas.isEmpty()) {
            EmptyDuaState(
                message = "No invocations found matching your search or filters.",
                onReset = {
                    viewModel.setDuaSearchQuery("")
                    viewModel.selectDuaCategory("All")
                    viewModel.setDuaFavoritesFilter(false)
                    viewModel.setDuaMemorizedFilter(false)
                }
            )
        } else if (state.isDuaGridView) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 300.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("dua_grid_list"),
                contentPadding = PaddingValues(bottom = 96.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(duas) { dua ->
                    DuaItemCard(
                        dua = dua,
                        state = state,
                        viewModel = viewModel
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("dua_vertical_list"),
                contentPadding = PaddingValues(bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(duas) { dua ->
                    DuaItemCard(
                        dua = dua,
                        state = state,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// Sub-View 3: Saved & Organized Bookmarks View
// -------------------------------------------------------------
@Composable
private fun SavedAndOrganizedView(
    savedDuas: List<DuaEntity>,
    allFolders: List<String>,
    bookmarkedCount: Int,
    memorizedCount: Int,
    organizedCount: Int,
    state: DeenUiState,
    viewModel: DeenViewModel
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Stats Overview Banner
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent.copy(alpha = 0.35f)),
            modifier = Modifier.fillMaxWidth().testTag("saved_duas_stats_card")
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatItem(label = "Bookmarked", count = "$bookmarkedCount", icon = Icons.Filled.Bookmark, color = GoldAccent)
                StatItem(label = "In Folders", count = "$organizedCount", icon = Icons.Default.Folder, color = EmeraldPrimary)
                StatItem(label = "Memorized", count = "$memorizedCount", icon = Icons.Default.CheckCircle, color = EmeraldPrimary)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Folders Filter Carousel
        Text(
            text = "My Custom Collections & Folders",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(4.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(allFolders) { folder ->
                val isSelected = state.selectedDuaFolder.equals(folder, ignoreCase = true)
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.selectDuaFolder(folder) },
                    label = { Text(folder, fontSize = 11.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = if (folder == "All") Icons.Default.FolderOpen else Icons.Default.Folder,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = EmeraldPrimary.copy(alpha = 0.18f),
                        selectedLabelColor = EmeraldPrimary
                    ),
                    modifier = Modifier.testTag("saved_folder_chip_${folder.replace(" ", "_")}")
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (savedDuas.isEmpty()) {
            EmptyDuaState(
                message = if (state.selectedDuaFolder != "All") "No invocations saved in folder \"${state.selectedDuaFolder}\"."
                else "You haven't bookmarked any invocations yet. Tap the bookmark icon on any Dua to save it here for quick access!",
                onReset = {
                    viewModel.selectDuaFolder("All")
                    viewModel.setDuaViewModeTab(1)
                }
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("saved_duas_list"),
                contentPadding = PaddingValues(bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(savedDuas) { dua ->
                    DuaItemCard(
                        dua = dua,
                        state = state,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}

@Composable
private fun StatItem(label: String, count: String, icon: ImageVector, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
            Text(text = count, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface)
        }
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

// -------------------------------------------------------------
// Component: Enhanced Dua Item Card
// -------------------------------------------------------------
@Composable
fun DuaItemCard(
    dua: DuaEntity,
    state: DeenUiState,
    viewModel: DeenViewModel
) {
    val context = LocalContext.current
    val isPlaying = state.playingAudioId == dua.id
    val currentCount = dua.practiceCount.coerceAtLeast(state.duaProgressCounts[dua.id] ?: 0)
    val isTargetReached = currentCount >= dua.recommendedCount

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isPlaying) EmeraldPrimary
            else if (dua.isFavorite) GoldAccent.copy(alpha = 0.7f)
            else if (dua.isMemorized) EmeraldPrimary.copy(alpha = 0.5f)
            else MaterialTheme.colorScheme.outline.copy(alpha = 0.22f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .adaptiveFocusable(shape = RoundedCornerShape(18.dp))
            .testTag("dua_card_${dua.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Top Row: Title, Badges, Bookmark & Organize Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = dua.title,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = dua.category,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        if (dua.customFolder.isNotBlank()) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = EmeraldPrimary.copy(alpha = 0.15f)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Icon(Icons.Default.Folder, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(10.dp))
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = dua.customFolder,
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                                        color = EmeraldPrimary
                                    )
                                }
                            }
                        }

                        if (dua.isMemorized) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = GoldAccent.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "Memorized",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold, color = GoldAccent),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }

                // Top Right Action Buttons (Bookmark, Organize, Share)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    // Organize / Custom Folder button
                    IconButton(
                        onClick = { viewModel.openDuaOrganizeDialog(dua) },
                        modifier = Modifier.size(36.dp).testTag("organize_btn_${dua.id}")
                    ) {
                        Icon(
                            imageVector = if (dua.customFolder.isNotBlank()) Icons.Default.FolderSpecial else Icons.Outlined.Folder,
                            contentDescription = "Organize in folder",
                            tint = if (dua.customFolder.isNotBlank()) EmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Bookmark Toggle button
                    IconButton(
                        onClick = { viewModel.toggleFavoriteDua(dua.id, dua.isFavorite) },
                        modifier = Modifier.size(36.dp).testTag("dua_favorite_btn_${dua.id}")
                    ) {
                        Icon(
                            imageVector = if (dua.isFavorite) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = if (dua.isFavorite) "Remove Bookmark" else "Bookmark Dua",
                            tint = if (dua.isFavorite) GoldAccent else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Share button
                    IconButton(
                        onClick = {
                            val shareContent = "🤲 ${dua.title}\n\n${dua.arabicText}\n\nTransliteration: ${dua.transliteration}\n\nTranslation: \"${dua.translation}\"\n\nRef: ${dua.reference}\nShared via Deen365"
                            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, shareContent)
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Share Invocation"))
                        },
                        modifier = Modifier.size(36.dp).testTag("share_dua_btn_${dua.id}")
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "Share", tint = EmeraldPrimary, modifier = Modifier.size(16.dp))
                    }
                }
            }

            // User Note Callout (if added)
            if (dua.userNote.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = GoldAccent.copy(alpha = 0.12f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent.copy(alpha = 0.3f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.openDuaOrganizeDialog(dua) }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.EditNote, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(16.dp))
                        Text(
                            text = dua.userNote,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic),
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Arabic Text Container
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = dua.arabicText,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Right,
                            lineHeight = 36.sp
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Ref: ${dua.reference}",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )

                        // Quick Copy Arabic Button
                        IconButton(
                            onClick = {
                                val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                cm.setPrimaryClip(ClipData.newPlainText("Dua Arabic", dua.arabicText))
                                Toast.makeText(context, "Arabic text copied!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.size(28.dp).testTag("copy_arabic_${dua.id}")
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy Arabic", tint = EmeraldPrimary, modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Audio Recitation Pill
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (isPlaying) EmeraldPrimary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (isPlaying) EmeraldPrimary else Color.Transparent),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.toggleRecitation(dua.id, dua.arabicText, dua.transliteration) }
                    .testTag("audio_play_btn_${dua.id}")
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
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Stop else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Stop" else "Listen",
                            tint = if (isPlaying) EmeraldPrimary else GoldAccent,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = if (isPlaying) "Playing Recitation (Tap to Stop)" else "Listen Recitation Audio",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (isPlaying) EmeraldPrimary else MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Icon(
                        imageVector = if (isPlaying) Icons.Default.VolumeUp else Icons.Outlined.VolumeUp,
                        contentDescription = null,
                        tint = if (isPlaying) EmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Transliteration
            Text(
                text = dua.transliteration,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    lineHeight = 18.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(6.dp))

            // English Translation
            Text(
                text = "\"${dua.translation}\"",
                style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 20.sp),
                color = MaterialTheme.colorScheme.onSurface
            )

            if (dua.explanation.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = dua.explanation,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, lineHeight = 16.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.85f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Bottom Learning & Practice Action Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    // Practice Counter Button
                    FilledTonalButton(
                        onClick = { viewModel.incrementDuaCount(dua.id, dua.recommendedCount) },
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = if (isTargetReached) EmeraldPrimary.copy(alpha = 0.18f) else MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (isTargetReached) EmeraldPrimary else MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier.testTag("practice_counter_${dua.id}")
                    ) {
                        Icon(
                            imageVector = if (isTargetReached) Icons.Default.CheckCircle else Icons.Default.Fingerprint,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isTargetReached) "Done ($currentCount)" else "$currentCount / ${dua.recommendedCount}x",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    // Memorize Toggle Button
                    FilledTonalButton(
                        onClick = { viewModel.toggleDuaMemorized(dua.id, dua.isMemorized) },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = if (dua.isMemorized) GoldAccent.copy(alpha = 0.22f) else MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (dua.isMemorized) GoldAccent else MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.testTag("memorize_toggle_${dua.id}")
                    ) {
                        Icon(
                            imageVector = if (dua.isMemorized) Icons.Default.Star else Icons.Outlined.StarBorder,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = if (dua.isMemorized) "Memorized" else "Memorize",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp)
                        )
                    }
                }

                // Learn & Practice Modal Trigger
                TextButton(
                    onClick = {
                        val item = DuaSeedData.DUAS.firstOrNull { it.id == dua.id } ?: com.example.data.seed.DuaItem(
                            id = dua.id,
                            title = dua.title,
                            category = dua.category,
                            arabicText = dua.arabicText,
                            transliteration = dua.transliteration,
                            translation = dua.translation,
                            explanation = dua.explanation,
                            reference = dua.reference,
                            recommendedCount = dua.recommendedCount
                        )
                        viewModel.openLearnDua(item)
                    },
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp),
                    modifier = Modifier.testTag("learn_dua_btn_${dua.id}")
                ) {
                    Icon(Icons.Default.School, contentDescription = null, modifier = Modifier.size(14.dp), tint = EmeraldPrimary)
                    Spacer(modifier = Modifier.width(3.dp))
                    Text("Learn", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = EmeraldPrimary))
                }
            }
        }
    }
}

@Composable
private fun EmptyDuaState(
    message: String,
    onReset: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp)
            .testTag("dua_empty_state")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(EmeraldPrimary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.SearchOff, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(30.dp))
            }
            Text(
                text = "No Invocations Found",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Button(
                onClick = onReset,
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Reset Filters")
            }
        }
    }
}

private fun getCategoryIcon(iconKey: String): ImageVector {
    return when (iconKey) {
        "wb_sunny" -> Icons.Default.WbSunny
        "mosque" -> Icons.Default.Mosque
        "psychology" -> Icons.Default.Psychology
        "favorite" -> Icons.Default.Favorite
        "shield" -> Icons.Default.Shield
        "healing" -> Icons.Default.Healing
        "flight" -> Icons.Default.Flight
        "restaurant" -> Icons.Default.Restaurant
        "home" -> Icons.Default.Home
        "bedtime" -> Icons.Default.Bedtime
        "auto_stories" -> Icons.Default.AutoStories
        else -> Icons.Default.Explore
    }
}
