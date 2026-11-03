package com.example.ui.screens.namaz

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent

enum class MadhhabType(val displayName: String, val arabicName: String, val imamName: String) {
    ALL("All Schools Comparison", "مُقَارَنَةُ الْمَذَاهِبِ", "Four Sunni Imams"),
    HANAFI("Hanafi", "الْحَنَفِيّ", "Imam Abu Hanifa (80–150 AH)"),
    SHAFII("Shafi'i", "الشَّافِعِيّ", "Imam Muhammad ibn Idris al-Shafi'i (150–204 AH)"),
    MALIKI("Maliki", "الْمَالِكِيّ", "Imam Malik ibn Anas (93–179 AH)"),
    HANBALI("Hanbali", "الْحَنْبَلِيّ", "Imam Ahmad ibn Hanbal (164–241 AH)")
}

data class MadhhabRuling(
    val prayerTimeOrAction: String,
    val hanafiRuling: String,
    val shafiiRuling: String,
    val malikiRuling: String,
    val hanbaliRuling: String,
    val hadithReference: String,
    val explanation: String
)

@Composable
fun NamazMadhhabView(
    modifier: Modifier = Modifier
) {
    var selectedMadhhab by remember { mutableStateOf(MadhhabType.ALL) }
    val rulings = remember { getSchoolOfThoughtRulings() }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth().testTag("madhhab_header_card")
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "The Four Sunni Schools of Fiqh",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "All four major madhhabs (Hanafi, Shafi'i, Maliki, Hanbali) are valid derivations from the authentic Sunnah of the Prophet ﷺ. Below are the verified hadith citations for each prayer action.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // School Selector Chips
        item {
            ScrollableTabRow(
                selectedTabIndex = selectedMadhhab.ordinal,
                edgePadding = 0.dp,
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                contentColor = EmeraldPrimary,
                modifier = Modifier.testTag("madhhab_tab_row")
            ) {
                MadhhabType.values().forEach { madhhab ->
                    Tab(
                        selected = selectedMadhhab == madhhab,
                        onClick = { selectedMadhhab = madhhab },
                        text = { Text(madhhab.displayName, fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                    )
                }
            }
        }

        items(rulings) { ruling ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth().testTag("ruling_card_${ruling.prayerTimeOrAction.take(10).replace(" ", "_")}")
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = ruling.prayerTimeOrAction,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = EmeraldPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = GoldAccent.copy(alpha = 0.18f)
                        ) {
                            Text(
                                text = "Hadith Reference",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    color = GoldAccent,
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Evidence: ${ruling.hadithReference}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier.padding(8.dp)
                        )
                    }

                    if (selectedMadhhab == MadhhabType.ALL || selectedMadhhab == MadhhabType.HANAFI) {
                        MadhhabRulingRow("Hanafi", ruling.hanafiRuling, EmeraldPrimary)
                    }
                    if (selectedMadhhab == MadhhabType.ALL || selectedMadhhab == MadhhabType.SHAFII) {
                        MadhhabRulingRow("Shafi'i", ruling.shafiiRuling, Color(0xFF0284C7))
                    }
                    if (selectedMadhhab == MadhhabType.ALL || selectedMadhhab == MadhhabType.MALIKI) {
                        MadhhabRulingRow("Maliki", ruling.malikiRuling, Color(0xFFD97706))
                    }
                    if (selectedMadhhab == MadhhabType.ALL || selectedMadhhab == MadhhabType.HANBALI) {
                        MadhhabRulingRow("Hanbali", ruling.hanbaliRuling, Color(0xFF7C3AED))
                    }

                    Text(
                        text = ruling.explanation,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun MadhhabRulingRow(madhhab: String, ruling: String, badgeColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            shape = RoundedCornerShape(4.dp),
            color = badgeColor.copy(alpha = 0.15f)
        ) {
            Text(
                text = madhhab,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = badgeColor),
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
        }
        Text(
            text = ruling,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
    }
}

fun getSchoolOfThoughtRulings(): List<MadhhabRuling> {
    return listOf(
        MadhhabRuling(
            prayerTimeOrAction = "Asr Prayer Time Start (Shadow Length)",
            hanafiRuling = "Shadow of an object reaches 2x its original length (Mithlayn) + noon shadow.",
            shafiiRuling = "Shadow of an object reaches 1x its original length (Mithl) + noon shadow.",
            malikiRuling = "Shadow reaches 1x its original length (Mithl). Preferred to pray before shadow reaches 2x.",
            hanbaliRuling = "Shadow reaches 1x its original length (Mithl).",
            hadithReference = "Sahih Muslim 612; Sunan Abi Dawud 393 (Jibril led the Prophet ﷺ Asr when shadow was equal to object).",
            explanation = "Imam Abu Hanifa based the 2x length on the hadith: 'Delay the prayer until it cools down in intense heat' and preserving the full Dhuhr time window."
        ),
        MadhhabRuling(
            prayerTimeOrAction = "Raising Hands (Rafa al-Yadayn)",
            hanafiRuling = "Raise hands only at initial Takbirat al-Ihram and Qunut of Witr.",
            shafiiRuling = "Raise hands at Takbirat al-Ihram, before Ruku, rising from Ruku, and after rising from first Tashahhud.",
            malikiRuling = "Only at Takbirat al-Ihram in primary Maliki transmission (Mudawwanah).",
            hanbaliRuling = "At Takbirat al-Ihram, bowing into Ruku, and rising from Ruku (same as Shafi'i).",
            hadithReference = "Sahih al-Bukhari 735 & Sahih Muslim 390 (from Ibn Umar); Sahih Muslim 399 & Sunan Abi Dawud 748 (from Ibn Mas'ud).",
            explanation = "Shafi'is and Hanbalis follow the explicit narrations of Ibn Umar, while Hanafis follow the practice transmitted by Abdullah ibn Mas'ud in Kufa."
        ),
        MadhhabRuling(
            prayerTimeOrAction = "Hand Placement in Qiyam (Standing)",
            hanafiRuling = "Men place hands below the navel (right hand grasping left wrist). Women place hands on chest.",
            shafiiRuling = "Both men and women place hands above the navel and below the chest, tilted slightly to the left over heart.",
            malikiRuling = "Sadl (arms resting naturally at the sides) is preferred in Fard prayer in the well-known position; Qabd (clasped) is recommended in Nafl.",
            hanbaliRuling = "Right hand over left wrist, placed below the navel or on the chest (both views narrated from Ahmad).",
            hadithReference = "Sunan Abi Dawud 756 (Ali: 'It is Sunnah to place hand below navel'); Sahih Muslim 401 (Wa'il ibn Hujr: 'Placed right on left hand').",
            explanation = "Imam Malik prioritized the uninterrupted living practice ('Amal) of the people of Medina, while others followed specific transmitted companion reports."
        ),
        MadhhabRuling(
            prayerTimeOrAction = "Recitation of Surah Al-Fatiha Behind the Imam",
            hanafiRuling = "Follower does not recite Fatiha behind Imam in either silent or loud prayers (Muqtadi remains silent).",
            shafiiRuling = "Recitation of Fatiha is obligatory (Fard) upon every individual, even behind the Imam in loud prayers.",
            malikiRuling = "Recite in silent prayers (Dhuhr, Asr). Do NOT recite in loud prayers (Fajr, Maghrib, Isha); listen attentively.",
            hanbaliRuling = "Mustahabb (recommended) in silent prayers and pauses of the Imam in loud prayers; prohibited when Imam is reciting aloud.",
            hadithReference = "Sahih al-Bukhari 756 ('No prayer for who does not recite Fatiha'); Surah Al-A'raf 7:204; Sahih Muslim 404 ('When the Imam recites, listen silently').",
            explanation = "Hanafis reconcile the evidence by considering the Imam's recitation as legally sufficient for the followers based on the Prophet's command."
        ),
        MadhhabRuling(
            prayerTimeOrAction = "Saying Ameen Aloud (Jahran) vs Silently (Sirran)",
            hanafiRuling = "Say Ameen quietly (Sirran) so only oneself hears it.",
            shafiiRuling = "Say Ameen aloud (Jahran) in unison with the Imam in loud prayers (Fajr, Maghrib, Isha).",
            malikiRuling = "Say Ameen silently (Sirran). The Imam does not say Ameen aloud in loud prayers.",
            hanbaliRuling = "Say Ameen aloud in loud prayers alongside the Imam.",
            hadithReference = "Sahih al-Bukhari 780 ('When the Imam says Ameen, say Ameen for the angels say Ameen'); Jami` at-Tirmidhi 248.",
            explanation = "Hanafis view Ameen as a supplication (Dua), and Quran 7:55 instructs: 'Call upon your Lord in humility and in secret'."
        ),
        MadhhabRuling(
            prayerTimeOrAction = "Tashahhud Finger Pointing & Movement",
            hanafiRuling = "Raise the index finger when saying 'Ash-hadu alla ilaha' and lower it at 'illa Allah'. Do not keep moving it.",
            shafiiRuling = "Raise the index finger at 'illa Allah' with a slight curl, pointing towards the Qibla without moving it back and forth.",
            malikiRuling = "Keep moving the index finger constantly from side to side throughout the entire Tashahhud.",
            hanbaliRuling = "Point the index finger every time the name of Allah or Lord is mentioned, without continuous shaking.",
            hadithReference = "Sahih Muslim 580 (Ibn Umar); Sunan an-Nasa'i 1275 (Wa'il ibn Hujr: 'I saw him move his finger, supplicating with it').",
            explanation = "Differences arise from differing interpretations of the Arabic word 'Yuharrikuha' in Wa'il ibn Hujr's narration versus Abdullah ibn az-Zubayr's report in Abu Dawud."
        ),
        MadhhabRuling(
            prayerTimeOrAction = "Qunut Supplication in Fajr & Witr",
            hanafiRuling = "Qunut is recited only in Witr prayer (every night before Ruku). No daily Qunut in Fajr.",
            shafiiRuling = "Qunut is Sunnah Mu'akkadah in the second Rak'ah of Fajr every day (after Ruku), and in second half of Ramadan Witr.",
            malikiRuling = "Mustahabb (desirable) in Fajr before Ruku, recited silently. No Qunut in Witr except in distress (Nazilah).",
            hanbaliRuling = "Qunut is Sunnah in Witr throughout the entire year (after Ruku). In Fajr only during calamities (Qunut an-Nawazil).",
            hadithReference = "Sunan Abi Dawud 1425 (Al-Hasan ibn Ali: 'The Prophet taught me words to say in Qunut of Witr'); Sunan an-Nasa'i 1080 (Anas).",
            explanation = "Anas ibn Malik reported: 'The Prophet ﷺ continued to recite Qunut in the dawn prayer until he departed this world' (Musnad Ahmad 12679)."
        )
    )
}
