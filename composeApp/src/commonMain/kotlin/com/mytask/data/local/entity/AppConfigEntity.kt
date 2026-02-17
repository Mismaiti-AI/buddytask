package com.mytask.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mytask.domain.model.AppConfig
import kotlin.time.Instant

@Entity(tableName = "app_config")
class AppConfigEntity {
    @PrimaryKey(autoGenerate = true) var id: Int = 0
    @ColumnInfo(name = "google_sheets_url") var googleSheetsUrl: String = ""
    @ColumnInfo(name = "created_at") var createdAt: Long = 0L
    @ColumnInfo(name = "updated_at") var updatedAt: Long = 0L

    fun toDomain(): AppConfig {
        return AppConfig(
            id = id,
            googleSheetsUrl = googleSheetsUrl,
            createdAt = Instant.fromEpochMilliseconds(createdAt),
            updatedAt = Instant.fromEpochMilliseconds(updatedAt)
        )
    }

    companion object {
        fun fromDomain(config: AppConfig): AppConfigEntity {
            return AppConfigEntity().apply {
                id = config.id
                googleSheetsUrl = config.googleSheetsUrl
                createdAt = config.createdAt.toEpochMilliseconds()
                updatedAt = config.updatedAt.toEpochMilliseconds()
            }
        }
    }
}