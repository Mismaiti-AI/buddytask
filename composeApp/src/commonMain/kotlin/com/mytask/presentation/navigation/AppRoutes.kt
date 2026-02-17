package com.mytask.presentation.navigation

import kotlinx.serialization.Serializable

object AppRoutes {
    // Tab routes (bottom nav):
    @Serializable object Dashboard
    @Serializable object Assignments
    @Serializable object Exams
    @Serializable object Projects

    // Detail routes (no bottom nav):
    @Serializable data class AssignmentDetail(val id: String)
    @Serializable data class ExamDetail(val id: String)
    @Serializable data class ProjectDetail(val id: String)
    @Serializable object Settings
    @Serializable object SheetUrlConfig
}