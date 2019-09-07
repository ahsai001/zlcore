package com.zaitunlabs.zlcore.utils

import java.util.HashMap
import java.util.Stack

import android.app.Activity
import android.content.Context
import android.os.Handler

class ApplicationWacther private constructor() {
    internal var activityStack: Stack<Activity>? = null
    internal var appContext: Context? = null
    @get:Synchronized
    var lastActivityStarted: Activity? = null
        internal set
    @get:Synchronized
    var isApplicationVisible = true
        internal set // initial value is true
    internal var listeners: HashMap<Any, AppWatcherListener>? = null
    private var isConfigurationChanged = false

    @Synchronized
    fun setLastActivityResumed(lastActivityResumed: Activity) {
        this.lastActivityStarted = lastActivityResumed
    }

    init {
        activityStack = Stack()
        listeners = HashMap()
    }

    @Synchronized
    private fun pushActivity(act: Activity): Int {
        activityStack!!.push(act)
        return activityStack!!.size
    }

    @Synchronized
    private fun popActivity(): Activity {
        return activityStack!!.pop() as Activity
    }

    @Synchronized
    private fun activityCounts(): Int {
        return activityStack!!.size
    }

    @Synchronized
    private fun removeActivity(act: Activity): Int {
        activityStack!!.remove(act)
        return activityStack!!.size
    }

    @Synchronized
    fun setConfigurationChanged(changed: Boolean) {
        this.isConfigurationChanged = changed
    }

    @Synchronized
    fun registerActivity(act: Activity): Int {
        synchronized(this) {

        }
        return pushActivity(act)
    }

    @Synchronized
    fun unregisterActivity(act: Activity): Int {
        synchronized(this) {
            if (activityCounts() <= 1 && !isConfigurationChanged) {
                // notify that no activity exist in application now
                val iterator = listeners!!.values.iterator()
                while (iterator.hasNext()) {
                    val listener = iterator.next()
                    listener.noActivityExistInApp()
                }
            }
        }
        return removeActivity(act)
    }

    @Synchronized
    fun reportActivityStopEvent(stopAct: Activity) {
        synchronized(this) {
            if (lastActivityStarted === stopAct) {
                isApplicationVisible = false
                // notify that application invisible now
                val iterator = listeners!!.values
                        .iterator()
                while (iterator.hasNext()) {
                    val listener = iterator.next()
                    listener.appVisible(isApplicationVisible)
                }
            }
        }
    }

    @Synchronized
    fun reportActivityStartEvent(resumeAct: Activity) {
        synchronized(this) {
            if (!isApplicationVisible) {
                isApplicationVisible = true
                // notify that application visible now
                val iterator = listeners!!.values
                        .iterator()
                while (iterator.hasNext()) {
                    val listener = iterator.next()
                    listener.appVisible(isApplicationVisible)
                }
            }
        }
    }

    @Synchronized
    fun registerAppWatcherListener(key: Any,
                                   listener: AppWatcherListener) {
        listeners!![key] = listener
    }


    fun connectivityChanged() {
        Handler().post {
            val isOnline = CommonUtils.isOnline(appContext!!)
            val iterator = listeners!!.values.iterator()
            while (iterator.hasNext()) {
                val listener = iterator.next()
                listener.connectivityChanged(isOnline)
            }
        }
    }

    @Synchronized
    fun unregisterAppWatcherListener(key: Any) {
        listeners!!.remove(key)
    }

    interface AppWatcherListener {
        fun appVisible(visible: Boolean)
        fun noActivityExistInApp()
        fun connectivityChanged(isOnline: Boolean)
    }

    companion object {

        private var instance: ApplicationWacther? = null

        @Synchronized
        fun getInstance(appContext: Context): ApplicationWacther {
            if (instance == null) {
                instance = ApplicationWacther()
                instance!!.appContext = appContext.applicationContext
            }
            return instance
        }

        @Synchronized
        fun initialize(appContext: Context): ApplicationWacther {
            return getInstance(appContext)
        }
    }


    //need to create function to show memory usage and storage
}
