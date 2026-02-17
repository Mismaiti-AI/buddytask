package com.mytask.data.repository.exam

import kotlinx.coroutines.flow.StateFlow
import com.mytask.domain.model.Exam

interface ExamRepository {
    val items: StateFlow<List<Exam>>
    val isLoading: StateFlow<Boolean>
    val error: StateFlow<String?>
    
    suspend fun loadAll()
    suspend fun getById(id: String): Exam?
    suspend fun insert(exam: Exam)
    suspend fun update(exam: Exam)
    suspend fun delete(id: String)
    suspend fun getUpcomingExams(): List<Exam>
}