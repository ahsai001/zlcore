package com.zaitunlabs.zlcore.utils

import android.app.NotificationManager
import android.content.Context

import androidx.core.app.NotificationCompat

/**
 * Created by ahsai on 2/14/2018.
 */

class NotificationProgressUtils(context: Context, private val title: String, private val desc: String, private val icon: Int, private val notifID: Int) {
    private val mNotifyManager: NotificationManager
    private val mBuilder: NotificationCompat.Builder

    init {
        mNotifyManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mBuilder = NotificationCompat.Builder(context, CommonUtils.getPackageName(context))
        mBuilder.setContentTitle(title)
                .setContentText(desc)
                .setSmallIcon(icon)
    }

    fun setProgress(max: Int, progress: Int) {
        mBuilder.setOngoing(true)
        mBuilder.setProgress(max, progress, false)
        mBuilder.setContentText("$desc $progress %")
        mNotifyManager.notify(notifID, mBuilder.build())
    }

    fun setIndeterminateProgress() {
        mBuilder.setOngoing(true)
        mBuilder.setProgress(0, 0, true)
        mBuilder.setContentText(desc)
        mNotifyManager.notify(notifID, mBuilder.build())
    }

    fun setComplete(completeText: String) {
        mBuilder.setOngoing(false)
        mBuilder.setContentText(completeText)
        mBuilder.setProgress(0, 0, false)
        mNotifyManager.notify(notifID, mBuilder.build())
    }
}