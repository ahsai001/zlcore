package com.zaitunlabs.zlcore.modules.shaum_sholat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.zaitunlabs.zlcore.R;
import com.zaitunlabs.zlcore.utils.CommonUtils;

/**
 * Created by ahmad s on 3/14/2016.
 */
public class ShaumReminderReceiver extends BroadcastReceiver {
    private final int NOTIFICATION_ID_FOR_REMINDER = 100;

    final public static String reminderReceiverTAG = "ShaumReminderReceiver";

    @Override
    public void onReceive(final Context context, final Intent intent) {
       CommonUtils.runCodeInWakeLock(context, reminderReceiverTAG, new Runnable() {
           @Override
           public void run() {
               String shaumDay = CommonUtils.getStringIntent(intent, ShaumSholatReminderService.PARAM_SHAUM_DAY, null);
               if(!TextUtils.isEmpty(shaumDay)) {
                   CommonUtils.showNotification(context, context.getString(R.string.app_name),
                           "Besok adalah hari "+shaumDay+", jangan lupa persiapannya bagi yang akan melakukan shaum/puasa", null, null, R.string.app_name, R.mipmap.icon, false, false);
               }
           }
       });
    }

}
