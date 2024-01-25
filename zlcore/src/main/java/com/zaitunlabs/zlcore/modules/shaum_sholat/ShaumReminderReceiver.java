package com.zaitunlabs.zlcore.modules.shaum_sholat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.zaitunlabs.zlcore.R;
import com.zaitunlabs.zlcore.utils.CommonUtil;

/**
 * Created by ahmad s on 3/14/2016.
 */
public class ShaumReminderReceiver extends BroadcastReceiver {
    final public static String reminderReceiverTAG = "ShaumReminderReceiver";

    @Override
    public void onReceive(final Context context, final Intent intent) {
       CommonUtil.runCodeInWakeLock(context, reminderReceiverTAG, new Runnable() {
           @Override
           public void run() {
               String shaumDay = CommonUtil.getStringIntent(intent, ShaumSholatReminderService.PARAM_SHAUM_DAY, null);
               if(!TextUtils.isEmpty(shaumDay)) {
                   CommonUtil.showNotification(context, context.getString(R.string.app_name), null, null,
                           String.format(context.getString(R.string.zlcore_shaum_reminder_notification),shaumDay), null, null, R.string.app_name, R.mipmap.icon, false, false);
               }
           }
       });
    }

}
