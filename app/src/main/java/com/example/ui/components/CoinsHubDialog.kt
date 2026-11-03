package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.repository.CoinTransaction
import com.example.data.repository.DeenCoinsState
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.EmeraldPrimaryVariant
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldLight
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CoinsHubDialog(
    state: DeenCoinsState,
    onDismiss: () -> Unit,
    onPurchasePremium: (hours: Int) -> Unit,
    onOpenAdminPanel: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: Store & Redeem, 1: Quests, 2: History

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .fillMaxHeight(0.88f)
                .clip(RoundedCornerShape(24.dp))
                .testTag("coins_hub_dialog"),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header with Gold Gradient and Coins Balance
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(EmeraldPrimary, EmeraldPrimaryVariant)
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = GoldAccent.copy(alpha = 0.25f),
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text("🪙", fontSize = 20.sp)
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Deen Coins Wallet",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    )
                                    Text(
                                        text = "1 Coin = 1 Hour of Premium Access",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = GoldLight
                                    )
                                }
                            }

                            IconButton(
                                onClick = onDismiss,
                                modifier = Modifier.testTag("close_coins_hub")
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Large Balance Display & Premium Status Pill
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Column {
                                Text(
                                    text = "AVAILABLE BALANCE",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = GoldLight.copy(alpha = 0.8f)
                                    )
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "${state.balance}",
                                        style = MaterialTheme.typography.headlineLarge.copy(
                                            fontWeight = FontWeight.Black,
                                            color = GoldLight,
                                            fontSize = 38.sp
                                        )
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Coins",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White.copy(alpha = 0.9f)
                                        )
                                    )
                                }
                            }

                            // Active Premium Badge
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (state.isPremium) GoldAccent else Color.White.copy(alpha = 0.15f),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (state.isPremium) GoldLight else Color.White.copy(alpha = 0.3f)
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = if (state.isPremium) Icons.Default.WorkspacePremium else Icons.Default.Lock,
                                        contentDescription = "Premium",
                                        tint = if (state.isPremium) EmeraldPrimaryVariant else Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = if (state.isPremium) "PRO ACTIVE: ${state.premiumRemainingHours}h left" else "FREE TIER",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = if (state.isPremium) EmeraldPrimaryVariant else Color.White
                                    )
                                }
                            }
                        }
                    }
                }

                // Sub-tabs: Store, Quests, History
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Unlock Pro", fontWeight = FontWeight.SemiBold) },
                        icon = { Icon(Icons.Default.Storefront, contentDescription = null, modifier = Modifier.size(18.dp)) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Daily Quests", fontWeight = FontWeight.SemiBold) },
                        icon = { Icon(Icons.Default.AssignmentTurnedIn, contentDescription = null, modifier = Modifier.size(18.dp)) }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text("History", fontWeight = FontWeight.SemiBold) },
                        icon = { Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(18.dp)) }
                    )
                }

                // Content
                Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    when (selectedTab) {
                        0 -> StoreTabContent(state = state, onPurchase = onPurchasePremium)
                        1 -> QuestsTabContent(state = state)
                        2 -> HistoryTabContent(transactions = state.transactions)
                    }
                }

                // Bottom Admin Panel Bar
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = "Admin",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Admin Offers & Promos",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
                            )
                        }

                        Button(
                            onClick = onOpenAdminPanel,
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            ),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("admin_offers_button")
                        ) {
                            Text("Manage Offers", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StoreTabContent(
    state: DeenCoinsState,
    onPurchase: (hours: Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = GoldAccent.copy(alpha = 0.12f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Stars,
                        contentDescription = null,
                        tint = GoldAccent,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Premium Features Unlocked with Coins",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "• Full Mosque Locator with GPS radar\n• Premium Royal Obsidian & Sapphire themes\n• Bottom navigation tab customizer\n• Quran & Hadith English/Urdu translations",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        item {
            Text(
                text = "Redeem Premium Access",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
        }

        // Option 1: 24 Hours / 1 Day (24 Coins)
        item {
            PurchasePackageCard(
                title = "24 Hours (1 Full Day)",
                subtitle = "Earned by completing 5 Namaz or Fasting today",
                coinCost = 24,
                userBalance = state.balance,
                isPopular = true,
                onBuy = { onPurchase(24) }
            )
        }

        // Option 2: 72 Hours / 3 Days (72 Coins)
        item {
            PurchasePackageCard(
                title = "72 Hours (3 Days Pro)",
                subtitle = "Weekend spiritual journey & uninterrupted focus",
                coinCost = 72,
                userBalance = state.balance,
                isPopular = false,
                onBuy = { onPurchase(72) }
            )
        }

        // Option 3: 168 Hours / 1 Week (168 Coins)
        item {
            PurchasePackageCard(
                title = "168 Hours (1 Full Week)",
                subtitle = "Best value for regular prayer & Quran reading streaks",
                coinCost = 168,
                userBalance = state.balance,
                isPopular = false,
                onBuy = { onPurchase(168) }
            )
        }
    }
}

@Composable
private fun PurchasePackageCard(
    title: String,
    subtitle: String,
    coinCost: Int,
    userBalance: Int,
    isPopular: Boolean,
    onBuy: () -> Unit
) {
    val canAfford = userBalance >= coinCost

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isPopular) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isPopular) MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
            else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    if (isPopular) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = GoldAccent
                        ) {
                            Text(
                                text = "POPULAR",
                                color = Color.Black,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "🪙 $coinCost Deen Coins",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = GoldAccent
                    )
                )
            }

            Button(
                onClick = onBuy,
                enabled = canAfford,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (canAfford) EmeraldPrimary else MaterialTheme.colorScheme.outlineVariant
                ),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                modifier = Modifier.testTag("buy_pack_${coinCost}")
            ) {
                Text(if (canAfford) "Unlock" else "Need Coins", fontSize = 13.sp)
            }
        }
    }
}

@Composable
private fun QuestsTabContent(state: DeenCoinsState) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Active Spiritual Rewards",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Earn Deen Coins automatically through worship and study:",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Quest 1: 5 Namaz
        item {
            QuestItemCard(
                icon = Icons.Default.Schedule,
                title = "Pray All 5 Daily Namaz",
                description = "Complete Fajr, Dhuhr, Asr, Maghrib, and Isha today",
                rewardText = "+24 Coins (1 Day Pro)",
                isCompletedToday = state.namazRewardClaimedDate.isNotEmpty()
            )
        }

        // Quest 2: Fasting
        item {
            QuestItemCard(
                icon = Icons.Default.NightsStay,
                title = "Observe a Day of Fasting (Sawm)",
                description = "Mark your completed fast from Suhur to Iftar",
                rewardText = "+24 Coins (1 Day Pro)",
                isCompletedToday = state.fastingRewardClaimedDate.isNotEmpty()
            )
        }

        // Quest 3: Quran Reading
        item {
            QuestItemCard(
                icon = Icons.Default.MenuBook,
                title = "Read a Quran Chapter (Surah)",
                description = "Complete reading any Surah to earn coins",
                rewardText = "+24 Coins per Chapter",
                isCompletedToday = state.quranChaptersCompleted.isNotEmpty()
            )
        }

        // Quest 4: Hadith Reading
        item {
            QuestItemCard(
                icon = Icons.Default.LibraryBooks,
                title = "Study Hadith Milestone",
                description = "Read a Hadith chapter milestone from Bukhari or Muslim",
                rewardText = "+24 Coins per Milestone",
                isCompletedToday = state.hadithChaptersCompleted.isNotEmpty()
            )
        }
    }
}

@Composable
private fun QuestItemCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    rewardText: String,
    isCompletedToday: Boolean
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = if (isCompletedToday) EmeraldPrimary else MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (isCompletedToday) Icons.Default.Check else icon,
                        contentDescription = null,
                        tint = if (isCompletedToday) Color.White else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Reward: $rewardText",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = GoldAccent,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }

            if (isCompletedToday) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = EmeraldPrimary.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "DONE",
                        color = EmeraldPrimary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun HistoryTabContent(transactions: List<CoinTransaction>) {
    if (transactions.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No coin transactions yet.\nComplete prayers or read Quran to earn coins!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    } else {
        val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy • HH:mm", Locale.getDefault()) }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(transactions) { tx ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = tx.reason,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                            )
                            Text(
                                text = dateFormat.format(Date(tx.timestamp)),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Text(
                            text = if (tx.isDeduction) "-${tx.amount} 🪙" else "+${tx.amount} 🪙",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (tx.isDeduction) MaterialTheme.colorScheme.error else EmeraldPrimary
                            )
                        )
                    }
                }
            }
        }
    }
}
