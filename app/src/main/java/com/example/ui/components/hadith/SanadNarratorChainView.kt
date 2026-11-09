package com.example.ui.components.hadith

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.*
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent

/**
 * Visual 'Sanad Narrator Chain' component integrated with Sunnah.com data structures.
 * Features:
 * - Interactive visual node tree with chronological generation levels.
 * - Clickable transmission links showing exact verb methodology (Sama', 'Ard, 'An'anah).
 * - Scholarly grading (Jarh wa Ta'dil) with Ibn Hajar / Adh-Dhahabi verdicts.
 * - Search & Filter by Tabaqah/role.
 * - Detailed transmission link dialog and narrator biographical dossier.
 */
@Composable
fun SanadNarratorChainView(
    chain: SanadChain,
    onClose: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedViewTab by remember { mutableIntStateOf(0) } // 0 = Visual Tree, 1 = Transmission Links Matrix, 2 = Narrator Directory
    var selectedRoleFilter by remember { mutableStateOf<NarratorRole?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    var inspectingLink by remember { mutableStateOf<TransmissionLink?>(null) }
    var inspectingNode by remember { mutableStateOf<SanadNode?>(null) }
    var expandedNodeIds by remember { mutableStateOf(setOf<String>()) }

    val filteredNodes = remember(chain.nodes, selectedRoleFilter, searchQuery) {
        chain.nodes.filter { node ->
            val matchesRole = selectedRoleFilter == null || node.role == selectedRoleFilter
            val matchesSearch = searchQuery.isBlank() ||
                    node.nameEnglish.contains(searchQuery, ignoreCase = true) ||
                    node.nameArabic.contains(searchQuery, ignoreCase = true) ||
                    node.scholarlyGrading.contains(searchQuery, ignoreCase = true) ||
                    node.placeOfResidence.contains(searchQuery, ignoreCase = true)
            matchesRole && matchesSearch
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("sanad_narrator_chain_component")
    ) {
        // Top Header & Metadata Banner
        SanadHeaderBanner(
            chain = chain,
            onClose = onClose
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Scholarly Authenticity & Continuity Card
        SanadHealthSummaryCard(
            chain = chain,
            onInspectLink = { inspectingLink = it }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // View Mode Tab Row (Tree vs Links Matrix vs Directory)
        TabRow(
            selectedTabIndex = selectedViewTab,
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            contentColor = EmeraldPrimary,
            modifier = Modifier.clip(RoundedCornerShape(12.dp))
        ) {
            Tab(
                selected = selectedViewTab == 0,
                onClick = { selectedViewTab = 0 },
                text = { Text("Visual Tree", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.Timeline, contentDescription = null, modifier = Modifier.size(16.dp)) },
                modifier = Modifier.testTag("tab_sanad_tree")
            )
            Tab(
                selected = selectedViewTab == 1,
                onClick = { selectedViewTab = 1 },
                text = { Text("Transmission Links (${chain.links.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.AutoMirrored.Filled.CompareArrows, contentDescription = null, modifier = Modifier.size(16.dp)) },
                modifier = Modifier.testTag("tab_sanad_links")
            )
            Tab(
                selected = selectedViewTab == 2,
                onClick = { selectedViewTab = 2 },
                text = { Text("Narrator Dossier (${chain.nodes.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.Badge, contentDescription = null, modifier = Modifier.size(16.dp)) },
                modifier = Modifier.testTag("tab_sanad_dossier")
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Role Filter & Search Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search narrator or city...", fontSize = 12.sp) },
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
                    .testTag("sanad_narrator_search_input")
            )

            if (selectedRoleFilter != null) {
                FilterChip(
                    selected = true,
                    onClick = { selectedRoleFilter = null },
                    label = { Text("Clear Filter", fontSize = 11.sp) },
                    trailingIcon = { Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(12.dp)) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = EmeraldPrimary.copy(alpha = 0.2f),
                        selectedLabelColor = EmeraldPrimary
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Quick Role Filter Pills
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                FilterChip(
                    selected = selectedRoleFilter == null,
                    onClick = { selectedRoleFilter = null },
                    label = { Text("All (${chain.nodes.size})", fontSize = 11.sp) }
                )
            }
            item {
                FilterChip(
                    selected = selectedRoleFilter == NarratorRole.PROPHET,
                    onClick = { selectedRoleFilter = NarratorRole.PROPHET },
                    label = { Text("Prophet ﷺ", fontSize = 11.sp) }
                )
            }
            item {
                FilterChip(
                    selected = selectedRoleFilter == NarratorRole.COMPANION,
                    onClick = { selectedRoleFilter = NarratorRole.COMPANION },
                    label = { Text("Sahaba", fontSize = 11.sp) }
                )
            }
            item {
                FilterChip(
                    selected = selectedRoleFilter == NarratorRole.SENIOR_TABII || selectedRoleFilter == NarratorRole.TABII,
                    onClick = { selectedRoleFilter = NarratorRole.TABII },
                    label = { Text("Tabi'in", fontSize = 11.sp) }
                )
            }
            item {
                FilterChip(
                    selected = selectedRoleFilter == NarratorRole.COMPILER,
                    onClick = { selectedRoleFilter = NarratorRole.COMPILER },
                    label = { Text("Compiler", fontSize = 11.sp) }
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Main Tab Content
        when (selectedViewTab) {
            0 -> {
                // Tab 0: Visual Tree
                SanadVisualTreeView(
                    chain = chain,
                    filteredNodes = filteredNodes,
                    expandedNodeIds = expandedNodeIds,
                    onToggleExpand = { id ->
                        expandedNodeIds = if (expandedNodeIds.contains(id)) expandedNodeIds - id else expandedNodeIds + id
                    },
                    onInspectNode = { inspectingNode = it },
                    onInspectLink = { inspectingLink = it }
                )
            }
            1 -> {
                // Tab 1: Transmission Links Matrix
                SanadTransmissionLinksMatrixView(
                    chain = chain,
                    onInspectLink = { inspectingLink = it }
                )
            }
            2 -> {
                // Tab 2: Narrator Dossier Directory
                SanadNarratorDirectoryView(
                    nodes = filteredNodes,
                    onInspectNode = { inspectingNode = it }
                )
            }
        }
    }

    // Modal: Transmission Link Inspector
    inspectingLink?.let { link ->
        TransmissionLinkDetailDialog(
            link = link,
            fromNode = chain.nodes.find { it.nodeId == link.fromNodeId },
            toNode = chain.nodes.find { it.nodeId == link.toNodeId },
            onDismiss = { inspectingLink = null }
        )
    }

    // Modal: Narrator Full Dossier
    inspectingNode?.let { node ->
        NarratorFullDossierDialog(
            node = node,
            onDismiss = { inspectingNode = null }
        )
    }
}

/**
 * Top header banner with collection badge and Sunnah.com tag.
 */
@Composable
private fun SanadHeaderBanner(
    chain: SanadChain,
    onClose: (() -> Unit)? = null
) {
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
                        imageVector = Icons.Default.AccountTree,
                        contentDescription = "Sanad Chain",
                        tint = EmeraldPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Column {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Sanad Narrator Chain",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = GoldAccent.copy(alpha = 0.2f),
                        border = BorderStroke(0.5.dp, GoldAccent)
                    ) {
                        Text(
                            text = "Sunnah.com Data",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF9E7100),
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                    }
                }
                Text(
                    text = "${chain.collectionName} • Hadith #${chain.hadithNumber} • ${chain.chapterTitle}",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        if (onClose != null) {
            IconButton(
                onClick = onClose,
                modifier = Modifier.testTag("close_sanad_component_btn")
            ) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

/**
 * Health & continuity summary card showcasing unbroken chain verification.
 */
@Composable
private fun SanadHealthSummaryCard(
    chain: SanadChain,
    onInspectLink: (TransmissionLink) -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = EmeraldPrimary.copy(alpha = 0.08f)
        ),
        border = BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.25f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Row 1: Chain Grade & Verified Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = null,
                        tint = EmeraldPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = chain.chainGrade,
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = EmeraldPrimary
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = EmeraldPrimary.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "${chain.nodes.size} Narrators • ${chain.spanYears} yrs",
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldPrimary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Text(
                text = chain.scholarsCommentary,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, lineHeight = 17.sp),
                color = MaterialTheme.colorScheme.onSurface
            )

            HorizontalDivider(thickness = 0.5.dp, color = EmeraldPrimary.copy(alpha = 0.2f))

            // Quick Stats Bar: Continuity, Uluww (Chain elevation), Focal Point
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SanadMetricPill(
                    icon = "⛓️",
                    label = "Continuity",
                    value = if (chain.isContinuityVerified) "Muttasil (Unbroken)" else "Munqati'"
                )
                SanadMetricPill(
                    icon = "📈",
                    label = "Chain Distance",
                    value = "${chain.nodes.size - 1} Links (High Isnad)"
                )
                SanadMetricPill(
                    icon = "🎯",
                    label = "Madar al-Isnad",
                    value = chain.nodes.find { it.isFocalPoint }?.nameEnglish?.take(14) ?: "Direct"
                )
            }
        }
    }
}

@Composable
private fun SanadMetricPill(
    icon: String,
    label: String,
    value: String
) {
    Column(horizontalAlignment = Alignment.Start) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(icon, fontSize = 11.sp)
            Text(label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Text(
            text = value,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1
        )
    }
}

/**
 * Tab 0: Visual Tree with interactive nodes and clickable transmission verbs.
 */
@Composable
private fun SanadVisualTreeView(
    chain: SanadChain,
    filteredNodes: List<SanadNode>,
    expandedNodeIds: Set<String>,
    onToggleExpand: (String) -> Unit,
    onInspectNode: (SanadNode) -> Unit,
    onInspectLink: (TransmissionLink) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 600.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        itemsIndexed(filteredNodes) { index, node ->
            val isLast = index == filteredNodes.size - 1
            val correspondingLink = if (index < chain.links.size) chain.links[index] else null

            VisualSanadNodeItem(
                node = node,
                link = correspondingLink,
                isLast = isLast,
                isExpanded = expandedNodeIds.contains(node.nodeId),
                onToggleExpand = { onToggleExpand(node.nodeId) },
                onInspectNode = { onInspectNode(node) },
                onInspectLink = { correspondingLink?.let(onInspectLink) }
            )
        }
    }
}

@Composable
private fun VisualSanadNodeItem(
    node: SanadNode,
    link: TransmissionLink?,
    isLast: Boolean,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onInspectNode: () -> Unit,
    onInspectLink: () -> Unit
) {
    val tierColor = when (node.gradeTier) {
        ScholarlyGradeTier.GOLD -> GoldAccent
        ScholarlyGradeTier.EMERALD -> EmeraldPrimary
        ScholarlyGradeTier.AMBER -> Color(0xFFD97706)
        ScholarlyGradeTier.PURPLE -> MaterialTheme.colorScheme.primary
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("sanad_node_${node.nodeId}"),
        verticalAlignment = Alignment.Top
    ) {
        // Vertical Tree Connector Column (Node circle + connector line + clickable link pill)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(44.dp)
        ) {
            // Node Generation Circle
            Surface(
                shape = CircleShape,
                color = tierColor,
                shadowElevation = 2.dp,
                modifier = Modifier
                    .size(28.dp)
                    .clickable { onInspectNode() }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "${node.generationLevel}",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = if (node.gradeTier == ScholarlyGradeTier.GOLD) Color.Black else Color.White
                    )
                }
            }

            if (!isLast) {
                // Interactive Transmission Link Verb Pill embedded on connector line
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(28.dp)
                        .background(tierColor.copy(alpha = 0.4f))
                )

                // Interactive Transmission Verb Chip
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = BorderStroke(0.5.dp, tierColor),
                    modifier = Modifier
                        .clickable { onInspectLink() }
                        .testTag("transmission_link_pill_${node.nodeId}")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = link?.formulaArabic ?: "عَنْ",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = tierColor
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(28.dp)
                        .background(tierColor.copy(alpha = 0.4f))
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Narrator Content Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isExpanded) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                else MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = if (isExpanded) 3.dp else 1.dp),
            border = BorderStroke(
                width = if (isExpanded) 1.5.dp else 0.5.dp,
                color = if (isExpanded) tierColor else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = if (isLast) 12.dp else 16.dp)
                .clickable { onToggleExpand() }
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Top Row: Role, Sunnah ID, Death Year
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
                            color = tierColor.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = node.role.titleEnglish,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = tierColor,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        if (node.sunnahNarratorId != null && node.sunnahNarratorId > 0) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Text(
                                    text = "#${node.sunnahNarratorId}",
                                    fontSize = 9.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                    ) {
                        Text(
                            text = if (node.yearPassedAH > 0) "${node.yearPassedAH} AH" else "Era of Revelation",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                // Arabic Name
                Text(
                    text = node.nameArabic,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    ),
                    textAlign = TextAlign.Right,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.fillMaxWidth()
                )

                // English Name
                Text(
                    text = node.nameEnglish,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Scholarly Grading Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = node.scholarlyGrading,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        ),
                        color = EmeraldPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (isExpanded) "Less" else "Inspect Dossier",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = tierColor
                        )
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null,
                            tint = tierColor,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // Expandable Section: Full Scholarly Dossier
                AnimatedVisibility(
                    visible = isExpanded,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        HorizontalDivider(
                            thickness = 0.5.dp,
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                        )

                        // Location & Tabaqah
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(13.dp))
                                Text(
                                    text = node.placeOfResidence,
                                    fontSize = 11.5.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            if (node.tabaqah.isNotBlank()) {
                                Text(
                                    text = node.tabaqah,
                                    fontSize = 10.5.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        // Biography
                        Text(
                            text = node.briefBiography,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, lineHeight = 17.sp),
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        // Jarh wa Ta'dil Evaluations
                        if (node.evaluations.isNotEmpty()) {
                            Text(
                                text = "Classical Scholarly Evaluations (Jarh wa Ta'dil):",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            node.evaluations.forEach { eval ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                        Text(
                                            text = "• ${eval.scholarName} (${eval.sourceBook})",
                                            fontSize = 10.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = EmeraldPrimary
                                        )
                                        Text(
                                            text = eval.verdictArabic,
                                            fontSize = 11.5.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            textAlign = TextAlign.Right,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                        Text(
                                            text = "\"${eval.verdictEnglish}\"",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }

                        // Action Buttons: Open Full Dossier & Copy Citation
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(
                                onClick = onInspectNode,
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Full Sunnah.com Profile", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Tab 1: Transmission Links Matrix showing connection verification between each student and sheikh.
 */
@Composable
private fun SanadTransmissionLinksMatrixView(
    chain: SanadChain,
    onInspectLink: (TransmissionLink) -> Unit
) {
    if (chain.links.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No detailed transmission links available for this narration.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 600.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(chain.links) { link ->
            val fromNode = chain.nodes.find { it.nodeId == link.fromNodeId }
            val toNode = chain.nodes.find { it.nodeId == link.toNodeId }

            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.2f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onInspectLink(link) }
                    .testTag("transmission_matrix_card_${link.linkId}")
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Transmitter -> Receiver Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = EmeraldPrimary.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = link.method.englishTerm,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldPrimary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = link.historicalMeetingCity,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    // Transmission Visual Flow with Arabic Verb
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Receiving Student
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Receiver (Student)",
                                fontSize = 9.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = fromNode?.nameEnglish?.take(22) ?: link.fromNodeId,
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        // Formula Pill Center
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = EmeraldPrimary,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = link.formulaArabic,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = link.formulaEnglish.take(16),
                                    fontSize = 8.sp,
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                            }
                        }

                        // Transmitting Sheikh
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.End
                        ) {
                            Text(
                                text = "Source (Sheikh)",
                                fontSize = 9.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = toNode?.nameEnglish?.take(22) ?: link.toNodeId,
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                textAlign = TextAlign.Right,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Text(
                        text = link.continuityNotes,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, lineHeight = 15.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/**
 * Tab 2: Narrator Directory View showcasing classical profiles.
 */
@Composable
private fun SanadNarratorDirectoryView(
    nodes: List<SanadNode>,
    onInspectNode: (SanadNode) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 600.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(nodes) { node ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onInspectNode(node) }
                    .testTag("narrator_directory_card_${node.nodeId}")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = when (node.gradeTier) {
                                ScholarlyGradeTier.GOLD -> GoldAccent
                                ScholarlyGradeTier.EMERALD -> EmeraldPrimary
                                else -> MaterialTheme.colorScheme.primary
                            },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "${node.generationLevel}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = if (node.gradeTier == ScholarlyGradeTier.GOLD) Color.Black else Color.White
                                )
                            }
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = node.nameEnglish,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = node.scholarlyGrading,
                                fontSize = 11.sp,
                                color = EmeraldPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "${node.role.titleEnglish} • ${if (node.yearPassedAH > 0) "${node.yearPassedAH} AH" else "Prophetic Era"} • ${node.placeOfResidence}",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Details",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

/**
 * Detailed modal dialog for inspecting an individual transmission link between two narrators.
 */
@Composable
fun TransmissionLinkDetailDialog(
    link: TransmissionLink,
    fromNode: SanadNode?,
    toNode: SanadNode?,
    onDismiss: () -> Unit
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
                .fillMaxWidth(0.92f)
                .padding(16.dp)
                .testTag("transmission_link_detail_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header
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
                            color = EmeraldPrimary.copy(alpha = 0.15f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.AutoMirrored.Filled.CompareArrows, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(18.dp))
                            }
                        }
                        Column {
                            Text(
                                text = "Transmission Link Analysis",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Sunnah.com Isnad Continuity Verification",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                // Formula Hero Card
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = EmeraldPrimary.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = link.formulaArabic,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldPrimary
                        )
                        Text(
                            text = link.formulaEnglish,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = EmeraldPrimary,
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Text(
                                text = link.method.rank,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                // Student & Sheikh Profile Strip
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Transmitter (Student):", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(fromNode?.nameEnglish ?: link.fromNodeId, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        Text(fromNode?.placeOfResidence ?: "", fontSize = 10.sp, color = EmeraldPrimary)
                    }
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(16.dp))
                    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                        Text("Source (Sheikh):", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(toNode?.nameEnglish ?: link.toNodeId, fontSize = 12.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Right, color = MaterialTheme.colorScheme.onSurface)
                        Text(toNode?.placeOfResidence ?: "", fontSize = 10.sp, textAlign = TextAlign.Right, color = EmeraldPrimary)
                    }
                }

                // Methodology Rules & Continuity
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Hadith Methodology Rule (Usul al-Hadith):", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Text(
                        text = link.method.description,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp, lineHeight = 16.sp),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Historical Meeting Verification:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Text(
                        text = "• Meeting City: ${link.historicalMeetingCity}\n• ${link.continuityNotes}",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp, lineHeight = 16.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Got It", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

/**
 * Full biographical dossier modal dialog for a narrator.
 */
@Composable
fun NarratorFullDossierDialog(
    node: SanadNode,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val tierColor = when (node.gradeTier) {
        ScholarlyGradeTier.GOLD -> GoldAccent
        ScholarlyGradeTier.EMERALD -> EmeraldPrimary
        ScholarlyGradeTier.AMBER -> Color(0xFFD97706)
        ScholarlyGradeTier.PURPLE -> MaterialTheme.colorScheme.primary
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.85f)
                .padding(16.dp)
                .testTag("narrator_full_dossier_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Surface(
                            shape = CircleShape,
                            color = tierColor.copy(alpha = 0.15f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "${node.generationLevel}",
                                    fontWeight = FontWeight.Bold,
                                    color = tierColor
                                )
                            }
                        }
                        Column {
                            Text(
                                text = "Sunnah.com Narrator Dossier",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "ID: ${node.sunnahNarratorId ?: "Canonical"} • ${node.role.titleEnglish}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        // Hero Names Card
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            border = BorderStroke(1.dp, tierColor.copy(alpha = 0.3f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = node.nameArabic,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Right,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Text(
                                    text = node.nameEnglish,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                if (node.kunya.isNotBlank()) {
                                    Text(
                                        text = "Kunya: ${node.kunya}",
                                        fontSize = 11.sp,
                                        color = EmeraldPrimary,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }

                    item {
                        // Key Metrics Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text("Death Year", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(
                                        text = if (node.yearPassedAH > 0) "${node.yearPassedAH} AH" else "11 AH (Era)",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text("Residence", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(
                                        text = node.placeOfResidence,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text("Narrations", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(
                                        text = if (node.totalHadithsNarrated > 0) "${node.totalHadithsNarrated}+" else "Canonical",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EmeraldPrimary
                                    )
                                }
                            }
                        }
                    }

                    item {
                        // Scholarly Grading Banner
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = tierColor.copy(alpha = 0.12f),
                            border = BorderStroke(1.dp, tierColor.copy(alpha = 0.3f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("Scholarly Rank & Reliability:", fontSize = 11.sp, color = tierColor, fontWeight = FontWeight.Bold)
                                Text(
                                    text = node.scholarlyGrading,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = node.gradeTier.label,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    item {
                        // Biography
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Biography & Historical Contribution:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            Text(
                                text = node.briefBiography,
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.5.sp, lineHeight = 18.sp),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    if (node.evaluations.isNotEmpty()) {
                        item {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("Jarh wa Ta'dil Classical Verdicts:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                node.evaluations.forEach { eval ->
                                    Card(
                                        shape = RoundedCornerShape(10.dp),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 6.dp)
                                    ) {
                                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(eval.scholarName, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EmeraldPrimary)
                                                Text(eval.sourceBook, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            }
                                            Text(
                                                text = eval.verdictArabic,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                textAlign = TextAlign.Right,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                            Text("\"${eval.verdictEnglish}\"", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (node.notableTeachers.isNotEmpty() || node.notableStudents.isNotEmpty()) {
                        item {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("Scholarly Network:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                if (node.notableTeachers.isNotEmpty()) {
                                    Text("• Teachers: ${node.notableTeachers.joinToString(", ")}", fontSize = 11.5.sp, color = MaterialTheme.colorScheme.onSurface)
                                }
                                if (node.notableStudents.isNotEmpty()) {
                                    Text("• Students: ${node.notableStudents.joinToString(", ")}", fontSize = 11.5.sp, color = MaterialTheme.colorScheme.onSurface)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Footer Buttons: Copy Bio & Dismiss
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val text = "${node.nameArabic}\n${node.nameEnglish}\nRank: ${node.scholarlyGrading}\n${node.briefBiography}"
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Narrator", text))
                            Toast.makeText(context, "Narrator info copied to clipboard", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Copy", fontSize = 12.sp)
                    }

                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Close", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

/**
 * Inline mini Sanad preview ribbon designed to be embedded directly into Hadith reader cards.
 */
@Composable
fun MiniSanadPreviewRibbon(
    chain: SanadChain,
    onOpenFullSanad: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        border = BorderStroke(0.5.dp, EmeraldPrimary.copy(alpha = 0.3f)),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onOpenFullSanad() }
            .testTag("mini_sanad_ribbon_${chain.hadithNumber}")
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text("⛓️", fontSize = 12.sp)
                    Text(
                        text = "Sanad Chain (${chain.nodes.size} Narrators • ${chain.authenticityRating})",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = EmeraldPrimary
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = "Inspect",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldPrimary
                    )
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = EmeraldPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            // Horizontal flow ribbon of nodes
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                chain.nodes.forEachIndexed { idx, node ->
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = when (node.gradeTier) {
                            ScholarlyGradeTier.GOLD -> GoldAccent.copy(alpha = 0.2f)
                            ScholarlyGradeTier.EMERALD -> EmeraldPrimary.copy(alpha = 0.15f)
                            else -> MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                        }
                    ) {
                        Text(
                            text = node.nameEnglish.split(" ").take(2).joinToString(" "),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    if (idx < chain.nodes.size - 1) {
                        Text(
                            text = "➔",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
