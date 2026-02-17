package com.mismaiti.core.di

import androidx.room.Room
import com.mismaiti.core.data.local.AppDatabase
import com.mismaiti.core.data.local.AppSettings
import com.mismaiti.core.settings.IosAppSettings
import io.ktor.client.engine.darwin.Darwin
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.dsl.module
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDefaults
import platform.Foundation.NSUserDomainMask

@OptIn(ExperimentalForeignApi::class)
actual fun platformModule() = module {
    single {
        // Get iOS Documents directory (proper location for databases)
        val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = false,
            error = null
        )
        val dbPath = requireNotNull(documentDirectory?.path) + "/study_plan.db"

        Room.databaseBuilder<AppDatabase>(name = dbPath)
            .setDriver(androidx.sqlite.driver.bundled.BundledSQLiteDriver())           // REQUIRED for KMP
            .setQueryCoroutineContext(Dispatchers.IO)   // REQUIRED for async
            .fallbackToDestructiveMigration(true)       // Dev convenience
            .build()
    }

    single {
        Darwin.create()
    }

    single<AppSettings> { IosAppSettings(NSUserDefaults.standardUserDefaults) }
}