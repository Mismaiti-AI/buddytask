package com.mytask.core.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController

/**
 * App Orchestrator - Pre-Built Template Component
 *
 * Top-level app state machine that controls which phase is shown:
 * Splash → Onboarding → Auth → Home (MainScaffold).
 *
 * State transitions are driven by AppState, not by navigation.
 * Each phase gets a fresh navigation scope — no back-press leaks
 * between phases (e.g. pressing back on Home won't go to Auth).
 *
 * Usage - Full app with all phases:
 * ```
 * AppOrchestrator(
 *     appState = viewModel.appState.collectAsState().value,
 *     splashContent = {
 *         GenericSplashScreen(
 *             appName = "My App",
 *             icon = Icons.Default.Home,
 *             onFinished = { viewModel.checkAuth() }
 *         )
 *     },
 *     onboardingContent = {
 *         GenericOnboardingScreen(
 *             pages = onboardingPages,
 *             onFinish = { viewModel.completeOnboarding() },
 *             onSkip = { viewModel.completeOnboarding() }
 *         )
 *     },
 *     authStartDestination = Routes.Login,
 *     authBuilder = { nav ->
 *         composable<Routes.Login> {
 *             GenericAuthScreen(
 *                 onSubmit = { viewModel.login() },
 *                 onToggleMode = { nav.navigate(Routes.Signup) },
 *                 ...
 *             )
 *         }
 *         composable<Routes.Signup> {
 *             GenericAuthScreen(isLogin = false, ...)
 *         }
 *     },
 *     tabs = listOf(
 *         NavigationTab(Routes.Home, "Home", Icons.Default.Home),
 *         NavigationTab(Routes.Search, "Search", Icons.Default.Search),
 *         NavigationTab(Routes.Profile, "Profile", Icons.Default.Person)
 *     ),
 *     homeStartDestination = Routes.Home,
 *     homeBuilder = { nav ->
 *         composable<Routes.Home> {
 *             GenericDashboardScreen(...)
 *         }
 *         composable<Routes.Search> {
 *             GenericSearchScreen(...)
 *         }
 *         composable<Routes.Profile> {
 *             GenericProfileScreen(...)
 *         }
 *         composable<Routes.Detail> { entry ->
 *             val detail = entry.toRoute<Routes.Detail>()
 *             GenericDetailScreen(
 *                 onBackClick = { nav.popBackStack() },
 *                 onEditClick = { nav.navigate(Routes.Edit(detail.itemId)) },
 *                 ...
 *             )
 *         }
 *     }
 * )
 * ```
 *
 * Usage - Simple app (no auth, no onboarding):
 * ```
 * AppOrchestrator(
 *     appState = AppState.Home,
 *     tabs = listOf(
 *         NavigationTab(Routes.Home, "Home", Icons.Default.Home),
 *         NavigationTab(Routes.Settings, "Settings", Icons.Default.Settings)
 *     ),
 *     homeStartDestination = Routes.Home,
 *     homeBuilder = { nav ->
 *         composable<Routes.Home> { GenericDashboardScreen(...) }
 *         composable<Routes.Settings> { GenericSettingsScreen(...) }
 *     }
 * )
 * ```
 */
@Composable
fun AppOrchestrator(
    appState: AppState,
    // Splash phase
    splashContent: (@Composable () -> Unit)? = null,
    // Onboarding phase
    onboardingContent: (@Composable () -> Unit)? = null,
    // Auth phase
    authStartDestination: Any = Routes.Login,
    authBuilder: (NavGraphBuilder.(NavHostController) -> Unit)? = null,
    // Home phase (MainScaffold)
    tabs: List<NavigationTab> = emptyList(),
    homeStartDestination: Any = Routes.Home,
    homeBuilder: (NavGraphBuilder.(NavHostController) -> Unit)? = null,
    showTopBar: Boolean = true
) {
    when (appState) {
        AppState.Splash -> {
            splashContent?.invoke()
        }

        AppState.Onboarding -> {
            onboardingContent?.invoke()
        }

        AppState.Auth -> {
            if (authBuilder != null) {
                AppNavigationGraph(
                    startDestination = authStartDestination,
                    builder = authBuilder
                )
            }
        }

        AppState.Home -> {
            if (tabs.isNotEmpty() && homeBuilder != null) {
                MainScaffold(
                    tabs = tabs,
                    startDestination = homeStartDestination,
                    showTopBar = showTopBar,
                    builder = homeBuilder
                )
            }
        }
    }
}

/**
 * App lifecycle phases.
 *
 * Flow: Splash → Onboarding (optional) → Auth (optional) → Home
 *
 * Transition logic (in your ViewModel):
 * ```
 * fun checkAuth() {
 *     _appState.value = when {
 *         !hasCompletedOnboarding -> AppState.Onboarding
 *         !isLoggedIn -> AppState.Auth
 *         else -> AppState.Home
 *     }
 * }
 *
 * fun completeOnboarding() {
 *     saveOnboardingComplete()
 *     _appState.value = if (isLoggedIn) AppState.Home else AppState.Auth
 * }
 *
 * fun login() {
 *     // after successful auth
 *     _appState.value = AppState.Home
 * }
 *
 * fun logout() {
 *     clearSession()
 *     _appState.value = AppState.Auth
 * }
 * ```
 */
enum class AppState {
    Splash,
    Onboarding,
    Auth,
    Home
}
