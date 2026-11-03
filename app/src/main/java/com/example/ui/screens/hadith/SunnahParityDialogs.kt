package com.example.ui.screens.hadith

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.seed.ImamBiography
import com.example.data.seed.NarratorDetail
import com.example.data.seed.SunnahExtraData
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FilterAltOff
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import com.example.data.seed.GharibGlossaryEntry
import com.example.data.seed.GharibHadithData

@Composable
fun GharibHadithGlossaryDialog(
    isOpen: Boolean,
    searchQuery: String = "",
    selectedCategory: String = "All Categories",
    onSearchQueryChange: (String) -> Unit = {},
    onCategoryChange: (String) -> Unit = {},
    onJumpToHadith: ((String, Int) -> Unit)? = null,
    onDismiss: () -> Unit
) {
    if (!isOpen) return

    var localQuery by remember(searchQuery) { mutableStateOf(searchQuery) }
    var localCategory by remember(selectedCategory) { mutableStateOf(selectedCategory) }
    var expandedEntryId by remember { mutableStateOf<String?>(null) }

    val displayedEntries = remember(localQuery, localCategory) {
        GharibHadithData.searchGlossary(localQuery, localCategory)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .fillMaxHeight(0.92f)
                .testTag("gharib_glossary_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(GoldAccent.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.AutoStories, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(22.dp))
                        }
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "Gharib al-Hadith Glossary",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = EmeraldPrimary.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = "غَرِيبُ الْحَدِيث",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EmeraldPrimary),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Classical Arabic vocabulary & rare Hadith terminology (An-Nihayah)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_gharib_glossary_dialog")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                // Search Bar
                OutlinedTextField(
                    value = localQuery,
                    onValueChange = {
                        localQuery = it
                        onSearchQueryChange(it)
                    },
                    placeholder = { Text("Search terms, roots, meanings (e.g. Mufarridoon, غ-ر-ر)...", fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = EmeraldPrimary) },
                    trailingIcon = {
                        if (localQuery.isNotEmpty()) {
                            IconButton(onClick = {
                                localQuery = ""
                                onSearchQueryChange("")
                            }) {
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
                        .testTag("gharib_search_input")
                )

                // Category Filter Chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(GharibHadithData.CATEGORIES) { cat ->
                        val isSelected = localCategory == cat
                        val count = if (cat == "All Categories") GharibHadithData.ENTRIES.size else GharibHadithData.ENTRIES.count { it.category.equals(cat, ignoreCase = true) }
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                localCategory = cat
                                onCategoryChange(cat)
                            },
                            label = { Text("$cat ($count)", fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = EmeraldPrimary,
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier.testTag("gharib_category_chip_${cat.replace(" ", "_")}")
                        )
                    }
                }

                // Status Count & Sourcing Indicator
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Showing ${displayedEntries.size} of ${GharibHadithData.ENTRIES.size} classical terms",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Source: Ibn al-Athir",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, color = GoldAccent, fontWeight = FontWeight.SemiBold)
                    )
                }

                // Glossary Entries List
                if (displayedEntries.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(24.dp)
                        ) {
                            Icon(Icons.Default.FilterAltOff, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(36.dp))
                            Text(
                                text = "No classical terms match '$localQuery'",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Try searching by Arabic root letters (e.g. غ-ر-ر) or English keywords.",
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Button(
                                onClick = {
                                    localQuery = ""
                                    localCategory = "All Categories"
                                    onSearchQueryChange("")
                                    onCategoryChange("All Categories")
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                            ) {
                                Text("Reset Filters", fontSize = 12.sp)
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(bottom = 12.dp)
                    ) {
                        items(displayedEntries) { entry ->
                            val isExpanded = expandedEntryId == entry.id
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isExpanded) EmeraldPrimary.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.18f)
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        expandedEntryId = if (isExpanded) null else entry.id
                                    }
                                    .testTag("gharib_card_${entry.id}")
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    // Top Row: Root Badge, Category Pill, and Arabic Term
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
                                                color = GoldAccent.copy(alpha = 0.18f)
                                            ) {
                                                Text(
                                                    text = "جِذْر: ${entry.rootArabic}",
                                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary),
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = EmeraldPrimary.copy(alpha = 0.12f)
                                            ) {
                                                Text(
                                                    text = entry.category,
                                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, color = EmeraldPrimary, fontWeight = FontWeight.SemiBold),
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }

                                        Text(
                                            text = entry.termArabic,
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = EmeraldPrimary
                                            ),
                                            textAlign = TextAlign.Right
                                        )
                                    }

                                    // Transliteration & Brief Meaning
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = entry.termTransliteration,
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Icon(
                                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                            contentDescription = if (isExpanded) "Collapse" else "Expand",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }

                                    Text(
                                        text = entry.briefMeaning,
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    // Expandable Detailed Classical Analysis & Hadith Citations
                                    AnimatedVisibility(visible = isExpanded) {
                                        Column(
                                            verticalArrangement = Arrangement.spacedBy(8.dp),
                                            modifier = Modifier.padding(top = 6.dp)
                                        ) {
                                            HorizontalDivider(
                                                thickness = 0.5.dp,
                                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                                            )

                                            // Lexicographical Analysis
                                            Text(
                                                text = "Lexicographical Analysis:",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = EmeraldPrimary)
                                            )
                                            Text(
                                                text = entry.detailedExplanation,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )

                                            // Classical Source Reference
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Text(
                                                    text = "📖 Lexicon: ${entry.classicalSource}",
                                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant),
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                )
                                            }

                                            // Hadith Occurrences
                                            if (entry.references.isNotEmpty()) {
                                                Text(
                                                    text = "Contextual Hadith Occurrence:",
                                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = GoldAccent)
                                                )

                                                entry.references.forEach { ref ->
                                                    Card(
                                                        shape = RoundedCornerShape(10.dp),
                                                        colors = CardDefaults.cardColors(
                                                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                                                        ),
                                                        border = androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
                                                        modifier = Modifier.fillMaxWidth()
                                                    ) {
                                                        Column(
                                                            modifier = Modifier.padding(10.dp),
                                                            verticalArrangement = Arrangement.spacedBy(4.dp)
                                                        ) {
                                                            Row(
                                                                modifier = Modifier.fillMaxWidth(),
                                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                                verticalAlignment = Alignment.CenterVertically
                                                            ) {
                                                                Text(
                                                                    text = "${ref.collection} #${ref.hadithNumber}",
                                                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                                                )

                                                                if (onJumpToHadith != null) {
                                                                    TextButton(
                                                                        onClick = { onJumpToHadith(ref.collectionId, ref.hadithNumber) },
                                                                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                                                        modifier = Modifier.height(26.dp)
                                                                    ) {
                                                                        Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(12.dp), tint = EmeraldPrimary)
                                                                        Spacer(Modifier.width(3.dp))
                                                                        Text("View in Library", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = EmeraldPrimary)
                                                                    }
                                                                }
                                                            }

                                                            Text(
                                                                text = ref.arabicSnippet,
                                                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold, color = EmeraldPrimary),
                                                                textAlign = TextAlign.Right,
                                                                modifier = Modifier.fillMaxWidth()
                                                            )

                                                            Text(
                                                                text = ref.englishContext,
                                                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                                                color = MaterialTheme.colorScheme.onSurfaceVariant
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
                    }
                }
            }
        }
    }
}

@Composable
fun ImamBiographiesDialog(
    isOpen: Boolean,
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
                .testTag("imam_biographies_dialog")
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Hadith Compilers & Imams",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Biographies & Authentication Methodologies",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(Modifier.height(10.dp))

                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(SunnahExtraData.IMAM_BIOGRAPHIES) { imam ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = imam.name,
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                        )
                                        Text(
                                            text = "${imam.majorWork} • ${imam.lifespan}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = EmeraldPrimary
                                        )
                                    }
                                    Text(
                                        text = imam.arabicName,
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = EmeraldPrimary
                                        )
                                    )
                                }

                                Spacer(Modifier.height(6.dp))

                                Text(
                                    text = "Compilation Methodology:",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = imam.methodology,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Spacer(Modifier.height(4.dp))

                                Text(
                                    text = "\"${imam.memorableQuote}\"",
                                    style = MaterialTheme.typography.labelSmall.copy(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NarratorBioDialog(
    narrator: NarratorDetail?,
    onDismiss: () -> Unit
) {
    if (narrator == null) return

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(narrator.nameEnglish, fontWeight = FontWeight.Bold)
                Text(narrator.nameArabic, color = EmeraldPrimary, fontSize = 14.sp)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = GoldAccent.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = "Total Narrations: ${narrator.totalNarrationsCount} Ahadith",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Text(
                    text = "Scholarly Grading: ${narrator.scholarlyGrading}",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = EmeraldPrimary
                )

                Text(
                    text = narrator.briefBiography,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "Passed: ${narrator.yearPassedAH} AH in Madinah",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
fun GoToHadithDialog(
    isOpen: Boolean,
    onJump: (String) -> Unit,
    onDismiss: () -> Unit
) {
    if (!isOpen) return

    var input by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Go to Hadith Reference", fontWeight = FontWeight.Bold)
        },
        text = {
            Column {
                Text(
                    text = "Enter collection name and number (e.g. bukhari:1, muslim:45, nawawi:1):",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    placeholder = { Text("e.g. bukhari:1") },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().testTag("go_to_hadith_input")
                )
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("bukhari:1", "muslim:1", "nawawi:1", "tirmidhi:1").forEach { preset ->
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = EmeraldPrimary.copy(alpha = 0.15f),
                            modifier = Modifier.clickable { input = preset }
                        ) {
                            Text(
                                text = preset,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldPrimary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (input.isNotBlank()) onJump(input)
                },
                modifier = Modifier.testTag("confirm_go_to_hadith_button")
            ) {
                Text("Jump")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
