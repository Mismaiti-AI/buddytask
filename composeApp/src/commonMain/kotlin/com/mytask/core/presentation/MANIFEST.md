# Pre-Built UI Manifest
# AI code generator: read this to know what pre-built screens/components exist and how to call them.
# Package: {app_package}.core.presentation

## How to use
# 1. Parse project context for required features
# 2. Match features to screen entries below
# 3. Use the API signatures to call pre-built screens correctly
# 4. Always include [core] - navigation and shared components
# 5. Import from {app_package}.core.presentation.* (NOT {app_package}.presentation.*)

# =============================================================================
# NAVIGATION API
# =============================================================================

## AppOrchestrator
# import {app_package}.core.presentation.navigation.AppOrchestrator
# import {app_package}.core.presentation.navigation.AppState
#
# ```kotlin
# enum class AppState { Splash, Onboarding, Auth, Home }
#
# @Composable
# fun AppOrchestrator(
#     appState: AppState,
#     splashContent: (@Composable () -> Unit)? = null,
#     onboardingContent: (@Composable () -> Unit)? = null,
#     authStartDestination: Any = Routes.Login,
#     authBuilder: (NavGraphBuilder.(NavHostController) -> Unit)? = null,
#     tabs: List<NavigationTab> = emptyList(),
#     homeStartDestination: Any = Routes.Home,
#     homeBuilder: (NavGraphBuilder.(NavHostController) -> Unit)? = null,
#     showTopBar: Boolean = true
# )
# ```
#
# AppOrchestrator is the top-level state machine that controls app phases.
# It delegates to MainScaffold (Home) and AppNavigationGraph (Auth) internally.
#
# AppOrchestrator (state machine)
#   ├── Splash → splashContent (GenericSplashScreen)
#   ├── Onboarding → onboardingContent (GenericOnboardingScreen)
#   ├── Auth → AppNavigationGraph (authBuilder lambda)
#   └── Home → MainScaffold (homeBuilder lambda)

## MainScaffold
# import {app_package}.core.presentation.navigation.MainScaffold
#
# ```kotlin
# @Composable
# fun MainScaffold(
#     tabs: List<NavigationTab>,
#     startDestination: Any,
#     showTopBar: Boolean = true,
#     builder: NavGraphBuilder.(NavHostController) -> Unit
# )
# ```
#
# Single NavHost for Home phase.
# - tabs → bottom nav tabs (NavigationTab with @Serializable route)
# - builder → registers all composable<Route> destinations
# - Bottom bar + top bar auto-hide on non-tab routes

## AppNavigationGraph
# import {app_package}.core.presentation.navigation.AppNavigationGraph
#
# ```kotlin
# @Composable
# fun AppNavigationGraph(
#     startDestination: Any,
#     builder: NavGraphBuilder.(NavHostController) -> Unit
# )
# ```

## NavigationTab
# import {app_package}.core.presentation.navigation.NavigationTab
#
# ```kotlin
# data class NavigationTab(
#     val route: Any,
#     val title: String,
#     val icon: ImageVector
# )
# ```

## Routes (default routes — apps should define their own)
# import {app_package}.core.presentation.navigation.Routes
#
# ```kotlin
# object Routes {
#     @Serializable object Home
#     @Serializable object Search
#     @Serializable object Profile
#     @Serializable data class Detail(val itemId: String)
#     @Serializable data class Edit(val itemId: String)
#     @Serializable object Create
#     @Serializable object Settings
#     @Serializable object Login
#     @Serializable object Signup
#     @Serializable object ForgotPassword
# }
# ```

## Navigation Rules
# Type-safe navigation:
#   - Routes are @Serializable objects (no args) or data classes (with args)
#   - Navigate: nav.navigate(MyRoutes.Detail(itemId))
#   - Extract args: entry.toRoute<MyRoutes.Detail>().itemId
#   - NEVER use arguments?.getString() — not available in KMP
#
# Pattern:
#   AppOrchestrator
#     ├── Splash: GenericSplashScreen → onFinished = { appState = AppState.Home }
#     ├── Onboarding: GenericOnboardingScreen → onFinish = { appState = AppState.Auth }
#     ├── Auth: authBuilder lambda
#     │   ├── composable<Routes.Login> → GenericAuthScreen
#     │   └── composable<Routes.Signup> → GenericAuthScreen(isLogin=false)
#     └── Home: homeBuilder lambda
#         ├── Tab routes (bottom bar visible):
#         │   ├── composable<AppRoutes.Dashboard> → DashboardScreen
#         │   ├── composable<AppRoutes.Items>     → ItemListScreen
#         │   └── composable<AppRoutes.Profile>   → ProfileScreen
#         └── Detail routes (bottom bar auto-hides):
#             ├── composable<AppRoutes.Detail>    → DetailScreen
#             ├── composable<AppRoutes.Edit>      → FormScreen
#             └── composable<AppRoutes.Create>    → FormScreen
#
# Code pattern:
#   ```kotlin
#   // Define routes (@Serializable)
#   object AppRoutes {
#       @Serializable object Dashboard
#       @Serializable object Items
#       @Serializable object Profile
#       @Serializable data class Detail(val itemId: String)
#       @Serializable data class Edit(val itemId: String)
#       @Serializable object Create
#       @Serializable object Settings
#   }
#
#   // Wire in App.kt
#   var appState by remember { mutableStateOf(AppState.Splash) }
#
#   AppOrchestrator(
#       appState = appState,
#       splashContent = {
#           GenericSplashScreen(
#               appName = "MyApp",
#               icon = Icons.Default.Home,
#               onFinished = { appState = AppState.Home }
#           )
#       },
#       tabs = listOf(
#           NavigationTab(AppRoutes.Dashboard, "Home", Icons.Default.Home),
#           NavigationTab(AppRoutes.Items, "Items", Icons.Default.List),
#           NavigationTab(AppRoutes.Profile, "Profile", Icons.Default.Person),
#       ),
#       homeStartDestination = AppRoutes.Dashboard,
#       showTopBar = false,
#       homeBuilder = { nav ->
#           composable<AppRoutes.Dashboard> { DashboardScreen(nav) }
#           composable<AppRoutes.Items> { ItemListScreen(nav) }
#           composable<AppRoutes.Profile> { ProfileScreen(nav) }
#           composable<AppRoutes.Detail> { entry ->
#               val route = entry.toRoute<AppRoutes.Detail>()
#               DetailScreen(route.itemId, nav)
#           }
#           composable<AppRoutes.Edit> { entry ->
#               val route = entry.toRoute<AppRoutes.Edit>()
#               FormScreen(route.itemId, nav)
#           }
#           composable<AppRoutes.Create> { FormScreen(null, nav) }
#           composable<AppRoutes.Settings> { SettingsScreen(nav) }
#       }
#   )
#   ```

# =============================================================================
# SCREEN APIs
# =============================================================================

## GenericListScreen
# features: list, catalog, feed, inventory, browse, collection
# import {app_package}.core.presentation.screens.GenericListScreen
#
# ```kotlin
# @Composable
# fun <T> GenericListScreen(
#     title: String,
#     items: List<T>,
#     isLoading: Boolean = false,
#     emptyMessage: String = "No items available",
#     onItemClick: (T) -> Unit = {},
#     onAddClick: () -> Unit = {},
#     onRefresh: () -> Unit = {},
#     searchEnabled: Boolean = false,
#     filterEnabled: Boolean = false,
#     itemContent: @Composable (T) -> Unit    // ← pass a card component here
# )
# ```
#
# requires_one_of for itemContent:
#   - ListItemCard   → for: contacts, settings items, simple lists
#   - ImageCard      → for: products, articles, galleries
#   - VideoCard      → for: video feeds, tutorials, media
#   - DetailCard     → for: orders, invoices, grouped info

## GenericDetailScreen
# features: detail, view, item-detail, profile-view, product-detail
# import {app_package}.core.presentation.screens.GenericDetailScreen
# import {app_package}.core.presentation.screens.MenuAction
#
# ```kotlin
# data class MenuAction<T>(
#     val label: String,
#     val icon: ImageVector? = null,
#     val onClick: (T) -> Unit
# )
#
# @Composable
# fun <T> GenericDetailScreen(
#     title: String,
#     item: T?,
#     isLoading: Boolean = false,
#     onBackClick: () -> Unit = {},
#     onEditClick: ((T) -> Unit)? = null,
#     onDeleteClick: ((T) -> Unit)? = null,
#     menuActions: List<MenuAction<T>> = emptyList(),
#     headerContent: (@Composable (T) -> Unit)? = null,
#     detailContent: @Composable (T) -> Unit    // ← compose detail layout here
# )
# ```
#
# optional components for detailContent:
#   - InfoRow        → for: label-value detail rows
#   - StatusBadge    → for: status display
#   - DetailCard     → for: grouped info sections
#   - ChipGroup      → for: tags display
#   - ConfirmDialog  → for: delete confirmation

## GenericFormScreen
# features: form, create, edit, add, input, registration
# import {app_package}.core.presentation.screens.GenericFormScreen
# import {app_package}.core.presentation.screens.FormField
# import {app_package}.core.presentation.screens.FieldType
#
# ```kotlin
# data class FormField(
#     val key: String,
#     val label: String,
#     val value: String,
#     val type: FieldType = FieldType.Text,
#     val required: Boolean = false,
#     val enabled: Boolean = true,
#     val readOnly: Boolean = false,
#     val placeholder: String? = null,
#     val error: String? = null,
#     val options: List<String> = emptyList()    // for Dropdown / RadioGroup
# )
#
# enum class FieldType {
#     Text, Number, Email, Phone, Password,
#     MultiLine, Dropdown, Checkbox, RadioGroup, Date
# }
#
# @Composable
# fun GenericFormScreen(
#     title: String,
#     fields: List<FormField>,
#     onFieldChange: (key: String, value: String) -> Unit,
#     onSubmit: () -> Unit,
#     onBackClick: () -> Unit = {},
#     isSubmitting: Boolean = false,
#     submitText: String = "Save",
#     cancelText: String = "Cancel",
#     showCancel: Boolean = true,
#     headerContent: (@Composable () -> Unit)? = null,
#     footerContent: (@Composable () -> Unit)? = null
# )
# ```

## GenericSearchScreen
# features: search, find, lookup, filter
# import {app_package}.core.presentation.screens.GenericSearchScreen
# import {app_package}.core.presentation.screens.SearchFilterChip
#
# ```kotlin
# data class SearchFilterChip(
#     val key: String,
#     val label: String,
#     val selected: Boolean = false
# )
#
# @Composable
# fun <T> GenericSearchScreen(
#     query: String,
#     onQueryChange: (String) -> Unit,
#     results: List<T>,
#     isSearching: Boolean = false,
#     onBackClick: () -> Unit = {},
#     onResultClick: (T) -> Unit = {},
#     filterChips: List<SearchFilterChip> = emptyList(),
#     onFilterClick: (SearchFilterChip) -> Unit = {},
#     recentSearches: List<String> = emptyList(),
#     onRecentClick: (String) -> Unit = {},
#     onClearRecent: () -> Unit = {},
#     emptyMessage: String = "No results found",
#     resultContent: @Composable (T) -> Unit    // ← pass a card component here
# )
# ```

## GenericDashboardScreen
# features: dashboard, home, overview, analytics, summary
# import {app_package}.core.presentation.screens.GenericDashboardScreen
# import {app_package}.core.presentation.screens.DashboardStat
# import {app_package}.core.presentation.screens.QuickAction
#
# ```kotlin
# data class DashboardStat(
#     val label: String,
#     val value: String,
#     val icon: ImageVector,
#     val iconTint: Color? = null,
#     val onClick: () -> Unit = {}
# )
#
# data class QuickAction(
#     val label: String,
#     val icon: ImageVector,
#     val onClick: () -> Unit
# )
#
# @Composable
# fun <T> GenericDashboardScreen(
#     title: String,
#     greeting: String? = null,
#     stats: List<DashboardStat> = emptyList(),
#     quickActions: List<QuickAction> = emptyList(),
#     isLoading: Boolean = false,
#     onSettingsClick: (() -> Unit)? = null,
#     onNotificationsClick: (() -> Unit)? = null,
#     recentItems: List<T> = emptyList(),
#     recentTitle: String = "Recent",
#     onSeeAllClick: (() -> Unit)? = null,
#     onRecentItemClick: (T) -> Unit = {},
#     recentItemContent: (@Composable (T) -> Unit)? = null,
#     extraContent: (@Composable () -> Unit)? = null
# )
# ```

## GenericSettingsScreen
# features: settings, preferences, configuration, options
# import {app_package}.core.presentation.screens.GenericSettingsScreen
# import {app_package}.core.presentation.screens.SettingsSection
# import {app_package}.core.presentation.screens.SettingsItem
#
# ```kotlin
# data class SettingsSection(
#     val title: String,
#     val items: List<SettingsItem>
# )
#
# sealed class SettingsItem {
#     data class Navigation(
#         val title: String,
#         val subtitle: String? = null,
#         val icon: ImageVector? = null,
#         val onClick: () -> Unit
#     ) : SettingsItem()
#
#     data class Toggle(
#         val title: String,
#         val checked: Boolean,
#         val onCheckedChange: (Boolean) -> Unit,
#         val subtitle: String? = null,
#         val icon: ImageVector? = null
#     ) : SettingsItem()
#
#     data class Info(
#         val title: String,
#         val value: String
#     ) : SettingsItem()
# }
#
# @Composable
# fun GenericSettingsScreen(
#     title: String = "Settings",
#     sections: List<SettingsSection>,
#     onBackClick: () -> Unit = {},
#     showBack: Boolean = true
# )
# ```

## GenericProfileScreen
# features: profile, account, user-profile, my-account
# import {app_package}.core.presentation.screens.GenericProfileScreen
# import {app_package}.core.presentation.screens.ProfileStat
# import {app_package}.core.presentation.screens.ProfileMenuSection
# import {app_package}.core.presentation.screens.ProfileMenuItem
#
# ```kotlin
# data class ProfileStat(
#     val label: String,
#     val value: String
# )
#
# data class ProfileMenuSection(
#     val title: String? = null,
#     val items: List<ProfileMenuItem>
# )
#
# data class ProfileMenuItem(
#     val title: String,
#     val subtitle: String? = null,
#     val icon: ImageVector? = null,
#     val iconTint: Color? = null,
#     val titleColor: Color? = null,
#     val onClick: () -> Unit
# )
#
# @Composable
# fun GenericProfileScreen(
#     name: String,
#     subtitle: String? = null,
#     avatarUrl: String? = null,
#     avatarText: String? = null,
#     onBackClick: () -> Unit = {},
#     onEditClick: (() -> Unit)? = null,
#     showBack: Boolean = true,
#     stats: List<ProfileStat> = emptyList(),
#     menuSections: List<ProfileMenuSection> = emptyList(),
#     headerExtraContent: (@Composable () -> Unit)? = null,
#     bottomContent: (@Composable () -> Unit)? = null
# )
# ```

## GenericSplashScreen
# features: splash, loading, startup, launch
# nav_pattern: AppOrchestrator Splash phase → splashContent
# import {app_package}.core.presentation.screens.GenericSplashScreen
#
# ```kotlin
# @Composable
# fun GenericSplashScreen(
#     appName: String,
#     tagline: String? = null,
#     icon: ImageVector? = null,
#     logoContent: (@Composable () -> Unit)? = null,
#     durationMillis: Int = 2000,
#     showLoading: Boolean = true,
#     backgroundColor: Color? = null,
#     onFinished: () -> Unit
# )
# ```

## GenericAuthScreen
# features: auth, login, signup, sign-in, register, authentication
# nav_pattern: AppOrchestrator Auth phase → authBuilder
# import {app_package}.core.presentation.screens.GenericAuthScreen
# import {app_package}.core.presentation.screens.SocialButton
#
# ```kotlin
# data class SocialButton(
#     val label: String,
#     val icon: ImageVector? = null,
#     val onClick: () -> Unit
# )
#
# @Composable
# fun GenericAuthScreen(
#     isLogin: Boolean = true,
#     email: String,
#     password: String,
#     onEmailChange: (String) -> Unit,
#     onPasswordChange: (String) -> Unit,
#     onSubmit: () -> Unit,
#     onToggleMode: () -> Unit = {},
#     isLoading: Boolean = false,
#     error: String? = null,
#     onForgotPassword: (() -> Unit)? = null,
#     socialButtons: List<SocialButton> = emptyList(),
#     showName: Boolean = false,
#     name: String = "",
#     onNameChange: (String) -> Unit = {},
#     showConfirmPassword: Boolean = false,
#     confirmPassword: String = "",
#     onConfirmPasswordChange: (String) -> Unit = {},
#     headerContent: (@Composable () -> Unit)? = null
# )
# ```

## GenericOnboardingScreen
# features: onboarding, walkthrough, intro, welcome, tutorial
# nav_pattern: AppOrchestrator Onboarding phase → onboardingContent
# import {app_package}.core.presentation.screens.GenericOnboardingScreen
# import {app_package}.core.presentation.screens.OnboardingPage
#
# ```kotlin
# data class OnboardingPage(
#     val title: String,
#     val description: String? = null,
#     val icon: ImageVector? = null,
#     val content: (@Composable () -> Unit)? = null
# )
#
# @Composable
# fun GenericOnboardingScreen(
#     pages: List<OnboardingPage>,
#     onFinish: () -> Unit,
#     onSkip: (() -> Unit)? = null,
#     finishText: String = "Get Started",
#     nextText: String = "Next",
#     skipText: String = "Skip"
# )
# ```

## GenericTabScreen
# features: tabs, tabbed, categories, segmented, multi-view
# import {app_package}.core.presentation.screens.GenericTabScreen
# import {app_package}.core.presentation.screens.TabItem
#
# ```kotlin
# data class TabItem(
#     val title: String,
#     val icon: ImageVector? = null,
#     val badge: String? = null,
#     val content: @Composable () -> Unit
# )
#
# @Composable
# fun GenericTabScreen(
#     title: String,
#     tabs: List<TabItem>,
#     onBackClick: () -> Unit = {},
#     showBack: Boolean = true,
#     initialTab: Int = 0,
#     scrollableTabs: Boolean = false,
#     actions: (@Composable () -> Unit)? = null
# )
# ```

## GenericChatScreen
# features: chat, messaging, conversation, support, inbox
# import {app_package}.core.presentation.screens.GenericChatScreen
# import {app_package}.core.presentation.screens.ChatMessage
# import {app_package}.core.presentation.screens.ChatBubbleAlignment
#
# ```kotlin
# data class ChatMessage<T>(
#     val id: String,
#     val content: T,
#     val alignment: ChatBubbleAlignment,
#     val timestamp: Long
# )
#
# enum class ChatBubbleAlignment { START, END }
#
# @Composable
# fun <T> GenericChatScreen(
#     title: String,
#     messages: List<ChatMessage<T>>,
#     onSendMessage: (String) -> Unit,
#     modifier: Modifier = Modifier,
#     onBackClick: (() -> Unit)? = null,
#     isTyping: Boolean = false,
#     emptyMessage: String = "No messages yet",
#     inputPlaceholder: String = "Type a message...",
#     dateHeaderProvider: ((ChatMessage<T>) -> String?)? = null,
#     bubbleContent: (@Composable (ChatMessage<T>) -> Unit)? = null
# )
# ```
#
# Features:
#   - LazyColumn(reverseLayout = true) for chat behavior
#   - Message bubbles: primaryContainer (END) vs surfaceVariant (START)
#   - Input bar at bottom with OutlinedTextField + send button, imePadding()
#   - Typing indicator: 3 animated dots
#   - Date headers between message groups via dateHeaderProvider

## GenericNotificationScreen
# features: notification, inbox, alerts, updates, activity
# import {app_package}.core.presentation.screens.GenericNotificationScreen
# import {app_package}.core.presentation.screens.NotificationItem
#
# ```kotlin
# data class NotificationItem<T>(
#     val id: String,
#     val content: T,
#     val timestamp: Long,
#     val isRead: Boolean = false,
#     val isDismissible: Boolean = true
# )
#
# @Composable
# fun <T> GenericNotificationScreen(
#     title: String,
#     notifications: List<NotificationItem<T>>,
#     modifier: Modifier = Modifier,
#     onNotificationClick: (NotificationItem<T>) -> Unit = {},
#     onDismiss: (NotificationItem<T>) -> Unit = {},
#     onMarkAllRead: (() -> Unit)? = null,
#     onBackClick: (() -> Unit)? = null,
#     emptyMessage: String = "No notifications",
#     dateGroupProvider: ((NotificationItem<T>) -> String?)? = null,
#     itemContent: @Composable (NotificationItem<T>) -> Unit
# )
# ```
#
# Features:
#   - SwipeToDismissBox for swipe-to-dismiss
#   - Unread indicator: 8.dp circle with primary color
#   - TopAppBar action: "Mark all as read"
#   - Date group headers via dateGroupProvider

## GenericGalleryScreen
# features: gallery, photos, images, media, grid, album
# import {app_package}.core.presentation.screens.GenericGalleryScreen
# import {app_package}.core.presentation.screens.GalleryItem
#
# ```kotlin
# data class GalleryItem<T>(
#     val id: String,
#     val content: T,
#     val thumbnailUrl: String? = null
# )
#
# @Composable
# fun <T> GenericGalleryScreen(
#     title: String,
#     items: List<GalleryItem<T>>,
#     modifier: Modifier = Modifier,
#     onItemClick: (GalleryItem<T>) -> Unit = {},
#     onBackClick: (() -> Unit)? = null,
#     gridColumns: Int = 3,
#     selectionMode: Boolean = false,
#     selectedIds: Set<String> = emptySet(),
#     onSelectionChange: (String, Boolean) -> Unit = { _, _ -> },
#     onExitSelection: (() -> Unit)? = null,
#     emptyMessage: String = "No images",
#     thumbnailContent: (@Composable (GalleryItem<T>) -> Unit)? = null
# )
# ```
#
# Features:
#   - LazyVerticalGrid(GridCells.Fixed(gridColumns)) with 1:1 aspect ratio cells
#   - AsyncImage with ContentScale.Crop for thumbnails
#   - Selection mode: Checkbox overlay on each cell
#   - TopAppBar shows selected count in selection mode

# =============================================================================
# COMPONENT APIs
# =============================================================================

## ListItemCard
# for: contacts, settings items, simple lists (no image)
# import {app_package}.core.presentation.components.ListItemCard
#
# ```kotlin
# @Composable
# fun ListItemCard(
#     title: String,
#     modifier: Modifier = Modifier,
#     subtitle: String? = null,
#     caption: String? = null,
#     leadingIcon: ImageVector? = null,
#     leadingIconTint: Color = MaterialTheme.colorScheme.primary,
#     avatarUrl: String? = null,
#     avatarText: String? = null,
#     showChevron: Boolean = false,
#     trailingContent: (@Composable () -> Unit)? = null
# )
# ```

## ImageCard
# for: products, articles, galleries
# import {app_package}.core.presentation.components.ImageCard
# import {app_package}.core.presentation.components.HorizontalImageCard
#
# ```kotlin
# @Composable
# fun ImageCard(
#     imageUrl: String,
#     title: String,
#     modifier: Modifier = Modifier,
#     subtitle: String? = null,
#     aspectRatio: Float = 16f / 9f,
#     badge: (@Composable () -> Unit)? = null,
#     footerContent: (@Composable () -> Unit)? = null
# )
#
# @Composable
# fun HorizontalImageCard(
#     imageUrl: String,
#     title: String,
#     modifier: Modifier = Modifier,
#     subtitle: String? = null,
#     imageSize: Float = 80f,
#     trailingContent: (@Composable () -> Unit)? = null
# )
# ```

## VideoCard
# for: video feeds, tutorials, media
# import {app_package}.core.presentation.components.VideoCard
#
# ```kotlin
# @Composable
# fun VideoCard(
#     thumbnailUrl: String,
#     title: String,
#     modifier: Modifier = Modifier,
#     subtitle: String? = null,
#     duration: String? = null,
#     metadata: String? = null,
#     aspectRatio: Float = 16f / 9f
# )
# ```

## DetailCard
# for: grouped info sections (orders, invoices)
# import {app_package}.core.presentation.components.DetailCard
# import {app_package}.core.presentation.components.DetailRow
#
# ```kotlin
# data class DetailRow(
#     val label: String,
#     val value: String = "",
#     val content: (@Composable () -> Unit)? = null
# )
#
# @Composable
# fun DetailCard(
#     title: String,
#     rows: List<DetailRow>,
#     modifier: Modifier = Modifier,
#     icon: ImageVector? = null,
#     actionText: String? = null,
#     onActionClick: () -> Unit = {}
# )
# ```

## StatCard
# for: dashboard stats
# import {app_package}.core.presentation.components.StatCard
#
# ```kotlin
# @Composable
# fun StatCard(
#     label: String,
#     value: String,
#     icon: ImageVector,
#     modifier: Modifier = Modifier,
#     iconTint: Color = MaterialTheme.colorScheme.primary,
#     onClick: () -> Unit = {}
# )
# ```

## ProgressCard
# for: progress tracking, completion percentage
# import {app_package}.core.presentation.components.ProgressCard
#
# ```kotlin
# @Composable
# fun ProgressCard(
#     title: String,
#     progress: Float,
#     modifier: Modifier = Modifier,
#     subtitle: String? = null,
#     icon: ImageVector? = null,
#     progressColor: Color = MaterialTheme.colorScheme.primary,
#     trackColor: Color = MaterialTheme.colorScheme.surfaceVariant
# )
# ```

## EmptyStateContent
# for: empty lists, no results
# import {app_package}.core.presentation.components.EmptyStateContent
#
# ```kotlin
# @Composable
# fun EmptyStateContent(
#     message: String,
#     modifier: Modifier = Modifier
# )
# ```

## ErrorContent
# for: error states, retry
# import {app_package}.core.presentation.components.ErrorContent
#
# ```kotlin
# @Composable
# fun ErrorContent(
#     title: String,
#     modifier: Modifier = Modifier,
#     message: String? = null,
#     icon: ImageVector = Icons.Default.ErrorOutline,
#     retryText: String = "Retry",
#     onRetry: (() -> Unit)? = null
# )
# ```

## LoadingButton
# for: submit buttons with loading state
# import {app_package}.core.presentation.components.LoadingButton
# import {app_package}.core.presentation.components.SecondaryButton
#
# ```kotlin
# @Composable
# fun LoadingButton(
#     text: String,
#     onClick: () -> Unit,
#     modifier: Modifier = Modifier,
#     isLoading: Boolean = false,
#     enabled: Boolean = true
# )
#
# @Composable
# fun SecondaryButton(
#     text: String,
#     onClick: () -> Unit,
#     modifier: Modifier = Modifier,
#     enabled: Boolean = true
# )
# ```

## SectionHeader
# for: section titles with optional action
# import {app_package}.core.presentation.components.SectionHeader
#
# ```kotlin
# @Composable
# fun SectionHeader(
#     title: String,
#     modifier: Modifier = Modifier,
#     actionText: String? = null,
#     onActionClick: () -> Unit = {}
# )
# ```

## SearchBar
# for: search input
# import {app_package}.core.presentation.components.SearchBar
#
# ```kotlin
# @Composable
# fun SearchBar(
#     query: String,
#     onQueryChange: (String) -> Unit,
#     onClose: () -> Unit,
#     modifier: Modifier = Modifier
# )
# ```

## InfoRow
# for: label-value pairs in detail screens
# import {app_package}.core.presentation.components.InfoRow
#
# ```kotlin
# @Composable
# fun InfoRow(
#     label: String,
#     value: String,
#     modifier: Modifier = Modifier,
#     icon: ImageVector? = null
# )
# ```

## StatusBadge
# for: status labels, tags
# import {app_package}.core.presentation.components.StatusBadge
#
# ```kotlin
# @Composable
# fun StatusBadge(
#     text: String,
#     modifier: Modifier = Modifier,
#     containerColor: Color = MaterialTheme.colorScheme.secondaryContainer,
#     contentColor: Color = MaterialTheme.colorScheme.onSecondaryContainer
# )
# ```

## ChipGroup
# for: tags, multi-select filters
# import {app_package}.core.presentation.components.ChipGroup
#
# ```kotlin
# @Composable
# fun ChipGroup(
#     chips: List<String>,
#     selectedChips: Set<String>,
#     onChipClick: (String) -> Unit,
#     modifier: Modifier = Modifier
# )
# ```

## ConfirmDialog
# for: delete confirmation, destructive actions
# import {app_package}.core.presentation.components.ConfirmDialog
#
# ```kotlin
# @Composable
# fun ConfirmDialog(
#     show: Boolean,
#     title: String,
#     message: String,
#     onConfirm: () -> Unit,
#     onDismiss: () -> Unit,
#     confirmText: String = "Confirm",
#     dismissText: String = "Cancel",
#     isDestructive: Boolean = false
# )
# ```

## SwitchRow
# for: toggle settings
# import {app_package}.core.presentation.components.SwitchRow
#
# ```kotlin
# @Composable
# fun SwitchRow(
#     title: String,
#     checked: Boolean,
#     onCheckedChange: (Boolean) -> Unit,
#     modifier: Modifier = Modifier,
#     description: String? = null,
#     icon: ImageVector? = null,
#     enabled: Boolean = true
# )
# ```

## RatingBar
# for: reviews, ratings, feedback, star rating
# import {app_package}.core.presentation.components.RatingBar
#
# ```kotlin
# @Composable
# fun RatingBar(
#     rating: Float,
#     modifier: Modifier = Modifier,
#     maxStars: Int = 5,
#     starSize: Dp = 24.dp,
#     starColor: Color = MaterialTheme.colorScheme.primary,
#     emptyStarColor: Color = MaterialTheme.colorScheme.outlineVariant,
#     onRatingChange: ((Float) -> Unit)? = null    // null = read-only
# )
# ```

## CounterRow
# for: quantity stepper, cart quantity, number input
# import {app_package}.core.presentation.components.CounterRow
#
# ```kotlin
# @Composable
# fun CounterRow(
#     value: Int,
#     onValueChange: (Int) -> Unit,
#     modifier: Modifier = Modifier,
#     label: String? = null,
#     minValue: Int = 0,
#     maxValue: Int = Int.MAX_VALUE,
#     step: Int = 1
# )
# ```

## ExpandableCard
# for: FAQ, accordion, collapsible sections, details
# import {app_package}.core.presentation.components.ExpandableCard
#
# ```kotlin
# @Composable
# fun ExpandableCard(
#     title: String,
#     modifier: Modifier = Modifier,
#     subtitle: String? = null,
#     icon: ImageVector? = null,
#     initiallyExpanded: Boolean = false,
#     content: @Composable () -> Unit
# )
# ```

## TimelineItem
# for: order tracking, activity history, progress steps, timeline
# import {app_package}.core.presentation.components.TimelineItem
# import {app_package}.core.presentation.components.TimelineEntry
#
# ```kotlin
# data class TimelineEntry<T>(
#     val id: String,
#     val content: T,
#     val timestamp: Long,
#     val icon: ImageVector? = null
# )
#
# @Composable
# fun TimelineItem(
#     modifier: Modifier = Modifier,
#     isFirst: Boolean = false,
#     isLast: Boolean = false,
#     nodeIcon: ImageVector? = null,
#     nodeColor: Color = MaterialTheme.colorScheme.primary,
#     lineColor: Color = MaterialTheme.colorScheme.outlineVariant,
#     nodeSize: Dp = 12.dp,
#     lineWidth: Dp = 2.dp,
#     content: @Composable () -> Unit
# )
# ```

## Carousel
# for: banners, featured content, image slider, promotions
# import {app_package}.core.presentation.components.Carousel
# import {app_package}.core.presentation.components.CarouselItem
#
# ```kotlin
# data class CarouselItem<T>(
#     val id: String,
#     val content: T
# )
#
# @Composable
# fun Carousel(
#     pageCount: Int,
#     modifier: Modifier = Modifier,
#     autoScroll: Boolean = false,
#     autoScrollDelayMillis: Long = 3000L,
#     pageContent: @Composable (page: Int) -> Unit
# )
# ```

# =============================================================================
# DECISION TREE (maps to AppOrchestrator params)
# =============================================================================
# 1. Does the project need a splash screen? → Provide splashContent
# 2. Is it a first-time app? → Include Onboarding, provide onboardingContent
# 3. Does the project have users/auth? → Include Auth, provide authBuilder
# 4. How many top-level sections? → Define tabs (NavigationTab list)
# 5. Does any tab list items? → Include List/Search + appropriate card
#    Add detail/edit as composable<Route> in homeBuilder (bottom bar auto-hides)
# 6. Can users create/edit? → Include Form + Detail
#    Add composable<AppRoutes.Detail>, composable<AppRoutes.Edit>, composable<AppRoutes.Create>
# 7. Does it have a home/overview? → Include Dashboard (as a tab)
# 8. Does it need search? → Include Search (as a tab)
# 9. Does it have settings? → Include Settings (as a homeBuilder destination)
# 10. Does it have categorized views? → Include Tab
# 11. Does it need error handling? → Include ErrorContent (recommended always)
# 12. Does it have delete/destructive actions? → Include ConfirmDialog
# 13. Does it have chat/messaging? → Include Chat
# 14. Does it have notifications/alerts? → Include Notification
# 15. Does it have a photo gallery/media grid? → Include Gallery

# =============================================================================
# AppOrchestrator Phase Decision
# =============================================================================
# - Has splash? → appState starts at Splash, splashContent triggers phase transition
# - Has onboarding? → Splash transitions to Onboarding, then Auth or Home
# - Has auth? → Onboarding/Splash transitions to Auth, login success → Home
# - No splash/onboarding/auth? → appState starts at Home directly
# - Single-section app? → Still use AppOrchestrator with Home phase only
# - showTopBar = false always → each screen manages its own TopAppBar

# =============================================================================
# DERIVATION RULES — Screen Selection from project-context.json
# =============================================================================
#
# These rules tell the plan generator which pre-built screens to use
# for each feature/screen in project-context.json.
#
# INPUT: project-context.json fields
# OUTPUT: screen type + components to include in implementation plan
#
# ─────────────────────────────────────────────────────────────────
# RULE 1: Screen name → Pre-built screen mapping
# ─────────────────────────────────────────────────────────────────
# Apply these rules to each screen in features[].screens[].name:
#
#   Screen name contains "List"       → GenericListScreen
#   Screen name contains "Detail"     → GenericDetailScreen
#   Screen name contains "Form"       → GenericFormScreen
#   Screen name contains "Create"     → GenericFormScreen
#   Screen name contains "Add"        → GenericFormScreen
#   Screen name contains "Edit"       → GenericFormScreen
#   Screen name contains "Dashboard"  → GenericDashboardScreen
#   Screen name contains "Search"     → GenericSearchScreen
#   Screen name contains "Settings"   → GenericSettingsScreen
#   Screen name contains "Config"     → GenericSettingsScreen (or GenericFormScreen)
#   Screen name contains "Profile"    → GenericProfileScreen
#   Screen name contains "Auth"       → GenericAuthScreen
#   Screen name contains "Login"      → GenericAuthScreen
#   Screen name contains "Signup"     → GenericAuthScreen
#   Screen name contains "Onboarding" → GenericOnboardingScreen
#   Screen name contains "Splash"     → GenericSplashScreen
#   Screen name contains "Tab"        → GenericTabScreen
#   Screen name contains "Chat"       → GenericChatScreen
#   Screen name contains "Message"    → GenericChatScreen
#   Screen name contains "Conversation" → GenericChatScreen
#   Screen name contains "Notification" → GenericNotificationScreen
#   Screen name contains "Inbox"      → GenericNotificationScreen
#   Screen name contains "Alert"      → GenericNotificationScreen
#   Screen name contains "Gallery"    → GenericGalleryScreen
#   Screen name contains "Photos"     → GenericGalleryScreen
#   Screen name contains "Album"      → GenericGalleryScreen
#
#   No match → GenericListScreen (default for data-driven screens)
#
# ─────────────────────────────────────────────────────────────────
# RULE 2: Card component selection from data_models[] fields
# ─────────────────────────────────────────────────────────────────
# When a ListScreen is selected, choose card component based on model fields:
#
#   Model has imageUrl/photo/thumbnail field → ImageCard
#   Model has videoUrl/mediaUrl field        → VideoCard
#   Model has 4+ display fields             → DetailCard
#   Model has rating/stars field             → ListItemCard + RatingBar (trailing)
#   Model has quantity/count field           → ListItemCard + CounterRow (trailing)
#   Feature has FAQ/collapsible sections     → ExpandableCard
#   Feature has order tracking/history       → TimelineItem
#   Feature has featured/banner content      → Carousel (in Dashboard or header)
#   Otherwise                                → ListItemCard (default)
#
# ─────────────────────────────────────────────────────────────────
# RULE 3: Detail screen component selection from data_models[] fields
# ─────────────────────────────────────────────────────────────────
# When a DetailScreen is selected, include optional components based on fields:
#
#   Model has status/state field              → StatusBadge
#   Model has tags/categories list field      → ChipGroup
#   Model has 3+ label-value display fields   → InfoRow
#   Feature allows delete action              → ConfirmDialog
#   Model has grouped related fields          → DetailCard
#
# ─────────────────────────────────────────────────────────────────
# RULE 4: Form field type from data_models[] field types
# ─────────────────────────────────────────────────────────────────
# When a FormScreen is selected, derive FormField types from model fields:
#
#   field type String                         → FieldType.Text
#   field type String (name contains email)   → FieldType.Email
#   field type String (name contains password)→ FieldType.Password
#   field type Int / Long / Double            → FieldType.Number
#   field type Boolean                        → FieldType.Checkbox
#   field type Instant / Date                 → FieldType.Date
#   field is enum or limited set              → FieldType.Dropdown
#   field type String (name contains desc/note/content) → FieldType.MultiLine
#
# ─────────────────────────────────────────────────────────────────
# RULE 5: Dashboard stat derivation
# ─────────────────────────────────────────────────────────────────
# When DashboardScreen is selected:
#
#   For each data_model → create one DashboardStat:
#     - label = model display_name or plural name (e.g. "Assignments")
#     - value = count of items
#     - icon = contextual Material icon
#
#   For each model with date/deadline field → create "Upcoming" quick action
#   For each model with Boolean completion field → create "Completed" stat
#   For each model with progress/percentage field → include ProgressCard
#
# ─────────────────────────────────────────────────────────────────
# RULE 6: Navigation derivation from project-context.json
# ─────────────────────────────────────────────────────────────────
# Bottom nav tabs:
#   For each item in navigation_flows.bottom_nav_items:
#     → Create @Serializable object route
#     → Create NavigationTab(route, displayName, icon)
#     → Map to appropriate screen from features
#
# Detail/form routes (not in bottom nav):
#   For each feature that has both List + Detail screens:
#     → Create @Serializable data class {Model}Detail(val id: String)
#   For each feature that allows create/edit:
#     → Create @Serializable object Create{Model}
#     → Create @Serializable data class Edit{Model}(val id: String)
#
# Navigation flows:
#   For each flow in navigation_flows.flows:
#     → Map source screen to nav.navigate(TargetRoute)
#     → List screens use onItemClick = { nav.navigate({Model}Detail(it.id)) }
#     → Detail screens use onEditClick = { nav.navigate(Edit{Model}(id)) }
#     → Detail screens use onBackClick = { nav.popBackStack() }
#     → Form screens use onSubmit + onBackClick = { nav.popBackStack() }
#
# ─────────────────────────────────────────────────────────────────
# RULE 7: AppOrchestrator phase derivation
# ─────────────────────────────────────────────────────────────────
#   app_config.splash_screen.enabled == true  → Include Splash phase
#   features has "onboarding" feature         → Include Onboarding phase
#   auth_config exists OR features has auth   → Include Auth phase
#   Always                                    → Include Home phase
#
#   Start state:
#     Has splash → AppState.Splash
#     No splash, has onboarding → AppState.Onboarding
#     No splash, no onboarding, has auth → AppState.Auth
#     None of above → AppState.Home
#
# ─────────────────────────────────────────────────────────────────
# RULE 8: Auth screen derivation
# ─────────────────────────────────────────────────────────────────
#   auth_config.social_providers contains "google" → Add SocialButton for Google
#   auth_config.social_providers contains "apple"  → Add SocialButton for Apple
#   auth_config exists                             → Include GenericAuthScreen
#   No auth_config                                 → Skip auth phase entirely
#
# ─────────────────────────────────────────────────────────────────
# RULE 9: Settings screen derivation
# ─────────────────────────────────────────────────────────────────
#   backend_config.config.config_type == "configurable" → GenericSettingsScreen for config
#   ui_design.has_dark_mode == true                     → Add dark mode SwitchRow
#   features has settings/preferences feature           → GenericSettingsScreen

# =============================================================================
# DERIVATION RULES — ViewModel from Screen
# =============================================================================
#
# Every screen gets a ViewModel. Derive ViewModel structure from screen type.
#
# ─────────────────────────────────────────────────────────────────
# RULE 10: ViewModel per screen
# ─────────────────────────────────────────────────────────────────
# For each screen in the implementation plan:
#   → Create {ScreenName}ViewModel in presentation/{feature}/ package
#   → Create {ScreenName}UiState sealed interface in SAME file
#
# UiState always has these variants:
#   sealed interface {Screen}UiState {
#       data object Loading : {Screen}UiState
#       data class Success(...) : {Screen}UiState
#       data class Error(val message: String) : {Screen}UiState
#   }
#
# ViewModel pattern (THIN — observe repository, no business logic):
#   class {Screen}ViewModel(
#       private val {model}Repository: {Model}Repository
#   ) : ViewModel() {
#       val uiState: StateFlow<{Screen}UiState> = combine(
#           repository.items, repository.isLoading, repository.error
#       ) { items, loading, error -> when { ... } }
#       .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Loading)
#   }
#
# ─────────────────────────────────────────────────────────────────
# RULE 11: ViewModel actions per screen type
# ─────────────────────────────────────────────────────────────────
#   ListScreen ViewModel:
#     - fun loadItems()          → repository.loadAll()
#     - fun deleteItem(id)       → repository.delete(id)
#     - fun refresh()            → repository.refresh()
#
#   DetailScreen ViewModel:
#     - fun loadItem(id)         → repository.getById(id)
#     - fun deleteItem(id)       → repository.delete(id)
#
#   FormScreen ViewModel:
#     - fun loadItem(id)         → repository.getById(id)  (edit mode)
#     - fun save(model)          → repository.insert(model)
#     - fun update(model)        → repository.update(model)
#
#   DashboardScreen ViewModel:
#     - fun loadDashboard()      → combine multiple repository flows
#     - Success state holds stats + recent items from each model
#
#   SearchScreen ViewModel:
#     - fun search(query)        → repository.search(query)
#     - fun clearSearch()        → reset to empty
#
#   SettingsScreen ViewModel:
#     - fun loadSettings()       → read from AppSettings
#     - fun updateSetting(k, v)  → write to AppSettings
#
#   AuthScreen ViewModel:
#     - fun signIn(email, pass)  → local auth or backend
#     - fun signInWithSocial()   → authRepository.signInWithSocial()
#     - fun signUp(...)          → create user
#
# ─────────────────────────────────────────────────────────────────
# RULE 12: Screen-to-ViewModel wiring pattern
# ─────────────────────────────────────────────────────────────────
# Every screen composable receives its ViewModel via Koin:
#
#   @Composable
#   fun {ScreenName}(
#       viewModel: {ScreenName}ViewModel = koinViewModel(),
#       onNavigateBack: () -> Unit = {},
#       onItemClick: (String) -> Unit = {},  // if list screen
#   ) {
#       val uiState by viewModel.uiState.collectAsState()
#       when (val state = uiState) {
#           is Loading -> CircularProgressIndicator()
#           is Success -> { /* render with pre-built GenericXxxScreen */ }
#           is Error -> ErrorContent(message = state.message, onRetry = { viewModel.load() })
#       }
#   }

# =============================================================================
# DERIVATION RULES — UseCase from Feature + Model
# =============================================================================
#
# UseCases are thin wrappers. Derive them mechanically from features and models.
#
# ─────────────────────────────────────────────────────────────────
# RULE 13: UseCase derivation from screen type
# ─────────────────────────────────────────────────────────────────
# For each feature, derive use cases from its screens:
#
#   Feature has ListScreen for {Model}:
#     → Get{Model}ListUseCase
#       invoke(): StateFlow<List<{Model}>> = repository.items
#
#   Feature has DetailScreen for {Model}:
#     → View{Model}DetailsUseCase
#       invoke(id: String): {Model}? = repository.getById(id)
#
#   Feature has FormScreen (create) for {Model}:
#     → Create{Model}UseCase
#       invoke(model: {Model}) = repository.insert(model)
#
#   Feature has FormScreen (edit) for {Model}:
#     → Update{Model}UseCase
#       invoke(model: {Model}) = repository.update(model)
#
#   Feature allows delete:
#     → Delete{Model}UseCase
#       invoke(id: String) = repository.delete(id)
#
# ─────────────────────────────────────────────────────────────────
# RULE 14: UseCase derivation from model fields
# ─────────────────────────────────────────────────────────────────
# Scan data_models[].fields for special field patterns:
#
#   Model has field "completed: Boolean" or "done: Boolean":
#     → Mark{Model}CompleteUseCase
#       invoke(id: String) = repository.markComplete(id)
#
#   Model has field "progress: Int" or "progress: Float":
#     → Update{Model}ProgressUseCase
#       invoke(id: String, progress: Int) = repository.updateProgress(id, progress)
#
#   Model has field with date/deadline + feature is Dashboard:
#     → GetUpcoming{Model}sUseCase
#       invoke(): List<{Model}> = repository.getUpcoming()
#
# ─────────────────────────────────────────────────────────────────
# RULE 15: Dashboard-specific use cases
# ─────────────────────────────────────────────────────────────────
# If a DashboardScreen exists:
#
#   → GetDashboardOverviewUseCase
#     Combines counts/stats from all repositories
#
#   For each model with a date field:
#     → GetUpcomingItemsUseCase (or GetUpcoming{Model}sUseCase)
#
# ─────────────────────────────────────────────────────────────────
# RULE 16: Settings/Config use cases
# ─────────────────────────────────────────────────────────────────
# If a SettingsScreen or ConfigScreen exists:
#
#   backend_config.config_type == "configurable":
#     → GetCurrentConfigUseCase
#     → UpdateConfigUseCase
#     → ValidateConfigUseCase (if config needs validation, e.g. URLs)
#
#   Feature has user preferences:
#     → GetSettingsUseCase
#     → UpdateSettingUseCase
#
# ─────────────────────────────────────────────────────────────────
# RULE 17: UseCase file structure
# ─────────────────────────────────────────────────────────────────
# All use cases go in: {package}/domain/usecase/
#
# Each use case is a single class with operator fun invoke():
#   class Get{Model}ListUseCase(
#       private val repository: {Model}Repository
#   ) {
#       operator fun invoke(): StateFlow<List<{Model}>> = repository.items
#   }
#
# Register in Koin as: factoryOf(::Get{Model}ListUseCase)

# =============================================================================
# DERIVATION RULES — Repository from Model
# =============================================================================
#
# ─────────────────────────────────────────────────────────────────
# RULE 18: Repository per data model
# ─────────────────────────────────────────────────────────────────
# For each entry in data_models[]:
#   → Create {Model}Repository interface in data/repositories/{model_lowercase}/
#   → Create {Model}RepositoryImpl class in SAME folder (SEPARATE file)
#
# Repository interface exposes:
#   interface {Model}Repository {
#       val items: StateFlow<List<{Model}>>
#       val isLoading: StateFlow<Boolean>
#       val error: StateFlow<String?>
#       suspend fun loadAll()
#       suspend fun getById(id: String): {Model}?
#       suspend fun insert(model: {Model})
#       suspend fun update(model: {Model})
#       suspend fun delete(id: String)
#   }
#
# Additional methods derived from model fields:
#   Model has "completed: Boolean" → suspend fun markComplete(id: String)
#   Model has "progress: Int"      → suspend fun updateProgress(id: String, progress: Int)
#   Model has date/deadline field   → suspend fun getUpcoming(): List<{Model}>
#   Feature has search              → suspend fun search(query: String): List<{Model}>
#
# ─────────────────────────────────────────────────────────────────
# RULE 19: Repository implementation by backend type
# ─────────────────────────────────────────────────────────────────
#   backend_config.type == "google_sheets":
#     → Constructor: class Impl(private val sheetsApi: SheetsApiService)
#     → loadAll(): sheetsApi.getAll("SheetName") + parse to domain model
#     → StateFlow pattern with MutableStateFlow internally
#
#   backend_config.type == "rest_api":
#     → Constructor: class Impl(private val client: HttpClient)
#     → loadAll(): client.get(endpoint) + parse response
#
#   backend_config.type == null (local-only):
#     → Constructor: class Impl(private val dao: {Model}Dao)
#     → loadAll(): dao.getAll().map { it.toDomain() }
#
# ─────────────────────────────────────────────────────────────────
# RULE 20: Room entity per data model
# ─────────────────────────────────────────────────────────────────
# For each entry in data_models[]:
#   → Create {Model}Entity in data/local/entity/
#   → Create {Model}Dao in data/local/dao/
#   → Add entity to AppDatabase @Database entities array
#   → Add abstract val {model}Dao: {Model}Dao to AppDatabase
#   → Bump DB version
#
# Entity field mapping from data_models[].fields:
#   String        → var field: String = ""
#   Int           → var field: Int = 0
#   Boolean       → var field: Boolean = false
#   Instant/Date  → var field: Long = 0L  (store as epoch millis)
#   Float/Double  → var field: Double = 0.0
#
# Always add:
#   @PrimaryKey(autoGenerate = true) var id: Int = 0
#
# Create mappers in entity file:
#   fun {Model}Entity.toDomain(): {Model}
#   fun {Model}.toEntity(): {Model}Entity
#
# ─────────────────────────────────────────────────────────────────
# RULE 21: Domain model per data model
# ─────────────────────────────────────────────────────────────────
# For each entry in data_models[]:
#   → Create data class {Model} in domain/model/
#   → Use Kotlin types directly (String, Int, Boolean, Instant)
#   → All fields from data_models[].fields with sensible defaults

# =============================================================================
# DERIVATION RULES — DI Registration
# =============================================================================
#
# ─────────────────────────────────────────────────────────────────
# RULE 22: Koin registration derivation
# ─────────────────────────────────────────────────────────────────
# After all classes are derived, register in Koin appModule():
#
# For each {Model}Dao:
#   single { get<AppDatabase>().{model}Dao }
#
# For each {Model}Repository:
#   singleOf(::{Model}RepositoryImpl) { bind<{Model}Repository>() }
#
# For each UseCase:
#   factoryOf(::{UseCaseName})
#
# For each ViewModel:
#   viewModelOf(::{ViewModelName})
#
# For AuthRepository (if auth_config exists):
#   single { AuthRepository(database = get(), backendHandler = getOrNull()) }

# =============================================================================
# COMPLETE DERIVATION EXAMPLE
# =============================================================================
#
# Given project-context.json:
#   data_models: [{ name: "Assignment", fields: ["title: String", "dueDate: Instant", "completed: Boolean"] }]
#   features: [{ name: "Assignment Tracking", screens: [
#     { name: "AssignmentListScreen" },
#     { name: "AssignmentDetailScreen" }
#   ]}]
#   navigation_flows: { bottom_nav_items: ["Dashboard", "Assignments"] }
#
# Derivation produces:
#
#   Domain:
#     domain/model/Assignment.kt                    (RULE 21)
#
#   Data:
#     data/local/entity/AssignmentEntity.kt          (RULE 20)
#     data/local/dao/AssignmentDao.kt                (RULE 20)
#     data/repositories/assignment/AssignmentRepository.kt      (RULE 18)
#     data/repositories/assignment/AssignmentRepositoryImpl.kt  (RULE 19)
#
#   UseCases:
#     domain/usecase/GetAssignmentListUseCase.kt     (RULE 13: ListScreen)
#     domain/usecase/ViewAssignmentDetailsUseCase.kt (RULE 13: DetailScreen)
#     domain/usecase/MarkAssignmentCompleteUseCase.kt(RULE 14: completed: Boolean)
#     domain/usecase/GetUpcomingAssignmentsUseCase.kt(RULE 14: dueDate + Dashboard)
#
#   ViewModels:
#     presentation/assignmentlist/AssignmentListViewModel.kt   (RULE 10)
#     presentation/assignmentdetail/AssignmentDetailViewModel.kt (RULE 10)
#
#   Screens (wrapper composables that call pre-built GenericXxxScreen):
#     presentation/assignmentlist/AssignmentListScreen.kt      (RULE 1: "List")
#       → calls GenericListScreen(
#             title = "Assignments",
#             items = state.assignments,
#             itemContent = { assignment ->
#                 ListItemCard(                                 (RULE 2: no image field)
#                     title = assignment.title,
#                     subtitle = "Due: ${assignment.dueDate}",
#                     trailingContent = { if (assignment.completed) Icon(Icons.Default.Check) }
#                 )
#             },
#             onItemClick = { onNavigateToDetail(it.id) }
#         )
#     presentation/assignmentdetail/AssignmentDetailScreen.kt  (RULE 1: "Detail")
#       → calls GenericDetailScreen(
#             title = "Assignment",
#             item = state.assignment,
#             detailContent = { assignment ->
#                 InfoRow(label = "Title", value = assignment.title)  (RULE 3: 3+ fields)
#                 InfoRow(label = "Due Date", value = assignment.dueDate.toString())
#                 InfoRow(label = "Status", value = if (assignment.completed) "Done" else "Pending")
#             },
#             onBackClick = { onNavigateBack() },
#             onEditClick = { onNavigateToEdit(it.id) }
#         )
#
#   Navigation:
#     @Serializable object Assignments                         (RULE 6: bottom nav)
#     @Serializable data class AssignmentDetail(val id: String)(RULE 6: list+detail)
#     NavigationTab(Assignments, "Assignments", Icons.Default.Assignment) (RULE 6)
#
#   DI:
#     single { get<AppDatabase>().assignmentDao }              (RULE 22)
#     singleOf(::AssignmentRepositoryImpl) { bind<AssignmentRepository>() } (RULE 22)
#     factoryOf(::GetAssignmentListUseCase)                    (RULE 22)
#     factoryOf(::ViewAssignmentDetailsUseCase)                (RULE 22)
#     factoryOf(::MarkAssignmentCompleteUseCase)               (RULE 22)
#     viewModelOf(::AssignmentListViewModel)                   (RULE 22)
#     viewModelOf(::AssignmentDetailViewModel)                 (RULE 22)
