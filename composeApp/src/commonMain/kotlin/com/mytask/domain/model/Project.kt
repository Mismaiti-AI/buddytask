package com.mytask.domain.model

import kotlin.time.Instant

data class Project(
    val id: Int = 0,
    val title: String = "",
    val description: String = "",
    val startDate: Instant = Instant.DISTANT_PAST,
    val dueDate: Instant = Instant.DISTANT_PAST,
    val subject: String = "",
    val progress: Int = 0,
    val completed: Boolean = false
)