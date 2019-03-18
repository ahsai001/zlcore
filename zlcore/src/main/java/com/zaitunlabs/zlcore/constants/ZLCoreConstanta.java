package com.zaitunlabs.zlcore.constants;

import android.content.Context;
import android.text.TextUtils;

import com.zaitunlabs.zlcore.R;
import com.zaitunlabs.zlcore.utils.CommonUtils;

/**
 * Created by ahsai on 3/15/2018.
 */

public class ZLCoreConstanta {
    public static final String ACTION_MANAGE_SHAUM_SHOLAT_REMINDER = "com.zaitunlabs.zlcore.action.MANAGGE_SHAUM_SHOLAT_REMINDER";
    public static String DATABASE_NAME = null;
    public static int DATABASE_VERSION = 1;
    public static String CRASH_MAIL_TO = null;

    public static void setDatabaseName(String databaseName) {
        DATABASE_NAME = databaseName;
    }

    public static void setDatabaseVersion(int databaseVersion) {
        DATABASE_VERSION = databaseVersion;
    }

    public static int getDatabaseVersion() {
        return DATABASE_VERSION;
    }

    public static String getDatabaseName(Context context) {
        return (TextUtils.isEmpty(ZLCoreConstanta.DATABASE_NAME)? CommonUtils.getPackageName(context):ZLCoreConstanta.DATABASE_NAME)+".db";
    }

    public static void setCrashMailTo(String crashMailTo) {
        CRASH_MAIL_TO = crashMailTo;
    }

    public static String getCrashMailTo(Context context) {
        return (CRASH_MAIL_TO==null?context.getString(R.string.zlcore_crash_mail_to):CRASH_MAIL_TO);
    }
}
