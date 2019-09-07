package com.zaitunlabs.zlcore.constants

import android.content.Context
import android.text.TextUtils

import com.zaitunlabs.zlcore.R
import com.zaitunlabs.zlcore.utils.CommonUtils

/**
 * Created by ahsai on 3/15/2018.
 */

object ZLCoreConstanta {
    val ACTION_MANAGE_SHAUM_SHOLAT_REMINDER = "com.zaitunlabs.zlcore.action.MANAGE_SHAUM_SHOLAT_REMINDER"
    var DATABASE_NAME: String? = null
    var databaseVersion = 1
    var CRASH_MAIL_TO: String? = null

    fun setDatabaseName(databaseName: String) {
        DATABASE_NAME = databaseName
    }

    fun getDatabaseName(context: Context): String {
        return (if (TextUtils.isEmpty(ZLCoreConstanta.DATABASE_NAME)) CommonUtils.getPackageName(context) else ZLCoreConstanta.DATABASE_NAME) + ".db"
    }

    fun setCrashMailTo(crashMailTo: String) {
        CRASH_MAIL_TO = crashMailTo
    }

    fun getCrashMailTo(context: Context): String {
        return if (CRASH_MAIL_TO == null) context.getString(R.string.zlcore_crash_mail_to) else CRASH_MAIL_TO
    }
}
