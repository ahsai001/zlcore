package com.zaitunlabs.zlcore.utils

import android.view.View

import java.lang.ref.WeakReference

/**
 * Created by ahsai on 7/28/2018.
 */

class ViewEnablerUtils(targetView: View, targetReportTotal: Int) : ReachLostUtils() {
    private val targetViewRef: WeakReference<View>?

    init {
        this.targetViewRef = WeakReference(targetView)
        setTargetedDoneTotal(targetReportTotal)
        setActionWhenReachTarget {
            if (this@ViewEnablerUtils.targetViewRef != null) {
                val targetView = this@ViewEnablerUtils.targetViewRef.get()
                if (targetView != null) {
                    targetView.isEnabled = true
                }
            }
        }
        setActionWhenLostTarget {
            if (this@ViewEnablerUtils.targetViewRef != null) {
                val targetView = this@ViewEnablerUtils.targetViewRef.get()
                if (targetView != null) {
                    targetView.isEnabled = false
                }
            }
        }
    }

    override fun init() {
        if (targetedDoneTotal > 0) {
            if (this@ViewEnablerUtils.targetViewRef != null) {
                val targetView = this@ViewEnablerUtils.targetViewRef.get()
                if (targetView != null) {
                    targetView.isEnabled = false
                }
            }
        }
    }
}
