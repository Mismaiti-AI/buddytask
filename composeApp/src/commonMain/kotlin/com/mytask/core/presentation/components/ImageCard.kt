package com.mytask.core.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

/**
 * Image Card - Displays an image with title, subtitle, and optional overlay badge.
 *
 * Usage inside GenericListScreen:
 * ```
 * GenericListScreen(items = products, ...) { product ->
 *     ImageCard(
 *         imageUrl = product.imageUrl,
 *         title = product.name,
 *         subtitle = product.price,
 *         badge = { StatusBadge(text = "New") }
 *     )
 * }
 * ```
 */
@Composable
fun ImageCard(
    imageUrl: String,
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    aspectRatio: Float = 16f / 9f,
    badge: (@Composable () -> Unit)? = null,
    footerContent: (@Composable () -> Unit)? = null
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // Image with optional badge overlay
        Box(modifier = Modifier.fillMaxWidth()) {
            AsyncImage(
                model = imageUrl,
                contentDescription = title,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(aspectRatio),
                contentScale = ContentScale.Crop
            )
            if (badge != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                ) {
                    badge()
                }
            }
        }

        // Text content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .padding(12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            if (subtitle != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            if (footerContent != null) {
                Spacer(modifier = Modifier.height(8.dp))
                footerContent()
            }
        }
    }
}

/**
 * Horizontal Image Card - Image on left, content on right.
 *
 * Usage:
 * ```
 * HorizontalImageCard(
 *     imageUrl = article.thumbnailUrl,
 *     title = article.title,
 *     subtitle = article.author,
 *     trailingContent = { Text(article.date) }
 * )
 * ```
 */
@Composable
fun HorizontalImageCard(
    imageUrl: String,
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    imageSize: Float = 80f,
    trailingContent: (@Composable () -> Unit)? = null
) {
    androidx.compose.foundation.layout.Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = MaterialTheme.shapes.medium
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = title,
                modifier = Modifier
                    .height(imageSize.dp)
                    .aspectRatio(1f),
                contentScale = ContentScale.Crop
            )
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            if (subtitle != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        if (trailingContent != null) {
            trailingContent()
        }
    }
}
