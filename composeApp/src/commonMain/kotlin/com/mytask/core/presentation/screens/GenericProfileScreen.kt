package com.mytask.core.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

/**
 * Generic Profile Screen - Pre-Built Template Component
 *
 * Profile/account screen with avatar, name, stats row,
 * and menu sections.
 *
 * Usage:
 * ```
 * GenericProfileScreen(
 *     name = "John Doe",
 *     subtitle = "john@example.com",
 *     avatarUrl = user.photoUrl,
 *     onBackClick = { navController.popBackStack() },
 *     onEditClick = { navController.navigate("edit-profile") },
 *     stats = listOf(
 *         ProfileStat(label = "Posts", value = "128"),
 *         ProfileStat(label = "Followers", value = "1.2K"),
 *         ProfileStat(label = "Following", value = "340"),
 *     ),
 *     menuSections = listOf(
 *         ProfileMenuSection(
 *             title = "Account",
 *             items = listOf(
 *                 ProfileMenuItem(
 *                     title = "Edit Profile",
 *                     icon = Icons.Default.Person,
 *                     onClick = { }
 *                 ),
 *                 ProfileMenuItem(
 *                     title = "Change Password",
 *                     icon = Icons.Default.Lock,
 *                     onClick = { }
 *                 ),
 *             )
 *         )
 *     )
 * )
 * ```
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenericProfileScreen(
    name: String,
    subtitle: String? = null,
    avatarUrl: String? = null,
    avatarText: String? = null,
    onBackClick: () -> Unit = {},
    onEditClick: (() -> Unit)? = null,
    showBack: Boolean = true,
    stats: List<com.mismaiti.core.presentation.screens.ProfileStat> = emptyList(),
    menuSections: List<com.mismaiti.core.presentation.screens.ProfileMenuSection> = emptyList(),
    headerExtraContent: (@Composable () -> Unit)? = null,
    bottomContent: (@Composable () -> Unit)? = null
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile") },
                navigationIcon = {
                    if (showBack) {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                        }
                    }
                },
                actions = {
                    if (onEditClick != null) {
                        IconButton(onClick = onEditClick) {
                            Icon(Icons.Default.Edit, "Edit Profile")
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Avatar & Name Header
            Surface(
                modifier = Modifier.fillMaxWidth(),
                tonalElevation = 1.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Avatar
                    if (avatarUrl != null) {
                        AsyncImage(
                            model = avatarUrl,
                            contentDescription = name,
                            modifier = Modifier
                                .size(96.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(96.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = (avatarText ?: name).take(2).uppercase(),
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = name,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    if (subtitle != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (headerExtraContent != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        headerExtraContent()
                    }
                }
            }

            // Stats Row
            if (stats.isNotEmpty()) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    tonalElevation = 0.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        stats.forEach { stat ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = stat.value,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = stat.label,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            }

            // Menu Sections
            menuSections.forEach { section ->
                Spacer(modifier = Modifier.height(8.dp))
                if (section.title != null) {
                    Text(
                        text = section.title,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
                section.items.forEach { menuItem ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = menuItem.onClick)
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (menuItem.icon != null) {
                            Icon(
                                imageVector = menuItem.icon,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = menuItem.iconTint
                                    ?: MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.size(16.dp))
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = menuItem.title,
                                style = MaterialTheme.typography.bodyLarge,
                                color = menuItem.titleColor
                                    ?: MaterialTheme.colorScheme.onSurface
                            )
                            if (menuItem.subtitle != null) {
                                Text(
                                    text = menuItem.subtitle,
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
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = MaterialTheme.colorScheme.outlineVariant
                )
            }

            // Bottom content (e.g. logout button)
            if (bottomContent != null) {
                Spacer(modifier = Modifier.height(16.dp))
                bottomContent()
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

data class ProfileStat(
    val label: String,
    val value: String
)

data class ProfileMenuSection(
    val title: String? = null,
    val items: List<com.mismaiti.core.presentation.screens.ProfileMenuItem>
)

data class ProfileMenuItem(
    val title: String,
    val subtitle: String? = null,
    val icon: ImageVector? = null,
    val iconTint: androidx.compose.ui.graphics.Color? = null,
    val titleColor: androidx.compose.ui.graphics.Color? = null,
    val onClick: () -> Unit
)
