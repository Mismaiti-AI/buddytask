package com.mytask.presentation.dashboardoverview

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
import com.mytask.domain.model.Exam
import com.mytask.domain.model.Project

// Repository (from Phase 3):
import com.mytask.data.repository.assignment.AssignmentRepository
import com.mytask.data.repository.exam.ExamRepository
import com.mytask.data.repository.project.ProjectRepository

// OR use cases (from Phase 4) if used:
import com.mytask.domain.usecase.GetDashboardOverviewUseCase
import com.mytask.domain.usecase.GetUpcomingItemsUseCase

sealed interface DashboardUiState {
    data object Loading : DashboardUiState
    data class Success(
        val totalAssignments: Int,
        val completedAssignments: Int,
        val totalExams: Int,
        val upcomingExams: Int,
        val totalProjects: Int,
        val activeProjects: Int,
        val upcomingItems: List<GetUpcomingItemsUseCase.UpcomingItem>
    ) : DashboardUiState
    data class Error(val message: String) : DashboardUiState
}

class DashboardViewModel(
    private val assignmentRepository: AssignmentRepository,
    private val examRepository: ExamRepository,
    private val projectRepository: ProjectRepository
) : ViewModel() {
    private val getDashboardOverviewUseCase = GetDashboardOverviewUseCase(
        assignmentRepository, 
        examRepository, 
        projectRepository
    )
    private val getUpcomingItemsUseCase = GetUpcomingItemsUseCase(
        assignmentRepository, 
        examRepository, 
        projectRepository
    )

    val uiState: StateFlow<DashboardUiState> = combine(
        assignmentRepository.items,
        examRepository.items,
        projectRepository.items,
        assignmentRepository.isLoading,
        examRepository.isLoading,
        projectRepository.isLoading,
        assignmentRepository.error,
        examRepository.error,
        projectRepository.error
    ) { assignments: List<Assignment>, exams: List<Exam>, projects: List<Project>,
        assignmentLoading: Boolean, examLoading: Boolean, projectLoading: Boolean,
        assignmentError: String?, examError: String?, projectError: String? ->
        
        when {
            assignmentError != null || examError != null || projectError != null -> {
                val errorMessage = listOfNotNull(assignmentError, examError, projectError).firstOrNull()
                DashboardUiState.Error(errorMessage ?: "Unknown error occurred")
            }
            assignmentLoading || examLoading || projectLoading -> DashboardUiState.Loading
            else -> {
                val dashboardStats = runCatching { 
                    getDashboardOverviewUseCase.invoke() 
                }.getOrNull()
                
                val upcomingItems = runCatching { 
                    getUpcomingItemsUseCase.invoke() 
                }.getOrNull() ?: emptyList()
                
                if (dashboardStats != null) {
                    DashboardUiState.Success(
                        totalAssignments = dashboardStats.totalAssignments,
                        completedAssignments = dashboardStats.completedAssignments,
                        totalExams = dashboardStats.totalExams,
                        upcomingExams = dashboardStats.upcomingExams,
                        totalProjects = dashboardStats.totalProjects,
                        activeProjects = dashboardStats.activeProjects,
                        upcomingItems = upcomingItems
                    )
                } else {
                    DashboardUiState.Error("Failed to load dashboard data")
                }
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardUiState.Loading)

    init { 
        loadDashboard() 
    }

    fun loadDashboard() { 
        viewModelScope.launch { 
            assignmentRepository.loadAll()
            examRepository.loadAll()
            projectRepository.loadAll()
        } 
    }
}