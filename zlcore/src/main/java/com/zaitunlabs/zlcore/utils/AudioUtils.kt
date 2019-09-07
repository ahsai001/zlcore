package com.zaitunlabs.zlcore.utils

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Build
import android.provider.Settings

import java.net.ContentHandler

import android.content.Context.NOTIFICATION_SERVICE

object AudioUtils {
    fun setRingerModeNormal(context: Context) {
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        am.ringerMode = AudioManager.RINGER_MODE_NORMAL
    }

    fun setRingerModeSilent(context: Context) {
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        am.ringerMode = AudioManager.RINGER_MODE_SILENT
    }

    fun setRingerModeVibrate(context: Context) {
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        am.ringerMode = AudioManager.RINGER_MODE_VIBRATE
    }

    fun getRingerModeString(context: Context): String {
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val modeConstantValue = am.ringerMode
        var ringerModeString = ""
        if (modeConstantValue == AudioManager.RINGER_MODE_SILENT) {
            ringerModeString = "Silent"
        } else if (modeConstantValue == AudioManager.RINGER_MODE_VIBRATE) {
            ringerModeString = "Vibrate"
        } else if (modeConstantValue == AudioManager.RINGER_MODE_NORMAL) {
            ringerModeString = "Normal"
        }
        return ringerModeString
    }


    //need permission <uses-permission android:name="android.permission.ACCESS_NOTIFICATION_POLICY"/>
    private fun changeInterruptionFiler(context: Context, interruptionFilter: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // If api level minimum 23
            val mNotificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            if (mNotificationManager.isNotificationPolicyAccessGranted) {
                /*
                    void setInterruptionFilter (int interruptionFilter)
                        Sets the current notification interruption filter.

                        The interruption filter defines which notifications are allowed to interrupt
                        the user (e.g. via sound & vibration) and is applied globally.

                        Only available if policy access is granted to this package.

                    Parameters
                        interruptionFilter : int
                        Value is INTERRUPTION_FILTER_NONE, INTERRUPTION_FILTER_PRIORITY,
                        INTERRUPTION_FILTER_ALARMS, INTERRUPTION_FILTER_ALL
                        or INTERRUPTION_FILTER_UNKNOWN.
                */

                // Set the interruption filter
                mNotificationManager.setInterruptionFilter(interruptionFilter)
            } else {
                val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
                context.startActivity(intent)
            }
        }
    }

    fun enableDND(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            changeInterruptionFiler(context, NotificationManager.INTERRUPTION_FILTER_NONE)
        }
    }

    fun disableDND(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            changeInterruptionFiler(context, NotificationManager.INTERRUPTION_FILTER_ALL)
        }
    }

    fun enableDNDForAlarmOnly(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            changeInterruptionFiler(context, NotificationManager.INTERRUPTION_FILTER_ALARMS)
        }
    }

    fun enableDNDForPriorityOnly(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            changeInterruptionFiler(context, NotificationManager.INTERRUPTION_FILTER_PRIORITY)
        }
    }
}
