package com.mytask.domain.usecase

import com.mytask.domain.model.Exam
import com.mytask.data.repository.exam.ExamRepository

class ViewExamDetailsUseCase(
    private val repository: ExamRepository
) {
    suspend operator fun invoke(id: String): Exam? = repository.getById(id)
}