package com.example.ui.util

import android.app.UiModeManager
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.GoldAccent

enum class WindowWidthSizeClass {
    COMPACT,  // < 600.dp (standard portrait phones)
    MEDIUM,   // 600..839.dp (portrait tablets, foldables unfolded, small landscape)
    EXPANDED  // >= 840.dp (tablets in landscape, desktop, Google TV / Android TV)
}

data class DeviceScreenProfile(
    val widthSizeClass: WindowWidthSizeClass,
    val isLandscape: Boolean,
    val isTablet: Boolean,
    val isTv: Boolean,
    val screenWidthDp: Dp,
    val screenHeightDp: Dp
) {
    /**
     * Whether to use a side NavigationRail instead of a bottom NavigationBar.
     * True on TV, landscape tablets, or whenever screen width >= 600dp.
     */
    val useNavigationRail: Boolean
        get() = isTv || widthSizeClass != WindowWidthSizeClass.COMPACT

    /**
     * Recommended column count for multi-pane dashboards and grids.
     */
    val columnCount: Int
        get() = when {
            isTv || (widthSizeClass == WindowWidthSizeClass.EXPANDED && isLandscape) -> 3
            widthSizeClass == WindowWidthSizeClass.EXPANDED || (widthSizeClass == WindowWidthSizeClass.MEDIUM && isLandscape) -> 2
            else -> 1
        }

    /**
     * Max width constraint for centered cards on very wide screens (TV / 4K / wide monitors).
     */
    val maxContentWidth: Dp
        get() = if (isTv) 1280.dp else 1100.dp
}

@Composable
fun rememberDeviceScreenProfile(): DeviceScreenProfile {
    val configuration = LocalConfiguration.current
    val context = LocalContext.current

    val widthDp = configuration.screenWidthDp.dp
    val heightDp = configuration.screenHeightDp.dp
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val uiModeManager = context.getSystemService(Context.UI_MODE_SERVICE) as? UiModeManager
    val isTvMode = uiModeManager?.currentModeType == Configuration.UI_MODE_TYPE_TELEVISION
    val hasTvFeature = context.packageManager.hasSystemFeature(PackageManager.FEATURE_LEANBACK) ||
            context.packageManager.hasSystemFeature("android.hardware.type.television")
    val isTv = isTvMode || hasTvFeature

    val widthSizeClass = when {
        widthDp < 600.dp -> WindowWidthSizeClass.COMPACT
        widthDp < 840.dp -> WindowWidthSizeClass.MEDIUM
        else -> WindowWidthSizeClass.EXPANDED
    }

    val isTablet = (widthSizeClass != WindowWidthSizeClass.COMPACT && !isTv)

    return remember(widthDp, heightDp, isLandscape, isTv) {
        DeviceScreenProfile(
            widthSizeClass = widthSizeClass,
            isLandscape = isLandscape,
            isTablet = isTablet,
            isTv = isTv,
            screenWidthDp = widthDp,
            screenHeightDp = heightDp
        )
    }
}

/**
 * Modifier to provide Google TV remote D-Pad focus feedback, with subtle zoom and gold focus outline.
 */
@Composable
fun Modifier.adaptiveFocusable(
    shape: Shape = RoundedCornerShape(16.dp),
    focusedBorderColor: Color = GoldAccent,
    focusedBorderWidth: Dp = 2.5.dp,
    scaleWhenFocused: Float = 1.02f
): Modifier {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val animatedScale by animateFloatAsState(
        targetValue = if (isFocused) scaleWhenFocused else 1f,
        animationSpec = tween(durationMillis = 150),
        label = "tv_focus_scale"
    )

    return this
        .scale(animatedScale)
        .focusable(interactionSource = interactionSource)
        .then(
            if (isFocused) {
                Modifier.border(width = focusedBorderWidth, color = focusedBorderColor, shape = shape)
            } else {
                Modifier
            }
        )
}
