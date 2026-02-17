package com.mytask.domain.usecase

import com.mytask.domain.model.Assignment
import com.mytask.data.repository.assignment.AssignmentRepository

class MarkAssignmentCompleteUseCase(
    private val repository: AssignmentRepository
) {
    suspend operator fun invoke(id: String) = repository.markComplete(id)
}