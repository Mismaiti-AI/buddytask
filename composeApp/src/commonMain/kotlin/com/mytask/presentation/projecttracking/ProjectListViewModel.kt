package com.mytask.presentation.projecttracking

// Framework
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// Domain model (from Phase 2):
import com.mytask.domain.model.Project

// Repository (from Phase 3):
import com.mytask.data.repository.project.ProjectRepository

sealed interface ProjectListUiState {
    data object Loading : ProjectListUiState
    data class Success(val items: List<Project>) : ProjectListUiState
    data class Error(val message: String) : ProjectListUiState
}

class ProjectListViewModel(
    private val repository: ProjectRepository
) : ViewModel() {
    val uiState: StateFlow<ProjectListUiState> = combine(
        repository.items,
        repository.isLoading,
        repository.error
    ) { items, loading, error ->
        when {
            error != null -> ProjectListUiState.Error(error)
            loading -> ProjectListUiState.Loading
            else -> ProjectListUiState.Success(items = items)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ProjectListUiState.Loading)

    init { loadProjects() }

    fun loadProjects() { 
        viewModelScope.launch { 
            repository.loadAll() 
        } 
    }
    
    fun deleteItem(id: String) { 
        viewModelScope.launch { 
            repository.delete(id) 
        } 
    }
    
    fun refresh() { loadProjects() }
}