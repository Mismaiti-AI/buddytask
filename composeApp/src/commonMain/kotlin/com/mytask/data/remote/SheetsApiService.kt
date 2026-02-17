package com.mytask.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.Serializable
import com.mytask.domain.model.Assignment
import com.mytask.domain.model.Exam
import com.mytask.domain.model.Project
import kotlin.time.Instant

class SheetsApiService(
    private val httpClient: HttpClient,
    private val scriptUrl: String
) {
    suspend fun fetchAssignments(): List<Assignment> {
        val response = httpClient.get("$scriptUrl?action=getAssignments")
        return response.body<List<AssignmentDto>>().map { it.toDomain() }
    }

    suspend fun fetchExams(): List<Exam> {
        val response = httpClient.get("$scriptUrl?action=getExams")
        return response.body<List<ExamDto>>().map { it.toDomain() }
    }

    suspend fun fetchProjects(): List<Project> {
        val response = httpClient.get("$scriptUrl?action=getProjects")
        return response.body<List<ProjectDto>>().map { it.toDomain() }
    }

    suspend fun validateConnection(url: String): Boolean {
        return try {
            httpClient.get("$scriptUrl?action=testConnection")
            true
        } catch (e: Exception) {
            false
        }
    }
}

@Serializable
data class AssignmentDto(
    val id: Int = 0,
    val title: String = "",
    val description: String = "",
    val dueDate: Long = 0L,
    val subject: String = "",
    val completed: Boolean = false,
    val priority: String = ""
) {
    fun toDomain(): Assignment = Assignment(
        id = id,
        title = title,
        description = description,
        dueDate = Instant.fromEpochMilliseconds(dueDate),
        subject = subject,
        completed = completed,
        priority = priority
    )
}

@Serializable
data class ExamDto(
    val id: Int = 0,
    val title: String = "",
    val subject: String = "",
    val examDate: Long = 0L,
    val description: String = "",
    val preparationStatus: Boolean = false
) {
    fun toDomain(): Exam = Exam(
        id = id,
        title = title,
        subject = subject,
        examDate = Instant.fromEpochMilliseconds(examDate),
        description = description,
        preparationStatus = preparationStatus
    )
}

@Serializable
data class ProjectDto(
    val id: Int = 0,
    val title: String = "",
    val description: String = "",
    val startDate: Long = 0L,
    val dueDate: Long = 0L,
    val subject: String = "",
    val progress: Int = 0,
    val completed: Boolean = false
) {
    fun toDomain(): Project = Project(
        id = id,
        title = title,
        description = description,
        startDate = Instant.fromEpochMilliseconds(startDate),
        dueDate = Instant.fromEpochMilliseconds(dueDate),
        subject = subject,
        progress = progress,
        completed = completed
    )
}