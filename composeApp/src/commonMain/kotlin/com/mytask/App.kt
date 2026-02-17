package com.mytask

import androidx.compose.runtime.*
import com.mytask.di.moduleList
import com.mytask.presentation.theme.AppTheme
import org.koin.compose.KoinApplication
import org.koin.dsl.KoinAppDeclaration


@Composable
fun App(koinAppDeclaration: KoinAppDeclaration? = null) {
    KoinApplication(application = {
        modules(moduleList())
        koinAppDeclaration?.invoke(this)
    }) {
        AppTheme {
            // TBD: set content here
        }
    }
}
