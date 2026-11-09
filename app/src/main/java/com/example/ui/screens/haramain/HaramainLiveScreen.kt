package com.example.ui.screens.haramain

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldLight
import com.example.ui.viewmodel.DeenUiState
import com.example.ui.viewmodel.DeenViewModel
import com.example.ui.viewmodel.MainTab
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HaramainLiveScreen(
    state: DeenUiState,
    viewModel: DeenViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    BackHandler {
        viewModel.selectTab(MainTab.DASHBOARD)
    }

    val liveState = state.haramainLiveState
    val currentChannel = remember(liveState.selectedMosque) {
        HaramainLiveData.getChannel(liveState.selectedMosque)
    }

    // Format Makkah Standard Time (UTC+3)
    val makkahTime = remember {
        val sdf = SimpleDateFormat("hh:mm:ss a", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("GMT+3")
        sdf.format(Date())
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                "Makkah & Madinah Live",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFFDC2626)) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                                ) {
                                    Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color.White))
                                    Text(
                                        text = "LIVE 24/7",
                                        color = Color.White,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            }
                        }
                        Text(
                            text = "Official Saudi Haramain Broadcasts",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.selectTab(MainTab.DASHBOARD) },
                        modifier = Modifier.testTag("haramain_back_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 14.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Mosque Switcher Tabs (Makkah vs Madinah)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    FilterChip(
                        selected = liveState.selectedMosque == HolyMosque.MAKKAH_HARAM,
                        onClick = { viewModel.setHaramainMosque(HolyMosque.MAKKAH_HARAM) },
                        label = { Text("🕋 Makkah (Al-Haram)", fontWeight = FontWeight.Bold) },
                        modifier = Modifier.weight(1f).testTag("select_makkah_stream")
                    )
                    FilterChip(
                        selected = liveState.selectedMosque == HolyMosque.MADINAH_NABAWI,
                        onClick = { viewModel.setHaramainMosque(HolyMosque.MADINAH_NABAWI) },
                        label = { Text("🕌 Madinah (An-Nabawi)", fontWeight = FontWeight.Bold) },
                        modifier = Modifier.weight(1f).testTag("select_madinah_stream")
                    )
                }
            }

            // Video Player Container Hero Card
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Black),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clickable {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(currentChannel.streamWebUrl))
                            context.startActivity(intent)
                        }
                        .testTag("stream_player_card")
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0xFF1E293B),
                                        Color(0xFF0F172A),
                                        Color.Black
                                    )
                                )
                            )
                            .padding(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color.Black.copy(alpha = 0.6f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color.Red))
                                        Text(currentChannel.channelName, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color.Black.copy(alpha = 0.6f)
                                ) {
                                    Text(
                                        text = "🇸🇦 Makkah Time: $makkahTime",
                                        color = GoldLight,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            // Center Play Icon
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = EmeraldPrimary,
                                    modifier = Modifier.size(56.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.PlayArrow, contentDescription = "Play Stream", tint = Color.White, modifier = Modifier.size(36.dp))
                                    }
                                }
                            }

                            // Bottom broadcast info
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                Column {
                                    Text(
                                        text = currentChannel.title,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color.White)
                                    )
                                    Text(
                                        text = currentChannel.broadcastContent,
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f))
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = EmeraldPrimary.copy(alpha = 0.8f)
                                ) {
                                    Text(
                                        text = "HD 1080p",
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Launch Full HD Stream in YouTube / Browser
            item {
                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(currentChannel.streamWebUrl))
                        context.startActivity(intent)
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                    modifier = Modifier.fillMaxWidth().testTag("launch_external_stream_button")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.LiveTv, contentDescription = null, modifier = Modifier.size(18.dp))
                        Text("Open Fullscreen Live Feed (YouTube HD)", fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Historical Virtue & Du'a Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Surface(shape = CircleShape, color = GoldAccent.copy(alpha = 0.2f), modifier = Modifier.size(32.dp)) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("✨", fontSize = 14.sp)
                                }
                            }
                            Text(
                                text = "Sanctity of the Holy Sanctuary",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                        }

                        Text(
                            text = currentChannel.historicalVirtue,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold, color = EmeraldPrimary)
                        )

                        Text(
                            text = currentChannel.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

                        Text(
                            text = if (liveState.selectedMosque == HolyMosque.MAKKAH_HARAM) {
                                "Du'a upon seeing the Kaaba:\n'Allāhumma zid hādhāl-bayta tashrīfan wa ta'ẓīman wa takrīman wa mahābah.'"
                            } else {
                                "Sending Salawat upon the Messenger (ﷺ):\n'Allāhumma ṣalli 'alā Muḥammadin wa 'alā āli Muḥammad.'"
                            },
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}
