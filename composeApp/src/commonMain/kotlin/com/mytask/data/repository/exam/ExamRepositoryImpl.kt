package com.mytask.data.repository.exam

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.mytask.data.local.dao.ExamDao
import com.mytask.data.local.entity.toDomain
import com.mytask.data.local.entity.toEntity
import com.mytask.domain.model.Exam
import com.mytask.data.remote.SheetsApiService
import kotlinx.coroutines.flow.update
import kotlin.time.Instant

class ExamRepositoryImpl(
    private val dao: ExamDao,
    private val remoteSource: SheetsApiService
) : ExamRepository {
    private val _items = MutableStateFlow<List<Exam>>(emptyList())
    override val items: StateFlow<List<Exam>> = _items.asStateFlow()
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
            val remote = remoteSource.fetchExams()
            
            // Update cache and emit fresh data
            remote.forEach { dao.insert(it.toEntity()) }
            _items.value = dao.getAll().map { it.toDomain() }
            _error.value = null
        } catch (e: Exception) {
            _error.value = e.message
        }
        _isLoading.value = false
    }

    override suspend fun getById(id: String): Exam? =
        dao.getById(id.toInt())?.toDomain()

    override suspend fun insert(exam: Exam) {
        dao.insert(exam.toEntity())
        loadAll()
    }

    override suspend fun update(exam: Exam) {
        dao.insert(exam.toEntity())
        loadAll()
    }

    override suspend fun delete(id: String) {
        dao.delete(id.toInt())
        loadAll()
    }

    override suspend fun getUpcomingExams(): List<Exam> {
        val now = Instant.now().toEpochMilliseconds()
        return dao.getUpcomingExams(now).map { it.toDomain() }
    }
}