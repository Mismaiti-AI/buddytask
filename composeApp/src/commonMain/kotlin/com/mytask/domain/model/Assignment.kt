package com.mytask.domain.model

import kotlin.time.Instant

data class Assignment(
    val id: Int = 0,
    val title: String = "",
    val description: String = "",
    val dueDate: Instant = Instant.DISTANT_PAST,
    val subject: String = "",
    val completed: Boolean = false,
    val priority: String = ""
)