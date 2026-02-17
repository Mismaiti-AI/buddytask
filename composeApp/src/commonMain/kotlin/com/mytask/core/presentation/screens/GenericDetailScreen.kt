package com.mytask.core.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.mytask.core.presentation.components.EmptyStateContent

/**
 * Generic Detail Screen - Pre-Built Template Component
 *
 * Works with ANY data type through generics.
 * Displays detail view with header, info sections, and actions.
 *
 * Usage:
 * ```
 * GenericDetailScreen(
 *     title = "Product Details",
 *     item = product,
 *     onBackClick = { navController.popBackStack() },
 *     onEditClick = { navController.navigate("edit/${it.id}") },
 *     onDeleteClick = { viewModel.delete(it) },
 *     headerContent = { product ->
 *         Icon(Icons.Default.ShoppingCart, null, Modifier.size(64.dp))
 *         Text(product.name, style = MaterialTheme.typography.headlineMedium)
 *         StatusBadge(text = product.status)
 *     },
 *     detailContent = { product ->
 *         InfoRow(label = "Price", value = product.price)
 *         InfoRow(label = "Category", value = product.category)
 *         HorizontalDivider()
 *         SectionHeader(title = "Description")
 *         Text(product.description)
 *     }
 * )
 * ```
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> GenericDetailScreen(
    title: String,
    item: T?,
    isLoading: Boolean = false,
    onBackClick: () -> Unit = {},
    onEditClick: ((T) -> Unit)? = null,
    onDeleteClick: ((T) -> Unit)? = null,
    menuActions: List<com.mytask.core.presentation.screens.MenuAction<T>> = emptyList(),
    headerContent: (@Composable (T) -> Unit)? = null,
    detailContent: @Composable (T) -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    if (item != null) {
                        if (onEditClick != null) {
                            IconButton(onClick = { onEditClick(item) }) {
                                Icon(Icons.Default.Edit, "Edit")
                            }
                        }
                        if (onDeleteClick != null || menuActions.isNotEmpty()) {
                            IconButton(onClick = { showMenu = true }) {
                                Icon(Icons.Default.MoreVert, "More")
                            }
                            DropdownMenu(
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false }
                            ) {
                                menuActions.forEach { action ->
                                    DropdownMenuItem(
                                        text = { Text(action.label) },
                                        onClick = {
                                            showMenu = false
                                            action.onClick(item)
                                        },
                                        leadingIcon = action.icon?.let { icon ->
                                            {
                                                Icon(
                                                    imageVector = icon,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                        }
                                    )
                                }
                                if (onDeleteClick != null) {
                                    if (menuActions.isNotEmpty()) {
                                        HorizontalDivider()
                                    }
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                "Delete",
                                                color = MaterialTheme.colorScheme.error
                                            )
                                        },
                                        onClick = {
                                            showMenu = false
                                            showDeleteDialog = true
                                        },
                                        leadingIcon = {
                                            Icon(
                                                Icons.Default.Delete,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.error,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    )
                                }
                            }
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
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                item == null -> {
                    EmptyStateContent(
                        message = "Item not found",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        // Header Section
                        if (headerContent != null) {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                tonalElevation = 1.dp
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    headerContent(item)
                                }
                            }
                        }

                        // Detail Content
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            detailContent(item)
                        }
                    }
                }
            }
        }
    }

    // Delete Confirmation Dialog
    if (showDeleteDialog && item != null && onDeleteClick != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirm Delete") },
            text = { Text("Are you sure you want to delete this item? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onDeleteClick(item)
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

data class MenuAction<T>(
    val label: String,
    val icon: ImageVector? = null,
    val onClick: (T) -> Unit
)
