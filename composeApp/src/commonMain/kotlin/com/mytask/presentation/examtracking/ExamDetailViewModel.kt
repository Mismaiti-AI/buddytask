package com.mytask.presentation.examtracking

// Framework
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Domain model (from Phase 2):
import com.mytask.domain.model.Exam

// Repository (from Phase 3):
import com.mytask.data.repository.exam.ExamRepository

sealed interface ExamDetailUiState {
    data object Loading : ExamDetailUiState
    data class Success(val item: Exam) : ExamDetailUiState
    data class Error(val message: String) : ExamDetailUiState
}

class ExamDetailViewModel(
    private val repository: ExamRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<ExamDetailUiState>(ExamDetailUiState.Loading)
    val uiState: StateFlow<ExamDetailUiState> = _uiState.asStateFlow()

    fun loadExam(id: String) {
        viewModelScope.launch {
            _uiState.value = ExamDetailUiState.Loading
            val item = repository.getById(id)
            _uiState.value = if (item != null) {
                ExamDetailUiState.Success(item)
            } else {
                ExamDetailUiState.Error("Exam not found")
            }
        }
    }

    fun deleteExam(id: String) {
        viewModelScope.launch { repository.delete(id) }
    }
}