package com.mytask.core.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

/**
 * Video Card - Thumbnail with play button overlay, title, channel, and duration.
 *
 * Usage inside GenericListScreen:
 * ```
 * GenericListScreen(items = videos, ...) { video ->
 *     VideoCard(
 *         thumbnailUrl = video.thumbnail,
 *         title = video.title,
 *         subtitle = video.channelName,
 *         duration = video.duration,
 *         metadata = "${video.views} views"
 *     )
 * }
 * ```
 */
@Composable
fun VideoCard(
    thumbnailUrl: String,
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    duration: String? = null,
    metadata: String? = null,
    aspectRatio: Float = 16f / 9f
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // Thumbnail with play overlay
        Box(modifier = Modifier.fillMaxWidth()) {
            AsyncImage(
                model = thumbnailUrl,
                contentDescription = title,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(aspectRatio),
                contentScale = ContentScale.Crop
            )

            // Play button overlay
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(48.dp)
                    .background(
                        color = Color.Black.copy(alpha = 0.6f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }

            // Duration badge
            if (duration != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                        .background(
                            color = Color.Black.copy(alpha = 0.75f),
                            shape = MaterialTheme.shapes.small
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = duration,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White
                    )
                }
            }
        }

        // Info
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            if (subtitle != null || metadata != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (subtitle != null) {
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                    if (subtitle != null && metadata != null) {
                        Text(
                            text = "·",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (metadata != null) {
                        Text(
                            text = metadata,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}
