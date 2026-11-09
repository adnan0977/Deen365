package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldLight
import com.example.ui.components.GoogleAdBannerPlaceholder
import com.example.ui.components.VersionControlCard
import com.example.ui.viewmodel.DeenUiState
import com.example.ui.viewmodel.DeenViewModel
import com.example.ui.viewmodel.MainTab

data class MoreFeatureItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val category: String,
    val badge: String? = null,
    val onClick: () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreScreen(
    state: DeenUiState,
    viewModel: DeenViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler { viewModel.setTab(MainTab.DASHBOARD) }

    val userProfile = state.userProfile
    val isUserLoggedIn = userProfile != null && userProfile.uid.isNotBlank()
    val isAdmin = userProfile?.role == "admin" || state.isAdminLoggedIn

    var selectedCategoryFilter by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }

    val categories = remember {
        listOf("All", "Pillars & Worship", "Quran & Dhikr", "Sunnah & Lifestyle", "Knowledge & Community")
    }

    val allFeatureItems = remember {
        listOf(
            // --- Pillars & Worship ---
            MoreFeatureItem(
                id = "namaz",
                title = "Namaz & Prayer Times",
                subtitle = "Timetable, Adhan alarms, 4 Madhhabs & steps",
                icon = Icons.Default.Schedule,
                category = "Pillars & Worship",
                badge = "5 Prayers",
                onClick = { viewModel.setTab(MainTab.NAMAZ_FASTING) }
            ),
            MoreFeatureItem(
                id = "qibla",
                title = "Qibla Compass",
                subtitle = "Precision magnetometer & Kaaba bearing",
                icon = Icons.Default.Explore,
                category = "Pillars & Worship",
                badge = "Live GPS",
                onClick = { viewModel.setTab(MainTab.QIBLA) }
            ),
            MoreFeatureItem(
                id = "fasting",
                title = "Fasting & Sawm Tracker",
                subtitle = "Suhoor/Iftar countdowns & White Days fasting",
                icon = Icons.Default.NightsStay,
                category = "Pillars & Worship",
                badge = "Sunnah",
                onClick = { viewModel.setTab(MainTab.FASTING) }
            ),
            MoreFeatureItem(
                id = "zakat",
                title = "Zakat Calculator",
                subtitle = "Live gold/silver prices, 2.5%, Hawl & Nisab",
                icon = Icons.Default.Savings,
                category = "Pillars & Worship",
                badge = "Live Gold",
                onClick = { viewModel.setTab(MainTab.ZAKAT) }
            ),
            MoreFeatureItem(
                id = "hajj_umrah",
                title = "Hajj & Umrah Guide",
                subtitle = "Step-by-step rituals, Tawaf counter & Talbiyah audio",
                icon = Icons.Default.Mosque,
                category = "Pillars & Worship",
                badge = "Interactive",
                onClick = { viewModel.setTab(MainTab.HAJJ_UMRAH) }
            ),
            MoreFeatureItem(
                id = "qada_salah",
                title = "Qada Salah Tracker",
                subtitle = "Missed prayers log, targets & milestone streaks",
                icon = Icons.Default.Update,
                category = "Pillars & Worship",
                badge = "Streaks",
                onClick = { viewModel.setTab(MainTab.QADA_SALAH) }
            ),

            // --- Quran & Dhikr ---
            MoreFeatureItem(
                id = "khatam_planner",
                title = "Khatam Quran Planner",
                subtitle = "Pacing per prayer & authentic Du'a Khatm al-Quran",
                icon = Icons.Default.Book,
                category = "Quran & Dhikr",
                badge = "30-Day Goal",
                onClick = { viewModel.setTab(MainTab.KHATAM_PLANNER) }
            ),
            MoreFeatureItem(
                id = "ruqyah_healing",
                title = "Ruqyah & Spiritual Healing",
                subtitle = "Audio recitation, Ayat ash-Shifa & prophetic ways",
                icon = Icons.Default.Healing,
                category = "Quran & Dhikr",
                badge = "Audio Shifa",
                onClick = { viewModel.setTab(MainTab.RUQYAH_HEALING) }
            ),
            MoreFeatureItem(
                id = "asma_ul_husna",
                title = "99 Names of Allah",
                subtitle = "Asma-ul-Husna audio, memorization & meanings",
                icon = Icons.Default.AutoAwesome,
                category = "Quran & Dhikr",
                badge = "99 Names",
                onClick = { viewModel.setTab(MainTab.ASMA_UL_HUSNA) }
            ),
            MoreFeatureItem(
                id = "tasbih",
                title = "Dhikr & Digital Tasbih",
                subtitle = "Haptic feedback, custom counts & morning/evening adkhar",
                icon = Icons.Default.Fingerprint,
                category = "Quran & Dhikr",
                badge = "Haptics",
                onClick = { viewModel.setTab(MainTab.TASBIH) }
            ),

            // --- Sunnah & Lifestyle ---
            MoreFeatureItem(
                id = "hijri_calendar",
                title = "Hijri Calendar & Events",
                subtitle = "12 lunar months, sacred dates & fasting occasions",
                icon = Icons.Default.CalendarMonth,
                category = "Sunnah & Lifestyle",
                badge = "1448 AH",
                onClick = { viewModel.setTab(MainTab.HIJRI_CALENDAR) }
            ),
            MoreFeatureItem(
                id = "halal_guide",
                title = "Halal Food & E-Numbers",
                subtitle = "350+ additive verdicts (Halal, Haram, Mushbooh)",
                icon = Icons.Default.Restaurant,
                category = "Sunnah & Lifestyle",
                badge = "350+ Codes",
                onClick = { viewModel.setTab(MainTab.HALAL_GUIDE) }
            ),
            MoreFeatureItem(
                id = "mirath_calc",
                title = "Inheritance (Mirath)",
                subtitle = "Quranic shares & comparative 4 Madhab jurisprudence",
                icon = Icons.Default.Calculate,
                category = "Sunnah & Lifestyle",
                badge = "4 Madhabs",
                onClick = { viewModel.setTab(MainTab.INHERITANCE_CALCULATOR) }
            ),
            MoreFeatureItem(
                id = "baby_names",
                title = "Islamic Baby Names",
                subtitle = "200+ authentic boy/girl names, meanings & Sunnah",
                icon = Icons.Default.Face,
                category = "Sunnah & Lifestyle",
                badge = "Meanings",
                onClick = { viewModel.setTab(MainTab.BABY_NAMES) }
            ),
            MoreFeatureItem(
                id = "haramain_live",
                title = "24/7 Haramain Live TV",
                subtitle = "Makkah Quran TV & Madinah Sunnah TV with dual clock",
                icon = Icons.Default.LiveTv,
                category = "Sunnah & Lifestyle",
                badge = "Makkah Live",
                onClick = { viewModel.setTab(MainTab.HARAMAIN_LIVE) }
            ),
            MoreFeatureItem(
                id = "mosques",
                title = "Mosque Locator",
                subtitle = "Nearest masajid, Google Maps navigation & Iqamah times",
                icon = Icons.Default.Place,
                category = "Sunnah & Lifestyle",
                badge = "Google Maps",
                onClick = { viewModel.setTab(MainTab.MOSQUES) }
            ),

            // --- Knowledge & Community ---
            MoreFeatureItem(
                id = "heritage",
                title = "Islamic Heritage (Alim.org)",
                subtitle = "25 Prophets, 4 Caliphs, Sahaba & Islamic Eras",
                icon = Icons.Default.HistoryEdu,
                category = "Knowledge & Community",
                badge = "Alim.org",
                onClick = { viewModel.setTab(MainTab.ISLAMIC_HERITAGE) }
            ),
            MoreFeatureItem(
                id = "books",
                title = "Classical Books Library",
                subtitle = "Foundational texts, crawler & night-mode reader",
                icon = Icons.Default.AutoStories,
                category = "Knowledge & Community",
                badge = "In-App Reader",
                onClick = { viewModel.setTab(MainTab.ISLAMIC_BOOKS) }
            ),
            MoreFeatureItem(
                id = "community",
                title = "Ummah Community",
                subtitle = "Reflections, discussions & mutual Du'a support",
                icon = Icons.Default.Groups,
                category = "Knowledge & Community",
                badge = "Ummah",
                onClick = { viewModel.setTab(MainTab.COMMUNITY) }
            ),
            MoreFeatureItem(
                id = "learning",
                title = "Learning Academy",
                subtitle = "Structured knowledge tracks, interactive quizzes",
                icon = Icons.Default.School,
                category = "Knowledge & Community",
                badge = "Interactive",
                onClick = { viewModel.setTab(MainTab.INTERACTIVE_LEARNING) }
            ),
            MoreFeatureItem(
                id = "sahaba",
                title = "Sahaba Biographies",
                subtitle = "Ashara Mubashara & linked Sahih Hadiths",
                icon = Icons.Default.Stars,
                category = "Knowledge & Community",
                badge = "Companions",
                onClick = { viewModel.setTab(MainTab.SAHABA_BIOGRAPHIES) }
            ),
            MoreFeatureItem(
                id = "videos",
                title = "Islamic Video Lectures",
                subtitle = "Curated YouTube lectures by scholar & topic",
                icon = Icons.Default.VideoLibrary,
                category = "Knowledge & Community",
                badge = "YouTube",
                onClick = { viewModel.setTab(MainTab.VIDEOS) }
            ),
            MoreFeatureItem(
                id = "settings",
                title = "Settings & App Themes",
                subtitle = "Emerald, Midnight & Andalusian themes, alerts & backup",
                icon = Icons.Default.Settings,
                category = "Knowledge & Community",
                badge = "Preferences",
                onClick = { viewModel.setTab(MainTab.SETTINGS) }
            )
        )
    }

    val filteredItems = remember(selectedCategoryFilter, searchQuery) {
        allFeatureItems.filter { item ->
            val matchesCategory = selectedCategoryFilter == "All" || item.category == selectedCategoryFilter
            val matchesQuery = searchQuery.isBlank() ||
                    item.title.contains(searchQuery, ignoreCase = true) ||
                    item.subtitle.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesQuery
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Screen Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 6.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "All Islamic Modules",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                    Text(
                        text = "Explore the complete Deen365 suite of features",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(
                    onClick = { viewModel.openSettings() },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .testTag("more_screen_settings_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search modules (e.g. Zakat, Mirath, Qibla...)") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("search_more_modules_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = EmeraldPrimary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                )
            )

            // Category Filter Rail
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                items(categories) { category ->
                    val isSelected = category == selectedCategoryFilter
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategoryFilter = category },
                        label = {
                            Text(
                                text = category,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EmeraldPrimary,
                            selectedLabelColor = Color.White
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.outlineVariant
                        ),
                        modifier = Modifier.testTag("filter_chip_${category.lowercase().replace(" ", "_")}")
                    )
                }
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 160.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 96.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. User Account & Role Header Banner
            item(span = { GridItemSpan(maxLineSpan) }) {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.openUserAccountDialog() }
                        .testTag("user_account_header_card")
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = EmeraldPrimary.copy(alpha = 0.15f),
                                modifier = Modifier.size(44.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = if (isUserLoggedIn) Icons.Default.AccountCircle else Icons.Default.PersonOutline,
                                        contentDescription = null,
                                        tint = EmeraldPrimary,
                                        modifier = Modifier.size(26.dp)
                                    )
                                }
                            }

                            Column {
                                Text(
                                    text = if (isUserLoggedIn) userProfile?.displayName ?: "Account" else "Sign In or Register",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                val roleLabel = when (userProfile?.role) {
                                    "admin" -> "Administrator"
                                    "subscriber" -> "Premium Subscriber"
                                    else -> if (isUserLoggedIn) "Normal User" else "Guest Mode (Tap to sync)"
                                }
                                Text(
                                    text = roleLabel,
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                    color = when (userProfile?.role) {
                                        "admin" -> MaterialTheme.colorScheme.error
                                        "subscriber" -> GoldAccent
                                        else -> EmeraldPrimary
                                    }
                                )
                            }
                        }

                        Button(
                            onClick = { viewModel.openUserAccountDialog() },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(if (isUserLoggedIn) "Manage" else "Login", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // 2. Administrator Management Card (Visible if Admin)
            if (isAdmin) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)),
                        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth().testTag("admin_management_card")
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(24.dp))
                                    Text(
                                        text = "Administrator Control Center",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = MaterialTheme.colorScheme.error.copy(alpha = 0.15f)
                                ) {
                                    Text("ADMIN ACCESS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { viewModel.openAdminDialog() },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.VideoLibrary, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Manage Videos", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }

                                FilledTonalButton(
                                    onClick = { viewModel.openUserAccountDialog() },
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Loyalty, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Manage Plans", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // 3. Grid of Modules
            items(filteredItems) { item ->
                ElevatedCard(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .clickable { item.onClick() }
                        .testTag("feature_card_${item.id}")
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = EmeraldPrimary.copy(alpha = 0.12f),
                                modifier = Modifier.size(42.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = item.title,
                                        tint = EmeraldPrimary,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }

                            if (item.badge != null) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = GoldAccent.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = item.badge,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = GoldAccent
                                        ),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1
                            )
                            Text(
                                text = item.subtitle,
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 2,
                                minLines = 2
                            )
                        }
                    }
                }
            }

            // Google Ads Reserved Banner Space
            item(span = { GridItemSpan(maxLineSpan) }) {
                GoogleAdBannerPlaceholder(
                    modifier = Modifier.padding(vertical = 4.dp),
                    adSlotName = "More Modules Bottom Banner"
                )
            }

            // Version Control Card & Release History
            item(span = { GridItemSpan(maxLineSpan) }) {
                VersionControlCard(
                    modifier = Modifier.padding(vertical = 6.dp)
                )
            }
        }
    }
}
