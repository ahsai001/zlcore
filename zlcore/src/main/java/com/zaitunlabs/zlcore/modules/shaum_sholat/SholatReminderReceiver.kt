package com.zaitunlabs.zlcore.modules.shaum_sholat

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

import com.zaitunlabs.zlcore.R
import com.zaitunlabs.zlcore.utils.CommonUtils
import com.zaitunlabs.zlcore.utils.Prefs

import java.util.Calendar

/**
 * Created by ahmad s on 3/14/2016.
 */
class SholatReminderReceiver : BroadcastReceiver() {
    private val NOTIFICATION_ID_FOR_REMINDER = 100

    override fun onReceive(context: Context, intent: Intent) {
        CommonUtils.runCodeInWakeLock(context, reminderReceiverTAG) {
            val code = CommonUtils.getIntIntent(intent, ShaumSholatReminderService.PARAM_SHOLAT_CODE, -1)
            val prefCode = CommonUtils.getStringIntent(intent, ShaumSholatReminderService.PARAM_PREFS_SHOLAT_CODE, null)

            var contentOfNotif = ""
            var reminderIntervalTime = 5
            when (prefCode) {
                ShaumSholatReminderService.START_SHUBUH_TIME -> {
                    contentOfNotif = context.getString(R.string.zlcore_sholat_reminder_subuh)
                    reminderIntervalTime = ShaumSholatReminderService.PARAM_DEFAULT_TIME_REMINDER
                }
                ShaumSholatReminderService.START_SYURUK_TIME -> {
                    contentOfNotif = context.getString(R.string.zlcore_sholat_reminder_syuruk)
                    reminderIntervalTime = ShaumSholatReminderService.PARAM_DEFAULT_TIME_REMINDER
                }
                ShaumSholatReminderService.START_DZUHUR_TIME -> {
                    contentOfNotif = context.getString(R.string.zlcore_sholat_reminder_dzuhur)
                    reminderIntervalTime = ShaumSholatReminderService.PARAM_DEFAULT_TIME_REMINDER
                }
                ShaumSholatReminderService.START_JUMAT_TIME -> {
                    contentOfNotif = context.getString(R.string.zlcore_sholat_reminder_jumat)
                    reminderIntervalTime = ShaumSholatReminderService.PARAM_JUMAT_TIME_REMINDER
                }
                ShaumSholatReminderService.START_ASHR_TIME -> {
                    contentOfNotif = context.getString(R.string.zlcore_sholat_reminder_ashr)
                    reminderIntervalTime = ShaumSholatReminderService.PARAM_DEFAULT_TIME_REMINDER
                }
                ShaumSholatReminderService.START_MAGHRIB_TIME -> {
                    contentOfNotif = context.getString(R.string.zlcore_sholat_reminder_maghrib)
                    reminderIntervalTime = ShaumSholatReminderService.PARAM_DEFAULT_TIME_REMINDER
                }
                ShaumSholatReminderService.START_ISYA_TIME -> {
                    contentOfNotif = context.getString(R.string.zlcore_sholat_reminder_isya)
                    reminderIntervalTime = ShaumSholatReminderService.PARAM_DEFAULT_TIME_REMINDER
                }
            }

            val time = Prefs.with(context).getLong(prefCode, -1)
            if (time + reminderIntervalTime * 60 * 1000 >= Calendar.getInstance().timeInMillis) {
                CommonUtils.showNotification(context, context.getString(R.string.app_name), contentOfNotif, null, null, R.string.app_name, R.mipmap.icon, false, false)
            }
        }
    }

    companion object {

        val reminderReceiverTAG = "SholatReminderReceiver"
    }

}
