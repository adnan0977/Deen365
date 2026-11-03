package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.AsrJuristicMethod
import com.example.domain.CalculationMethod
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent

@Composable
fun CalculationSettingsDialog(
    currentMethod: CalculationMethod,
    currentAsrMethod: AsrJuristicMethod,
    onSelectMethod: (CalculationMethod) -> Unit,
    onSelectAsrMethod: (AsrJuristicMethod) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Calculation Method, 1: Asr Juristic Method

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = "Prayer Calculation Settings",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Astronomical formulas and Asr school derivations",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    contentColor = EmeraldPrimary
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Authority", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Asr School", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                    )
                }

                if (selectedTab == 0) {
                    LazyColumn(
                        modifier = Modifier.heightIn(max = 350.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(CalculationMethod.values().toList()) { method ->
                            val isSelected = method == currentMethod
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) EmeraldPrimary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                                border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary) else null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onSelectMethod(method) }
                                    .testTag("calc_method_${method.name.lowercase()}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = method.title,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                color = if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.onSurface
                                            )
                                        )
                                        Text(
                                            text = "Fajr Angle: ${method.fajrAngle}° • Isha Angle: ${method.ishaAngle}°",
                                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    if (isSelected) {
                                        Icon(Icons.Default.Check, contentDescription = "Selected", tint = EmeraldPrimary)
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        AsrJuristicMethod.values().forEach { asr ->
                            val isSelected = asr == currentAsrMethod
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) GoldAccent.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                                border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, GoldAccent) else null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onSelectAsrMethod(asr) }
                                    .testTag("asr_method_${asr.name.lowercase()}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = asr.displayName,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                color = if (isSelected) GoldAccent else MaterialTheme.colorScheme.onSurface
                                            )
                                        )
                                        Text(
                                            text = if (asr == AsrJuristicMethod.STANDARD) {
                                                "Shadow factor = 1 (Standard for Shafi'i, Maliki, Hanbali)"
                                            } else {
                                                "Shadow factor = 2 (Hanafi juristic derivation)"
                                            },
                                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    if (isSelected) {
                                        Icon(Icons.Default.Check, contentDescription = "Selected", tint = GoldAccent)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Done", fontWeight = FontWeight.Bold, color = EmeraldPrimary)
            }
        }
    )
}
