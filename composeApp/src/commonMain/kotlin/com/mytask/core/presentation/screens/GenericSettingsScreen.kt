package com.mytask.core.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.mytask.core.presentation.components.SectionHeader

/**
 * Generic Settings Screen - Pre-Built Template Component
 *
 * Configurable settings screen with sections containing
 * navigation items, toggles, and info rows.
 *
 * Usage:
 * ```
 * GenericSettingsScreen(
 *     title = "Settings",
 *     onBackClick = { navController.popBackStack() },
 *     sections = listOf(
 *         SettingsSection(
 *             title = "Account",
 *             items = listOf(
 *                 SettingsItem.Navigation(
 *                     title = "Profile",
 *                     subtitle = "john@example.com",
 *                     icon = Icons.Default.Person,
 *                     onClick = { navController.navigate("profile") }
 *                 ),
 *                 SettingsItem.Navigation(
 *                     title = "Notifications",
 *                     icon = Icons.Default.Notifications,
 *                     onClick = { navController.navigate("notifications") }
 *                 ),
 *             )
 *         ),
 *         SettingsSection(
 *             title = "Preferences",
 *             items = listOf(
 *                 SettingsItem.Toggle(
 *                     title = "Dark Mode",
 *                     subtitle = "Use dark theme",
 *                     icon = Icons.Default.DarkMode,
 *                     checked = isDarkMode,
 *                     onCheckedChange = { viewModel.setDarkMode(it) }
 *                 ),
 *                 SettingsItem.Toggle(
 *                     title = "Push Notifications",
 *                     checked = pushEnabled,
 *                     onCheckedChange = { viewModel.setPush(it) }
 *                 ),
 *             )
 *         ),
 *         SettingsSection(
 *             title = "About",
 *             items = listOf(
 *                 SettingsItem.Info(title = "Version", value = "1.0.0"),
 *                 SettingsItem.Info(title = "Build", value = "42"),
 *             )
 *         )
 *     )
 * )
 * ```
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenericSettingsScreen(
    title: String = "Settings",
    sections: List<SettingsSection>,
    onBackClick: () -> Unit = {},
    showBack: Boolean = true
) {
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            sections.forEach { section ->
                item {
                    SectionHeader(title = section.title)
                }
                items(section.items) { item ->
                    when (item) {
                        is SettingsItem.Navigation -> NavigationRow(
                            item
                        )
                        is SettingsItem.Toggle -> ToggleRow(
                            item
                        )
                        is SettingsItem.Info -> InfoSettingsRow(
                            item
                        )
                    }
                }
                item {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun NavigationRow(item: SettingsItem.Navigation) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = item.onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (item.icon != null) {
            Icon(
                imageVector = item.icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.size(16.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (item.subtitle != null) {
                Text(
                    text = item.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ToggleRow(item: SettingsItem.Toggle) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { item.onCheckedChange(!item.checked) }
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (item.icon != null) {
            Icon(
                imageVector = item.icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.size(16.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (item.subtitle != null) {
                Text(
                    text = item.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Switch(
            checked = item.checked,
            onCheckedChange = item.onCheckedChange
        )
    }
}

@Composable
private fun InfoSettingsRow(item: SettingsItem.Info) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = item.title,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = item.value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

data class SettingsSection(
    val title: String,
    val items: List<SettingsItem>
)

sealed class SettingsItem {
    data class Navigation(
        val title: String,
        val subtitle: String? = null,
        val icon: ImageVector? = null,
        val onClick: () -> Unit
    ) : SettingsItem()

    data class Toggle(
        val title: String,
        val checked: Boolean,
        val onCheckedChange: (Boolean) -> Unit,
        val subtitle: String? = null,
        val icon: ImageVector? = null
    ) : SettingsItem()

    data class Info(
        val title: String,
        val value: String
    ) : SettingsItem()
}
