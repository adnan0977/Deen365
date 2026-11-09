package com.example.ui.screens.hadith

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Balance
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Timeline
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
import com.example.data.local.HadithEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HadithReaderDetailView(
    collectionId: String,
    collectionTitle: String,
    chapterNumber: Int,
    hadiths: List<HadithEntity>,
    onBack: () -> Unit,
    onToggleBookmark: (HadithEntity) -> Unit,
    onToggleRead: (HadithEntity) -> Unit,
    onCopyHadith: (HadithEntity) -> Unit,
    onShareHadith: (HadithEntity) -> Unit,
    onOpenGlossary: () -> Unit,
    onOpenTutorial: () -> Unit,
    onOpenSanadChain: ((HadithEntity) -> Unit)? = null,
    onOpenComparativeFiqh: (() -> Unit)? = null,
    isLoading: Boolean = false,
    modifier: Modifier = Modifier
) {
    val chapterTitle = when (chapterNumber) {
        1 -> "1 Revelation"
        2 -> "2 Belief"
        3 -> "3 Knowledge"
        4 -> "4 Ablutions (Wudu')"
        5 -> "5 Bathing (Ghusl)"
        6 -> "6 Menstrual Periods"
        7 -> "7 Tayammum"
        8 -> "8 Prayers (Salat)"
        else -> "$chapterNumber Chapter"
    }

    val chapterSubheader = when (chapterNumber) {
        1 -> "Chapter 1. How the Divine Revelation started being revealed to Allah's Messenger"
        2 -> "Chapter 2. Belief and its essential branches"
        3 -> "Chapter 3. The Virtues of Knowledge and Teaching"
        4 -> "Chapter 4. The Rules and Sunnahs of Ablution"
        else -> "Chapter $chapterNumber. Traditions and Invocations"
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top App Bar
        TopAppBar(
            title = {
                Column {
                    Text(
                        text = collectionTitle,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Text(
                        text = chapterTitle,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
                    )
                }
            },
            navigationIcon = {
                IconButton(onClick = onBack, modifier = Modifier.testTag("reader_back_btn")) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            },
            actions = {
                if (onOpenComparativeFiqh != null) {
                    IconButton(
                        onClick = onOpenComparativeFiqh,
                        modifier = Modifier.testTag("topbar_fiqh_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Balance,
                            contentDescription = "Comparative Fiqh",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
                IconButton(onClick = onOpenGlossary) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.MenuBook,
                        contentDescription = "Gharib Glossary",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
                IconButton(onClick = onOpenTutorial) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Tutorial & Settings",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = MaterialTheme.colorScheme.onPrimary,
                navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                actionIconContentColor = MaterialTheme.colorScheme.onPrimary
            )
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(top = 14.dp, bottom = 96.dp)
        ) {
            // Banner Card: "Start reading. Grow closer to the Sunnah."
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Start reading. Grow closer to the Sunnah.",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            // Chapter Section Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = chapterSubheader,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            lineHeight = 22.sp
                        ),
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.weight(1f).padding(vertical = 4.dp)
                    )
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = if (isLoading) "Fetching API..." else "${hadiths.size} Hadiths",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            if (isLoading) {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.primary,
                                strokeWidth = 3.dp,
                                modifier = Modifier.size(36.dp)
                            )
                            Text(
                                text = "Fetching $collectionTitle Chapter $chapterNumber via API...",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Caching newly loaded hadiths directly into Room DB for offline access",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            // Hadith Items
            items(hadiths) { hadith ->
                var menuExpanded by remember { mutableStateOf(false) }

                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("hadith_reader_card_${hadith.hadithNumber}")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Top Bar inside Card: Large Hadith Number & Sahih Badge
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${hadith.hadithNumber}",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 28.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                                ) {
                                    Text(
                                        text = hadith.grade.ifBlank { "Sahih" },
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }

                                Box {
                                    IconButton(
                                        onClick = { menuExpanded = true },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.MoreVert,
                                            contentDescription = "Options",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    DropdownMenu(
                                        expanded = menuExpanded,
                                        onDismissRequest = { menuExpanded = false }
                                    ) {
                                        DropdownMenuItem(
                                            text = { Text("Copy Hadith") },
                                            onClick = {
                                                menuExpanded = false
                                                onCopyHadith(hadith)
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = { Text("Share Hadith") },
                                            onClick = {
                                                menuExpanded = false
                                                onShareHadith(hadith)
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = { Text("Visual Sanad Chain") },
                                            onClick = {
                                                menuExpanded = false
                                                onOpenSanadChain?.invoke(hadith)
                                            }
                                        )
                                        if (onOpenComparativeFiqh != null) {
                                            DropdownMenuItem(
                                                text = { Text("Comparative Fiqh (4 Madhabs)") },
                                                onClick = {
                                                    menuExpanded = false
                                                    onOpenComparativeFiqh()
                                                }
                                            )
                                        }
                                        DropdownMenuItem(
                                            text = { Text("Gharib Vocabulary") },
                                            onClick = {
                                                menuExpanded = false
                                                onOpenGlossary()
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = { Text("Marking Tutorial") },
                                            onClick = {
                                                menuExpanded = false
                                                onOpenTutorial()
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        // Arabic Text
                        Text(
                            text = hadith.arabicText,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontSize = 21.sp,
                                lineHeight = 38.sp,
                                fontWeight = FontWeight.Normal
                            ),
                            textAlign = TextAlign.Right,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        )

                        // Narrator Introduction
                        if (hadith.narrator.isNotBlank()) {
                            Text(
                                text = "Narrated ${hadith.narrator}:",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        // English Translation
                        Text(
                            text = hadith.translation,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 14.sp,
                                lineHeight = 22.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        HorizontalDivider(
                            thickness = 0.5.dp,
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                            modifier = Modifier.padding(top = 4.dp)
                        )

                        // Reference & Links
                        Text(
                            text = "USC - MSA web (English): Vol. 1, Book $chapterNumber, Hadith ${hadith.hadithNumber}",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        // Inline Sunnah.com Sanad Chain Preview Ribbon
                        val sanadChain = remember(hadith.collection, hadith.hadithNumber) {
                            com.example.data.seed.SanadChainSeedData.getSanadForHadith(hadith.collection, hadith.hadithNumber)
                        }
                        com.example.ui.components.hadith.MiniSanadPreviewRibbon(
                            chain = sanadChain,
                            onOpenFullSanad = {
                                if (onOpenSanadChain != null) {
                                    onOpenSanadChain(hadith)
                                } else {
                                    onOpenGlossary()
                                }
                            }
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Similar Ahadith",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.SemiBold
                                ),
                                modifier = Modifier.clickable { onOpenGlossary() }
                            )
                            Text(
                                text = "Visual Sanad Chain",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier
                                    .testTag("narration_chain_btn_${hadith.hadithNumber}")
                                    .clickable {
                                        if (onOpenSanadChain != null) {
                                            onOpenSanadChain(hadith)
                                        } else {
                                            onOpenGlossary()
                                        }
                                    }
                            )
                        }

                        // Action Bar: Bookmark, Notes, Copy, Share, and Circular Check Button
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                IconButton(
                                    onClick = { onToggleBookmark(hadith) },
                                    modifier = Modifier.size(34.dp)
                                ) {
                                    Icon(
                                        imageVector = if (hadith.isFavorite) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                        contentDescription = "Bookmark",
                                        tint = if (hadith.isFavorite) Color(0xFFF59E0B) else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                IconButton(
                                    onClick = onOpenGlossary,
                                    modifier = Modifier.size(34.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.MenuBook,
                                        contentDescription = "Vocabulary",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                IconButton(
                                    onClick = { onCopyHadith(hadith) },
                                    modifier = Modifier.size(34.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copy",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                IconButton(
                                    onClick = { onShareHadith(hadith) },
                                    modifier = Modifier.size(34.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Share,
                                        contentDescription = "Share",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            // Floating Circular Checkmark Button
                            Surface(
                                shape = CircleShape,
                                color = if (hadith.isRead) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                                shadowElevation = 3.dp,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.5.dp,
                                    if (hadith.isRead) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                                ),
                                modifier = Modifier
                                    .size(36.dp)
                                    .clickable { onToggleRead(hadith) }
                                    .testTag("reader_check_btn_${hadith.hadithNumber}")
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Mark as read",
                                        tint = if (hadith.isRead) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(18.dp)
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
