package com.zaitunlabs.zlcore.utils

import android.content.Context
import android.text.TextUtils

/**
 * Created by ahmad s on 3/2/2016.
 */
object Hawk {
    private var appContext: Context? = null
    fun remove(field: String) {
        Prefs.with(appContext).remove(field)
    }

    operator fun get(key: String, defaultValue: String): String? {
        return Prefs.with(appContext).getString(key, defaultValue)
    }

    operator fun get(key: String, defaultValue: Int): Int {
        return Prefs.with(appContext).getInt(key, defaultValue)
    }

    operator fun get(key: String, defaultValue: Boolean): Boolean {
        return Prefs.with(appContext).getBoolean(key, defaultValue)
    }

    fun put(key: String, value: String) {
        if (TextUtils.isEmpty(key)) return
        Prefs.with(appContext).save(key, value)
    }

    fun put(key: String, value: Long) {
        if (TextUtils.isEmpty(key)) return
        Prefs.with(appContext).save(key, value)
    }

    operator fun get(key: String, defaultValue: Long): Long {
        return Prefs.with(appContext).getLong(key, defaultValue)
    }

    fun put(key: String, value: Int) {
        if (TextUtils.isEmpty(key)) return
        Prefs.with(appContext).save(key, value)
    }

    fun put(key: String, value: Boolean) {
        if (TextUtils.isEmpty(key)) return
        Prefs.with(appContext).save(key, value)
    }

    fun init(context: Context) {
        appContext = context.applicationContext
    }
}