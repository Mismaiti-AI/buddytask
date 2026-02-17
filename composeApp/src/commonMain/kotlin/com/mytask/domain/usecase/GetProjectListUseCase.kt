package com.mytask.domain.usecase

import kotlinx.coroutines.flow.StateFlow
import com.mytask.domain.model.Project
import com.mytask.data.repository.project.ProjectRepository

class GetProjectListUseCase(
    private val repository: ProjectRepository
) {
    operator fun invoke(): StateFlow<List<Project>> = repository.items
}