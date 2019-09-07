package com.zaitunlabs.zlcore.utils

import org.greenrobot.eventbus.EventBus

/**
 * Created by ahsai on 6/15/2017.
 */

object EventsUtils {
    fun register(subscriber: Any) {
        EventBus.getDefault().register(subscriber)
    }

    fun unregister(subscriber: Any) {
        EventBus.getDefault().unregister(subscriber)
    }
}
