package com.mytask.data.repository.project

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.mytask.data.local.dao.ProjectDao
import com.mytask.data.local.entity.toDomain
import com.mytask.data.local.entity.toEntity
import com.mytask.domain.model.Project
import com.mytask.data.remote.SheetsApiService
import kotlinx.coroutines.flow.update
import kotlin.time.Instant
import kotlin.time.Clock

class ProjectRepositoryImpl(
    private val dao: ProjectDao,
    private val remoteSource: SheetsApiService
) : ProjectRepository {
    private val _items = MutableStateFlow<List<Project>>(emptyList())
    override val items: StateFlow<List<Project>> = _items.asStateFlow()
    private val _isLoading = MutableStateFlow(false)
    override val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    private val _error = MutableStateFlow<String?>(null)
    override val error: StateFlow<String?> = _error.asStateFlow()

    override suspend fun loadAll() {
        _isLoading.value = true
        try {
            // Emit cached data immediately
            _items.value = dao.getAll().map { it.toDomain() }

            // Fetch remote in background
            val remote = remoteSource.fetchProjects()

            // Update cache and emit fresh data
            remote.forEach { dao.insert(it.toEntity()) }
            _items.value = dao.getAll().map { it.toDomain() }
            _error.value = null
        } catch (e: Exception) {
            _error.value = e.message
        }
        _isLoading.value = false
    }

    override suspend fun getById(id: String): Project? =
        dao.getById(id.toInt())?.toDomain()

    override suspend fun insert(project: Project) {
        dao.insert(project.toEntity())
        loadAll()
    }

    override suspend fun update(project: Project) {
        dao.insert(project.toEntity())
        loadAll()
    }

    override suspend fun delete(id: String) {
        dao.delete(id.toInt())
        loadAll()
    }

    override suspend fun updateProgress(id: String, progress: Int) {
        dao.updateProgress(id.toInt(), progress)
        loadAll()
    }

    override suspend fun getUpcomingProjects(): List<Project> {
        val now = Clock.System.now().toEpochMilliseconds()
        return dao.getUpcomingProjects(now).map { it.toDomain() }
    }
}