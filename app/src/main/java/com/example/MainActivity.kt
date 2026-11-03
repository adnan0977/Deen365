package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.notification.PrayerNotificationHelper
import com.example.ui.screens.DeenMainScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.DeenViewModel
import com.example.ui.viewmodel.MainTab

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Ensure notification channels are set up on Android 8.0+
        PrayerNotificationHelper.createNotificationChannel(this)

        setContent {
            val viewModel: DeenViewModel = viewModel()
            val state by viewModel.uiState.collectAsStateWithLifecycle()
            val systemInDark = androidx.compose.foundation.isSystemInDarkTheme()

            // Keep system dark mode synced when in SYSTEM mode
            LaunchedEffect(systemInDark) {
                viewModel.onSystemDarkModeChanged(systemInDark)
            }

            // Handle navigation from prayer or fasting notification tap
            LaunchedEffect(intent) {
                val openTab = intent?.getStringExtra("OPEN_TAB")
                if (openTab == "NAMAZ" || openTab == "FASTING") {
                    viewModel.setTab(MainTab.NAMAZ)
                }
            }

            val effectiveDark = when (state.darkModeConfig) {
                com.example.domain.DarkModeConfig.SYSTEM -> systemInDark
                com.example.domain.DarkModeConfig.LIGHT -> false
                com.example.domain.DarkModeConfig.DARK -> true
            }

            MyApplicationTheme(
                darkModeConfig = state.darkModeConfig,
                darkTheme = effectiveDark,
                themeType = state.selectedThemeType
            ) {
                DeenMainScreen(viewModel = viewModel)
            }
        }
    }
}
