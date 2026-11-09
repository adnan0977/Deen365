package com.example.ui.screens.qada

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.QadaPrayerType
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.viewmodel.DeenUiState
import com.example.ui.viewmodel.DeenViewModel
import com.example.ui.viewmodel.MainTab

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QadaSalahScreen(
    state: DeenUiState,
    viewModel: DeenViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.selectTab(MainTab.DASHBOARD)
    }

    val qadaState = state.qadaSalahState
    val counts = qadaState.counts

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Qada Salah Tracker",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = EmeraldPrimary.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "قضاء الصلاة",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = FontFamily.Serif,
                                        fontWeight = FontWeight.Bold,
                                        color = EmeraldPrimary
                                    ),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Missed Prayer Calculator • Daily Make-up Log",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.selectTab(MainTab.DASHBOARD) },
                        modifier = Modifier.testTag("qada_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Home"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.setQadaSetupWizardOpen(true) },
                        modifier = Modifier.testTag("qada_calculator_wizard_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Calculate,
                            contentDescription = "Lifetime Deficit Calculator",
                            tint = EmeraldPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = modifier.testTag("qada_salah_screen")
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(vertical = 14.dp)
        ) {
            // Overall Progress Hero Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("qada_hero_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        EmeraldPrimary.copy(alpha = 0.10f),
                                        MaterialTheme.colorScheme.surface
                                    )
                                )
                            )
                            .padding(16.dp)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "OVERALL MAKE-UP PROGRESS",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 1.sp,
                                            color = EmeraldPrimary
                                        )
                                    )
                                    Text(
                                        text = "${counts.totalCompleted} Completed • ${counts.totalRemaining} Remaining",
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(20.dp),
                                    color = GoldAccent.copy(alpha = 0.15f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = "🔥", fontSize = 12.sp)
                                        Spacer(Modifier.width(4.dp))
                                        Text(
                                            text = "${qadaState.dailyStreak} Day Streak",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = GoldAccent
                                            )
                                        )
                                    }
                                }
                            }

                            Spacer(Modifier.height(10.dp))

                            Row(
                                verticalAlignment = Alignment.Bottom,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text(
                                        text = "${String.format(java.util.Locale.getDefault(), "%.1f", counts.completionPercentage)}%",
                                        style = MaterialTheme.typography.displaySmall.copy(
                                            fontWeight = FontWeight.ExtraBold,
                                            color = EmeraldPrimary
                                        )
                                    )
                                }

                                TextButton(
                                    onClick = { viewModel.setQadaSetupWizardOpen(true) },
                                    modifier = Modifier.testTag("recalc_deficit_button")
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("Recalculate Deficit", fontSize = 11.sp)
                                }
                            }

                            Spacer(Modifier.height(8.dp))

                            LinearProgressIndicator(
                                progress = { counts.completionPercentage / 100f },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(CircleShape),
                                color = EmeraldPrimary,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )

                            Spacer(Modifier.height(8.dp))

                            Text(
                                text = "💡 Tip: Pray 1 Qada prayer before or after each obligatory prayer to clear 1 year's backlog in 1 year.",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // 6 Prayers Grid / List
            item {
                Text(
                    text = "Daily Prayers to Make Up",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Render each of the 6 prayers
            val prayerTypes = listOf(
                Pair(QadaPrayerType.FAJR, Pair(counts.fajrRemaining, counts.fajrCompleted)),
                Pair(QadaPrayerType.DHUHR, Pair(counts.dhuhrRemaining, counts.dhuhrCompleted)),
                Pair(QadaPrayerType.ASR, Pair(counts.asrRemaining, counts.asrCompleted)),
                Pair(QadaPrayerType.MAGHRIB, Pair(counts.maghribRemaining, counts.maghribCompleted)),
                Pair(QadaPrayerType.ISHA, Pair(counts.ishaRemaining, counts.ishaCompleted)),
                Pair(QadaPrayerType.WITR, Pair(counts.witrRemaining, counts.witrCompleted))
            )

            items(prayerTypes) { (type, countPair) ->
                val (remaining, completed) = countPair
                QadaPrayerCard(
                    prayerType = type,
                    remaining = remaining,
                    completed = completed,
                    onAddOne = { viewModel.markQadaPrayerCompleted(type, 1) },
                    onAddFive = { viewModel.markQadaPrayerCompleted(type, 5) },
                    onUndoOne = { viewModel.undoQadaPrayerCompleted(type, 1) }
                )
            }

            // Fiqh Proofs & Guidance Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Prophetic Evidence & Fiqh of Qada",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = "\"Whoever forgets a prayer or sleeps through it, let him pray it when he remembers it; there is no expiation for it except that.\" (Sahih al-Bukhari #597 • Sahih Muslim #684)",
                            style = MaterialTheme.typography.bodySmall.copy(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic, fontSize = 11.5.sp),
                            color = EmeraldPrimary
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = "• The 4 Sunni Madhabs (Hanafi, Maliki, Shafi'i, Hanbali) agree that missed prayers remain an outstanding debt to Allah and must be made up as soon as reasonably possible without neglecting basic worldly responsibilities.\n" +
                                    "• In the Hanafi school, Witr is Wajib and is therefore made up alongside the 5 Fard prayers.",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, lineHeight = 16.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            item {
                Spacer(Modifier.height(24.dp))
            }
        }
    }

    // Lifetime Deficit Calculator Dialog
    if (qadaState.isSetupWizardOpen) {
        QadaCalculatorWizardDialog(
            currentInput = qadaState.calculationInput,
            onConfirm = { daysMissed ->
                viewModel.setQadaLifetimeDeficit(daysMissed)
            },
            onDismiss = { viewModel.setQadaSetupWizardOpen(false) }
        )
    }
}

@Composable
private fun QadaPrayerCard(
    prayerType: QadaPrayerType,
    remaining: Int,
    completed: Int,
    onAddOne: () -> Unit,
    onAddFive: () -> Unit,
    onUndoOne: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("qada_card_${prayerType.name.lowercase()}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = prayerType.iconEmoji, fontSize = 22.sp)
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = prayerType.title,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "(${prayerType.rakahs} Rakat)",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = "${completed} Made Up",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp, color = EmeraldPrimary)
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = remaining.toString(),
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = if (remaining == 0) EmeraldPrimary else MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier.testTag("remaining_${prayerType.name.lowercase()}")
                    )
                    Text(
                        text = if (remaining == 0) "Complete! ✓" else "Remaining",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (remaining == 0) EmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Undo -1
                OutlinedButton(
                    onClick = onUndoOne,
                    modifier = Modifier.weight(1f).testTag("undo_${prayerType.name.lowercase()}"),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                ) {
                    Text("-1 Undo", fontSize = 11.sp)
                }

                // Quick +5 Batch
                Button(
                    onClick = onAddFive,
                    modifier = Modifier.weight(1f).testTag("plus5_${prayerType.name.lowercase()}"),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary.copy(alpha = 0.15f)),
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                ) {
                    Text("+5 Fast", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EmeraldPrimary)
                }

                // Add +1
                Button(
                    onClick = onAddOne,
                    modifier = Modifier.weight(1.5f).testTag("plus1_${prayerType.name.lowercase()}"),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("+1 Done", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun QadaCalculatorWizardDialog(
    currentInput: com.example.data.model.QadaCalculationInput,
    onConfirm: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var yearsMissedText by remember { mutableStateOf(currentInput.yearsMissed.toString()) }
    var pubertyAgeText by remember { mutableStateOf(currentInput.pubertyAge.toString()) }
    var currentAgeText by remember { mutableStateOf(currentInput.currentAge.toString()) }

    val calculatedDays = remember(yearsMissedText) {
        val years = yearsMissedText.toDoubleOrNull() ?: 1.0
        (years * 365.25).toInt()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Calculate, contentDescription = null, tint = EmeraldPrimary)
                Spacer(Modifier.width(8.dp))
                Text("Calculate Missed Prayers", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Estimate how many years of prayers you need to make up. In Islamic jurisprudence, prayers become obligatory upon reaching puberty (around age 14-15).",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = yearsMissedText,
                    onValueChange = { yearsMissedText = it },
                    label = { Text("Approximate Years Missed") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("qada_years_input")
                )

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = EmeraldPrimary.copy(alpha = 0.1f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "Estimated Deficit: $calculatedDays Days",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = EmeraldPrimary)
                        )
                        Text(
                            text = "Sets each prayer (Fajr, Dhuhr, Asr, Maghrib, Isha, Witr) to $calculatedDays remaining.",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(calculatedDays) },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                modifier = Modifier.testTag("qada_confirm_deficit_button")
            ) {
                Text("Set Deficit")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
