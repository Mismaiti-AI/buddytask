package com.mytask.data.repository.assignment

import kotlinx.coroutines.flow.StateFlow
import com.mytask.domain.model.Assignment

interface AssignmentRepository {
    val items: StateFlow<List<Assignment>>
    val isLoading: StateFlow<Boolean>
    val error: StateFlow<String?>
    
    suspend fun loadAll()
    suspend fun getById(id: String): Assignment?
    suspend fun insert(assignment: Assignment)
    suspend fun update(assignment: Assignment)
    suspend fun delete(id: String)
    suspend fun markComplete(id: String)
    suspend fun getUpcomingAssignments(): List<Assignment>
}