package com.example.ui.screens.hadith

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class HadithBookCardItem(
    val id: String,
    val title: String,
    val arabicTitle: String,
    val hadithCountStr: String,
    val badgeLetter: String,
    val badgeColor: Color
)

data class TopicCardItem(
    val title: String,
    val icon: ImageVector,
    val filterQuery: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HadithCollectionHubView(
    readingStreak: Int,
    dailyHadithRead: Boolean,
    onToggleDailyHadithRead: () -> Unit,
    onSelectBook: (String, String) -> Unit,
    onSelectTopic: (String) -> Unit,
    onOpenGlossary: () -> Unit,
    onOpenKitabTracker: () -> Unit,
    onOpenGoToHadith: () -> Unit,
    onOpenMenu: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isDailyHadithExpanded by remember { mutableStateOf(false) }

    val allBooks = listOf(
        HadithBookCardItem(
            id = "bukhari",
            title = "Sahih Bukhari",
            arabicTitle = "صحيح البخاري",
            hadithCountStr = "7563 Ahadith",
            badgeLetter = "B",
            badgeColor = Color(0xFFE25A53)
        ),
        HadithBookCardItem(
            id = "muslim",
            title = "Sahih Muslim",
            arabicTitle = "صحيح مسلم",
            hadithCountStr = "3033 Ahadith",
            badgeLetter = "M",
            badgeColor = Color(0xFFE05688)
        ),
        HadithBookCardItem(
            id = "nasai",
            title = "Sunan an-Nasai",
            arabicTitle = "سنن النسائي",
            hadithCountStr = "5758 Ahadith",
            badgeLetter = "N",
            badgeColor = Color(0xFF3F51B5)
        ),
        HadithBookCardItem(
            id = "abudawud",
            title = "Abu Dawood",
            arabicTitle = "سنن أبي داود",
            hadithCountStr = "5274 Ahadith",
            badgeLetter = "AD",
            badgeColor = Color(0xFF4C79F2)
        ),
        HadithBookCardItem(
            id = "tirmidhi",
            title = "Jamiat Tirmidhi",
            arabicTitle = "جامع الترمذي",
            hadithCountStr = "3960 Ahadith",
            badgeLetter = "T",
            badgeColor = Color(0xFF4FC3F7)
        ),
        HadithBookCardItem(
            id = "ibnmajah",
            title = "Sunan Ibn Majah",
            arabicTitle = "سنن ابن ماجه",
            hadithCountStr = "4341 Ahadith",
            badgeLetter = "IM",
            badgeColor = Color(0xFF389B82)
        ),
        HadithBookCardItem(
            id = "malik",
            title = "Muwatta Malik",
            arabicTitle = "موطأ مالك",
            hadithCountStr = "1858 Ahadith",
            badgeLetter = "M",
            badgeColor = Color(0xFFE67E22)
        ),
        HadithBookCardItem(
            id = "nawawi",
            title = "40 Hadith Nawawi",
            arabicTitle = "الأربعون النووية",
            hadithCountStr = "42 Ahadith",
            badgeLetter = "N",
            badgeColor = Color(0xFF8B5CF6)
        )
    )

    val row1Topics = listOf(
        TopicCardItem("Quranic Knowledge", Icons.Outlined.AutoStories, "Quran"),
        TopicCardItem("Aqidah (Belief)", Icons.Outlined.FavoriteBorder, "Belief"),
        TopicCardItem("Sciences of Hadith", Icons.Outlined.School, "Knowledge"),
        TopicCardItem("Akhirah (Afterlife)", Icons.Outlined.Landscape, "Afterlife"),
        TopicCardItem("Ibadah (worship of Allah)", Icons.Outlined.Place, "Prayer")
    )

    val row2Topics = listOf(
        TopicCardItem("Mu'amalat (Dealings)", Icons.Outlined.Balance, "Dealings"),
        TopicCardItem("Family law", Icons.Outlined.People, "Family"),
        TopicCardItem("History & Biographies", Icons.Outlined.BookmarkBorder, "Prophets"),
        TopicCardItem("Politics", Icons.Outlined.AccountBalance, "Governance"),
        TopicCardItem("Etiquette and Manners", Icons.Outlined.ThumbUp, "Manners")
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // 1. Top App Bar matching unified design system
        Surface(
            color = MaterialTheme.colorScheme.primary,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clickable(onClick = onOpenMenu)
                            .testTag("hadith_hub_menu_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menu",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(26.dp)
                        )
                        // Accent indicator dot on upper right of hamburger icon
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .align(Alignment.TopEnd)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.error)
                        )
                    }

                    Text(
                        text = "Hadith Collection",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 19.sp
                        ),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }

                // Feather Streak badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.18f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .testTag("hadith_reading_streak_badge")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.HistoryEdu,
                        contentDescription = "Reading Streak",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(17.dp)
                    )
                    Text(
                        text = "$readingStreak",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 14.sp
                    )
                }
            }
        }

        // Scrollable Hub Content
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Section 1: Daily Hadith
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Daily Hadith",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                        shadowElevation = 0.5.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("daily_hadith_card")
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Lantern Icon Container
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.TipsAndUpdates,
                                    contentDescription = "Lantern",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            // Hadith text
                            Text(
                                text = "'Aishah (May Allah be pleased with her) reported: Messenger of Allah (ﷺ) heard the voices of altercation of two disputants at the door; both the voices were quite loud. One was asking the other to make his debts little (so that he could pay them) a...",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    lineHeight = 22.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                ),
                                maxLines = if (isDailyHadithExpanded) 20 else 5,
                                overflow = TextOverflow.Ellipsis
                            )

                            AnimatedVisibility(visible = isDailyHadithExpanded) {
                                Text(
                                    text = "and the other was refusing to remit anything. The Prophet (ﷺ) came out to them and said: 'Where is he who swore by Allah that he would not do good?' The man said: 'It is I, O Messenger of Allah! And he can have whatever he likes.' [Sahih al-Bukhari & Sahih Muslim]",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        lineHeight = 20.sp
                                    ),
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }

                            // Action Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isDailyHadithExpanded) "Read less ⌃" else "Read more ⌵",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.primary
                                    ),
                                    modifier = Modifier
                                        .clickable { isDailyHadithExpanded = !isDailyHadithExpanded }
                                        .padding(vertical = 4.dp)
                                        .testTag("daily_hadith_read_more_btn")
                                )

                                // Circular Checkmark Button
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(if (dailyHadithRead) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
                                        .border(
                                            width = 1.5.dp,
                                            color = if (dailyHadithRead) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                                            shape = CircleShape
                                        )
                                        .clickable(onClick = onToggleDailyHadithRead)
                                        .testTag("daily_hadith_mark_read_btn"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Mark Hadith Read",
                                        tint = if (dailyHadithRead) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Section 2: Explore By Topics (2 horizontally scrollable rows matching screenshots)
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Explore By Topics",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    // Two-row horizontal scroll
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Row 1
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            row1Topics.forEach { topic ->
                                TopicBadgeCard(
                                    topic = topic,
                                    onClick = { onSelectTopic(topic.filterQuery) }
                                )
                            }
                        }

                        // Row 2
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            row2Topics.forEach { topic ->
                                TopicBadgeCard(
                                    topic = topic,
                                    onClick = { onSelectTopic(topic.filterQuery) }
                                )
                            }
                        }
                    }
                }
            }

            // Section 3: All Books (2-Column Grid matching Screenshot 1, 3, 5, 7)
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "All Books",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            TextButton(
                                onClick = onOpenGlossary,
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "Glossary",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            TextButton(
                                onClick = onOpenKitabTracker,
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "Kitab Tracker",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    // 2-Column Grid of Books
                    val bookPairs = allBooks.chunked(2)
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        bookPairs.forEach { pair ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                pair.forEach { book ->
                                    Box(modifier = Modifier.weight(1f)) {
                                        HadithBookGridCard(
                                            book = book,
                                            onClick = { onSelectBook(book.id, book.title) }
                                        )
                                    }
                                }
                                if (pair.size == 1) {
                                    Spacer(modifier = Modifier.weight(1f))
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
fun TopicBadgeCard(
    topic: TopicCardItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        shadowElevation = 0.5.dp,
        modifier = modifier
            .width(220.dp)
            .clickable(onClick = onClick)
            .testTag("topic_card_${topic.title.replace(' ', '_').lowercase()}")
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = topic.icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Text(
                text = topic.title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp
                ),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun HadithBookGridCard(
    book: HadithBookCardItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        shadowElevation = 0.5.dp,
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("book_card_${book.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 18.dp, horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Initial Badge Container
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(13.dp))
                    .background(book.badgeColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = book.badgeLetter,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    ),
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            // English Book Name
            Text(
                text = book.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                ),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            // Arabic Book Name
            Text(
                text = book.arabicTitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 13.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            // Hadith Count
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.MenuBook,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    modifier = Modifier.size(13.dp)
                )
                Text(
                    text = book.hadithCountStr,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 11.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
