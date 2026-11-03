package com.example.ui.screens.namaz

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.screens.PrayerRulesAndConsiderationsView
import com.example.ui.screens.RakatScheduleAndTimingsView

@Composable
fun NamazRakatRulesView(
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("namaz_rakat_rules_column"),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            RakatScheduleAndTimingsView()
        }
        item {
            PrayerRulesAndConsiderationsView()
        }
    }
}
