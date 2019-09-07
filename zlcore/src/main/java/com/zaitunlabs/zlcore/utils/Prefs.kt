package com.zaitunlabs.zlcore.utils

/*
 * Copyright (C) 2014 Alexrs95
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import android.content.Context

/**
 * @author Alejandro Rodriguez <https:></https:>//github.com/Alexrs95/Prefs>
 *
 *
 * Wrapper over the Android Preferences which provides a fluid syntax
 */
class Prefs internal constructor(context: Context) {

    val all: Map<String, *>
        get() = preferences.all

    init {
        //preferences = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        preferences = SPrefs(context)
        editor = preferences.edit()
    }

    fun save(key: String, value: Boolean) {
        editor.putBoolean(key, value).apply()
    }

    fun save(key: String, value: String) {
        editor.putString(key, value).apply()
    }

    fun save(key: String, value: Int) {
        editor.putInt(key, value).apply()
    }

    fun save(key: String, value: Float) {
        editor.putFloat(key, value).apply()
    }

    fun save(key: String, value: Long) {
        editor.putLong(key, value).apply()
    }

    fun save(key: String, value: Set<String>) {
        //editor.putStringSet(key, value).apply();
    }

    fun getBoolean(key: String, defValue: Boolean): Boolean {
        return preferences.getBoolean(key, defValue)
    }

    fun getString(key: String, defValue: String): String? {
        return preferences.getString(key, defValue)
    }

    fun getInt(key: String, defValue: Int): Int {
        return preferences.getInt(key, defValue)
    }

    fun getFloat(key: String, defValue: Float): Float {
        return preferences.getFloat(key, defValue)
    }

    fun getLong(key: String, defValue: Long): Long {
        return preferences.getLong(key, defValue)
    }

    fun getStringSet(key: String, defValue: Set<String>): Set<String>? {
        //return preferences.getStringSet(key, defValue);
        return null
    }

    fun remove(key: String) {
        editor.remove(key).apply()
    }

    private class Builder(context: Context?) {
        private val context: Context

        init {
            if (context == null) {
                throw IllegalArgumentException("Context must not be null.")
            }
            this.context = context.applicationContext
        }

        /**
         * Method that creates an instance of Prefs
         *
         * @return an instance of Prefs
         */
        fun build(): Prefs {
            return Prefs(context)
        }
    }

    companion object {
        private val TAG = "Prefs"
        internal var singleton: Prefs? = null
        //static SharedPreferences preferences;
        //static SharedPreferences.Editor editor;

        internal var preferences: SPrefs
        internal var editor: SPrefs.Editor

        fun with(context: Context): Prefs {
            if (singleton == null) {
                singleton = Builder(context).build()
            }
            return singleton
        }
    }
}