package com.example.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.example.domain.model.SpiritualGoals
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class UserProfile(
    val uid: String = "",
    val email: String = "",
    val displayName: String = "",
    val role: String = "normal", // "normal", "subscriber", "admin"
    val subscriptionPlan: String = "Free",
    val createdAt: Long = System.currentTimeMillis(),
    val avatarId: String = "avatar_mosque",
    val spiritualIntentionBio: String = "Seeking closeness to Allah through continuous learning and worship",
    val spiritualGoals: SpiritualGoals = SpiritualGoals(),
    val unlockedBadgeIds: List<String> = emptyList()
)

data class SubscriptionPlanItem(
    val id: String = "",
    val name: String = "",
    val price: String = "",
    val billingCycle: String = "Monthly",
    val description: String = "",
    val features: List<String> = emptyList(),
    val isPopular: Boolean = false,
    val isActive: Boolean = true
)

class UserRepository(
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {
    private val TAG = "UserRepository"
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile.asStateFlow()

    private val _subscriptionPlans = MutableStateFlow<List<SubscriptionPlanItem>>(getDefaultPlans())
    val subscriptionPlans: StateFlow<List<SubscriptionPlanItem>> = _subscriptionPlans.asStateFlow()

    init {
        try {
            auth.addAuthStateListener { firebaseAuth ->
                val user = firebaseAuth.currentUser
                if (user != null) {
                    fetchOrInitUserProfile(user)
                } else {
                    _userProfile.value = null
                }
            }
            fetchSubscriptionPlans()
        } catch (e: Throwable) {
            Log.w(TAG, "Firebase services not initialized: ${e.message}")
        }
    }

    fun getCurrentUser(): FirebaseUser? {
        return try {
            auth.currentUser
        } catch (e: Throwable) {
            null
        }
    }

    fun isGuest(): Boolean = getCurrentUser() == null

    fun isSubscriberOrAdmin(): Boolean {
        val role = _userProfile.value?.role ?: "normal"
        return role == "subscriber" || role == "admin"
    }

    fun isAdmin(): Boolean {
        val email = getCurrentUser()?.email?.lowercase() ?: ""
        val role = _userProfile.value?.role ?: "normal"
        return role == "admin" || email.contains("admin") || email == "admin@deen365.com"
    }

    private fun determineRole(email: String, existingRole: String?): String {
        val lower = email.lowercase()
        return when {
            existingRole == "admin" || lower.contains("admin") || lower == "admin@deen365.com" -> "admin"
            existingRole == "subscriber" -> "subscriber"
            else -> existingRole ?: "normal"
        }
    }

    private fun fetchOrInitUserProfile(user: FirebaseUser) {
        scope.launch {
            try {
                val docRef = firestore.collection("users").document(user.uid)
                val snapshot = docRef.get().await()

                if (snapshot.exists()) {
                    val profile = snapshot.toObject(UserProfile::class.java)
                    if (profile != null) {
                        val computedRole = determineRole(user.email ?: "", profile.role)
                        val updated = profile.copy(
                            uid = user.uid,
                            email = user.email ?: profile.email,
                            displayName = user.displayName ?: profile.displayName,
                            role = computedRole
                        )
                        _userProfile.value = updated
                        if (computedRole != profile.role) {
                            docRef.update("role", computedRole)
                        }
                        return@launch
                    }
                }

                // Initial creation in Firestore
                val initialRole = determineRole(user.email ?: "", null)
                val newProfile = UserProfile(
                    uid = user.uid,
                    email = user.email ?: "user@deen365.app",
                    displayName = user.displayName ?: user.email?.substringBefore("@") ?: "Believer",
                    role = initialRole,
                    subscriptionPlan = if (initialRole == "subscriber" || initialRole == "admin") "Premium Access" else "Free",
                    createdAt = System.currentTimeMillis()
                )
                docRef.set(newProfile, SetOptions.merge()).await()
                _userProfile.value = newProfile
                Log.d(TAG, "Saved user profile in Firestore: ${newProfile.email} with role ${newProfile.role}")
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching user profile from Firestore: ${e.message}", e)
                val fallbackRole = determineRole(user.email ?: "", null)
                _userProfile.value = UserProfile(
                    uid = user.uid,
                    email = user.email ?: "",
                    displayName = user.displayName ?: "Believer",
                    role = fallbackRole
                )
            }
        }
    }

    fun registerWithEmail(
        email: String,
        password: String,
        displayName: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        scope.launch {
            try {
                val result = auth.createUserWithEmailAndPassword(email.trim(), password).await()
                val user = result.user
                if (user != null) {
                    if (displayName.isNotBlank()) {
                        val update = UserProfileChangeRequest.Builder()
                            .setDisplayName(displayName.trim())
                            .build()
                        user.updateProfile(update).await()
                    }
                    fetchOrInitUserProfile(user)
                    onSuccess()
                } else {
                    onError("Failed to create account")
                }
            } catch (e: Exception) {
                onError(e.localizedMessage ?: "Registration failed")
            }
        }
    }

    fun signInWithEmail(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        scope.launch {
            try {
                val result = auth.signInWithEmailAndPassword(email.trim(), password).await()
                if (result.user != null) {
                    fetchOrInitUserProfile(result.user!!)
                    onSuccess()
                } else {
                    onError("Sign in failed")
                }
            } catch (e: Exception) {
                onError(e.localizedMessage ?: "Invalid email or password")
            }
        }
    }

    fun sendPasswordReset(
        email: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        scope.launch {
            try {
                auth.sendPasswordResetEmail(email.trim()).await()
                onSuccess()
            } catch (e: Exception) {
                onError(e.localizedMessage ?: "Password reset failed")
            }
        }
    }

    fun signInWithGoogleToken(
        idToken: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        scope.launch {
            try {
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                val result = auth.signInWithCredential(credential).await()
                if (result.user != null) {
                    fetchOrInitUserProfile(result.user!!)
                    onSuccess()
                } else {
                    onError("Google Sign-In failed")
                }
            } catch (e: Exception) {
                onError(e.localizedMessage ?: "Google Sign-In error")
            }
        }
    }

    fun signOut() {
        auth.signOut()
        _userProfile.value = null
    }

    fun upgradeToSubscriber(planName: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val user = auth.currentUser
        if (user == null) {
            onError("Please sign in or register first to activate a subscription.")
            return
        }
        scope.launch {
            try {
                val docRef = firestore.collection("users").document(user.uid)
                docRef.update(
                    mapOf(
                        "role" to "subscriber",
                        "subscriptionPlan" to planName
                    )
                ).await()
                _userProfile.value = _userProfile.value?.copy(
                    role = "subscriber",
                    subscriptionPlan = planName
                )
                onSuccess()
            } catch (e: Exception) {
                Log.e(TAG, "Error updating subscription in Firestore: ${e.message}", e)
                // Local optimistic update
                _userProfile.value = _userProfile.value?.copy(
                    role = "subscriber",
                    subscriptionPlan = planName
                )
                onSuccess()
            }
        }
    }

    fun updateUserProfileDetails(
        displayName: String,
        avatarId: String,
        bio: String,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        val user = auth.currentUser
        val updated = (_userProfile.value ?: UserProfile()).copy(
            displayName = displayName.ifBlank { "Believer" },
            avatarId = avatarId,
            spiritualIntentionBio = bio
        )
        _userProfile.value = updated

        if (user != null) {
            scope.launch {
                try {
                    val update = UserProfileChangeRequest.Builder()
                        .setDisplayName(displayName.trim())
                        .build()
                    user.updateProfile(update).await()

                    firestore.collection("users").document(user.uid)
                        .set(updated, SetOptions.merge())
                        .await()
                    onSuccess()
                } catch (e: Exception) {
                    Log.w(TAG, "Error updating user details in Firestore: ${e.message}")
                    onSuccess() // Optimistic local success
                }
            }
        } else {
            onSuccess()
        }
    }

    fun updateSpiritualGoals(
        goals: SpiritualGoals,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        val user = auth.currentUser
        val updated = (_userProfile.value ?: UserProfile()).copy(
            spiritualGoals = goals
        )
        _userProfile.value = updated

        if (user != null) {
            scope.launch {
                try {
                    firestore.collection("users").document(user.uid)
                        .update("spiritualGoals", goals)
                        .await()
                    onSuccess()
                } catch (e: Exception) {
                    Log.w(TAG, "Error updating goals in Firestore: ${e.message}")
                    onSuccess() // Optimistic local success
                }
            }
        } else {
            onSuccess()
        }
    }

    fun updateRoleAsAdmin(targetUid: String, newRole: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (!isAdmin()) {
            onError("Admin privileges required.")
            return
        }
        scope.launch {
            try {
                firestore.collection("users").document(targetUid)
                    .update("role", newRole)
                    .await()
                if (auth.currentUser?.uid == targetUid) {
                    _userProfile.value = _userProfile.value?.copy(role = newRole)
                }
                onSuccess()
            } catch (e: Exception) {
                onError(e.localizedMessage ?: "Failed to update role")
            }
        }
    }

    private fun fetchSubscriptionPlans() {
        scope.launch {
            try {
                val snap = firestore.collection("plans").get().await()
                if (!snap.isEmpty) {
                    val plans = snap.documents.mapNotNull { it.toObject(SubscriptionPlanItem::class.java) }
                    if (plans.isNotEmpty()) {
                        _subscriptionPlans.value = plans
                        return@launch
                    }
                }
                // Seed default plans into Firestore if empty
                getDefaultPlans().forEach { plan ->
                    firestore.collection("plans").document(plan.id).set(plan)
                }
            } catch (e: Exception) {
                Log.w(TAG, "Using default plans due to offline or rules: ${e.message}")
            }
        }
    }

    fun saveSubscriptionPlan(plan: SubscriptionPlanItem, onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (!isAdmin()) {
            onError("Only administrators can manage plans.")
            return
        }
        scope.launch {
            try {
                firestore.collection("plans").document(plan.id).set(plan).await()
                _subscriptionPlans.value = _subscriptionPlans.value.filter { it.id != plan.id } + plan
                onSuccess()
            } catch (e: Exception) {
                onError(e.localizedMessage ?: "Failed to save plan")
            }
        }
    }

    companion object {
        fun getDefaultPlans(): List<SubscriptionPlanItem> {
            return listOf(
                SubscriptionPlanItem(
                    id = "plan_monthly",
                    name = "Spiritual Companion Monthly",
                    price = "$4.99",
                    billingCycle = "/ month",
                    description = "Full access to Deen AI spiritual mentor with unlimited queries & hadith exploration",
                    features = listOf(
                        "Unlimited Deen AI (Gemini 3.5 Spiritual Advisor)",
                        "Personalized Duas & Hadith recommendations",
                        "Priority offline Quran recitation downloads",
                        "Sync across unlimited devices with Firestore",
                        "Ad-free spiritual sanctuary"
                    ),
                    isPopular = true,
                    isActive = true
                ),
                SubscriptionPlanItem(
                    id = "plan_annual",
                    name = "Barakah Annual Pass",
                    price = "$39.99",
                    billingCycle = "/ year (Save 33%)",
                    description = "Best value for seekers of sacred knowledge and lifetime spiritual habit consistency",
                    features = listOf(
                        "All Monthly features included",
                        "Personalized Ramadan & Fasting AI coaching",
                        "Early access to new Quran & Hadith audio translations",
                        "Exclusive Islamic Lecture notes & summaries",
                        "Family sharing up to 5 members"
                    ),
                    isPopular = false,
                    isActive = true
                ),
                SubscriptionPlanItem(
                    id = "plan_lifetime",
                    name = "Sadaqah Jariyah Lifetime",
                    price = "$99.99",
                    billingCycle = "one-time",
                    description = "Support ongoing Islamic open knowledge and unlock all current and future modules forever",
                    features = listOf(
                        "Lifetime access to all Deen AI features",
                        "All future apps, themes, and audio recitations",
                        "Patron badge in community reflections",
                        "Direct feedback channel with development team"
                    ),
                    isPopular = false,
                    isActive = true
                )
            )
        }
    }
}
