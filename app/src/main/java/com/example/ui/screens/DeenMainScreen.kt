package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.AdminVideoManagementDialog
import com.example.ui.components.UserAccountDialog
import com.example.ui.components.CoinsHubDialog
import com.example.ui.components.AdminOffersDialog
import com.example.ui.components.ReciterSelectionDialog
import com.example.ui.components.DeenAiAssistantSheet
import com.example.ui.components.BottomMenuCustomizerDialog
import com.example.ui.screens.heritage.IslamicHeritageScreen
import com.example.ui.screens.books.IslamicBooksScreen
import com.example.ui.screens.community.CommunityScreen
import com.example.ui.screens.learning.InteractiveLearningScreen
import com.example.ui.screens.sahaba.SahabaBiographiesScreen
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldLight
import com.example.ui.viewmodel.DeenViewModel
import com.example.ui.viewmodel.MainTab

private fun getTabMetadata(tab: MainTab): Pair<String, Pair<ImageVector, ImageVector>> {
    return when (tab) {
        MainTab.DASHBOARD -> Pair("Home", Pair(Icons.Filled.Home, Icons.Outlined.Home))
        MainTab.QURAN -> Pair("Quran", Pair(Icons.Filled.MenuBook, Icons.Outlined.MenuBook))
        MainTab.VIDEOS -> Pair("Videos", Pair(Icons.Filled.VideoLibrary, Icons.Outlined.VideoLibrary))
        MainTab.HADITH_DUAS -> Pair("Hadith", Pair(Icons.Filled.LibraryBooks, Icons.Outlined.LibraryBooks))
        MainTab.NAMAZ, MainTab.NAMAZ_FASTING -> Pair("Namaz", Pair(Icons.Filled.Schedule, Icons.Outlined.Schedule))
        MainTab.TASBIH -> Pair("Dhikr", Pair(Icons.Filled.Fingerprint, Icons.Outlined.Fingerprint))
        MainTab.QIBLA -> Pair("Qibla", Pair(Icons.Filled.Explore, Icons.Outlined.Explore))
        MainTab.FASTING -> Pair("Fasting", Pair(Icons.Filled.NightsStay, Icons.Outlined.NightsStay))
        MainTab.ISLAMIC_HERITAGE -> Pair("Heritage", Pair(Icons.Filled.HistoryEdu, Icons.Outlined.HistoryEdu))
        MainTab.ISLAMIC_BOOKS -> Pair("Books", Pair(Icons.Filled.AutoStories, Icons.Outlined.AutoStories))
        MainTab.COMMUNITY -> Pair("Community", Pair(Icons.Filled.Groups, Icons.Outlined.Groups))
        MainTab.INTERACTIVE_LEARNING -> Pair("Academy", Pair(Icons.Filled.School, Icons.Outlined.School))
        MainTab.SAHABA_BIOGRAPHIES -> Pair("Sahaba", Pair(Icons.Filled.Stars, Icons.Outlined.Stars))
        MainTab.MOSQUES -> Pair("Mosques", Pair(Icons.Filled.Mosque, Icons.Outlined.Mosque))
        MainTab.AI_GUIDE -> Pair("Deen AI", Pair(Icons.Filled.AutoAwesome, Icons.Outlined.AutoAwesome))
        MainTab.MORE -> Pair("More", Pair(Icons.Filled.Widgets, Icons.Outlined.Widgets))
        MainTab.SETTINGS -> Pair("Settings", Pair(Icons.Filled.Settings, Icons.Outlined.Settings))
    }
}

@Composable
fun DeenMainScreen(
    viewModel: DeenViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val deviceProfile = com.example.ui.util.rememberDeviceScreenProfile()

    val fiveTabs = remember(state.selectedBottomTabs) {
        val list = state.selectedBottomTabs
        if (list.size >= 5) list.take(5) else listOf(
            MainTab.DASHBOARD,
            MainTab.NAMAZ,
            MainTab.AI_GUIDE,
            MainTab.QURAN,
            MainTab.SETTINGS
        )
    }

    val railTabs = remember {
        listOf(
            MainTab.DASHBOARD,
            MainTab.QURAN,
            MainTab.NAMAZ,
            MainTab.FASTING,
            MainTab.HADITH_DUAS,
            MainTab.VIDEOS,
            MainTab.AI_GUIDE,
            MainTab.SETTINGS
        )
    }

    @Composable
    fun ContentPane(modifier: Modifier = Modifier) {
        Box(modifier = modifier.fillMaxSize()) {
            when (state.currentTab) {
                MainTab.DASHBOARD -> DashboardScreen(state = state, viewModel = viewModel)
                MainTab.QURAN -> QuranScreen(state = state, viewModel = viewModel)
                MainTab.VIDEOS -> IslamicVideosScreen(state = state, viewModel = viewModel)
                MainTab.AI_GUIDE -> AiGuidanceScreen(state = state, viewModel = viewModel)
                MainTab.HADITH_DUAS -> HadithAndDuaScreen(state = state, viewModel = viewModel)
                MainTab.NAMAZ, MainTab.NAMAZ_FASTING -> NamazScreen(state = state, viewModel = viewModel)
                MainTab.QIBLA -> QiblaScreen(state = state, viewModel = viewModel)
                MainTab.FASTING -> FastingScreen(state = state, viewModel = viewModel)
                MainTab.ISLAMIC_HERITAGE -> IslamicHeritageScreen(state = state, viewModel = viewModel)
                MainTab.ISLAMIC_BOOKS -> IslamicBooksScreen(state = state, viewModel = viewModel)
                MainTab.COMMUNITY -> CommunityScreen(state = state, viewModel = viewModel)
                MainTab.INTERACTIVE_LEARNING -> InteractiveLearningScreen(state = state, viewModel = viewModel)
                MainTab.SAHABA_BIOGRAPHIES -> SahabaBiographiesScreen(state = state, viewModel = viewModel)
                MainTab.MOSQUES -> MosqueLocatorScreen(state = state, viewModel = viewModel)
                MainTab.TASBIH -> DhikrTrackingScreen(state = state, viewModel = viewModel)
                MainTab.MORE -> MoreScreen(state = state, viewModel = viewModel)
                MainTab.SETTINGS -> SettingsScreen(state = state, viewModel = viewModel)
            }
        }
    }

    if (deviceProfile.useNavigationRail) {
        // Tablet, Landscape & Google TV Layout with NavigationRail
        Row(
            modifier = modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
        ) {
            NavigationRail(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
                header = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(vertical = 12.dp)
                    ) {
                        Surface(
                            shape = androidx.compose.foundation.shape.CircleShape,
                            color = EmeraldPrimary,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Mosque,
                                    contentDescription = "Deen365",
                                    tint = GoldLight,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Deen365",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = EmeraldPrimary
                        )
                        if (deviceProfile.isTv) {
                            Surface(
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(6.dp),
                                color = GoldAccent.copy(alpha = 0.25f),
                                modifier = Modifier.padding(top = 2.dp)
                            ) {
                                Text(
                                    text = "TV",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                                )
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxHeight()
                    .testTag("deen_navigation_rail")
            ) {
                Spacer(Modifier.height(8.dp))
                railTabs.forEach { tab ->
                    val (label, icons) = getTabMetadata(tab)
                    val isSelected = state.currentTab == tab

                    NavigationRailItem(
                        selected = isSelected,
                        onClick = { viewModel.setTab(tab) },
                        icon = {
                            if (tab == MainTab.AI_GUIDE) {
                                BadgedBox(badge = {
                                    Badge(containerColor = GoldAccent) {
                                        Text("AI", color = Color.Black, fontSize = 9.sp)
                                    }
                                }) {
                                    Icon(
                                        imageVector = if (isSelected) Icons.Filled.AutoAwesome else Icons.Outlined.AutoAwesome,
                                        contentDescription = label,
                                        tint = if (isSelected) GoldAccent else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            } else {
                                Icon(
                                    imageVector = if (isSelected) icons.first else icons.second,
                                    contentDescription = label
                                )
                            }
                        },
                        label = { Text(label, fontSize = 11.sp, maxLines = 1) },
                        colors = NavigationRailItemDefaults.colors(
                            selectedIconColor = EmeraldPrimary,
                            selectedTextColor = EmeraldPrimary,
                            indicatorColor = GoldAccent.copy(alpha = 0.25f)
                        ),
                        modifier = Modifier
                            .padding(vertical = 2.dp)
                            .testTag("rail_item_${tab.name.lowercase()}")
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // Bottom Settings in Rail
                NavigationRailItem(
                    selected = state.currentTab == MainTab.SETTINGS,
                    onClick = { viewModel.setTab(MainTab.SETTINGS) },
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                    label = { Text("Settings", fontSize = 11.sp) },
                    colors = NavigationRailItemDefaults.colors(
                        selectedIconColor = EmeraldPrimary,
                        selectedTextColor = EmeraldPrimary,
                        indicatorColor = GoldAccent.copy(alpha = 0.25f)
                    ),
                    modifier = Modifier
                        .padding(bottom = 12.dp)
                        .testTag("rail_item_settings")
                )
            }

            // Main Content Area for Tablets / Google TV
            ContentPane(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )
        }
    } else {
        // Compact Handheld Phone Layout with Bottom NavigationBar
        Scaffold(
            modifier = modifier.fillMaxSize(),
            contentWindowInsets = WindowInsets.safeDrawing,
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 0.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .drawBehind {
                            drawLine(
                                color = Color.Gray.copy(alpha = 0.18f),
                                start = Offset(0f, 0f),
                                end = Offset(size.width, 0f),
                                strokeWidth = 1.dp.toPx()
                            )
                        }
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .testTag("deen_bottom_navigation")
                ) {
                    fiveTabs.forEachIndexed { index, tab ->
                        val (label, icons) = getTabMetadata(tab)
                        val isSelected = state.currentTab == tab

                        if (tab == MainTab.AI_GUIDE || index == 2) {
                            // Elevated Center DEEN AI Action Button - Only this item is elevated inside a prominent circle
                            NavigationBarItem(
                                selected = state.currentTab == MainTab.AI_GUIDE || state.isDeenAiSheetOpen,
                                onClick = { viewModel.openDeenAiSheet() },
                                icon = {
                                    Surface(
                                        shape = CircleShape,
                                        color = GoldAccent,
                                        shadowElevation = 8.dp,
                                        border = BorderStroke(3.dp, MaterialTheme.colorScheme.surface),
                                        modifier = Modifier
                                            .size(50.dp)
                                            .offset(y = (-10).dp)
                                    ) {
                                        Box(
                                            contentAlignment = Alignment.Center,
                                            modifier = Modifier.background(
                                                Brush.linearGradient(
                                                    colors = listOf(GoldAccent, GoldLight)
                                                )
                                            )
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.AutoAwesome,
                                                contentDescription = "Deen AI",
                                                tint = Color.Black,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                    }
                                },
                                label = {
                                    Text(
                                        text = "Deen AI",
                                        maxLines = 1,
                                        fontWeight = FontWeight.Bold,
                                        color = GoldAccent,
                                        fontSize = 11.sp
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    indicatorColor = Color.Transparent
                                ),
                                modifier = Modifier.testTag("nav_item_ai_guide")
                            )
                        } else {
                            NavigationBarItem(
                                selected = isSelected,
                                onClick = { viewModel.setTab(tab) },
                                icon = {
                                    Icon(
                                        imageVector = if (isSelected) icons.first else icons.second,
                                        contentDescription = label
                                    )
                                },
                                label = {
                                    Text(
                                        text = label,
                                        maxLines = 1,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = EmeraldPrimary,
                                    selectedTextColor = EmeraldPrimary,
                                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                    indicatorColor = GoldAccent.copy(alpha = 0.2f)
                                ),
                                modifier = Modifier.testTag("nav_item_${tab.name.lowercase()}")
                            )
                        }
                    }
                }
            }
        ) { paddingValues ->
            ContentPane(modifier = Modifier.padding(paddingValues))
        }
    }

    // Modal Settings Dialog
    if (state.isSettingsDialogOpen) {
        Dialog(
            onDismissRequest = { viewModel.closeSettings() },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(modifier = Modifier.fillMaxSize()) {
                SettingsScreen(
                    state = state,
                    viewModel = viewModel,
                    onBack = { viewModel.closeSettings() }
                )
            }
        }
    }

    // User Account & Login Dialog
    if (state.isUserAccountDialogOpen) {
        UserAccountDialog(
            state = state,
            viewModel = viewModel,
            onDismiss = { viewModel.closeUserAccountDialog() }
        )
    }

    // Admin Video / Subscriptions / Translation Management Dialog
    if (state.isAdminDialogOpen) {
        AdminVideoManagementDialog(
            isAdminLoggedIn = state.userProfile?.role == "admin" || state.isAdminLoggedIn,
            customVideos = state.customVideos,
            onLoginAdmin = { passcode ->
                viewModel.loginAdmin(passcode)
            },
            onLogoutAdmin = { viewModel.logoutAdmin() },
            onAddVideo = { video -> viewModel.addCustomVideo(video) },
            onDeleteVideo = { videoId -> viewModel.deleteCustomVideo(videoId) },
            onDismiss = { viewModel.closeAdminDialog() }
        )
    }

    // Daily Quranic Reflection Detail & Sharing Dialog
    state.viewingReflectionDetails?.let { reflection ->
        com.example.ui.components.DailyReflectionDetailDialog(
            reflection = reflection,
            onDismiss = { viewModel.closeDailyReflectionDetails() },
            onAskAi = { viewModel.askAiAboutDailyReflection(it) }
        )
    }

    // Subscription Required Gate Dialog for AI features
    if (state.showSubscriptionRequiredDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissSubscriptionRequiredDialog() },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = GoldAccent)
                    Text("Deen AI Subscription Required", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Gemini 3.5 Spiritual AI Guidance is reserved for subscribed members to maintain high server quality.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = EmeraldPrimary.copy(alpha = 0.12f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Monthly Plan: $4.99 / month", fontWeight = FontWeight.Bold, color = EmeraldPrimary)
                            Text("• Unlimited spiritual queries with Hadith citations", fontSize = 11.sp)
                            Text("• Personalized Ramadan & prayer habit coaching", fontSize = 11.sp)
                            Text("• Managed and verified by Deen365 administrators", fontSize = 11.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.dismissSubscriptionRequiredDialog()
                        viewModel.openUserAccountDialog()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                ) {
                    Text("View Plans & Subscribe", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissSubscriptionRequiredDialog() }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Deen Coins & Subscription Wallet Dialog
    if (state.isCoinsHubOpen) {
        CoinsHubDialog(
            state = state.coinsState,
            onDismiss = { viewModel.closeCoinsHub() },
            onPurchasePremium = { hours -> viewModel.purchasePremiumWithCoins(hours) },
            onOpenAdminPanel = {
                viewModel.closeCoinsHub()
                viewModel.openAdminOffers()
            }
        )
    }

    // Admin Promotional Offers Switchboard Dialog
    if (state.isAdminOffersOpen) {
        AdminOffersDialog(
            state = state.coinsState,
            proModuleIds = state.proModuleIds,
            onDismiss = { viewModel.closeAdminOffers() },
            onToggleOffer = { id, enabled -> viewModel.toggleAdminOffer(id, enabled) },
            onToggleModulePro = { id, isPro -> viewModel.toggleModuleProStatus(id, isPro) },
            onGrantTestCoins = { amount -> viewModel.grantAdminTestCoins(amount) },
            onToggleInstantPremium = { viewModel.toggleAdminInstantPremium() },
            onResetCoins = { viewModel.resetAdminCoins() }
        )
    }

    // Reciter / Speaker Selection Dialog
    if (state.isReciterDialogOpen) {
        ReciterSelectionDialog(
            currentReciter = state.salahAudioState.selectedReciter,
            isPlayingPreview = state.salahAudioState.isPlaying,
            onDismiss = { viewModel.closeReciterDialog() },
            onSelectReciter = { reciter -> viewModel.setSalahReciter(reciter) },
            onTogglePreviewAudio = { reciter ->
                viewModel.playSalahStepAudio("fatiha", "Surah Al-Fatiha Preview", "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ", reciter.previewUrl)
            }
        )
    }

    // Deen AI Spiritual Assistant Sheet
    if (state.isDeenAiSheetOpen) {
        DeenAiAssistantSheet(
            state = state,
            viewModel = viewModel,
            onDismiss = { viewModel.closeDeenAiSheet() }
        )
    }

    // Bottom Menu Customizer Dialog
    if (state.isBottomMenuCustomizerOpen) {
        BottomMenuCustomizerDialog(
            currentTabs = state.selectedBottomTabs,
            isPremium = state.coinsState.isPremium,
            onDismiss = { viewModel.closeBottomMenuCustomizer() },
            onSaveTabs = { newTabs -> viewModel.updateBottomTabs(newTabs) },
            onOpenCoinsHub = {
                viewModel.closeBottomMenuCustomizer()
                viewModel.openCoinsHub()
            }
        )
    }
}
