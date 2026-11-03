package com.example.ui.screens.community

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.CommunityComment
import com.example.data.model.CommunityPost
import com.example.data.model.CommunityTopicCategory
import com.example.data.model.DuaRequestItem
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.viewmodel.DeenUiState
import com.example.ui.viewmodel.DeenViewModel
import com.example.ui.viewmodel.MainTab
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityScreen(
    state: DeenUiState,
    viewModel: DeenViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.setTab(MainTab.DASHBOARD)
    }

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Reflections", "Du'a Requests", "Etiquette & Moderation")

    val filteredPosts = remember(state.communityPosts, state.selectedCommunityCategory) {
        val cat = state.selectedCommunityCategory
        if (cat == null) state.communityPosts else state.communityPosts.filter { it.category == cat }
    }

    val isAdminOrMod = state.userProfile?.role == "admin" || state.isAdminLoggedIn

    Scaffold(
        modifier = modifier.fillMaxSize().testTag("community_screen"),
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(
                            onClick = { viewModel.setTab(MainTab.DASHBOARD) },
                            modifier = Modifier.size(40.dp).testTag("community_back_button")
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = "Ummah Community",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                )
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = EmeraldPrimary.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = "Deen365",
                                        color = EmeraldPrimary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Learn, reflect, and make sincere Du'a for each other",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(
                        onClick = { viewModel.openUserAccountDialog() },
                        modifier = Modifier.size(40.dp).testTag("community_profile_button")
                    ) {
                        Icon(
                            Icons.Default.AccountCircle,
                            contentDescription = "My Profile",
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = EmeraldPrimary,
                    modifier = Modifier.fillMaxWidth().testTag("community_tab_row")
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = {
                                Text(
                                    text = title,
                                    fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 13.sp
                                )
                            },
                            modifier = Modifier.testTag("community_tab_$index")
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            if (selectedTabIndex == 0) {
                FloatingActionButton(
                    onClick = { viewModel.openCreatePostDialog() },
                    containerColor = EmeraldPrimary,
                    contentColor = Color.White,
                    modifier = Modifier.testTag("create_post_fab")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Share Reflection")
                        Text("Share Reflection", fontWeight = FontWeight.Bold)
                    }
                }
            } else if (selectedTabIndex == 1) {
                FloatingActionButton(
                    onClick = { viewModel.openCreateDuaDialog() },
                    containerColor = GoldAccent,
                    contentColor = Color.Black,
                    modifier = Modifier.testTag("create_dua_fab")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("🤲", fontSize = 16.sp)
                        Text("Ask for Du'a", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (selectedTabIndex) {
                0 -> DiscussionsFeed(
                    posts = filteredPosts,
                    selectedCategory = state.selectedCommunityCategory,
                    onSelectCategory = { viewModel.filterCommunityCategory(it) },
                    onLike = { viewModel.toggleLikeCommunityPost(it) },
                    onComment = { viewModel.openPostComments(it) },
                    onReport = { viewModel.openReportPostDialog(it) },
                    isAdmin = isAdminOrMod,
                    onModerateDelete = { viewModel.moderateDeletePost(it) }
                )
                1 -> DuaRequestsFeed(
                    duaRequests = state.communityDuaRequests,
                    onSayAmeen = { viewModel.sayAmeenToDua(it) }
                )
                2 -> EtiquetteAndModerationView(
                    reports = state.communityReports,
                    flaggedPosts = state.communityPosts.filter { it.isFlagged || it.reportCount > 0 },
                    isAdmin = isAdminOrMod,
                    onHidePost = { viewModel.moderateHidePost(it) },
                    onDeletePost = { viewModel.moderateDeletePost(it) }
                )
            }
        }
    }

    // Create Reflection Dialog
    if (state.isCreatePostDialogOpen) {
        CreatePostDialog(
            authorName = state.userProfile?.displayName ?: "Fellow Believer",
            onDismiss = { viewModel.closeCreatePostDialog() },
            onSubmit = { title, content, category, ayahRef, hadithRef ->
                viewModel.submitCommunityPost(title, content, category, ayahRef, hadithRef)
            }
        )
    }

    // Create Du'a Request Dialog
    if (state.isCreateDuaDialogOpen) {
        CreateDuaRequestDialog(
            requesterName = state.userProfile?.displayName ?: "Fellow Believer",
            onDismiss = { viewModel.closeCreateDuaDialog() },
            onSubmit = { title, details ->
                viewModel.submitDuaRequest(title, details)
            }
        )
    }

    // Comments Sheet / Dialog
    state.activeCommentsPostId?.let { postId ->
        val post = state.communityPosts.firstOrNull { it.id == postId }
        val comments = state.communityComments[postId] ?: emptyList()
        CommentsDialog(
            post = post,
            comments = comments,
            currentUserName = state.userProfile?.displayName ?: "Fellow Believer",
            onDismiss = { viewModel.closePostComments() },
            onAddComment = { text -> viewModel.submitComment(postId, text) }
        )
    }

    // Report Post Dialog
    state.reportingPostId?.let { postId ->
        ReportContentDialog(
            postId = postId,
            onDismiss = { viewModel.closeReportPostDialog() },
            onSubmit = { reason -> viewModel.submitReportPost(postId, reason) }
        )
    }
}

@Composable
private fun DiscussionsFeed(
    posts: List<CommunityPost>,
    selectedCategory: CommunityTopicCategory?,
    onSelectCategory: (CommunityTopicCategory?) -> Unit,
    onLike: (String) -> Unit,
    onComment: (String) -> Unit,
    onReport: (String) -> Unit,
    isAdmin: Boolean,
    onModerateDelete: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 88.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Category Filter Chips
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth().testTag("community_category_chips")
            ) {
                item {
                    FilterChip(
                        selected = selectedCategory == null,
                        onClick = { onSelectCategory(null) },
                        label = { Text("All Topics", fontSize = 12.sp) }
                    )
                }
                items(CommunityTopicCategory.values()) { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { onSelectCategory(cat) },
                        label = { Text("${cat.iconEmoji} ${cat.displayName}", fontSize = 12.sp) }
                    )
                }
            }
        }

        if (posts.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth().padding(top = 32.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("🌱", fontSize = 36.sp)
                        Text(
                            text = "No reflections in this topic yet",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Be the first to share an inspiring Quranic reflection or Hadith lesson!",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(posts, key = { it.id }) { post ->
                PostCard(
                    post = post,
                    onLike = { onLike(post.id) },
                    onComment = { onComment(post.id) },
                    onReport = { onReport(post.id) },
                    isAdmin = isAdmin,
                    onDelete = { onModerateDelete(post.id) }
                )
            }
        }
    }
}

@Composable
private fun PostCard(
    post: CommunityPost,
    onLike: () -> Unit,
    onComment: () -> Unit,
    onReport: () -> Unit,
    isAdmin: Boolean,
    onDelete: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("MMM d, h:mm a", Locale.getDefault()) }
    val formattedDate = remember(post.timestamp) { dateFormat.format(Date(post.timestamp)) }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth().testTag("post_card_${post.id}")
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            // Header: Author info & category
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
                        modifier = Modifier.size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = post.authorName.take(1).uppercase(),
                                fontWeight = FontWeight.Bold,
                                color = EmeraldPrimary,
                                fontSize = 16.sp
                            )
                        }
                    }
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = post.authorName,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (post.authorRole != "member") {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = if (post.authorRole == "admin") GoldAccent else EmeraldPrimary.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = post.authorRole.uppercase(),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (post.authorRole == "admin") Color.Black else EmeraldPrimary,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                        Text(
                            text = formattedDate,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = "${post.category.iconEmoji} ${post.category.displayName}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Post Title & Content
            Text(
                text = post.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = post.content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
                lineHeight = 20.sp
            )

            // Optional Citations
            if (!post.ayahReference.isNullOrBlank() || !post.hadithReference.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    post.ayahReference?.let { ayah ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = EmeraldPrimary.copy(alpha = 0.1f)
                        ) {
                            Text(
                                text = "📖 $ayah",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = EmeraldPrimary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                    post.hadithReference?.let { hadith ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = GoldAccent.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "📜 $hadith",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF8C6D00),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            if (post.isFlagged) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "⚠️ Flagged by community for moderator review (${post.reportCount} reports)",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
            Spacer(modifier = Modifier.height(6.dp))

            // Action Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    TextButton(
                        onClick = onLike,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("post_like_${post.id}")
                    ) {
                        Icon(
                            imageVector = if (post.likesCount > 0) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Like",
                            tint = if (post.likesCount > 0) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${post.likesCount}",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    TextButton(
                        onClick = onComment,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("post_comments_${post.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ChatBubbleOutline,
                            contentDescription = "Comment",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${post.commentsCount}",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(
                        onClick = onReport,
                        modifier = Modifier.size(36.dp).testTag("post_report_${post.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Flag,
                            contentDescription = "Report",
                            tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    if (isAdmin) {
                        IconButton(
                            onClick = onDelete,
                            modifier = Modifier.size(36.dp).testTag("post_admin_delete_${post.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = "Delete",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DuaRequestsFeed(
    duaRequests: List<DuaRequestItem>,
    onSayAmeen: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 88.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = GoldAccent.copy(alpha = 0.15f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🕊️", fontSize = 24.sp)
                    Column {
                        Text(
                            text = "The Supplication for Your Brother in His Absence",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "The Prophet ﷺ said: 'The supplication of a Muslim for his brother in his absence is answered. At his head is an angel who says: Ameen, and for you the same.' (Sahih Muslim)",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        items(duaRequests, key = { it.id }) { req ->
            DuaRequestCard(
                request = req,
                onSayAmeen = { onSayAmeen(req.id) }
            )
        }
    }
}

@Composable
private fun DuaRequestCard(
    request: DuaRequestItem,
    onSayAmeen: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("MMM d, yyyy", Locale.getDefault()) }
    val formattedDate = remember(request.timestamp) { dateFormat.format(Date(request.timestamp)) }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth().testTag("dua_card_${request.id}")
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("🤲", fontSize = 18.sp)
                    Text(
                        text = request.requesterName,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = request.requestTitle,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = request.requestDetails,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Ameen Button
            Button(
                onClick = onSayAmeen,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                modifier = Modifier.fillMaxWidth().testTag("say_ameen_button_${request.id}")
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🤲", fontSize = 16.sp)
                    Text(
                        text = "Said Ameen & Made Du'a (${request.ameenCount})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun EtiquetteAndModerationView(
    reports: List<com.example.data.model.ContentReportItem>,
    flaggedPosts: List<CommunityPost>,
    isAdmin: Boolean,
    onHidePost: (String) -> Unit,
    onDeletePost: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 88.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = EmeraldPrimary)
                        Text(
                            text = "Deen365 Community Code of Conduct",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    Text(
                        text = "Our community is dedicated to mutual spiritual elevation in accordance with Quran and Sunnah:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    EtiquetteRuleItem("1. Adab & Kindness", "Address all brothers and sisters with gentleness and avoid sectarian insults or ridicule.")
                    EtiquetteRuleItem("2. Verified Knowledge", "When citing verses or hadiths, cite their chapter/number or scholar reference; never invent false attributions.")
                    EtiquetteRuleItem("3. Mutual Privacy", "Protect the personal secrets and private struggles shared in Du'a requests.")
                    EtiquetteRuleItem("4. Active Moderation", "Any post accumulating reports is automatically reviewed by administrators to maintain an uplifting environment.")
                }
            }
        }

        if (isAdmin) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Moderation Queue (${flaggedPosts.size} flagged)",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            if (flaggedPosts.isEmpty()) {
                item {
                    Text(
                        text = "All clear! No pending reported content in the moderation queue.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                items(flaggedPosts) { post ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Flagged: ${post.title}",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.error
                            )
                            Text(text = post.content, style = MaterialTheme.typography.bodySmall, maxLines = 2)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedButton(
                                    onClick = { onHidePost(post.id) },
                                    modifier = Modifier.testTag("mod_hide_${post.id}")
                                ) {
                                    Text("Hide Post", fontSize = 11.sp)
                                }
                                Button(
                                    onClick = { onDeletePost(post.id) },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                                    modifier = Modifier.testTag("mod_delete_${post.id}")
                                ) {
                                    Text("Delete Permanently", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EtiquetteRuleItem(title: String, desc: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(18.dp))
        Column {
            Text(title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
            Text(desc, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun CreatePostDialog(
    authorName: String,
    onDismiss: () -> Unit,
    onSubmit: (title: String, content: String, category: CommunityTopicCategory, ayahRef: String?, hadithRef: String?) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(CommunityTopicCategory.SPIRITUAL_GROWTH) }
    var ayahRef by remember { mutableStateOf("") }
    var hadithRef by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth().testTag("create_post_dialog")
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Share Spiritual Reflection",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Reflection Title") },
                    placeholder = { Text("e.g. Lesson on Sabr during hardship") },
                    modifier = Modifier.fillMaxWidth().testTag("post_title_input"),
                    singleLine = true
                )

                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Your Reflection / Learning") },
                    placeholder = { Text("Share what touched your heart...") },
                    modifier = Modifier.fillMaxWidth().height(120.dp).testTag("post_content_input"),
                    maxLines = 5
                )

                OutlinedTextField(
                    value = ayahRef,
                    onValueChange = { ayahRef = it },
                    label = { Text("Ayah Reference (Optional)") },
                    placeholder = { Text("e.g. Surah Al-Baqarah 2:153") },
                    modifier = Modifier.fillMaxWidth().testTag("post_ayah_ref_input"),
                    singleLine = true
                )

                OutlinedTextField(
                    value = hadithRef,
                    onValueChange = { hadithRef = it },
                    label = { Text("Hadith Citation (Optional)") },
                    placeholder = { Text("e.g. Sahih Muslim #2999") },
                    modifier = Modifier.fillMaxWidth().testTag("post_hadith_ref_input"),
                    singleLine = true
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (title.isNotBlank() && content.isNotBlank()) {
                                onSubmit(
                                    title.trim(),
                                    content.trim(),
                                    category,
                                    ayahRef.trim().ifBlank { null },
                                    hadithRef.trim().ifBlank { null }
                                )
                            }
                        },
                        enabled = title.isNotBlank() && content.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                        modifier = Modifier.testTag("submit_post_button")
                    ) {
                        Text("Publish", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun CreateDuaRequestDialog(
    requesterName: String,
    onDismiss: () -> Unit,
    onSubmit: (title: String, details: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var details by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth().testTag("create_dua_dialog")
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Request Du'a from the Ummah",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = EmeraldPrimary
                )

                Text(
                    text = "Whatever difficulty, illness, or exam you are facing, let your brothers and sisters join you in prayer.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Du'a Topic") },
                    placeholder = { Text("e.g. Du'a for my mother's surgery") },
                    modifier = Modifier.fillMaxWidth().testTag("dua_title_input"),
                    singleLine = true
                )

                OutlinedTextField(
                    value = details,
                    onValueChange = { details = it },
                    label = { Text("Details & Specific Prayers") },
                    placeholder = { Text("Share whatever you are comfortable asking for...") },
                    modifier = Modifier.fillMaxWidth().height(110.dp).testTag("dua_details_input"),
                    maxLines = 4
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (title.isNotBlank() && details.isNotBlank()) {
                                onSubmit(title.trim(), details.trim())
                            }
                        },
                        enabled = title.isNotBlank() && details.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                        modifier = Modifier.testTag("submit_dua_button")
                    ) {
                        Text("Submit Request", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun CommentsDialog(
    post: CommunityPost?,
    comments: List<CommunityComment>,
    currentUserName: String,
    onDismiss: () -> Unit,
    onAddComment: (String) -> Unit
) {
    var newCommentText by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.8f).testTag("comments_dialog")
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Discussion & Comments (${comments.size})",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                post?.let {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(text = it.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(
                                text = it.content,
                                style = MaterialTheme.typography.labelSmall,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                LazyColumn(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    if (comments.isEmpty()) {
                        item {
                            Text(
                                text = "No comments yet. Write the first uplifting reply!",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(vertical = 24.dp)
                            )
                        }
                    } else {
                        items(comments) { comment ->
                            Card(
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = comment.authorName,
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                        color = EmeraldPrimary
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = comment.text,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    }
                }

                // Input row
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = newCommentText,
                        onValueChange = { newCommentText = it },
                        placeholder = { Text("Write a reply with Adab...", fontSize = 12.sp) },
                        modifier = Modifier.weight(1f).testTag("comment_input"),
                        maxLines = 3
                    )
                    IconButton(
                        onClick = {
                            if (newCommentText.isNotBlank()) {
                                onAddComment(newCommentText.trim())
                                newCommentText = ""
                            }
                        },
                        enabled = newCommentText.isNotBlank(),
                        modifier = Modifier.testTag("send_comment_button")
                    ) {
                        Icon(
                            Icons.Default.Send,
                            contentDescription = "Send",
                            tint = if (newCommentText.isNotBlank()) EmeraldPrimary else MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ReportContentDialog(
    postId: String,
    onDismiss: () -> Unit,
    onSubmit: (String) -> Unit
) {
    var selectedReason by remember { mutableStateOf("Inappropriate / Disrespectful") }
    val reasons = listOf(
        "Inappropriate / Disrespectful",
        "False Religious Attribution / Unverified Hadith",
        "Spam / Commercial Promotion",
        "Sectarian Attacks / Hate Speech"
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth().testTag("report_dialog")
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Report Reflection",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.error
                )
                Text(
                    text = "Help maintain a pure, spiritually safe sanctuary for all believers. Why are you reporting this post?",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                reasons.forEach { reason ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedReason = reason }
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        RadioButton(
                            selected = selectedReason == reason,
                            onClick = { selectedReason = reason }
                        )
                        Text(text = reason, fontSize = 13.sp)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { onSubmit(selectedReason) },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        modifier = Modifier.testTag("submit_report_button")
                    ) {
                        Text("Submit Report", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
