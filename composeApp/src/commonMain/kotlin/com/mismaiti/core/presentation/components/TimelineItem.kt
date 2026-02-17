package com.mismaiti.core.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Timeline Item - Timeline entry with vertical line connector and node circle.
 *
 * Usage:
 * ```
 * Column {
 *     entries.forEachIndexed { index, entry ->
 *         TimelineItem(
 *             isFirst = index == 0,
 *             isLast = index == entries.lastIndex,
 *             nodeIcon = entry.icon
 *         ) {
 *             Text(entry.title)
 *             Text(entry.description)
 *         }
 *     }
 * }
 * ```
 */
@Composable
fun TimelineItem(
    modifier: Modifier = Modifier,
    isFirst: Boolean = false,
    isLast: Boolean = false,
    nodeIcon: ImageVector? = null,
    nodeColor: Color = MaterialTheme.colorScheme.primary,
    lineColor: Color = MaterialTheme.colorScheme.outlineVariant,
    nodeSize: Dp = 12.dp,
    lineWidth: Dp = 2.dp,
    content: @Composable () -> Unit
) {
    Row(modifier = modifier.padding(horizontal = 16.dp)) {
        // Leading column: line + node + line
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(nodeSize + 12.dp)
        ) {
            // Top line
            if (!isFirst) {
                Box(
                    modifier = Modifier
                        .width(lineWidth)
                        .height(12.dp)
                        .background(lineColor)
                )
            } else {
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Node circle
            if (nodeIcon != null) {
                Box(
                    modifier = Modifier
                        .size(nodeSize + 8.dp)
                        .clip(CircleShape)
                        .background(nodeColor.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = nodeIcon,
                        contentDescription = null,
                        modifier = Modifier.size(nodeSize),
                        tint = nodeColor
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(nodeSize)
                        .clip(CircleShape)
                        .background(nodeColor)
                )
            }

            // Bottom line
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(lineWidth)
                        .fillMaxHeight()
                        .background(lineColor)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Content
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = 16.dp)
        ) {
            content()
        }
    }
}

data class TimelineEntry<T>(
    val id: String,
    val content: T,
    val timestamp: Long,
    val icon: ImageVector? = null
)
