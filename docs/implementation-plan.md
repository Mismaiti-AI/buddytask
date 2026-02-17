# Implementation Plan

## Phase 1: Database Layer
Define Room entities and DAOs for Assignment, Exam, Project, and AppConfig. Update AppDatabase.kt to include new entities and DAOs, and increment database version.

- [x] AssignmentEntity.kt — Room entity for Assignment with fields mapped to snake_case columns; includes toDomain() and fromDomain() mappers.
- [x] AssignmentDao.kt — DAO interface with suspend functions for CRUD operations on AssignmentEntity, including markComplete and getUpcoming.
- [x] ExamEntity.kt — Room entity for Exam with Instant fields stored as Long; includes mappers to/from domain model.
- [x] ExamDao.kt — DAO for ExamEntity with standard CRUD and getUpcomingExams.
- [x] ProjectEntity.kt — Room entity for Project with progress and date fields; includes mappers.
- [x] ProjectDao.kt — DAO for ProjectEntity with CRUD and updateProgress support.
- [x] AppConfigEntity.kt — Entity for AppConfig storing Google Sheets URL and timestamps.
- [x] AppConfigDao.kt — DAO for AppConfigEntity with insert/update/getLatest.
- [x] [modify] AppDatabase.kt — Add AssignmentEntity, ExamEntity, ProjectEntity, AppConfigEntity to entities list; add corresponding DAO abstract methods; increment version to 2.

## Phase 2: Domain Models and Mappers
Create pure Kotlin data classes for domain models. Embed mapping logic in entity files as extension functions.

- [x] Assignment.kt — Pure data class Assignment with val fields, defaults, and Instant for dueDate.
- [x] Exam.kt — Pure data class Exam with examDate as Instant and preparationStatus Boolean.
- [x] Project.kt — Pure data class Project with startDate, dueDate as Instant, progress Int, completed Boolean.
- [x] AppConfig.kt — Pure data class AppConfig with googleSheetsUrl and timestamp fields.

## Phase 3: Repositories and API Services
Implement repository interfaces and Google Sheets API service for remote data fetching with Room caching.

- [x] AssignmentRepository.kt — Interface declaring StateFlows (items, isLoading, error) and methods: loadAll, getById, markComplete, getUpcomingAssignments.
- [x] AssignmentRepositoryImpl.kt — Implementation using AssignmentDao and SheetsApiService; cache-first strategy with refresh from Google Sheets.
- [x] ExamRepository.kt — Interface for Exam data operations including getUpcomingExams.
- [x] ExamRepositoryImpl.kt — Implementation using ExamDao and SheetsApiService.
- [x] ProjectRepository.kt — Interface for Project operations including updateProgress.
- [x] ProjectRepositoryImpl.kt — Implementation using ProjectDao and SheetsApiService.
- [x] AppConfigRepository.kt — Interface for managing AppConfig: getCurrent, updateUrl, validateUrl.
- [x] AppConfigRepositoryImpl.kt — Implementation using AppConfigDao and SheetsApiService for validation.
- [x] SheetsApiService.kt — Ktor-based service calling Google Apps Script endpoint to fetch Assignment/Exam/Project data from configured spreadsheet.

## Phase 4: Use Cases
Implement single-responsibility use cases for all user actions derived from features and data models.

- [x] GetAssignmentListUseCase.kt — Use case invoking AssignmentRepository.loadAll().
- [x] MarkAssignmentCompleteUseCase.kt — Use case to toggle assignment completion status via repository.
- [x] ViewAssignmentDetailsUseCase.kt — Use case fetching assignment by ID.
- [x] GetExamListUseCase.kt — Use case for loading all exams.
- [x] ViewExamDetailsUseCase.kt — Use case fetching exam by ID.
- [x] GetProjectListUseCase.kt — Use case for loading all projects.
- [x] ViewProjectDetailsUseCase.kt — Use case fetching project by ID.
- [x] GetDashboardOverviewUseCase.kt — Use case combining assignments, exams, and projects for dashboard stats.
- [x] GetUpcomingItemsUseCase.kt — Use case fetching upcoming items across all models.
- [x] UpdateGoogleSheetUrlUseCase.kt — Use case to update the Google Sheets URL in AppConfig.
- [x] ValidateSheetUrlUseCase.kt — Use case validating sheet URL via SheetsApiService.
- [x] GetCurrentSheetConfigUseCase.kt — Use case retrieving current AppConfig.

## Phase 5: ViewModels and UiState
Create thin ViewModels per screen that expose UiState sealed interfaces and delegate to use cases.

- [x] AssignmentListViewModel.kt — ViewModel with UiState (Loading, Success(List<Assignment>), Error); actions: loadAssignments, markComplete, refresh.
- [x] AssignmentDetailViewModel.kt — ViewModel with UiState for single Assignment; action: loadAssignment.
- [x] ExamListViewModel.kt — ViewModel for Exam list with loadExams action.
- [x] ExamDetailViewModel.kt — ViewModel for Exam detail.
- [x] ProjectListViewModel.kt — ViewModel for Project list.
- [x] ProjectDetailViewModel.kt — ViewModel for Project detail.
- [x] DashboardViewModel.kt — ViewModel combining data from multiple repositories for dashboard overview.
- [x] SettingsViewModel.kt — ViewModel for general settings (e.g., dark mode).
- [x] SheetUrlConfigViewModel.kt — ViewModel with actions: loadCurrentConfig, updateSheetUrl, validateUrl.

## Phase 6: Screen Wrappers
Implement composable screen wrappers that call pre-built GenericXxxScreen components from core/ with appropriate card types and actions.

- [x] AssignmentListScreen.kt — Composable calling GenericListScreen<Assignment> with ImageCard or ListItemCard (no image → ListItemCard); supports mark-as-complete action.
- [x] AssignmentDetailScreen.kt — Composable calling GenericDetailScreen<Assignment> with DetailCard and StatusBadge (for completed).
- [x] ExamListScreen.kt — GenericListScreen<Exam> using ListItemCard.
- [x] ExamDetailScreen.kt — GenericDetailScreen<Exam> with InfoRow for exam details.
- [x] ProjectListScreen.kt — GenericListScreen<Project> with ProgressCard or DetailCard (4+ fields).
- [x] ProjectDetailScreen.kt — GenericDetailScreen<Project> showing progress and dates.
- [x] DashboardScreen.kt — GenericDashboardScreen with StatCards for counts and Upcoming sections.
- [x] SettingsScreen.kt — GenericSettingsScreen with SwitchRow for dark mode and navigation to SheetUrlConfigScreen.
- [x] SheetUrlConfigScreen.kt — GenericFormScreen for editing Google Sheets URL with validation.

## Phase 7: Navigation, DI, and App Wiring
Define routes, configure Koin DI, update App.kt to use AppOrchestrator, and set up bottom navigation tabs.

- [x] AppRoutes.kt — @Serializable route objects: AssignmentListRoute, AssignmentDetailRoute(id), ExamListRoute, etc.; DashboardRoute as home.
- [x] [modify] AppModule.kt — Register all DAOs, Repositories (bind + impl), UseCases (factoryOf), and ViewModels (viewModelOf).
- [x] [modify] App.kt — Initialize Koin with AppModule; set content to AppOrchestrator with splash enabled.
- [x] [modify] AppOrchestrator.kt — Configure phases: Splash → Home (Dashboard). Define bottom tabs for Dashboard, Assignments, Exams, Projects (first 4 features).
