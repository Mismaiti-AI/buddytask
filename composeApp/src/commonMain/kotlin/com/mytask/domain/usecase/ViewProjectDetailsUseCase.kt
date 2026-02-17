package com.mytask.domain.usecase

import com.mytask.domain.model.Project
import com.mytask.data.repository.project.ProjectRepository

class ViewProjectDetailsUseCase(
    private val repository: ProjectRepository
) {
    suspend operator fun invoke(id: String): Project? = repository.getById(id)
}