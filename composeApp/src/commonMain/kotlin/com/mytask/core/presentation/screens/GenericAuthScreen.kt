package com.mytask.core.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.mytask.core.presentation.components.LoadingButton

/**
 * Generic Auth Screen - Pre-Built Template Component
 *
 * Login/signup screen with email & password fields,
 * social login buttons, and mode toggle.
 *
 * Usage:
 * ```
 * GenericAuthScreen(
 *     isLogin = isLoginMode,
 *     email = email,
 *     password = password,
 *     onEmailChange = { email = it },
 *     onPasswordChange = { password = it },
 *     onSubmit = { viewModel.authenticate() },
 *     onToggleMode = { isLoginMode = !isLoginMode },
 *     isLoading = uiState.isLoading,
 *     error = uiState.error,
 *     onForgotPassword = { navController.navigate("forgot-password") },
 *     socialButtons = listOf(
 *         SocialButton(label = "Continue with Google", icon = Icons.Default.Mail) {
 *             viewModel.signInWithGoogle()
 *         },
 *     ),
 *     // For signup mode, extra fields:
 *     showName = !isLoginMode,
 *     name = name,
 *     onNameChange = { name = it },
 *     showConfirmPassword = !isLoginMode,
 *     confirmPassword = confirmPassword,
 *     onConfirmPasswordChange = { confirmPassword = it }
 * )
 * ```
 */
@Composable
fun GenericAuthScreen(
    isLogin: Boolean = true,
    email: String,
    password: String,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onToggleMode: () -> Unit = {},
    isLoading: Boolean = false,
    error: String? = null,
    onForgotPassword: (() -> Unit)? = null,
    socialButtons: List<SocialButton> = emptyList(),
    showName: Boolean = false,
    name: String = "",
    onNameChange: (String) -> Unit = {},
    showConfirmPassword: Boolean = false,
    confirmPassword: String = "",
    onConfirmPasswordChange: (String) -> Unit = {},
    headerContent: (@Composable () -> Unit)? = null
) {
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            // Header (logo, app name)
            if (headerContent != null) {
                headerContent()
            } else {
                Text(
                    text = if (isLogin) "Welcome Back" else "Create Account",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (isLogin) "Sign in to continue"
                    else "Sign up to get started",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Error
            if (error != null) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.errorContainer
                ) {
                    Text(
                        text = error,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Name field (signup only)
            if (showName) {
                OutlinedTextField(
                    value = name,
                    onValueChange = onNameChange,
                    label = { Text("Full Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !isLoading,
                    shape = MaterialTheme.shapes.medium
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Email
            OutlinedTextField(
                value = email,
                onValueChange = onEmailChange,
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !isLoading,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                shape = MaterialTheme.shapes.medium
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Password
            OutlinedTextField(
                value = password,
                onValueChange = onPasswordChange,
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !isLoading,
                visualTransformation = if (passwordVisible) VisualTransformation.None
                else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.VisibilityOff
                            else Icons.Default.Visibility,
                            contentDescription = if (passwordVisible) "Hide" else "Show"
                        )
                    }
                },
                shape = MaterialTheme.shapes.medium
            )

            // Confirm Password (signup only)
            if (showConfirmPassword) {
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = onConfirmPasswordChange,
                    label = { Text("Confirm Password") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !isLoading,
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None
                    else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(onClick = {
                            confirmPasswordVisible = !confirmPasswordVisible
                        }) {
                            Icon(
                                imageVector = if (confirmPasswordVisible) Icons.Default.VisibilityOff
                                else Icons.Default.Visibility,
                                contentDescription = if (confirmPasswordVisible) "Hide" else "Show"
                            )
                        }
                    },
                    shape = MaterialTheme.shapes.medium
                )
            }

            // Forgot password
            if (isLogin && onForgotPassword != null) {
                TextButton(
                    onClick = onForgotPassword,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Forgot Password?")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Submit
            LoadingButton(
                text = if (isLogin) "Sign In" else "Sign Up",
                onClick = onSubmit,
                modifier = Modifier.fillMaxWidth(),
                isLoading = isLoading,
                enabled = email.isNotBlank() && password.isNotBlank()
            )

            // Social buttons
            if (socialButtons.isNotEmpty()) {
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(modifier = Modifier.weight(1f))
                    Text(
                        text = "  OR  ",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    HorizontalDivider(modifier = Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(16.dp))
                socialButtons.forEach { social ->
                    OutlinedButton(
                        onClick = social.onClick,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading
                    ) {
                        if (social.icon != null) {
                            Icon(
                                imageVector = social.icon,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.size(8.dp))
                        }
                        Text(social.label)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Toggle login/signup
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isLogin) "Don't have an account?"
                    else "Already have an account?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                TextButton(onClick = onToggleMode) {
                    Text(
                        text = if (isLogin) "Sign Up" else "Sign In",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

data class SocialButton(
    val label: String,
    val icon: ImageVector? = null,
    val onClick: () -> Unit
)
