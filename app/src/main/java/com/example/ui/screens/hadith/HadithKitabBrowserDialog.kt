package com.example.ui.screens.hadith

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.repository.CollectionProgress
import com.example.data.seed.HadithKitabItem
import com.example.data.seed.HadithKitabProgressData
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent

@Composable
fun HadithKitabBrowserDialog(
    isOpen: Boolean,
    completedBookIds: Set<String>,
    collectionProgressList: List<CollectionProgress> = emptyList(),
    onToggleBookCompleted: (String) -> Unit,
    onMarkCollectionCompleted: ((String, Boolean) -> Unit)? = null,
    onClearAllProgress: (() -> Unit)? = null,
    onSelectBook: (HadithKitabItem) -> Unit,
    onDismiss: () -> Unit
) {
    if (!isOpen) return

    val collections = listOf("All", "bukhari", "muslim", "nawawi")
    var selectedCollection by remember { mutableStateOf("All") }

    val books = remember(selectedCollection) {
        HadithKitabProgressData.getBooksForCollection(selectedCollection)
    }

    val totalBooks = books.size
    val completedCount = books.count { completedBookIds.contains(it.id) }
    val progressFraction = if (totalBooks > 0) completedCount.toFloat() / totalBooks.toFloat() else 0f

    // Total stats across all books
    val allBooks = HadithKitabProgressData.PROMINENT_BOOKS
    val overallCompleted = allBooks.count { completedBookIds.contains(it.id) }
    val overallTotal = allBooks.size
    val overallFraction = if (overallTotal > 0) overallCompleted.toFloat() / overallTotal.toFloat() else 0f

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
                .testTag("hadith_kitab_browser_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // 1. Header with Room Storage Tag
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
                            Icon(Icons.Default.MenuBook, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(22.dp))
                        }
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "Kitab & Bab Tracker",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = EmeraldPrimary.copy(alpha = 0.15f)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(3.dp),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Icon(Icons.Default.Storage, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(11.dp))
                                        Text(
                                            text = "Room DB",
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold, color = EmeraldPrimary)
                                        )
                                    }
                                }
                            }
                            Text(
                                text = "Mark Hadith chapters as read with persistent Room progress",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_kitab_browser_dialog")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                // 2. Collection Progress Across Collections (Bukhari, Muslim, Nawawi)
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Progress Across Collections",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "$overallCompleted of $overallTotal Total Chapters Read (${(overallFraction * 100).toInt()}%)",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = EmeraldPrimary)
                            )
                        }

                        LinearProgressIndicator(
                            progress = { overallFraction },
                            color = EmeraldPrimary,
                            trackColor = EmeraldPrimary.copy(alpha = 0.2f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                        )

                        // Mini summary cards for each individual collection
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val bukhariTotal = allBooks.count { it.collectionId.equals("bukhari", ignoreCase = true) }
                            val bukhariDone = allBooks.count { it.collectionId.equals("bukhari", ignoreCase = true) && completedBookIds.contains(it.id) }
                            val bukhariPct = if (bukhariTotal > 0) (bukhariDone * 100 / bukhariTotal) else 0

                            val muslimTotal = allBooks.count { it.collectionId.equals("muslim", ignoreCase = true) }
                            val muslimDone = allBooks.count { it.collectionId.equals("muslim", ignoreCase = true) && completedBookIds.contains(it.id) }
                            val muslimPct = if (muslimTotal > 0) (muslimDone * 100 / muslimTotal) else 0

                            val nawawiTotal = allBooks.count { it.collectionId.equals("nawawi", ignoreCase = true) }
                            val nawawiDone = allBooks.count { it.collectionId.equals("nawawi", ignoreCase = true) && completedBookIds.contains(it.id) }
                            val nawawiPct = if (nawawiTotal > 0) (nawawiDone * 100 / nawawiTotal) else 0

                            CollectionProgressBadge(
                                title = "Bukhari",
                                done = bukhariDone,
                                total = bukhariTotal,
                                percentage = bukhariPct,
                                isSelected = selectedCollection == "bukhari",
                                onClick = { selectedCollection = "bukhari" },
                                modifier = Modifier.weight(1f)
                            )

                            CollectionProgressBadge(
                                title = "Muslim",
                                done = muslimDone,
                                total = muslimTotal,
                                percentage = muslimPct,
                                isSelected = selectedCollection == "muslim",
                                onClick = { selectedCollection = "muslim" },
                                modifier = Modifier.weight(1f)
                            )

                            CollectionProgressBadge(
                                title = "40 Nawawi",
                                done = nawawiDone,
                                total = nawawiTotal,
                                percentage = nawawiPct,
                                isSelected = selectedCollection == "nawawi",
                                onClick = { selectedCollection = "nawawi" },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // 3. Collection Filter Chips & Batch Actions Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(collections) { col ->
                            val isSelected = col == selectedCollection
                            val count = if (col == "All") overallCompleted else allBooks.count { it.collectionId.equals(col, ignoreCase = true) && completedBookIds.contains(it.id) }
                            val max = if (col == "All") overallTotal else allBooks.count { it.collectionId.equals(col, ignoreCase = true) }
                            val label = when (col) {
                                "bukhari" -> "Bukhari ($count/$max)"
                                "muslim" -> "Muslim ($count/$max)"
                                "nawawi" -> "Nawawi ($count/$max)"
                                else -> "All ($count/$max)"
                            }
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedCollection = col },
                                label = { Text(label, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = EmeraldPrimary,
                                    selectedLabelColor = Color.White
                                ),
                                modifier = Modifier.testTag("kitab_collection_chip_$col")
                            )
                        }
                    }

                    // Batch Actions Menu
                    if (onMarkCollectionCompleted != null && selectedCollection != "All") {
                        val isAllInCollectionDone = books.isNotEmpty() && books.all { completedBookIds.contains(it.id) }
                        IconButton(
                            onClick = { onMarkCollectionCompleted(selectedCollection, !isAllInCollectionDone) },
                            modifier = Modifier
                                .size(32.dp)
                                .testTag("batch_toggle_collection_btn")
                        ) {
                            Icon(
                                imageVector = if (isAllInCollectionDone) Icons.Default.DeleteSweep else Icons.Default.DoneAll,
                                contentDescription = if (isAllInCollectionDone) "Mark Collection Unread" else "Mark Collection Read",
                                tint = if (isAllInCollectionDone) Color.Gray else EmeraldPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                // 4. Chapters List
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 12.dp)
                ) {
                    items(books) { book ->
                        val isCompleted = completedBookIds.contains(book.id)
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isCompleted) EmeraldPrimary.copy(alpha = 0.6f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelectBook(book) }
                                .testTag("kitab_item_${book.id}")
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
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
                                            color = GoldAccent.copy(alpha = 0.15f)
                                        ) {
                                            Text(
                                                text = "${book.collectionId.replaceFirstChar { it.uppercase() }} • Book ${book.bookNumber}",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary),
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                        Text(
                                            text = "${book.hadithCount} Ahadith",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    // Room-backed Completion Button
                                    FilledTonalButton(
                                        onClick = { onToggleBookCompleted(book.id) },
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                        colors = ButtonDefaults.filledTonalButtonColors(
                                            containerColor = if (isCompleted) EmeraldPrimary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                                            contentColor = if (isCompleted) EmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                        ),
                                        modifier = Modifier
                                            .height(30.dp)
                                            .testTag("kitab_toggle_${book.id}")
                                    ) {
                                        Icon(
                                            imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Outlined.CheckCircleOutline,
                                            contentDescription = null,
                                            modifier = Modifier.size(15.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (isCompleted) "Read" else "Mark Read",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = book.titleEnglish,
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        text = book.titleArabic,
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = EmeraldPrimary),
                                        textAlign = TextAlign.Right
                                    )
                                }

                                Text(
                                    text = book.overview,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                // Room persistence status row
                                if (isCompleted) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = EmeraldPrimary,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Text(
                                            text = "Marked as completed in Room Database",
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, color = EmeraldPrimary, fontWeight = FontWeight.SemiBold)
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

@Composable
private fun CollectionProgressBadge(
    title: String,
    done: Int,
    total: Int,
    percentage: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) EmeraldPrimary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
        ),
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "$done/$total ($percentage%)",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, color = EmeraldPrimary, fontWeight = FontWeight.SemiBold)
            )
        }
    }
}
