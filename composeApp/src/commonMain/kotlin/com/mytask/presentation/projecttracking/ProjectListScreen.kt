package com.mytask.presentation.projecttracking

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import org.koin.compose.viewmodel.koinViewModel

import com.mytask.domain.model.Project

import com.mytask.core.presentation.screens.GenericListScreen
import com.mytask.core.presentation.components.DetailCard
import com.mytask.core.presentation.components.DetailRow
import com.mytask.core.presentation.components.ErrorContent

@Composable
fun ProjectListScreen(
    viewModel: ProjectListViewModel = koinViewModel(),
    onItemClick: (String) -> Unit = {},
    onBackClick: () -> Unit = {},
    onAddClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    when (val state = uiState) {
        is ProjectListUiState.Loading -> CircularProgressIndicator()
        is ProjectListUiState.Success -> {
            GenericListScreen(
                title = "School Projects",
                items = state.items,
                onItemClick = { project -> onItemClick(project.id.toString()) },
                onAddClick = onAddClick,
                onRefresh = { viewModel.refresh() },
                itemContent = { project ->
                    DetailCard(
                        title = project.title,
                        rows = listOf(
                            DetailRow("Subject", project.subject),
                            DetailRow("Progress", "${project.progress}%"),
                            DetailRow("Start Date", project.startDate.toString().substring(0, 10)),
                            DetailRow("Due Date", project.dueDate.toString().substring(0, 10))
                        ),
                        icon = Icons.Default.BarChart
                    )
                }
            )
        }
        is ProjectListUiState.Error -> ErrorContent(
            title = "Error",
            message = state.message,
            onRetry = { viewModel.refresh() }
        )
    }
}