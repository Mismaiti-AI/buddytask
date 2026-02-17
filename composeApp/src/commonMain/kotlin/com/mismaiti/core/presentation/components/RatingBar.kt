package com.mismaiti.core.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Rating Bar - Star rating display/input (1-5 stars, half-star support, read-only or interactive).
 *
 * Usage:
 * ```
 * // Read-only
 * RatingBar(rating = 3.5f)
 *
 * // Interactive
 * RatingBar(
 *     rating = userRating,
 *     onRatingChange = { newRating -> viewModel.updateRating(newRating) }
 * )
 * ```
 */
@Composable
fun RatingBar(
    rating: Float,
    modifier: Modifier = Modifier,
    maxStars: Int = 5,
    starSize: Dp = 24.dp,
    starColor: Color = MaterialTheme.colorScheme.primary,
    emptyStarColor: Color = MaterialTheme.colorScheme.outlineVariant,
    onRatingChange: ((Float) -> Unit)? = null
) {
    Row(modifier = modifier) {
        for (i in 1..maxStars) {
            val icon = when {
                rating >= i -> Icons.Filled.Star
                rating >= i - 0.5f -> Icons.AutoMirrored.Filled.StarHalf
                else -> Icons.Outlined.StarOutline
            }
            val tint = when {
                rating >= i - 0.5f -> starColor
                else -> emptyStarColor
            }
            Icon(
                imageVector = icon,
                contentDescription = "Star $i",
                modifier = Modifier
                    .size(starSize)
                    .then(
                        if (onRatingChange != null) {
                            Modifier.clickable { onRatingChange(i.toFloat()) }
                        } else {
                            Modifier
                        }
                    ),
                tint = tint
            )
        }
    }
}
