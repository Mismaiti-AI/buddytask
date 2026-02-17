package com.mytask.di

import com.mytask.core.data.auth.AuthRepository
import com.mytask.core.di.networkModule
import com.mytask.core.di.platformModule
import org.koin.core.module.Module
import org.koin.dsl.module

fun moduleList(): List<Module> = listOf(
    platformModule(),
    networkModule(),
    appModule()
)

fun appModule() = module {
    single { AuthRepository(database = get(), backendHandler = getOrNull()) }
}


