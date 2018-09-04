package com.zaitunlabs.zlcore.modules.shaum_sholat;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.text.TextUtils;

import com.zaitunlabs.zlcore.utils.IntegerIDUtils;
import com.zaitunlabs.zlcore.utils.LocationUtils;
import com.zaitunlabs.zlcore.utils.Prefs;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;

import id.web.michsan.praytimes.Configuration;
import id.web.michsan.praytimes.Method;
import id.web.michsan.praytimes.PrayTimes;
import id.web.michsan.praytimes.Util;

/**
 * Created by ahmad s on 3/14/2016.
 */
public class ShaumSholatReminderService extends IntentService {
    public ShaumSholatReminderService(String name) {
        super(name);
    }

    public ShaumSholatReminderService(){
        super(ShaumSholatReminderService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(final Intent intent) {
        //set ashr/dzikir sore reminder and subuh/dzikir pagi reminder
        //getcurrent location

        long dateInMillis = Prefs.with(ShaumSholatReminderService.this).getLong(SHAUM_SHOLAT_PREFS_LAST_UPDATE,0);

        boolean isNeedRunning = true;

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

        if(isNeedRunning) {
            final LocationUtils helper = new LocationUtils(this);
            helper.setUpdateLocationCallback(new LocationUtils.LocationHelperCallback() {
                @Override
                public void currentLocationUpdate(Location newLocation) {
                    //set new schedule
                    PrayTimes pt = new PrayTimes(Method.MAKKAH);

                    // Adjustments
                    pt.adjust(PrayTimes.Time.FAJR, Configuration.angle(20));
                    pt.adjust(PrayTimes.Time.ISHA, Configuration.angle(18));

                    pt.adjust(PrayTimes.Time.IMSAK, Configuration.minutes(10));

                    // Offset tunings
                    pt.tuneOffset(PrayTimes.Time.FAJR, 2);
                    pt.tuneOffset(PrayTimes.Time.DHUHR, 2);
                    pt.tuneOffset(PrayTimes.Time.ASR, 2);
                    pt.tuneOffset(PrayTimes.Time.MAGHRIB, 2);
                    pt.tuneOffset(PrayTimes.Time.ISHA, 2);

                    pt.setAsrFactor(Method.ASR_FACTOR_STANDARD);

                    // Calculate praytimes
                    double elevation = newLocation.getAltitude();
                    double lat = newLocation.getLatitude();
                    double lng = newLocation.getLongitude();
                    id.web.michsan.praytimes.Location location = new id.web.michsan.praytimes.Location(lat, lng, elevation);
                    // Timezone is defined in the calendar
                    Map<PrayTimes.Time, Double> times = pt.getTimes(new GregorianCalendar(), location);

                    Util.DayTime fajrTime = Util.toDayTime(times.get(PrayTimes.Time.FAJR), false);
                    Util.DayTime syurukTime = Util.toDayTime(times.get(PrayTimes.Time.SUNRISE), false);
                    Util.DayTime dzuhurTime = Util.toDayTime(times.get(PrayTimes.Time.DHUHR), false);
                    Util.DayTime ashrTime = Util.toDayTime(times.get(PrayTimes.Time.ASR), false);
                    Util.DayTime maghribTime = Util.toDayTime(times.get(PrayTimes.Time.MAGHRIB), false);
                    Util.DayTime isyaTime = Util.toDayTime(times.get(PrayTimes.Time.ISHA), false);

                    // Set the alarm to start next day 02:00 AM
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(calendar.getTime());
                    calendar.add(Calendar.DAY_OF_YEAR, 1);
                    calendar.set(Calendar.HOUR_OF_DAY, 2);

                    setManageReminderAlarm(calendar.getTimeInMillis(), ManageShaumSholatReminderReceiver.class);

                    //set last_update setting that alarm already set
                    Calendar updateCal = Calendar.getInstance();
                    updateCal.setTime(updateCal.getTime());
                    Prefs.with(ShaumSholatReminderService.this).save(SHAUM_SHOLAT_PREFS_LAST_UPDATE, updateCal.getTimeInMillis());



                    //setup notif for sholat
                    Calendar sholatCalendar = Calendar.getInstance();
                    //shubuh
                    sholatCalendar.setTime(sholatCalendar.getTime());
                    sholatCalendar.set(Calendar.HOUR_OF_DAY, fajrTime.getHours());
                    sholatCalendar.set(Calendar.MINUTE, fajrTime.getMinutes());
                    setSholatReminderAlarm(sholatCalendar.getTimeInMillis() - (PARAM_DEFAULT_TIME_REMINDER*60*1000),
                            SholatReminderReceiver.class,REQUEST_CODE_FOR_SHUBUH,START_SHUBUH_TIME);
                    //syuruk
                    sholatCalendar.setTime(sholatCalendar.getTime());
                    sholatCalendar.set(Calendar.HOUR_OF_DAY, syurukTime.getHours());
                    sholatCalendar.set(Calendar.MINUTE, syurukTime.getMinutes());
                    setSholatReminderAlarm(sholatCalendar.getTimeInMillis() - (PARAM_DEFAULT_TIME_REMINDER*60*1000),
                            SholatReminderReceiver.class,REQUEST_CODE_FOR_SYURUK,START_SYURUK_TIME);


                    //jumat or not
                    int dayOfWeek = sholatCalendar.get(Calendar.DAY_OF_WEEK);
                    if(dayOfWeek == Calendar.FRIDAY){
                        sholatCalendar.setTime(sholatCalendar.getTime());
                        sholatCalendar.set(Calendar.HOUR_OF_DAY, dzuhurTime.getHours());
                        sholatCalendar.set(Calendar.MINUTE, dzuhurTime.getMinutes());
                        setSholatReminderAlarm(sholatCalendar.getTimeInMillis() - (PARAM_JUMAT_TIME_REMINDER*60*1000),
                                SholatReminderReceiver.class,REQUEST_CODE_FOR_JUMAT,START_JUMAT_TIME);
                    }

                    //dzuhur
                    sholatCalendar.setTime(sholatCalendar.getTime());
                    sholatCalendar.set(Calendar.HOUR_OF_DAY, dzuhurTime.getHours());
                    sholatCalendar.set(Calendar.MINUTE, dzuhurTime.getMinutes());
                    setSholatReminderAlarm(sholatCalendar.getTimeInMillis() - (PARAM_DEFAULT_TIME_REMINDER*60*1000),
                            SholatReminderReceiver.class,REQUEST_CODE_FOR_DZUHUR,START_DZUHUR_TIME);



                    //ashar
                    sholatCalendar.setTime(sholatCalendar.getTime());
                    sholatCalendar.set(Calendar.HOUR_OF_DAY, ashrTime.getHours());
                    sholatCalendar.set(Calendar.MINUTE, ashrTime.getMinutes());
                    setSholatReminderAlarm(sholatCalendar.getTimeInMillis() - (PARAM_DEFAULT_TIME_REMINDER*60*1000),
                            SholatReminderReceiver.class,REQUEST_CODE_FOR_ASHR,START_ASHR_TIME);
                    //maghrib
                    sholatCalendar.setTime(sholatCalendar.getTime());
                    sholatCalendar.set(Calendar.HOUR_OF_DAY, maghribTime.getHours());
                    sholatCalendar.set(Calendar.MINUTE, maghribTime.getMinutes());
                    setSholatReminderAlarm(sholatCalendar.getTimeInMillis() - (PARAM_DEFAULT_TIME_REMINDER*60*1000),
                            SholatReminderReceiver.class,REQUEST_CODE_FOR_MAGHRIB,START_MAGHRIB_TIME);
                    //isya
                    sholatCalendar.setTime(sholatCalendar.getTime());
                    sholatCalendar.set(Calendar.HOUR_OF_DAY, isyaTime.getHours());
                    sholatCalendar.set(Calendar.MINUTE, isyaTime.getMinutes());
                    setSholatReminderAlarm(sholatCalendar.getTimeInMillis() - (PARAM_DEFAULT_TIME_REMINDER*60*1000),
                            SholatReminderReceiver.class,REQUEST_CODE_FOR_ISYA,START_ISYA_TIME);




                    //setup shaum reminder if needed
                    setShaumReminderAlarm(sholatCalendar.getTimeInMillis());

                    WakefulBroadcastReceiver.completeWakefulIntent(intent);
                }

                
                @Override
                public void failed(String reason) {

                    //Set the alarm to start again in a minutes
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(calendar.getTime());
                    calendar.add(Calendar.MINUTE, 5);

                    setManageReminderAlarm(calendar.getTimeInMillis(), ManageShaumSholatReminderReceiver.class);

                    WakefulBroadcastReceiver.completeWakefulIntent(intent);
                }
            });
            helper.init();
            helper.start();
        }else{
            WakefulBroadcastReceiver.completeWakefulIntent(intent);
        }
    }

    private void setManageReminderAlarm(long time, Class receiver){
        AlarmManager alarmMgr = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);

        Intent reminderIntent = new Intent(this, receiver);
        reminderIntent.setAction("com.zaitunlabs.zlcore.manage_reminder_alarm"+IntegerIDUtils.getID(this));
        PendingIntent alarmIntent = PendingIntent.getBroadcast(this, 212, reminderIntent, 0);

        alarmMgr.set(AlarmManager.RTC_WAKEUP, time, alarmIntent);
    }




    private void setSholatReminderAlarm(long time, Class receiver, int code, String prefCode){
        AlarmManager alarmMgr = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);

        Intent reminderIntent = new Intent(this, receiver);
        reminderIntent.putExtra(PARAM_SHOLAT_CODE, code);
        reminderIntent.putExtra(PARAM_PREFS_SHOLAT_CODE, prefCode);
        reminderIntent.setAction("com.zaitunlabs.zlcore.sholat_reminder_alarm"+code);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(this, 335, reminderIntent, 0);

        alarmMgr.cancel(alarmIntent);

        alarmMgr.set(AlarmManager.RTC_WAKEUP, time, alarmIntent);
        Prefs.with(this).save(prefCode,time);
    }

    private void setShaumReminderAlarm(long reminderTime){
        String dayOfShaum = null;
        Calendar shaumCalendar = Calendar.getInstance();
        int dayOfWeek = shaumCalendar.get(Calendar.DAY_OF_WEEK);
        if(dayOfWeek == Calendar.SUNDAY || dayOfWeek == Calendar.WEDNESDAY){
            if(dayOfWeek == Calendar.SUNDAY){
                dayOfShaum = "senin";
            } else if(dayOfWeek == Calendar.WEDNESDAY){
                dayOfShaum = "kamis";
            }

        }

        if(!TextUtils.isEmpty(dayOfShaum)) {
            AlarmManager alarmMgr = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);

            Intent reminderIntent = new Intent(this, ShaumReminderReceiver.class);
            reminderIntent.putExtra(PARAM_SHAUM_DAY, dayOfShaum);
            reminderIntent.setAction("com.zaitunlabs.zlcore.shaum_reminder_alarm");
            PendingIntent alarmIntent = PendingIntent.getBroadcast(this, 445, reminderIntent, 0);

            alarmMgr.cancel(alarmIntent);

            alarmMgr.set(AlarmManager.RTC_WAKEUP, reminderTime, alarmIntent);
        }
    }




    public static final String PARAM_SHOW_FLAG = "param_show_flag";
    public static final String PARAM_MESSAGE = "param_message";
    public static final String SHAUM_SHOLAT_PREFS_LAST_UPDATE = "shaum_shalat_prefs_last_update";
    public static final String PARAM_SHOLAT_CODE = "param_sholat_code";
    public static final String PARAM_PREFS_SHOLAT_CODE = "param_prefs_sholat_code";
    public static final String PARAM_SHAUM_DAY = "param_shaum_day";

    public static final int PARAM_DEFAULT_TIME_REMINDER = 5;
    public static final int PARAM_JUMAT_TIME_REMINDER = 50;


    public static final int REQUEST_CODE_FOR_SHUBUH = 0;
    public static final int REQUEST_CODE_FOR_SYURUK = 1;
    public static final int REQUEST_CODE_FOR_DZUHUR = 2;
    public static final int REQUEST_CODE_FOR_ASHR = 3;
    public static final int REQUEST_CODE_FOR_MAGHRIB = 4;
    public static final int REQUEST_CODE_FOR_ISYA = 5;
    public static final int REQUEST_CODE_FOR_JUMAT = 6;


    public static final String START_SHUBUH_TIME = "prefs_start_shubuh_timems";
    public static final String START_SYURUK_TIME = "prefs_start_syuruk_timems";
    public static final String START_DZUHUR_TIME = "prefs_start_dzuhur_timems";
    public static final String START_JUMAT_TIME = "prefs_start_jumat_timems";
    public static final String START_ASHR_TIME = "prefs_start_ashr_timems";
    public static final String START_MAGHRIB_TIME = "prefs_start_maghrib_timems";
    public static final String START_ISYA_TIME = "prefs_start_isya_timems";

}
