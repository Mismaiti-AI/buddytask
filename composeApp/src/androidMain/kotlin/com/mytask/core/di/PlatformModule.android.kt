package com.mytask.core.di

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.mytask.core.data.local.AppDatabase
import com.mytask.core.data.local.AppSettings
import com.mytask.core.settings.AndroidAppSettings
import io.ktor.client.engine.okhttp.OkHttp
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule() = module {
    // Database
    single {
        val appContext = androidContext().applicationContext
        val dbFile = appContext.getDatabasePath("study_plan.db")
        Room.databaseBuilder<AppDatabase>(
            context = appContext,
            name = dbFile.absolutePath
        )
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .fallbackToDestructiveMigration(true)
            .build()
    }

    single {
        OkHttp.create {
            preconfigured = getOkHttpClient()
        }
    }

    single<AppSettings> { AndroidAppSettings(androidContext()) }
}

private fun getOkHttpClient() = OkHttpClient.Builder().build()