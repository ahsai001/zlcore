package com.zaitunlabs.zlcore.utils;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by ahsai on 6/15/2017.
 */

public class EventsUtils {
    public static void register(Object subscriber){
        EventBus.getDefault().register(subscriber);
    }

    public static void unregister(Object subscriber){
        EventBus.getDefault().unregister(subscriber);
    }
}
