package com.example.ui.screens.quran

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.local.AyahEntity
import com.example.data.seed.RabbanaDua
import com.example.data.seed.RabbanaSeedData
import com.example.data.seed.SajdahVerse
import com.example.data.seed.SajdahVersesData
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent

@Composable
fun RabbanaDuasDialog(
    isOpen: Boolean,
    onDismiss: () -> Unit
) {
    if (!isOpen) return

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.85f)
                .testTag("rabbana_duas_dialog")
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "40 Rabbana Duas",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Prophetic supplications from the Holy Quran",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(Modifier.height(10.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(RabbanaSeedData.ALL_DUAS) { dua ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = EmeraldPrimary.copy(alpha = 0.15f)
                                    ) {
                                        Text(
                                            text = "${dua.surahName} (${dua.ayahCoordinate})",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = EmeraldPrimary,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Text(
                                        text = dua.theme,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Spacer(Modifier.height(8.dp))

                                Text(
                                    text = dua.arabicText,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        lineHeight = 26.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    ),
                                    textAlign = TextAlign.Right,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(Modifier.height(4.dp))

                                Text(
                                    text = "\"${dua.translation}\"",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(Modifier.height(4.dp))

                                Text(
                                    text = "Benefit: ${dua.spiritualBenefit}",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SajdahVersesDialog(
    isOpen: Boolean,
    onDismiss: () -> Unit
) {
    if (!isOpen) return

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.85f)
                .testTag("sajdah_verses_dialog")
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "15 Sajdah Verses (Tilawah)",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Verses requiring prostration of recitation",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(Modifier.height(10.dp))

                // Sajdah Tilawah Supplication Card
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = EmeraldPrimary.copy(alpha = 0.12f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Du'a for Sajdah Tilawah:",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = EmeraldPrimary
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = SajdahVersesData.DEFAULT_SAJDA_DUA_ARABIC,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            textAlign = TextAlign.Right,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = SajdahVersesData.DEFAULT_SAJDA_DUA_TRANSLATION,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(Modifier.height(10.dp))

                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(SajdahVersesData.ALL_SAJDAH_VERSES) { sajdah ->
                        Card(
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "${sajdah.id}. ${sajdah.surahName} (${sajdah.surahNumber}:${sajdah.ayahNumber})",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = EmeraldPrimary
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = GoldAccent.copy(alpha = 0.2f)
                                    ) {
                                        Text(
                                            text = sajdah.fiqhRuling,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = sajdah.arabicVerseText,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    textAlign = TextAlign.Right,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(Modifier.height(2.dp))
                                Text(
                                    text = sajdah.englishTranslation,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GoToVerseDialog(
    isOpen: Boolean,
    onJump: (String) -> Unit,
    onDismiss: () -> Unit
) {
    if (!isOpen) return

    var coordinateInput by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Go to Ayah Coordinate", fontWeight = FontWeight.Bold)
        },
        text = {
            Column {
                Text(
                    text = "Enter Surah and Ayah number separated by a colon (e.g. 2:255 for Ayat al-Kursi, 36:1, 18:10):",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    value = coordinateInput,
                    onValueChange = { coordinateInput = it },
                    placeholder = { Text("e.g. 2:255") },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().testTag("go_to_verse_input")
                )
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("2:255", "18:10", "36:1", "55:1", "67:1").forEach { preset ->
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = EmeraldPrimary.copy(alpha = 0.15f),
                            modifier = Modifier.clickable { coordinateInput = preset }
                        ) {
                            Text(
                                text = preset,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldPrimary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (coordinateInput.isNotBlank()) {
                        onJump(coordinateInput)
                    }
                },
                modifier = Modifier.testTag("confirm_go_to_verse_button")
            ) {
                Text("Jump to Ayah")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun TajweedLegendSheet(
    isOpen: Boolean,
    onDismiss: () -> Unit
) {
    if (!isOpen) return

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Color-Coded Tajweed Guide", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(
                    Triple(Color(0xFFE53935), "Ghunnah (Nasalization)", "2 Harakah nasal sound on Nun and Meem Mushaddadah"),
                    Triple(Color(0xFF1E88E5), "Qalqalah (Echoing)", "Vibrating bounce on Qaf, Ta, Ba, Jeem, Dal when Sakin"),
                    Triple(Color(0xFF43A047), "Idgham (Merging)", "Merging noon sakin or tanween into Ya, Ra, Meem, Lam, Waw, Nun"),
                    Triple(Color(0xFFFB8C00), "Ikhfa (Concealment)", "Light nasal concealment before 15 designated Arabic letters"),
                    Triple(Color(0xFF8E24AA), "Madd (Elongation)", "Extending elongation 4 to 6 Harakat for Madd Wajib / Lazim")
                ).forEach { (color, title, desc) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = color,
                            modifier = Modifier.size(16.dp)
                        ) {}
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text(title, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = color)
                            Text(desc, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Got It")
            }
        }
    )
}

@Composable
fun AyahImageCardGeneratorDialog(
    isOpen: Boolean,
    ayah: AyahEntity?,
    onDismiss: () -> Unit
) {
    if (!isOpen || ayah == null) return

    var selectedGradientIndex by remember { mutableIntStateOf(0) }
    val gradients = listOf(
        listOf(Color(0xFF064E3B), Color(0xFF047857)),
        listOf(Color(0xFF1E1B4B), Color(0xFF312E81)),
        listOf(Color(0xFF78350F), Color(0xFFD97706)),
        listOf(Color(0xFF0F172A), Color(0xFF334155))
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .wrapContentHeight()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Ayah Quote Card Generator",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Ready to share on social media stories",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(16.dp))

                // The Preview Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Brush.verticalGradient(gradients[selectedGradientIndex]))
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Surah ${ayah.surahNumber} • Ayah ${ayah.ayahNumber}",
                            color = GoldAccent,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = ayah.arabicText,
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, lineHeight = 28.sp),
                            textAlign = TextAlign.Center,
                            maxLines = 4
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "\"${ayah.translation}\"",
                            color = Color.White.copy(alpha = 0.9f),
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            maxLines = 3
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "Deen365 Spiritual Sanctuary",
                            color = GoldAccent.copy(alpha = 0.8f),
                            fontSize = 9.sp
                        )
                    }
                }

                Spacer(Modifier.height(14.dp))

                // Gradient Swatches
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    gradients.forEachIndexed { idx, grad ->
                        Surface(
                            shape = CircleShape,
                            color = grad[0],
                            border = if (selectedGradientIndex == idx) androidx.compose.foundation.BorderStroke(2.dp, GoldAccent) else null,
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .clickable { selectedGradientIndex = idx }
                        ) {}
                    }
                }

                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Share, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Done")
                }
            }
        }
    }
}
