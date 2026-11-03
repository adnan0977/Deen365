package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.viewmodel.MainTab

@Composable
fun BottomMenuCustomizerDialog(
    currentTabs: List<MainTab>,
    isPremium: Boolean,
    onDismiss: () -> Unit,
    onSaveTabs: (List<MainTab>) -> Unit,
    onOpenCoinsHub: () -> Unit
) {
    var tabsOrder by remember { mutableStateOf(currentTabs) }

    val availableTabs = listOf(
        MainTab.DASHBOARD,
        MainTab.NAMAZ,
        MainTab.AI_GUIDE,
        MainTab.QURAN,
        MainTab.COMMUNITY,
        MainTab.INTERACTIVE_LEARNING,
        MainTab.SAHABA_BIOGRAPHIES,
        MainTab.SETTINGS,
        MainTab.FASTING,
        MainTab.TASBIH,
        MainTab.HADITH_DUAS,
        MainTab.QIBLA,
        MainTab.MOSQUES,
        MainTab.VIDEOS
    )

    fun getTabLabel(tab: MainTab): String = when (tab) {
        MainTab.DASHBOARD -> "Home"
        MainTab.NAMAZ -> "Namaz"
        MainTab.AI_GUIDE -> "Deen AI (Center)"
        MainTab.QURAN -> "Quran"
        MainTab.MORE -> "More"
        MainTab.FASTING -> "Fasting"
        MainTab.TASBIH -> "Dhikr"
        MainTab.HADITH_DUAS -> "Hadith"
        MainTab.QIBLA -> "Qibla"
        MainTab.MOSQUES -> "Mosques"
        MainTab.VIDEOS -> "Videos"
        MainTab.ISLAMIC_HERITAGE -> "Heritage"
        MainTab.ISLAMIC_BOOKS -> "Books"
        MainTab.COMMUNITY -> "Community"
        MainTab.INTERACTIVE_LEARNING -> "Academy"
        MainTab.SAHABA_BIOGRAPHIES -> "Sahaba"
        MainTab.NAMAZ_FASTING -> "Namaz"
        MainTab.SETTINGS -> "Settings"
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.85f)
                .testTag("bottom_menu_customizer_dialog")
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Bottom Menu Order",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = GoldAccent
                            ) {
                                Text(
                                    text = "PRO",
                                    color = Color.Black,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                )
                            }
                        }
                        Text(
                            text = "Arrange your 5 bottom navigation slots",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (!isPremium) {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = GoldAccent.copy(alpha = 0.15f)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "Premium Feature Locked",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Customizing bottom navigation tabs requires an active Pro subscription. Unlock 24 hours of Pro with 24 Deen Coins (earned free by praying 5 Namaz or fasting)!",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = onOpenCoinsHub,
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                            ) {
                                Text("Unlock with Deen Coins (24 🪙)")
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }

                Text(
                    text = "Current 5 Slots (Use arrows to reorder):",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(tabsOrder) { index, tab ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (index == 2) GoldAccent.copy(alpha = 0.15f)
                                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (index == 2) GoldAccent else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = MaterialTheme.colorScheme.primaryContainer,
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = "${index + 1}",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = getTabLabel(tab),
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
                                    )
                                    if (index == 2) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "(Center)",
                                            style = MaterialTheme.typography.labelSmall.copy(color = GoldAccent)
                                        )
                                    }
                                }

                                Row {
                                    IconButton(
                                        onClick = {
                                            if (index > 0 && isPremium) {
                                                val list = tabsOrder.toMutableList()
                                                val item = list.removeAt(index)
                                                list.add(index - 1, item)
                                                tabsOrder = list
                                            }
                                        },
                                        enabled = isPremium && index > 0
                                    ) {
                                        Icon(Icons.Default.ArrowUpward, contentDescription = "Move Up", modifier = Modifier.size(18.dp))
                                    }

                                    IconButton(
                                        onClick = {
                                            if (index < tabsOrder.size - 1 && isPremium) {
                                                val list = tabsOrder.toMutableList()
                                                val item = list.removeAt(index)
                                                list.add(index + 1, item)
                                                tabsOrder = list
                                            }
                                        },
                                        enabled = isPremium && index < tabsOrder.size - 1
                                    ) {
                                        Icon(Icons.Default.ArrowDownward, contentDescription = "Move Down", modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            tabsOrder = listOf(
                                MainTab.DASHBOARD,
                                MainTab.NAMAZ,
                                MainTab.AI_GUIDE,
                                MainTab.QURAN,
                                MainTab.MORE
                            )
                        },
                        enabled = isPremium,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Default", fontSize = 12.sp)
                    }

                    Button(
                        onClick = {
                            onSaveTabs(tabsOrder)
                            onDismiss()
                        },
                        enabled = isPremium,
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Apply", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
