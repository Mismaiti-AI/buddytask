package com.mytask.presentation.projecttracking

// Framework
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Domain model (from Phase 2):
import com.mytask.domain.model.Project

// Repository (from Phase 3):
import com.mytask.data.repository.project.ProjectRepository

sealed interface ProjectDetailUiState {
    data object Loading : ProjectDetailUiState
    data class Success(val item: Project) : ProjectDetailUiState
    data class Error(val message: String) : ProjectDetailUiState
}

class ProjectDetailViewModel(
    private val repository: ProjectRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<ProjectDetailUiState>(ProjectDetailUiState.Loading)
    val uiState: StateFlow<ProjectDetailUiState> = _uiState.asStateFlow()

    fun loadProject(id: String) {
        viewModelScope.launch {
            _uiState.value = ProjectDetailUiState.Loading
            val item = repository.getById(id)
            _uiState.value = if (item != null) {
                ProjectDetailUiState.Success(item)
            } else {
                ProjectDetailUiState.Error("Project not found")
            }
        }
    }

    fun deleteProject(id: String) {
        viewModelScope.launch { repository.delete(id) }
    }
}