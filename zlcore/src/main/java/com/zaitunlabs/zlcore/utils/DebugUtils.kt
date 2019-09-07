package com.zaitunlabs.zlcore.utils

import android.util.Log


/**
 * Created by ahmad s on 10/9/2015.
 */
class DebugUtils private constructor() {

    private var debuggingLevel = NO_LEVEL

    companion object {
        val VERBOSE_LEVEL = 1
        val DEBUG_LEVEL = 2
        val INFO_LEVEL = 3
        val WARNING_LEVEL = 4
        val ERROR_LEVEL = 5
        val NO_LEVEL = 6

        private var INSTANCE: DebugUtils? = null

        private val instance: DebugUtils
            get() {
                synchronized(DebugUtils::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = DebugUtils()
                    }
                    return INSTANCE
                }
            }

        fun setDebugingLevel(debuggingLevel: Int) {
            instance.debuggingLevel = debuggingLevel
        }

        fun logV(tag: String, message: String) {
            if (instance.debuggingLevel <= VERBOSE_LEVEL) {
                Log.v(tag, message)
            }
        }

        fun logD(tag: String, message: String) {
            if (instance.debuggingLevel <= DEBUG_LEVEL) {
                Log.d(tag, message)
            }
        }

        fun logI(tag: String, message: String) {
            if (instance.debuggingLevel <= INFO_LEVEL) {
                Log.i(tag, message)
            }
        }

        fun logW(tag: String, message: String) {
            if (instance.debuggingLevel <= WARNING_LEVEL) {
                Log.w(tag, message)
            }
        }

        fun logE(tag: String, message: String) {
            if (instance.debuggingLevel <= ERROR_LEVEL) {
                Log.e(tag, message)
            }
        }
    }
}
