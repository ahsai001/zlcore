package com.zaitunlabs.zlcore.modules.shaum_sholat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.zaitunlabs.zlcore.R;
import com.zaitunlabs.zlcore.utils.CommonUtils;
import com.zaitunlabs.zlcore.utils.Prefs;

import java.util.Calendar;

/**
 * Created by ahmad s on 3/14/2016.
 */
public class SholatReminderReceiver extends BroadcastReceiver {
    private final int NOTIFICATION_ID_FOR_REMINDER = 100;

    final public static String reminderReceiverTAG = "SholatReminderReceiver";

    @Override
    public void onReceive(final Context context, final Intent intent) {
       CommonUtils.runCodeInWakeLock(context, reminderReceiverTAG, new Runnable() {
           @Override
           public void run() {
               int code = CommonUtils.getIntIntent(intent, ShaumSholatReminderService.PARAM_SHOLAT_CODE, -1);
               String prefCode = CommonUtils.getStringIntent(intent, ShaumSholatReminderService.PARAM_PREFS_SHOLAT_CODE, null);

               String contentOfNotif = "";
               int reminderIntervalTime = 5;
               switch (prefCode){
                   case ShaumSholatReminderService.START_SHUBUH_TIME:
                       contentOfNotif = context.getString(R.string.sholat_reminder_subuh);
                       reminderIntervalTime = ShaumSholatReminderService.PARAM_DEFAULT_TIME_REMINDER;
                       break;
                   case ShaumSholatReminderService.START_SYURUK_TIME:
                       contentOfNotif = context.getString(R.string.sholat_reminder_syuruk);
                       reminderIntervalTime = ShaumSholatReminderService.PARAM_DEFAULT_TIME_REMINDER;
                       break;
                   case ShaumSholatReminderService.START_DZUHUR_TIME:
                       contentOfNotif = context.getString(R.string.sholat_reminder_dzuhur);
                       reminderIntervalTime = ShaumSholatReminderService.PARAM_DEFAULT_TIME_REMINDER;
                       break;
                   case ShaumSholatReminderService.START_JUMAT_TIME:
                       contentOfNotif = context.getString(R.string.sholat_reminder_jumat);
                       reminderIntervalTime = ShaumSholatReminderService.PARAM_JUMAT_TIME_REMINDER;
                       break;
                   case ShaumSholatReminderService.START_ASHR_TIME:
                       contentOfNotif = context.getString(R.string.sholat_reminder_ashr);
                       reminderIntervalTime = ShaumSholatReminderService.PARAM_DEFAULT_TIME_REMINDER;
                       break;
                   case ShaumSholatReminderService.START_MAGHRIB_TIME:
                       contentOfNotif = context.getString(R.string.sholat_reminder_maghrib);
                       reminderIntervalTime = ShaumSholatReminderService.PARAM_DEFAULT_TIME_REMINDER;
                       break;
                   case ShaumSholatReminderService.START_ISYA_TIME:
                       contentOfNotif = context.getString(R.string.sholat_reminder_isya);
                       reminderIntervalTime = ShaumSholatReminderService.PARAM_DEFAULT_TIME_REMINDER;
                       break;
               }

               long time = Prefs.with(context).getLong(prefCode,-1);
               if(time+(reminderIntervalTime*60*1000) >= Calendar.getInstance().getTimeInMillis()) {
                   CommonUtils.showNotification(context, context.getString(R.string.app_name), contentOfNotif, null, null, R.string.app_name, R.mipmap.icon, false, false);
               }
           }
       });
    }

}
