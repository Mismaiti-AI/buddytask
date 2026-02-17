package com.mytask.presentation.dashboardoverview

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Settings
import org.koin.compose.viewmodel.koinViewModel

import com.mytask.domain.model.Assignment
import com.mytask.domain.model.Exam
import com.mytask.domain.model.Project

import com.mytask.core.presentation.screens.GenericDashboardScreen
import com.mytask.core.presentation.screens.DashboardStat
import com.mytask.core.presentation.screens.QuickAction
import com.mytask.core.presentation.components.ListItemCard
import com.mytask.core.presentation.components.ErrorContent

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = koinViewModel(),
    onSettingsClick: () -> Unit = {},
    onAssignmentClick: (String) -> Unit = {},
    onExamClick: (String) -> Unit = {},
    onProjectClick: (String) -> Unit = {},
    onSeeAllClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    when (val state = uiState) {
        is DashboardUiState.Loading -> CircularProgressIndicator()
        is DashboardUiState.Success -> {
            GenericDashboardScreen(
                title = "Dashboard",
                greeting = "Welcome back!",
                stats = listOf(
                    DashboardStat(
                        label = "Assignments",
                        value = "${state.totalAssignments}",
                        icon = Icons.Default.Assignment
                    ),
                    DashboardStat(
                        label = "Exams",
                        value = "${state.totalExams}",
                        icon = Icons.Default.Event
                    ),
                    DashboardStat(
                        label = "Projects",
                        value = "${state.totalProjects}",
                        icon = Icons.Default.BarChart
                    )
                ),
                quickActions = listOf(
                    QuickAction(
                        label = "New Assignment",
                        icon = Icons.Default.Assignment,
                        onClick = { /* Handle new assignment */ }
                    ),
                    QuickAction(
                        label = "New Exam",
                        icon = Icons.Default.Event,
                        onClick = { /* Handle new exam */ }
                    ),
                    QuickAction(
                        label = "New Project",
                        icon = Icons.Default.BarChart,
                        onClick = { /* Handle new project */ }
                    )
                ),
                onSettingsClick = onSettingsClick,
                recentItems = state.upcomingItems.map { item ->
                    // Convert upcoming items to a displayable format
                    // This would need to be adapted based on the actual structure of UpcomingItem
                    Assignment(
                        id = item.id.toIntOrNull() ?: 0,
                        title = item.title,
                        subject = item.type,
                        dueDate = kotlin.time.Instant.fromEpochMilliseconds(item.date),
                        completed = false
                    )
                },
                recentTitle = "Upcoming",
                onRecentItemClick = { item ->
                    when(item) {
                        is Assignment -> onAssignmentClick(item.id.toString())
                        is Exam -> onExamClick(item.id.toString())
                        is Project -> onProjectClick(item.id.toString())
                        else -> {}
                    }
                },
                recentItemContent = { item ->
                    when(item) {
                        is Assignment -> ListItemCard(
                            title = item.title,
                            subtitle = item.subject,
                            caption = "Due: ${item.dueDate.toString().substring(0, 10)}"
                        )
                        is Exam -> ListItemCard(
                            title = item.title,
                            subtitle = item.subject,
                            caption = "Date: ${item.examDate.toString().substring(0, 10)}"
                        )
                        is Project -> ListItemCard(
                            title = item.title,
                            subtitle = item.subject,
                            caption = "Due: ${item.dueDate.toString().substring(0, 10)}"
                        )
                        else -> ListItemCard(title = "Unknown Item")
                    }
                },
                onSeeAllClick = onSeeAllClick
            )
        }
        is DashboardUiState.Error -> ErrorContent(
            title = "Error",
            message = state.message,
            onRetry = { viewModel.loadDashboard() }
        )
    }
}