package com.zaitunlabs.zlcore.utils;

import android.util.Log;


/**
 * Created by ahmad s on 10/9/2015.
 */
public class DebugUtil {
    public static final int VERBOSE_LEVEL = 1;
    public static final int DEBUG_LEVEL = 2;
    public static final int INFO_LEVEL = 3;
    public static final int WARNING_LEVEL = 4;
    public static final int ERROR_LEVEL = 5;
    public static final int NO_LEVEL = 6;

    private int debuggingLevel = NO_LEVEL;

    private static DebugUtil INSTANCE;

    private DebugUtil(){
    }

    private static DebugUtil getInstance(){
        synchronized (DebugUtil.class){
            if(INSTANCE == null) {
                INSTANCE = new DebugUtil();
            }
            return INSTANCE;
        }
    }

    public static void setDebugingLevel(int debuggingLevel){
        getInstance().debuggingLevel = debuggingLevel;
    }

    public static void logV(String tag, String message){
        if(getInstance().debuggingLevel <= VERBOSE_LEVEL) {
            if(message == null)return;
            Log.v(tag, message);
        }
    }
    public static void logD(String tag, String message){
        if(getInstance().debuggingLevel <= DEBUG_LEVEL) {
            if(message == null)return;
            Log.d(tag, message);
        }
    }
    public static void logI(String tag, String message){
        if(getInstance().debuggingLevel <= INFO_LEVEL) {
            if(message == null)return;
            Log.i(tag, message);
        }
    }
    public static void logW(String tag, String message){
        if(getInstance().debuggingLevel <= WARNING_LEVEL) {
            if(message == null)return;
            Log.w(tag, message);
        }
    }
    public static void logE(String tag, String message){
        if(getInstance().debuggingLevel <= ERROR_LEVEL) {
            if(message == null)return;
            Log.e(tag, message);
        }
    }
}
