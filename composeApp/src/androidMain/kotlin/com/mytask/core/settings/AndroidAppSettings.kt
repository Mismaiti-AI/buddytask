package com.mytask.core.settings

import android.content.Context
import com.mytask.core.data.local.AppSettings
import androidx.core.content.edit

class AndroidAppSettings(context: Context): AppSettings {

    private val sharedPreferences = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)

    override fun getString(key: String, default: String): String {
        return sharedPreferences.getString(key, default) ?: default
    }

    override fun putString(key: String, value: String) {
        sharedPreferences.edit { putString(key, value) }
    }

    override fun getBoolean(key: String, default: Boolean): Boolean {
        return sharedPreferences.getBoolean(key, default)
    }

    override fun putBoolean(key: String, value: Boolean) {
        sharedPreferences.edit { putBoolean(key, value) }
    }

    override fun getInt(key: String, default: Int): Int {
        return sharedPreferences.getInt(key, default)
    }

    override fun putInt(key: String, value: Int) {
        sharedPreferences.edit { putInt(key, value) }
    }

    override fun remove(key: String) {
        sharedPreferences.edit { remove(key) }
    }
}