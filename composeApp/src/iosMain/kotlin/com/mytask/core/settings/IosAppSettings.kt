package com.mytask.core.settings

import com.mytask.core.data.local.AppSettings
import platform.Foundation.NSUserDefaults

class IosAppSettings(private val defaults: NSUserDefaults) : AppSettings {
    override fun getString(key: String, default: String): String =
        defaults.stringForKey(key) ?: default

    override fun putString(key: String, value: String) {
        defaults.setObject(value, key)
    }

    override fun getBoolean(key: String, default: Boolean): Boolean =
        defaults.boolForKey(key)

    override fun putBoolean(key: String, value: Boolean) {
        defaults.setBool(value, key)
    }

    override fun getInt(key: String, default: Int): Int =
        defaults.integerForKey(key).toInt()

    override fun putInt(key: String, value: Int) {
        defaults.setInteger(value.toLong(), key)
    }

    override fun remove(key: String) {
        defaults.removeObjectForKey(key)
    }
}