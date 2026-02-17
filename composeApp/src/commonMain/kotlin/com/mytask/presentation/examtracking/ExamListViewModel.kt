package com.mytask.presentation.examtracking

// Framework
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// Domain model (from Phase 2):
import com.mytask.domain.model.Exam

// Repository (from Phase 3):
import com.mytask.data.repository.exam.ExamRepository

sealed interface ExamListUiState {
    data object Loading : ExamListUiState
    data class Success(val items: List<Exam>) : ExamListUiState
    data class Error(val message: String) : ExamListUiState
}

class ExamListViewModel(
    private val repository: ExamRepository
) : ViewModel() {
    val uiState: StateFlow<ExamListUiState> = combine(
        repository.items,
        repository.isLoading,
        repository.error
    ) { items, loading, error ->
        when {
            error != null -> ExamListUiState.Error(error)
            loading -> ExamListUiState.Loading
            else -> ExamListUiState.Success(items = items)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ExamListUiState.Loading)

    init { loadExams() }

    fun loadExams() { 
        viewModelScope.launch { 
            repository.loadAll() 
        } 
    }
    
    fun deleteItem(id: String) { 
        viewModelScope.launch { 
            repository.delete(id) 
        } 
    }
    
    fun refresh() { loadExams() }
}