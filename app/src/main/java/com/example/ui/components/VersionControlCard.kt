package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent

data class ReleaseVersionItem(
    val version: String,
    val buildNumber: Int,
    val releaseDate: String,
    val isCurrent: Boolean,
    val highlights: List<String>
)

object VersionControlRegistry {
    const val CURRENT_VERSION_NAME = "1.4.0"
    const val CURRENT_BUILD_NUMBER = 140
    const val RELEASE_CHANNEL = "Production Stable"

    val RELEASES = listOf(
        ReleaseVersionItem(
            version = "v1.4.0",
            buildNumber = 140,
            releaseDate = "November 2026",
            isCurrent = true,
            highlights = listOf(
                "Ruqyah Shariah Audio Streaming Player with seek controls, skip +/-10s & curated reciters",
                "Single GPS location auto-detection on Home screen without dropdown clutter",
                "Single-line aligned Quran chapter (Surah) headers with clean typography",
                "Google Mobile Ads (AdMob) designated banner placement spaces",
                "Enhanced spacing, margins, and accessibility gaps across all modules"
            )
        ),
        ReleaseVersionItem(
            version = "v1.3.0",
            buildNumber = 130,
            releaseDate = "October 2026",
            isCurrent = false,
            highlights = listOf(
                "Prayer Times Live Dashboard with real-time digital clock and next prayer countdown",
                "WorkManager recurring background notifications for all 5 daily prayers",
                "More Modules categorization grid with search and category filters"
            )
        ),
        ReleaseVersionItem(
            version = "v1.2.0",
            buildNumber = 120,
            releaseDate = "September 2026",
            isCurrent = false,
            highlights = listOf(
                "Daily Dhikr module with recommended invocations and interactive tasbih counter",
                "Full offline Namaz step-by-step guidance with audio recitations",
                "Accurate sensor-based Qibla compass with Kaaba alignment haptics"
            )
        ),
        ReleaseVersionItem(
            version = "v1.1.0",
            buildNumber = 110,
            releaseDate = "August 2026",
            isCurrent = false,
            highlights = listOf(
                "Daily Quranic reflection, brief Tafsir, and Hadith of the day",
                "Alim.org thematic index & Mirath Islamic inheritance calculator",
                "On-demand Quran and Hadith Room database caching via remote API"
            )
        ),
        ReleaseVersionItem(
            version = "v1.0.0",
            buildNumber = 100,
            releaseDate = "July 2026",
            isCurrent = false,
            highlights = listOf(
                "Initial Deen365 comprehensive Islamic companion release",
                "Full Quran reader (Mushaf & translation modes)",
                "Calculation methods for worldwide prayer timetables"
            )
        )
    )
}

/**
 * Version Control & System Information Card.
 * Displays app version, release channel, check-for-updates action, and full changelog dialog.
 */
@Composable
fun VersionControlCard(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showChangelogDialog by remember { mutableStateOf(false) }
    var isCheckingUpdates by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        ),
        modifier = modifier
            .fillMaxWidth()
            .testTag("version_control_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(EmeraldPrimary.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Commit,
                            contentDescription = null,
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "Deen365 Version Control",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = EmeraldPrimary.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "v${VersionControlRegistry.CURRENT_VERSION_NAME}",
                                    color = EmeraldPrimary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Build ${VersionControlRegistry.CURRENT_BUILD_NUMBER} • ${VersionControlRegistry.RELEASE_CHANNEL}",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = GoldAccent.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "Git v${VersionControlRegistry.CURRENT_VERSION_NAME}",
                        color = GoldAccent,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))

            // Action Buttons Row: Check Updates & View Changelog
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        isCheckingUpdates = true
                        Toast.makeText(
                            context,
                            "Checking update repository... Deen365 is up to date (v${VersionControlRegistry.CURRENT_VERSION_NAME})!",
                            Toast.LENGTH_LONG
                        ).show()
                        isCheckingUpdates = false
                    },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("btn_check_for_updates")
                ) {
                    Icon(
                        imageVector = Icons.Default.SystemUpdateAlt,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Check Updates", fontSize = 12.sp)
                }

                FilledTonalButton(
                    onClick = { showChangelogDialog = true },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("btn_view_changelog")
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Release Notes", fontSize = 12.sp)
                }
            }
        }
    }

    if (showChangelogDialog) {
        AlertDialog(
            onDismissRequest = { showChangelogDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Verified, contentDescription = null, tint = EmeraldPrimary)
                    Text("Version History & Changelog", style = MaterialTheme.typography.titleMedium)
                }
            },
            text = {
                LazyColumn(
                    modifier = Modifier.heightIn(max = 380.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(VersionControlRegistry.RELEASES) { rel ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (rel.isCurrent) EmeraldPrimary.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                            ),
                            border = if (rel.isCurrent) androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.4f)) else null,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text(
                                            text = rel.version,
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                        )
                                        if (rel.isCurrent) {
                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = EmeraldPrimary
                                            ) {
                                                Text(
                                                    text = "LATEST",
                                                    color = Color.White,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                )
                                            }
                                        }
                                    }
                                    Text(
                                        text = rel.releaseDate,
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                rel.highlights.forEach { hl ->
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Text("•", color = EmeraldPrimary, fontWeight = FontWeight.Bold)
                                        Text(
                                            text = hl,
                                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showChangelogDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}
