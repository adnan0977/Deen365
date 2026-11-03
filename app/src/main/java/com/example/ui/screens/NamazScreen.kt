package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.namaz.CalculationMethodDialog
import com.example.ui.screens.namaz.NamazMadhhabView
import com.example.ui.screens.namaz.NamazRakatRulesView
import com.example.ui.screens.namaz.NamazTimetableView
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.viewmodel.DeenUiState
import com.example.ui.viewmodel.DeenViewModel

// Re-export for backward compatibility
typealias MadhhabType = com.example.ui.screens.namaz.MadhhabType
typealias MadhhabRuling = com.example.ui.screens.namaz.MadhhabRuling

@Composable
fun NamazScreen(
    state: DeenUiState,
    viewModel: DeenViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTopTab by remember { mutableIntStateOf(0) }
    var showCityDialog by remember { mutableStateOf(false) }
    var showMethodDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Main Navigation Tabs for Namaz Page
        ScrollableTabRow(
            selectedTabIndex = selectedTopTab,
            edgePadding = 12.dp,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = EmeraldPrimary,
            modifier = Modifier.fillMaxWidth().testTag("namaz_top_tabs")
        ) {
            Tab(
                selected = selectedTopTab == 0,
                onClick = { selectedTopTab = 0 },
                text = { Text("Timetable", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(16.dp)) },
                modifier = Modifier.testTag("tab_namaz_timetable")
            )
            Tab(
                selected = selectedTopTab == 1,
                onClick = { selectedTopTab = 1 },
                text = { Text("Learn Salah & Azan", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.School, contentDescription = null, modifier = Modifier.size(16.dp)) },
                modifier = Modifier.testTag("tab_learn_salah_azan")
            )
            Tab(
                selected = selectedTopTab == 2,
                onClick = { selectedTopTab = 2 },
                text = { Text("Salah Walk", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.Accessibility, contentDescription = null, modifier = Modifier.size(16.dp)) },
                modifier = Modifier.testTag("tab_salah_walk")
            )
            Tab(
                selected = selectedTopTab == 3,
                onClick = { selectedTopTab = 3 },
                text = { Text("Schools", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(16.dp)) },
                modifier = Modifier.testTag("tab_schools_fiqh")
            )
            Tab(
                selected = selectedTopTab == 4,
                onClick = { selectedTopTab = 4 },
                text = { Text("Rakat", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp)) },
                modifier = Modifier.testTag("tab_rakat_rules")
            )
            Tab(
                selected = selectedTopTab == 5,
                onClick = { selectedTopTab = 5 },
                text = { Text("History & Sync", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.CloudSync, contentDescription = null, modifier = Modifier.size(16.dp)) },
                modifier = Modifier.testTag("tab_prayer_history_sync")
            )
        }

        when (selectedTopTab) {
            0 -> {
                NamazTimetableView(
                    state = state,
                    viewModel = viewModel,
                    onOpenCityPicker = { showCityDialog = true },
                    onOpenMethodPicker = { showMethodDialog = true }
                )
            }
            1 -> {
                com.example.ui.components.EachNamazLearningView(state = state, viewModel = viewModel)
            }
            2 -> {
                NamazGuideView(state = state, viewModel = viewModel)
            }
            3 -> {
                NamazMadhhabView()
            }
            4 -> {
                NamazRakatRulesView()
            }
            5 -> {
                com.example.ui.components.PrayerHistoryView(
                    state = state,
                    viewModel = viewModel
                )
            }
        }
    }

    if (showCityDialog) {
        CitySelectionDialog(
            currentCity = state.selectedCity,
            onDismiss = { showCityDialog = false },
            onSelect = {
                viewModel.selectCity(it)
                showCityDialog = false
            }
        )
    }

    if (showMethodDialog) {
        CalculationMethodDialog(
            currentMethod = state.selectedCalculationMethod,
            onSelect = {
                viewModel.selectCalculationMethod(it)
                showMethodDialog = false
            },
            onDismiss = { showMethodDialog = false }
        )
    }

    if (state.isMosqueTimingsDialogOpen) {
        com.example.ui.screens.namaz.MosqueTimingsDialog(
            isOpen = true,
            mosqueName = state.mosqueName,
            isGlobalMosqueActive = state.isGlobalMosqueActive,
            timings = state.mosqueTimings,
            onDismiss = { viewModel.openMosqueTimingsDialog(false) },
            onSaveMosqueName = { viewModel.setMosqueName(it) },
            onToggleGlobalMode = { viewModel.toggleGlobalMosqueTiming(it) },
            onUpdateTiming = { id, time, active, preAlarm, soundMode ->
                viewModel.updateMosqueTiming(id, time, active, preAlarm, soundMode)
            },
            onResetAll = { viewModel.resetMosqueTimingsToDefault() },
            onTestAlarm = { viewModel.triggerTestMosqueAlarm(it) }
        )
    }
}
