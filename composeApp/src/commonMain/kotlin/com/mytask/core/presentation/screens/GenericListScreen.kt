package com.mytask.core.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mytask.core.presentation.components.EmptyStateContent
import com.mytask.core.presentation.components.SearchBar

/**
 * Generic List Screen - Pre-Built Template Component
 *
 * Works with ANY data type through generics.
 * Reusable for all list-based features.
 * Combine with card components for different layouts.
 *
 * Usage with ListItemCard:
 * ```
 * GenericListScreen(
 *     title = "Contacts",
 *     items = contacts,
 *     isLoading = uiState.isLoading,
 *     emptyMessage = "No contacts yet",
 *     searchEnabled = true,
 *     onItemClick = { navController.navigate("contact/${it.id}") },
 *     onAddClick = { navController.navigate("contact/new") }
 * ) { contact ->
 *     ListItemCard(
 *         title = contact.name,
 *         subtitle = contact.email,
 *         avatarUrl = contact.photoUrl,
 *         showChevron = true,
 *         trailingContent = { StatusBadge(text = contact.role) }
 *     )
 * }
 * ```
 *
 * Usage with ImageCard:
 * ```
 * GenericListScreen(
 *     title = "Products",
 *     items = products,
 *     onItemClick = { navController.navigate("product/${it.id}") },
 *     onAddClick = { navController.navigate("product/new") }
 * ) { product ->
 *     ImageCard(
 *         imageUrl = product.imageUrl,
 *         title = product.name,
 *         subtitle = "$${product.price}",
 *         badge = { StatusBadge(text = "New") }
 *     )
 * }
 * ```
 *
 * Usage with VideoCard:
 * ```
 * GenericListScreen(
 *     title = "Tutorials",
 *     items = videos,
 *     onItemClick = { openVideo(it.url) }
 * ) { video ->
 *     VideoCard(
 *         thumbnailUrl = video.thumbnail,
 *         title = video.title,
 *         subtitle = video.channel,
 *         duration = video.duration,
 *         metadata = "${video.views} views"
 *     )
 * }
 * ```
 *
 * Usage with DetailCard:
 * ```
 * GenericListScreen(
 *     title = "Orders",
 *     items = orders,
 *     filterEnabled = true,
 *     onItemClick = { navController.navigate("order/${it.id}") }
 * ) { order ->
 *     DetailCard(
 *         title = "Order #${order.id}",
 *         icon = Icons.Default.Receipt,
 *         rows = listOf(
 *             DetailRow(label = "Date", value = order.date),
 *             DetailRow(label = "Total", value = "$${order.total}"),
 *             DetailRow(label = "Status") {
 *                 StatusBadge(text = order.status)
 *             }
 *         )
 *     )
 * }
 * ```
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> GenericListScreen(
    title: String,
    items: List<T>,
    isLoading: Boolean = false,
    emptyMessage: String = "No items available",
    onItemClick: (T) -> Unit = {},
    onAddClick: () -> Unit = {},
    onRefresh: () -> Unit = {},
    searchEnabled: Boolean = false,
    filterEnabled: Boolean = false,
    itemContent: @Composable (T) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var showSearch by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            if (showSearch) {
                SearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    onClose = {
                        showSearch = false
                        searchQuery = ""
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                TopAppBar(
                    title = { Text(title) },
                    actions = {
                        if (searchEnabled) {
                            IconButton(onClick = { showSearch = true }) {
                                Icon(Icons.Default.Search, "Search")
                            }
                        }
                        if (filterEnabled) {
                            IconButton(onClick = { /* TODO: Filter */ }) {
                                Icon(Icons.Default.FilterList, "Filter")
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, "Add")
            }
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
                items.isEmpty() -> {
                    EmptyStateContent(
                        message = emptyMessage,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(items) { item ->
                            Surface(
                                onClick = { onItemClick(item) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = MaterialTheme.shapes.medium,
                                tonalElevation = 1.dp
                            ) {
                                itemContent(item)
                            }
                        }
                    }
                }
            }
        }
    }
}
