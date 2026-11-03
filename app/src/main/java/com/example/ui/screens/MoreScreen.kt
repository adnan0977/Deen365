package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import com.example.ui.components.AdminVideoManagementDialog
import com.example.ui.components.UserAccountDialog
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldLight
import com.example.ui.viewmodel.DeenUiState
import com.example.ui.viewmodel.DeenViewModel
import com.example.ui.viewmodel.MainTab

data class MoreFeatureItem(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
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

    val featureItems = remember {
        listOf(
            MoreFeatureItem(
                title = "Holy Quran",
                subtitle = "114 Surahs, translation & audio",
                icon = Icons.Default.MenuBook,
                badge = "Recitations",
                onClick = { viewModel.setTab(MainTab.QURAN) }
            ),
            MoreFeatureItem(
                title = "Namaz & Schools of Thought",
                subtitle = "Timetable & 4 Madhhab guides",
                icon = Icons.Default.Schedule,
                badge = "4 Madhhabs",
                onClick = { viewModel.setTab(MainTab.NAMAZ) }
            ),
            MoreFeatureItem(
                title = "Qibla Direction",
                subtitle = "Precision compass & globe theme",
                icon = Icons.Default.Explore,
                badge = "Sensors",
                onClick = { viewModel.setTab(MainTab.QIBLA) }
            ),
            MoreFeatureItem(
                title = "Fasting & Calendar",
                subtitle = "Hijri date, Suhoor & Iftar times",
                icon = Icons.Default.NightsStay,
                badge = "Hijri 1446",
                onClick = { viewModel.setTab(MainTab.FASTING) }
            ),
            MoreFeatureItem(
                title = "Mosque Locator",
                subtitle = "Nearest masajid with Google Maps",
                icon = Icons.Default.Mosque,
                badge = "GPS Nav",
                onClick = { viewModel.setTab(MainTab.MOSQUES) }
            ),
            MoreFeatureItem(
                title = "Islamic Videos",
                subtitle = "YouTube lectures by scholar & topic",
                icon = Icons.Default.VideoLibrary,
                badge = "YouTube",
                onClick = { viewModel.setTab(MainTab.VIDEOS) }
            ),
            MoreFeatureItem(
                title = "Hadith & Prophetic Sunnah",
                subtitle = "Bukhari, Muslim, 40 Nawawi & search",
                icon = Icons.Default.LibraryBooks,
                badge = "Searchable",
                onClick = { viewModel.setTab(MainTab.HADITH_DUAS) }
            ),
            MoreFeatureItem(
                title = "Duas & Memorization",
                subtitle = "Hisn al-Muslim grid & learn mode",
                icon = Icons.Default.VolunteerActivism,
                badge = "Learn Mode",
                onClick = { viewModel.setTab(MainTab.HADITH_DUAS) }
            ),
            MoreFeatureItem(
                title = "Dhikr & Tasbih",
                subtitle = "Daily targets & digital counter",
                icon = Icons.Default.Fingerprint,
                badge = "Counter",
                onClick = { viewModel.setTab(MainTab.TASBIH) }
            ),
            MoreFeatureItem(
                title = "Islamic Heritage (Alim.org)",
                subtitle = "25 Prophets, 4 Caliphs, Sahaba & Eras",
                icon = Icons.Default.HistoryEdu,
                badge = "Alim.org",
                onClick = { viewModel.setTab(MainTab.ISLAMIC_HERITAGE) }
            ),
            MoreFeatureItem(
                title = "Islamic Books & Reader",
                subtitle = "Classical works, crawler & in-app reader",
                icon = Icons.Default.AutoStories,
                badge = "Library",
                onClick = { viewModel.setTab(MainTab.ISLAMIC_BOOKS) }
            ),
            MoreFeatureItem(
                title = "Ummah Community",
                subtitle = "Reflections, discussions & mutual Du'a support",
                icon = Icons.Default.Groups,
                badge = "Community",
                onClick = { viewModel.setTab(MainTab.COMMUNITY) }
            ),
            MoreFeatureItem(
                title = "Learning Academy",
                subtitle = "Interactive knowledge tracks & quizzes",
                icon = Icons.Default.School,
                badge = "Quizzes",
                onClick = { viewModel.setTab(MainTab.INTERACTIVE_LEARNING) }
            ),
            MoreFeatureItem(
                title = "Sahaba Biographies",
                subtitle = "Ashara Mubashara & linked Hadiths",
                icon = Icons.Default.Stars,
                badge = "Companions",
                onClick = { viewModel.setTab(MainTab.SAHABA_BIOGRAPHIES) }
            ),
            MoreFeatureItem(
                title = "Deen AI Companion",
                subtitle = "Spiritual guidance with Gemini 3.5",
                icon = Icons.Default.AutoAwesome,
                badge = "Premium AI",
                onClick = { viewModel.setTab(MainTab.AI_GUIDE) }
            ),
            MoreFeatureItem(
                title = "Theme Suite & Settings",
                subtitle = "Emerald, Midnight & Andalusian",
                icon = Icons.Default.Palette,
                badge = "3 Themes",
                onClick = { viewModel.setTab(MainTab.SETTINGS) }
            )
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Screen Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Deen365 Explore",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
                Text(
                    text = "All Islamic modules, navigation & accounts",
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

        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 160.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 96.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 1. Prominent User Account & Role Header Banner
            item(span = { GridItemSpan(maxLineSpan) }) {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.openUserAccountDialog() }
                        .testTag("user_account_header_card")
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
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
                                modifier = Modifier.size(46.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = if (isUserLoggedIn) Icons.Default.AccountCircle else Icons.Default.PersonOutline,
                                        contentDescription = null,
                                        tint = EmeraldPrimary,
                                        modifier = Modifier.size(28.dp)
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
                                    else -> if (isUserLoggedIn) "Normal User" else "Guest Mode (Tap to login)"
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
                            Text(if (isUserLoggedIn) "Manage" else "Login / Join", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // 2. Administrator Management Card (Visible if Admin or logged in as Admin)
            if (isAdmin) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f)),
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

                            Text(
                                text = "Manage YouTube video lectures, configure subscription pricing in Firestore, and review user translation modules.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

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

            // 3. Grid of all Modules
            items(featureItems) { item ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { item.onClick() }
                        .testTag("feature_card_${item.title.lowercase().replace(" ", "_")}")
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = EmeraldPrimary.copy(alpha = 0.12f),
                                modifier = Modifier.size(38.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = item.title,
                                        tint = EmeraldPrimary,
                                        modifier = Modifier.size(20.dp)
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
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GoldAccent),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
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
                                maxLines = 2
                            )
                        }
                    }
                }
            }
        }
    }
}
