package com.mytask.data.repository.appconfig

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.mytask.data.local.dao.AppConfigDao
import com.mytask.data.local.entity.toDomain
import com.mytask.data.local.entity.toEntity
import com.mytask.domain.model.AppConfig
import com.mytask.data.remote.SheetsApiService
import kotlinx.coroutines.flow.update
import kotlin.time.Instant

class AppConfigRepositoryImpl(
    private val dao: AppConfigDao,
    private val remoteSource: SheetsApiService
) : AppConfigRepository {
    private val _items = MutableStateFlow<List<AppConfig>>(emptyList())
    override val items: StateFlow<List<AppConfig>> = _items.asStateFlow()
    private val _isLoading = MutableStateFlow(false)
    override val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    private val _error = MutableStateFlow<String?>(null)
    override val error: StateFlow<String?> = _error.asStateFlow()

    override suspend fun getCurrent(): AppConfig? {
        return dao.getLatest()?.toDomain()
    }

    override suspend fun updateUrl(url: String) {
        val config = AppConfig(
            id = 0,
            googleSheetsUrl = url,
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
        dao.insert(config.toEntity())
    }

    override suspend fun validateUrl(url: String): Boolean {
        return try {
            remoteSource.validateConnection(url)
            true
        } catch (e: Exception) {
            false
        }
    }
}