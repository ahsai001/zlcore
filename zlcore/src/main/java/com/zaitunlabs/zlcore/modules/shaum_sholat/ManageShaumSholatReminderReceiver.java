package com.zaitunlabs.zlcore.modules.shaum_sholat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.zaitunlabs.zlcore.constants.ZLCoreConstanta;

import androidx.core.app.JobIntentService;


/**
 * Created by ahmad s on 3/14/2016.
 */

public class ManageShaumSholatReminderReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction() != null && (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED) ||
                intent.getAction().startsWith(ZLCoreConstanta.ACTION_MANAGE_SHAUM_SHOLAT_REMINDER))) {
            JobIntentService.enqueueWork(context, ShaumSholatReminderService.class, ShaumSholatReminderService.JOB_ID, new Intent());
        }
    }

    public static void start(Context context){
        Intent setShaumSholatReminderIntent = new Intent(context, ManageShaumSholatReminderReceiver.class);
        setShaumSholatReminderIntent.setAction(ZLCoreConstanta.ACTION_MANAGE_SHAUM_SHOLAT_REMINDER);
        context.sendBroadcast(setShaumSholatReminderIntent);
    }
}
