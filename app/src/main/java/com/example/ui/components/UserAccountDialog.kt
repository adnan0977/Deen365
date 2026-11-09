package com.example.ui.components

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.components.profile.UserProfileAndGoalsView
import com.example.ui.viewmodel.DeenUiState
import com.example.ui.viewmodel.DeenViewModel

/**
 * User Profile & Authentication Dialog hosting the full UserProfileAndGoalsView:
 * 1. Visual Progress Overview across Quran, Hadith, Dua, Namaz, and Fasting.
 * 2. Spiritual Goals Customization (Daily/Weekly targets).
 * 3. Achievements & Badges (10 unlockable milestones).
 * 4. Account Authentication (Email registration, Login, Password Reset, Guest sync).
 */
@Composable
fun UserAccountDialog(
    state: DeenUiState,
    viewModel: DeenViewModel,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f)
                .clip(RoundedCornerShape(24.dp)),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            UserProfileAndGoalsView(
                state = state,
                viewModel = viewModel,
                onDismiss = onDismiss
            )
        }
    }
}
