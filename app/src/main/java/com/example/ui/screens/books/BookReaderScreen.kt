package com.example.ui.screens.books

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BookChapter
import com.example.data.model.IslamicBook
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookReaderScreen(
    book: IslamicBook,
    currentChapterIndex: Int,
    fontSizeSp: Int,
    readerTheme: String, // "light", "sepia", "midnight"
    isDrawerOpen: Boolean,
    onBack: () -> Unit,
    onSelectChapter: (Int) -> Unit,
    onToggleDrawer: () -> Unit,
    onFontSizeChange: (Int) -> Unit,
    onThemeChange: (String) -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentChapter = book.chapters.getOrNull(currentChapterIndex) ?: book.chapters.firstOrNull()

    // Determine colors based on reading theme
    val backgroundColor = when (readerTheme) {
        "sepia" -> Color(0xFFFBF0D9)
        "midnight" -> Color(0xFF121417)
        else -> MaterialTheme.colorScheme.background
    }
    val textColor = when (readerTheme) {
        "sepia" -> Color(0xFF3E2723)
        "midnight" -> Color(0xFFE0E0E0)
        else -> MaterialTheme.colorScheme.onBackground
    }
    val surfaceColor = when (readerTheme) {
        "sepia" -> Color(0xFFF4E4C1)
        "midnight" -> Color(0xFF1E2228)
        else -> MaterialTheme.colorScheme.surface
    }

    var showFormattingControls by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = book.title,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            maxLines = 1
                        )
                        Text(
                            text = currentChapter?.titleEnglish ?: "Chapter ${currentChapterIndex + 1}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("book_reader_back_button")) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showFormattingControls = !showFormattingControls }) {
                        Icon(Icons.Default.FormatSize, contentDescription = "Font & Theme")
                    }
                    IconButton(onClick = onToggleFavorite) {
                        Icon(
                            imageVector = if (book.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (book.isFavorite) Color.Red else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(onClick = onToggleDrawer, modifier = Modifier.testTag("book_chapters_drawer_button")) {
                        Icon(Icons.Default.List, contentDescription = "Table of Contents")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = surfaceColor)
            )
        },
        bottomBar = {
            // Chapter Navigation Footer
            Surface(
                color = surfaceColor,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = { if (currentChapterIndex > 0) onSelectChapter(currentChapterIndex - 1) },
                        enabled = currentChapterIndex > 0
                    ) {
                        Icon(Icons.Default.ChevronLeft, contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                        Text("Previous")
                    }

                    Text(
                        text = "Ch ${currentChapterIndex + 1} of ${book.chapters.size}",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = textColor
                    )

                    TextButton(
                        onClick = { if (currentChapterIndex < book.chapters.size - 1) onSelectChapter(currentChapterIndex + 1) },
                        enabled = currentChapterIndex < book.chapters.size - 1
                    ) {
                        Text("Next")
                        Spacer(Modifier.width(4.dp))
                        Icon(Icons.Default.ChevronRight, contentDescription = null)
                    }
                }
            }
        },
        modifier = modifier
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(backgroundColor)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                // Formatting Controls Panel
                if (showFormattingControls) {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = surfaceColor),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            // Font Size Controls
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Font Size: ${fontSizeSp}sp", fontSize = 12.sp, color = textColor)
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedButton(
                                        onClick = { onFontSizeChange(fontSizeSp - 2) },
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                        modifier = Modifier.height(32.dp)
                                    ) {
                                        Text("A-", fontSize = 11.sp)
                                    }
                                    OutlinedButton(
                                        onClick = { onFontSizeChange(fontSizeSp + 2) },
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                        modifier = Modifier.height(32.dp)
                                    ) {
                                        Text("A+", fontSize = 11.sp)
                                    }
                                }
                            }

                            Spacer(Modifier.height(10.dp))

                            // Theme Selector Chips
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf(
                                    Triple("light", "Light", Color(0xFFF7F7F7)),
                                    Triple("sepia", "Sepia", Color(0xFFFBF0D9)),
                                    Triple("midnight", "Night", Color(0xFF1E2228))
                                ).forEach { (themeKey, label, chipBg) ->
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = chipBg,
                                        border = if (readerTheme == themeKey) androidx.compose.foundation.BorderStroke(2.dp, EmeraldPrimary) else null,
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable { onThemeChange(themeKey) }
                                    ) {
                                        Text(
                                            text = label,
                                            fontSize = 12.sp,
                                            fontWeight = if (readerTheme == themeKey) FontWeight.Bold else FontWeight.Normal,
                                            color = if (themeKey == "midnight") Color.White else Color.Black,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.padding(vertical = 8.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Chapter Header
                if (currentChapter != null) {
                    if (!currentChapter.titleArabic.isNullOrBlank()) {
                        Text(
                            text = currentChapter.titleArabic,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = EmeraldPrimary
                            ),
                            textAlign = TextAlign.Right,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(6.dp))
                    }

                    Text(
                        text = currentChapter.titleEnglish,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = textColor
                        )
                    )

                    Spacer(Modifier.height(12.dp))
                    Divider(color = textColor.copy(alpha = 0.15f))
                    Spacer(Modifier.height(16.dp))

                    // Main Text Content
                    Text(
                        text = currentChapter.textContent,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = fontSizeSp.sp,
                            lineHeight = (fontSizeSp * 1.6f).sp,
                            color = textColor
                        )
                    )
                }

                Spacer(Modifier.height(60.dp))
            }

            // Table of Contents Drawer Modal
            if (isDrawerOpen) {
                Surface(
                    color = surfaceColor,
                    shape = RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp),
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(0.85f)
                        .align(Alignment.CenterEnd)
                        .testTag("book_chapter_drawer")
                ) {
                    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Table of Contents",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = textColor
                            )
                            IconButton(onClick = onToggleDrawer) {
                                Icon(Icons.Default.Close, contentDescription = "Close", tint = textColor)
                            }
                        }

                        Divider(color = textColor.copy(alpha = 0.15f), modifier = Modifier.padding(vertical = 8.dp))

                        LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            itemsIndexed(book.chapters) { index, chapter ->
                                val isSelected = index == currentChapterIndex
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) EmeraldPrimary.copy(alpha = 0.15f) else Color.Transparent,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { onSelectChapter(index) }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "${index + 1}.",
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) EmeraldPrimary else textColor,
                                            fontSize = 12.sp,
                                            modifier = Modifier.width(24.dp)
                                        )
                                        Text(
                                            text = chapter.titleEnglish,
                                            fontSize = 13.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) EmeraldPrimary else textColor
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
