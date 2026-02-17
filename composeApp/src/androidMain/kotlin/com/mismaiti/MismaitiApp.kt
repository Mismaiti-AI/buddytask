package com.mismaiti

import android.app.Application
import co.touchlab.kermit.Logger

class MismaitiApp : Application() {
    override fun onCreate() {
        super.onCreate()

        Logger.withTag("MismaitiApp").d("onCreate")
    }
}