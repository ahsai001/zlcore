package com.zaitunlabs.zlcore.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import android.text.TextUtils;
import android.webkit.URLUtil;

import com.zaitunlabs.zlcore.R;
import com.zaitunlabs.zlcore.activities.InfoPopup;
import com.zaitunlabs.zlcore.activities.MessageListActivity;
import com.zaitunlabs.zlcore.activities.ReminderPopup;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static com.zaitunlabs.zlcore.activities.InfoPopup.EXTRA_INFO_ID;
import static com.zaitunlabs.zlcore.activities.InfoPopup.EXTRA_DATA;

/**
 * Created by ahmad s on 2/24/2016.
 */
public class NotificationUtils {
    private static AtomicInteger atomicInteger = null;
    private static final String ATOMIC_INIT_VALUE_FOR_NOTIF = "atomic_init_value_for_fcm";
    private static final int init_value = 0;

    public static int getID(Context context) {
        synchronized (NotificationUtils.class){
            if(atomicInteger == null){
                int init = Prefs.with(context).getInt(ATOMIC_INIT_VALUE_FOR_NOTIF, init_value);
                atomicInteger = new AtomicInteger(init);
            }
            int nextValue = atomicInteger.incrementAndGet();
            Prefs.with(context).save(ATOMIC_INIT_VALUE_FOR_NOTIF,nextValue);
            return nextValue;
        }
    }


    public static void onMessageReceived(Context context, Map<String, String> data, String notifTitle, String notifBody,
                                         Class homePageClass, Class messageListClass, Bundle messageListClassData,
                                         int appNameResId, int iconResId,
                                         Map<String, CustomTypeCallBackHandler> customTypeCallBackList) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "zlcore:smartFirebaseMessagingServiceTAG");

        wl.acquire();

        Notification notif = null;
        long infoId = -1;

        if(notifTitle != null || notifBody != null){
            //notification
            notif = getNotification(context, notifTitle,notifBody, null, homePageClass, data, appNameResId, iconResId, null, true, false);
        }else{
            //data
            String title = data.get("title");
            String body = data.get("body");
            String photo = data.get("photo"); //photo url

            String type = data.get("type");
            //1. notif | 2. notif+ | 3. popup ==> notif+popup | 4. wakeup | 5. reminder
            //notif+ dan popup harus ada messagelistactivity

            String needlogin = data.get("needlogin"); //need login to notif, remind and saved, yes or no
            String action = data.get("action"); //url or full path activity
            String isHeadsUp = data.get("headsup"); //yes or no

            if(needlogin.toLowerCase().equals("yes") && !PrefsData.isAccountLogin())return;


            CustomTypeCallBackHandler customTypeCallBackHandler = null;
            if(customTypeCallBackList != null && customTypeCallBackList.containsKey(type.toLowerCase())){
                customTypeCallBackHandler = customTypeCallBackList.get(type);
            }



            if(type.toLowerCase().equals("wakeup")) {
                //do nothing, just to wakeup this app,
            } else if(type.toLowerCase().equals("reminder")) {
                //just show reminder, not notif, not popup, and not saved
                ReminderPopup.start(context,title,body);
            } else if(customTypeCallBackHandler != null && !customTypeCallBackHandler.isShowInNotification()){
                //custom handle without notification
                customTypeCallBackHandler.handleCustomType();
            } else {
                //handle with notification
                int intType = 1;
                Intent targetIntent = null;

                if(messageListClass == null){
                    messageListClass = MessageListActivity.class;
                }

                if(messageListClassData == null){
                    messageListClassData = new Bundle();
                }

                if(type.toLowerCase().equals("notif")){
                    intType = 1;
                    if(!TextUtils.isEmpty(action)) {
                        if (URLUtil.isValidUrl(action)) {
                            //open browser
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse(action));
                            targetIntent = Intent.createChooser(i, "open link with :").setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        } else {
                            //may be this is activity name
                            try {
                                Class nextClass = Class.forName(action);
                                targetIntent = new Intent(context.getApplicationContext(), nextClass);
                            } catch (ClassNotFoundException e) {
                                targetIntent = new Intent(context.getApplicationContext(), homePageClass);
                            }
                        }
                    } else {
                        targetIntent = new Intent(context.getApplicationContext(), homePageClass);
                    }
                } else if(type.toLowerCase().equals("notif+")){
                    intType = 2;
                    targetIntent = new Intent(context.getApplicationContext(), messageListClass);
                    infoId = InfoUtils.insertNewInfo(title, body, photo, action, intType);
                } else if(type.toLowerCase().equals("popup")){
                    intType = 3;
                    targetIntent = new Intent(context.getApplicationContext(), messageListClass);
                    infoId = InfoUtils.insertNewInfo(title, body, photo, action, intType);
                } else if(customTypeCallBackHandler != null && customTypeCallBackHandler.isShowInNotification()){
                    intType = customTypeCallBackHandler.getInformationTypeId();
                    targetIntent = customTypeCallBackHandler.handleCustomType();
                    if(customTypeCallBackHandler.isShowInInformationList()) {
                        infoId = InfoUtils.insertNewInfo(title, body, photo, action, intType);
                    }
                }

                if(type.toLowerCase().equals("popup")){
                    InfoPopup.start(context, messageListClass, messageListClassData);
                }

                if(targetIntent != null) {
                    targetIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    targetIntent.putExtra(EXTRA_DATA, messageListClassData);
                    targetIntent.putExtra(EXTRA_INFO_ID, infoId);
                }

                notif = getNotification(context,title, body, photo, targetIntent, null, appNameResId, iconResId, null, false,
                        !TextUtils.isEmpty(isHeadsUp) && isHeadsUp.toLowerCase().equals("yes"));
            }
        }

        if(notif != null){
            NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            nm.notify(NotificationUtils.getID(context), notif);
        }


        wl.release();
    }


    public static interface CustomTypeCallBackHandler {
        Intent handleCustomType();
        boolean isShowInNotification();
        boolean isShowInInformationList();
        int getInformationTypeId();
    }

    public static interface CallBackIntentFromNotification {
        void showMessagesPage(Bundle bundle);
    }

    public static void handleIntentFromNotification(final Intent intent, CallBackIntentFromNotification callBackIntentFromNotification ){
        //place this in onCreate and onNewIntent
        Bundle extraData = CommonUtils.getBundleIntent(intent, InfoPopup.EXTRA_DATA, null);
        if(extraData != null) {
            callBackIntentFromNotification.showMessagesPage(extraData);
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                long extraInfoId = CommonUtils.getLongIntent(intent, InfoPopup.EXTRA_INFO_ID, -1);
                InfoUtils.scrollInfoList(extraInfoId);
            }
        },200);
    }

    public static void showNotification(Context context, String title, String content, Class nextActivity,
                                        HashMap<String, String> data, int appNameResId, int iconResId, boolean autocancel, boolean isHeadsUp){
        showNotification(context,title,content, null, nextActivity,data,appNameResId,iconResId, NotificationUtils.getID(context),null, autocancel, isHeadsUp);
    }

    public static void showNotification(Context context, String title, String content, Class nextActivity,
                                        HashMap<String, String> data, int appNameResId, int iconResId,int notifID, String pendingIntentAction, boolean autocancel, boolean isHeadsUp){
        showNotification(context,title,content,null, nextActivity,data,appNameResId,iconResId, notifID,pendingIntentAction, autocancel, isHeadsUp);
    }

    public static void showNotification(Context context, String title, String content,  String imageUrl, Class nextActivity,
                                        HashMap<String, String> data, int appNameResId, int iconResId, int notifID, String pendingIntentAction, boolean autocancel, boolean isHeadsUp){
        Notification notif = getNotification(context,title, content,imageUrl, nextActivity, data, appNameResId, iconResId, pendingIntentAction, autocancel, isHeadsUp);
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(notifID, notif);
    }

    public static Notification getNotification(Context context, String title, String content, String imageUrl, Class nextActivity,
                                               Map<String, String> data, int appNameResId, int iconResId, String pendingIntentAction, boolean autocancel, boolean isHeadsUp){
        Intent nextIntent = null;
        if(nextActivity != null) {
            nextIntent = new Intent(context, nextActivity);
        }
        return getNotification(context,title,content,imageUrl,nextIntent,data,appNameResId,iconResId,pendingIntentAction,autocancel, isHeadsUp);
    }


    public static Notification getNotification(Context context, String title, String content, String imageUrl, Intent nextIntent,
                                               Map<String, String> data, int appNameResId, int iconResId, String pendingIntentAction, boolean autocancel, boolean isHeadsUp){
        PendingIntent pi = null;
        if(nextIntent != null) {
            if (data != null) {
                Object[] keys = data.keySet().toArray();
                for (Object key : keys) {
                    nextIntent.putExtra((String) key, data.get(key));
                }
            }
            if(TextUtils.isEmpty(pendingIntentAction)) {
                if(TextUtils.isEmpty(nextIntent.getAction())) {
                    nextIntent.setAction("com.zaitunlabs.zlcore.general_reminder_notification" + NotificationUtils.getID(context));
                }
            } else {
                nextIntent.setAction(pendingIntentAction);
            }
            pi = PendingIntent.getActivity(context, 131, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        Notification notification = null;

        Bitmap iconBitMap = null;
        if (!TextUtils.isEmpty(imageUrl)) {
            iconBitMap = CommonUtils.getBitmapFromURL(imageUrl);
        }

        String channelID = CommonUtils.getPackageName(context);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String name = context.getString(R.string.app_name);
            String description = context.getString(R.string.app_name);
            int importance = NotificationManager.IMPORTANCE_HIGH; //Important for heads-up notification
            NotificationChannel channel = new NotificationChannel(channelID, name, importance);
            channel.setDescription(description);
            channel.setShowBadge(true);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }


        String notifTitle = TextUtils.isEmpty(title) ? context.getString(appNameResId) : title;
        String notifText = TextUtils.isEmpty(content) ? context.getString(appNameResId) : content;
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,channelID);

        builder.setContentTitle(notifTitle)
                .setContentText(notifText);

        if(iconBitMap != null) {
            NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
            bigPictureStyle.setBigContentTitle(title);
            bigPictureStyle.setSummaryText(CommonUtils.fromHtml(notifText).toString());
            bigPictureStyle.bigPicture(iconBitMap);
            builder.setStyle(bigPictureStyle);
        } else {
            /*
            NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
            inboxStyle.addLine(notifText);
            inboxStyle.setBigContentTitle(title);*/
            builder.setStyle(new NotificationCompat.BigTextStyle().bigText(notifText).setBigContentTitle(title));
        }

        builder.setSubText(context.getString(R.string.app_name));

        builder.setAutoCancel(autocancel)
                .setLights(Color.RED, 1000, 1000)
                .setVibrate(new long[]{0, 400, 250, 400})
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        if(isHeadsUp){
            builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        }

        if(pi != null) {
            builder.setContentIntent(pi);
        }

        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(),iconResId));
        builder.setSmallIcon(iconResId);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            notification = builder.build();
        }else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH){
            notification = builder.getNotification();
        }
        return notification;
    }



}