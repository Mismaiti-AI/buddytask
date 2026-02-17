package com.mytask.data.repository.appconfig

import kotlinx.coroutines.flow.StateFlow
import com.mytask.domain.model.AppConfig

interface AppConfigRepository {
    val items: StateFlow<List<AppConfig>>
    val isLoading: StateFlow<Boolean>
    val error: StateFlow<String?>
    
    suspend fun getCurrent(): AppConfig?
    suspend fun updateUrl(url: String)
    suspend fun validateUrl(url: String): Boolean
}