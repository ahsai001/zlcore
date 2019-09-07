package com.zaitunlabs.zlcore.utils

import android.content.Context

import java.util.concurrent.atomic.AtomicInteger

/**
 * Created by ahmad s on 2/24/2016.
 */
object IntegerIDUtils {
    private var atomicInteger: AtomicInteger? = null
    private val ATOMIC_INIT_VALUE_FOR_NOTIF = "atomic_init_value_for_increment_id"
    private val init_value = 0
    private val max_value = 65535

    val id: Int
        get() {
            synchronized(IntegerIDUtils::class.java) {
                return (System.currentTimeMillis() / 1000).toInt()
            }
        }

    fun getID(context: Context): Int {
        synchronized(IntegerIDUtils::class.java) {
            if (atomicInteger == null) {
                val init = Prefs.with(context).getInt(ATOMIC_INIT_VALUE_FOR_NOTIF, init_value)
                atomicInteger = AtomicInteger(init)
            }

            var nextValue = atomicInteger!!.incrementAndGet()

            //use limit value
            if (nextValue > max_value) {
                nextValue = init_value + 1
                atomicInteger!!.set(nextValue)
            }

            Prefs.with(context).save(ATOMIC_INIT_VALUE_FOR_NOTIF, nextValue)
            return nextValue
        }
    }
}