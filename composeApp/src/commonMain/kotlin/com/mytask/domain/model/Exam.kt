package com.mytask.domain.model

import kotlin.time.Instant

data class Exam(
    val id: Int = 0,
    val title: String = "",
    val subject: String = "",
    val examDate: Instant = Instant.DISTANT_PAST,
    val description: String = "",
    val preparationStatus: Boolean = false
)