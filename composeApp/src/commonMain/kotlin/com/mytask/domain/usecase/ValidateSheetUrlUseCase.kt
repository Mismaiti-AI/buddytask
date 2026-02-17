package com.mytask.domain.usecase

import com.mytask.data.repository.appconfig.AppConfigRepository

class ValidateSheetUrlUseCase(
    private val repository: AppConfigRepository
) {
    suspend operator fun invoke(url: String): Boolean = repository.validateUrl(url)
}