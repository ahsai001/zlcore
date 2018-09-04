package com.zaitunlabs.zlcore.modules.shaum_sholat;

import android.content.Context;
import android.os.CountDownTimer;
import android.widget.TextView;

import com.zaitunlabs.zlcore.utils.DateStringUtils;
import com.zaitunlabs.zlcore.utils.Prefs;

import java.lang.ref.WeakReference;
import java.util.Calendar;

/**
 * Created by ahsai on 2/5/2018.
 */

public class CountDownSholatReminderUtils {
    private CountDownTimer countDownTimer = null;
    private String sholatType = "";
    private WeakReference<TextView> targetViewRef = null;

    public void startCountDown(final Context context, TextView targetView){
        this.targetViewRef = new WeakReference<>(targetView);
        long shubuhTime = Prefs.with(context).getLong(ShaumSholatReminderService.START_SHUBUH_TIME,-1) + (ShaumSholatReminderService.PARAM_DEFAULT_TIME_REMINDER*60*1000);
        long syurukTime = Prefs.with(context).getLong(ShaumSholatReminderService.START_SYURUK_TIME,-1) + (ShaumSholatReminderService.PARAM_DEFAULT_TIME_REMINDER*60*1000);
        long dzuhurTime = Prefs.with(context).getLong(ShaumSholatReminderService.START_DZUHUR_TIME,-1) + (ShaumSholatReminderService.PARAM_DEFAULT_TIME_REMINDER*60*1000);
        long asharTime = Prefs.with(context).getLong(ShaumSholatReminderService.START_ASHR_TIME,-1) + (ShaumSholatReminderService.PARAM_DEFAULT_TIME_REMINDER*60*1000);
        long maghribTime = Prefs.with(context).getLong(ShaumSholatReminderService.START_MAGHRIB_TIME,-1) + (ShaumSholatReminderService.PARAM_DEFAULT_TIME_REMINDER*60*1000);
        long isyaTime = Prefs.with(context).getLong(ShaumSholatReminderService.START_ISYA_TIME,-1) + (ShaumSholatReminderService.PARAM_DEFAULT_TIME_REMINDER*60*1000);

        Calendar calendar = Calendar.getInstance();
        long countDownTime = -1;
        if(calendar.getTimeInMillis() < shubuhTime){
            countDownTime = shubuhTime - calendar.getTimeInMillis();
            sholatType = "Shubuh";
        } else if(calendar.getTimeInMillis() < syurukTime){
            countDownTime = syurukTime - calendar.getTimeInMillis();
            sholatType = "Syuruk";
        } else if(calendar.getTimeInMillis() < dzuhurTime){
            countDownTime = dzuhurTime - calendar.getTimeInMillis();
            sholatType = "Dzuhur";
        } else if(calendar.getTimeInMillis() < asharTime){
            countDownTime = asharTime - calendar.getTimeInMillis();
            sholatType = "Ashr";
        } else if(calendar.getTimeInMillis() < maghribTime){
            countDownTime = maghribTime - calendar.getTimeInMillis();
            sholatType = "Maghrib";
        } else if(calendar.getTimeInMillis() < isyaTime){
            countDownTime = isyaTime - calendar.getTimeInMillis();
            sholatType = "Isya";
        } else if(calendar.getTimeInMillis() < shubuhTime + (24*60*60*1000)){
            countDownTime = shubuhTime + (24*60*60*1000) - calendar.getTimeInMillis();
            sholatType = "Shubuh";
        }


        if(countDownTime > -1 && this.targetViewRef != null) {
            countDownTimer = new CountDownTimer(countDownTime, 1000) {
                public void onTick(long millisUntilFinished) {
                    TextView view = targetViewRef.get();
                    if(view !=  null) {
                        view.setText(sholatType + " " + DateStringUtils.getDurationInString(millisUntilFinished/1000));
                    }
                }

                public void onFinish() {
                    TextView view = targetViewRef.get();
                    if(view !=  null) {
                        stopCountDown();
                        startCountDown(context,view);
                    }
                }

            }.start();
        }
    }

    public void stopCountDown(){
        if(countDownTimer != null){
            countDownTimer.cancel();
            countDownTimer = null;
        }

        targetViewRef = null;
    }
}
