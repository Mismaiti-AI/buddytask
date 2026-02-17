package com.mismaiti.core.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mismaiti.core.presentation.components.EmptyStateContent
import com.mismaiti.core.presentation.components.SectionHeader

/**
 * Generic Search Screen - Pre-Built Template Component
 *
 * Dedicated search screen with query input, filter chips,
 * recent searches, suggestions, and results.
 *
 * Usage:
 * ```
 * GenericSearchScreen(
 *     query = searchQuery,
 *     onQueryChange = { viewModel.search(it) },
 *     results = searchResults,
 *     isSearching = isLoading,
 *     onBackClick = { navController.popBackStack() },
 *     filterChips = listOf(
 *         FilterChip(label = "All", selected = true),
 *         FilterChip(label = "Products", selected = false),
 *         FilterChip(label = "Categories", selected = false),
 *     ),
 *     onFilterClick = { viewModel.setFilter(it) },
 *     recentSearches = recentQueries,
 *     onRecentClick = { viewModel.search(it) },
 *     resultContent = { item ->
 *         ListItem(headlineContent = { Text(item.name) })
 *     }
 * )
 * ```
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun <T> GenericSearchScreen(
    query: String,
    onQueryChange: (String) -> Unit,
    results: List<T>,
    isSearching: Boolean = false,
    onBackClick: () -> Unit = {},
    onResultClick: (T) -> Unit = {},
    filterChips: List<com.mismaiti.core.presentation.screens.SearchFilterChip> = emptyList(),
    onFilterClick: (com.mismaiti.core.presentation.screens.SearchFilterChip) -> Unit = {},
    recentSearches: List<String> = emptyList(),
    onRecentClick: (String) -> Unit = {},
    onClearRecent: () -> Unit = {},
    emptyMessage: String = "No results found",
    resultContent: @Composable (T) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    TextField(
                        value = query,
                        onValueChange = onQueryChange,
                        placeholder = { Text("Search...") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface
                        ),
                        trailingIcon = {
                            if (query.isNotEmpty()) {
                                IconButton(onClick = { onQueryChange("") }) {
                                    Icon(Icons.Default.Clear, "Clear")
                                }
                            }
                        }
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Filter Chips
            if (filterChips.isNotEmpty()) {
                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    filterChips.forEach { chip ->
                        FilterChip(
                            selected = chip.selected,
                            onClick = { onFilterClick(chip) },
                            label = { Text(chip.label) }
                        )
                    }
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    isSearching -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    query.isEmpty() && recentSearches.isNotEmpty() -> {
                        // Show recent searches
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            item {
                                SectionHeader(
                                    title = "Recent Searches",
                                    actionText = "Clear",
                                    onActionClick = onClearRecent
                                )
                            }
                            items(recentSearches) { recent ->
                                ListItem(
                                    headlineContent = { Text(recent) },
                                    leadingContent = {
                                        Icon(
                                            Icons.Default.History,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    },
                                    modifier = Modifier.clickable { onRecentClick(recent) }
                                )
                            }
                        }
                    }
                    query.isNotEmpty() && results.isEmpty() -> {
                        EmptyStateContent(
                            message = emptyMessage,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    results.isNotEmpty() -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(results) { item ->
                                Surface(
                                    onClick = { onResultClick(item) },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = MaterialTheme.shapes.medium,
                                    tonalElevation = 1.dp
                                ) {
                                    resultContent(item)
                                }
                            }
                        }
                    }
                    else -> {
                        // Empty state - no query, no recent
                        Column(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                modifier = Modifier.padding(bottom = 16.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Start typing to search",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

data class SearchFilterChip(
    val key: String,
    val label: String,
    val selected: Boolean = false
)
