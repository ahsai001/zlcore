package com.zaitunlabs.zlcore.utils

import android.os.Handler

import java.util.concurrent.atomic.AtomicInteger

/**
 * Created by ahmad s on 2019-07-04.
 */
class MultipleClickHandler(private val delayTime: Int, private val multipleClickListener: MultipleClickListener) {
    private var mCounter = AtomicInteger()
    private val handler = Handler()
    private val mRunnable = Runnable { mCounter = AtomicInteger() }

    fun handle() {
        handler.removeCallbacks(mRunnable)
        handler.postDelayed(mRunnable, delayTime.toLong())
        multipleClickListener.onHandle(mCounter.incrementAndGet())
    }

    interface MultipleClickListener {
        fun onHandle(totalClick: Int)
    }


}
