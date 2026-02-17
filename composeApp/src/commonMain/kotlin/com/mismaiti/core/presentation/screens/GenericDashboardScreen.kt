package com.mismaiti.core.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.mismaiti.core.presentation.components.SectionHeader
import com.mismaiti.core.presentation.components.StatCard

/**
 * Generic Dashboard Screen - Pre-Built Template Component
 *
 * Dashboard layout with stats overview, quick actions,
 * and recent items sections. Fully data-driven.
 *
 * Usage:
 * ```
 * GenericDashboardScreen(
 *     title = "Dashboard",
 *     greeting = "Welcome back, John",
 *     stats = listOf(
 *         DashboardStat(label = "Products", value = "124", icon = Icons.Default.ShoppingCart),
 *         DashboardStat(label = "Orders", value = "56", icon = Icons.Default.Receipt),
 *         DashboardStat(label = "Revenue", value = "$12K", icon = Icons.Default.AttachMoney),
 *     ),
 *     quickActions = listOf(
 *         QuickAction(label = "Add Product", icon = Icons.Default.Add) { },
 *         QuickAction(label = "New Order", icon = Icons.Default.ShoppingCart) { },
 *     ),
 *     onSettingsClick = { navController.navigate("settings") },
 *     recentItems = recentOrders,
 *     recentTitle = "Recent Orders",
 *     onSeeAllClick = { navController.navigate("orders") },
 *     recentItemContent = { order ->
 *         ListItem(headlineContent = { Text(order.title) })
 *     }
 * )
 * ```
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> GenericDashboardScreen(
    title: String,
    greeting: String? = null,
    stats: List<com.mismaiti.core.presentation.screens.DashboardStat> = emptyList(),
    quickActions: List<com.mismaiti.core.presentation.screens.QuickAction> = emptyList(),
    isLoading: Boolean = false,
    onSettingsClick: (() -> Unit)? = null,
    onNotificationsClick: (() -> Unit)? = null,
    recentItems: List<T> = emptyList(),
    recentTitle: String = "Recent",
    onSeeAllClick: (() -> Unit)? = null,
    onRecentItemClick: (T) -> Unit = {},
    recentItemContent: (@Composable (T) -> Unit)? = null,
    extraContent: (@Composable () -> Unit)? = null
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                actions = {
                    if (onNotificationsClick != null) {
                        IconButton(onClick = onNotificationsClick) {
                            Icon(Icons.Default.Notifications, "Notifications")
                        }
                    }
                    if (onSettingsClick != null) {
                        IconButton(onClick = onSettingsClick) {
                            Icon(Icons.Default.Settings, "Settings")
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
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Greeting
                    if (greeting != null) {
                        item {
                            Text(
                                text = greeting,
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }

                    // Stats Row
                    if (stats.isNotEmpty()) {
                        item {
                            LazyRow(
                                modifier = Modifier.fillMaxWidth(),
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(stats) { stat ->
                                    StatCard(
                                        label = stat.label,
                                        value = stat.value,
                                        icon = stat.icon,
                                        iconTint = stat.iconTint
                                            ?: MaterialTheme.colorScheme.primary,
                                        onClick = stat.onClick
                                    )
                                }
                            }
                        }
                    }

                    // Quick Actions
                    if (quickActions.isNotEmpty()) {
                        item {
                            SectionHeader(
                                title = "Quick Actions"
                            )
                        }
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                quickActions.forEach { action ->
                                    FilledTonalButton(
                                        onClick = action.onClick,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(
                                            imageVector = action.icon,
                                            contentDescription = null,
                                            modifier = Modifier.padding(end = 8.dp)
                                        )
                                        Text(
                                            text = action.label,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Extra Content
                    if (extraContent != null) {
                        item {
                            extraContent()
                        }
                    }

                    // Recent Items
                    if (recentItemContent != null && recentItems.isNotEmpty()) {
                        item {
                            SectionHeader(
                                title = recentTitle,
                                actionText = if (onSeeAllClick != null) "See All" else null,
                                onActionClick = { onSeeAllClick?.invoke() }
                            )
                        }
                        items(recentItems) { item ->
                            Surface(
                                onClick = { onRecentItemClick(item) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                                shape = MaterialTheme.shapes.medium,
                                tonalElevation = 1.dp
                            ) {
                                recentItemContent(item)
                            }
                        }
                    }
                }
            }
        }
    }
}

data class DashboardStat(
    val label: String,
    val value: String,
    val icon: ImageVector,
    val iconTint: androidx.compose.ui.graphics.Color? = null,
    val onClick: () -> Unit = {}
)

data class QuickAction(
    val label: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)
