package com.mytask.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mytask.domain.model.Exam
import java.time.Instant

@Entity(tableName = "exams")
class ExamEntity {
    @PrimaryKey(autoGenerate = true) var id: Int = 0
    @ColumnInfo(name = "title") var title: String = ""
    @ColumnInfo(name = "subject") var subject: String = ""
    @ColumnInfo(name = "exam_date") var examDate: Long = 0L
    @ColumnInfo(name = "description") var description: String = ""
    @ColumnInfo(name = "preparation_status") var preparationStatus: Boolean = false

    fun toDomain(): Exam {
        return Exam(
            id = id,
            title = title,
            subject = subject,
            examDate = Instant.ofEpochMilli(examDate),
            description = description,
            preparationStatus = preparationStatus
        )
    }

    companion object {
        fun fromDomain(exam: Exam): ExamEntity {
            return ExamEntity().apply {
                id = exam.id
                title = exam.title
                subject = exam.subject
                examDate = exam.examDate.toEpochMilli()
                description = exam.description
                preparationStatus = exam.preparationStatus
            }
        }
    }
}