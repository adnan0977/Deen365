package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.repository.SubscriptionPlanItem
import com.example.data.repository.UserProfile
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldLight
import com.example.ui.viewmodel.DeenUiState
import com.example.ui.viewmodel.DeenViewModel

@Composable
fun UserAccountDialog(
    state: DeenUiState,
    viewModel: DeenViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val userProfile = state.userProfile
    val isUserLoggedIn = userProfile != null && userProfile.uid.isNotBlank()

    var authMode by remember { mutableIntStateOf(0) } // 0: Login, 1: Register, 2: Reset Password, 3: Plans
    var emailInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var nameInput by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.88f)
                .clip(RoundedCornerShape(24.dp)),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
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
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (isUserLoggedIn) Icons.Default.AccountCircle else Icons.Default.PersonOutline,
                                    contentDescription = null,
                                    tint = EmeraldPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Column {
                            Text(
                                text = if (isUserLoggedIn) userProfile?.displayName ?: "Account" else "Account & Authentication",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            val roleLabel = when (userProfile?.role) {
                                "admin" -> "Administrator"
                                "subscriber" -> "Premium Subscriber"
                                else -> if (isUserLoggedIn) "Normal User" else "Guest Mode (Login optional)"
                            }
                            Text(
                                text = roleLabel,
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = when (userProfile?.role) {
                                    "admin" -> MaterialTheme.colorScheme.error
                                    "subscriber" -> GoldAccent
                                    else -> EmeraldPrimary
                                }
                            )
                        }
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.testTag("user_dialog_close")) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (isUserLoggedIn) {
                    // Profile Overview & Subscription Info
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // User Profile Summary Card
                        item {
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text("Signed In Account", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(userProfile?.email ?: "", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface)

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Access Role: ${userProfile?.role?.uppercase() ?: "NORMAL"}", fontWeight = FontWeight.Bold, color = EmeraldPrimary, fontSize = 12.sp)
                                        Text("Plan: ${userProfile?.subscriptionPlan ?: "Free"}", fontWeight = FontWeight.SemiBold, color = GoldAccent, fontSize = 12.sp)
                                    }
                                }
                            }
                        }

                        // Subscription Status or Upgrade Banner
                        item {
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (userProfile?.role == "subscriber" || userProfile?.role == "admin") EmeraldPrimary else MaterialTheme.colorScheme.surfaceVariant
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.AutoAwesome,
                                            contentDescription = null,
                                            tint = if (userProfile?.role == "subscriber" || userProfile?.role == "admin") GoldAccent else EmeraldPrimary
                                        )
                                        Text(
                                            text = if (userProfile?.role == "subscriber" || userProfile?.role == "admin") "Deen AI Premium Active" else "Unlock Deen AI Companion",
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = if (userProfile?.role == "subscriber" || userProfile?.role == "admin") Color.White else MaterialTheme.colorScheme.onSurface
                                            )
                                        )
                                    }

                                    Text(
                                        text = if (userProfile?.role == "subscriber" || userProfile?.role == "admin") {
                                            "You have unlimited access to Gemini 3.5 spiritual guidance, hadith insights, and custom prayer assistance."
                                        } else {
                                            "Deen AI spiritual guidance is exclusive to subscribers. Select a plan to unlock immediate access."
                                        },
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (userProfile?.role == "subscriber" || userProfile?.role == "admin") Color.White.copy(alpha = 0.9f) else MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    if (userProfile?.role != "subscriber" && userProfile?.role != "admin") {
                                        Button(
                                            onClick = {
                                                viewModel.upgradeToSubscriber("Spiritual Companion Monthly")
                                                Toast.makeText(context, "Upgraded to Subscriber! Deen AI Unlocked.", Toast.LENGTH_SHORT).show()
                                            },
                                            shape = RoundedCornerShape(10.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = Color.Black),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text("Activate Monthly Plan ($4.99/mo)", fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }

                        // Available Plans List
                        item {
                            Text(
                                text = "Available Subscription Plans",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        items(state.subscriptionPlans) { plan ->
                            SubscriptionPlanCard(
                                plan = plan,
                                isCurrentPlan = userProfile?.subscriptionPlan == plan.name,
                                onSelect = {
                                    viewModel.upgradeToSubscriber(plan.name)
                                    Toast.makeText(context, "Activated ${plan.name}!", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }

                        // Sign Out Button
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedButton(
                                onClick = {
                                    viewModel.signOut()
                                    Toast.makeText(context, "Signed out successfully", Toast.LENGTH_SHORT).show()
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                                modifier = Modifier.fillMaxWidth().testTag("sign_out_button")
                            ) {
                                Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Sign Out of Account")
                            }
                        }
                    }
                } else {
                    // Not Logged In: Guest Mode with Login / Register / Forgot Password options
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = EmeraldPrimary.copy(alpha = 0.12f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Login is NOT compulsory! You can read the entire Quran, Hadiths, Namaz timings & use Qibla freely as a guest.",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = EmeraldPrimary),
                                modifier = Modifier.padding(10.dp)
                            )
                        }

                        // Auth Sub-tabs: Login, Register, Forgot Password
                        TabRow(
                            selectedTabIndex = authMode,
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            contentColor = EmeraldPrimary
                        ) {
                            Tab(
                                selected = authMode == 0,
                                onClick = { authMode = 0; errorMessage = null; successMessage = null },
                                text = { Text("Sign In", fontWeight = FontWeight.Bold, fontSize = 11.sp) }
                            )
                            Tab(
                                selected = authMode == 1,
                                onClick = { authMode = 1; errorMessage = null; successMessage = null },
                                text = { Text("Register", fontWeight = FontWeight.Bold, fontSize = 11.sp) }
                            )
                            Tab(
                                selected = authMode == 2,
                                onClick = { authMode = 2; errorMessage = null; successMessage = null },
                                text = { Text("Reset Pass", fontWeight = FontWeight.Bold, fontSize = 11.sp) }
                            )
                        }

                        if (errorMessage != null) {
                            Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                        }
                        if (successMessage != null) {
                            Text(text = successMessage!!, color = EmeraldPrimary, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                        }

                        when (authMode) {
                            0 -> {
                                // Sign In
                                OutlinedTextField(
                                    value = emailInput,
                                    onValueChange = { emailInput = it },
                                    label = { Text("Email Address") },
                                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth().testTag("login_email_input")
                                )

                                OutlinedTextField(
                                    value = passwordInput,
                                    onValueChange = { passwordInput = it },
                                    label = { Text("Password") },
                                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                                    visualTransformation = PasswordVisualTransformation(),
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth().testTag("login_password_input")
                                )

                                Button(
                                    onClick = {
                                        if (emailInput.isBlank() || passwordInput.isBlank()) {
                                            errorMessage = "Please enter both email and password."
                                            return@Button
                                        }
                                        isSubmitting = true
                                        errorMessage = null
                                        viewModel.signInWithEmail(
                                            email = emailInput.trim(),
                                            password = passwordInput,
                                            onSuccess = {
                                                isSubmitting = false
                                                Toast.makeText(context, "Welcome back!", Toast.LENGTH_SHORT).show()
                                            },
                                            onError = {
                                                isSubmitting = false
                                                errorMessage = it
                                            }
                                        )
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                                    modifier = Modifier.fillMaxWidth().testTag("email_sign_in_button"),
                                    enabled = !isSubmitting
                                ) {
                                    if (isSubmitting) {
                                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp))
                                    } else {
                                        Text("Sign In with Email", fontWeight = FontWeight.Bold)
                                    }
                                }

                                // Quick Test Credential Demo Buttons
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text("Quick Demo Logins (Auto-Detect Role):", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            FilledTonalButton(
                                                onClick = {
                                                    emailInput = "admin@deen365.com"
                                                    passwordInput = "Admin@123456"
                                                },
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Text("Fill Admin", fontSize = 10.sp)
                                            }
                                            FilledTonalButton(
                                                onClick = {
                                                    emailInput = "believer@deen365.app"
                                                    passwordInput = "User@123456"
                                                },
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Text("Fill Normal", fontSize = 10.sp)
                                            }
                                        }
                                    }
                                }
                            }

                            1 -> {
                                // Register
                                OutlinedTextField(
                                    value = nameInput,
                                    onValueChange = { nameInput = it },
                                    label = { Text("Full Name") },
                                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth().testTag("register_name_input")
                                )

                                OutlinedTextField(
                                    value = emailInput,
                                    onValueChange = { emailInput = it },
                                    label = { Text("Email Address") },
                                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth().testTag("register_email_input")
                                )

                                OutlinedTextField(
                                    value = passwordInput,
                                    onValueChange = { passwordInput = it },
                                    label = { Text("Create Password (min 6 chars)") },
                                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                                    visualTransformation = PasswordVisualTransformation(),
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth().testTag("register_password_input")
                                )

                                Button(
                                    onClick = {
                                        if (nameInput.isBlank() || emailInput.isBlank() || passwordInput.isBlank()) {
                                            errorMessage = "Please fill in all fields."
                                            return@Button
                                        }
                                        isSubmitting = true
                                        errorMessage = null
                                        viewModel.registerWithEmail(
                                            email = emailInput.trim(),
                                            password = passwordInput,
                                            displayName = nameInput.trim(),
                                            onSuccess = {
                                                isSubmitting = false
                                                Toast.makeText(context, "Account created in Firestore!", Toast.LENGTH_SHORT).show()
                                            },
                                            onError = {
                                                isSubmitting = false
                                                errorMessage = it
                                            }
                                        )
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                                    modifier = Modifier.fillMaxWidth().testTag("register_submit_button"),
                                    enabled = !isSubmitting
                                ) {
                                    if (isSubmitting) {
                                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp))
                                    } else {
                                        Text("Register & Save to Database", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            2 -> {
                                // Password Reset
                                Text(
                                    text = "Enter your registered email address to receive an official password reset link.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                OutlinedTextField(
                                    value = emailInput,
                                    onValueChange = { emailInput = it },
                                    label = { Text("Registered Email") },
                                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth().testTag("reset_password_email_input")
                                )

                                Button(
                                    onClick = {
                                        if (emailInput.isBlank()) {
                                            errorMessage = "Please enter your email."
                                            return@Button
                                        }
                                        isSubmitting = true
                                        errorMessage = null
                                        viewModel.sendPasswordReset(
                                            email = emailInput.trim(),
                                            onSuccess = {
                                                isSubmitting = false
                                                successMessage = "Password reset email sent! Check your inbox."
                                            },
                                            onError = {
                                                isSubmitting = false
                                                errorMessage = it
                                            }
                                        )
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                                    modifier = Modifier.fillMaxWidth().testTag("send_reset_link_btn"),
                                    enabled = !isSubmitting
                                ) {
                                    if (isSubmitting) {
                                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp))
                                    } else {
                                        Text("Send Password Reset Email", fontWeight = FontWeight.Bold)
                                    }
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
private fun SubscriptionPlanCard(
    plan: SubscriptionPlanItem,
    isCurrentPlan: Boolean,
    onSelect: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrentPlan) EmeraldPrimary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface
        ),
        border = if (isCurrentPlan) androidx.compose.foundation.BorderStroke(1.5.dp, GoldAccent) else androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(plan.name, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface)
                Text(
                    text = "${plan.price} ${plan.billingCycle}",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold, color = GoldAccent)
                )
            }

            Text(plan.description, style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)

            plan.features.take(3).forEach { feature ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(14.dp))
                    Text(feature, style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp), color = MaterialTheme.colorScheme.onSurface)
                }
            }

            if (!isCurrentPlan) {
                Spacer(modifier = Modifier.height(4.dp))
                Button(
                    onClick = onSelect,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                    contentPadding = PaddingValues(vertical = 4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Select ${plan.name}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
