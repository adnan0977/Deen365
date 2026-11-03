package com.example.ui.screens.sahaba

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.seed.DetailedSahabiBiography
import com.example.data.seed.LinkedHadithRef
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.viewmodel.DeenUiState
import com.example.ui.viewmodel.DeenViewModel
import com.example.ui.viewmodel.MainTab

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SahabaBiographiesScreen(
    state: DeenUiState,
    viewModel: DeenViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        if (state.selectedSahabiId != null) {
            viewModel.selectSahabi(null)
        } else {
            viewModel.setTab(MainTab.DASHBOARD)
        }
    }

    val selectedSahabi = remember(state.sahabaList, state.selectedSahabiId) {
        state.sahabaList.firstOrNull { it.id == state.selectedSahabiId }
    }

    val filteredSahaba = remember(state.sahabaList, state.sahabaFilterCategory) {
        if (state.sahabaFilterCategory == "All") state.sahabaList
        else state.sahabaList.filter { it.category.equals(state.sahabaFilterCategory, ignoreCase = true) }
    }

    Scaffold(
        modifier = modifier.fillMaxSize().testTag("sahaba_biographies_screen"),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = selectedSahabi?.nameEnglish ?: "Sahaba Biographies",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            maxLines = 1
                        )
                        Text(
                            text = selectedSahabi?.honorificTitle ?: "Ashara Mubashara & Prominent Companions",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (selectedSahabi != null) {
                                viewModel.selectSahabi(null)
                            } else {
                                viewModel.setTab(MainTab.DASHBOARD)
                            }
                        },
                        modifier = Modifier.testTag("sahaba_back_button")
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = GoldAccent.copy(alpha = 0.2f),
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        Text(
                            text = "Ashara Mubashara",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (selectedSahabi != null) {
                SahabiChronicleDetailView(
                    sahabi = selectedSahabi,
                    onOpenHadith = { coll, num ->
                        viewModel.jumpToHadithFromSahaba(coll, num)
                    }
                )
            } else {
                SahabaListView(
                    sahaba = filteredSahaba,
                    selectedFilter = state.sahabaFilterCategory,
                    onSelectFilter = { viewModel.filterSahabaCategory(it) },
                    onSelectSahabi = { viewModel.selectSahabi(it) }
                )
            }
        }
    }
}

@Composable
private fun SahabaListView(
    sahaba: List<DetailedSahabiBiography>,
    selectedFilter: String,
    onSelectFilter: (String) -> Unit,
    onSelectSahabi: (String) -> Unit
) {
    val categories = listOf("All", "Ashara Mubashara", "Pioneers & Martyrs")

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = EmeraldPrimary.copy(alpha = 0.12f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("🌟", fontSize = 28.sp)
                    Column {
                        Text(
                            text = "The Stars of Guidance",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "The Prophet ﷺ said: 'My companions are like the stars; whichever of them you follow, you will be rightly guided.' Explore their sacrifices and prophetic hadiths.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Category Filter Chips
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                items(categories) { cat ->
                    FilterChip(
                        selected = selectedFilter == cat,
                        onClick = { onSelectFilter(cat) },
                        label = { Text(cat, fontSize = 12.sp) }
                    )
                }
            }
        }

        items(sahaba, key = { it.id }) { s ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectSahabi(s.id) }
                    .testTag("sahabi_card_${s.id}")
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = s.nameEnglish,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = s.nameArabic,
                                style = MaterialTheme.typography.bodyMedium,
                                color = EmeraldPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = GoldAccent.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = s.category,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = s.honorificTitle,
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = "⏳ ${s.lifespan}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "📜 ${s.linkedHadiths.size} Linked Hadiths",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = EmeraldPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = s.conversionOrder,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2
                    )
                }
            }
        }
    }
}

@Composable
private fun SahabiChronicleDetailView(
    sahabi: DetailedSahabiBiography,
    onOpenHadith: (collection: String, hadithNumber: Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Header Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = EmeraldPrimary.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = sahabi.category.uppercase(),
                                color = EmeraldPrimary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        Text(text = sahabi.lifespan, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    Text(
                        text = sahabi.nameArabic,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldPrimary
                    )

                    Text(
                        text = sahabi.nameEnglish,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = sahabi.honorificTitle,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = "• ${sahabi.conversionOrder}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Famous Quote Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = GoldAccent.copy(alpha = 0.15f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("💬 Memorable Statement", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF8C6D00))
                    Text(
                        text = "\"${sahabi.famousQuote}\"",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 22.sp
                    )
                }
            }
        }

        // Life Story Chronicle
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Life Story & Historical Chronicle",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = sahabi.lifeStoryChronicle,
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 22.sp
                    )
                }
            }
        }

        // Major Trials & Sacrifices
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("🛡️", fontSize = 18.sp)
                        Text(
                            text = "Major Trials & Sacrifices for Allah",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    sahabi.majorTrials.forEach { trial ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("•", color = EmeraldPrimary, fontWeight = FontWeight.Bold)
                            Text(text = trial, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }

        // Historical Milestones & Services to Islam
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("🏛️", fontSize = 18.sp)
                        Text(
                            text = "Key Milestones & Service to Islam",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    sahabi.milestonesAndService.forEach { milestone ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("✓", color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                            Text(text = milestone, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }

        // Linked Prophetic Hadiths Section
        item {
            Text(
                text = "Linked Prophetic Hadiths (${sahabi.linkedHadiths.size})",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        items(sahabi.linkedHadiths) { ref ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.25f)),
                modifier = Modifier.fillMaxWidth().testTag("linked_hadith_${ref.collection}_${ref.hadithNumber}")
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = EmeraldPrimary.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "${ref.collection.uppercase()} #${ref.hadithNumber}",
                                color = EmeraldPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFF2E7D32).copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = ref.grade,
                                color = Color(0xFF2E7D32),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Text(
                        text = "Narrated by: ${ref.narrator}",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = ref.arabicText,
                        fontSize = 18.sp,
                        color = EmeraldPrimary,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 28.sp,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    )

                    Text(
                        text = "\"${ref.translation}\"",
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 20.sp
                    )

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                    ) {
                        Text(
                            text = "Historical Context: ${ref.contextDescription}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(10.dp)
                        )
                    }

                    // Button to jump to Hadith in library
                    Button(
                        onClick = { onOpenHadith(ref.collection, ref.hadithNumber) },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                        modifier = Modifier.fillMaxWidth().testTag("open_hadith_button_${ref.collection}_${ref.hadithNumber}")
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
                            Text(
                                text = "Open in Hadith Library (#${ref.hadithNumber})",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
