package com.mismaiti.core.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mismaiti.core.presentation.components.EmptyStateContent

/**
 * Generic Notification Screen - Pre-Built Template Component
 *
 * Notification inbox with read/unread indicators, swipe-to-dismiss, date grouping, and mark all read.
 *
 * Usage:
 * ```
 * GenericNotificationScreen(
 *     title = "Notifications",
 *     notifications = notifications,
 *     onNotificationClick = { notification -> viewModel.markAsRead(notification.id) },
 *     onDismiss = { notification -> viewModel.dismiss(notification.id) },
 *     onMarkAllRead = { viewModel.markAllRead() },
 *     onBackClick = { navController.popBackStack() }
 * ) { notification ->
 *     ListItemCard(
 *         title = notification.title,
 *         subtitle = notification.body,
 *         leadingIcon = notification.icon
 *     )
 * }
 * ```
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> GenericNotificationScreen(
    title: String,
    notifications: List<NotificationItem<T>>,
    modifier: Modifier = Modifier,
    onNotificationClick: (NotificationItem<T>) -> Unit = {},
    onDismiss: (NotificationItem<T>) -> Unit = {},
    onMarkAllRead: (() -> Unit)? = null,
    onBackClick: (() -> Unit)? = null,
    emptyMessage: String = "No notifications",
    dateGroupProvider: ((NotificationItem<T>) -> String?)? = null,
    itemContent: @Composable (NotificationItem<T>) -> Unit
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    if (onBackClick != null) {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                        }
                    }
                },
                actions = {
                    if (onMarkAllRead != null && notifications.any { !it.isRead }) {
                        IconButton(onClick = onMarkAllRead) {
                            Icon(Icons.Filled.DoneAll, "Mark all as read")
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
            if (notifications.isEmpty()) {
                EmptyStateContent(
                    message = emptyMessage,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    var lastGroup: String? = null
                    notifications.forEach { notification ->
                        val group = dateGroupProvider?.invoke(notification)
                        if (group != null && group != lastGroup) {
                            lastGroup = group
                            item(key = "header_$group") {
                                NotificationDateHeader(group)
                            }
                        }

                        item(key = notification.id) {
                            if (notification.isDismissible) {
                                SwipeToDismissNotification(
                                    notification = notification,
                                    onDismiss = onDismiss,
                                    onClick = onNotificationClick,
                                    itemContent = itemContent
                                )
                            } else {
                                NotificationRow(
                                    notification = notification,
                                    onClick = onNotificationClick,
                                    itemContent = itemContent
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun <T> SwipeToDismissNotification(
    notification: NotificationItem<T>,
    onDismiss: (NotificationItem<T>) -> Unit,
    onClick: (NotificationItem<T>) -> Unit,
    itemContent: @Composable (NotificationItem<T>) -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState()

    LaunchedEffect(dismissState.currentValue) {
        if (dismissState.currentValue != SwipeToDismissBoxValue.Settled) {
            onDismiss(notification)
        }
    }

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.errorContainer)
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Text(
                    text = "Dismiss",
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    ) {
        NotificationRow(
            notification = notification,
            onClick = onClick,
            itemContent = itemContent
        )
    }
}

@Composable
private fun <T> NotificationRow(
    notification: NotificationItem<T>,
    onClick: (NotificationItem<T>) -> Unit,
    itemContent: @Composable (NotificationItem<T>) -> Unit
) {
    Surface(
        onClick = { onClick(notification) },
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (!notification.isRead) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                )
                Spacer(modifier = Modifier.width(8.dp))
            } else {
                Spacer(modifier = Modifier.width(16.dp))
            }
            Box(modifier = Modifier.weight(1f)) {
                itemContent(notification)
            }
        }
    }
}

@Composable
private fun NotificationDateHeader(date: String) {
    Text(
        text = date,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

data class NotificationItem<T>(
    val id: String,
    val content: T,
    val timestamp: Long,
    val isRead: Boolean = false,
    val isDismissible: Boolean = true
)
