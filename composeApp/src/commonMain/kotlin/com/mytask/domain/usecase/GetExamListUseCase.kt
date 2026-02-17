package com.mytask.domain.usecase

import kotlinx.coroutines.flow.StateFlow
import com.mytask.domain.model.Exam
import com.mytask.data.repository.exam.ExamRepository

class GetExamListUseCase(
    private val repository: ExamRepository
) {
    operator fun invoke(): StateFlow<List<Exam>> = repository.items
}