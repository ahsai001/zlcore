package com.zaitunlabs.zlcore.utils;

import android.content.Context;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by ahmad s on 2/24/2016.
 */
public class IntegerIDUtil {
    private static AtomicInteger atomicInteger = null;
    private static final String ATOMIC_INIT_VALUE_FOR_NOTIF = "atomic_init_value_for_increment_id";
    private static final int init_value = 0;
    private static final int max_value = 65535;
    public static int getID(Context context) {
        synchronized (IntegerIDUtil.class){
            if(atomicInteger == null){
                int init = Prefs.with(context).getInt(ATOMIC_INIT_VALUE_FOR_NOTIF, init_value);
                atomicInteger = new AtomicInteger(init);
            }

            int nextValue = atomicInteger.incrementAndGet();

            //use limit value
            if(nextValue > max_value){
                nextValue = init_value+1;
                atomicInteger.set(nextValue);
            }

            Prefs.with(context).save(ATOMIC_INIT_VALUE_FOR_NOTIF,nextValue);
            return nextValue;
        }
    }

    public static int getID() {
        synchronized (IntegerIDUtil.class){
            return (int) (System.currentTimeMillis()/1000);
        }
    }
}