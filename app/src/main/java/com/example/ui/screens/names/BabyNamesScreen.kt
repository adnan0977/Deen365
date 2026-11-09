package com.example.ui.screens.names

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
import androidx.compose.ui.text.style.TextAlign
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
fun BabyNamesScreen(
    state: DeenUiState,
    viewModel: DeenViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.selectTab(MainTab.DASHBOARD)
    }

    val babyState = state.babyNamesState
    val filteredNames = remember(babyState.searchQuery, babyState.selectedGender, babyState.selectedLetter) {
        BabyNamesData.filter(babyState.searchQuery, babyState.selectedGender, babyState.selectedLetter)
    }

    var showSunnahEtiquettesDialog by remember { mutableStateOf(false) }

    val alphabetList = remember {
        listOf("All") + ('A'..'Z').map { it.toString() }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                "Islamic Baby Names",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Surface(shape = RoundedCornerShape(6.dp), color = GoldAccent.copy(alpha = 0.2f)) {
                                Text(
                                    text = "Dictionary",
                                    color = GoldAccent,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Arabic Origins • Prophetic Meanings • Sunnah Etiquettes",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.selectTab(MainTab.DASHBOARD) },
                        modifier = Modifier.testTag("baby_names_back_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showSunnahEtiquettesDialog = true },
                        modifier = Modifier.testTag("baby_names_sunnah_button")
                    ) {
                        Icon(Icons.Default.Info, contentDescription = "Sunnah of Naming", tint = EmeraldPrimary)
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
                value = babyState.searchQuery,
                onValueChange = { viewModel.setBabyNamesSearchQuery(it) },
                placeholder = { Text("Search name (Muhammad, Maryam) or meaning") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = EmeraldPrimary) },
                trailingIcon = {
                    if (babyState.searchQuery.isNotBlank()) {
                        IconButton(onClick = { viewModel.setBabyNamesSearchQuery("") }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth().testTag("baby_names_search_field")
            )

            // Gender Filters
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = babyState.selectedGender == BabyGender.ALL,
                    onClick = { viewModel.setBabyNamesGender(BabyGender.ALL) },
                    label = { Text("All") },
                    modifier = Modifier.weight(1f).testTag("baby_gender_all")
                )
                FilterChip(
                    selected = babyState.selectedGender == BabyGender.BOY,
                    onClick = { viewModel.setBabyNamesGender(BabyGender.BOY) },
                    label = { Text("Boys") },
                    leadingIcon = { Icon(Icons.Default.Male, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    modifier = Modifier.weight(1f).testTag("baby_gender_boys")
                )
                FilterChip(
                    selected = babyState.selectedGender == BabyGender.GIRL,
                    onClick = { viewModel.setBabyNamesGender(BabyGender.GIRL) },
                    label = { Text("Girls") },
                    leadingIcon = { Icon(Icons.Default.Female, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    modifier = Modifier.weight(1f).testTag("baby_gender_girls")
                )
            }

            // Alphabet Horizontal Bar
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                contentPadding = PaddingValues(vertical = 2.dp)
            ) {
                items(alphabetList) { letter ->
                    val isSelected = babyState.selectedLetter == letter
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { viewModel.setBabyNamesLetter(letter) }
                    ) {
                        Text(
                            text = letter,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            // Names List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (filteredNames.isEmpty()) {
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
                                Text("👶", fontSize = 28.sp)
                                Text(
                                    text = "No names matching criteria",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "Try clearing the letter filter or searching for another keyword.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                } else {
                    items(filteredNames) { nameItem ->
                        val isFav = babyState.favoriteIds.contains(nameItem.id)

                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
                            modifier = Modifier.fillMaxWidth().testTag("baby_name_${nameItem.id}")
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Text(
                                            text = nameItem.name,
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                        )
                                        if (nameItem.isQuranic) {
                                            Surface(shape = RoundedCornerShape(4.dp), color = GoldAccent.copy(alpha = 0.2f)) {
                                                Text(
                                                    text = "Quranic",
                                                    color = GoldAccent,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = if (nameItem.gender == BabyGender.BOY) Color(0xFF2563EB).copy(alpha = 0.15f)
                                            else Color(0xFFDB2777).copy(alpha = 0.15f)
                                        ) {
                                            Text(
                                                text = if (nameItem.gender == BabyGender.BOY) "Boy" else "Girl",
                                                color = if (nameItem.gender == BabyGender.BOY) Color(0xFF2563EB) else Color(0xFFDB2777),
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text(
                                            text = nameItem.arabic,
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = EmeraldPrimary),
                                            textAlign = TextAlign.End
                                        )
                                        IconButton(
                                            onClick = { viewModel.toggleBabyNameFavorite(nameItem.id) },
                                            modifier = Modifier.size(28.dp).testTag("fav_name_${nameItem.id}")
                                        ) {
                                            Icon(
                                                imageVector = if (isFav) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                                contentDescription = "Favorite",
                                                tint = if (isFav) Color(0xFFE11D48) else MaterialTheme.colorScheme.outlineVariant,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }

                                Text(
                                    text = "Meaning: ${nameItem.meaning}",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Text(
                                    text = nameItem.significance,
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Sunnah Etiquettes Dialog
    if (showSunnahEtiquettesDialog) {
        AlertDialog(
            onDismissRequest = { showSunnahEtiquettesDialog = false },
            title = {
                Text(
                    "Prophetic Sunnah of Naming Children",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = EmeraldPrimary)
                )
            },
            text = {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(BabyNamesData.SUNNAH_ETIQUETTES) { etiquette ->
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("•", color = EmeraldPrimary, fontWeight = FontWeight.Bold)
                            Text(etiquette, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSunnahEtiquettesDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}
