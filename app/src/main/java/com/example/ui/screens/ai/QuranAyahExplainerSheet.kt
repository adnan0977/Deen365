package com.example.ui.screens.ai

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.AyahEntity
import com.example.data.model.AyahExplanationResult
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranAyahExplainerSheet(
    isOpen: Boolean,
    ayah: AyahEntity?,
    surahName: String,
    explanation: AyahExplanationResult?,
    isLoading: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!isOpen || ayah == null) return

    val context = LocalContext.current
    var selectedSection by remember { mutableStateOf(0) } // 0: Revelation, 1: Interpretation, 2: Actions

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = { BottomSheetDefaults.DragHandle() },
        modifier = modifier.testTag("quran_ayah_explainer_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
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
                        color = GoldAccent.copy(alpha = 0.2f),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = GoldAccent,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                    Column {
                        Text(
                            text = "AI Tafsir & Context",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Surah $surahName • Ayah ${ayah.ayahNumber}",
                            style = MaterialTheme.typography.bodySmall,
                            color = EmeraldPrimary
                        )
                    }
                }

                IconButton(
                    onClick = {
                        val textToCopy = "Surah $surahName (${ayah.surahNumber}:${ayah.ayahNumber})\n\n${ayah.arabicText}\n\n\"${ayah.translation}\"\n\nContext & Tafsir:\n${explanation?.classicalInterpretation.orEmpty()}"
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("Ayah Explanation", textToCopy))
                        Toast.makeText(context, "Copied explanation to clipboard", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.testTag("copy_ayah_explanation_btn")
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            // Verse Card
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = EmeraldPrimary.copy(alpha = 0.08f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = ayah.arabicText,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontSize = 24.sp,
                            lineHeight = 38.sp,
                            textAlign = TextAlign.Right,
                            fontWeight = FontWeight.Medium
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = ayah.translation,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            lineHeight = 22.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        CircularProgressIndicator(color = EmeraldPrimary)
                        Text(
                            text = "Consulting authentic classical Tafsir...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else if (explanation != null) {
                // Section Tabs
                TabRow(
                    selectedTabIndex = selectedSection,
                    containerColor = Color.Transparent,
                    contentColor = EmeraldPrimary
                ) {
                    Tab(
                        selected = selectedSection == 0,
                        onClick = { selectedSection = 0 },
                        text = { Text("Revelation", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedSection == 1,
                        onClick = { selectedSection = 1 },
                        text = { Text("Classic Tafsir", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedSection == 2,
                        onClick = { selectedSection = 2 },
                        text = { Text("Daily Deeds", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                    )
                }

                when (selectedSection) {
                    0 -> {
                        // Revelation Context (Asbab al-Nuzul)
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Icon(Icons.Default.HistoryEdu, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(18.dp))
                                    Text(
                                        text = "Asbab al-Nuzul (Circumstances of Revelation)",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Text(
                                    text = explanation.revelationContext,
                                    style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                    1 -> {
                        // Classical Interpretation
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Icon(Icons.Default.MenuBook, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(18.dp))
                                    Text(
                                        text = "Classical Commentary Summary",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Text(
                                    text = explanation.classicalInterpretation,
                                    style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                    2 -> {
                        // Practical Action Steps
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(18.dp))
                                    Text(
                                        text = "3 Concrete Actions for Today",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                explanation.practicalActionSteps.forEachIndexed { idx, step ->
                                    Row(
                                        verticalAlignment = Alignment.Top,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Surface(
                                            shape = CircleShape,
                                            color = EmeraldPrimary,
                                            modifier = Modifier.size(20.dp).padding(top = 2.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text(
                                                    text = "${idx + 1}",
                                                    style = MaterialTheme.typography.labelSmall.copy(color = Color.White, fontSize = 10.sp)
                                                )
                                            }
                                        }
                                        Text(
                                            text = step,
                                            style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 20.sp),
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Recommended Dua Card
                if (explanation.recommendedDua.isNotBlank()) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = GoldAccent.copy(alpha = 0.12f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Related Supplication (Dua):",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = GoldAccent)
                                )
                                TextButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        clipboard.setPrimaryClip(ClipData.newPlainText("Ayah Dua", explanation.recommendedDua))
                                        Toast.makeText(context, "Dua copied!", Toast.LENGTH_SHORT).show()
                                    },
                                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("Copy Dua", fontSize = 11.sp, color = EmeraldPrimary)
                                }
                            }
                            Text(
                                text = explanation.recommendedDua,
                                style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
