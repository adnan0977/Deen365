package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.repository.AdminOffer
import com.example.data.repository.DeenCoinsState
import com.example.ui.model.DefaultHomeModules
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent

@Composable
fun AdminOffersDialog(
    state: DeenCoinsState,
    proModuleIds: Set<String> = emptySet(),
    onDismiss: () -> Unit,
    onToggleOffer: (offerId: String, isEnabled: Boolean) -> Unit,
    onToggleModulePro: ((moduleId: String, isPro: Boolean) -> Unit)? = null,
    onGrantTestCoins: (amount: Int) -> Unit,
    onToggleInstantPremium: () -> Unit,
    onResetCoins: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .fillMaxHeight(0.85f)
                .clip(RoundedCornerShape(20.dp))
                .testTag("admin_offers_dialog"),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Deen Admin Switchboard",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = "Manage Promotional Offers & Coin Economy",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                )
                            }
                        }

                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }
                }

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Quick Action Test Buttons
                    item {
                        Text(
                            text = "Admin Test Controls",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { onGrantTestCoins(24) },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("+24 Coins", fontSize = 11.5.sp)
                            }

                            Button(
                                onClick = { onGrantTestCoins(100) },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("+100 Coins", fontSize = 11.5.sp)
                            }

                            Button(
                                onClick = onToggleInstantPremium,
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (state.isPremium) MaterialTheme.colorScheme.error else GoldAccent
                                ),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                modifier = Modifier.weight(1.3f)
                            ) {
                                Text(if (state.isPremium) "Disable Pro" else "Force Pro", fontSize = 11.5.sp, color = Color.Black)
                            }
                        }
                    }

                    item {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                        Text(
                            text = "Active Promotional Offers & Quests",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Offers List
                    items(state.offers) { offer ->
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (offer.isEnabled) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (offer.isEnabled) MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                                else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = offer.title,
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                        )
                                        if (offer.isSpecialPromo) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = GoldAccent
                                            ) {
                                                Text(
                                                    text = "SPECIAL",
                                                    color = Color.Black,
                                                    fontSize = 8.sp,
                                                    fontWeight = FontWeight.Black,
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                )
                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = offer.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = if (offer.multiplier > 1.0) "Rate: ${offer.multiplier}x Multiplier"
                                        else "Reward: +${offer.rewardCoins} Deen Coins",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = EmeraldPrimary,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    )
                                }

                                Switch(
                                    checked = offer.isEnabled,
                                    onCheckedChange = { onToggleOffer(offer.id, it) },
                                    modifier = Modifier.testTag("switch_offer_${offer.id}")
                                )
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Module PRO Access Controls",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Mark any module as PRO. Premium users & Admins get full access; standard users must unlock via Deen Coins.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    items(DefaultHomeModules.ALL_MODULES) { module ->
                        val isPro = proModuleIds.contains(module.id) || module.isPro
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = if (isPro) GoldAccent.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)),
                            modifier = Modifier.fillMaxWidth().testTag("admin_pro_toggle_${module.id}")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isPro) GoldAccent else EmeraldPrimary.copy(alpha = 0.15f)
                                    ) {
                                        Text(
                                            text = if (isPro) "PRO" else "FREE",
                                            color = if (isPro) Color.Black else EmeraldPrimary,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 9.sp,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Column {
                                        Text(
                                            text = module.title,
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = module.subtitle,
                                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Switch(
                                    checked = isPro,
                                    onCheckedChange = { onToggleModulePro?.invoke(module.id, it) },
                                    modifier = Modifier.testTag("switch_pro_${module.id}")
                                )
                            }
                        }
                    }

                    item {
                        OutlinedButton(
                            onClick = onResetCoins,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Reset Coins & Quests to Factory Default", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}
