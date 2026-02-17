package com.mytask.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mytask.domain.model.Assignment
import kotlin.time.Instant

@Entity(tableName = "assignments")
class AssignmentEntity {
    @PrimaryKey(autoGenerate = true) var id: Int = 0
    @ColumnInfo(name = "title") var title: String = ""
    @ColumnInfo(name = "description") var description: String = ""
    @ColumnInfo(name = "due_date") var dueDate: Long = 0L
    @ColumnInfo(name = "subject") var subject: String = ""
    @ColumnInfo(name = "completed") var completed: Boolean = false
    @ColumnInfo(name = "priority") var priority: String = ""

    fun toDomain(): Assignment {
        return Assignment(
            id = id,
            title = title,
            description = description,
            dueDate = Instant.fromEpochMilliseconds(dueDate),
            subject = subject,
            completed = completed,
            priority = priority
        )
    }

    companion object {
        fun fromDomain(assignment: Assignment): AssignmentEntity {
            return AssignmentEntity().apply {
                id = assignment.id
                title = assignment.title
                description = assignment.description
                dueDate = assignment.dueDate.toEpochMilliseconds()
                subject = assignment.subject
                completed = assignment.completed
                priority = assignment.priority
            }
        }
    }
}