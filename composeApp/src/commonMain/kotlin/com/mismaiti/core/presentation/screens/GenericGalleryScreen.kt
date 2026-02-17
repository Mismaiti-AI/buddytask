package com.mismaiti.core.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.mismaiti.core.presentation.components.EmptyStateContent

/**
 * Generic Gallery Screen - Pre-Built Template Component
 *
 * Image grid with selection mode, thumbnails, and selected count display.
 *
 * Usage:
 * ```
 * GenericGalleryScreen(
 *     title = "Photos",
 *     items = galleryItems,
 *     onItemClick = { item -> viewModel.openPhoto(item.id) },
 *     selectionMode = isSelecting,
 *     selectedIds = selectedIds,
 *     onSelectionChange = { id, selected -> viewModel.toggleSelection(id, selected) },
 *     onExitSelection = { viewModel.clearSelection() },
 *     onBackClick = { navController.popBackStack() }
 * )
 * ```
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> GenericGalleryScreen(
    title: String,
    items: List<GalleryItem<T>>,
    modifier: Modifier = Modifier,
    onItemClick: (GalleryItem<T>) -> Unit = {},
    onBackClick: (() -> Unit)? = null,
    gridColumns: Int = 3,
    selectionMode: Boolean = false,
    selectedIds: Set<String> = emptySet(),
    onSelectionChange: (String, Boolean) -> Unit = { _, _ -> },
    onExitSelection: (() -> Unit)? = null,
    emptyMessage: String = "No images",
    thumbnailContent: (@Composable (GalleryItem<T>) -> Unit)? = null
) {
    val selectedCount = selectedIds.size

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    if (selectionMode && selectedCount > 0) {
                        Text("$selectedCount selected")
                    } else {
                        Text(title)
                    }
                },
                navigationIcon = {
                    if (selectionMode && onExitSelection != null) {
                        IconButton(onClick = onExitSelection) {
                            Icon(Icons.Filled.Close, "Exit selection")
                        }
                    } else if (onBackClick != null) {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (items.isEmpty()) {
                EmptyStateContent(
                    message = emptyMessage,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(gridColumns),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(items, key = { it.id }) { item ->
                        GalleryCell(
                            item = item,
                            selectionMode = selectionMode,
                            isSelected = selectedIds.contains(item.id),
                            onClick = {
                                if (selectionMode) {
                                    onSelectionChange(item.id, !selectedIds.contains(item.id))
                                } else {
                                    onItemClick(item)
                                }
                            },
                            thumbnailContent = thumbnailContent
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun <T> GalleryCell(
    item: GalleryItem<T>,
    selectionMode: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit,
    thumbnailContent: (@Composable (GalleryItem<T>) -> Unit)?
) {
    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        shape = MaterialTheme.shapes.small
    ) {
        Box {
            if (thumbnailContent != null) {
                thumbnailContent(item)
            } else if (item.thumbnailUrl != null) {
                AsyncImage(
                    model = item.thumbnailUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Image,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (selectionMode) {
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = null,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                )
            }

            if (isSelected && !selectionMode) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = "Selected",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}

data class GalleryItem<T>(
    val id: String,
    val content: T,
    val thumbnailUrl: String? = null
)
