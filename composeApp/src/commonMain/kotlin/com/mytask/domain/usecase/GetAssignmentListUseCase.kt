package com.mytask.domain.usecase

import kotlinx.coroutines.flow.StateFlow
import com.mytask.domain.model.Assignment
import com.mytask.data.repository.assignment.AssignmentRepository

class GetAssignmentListUseCase(
    private val repository: AssignmentRepository
) {
    operator fun invoke(): StateFlow<List<Assignment>> = repository.items
}