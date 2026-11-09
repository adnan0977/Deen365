package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.remote.AiCategory
import com.example.data.remote.AiGuidanceResult
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.EmeraldPrimaryVariant
import com.example.ui.theme.GoldAccent
import com.example.ui.screens.ai.PersonalizedAiSpiritualGuideCard
import com.example.ui.screens.ai.SpiritualAiChatView
import com.example.ui.theme.GoldLight
import com.example.ui.viewmodel.DeenUiState
import com.example.ui.viewmodel.DeenViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiGuidanceScreen(
    state: DeenUiState,
    viewModel: DeenViewModel,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Tab Navigation: Spiritual Chat vs Daily Guide & Wisdom
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            PrimaryTabRow(
                selectedTabIndex = state.activeChatTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = EmeraldPrimary,
                modifier = Modifier.testTag("ai_mode_tab_row")
            ) {
                Tab(
                    selected = state.activeChatTab == 0,
                    onClick = { viewModel.setActiveChatTab(0) },
                    text = { Text("Spiritual Chat", fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    modifier = Modifier.testTag("ai_tab_chat")
                )
                Tab(
                    selected = state.activeChatTab == 1,
                    onClick = { viewModel.setActiveChatTab(1) },
                    text = { Text("Daily Guide & Wisdom", fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    modifier = Modifier.testTag("ai_tab_daily_guide")
                )
            }
        }

        if (state.activeChatTab == 0) {
            SpiritualAiChatView(
                state = state,
                viewModel = viewModel,
                modifier = Modifier.weight(1f)
            )
        } else {
            AiReflectionsListView(
                state = state,
                viewModel = viewModel,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun AiReflectionsListView(
    state: DeenUiState,
    viewModel: DeenViewModel,
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    val moodChips = listOf(
        "Seeking Inner Peace",
        "Struggling with Focus in Salah",
        "Overcoming Anxiety & Stress",
        "Patience during Hardship",
        "Seeking Forgiveness & Hope",
        "Spiritual Mindset for Fasting",
        "Gratitude for Blessings"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Screen Header
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f, fill = false)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(GoldAccent),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "Deen AI",
                                tint = EmeraldPrimaryVariant,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Deen365 Spiritual Guide",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )
                            Text(
                                text = "Personalized reflections rooted in Quran & Sunnah",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(
                        onClick = { viewModel.openSettings() },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .testTag("ai_guidance_settings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // Personalized Daily Spiritual Guide (AI-Powered)
        item {
            val prayersToday = state.prayerRecord.completedCount
            PersonalizedAiSpiritualGuideCard(
                guidance = state.aiSpiritualGuidance,
                isLoading = state.isAiSpiritualGuideLoading,
                focusArea = state.aiGuideFocusArea,
                prayersCompletedToday = prayersToday,
                fastingStreakDays = state.fastingStreak,
                completedDeeds = state.completedPracticalDeeds,
                onSelectFocusArea = { viewModel.loadPersonalizedSpiritualGuidance(it) },
                onToggleDeed = { viewModel.togglePracticalDeed(it) },
                onRefresh = { viewModel.loadPersonalizedSpiritualGuidance(state.aiGuideFocusArea) }
            )
        }

        // 5 Prominent Category Filter Pills
        item {
            Text(
                text = "Guidance Categories",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(AiCategory.values()) { category ->
                    val isSelected = state.aiCategory == category
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setAiCategory(category) },
                        label = {
                            Text(
                                text = category.title,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = when (category) {
                                    AiCategory.DAILY_REFLECTION -> Icons.Default.MenuBook
                                    AiCategory.HADITH_INSIGHT -> Icons.Default.LibraryBooks
                                    AiCategory.DUA_EXPLORATION -> Icons.Default.Favorite
                                    AiCategory.NAMAZ_ADVICE -> Icons.Default.AccessibilityNew
                                    AiCategory.FASTING_GUIDE -> Icons.Default.NightsStay
                                    AiCategory.PRACTICE_QNA -> Icons.Default.School
                                },
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = Color.White,
                            selectedLeadingIconColor = GoldLight
                        ),
                        modifier = Modifier.testTag("ai_category_${category.name.lowercase()}")
                    )
                }
            }
        }

        // Mood & Situation Quick Chips
        item {
            Text(
                text = "What is on your heart today?",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(moodChips) { mood ->
                    SuggestionChip(
                        onClick = {
                            viewModel.setAiQuery(mood)
                            viewModel.requestAiGuidance(state.aiCategory, mood)
                        },
                        label = { Text(mood, style = MaterialTheme.typography.labelSmall) },
                        shape = RoundedCornerShape(16.dp),
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                }
            }
        }

        // Search / Prompt Input Bar
        item {
            OutlinedTextField(
                value = state.aiInputQuery,
                onValueChange = { viewModel.setAiQuery(it) },
                placeholder = {
                    Text(
                        "Ask about Quran, Hadith, Dua, Namaz, or Fasting...",
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Psychology,
                        contentDescription = "Ask",
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                trailingIcon = {
                    if (state.isLoadingAi) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    } else {
                        IconButton(
                            onClick = {
                                if (state.aiInputQuery.isNotBlank()) {
                                    keyboardController?.hide()
                                    viewModel.requestAiGuidance()
                                }
                            },
                            modifier = Modifier.testTag("ai_send_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Send",
                                tint = if (state.aiInputQuery.isNotBlank()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = {
                    if (state.aiInputQuery.isNotBlank()) {
                        keyboardController?.hide()
                        viewModel.requestAiGuidance()
                    }
                }),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("ai_prompt_input")
            )
        }

        // Active Structured Guidance Card
        item {
            if (state.isLoadingAi) {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        Text(
                            text = "Seeking wisdom from Quran & Sunnah...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                state.currentAiGuidance?.let { guidance ->
                    GuidanceStructuredCard(
                        guidance = guidance,
                        completedSteps = state.completedActionSteps,
                        onToggleStep = { viewModel.toggleActionStep(it) },
                        onSelectFollowUp = {
                            viewModel.setAiQuery(it)
                            viewModel.requestAiGuidance(state.aiCategory, it)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun GuidanceStructuredCard(
    guidance: AiGuidanceResult,
    completedSteps: Set<Int>,
    onToggleStep: (Int) -> Unit,
    onSelectFollowUp: (String) -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, GoldAccent.copy(alpha = 0.5f)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("ai_guidance_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header with Category & Reference Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = guidance.category.title.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Text(
                    text = guidance.reference,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // 1. Sacred Arabic Text Box
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent.copy(alpha = 0.35f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = guidance.arabicText,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Right,
                            lineHeight = 36.sp
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // 2. Clear English Translation
            Column {
                Text(
                    text = "TRANSLATION & MEANING",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = GoldAccent
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "\"${guidance.translation}\"",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
            }

            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

            // 3. Spiritual Insight & Contemplation
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = null,
                        tint = GoldAccent,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "SPIRITUAL CONTEMPLATION",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = guidance.spiritualInsight,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 22.sp
                )
            }

            // 4. Practical Action Steps Today (Interactive Checklist!)
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = EmeraldPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "PRACTICAL ACTION STEPS TODAY",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                guidance.practicalActionSteps.forEachIndexed { index, step ->
                    val isChecked = completedSteps.contains(index)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onToggleStep(index) }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = { onToggleStep(index) },
                            colors = CheckboxDefaults.colors(
                                checkedColor = MaterialTheme.colorScheme.primary,
                                checkmarkColor = GoldLight
                            ),
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = step,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = if (isChecked) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurface
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // 5. Recommended Dua
            if (guidance.recommendedDua.isNotBlank()) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = GoldAccent.copy(alpha = 0.12f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = null,
                                tint = GoldAccent,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "RECOMMENDED DUA",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = GoldAccent
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = guidance.recommendedDua,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // 6. Follow-up Prompts
            if (guidance.followUpPrompts.isNotEmpty()) {
                Column {
                    Text(
                        text = "EXPLORE FURTHER",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    guidance.followUpPrompts.forEach { prompt ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                                .clickable { onSelectFollowUp(prompt) }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = prompt,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.weight(1f)
                                )
                                Icon(
                                    imageVector = Icons.Default.ArrowForward,
                                    contentDescription = "Ask",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AiSubscriptionGateView(
    state: DeenUiState,
    viewModel: DeenViewModel,
    modifier: Modifier = Modifier
) {
    val userRole = state.userProfile?.role ?: "normal"
    val isLoggedIn = state.userProfile != null

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 24.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier.fillMaxWidth().testTag("ai_subscription_gate_card")
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(EmeraldPrimary.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Subscription Gate",
                            tint = GoldAccent,
                            modifier = Modifier.size(38.dp)
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Deen AI Spiritual Companion",
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = GoldAccent.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "SUBSCRIBER EXCLUSIVE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 1.sp,
                                    color = EmeraldPrimary
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Text(
                        text = "Access to personalized Gemini AI spiritual guidance, contextual Quran & Hadith reflections, and emotional wellness Duas is restricted to active subscribers.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )

                    Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                    // Feature highlights
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        SubscriptionFeatureRow("Unlimited contextual reflections with Quran & Hadith citations")
                        SubscriptionFeatureRow("Personalized emotional & hardship Dua recommendations")
                        SubscriptionFeatureRow("Custom school-of-thought Salah & Fasting advice")
                        SubscriptionFeatureRow("AI Habit & Spiritual Progress Analyzer")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Subscription Action
                    Button(
                        onClick = {
                            viewModel.upgradeToSubscriber("Premium Member")
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                        modifier = Modifier.fillMaxWidth().testTag("upgrade_subscription_button")
                    ) {
                        Icon(Icons.Default.Stars, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isLoggedIn) "Subscribe to Deen AI ($4.99/mo)" else "Get Subscriber Access",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }

                    OutlinedButton(
                        onClick = { viewModel.openUserAccountDialog() },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth().testTag("ai_gate_account_button")
                    ) {
                        Icon(Icons.Default.AccountCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (isLoggedIn) "Manage Account & Roles" else "Sign In with Existing Account")
                    }

                    Text(
                        text = "Current Account Role: ${userRole.replaceFirstChar { it.uppercase() }}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun SubscriptionFeatureRow(text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = EmeraldPrimary,
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
