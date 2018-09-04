package com.zaitunlabs.zlcore.utils;

import android.content.Context;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by ahmad s on 2/24/2016.
 */
public class AtomicIntegerUtils {
    private static final int init_value = 0;


    //pushy.me notif
    private static AtomicInteger pushyMeAtomicInteger = null;
    private static final Object pushyMeLock = new Object();
    private static final String ATOMIC_INIT_VALUE_FOR_PUSHYME_NOTIF = "atomic_init_value_for_pushyme_notif";


    public static int getPushyMeNotifID(Context context) {
        synchronized (pushyMeLock){
            if(pushyMeAtomicInteger == null){
                int init = Prefs.with(context).getInt(ATOMIC_INIT_VALUE_FOR_PUSHYME_NOTIF, init_value);
                pushyMeAtomicInteger = new AtomicInteger(init);
            }
            int nextValue = pushyMeAtomicInteger.incrementAndGet();
            Prefs.with(context).save(ATOMIC_INIT_VALUE_FOR_PUSHYME_NOTIF,nextValue);
            return nextValue;
        }
    }
}