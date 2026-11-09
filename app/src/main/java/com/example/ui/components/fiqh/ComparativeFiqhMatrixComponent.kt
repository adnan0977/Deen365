package com.example.ui.components.fiqh

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ComparativeFiqhTopic
import com.example.data.model.Madhab
import com.example.data.model.MadhabRuling
import com.example.data.seed.ComparativeFiqhSeedData
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent

/**
 * Interactive Comparative Fiqh Matrix Component allowing users to toggle between
 * the four major Sunni Madhabs (Hanafi, Maliki, Shafi'i, Hanbali) for selected
 * jurisprudential topics with scriptural evidences, legal grades, and classical citations.
 */
@Composable
fun ComparativeFiqhMatrixComponent(
    initialTopicId: String? = null,
    onClose: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val allTopics = remember { ComparativeFiqhSeedData.TOPICS }
    var selectedTopic by remember {
        mutableStateOf(allTopics.firstOrNull { it.topicId == initialTopicId } ?: allTopics.first())
    }

    var selectedCategoryFilter by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }
    var selectedViewMode by remember { mutableIntStateOf(0) } // 0 = Comparative Cards, 1 = Matrix Table, 2 = School Foundations

    // Interactive Madhab Toggles: set of active/selected Madhabs (defaults to all 4 selected)
    var activeMadhabs by remember { mutableStateOf(setOf(Madhab.HANAFI, Madhab.MALIKI, Madhab.SHAFII, Madhab.HANBALI)) }

    val categories = remember(allTopics) {
        listOf("All") + allTopics.map { it.category }.distinct()
    }

    val filteredTopics = remember(allTopics, selectedCategoryFilter, searchQuery) {
        allTopics.filter { topic ->
            val matchesCategory = selectedCategoryFilter == "All" || topic.category == selectedCategoryFilter
            val matchesSearch = searchQuery.isBlank() ||
                    topic.title.contains(searchQuery, ignoreCase = true) ||
                    topic.coreQuestion.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesSearch
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("comparative_fiqh_matrix_component")
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = EmeraldPrimary.copy(alpha = 0.15f),
                    modifier = Modifier.size(42.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Balance,
                            contentDescription = "Comparative Fiqh",
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Comparative Fiqh Matrix",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = GoldAccent.copy(alpha = 0.2f),
                            border = BorderStroke(0.5.dp, GoldAccent)
                        ) {
                            Text(
                                text = "4 Sunni Madhabs",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF9E7100),
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text(
                        text = "Scholarly convergence and divergence (فقه الخلاف والأدلة)",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (onClose != null) {
                IconButton(
                    onClick = onClose,
                    modifier = Modifier.testTag("close_fiqh_matrix_btn")
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Search & Category Filter
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search topics, wudu, salah...", fontSize = 12.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp)) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear", modifier = Modifier.size(14.dp))
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = EmeraldPrimary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("fiqh_topic_search_input")
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Category Filter Pills
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(categories) { cat ->
                FilterChip(
                    selected = selectedCategoryFilter == cat,
                    onClick = { selectedCategoryFilter = cat },
                    label = { Text(cat, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = EmeraldPrimary.copy(alpha = 0.15f),
                        selectedLabelColor = EmeraldPrimary
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Horizontal Topic Selector Pills
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(filteredTopics) { topic ->
                val isSelected = selectedTopic.topicId == topic.topicId
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedTopic = topic },
                    label = {
                        Text(
                            text = topic.title,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = EmeraldPrimary,
                        selectedLabelColor = Color.White
                    ),
                    modifier = Modifier.testTag("fiqh_topic_chip_${topic.topicId}")
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Interactive Madhab Toggles Bar (Hanafi, Maliki, Shafi'i, Hanbali)
        MadhabToggleRibbon(
            activeMadhabs = activeMadhabs,
            onToggleMadhab = { madhab ->
                activeMadhabs = if (activeMadhabs.contains(madhab)) {
                    // Prevent deselecting all; keep at least one active
                    if (activeMadhabs.size > 1) activeMadhabs - madhab else activeMadhabs
                } else {
                    activeMadhabs + madhab
                }
            },
            onSelectAll = {
                activeMadhabs = setOf(Madhab.HANAFI, Madhab.MALIKI, Madhab.SHAFII, Madhab.HANBALI)
            },
            onIsolateSchool = { madhab ->
                activeMadhabs = setOf(madhab)
            }
        )

        Spacer(modifier = Modifier.height(10.dp))

        // View Mode Switcher TabRow
        TabRow(
            selectedTabIndex = selectedViewMode,
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            contentColor = EmeraldPrimary,
            modifier = Modifier.clip(RoundedCornerShape(12.dp))
        ) {
            Tab(
                selected = selectedViewMode == 0,
                onClick = { selectedViewMode = 0 },
                text = { Text("Rulings & Evidence", fontSize = 11.5.sp, fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.ViewAgenda, contentDescription = null, modifier = Modifier.size(16.dp)) },
                modifier = Modifier.testTag("tab_fiqh_rulings")
            )
            Tab(
                selected = selectedViewMode == 1,
                onClick = { selectedViewMode = 1 },
                text = { Text("At-a-Glance Matrix", fontSize = 11.5.sp, fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.GridOn, contentDescription = null, modifier = Modifier.size(16.dp)) },
                modifier = Modifier.testTag("tab_fiqh_matrix")
            )
            Tab(
                selected = selectedViewMode == 2,
                onClick = { selectedViewMode = 2 },
                text = { Text("Foundations (Usul)", fontSize = 11.5.sp, fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(16.dp)) },
                modifier = Modifier.testTag("tab_fiqh_usul")
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Core Question & Consensus Banner
        CoreQuestionBanner(selectedTopic = selectedTopic)

        Spacer(modifier = Modifier.height(10.dp))

        // Content Area based on selected view mode
        when (selectedViewMode) {
            0 -> {
                // View 0: Interactive Comparative Cards
                ComparativeCardsList(
                    topic = selectedTopic,
                    activeMadhabs = activeMadhabs,
                    onCopyRuling = { ruling ->
                        val text = "${selectedTopic.title}\nSchool: ${ruling.madhab.displayName} (${ruling.madhab.arabicName})\nGrade: ${ruling.legalGrade}\n\nRuling: ${ruling.rulingSummary}\n\nEvidence: ${ruling.primaryDalil}\n\nExplanation: ${ruling.detailedExplanation}"
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("Fiqh Ruling", text))
                        Toast.makeText(context, "${ruling.madhab.displayName} ruling copied to clipboard", Toast.LENGTH_SHORT).show()
                    }
                )
            }
            1 -> {
                // View 1: At-a-Glance Matrix Table
                ComparativeSummaryMatrixTable(
                    topic = selectedTopic,
                    activeMadhabs = activeMadhabs
                )
            }
            2 -> {
                // View 2: School Foundations (Usul al-Fiqh)
                MadhabFoundationsOverview(
                    activeMadhabs = activeMadhabs
                )
            }
        }
    }
}

/**
 * 4-Madhab Toggle Ribbon with interactive toggle chips, color-coded badges, and quick actions.
 */
@Composable
private fun MadhabToggleRibbon(
    activeMadhabs: Set<Madhab>,
    onToggleMadhab: (Madhab) -> Unit,
    onSelectAll: () -> Unit,
    onIsolateSchool: (Madhab) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Toggle Madhabs to Compare (${activeMadhabs.size}/4):",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (activeMadhabs.size < 4) {
                    Text(
                        text = "Show All 4",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldPrimary,
                        modifier = Modifier
                            .clickable { onSelectAll() }
                            .testTag("fiqh_show_all_btn")
                    )
                }
            }

            // The 4 interactive toggle chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Madhab.entries.forEach { madhab ->
                    val isActive = activeMadhabs.contains(madhab)
                    val madhabColor = Color(madhab.brandColorHex)

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isActive) madhabColor else MaterialTheme.colorScheme.surface,
                        border = BorderStroke(
                            width = if (isActive) 1.5.dp else 1.dp,
                            color = if (isActive) madhabColor else MaterialTheme.colorScheme.outlineVariant
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { onToggleMadhab(madhab) }
                            .testTag("madhab_toggle_${madhab.name.lowercase()}")
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                                if (isActive) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(11.dp)
                                    )
                                }
                                Text(
                                    text = madhab.displayName,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isActive) Color.White else MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1
                                )
                            }
                            Text(
                                text = madhab.arabicName,
                                fontSize = 9.sp,
                                color = if (isActive) Color.White.copy(alpha = 0.85f) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Banner highlighting the Core Juristic Inquiry and Consensus points (Ijma').
 */
@Composable
private fun CoreQuestionBanner(
    selectedTopic: ComparativeFiqhTopic
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.25f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.HelpOutline,
                    contentDescription = null,
                    tint = EmeraldPrimary,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "Core Juristic Inquiry",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = EmeraldPrimary
                )
            }

            Text(
                text = selectedTopic.coreQuestion,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )

            HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text("🤝", fontSize = 12.sp)
                Text(
                    text = "Consensus (Ijma' / Ittifaq): ${selectedTopic.consensusPoints}",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Tab 0: Comparative Cards List displaying rulings for active Madhabs.
 */
@Composable
private fun ComparativeCardsList(
    topic: ComparativeFiqhTopic,
    activeMadhabs: Set<Madhab>,
    onCopyRuling: (MadhabRuling) -> Unit
) {
    val displayedRulings = topic.rulings.filter { activeMadhabs.contains(it.madhab) }

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 600.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(displayedRulings) { ruling ->
            MadhabRulingCard(
                ruling = ruling,
                onCopy = { onCopyRuling(ruling) }
            )
        }

        if (topic.practicalTakeaway.isNotBlank()) {
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = GoldAccent.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, GoldAccent.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("💡", fontSize = 16.sp)
                        Column {
                            Text(
                                text = "Practical Unity & Adab al-Ikhtilaf:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF9E7100)
                            )
                            Text(
                                text = topic.practicalTakeaway,
                                fontSize = 11.5.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Detailed card for an individual school's ruling with Dalil and classical citations.
 */
@Composable
private fun MadhabRulingCard(
    ruling: MadhabRuling,
    onCopy: () -> Unit
) {
    val madhabColor = Color(ruling.madhab.brandColorHex)
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
        border = BorderStroke(1.2.dp, madhabColor.copy(alpha = 0.4f)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("fiqh_ruling_card_${ruling.madhab.name.lowercase()}")
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header Row: Madhab Pill + Era + Legal Grade Pill
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
                        color = madhabColor
                    ) {
                        Text(
                            text = "${ruling.madhab.displayName} (${ruling.madhab.arabicName})",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Text(
                        text = ruling.madhab.era,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = madhabColor.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = ruling.legalGrade,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = madhabColor
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Ruling Summary
            Text(
                text = ruling.rulingSummary,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            // Primary Dalil Evidence Box
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                border = BorderStroke(0.5.dp, madhabColor.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = null,
                            tint = madhabColor,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = "Primary Dalil (Scriptural Evidence):",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.5.sp,
                                color = madhabColor
                            )
                        )
                    }
                    Text(
                        text = ruling.primaryDalil,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp, lineHeight = 16.sp),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Explanation
            Text(
                text = ruling.detailedExplanation,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, lineHeight = 17.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Expandable Classical Reference & Juristic Principle
            if (ruling.classicalManualRef.isNotBlank() || ruling.juristicPrinciple.isNotBlank()) {
                AnimatedVisibility(visible = isExpanded) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        if (ruling.juristicPrinciple.isNotBlank()) {
                            Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("⚖️", fontSize = 11.sp)
                                Column {
                                    Text("Underlying Juristic Principle (Asl al-Madhhab):", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = madhabColor)
                                    Text(ruling.juristicPrinciple, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
                                }
                            }
                        }

                        if (ruling.classicalManualRef.isNotBlank()) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(Icons.Default.Bookmark, contentDescription = null, tint = madhabColor, modifier = Modifier.size(12.dp))
                                Text("Reference: ${ruling.classicalManualRef}", fontSize = 10.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        if (ruling.scholarlyNote.isNotBlank()) {
                            Text(
                                text = "Note: ${ruling.scholarlyNote}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 10.5.sp,
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Footer Action Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isExpanded) "Show Less" else "Juristic Source & Principle",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = madhabColor,
                    modifier = Modifier.clickable { isExpanded = !isExpanded }
                )

                IconButton(
                    onClick = onCopy,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy Ruling",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }
        }
    }
}

/**
 * Tab 1: At-a-Glance Matrix Table comparing rulings across active schools.
 */
@Composable
private fun ComparativeSummaryMatrixTable(
    topic: ComparativeFiqhTopic,
    activeMadhabs: Set<Madhab>
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.2f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Summary Matrix: ${topic.title}",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            // Table Rows for each active school
            topic.rulings.filter { activeMadhabs.contains(it.madhab) }.forEach { ruling ->
                val madhabColor = Color(ruling.madhab.brandColorHex)

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                    border = BorderStroke(0.5.dp, madhabColor.copy(alpha = 0.35f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Surface(shape = RoundedCornerShape(4.dp), color = madhabColor) {
                                    Text(
                                        text = ruling.madhab.displayName,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Text(
                                    text = ruling.legalGrade,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = madhabColor
                                )
                            }
                            Text(
                                text = ruling.rulingSummary,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Tab 2: School Foundations (Usul al-Madhahib) deep-dive.
 */
@Composable
private fun MadhabFoundationsOverview(
    activeMadhabs: Set<Madhab>
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 600.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(Madhab.entries.filter { activeMadhabs.contains(it) }) { madhab ->
            val madhabColor = Color(madhab.brandColorHex)

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, madhabColor.copy(alpha = 0.35f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(shape = RoundedCornerShape(6.dp), color = madhabColor) {
                            Text(
                                text = "${madhab.displayName} School (${madhab.arabicName})",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }

                        Text(
                            text = madhab.era,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Text(
                        text = "Founding Imam: ${madhab.founder}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = "Geographic Center: ${madhab.geographicCenter}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                            Text("Core Jurisprudential Methodology (Usul):", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = madhabColor)
                            Text(madhab.coreMethodology, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }

                    Text(
                        text = "Primary Classical Manual: ${madhab.primaryTextbook}",
                        fontSize = 10.5.sp,
                        color = EmeraldPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
