package com.zaitunlabs.zlcore.utils;

import android.content.Context;
import android.text.TextUtils;

/**
 * Created by ahmad s on 3/2/2016.
 */
public class Hawk{
    private static Context appContext;
    public static void remove(String field){
        Prefs.with(appContext).remove(field);
    }

    public static String get(String key, String defaultValue){
        return Prefs.with(appContext).getString(key, defaultValue);
    }
    public static int get(String key, int defaultValue){
        return Prefs.with(appContext).getInt(key, defaultValue);
    }
    public static boolean get(String key, boolean defaultValue){
        return Prefs.with(appContext).getBoolean(key, defaultValue);
    }

    public static void put(String key, String value){
        if(TextUtils.isEmpty(key))return;
        Prefs.with(appContext).save(key, value);
    }

    public static void put(String key, long value){
        if(TextUtils.isEmpty(key))return;
        Prefs.with(appContext).save(key, value);
    }

    public static long get(String key, long defaultValue){
        return Prefs.with(appContext).getLong(key, defaultValue);
    }

    public static void put(String key, int value){
        if(TextUtils.isEmpty(key))return;
        Prefs.with(appContext).save(key, value);
    }

    public static void put(String key, boolean value){
        if(TextUtils.isEmpty(key))return;
        Prefs.with(appContext).save(key, value);
    }

    public static void init(Context context){
        appContext = context.getApplicationContext();
    }
}