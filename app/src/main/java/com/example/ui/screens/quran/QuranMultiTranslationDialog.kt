package com.example.ui.screens.quran

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.TranslationEdition
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent

@Composable
fun QuranMultiTranslationDialog(
    isOpen: Boolean,
    primaryEdition: TranslationEdition,
    comparisonEditionIds: Set<String>,
    onSelectPrimary: (TranslationEdition) -> Unit,
    onToggleComparisonEdition: (String) -> Unit,
    onDismiss: () -> Unit
) {
    if (!isOpen) return

    var activeTab by remember { mutableStateOf(0) } // 0: Primary Edition, 1: Compare Stack

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 620.dp)
                .padding(12.dp)
                .testTag("multi_translation_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
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
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Translate,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Column {
                            Text(
                                text = "Quran Translations",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Compare classical & contemporary meanings",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                // Tab Selector
                TabRow(
                    selectedTabIndex = activeTab,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                    modifier = Modifier.clip(RoundedCornerShape(12.dp)),
                    indicator = {},
                    divider = {}
                ) {
                    Tab(
                        selected = activeTab == 0,
                        onClick = { activeTab = 0 },
                        text = {
                            Text(
                                "Primary Edition",
                                fontWeight = if (activeTab == 0) FontWeight.Bold else FontWeight.Normal,
                                color = if (activeTab == 0) EmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        modifier = Modifier.testTag("tab_primary_translation")
                    )
                    Tab(
                        selected = activeTab == 1,
                        onClick = { activeTab = 1 },
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Default.CompareArrows, contentDescription = null, modifier = Modifier.size(15.dp))
                                Text(
                                    "Stacked Comparison (${comparisonEditionIds.size})",
                                    fontWeight = if (activeTab == 1) FontWeight.Bold else FontWeight.Normal,
                                    color = if (activeTab == 1) EmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        },
                        modifier = Modifier.testTag("tab_compare_translations")
                    )
                }

                if (activeTab == 0) {
                    // List of all editions to select Primary
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f, fill = false),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(TranslationEdition.ALL_EDITIONS) { edition ->
                            val isSelected = edition.id == primaryEdition.id
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) EmeraldPrimary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                                border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, EmeraldPrimary) else null,
                                onClick = { onSelectPrimary(edition) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("select_primary_edition_${edition.id}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Text(
                                                text = edition.name,
                                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                                color = if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.onSurface
                                            )
                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = GoldAccent.copy(alpha = 0.15f)
                                            ) {
                                                Text(
                                                    text = edition.language,
                                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.SemiBold),
                                                    color = GoldAccent,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                        Text(
                                            text = edition.author,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    if (isSelected) {
                                        Icon(Icons.Default.Check, contentDescription = "Active", tint = EmeraldPrimary)
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // Comparison Stack Multiselection
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f, fill = false),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Select translations to display simultaneously under each verse:",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        LazyColumn(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            items(TranslationEdition.ALL_EDITIONS) { edition ->
                                val isChecked = comparisonEditionIds.contains(edition.id)
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isChecked) EmeraldPrimary.copy(alpha = 0.09f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onToggleComparisonEdition(edition.id) }
                                        .testTag("checkbox_compare_edition_${edition.id}")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Checkbox(
                                            checked = isChecked,
                                            onCheckedChange = { onToggleComparisonEdition(edition.id) },
                                            colors = CheckboxDefaults.colors(checkedColor = EmeraldPrimary)
                                        )

                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                Text(
                                                    text = edition.name,
                                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                                Surface(
                                                    shape = RoundedCornerShape(4.dp),
                                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                                ) {
                                                    Text(
                                                        text = edition.language,
                                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                                        color = MaterialTheme.colorScheme.primary,
                                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                    )
                                                }
                                            }
                                            Text(
                                                text = edition.author,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Done Button
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("done_multi_translation_btn")
                ) {
                    Text("Done", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
