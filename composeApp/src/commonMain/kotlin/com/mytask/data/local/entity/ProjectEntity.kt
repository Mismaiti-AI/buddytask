package com.mytask.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mytask.domain.model.Project
import java.time.Instant

@Entity(tableName = "projects")
class ProjectEntity {
    @PrimaryKey(autoGenerate = true) var id: Int = 0
    @ColumnInfo(name = "title") var title: String = ""
    @ColumnInfo(name = "description") var description: String = ""
    @ColumnInfo(name = "start_date") var startDate: Long = 0L
    @ColumnInfo(name = "due_date") var dueDate: Long = 0L
    @ColumnInfo(name = "subject") var subject: String = ""
    @ColumnInfo(name = "progress") var progress: Int = 0
    @ColumnInfo(name = "completed") var completed: Boolean = false

    fun toDomain(): Project {
        return Project(
            id = id,
            title = title,
            description = description,
            startDate = Instant.ofEpochMilli(startDate),
            dueDate = Instant.ofEpochMilli(dueDate),
            subject = subject,
            progress = progress,
            completed = completed
        )
    }

    companion object {
        fun fromDomain(project: Project): ProjectEntity {
            return ProjectEntity().apply {
                id = project.id
                title = project.title
                description = project.description
                startDate = project.startDate.toEpochMilli()
                dueDate = project.dueDate.toEpochMilli()
                subject = project.subject
                progress = project.progress
                completed = project.completed
            }
        }
    }
}