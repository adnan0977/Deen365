package com.example.ui.screens.ai

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.example.data.remote.AiGuidanceResult
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.EmeraldPrimaryVariant
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldLight

@Composable
fun PersonalizedGuidanceCard(
    guidance: AiGuidanceResult?,
    isLoading: Boolean,
    completedPrayersCount: Int,
    prayerStreak: Int,
    fastingStreak: Int,
    isFastingToday: Boolean,
    onRefreshGuidance: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent.copy(alpha = 0.35f)),
        modifier = modifier.fillMaxWidth().testTag("personalized_guidance_card")
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header: Title & Dynamic Telemetry Badges
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
                        color = GoldAccent.copy(alpha = 0.2f),
                        modifier = Modifier.size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Psychology,
                                contentDescription = null,
                                tint = GoldAccent,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                    Column {
                        Text(
                            text = "Daily Spiritual Advisor",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "AI reflection based on your live habits",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(
                    onClick = onRefreshGuidance,
                    modifier = Modifier.size(34.dp).testTag("refresh_spiritual_guidance_btn")
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = EmeraldPrimary)
                    } else {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = EmeraldPrimary, modifier = Modifier.size(18.dp))
                    }
                }
            }

            // Real Telemetry Pill Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Prayer Streak Badge
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = EmeraldPrimary.copy(alpha = 0.1f),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(14.dp))
                        Column {
                            Text(text = "$completedPrayersCount/5 Today", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp), color = EmeraldPrimary)
                            Text(text = "$prayerStreak-Day Streak", style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                // Fasting Status Badge
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = GoldAccent.copy(alpha = 0.12f),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.WbTwilight, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(14.dp))
                        Column {
                            Text(text = if (isFastingToday) "Fasting Active" else "Not Fasting", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp), color = GoldAccent)
                            Text(text = "$fastingStreak-Day Streak", style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            // Guidance Content
            if (guidance != null) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = guidance.spiritualInsight,
                        style = MaterialTheme.typography.bodySmall.copy(lineHeight = 19.sp),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // Prophetic Reference
                    if (guidance.arabicText.isNotBlank()) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = guidance.arabicText,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = EmeraldPrimary,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Right
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Text(
                                    text = "\"${guidance.translation}\" — ${guidance.reference}",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // Top Action Step
                    if (guidance.practicalActionSteps.isNotEmpty()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(top = 2.dp)
                        ) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(14.dp))
                            Text(
                                text = "Action: ${guidance.practicalActionSteps.first()}",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                            )
                        }
                    }
                }
            } else if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(strokeWidth = 2.dp, color = EmeraldPrimary, modifier = Modifier.size(24.dp))
                }
            } else {
                Button(
                    onClick = onRefreshGuidance,
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("generate_initial_guidance_btn")
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Generate Today's Custom Spiritual Advice", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
