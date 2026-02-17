# App Architecture Manifest
# AI code generator: read this FIRST before any other manifest.
# This defines the project structure, architecture layers, and file placement rules.
# Template package: com.mismaiti (replaced with {app_package} by backend in Phase 0)

# =============================================================================
# GOLDEN RULE
# =============================================================================
#
# core/ = PRE-BUILT. DO NOT MODIFY, DO NOT CREATE FILES INSIDE core/.
# All generated code goes OUTSIDE core/ in app-specific packages.
#
# core/ is a library of reusable infrastructure (screens, components, database,
# auth, DI, navigation). Generated code IMPORTS from core/ but never edits it.

# =============================================================================
# PROJECT STRUCTURE
# =============================================================================
#
# {app_package}/                          (com.mismaiti before Phase 0 refactor)
# ├── App.kt                              ← App entry point (MODIFY to wire AppOrchestrator)
# │
# ├── core/                               ← PRE-BUILT — DO NOT MODIFY
# │   ├── data/
# │   │   ├── auth/                       ← Social auth (Google/Apple sign-in)
# │   │   │   ├── SocialAuthProvider.kt        expect suspend fun signInWithSocialProvider()
# │   │   │   ├── SocialAuthResult.kt          SocialAuthResult, AuthProvider enum
# │   │   │   └── AuthRepository.kt            AuthRepository, SocialAuthBackendHandler
# │   │   ├── chat/                       ← AI Chat Service (pluggable AI providers)
# │   │   │   └── AiChatService.kt             AiChatService interface, AiChatResponse
# │   │   └── local/                      ← Room database + settings
# │   │       ├── AppDatabase.kt               @Database definition
# │   │       ├── AppDatabaseConstructor.kt    KMP constructor (auto-generated)
# │   │       ├── AppSettings.kt               Key-value storage interface
# │   │       └── model/
# │   │           ├── UserEntity.kt            Pre-built user table
# │   │           └── UserDao.kt               Pre-built user queries
# │   ├── di/
# │   │   └── PlatformModule.kt           ← expect fun platformModule() + networkModule()
# │   └── presentation/
# │       ├── components/                 ← Reusable UI components (23 total)
# │       │   ├── ListItemCard, ImageCard, VideoCard, DetailCard, StatCard
# │       │   ├── ProgressCard, EmptyState, ErrorScreen, LoadingButton
# │       │   ├── SectionHeader, SearchBar, InfoRow, StatusBadge
# │       │   ├── ChipGroup, ConfirmDialog, SwitchRow
# │       │   ├── RatingBar, CounterRow, ExpandableCard          ← NEW
# │       │   ├── TimelineItem, Carousel                         ← NEW
# │       ├── navigation/                ← AppOrchestrator, MainScaffold, Routes, NavigationTab
# │       └── screens/                   ← Generic screens (14 total)
# │           ├── GenericListScreen, GenericDetailScreen, GenericFormScreen
# │           ├── GenericSearchScreen, GenericDashboardScreen, GenericSettingsScreen
# │           ├── GenericProfileScreen, GenericAuthScreen, GenericSplashScreen
# │           ├── GenericOnboardingScreen, GenericTabScreen
# │           ├── GenericChatScreen, GenericNotificationScreen    ← NEW
# │           └── GenericGalleryScreen                            ← NEW
# │
# ├── di/
# │   └── AppModule.kt                   ← MODIFY to register new DAOs, repos, VMs
# │
# ├── presentation/
# │   └── theme/                          ← App theme (AppColors, AppTheme)
# │
# │── ── ── ── ── ── ── ── ── ── ── ── ── ── ── ── ── ── ── ── ── ──
# │   BELOW THIS LINE = GENERATED CODE (created per project-context.json)
# │── ── ── ── ── ── ── ── ── ── ── ── ── ── ── ── ── ── ── ── ── ──
# │
# ├── data/                               ← App-specific data layer
# │   ├── local/
# │   │   ├── entity/                    ← Room entities (one file per model)
# │   │   │   └── {Model}Entity.kt
# │   │   └── dao/                       ← Room DAOs (one file per model)
# │   │       └── {Model}Dao.kt
# │   ├── remote/                         ← API services (if backend exists)
# │   │   └── {Service}ApiService.kt
# │   └── repository/                     ← Repository interface + impl (grouped per model)
# │       └── {model}/
# │           ├── {Model}Repository.kt        interface
# │           └── {Model}RepositoryImpl.kt    implementation
# │
# ├── domain/                             ← App-specific domain layer
# │   ├── model/                          ← Domain models (one file per model)
# │   │   └── {Model}.kt
# │   └── usecase/                        ← Use cases (one file per use case)
# │       └── {Action}{Model}UseCase.kt
# │
# └── presentation/                       ← App-specific presentation layer
#     └── {feature}/                      ← One package per feature/screen
#         ├── {Screen}ViewModel.kt            ViewModel + UiState (same file)
#         └── {Screen}Screen.kt               Composable wrapper that calls GenericXxxScreen
#
#
# Platform-specific (androidMain / iosMain):
# ├── core/                               ← PRE-BUILT — DO NOT MODIFY
# │   ├── data/auth/SocialAuthProvider.{platform}.kt
# │   ├── di/PlatformModule.{platform}.kt
# │   └── settings/{Platform}AppSettings.kt
# └── presentation/theme/AppTheme.{platform}.kt

# =============================================================================
# ARCHITECTURE LAYERS
# =============================================================================
#
# Presentation → Domain → Data
#
# ┌─────────────────────────────────────────────────────────────┐
# │ PRESENTATION (UI)                                           │
# │                                                             │
# │  Screen composable (wrapper)                                │
# │    → observes ViewModel.uiState (StateFlow)                 │
# │    → calls GenericXxxScreen from core/ with data            │
# │    → passes navigation callbacks                            │
# │                                                             │
# │  ViewModel (THIN — no business logic)                       │
# │    → combines repository StateFlows into UiState            │
# │    → delegates actions to use cases / repository            │
# │                                                             │
# ├─────────────────────────────────────────────────────────────┤
# │ DOMAIN (business logic)                                     │
# │                                                             │
# │  UseCase (thin wrapper, single responsibility)              │
# │    → calls repository methods                               │
# │    → operator fun invoke() for clean call sites             │
# │                                                             │
# │  Domain Model (pure data class, no Room annotations)        │
# │    → used by ViewModel and UseCase                          │
# │    → mapped to/from Entity via extension functions          │
# │                                                             │
# ├─────────────────────────────────────────────────────────────┤
# │ DATA (persistence + network)                                │
# │                                                             │
# │  Repository (interface + impl)                              │
# │    → owns MutableStateFlow for items, isLoading, error      │
# │    → impl talks to DAO (local) or API service (remote)      │
# │                                                             │
# │  Entity (Room @Entity, mutable vars with defaults)          │
# │    → maps to/from Domain Model                              │
# │                                                             │
# │  DAO (Room @Dao interface, SQL queries)                     │
# │                                                             │
# │  API Service (Ktor HttpClient wrapper, if backend exists)   │
# │                                                             │
# └─────────────────────────────────────────────────────────────┘
#
# Dependency direction: Presentation → Domain → Data
# Domain layer has NO dependencies on Room, Ktor, or Compose.
# Data layer has NO dependencies on Compose.

# =============================================================================
# FILE PLACEMENT RULES
# =============================================================================
#
# RULE P1: Entity files
# ─────────────────────
#   Location: data/local/entity/{Model}Entity.kt
#   Package: {app_package}.data.local.entity
#   Contains: @Entity class + toDomain() and toEntity() mapper extensions
#   One file per data model.
#
# RULE P2: DAO files
# ──────────────────
#   Location: data/local/dao/{Model}Dao.kt
#   Package: {app_package}.data.local.dao
#   Contains: @Dao interface with queries
#   One file per data model.
#
# RULE P3: Repository files (grouped per model)
# ──────────────────────────────────────────────
#   Location: data/repository/{model_lowercase}/{Model}Repository.kt      (interface)
#             data/repository/{model_lowercase}/{Model}RepositoryImpl.kt  (implementation)
#   Package: {app_package}.data.repository.{model_lowercase}
#   Interface + Impl are SEPARATE files in the SAME folder.
#   One folder per data model.
#
#   Example:
#     data/repository/assignment/AssignmentRepository.kt
#     data/repository/assignment/AssignmentRepositoryImpl.kt
#
# RULE P4: API service files (if backend exists)
# ───────────────────────────────────────────────
#   Location: data/remote/{Service}ApiService.kt
#   Package: {app_package}.data.remote
#   Contains: class with Ktor HttpClient calls
#   One file per API service (can serve multiple models).
#
# RULE P5: Domain model files
# ────────────────────────────
#   Location: domain/model/{Model}.kt
#   Package: {app_package}.domain.model
#   Contains: data class with val fields and sensible defaults
#   One file per data model.
#
# RULE P6: UseCase files
# ───────────────────────
#   Location: domain/usecase/{Action}{Model}UseCase.kt
#   Package: {app_package}.domain.usecase
#   Contains: single class with operator fun invoke()
#   One file per use case.
#
#   Naming convention:
#     Get{Model}ListUseCase        — list items
#     View{Model}DetailsUseCase    — get single item
#     Create{Model}UseCase         — insert new
#     Update{Model}UseCase         — update existing
#     Delete{Model}UseCase         — delete
#     Mark{Model}CompleteUseCase   — toggle completion
#     GetUpcoming{Model}sUseCase   — filtered by date
#
# RULE P7: ViewModel + UiState files
# ────────────────────────────────────
#   Location: presentation/{feature_lowercase}/{Screen}ViewModel.kt
#   Package: {app_package}.presentation.{feature_lowercase}
#   Contains: ViewModel class + UiState sealed interface (SAME file)
#   One file per screen.
#
#   Example:
#     presentation/assignments/AssignmentListViewModel.kt
#       → class AssignmentListViewModel + sealed interface AssignmentListUiState
#
# RULE P8: Screen wrapper files
# ──────────────────────────────
#   Location: presentation/{feature_lowercase}/{Screen}Screen.kt
#   Package: {app_package}.presentation.{feature_lowercase}
#   Contains: @Composable function that:
#     1. Gets ViewModel via koinViewModel()
#     2. Collects uiState
#     3. Calls pre-built GenericXxxScreen from core/ with the data
#   One file per screen.
#
#   Example:
#     presentation/assignments/AssignmentListScreen.kt
#       → @Composable fun AssignmentListScreen(viewModel = koinViewModel(), ...)
#       → calls GenericListScreen(title, items, itemContent = { ListItemCard(...) })
#
# RULE P9: Routes file
# ─────────────────────
#   Location: presentation/navigation/AppRoutes.kt
#   Package: {app_package}.presentation.navigation
#   Contains: object AppRoutes with all @Serializable route definitions
#   Single file for the entire app.
#
# RULE P10: Feature grouping
# ───────────────────────────
#   Each feature gets ONE folder under presentation/ containing:
#     - {Screen}ViewModel.kt
#     - {Screen}Screen.kt
#
#   Features with multiple screens (e.g., List + Detail + Form) share ONE folder:
#     presentation/assignments/
#       ├── AssignmentListViewModel.kt
#       ├── AssignmentListScreen.kt
#       ├── AssignmentDetailViewModel.kt
#       ├── AssignmentDetailScreen.kt
#       ├── AssignmentFormViewModel.kt
#       └── AssignmentFormScreen.kt

# =============================================================================
# WHAT TO MODIFY vs WHAT NOT TO TOUCH
# =============================================================================
#
# ✅ MODIFY these files:
#   App.kt                    → Wire AppOrchestrator with routes, tabs, screens
#   di/AppModule.kt           → Register new DAOs, repositories, use cases, ViewModels
#
# ✅ CREATE files in these locations:
#   data/local/entity/        → New Room entities
#   data/local/dao/           → New Room DAOs
#   data/repository/{model}/  → New repositories (interface + impl)
#   data/remote/              → New API services
#   domain/model/             → New domain models
#   domain/usecase/           → New use cases
#   presentation/{feature}/   → New ViewModels + screen wrappers
#   presentation/navigation/  → AppRoutes.kt (route definitions)
#
# ❌ DO NOT MODIFY anything under core/:
#   core/data/                → Pre-built database, auth, settings
#   core/di/                  → Pre-built platform modules
#   core/presentation/        → Pre-built screens, components, navigation
#
# ❌ DO NOT MODIFY AppDatabase.kt directly. Instead:
#   → Add entities and DAOs to di/AppModule.kt registration
#   → The plan generator will output instructions to update AppDatabase
#     (add entity to array, add DAO abstract val, bump version)
#   → This is the ONE exception where core/ gets a minimal edit

# =============================================================================
# REQUIRED IMPORTS PER FILE TYPE
# =============================================================================
#
# Each generated file type needs specific framework + cross-phase imports.
# Use these exact imports — do NOT guess or use alternatives.
#
# ─────────────────────────────────────────────────────────────────
# Entity file (data/local/entity/{Model}Entity.kt)
# ─────────────────────────────────────────────────────────────────
# ```kotlin
# package {app_package}.data.local.entity
#
# // Framework
# import androidx.room.ColumnInfo
# import androidx.room.Entity
# import androidx.room.PrimaryKey
#
# // Cross-phase (Phase 2 — add when mappers are added)
# import {app_package}.domain.model.{Model}
# ```
#
# ─────────────────────────────────────────────────────────────────
# DAO file (data/local/dao/{Model}Dao.kt)
# ─────────────────────────────────────────────────────────────────
# ```kotlin
# package {app_package}.data.local.dao
#
# // Framework
# import androidx.room.Dao
# import androidx.room.Query
# import androidx.room.Upsert
#
# // Cross-phase (Phase 1 entity)
# import {app_package}.data.local.entity.{Model}Entity
# ```
#
# ─────────────────────────────────────────────────────────────────
# Domain model file (domain/model/{Model}.kt)
# ─────────────────────────────────────────────────────────────────
# ```kotlin
# package {app_package}.domain.model
#
# // Framework (only if model has timestamp fields)
# import kotlin.time.Duration
# import kotlin.time.TimeSource
# // For Instant fields:
# import kotlinx.datetime.Instant
# ```
# Note: most domain models need NO imports (pure data classes).
#
# ─────────────────────────────────────────────────────────────────
# Repository interface (data/repository/{model}/{Model}Repository.kt)
# ─────────────────────────────────────────────────────────────────
# ```kotlin
# package {app_package}.data.repository.{model_lowercase}
#
# // Framework
# import kotlinx.coroutines.flow.StateFlow
#
# // Cross-phase (Phase 2 domain model)
# import {app_package}.domain.model.{Model}
# ```
#
# ─────────────────────────────────────────────────────────────────
# Repository impl (data/repository/{model}/{Model}RepositoryImpl.kt)
# ─────────────────────────────────────────────────────────────────
# ```kotlin
# package {app_package}.data.repository.{model_lowercase}
#
# // Framework
# import kotlinx.coroutines.flow.MutableStateFlow
# import kotlinx.coroutines.flow.StateFlow
# import kotlinx.coroutines.flow.asStateFlow
#
# // Cross-phase (Phase 1 DAO, Phase 2 domain model + mappers)
# import {app_package}.data.local.dao.{Model}Dao
# import {app_package}.data.local.entity.toDomain   // mapper extension
# import {app_package}.data.local.entity.toEntity   // mapper extension
# import {app_package}.domain.model.{Model}
# ```
#
# If backend exists (Ktor):
# ```kotlin
# import io.ktor.client.HttpClient
# import io.ktor.client.call.body
# import io.ktor.client.request.get
# import io.ktor.client.request.post
# import io.ktor.client.request.setBody
# import io.ktor.http.ContentType
# import io.ktor.http.contentType
# ```
#
# ─────────────────────────────────────────────────────────────────
# API service file (data/remote/{Service}ApiService.kt)
# ─────────────────────────────────────────────────────────────────
# ```kotlin
# package {app_package}.data.remote
#
# // Framework
# import io.ktor.client.HttpClient
# import io.ktor.client.call.body
# import io.ktor.client.request.get
# import io.ktor.client.request.post
# import io.ktor.client.request.put
# import io.ktor.client.request.delete
# import io.ktor.client.request.setBody
# import io.ktor.http.ContentType
# import io.ktor.http.contentType
# import kotlinx.serialization.Serializable
# ```
#
# ─────────────────────────────────────────────────────────────────
# UseCase file (domain/usecase/{Action}{Model}UseCase.kt)
# ─────────────────────────────────────────────────────────────────
# ```kotlin
# package {app_package}.domain.usecase
#
# // Framework (only if returning StateFlow)
# import kotlinx.coroutines.flow.StateFlow
#
# // Cross-phase (Phase 2 domain model, Phase 3 repository interface)
# import {app_package}.domain.model.{Model}
# import {app_package}.data.repository.{model_lowercase}.{Model}Repository
# ```
#
# ─────────────────────────────────────────────────────────────────
# ViewModel file (presentation/{feature}/{Screen}ViewModel.kt)
# ─────────────────────────────────────────────────────────────────
# ```kotlin
# package {app_package}.presentation.{feature_lowercase}
#
# // Framework
# import androidx.lifecycle.ViewModel
# import androidx.lifecycle.viewModelScope
# import kotlinx.coroutines.flow.SharingStarted
# import kotlinx.coroutines.flow.StateFlow
# import kotlinx.coroutines.flow.combine
# import kotlinx.coroutines.flow.stateIn
# import kotlinx.coroutines.flow.MutableStateFlow
# import kotlinx.coroutines.flow.asStateFlow
# import kotlinx.coroutines.launch
#
# // Cross-phase (Phase 2 domain model, Phase 3 repository, Phase 4 use cases)
# import {app_package}.domain.model.{Model}
# import {app_package}.data.repository.{model_lowercase}.{Model}Repository
# // OR use cases if used:
# import {app_package}.domain.usecase.Get{Model}ListUseCase
# import {app_package}.domain.usecase.Delete{Model}UseCase
# ```
#
# ─────────────────────────────────────────────────────────────────
# Screen wrapper file (presentation/{feature}/{Screen}Screen.kt)
# ─────────────────────────────────────────────────────────────────
# ```kotlin
# package {app_package}.presentation.{feature_lowercase}
#
# // Framework
# import androidx.compose.runtime.Composable
# import androidx.compose.runtime.collectAsState
# import androidx.compose.runtime.getValue
# import androidx.compose.material3.CircularProgressIndicator
# import androidx.compose.material.icons.Icons
# import androidx.compose.material.icons.filled.*    // for specific icons
# import org.koin.compose.viewmodel.koinViewModel
#
# // Cross-phase (Phase 5 ViewModel — same package, no import needed if same file)
# // import {app_package}.presentation.{feature_lowercase}.{Screen}ViewModel
# // import {app_package}.presentation.{feature_lowercase}.{Screen}UiState
#
# // Cross-phase (Phase 2 domain model — for type references)
# import {app_package}.domain.model.{Model}
#
# // Pre-built screens from core/ (pick the one this screen uses)
# import {app_package}.core.presentation.screens.GenericListScreen
# import {app_package}.core.presentation.screens.GenericDetailScreen
# import {app_package}.core.presentation.screens.GenericFormScreen
# import {app_package}.core.presentation.screens.GenericDashboardScreen
# import {app_package}.core.presentation.screens.GenericSearchScreen
# import {app_package}.core.presentation.screens.GenericSettingsScreen
# import {app_package}.core.presentation.screens.GenericProfileScreen
# import {app_package}.core.presentation.screens.GenericAuthScreen
# import {app_package}.core.presentation.screens.GenericSplashScreen
# import {app_package}.core.presentation.screens.GenericOnboardingScreen
# import {app_package}.core.presentation.screens.GenericTabScreen
# import {app_package}.core.presentation.screens.GenericChatScreen
# import {app_package}.core.presentation.screens.GenericNotificationScreen
# import {app_package}.core.presentation.screens.GenericGalleryScreen
#
# // Pre-built screen data classes (import only what's used)
# import {app_package}.core.presentation.screens.DashboardStat
# import {app_package}.core.presentation.screens.QuickAction
# import {app_package}.core.presentation.screens.FormField
# import {app_package}.core.presentation.screens.FieldType
# import {app_package}.core.presentation.screens.SettingsSection
# import {app_package}.core.presentation.screens.SettingsItem
# import {app_package}.core.presentation.screens.ProfileStat
# import {app_package}.core.presentation.screens.ProfileMenuSection
# import {app_package}.core.presentation.screens.ProfileMenuItem
# import {app_package}.core.presentation.screens.OnboardingPage
# import {app_package}.core.presentation.screens.SocialButton
# import {app_package}.core.presentation.screens.TabItem
# import {app_package}.core.presentation.screens.MenuAction
# import {app_package}.core.presentation.screens.SearchFilterChip
# import {app_package}.core.presentation.screens.ChatMessage
# import {app_package}.core.presentation.screens.ChatBubbleAlignment
# import {app_package}.core.presentation.screens.NotificationItem
# import {app_package}.core.presentation.screens.GalleryItem
#
# // Pre-built components (import only what's used)
# import {app_package}.core.presentation.components.ListItemCard
# import {app_package}.core.presentation.components.ImageCard
# import {app_package}.core.presentation.components.HorizontalImageCard
# import {app_package}.core.presentation.components.VideoCard
# import {app_package}.core.presentation.components.DetailCard
# import {app_package}.core.presentation.components.DetailRow
# import {app_package}.core.presentation.components.StatCard
# import {app_package}.core.presentation.components.ProgressCard
# import {app_package}.core.presentation.components.InfoRow
# import {app_package}.core.presentation.components.StatusBadge
# import {app_package}.core.presentation.components.ChipGroup
# import {app_package}.core.presentation.components.ConfirmDialog
# import {app_package}.core.presentation.components.SwitchRow
# import {app_package}.core.presentation.components.SearchBar
# import {app_package}.core.presentation.components.LoadingButton
# import {app_package}.core.presentation.components.SecondaryButton
# import {app_package}.core.presentation.components.SectionHeader
# import {app_package}.core.presentation.components.EmptyStateContent
# import {app_package}.core.presentation.components.ErrorContent
# import {app_package}.core.presentation.components.RatingBar
# import {app_package}.core.presentation.components.CounterRow
# import {app_package}.core.presentation.components.ExpandableCard
# import {app_package}.core.presentation.components.TimelineItem
# import {app_package}.core.presentation.components.TimelineEntry
# import {app_package}.core.presentation.components.Carousel
# import {app_package}.core.presentation.components.CarouselItem
# ```
#
# ─────────────────────────────────────────────────────────────────
# Routes file (presentation/navigation/AppRoutes.kt)
# ─────────────────────────────────────────────────────────────────
# ```kotlin
# package {app_package}.presentation.navigation
#
# // Framework
# import kotlinx.serialization.Serializable
# ```
#
# ─────────────────────────────────────────────────────────────────
# App.kt (wiring AppOrchestrator)
# ─────────────────────────────────────────────────────────────────
# ```kotlin
# package {app_package}
#
# // Framework
# import androidx.compose.runtime.*
# import androidx.compose.material.icons.Icons
# import androidx.compose.material.icons.filled.*
# import androidx.navigation.compose.composable
# import androidx.navigation.toRoute
#
# // Pre-built navigation from core/
# import {app_package}.core.presentation.navigation.AppOrchestrator
# import {app_package}.core.presentation.navigation.AppState
# import {app_package}.core.presentation.navigation.NavigationTab
#
# // App routes
# import {app_package}.presentation.navigation.AppRoutes
#
# // App screens (from Phase 6)
# import {app_package}.presentation.{feature}.{Screen}Screen
# ```
#
# ─────────────────────────────────────────────────────────────────
# AppModule.kt (DI registration)
# ─────────────────────────────────────────────────────────────────
# ```kotlin
# package {app_package}.di
#
# // Framework
# import org.koin.core.module.Module
# import org.koin.core.module.dsl.singleOf
# import org.koin.core.module.dsl.factoryOf
# import org.koin.core.module.dsl.viewModelOf
# import org.koin.dsl.bind
# import org.koin.dsl.module
#
# // Pre-built from core/
# import {app_package}.core.data.local.AppDatabase
# import {app_package}.core.data.auth.AuthRepository
# import {app_package}.core.di.platformModule
# import {app_package}.core.di.networkModule
#
# // Cross-phase (all phases — DAOs, repos, use cases, VMs)
# import {app_package}.data.local.dao.{Model}Dao
# import {app_package}.data.repository.{model_lowercase}.{Model}Repository
# import {app_package}.data.repository.{model_lowercase}.{Model}RepositoryImpl
# import {app_package}.domain.usecase.Get{Model}ListUseCase
# import {app_package}.presentation.{feature_lowercase}.{Screen}ViewModel
# ```

# =============================================================================
# AVAILABLE DEPENDENCIES (already in gradle — do NOT add these)
# =============================================================================
#
# Compose UI:
#   compose.ui, compose.foundation, compose.material3
#   compose.materialIconsExtended, compose.runtime
#   compose.components.resources, compose.ui.tooling.preview
#
# Navigation:
#   androidx-navigation-compose (2.9.2)
#   Type-safe: @Serializable routes, composable<Route>, toRoute<Route>()
#
# Architecture:
#   androidx-lifecycle-viewmodelCompose
#   androidx-lifecycle-runtimeCompose
#
# Database:
#   androidx-room-runtime (2.8.4)
#   androidx-sqlite-bundled (BundledSQLiteDriver)
#   KSP room compiler configured
#
# Dependency Injection:
#   koin-core (4.1.1)
#   koin-compose, koin-compose-viewmodel
#   koinViewModel() for Compose injection
#
# Networking:
#   ktor-client-core (3.4.0)
#   ktor-client-content-negotiation, ktor-serialization-json
#   ktor-client-logging
#   Platform engines: ktor-client-okhttp (Android), ktor-client-darwin (iOS)
#
# Serialization:
#   kotlinx-serialization-json
#   @Serializable annotation available
#
# Image Loading:
#   coil-compose, coil-network-ktor (3.3.0)
#   AsyncImage composable available
#
# Logging:
#   kermit (co.touchlab.kermit.Logger)
#
# Coroutines:
#   kotlinx-coroutines-core
#   StateFlow, MutableStateFlow, combine, stateIn
#
# Date/Time:
#   kotlinx-datetime
#   kotlin.time.Instant (prefer this for timestamps)
#
# Android-only:
#   androidx-credentials (1.5.0) — Google Sign-In
#   googleid (1.1.1) — Google ID token
#   androidx-activity-compose

# =============================================================================
# KOTLIN / COMPOSE VERSION CONSTRAINTS
# =============================================================================
#
# Kotlin: 2.3.10
# Compose Multiplatform: 1.10.1
# Targets: Android (JVM 11, minSdk 24) + iOS (arm64, simulatorArm64)
#
# KMP rules:
#   - Use expect/actual for platform-specific code
#   - Use @Serializable (not Parcelable) for route args
#   - Use BundledSQLiteDriver (not Android-only SQLite)
#   - Use koinViewModel() (not viewModel() or hiltViewModel())
#   - NEVER use arguments?.getString() for navigation — use toRoute<T>()
#   - Use kotlin.time.Instant for timestamps (NOT java.time or kotlinx.datetime.Instant)

# =============================================================================
# COMPLETE FILE GENERATION EXAMPLE
# =============================================================================
#
# Given project-context.json with:
#   data_models: [{ name: "Assignment", fields: [...] }]
#   features: [{ name: "assignments", screens: ["AssignmentList", "AssignmentDetail"] }]
#
# Generated files:
#
#   data/local/entity/AssignmentEntity.kt
#     package {app_package}.data.local.entity
#     → @Entity class + toDomain() + toEntity()
#
#   data/local/dao/AssignmentDao.kt
#     package {app_package}.data.local.dao
#     → @Dao interface
#
#   data/repository/assignment/AssignmentRepository.kt
#     package {app_package}.data.repository.assignment
#     → interface AssignmentRepository
#
#   data/repository/assignment/AssignmentRepositoryImpl.kt
#     package {app_package}.data.repository.assignment
#     → class AssignmentRepositoryImpl(dao) : AssignmentRepository
#
#   domain/model/Assignment.kt
#     package {app_package}.domain.model
#     → data class Assignment(...)
#
#   domain/usecase/GetAssignmentListUseCase.kt
#     package {app_package}.domain.usecase
#     → class GetAssignmentListUseCase(repository)
#
#   domain/usecase/ViewAssignmentDetailsUseCase.kt
#     package {app_package}.domain.usecase
#     → class ViewAssignmentDetailsUseCase(repository)
#
#   presentation/assignments/AssignmentListViewModel.kt
#     package {app_package}.presentation.assignments
#     → class AssignmentListViewModel + sealed interface AssignmentListUiState
#
#   presentation/assignments/AssignmentListScreen.kt
#     package {app_package}.presentation.assignments
#     → @Composable fun AssignmentListScreen(viewModel = koinViewModel(), ...)
#     → calls GenericListScreen(...) from core/
#
#   presentation/assignments/AssignmentDetailViewModel.kt
#     package {app_package}.presentation.assignments
#     → class AssignmentDetailViewModel + sealed interface AssignmentDetailUiState
#
#   presentation/assignments/AssignmentDetailScreen.kt
#     package {app_package}.presentation.assignments
#     → @Composable fun AssignmentDetailScreen(viewModel = koinViewModel(), ...)
#     → calls GenericDetailScreen(...) from core/
#
#   presentation/navigation/AppRoutes.kt
#     package {app_package}.presentation.navigation
#     → object AppRoutes { @Serializable object Assignments; @Serializable data class AssignmentDetail(val id: String) }
#
#   Modified files:
#     core/data/local/AppDatabase.kt  → add AssignmentEntity to entities, add assignmentDao
#     di/AppModule.kt                 → register DAO, repository, use cases, ViewModels
#     App.kt                          → wire AppOrchestrator with tabs, routes, screens

# =============================================================================
# MANIFEST LOADING ORDER
# =============================================================================
#
# System prompt should inject manifests in this order:
#   1. THIS FILE (MANIFEST.md)         — architecture, structure, placement rules
#   2. core/data/MANIFEST.md           — pre-built data APIs (database, auth, settings, DI)
#   3. core/presentation/MANIFEST.md   — pre-built UI APIs (screens, components, navigation)
#
# The data and presentation manifests contain:
#   - Full API signatures (import + function/class signatures)
#   - Derivation rules (how to generate code from project-context.json)
#   - Usage examples

# =============================================================================
# IMPLEMENTATION PHASES
# =============================================================================
#
# The implementation plan MUST be structured as an ordered list of phases.
# The backend executes phases sequentially — one API call per phase.
# Each phase produces specific files. Later phases depend on earlier ones.
#
# ─────────────────────────────────────────────────────────────────
# PHASE 0: Package Refactor (backend handles — NOT an LLM call)
# ─────────────────────────────────────────────────────────────────
# The template ships with placeholder package: com.mismaiti
# Before any code generation, the backend refactors ALL files:
#
#   - Rename com.mismaiti → {app_package} from project-context.json
#     (e.g., com.mismaiti → com.acme.taskmanager)
#   - Update all package declarations, import statements, directory paths
#   - Update Android manifest, build.gradle.kts applicationId
#   - Update iOS bundle identifier references
#
# This is a find-and-replace operation done by the backend, NOT by LLM.
# After Phase 0, all manifests and code use the real app package.
#
# All subsequent phases and Qwen-Coder prompts use the REAL package name.
# The plan generator should use {app_package} placeholder in the JSON plan,
# and the backend substitutes the actual package before sending to Qwen-Coder.
#
# project-context.json field:
#   app_config.package_name: "com.acme.taskmanager"
#
# ─────────────────────────────────────────────────────────────────
# PHASE 1: Database Layer (Entities + DAOs)
# ─────────────────────────────────────────────────────────────────
# Input:  Root MANIFEST + Data MANIFEST + project-context.json
# Output: For each data_model:
#           - data/local/entity/{Model}Entity.kt
#           - data/local/dao/{Model}Dao.kt
# Also:   Update core/data/local/AppDatabase.kt
#           - Add {Model}Entity::class to entities array
#           - Add abstract val {model}Dao: {Model}Dao
#           - Bump version number
#
# Rules applied: D4 (entity/DAO generation)
#
# ─────────────────────────────────────────────────────────────────
# PHASE 2: Domain Models + Mappers
# ─────────────────────────────────────────────────────────────────
# Input:  Root MANIFEST + Data MANIFEST + Phase 1 output
# Output: For each data_model:
#           - domain/model/{Model}.kt
#         Add mapper extensions to entity files from Phase 1:
#           - fun {Model}Entity.toDomain(): {Model}
#           - fun {Model}.toEntity(): {Model}Entity
#
# Rules applied: D5 (domain model generation), RULE 21
#
# ─────────────────────────────────────────────────────────────────
# PHASE 3: Repositories
# ─────────────────────────────────────────────────────────────────
# Input:  Root MANIFEST + Data MANIFEST + Phase 1–2 output
# Output: For each data_model:
#           - data/repository/{model}//{Model}Repository.kt       (interface)
#           - data/repository/{model}/{Model}RepositoryImpl.kt   (implementation)
#         If backend exists:
#           - data/remote/{Service}ApiService.kt
#
# Rules applied: RULE 18 (repository interface), RULE 19 (impl by backend type),
#                D2 (backend type), D3 (connectivity mode)
#
# ─────────────────────────────────────────────────────────────────
# PHASE 4: Use Cases
# ─────────────────────────────────────────────────────────────────
# Input:  Root MANIFEST + Presentation MANIFEST + Phase 3 output (repository interfaces)
# Output: For each derived use case:
#           - domain/usecase/{Action}{Model}UseCase.kt
#
# Rules applied: RULE 13 (from screen type), RULE 14 (from model fields),
#                RULE 15 (dashboard-specific), RULE 16 (settings/config)
#
# ─────────────────────────────────────────────────────────────────
# PHASE 5: ViewModels
# ─────────────────────────────────────────────────────────────────
# Input:  Root MANIFEST + Presentation MANIFEST + Phase 3–4 output
# Output: For each screen:
#           - presentation/{feature}/{Screen}ViewModel.kt
#             (contains ViewModel class + UiState sealed interface)
#
# Rules applied: RULE 10 (ViewModel per screen), RULE 11 (actions per screen type)
#
# ─────────────────────────────────────────────────────────────────
# PHASE 6: Screen Wrappers
# ─────────────────────────────────────────────────────────────────
# Input:  Root MANIFEST + Presentation MANIFEST (full — screen APIs needed)
#         + Phase 5 output (ViewModels)
# Output: For each screen:
#           - presentation/{feature}/{Screen}Screen.kt
#             (@Composable wrapper that calls GenericXxxScreen from core/)
#
# Rules applied: RULE 1 (screen mapping), RULE 2 (card selection),
#                RULE 3 (detail components), RULE 4 (form fields),
#                RULE 5 (dashboard stats), RULE 12 (wiring pattern)
#
# ─────────────────────────────────────────────────────────────────
# PHASE 7: Navigation + DI + App Wiring
# ─────────────────────────────────────────────────────────────────
# Input:  Root MANIFEST + Presentation MANIFEST + ALL previous phase output
# Output:
#   - presentation/navigation/AppRoutes.kt    (@Serializable route definitions)
#   - di/AppModule.kt                         (register all DAOs, repos, use cases, VMs)
#   - App.kt                                  (wire AppOrchestrator with tabs, routes, screens)
#
# Rules applied: RULE 6 (navigation derivation), RULE 7 (orchestrator phases),
#                RULE 8 (auth screen), RULE 9 (settings), RULE 22 (DI registration)
#
# ─────────────────────────────────────────────────────────────────
#
# PHASE EXECUTION PROTOCOL
# ─────────────────────────────────────────────────────────────────
#
# For each phase, the backend API call includes:
#
#   system_prompt:
#     - Root MANIFEST (always)
#     - Relevant layer manifest (see per-phase input above)
#
#   user_prompt:
#     - Phase number and name
#     - Specific tasks for this phase (from the implementation plan)
#     - project-context.json (or relevant subset)
#     - Output signatures from previous phases (class names, function signatures)
#       so the model knows what's already been created
#
#   expected_response:
#     - Complete file contents for each output file
#     - Each file prefixed with its full path and package declaration
#
# If a phase fails (compilation error, wrong output), retry ONLY that phase.
# Previous phase outputs remain intact.
#
# ─────────────────────────────────────────────────────────────────
#
# PLAN OUTPUT FORMAT
# ─────────────────────────────────────────────────────────────────
#
# The plan generator (Qwen3-Max) MUST output the plan as JSON.
# Use {app_package} as placeholder — backend substitutes the real package
# from project-context.json app_config.package_name before sending to Qwen-Coder.
#
#   {
#     "phases": [
#       {
#         "phase": 1,
#         "name": "Database Layer",
#         "description": "Create Room entities and DAOs for all data models",
#         "manifests": ["root", "data"],
#         "tasks": [
#           {
#             "action": "create",
#             "file": "data/local/entity/AssignmentEntity.kt",
#             "package": "{app_package}.data.local.entity",
#             "description": "Room entity for Assignment with fields: title (String), dueDate (Long), completed (Boolean)"
#           },
#           {
#             "action": "create",
#             "file": "data/local/dao/AssignmentDao.kt",
#             "package": "{app_package}.data.local.dao",
#             "description": "DAO with getAll, getById, insert, delete, markComplete, getUpcoming"
#           },
#           {
#             "action": "modify",
#             "file": "core/data/local/AppDatabase.kt",
#             "description": "Add AssignmentEntity to entities array, add assignmentDao, bump version to 3"
#           }
#         ]
#       },
#       {
#         "phase": 2,
#         "name": "Domain Models",
#         "manifests": ["root", "data"],
#         "tasks": [
#           {
#             "action": "create",
#             "file": "domain/model/Assignment.kt",
#             "package": "{app_package}.domain.model",
#             "description": "Domain model: data class Assignment(id, title, dueDate: Instant, completed: Boolean)"
#           },
#           {
#             "action": "modify",
#             "file": "data/local/entity/AssignmentEntity.kt",
#             "description": "Add toDomain() and toEntity() mapper extensions"
#           }
#         ]
#       },
#       {
#         "phase": 3,
#         "name": "Repositories",
#         "manifests": ["root", "data"],
#         "tasks": [
#           {
#             "action": "create",
#             "file": "data/repository/assignment/AssignmentRepository.kt",
#             "package": "{app_package}.data.repository.assignment",
#             "description": "Interface with items StateFlow, loadAll, getById, insert, update, delete, markComplete, getUpcoming"
#           },
#           {
#             "action": "create",
#             "file": "data/repository/assignment/AssignmentRepositoryImpl.kt",
#             "package": "{app_package}.data.repository.assignment",
#             "description": "Local-only implementation using AssignmentDao"
#           }
#         ]
#       },
#       {
#         "phase": 4,
#         "name": "Use Cases",
#         "manifests": ["root", "presentation"],
#         "tasks": [
#           {
#             "action": "create",
#             "file": "domain/usecase/GetAssignmentListUseCase.kt",
#             "package": "{app_package}.domain.usecase",
#             "description": "Returns repository.items StateFlow"
#           }
#         ]
#       },
#       {
#         "phase": 5,
#         "name": "ViewModels",
#         "manifests": ["root", "presentation"],
#         "tasks": [
#           {
#             "action": "create",
#             "file": "presentation/assignments/AssignmentListViewModel.kt",
#             "package": "{app_package}.presentation.assignments",
#             "description": "ViewModel + AssignmentListUiState (Loading/Success/Error), actions: loadItems, deleteItem, refresh"
#           }
#         ]
#       },
#       {
#         "phase": 6,
#         "name": "Screen Wrappers",
#         "manifests": ["root", "presentation"],
#         "tasks": [
#           {
#             "action": "create",
#             "file": "presentation/assignments/AssignmentListScreen.kt",
#             "package": "{app_package}.presentation.assignments",
#             "description": "Composable wrapper: koinViewModel() → collectAsState → GenericListScreen with ListItemCard"
#           }
#         ]
#       },
#       {
#         "phase": 7,
#         "name": "Navigation + DI + Wiring",
#         "manifests": ["root", "presentation"],
#         "tasks": [
#           {
#             "action": "create",
#             "file": "presentation/navigation/AppRoutes.kt",
#             "package": "{app_package}.presentation.navigation",
#             "description": "@Serializable routes: Dashboard, Assignments, AssignmentDetail(id)"
#           },
#           {
#             "action": "modify",
#             "file": "di/AppModule.kt",
#             "description": "Register assignmentDao, AssignmentRepositoryImpl, use cases, ViewModels"
#           },
#           {
#             "action": "modify",
#             "file": "App.kt",
#             "description": "Wire AppOrchestrator: splashContent, tabs, homeBuilder with all routes"
#           }
#         ]
#       }
#     ]
#   }
#
# Each task has:
#   - action: "create" (new file) or "modify" (edit existing)
#   - file: path relative to com/mismaiti/
#   - package: full package declaration (for create only)
#   - description: what the file should contain (enough detail for Qwen-Coder)
