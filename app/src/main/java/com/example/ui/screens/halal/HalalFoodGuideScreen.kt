package com.example.ui.screens.halal

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.data.model.*
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.viewmodel.DeenUiState
import com.example.ui.viewmodel.DeenViewModel
import com.example.ui.viewmodel.MainTab

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalalFoodGuideScreen(
    state: DeenUiState,
    viewModel: DeenViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.selectTab(MainTab.DASHBOARD)
    }

    val halalState = state.halalFoodState
    val filteredList = remember(halalState.searchQuery, halalState.selectedStatusFilter) {
        HalalFoodData.search(halalState.searchQuery, halalState.selectedStatusFilter)
    }

    var selectedItemForDialog by remember { mutableStateOf<ENumberItem?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                "Halal Food & E-Numbers",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Surface(shape = RoundedCornerShape(6.dp), color = EmeraldPrimary.copy(alpha = 0.15f)) {
                                Text(
                                    text = "350+ Codes",
                                    color = EmeraldPrimary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Additives • Ingredients • Permissibility Verifying",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.selectTab(MainTab.DASHBOARD) },
                        modifier = Modifier.testTag("halal_back_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Search Bar
            OutlinedTextField(
                value = halalState.searchQuery,
                onValueChange = { viewModel.setHalalSearchQuery(it) },
                placeholder = { Text("Search E-number (e.g. E120) or ingredient (Gelatin)") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = EmeraldPrimary) },
                trailingIcon = {
                    if (halalState.searchQuery.isNotBlank()) {
                        IconButton(onClick = { viewModel.setHalalSearchQuery("") }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth().testTag("halal_search_field")
            )

            // Status Filter Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                item {
                    FilterChip(
                        selected = halalState.selectedStatusFilter == null,
                        onClick = { viewModel.setHalalStatusFilter(null) },
                        label = { Text("All (${HalalFoodData.ALL_E_NUMBERS.size})") },
                        modifier = Modifier.testTag("halal_filter_all")
                    )
                }
                item {
                    FilterChip(
                        selected = halalState.selectedStatusFilter == HalalStatus.HALAL,
                        onClick = { viewModel.setHalalStatusFilter(HalalStatus.HALAL) },
                        label = { Text("Halal") },
                        leadingIcon = {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF16A34A)))
                        },
                        modifier = Modifier.testTag("halal_filter_halal")
                    )
                }
                item {
                    FilterChip(
                        selected = halalState.selectedStatusFilter == HalalStatus.HARAM,
                        onClick = { viewModel.setHalalStatusFilter(HalalStatus.HARAM) },
                        label = { Text("Haram") },
                        leadingIcon = {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFFDC2626)))
                        },
                        modifier = Modifier.testTag("halal_filter_haram")
                    )
                }
                item {
                    FilterChip(
                        selected = halalState.selectedStatusFilter == HalalStatus.MUSHBOOH,
                        onClick = { viewModel.setHalalStatusFilter(HalalStatus.MUSHBOOH) },
                        label = { Text("Mushbooh") },
                        leadingIcon = {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFFD97706)))
                        },
                        modifier = Modifier.testTag("halal_filter_mushbooh")
                    )
                }
            }

            // Results List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (filteredList.isEmpty()) {
                    item {
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth().padding(top = 20.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text("🔍", fontSize = 28.sp)
                                Text(
                                    text = "No E-numbers matching '${halalState.searchQuery}'",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "Try searching by number (e.g., E471) or name (e.g., Carmine, Lecithin).",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                } else {
                    items(filteredList) { item ->
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
                            modifier = Modifier.fillMaxWidth().clickable { selectedItemForDialog = item }.testTag("e_number_${item.code}")
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = getStatusColor(item.status).copy(alpha = 0.15f),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, getStatusColor(item.status).copy(alpha = 0.4f)),
                                        modifier = Modifier.size(46.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = item.code,
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontWeight = FontWeight.ExtraBold,
                                                    fontSize = 11.sp,
                                                    color = getStatusColor(item.status)
                                                )
                                            )
                                        }
                                    }

                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Text(
                                                text = item.name,
                                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                            )
                                        }
                                        Text(
                                            text = "${item.category} • Origin: ${item.origin.name.lowercase().replaceFirstChar { it.uppercase() }}",
                                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = getStatusColor(item.status)
                                ) {
                                    Text(
                                        text = item.status.name,
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Detail Dialog
    selectedItemForDialog?.let { item ->
        AlertDialog(
            onDismissRequest = { selectedItemForDialog = null },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("${item.code}: ${item.name}", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    Surface(shape = RoundedCornerShape(6.dp), color = getStatusColor(item.status)) {
                        Text(item.status.name, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(item.description, style = MaterialTheme.typography.bodyMedium)
                    Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

                    Text("Islamic Fiqh Ruling:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                    Text(item.fiqhVerdict, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)

                    Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                    Text("Commonly Found In:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                    Text(item.commonFoods, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedItemForDialog = null }) {
                    Text("Close")
                }
            }
        )
    }
}

private fun getStatusColor(status: HalalStatus): Color {
    return when (status) {
        HalalStatus.HALAL -> Color(0xFF16A34A)
        HalalStatus.HARAM -> Color(0xFFDC2626)
        HalalStatus.MUSHBOOH -> Color(0xFFD97706)
    }
}
