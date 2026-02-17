package com.mytask.core.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

/**
 * List Item Card - Flexible list item with leading icon/avatar, text, and trailing content.
 *
 * Usage inside GenericListScreen:
 * ```
 * // With icon
 * GenericListScreen(items = settings, ...) { setting ->
 *     ListItemCard(
 *         title = setting.title,
 *         subtitle = setting.description,
 *         leadingIcon = setting.icon,
 *         showChevron = true
 *     )
 * }
 *
 * // With avatar image
 * GenericListScreen(items = users, ...) { user ->
 *     ListItemCard(
 *         title = user.name,
 *         subtitle = user.email,
 *         avatarUrl = user.avatarUrl,
 *         trailingContent = { StatusBadge(text = user.role) }
 *     )
 * }
 * ```
 */
@Composable
fun ListItemCard(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    caption: String? = null,
    leadingIcon: ImageVector? = null,
    leadingIconTint: Color = MaterialTheme.colorScheme.primary,
    avatarUrl: String? = null,
    avatarText: String? = null,
    showChevron: Boolean = false,
    trailingContent: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Leading: Icon, Avatar Image, or Avatar Text
        when {
            avatarUrl != null -> {
                AsyncImage(
                    model = avatarUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(12.dp))
            }
            avatarText != null -> {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = avatarText.take(2).uppercase(),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
            }
            leadingIcon != null -> {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = leadingIconTint.copy(alpha = 0.12f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = leadingIconTint
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
            }
        }

        // Text content
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (subtitle != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            if (caption != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = caption,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // Trailing
        if (trailingContent != null) {
            Spacer(modifier = Modifier.width(8.dp))
            trailingContent()
        }
        if (showChevron) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
