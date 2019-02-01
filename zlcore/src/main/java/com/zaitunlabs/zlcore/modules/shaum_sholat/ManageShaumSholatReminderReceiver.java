package com.zaitunlabs.zlcore.modules.shaum_sholat;

import android.content.Context;
import android.content.Intent;
import androidx.legacy.content.WakefulBroadcastReceiver;


/**
 * Created by ahmad s on 3/14/2016.
 */

public class ManageShaumSholatReminderReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        startWakefulService(context, new Intent(context, ShaumSholatReminderService.class));
    }

    public static void start(Context context){
        Intent setShaumSholatReminderIntent = new Intent(context, ManageShaumSholatReminderReceiver.class);
        context.sendBroadcast(setShaumSholatReminderIntent);
    }
}
