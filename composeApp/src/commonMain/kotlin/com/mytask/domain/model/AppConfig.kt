package com.mytask.domain.model

import kotlin.time.Instant

data class AppConfig(
    val id: Int = 0,
    val googleSheetsUrl: String = "",
    val createdAt: Instant = Instant.DISTANT_PAST,
    val updatedAt: Instant = Instant.DISTANT_PAST
)