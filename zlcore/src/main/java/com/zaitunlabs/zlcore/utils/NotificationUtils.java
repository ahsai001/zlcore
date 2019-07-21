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
import android.os.Parcelable;
import android.os.PowerManager;
import android.text.TextUtils;
import android.webkit.URLUtil;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.zaitunlabs.zlcore.R;
import com.zaitunlabs.zlcore.activities.InfoPopup;
import com.zaitunlabs.zlcore.activities.MessageListActivity;
import com.zaitunlabs.zlcore.activities.ReminderPopup;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.zaitunlabs.zlcore.activities.InfoPopup.EXTRA_DATA;
import static com.zaitunlabs.zlcore.activities.InfoPopup.EXTRA_INFO_ID;


/**
 * Created by ahmad s on 2/24/2016.
 */
public class NotificationUtils {

    public static int getID() {
        synchronized (NotificationUtils.class){
            return IntegerIDUtils.getID();
        }
    }


    public static void onMessageReceived(Context context, Map<String, Object> data, String notifTitle, String notifBody,
                                         Class homePageClass, Class messageListClass, Bundle messageListClassData,
                                         int appNameResId, int iconResId,
                                         Map<String, CustomTypeCallBackHandler> customTypeCallBackList, boolean isLoggedIn) {
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
            String title = (String) data.get("title");
            String body = (String) data.get("body");
            String photo = (String) data.get("photo"); //photo url

            String type = (String) data.get("type");
            //1. notif | 2. notif+ | 3. popup ==> notif+popup | 4. wakeup | 5. reminder
            //notif+ dan popup harus ada messagelistactivity

            String needlogin = (String) data.get("needlogin"); //need login to notif, remind and saved, yes or no
            String action = (String) data.get("action"); //url or full path class
            String isHeadsUp = (String) data.get("headsup"); //yes or no

            if(!TextUtils.isEmpty(needlogin) && needlogin.toLowerCase().equals("yes") && !isLoggedIn)return;


            CustomTypeCallBackHandler customTypeCallBackHandler = null;
            if(customTypeCallBackList != null && !TextUtils.isEmpty(type) && customTypeCallBackList.containsKey(type.toLowerCase())){
                customTypeCallBackHandler = customTypeCallBackList.get(type);
            }


            if(type.toLowerCase().equals("wakeup")) {
                //do nothing, just to wakeup this app,
            } else if(type.toLowerCase().equals("reminder")) {
                //just show reminder, not notif, not popup, and not saved
                ReminderPopup.start(context,title,body);
            } else if(customTypeCallBackHandler != null && !customTypeCallBackHandler.isShownInNotification()){
                //custom handle without notification
                customTypeCallBackHandler.handleCustomType();
            } else {
                //handle with notification
                int intType = 1;
                Intent nextIntent = null;
                int nextIntentComponentType = INTENT_COMPONENT_TYPE_ACTIVITY;

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
                            nextIntent = Intent.createChooser(i, "open link with :").setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        } else {
                            //may be this is class name
                            try {
                                if(action.startsWith("activity://")){
                                    nextIntentComponentType = INTENT_COMPONENT_TYPE_ACTIVITY;
                                    action = action.replace("activity://","");
                                } else if(action.startsWith("service://")){
                                    nextIntentComponentType = INTENT_COMPONENT_TYPE_SERVICE;
                                    action = action.replace("service://","");
                                } else if(action.startsWith("receiver://")){
                                    nextIntentComponentType = INTENT_COMPONENT_TYPE_BROADCAST;
                                    action = action.replace("receiver://","");
                                } else if(action.startsWith("foreground-service://")){
                                    nextIntentComponentType = INTENT_COMPONENT_TYPE_FOREGROUND_SERVICE;
                                    action = action.replace("foreground-service://","");
                                }
                                Class nextClass = Class.forName(action);
                                nextIntent = new Intent(context.getApplicationContext(), nextClass);
                            } catch (ClassNotFoundException e) {
                                nextIntent = new Intent(context.getApplicationContext(), homePageClass);
                            }
                        }
                    } else {
                        nextIntent = new Intent(context.getApplicationContext(), homePageClass);
                    }
                } else if(type.toLowerCase().equals("notif+")){
                    intType = 2;
                    nextIntent = new Intent(context.getApplicationContext(), messageListClass);
                    infoId = InfoUtils.insertNewInfo(title, body, photo, action, intType);
                } else if(type.toLowerCase().equals("popup")){
                    intType = 3;
                    nextIntent = new Intent(context.getApplicationContext(), messageListClass);
                    infoId = InfoUtils.insertNewInfo(title, body, photo, action, intType);
                } else if(customTypeCallBackHandler != null && customTypeCallBackHandler.isShownInNotification()){
                    intType = customTypeCallBackHandler.getInformationTypeId();
                    nextIntentComponentType = customTypeCallBackHandler.getIntentComponentType();
                    nextIntent = customTypeCallBackHandler.handleCustomType();
                    if(customTypeCallBackHandler.isShownInInformationList()) {
                        infoId = InfoUtils.insertNewInfo(title, body, photo, action, intType);
                    }
                }

                if(type.toLowerCase().equals("popup")){
                    InfoPopup.start(context, messageListClass, messageListClassData);
                }

                if(nextIntent != null) {
                    nextIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    nextIntent.putExtra(EXTRA_DATA, messageListClassData);
                    nextIntent.putExtra(EXTRA_INFO_ID, infoId);
                }

                notif = getNotification(context,title, body, photo,
                        nextIntentComponentType,nextIntent,
                        INTENT_COMPONENT_TYPE_SERVICE,null,
                        null, appNameResId, iconResId, null, false,
                        !TextUtils.isEmpty(isHeadsUp) && isHeadsUp.toLowerCase().equals("yes"));
            }
        }

        if(notif != null){
            NotificationManagerCompat nm = NotificationManagerCompat.from(context);
            nm.notify(NotificationUtils.getID(), notif);
        }


        wl.release();
    }


    public interface CustomTypeCallBackHandler {
        Intent handleCustomType();
        int getIntentComponentType();
        boolean isShownInNotification();
        boolean isShownInInformationList();
        int getInformationTypeId();
    }

    public interface CallBackIntentFromNotification {
        void handle(Bundle data, boolean showMessagePage, long infoId);
    }

    public static void showNotification(Context context, String title, String content, Class nextActivity,
                                        HashMap<String, Object> data, int appNameResId, int iconResId, boolean autocancel, boolean isHeadsUp){
        showNotification(context,title,content, null, nextActivity,data,appNameResId,iconResId, NotificationUtils.getID(),null, autocancel, isHeadsUp);
    }

    public static void showNotification(Context context, String title, String content, Class nextActivity,
                                        HashMap<String, Object> data, int appNameResId, int iconResId,int notifID, String pendingIntentAction, boolean autocancel, boolean isHeadsUp){
        showNotification(context,title,content,null, nextActivity,data,appNameResId,iconResId, notifID,pendingIntentAction, autocancel, isHeadsUp);
    }

    public static void showNotification(Context context, String title, String content,  String imageUrl, Class nextActivity,
                                        HashMap<String, Object> data, int appNameResId, int iconResId, int notifID, String pendingIntentAction, boolean autocancel, boolean isHeadsUp){
        Notification notif = getNotification(context,title, content,imageUrl, nextActivity, data, appNameResId, iconResId, pendingIntentAction, autocancel, isHeadsUp);
        NotificationManagerCompat nm = NotificationManagerCompat.from(context);
        nm.notify(notifID, notif);
    }

    public static void showNotification(Context context, String title, String content, String imageUrl,
                                               int nextIntentType, Intent nextIntent,
                                               int deleteIntentType, Intent deleteIntent,
                                               Map<String, Object> data, int appNameResId, int iconResId, int notifID, String pendingIntentAction, boolean autocancel, boolean isHeadsUp){
        Notification notif = getNotification(context, title, content, imageUrl, nextIntentType, nextIntent,
                deleteIntentType, deleteIntent, data, appNameResId, iconResId,
                pendingIntentAction, autocancel, isHeadsUp);
        NotificationManagerCompat nm = NotificationManagerCompat.from(context);
        nm.notify(notifID, notif);
    }

    public static void showNotification(Context context, String title, String content, String imageUrl,
                                               PendingIntent nextPendingIntent,
                                               PendingIntent deletePendingIntent,
                                               PendingIntent fullScreenPendingIntent,
                                               Uri soundUri,
                                               int appNameResId, int iconResId, int notifID,
                                               boolean autocancel, boolean isHeadsUp){
        Notification notif = getNotification(context, title, content, imageUrl,
                nextPendingIntent, deletePendingIntent, fullScreenPendingIntent,
                soundUri, appNameResId, iconResId, autocancel, isHeadsUp);
        NotificationManagerCompat nm = NotificationManagerCompat.from(context);
        nm.notify(notifID, notif);
    }

    public static Notification getNotification(Context context, String title, String content, String imageUrl, Class nextActivity,
                                               Map<String, Object> data, int appNameResId, int iconResId, String pendingIntentAction, boolean autocancel, boolean isHeadsUp){
        Intent nextIntent = null;
        if(nextActivity != null) {
            nextIntent = new Intent(context, nextActivity);
        }
        return getNotification(context,title,content,imageUrl,
                INTENT_COMPONENT_TYPE_ACTIVITY,nextIntent
                ,INTENT_COMPONENT_TYPE_SERVICE,null,data,appNameResId,iconResId,pendingIntentAction,autocancel, isHeadsUp);
    }


    public static final int INTENT_COMPONENT_TYPE_ACTIVITY = 1;
    public static final int INTENT_COMPONENT_TYPE_SERVICE = 2;
    public static final int INTENT_COMPONENT_TYPE_BROADCAST = 3;
    public static final int INTENT_COMPONENT_TYPE_FOREGROUND_SERVICE = 4;

    public static Notification getNotification(Context context, String title, String content, String imageUrl,
                                               int nextIntentType, Intent nextIntent,
                                               int deleteIntentType, Intent deleteIntent,
                                               Map<String, Object> data, int appNameResId, int iconResId, String pendingIntentAction, boolean autocancel, boolean isHeadsUp){
        PendingIntent nextPendingIntent = null;
        if(nextIntent != null) {
            if (data != null) {
                Set<String> keys = data.keySet();
                for (String key : keys) {
                    Object value = data.get(key);
                    if (value instanceof Bundle) {
                        nextIntent.putExtra(key, (Bundle) value);
                    }  else if (value instanceof Boolean) {
                        nextIntent.putExtra(key, (Boolean) value);
                    } else if (value instanceof boolean[]) {
                        nextIntent.putExtra(key, (boolean[]) value);
                    } else if (value instanceof Byte) {
                        nextIntent.putExtra(key, (Byte) value);
                    } else if (value instanceof byte[]) {
                        nextIntent.putExtra(key, (byte[]) value);
                    } else if (value instanceof Character) {
                        nextIntent.putExtra(key, (Character) value);
                    } else if (value instanceof char[]) {
                        nextIntent.putExtra(key, (char[]) value);
                    } else if (value instanceof Double) {
                        nextIntent.putExtra(key, (Double) value);
                    } else if (value instanceof double[]) {
                        nextIntent.putExtra(key, (double[]) value);
                    } else if (value instanceof Float) {
                        nextIntent.putExtra(key, (Float) value);
                    } else if (value instanceof float[]) {
                        nextIntent.putExtra(key, (float[]) value);
                    } else if (value instanceof Integer) {
                        nextIntent.putExtra(key, (Integer) value);
                    } else if (value instanceof int[]) {
                        nextIntent.putExtra(key, (int[]) value);
                    } else if (value instanceof String) {
                        nextIntent.putExtra(key, (String) value);
                    } else if (value instanceof String[]) {
                        nextIntent.putExtra(key, (String[]) value);
                    } else if (value instanceof CharSequence) {
                        nextIntent.putExtra(key, (CharSequence) value);
                    } else if (value instanceof CharSequence[]) {
                        nextIntent.putExtra(key, (CharSequence[]) value);
                    }  else if (value instanceof Long) {
                        nextIntent.putExtra(key, (Long) value);
                    } else if (value instanceof long[]) {
                        nextIntent.putExtra(key, (long[]) value);
                    } else if (value instanceof Short) {
                        nextIntent.putExtra(key, (Short) value);
                    } else if (value instanceof Parcelable) {
                        nextIntent.putExtra(key, (Parcelable) value);
                    } else if (value instanceof Parcelable[]) {
                        nextIntent.putExtra(key, (Parcelable[]) value);
                    } else if (value instanceof short[]) {
                        nextIntent.putExtra(key, (short[]) value);
                    } else if (value instanceof Serializable) {
                        nextIntent.putExtra(key, (Serializable) value);
                    }
                }
            }
            if(TextUtils.isEmpty(pendingIntentAction)) {
                if(TextUtils.isEmpty(nextIntent.getAction())) {
                    nextIntent.setAction("com.zaitunlabs.zlcore.general_reminder_notification" + NotificationUtils.getID());
                }
            } else {
                nextIntent.setAction(pendingIntentAction);
            }

            if(nextIntentType == INTENT_COMPONENT_TYPE_ACTIVITY) {
                nextPendingIntent = PendingIntent.getActivity(context, 131, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            } else if(nextIntentType == INTENT_COMPONENT_TYPE_SERVICE){
                nextPendingIntent = PendingIntent.getService(context, 131, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            } else if(nextIntentType == INTENT_COMPONENT_TYPE_BROADCAST){
                nextPendingIntent = PendingIntent.getBroadcast(context, 131, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            } else if(nextIntentType == INTENT_COMPONENT_TYPE_FOREGROUND_SERVICE){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    nextPendingIntent = PendingIntent.getForegroundService(context, 131, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                }
            }
        }

        PendingIntent deletePendingIntent = null;
        if(deleteIntent != null) {

            if(TextUtils.isEmpty(pendingIntentAction)) {
                if(TextUtils.isEmpty(deleteIntent.getAction())) {
                    deleteIntent.setAction("com.zaitunlabs.zlcore.general_reminder_notification" + "_delete" + NotificationUtils.getID());
                }
            } else {
                deleteIntent.setAction(pendingIntentAction+"_delete");
            }

            if(deleteIntentType == INTENT_COMPONENT_TYPE_ACTIVITY) {
                deletePendingIntent = PendingIntent.getActivity(context, 132, deleteIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            } else if(deleteIntentType == INTENT_COMPONENT_TYPE_SERVICE){
                deletePendingIntent = PendingIntent.getService(context, 132, deleteIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            } else if(deleteIntentType == INTENT_COMPONENT_TYPE_BROADCAST){
                deletePendingIntent = PendingIntent.getBroadcast(context, 132, deleteIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            } else if(deleteIntentType == INTENT_COMPONENT_TYPE_FOREGROUND_SERVICE){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    deletePendingIntent = PendingIntent.getForegroundService(context, 132, deleteIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                }
            }
        }
        return getNotification(context,title,content,imageUrl,nextPendingIntent,deletePendingIntent, null,
                null,appNameResId,iconResId,autocancel,isHeadsUp);
    }


    public static Notification getNotification(Context context, String title, String content, String imageUrl,
                                               PendingIntent nextPendingIntent,
                                               PendingIntent deletePendingIntent,
                                               PendingIntent fullScreenPendingIntent,
                                               Uri soundUri,
                                               int appNameResId, int iconResId,
                                               boolean autocancel, boolean isHeadsUp){
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

        builder.setContentTitle(notifTitle).setContentText(notifText);

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
        builder.setTicker(title);

        builder.setAutoCancel(autocancel)
                .setLights(Color.RED, 1000, 1000)
                .setVibrate(new long[]{0, 400, 250, 400})
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary));

        if(soundUri != null){
            builder.setSound(soundUri);
        } else {
            builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        }

        if(isHeadsUp){
            builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        }

        if(nextPendingIntent != null) {
            builder.setContentIntent(nextPendingIntent);
        }

        if(deletePendingIntent != null){
            builder.setDeleteIntent(deletePendingIntent);
        }

        if(fullScreenPendingIntent != null){
            builder.setFullScreenIntent(fullScreenPendingIntent, true);
        }

        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(),iconResId));
        builder.setSmallIcon(iconResId);


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            notification = builder.build();
        } else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH){
            notification = builder.getNotification();
        }
        return notification;
    }

    public static void handleIntentFromNotification(final Intent intent, CallBackIntentFromNotification callBackIntentFromNotification ){
        //place this in onCreate and onNewIntent
        Bundle extraData = CommonUtils.getBundleIntent(intent, EXTRA_DATA, null);
        final long extraInfoId = CommonUtils.getLongIntent(intent, EXTRA_INFO_ID, -1);
        if(extraData != null) {
            callBackIntentFromNotification.handle(extraData, extraInfoId > -1, extraInfoId);
            if(extraInfoId > -1) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        InfoUtils.scrollInfoList(extraInfoId);
                    }
                }, 200);
            }
        }
    }
}