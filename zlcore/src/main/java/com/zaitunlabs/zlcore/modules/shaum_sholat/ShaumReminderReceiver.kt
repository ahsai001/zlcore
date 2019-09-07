package com.zaitunlabs.zlcore.modules.shaum_sholat

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.text.TextUtils

import com.zaitunlabs.zlcore.R
import com.zaitunlabs.zlcore.utils.CommonUtils

/**
 * Created by ahmad s on 3/14/2016.
 */
class ShaumReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        CommonUtils.runCodeInWakeLock(context, reminderReceiverTAG) {
            val shaumDay = CommonUtils.getStringIntent(intent, ShaumSholatReminderService.PARAM_SHAUM_DAY, null)
            if (!TextUtils.isEmpty(shaumDay)) {
                CommonUtils.showNotification(context, context.getString(R.string.app_name),
                        String.format(context.getString(R.string.zlcore_shaum_reminder_notification), shaumDay), null, null, R.string.app_name, R.mipmap.icon, false, false)
            }
        }
    }

    companion object {
        val reminderReceiverTAG = "ShaumReminderReceiver"
    }

}
