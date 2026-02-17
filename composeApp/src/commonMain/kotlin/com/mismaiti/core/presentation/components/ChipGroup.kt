package com.mismaiti.core.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Chip Group - Multi-select or single-select chip row.
 *
 * Usage (single select):
 * ```
 * ChipGroup(
 *     chips = listOf("All", "Active", "Completed"),
 *     selectedChips = setOf(selectedFilter),
 *     onChipClick = { selectedFilter = it }
 * )
 * ```
 *
 * Usage (multi select):
 * ```
 * ChipGroup(
 *     chips = listOf("Kotlin", "Swift", "Dart"),
 *     selectedChips = selectedTags,
 *     onChipClick = { tag ->
 *         selectedTags = if (tag in selectedTags) selectedTags - tag
 *                        else selectedTags + tag
 *     }
 * )
 * ```
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChipGroup(
    chips: List<String>,
    selectedChips: Set<String>,
    onChipClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    FlowRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        chips.forEach { chip ->
            FilterChip(
                selected = chip in selectedChips,
                onClick = { onChipClick(chip) },
                label = { Text(chip) }
            )
        }
    }
}
