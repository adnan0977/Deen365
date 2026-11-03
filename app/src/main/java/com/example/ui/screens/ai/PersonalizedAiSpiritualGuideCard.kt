package com.example.ui.screens.ai

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.remote.PersonalizedSpiritualGuidance
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent

@Composable
fun PersonalizedAiSpiritualGuideCard(
    guidance: PersonalizedSpiritualGuidance?,
    isLoading: Boolean,
    focusArea: String,
    prayersCompletedToday: Int,
    fastingStreakDays: Int,
    completedDeeds: Set<Int>,
    onSelectFocusArea: (String) -> Unit,
    onToggleDeed: (Int) -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusAreas = listOf(
        Pair("salah_consistency", "🕌 Salah Consistency"),
        Pair("fasting_vitality", "🌙 Fasting & Ramadan"),
        Pair("anxiety_adversity", "🕊️ Peace & Hardship")
    )

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent.copy(alpha = 0.4f)),
        modifier = modifier.fillMaxWidth().testTag("ai_spiritual_guide_card")
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Row
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
                        modifier = Modifier.size(42.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("✨", fontSize = 22.sp)
                        }
                    }
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "AI Spiritual Companion",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = EmeraldPrimary.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "PERSONALIZED",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = EmeraldPrimary,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Daily reflection tailored to your prayers & fasting",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(
                    onClick = onRefresh,
                    modifier = Modifier.size(36.dp).testTag("refresh_ai_guide_button")
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp, color = EmeraldPrimary)
                    } else {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = EmeraldPrimary)
                    }
                }
            }

            // User Telemetry Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = EmeraldPrimary.copy(alpha = 0.1f),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(16.dp))
                        Column {
                            Text(text = "$prayersCompletedToday/5 Prayers Today", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = EmeraldPrimary)
                            Text(text = "Daily Compliance", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = GoldAccent.copy(alpha = 0.15f),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.NightsStay, contentDescription = null, tint = Color(0xFF8C6D00), modifier = Modifier.size(16.dp))
                        Column {
                            Text(text = "$fastingStreakDays-Day Fasting", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = Color(0xFF8C6D00))
                            Text(text = "Active Sawm Streak", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            // Focus Area Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                focusAreas.forEach { (key, label) ->
                    val isSelected = focusArea == key
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onSelectFocusArea(key) }
                            .testTag("focus_chip_$key")
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp)
                        ) {
                            Text(
                                text = label,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            if (guidance != null) {
                // Topic Title
                Text(
                    text = guidance.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )

                // Encouraging Inspirational Message
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = guidance.inspirationalMessage,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 20.sp,
                        modifier = Modifier.padding(12.dp)
                    )
                }

                // Quranic Reflection Section
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = EmeraldPrimary.copy(alpha = 0.08f)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.25f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("📖 Quranic Reflection", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = EmeraldPrimary)
                            Text(guidance.quranAyahReference, fontSize = 10.sp, color = EmeraldPrimary, fontWeight = FontWeight.SemiBold)
                        }
                        Text(
                            text = guidance.quranAyahArabic,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldPrimary,
                            textAlign = TextAlign.Right,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                        )
                        Text(
                            text = "\"${guidance.quranAyahTranslation}\"",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Relevant Prophetic Hadith Section
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = GoldAccent.copy(alpha = 0.1f)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent.copy(alpha = 0.35f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("📜 Authentic Hadith", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color(0xFF8C6D00))
                            Text(guidance.hadithReference, fontSize = 10.sp, color = Color(0xFF8C6D00), fontWeight = FontWeight.SemiBold)
                        }
                        Text(
                            text = guidance.hadithArabic,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF8C6D00),
                            textAlign = TextAlign.Right,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                        )
                        Text(
                            text = "\"${guidance.hadithTranslation}\"",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Recommended Dua Section
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("🤲 Recommended Supplication", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = EmeraldPrimary)
                        Text(
                            text = guidance.recommendedDuaArabic,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldPrimary,
                            textAlign = TextAlign.Right,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
                        )
                        Text(
                            text = "\"${guidance.recommendedDuaTranslation}\"",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Namaz & Fasting Advice
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("🕌 Namaz Guidance", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = EmeraldPrimary)
                            Text(text = guidance.prayerGuidance, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }

                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("🌙 Fasting Guidance", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = GoldAccent)
                            Text(text = guidance.fastingGuidance, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }

                // Daily Practical Deeds Checklist
                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Daily Practical Deeds (${completedDeeds.size}/${guidance.practicalDeeds.size})",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    guidance.practicalDeeds.forEachIndexed { idx, deed ->
                        val isDone = completedDeeds.contains(idx)
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isDone) EmeraldPrimary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onToggleDeed(idx) }
                                .testTag("deed_checkbox_$idx")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Checkbox(
                                    checked = isDone,
                                    onCheckedChange = { onToggleDeed(idx) }
                                )
                                Text(
                                    text = deed,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (isDone) EmeraldPrimary else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
