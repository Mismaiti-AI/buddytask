package com.mytask

import android.app.Application
import co.touchlab.kermit.Logger

class BuddyTaskApp : Application() {
    override fun onCreate() {
        super.onCreate()

        Logger.withTag("BuddyTaskApp").d("onCreate")
    }
}