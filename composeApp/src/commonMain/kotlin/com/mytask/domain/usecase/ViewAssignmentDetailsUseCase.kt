package com.mytask.domain.usecase

import com.mytask.domain.model.Assignment
import com.mytask.data.repository.assignment.AssignmentRepository

class ViewAssignmentDetailsUseCase(
    private val repository: AssignmentRepository
) {
    suspend operator fun invoke(id: String): Assignment? = repository.getById(id)
}