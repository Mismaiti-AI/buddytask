package com.mytask.core.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

/**
 * Detail Card - Grouped info section with title, rows, and optional action.
 * Designed for use in GenericDetailScreen or standalone.
 *
 * Usage:
 * ```
 * DetailCard(
 *     title = "Contact Info",
 *     icon = Icons.Default.Phone,
 *     actionText = "Edit",
 *     onActionClick = { navigateToEdit() },
 *     rows = listOf(
 *         DetailRow(label = "Email", value = user.email),
 *         DetailRow(label = "Phone", value = user.phone),
 *         DetailRow(label = "Address", value = user.address),
 *     )
 * )
 * ```
 */
@Composable
fun DetailCard(
    title: String,
    rows: List<com.mismaiti.core.presentation.components.DetailRow>,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    actionText: String? = null,
    onActionClick: () -> Unit = {}
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (icon != null) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                if (actionText != null) {
                    TextButton(onClick = onActionClick) {
                        Text(
                            text = actionText,
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Rows
            rows.forEachIndexed { index, row ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = row.label,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(0.4f)
                    )
                    if (row.content != null) {
                        row.content.invoke()
                    } else {
                        Text(
                            text = row.value,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(0.6f),
                            textAlign = androidx.compose.ui.text.style.TextAlign.End
                        )
                    }
                }
                if (index < rows.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                }
            }
        }
    }
}

data class DetailRow(
    val label: String,
    val value: String = "",
    val content: (@Composable () -> Unit)? = null
)
