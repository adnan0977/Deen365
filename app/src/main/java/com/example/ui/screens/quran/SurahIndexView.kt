package com.example.ui.screens.quran

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GoogleAdBannerPlaceholder
import com.example.data.seed.JuzMeta
import com.example.data.seed.JuzSeedData
import com.example.data.seed.SurahMeta
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.util.adaptiveFocusable
import com.example.ui.util.rememberDeviceScreenProfile

@Composable
fun SurahIndexView(
    surahs: List<SurahMeta>,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onSelectSurah: (SurahMeta) -> Unit,
    dailyQuranGoalMinutes: Int = 15,
    todayReadingSeconds: Int = 0,
    quranStreakDays: Int = 0,
    isGoalCompletedToday: Boolean = false,
    onChangeGoal: () -> Unit = {},
    onOpenThematicIndex: () -> Unit = {},
    onOpenComparativeFiqh: () -> Unit = {},
    onOpenKitabBab: () -> Unit = {},
    onOpenGharibGlossary: () -> Unit = {},
    onOpenSettings: () -> Unit = {},
    onOpenGoToVerse: () -> Unit = {},
    onOpenRabbana: () -> Unit = {},
    onOpenSajdah: () -> Unit = {},
    onOpenTajweedLegend: () -> Unit = {},
    onOpenQuranExplorer: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Surahs, 1: 30 Juz, 2: Study Hub

    val filtered = remember(surahs, searchQuery) {
        if (searchQuery.isBlank()) surahs
        else surahs.filter {
            it.englishName.contains(searchQuery, ignoreCase = true) ||
                    it.englishTranslation.contains(searchQuery, ignoreCase = true) ||
                    it.arabicName.contains(searchQuery) ||
                    it.number.toString() == searchQuery.trim()
        }
    }

    val filteredJuz = remember(searchQuery) {
        if (searchQuery.isBlank()) JuzSeedData.ALL_JUZ
        else JuzSeedData.ALL_JUZ.filter {
            it.arabicName.contains(searchQuery) ||
                    it.transliteration.contains(searchQuery, ignoreCase = true) ||
                    it.startSurahName.contains(searchQuery, ignoreCase = true) ||
                    it.juzNumber.toString() == searchQuery.trim()
        }
    }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 320.dp),
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                // Top Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("quran_search_field")
                        .adaptiveFocusable(),
                    placeholder = { Text("Search Surah name, number, meaning...") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = EmeraldPrimary
                        )
                    },
                    trailingIcon = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (searchQuery.isNotBlank()) {
                                IconButton(onClick = { onSearchChange("") }) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear search")
                                }
                            }
                            IconButton(onClick = onOpenGoToVerse, modifier = Modifier.testTag("open_goto_verse_btn")) {
                                Icon(Icons.Default.NearMe, contentDescription = "Jump to Ayah", tint = EmeraldPrimary)
                            }
                            IconButton(onClick = onOpenSettings) {
                                Icon(Icons.Default.Settings, contentDescription = "Settings")
                            }
                        }
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EmeraldPrimary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    ),
                    singleLine = true
                )

                // Quran Daily Reading Goal & Streak Card
                val readMinutes = todayReadingSeconds / 60
                val readSecondsRemainder = todayReadingSeconds % 60
                val progressFraction = (todayReadingSeconds.toFloat() / (dailyQuranGoalMinutes * 60).toFloat()).coerceIn(0f, 1f)

                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isGoalCompletedToday) GoldAccent else EmeraldPrimary.copy(alpha = 0.35f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onChangeGoal() }
                        .testTag("quran_daily_goal_card")
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
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
                                    color = if (isGoalCompletedToday) GoldAccent.copy(alpha = 0.2f) else EmeraldPrimary.copy(alpha = 0.15f),
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = if (isGoalCompletedToday) Icons.Default.CheckCircle else Icons.Default.Timer,
                                            contentDescription = null,
                                            tint = if (isGoalCompletedToday) GoldAccent else EmeraldPrimary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }

                                Column {
                                    Text(
                                        text = if (isGoalCompletedToday) "Daily Goal Achieved! 🎉" else "Daily Quran Reading Goal",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "${readMinutes}m ${readSecondsRemainder}s read of ${dailyQuranGoalMinutes}m goal • Tap to adjust",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            // Streak Badge
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = GoldAccent.copy(alpha = 0.18f),
                                border = androidx.compose.foundation.BorderStroke(0.5.dp, GoldAccent)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocalFireDepartment,
                                        contentDescription = null,
                                        tint = GoldAccent,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = "$quranStreakDays Day Streak",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = GoldAccent
                                    )
                                }
                            }
                        }

                        // Progress Bar
                        LinearProgressIndicator(
                            progress = { progressFraction },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = if (isGoalCompletedToday) GoldAccent else EmeraldPrimary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    }
                }

                // 3-Tab Quran Index Bar
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = EmeraldPrimary,
                    modifier = Modifier.fillMaxWidth().testTag("quran_index_tabs")
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Surahs (114)", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        icon = { Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("30 Juz / Paras", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        icon = { Icon(Icons.Default.CollectionsBookmark, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text("Study Hub", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        icon = { Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    )
                    Tab(
                        selected = false,
                        onClick = { onOpenQuranExplorer("1:1") },
                        text = { Text("Explorer", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        icon = { Icon(Icons.Default.Explore, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    )
                }

                // Quick Filter Chips (Popular Surahs) when on Tab 0
                if (selectedTab == 0) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("Al-Fatiha", "Al-Baqarah", "Yaseen", "Al-Mulk", "Al-Kahf").forEach { name ->
                            val isMatched = searchQuery.equals(name, ignoreCase = true)
                            FilterChip(
                                selected = isMatched,
                                onClick = {
                                    if (isMatched) onSearchChange("") else onSearchChange(name)
                                },
                                label = { Text(name, fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = EmeraldPrimary.copy(alpha = 0.15f),
                                    selectedLabelColor = EmeraldPrimary
                                ),
                                modifier = Modifier.testTag("filter_chip_$name")
                            )
                        }
                    }
                }
            }
        }

        // Google Ads Space
        item(span = { GridItemSpan(maxLineSpan) }) {
            GoogleAdBannerPlaceholder(
                modifier = Modifier.padding(vertical = 4.dp),
                adSlotName = "Quran Surah Index Banner"
            )
        }

        when (selectedTab) {
            0 -> {
                // Tab 0: Surahs
                items(filtered, key = { it.number }) { surah ->
                    SurahIndexCard(
                        surah = surah,
                        onClick = { onSelectSurah(surah) }
                    )
                }
            }
            1 -> {
                // Tab 1: 30 Juz
                items(filteredJuz, key = { it.juzNumber }) { juz ->
                    JuzIndexCard(
                        juz = juz,
                        onClick = {
                            val targetSurah = surahs.firstOrNull { it.number == juz.startSurahNumber }
                            if (targetSurah != null) onSelectSurah(targetSurah)
                        }
                    )
                }
            }
            2 -> {
                // Tab 2: Study Hub Cards
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        QuranStudyHubItem(
                            title = "40 Rabbana Quranic Duas",
                            subtitle = "Prophetic and righteous supplications from the Quran",
                            icon = Icons.Default.VolunteerActivism,
                            badge = "40 Duas",
                            onClick = onOpenRabbana
                        )
                        QuranStudyHubItem(
                            title = "15 Sajdah Tilawah Verses",
                            subtitle = "Verses requiring prostration of recitation with Fiqh rulings",
                            icon = Icons.Default.VerticalAlignBottom,
                            badge = "15 Verses",
                            onClick = onOpenSajdah
                        )
                        QuranStudyHubItem(
                            title = "Color-Coded Tajweed Legend",
                            subtitle = "Rules of Ghunnah, Qalqalah, Idgham, Ikhfa, and Madd",
                            icon = Icons.Default.Palette,
                            badge = "Tajweed",
                            onClick = onOpenTajweedLegend
                        )
                        QuranStudyHubItem(
                            title = "Direct Go-to-Ayah Coordinate",
                            subtitle = "Jump directly to any verse (e.g. 2:255, 36:1, 18:10)",
                            icon = Icons.Default.NearMe,
                            badge = "Jump",
                            onClick = onOpenGoToVerse
                        )
                        QuranStudyHubItem(
                            title = "Alim.org Thematic Subject Index",
                            subtitle = "Cross-corpus topics across Tawhid, Akhlaq, Family, and Social Justice",
                            icon = Icons.Default.AutoStories,
                            badge = "Alim.org",
                            onClick = onOpenThematicIndex
                        )
                        QuranStudyHubItem(
                            title = "4-Madhhab Comparative Fiqh",
                            subtitle = "Hanafi, Shafi'i, Maliki, and Hanbali rulings with textual proofs",
                            icon = Icons.Default.Balance,
                            badge = "4 Madhhabs",
                            onClick = onOpenComparativeFiqh
                        )
                        QuranStudyHubItem(
                            title = "Sunnah.com Kitab & Bab Tracker",
                            subtitle = "Track your study completion through classical Hadith chapters",
                            icon = Icons.Default.Checklist,
                            badge = "Sunnah.com",
                            onClick = onOpenKitabBab
                        )
                        QuranStudyHubItem(
                            title = "Gharib al-Hadith Glossary",
                            subtitle = "Classical Arabic vocabulary & rare Hadith terminology (An-Nihayah)",
                            icon = Icons.Default.AutoStories,
                            badge = "Ibn al-Athir",
                            onClick = onOpenGharibGlossary
                        )
                        QuranStudyHubItem(
                            title = "Quran Explorer (Quran.com API v4)",
                            subtitle = "Word-by-word morphology, tri-consonant roots, segments & grammar breakdown",
                            icon = Icons.Default.Explore,
                            badge = "Quran.com",
                            onClick = { onOpenQuranExplorer("1:1") }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SurahIndexCard(
    surah: SurahMeta,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("surah_card_${surah.number}")
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
                        .border(0.5.dp, GoldAccent.copy(alpha = 0.4f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${surah.number}",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = surah.englishName,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${surah.englishTranslation} • ${surah.revelationType} (${surah.numberOfAyahs} verses)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = surah.arabicName,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                ),
                maxLines = 1,
                textAlign = TextAlign.End
            )
        }
    }
}

@Composable
fun JuzIndexCard(
    juz: JuzMeta,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("juz_card_${juz.juzNumber}")
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = EmeraldPrimary.copy(alpha = 0.15f),
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "${juz.juzNumber}",
                            fontWeight = FontWeight.Bold,
                            color = EmeraldPrimary
                        )
                    }
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = "Juz ${juz.juzNumber}: ${juz.transliteration}",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Starts at ${juz.startSurahName} (${juz.startAyahNumber}) • ${juz.totalVerses} Ayahs",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = juz.arabicName,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = EmeraldPrimary
                ),
                maxLines = 1,
                textAlign = TextAlign.End
            )
        }
    }
}

@Composable
private fun QuranStudyHubItem(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    badge: String,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = EmeraldPrimary.copy(alpha = 0.12f),
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = EmeraldPrimary)
                }
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(Modifier.width(8.dp))
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = GoldAccent.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = badge,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldAccent,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                }
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = EmeraldPrimary)
        }
    }
}
