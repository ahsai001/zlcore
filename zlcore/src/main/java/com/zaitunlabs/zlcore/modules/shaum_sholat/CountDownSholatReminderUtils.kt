package com.zaitunlabs.zlcore.modules.shaum_sholat

import android.content.Context
import android.os.CountDownTimer
import android.widget.TextView

import com.zaitunlabs.zlcore.utils.DateStringUtils
import com.zaitunlabs.zlcore.utils.Prefs

import java.lang.ref.WeakReference
import java.util.Calendar

/**
 * Created by ahsai on 2/5/2018.
 */

class CountDownSholatReminderUtils {
    private var countDownTimer: CountDownTimer? = null
    private var sholatType = ""
    private var targetViewRef: WeakReference<TextView>? = null

    fun startCountDown(context: Context, targetView: TextView?) {
        this.targetViewRef = WeakReference<TextView>(targetView)
        val shubuhTime = Prefs.with(context).getLong(ShaumSholatReminderService.START_SHUBUH_TIME, -1) + ShaumSholatReminderService.PARAM_DEFAULT_TIME_REMINDER * 60 * 1000
        val syurukTime = Prefs.with(context).getLong(ShaumSholatReminderService.START_SYURUK_TIME, -1) + ShaumSholatReminderService.PARAM_DEFAULT_TIME_REMINDER * 60 * 1000
        val dzuhurTime = Prefs.with(context).getLong(ShaumSholatReminderService.START_DZUHUR_TIME, -1) + ShaumSholatReminderService.PARAM_DEFAULT_TIME_REMINDER * 60 * 1000
        val asharTime = Prefs.with(context).getLong(ShaumSholatReminderService.START_ASHR_TIME, -1) + ShaumSholatReminderService.PARAM_DEFAULT_TIME_REMINDER * 60 * 1000
        val maghribTime = Prefs.with(context).getLong(ShaumSholatReminderService.START_MAGHRIB_TIME, -1) + ShaumSholatReminderService.PARAM_DEFAULT_TIME_REMINDER * 60 * 1000
        val isyaTime = Prefs.with(context).getLong(ShaumSholatReminderService.START_ISYA_TIME, -1) + ShaumSholatReminderService.PARAM_DEFAULT_TIME_REMINDER * 60 * 1000

        val calendar = Calendar.getInstance()
        var countDownTime: Long = -1
        if (calendar.timeInMillis < shubuhTime) {
            countDownTime = shubuhTime - calendar.timeInMillis
            sholatType = "Shubuh"
        } else if (calendar.timeInMillis < syurukTime) {
            countDownTime = syurukTime - calendar.timeInMillis
            sholatType = "Syuruk"
        } else if (calendar.timeInMillis < dzuhurTime) {
            countDownTime = dzuhurTime - calendar.timeInMillis
            sholatType = "Dzuhur"
        } else if (calendar.timeInMillis < asharTime) {
            countDownTime = asharTime - calendar.timeInMillis
            sholatType = "Ashr"
        } else if (calendar.timeInMillis < maghribTime) {
            countDownTime = maghribTime - calendar.timeInMillis
            sholatType = "Maghrib"
        } else if (calendar.timeInMillis < isyaTime) {
            countDownTime = isyaTime - calendar.timeInMillis
            sholatType = "Isya"
        } else if (calendar.timeInMillis < shubuhTime + 24 * 60 * 60 * 1000) {
            countDownTime = shubuhTime + 24 * 60 * 60 * 1000 - calendar.timeInMillis
            sholatType = "Shubuh"
        }


        if (countDownTime > -1 && this.targetViewRef != null) {
            countDownTimer = object : CountDownTimer(countDownTime, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    val view = targetViewRef!!.get()
                    if (view != null) {
                        view.text = sholatType + " " + DateStringUtils.getDurationInString(millisUntilFinished / 1000)
                    }
                }

                override fun onFinish() {
                    val view = targetViewRef!!.get()
                    if (view != null) {
                        stopCountDown()
                        startCountDown(context, view)
                    }
                }

            }.start()
        }
    }

    fun stopCountDown() {
        if (countDownTimer != null) {
            countDownTimer!!.cancel()
            countDownTimer = null
        }

        targetViewRef = null
    }
}
