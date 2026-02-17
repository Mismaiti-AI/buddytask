package com.mytask.domain.usecase

import com.mytask.domain.model.AppConfig
import com.mytask.data.repository.appconfig.AppConfigRepository

class GetCurrentSheetConfigUseCase(
    private val repository: AppConfigRepository
) {
    suspend operator fun invoke(): AppConfig? = repository.getCurrent()
}