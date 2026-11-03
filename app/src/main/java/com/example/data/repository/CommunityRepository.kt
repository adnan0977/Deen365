package com.example.data.repository

import android.util.Log
import com.example.data.model.CommunityComment
import com.example.data.model.CommunityPost
import com.example.data.model.CommunityTopicCategory
import com.example.data.model.ContentReportItem
import com.example.data.model.DuaRequestItem
import com.example.data.seed.CommunitySeedData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class CommunityRepository(
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {
    private val TAG = "CommunityRepository"
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    private val _posts = MutableStateFlow<List<CommunityPost>>(CommunitySeedData.INITIAL_POSTS)
    val posts: StateFlow<List<CommunityPost>> = _posts.asStateFlow()

    private val _duaRequests = MutableStateFlow<List<DuaRequestItem>>(CommunitySeedData.INITIAL_DUA_REQUESTS)
    val duaRequests: StateFlow<List<DuaRequestItem>> = _duaRequests.asStateFlow()

    private val _comments = MutableStateFlow<Map<String, List<CommunityComment>>>(CommunitySeedData.INITIAL_COMMENTS)
    val comments: StateFlow<Map<String, List<CommunityComment>>> = _comments.asStateFlow()

    private val _reports = MutableStateFlow<List<ContentReportItem>>(emptyList())
    val reports: StateFlow<List<ContentReportItem>> = _reports.asStateFlow()

    init {
        fetchCommunityData()
    }

    fun fetchCommunityData() {
        scope.launch {
            try {
                // Try fetching remote posts from Firestore
                val snapshot = firestore.collection("community_posts")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .limit(50)
                    .get()
                    .await()

                val remotePosts = snapshot.documents.mapNotNull { doc ->
                    try {
                        val categoryStr = doc.getString("category") ?: CommunityTopicCategory.SPIRITUAL_GROWTH.name
                        val category = try {
                            CommunityTopicCategory.valueOf(categoryStr)
                        } catch (_: Exception) {
                            CommunityTopicCategory.SPIRITUAL_GROWTH
                        }
                        CommunityPost(
                            id = doc.id,
                            authorId = doc.getString("authorId") ?: "",
                            authorName = doc.getString("authorName") ?: "Believer",
                            authorRole = doc.getString("authorRole") ?: "member",
                            category = category,
                            title = doc.getString("title") ?: "",
                            content = doc.getString("content") ?: "",
                            ayahReference = doc.getString("ayahReference"),
                            hadithReference = doc.getString("hadithReference"),
                            likesCount = (doc.getLong("likesCount") ?: 0L).toInt(),
                            commentsCount = (doc.getLong("commentsCount") ?: 0L).toInt(),
                            timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis(),
                            isPinned = doc.getBoolean("isPinned") ?: false,
                            isFlagged = doc.getBoolean("isFlagged") ?: false,
                            reportCount = (doc.getLong("reportCount") ?: 0L).toInt()
                        )
                    } catch (_: Exception) {
                        null
                    }
                }

                if (remotePosts.isNotEmpty()) {
                    val combined = (remotePosts + CommunitySeedData.INITIAL_POSTS).distinctBy { it.id }
                    _posts.value = combined
                }
            } catch (e: Exception) {
                Log.w(TAG, "Firestore community posts query fallback: ${e.message}")
            }

            try {
                // Try fetching remote du'a requests
                val duaSnap = firestore.collection("dua_requests")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .limit(30)
                    .get()
                    .await()

                val remoteDuas = duaSnap.documents.mapNotNull { doc ->
                    try {
                        DuaRequestItem(
                            id = doc.id,
                            requesterId = doc.getString("requesterId") ?: "",
                            requesterName = doc.getString("requesterName") ?: "Sister/Brother in Islam",
                            requestTitle = doc.getString("requestTitle") ?: "",
                            requestDetails = doc.getString("requestDetails") ?: "",
                            ameenCount = (doc.getLong("ameenCount") ?: 0L).toInt(),
                            timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis()
                        )
                    } catch (_: Exception) {
                        null
                    }
                }

                if (remoteDuas.isNotEmpty()) {
                    val combinedDuas = (remoteDuas + CommunitySeedData.INITIAL_DUA_REQUESTS).distinctBy { it.id }
                    _duaRequests.value = combinedDuas
                }
            } catch (e: Exception) {
                Log.w(TAG, "Firestore dua requests query fallback: ${e.message}")
            }
        }
    }

    suspend fun createPost(
        title: String,
        content: String,
        category: CommunityTopicCategory,
        authorName: String,
        authorRole: String = "member",
        ayahRef: String? = null,
        hadithRef: String? = null
    ): Result<CommunityPost> {
        val currentUserId = auth.currentUser?.uid ?: "user_local_${System.currentTimeMillis()}"
        val newPost = CommunityPost(
            id = "post_${System.currentTimeMillis()}",
            authorId = currentUserId,
            authorName = authorName.ifBlank { "Believer" },
            authorRole = authorRole,
            category = category,
            title = title,
            content = content,
            ayahReference = ayahRef?.ifBlank { null },
            hadithReference = hadithRef?.ifBlank { null },
            likesCount = 1,
            commentsCount = 0,
            timestamp = System.currentTimeMillis()
        )

        _posts.value = listOf(newPost) + _posts.value

        try {
            if (auth.currentUser != null) {
                val postMap = hashMapOf(
                    "authorId" to newPost.authorId,
                    "authorName" to newPost.authorName,
                    "authorRole" to newPost.authorRole,
                    "category" to newPost.category.name,
                    "title" to newPost.title,
                    "content" to newPost.content,
                    "ayahReference" to newPost.ayahReference,
                    "hadithReference" to newPost.hadithReference,
                    "likesCount" to newPost.likesCount,
                    "commentsCount" to newPost.commentsCount,
                    "timestamp" to newPost.timestamp,
                    "isPinned" to false,
                    "isFlagged" to false,
                    "reportCount" to 0
                )
                firestore.collection("community_posts").document(newPost.id).set(postMap).await()
            }
        } catch (e: Exception) {
            Log.w(TAG, "Offline fallback for createPost: ${e.message}")
        }

        return Result.success(newPost)
    }

    fun toggleLikePost(postId: String) {
        val currentUserId = auth.currentUser?.uid ?: "guest"
        _posts.value = _posts.value.map { post ->
            if (post.id == postId) {
                val isLiked = post.likedByUsers.contains(currentUserId)
                val newLikedBy = if (isLiked) post.likedByUsers - currentUserId else post.likedByUsers + currentUserId
                val newCount = if (isLiked) (post.likesCount - 1).coerceAtLeast(0) else post.likesCount + 1
                post.copy(likesCount = newCount, likedByUsers = newLikedBy)
            } else post
        }
    }

    suspend fun addComment(postId: String, text: String, authorName: String): Result<CommunityComment> {
        val currentUserId = auth.currentUser?.uid ?: "user_${System.currentTimeMillis()}"
        val newComment = CommunityComment(
            id = "comment_${System.currentTimeMillis()}",
            postId = postId,
            authorId = currentUserId,
            authorName = authorName.ifBlank { "Fellow Believer" },
            text = text,
            timestamp = System.currentTimeMillis()
        )

        val existing = _comments.value[postId] ?: emptyList()
        val updatedMap = _comments.value.toMutableMap()
        updatedMap[postId] = existing + newComment
        _comments.value = updatedMap

        // Increment commentsCount on the post
        _posts.value = _posts.value.map { post ->
            if (post.id == postId) post.copy(commentsCount = post.commentsCount + 1) else post
        }

        return Result.success(newComment)
    }

    suspend fun createDuaRequest(title: String, details: String, requesterName: String): Result<DuaRequestItem> {
        val currentUserId = auth.currentUser?.uid ?: "user_local_${System.currentTimeMillis()}"
        val newReq = DuaRequestItem(
            id = "dua_${System.currentTimeMillis()}",
            requesterId = currentUserId,
            requesterName = requesterName.ifBlank { "Sister/Brother in Islam" },
            requestTitle = title,
            requestDetails = details,
            ameenCount = 1,
            timestamp = System.currentTimeMillis()
        )

        _duaRequests.value = listOf(newReq) + _duaRequests.value

        try {
            if (auth.currentUser != null) {
                val map = hashMapOf(
                    "requesterId" to newReq.requesterId,
                    "requesterName" to newReq.requesterName,
                    "requestTitle" to newReq.requestTitle,
                    "requestDetails" to newReq.requestDetails,
                    "ameenCount" to newReq.ameenCount,
                    "timestamp" to newReq.timestamp
                )
                firestore.collection("dua_requests").document(newReq.id).set(map).await()
            }
        } catch (_: Exception) {}

        return Result.success(newReq)
    }

    fun sayAmeenToDua(requestId: String) {
        val currentUserId = auth.currentUser?.uid ?: "guest"
        _duaRequests.value = _duaRequests.value.map { req ->
            if (req.id == requestId) {
                val alreadyAmeen = req.ameenUserIds.contains(currentUserId)
                val newAmeenList = if (alreadyAmeen) req.ameenUserIds - currentUserId else req.ameenUserIds + currentUserId
                val newCount = if (alreadyAmeen) (req.ameenCount - 1).coerceAtLeast(0) else req.ameenCount + 1
                req.copy(ameenCount = newCount, ameenUserIds = newAmeenList)
            } else req
        }
    }

    fun reportPost(postId: String, reason: String) {
        val currentUserId = auth.currentUser?.uid ?: "guest"
        val report = ContentReportItem(
            reportId = "report_${System.currentTimeMillis()}",
            contentId = postId,
            contentType = "post",
            reportedByUserId = currentUserId,
            reason = reason,
            timestamp = System.currentTimeMillis(),
            status = "pending"
        )
        _reports.value = listOf(report) + _reports.value

        // Increase report count on post; if reports >= 3, automatically flag it
        _posts.value = _posts.value.map { post ->
            if (post.id == postId) {
                val newCount = post.reportCount + 1
                post.copy(reportCount = newCount, isFlagged = newCount >= 3)
            } else post
        }
    }

    fun moderateHidePost(postId: String) {
        _posts.value = _posts.value.map { post ->
            if (post.id == postId) post.copy(isFlagged = true) else post
        }
    }

    fun moderateDeletePost(postId: String) {
        _posts.value = _posts.value.filter { it.id != postId }
    }
}
