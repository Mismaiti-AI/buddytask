package com.mytask.presentation.assignmenttracking

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import org.koin.compose.viewmodel.koinViewModel

import com.mytask.domain.model.Assignment

import com.mytask.core.presentation.screens.GenericListScreen
import com.mytask.core.presentation.components.ListItemCard
import com.mytask.core.presentation.components.ErrorContent
import androidx.compose.material3.MaterialTheme

@Composable
fun AssignmentListScreen(
    viewModel: AssignmentListViewModel = koinViewModel(),
    onItemClick: (String) -> Unit = {},
    onBackClick: () -> Unit = {},
    onAddClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    when (val state = uiState) {
        is AssignmentListUiState.Loading -> CircularProgressIndicator()
        is AssignmentListUiState.Success -> {
            GenericListScreen(
                title = "My Assignments",
                items = state.items,
                onItemClick = { assignment -> onItemClick(assignment.id.toString()) },
                onAddClick = onAddClick,
                onRefresh = { viewModel.refresh() },
                itemContent = { assignment ->
                    ListItemCard(
                        title = assignment.title,
                        subtitle = assignment.subject,
                        caption = "Due: ${assignment.dueDate.toString().substring(0, 10)}",
                        leadingIcon = if(assignment.completed) Icons.Default.CheckCircle else Icons.Default.Circle,
                        trailingContent = {
                            if(assignment.completed) {
                                androidx.compose.material3.Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Completed",
                                    tint = androidx.compose.material3.MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    )
                }
            )
        }
        is AssignmentListUiState.Error -> ErrorContent(
            title = "Error",
            message = state.message,
            onRetry = { viewModel.refresh() }
        )
    }
}