package com.example.ui.components.profile

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.SpiritualAchievementsEngine
import com.example.domain.model.ModuleProgressSummary
import com.example.domain.model.SpiritualBadge
import com.example.domain.model.SpiritualGoals
import com.example.domain.model.UserAvatar
import com.example.ui.theme.EmeraldAccent
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.EmeraldPrimaryVariant
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldLight
import com.example.ui.viewmodel.DeenUiState
import com.example.ui.viewmodel.DeenViewModel

@Composable
fun UserProfileAndGoalsView(
    state: DeenUiState,
    viewModel: DeenViewModel,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val userProfile = state.userProfile
    val isUserLoggedIn = userProfile != null && userProfile.uid.isNotBlank()

    // Active tab in Profile System: 0: Progress, 1: Spiritual Goals, 2: Achievements, 3: Account & Auth
    var selectedTab by remember { mutableIntStateOf(0) }

    val goals = state.spiritualGoals
    val progressSummary = remember(state, goals) {
        SpiritualAchievementsEngine.computeProgressSummary(state, goals)
    }
    val allBadges = remember(state, goals) {
        SpiritualAchievementsEngine.computeBadges(state, goals)
    }

    var isEditingProfile by remember { mutableStateOf(false) }
    var editName by remember { mutableStateOf(userProfile?.displayName ?: "Believer") }
    var editBio by remember { mutableStateOf(userProfile?.spiritualIntentionBio ?: "Seeking closeness to Allah through continuous learning and worship") }
    var selectedAvatarId by remember { mutableStateOf(userProfile?.avatarId ?: UserAvatar.MOSQUE.id) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("user_profile_system_view")
    ) {
        // Top Header Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Avatar Badge with quick edit click
                val avatar = UserAvatar.fromId(userProfile?.avatarId ?: selectedAvatarId)
                Surface(
                    shape = CircleShape,
                    color = EmeraldPrimary.copy(alpha = 0.15f),
                    border = BorderStroke(1.5.dp, EmeraldPrimary.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .size(46.dp)
                        .clickable { isEditingProfile = true }
                        .testTag("profile_avatar_badge")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(avatar.emoji, fontSize = 22.sp)
                    }
                }

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = if (isUserLoggedIn) userProfile?.displayName ?: "Believer" else "Guest Seeker",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (isUserLoggedIn) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = if (userProfile?.role == "subscriber" || userProfile?.role == "admin") GoldAccent else EmeraldPrimary
                            ) {
                                Text(
                                    text = if (userProfile?.role == "subscriber") "PRO" else if (userProfile?.role == "admin") "ADMIN" else "MEMBER",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (userProfile?.role == "subscriber") Color.Black else Color.White,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                    }
                    Text(
                        text = progressSummary.spiritualRankTitle,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = EmeraldPrimary
                    )
                }
            }

            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .testTag("close_profile_view_btn")
                    .testTag("user_dialog_close")
            ) {
                Icon(Icons.Default.Close, contentDescription = "Close Profile")
            }
        }

        // Secondary Tabs: 4 Modules
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            edgePadding = 16.dp,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = EmeraldPrimary,
            modifier = Modifier.fillMaxWidth().testTag("profile_top_tabs")
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Progress", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                icon = { Icon(Icons.Default.Insights, contentDescription = null, modifier = Modifier.size(16.dp)) },
                modifier = Modifier.testTag("tab_profile_progress")
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Spiritual Goals", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                icon = { Icon(Icons.Default.Flag, contentDescription = null, modifier = Modifier.size(16.dp)) },
                modifier = Modifier.testTag("tab_profile_goals")
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text("Achievements (${progressSummary.unlockedBadgesCount}/${progressSummary.totalBadgesCount})", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                icon = { Icon(Icons.Default.EmojiEvents, contentDescription = null, modifier = Modifier.size(16.dp)) },
                modifier = Modifier.testTag("tab_profile_achievements")
            )
            Tab(
                selected = selectedTab == 3,
                onClick = { selectedTab = 3 },
                text = { Text(if (isUserLoggedIn) "Account" else "Sign In / Register", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                icon = { Icon(Icons.Default.ManageAccounts, contentDescription = null, modifier = Modifier.size(16.dp)) },
                modifier = Modifier.testTag("tab_profile_account")
            )
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f))

        // Content Area based on Tab
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            when (selectedTab) {
                0 -> ProgressOverviewSection(progressSummary = progressSummary, state = state, onNavigateToGoals = { selectedTab = 1 })
                1 -> SpiritualGoalsSection(goals = goals, onSaveGoals = { viewModel.updateSpiritualGoals(it) })
                2 -> AchievementsSection(badges = allBadges)
                3 -> AccountAuthSection(
                    state = state,
                    viewModel = viewModel,
                    isUserLoggedIn = isUserLoggedIn,
                    userProfile = userProfile,
                    onOpenProfileEdit = { isEditingProfile = true }
                )
            }
        }
    }

    // Edit Profile Modal Dialog
    if (isEditingProfile) {
        AlertDialog(
            onDismissRequest = { isEditingProfile = false },
            title = {
                Text("Edit Spiritual Profile", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text("Select Spiritual Motif Avatar:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        UserAvatar.entries.take(4).forEach { av ->
                            val isSelected = av.id == selectedAvatarId
                            Surface(
                                shape = CircleShape,
                                color = if (isSelected) EmeraldPrimary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                                border = if (isSelected) BorderStroke(2.dp, EmeraldPrimary) else null,
                                modifier = Modifier
                                    .size(46.dp)
                                    .clickable { selectedAvatarId = av.id }
                                    .testTag("avatar_option_${av.id}")
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(av.emoji, fontSize = 20.sp)
                                }
                            }
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        UserAvatar.entries.drop(4).take(4).forEach { av ->
                            val isSelected = av.id == selectedAvatarId
                            Surface(
                                shape = CircleShape,
                                color = if (isSelected) EmeraldPrimary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                                border = if (isSelected) BorderStroke(2.dp, EmeraldPrimary) else null,
                                modifier = Modifier
                                    .size(46.dp)
                                    .clickable { selectedAvatarId = av.id }
                                    .testTag("avatar_option_${av.id}")
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(av.emoji, fontSize = 20.sp)
                                }
                            }
                        }
                    }

                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Display Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("edit_profile_name_input")
                    )

                    OutlinedTextField(
                        value = editBio,
                        onValueChange = { editBio = it },
                        label = { Text("Personal Spiritual Intention / Niyyah") },
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth().testTag("edit_profile_bio_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateUserProfileDetails(editName, selectedAvatarId, editBio)
                        isEditingProfile = false
                        Toast.makeText(context, "Spiritual profile updated!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                    modifier = Modifier.testTag("save_profile_edit_btn")
                ) {
                    Text("Save Profile")
                }
            },
            dismissButton = {
                TextButton(onClick = { isEditingProfile = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

/**
 * Section 1: Progress Overview across the 5 modules with visual indicators.
 */
@Composable
private fun ProgressOverviewSection(
    progressSummary: ModuleProgressSummary,
    state: DeenUiState,
    onNavigateToGoals: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Hero Vitality Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = EmeraldPrimary),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier.fillMaxWidth().testTag("hero_vitality_card")
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(EmeraldPrimary, EmeraldPrimaryVariant, Color(0xFF0F5132))
                            )
                        )
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = GoldAccent.copy(alpha = 0.25f)
                            ) {
                                Text(
                                    text = "SPIRITUAL VITALITY SCORE",
                                    color = GoldLight,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = 1.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Text(
                                text = "${progressSummary.overallSpiritualVitalityScore}% Consistency",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                            )
                            Text(
                                text = "Rank: ${progressSummary.spiritualRankTitle} • ${progressSummary.unlockedBadgesCount} Badges Unlocked",
                                style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.9f))
                            )
                        }

                        // Circular Progress Gauge
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(68.dp)) {
                            CircularProgressIndicator(
                                progress = { progressSummary.overallSpiritualVitalityScore / 100f },
                                modifier = Modifier.size(68.dp),
                                strokeWidth = 7.dp,
                                color = GoldAccent,
                                trackColor = Color.White.copy(alpha = 0.2f)
                            )
                            Text(
                                text = "${progressSummary.overallSpiritualVitalityScore}%",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                            )
                        }
                    }
                }
            }
        }

        // Section Title
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Module Progress Tracking",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                TextButton(onClick = onNavigateToGoals) {
                    Text("Adjust Goals", fontWeight = FontWeight.Bold, color = EmeraldPrimary, fontSize = 12.sp)
                }
            }
        }

        // Module 1: Quranic Studies
        item {
            ModuleProgressCard(
                icon = Icons.Default.MenuBook,
                iconColor = EmeraldPrimary,
                title = "Quranic Studies",
                arabicTitle = "دراسة القرآن الكريم",
                primaryStat = "${progressSummary.quranAyahsReadToday} / ${progressSummary.quranAyahsGoal} Ayahs",
                subtitle = "Daily Reading Goal • ${progressSummary.quranSurahsCompleted} Surahs Explored",
                progress = progressSummary.quranProgressPercentage,
                tag = "quran_progress_card"
            )
        }

        // Module 2: Hadith Learning
        item {
            ModuleProgressCard(
                icon = Icons.Default.HistoryEdu,
                iconColor = GoldAccent,
                title = "Hadith Learning",
                arabicTitle = "تعلم الحديث الشريف",
                primaryStat = "${progressSummary.hadithsStudiedCount} / ${progressSummary.hadithsStudiedGoal} Studied",
                subtitle = "Weekly Target • ${progressSummary.hadithsMemorizedCount} Memorized",
                progress = progressSummary.hadithProgressPercentage,
                tag = "hadith_progress_card"
            )
        }

        // Module 3: Dua Memorization
        item {
            ModuleProgressCard(
                icon = Icons.Default.Favorite,
                iconColor = Color(0xFFE91E63),
                title = "Dua Memorization",
                arabicTitle = "حفظ الأدعية والأذكار",
                primaryStat = "${progressSummary.duasMemorizedCount} / ${progressSummary.duasMemorizedGoal} Memorized",
                subtitle = "Monthly Goal • ${progressSummary.duasFavoritedCount} Bookmarked Invocations",
                progress = progressSummary.duaProgressPercentage,
                tag = "dua_progress_card"
            )
        }

        // Module 4: Understanding Namaz & Salah Consistency
        item {
            ModuleProgressCard(
                icon = Icons.Default.AccessTime,
                iconColor = EmeraldAccent,
                title = "Understanding Namaz & Consistency",
                arabicTitle = "المحافظة على الصلاة وفهمها",
                primaryStat = "${progressSummary.salahOfferedToday} / 5 Prayers Today",
                subtitle = "${progressSummary.salahStreakDays} Day Streak • ${progressSummary.salahStepsUnderstood}/8 Steps Meaning Mastered",
                progress = progressSummary.salahProgressPercentage,
                tag = "salah_progress_card"
            )
        }

        // Module 5: Fasting (Sawm) Journey
        item {
            ModuleProgressCard(
                icon = Icons.Default.NightsStay,
                iconColor = Color(0xFF3F51B5),
                title = "Fasting (Sawm) Journey",
                arabicTitle = "رحلة الصيام والقيام",
                primaryStat = "${progressSummary.fastingDaysThisMonth} / ${progressSummary.fastingGoalDays} Days Fasted",
                subtitle = "${progressSummary.fastingStreak} Day Streak • Sunnah Mondays & Thursdays Focus",
                progress = progressSummary.fastingProgressPercentage,
                tag = "fasting_progress_card"
            )
        }
    }
}

@Composable
private fun ModuleProgressCard(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    arabicTitle: String,
    primaryStat: String,
    subtitle: String,
    progress: Float,
    tag: String
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth().testTag(tag)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
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
                        color = iconColor.copy(alpha = 0.15f),
                        modifier = Modifier.size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
                        }
                    }
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                            Text(arabicTitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
                        }
                        Text(subtitle, style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = iconColor.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, color = iconColor),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            // Linear Progress Bar
            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(7.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = iconColor,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Target: $primaryStat", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(if (progress >= 1f) "Goal Completed! 🎉" else "${((1f - progress) * 100).toInt()}% Remaining", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold), color = if (progress >= 1f) EmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

/**
 * Section 2: Spiritual Goals Customization (Set and adjust targets across all 5 modules).
 */
@Composable
private fun SpiritualGoalsSection(
    goals: SpiritualGoals,
    onSaveGoals: (SpiritualGoals) -> Unit
) {
    val context = LocalContext.current
    var quranAyahs by remember(goals) { mutableIntStateOf(goals.dailyQuranAyahsTarget) }
    var hadithTarget by remember(goals) { mutableIntStateOf(goals.weeklyHadithsToStudy) }
    var duaTarget by remember(goals) { mutableIntStateOf(goals.monthlyDuasToMemorize) }
    var fastingTarget by remember(goals) { mutableIntStateOf(goals.monthlyFastingTargetDays) }
    var salahMeaningSteps by remember(goals) { mutableIntStateOf(goals.salahMeaningUnderstandingCompleted) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().testTag("spiritual_goals_section"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(Icons.Default.Flag, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(24.dp))
                    Column {
                        Text("Personal Spiritual Intentions", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                        Text(
                            "Set realistic goals for your daily worship and study. Adjust targets as your spiritual capacity expands.",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Goal 1: Quran Daily Target
        item {
            GoalStepperCard(
                title = "Quran Daily Reading Goal",
                arabicSubtitle = "ورد القرآن اليومي",
                currentValue = quranAyahs,
                unit = "Ayahs per day",
                minValue = 1,
                maxValue = 100,
                step = 5,
                presets = listOf(5, 10, 20, 50),
                onValueChange = { quranAyahs = it },
                tag = "goal_quran_stepper"
            )
        }

        // Goal 2: Hadith Weekly Target
        item {
            GoalStepperCard(
                title = "Hadith Learning Goal",
                arabicSubtitle = "دراسة الحديث الأسبوعية",
                currentValue = hadithTarget,
                unit = "Hadiths studied per week",
                minValue = 1,
                maxValue = 30,
                step = 1,
                presets = listOf(2, 5, 7, 10),
                onValueChange = { hadithTarget = it },
                tag = "goal_hadith_stepper"
            )
        }

        // Goal 3: Dua Memorization Goal
        item {
            GoalStepperCard(
                title = "Dua Memorization Goal",
                arabicSubtitle = "حفظ الأدعية شهرياً",
                currentValue = duaTarget,
                unit = "Duas memorized per month",
                minValue = 1,
                maxValue = 20,
                step = 1,
                presets = listOf(2, 4, 8, 10),
                onValueChange = { duaTarget = it },
                tag = "goal_dua_stepper"
            )
        }

        // Goal 4: Understanding Namaz Meaning
        item {
            GoalStepperCard(
                title = "Understanding Namaz Steps",
                arabicSubtitle = "فهم معاني أركان الصلاة",
                currentValue = salahMeaningSteps,
                unit = "of 8 core prayer steps understood in Arabic",
                minValue = 0,
                maxValue = 8,
                step = 1,
                presets = listOf(2, 4, 6, 8),
                onValueChange = { salahMeaningSteps = it },
                tag = "goal_salah_understanding_stepper"
            )
        }

        // Goal 5: Fasting (Sawm) Goal
        item {
            GoalStepperCard(
                title = "Monthly Voluntary Fasting Target",
                arabicSubtitle = "صيام التطوع شهرياً",
                currentValue = fastingTarget,
                unit = "Days fasted per month (Sunnah)",
                minValue = 1,
                maxValue = 15,
                step = 1,
                presets = listOf(2, 4, 6, 8),
                onValueChange = { fastingTarget = it },
                tag = "goal_fasting_stepper"
            )
        }

        // Save Goals Button
        item {
            Button(
                onClick = {
                    val updated = goals.copy(
                        dailyQuranAyahsTarget = quranAyahs,
                        weeklyHadithsToStudy = hadithTarget,
                        monthlyDuasToMemorize = duaTarget,
                        monthlyFastingTargetDays = fastingTarget,
                        salahMeaningUnderstandingCompleted = salahMeaningSteps,
                        updatedAt = System.currentTimeMillis()
                    )
                    onSaveGoals(updated)
                    Toast.makeText(context, "Spiritual goals saved and synced!", Toast.LENGTH_SHORT).show()
                },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().testTag("save_spiritual_goals_btn"),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("Save & Apply Spiritual Goals", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun GoalStepperCard(
    title: String,
    arabicSubtitle: String,
    currentValue: Int,
    unit: String,
    minValue: Int,
    maxValue: Int,
    step: Int,
    presets: List<Int>,
    onValueChange: (Int) -> Unit,
    tag: String
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)),
        modifier = Modifier.fillMaxWidth().testTag(tag)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                    Text(arabicSubtitle, style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = EmeraldPrimary.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "$currentValue",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black, color = EmeraldPrimary),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp)
                    )
                }
            }

            Text(unit, style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)

            // Stepper controls: - / Slider / +
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = { onValueChange((currentValue - step).coerceAtLeast(minValue)) },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(Icons.Default.RemoveCircleOutline, contentDescription = "Decrease", tint = EmeraldPrimary)
                }

                Slider(
                    value = currentValue.toFloat(),
                    onValueChange = { onValueChange(it.toInt().coerceIn(minValue, maxValue)) },
                    valueRange = minValue.toFloat()..maxValue.toFloat(),
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = { onValueChange((currentValue + step).coerceAtMost(maxValue)) },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(Icons.Default.AddCircleOutline, contentDescription = "Increase", tint = EmeraldPrimary)
                }
            }

            // Quick preset chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                presets.forEach { presetVal ->
                    val isSelected = currentValue == presetVal
                    FilterChip(
                        selected = isSelected,
                        onClick = { onValueChange(presetVal) },
                        label = { Text("$presetVal", fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EmeraldPrimary.copy(alpha = 0.18f),
                            selectedLabelColor = EmeraldPrimary
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

/**
 * Section 3: Achievements & Badges Grid (10 visual badges with progress & unlocked state).
 */
@Composable
private fun AchievementsSection(badges: List<SpiritualBadge>) {
    var selectedFilter by remember { mutableStateOf("All") }
    val filteredBadges = remember(badges, selectedFilter) {
        when (selectedFilter) {
            "Unlocked" -> badges.filter { it.isUnlocked }
            "In Progress" -> badges.filter { !it.isUnlocked }
            else -> badges
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().testTag("achievements_section"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Filter Chips Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("All", "Unlocked", "In Progress").forEach { filter ->
                val isSelected = selectedFilter == filter
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedFilter = filter },
                    label = { Text(filter, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = EmeraldPrimary.copy(alpha = 0.18f),
                        selectedLabelColor = EmeraldPrimary
                    ),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Badges List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filteredBadges) { badge ->
                BadgeCard(badge = badge)
            }
        }
    }
}

@Composable
private fun BadgeCard(badge: SpiritualBadge) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (badge.isUnlocked) GoldAccent.copy(alpha = 0.10f) else MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            1.dp,
            if (badge.isUnlocked) GoldAccent.copy(alpha = 0.6f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)
        ),
        modifier = Modifier.fillMaxWidth().testTag("badge_card_${badge.id}")
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Icon
            Surface(
                shape = CircleShape,
                color = if (badge.isUnlocked) GoldAccent.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surfaceVariant,
                border = if (badge.isUnlocked) BorderStroke(1.5.dp, GoldAccent) else null,
                modifier = Modifier.size(46.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(badge.iconEmoji, fontSize = 22.sp)
                }
            }

            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(badge.title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                        Text(badge.arabicTitle, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
                    }

                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = if (badge.isUnlocked) GoldAccent else MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = if (badge.isUnlocked) "UNLOCKED" else "IN PROGRESS",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Black,
                                color = if (badge.isUnlocked) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                    }
                }

                Text(badge.description, style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)

                // Progress Bar
                val fraction = (badge.currentProgress.toFloat() / badge.targetRequired.toFloat()).coerceIn(0f, 1f)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LinearProgressIndicator(
                        progress = { fraction },
                        modifier = Modifier
                            .weight(1f)
                            .height(5.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = if (badge.isUnlocked) GoldAccent else EmeraldPrimary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                    Text(
                        text = "${badge.currentProgress}/${badge.targetRequired} ${badge.unit}",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}

/**
 * Section 4: Account & Auth (Sign In, Register, Forgot Password, Profile Overview).
 */
@Composable
private fun AccountAuthSection(
    state: DeenUiState,
    viewModel: DeenViewModel,
    isUserLoggedIn: Boolean,
    userProfile: com.example.data.repository.UserProfile?,
    onOpenProfileEdit: () -> Unit
) {
    val context = LocalContext.current
    var authMode by remember { mutableIntStateOf(0) } // 0: Sign In, 1: Register, 2: Forgot Password
    var emailInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var nameInput by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().testTag("account_auth_section"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        if (isUserLoggedIn) {
            // Signed In View
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Active Deen365 Cloud Account", style = MaterialTheme.typography.labelSmall, color = EmeraldPrimary)
                            TextButton(onClick = onOpenProfileEdit) {
                                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Edit Profile", fontSize = 11.sp)
                            }
                        }

                        Text(userProfile?.displayName ?: "Believer", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                        Text(userProfile?.email ?: "", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            text = "\"${userProfile?.spiritualIntentionBio ?: "Seeking closeness to Allah through continuous learning and worship"}\"",
                            style = MaterialTheme.typography.bodySmall.copy(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Role: ${userProfile?.role?.uppercase() ?: "NORMAL"}", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = EmeraldPrimary)
                            Text("Plan: ${userProfile?.subscriptionPlan ?: "Free"}", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = GoldAccent)
                        }
                    }
                }
            }

            // Sign Out Button
            item {
                OutlinedButton(
                    onClick = {
                        viewModel.signOut()
                        Toast.makeText(context, "Signed out successfully", Toast.LENGTH_SHORT).show()
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("profile_sign_out_btn")
                        .testTag("sign_out_button")
                ) {
                    Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Sign Out of Deen365 Account")
                }
            }
        } else {
            // Guest Mode / Authentication Form
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(Icons.Default.CloudSync, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(24.dp))
                        Column {
                            Text("Guest Mode Active", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                            Text(
                                "Your spiritual goals and progress are saved locally on this device. Create an account to sync your worship journey across all devices with Firebase.",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Auth Mode Toggle
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Sign In", "Create Account", "Reset Password").forEachIndexed { index, title ->
                        val isSelected = authMode == index
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                authMode = index
                                errorMessage = null
                                successMessage = null
                            },
                            label = { Text(title, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = EmeraldPrimary.copy(alpha = 0.18f),
                                selectedLabelColor = EmeraldPrimary
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Error / Success feedback
            if (errorMessage != null) {
                item {
                    Text(errorMessage ?: "", color = MaterialTheme.colorScheme.error, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }
            if (successMessage != null) {
                item {
                    Text(successMessage ?: "", color = EmeraldPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            // Registration Name Input
            if (authMode == 1) {
                item {
                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        label = { Text("Full Name / Display Name") },
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth().testTag("auth_name_input")
                    )
                }
            }

            // Email Input
            item {
                OutlinedTextField(
                    value = emailInput,
                    onValueChange = { emailInput = it },
                    label = { Text("Email Address") },
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth().testTag("auth_email_input")
                )
            }

            // Password Input (except reset)
            if (authMode != 2) {
                item {
                    OutlinedTextField(
                        value = passwordInput,
                        onValueChange = { passwordInput = it },
                        label = { Text("Password") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth().testTag("auth_password_input")
                    )
                }
            }

            // Submit Button
            item {
                Button(
                    onClick = {
                        errorMessage = null
                        successMessage = null
                        isSubmitting = true
                        when (authMode) {
                            0 -> { // Login
                                viewModel.signInWithEmail(
                                    email = emailInput,
                                    password = passwordInput,
                                    onSuccess = {
                                        isSubmitting = false
                                        Toast.makeText(context, "Welcome back to Deen365!", Toast.LENGTH_SHORT).show()
                                    },
                                    onError = { err ->
                                        isSubmitting = false
                                        errorMessage = err
                                    }
                                )
                            }
                            1 -> { // Register
                                viewModel.registerWithEmail(
                                    email = emailInput,
                                    password = passwordInput,
                                    displayName = nameInput,
                                    onSuccess = {
                                        isSubmitting = false
                                        Toast.makeText(context, "Account created successfully! Welcome to Deen365.", Toast.LENGTH_LONG).show()
                                    },
                                    onError = { err ->
                                        isSubmitting = false
                                        errorMessage = err
                                    }
                                )
                            }
                            2 -> { // Reset Password
                                viewModel.sendPasswordReset(
                                    email = emailInput,
                                    onSuccess = {
                                        isSubmitting = false
                                        successMessage = "Password reset email sent! Check your inbox."
                                    },
                                    onError = { err ->
                                        isSubmitting = false
                                        errorMessage = err
                                    }
                                )
                            }
                        }
                    },
                    enabled = !isSubmitting && emailInput.isNotBlank() && (authMode == 2 || passwordInput.isNotBlank()),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("auth_submit_btn"),
                    contentPadding = PaddingValues(vertical = 12.dp)
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                    } else {
                        Text(
                            text = when (authMode) {
                                0 -> "Sign In to Account"
                                1 -> "Create Deen365 Account"
                                else -> "Send Password Reset Email"
                            },
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
