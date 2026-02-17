package com.mytask.core.presentation.navigation

import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.serialization.Serializable

// ============================================================
// Type-Safe Route Definitions
// ============================================================

/**
 * Type-safe routes using @Serializable objects/classes.
 *
 * - Object routes: screens with no arguments (tabs, create, settings)
 * - Data class routes: screens with arguments (detail, edit)
 *
 * Add your app-specific routes here:
 * ```
 * @Serializable object Products
 * @Serializable data class ProductDetail(val productId: String)
 * @Serializable data class CategoryItems(val categoryId: String, val name: String)
 * ```
 */
object Routes {
    // Tab routes (bottom nav)
    @Serializable object Home
    @Serializable object Search
    @Serializable object Profile

    // Detail routes (with args)
    @Serializable data class Detail(val itemId: String)
    @Serializable data class Edit(val itemId: String)

    // Simple routes (no args)
    @Serializable object Create
    @Serializable object Settings

    // Auth routes
    @Serializable object Login
    @Serializable object Signup
    @Serializable object ForgotPassword
}

// ============================================================
// MainScaffold - App Shell with Bottom Nav
// ============================================================

/**
 * Main App Scaffold - Pre-Built Template Component
 *
 * Single NavHost app shell with type-safe navigation.
 * Bottom bar and top bar auto-hide on non-tab routes.
 *
 * - `tabs` → bottom nav tabs (NavigationTab)
 * - `builder` → NavGraphBuilder lambda to register all destinations
 *
 * Usage - App with bottom nav + detail/edit:
 * ```
 * MainScaffold(
 *     tabs = listOf(
 *         NavigationTab(
 *             route = Routes.Home,
 *             title = "Home",
 *             icon = Icons.Default.Home
 *         ),
 *         NavigationTab(
 *             route = Routes.Search,
 *             title = "Search",
 *             icon = Icons.Default.Search
 *         ),
 *         NavigationTab(
 *             route = Routes.Profile,
 *             title = "Profile",
 *             icon = Icons.Default.Person
 *         )
 *     ),
 *     startDestination = Routes.Home
 * ) { nav ->
 *     // Tab screens
 *     composable<Routes.Home> {
 *         GenericDashboardScreen(
 *             onStatClick = { nav.navigate(Routes.Detail(it.id)) },
 *             ...
 *         )
 *     }
 *     composable<Routes.Search> {
 *         GenericSearchScreen(...)
 *     }
 *     composable<Routes.Profile> {
 *         GenericProfileScreen(...)
 *     }
 *
 *     // Detail routes (bottom bar auto-hides)
 *     composable<Routes.Detail> { entry ->
 *         val detail = entry.toRoute<Routes.Detail>()
 *         GenericDetailScreen(
 *             title = "Order #${detail.itemId}",
 *             onBackClick = { nav.popBackStack() },
 *             onEditClick = { nav.navigate(Routes.Edit(detail.itemId)) },
 *             ...
 *         )
 *     }
 *     composable<Routes.Edit> { entry ->
 *         val edit = entry.toRoute<Routes.Edit>()
 *         GenericFormScreen(
 *             title = "Edit #${edit.itemId}",
 *             onBackClick = { nav.popBackStack() },
 *             ...
 *         )
 *     }
 *     composable<Routes.Create> {
 *         GenericFormScreen(
 *             title = "New Order",
 *             onBackClick = { nav.popBackStack() },
 *             ...
 *         )
 *     }
 * }
 * ```
 *
 * Usage - Simple app (tabs only):
 * ```
 * MainScaffold(
 *     tabs = listOf(
 *         NavigationTab(Routes.Home, "Home", Icons.Default.Home),
 *         NavigationTab(Routes.Settings, "Settings", Icons.Default.Settings)
 *     ),
 *     startDestination = Routes.Home
 * ) { nav ->
 *     composable<Routes.Home> { GenericDashboardScreen(...) }
 *     composable<Routes.Settings> { GenericSettingsScreen(...) }
 * }
 * ```
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffold(
    tabs: List<NavigationTab>,
    startDestination: Any,
    showTopBar: Boolean = true,
    builder: androidx.navigation.NavGraphBuilder.(NavHostController) -> Unit
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val isOnTab = tabs.any { tab ->
        currentDestination?.hasRoute(tab.route::class) == true
    }

    val currentTabTitle = tabs.find { tab ->
        currentDestination?.hasRoute(tab.route::class) == true
    }?.title ?: "App"

    Scaffold(
        topBar = {
            if (showTopBar && isOnTab) {
                TopAppBar(
                    title = { Text(currentTabTitle) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )
            }
        },
        bottomBar = {
            if (isOnTab) {
                NavigationBar {
                    tabs.forEach { tab ->
                        NavigationBarItem(
                            icon = { Icon(tab.icon, contentDescription = tab.title) },
                            label = { Text(tab.title) },
                            selected = currentDestination?.hierarchy?.any {
                                it.hasRoute(tab.route::class)
                            } == true,
                            onClick = {
                                navController.navigate(tab.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier
                .padding(padding)
                .consumeWindowInsets(padding)
        ) {
            builder(navController)
        }
    }
}

// ============================================================
// AppNavigationGraph - Standalone Flows (Auth, etc.)
// ============================================================

/**
 * Standalone navigation graph for full-screen flows
 * without bottom nav (auth, onboarding).
 *
 * Usage - Auth flow:
 * ```
 * AppNavigationGraph(
 *     startDestination = Routes.Login
 * ) { nav ->
 *     composable<Routes.Login> {
 *         GenericAuthScreen(
 *             onSubmit = { viewModel.login() },
 *             onToggleMode = { nav.navigate(Routes.Signup) },
 *             onForgotPassword = { nav.navigate(Routes.ForgotPassword) },
 *             ...
 *         )
 *     }
 *     composable<Routes.Signup> {
 *         GenericAuthScreen(
 *             isLogin = false,
 *             onToggleMode = { nav.popBackStack() },
 *             ...
 *         )
 *     }
 *     composable<Routes.ForgotPassword> {
 *         // forgot password screen
 *     }
 * }
 * ```
 */
@Composable
fun AppNavigationGraph(
    startDestination: Any,
    builder: androidx.navigation.NavGraphBuilder.(NavHostController) -> Unit
) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        builder(navController)
    }
}

// ============================================================
// Data Classes
// ============================================================

/**
 * Bottom nav tab definition.
 * `route` must be a @Serializable object matching a composable<T> registration.
 */
data class NavigationTab(
    val route: Any,
    val title: String,
    val icon: ImageVector
)
