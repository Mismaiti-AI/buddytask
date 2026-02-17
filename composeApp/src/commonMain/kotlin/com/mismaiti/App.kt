package com.mismaiti

import androidx.compose.runtime.*
import com.mismaiti.di.moduleList
import com.mismaiti.presentation.theme.AppTheme
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
