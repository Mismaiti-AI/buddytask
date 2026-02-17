package com.mytask.data.local.entity

import com.mytask.domain.model.AppConfig
import com.mytask.domain.model.Assignment
import com.mytask.domain.model.Exam
import com.mytask.domain.model.Project

// Extension functions for domain to entity mapping
fun AppConfig.toEntity(): AppConfigEntity = AppConfigEntity.fromDomain(this)
fun Assignment.toEntity(): AssignmentEntity = AssignmentEntity.fromDomain(this)
fun Exam.toEntity(): ExamEntity = ExamEntity.fromDomain(this)
fun Project.toEntity(): ProjectEntity = ProjectEntity.fromDomain(this)

// Extension functions for list mapping
fun List<AppConfigEntity>.toDomain(): List<AppConfig> = map { it.toDomain() }
fun List<AssignmentEntity>.toDomain(): List<Assignment> = map { it.toDomain() }
fun List<ExamEntity>.toDomain(): List<Exam> = map { it.toDomain() }
fun List<ProjectEntity>.toDomain(): List<Project> = map { it.toDomain() }
