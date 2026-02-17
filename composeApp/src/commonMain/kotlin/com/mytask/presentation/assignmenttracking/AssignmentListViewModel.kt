package com.mytask.presentation.assignmenttracking

// Framework
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// Domain model (from Phase 2):
import com.mytask.domain.model.Assignment

// Repository (from Phase 3):
import com.mytask.data.repository.assignment.AssignmentRepository

// OR use cases (from Phase 4) if used:
import com.mytask.domain.usecase.GetAssignmentListUseCase
import com.mytask.domain.usecase.MarkAssignmentCompleteUseCase

sealed interface AssignmentListUiState {
    data object Loading : AssignmentListUiState
    data class Success(val items: List<Assignment>) : AssignmentListUiState
    data class Error(val message: String) : AssignmentListUiState
}

class AssignmentListViewModel(
    private val repository: AssignmentRepository
) : ViewModel() {
    val uiState: StateFlow<AssignmentListUiState> = combine(
        repository.items,
        repository.isLoading,
        repository.error
    ) { items, loading, error ->
        when {
            error != null -> AssignmentListUiState.Error(error)
            loading -> AssignmentListUiState.Loading
            else -> AssignmentListUiState.Success(items = items)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AssignmentListUiState.Loading)

    init { loadAssignments() }

    fun loadAssignments() { 
        viewModelScope.launch { 
            repository.loadAll() 
        } 
    }
    
    fun markComplete(id: String) { 
        viewModelScope.launch { 
            repository.markComplete(id) 
        } 
    }
    
    fun refresh() { loadAssignments() }
}