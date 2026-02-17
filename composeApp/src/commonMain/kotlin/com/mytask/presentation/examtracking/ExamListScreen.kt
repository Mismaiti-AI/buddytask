package com.mytask.presentation.examtracking

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import org.koin.compose.viewmodel.koinViewModel

import com.mytask.domain.model.Exam

import com.mytask.core.presentation.screens.GenericListScreen
import com.mytask.core.presentation.components.ListItemCard
import com.mytask.core.presentation.components.ErrorContent

@Composable
fun ExamListScreen(
    viewModel: ExamListViewModel = koinViewModel(),
    onItemClick: (String) -> Unit = {},
    onBackClick: () -> Unit = {},
    onAddClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    when (val state = uiState) {
        is ExamListUiState.Loading -> CircularProgressIndicator()
        is ExamListUiState.Success -> {
            GenericListScreen(
                title = "Upcoming Exams",
                items = state.items,
                onItemClick = { exam -> onItemClick(exam.id.toString()) },
                onAddClick = onAddClick,
                onRefresh = { viewModel.refresh() },
                itemContent = { exam ->
                    ListItemCard(
                        title = exam.title,
                        subtitle = exam.subject,
                        caption = "Date: ${exam.examDate.toString().substring(0, 10)}",
                        leadingIcon = Icons.Default.Event
                    )
                }
            )
        }
        is ExamListUiState.Error -> ErrorContent(
            title = "Error",
            message = state.message,
            onRetry = { viewModel.refresh() }
        )
    }
}