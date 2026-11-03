package com.example.ui.screens.ai

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.remote.AiGuidanceResult
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent

@Composable
fun HadithDuaAiCompanionView(
    result: AiGuidanceResult?,
    isLoading: Boolean,
    onSearch: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var queryText by remember { mutableStateOf("") }
    var selectedPill by remember { mutableStateOf<String?>(null) }

    val scenarioPills = listOf(
        "Anxiety & Stress",
        "Sustenance & Debt",
        "Patience in Hardship",
        "Istikhara & Decisions",
        "Family Reconciliation",
        "Forgiveness & Repentance"
    )

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.25f)),
        modifier = modifier.fillMaxWidth().testTag("hadith_dua_ai_companion_card")
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = EmeraldPrimary.copy(alpha = 0.12f),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = EmeraldPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Column {
                        Text(
                            text = "Hadith & Dua AI Companion",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Authentic Sunnah answers for your life situations",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Search Bar
            OutlinedTextField(
                value = queryText,
                onValueChange = { queryText = it },
                placeholder = { Text("What situation or question is on your heart?", fontSize = 13.sp) },
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = EmeraldPrimary) },
                trailingIcon = {
                    if (queryText.isNotBlank()) {
                        IconButton(onClick = {
                            onSearch(queryText)
                        }) {
                            Icon(Icons.Default.ArrowForward, contentDescription = "Ask", tint = EmeraldPrimary)
                        }
                    }
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = {
                    if (queryText.isNotBlank()) onSearch(queryText)
                }),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth().testTag("hadith_ai_query_input")
            )

            // Quick Scenario Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(scenarioPills) { pill ->
                    val isSelected = selectedPill == pill
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            selectedPill = pill
                            queryText = pill
                            onSearch(pill)
                        },
                        label = { Text(pill, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EmeraldPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            // Loading Indicator
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = EmeraldPrimary)
                        Text(
                            text = "Searching verified Sunnah literature...",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Results View
            AnimatedVisibility(visible = result != null && !isLoading) {
                if (result != null) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Citation Badge
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = GoldAccent.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = result.reference,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = GoldAccent),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }

                            IconButton(
                                onClick = {
                                    val copyContent = "${result.reference}\n\n${result.arabicText}\n\n${result.translation}\n\nExplanation:\n${result.spiritualInsight}\n\nDua:\n${result.recommendedDua}"
                                    val clip = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    clip.setPrimaryClip(ClipData.newPlainText("Hadith Companion", copyContent))
                                    Toast.makeText(context, "Hadith & Dua copied!", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.size(28.dp).testTag("copy_hadith_companion_btn")
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = EmeraldPrimary, modifier = Modifier.size(16.dp))
                            }
                        }

                        // Arabic Script
                        Text(
                            text = result.arabicText,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontSize = 20.sp,
                                lineHeight = 32.sp,
                                textAlign = TextAlign.Right,
                                fontWeight = FontWeight.Medium
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        // English Translation
                        Text(
                            text = "\"${result.translation}\"",
                            style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 20.sp, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        // Practical Commentary
                        Text(
                            text = result.spiritualInsight,
                            style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp),
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        // Action Steps
                        if (result.practicalActionSteps.isNotEmpty()) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "Sunnah Action Steps:",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = EmeraldPrimary)
                                )
                                result.practicalActionSteps.forEach { step ->
                                    Row(
                                        verticalAlignment = Alignment.Top,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text("•", color = EmeraldPrimary, fontWeight = FontWeight.Bold)
                                        Text(step, style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            }
                        }

                        // Recommended Dua
                        if (result.recommendedDua.isNotBlank()) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = EmeraldPrimary.copy(alpha = 0.1f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(10.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = "Prescribed Supplication:",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = EmeraldPrimary)
                                    )
                                    Text(
                                        text = result.recommendedDua,
                                        style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp),
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
