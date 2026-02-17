package com.mytask.domain.usecase

import com.mytask.domain.model.Assignment
import com.mytask.domain.model.Exam
import com.mytask.domain.model.Project
import com.mytask.data.repository.assignment.AssignmentRepository
import com.mytask.data.repository.exam.ExamRepository
import com.mytask.data.repository.project.ProjectRepository

class GetDashboardOverviewUseCase(
    private val assignmentRepository: AssignmentRepository,
    private val examRepository: ExamRepository,
    private val projectRepository: ProjectRepository
) {
    data class DashboardStats(
        val totalAssignments: Int,
        val completedAssignments: Int,
        val totalExams: Int,
        val upcomingExams: Int,
        val totalProjects: Int,
        val activeProjects: Int
    )
    
    suspend operator fun invoke(): DashboardStats {
        val assignments = assignmentRepository.items.value
        val exams = examRepository.items.value
        val projects = projectRepository.items.value
        
        return DashboardStats(
            totalAssignments = assignments.size,
            completedAssignments = assignments.count { it.completed },
            totalExams = exams.size,
            upcomingExams = examRepository.getUpcomingExams().size,
            totalProjects = projects.size,
            activeProjects = projects.count { !it.completed }
        )
    }
}