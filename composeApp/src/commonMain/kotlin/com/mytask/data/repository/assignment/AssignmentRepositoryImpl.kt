package com.mytask.data.repository.assignment

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.mytask.data.local.dao.AssignmentDao
import com.mytask.data.local.entity.toDomain
import com.mytask.data.local.entity.toEntity
import com.mytask.domain.model.Assignment
import com.mytask.data.remote.SheetsApiService
import kotlinx.coroutines.flow.update
import kotlin.time.Instant
import kotlin.time.Clock

class AssignmentRepositoryImpl(
    private val dao: AssignmentDao,
    private val remoteSource: SheetsApiService
) : AssignmentRepository {
    private val _items = MutableStateFlow<List<Assignment>>(emptyList())
    override val items: StateFlow<List<Assignment>> = _items.asStateFlow()
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
            val remote = remoteSource.fetchAssignments()

            // Update cache and emit fresh data
            remote.forEach { dao.insert(it.toEntity()) }
            _items.value = dao.getAll().map { it.toDomain() }
            _error.value = null
        } catch (e: Exception) {
            _error.value = e.message
        }
        _isLoading.value = false
    }

    override suspend fun getById(id: String): Assignment? =
        dao.getById(id.toInt())?.toDomain()

    override suspend fun insert(assignment: Assignment) {
        dao.insert(assignment.toEntity())
        loadAll()
    }

    override suspend fun update(assignment: Assignment) {
        dao.insert(assignment.toEntity())
        loadAll()
    }

    override suspend fun delete(id: String) {
        dao.delete(id.toInt())
        loadAll()
    }

    override suspend fun markComplete(id: String) {
        dao.markComplete(id.toInt())
        loadAll()
    }

    override suspend fun getUpcomingAssignments(): List<Assignment> {
        val now = Clock.System.now().toEpochMilliseconds()
        return dao.getUpcoming(now).map { it.toDomain() }
    }
}