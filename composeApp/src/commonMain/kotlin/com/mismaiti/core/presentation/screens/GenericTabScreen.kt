package com.mismaiti.core.presentation.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

/**
 * Generic Tab Screen - Pre-Built Template Component
 *
 * Tabbed content screen with configurable tabs.
 * Each tab has its own content composable.
 *
 * Usage:
 * ```
 * GenericTabScreen(
 *     title = "Orders",
 *     onBackClick = { navController.popBackStack() },
 *     tabs = listOf(
 *         TabItem(title = "Pending", icon = Icons.Default.HourglassEmpty) {
 *             GenericListScreen(
 *                 title = "Pending",
 *                 items = pendingOrders,
 *                 ...
 *             ) { order -> ListItemCard(...) }
 *         },
 *         TabItem(title = "Shipped", icon = Icons.Default.LocalShipping) {
 *             GenericListScreen(
 *                 title = "Shipped",
 *                 items = shippedOrders,
 *                 ...
 *             ) { order -> ListItemCard(...) }
 *         },
 *         TabItem(title = "Delivered", icon = Icons.Default.CheckCircle) {
 *             GenericListScreen(
 *                 title = "Delivered",
 *                 items = deliveredOrders,
 *                 ...
 *             ) { order -> ListItemCard(...) }
 *         },
 *     )
 * )
 * ```
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenericTabScreen(
    title: String,
    tabs: List<com.mismaiti.core.presentation.screens.TabItem>,
    onBackClick: () -> Unit = {},
    showBack: Boolean = true,
    initialTab: Int = 0,
    scrollableTabs: Boolean = false,
    actions: (@Composable () -> Unit)? = null
) {
    val pagerState = rememberPagerState(
        initialPage = initialTab,
        pageCount = { tabs.size }
    )
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    if (showBack) {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                        }
                    }
                },
                actions = {
                    if (actions != null) actions()
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Tab Row
            if (scrollableTabs) {
                PrimaryScrollableTabRow(
                    selectedTabIndex = pagerState.currentPage,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary,
                    edgePadding = 16.dp
                ) {
                    tabs.forEachIndexed { index, tab ->
                        TabContent(
                            tab = tab,
                            selected = pagerState.currentPage == index,
                            onClick = {
                                scope.launch { pagerState.animateScrollToPage(index) }
                            }
                        )
                    }
                }
            } else {
                PrimaryTabRow(
                    selectedTabIndex = pagerState.currentPage,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary
                ) {
                    tabs.forEachIndexed { index, tab ->
                        TabContent(
                            tab = tab,
                            selected = pagerState.currentPage == index,
                            onClick = {
                                scope.launch { pagerState.animateScrollToPage(index) }
                            }
                        )
                    }
                }
            }

            // Pages
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                Box(modifier = Modifier.fillMaxSize()) {
                    tabs[page].content()
                }
            }
        }
    }
}

@Composable
private fun TabContent(
    tab: com.mismaiti.core.presentation.screens.TabItem,
    selected: Boolean,
    onClick: () -> Unit
) {
    if (tab.icon != null) {
        Tab(
            selected = selected,
            onClick = onClick,
            text = { Text(tab.title) },
            icon = {
                if (tab.badge != null) {
                    BadgedBox(
                        badge = {
                            Badge { Text(tab.badge) }
                        }
                    ) {
                        Icon(tab.icon, contentDescription = tab.title)
                    }
                } else {
                    Icon(tab.icon, contentDescription = tab.title)
                }
            }
        )
    } else {
        Tab(
            selected = selected,
            onClick = onClick,
            text = {
                if (tab.badge != null) {
                    BadgedBox(
                        badge = {
                            Badge { Text(tab.badge) }
                        }
                    ) {
                        Text(tab.title)
                    }
                } else {
                    Text(tab.title)
                }
            }
        )
    }
}

data class TabItem(
    val title: String,
    val icon: ImageVector? = null,
    val badge: String? = null,
    val content: @Composable () -> Unit
)
