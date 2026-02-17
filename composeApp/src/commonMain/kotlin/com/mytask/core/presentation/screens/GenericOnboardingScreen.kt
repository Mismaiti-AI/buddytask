package com.mytask.core.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

/**
 * Generic Onboarding Screen - Pre-Built Template Component
 *
 * Horizontal pager with page indicators, skip and next/done buttons.
 *
 * Usage:
 * ```
 * GenericOnboardingScreen(
 *     pages = listOf(
 *         OnboardingPage(
 *             title = "Welcome",
 *             description = "Discover amazing features",
 *             icon = Icons.Default.Explore
 *         ),
 *         OnboardingPage(
 *             title = "Stay Connected",
 *             description = "Connect with people around you",
 *             icon = Icons.Default.People
 *         ),
 *         OnboardingPage(
 *             title = "Get Started",
 *             description = "Create your account and begin",
 *             icon = Icons.Default.Rocket
 *         ),
 *     ),
 *     onFinish = { navController.navigate("home") },
 *     onSkip = { navController.navigate("home") }
 * )
 * ```
 */
@Composable
fun GenericOnboardingScreen(
    pages: List<com.mismaiti.core.presentation.screens.OnboardingPage>,
    onFinish: () -> Unit,
    onSkip: (() -> Unit)? = null,
    finishText: String = "Get Started",
    nextText: String = "Next",
    skipText: String = "Skip"
) {
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()
    val isLastPage = pagerState.currentPage == pages.size - 1

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Skip button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                if (onSkip != null && !isLastPage) {
                    TextButton(onClick = onSkip) {
                        Text(skipText)
                    }
                }
            }

            // Pages
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) { page ->
                OnboardingPageContent(
                    page = pages[page],
                    customContent = pages[page].content
                )
            }

            // Indicators + buttons
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Page indicators
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    repeat(pages.size) { index ->
                        Box(
                            modifier = Modifier
                                .size(
                                    width = if (index == pagerState.currentPage) 24.dp else 8.dp,
                                    height = 8.dp
                                )
                                .clip(CircleShape)
                                .background(
                                    if (index == pagerState.currentPage)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.outlineVariant
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Next / Finish button
                Button(
                    onClick = {
                        if (isLastPage) {
                            onFinish()
                        } else {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (isLastPage) finishText else nextText)
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun OnboardingPageContent(
    page: com.mismaiti.core.presentation.screens.OnboardingPage,
    customContent: (@Composable () -> Unit)?
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (customContent != null) {
            customContent()
        } else {
            if (page.icon != null) {
                Icon(
                    imageVector = page.icon,
                    contentDescription = null,
                    modifier = Modifier.size(120.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(32.dp))
            }
        }

        Text(
            text = page.title,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        if (page.description != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = page.description,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

data class OnboardingPage(
    val title: String,
    val description: String? = null,
    val icon: ImageVector? = null,
    val content: (@Composable () -> Unit)? = null
)
