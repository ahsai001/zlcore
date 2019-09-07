package com.zaitunlabs.zlcore.utils


import android.os.Handler

import androidx.viewpager.widget.ViewPager


abstract class ViewPageSliderUtil {
    private val handler: Handler
    private val runnable: Runnable
    private var delay = 2000
    private var page = 0
    private var isRunning = false

    abstract val viewPager: ViewPager

    init {
        handler = Handler()
        runnable = object : Runnable {
            override fun run() {
                if (viewPager.adapter!!.count == page) {
                    page = 0
                } else {
                    page++
                }
                viewPager.setCurrentItem(page, true)
                handler.postDelayed(this, delay.toLong())
            }
        }
    }

    fun setDelay(delay: Int): ViewPageSliderUtil {
        this.delay = delay
        return this
    }

    @Synchronized
    fun start() {
        if (!isRunning) {
            isRunning = true
            handler.postDelayed(runnable, delay.toLong())
        }
    }


    @Synchronized
    fun stop() {
        isRunning = false
        handler.removeCallbacks(runnable)
    }
}
