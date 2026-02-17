package com.mytask.data.repository.project

import kotlinx.coroutines.flow.StateFlow
import com.mytask.domain.model.Project

interface ProjectRepository {
    val items: StateFlow<List<Project>>
    val isLoading: StateFlow<Boolean>
    val error: StateFlow<String?>
    
    suspend fun loadAll()
    suspend fun getById(id: String): Project?
    suspend fun insert(project: Project)
    suspend fun update(project: Project)
    suspend fun delete(id: String)
    suspend fun updateProgress(id: String, progress: Int)
    suspend fun getUpcomingProjects(): List<Project>
}