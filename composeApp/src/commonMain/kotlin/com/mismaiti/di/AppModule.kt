package com.mismaiti.di

import com.mismaiti.core.data.auth.AuthRepository
import com.mismaiti.core.di.networkModule
import com.mismaiti.core.di.platformModule
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


