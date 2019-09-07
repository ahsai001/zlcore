package com.zaitunlabs.zlcore.modules.shaum_sholat

import android.app.AlarmManager
import android.app.IntentService
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.location.Location
import androidx.core.app.JobIntentService
import androidx.legacy.content.WakefulBroadcastReceiver
import android.text.TextUtils

import com.zaitunlabs.zlcore.constants.ZLCoreConstanta
import com.zaitunlabs.zlcore.utils.CommonUtils
import com.zaitunlabs.zlcore.utils.IntegerIDUtils
import com.zaitunlabs.zlcore.utils.LocationUtils
import com.zaitunlabs.zlcore.utils.Prefs

import java.util.Calendar
import java.util.GregorianCalendar

import id.web.michsan.praytimes.Configuration
import id.web.michsan.praytimes.Method
import id.web.michsan.praytimes.PrayTimes
import id.web.michsan.praytimes.Util

/**
 * Created by ahmad s on 3/14/2016.
 */
class ShaumSholatReminderService : JobIntentService() {
    override fun onHandleWork(intent: Intent) {
        onHandleIntent(intent)
    }

    protected fun onHandleIntent(intent: Intent) {
        //set ashr/dzikir sore reminder and subuh/dzikir pagi reminder
        //getcurrent location

        val dateInMillis = Prefs.with(this@ShaumSholatReminderService).getLong(SHAUM_SHOLAT_PREFS_LAST_UPDATE, 0)

        val isNeedRunning = true

        /*
        isNeedRunning = false;
        if(dateInMillis > 0){
            Date lastupdate = new Date(dateInMillis);
            if(CommonUtils.compareToDay(Calendar.getInstance().getTime(),lastupdate)==0){
                isNeedRunning = false;
            }else{
                isNeedRunning = true;
            }
        }else{
            isNeedRunning = true;
        }
        */

        if (isNeedRunning) {
            val helper = LocationUtils(this)
            helper.updateLocationCallback = object : LocationUtils.LocationHelperCallback {
                override fun currentLocationUpdate(newLocation: Location) {
                    //set new schedule
                    val pt = PrayTimes(Method.MAKKAH)

                    // Adjustments
                    pt.adjust(PrayTimes.Time.FAJR, Configuration.angle(20.0))
                    pt.adjust(PrayTimes.Time.ISHA, Configuration.angle(18.0))

                    pt.adjust(PrayTimes.Time.IMSAK, Configuration.minutes(10.0))

                    // Offset tunings
                    pt.tuneOffset(PrayTimes.Time.FAJR, 2)
                    pt.tuneOffset(PrayTimes.Time.DHUHR, 2)
                    pt.tuneOffset(PrayTimes.Time.ASR, 2)
                    pt.tuneOffset(PrayTimes.Time.MAGHRIB, 2)
                    pt.tuneOffset(PrayTimes.Time.ISHA, 2)

                    pt.setAsrFactor(Method.ASR_FACTOR_STANDARD.toDouble())

                    // Calculate praytimes
                    val elevation = newLocation.altitude
                    val lat = newLocation.latitude
                    val lng = newLocation.longitude
                    val location = id.web.michsan.praytimes.Location(lat, lng, elevation)
                    // Timezone is defined in the calendar
                    val times = pt.getTimes(GregorianCalendar(), location)

                    val fajrTime = Util.toDayTime(times[PrayTimes.Time.FAJR]!!, false)
                    val syurukTime = Util.toDayTime(times[PrayTimes.Time.SUNRISE]!!, false)
                    val dzuhurTime = Util.toDayTime(times[PrayTimes.Time.DHUHR]!!, false)
                    val ashrTime = Util.toDayTime(times[PrayTimes.Time.ASR]!!, false)
                    val maghribTime = Util.toDayTime(times[PrayTimes.Time.MAGHRIB]!!, false)
                    val isyaTime = Util.toDayTime(times[PrayTimes.Time.ISHA]!!, false)

                    // Set the alarm to start next day 02:00 AM
                    val calendar = Calendar.getInstance()
                    calendar.time = calendar.time
                    calendar.add(Calendar.DAY_OF_YEAR, 1)
                    calendar.set(Calendar.HOUR_OF_DAY, 2)

                    setManageReminderAlarm(calendar.timeInMillis, ManageShaumSholatReminderReceiver::class.java)

                    //set last_update setting that alarm already set
                    val updateCal = Calendar.getInstance()
                    updateCal.time = updateCal.time
                    Prefs.with(this@ShaumSholatReminderService).save(SHAUM_SHOLAT_PREFS_LAST_UPDATE, updateCal.timeInMillis)


                    //setup notif for sholat
                    val sholatCalendar = Calendar.getInstance()
                    //shubuh
                    sholatCalendar.time = sholatCalendar.time
                    sholatCalendar.set(Calendar.HOUR_OF_DAY, fajrTime.hours)
                    sholatCalendar.set(Calendar.MINUTE, fajrTime.minutes)
                    setSholatReminderAlarm(sholatCalendar.timeInMillis - PARAM_DEFAULT_TIME_REMINDER * 60 * 1000,
                            SholatReminderReceiver::class.java, REQUEST_CODE_FOR_SHUBUH, START_SHUBUH_TIME)
                    //syuruk
                    sholatCalendar.time = sholatCalendar.time
                    sholatCalendar.set(Calendar.HOUR_OF_DAY, syurukTime.hours)
                    sholatCalendar.set(Calendar.MINUTE, syurukTime.minutes)
                    setSholatReminderAlarm(sholatCalendar.timeInMillis - PARAM_DEFAULT_TIME_REMINDER * 60 * 1000,
                            SholatReminderReceiver::class.java, REQUEST_CODE_FOR_SYURUK, START_SYURUK_TIME)


                    //jumat or not
                    val dayOfWeek = sholatCalendar.get(Calendar.DAY_OF_WEEK)
                    if (dayOfWeek == Calendar.FRIDAY) {
                        sholatCalendar.time = sholatCalendar.time
                        sholatCalendar.set(Calendar.HOUR_OF_DAY, dzuhurTime.hours)
                        sholatCalendar.set(Calendar.MINUTE, dzuhurTime.minutes)
                        setSholatReminderAlarm(sholatCalendar.timeInMillis - PARAM_JUMAT_TIME_REMINDER * 60 * 1000,
                                SholatReminderReceiver::class.java, REQUEST_CODE_FOR_JUMAT, START_JUMAT_TIME)
                    }

                    //dzuhur
                    sholatCalendar.time = sholatCalendar.time
                    sholatCalendar.set(Calendar.HOUR_OF_DAY, dzuhurTime.hours)
                    sholatCalendar.set(Calendar.MINUTE, dzuhurTime.minutes)
                    setSholatReminderAlarm(sholatCalendar.timeInMillis - PARAM_DEFAULT_TIME_REMINDER * 60 * 1000,
                            SholatReminderReceiver::class.java, REQUEST_CODE_FOR_DZUHUR, START_DZUHUR_TIME)


                    //ashar
                    sholatCalendar.time = sholatCalendar.time
                    sholatCalendar.set(Calendar.HOUR_OF_DAY, ashrTime.hours)
                    sholatCalendar.set(Calendar.MINUTE, ashrTime.minutes)
                    setSholatReminderAlarm(sholatCalendar.timeInMillis - PARAM_DEFAULT_TIME_REMINDER * 60 * 1000,
                            SholatReminderReceiver::class.java, REQUEST_CODE_FOR_ASHR, START_ASHR_TIME)
                    //maghrib
                    sholatCalendar.time = sholatCalendar.time
                    sholatCalendar.set(Calendar.HOUR_OF_DAY, maghribTime.hours)
                    sholatCalendar.set(Calendar.MINUTE, maghribTime.minutes)
                    setSholatReminderAlarm(sholatCalendar.timeInMillis - PARAM_DEFAULT_TIME_REMINDER * 60 * 1000,
                            SholatReminderReceiver::class.java, REQUEST_CODE_FOR_MAGHRIB, START_MAGHRIB_TIME)
                    //isya
                    sholatCalendar.time = sholatCalendar.time
                    sholatCalendar.set(Calendar.HOUR_OF_DAY, isyaTime.hours)
                    sholatCalendar.set(Calendar.MINUTE, isyaTime.minutes)
                    setSholatReminderAlarm(sholatCalendar.timeInMillis - PARAM_DEFAULT_TIME_REMINDER * 60 * 1000,
                            SholatReminderReceiver::class.java, REQUEST_CODE_FOR_ISYA, START_ISYA_TIME)


                    //setup shaum reminder if needed
                    setShaumReminderAlarm(sholatCalendar.timeInMillis)

                    //WakefulBroadcastReceiver.completeWakefulIntent(intent);
                }


                override fun failed(reason: String) {

                    //Set the alarm to start again in a minutes
                    val calendar = Calendar.getInstance()
                    calendar.time = calendar.time
                    calendar.add(Calendar.MINUTE, 5)

                    setManageReminderAlarm(calendar.timeInMillis, ManageShaumSholatReminderReceiver::class.java)

                    //WakefulBroadcastReceiver.completeWakefulIntent(intent);
                }
            }
            helper.init()
            helper.start()
        } else {
            //WakefulBroadcastReceiver.completeWakefulIntent(intent);
        }
    }

    private fun setManageReminderAlarm(time: Long, receiver: Class<*>) {
        val alarmMgr = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val reminderIntent = Intent(this, receiver)
        reminderIntent.action = ZLCoreConstanta.ACTION_MANAGE_SHAUM_SHOLAT_REMINDER + IntegerIDUtils.getID(this)
        val alarmIntent = PendingIntent.getBroadcast(this, 212, reminderIntent, 0)

        alarmMgr.set(AlarmManager.RTC_WAKEUP, time, alarmIntent)
    }


    private fun setSholatReminderAlarm(time: Long, receiver: Class<*>, code: Int, prefCode: String) {
        val alarmMgr = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val reminderIntent = Intent(this, receiver)
        reminderIntent.putExtra(PARAM_SHOLAT_CODE, code)
        reminderIntent.putExtra(PARAM_PREFS_SHOLAT_CODE, prefCode)
        reminderIntent.action = "com.zaitunlabs.zlcore.sholat_reminder_alarm$code"
        val alarmIntent = PendingIntent.getBroadcast(this, 335, reminderIntent, 0)

        alarmMgr.cancel(alarmIntent)

        alarmMgr.set(AlarmManager.RTC_WAKEUP, time, alarmIntent)
        Prefs.with(this).save(prefCode, time)
    }

    private fun setShaumReminderAlarm(reminderTime: Long) {
        var dayOfShaum: String? = null
        val shaumCalendar = Calendar.getInstance()
        val dayOfWeek = shaumCalendar.get(Calendar.DAY_OF_WEEK)
        if (dayOfWeek == Calendar.SUNDAY || dayOfWeek == Calendar.WEDNESDAY) {
            shaumCalendar.add(Calendar.DAY_OF_WEEK, 1)
            dayOfShaum = CommonUtils.getDayName(shaumCalendar, null).toLowerCase()
        }

        if (!TextUtils.isEmpty(dayOfShaum)) {
            val alarmMgr = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            val reminderIntent = Intent(this, ShaumReminderReceiver::class.java)
            reminderIntent.putExtra(PARAM_SHAUM_DAY, dayOfShaum)
            reminderIntent.action = "com.zaitunlabs.zlcore.shaum_reminder_alarm"
            val alarmIntent = PendingIntent.getBroadcast(this, 445, reminderIntent, 0)

            alarmMgr.cancel(alarmIntent)

            alarmMgr.set(AlarmManager.RTC_WAKEUP, reminderTime, alarmIntent)
        }
    }

    companion object {
        val JOB_ID = 11000014


        val PARAM_SHOW_FLAG = "param_show_flag"
        val PARAM_MESSAGE = "param_message"
        val SHAUM_SHOLAT_PREFS_LAST_UPDATE = "shaum_shalat_prefs_last_update"
        val PARAM_SHOLAT_CODE = "param_sholat_code"
        val PARAM_PREFS_SHOLAT_CODE = "param_prefs_sholat_code"
        val PARAM_SHAUM_DAY = "param_shaum_day"

        val PARAM_DEFAULT_TIME_REMINDER = 5
        val PARAM_JUMAT_TIME_REMINDER = 50


        val REQUEST_CODE_FOR_SHUBUH = 0
        val REQUEST_CODE_FOR_SYURUK = 1
        val REQUEST_CODE_FOR_DZUHUR = 2
        val REQUEST_CODE_FOR_ASHR = 3
        val REQUEST_CODE_FOR_MAGHRIB = 4
        val REQUEST_CODE_FOR_ISYA = 5
        val REQUEST_CODE_FOR_JUMAT = 6


        val START_SHUBUH_TIME = "prefs_start_shubuh_timems"
        val START_SYURUK_TIME = "prefs_start_syuruk_timems"
        val START_DZUHUR_TIME = "prefs_start_dzuhur_timems"
        val START_JUMAT_TIME = "prefs_start_jumat_timems"
        val START_ASHR_TIME = "prefs_start_ashr_timems"
        val START_MAGHRIB_TIME = "prefs_start_maghrib_timems"
        val START_ISYA_TIME = "prefs_start_isya_timems"
    }

}
