package com.zaitunlabs.zlcore.utils;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.provider.Settings;

import java.net.ContentHandler;

import static android.content.Context.NOTIFICATION_SERVICE;

public class AudioUtil {
    public static void setRingerModeNormal(Context context){
        AudioManager am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
    }
    public static void setRingerModeSilent(Context context){
        AudioManager am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
    }
    public static void setRingerModeVibrate(Context context){
        AudioManager am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
    }

    public static String getRingerModeString(Context context){
        AudioManager am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        int modeConstantValue = am.getRingerMode();
        String ringerModeString = "";
        if(modeConstantValue == AudioManager.RINGER_MODE_SILENT){
            ringerModeString = "Silent";
        } else if(modeConstantValue == AudioManager.RINGER_MODE_VIBRATE){
            ringerModeString = "Vibrate";
        }else if(modeConstantValue == AudioManager.RINGER_MODE_NORMAL){
            ringerModeString = "Normal";
        }
        return ringerModeString;
    }


    //need permission <uses-permission android:name="android.permission.ACCESS_NOTIFICATION_POLICY"/>
    private static void changeInterruptionFiler(Context context, int interruptionFilter){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){ // If api level minimum 23
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            if(mNotificationManager.isNotificationPolicyAccessGranted()){
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
                mNotificationManager.setInterruptionFilter(interruptionFilter);
            }else {
                Intent intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                context.startActivity(intent);
            }
        }
    }

    public static void enableDND(Context context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            changeInterruptionFiler(context, NotificationManager.INTERRUPTION_FILTER_NONE);
        }
    }

    public static void disableDND(Context context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            changeInterruptionFiler(context, NotificationManager.INTERRUPTION_FILTER_ALL);
        }
    }

    public static void enableDNDForAlarmOnly(Context context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            changeInterruptionFiler(context, NotificationManager.INTERRUPTION_FILTER_ALARMS);
        }
    }

    public static void enableDNDForPriorityOnly(Context context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            changeInterruptionFiler(context, NotificationManager.INTERRUPTION_FILTER_PRIORITY);
        }
    }
}
