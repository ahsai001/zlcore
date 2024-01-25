package com.zaitunlabs.zlcore.utils;

import android.app.NotificationManager;
import android.content.Context;

import androidx.core.app.NotificationCompat;

/**
 * Created by ahsai on 2/14/2018.
 */

public class NotificationProgressUtil {
    private int notifID;
    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;
    private String title;
    private String desc;
    private int icon;

    public NotificationProgressUtil(Context context, String title, String desc, int icon, int notifID){
        mNotifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(context, CommonUtil.getPackageName(context));
        mBuilder.setContentTitle(title)
                .setContentText(desc)
                .setSmallIcon(icon);


        this.notifID = notifID;
        this.title = title;
        this.desc = desc;
        this.icon = icon;
    }

    public void setProgress(int max, int progress){
        mBuilder.setOngoing(true);
        mBuilder.setProgress(max, progress, false);
        mBuilder.setContentText(desc+" "+progress+" %");
        mNotifyManager.notify(notifID, mBuilder.build());
    }

    public void setIndeterminateProgress(){
        mBuilder.setOngoing(true);
        mBuilder.setProgress(0, 0, true);
        mBuilder.setContentText(desc);
        mNotifyManager.notify(notifID, mBuilder.build());
    }

    public void setComplete(String completeText){
        mBuilder.setOngoing(false);
        mBuilder.setContentText(completeText);
        mBuilder.setProgress(0,0,false);
        mNotifyManager.notify(notifID, mBuilder.build());
    }
}