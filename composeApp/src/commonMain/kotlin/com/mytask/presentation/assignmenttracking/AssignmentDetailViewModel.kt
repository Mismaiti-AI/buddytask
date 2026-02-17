package com.mytask.presentation.assignmenttracking

// Framework
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Domain model (from Phase 2):
import com.mytask.domain.model.Assignment

// Repository (from Phase 3):
import com.mytask.data.repository.assignment.AssignmentRepository

sealed interface AssignmentDetailUiState {
    data object Loading : AssignmentDetailUiState
    data class Success(val item: Assignment) : AssignmentDetailUiState
    data class Error(val message: String) : AssignmentDetailUiState
}

class AssignmentDetailViewModel(
    private val repository: AssignmentRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<AssignmentDetailUiState>(AssignmentDetailUiState.Loading)
    val uiState: StateFlow<AssignmentDetailUiState> = _uiState.asStateFlow()

    fun loadAssignment(id: String) {
        viewModelScope.launch {
            _uiState.value = AssignmentDetailUiState.Loading
            val item = repository.getById(id)
            _uiState.value = if (item != null) {
                AssignmentDetailUiState.Success(item)
            } else {
                AssignmentDetailUiState.Error("Assignment not found")
            }
        }
    }

    fun deleteAssignment(id: String) {
        viewModelScope.launch { repository.delete(id) }
    }
}