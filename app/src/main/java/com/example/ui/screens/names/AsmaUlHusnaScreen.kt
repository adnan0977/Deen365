package com.example.ui.screens.names

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.AllahName
import com.example.data.model.AsmaUlHusnaData
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldLight
import com.example.ui.viewmodel.DeenUiState
import com.example.ui.viewmodel.DeenViewModel
import com.example.ui.viewmodel.MainTab

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AsmaUlHusnaScreen(
    state: DeenUiState,
    viewModel: DeenViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.selectTab(MainTab.DASHBOARD)
    }

    val asmaState = state.asmaUlHusnaState
    val allNames = remember { AsmaUlHusnaData.ALL_99_NAMES }

    // Filter names based on search query and active tab
    val filteredNames = remember(asmaState.searchQuery, asmaState.activeFilterTab, asmaState.memorizedIds, asmaState.favoriteIds) {
        allNames.filter { name ->
            val matchesSearch = asmaState.searchQuery.isBlank() ||
                    name.arabic.contains(asmaState.searchQuery) ||
                    name.transliteration.contains(asmaState.searchQuery, ignoreCase = true) ||
                    name.meaning.contains(asmaState.searchQuery, ignoreCase = true)

            val matchesFilter = when (asmaState.activeFilterTab) {
                1 -> asmaState.memorizedIds.contains(name.number)
                2 -> asmaState.favoriteIds.contains(name.number)
                else -> true
            }

            matchesSearch && matchesFilter
        }
    }

    val memorizedCount = asmaState.memorizedIds.size
    val progressPercent = (memorizedCount.toFloat() / 99f).coerceIn(0f, 1f)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "99 Names of Allah",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = GoldAccent.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "أسماء الله الحسنى",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = FontFamily.Serif,
                                        fontWeight = FontWeight.Bold,
                                        color = GoldAccent
                                    ),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Audio Pronunciation • Meanings • Memorization",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.selectTab(MainTab.DASHBOARD) },
                        modifier = Modifier.testTag("asma_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Home"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = modifier.testTag("asma_ul_husna_screen")
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Memorization Progress Hero Banner
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("memorization_hero_card"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    EmeraldPrimary.copy(alpha = 0.10f),
                                    GoldAccent.copy(alpha = 0.08f)
                                )
                            )
                        )
                        .padding(14.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Stars,
                                    contentDescription = null,
                                    tint = GoldAccent,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = "Memorization Challenge",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Text(
                                text = "$memorizedCount / 99 (${(progressPercent * 100).toInt()}%)",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldPrimary
                                )
                            )
                        }

                        Spacer(Modifier.height(8.dp))

                        LinearProgressIndicator(
                            progress = { progressPercent },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(CircleShape),
                            color = EmeraldPrimary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )

                        Spacer(Modifier.height(6.dp))

                        Text(
                            text = "\"Allah has ninety-nine names; whoever memorizes them will enter Paradise.\" (Sahih al-Bukhari #2736)",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.5.sp, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Search Bar
            OutlinedTextField(
                value = asmaState.searchQuery,
                onValueChange = { viewModel.setAsmaSearchQuery(it) },
                placeholder = { Text("Search by name, transliteration or meaning...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (asmaState.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setAsmaSearchQuery("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .testTag("asma_search_input"),
                shape = RoundedCornerShape(10.dp)
            )

            // Filter Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = asmaState.activeFilterTab == 0,
                    onClick = { viewModel.setAsmaFilterTab(0) },
                    label = { Text("All (99)") },
                    modifier = Modifier.testTag("filter_all_names")
                )
                FilterChip(
                    selected = asmaState.activeFilterTab == 1,
                    onClick = { viewModel.setAsmaFilterTab(1) },
                    label = { Text("Memorized ($memorizedCount)") },
                    leadingIcon = { Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    modifier = Modifier.testTag("filter_memorized_names")
                )
                FilterChip(
                    selected = asmaState.activeFilterTab == 2,
                    onClick = { viewModel.setAsmaFilterTab(2) },
                    label = { Text("Favorites (${asmaState.favoriteIds.size})") },
                    leadingIcon = { Icon(Icons.Default.Favorite, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    modifier = Modifier.testTag("filter_favorite_names")
                )
            }

            // Grid of 99 Names
            if (filteredNames.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.SearchOff,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outlineVariant,
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = "No Names Found",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Try adjusting your search query or filter selection.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp),
                    contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredNames, key = { it.number }) { item ->
                        val isMemorized = asmaState.memorizedIds.contains(item.number)
                        val isFavorite = asmaState.favoriteIds.contains(item.number)
                        val dhikrCount = asmaState.nameDhikrCounters.getOrDefault(item.number, 0)

                        AllahNameGridCard(
                            name = item,
                            isMemorized = isMemorized,
                            isFavorite = isFavorite,
                            dhikrCount = dhikrCount,
                            onCardClick = { viewModel.selectNameForDetail(item) },
                            onPlayPronounce = { viewModel.playNamePronunciation(item) },
                            onToggleMemorized = { viewModel.toggleNameMemorized(item.number) },
                            onToggleFavorite = { viewModel.toggleNameFavorite(item.number) },
                            onIncrementDhikr = { viewModel.incrementNameDhikr(item.number) }
                        )
                    }
                }
            }
        }
    }

    // Detail Modal Dialog
    if (asmaState.isDetailDialogOpen && asmaState.selectedNameForDetail != null) {
        val selected = asmaState.selectedNameForDetail
        val isMemorized = asmaState.memorizedIds.contains(selected.number)
        val isFavorite = asmaState.favoriteIds.contains(selected.number)
        val dhikrCount = asmaState.nameDhikrCounters.getOrDefault(selected.number, 0)

        AllahNameDetailDialog(
            name = selected,
            isMemorized = isMemorized,
            isFavorite = isFavorite,
            dhikrCount = dhikrCount,
            onDismiss = { viewModel.selectNameForDetail(null) },
            onPlayPronounce = { viewModel.playNamePronunciation(selected) },
            onToggleMemorized = { viewModel.toggleNameMemorized(selected.number) },
            onToggleFavorite = { viewModel.toggleNameFavorite(selected.number) },
            onIncrementDhikr = { viewModel.incrementNameDhikr(selected.number) }
        )
    }
}

@Composable
private fun AllahNameGridCard(
    name: AllahName,
    isMemorized: Boolean,
    isFavorite: Boolean,
    dhikrCount: Int,
    onCardClick: () -> Unit,
    onPlayPronounce: () -> Unit,
    onToggleMemorized: () -> Unit,
    onToggleFavorite: () -> Unit,
    onIncrementDhikr: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onCardClick() }
            .testTag("name_card_${name.number}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar: Number badge & Favorite Icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = EmeraldPrimary.copy(alpha = 0.12f),
                    modifier = Modifier.size(26.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = name.number.toString(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = EmeraldPrimary
                            )
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onToggleFavorite,
                        modifier = Modifier.size(28.dp).testTag("fav_btn_${name.number}")
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outlineVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = onToggleMemorized,
                        modifier = Modifier.size(28.dp).testTag("mem_btn_${name.number}")
                    ) {
                        Icon(
                            imageVector = if (isMemorized) Icons.Default.CheckCircle else Icons.Outlined.CheckCircle,
                            contentDescription = "Memorized",
                            tint = if (isMemorized) EmeraldPrimary else MaterialTheme.colorScheme.outlineVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(6.dp))

            // Arabic Calligraphy Text
            Text(
                text = name.arabic,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    color = GoldAccent
                ),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(4.dp))

            // Transliteration
            Text(
                text = name.transliteration,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            // Meaning
            Text(
                text = name.meaning,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(10.dp))

            // Bottom Actions: Audio Pronounce & Dhikr Tap
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Audio Speak
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .clickable { onPlayPronounce() }
                        .testTag("audio_btn_${name.number}")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "Pronounce",
                            modifier = Modifier.size(16.dp),
                            tint = EmeraldPrimary
                        )
                    }
                }

                // Dhikr Counter Tap Pill
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = EmeraldPrimary.copy(alpha = 0.12f),
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onIncrementDhikr() }
                        .testTag("dhikr_pill_${name.number}")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Fingerprint,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = EmeraldPrimary
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = if (dhikrCount > 0) dhikrCount.toString() else "Dhikr",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = EmeraldPrimary
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AllahNameDetailDialog(
    name: AllahName,
    isMemorized: Boolean,
    isFavorite: Boolean,
    dhikrCount: Int,
    onDismiss: () -> Unit,
    onPlayPronounce: () -> Unit,
    onToggleMemorized: () -> Unit,
    onToggleFavorite: () -> Unit,
    onIncrementDhikr: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("name_detail_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = EmeraldPrimary.copy(alpha = 0.15f),
                        modifier = Modifier.size(32.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "#${name.number}",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldPrimary
                                )
                            )
                        }
                    }

                    Row {
                        IconButton(onClick = onToggleFavorite) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = if (isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outlineVariant
                            )
                        }
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }
                }

                Spacer(Modifier.height(10.dp))

                // Grand Arabic Calligraphy
                Text(
                    text = name.arabic,
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        color = GoldAccent
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(6.dp))

                Text(
                    text = name.transliteration,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = name.meaning,
                    style = MaterialTheme.typography.titleSmall.copy(color = EmeraldPrimary),
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(14.dp))

                // Explanation
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Divine Explanation",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = name.explanation,
                            style = MaterialTheme.typography.bodySmall.copy(lineHeight = 16.sp),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(Modifier.height(10.dp))

                // Quranic Reference
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.MenuBook,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = EmeraldPrimary
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "Quran Reference: ${name.quranicReference}",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(Modifier.height(6.dp))

                // Dhikr Benefits
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = GoldAccent
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "Spiritual Benefit: ${name.benefits}",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp, lineHeight = 15.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(Modifier.height(16.dp))

                // Action Bar: Pronounce, Memorize, and Dhikr Counter
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onPlayPronounce,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.VolumeUp, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Pronounce", fontSize = 12.sp)
                    }

                    Button(
                        onClick = onIncrementDhikr,
                        modifier = Modifier.weight(1f).testTag("dialog_dhikr_button"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                    ) {
                        Icon(Icons.Default.Fingerprint, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Dhikr: $dhikrCount", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(Modifier.height(8.dp))

                FilterChip(
                    selected = isMemorized,
                    onClick = onToggleMemorized,
                    label = { Text(if (isMemorized) "✓ Memorized in Heart" else "+ Mark as Memorized") },
                    leadingIcon = {
                        Icon(
                            imageVector = if (isMemorized) Icons.Default.CheckCircle else Icons.Outlined.CheckCircle,
                            contentDescription = null,
                            tint = if (isMemorized) EmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
