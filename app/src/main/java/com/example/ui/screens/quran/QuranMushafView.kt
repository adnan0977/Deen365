package com.example.ui.screens.quran

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.AyahEntity
import com.example.data.seed.SurahMeta
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent

@Composable
fun QuranMushafView(
    surah: SurahMeta,
    ayahs: List<AyahEntity>,
    arabicFontSizeSp: Int,
    currentPlayingAyahNumber: Int?,
    isQuranAudioPlaying: Boolean,
    onPlayAyahAudio: (AyahEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("quran_mushaf_view"),
        contentPadding = PaddingValues(top = 16.dp, bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent.copy(alpha = 0.35f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = EmeraldPrimary,
                            fontSize = (arabicFontSizeSp * 0.95f).sp
                        )
                    )

                    HorizontalDivider(color = GoldAccent.copy(alpha = 0.25f))

                    // Continuous flowing script with verse end marker
                    val annotatedString = buildAnnotatedString {
                        ayahs.forEach { ayah ->
                            val isPlaying = currentPlayingAyahNumber == ayah.ayahNumber && isQuranAudioPlaying
                            withStyle(
                                style = SpanStyle(
                                    color = if (isPlaying) EmeraldPrimary else Color.Unspecified,
                                    fontWeight = if (isPlaying) FontWeight.ExtraBold else FontWeight.SemiBold
                                )
                            ) {
                                append(ayah.arabicText)
                                append(" ")
                            }

                            // Ayah end circular ornament ۝ {ayahNumber}
                            withStyle(
                                style = SpanStyle(
                                    color = GoldAccent,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = (arabicFontSizeSp * 0.75f).sp
                                )
                            ) {
                                append(" ۝${ayah.ayahNumber} ")
                            }
                        }
                    }

                    Text(
                        text = annotatedString,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = arabicFontSizeSp.sp,
                            lineHeight = (arabicFontSizeSp * 1.8f).sp,
                            textAlign = TextAlign.Right
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("mushaf_text_container")
                    )
                }
            }
        }
    }
}
