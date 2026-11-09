package com.example.ui.screens

import android.content.Context
import android.hardware.SensorManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.CityPreset
import com.example.domain.PrayerTimeCalculator
import com.example.domain.QiblaCalculator
import com.example.domain.QiblaTheme
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.EmeraldAccent
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldLight
import com.example.ui.viewmodel.DeenUiState
import com.example.ui.viewmodel.DeenViewModel
import kotlin.math.*

@Composable
fun QiblaScreen(
    state: DeenUiState,
    viewModel: DeenViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val vibrator = remember {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator ?: (context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator)
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }
        } catch (_: Throwable) {
            null
        }
    }

    DisposableEffect(Unit) {
        viewModel.startQiblaCompass()
        onDispose {
            viewModel.stopQiblaCompass()
        }
    }

    val sensorData = state.qiblaSensorData
    val targetQiblaAngle = state.qiblaDirection.bearingDegrees.toFloat()
    val currentHeading = if (sensorData.declination != 0f) sensorData.trueHeading else state.deviceHeading

    // Angle to rotate compass so Kaaba is at top when user faces Qibla
    val diffToQibla = remember(currentHeading, targetQiblaAngle) {
        var diff = targetQiblaAngle - currentHeading
        while (diff < -180f) diff += 360f
        while (diff > 180f) diff -= 360f
        diff
    }

    val isFacingQibla = abs(diffToQibla) <= 3.5f
    var showCalibrationDialog by remember { mutableStateOf(false) }
    var showCityPicker by remember { mutableStateOf(false) }

    // Haptic feedback pulse once aligned
    var hasVibratedForCurrentAlignment by remember { mutableStateOf(false) }
    LaunchedEffect(isFacingQibla) {
        if (isFacingQibla && !hasVibratedForCurrentAlignment) {
            hasVibratedForCurrentAlignment = true
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator?.vibrate(VibrationEffect.createOneShot(80, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(80)
                }
            } catch (_: Exception) {}
        } else if (!isFacingQibla) {
            hasVibratedForCurrentAlignment = false
        }
    }

    val qiblaTheme = if (state.isDarkMode && state.selectedQiblaTheme == QiblaTheme.EMERALD_GOLD) {
        QiblaTheme.CELESTIAL_MIDNIGHT
    } else {
        state.selectedQiblaTheme
    }

    val animatedRotation by animateFloatAsState(
        targetValue = -currentHeading,
        animationSpec = tween(durationMillis = 180),
        label = "compass_rotation"
    )

    val alignmentColor by animateColorAsState(
        targetValue = if (isFacingQibla) GoldAccent else EmeraldPrimary,
        animationSpec = tween(durationMillis = 250),
        label = "alignment_color"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Header with Title & City Selector
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("qibla_header_card")
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = EmeraldPrimary.copy(alpha = 0.15f),
                                modifier = Modifier.size(42.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Default.Explore,
                                        contentDescription = "Qibla",
                                        tint = EmeraldPrimary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                            Column {
                                Text(
                                    text = "Qibla Direction (Al-Qiblah)",
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Real-Time Sensor Compass • True North Vector",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Calibration Guide Shortcut
                        FilledTonalIconButton(
                            onClick = { showCalibrationDialog = true },
                            colors = IconButtonDefaults.filledTonalIconButtonColors(
                                containerColor = GoldAccent.copy(alpha = 0.15f)
                            ),
                            modifier = Modifier.testTag("qibla_calibration_guide_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.AllInclusive,
                                contentDescription = "Calibrate",
                                tint = GoldAccent,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // Theme Selector Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        QiblaTheme.entries.forEach { theme ->
                            val isSelected = theme == qiblaTheme
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) GoldAccent.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                                border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, GoldAccent) else null,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { viewModel.setQiblaTheme(theme) }
                                    .testTag("qibla_theme_btn_${theme.id}")
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = when (theme) {
                                            QiblaTheme.EMERALD_GOLD -> "Emerald"
                                            QiblaTheme.CELESTIAL_MIDNIGHT -> "Celestial"
                                            QiblaTheme.EARTH_SATELLITE -> "Satellite"
                                            QiblaTheme.MINIMALIST_OBSIDIAN -> "Obsidian"
                                        },
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            fontSize = 10.sp
                                        ),
                                        color = if (isSelected) GoldAccent else MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }

                    // City chip and Kaaba Bearing
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            onClick = { showCityPicker = true }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(16.dp))
                                Text(
                                    text = state.selectedCity.name,
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null, modifier = Modifier.size(16.dp))
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isFacingQibla) GoldAccent.copy(alpha = 0.2f) else EmeraldPrimary.copy(alpha = 0.12f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "Qibla: ${state.qiblaDirection.bearingDegrees.roundToInt()}° ${state.qiblaDirection.compassDirection}",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = if (isFacingQibla) GoldAccent else EmeraldPrimary
                                )
                            }
                        }
                    }
                }
            }
        }

        // 2. Alignment Status Banner
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = if (isFacingQibla) GoldAccent.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                border = if (isFacingQibla) androidx.compose.foundation.BorderStroke(2.dp, GoldAccent) else null,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("qibla_alignment_status")
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = if (isFacingQibla) Icons.Default.CheckCircle else if (diffToQibla > 0) Icons.Default.RotateRight else Icons.Default.RotateLeft,
                        contentDescription = null,
                        tint = if (isFacingQibla) GoldAccent else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isFacingQibla) "Facing the Holy Kaaba (Alhamdulillah)" else "Turn ${abs(diffToQibla).roundToInt()}° ${if (diffToQibla > 0) "to the Right" else "to the Left"}",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (isFacingQibla) GoldAccent else MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (isFacingQibla) "Your phone is aligned within 3° of the Holy Kaaba in Makkah." else "Heading: ${currentHeading.roundToInt()}° • Kaaba Azimuth: ${targetQiblaAngle.roundToInt()}°",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // 3. Main Live Compass Dial & Kaaba Canvas
        item {
            Box(
                modifier = Modifier
                    .size(310.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            colors = qiblaTheme.dialBackground
                        )
                    )
                    .border(
                        width = if (isFacingQibla) 3.5.dp else 2.dp,
                        brush = Brush.sweepGradient(
                            listOf(
                                alignmentColor,
                                qiblaTheme.ringColor,
                                alignmentColor
                            )
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Static Top Reference Pointer (Device Heading Top)
                Icon(
                    imageVector = Icons.Default.ArrowDropUp,
                    contentDescription = null,
                    tint = if (isFacingQibla) GoldAccent else Color.Red,
                    modifier = Modifier
                        .size(38.dp)
                        .align(Alignment.TopCenter)
                        .padding(top = 4.dp)
                )

                // Rotating Compass Dial (Smoothly driven by Android Sensor)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .rotate(animatedRotation),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val center = Offset(size.width / 2, size.height / 2)
                        val radius = size.width / 2 - 24.dp.toPx()

                        // Astrolabe / Satellite Orbit Rings
                        drawCircle(
                            color = qiblaTheme.ringColor.copy(alpha = 0.35f),
                            radius = radius,
                            center = center,
                            style = Stroke(width = 1.5.dp.toPx())
                        )
                        drawCircle(
                            color = qiblaTheme.ringColor.copy(alpha = 0.15f),
                            radius = radius * 0.75f,
                            center = center,
                            style = Stroke(width = 1.dp.toPx())
                        )

                        // 72 Degree Ticks (Every 5 degrees, longer every 30)
                        for (deg in 0 until 360 step 5) {
                            val angleRad = Math.toRadians(deg.toDouble() - 90.0)
                            val isMajor = deg % 30 == 0
                            val tickLen = if (isMajor) 14.dp.toPx() else 7.dp.toPx()
                            val startX = center.x + (radius - tickLen) * cos(angleRad).toFloat()
                            val startY = center.y + (radius - tickLen) * sin(angleRad).toFloat()
                            val endX = center.x + radius * cos(angleRad).toFloat()
                            val endY = center.y + radius * sin(angleRad).toFloat()

                            drawLine(
                                color = if (deg == 0) Color.Red else if (isMajor) GoldAccent.copy(alpha = 0.8f) else Color.White.copy(alpha = 0.35f),
                                start = Offset(startX, startY),
                                end = Offset(endX, endY),
                                strokeWidth = if (isMajor) 2.dp.toPx() else 1.dp.toPx(),
                                cap = StrokeCap.Round
                            )
                        }

                        // Target Qibla Radial Needle Pointer towards Kaaba
                        val qiblaRad = Math.toRadians(targetQiblaAngle.toDouble() - 90.0)
                        val qiblaX = center.x + (radius - 18.dp.toPx()) * cos(qiblaRad).toFloat()
                        val qiblaY = center.y + (radius - 18.dp.toPx()) * sin(qiblaRad).toFloat()

                        drawLine(
                            color = qiblaTheme.needlePointerColor,
                            start = center,
                            end = Offset(qiblaX, qiblaY),
                            strokeWidth = 3.5.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    }

                    // Cardinal Direction Labels
                    Text(
                        text = "N",
                        color = Color.Red,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp,
                        modifier = Modifier.align(Alignment.TopCenter).padding(top = 28.dp)
                    )
                    Text(
                        text = "S",
                        color = Color.White.copy(alpha = 0.8f),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 28.dp)
                    )
                    Text(
                        text = "E",
                        color = Color.White.copy(alpha = 0.8f),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.align(Alignment.CenterEnd).padding(end = 28.dp)
                    )
                    Text(
                        text = "W",
                        color = Color.White.copy(alpha = 0.8f),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.align(Alignment.CenterStart).padding(start = 28.dp)
                    )

                    // Target Holy Kaaba Icon on rotating rim
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .rotate(targetQiblaAngle),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = qiblaTheme.kaabaBeaconColor,
                            border = androidx.compose.foundation.BorderStroke(2.dp, Color.Black),
                            modifier = Modifier
                                .padding(top = 38.dp)
                                .size(34.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Mosque,
                                    contentDescription = "Holy Kaaba",
                                    tint = Color.Black,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }

                // Central Hub: Heading readout & status
                Surface(
                    shape = CircleShape,
                    color = if (isFacingQibla) GoldAccent.copy(alpha = 0.25f) else Color.Black.copy(alpha = 0.6f),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, alignmentColor),
                    modifier = Modifier.size(90.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "${currentHeading.roundToInt()}°",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = alignmentColor
                            )
                        )
                        Text(
                            text = if (isFacingQibla) "ALIGNED" else state.qiblaDirection.compassDirection,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                color = Color.White
                            )
                        )
                    }
                }
            }
        }

        // 4. Real-time Circular Spirit Bubble Level (Bullseye Level)
        item {
            QiblaBubbleLevelCard(
                pitch = sensorData.pitch,
                roll = sensorData.roll,
                isFlat = sensorData.isFlat
            )
        }

        // 5. Hardware Sensor Telemetry & Diagnostics Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("qibla_sensor_telemetry_card")
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Sensors, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(18.dp))
                            Text(
                                text = "Sensor Telemetry & Magnetometer",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        val (statusText, statusColor) = when (sensorData.accuracy) {
                            SensorManager.SENSOR_STATUS_ACCURACY_HIGH -> "High Accuracy" to EmeraldPrimary
                            SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM -> "Medium" to GoldAccent
                            SensorManager.SENSOR_STATUS_ACCURACY_LOW -> "Low Accuracy" to Color(0xFFF97316)
                            else -> "Calibrate Required" to MaterialTheme.colorScheme.error
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = statusColor.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = statusText,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = statusColor),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    // Sensor Type and Field Strength Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("Hardware Sensor", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = sensorData.sensorTypeUsed,
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("Magnetic Field", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${sensorData.magneticFieldStrength.roundToInt()} µT (${if (sensorData.isMagneticInterference) "Distortion" else "Normal"})",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (sensorData.isMagneticInterference) MaterialTheme.colorScheme.error else EmeraldPrimary
                                )
                            }
                        }
                    }

                    // Magnetic Interference Alert if detected
                    if (sensorData.isMagneticInterference) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                                Text(
                                    text = "Magnetic distortion detected (${sensorData.magneticFieldStrength.roundToInt()} µT). Move away from laptops, metallic tables, or electrical chargers.",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                    }

                    // Magnetic Declination & True North info
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Magnetic Declination: ${if (sensorData.declination >= 0) "+" else ""}${String.format(java.util.Locale.US, "%.1f", sensorData.declination)}°",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "True North Vector Corrected",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = EmeraldPrimary
                        )
                    }

                    // Figure-8 Calibration Guide button
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showCalibrationDialog = true },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AllInclusive,
                            contentDescription = "Figure 8",
                            tint = GoldAccent,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "How to calibrate compass: Wave phone in a 3D figure-8 motion",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }

        // 6. Geographic Coordinates & Sacred Mecca Metrics
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Sacred Kaaba Geography & Great Circle Vector",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        MetricInfoBox(
                            title = "Mecca Distance",
                            value = "${state.qiblaDirection.distanceKm.roundToInt()} km",
                            subtitle = "${(state.qiblaDirection.distanceKm * 0.621371).roundToInt()} miles",
                            modifier = Modifier.weight(1f)
                        )
                        MetricInfoBox(
                            title = "Kaaba Bearing",
                            value = "${targetQiblaAngle.roundToInt()}°",
                            subtitle = state.qiblaDirection.compassDirection,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        MetricInfoBox(
                            title = "Your Location",
                            value = state.selectedCity.name,
                            subtitle = "Lat ${state.selectedCity.latitude.roundToInt()}°, Lon ${state.selectedCity.longitude.roundToInt()}°",
                            modifier = Modifier.weight(1f)
                        )
                        MetricInfoBox(
                            title = "Holy Sanctuary",
                            value = "Makkah al-Mukarramah",
                            subtitle = "21.4225° N, 39.8262° E",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }

    if (showCityPicker) {
        CitySelectionDialog(
            currentCity = state.selectedCity,
            onDismiss = { showCityPicker = false },
            onSelect = {
                viewModel.selectCity(it)
                showCityPicker = false
            }
        )
    }

    if (showCalibrationDialog) {
        QiblaCalibrationDialog(onDismiss = { showCalibrationDialog = false })
    }
}

@Composable
fun QiblaBubbleLevelCard(
    pitch: Float,
    roll: Float,
    isFlat: Boolean
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("qibla_bubble_level")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = if (isFlat) Icons.Default.CheckCircle else Icons.Default.ScreenRotation,
                        contentDescription = null,
                        tint = if (isFlat) EmeraldPrimary else GoldAccent,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = if (isFlat) "Device is Horizontally Flat" else "Level Phone Horizontally",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (isFlat) EmeraldPrimary else MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    text = if (isFlat) {
                        "Optimal sensor orientation for compass accuracy."
                    } else {
                        "Tilt: ${abs(pitch).roundToInt()}° Pitch, ${abs(roll).roundToInt()}° Roll. Center bubble for maximum accuracy."
                    },
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Bullseye Spirit Level Canvas
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .border(1.5.dp, if (isFlat) EmeraldPrimary else GoldAccent.copy(alpha = 0.6f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val center = Offset(size.width / 2, size.height / 2)
                    val maxRadius = size.width / 2 - 4.dp.toPx()

                    // Bullseye Center Rings
                    drawCircle(color = Color.Gray.copy(alpha = 0.3f), radius = maxRadius * 0.66f, center = center, style = Stroke(1.dp.toPx()))
                    drawCircle(color = Color.Gray.copy(alpha = 0.45f), radius = maxRadius * 0.33f, center = center, style = Stroke(1.dp.toPx()))

                    // Crosshairs
                    drawLine(color = Color.Gray.copy(alpha = 0.3f), start = Offset(center.x, 4f), end = Offset(center.x, size.height - 4f), strokeWidth = 1.dp.toPx())
                    drawLine(color = Color.Gray.copy(alpha = 0.3f), start = Offset(4f, center.y), end = Offset(size.width - 4f, center.y), strokeWidth = 1.dp.toPx())

                    // Moving Bubble based on pitch & roll
                    // Pitch tilts front/back (Y axis), Roll tilts left/right (X axis)
                    val bubbleOffsetX = (roll / 30f * maxRadius * 0.8f).coerceIn(-maxRadius * 0.8f, maxRadius * 0.8f)
                    val bubbleOffsetY = (pitch / 30f * maxRadius * 0.8f).coerceIn(-maxRadius * 0.8f, maxRadius * 0.8f)

                    val bubbleCenter = Offset(center.x + bubbleOffsetX, center.y + bubbleOffsetY)
                    drawCircle(
                        color = if (isFlat) EmeraldPrimary else GoldAccent,
                        radius = 8.dp.toPx(),
                        center = bubbleCenter
                    )
                }
            }
        }
    }
}

@Composable
fun QiblaCalibrationDialog(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.AllInclusive, contentDescription = null, tint = GoldAccent)
                Text("Magnetometer Calibration")
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Android magnetometers need occasional recalibration when exposed to magnetic fields, metal objects, or electronic chargers.",
                    style = MaterialTheme.typography.bodyMedium
                )

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = EmeraldPrimary.copy(alpha = 0.12f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "Calibration Steps:",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = EmeraldPrimary
                        )
                        Text(
                            text = "1. Hold your phone firmly in your hand away from metal items or laptops.\n2. Wave the phone in a smooth 3D figure-8 pattern through the air 3 to 4 times.\n3. The sensor accuracy will update to High Precision automatically.",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
            ) {
                Text("Got It")
            }
        }
    )
}

@Composable
private fun MetricInfoBox(
    title: String,
    value: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
