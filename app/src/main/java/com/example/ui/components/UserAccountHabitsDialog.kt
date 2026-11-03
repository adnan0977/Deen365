package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
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
import com.example.data.local.PrayerRecordEntity
import com.example.data.local.UserHabitEntity
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.EmeraldPrimaryVariant
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserAccountHabitsDialog(
    userHabit: UserHabitEntity,
    prayerRecord: PrayerRecordEntity,
    isGeneratingAiHabit: Boolean,
    aiHabitInsight: String,
    onSaveUserHabit: (UserHabitEntity) -> Unit,
    onGenerateAiInsight: () -> Unit,
    onDismiss: () -> Unit
) {
    var isEditingProfile by remember { mutableStateOf(false) }
    var nameInput by remember { mutableStateOf(userHabit.userName) }
    var emailInput by remember { mutableStateOf(userHabit.userEmail) }
    var goalInput by remember { mutableStateOf(userHabit.primarySpiritualGoal) }
    var showSubscriptionSuccess by remember { mutableStateOf(false) }

    val completedPrayersCount = listOf(
        prayerRecord.fajr,
        prayerRecord.dhuhr,
        prayerRecord.asr,
        prayerRecord.maghrib,
        prayerRecord.isha
    ).count { it }

    val watchedCount = userHabit.getWatchedIdsSet().size

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f)
                .clip(RoundedCornerShape(24.dp)),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = EmeraldPrimary.copy(alpha = 0.15f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "Profile",
                                tint = EmeraldPrimary,
                                modifier = Modifier
                                    .padding(8.dp)
                                    .size(28.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Account & Spiritual Habits",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = userHabit.subscriptionTier,
                                style = MaterialTheme.typography.bodySmall,
                                color = GoldAccent
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("user_dialog_close")
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Profile & Subscription Banner
                    item {
                        Card(
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = userHabit.userName,
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = userHabit.userEmail,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    FilledTonalButton(
                                        onClick = { isEditingProfile = !isEditingProfile },
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (isEditingProfile) Icons.Default.Check else Icons.Default.Edit,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(if (isEditingProfile) "Done" else "Edit")
                                    }
                                }

                                if (isEditingProfile) {
                                    OutlinedTextField(
                                        value = nameInput,
                                        onValueChange = { nameInput = it },
                                        label = { Text("Display Name") },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    OutlinedTextField(
                                        value = emailInput,
                                        onValueChange = { emailInput = it },
                                        label = { Text("Email Address") },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    OutlinedTextField(
                                        value = goalInput,
                                        onValueChange = { goalInput = it },
                                        label = { Text("Spiritual Goal") },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Button(
                                        onClick = {
                                            onSaveUserHabit(
                                                userHabit.copy(
                                                    userName = if (nameInput.isBlank()) userHabit.userName else nameInput.trim(),
                                                    userEmail = if (emailInput.isBlank()) userHabit.userEmail else emailInput.trim(),
                                                    primarySpiritualGoal = if (goalInput.isBlank()) userHabit.primarySpiritualGoal else goalInput.trim(),
                                                    isLoggedIn = true
                                                )
                                            )
                                            isEditingProfile = false
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                                    ) {
                                        Text("Save Profile Changes")
                                    }
                                }

                                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                                // Subscription Tier status
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.WorkspacePremium,
                                            contentDescription = "Subscription",
                                            tint = GoldAccent,
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Column {
                                            Text(
                                                text = "Subscription Status",
                                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                                            )
                                            Text(
                                                text = if (userHabit.subscriptionTier.contains("Premium")) "Premium Supporter (Active)" else "Free Member",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = if (userHabit.subscriptionTier.contains("Premium")) EmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }

                                    if (!userHabit.subscriptionTier.contains("Premium")) {
                                        Button(
                                            onClick = {
                                                onSaveUserHabit(
                                                    userHabit.copy(
                                                        subscriptionTier = "Deen365 Premium Supporter",
                                                        isSubscriptionActive = true
                                                    )
                                                )
                                                showSubscriptionSuccess = true
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = Color.Black),
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                        ) {
                                            Text("Upgrade")
                                        }
                                    }
                                }

                                if (showSubscriptionSuccess) {
                                    Text(
                                        text = "Alhamdulillah! You are now a Deen365 Premium Supporter.",
                                        color = EmeraldPrimary,
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                                    )
                                }
                            }
                        }
                    }

                    // Habit Analytics Summary Cards
                    item {
                        Text(
                            text = "Daily Spiritual & Study Habits",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Today's Namaz card
                            Card(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(16.dp))
                                        Text("Daily Salah", style = MaterialTheme.typography.labelSmall)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "$completedPrayersCount / 5",
                                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                        color = EmeraldPrimary
                                    )
                                    Text(
                                        text = if (completedPrayersCount == 5) "All completed!" else "${5 - completedPrayersCount} remaining",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            // Watch time card
                            Card(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(Icons.Default.PlayCircle, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(16.dp))
                                        Text("Lectures", style = MaterialTheme.typography.labelSmall)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "$watchedCount watched",
                                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                        color = GoldAccent
                                    )
                                    Text(
                                        text = "${userHabit.totalWatchMinutes} study mins",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    // Primary Goal Card
                    item {
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = EmeraldPrimary.copy(alpha = 0.08f)),
                            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(EmeraldPrimary.copy(alpha = 0.3f), GoldAccent.copy(alpha = 0.3f))))
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(Icons.Default.TrackChanges, contentDescription = null, tint = EmeraldPrimary)
                                Column {
                                    Text(
                                        text = "Spiritual Focus & Goal",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                                        color = EmeraldPrimary
                                    )
                                    Text(
                                        text = userHabit.primarySpiritualGoal,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }

                    // AI Habit Understanding Section
                    item {
                        Card(
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(GoldAccent.copy(alpha = 0.5f), EmeraldPrimary.copy(alpha = 0.5f))))
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = "AI Habit Guidance",
                                        tint = GoldAccent,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Column {
                                        Text(
                                            text = "AI Spiritual Habit Understanding",
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "Powered by Gemini 3.5 Flash",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = GoldAccent
                                        )
                                    }
                                }

                                Text(
                                    text = "Deen AI evaluates your prayer consistency, lecture topics watched, and spiritual goals to synthesize personalized diagnosis, encouragement, and actionable routines.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Button(
                                    onClick = onGenerateAiInsight,
                                    enabled = !isGeneratingAiHabit,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("analyze_habits_button"),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = EmeraldPrimary
                                    )
                                ) {
                                    if (isGeneratingAiHabit) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(18.dp),
                                            color = Color.White,
                                            strokeWidth = 2.dp
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Analyzing Spiritual Habits...")
                                    } else {
                                        Icon(Icons.Default.Psychology, contentDescription = null)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Analyze My Habits with AI")
                                    }
                                }

                                val displayInsight = if (aiHabitInsight.isNotBlank()) aiHabitInsight else userHabit.lastAiHabitInsight
                                if (displayInsight.isNotBlank()) {
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                Icon(Icons.Default.Lightbulb, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(18.dp))
                                                Text(
                                                    text = "AI Spiritual Guidance",
                                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                            }
                                            Text(
                                                text = displayInsight,
                                                style = MaterialTheme.typography.bodySmall.copy(lineHeight = 20.sp),
                                                color = MaterialTheme.colorScheme.onSurface
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
}
