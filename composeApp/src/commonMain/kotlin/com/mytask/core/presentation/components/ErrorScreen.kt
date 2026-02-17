package com.mytask.core.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * Error Screen - Full-screen error or offline state with retry.
 *
 * Usage:
 * ```
 * // Generic error
 * ErrorContent(
 *     title = "Something went wrong",
 *     message = "Please try again later",
 *     onRetry = { viewModel.retry() }
 * )
 *
 * // Offline state
 * ErrorContent(
 *     title = "No Internet",
 *     message = "Check your connection and try again",
 *     icon = Icons.Default.CloudOff,
 *     onRetry = { viewModel.retry() }
 * )
 * ```
 */
@Composable
fun ErrorContent(
    title: String,
    modifier: Modifier = Modifier,
    message: String? = null,
    icon: ImageVector = Icons.Default.ErrorOutline,
    retryText: String = "Retry",
    onRetry: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(72.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        if (message != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
        if (onRetry != null) {
            Spacer(modifier = Modifier.height(24.dp))
            LoadingButton(
                text = retryText,
                onClick = onRetry
            )
        }
    }
}
