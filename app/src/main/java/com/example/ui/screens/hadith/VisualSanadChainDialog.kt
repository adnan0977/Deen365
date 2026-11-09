package com.example.ui.screens.hadith

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.SanadChain
import com.example.ui.components.hadith.SanadNarratorChainView

/**
 * Fullscreen scholarly dialog displaying the interactive Sanad Narrator Chain
 * adhering to Sunnah.com taxonomy, transmission links, and Jarh wa Ta'dil grades.
 */
@Composable
fun VisualSanadChainDialog(
    chain: SanadChain?,
    onDismiss: () -> Unit
) {
    if (chain == null) return

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .fillMaxHeight(0.92f)
                .testTag("visual_sanad_dialog")
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                SanadNarratorChainView(
                    chain = chain,
                    onClose = onDismiss,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
