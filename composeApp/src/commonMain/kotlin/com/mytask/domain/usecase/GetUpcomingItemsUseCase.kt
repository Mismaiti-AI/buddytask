package com.mytask.domain.usecase

import com.mytask.domain.model.Assignment
import com.mytask.domain.model.Exam
import com.mytask.domain.model.Project
import com.mytask.data.repository.assignment.AssignmentRepository
import com.mytask.data.repository.exam.ExamRepository
import com.mytask.data.repository.project.ProjectRepository

class GetUpcomingItemsUseCase(
    private val assignmentRepository: AssignmentRepository,
    private val examRepository: ExamRepository,
    private val projectRepository: ProjectRepository
) {
    data class UpcomingItem(
        val id: String,
        val title: String,
        val type: String,
        val date: Long
    )
    
    suspend operator fun invoke(): List<UpcomingItem> {
        val upcomingAssignments = assignmentRepository.getUpcomingAssignments()
        val upcomingExams = examRepository.getUpcomingExams()
        val upcomingProjects = projectRepository.getUpcomingProjects()
        
        val items = mutableListOf<UpcomingItem>()
        
        items.addAll(upcomingAssignments.map { 
            UpcomingItem(
                id = it.id.toString(),
                title = it.title,
                type = "Assignment",
                date = it.dueDate.toEpochMilliseconds()
            )
        })
        
        items.addAll(upcomingExams.map {
            UpcomingItem(
                id = it.id.toString(),
                title = it.title,
                type = "Exam",
                date = it.examDate.toEpochMilliseconds()
            )
        })
        
        items.addAll(upcomingProjects.map {
            UpcomingItem(
                id = it.id.toString(),
                title = it.title,
                type = "Project",
                date = it.dueDate.toEpochMilliseconds()
            )
        })
        
        return items.sortedBy { it.date }
    }
}