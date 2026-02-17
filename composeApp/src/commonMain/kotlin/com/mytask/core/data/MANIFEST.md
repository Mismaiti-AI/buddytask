# Core Data Manifest
# AI code generator: read this to know what pre-built data infrastructure exists and how to use it.
# Package: {app_package}.core.data
#
# NOTE: Derivation rules for generating repositories, entities, use cases, ViewModels, DI
# from project-context.json are in the Presentation MANIFEST: core/presentation/MANIFEST.md
# Rules 13–22 cover the full derivation pipeline. This file documents what's pre-built.

## How to use
# 1. Parse project context for required features (database, auth, settings, networking)
# 2. Match features to sections below
# 3. Use the API signatures to call pre-built data classes correctly
# 4. Platform-specific implementations exist in androidMain/iosMain (same package paths)

# =============================================================================
# DATABASE (Room)
# =============================================================================

## AppDatabase
# import {app_package}.core.data.local.AppDatabase
#
# ```kotlin
# @Database(entities = [UserEntity::class], version = 2, exportSchema = false)
# @ConstructedBy(AppDatabaseConstructor::class)
# abstract class AppDatabase : RoomDatabase() {
#     abstract val userDao: UserDao
# }
# ```
#
# Adding a new entity:
#   1. Create {Model}Entity.kt (see entity pattern below)
#   2. Create {Model}Dao.kt (see DAO pattern below)
#   3. Add {Model}Entity::class to @Database entities array
#   4. Add: abstract val {model}Dao: {Model}Dao
#   5. Bump version number
#
# Database is created per-platform in PlatformModule with:
#   - BundledSQLiteDriver (works on both Android & iOS)
#   - Dispatchers.IO for query coroutine context
#   - fallbackToDestructiveMigration(true) — dev convenience, remove for production

## AppDatabaseConstructor (DO NOT EDIT)
# import {app_package}.core.data.local.AppDatabaseConstructor
#
# ```kotlin
# expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
#     override fun initialize(): AppDatabase
# }
# ```

## UserEntity (pre-built)
# import {app_package}.core.data.local.model.UserEntity
#
# ```kotlin
# @Entity(tableName = "users")
# class UserEntity {
#     @PrimaryKey(autoGenerate = true)
#     var id: Int = 0
#
#     @ColumnInfo(name = "name")
#     var name: String = ""
#
#     @ColumnInfo(name = "email")
#     var email: String = ""
#
#     @ColumnInfo(name = "auth_provider")
#     var authProvider: String = "email"
#
#     @ColumnInfo(name = "provider_user_id")
#     var providerUserId: String = ""
# }
# ```

## UserDao (pre-built)
# import {app_package}.core.data.local.model.UserDao
#
# ```kotlin
# @Dao
# interface UserDao {
#     @Query("SELECT * FROM users")
#     fun getAll(): List<UserEntity>
#
#     @Upsert
#     fun insert(user: UserEntity)
#
#     @Query("DELETE FROM users WHERE id = :id")
#     fun deleteUser(id: Int)
#
#     @Query("SELECT * FROM users WHERE auth_provider = :provider AND provider_user_id = :providerUserId LIMIT 1")
#     fun getByProviderId(provider: String, providerUserId: String): UserEntity?
# }
# ```

## Entity Pattern (for new entities)
# ```kotlin
# @Entity(tableName = "{table_name}")
# class {Model}Entity {
#     @PrimaryKey(autoGenerate = true) var id: Int = 0
#     @ColumnInfo(name = "field_name") var fieldName: String = ""
#     @ColumnInfo(name = "count") var count: Int = 0
#     @ColumnInfo(name = "active") var active: Boolean = false
#     @ColumnInfo(name = "due_date") var dueDate: Long = 0L    // Instant → store as epoch millis
#     @ColumnInfo(name = "price") var price: Double = 0.0
# }
# ```
#
# Column name convention: camelCase field → snake_case column name

## DAO Pattern (for new DAOs)
# ```kotlin
# @Dao
# interface {Model}Dao {
#     @Query("SELECT * FROM {table}") fun getAll(): List<{Model}Entity>
#     @Query("SELECT * FROM {table} WHERE id = :id") fun getById(id: Int): {Model}Entity?
#     @Upsert fun insert(entity: {Model}Entity)
#     @Query("DELETE FROM {table} WHERE id = :id") fun delete(id: Int)
# }
# ```
#
# Additional queries from model fields:
#   Model has "completed: Boolean":
#     @Query("UPDATE {table} SET completed = 1 WHERE id = :id") fun markComplete(id: Int)
#   Model has date field (dueDate, examDate, etc.):
#     @Query("SELECT * FROM {table} WHERE {date_col} > :now ORDER BY {date_col} ASC")
#     fun getUpcoming(now: Long): List<{Model}Entity>

## Domain Model Pattern (for new models)
# ```kotlin
# data class {Model}(
#     val id: Int = 0,
#     val title: String = "",
#     val dueDate: Instant = Instant.DISTANT_PAST,
#     val completed: Boolean = false
# )
# ```
#
# Use kotlin.time.Instant for timestamps (NOT kotlinx.datetime).

## Entity-Domain Mapper Pattern
# ```kotlin
# fun {Model}Entity.toDomain(): {Model} = {Model}(
#     id = id,
#     title = title,
#     dueDate = Instant.fromEpochMilliseconds(dueDate),
#     completed = completed
# )
#
# fun {Model}.toEntity(): {Model}Entity = {Model}Entity().apply {
#     this.id = this@toEntity.id
#     this.title = this@toEntity.title
#     this.dueDate = this@toEntity.dueDate.toEpochMilliseconds()
#     this.completed = this@toEntity.completed
# }
# ```

# =============================================================================
# AI CHAT SERVICE (Pluggable AI Providers)
# =============================================================================

## AiChatService
# import {app_package}.core.data.chat.AiChatService
# import {app_package}.core.data.chat.AiChatResponse
#
# ```kotlin
# interface AiChatService {
#     suspend fun sendMessage(
#         message: String,
#         conversationId: String? = null
#     ): AiChatResponse
#
#     fun sendMessageStream(
#         message: String,
#         conversationId: String? = null
#     ): Flow<String>
#
#     suspend fun clearConversation(conversationId: String)
# }
#
# data class AiChatResponse(
#     val message: String,
#     val conversationId: String? = null,
#     val metadata: Map<String, String> = emptyMap()
# )
# ```
#
# Usage:
#   1. Implement AiChatService for your AI provider (OpenAI, Anthropic, Gemini, etc.)
#   2. Register in Koin: single<AiChatService> { MyAiChatServiceImpl(get()) }
#   3. Inject in ViewModel: class ChatViewModel(private val aiChat: AiChatService)
#   4. Use with GenericChatScreen for full chat UI
#
# Example implementation:
#   class OpenAiChatService(private val client: HttpClient) : AiChatService {
#       override suspend fun sendMessage(message: String, conversationId: String?): AiChatResponse {
#           val response = client.post("https://api.openai.com/v1/chat/completions") { ... }
#           return AiChatResponse(message = response.choices.first().message.content)
#       }
#       override fun sendMessageStream(message: String, conversationId: String?): Flow<String> {
#           return flow { /* SSE streaming implementation */ }
#       }
#       override suspend fun clearConversation(conversationId: String) { /* clear context */ }
#   }

# =============================================================================
# SETTINGS (Key-Value Storage)
# =============================================================================

## AppSettings
# import {app_package}.core.data.local.AppSettings
#
# ```kotlin
# interface AppSettings {
#     fun getString(key: String, default: String): String
#     fun putString(key: String, value: String)
#     fun getBoolean(key: String, default: Boolean): Boolean
#     fun putBoolean(key: String, value: Boolean)
#     fun getInt(key: String, default: Int): Int
#     fun putInt(key: String, value: Int)
#     fun remove(key: String)
# }
# ```
#
# Platform implementations (pre-built, registered in platformModule):
#   - Android: AndroidAppSettings (SharedPreferences)
#   - iOS: IosAppSettings (NSUserDefaults)
#
# Usage:
#   val settings: AppSettings = get()  // from Koin
#   settings.putString("key", "value")
#   settings.getString("key", "default")

# =============================================================================
# SOCIAL AUTH (Google Sign-In / Apple Sign-In)
# =============================================================================

## signInWithSocialProvider (expect/actual)
# import {app_package}.core.data.auth.signInWithSocialProvider
#
# ```kotlin
# // Common (expect):
# expect suspend fun signInWithSocialProvider(): SocialAuthResult
#
# // Android (actual) — Google Credential Manager:
# const val WEB_CLIENT_ID = "YOUR_WEB_CLIENT_ID_HERE"  // user must fill this in
# actual suspend fun signInWithSocialProvider(): SocialAuthResult
# // Uses CredentialManager + GetSignInWithGoogleOption
# // Returns: SocialAuthResult(id=googleId, name, email, provider=GOOGLE, idToken=googleJwt)
#
# // iOS (actual) — Apple Sign-In via ASAuthorization:
# actual suspend fun signInWithSocialProvider(): SocialAuthResult
# // Uses ASAuthorizationAppleIDProvider + CompletableDeferred delegate bridge
# // Returns: SocialAuthResult(id=appleUserId, name, email, provider=APPLE, idToken=appleIdentityToken)
# ```

## SocialAuthResult
# import {app_package}.core.data.auth.SocialAuthResult
# import {app_package}.core.data.auth.AuthProvider
#
# ```kotlin
# data class SocialAuthResult(
#     val id: String,         // User ID from provider (Google email / Apple user ID)
#     val name: String,       // Display name from provider
#     val email: String,      // Email from provider
#     val provider: AuthProvider,
#     val idToken: String? = null  // Google JWT / Apple identity token (for backend verification)
# )
#
# enum class AuthProvider {
#     GOOGLE,
#     APPLE,
#     EMAIL
# }
# ```

## AuthRepository
# import {app_package}.core.data.auth.AuthRepository
# import {app_package}.core.data.auth.SocialSignInResult
#
# ```kotlin
# class AuthRepository(
#     private val database: AppDatabase,
#     private val backendHandler: SocialAuthBackendHandler? = null  // null = local-only mode
# ) {
#     suspend fun signInWithSocial(): SocialSignInResult
#     fun getUserByProviderId(provider: String, providerUserId: String): UserEntity?
# }
#
# sealed class SocialSignInResult {
#     data class Success(val user: UserEntity) : SocialSignInResult()
#     data object Cancelled : SocialSignInResult()
#     data class Error(val message: String) : SocialSignInResult()
# }
# ```

## SocialAuthBackendHandler (optional — for backend-verified auth)
# import {app_package}.core.data.auth.SocialAuthBackendHandler
# import {app_package}.core.data.auth.BackendAuthResponse
#
# ```kotlin
# interface SocialAuthBackendHandler {
#     suspend fun authenticate(result: SocialAuthResult): BackendAuthResponse
# }
#
# data class BackendAuthResponse(
#     val userId: String,
#     val name: String,
#     val email: String
# )
# ```

## Two Auth Modes
#
# MODE 1: LOCAL-ONLY (default, no backend)
# ─────────────────────────────────────────
#   Native sign-in (Google/Apple) → grab user profile (name, email) → save to Room.
#
#   NOT real authentication — no session, no token verification, no security.
#   Useful for:
#     - Quick user profile setup (skip manual name/email form)
#     - Personalizing the app locally
#     - Prototyping before a backend exists
#
#   Config: none. Just call AuthRepository.signInWithSocial().
#
#
# MODE 2: WITH BACKEND (production)
# ─────────────────────────────────
#   Native sign-in (Google/Apple) → get ID token → send to your backend
#   → backend verifies token → returns authenticated user → save to Room.
#
#   Real authentication. Your backend verifies the idToken:
#     - Google: verify JWT via Google's tokeninfo or server library
#     - Apple: verify identity token with Apple's public keys
#
#   Config:
#     1. Implement SocialAuthBackendHandler interface
#     2. Register in Koin: single<SocialAuthBackendHandler> { YourImpl(get()) }
#     That's it — AuthRepository auto-detects it via getOrNull().
#
#   ```kotlin
#   class MySocialAuthBackend(private val api: MyApi) : SocialAuthBackendHandler {
#       override suspend fun authenticate(result: SocialAuthResult): BackendAuthResponse {
#           val response = api.socialLogin(
#               provider = result.provider.name,
#               idToken = result.idToken ?: error("Missing ID token"),
#           )
#           return BackendAuthResponse(
#               userId = response.userId,
#               name = response.name,
#               email = response.email
#           )
#       }
#   }
#   ```

## Auth Usage in ViewModel
# ```kotlin
# viewModelScope.launch {
#     when (val result = authRepository.signInWithSocial()) {
#         is SocialSignInResult.Success -> { /* navigate to home */ }
#         is SocialSignInResult.Cancelled -> { /* do nothing */ }
#         is SocialSignInResult.Error -> { /* show error: result.message */ }
#     }
# }
# ```

## Platform Setup Required
#
# Android — Google Sign-In:
#   1. Go to Google Cloud Console → APIs & Services → Credentials
#   2. Create OAuth 2.0 client → Web application type → copy Web Client ID
#   3. Paste into SocialAuthProvider.android.kt → WEB_CLIENT_ID constant
#   4. Create Android OAuth client (SHA-1 + package name) — no code change needed
#
# iOS — Apple Sign-In:
#   1. Open iosApp.xcodeproj in Xcode
#   2. Target → Signing & Capabilities → Add "Sign in with Apple"
#   3. Ensure Apple Developer account has this capability enabled

# =============================================================================
# DEPENDENCY INJECTION (Koin)
# =============================================================================

## platformModule (expect/actual)
# import {app_package}.core.di.platformModule
#
# ```kotlin
# // Common (expect):
# expect fun platformModule(): Module
#
# // Android (actual):
# actual fun platformModule() = module {
#     single { Room.databaseBuilder<AppDatabase>(...).build() }  // Room DB
#     single { OkHttp.create { ... } }                           // Ktor engine
#     single<AppSettings> { AndroidAppSettings(androidContext()) } // Settings
# }
#
# // iOS (actual):
# actual fun platformModule() = module {
#     single { Room.databaseBuilder<AppDatabase>(...).build() }  // Room DB
#     single { Darwin.create() }                                  // Ktor engine
#     single<AppSettings> { IosAppSettings(NSUserDefaults.standardUserDefaults) } // Settings
# }
# ```

## networkModule
# import {app_package}.core.di.networkModule
#
# ```kotlin
# fun networkModule() = module {
#     factory {
#         HttpClient(get()) {                      // get() = platform engine (OkHttp/Darwin)
#             install(ContentNegotiation) {
#                 json(Json {
#                     prettyPrint = true
#                     isLenient = true
#                     ignoreUnknownKeys = true
#                 })
#             }
#             install(Logging) { level = LogLevel.ALL }
#             install(HttpTimeout) {
#                 connectTimeoutMillis = 60_000
#                 requestTimeoutMillis = 120_000
#                 socketTimeoutMillis = 120_000
#             }
#             install(HttpRequestRetry) {
#                 retryOnServerErrors(maxRetries = 1)
#                 exponentialDelay()
#             }
#             expectSuccess = false
#         }
#     }
# }
# ```
#
# Usage:
#   val client: HttpClient = get()  // from Koin
#   val response = client.get("https://api.example.com/data")

## appModule
# import {app_package}.di.appModule
# import {app_package}.di.moduleList
#
# ```kotlin
# fun moduleList(): List<Module> = listOf(
#     platformModule(),    // DB, HTTP engine, settings
#     networkModule(),     // Ktor HttpClient
#     appModule()          // Repositories, ViewModels
# )
#
# fun appModule() = module {
#     single { AuthRepository(database = get(), backendHandler = getOrNull()) }
#     // Add your DAOs, repositories, use cases, ViewModels here
# }
# ```

## DI Registration Patterns
# ```kotlin
# // DAO (from AppDatabase):
# single { get<AppDatabase>().{model}Dao }
#
# // Repository (interface + impl):
# singleOf(::{Model}RepositoryImpl) { bind<{Model}Repository>() }
#
# // UseCase:
# factoryOf(::{UseCaseName})
#
# // ViewModel:
# viewModelOf(::{ViewModelName})
#
# // Backend handler (optional):
# single<SocialAuthBackendHandler> { MySocialAuthBackend(get()) }
# ```

## Module Loading Order
# In moduleList():
#   1. platformModule()  — DB, HTTP engine, settings
#   2. networkModule()   — Ktor HttpClient (depends on platform engine)
#   3. appModule()       — Repositories, UseCases, ViewModels (depends on DB + HttpClient)

## Adding New Dependencies
#   - Platform-specific (DB, settings): add to PlatformModule.{android,ios}.kt
#   - App-wide (repository, use case, ViewModel): add to AppModule.kt → appModule()
#   - Network-related (API service): create service class injected with HttpClient

# =============================================================================
# REPOSITORY PATTERN
# =============================================================================

## Repository Interface Pattern
# ```kotlin
# interface {Model}Repository {
#     val items: StateFlow<List<{Model}>>
#     val isLoading: StateFlow<Boolean>
#     val error: StateFlow<String?>
#     suspend fun loadAll()
#     suspend fun getById(id: String): {Model}?
#     suspend fun insert(model: {Model})
#     suspend fun update(model: {Model})
#     suspend fun delete(id: String)
# }
# ```
#
# Additional methods from model fields:
#   Model has "completed: Boolean" → suspend fun markComplete(id: String)
#   Model has "progress: Int"      → suspend fun updateProgress(id: String, progress: Int)
#   Model has date/deadline field   → suspend fun getUpcoming(): List<{Model}>
#   Feature has search              → suspend fun search(query: String): List<{Model}>

## Repository Implementation — Local-Only
# ```kotlin
# class {Model}RepositoryImpl(
#     private val dao: {Model}Dao
# ) : {Model}Repository {
#     private val _items = MutableStateFlow<List<{Model}>>(emptyList())
#     override val items: StateFlow<List<{Model}>> = _items.asStateFlow()
#     private val _isLoading = MutableStateFlow(false)
#     override val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
#     private val _error = MutableStateFlow<String?>(null)
#     override val error: StateFlow<String?> = _error.asStateFlow()
#
#     override suspend fun loadAll() {
#         _isLoading.value = true
#         _items.value = dao.getAll().map { it.toDomain() }
#         _isLoading.value = false
#     }
#     override suspend fun getById(id: String): {Model}? = dao.getById(id.toInt())?.toDomain()
#     override suspend fun insert(model: {Model}) { dao.insert(model.toEntity()); loadAll() }
#     override suspend fun update(model: {Model}) { dao.insert(model.toEntity()); loadAll() }
#     override suspend fun delete(id: String) { dao.delete(id.toInt()); loadAll() }
# }
# ```

## Repository Implementation — With Remote (Google Sheets / REST API)
# ```kotlin
# class {Model}RepositoryImpl(
#     private val dao: {Model}Dao,
#     private val remoteSource: {RemoteService}
# ) : {Model}Repository {
#     private val _items = MutableStateFlow<List<{Model}>>(emptyList())
#     private val _isLoading = MutableStateFlow(false)
#     private val _error = MutableStateFlow<String?>(null)
#
#     override suspend fun loadAll() {
#         _isLoading.value = true
#         // 1. Emit cached data immediately
#         _items.value = dao.getAll().map { it.toDomain() }
#         // 2. Fetch remote in background
#         try {
#             val remote = remoteSource.fetchAll()
#             // 3. Update cache + emit fresh data
#             remote.forEach { dao.insert(it.toEntity()) }
#             _items.value = dao.getAll().map { it.toDomain() }
#             _error.value = null
#         } catch (e: Exception) {
#             // 4. Keep cached data, emit error
#             _error.value = e.message
#         }
#         _isLoading.value = false
#     }
# }
# ```

# =============================================================================
# DECISION TREE
# =============================================================================
#
# 1. Does the project store data locally?
#    → Use Room: add entities to AppDatabase, create DAOs
#
# 2. Does the project need user preferences?
#    → Use AppSettings (already registered in platformModule)
#
# 3. Does the project have social sign-in?
#    → Use AuthRepository.signInWithSocial()
#    → Wire SocialButton.onClick in GenericAuthScreen to call it
#
# 4. Does the project have a backend?
#    YES → Implement SocialAuthBackendHandler, register in Koin
#    NO  → Local-only mode works out of the box (profile autofill only)
#
# 5. Does the project call REST APIs?
#    → Use the pre-configured Ktor HttpClient from Koin
#    → Create a service class: class MyApi(private val client: HttpClient)

# =============================================================================
# DERIVATION RULES — Data Layer from project-context.json
# =============================================================================
#
# These rules tell the plan generator how to configure the data layer.
# (Screen/ViewModel/UseCase derivation rules are in presentation/MANIFEST.md Rules 10–22)
#
# ─────────────────────────────────────────────────────────────────
# RULE D1: Auth mode from project-context.json
# ─────────────────────────────────────────────────────────────────
#   auth_config exists:
#     → Include AuthRepository in DI
#     → Wire GenericAuthScreen with SocialButton callbacks
#
#   auth_config.social_providers contains "google":
#     → Android: signInWithSocialProvider() already handles this
#     → User must configure WEB_CLIENT_ID
#
#   auth_config.social_providers contains "apple":
#     → iOS: signInWithSocialProvider() already handles this
#     → User must add "Sign in with Apple" capability in Xcode
#
#   auth_config.backend_mode == "local_only" OR no backend_config:
#     → AuthRepository works out of the box (MODE 1)
#     → No SocialAuthBackendHandler needed
#
#   auth_config.backend_mode == "backend_verified":
#     → Plan must include: create SocialAuthBackendHandler implementation
#     → Plan must include: register in Koin appModule
#     → Implementation needs backend API endpoint for token verification
#
#   No auth_config:
#     → Skip all auth-related screens and wiring
#
# ─────────────────────────────────────────────────────────────────
# RULE D2: Backend type → data layer pattern
# ─────────────────────────────────────────────────────────────────
#   backend_config.type == "google_sheets":
#     → Create SheetsApiService for HTTP calls
#     → Repositories use SheetsApiService (not DAOs directly)
#     → Room used for local caching only
#
#   backend_config.type == "rest_api":
#     → Repositories use Ktor HttpClient
#     → Room used for local caching
#
#   backend_config.type == null OR no backend_config:
#     → Repositories use Room DAOs directly
#     → No remote data source
#     → All CRUD operations are local
#
# ─────────────────────────────────────────────────────────────────
# RULE D3: Connectivity mode → repository pattern
# ─────────────────────────────────────────────────────────────────
#   connectivity_mode == "both" (default):
#     loadAll():
#       1. Emit cached data from Room immediately
#       2. Fetch from remote in background
#       3. On success: update Room cache + emit fresh data
#       4. On failure: keep cached data, emit error
#
#   connectivity_mode == "offline":
#     Repository uses Room DAOs only. No remote calls.
#
#   connectivity_mode == "online":
#     Repository fetches from remote only. No Room caching.
#
# ─────────────────────────────────────────────────────────────────
# RULE D4: Entity/DAO generation from data_models[]
# ─────────────────────────────────────────────────────────────────
# For each data_models[] entry, generate Room entity + DAO:
#
#   Field type mapping:
#     "title: String"       → @ColumnInfo(name = "title") var title: String = ""
#     "count: Int"          → @ColumnInfo(name = "count") var count: Int = 0
#     "active: Boolean"     → @ColumnInfo(name = "active") var active: Boolean = false
#     "dueDate: Instant"    → @ColumnInfo(name = "due_date") var dueDate: Long = 0L
#     "price: Double"       → @ColumnInfo(name = "price") var price: Double = 0.0
#
#   Column name convention: camelCase field → snake_case column name
#
#   DAO standard queries:
#     @Query("SELECT * FROM {table}") fun getAll(): List<{Model}Entity>
#     @Query("SELECT * FROM {table} WHERE id = :id") fun getById(id: Int): {Model}Entity?
#     @Upsert fun insert(entity: {Model}Entity)
#     @Query("DELETE FROM {table} WHERE id = :id") fun delete(id: Int)
#
#   Additional DAO queries from model fields:
#     Model has "completed: Boolean":
#       @Query("UPDATE {table} SET completed = 1 WHERE id = :id") fun markComplete(id: Int)
#     Model has date field (dueDate, examDate, etc.):
#       @Query("SELECT * FROM {table} WHERE {date_col} > :now ORDER BY {date_col} ASC")
#       fun getUpcoming(now: Long): List<{Model}Entity>
#
# ─────────────────────────────────────────────────────────────────
# RULE D5: Domain model generation from data_models[]
# ─────────────────────────────────────────────────────────────────
# For each data_models[] entry:
#
#   data class {Model}(
#       val id: Int = 0,
#       // all fields from data_models[].fields with Kotlin types
#   )
#
#   Use kotlin.time.Instant for timestamps (NOT kotlinx.datetime).
#
# ─────────────────────────────────────────────────────────────────
# RULE D6: Settings derivation
# ─────────────────────────────────────────────────────────────────
#   backend_config.config.config_type == "configurable":
#     → Store config values in AppSettings
#     → Keys: "{config_field}_key" (e.g. "google_sheets_url_key")
#
#   ui_design.has_dark_mode == true:
#     → Store dark mode preference: settings.putBoolean("dark_mode", value)
#
#   Any feature needs user preferences:
#     → Use AppSettings with descriptive key names
