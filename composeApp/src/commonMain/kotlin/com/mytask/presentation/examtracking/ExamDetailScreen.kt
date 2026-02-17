package com.mytask.presentation.examtracking

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import org.koin.compose.viewmodel.koinViewModel

import com.mytask.domain.model.Exam

import com.mytask.core.presentation.screens.GenericDetailScreen
import com.mytask.core.presentation.components.DetailCard
import com.mytask.core.presentation.components.DetailRow
import com.mytask.core.presentation.components.InfoRow
import com.mytask.core.presentation.components.ErrorContent

@Composable
fun ExamDetailScreen(
    examId: String,
    viewModel: ExamDetailViewModel = koinViewModel(),
    onBackClick: () -> Unit = {},
    onEditClick: (String) -> Unit = {},
    onDeleteClick: (String) -> Unit = {}
) {
    LaunchedEffect(examId) {
        viewModel.loadExam(examId)
    }

    val uiState by viewModel.uiState.collectAsState()
    
    when (val state = uiState) {
        is ExamDetailUiState.Loading -> CircularProgressIndicator()
        is ExamDetailUiState.Success -> {
            GenericDetailScreen(
                title = state.item.title,
                item = state.item,
                onBackClick = onBackClick,
                onEditClick = { item -> onEditClick(item.id.toString()) },
                onDeleteClick = { item -> 
                    viewModel.deleteExam(item.id.toString())
                    onDeleteClick(item.id.toString())
                },
                detailContent = { exam ->
                    DetailCard(
                        title = "Exam Details",
                        rows = listOf(
                            DetailRow("Subject", exam.subject),
                            DetailRow("Date", exam.examDate.toString().substring(0, 10)),
                            DetailRow("Description", exam.description)
                        ),
                        icon = Icons.Default.Event
                    )
                    
                    InfoRow(
                        label = "Preparation Status",
                        value = if(exam.preparationStatus) "Ready" else "Not Ready"
                    )
                }
            )
        }
        is ExamDetailUiState.Error -> ErrorContent(
            title = "Error",
            message = state.message,
            onRetry = { viewModel.loadExam(examId) }
        )
    }
}