package com.mytask

import androidx.compose.runtime.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import org.koin.compose.KoinApplication
import org.koin.core.KoinApplication as KoinAppDeclaration

// Pre-built navigation from core/
import com.mytask.core.presentation.navigation.AppOrchestrator
import com.mytask.core.presentation.navigation.AppState
import com.mytask.core.presentation.navigation.NavigationTab

// App routes
import com.mytask.presentation.navigation.AppRoutes

// App screens (from Phase 6)
import com.mytask.presentation.assignmenttracking.AssignmentListScreen
import com.mytask.presentation.assignmenttracking.AssignmentDetailScreen
import com.mytask.presentation.examtracking.ExamListScreen
import com.mytask.presentation.examtracking.ExamDetailScreen
import com.mytask.presentation.projecttracking.ProjectListScreen
import com.mytask.presentation.projecttracking.ProjectDetailScreen
import com.mytask.presentation.dashboardoverview.DashboardScreen
import com.mytask.presentation.googlesheetsconfiguration.SettingsScreen
import com.mytask.presentation.googlesheetsconfiguration.SheetUrlConfigScreen
import com.mytask.core.presentation.screens.GenericSplashScreen
import com.mytask.presentation.theme.AppTheme
import com.mytask.di.moduleList

@Composable
fun App(koinAppDeclaration: ((KoinAppDeclaration) -> Unit)? = null) {
    KoinApplication(application = {
        modules(moduleList())
        koinAppDeclaration?.invoke(this)
    }) {
        AppTheme {
            var appState by remember { mutableStateOf(AppState.Splash) }

            AppOrchestrator(
                appState = appState,
                splashContent = {
                    GenericSplashScreen(
                        appName = "BuddyTask",
                        icon = Icons.Default.Assignment,
                        onFinished = { appState = AppState.Home }
                    )
                },
                tabs = listOf(
                    NavigationTab(AppRoutes.Dashboard, "Dashboard", Icons.Default.Assignment),
                    NavigationTab(AppRoutes.Assignments, "Assignments", Icons.Default.CheckCircle),
                    NavigationTab(AppRoutes.Exams, "Exams", Icons.Default.Event),
                    NavigationTab(AppRoutes.Projects, "Projects", Icons.Default.BarChart),
                ),
                homeStartDestination = AppRoutes.Dashboard,
                showTopBar = false,
                homeBuilder = { nav ->
                    // Tab destinations (bottom bar visible)
                    composable<AppRoutes.Dashboard> { DashboardScreen(
                        onSettingsClick = { nav.navigate(AppRoutes.Settings) },
                        onAssignmentClick = { id -> nav.navigate(AppRoutes.AssignmentDetail(id)) },
                        onExamClick = { id -> nav.navigate(AppRoutes.ExamDetail(id)) },
                        onProjectClick = { id -> nav.navigate(AppRoutes.ProjectDetail(id)) }
                    ) }
                    composable<AppRoutes.Assignments> { AssignmentListScreen(
                        onItemClick = { id -> nav.navigate(AppRoutes.AssignmentDetail(id)) }
                    ) }
                    composable<AppRoutes.Exams> { ExamListScreen(
                        onItemClick = { id -> nav.navigate(AppRoutes.ExamDetail(id)) }
                    ) }
                    composable<AppRoutes.Projects> { ProjectListScreen(
                        onItemClick = { id -> nav.navigate(AppRoutes.ProjectDetail(id)) }
                    ) }

                    // Detail destinations (bottom bar auto-hides)
                    composable<AppRoutes.AssignmentDetail> { entry ->
                        val route = entry.toRoute<AppRoutes.AssignmentDetail>()
                        AssignmentDetailScreen(
                            assignmentId = route.id,
                            onBackClick = { nav.popBackStack() },
                            onEditClick = { /* Handle edit if needed */ },
                            onDeleteClick = { /* Handle delete if needed */ }
                        )
                    }
                    composable<AppRoutes.ExamDetail> { entry ->
                        val route = entry.toRoute<AppRoutes.ExamDetail>()
                        ExamDetailScreen(
                            examId = route.id,
                            onBackClick = { nav.popBackStack() },
                            onEditClick = { /* Handle edit if needed */ },
                            onDeleteClick = { /* Handle delete if needed */ }
                        )
                    }
                    composable<AppRoutes.ProjectDetail> { entry ->
                        val route = entry.toRoute<AppRoutes.ProjectDetail>()
                        ProjectDetailScreen(
                            projectId = route.id,
                            onBackClick = { nav.popBackStack() },
                            onEditClick = { /* Handle edit if needed */ },
                            onDeleteClick = { /* Handle delete if needed */ }
                        )
                    }
                    composable<AppRoutes.Settings> {
                        SettingsScreen(
                            onBackClick = { nav.popBackStack() },
                            onSheetUrlConfigClick = { nav.navigate(AppRoutes.SheetUrlConfig) }
                        )
                    }
                    composable<AppRoutes.SheetUrlConfig> {
                        SheetUrlConfigScreen(
                            onBackClick = { nav.popBackStack() }
                        )
                    }
                }
            )
        }
    }
}