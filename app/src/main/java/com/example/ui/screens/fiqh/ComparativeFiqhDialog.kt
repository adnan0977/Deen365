package com.example.ui.screens.fiqh

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.components.fiqh.ComparativeFiqhMatrixComponent

/**
 * Fullscreen dialog hosting the interactive Comparative Fiqh Matrix component,
 * enabling users to toggle between the 4 Sunni Madhabs across jurisprudential topics.
 */
@Composable
fun ComparativeFiqhDialog(
    initialTopicId: String? = null,
    onDismiss: () -> Unit
) {
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
                .testTag("comparative_fiqh_dialog")
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                ComparativeFiqhMatrixComponent(
                    initialTopicId = initialTopicId,
                    onClose = onDismiss,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
