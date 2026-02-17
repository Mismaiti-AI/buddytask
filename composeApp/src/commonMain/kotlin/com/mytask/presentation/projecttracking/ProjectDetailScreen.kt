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

import com.mytask.core.presentation.screens.GenericDetailScreen
import com.mytask.core.presentation.components.DetailCard
import com.mytask.core.presentation.components.DetailRow
import com.mytask.core.presentation.components.ProgressCard
import com.mytask.core.presentation.components.InfoRow
import com.mytask.core.presentation.components.ErrorContent

@Composable
fun ProjectDetailScreen(
    projectId: String,
    viewModel: ProjectDetailViewModel = koinViewModel(),
    onBackClick: () -> Unit = {},
    onEditClick: (String) -> Unit = {},
    onDeleteClick: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    
    when (val state = uiState) {
        is ProjectDetailUiState.Loading -> CircularProgressIndicator()
        is ProjectDetailUiState.Success -> {
            GenericDetailScreen(
                title = state.item.title,
                item = state.item,
                onBackClick = onBackClick,
                onEditClick = { item -> onEditClick(item.id.toString()) },
                onDeleteClick = { item -> 
                    viewModel.deleteProject(item.id.toString())
                    onDeleteClick(item.id.toString())
                },
                detailContent = { project ->
                    ProgressCard(
                        title = "Progress",
                        progress = project.progress.toFloat() / 100f,
                        subtitle = "${project.progress}% Complete"
                    )
                    
                    DetailCard(
                        title = "Project Details",
                        rows = listOf(
                            DetailRow("Subject", project.subject),
                            DetailRow("Start Date", project.startDate.toString().substring(0, 10)),
                            DetailRow("Due Date", project.dueDate.toString().substring(0, 10)),
                            DetailRow("Description", project.description)
                        ),
                        icon = Icons.Default.BarChart
                    )
                    
                    InfoRow(
                        label = "Status",
                        value = if(project.completed) "Completed" else "Active"
                    )
                }
            )
        }
        is ProjectDetailUiState.Error -> ErrorContent(
            title = "Error",
            message = state.message,
            onRetry = { viewModel.loadProject(projectId) }
        )
    }
}