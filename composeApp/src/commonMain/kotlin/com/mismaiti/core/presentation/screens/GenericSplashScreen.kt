package com.mismaiti.core.presentation.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Generic Splash Screen - Pre-Built Template Component
 *
 * Full-screen splash with app icon/logo, app name, optional tagline,
 * and a loading indicator. Automatically calls onFinished after
 * the specified delay.
 *
 * Used as the Splash phase in AppOrchestrator.
 *
 * Usage - Basic splash with icon:
 * ```
 * GenericSplashScreen(
 *     appName = "My App",
 *     icon = Icons.Default.ShoppingCart,
 *     onFinished = { viewModel.checkAuth() }
 * )
 * ```
 *
 * Usage - Splash with tagline and custom duration:
 * ```
 * GenericSplashScreen(
 *     appName = "Foodie",
 *     tagline = "Delicious food, delivered fast",
 *     icon = Icons.Default.Restaurant,
 *     durationMillis = 2500,
 *     onFinished = { viewModel.checkAuth() }
 * )
 * ```
 *
 * Usage - Splash with custom logo content:
 * ```
 * GenericSplashScreen(
 *     appName = "My App",
 *     onFinished = { viewModel.checkAuth() },
 *     logoContent = {
 *         AsyncImage(
 *             model = "logo.png",
 *             contentDescription = "Logo",
 *             modifier = Modifier.size(120.dp)
 *         )
 *     }
 * )
 * ```
 *
 * Usage - Wiring with AppOrchestrator:
 * ```
 * AppOrchestrator(
 *     appState = viewModel.appState.collectAsState().value,
 *     splashContent = {
 *         GenericSplashScreen(
 *             appName = "My App",
 *             icon = Icons.Default.Home,
 *             onFinished = { viewModel.checkAuth() }
 *         )
 *     },
 *     ...
 * )
 * ```
 */
@Composable
fun GenericSplashScreen(
    appName: String,
    tagline: String? = null,
    icon: ImageVector? = null,
    logoContent: (@Composable () -> Unit)? = null,
    durationMillis: Int = 2000,
    showLoading: Boolean = true,
    backgroundColor: Color? = null,
    onFinished: () -> Unit
) {
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 600)
        )
        kotlinx.coroutines.delay(durationMillis.toLong() - 600)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor ?: MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.alpha(alpha.value),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo / Icon
            if (logoContent != null) {
                logoContent()
            } else if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = appName,
                    modifier = Modifier.size(96.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // App name
            Text(
                text = appName,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            // Tagline
            if (tagline != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = tagline,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            }

            // Loading indicator
            if (showLoading) {
                Spacer(modifier = Modifier.height(48.dp))
                CircularProgressIndicator(
                    modifier = Modifier.size(32.dp),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    strokeWidth = 2.dp
                )
            }
        }
    }
}
