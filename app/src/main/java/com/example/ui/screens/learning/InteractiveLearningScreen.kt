package com.example.ui.screens.learning

import androidx.activity.compose.BackHandler
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.seed.InteractiveLearningModulesData
import com.example.data.seed.LearningLesson
import com.example.data.seed.LearningTrack
import com.example.data.seed.QuizQuestion
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.viewmodel.DeenUiState
import com.example.ui.viewmodel.DeenViewModel
import com.example.ui.viewmodel.MainTab

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InteractiveLearningScreen(
    state: DeenUiState,
    viewModel: DeenViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        if (state.activeLearningLessonId != null) {
            viewModel.selectLearningLesson(null)
        } else if (state.selectedLearningTrackId != null) {
            viewModel.selectLearningTrack(null)
        } else {
            viewModel.setTab(MainTab.DASHBOARD)
        }
    }

    val selectedTrack = remember(state.learningTracks, state.selectedLearningTrackId) {
        state.learningTracks.firstOrNull { it.id == state.selectedLearningTrackId }
    }

    val activeLesson = remember(selectedTrack, state.activeLearningLessonId) {
        selectedTrack?.lessons?.firstOrNull { it.id == state.activeLearningLessonId }
    }

    Scaffold(
        modifier = modifier.fillMaxSize().testTag("interactive_learning_screen"),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = if (activeLesson != null) activeLesson.title else selectedTrack?.title ?: "Learning Academy",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                maxLines = 1
                            )
                        }
                        Text(
                            text = if (activeLesson != null) "Lesson ${activeLesson.lessonNumber} • Interactive Module" else "Quran, Hadith, Duas, Namaz & Fasting",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (activeLesson != null) {
                                viewModel.selectLearningLesson(null)
                            } else if (selectedTrack != null) {
                                viewModel.selectLearningTrack(null)
                            } else {
                                viewModel.setTab(MainTab.DASHBOARD)
                            }
                        },
                        modifier = Modifier.testTag("learning_back_button")
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = GoldAccent.copy(alpha = 0.2f),
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text("🎓", fontSize = 12.sp)
                            Text(
                                text = "${state.completedLessonIds.size} Completed",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when {
                activeLesson != null -> {
                    LessonDetailView(
                        lesson = activeLesson,
                        isCompleted = state.completedLessonIds.contains(activeLesson.id),
                        onStartQuiz = { viewModel.startQuiz(activeLesson.id) }
                    )
                }
                selectedTrack != null -> {
                    TrackLessonsListView(
                        track = selectedTrack,
                        completedLessonIds = state.completedLessonIds,
                        onSelectLesson = { viewModel.selectLearningLesson(it) }
                    )
                }
                else -> {
                    TracksOverviewList(
                        tracks = state.learningTracks,
                        completedLessonIds = state.completedLessonIds,
                        onSelectTrack = { viewModel.selectLearningTrack(it) }
                    )
                }
            }
        }
    }

    // Interactive Quiz Dialog
    state.activeQuizLessonId?.let { lessonId ->
        val quizLesson = remember(lessonId) {
            InteractiveLearningModulesData.ALL_TRACKS
                .flatMap { it.lessons }
                .firstOrNull { it.id == lessonId }
        }

        if (quizLesson != null) {
            QuizKnowledgeDialog(
                lesson = quizLesson,
                selectedAnswers = state.activeQuizAnswers,
                isSubmitted = state.isQuizSubmitted,
                onSelectAnswer = { qId, optIndex -> viewModel.selectQuizAnswer(qId, optIndex) },
                onSubmit = { viewModel.submitQuiz() },
                onReset = { viewModel.resetQuiz() },
                onDismiss = { viewModel.closeQuizDialog() }
            )
        }
    }
}

@Composable
private fun TracksOverviewList(
    tracks: List<LearningTrack>,
    completedLessonIds: Set<String>,
    onSelectTrack: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = EmeraldPrimary.copy(alpha = 0.12f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🏛️", fontSize = 28.sp)
                    Column {
                        Text(
                            text = "Authentic Islamic Knowledge Academy",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Deepen your understanding of Quranic sciences, Hadith methodology, Duas, and Fiqh of Salah & Sawm through structured lessons and instant-feedback quizzes.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        items(tracks, key = { it.id }) { track ->
            val completedInTrack = track.lessons.count { completedLessonIds.contains(it.id) }
            val progressPercent = if (track.lessons.isNotEmpty()) (completedInTrack.toFloat() / track.lessons.size) else 0f

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectTrack(track.id) }
                    .testTag("learning_track_${track.id}")
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
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
                                modifier = Modifier.size(44.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(track.categoryIcon, fontSize = 20.sp)
                                }
                            }
                            Column {
                                Text(
                                    text = track.title,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = track.arabicTitle,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = EmeraldPrimary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Icon(
                            Icons.Default.ChevronRight,
                            contentDescription = "Open Track",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = track.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LinearProgressIndicator(
                            progress = { progressPercent },
                            modifier = Modifier
                                .weight(1f)
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = EmeraldPrimary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "$completedInTrack/${track.lessons.size} Lessons",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = EmeraldPrimary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TrackLessonsListView(
    track: LearningTrack,
    completedLessonIds: Set<String>,
    onSelectLesson: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = track.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = track.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        items(track.lessons, key = { it.id }) { lesson ->
            val isDone = completedLessonIds.contains(lesson.id)

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isDone) EmeraldPrimary.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
                ),
                border = if (isDone) androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.5f)) else null,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectLesson(lesson.id) }
                    .testTag("learning_lesson_${lesson.id}")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = if (isDone) EmeraldPrimary else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                if (isDone) {
                                    Icon(Icons.Default.Check, contentDescription = "Completed", tint = Color.White, modifier = Modifier.size(20.dp))
                                } else {
                                    Text(
                                        text = "${lesson.lessonNumber}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }

                        Column {
                            Text(
                                text = lesson.title,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = lesson.summary,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 2
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = "⏱️ ${lesson.estimatedMinutes} min read",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.outline
                                )
                                Text(
                                    text = "• 📝 ${lesson.quizQuestions.size} quiz questions",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }
                        }
                    }

                    Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = "Start Lesson",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun LessonDetailView(
    lesson: LearningLesson,
    isCompleted: Boolean,
    onStartQuiz: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = EmeraldPrimary.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "LESSON ${lesson.lessonNumber}",
                                color = EmeraldPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        if (isCompleted) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = EmeraldPrimary
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                                    Text("Mastered", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    Text(
                        text = lesson.title,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = lesson.summary,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Key Quote Banner
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = GoldAccent.copy(alpha = 0.12f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(14.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("📜", fontSize = 20.sp)
                    Text(
                        text = lesson.keyAyahOrHadithQuote,
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // Lesson Content Body
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Lesson Text & Explanation",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = lesson.contentMarkdown,
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 22.sp
                    )
                }
            }
        }

        // Practical Example Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = EmeraldPrimary.copy(alpha = 0.08f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("💡", fontSize = 18.sp)
                        Text(
                            text = "Practical Life Application",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = EmeraldPrimary
                        )
                    }
                    Text(
                        text = lesson.practicalExample,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // Quiz Action Button
        item {
            Button(
                onClick = onStartQuiz,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                modifier = Modifier.fillMaxWidth().testTag("start_quiz_button")
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Quiz, contentDescription = null)
                    Text(
                        text = if (isCompleted) "Retake Knowledge Quiz (${lesson.quizQuestions.size} Qs)" else "Take Knowledge Quiz & Earn Mastery",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun QuizKnowledgeDialog(
    lesson: LearningLesson,
    selectedAnswers: Map<String, Int>,
    isSubmitted: Boolean,
    onSelectAnswer: (String, Int) -> Unit,
    onSubmit: () -> Unit,
    onReset: () -> Unit,
    onDismiss: () -> Unit
) {
    val score = remember(isSubmitted, selectedAnswers, lesson.quizQuestions) {
        if (!isSubmitted) 0
        else lesson.quizQuestions.count { q -> selectedAnswers[q.id] == q.correctOptionIndex }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.9f).testTag("quiz_dialog")
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Knowledge Assessment",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = lesson.title,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                if (isSubmitted) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (score == lesson.quizQuestions.size) EmeraldPrimary.copy(alpha = 0.15f) else GoldAccent.copy(alpha = 0.15f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(if (score == lesson.quizQuestions.size) "🏆" else "📖", fontSize = 24.sp)
                            Column {
                                Text(
                                    text = if (score == lesson.quizQuestions.size) "Masha'Allah! Perfect Score: $score/${lesson.quizQuestions.size}" else "Score: $score/${lesson.quizQuestions.size}",
                                    fontWeight = FontWeight.Bold,
                                    color = if (score == lesson.quizQuestions.size) EmeraldPrimary else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (score == lesson.quizQuestions.size) "You have successfully mastered this lesson!" else "Review the detailed explanations below to refine your knowledge.",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(lesson.quizQuestions, key = { it.id }) { q ->
                        val selectedOpt = selectedAnswers[q.id]

                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = q.questionText,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                q.options.forEachIndexed { optIndex, optionText ->
                                    val isSelected = selectedOpt == optIndex
                                    val isCorrect = optIndex == q.correctOptionIndex

                                    val bgColor = when {
                                        !isSubmitted && isSelected -> EmeraldPrimary.copy(alpha = 0.15f)
                                        isSubmitted && isCorrect -> Color(0xFF1B5E20).copy(alpha = 0.15f)
                                        isSubmitted && isSelected && !isCorrect -> Color(0xFFB71C1C).copy(alpha = 0.15f)
                                        else -> MaterialTheme.colorScheme.surface
                                    }

                                    val borderColor = when {
                                        !isSubmitted && isSelected -> EmeraldPrimary
                                        isSubmitted && isCorrect -> Color(0xFF2E7D32)
                                        isSubmitted && isSelected && !isCorrect -> Color(0xFFC62828)
                                        else -> Color.Transparent
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = bgColor,
                                        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable(enabled = !isSubmitted) {
                                                onSelectAnswer(q.id, optIndex)
                                            }
                                            .testTag("quiz_option_${q.id}_$optIndex")
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(
                                                modifier = Modifier.weight(1f),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                RadioButton(
                                                    selected = isSelected,
                                                    onClick = { if (!isSubmitted) onSelectAnswer(q.id, optIndex) },
                                                    enabled = !isSubmitted
                                                )
                                                Text(text = optionText, fontSize = 13.sp)
                                            }

                                            if (isSubmitted) {
                                                if (isCorrect) {
                                                    Icon(Icons.Default.CheckCircle, contentDescription = "Correct", tint = Color(0xFF2E7D32), modifier = Modifier.size(18.dp))
                                                } else if (isSelected) {
                                                    Icon(Icons.Default.Cancel, contentDescription = "Incorrect", tint = Color(0xFFC62828), modifier = Modifier.size(18.dp))
                                                }
                                            }
                                        }
                                    }
                                }

                                if (isSubmitted) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = EmeraldPrimary.copy(alpha = 0.08f),
                                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                                    ) {
                                        Column(modifier = Modifier.padding(10.dp)) {
                                            Text(
                                                text = "Scholarly Explanation:",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp,
                                                color = EmeraldPrimary
                                            )
                                            Text(
                                                text = q.explanation,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Bottom Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isSubmitted) {
                        OutlinedButton(
                            onClick = onReset,
                            modifier = Modifier.testTag("quiz_retry_button")
                        ) {
                            Text("Try Again")
                        }
                        Button(
                            onClick = onDismiss,
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                            modifier = Modifier.testTag("quiz_finish_button")
                        ) {
                            Text("Done", fontWeight = FontWeight.Bold)
                        }
                    } else {
                        TextButton(onClick = onDismiss) {
                            Text("Cancel")
                        }
                        val allAnswered = lesson.quizQuestions.all { selectedAnswers.containsKey(it.id) }
                        Button(
                            onClick = onSubmit,
                            enabled = allAnswered,
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                            modifier = Modifier.testTag("quiz_submit_button")
                        ) {
                            Text("Submit Answers", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
