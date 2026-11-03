package com.example.ui.screens.hadith

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.seed.HadithChapterMeta
import com.example.data.seed.HadithSeedData

data class ChapterDisplayItem(
    val bookNumber: Int,
    val title: String,
    val hadithRange: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HadithChaptersListView(
    collectionId: String,
    collectionTitle: String,
    onBack: () -> Unit,
    onSelectChapter: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    // Generate chapter ranges matching classical canonical book numbers
    val chapters = when (collectionId.lowercase()) {
        "bukhari" -> listOf(
            ChapterDisplayItem(1, "1. Revelation", "Hadith No: 1-7"),
            ChapterDisplayItem(2, "2. Belief", "Hadith No: 8-58"),
            ChapterDisplayItem(3, "3. Knowledge", "Hadith No: 59-134"),
            ChapterDisplayItem(4, "4. Ablutions (Wudu')", "Hadith No: 135-247"),
            ChapterDisplayItem(5, "5. Bathing (Ghusl)", "Hadith No: 248-293"),
            ChapterDisplayItem(6, "6. Menstrual Periods", "Hadith No: 294-333"),
            ChapterDisplayItem(7, "7. Rubbing hands and feet with dust (Tayammum)", "Hadith No: 334-348"),
            ChapterDisplayItem(8, "8. Prayers (Salat)", "Hadith No: 349-520"),
            ChapterDisplayItem(24, "24. Obligatory Charity (Zakat)", "Hadith No: 1395-1512"),
            ChapterDisplayItem(31, "31. Fasting (Sawm)", "Hadith No: 1891-2007")
        )
        "muslim" -> listOf(
            ChapterDisplayItem(1, "1. Faith (Kitab al-Iman)", "Hadith No: 1-92"),
            ChapterDisplayItem(2, "2. Purification (Taharah)", "Hadith No: 93-222"),
            ChapterDisplayItem(4, "4. Prayers (Kitab as-Salah)", "Hadith No: 397-571"),
            ChapterDisplayItem(13, "13. Fasting (Kitab as-Siyam)", "Hadith No: 1079-1167"),
            ChapterDisplayItem(35, "35. Supplications & Repentance", "Hadith No: 2675-2740")
        )
        "nasai" -> listOf(
            ChapterDisplayItem(1, "1. Purification", "Hadith No: 1-95"),
            ChapterDisplayItem(5, "5. The Book of Salah", "Hadith No: 448-608"),
            ChapterDisplayItem(22, "22. The Book of Fasting", "Hadith No: 2156-2240")
        )
        "abudawud" -> listOf(
            ChapterDisplayItem(1, "1. Purification (Kitab al-Taharah)", "Hadith No: 1-80"),
            ChapterDisplayItem(2, "2. Prayer (Kitab al-Salah)", "Hadith No: 391-540"),
            ChapterDisplayItem(14, "14. Fasting (Kitab al-Sawm)", "Hadith No: 2313-2382")
        )
        "tirmidhi" -> listOf(
            ChapterDisplayItem(1, "1. The Book of Purification", "Hadith No: 1-65"),
            ChapterDisplayItem(27, "27. Righteousness and Relations", "Hadith No: 1897-1986"),
            ChapterDisplayItem(48, "48. Supplications (Da'awat)", "Hadith No: 3369-3488")
        )
        "ibnmajah" -> listOf(
            ChapterDisplayItem(1, "1. The Book of the Sunnah", "Hadith No: 1-266"),
            ChapterDisplayItem(2, "2. The Book of Purification", "Hadith No: 267-666"),
            ChapterDisplayItem(7, "7. The Book of Fasting", "Hadith No: 1638-1778")
        )
        else -> listOf(
            ChapterDisplayItem(1, "1. The Essential Traditions", "Hadith No: 1-42")
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top App Bar
        TopAppBar(
            title = {
                Text(
                    text = collectionTitle,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            },
            navigationIcon = {
                IconButton(onClick = onBack, modifier = Modifier.testTag("chapter_list_back_btn")) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = MaterialTheme.colorScheme.onPrimary,
                navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
            )
        )

        // Chapters List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 96.dp)
        ) {
            items(chapters) { item ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectChapter(item.bookNumber) }
                        .testTag("chapter_card_${item.bookNumber}")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = item.hadithRange,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
