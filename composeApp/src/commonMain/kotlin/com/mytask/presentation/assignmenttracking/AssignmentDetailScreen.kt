package com.mytask.presentation.assignmenttracking

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import org.koin.compose.viewmodel.koinViewModel

import com.mytask.domain.model.Assignment

import com.mytask.core.presentation.screens.GenericDetailScreen
import com.mytask.core.presentation.components.DetailCard
import com.mytask.core.presentation.components.DetailRow
import com.mytask.core.presentation.components.StatusBadge
import com.mytask.core.presentation.components.ErrorContent

@Composable
fun AssignmentDetailScreen(
    assignmentId: String,
    viewModel: AssignmentDetailViewModel = koinViewModel(),
    onBackClick: () -> Unit = {},
    onEditClick: (String) -> Unit = {},
    onDeleteClick: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    
    when (val state = uiState) {
        is AssignmentDetailUiState.Loading -> CircularProgressIndicator()
        is AssignmentDetailUiState.Success -> {
            GenericDetailScreen(
                title = state.item.title,
                item = state.item,
                onBackClick = onBackClick,
                onEditClick = { item -> onEditClick(item.id.toString()) },
                onDeleteClick = { item -> 
                    viewModel.deleteAssignment(item.id.toString())
                    onDeleteClick(item.id.toString())
                },
                detailContent = { assignment ->
                    DetailCard(
                        title = "Assignment Details",
                        rows = listOf(
                            DetailRow("Subject", assignment.subject),
                            DetailRow("Due Date", assignment.dueDate.toString().substring(0, 10)),
                            DetailRow("Priority", assignment.priority),
                            DetailRow("Description", assignment.description)
                        ),
                        icon = if(assignment.completed) Icons.Default.CheckCircle else null
                    )
                    
                    if(assignment.completed) {
                        StatusBadge(text = "Completed")
                    } else {
                        StatusBadge(text = "Pending")
                    }
                }
            )
        }
        is AssignmentDetailUiState.Error -> ErrorContent(
            title = "Error",
            message = state.message,
            onRetry = { viewModel.loadAssignment(assignmentId) }
        )
    }
}