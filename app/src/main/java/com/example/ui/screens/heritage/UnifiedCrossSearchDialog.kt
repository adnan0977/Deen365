package com.example.ui.screens.heritage

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.domain.SearchCorpusType
import com.example.domain.UnifiedSearchResult
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.viewmodel.DeenViewModel

@Composable
fun UnifiedCrossSearchDialog(
    isOpen: Boolean,
    query: String,
    results: List<UnifiedSearchResult>,
    activeFilter: SearchCorpusType,
    onQueryChange: (String) -> Unit,
    onFilterChange: (SearchCorpusType) -> Unit,
    onSelectResult: (UnifiedSearchResult) -> Unit,
    onDismiss: () -> Unit
) {
    if (!isOpen) return

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.85f)
                .testTag("unified_search_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Unified Islamic Search",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Search across Quran, Hadith, Heritage & Books",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_unified_search_button")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(Modifier.height(12.dp))

                // Search Input Field
                OutlinedTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("unified_search_input_field"),
                    placeholder = { Text("Search prophet, hadith, concept, book...", fontSize = 13.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = EmeraldPrimary
                        )
                    },
                    trailingIcon = {
                        if (query.isNotBlank()) {
                            IconButton(onClick = { onQueryChange("") }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(Modifier.height(10.dp))

                // Corpus Filter Chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(SearchCorpusType.values()) { filter ->
                        val isSelected = filter == activeFilter
                        FilterChip(
                            selected = isSelected,
                            onClick = { onFilterChange(filter) },
                            label = { Text(filter.label, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = EmeraldPrimary,
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier.testTag("filter_chip_${filter.name}")
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                // Search Results Count
                Text(
                    text = "${results.size} matches found",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
                )

                Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

                // Results List
                if (results.isEmpty() && query.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🔍", fontSize = 32.sp)
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = "No records found matching '$query'",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(results) { item ->
                            SearchResultItemCard(item = item, onClick = { onSelectResult(item) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchResultItemCard(
    item: UnifiedSearchResult,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .testTag("search_result_${item.referenceId}")
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = when (item.corpusType) {
                        SearchCorpusType.QURAN -> EmeraldPrimary.copy(alpha = 0.15f)
                        SearchCorpusType.HADITH -> GoldAccent.copy(alpha = 0.2f)
                        SearchCorpusType.HERITAGE -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f)
                        SearchCorpusType.BOOKS -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)
                        SearchCorpusType.ALL -> MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                    }
                ) {
                    Text(
                        text = item.corpusType.label,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = when (item.corpusType) {
                            SearchCorpusType.QURAN -> EmeraldPrimary
                            SearchCorpusType.HADITH -> GoldAccent
                            else -> MaterialTheme.colorScheme.primary
                        },
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Text(
                        text = item.metadataBadge,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(Modifier.height(6.dp))

            Text(
                text = item.title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = item.subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (!item.arabicSnippet.isNullOrBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = item.arabicSnippet,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                    color = EmeraldPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(Modifier.height(4.dp))

            Text(
                text = item.snippet,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
