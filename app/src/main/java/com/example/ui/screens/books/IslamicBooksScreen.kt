package com.example.ui.screens.books

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BookCategory
import com.example.data.model.IslamicBook
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.viewmodel.DeenUiState
import com.example.ui.viewmodel.DeenViewModel
import com.example.ui.viewmodel.MainTab

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IslamicBooksScreen(
    state: DeenUiState,
    viewModel: DeenViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        if (state.selectedBook != null) {
            viewModel.closeBook()
        } else {
            viewModel.setTab(MainTab.DASHBOARD)
        }
    }

    if (state.selectedBook != null) {
        BookReaderScreen(
            book = state.selectedBook,
            currentChapterIndex = state.currentReadingChapterIndex,
            fontSizeSp = state.readerFontSizeSp,
            readerTheme = state.readerTheme,
            isDrawerOpen = state.isBookDrawerOpen,
            onBack = { viewModel.closeBook() },
            onSelectChapter = { viewModel.selectBookChapter(it) },
            onToggleDrawer = { viewModel.toggleBookDrawer() },
            onFontSizeChange = { viewModel.setReaderFontSize(it) },
            onThemeChange = { viewModel.setReaderTheme(it) },
            onToggleFavorite = { viewModel.toggleBookFavorite(state.selectedBook.id) },
            modifier = modifier
        )
        return
    }

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<BookCategory?>(null) }

    val filteredBooks = remember(state.islamicBooksList, searchQuery, selectedCategory) {
        state.islamicBooksList.filter { book ->
            val matchQuery = searchQuery.isBlank() ||
                    book.title.contains(searchQuery, ignoreCase = true) ||
                    book.author.contains(searchQuery, ignoreCase = true) ||
                    book.arabicTitle.contains(searchQuery)
            val matchCat = selectedCategory == null || book.category == selectedCategory
            matchQuery && matchCat
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Islamic Books Library",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Classical & Contemporary Digital Works",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.setTab(MainTab.DASHBOARD) },
                        modifier = Modifier.testTag("books_back_button")
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Live Crawler Trigger Button
                    IconButton(
                        onClick = { viewModel.crawlNewBooks() },
                        enabled = !state.isCrawlingBooks,
                        modifier = Modifier.testTag("crawl_books_button")
                    ) {
                        if (state.isCrawlingBooks) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = EmeraldPrimary
                            )
                        } else {
                            Icon(
                                Icons.Default.CloudSync,
                                contentDescription = "Crawl Open Islamic Books",
                                tint = EmeraldPrimary
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Search Input Field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search title, author, topic...", fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = EmeraldPrimary) },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("book_search_input")
            )

            // Category Filter Chips
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedCategory == null,
                        onClick = { selectedCategory = null },
                        label = { Text("All Books", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EmeraldPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
                items(BookCategory.values()) { cat ->
                    val isSelected = cat == selectedCategory
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategory = cat },
                        label = { Text(cat.displayName, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EmeraldPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            // Bookshelf List
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(top = 8.dp, bottom = 48.dp)
            ) {
                items(filteredBooks) { book ->
                    IslamicBookCard(
                        book = book,
                        onClick = { viewModel.openBook(book) }
                    )
                }
            }
        }
    }
}

@Composable
private fun IslamicBookCard(
    book: IslamicBook,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .testTag("book_card_${book.id}")
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Book Cover Badge
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = EmeraldPrimary.copy(alpha = 0.12f),
                modifier = Modifier.size(54.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(book.coverEmoji, fontSize = 28.sp)
                }
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = book.title,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    if (book.isCrawled) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = GoldAccent.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "Crawled",
                                color = GoldAccent,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }
                }

                Text(
                    text = "By ${book.author}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )

                Spacer(Modifier.height(4.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = book.category.displayName,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Text(
                        text = "${book.chapters.size} Chapters",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = EmeraldPrimary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
