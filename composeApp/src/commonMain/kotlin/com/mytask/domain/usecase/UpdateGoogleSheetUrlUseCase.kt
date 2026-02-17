package com.mytask.domain.usecase

import com.mytask.data.repository.appconfig.AppConfigRepository

class UpdateGoogleSheetUrlUseCase(
    private val repository: AppConfigRepository
) {
    suspend operator fun invoke(url: String) = repository.updateUrl(url)
}