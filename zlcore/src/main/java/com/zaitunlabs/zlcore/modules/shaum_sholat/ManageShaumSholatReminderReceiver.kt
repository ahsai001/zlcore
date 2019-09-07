package com.zaitunlabs.zlcore.modules.shaum_sholat

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

import com.zaitunlabs.zlcore.constants.ZLCoreConstanta

import androidx.core.app.JobIntentService


/**
 * Created by ahmad s on 3/14/2016.
 */

class ManageShaumSholatReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != null && (intent.action == Intent.ACTION_BOOT_COMPLETED ||
                        intent.action == Intent.ACTION_LOCKED_BOOT_COMPLETED ||
                        intent.action == Intent.ACTION_REBOOT ||
                        intent.action == "android.intent.action.QUICKBOOT_POWERON" ||
                        intent.action!!.startsWith(ZLCoreConstanta.ACTION_MANAGE_SHAUM_SHOLAT_REMINDER))) {
            JobIntentService.enqueueWork(context, ShaumSholatReminderService::class.java!!, ShaumSholatReminderService.JOB_ID, Intent())
        }
    }

    companion object {

        fun start(context: Context) {
            val setShaumSholatReminderIntent = Intent(context, ManageShaumSholatReminderReceiver::class.java)
            setShaumSholatReminderIntent.action = ZLCoreConstanta.ACTION_MANAGE_SHAUM_SHOLAT_REMINDER
            context.sendBroadcast(setShaumSholatReminderIntent)
        }
    }
}
