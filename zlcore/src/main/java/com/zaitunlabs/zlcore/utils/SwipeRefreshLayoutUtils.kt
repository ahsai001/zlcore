package com.zaitunlabs.zlcore.utils

import androidx.annotation.ColorRes
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

import java.lang.ref.WeakReference

/**
 * Created by ahsai on 7/18/2018.
 */

class SwipeRefreshLayoutUtils {
    private var swipeRefreshLayoutRef: WeakReference<SwipeRefreshLayout>? = null
    private var refreshAction: Runnable? = null

    private fun doRefresh(refreshAction: Runnable) {
        refreshAction.run()
        //new Handler(Looper.getMainLooper()).post(refreshAction);
    }

    fun setColorSchemeResources(@ColorRes vararg colorResIds: Int) {
        if (swipeRefreshLayoutRef != null) {
            val swipeRefreshLayout = swipeRefreshLayoutRef!!.get()
            swipeRefreshLayout?.setColorSchemeResources(*colorResIds)
        }
    }

    fun setColorSchemeColors(vararg colors: Int) {
        if (swipeRefreshLayoutRef != null) {
            val swipeRefreshLayout = swipeRefreshLayoutRef!!.get()
            swipeRefreshLayout?.setColorSchemeColors(*colors)
        }
    }

    fun setEnabled(enabled: Boolean) {
        if (swipeRefreshLayoutRef != null) {
            val swipeRefreshLayout = swipeRefreshLayoutRef!!.get()
            if (swipeRefreshLayout != null) {
                swipeRefreshLayout.isEnabled = enabled
            }
        }
    }

    fun setProgressBackgroundColorSchemeColor(color: Int) {
        if (swipeRefreshLayoutRef != null) {
            val swipeRefreshLayout = swipeRefreshLayoutRef!!.get()
            swipeRefreshLayout?.setProgressBackgroundColorSchemeColor(color)
        }
    }

    fun setProgressBackgroundColorSchemeResource(colorRes: Int) {
        if (swipeRefreshLayoutRef != null) {
            val swipeRefreshLayout = swipeRefreshLayoutRef!!.get()
            swipeRefreshLayout?.setProgressBackgroundColorSchemeResource(colorRes)
        }
    }

    fun refreshNow(): Boolean {
        if (swipeRefreshLayoutRef != null && refreshAction != null) {
            val swipeRefreshLayout = swipeRefreshLayoutRef!!.get()
            if (swipeRefreshLayout != null) {
                swipeRefreshLayout.isRefreshing = true
                doRefresh(refreshAction!!)
                return true
            }
        }
        return false
    }

    fun refreshDone() {
        if (swipeRefreshLayoutRef != null) {
            val swipeRefreshLayout = swipeRefreshLayoutRef!!.get()
            if (swipeRefreshLayout != null) {
                swipeRefreshLayout.isRefreshing = false
            }
        }
    }

    companion object {
        fun init(swipeRefreshLayout: SwipeRefreshLayout?, refreshAction: Runnable): SwipeRefreshLayoutUtils {
            val swipeRefreshLayoutUtils = SwipeRefreshLayoutUtils()
            if (swipeRefreshLayout != null) {
                swipeRefreshLayoutUtils.swipeRefreshLayoutRef = WeakReference(swipeRefreshLayout)
                swipeRefreshLayout.setOnRefreshListener {
                    if (swipeRefreshLayoutUtils.refreshAction != null) {
                        swipeRefreshLayoutUtils.doRefresh(swipeRefreshLayoutUtils.refreshAction!!)
                    }
                }
                swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                        android.R.color.holo_green_light,
                        android.R.color.holo_orange_light,
                        android.R.color.holo_red_light)

            }
            swipeRefreshLayoutUtils.refreshAction = refreshAction
            return swipeRefreshLayoutUtils
        }
    }
}
