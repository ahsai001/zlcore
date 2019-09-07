package com.zaitunlabs.zlcore.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Parcelable
import android.os.PowerManager
import android.text.TextUtils
import android.webkit.URLUtil

import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

import com.zaitunlabs.zlcore.R
import com.zaitunlabs.zlcore.activities.InfoPopup
import com.zaitunlabs.zlcore.activities.MessageListActivity
import com.zaitunlabs.zlcore.activities.ReminderPopup
import com.zaitunlabs.zlcore.core.WebViewActivity

import java.io.Serializable
import java.util.HashMap

import com.zaitunlabs.zlcore.activities.InfoPopup.EXTRA_DATA
import com.zaitunlabs.zlcore.activities.InfoPopup.EXTRA_INFO_ID
import com.zaitunlabs.zlcore.core.WebViewActivity.PARAM_IS_MEID


/**
 * Created by ahmad s on 2/24/2016.
 */
object NotificationUtils {

    val id: Int
        get() {
            synchronized(NotificationUtils::class.java) {
                return IntegerIDUtils.id
            }
        }


    val INTENT_COMPONENT_TYPE_ACTIVITY = 1
    val INTENT_COMPONENT_TYPE_SERVICE = 2
    val INTENT_COMPONENT_TYPE_BROADCAST = 3
    val INTENT_COMPONENT_TYPE_FOREGROUND_SERVICE = 4


    fun onMessageReceived(context: Context, data: Map<String, Any>, notifTitle: String?, notifBody: String?,
                          homePageClass: Class<*>, messageListClass: Class<*>?, messageListClassData: Bundle?,
                          appNameResId: Int, iconResId: Int,
                          customTypeCallBackList: Map<String, CustomTypeCallBackHandler>?, isLoggedIn: Boolean) {
        var messageListClass = messageListClass
        var messageListClassData = messageListClassData
        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "zlcore:smartFirebaseMessagingServiceTAG")

        wl.acquire()

        var notif: Notification? = null
        var infoId: Long = -1

        if (notifTitle != null || notifBody != null) {
            //notification
            notif = getNotification(context, notifTitle, notifBody, null, homePageClass, data, appNameResId, iconResId, null, true, false)
        } else {
            //data wajib fcm adalah type and needlogin
            val needlogin = data["needlogin"] as String? //need login to notif, remind and saved, yes or no
            if (!TextUtils.isEmpty(needlogin) && needlogin!!.toLowerCase() == "yes" && !isLoggedIn) return

            val type = data["type"] as String?
            //predefined : 1. notif | 2. notif+ | 3. popup ==> notif+popup | 4. wakeup | 5. reminder
            //notif+ dan popup harus ada messagelistactivity

            //data custom fcm
            val title = data["title"] as String?
            val body = data["body"] as String?
            val photo = data["photo"] as String? //photo url

            var action: String = data["action"] as String? //url or full path class
            val isHeadsUp = data["headsup"] as String? //yes or no

            var customTypeCallBackHandler: CustomTypeCallBackHandler? = null
            if (customTypeCallBackList != null && !TextUtils.isEmpty(type) && customTypeCallBackList.containsKey(type!!.toLowerCase())) {
                customTypeCallBackHandler = customTypeCallBackList[type]
            }

            if (type!!.toLowerCase() == "wakeup") {
                //do nothing, just to wakeup this app,
            } else if (type.toLowerCase() == "reminder") {
                //just show reminder, not notif, not popup, and not saved
                ReminderPopup.start(context, title, body)
            } else if (customTypeCallBackHandler != null && !customTypeCallBackHandler.isShownInNotifCenter(data)) {
                //custom handle without notification
                customTypeCallBackHandler.handleCustom(data)
            } else {
                //handle with notification
                var intType = 1
                var nextIntent: Intent? = null
                var nextIntentComponentType = INTENT_COMPONENT_TYPE_ACTIVITY

                if (messageListClass == null) {
                    messageListClass = MessageListActivity::class.java
                }

                if (messageListClassData == null) {
                    messageListClassData = Bundle()
                }

                if (type.toLowerCase() == "notif") {
                    intType = 1
                    if (!TextUtils.isEmpty(action)) {
                        if (URLUtil.isValidUrl(action)) {
                            //open browser
                            val i = Intent(Intent.ACTION_VIEW)
                            i.data = Uri.parse(action)
                            nextIntent = Intent.createChooser(i, "open link with :").setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        } else if (action.startsWith("webview://")) {
                            nextIntent = Intent(context.applicationContext, WebViewActivity::class.java)
                            var htmlContent = action.replace("webview://", "")
                            if (htmlContent.startsWith("base64/")) {
                                htmlContent = htmlContent.replace("base64/", "")
                                htmlContent = CommonUtils.decodeBase64(htmlContent)
                            }
                            nextIntent.putExtra(WebViewActivity.PARAM_URL, htmlContent)
                            nextIntent.putExtra(WebViewActivity.PARAM_PAGE_TAG, "webviewNotif")
                            nextIntent.putExtra(PARAM_IS_MEID, false)
                        } else {
                            //may be this is class name
                            try {
                                if (action.startsWith("activity://")) {
                                    nextIntentComponentType = INTENT_COMPONENT_TYPE_ACTIVITY
                                    action = action.replace("activity://", "")
                                } else if (action.startsWith("service://")) {
                                    nextIntentComponentType = INTENT_COMPONENT_TYPE_SERVICE
                                    action = action.replace("service://", "")
                                } else if (action.startsWith("receiver://")) {
                                    nextIntentComponentType = INTENT_COMPONENT_TYPE_BROADCAST
                                    action = action.replace("receiver://", "")
                                } else if (action.startsWith("foreground-service://")) {
                                    nextIntentComponentType = INTENT_COMPONENT_TYPE_FOREGROUND_SERVICE
                                    action = action.replace("foreground-service://", "")
                                }
                                val nextClass = Class.forName(action)
                                nextIntent = Intent(context.applicationContext, nextClass)
                            } catch (e: ClassNotFoundException) {
                                nextIntent = Intent(context.applicationContext, homePageClass)
                            }

                            val uri = Uri.parse(action)
                            val keys = uri.queryParameterNames
                            for (key in keys) {
                                val value = uri.getQueryParameter(key)
                                nextIntent!!.putExtra(key, value)
                            }
                        }
                    } else {
                        nextIntent = Intent(context.applicationContext, homePageClass)
                    }
                } else if (type.toLowerCase() == "notif+") {
                    intType = 2
                    nextIntent = Intent(context.applicationContext, messageListClass)
                    infoId = InfoUtils.insertNewInfo(title, body, photo, action, intType)
                } else if (type.toLowerCase() == "popup") {
                    intType = 3
                    nextIntent = Intent(context.applicationContext, messageListClass)
                    infoId = InfoUtils.insertNewInfo(title, body, photo, action, intType)
                } else if (customTypeCallBackHandler != null && customTypeCallBackHandler.isShownInNotifCenter(data)) {
                    customTypeCallBackHandler.handleCustom(data)
                    intType = customTypeCallBackHandler.getTypeId(data)

                    if (customTypeCallBackHandler.isShownInInfoList(data)) {
                        nextIntent = Intent(context.applicationContext, messageListClass)
                        infoId = InfoUtils.insertNewInfo(customTypeCallBackHandler.getTitle(data),
                                customTypeCallBackHandler.getBody(data),
                                customTypeCallBackHandler.getPhotoUrl(data),
                                customTypeCallBackHandler.getInfoUrl(data),
                                intType)
                    } else {
                        nextIntentComponentType = customTypeCallBackHandler.getNextIntentComponentType(data)
                        nextIntent = customTypeCallBackHandler.getNextIntent(data)
                    }
                }

                if (type.toLowerCase() == "popup") {
                    InfoPopup.start(context, messageListClass, messageListClassData)
                }

                if (nextIntent != null) {
                    nextIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    nextIntent.putExtra(EXTRA_DATA, messageListClassData)
                    nextIntent.putExtra(EXTRA_INFO_ID, infoId)
                }

                notif = getNotification(context, title, body, photo,
                        nextIntentComponentType, nextIntent,
                        INTENT_COMPONENT_TYPE_SERVICE, null, null, appNameResId, iconResId, null, false,
                        !TextUtils.isEmpty(isHeadsUp) && isHeadsUp!!.toLowerCase() == "yes")
            }
        }

        if (notif != null) {
            val nm = NotificationManagerCompat.from(context)
            nm.notify(NotificationUtils.id, notif)
        }


        wl.release()
    }


    interface CustomTypeCallBackHandler {
        fun handleCustom(data: Map<String, Any>)

        fun isShownInNotifCenter(data: Map<String, Any>): Boolean
        fun getNextIntent(data: Map<String, Any>): Intent
        fun getNextIntentComponentType(data: Map<String, Any>): Int

        fun isShownInInfoList(data: Map<String, Any>): Boolean
        fun getTitle(data: Map<String, Any>): String
        fun getBody(data: Map<String, Any>): String
        fun getPhotoUrl(data: Map<String, Any>): String
        fun getInfoUrl(data: Map<String, Any>): String
        fun getTypeId(data: Map<String, Any>): Int
    }

    interface CallBackIntentFromNotification {
        fun handle(data: Bundle, showMessagePage: Boolean, infoId: Long)
    }

    fun showNotification(context: Context, title: String, content: String, nextActivity: Class<*>,
                         data: HashMap<String, Any>, appNameResId: Int, iconResId: Int, autocancel: Boolean, isHeadsUp: Boolean) {
        showNotification(context, title, content, null, nextActivity, data, appNameResId, iconResId, NotificationUtils.id, null, autocancel, isHeadsUp)
    }

    fun showNotification(context: Context, title: String, content: String, nextActivity: Class<*>,
                         data: HashMap<String, Any>, appNameResId: Int, iconResId: Int, notifID: Int, pendingIntentAction: String, autocancel: Boolean, isHeadsUp: Boolean) {
        showNotification(context, title, content, null, nextActivity, data, appNameResId, iconResId, notifID, pendingIntentAction, autocancel, isHeadsUp)
    }

    fun showNotification(context: Context, title: String, content: String, imageUrl: String?, nextActivity: Class<*>,
                         data: HashMap<String, Any>, appNameResId: Int, iconResId: Int, notifID: Int, pendingIntentAction: String?, autocancel: Boolean, isHeadsUp: Boolean) {
        val notif = getNotification(context, title, content, imageUrl, nextActivity, data, appNameResId, iconResId, pendingIntentAction, autocancel, isHeadsUp)
        val nm = NotificationManagerCompat.from(context)
        nm.notify(notifID, notif!!)
    }

    fun showNotification(context: Context, title: String, content: String, imageUrl: String,
                         nextIntentType: Int, nextIntent: Intent,
                         deleteIntentType: Int, deleteIntent: Intent,
                         data: Map<String, Any>, appNameResId: Int, iconResId: Int, notifID: Int, pendingIntentAction: String, autocancel: Boolean, isHeadsUp: Boolean) {
        val notif = getNotification(context, title, content, imageUrl, nextIntentType, nextIntent,
                deleteIntentType, deleteIntent, data, appNameResId, iconResId,
                pendingIntentAction, autocancel, isHeadsUp)
        val nm = NotificationManagerCompat.from(context)
        nm.notify(notifID, notif!!)
    }

    fun showNotification(context: Context, title: String, content: String, imageUrl: String,
                         nextPendingIntent: PendingIntent,
                         deletePendingIntent: PendingIntent,
                         fullScreenPendingIntent: PendingIntent,
                         soundUri: Uri,
                         appNameResId: Int, iconResId: Int, notifID: Int,
                         autocancel: Boolean, isHeadsUp: Boolean) {
        val notif = getNotification(context, title, content, imageUrl,
                nextPendingIntent, deletePendingIntent, fullScreenPendingIntent,
                soundUri, appNameResId, iconResId, autocancel, isHeadsUp)
        val nm = NotificationManagerCompat.from(context)
        nm.notify(notifID, notif!!)
    }

    fun getNotification(context: Context, title: String?, content: String, imageUrl: String?, nextActivity: Class<*>?,
                        data: Map<String, Any>, appNameResId: Int, iconResId: Int, pendingIntentAction: String?, autocancel: Boolean, isHeadsUp: Boolean): Notification? {
        var nextIntent: Intent? = null
        if (nextActivity != null) {
            nextIntent = Intent(context, nextActivity)
        }
        return getNotification(context, title, content, imageUrl,
                INTENT_COMPONENT_TYPE_ACTIVITY, nextIntent, INTENT_COMPONENT_TYPE_SERVICE, null, data, appNameResId, iconResId, pendingIntentAction, autocancel, isHeadsUp)
    }

    fun getNotification(context: Context, title: String?, content: String, imageUrl: String?,
                        nextIntentType: Int, nextIntent: Intent?,
                        deleteIntentType: Int, deleteIntent: Intent?,
                        data: Map<String, Any>?, appNameResId: Int, iconResId: Int, pendingIntentAction: String?, autocancel: Boolean, isHeadsUp: Boolean): Notification? {
        var nextPendingIntent: PendingIntent? = null
        if (nextIntent != null) {
            if (data != null) {
                val keys = data.keys
                for (key in keys) {
                    val value = data[key]
                    if (value is Bundle) {
                        nextIntent.putExtra(key, value as Bundle?)
                    } else if (value is Boolean) {
                        nextIntent.putExtra(key, value as Boolean?)
                    } else if (value is BooleanArray) {
                        nextIntent.putExtra(key, value as BooleanArray?)
                    } else if (value is Byte) {
                        nextIntent.putExtra(key, value as Byte?)
                    } else if (value is ByteArray) {
                        nextIntent.putExtra(key, value as ByteArray?)
                    } else if (value is Char) {
                        nextIntent.putExtra(key, value as Char?)
                    } else if (value is CharArray) {
                        nextIntent.putExtra(key, value as CharArray?)
                    } else if (value is Double) {
                        nextIntent.putExtra(key, value as Double?)
                    } else if (value is DoubleArray) {
                        nextIntent.putExtra(key, value as DoubleArray?)
                    } else if (value is Float) {
                        nextIntent.putExtra(key, value as Float?)
                    } else if (value is FloatArray) {
                        nextIntent.putExtra(key, value as FloatArray?)
                    } else if (value is Int) {
                        nextIntent.putExtra(key, value as Int?)
                    } else if (value is IntArray) {
                        nextIntent.putExtra(key, value as IntArray?)
                    } else if (value is String) {
                        nextIntent.putExtra(key, value as String?)
                    } else if (value is Array<String>) {
                        nextIntent.putExtra(key, value as Array<String>?)
                    } else if (value is CharSequence) {
                        nextIntent.putExtra(key, value as CharSequence?)
                    } else if (value is Array<CharSequence>) {
                        nextIntent.putExtra(key, value as Array<CharSequence>?)
                    } else if (value is Long) {
                        nextIntent.putExtra(key, value as Long?)
                    } else if (value is LongArray) {
                        nextIntent.putExtra(key, value as LongArray?)
                    } else if (value is Short) {
                        nextIntent.putExtra(key, value as Short?)
                    } else if (value is Parcelable) {
                        nextIntent.putExtra(key, value as Parcelable?)
                    } else if (value is Array<Parcelable>) {
                        nextIntent.putExtra(key, value as Array<Parcelable>?)
                    } else if (value is ShortArray) {
                        nextIntent.putExtra(key, value as ShortArray?)
                    } else if (value is Serializable) {
                        nextIntent.putExtra(key, value as Serializable?)
                    }
                }
            }
            if (TextUtils.isEmpty(pendingIntentAction)) {
                if (TextUtils.isEmpty(nextIntent.action)) {
                    nextIntent.action = "com.zaitunlabs.zlcore.general_reminder_notification" + NotificationUtils.id
                }
            } else {
                nextIntent.action = pendingIntentAction
            }

            if (nextIntentType == INTENT_COMPONENT_TYPE_ACTIVITY) {
                nextPendingIntent = PendingIntent.getActivity(context, 131, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            } else if (nextIntentType == INTENT_COMPONENT_TYPE_SERVICE) {
                nextPendingIntent = PendingIntent.getService(context, 131, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            } else if (nextIntentType == INTENT_COMPONENT_TYPE_BROADCAST) {
                nextPendingIntent = PendingIntent.getBroadcast(context, 131, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            } else if (nextIntentType == INTENT_COMPONENT_TYPE_FOREGROUND_SERVICE) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    nextPendingIntent = PendingIntent.getForegroundService(context, 131, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                }
            }
        }

        var deletePendingIntent: PendingIntent? = null
        if (deleteIntent != null) {

            if (TextUtils.isEmpty(pendingIntentAction)) {
                if (TextUtils.isEmpty(deleteIntent.action)) {
                    deleteIntent.action = "com.zaitunlabs.zlcore.general_reminder_notification" + "_delete" + NotificationUtils.id
                }
            } else {
                deleteIntent.action = pendingIntentAction!! + "_delete"
            }

            if (deleteIntentType == INTENT_COMPONENT_TYPE_ACTIVITY) {
                deletePendingIntent = PendingIntent.getActivity(context, 132, deleteIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            } else if (deleteIntentType == INTENT_COMPONENT_TYPE_SERVICE) {
                deletePendingIntent = PendingIntent.getService(context, 132, deleteIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            } else if (deleteIntentType == INTENT_COMPONENT_TYPE_BROADCAST) {
                deletePendingIntent = PendingIntent.getBroadcast(context, 132, deleteIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            } else if (deleteIntentType == INTENT_COMPONENT_TYPE_FOREGROUND_SERVICE) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    deletePendingIntent = PendingIntent.getForegroundService(context, 132, deleteIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                }
            }
        }
        return getNotification(context, title, content, imageUrl, nextPendingIntent, deletePendingIntent, null, null, appNameResId, iconResId, autocancel, isHeadsUp)
    }


    fun getNotification(context: Context, title: String?, content: String, imageUrl: String?,
                        nextPendingIntent: PendingIntent?,
                        deletePendingIntent: PendingIntent?,
                        fullScreenPendingIntent: PendingIntent?,
                        soundUri: Uri?,
                        appNameResId: Int, iconResId: Int,
                        autocancel: Boolean, isHeadsUp: Boolean): Notification? {
        var notification: Notification? = null

        var iconBitMap: Bitmap? = null
        if (!TextUtils.isEmpty(imageUrl)) {
            iconBitMap = CommonUtils.getBitmapFromURL(imageUrl)
        }

        val channelID = CommonUtils.getPackageName(context)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.app_name)
            val description = context.getString(R.string.app_name)
            val importance = NotificationManager.IMPORTANCE_HIGH //Important for heads-up notification
            val channel = NotificationChannel(channelID, name, importance)
            channel.description = description
            channel.setShowBadge(true)
            channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            val notificationManager = context.getSystemService<NotificationManager>(NotificationManager::class.java!!)
            notificationManager.createNotificationChannel(channel)
        }


        val notifTitle = if (TextUtils.isEmpty(title)) context.getString(appNameResId) else title
        val notifText = if (TextUtils.isEmpty(content)) context.getString(appNameResId) else content
        val builder = NotificationCompat.Builder(context, channelID)

        builder.setContentTitle(notifTitle).setContentText(notifText)

        if (iconBitMap != null) {
            val bigPictureStyle = NotificationCompat.BigPictureStyle()
            bigPictureStyle.setBigContentTitle(title)
            bigPictureStyle.setSummaryText(CommonUtils.fromHtml(notifText).toString())
            bigPictureStyle.bigPicture(iconBitMap)
            builder.setStyle(bigPictureStyle)
        } else {
            /*
            NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
            inboxStyle.addLine(notifText);
            inboxStyle.setBigContentTitle(title);*/
            builder.setStyle(NotificationCompat.BigTextStyle().bigText(notifText).setBigContentTitle(title))
        }

        builder.setSubText(context.getString(R.string.app_name))
        builder.setTicker(title)

        builder.setAutoCancel(autocancel)
                .setLights(Color.RED, 1000, 1000)
                .setVibrate(longArrayOf(0, 400, 250, 400)).color = ContextCompat.getColor(context, R.color.colorPrimary)

        if (soundUri != null) {
            builder.setSound(soundUri)
        } else {
            builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
        }

        if (isHeadsUp) {
            builder.priority = NotificationCompat.PRIORITY_HIGH
        }

        if (nextPendingIntent != null) {
            builder.setContentIntent(nextPendingIntent)
        }

        if (deletePendingIntent != null) {
            builder.setDeleteIntent(deletePendingIntent)
        }

        if (fullScreenPendingIntent != null) {
            builder.setFullScreenIntent(fullScreenPendingIntent, true)
        }

        builder.setLargeIcon(BitmapFactory.decodeResource(context.resources, iconResId))
        builder.setSmallIcon(iconResId)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            notification = builder.build()
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            notification = builder.notification
        }
        return notification
    }

    fun handleIntentFromNotification(intent: Intent, callBackIntentFromNotification: CallBackIntentFromNotification) {
        //place this in onCreate and onNewIntent
        val extraData = CommonUtils.getBundleIntent(intent, EXTRA_DATA, null)
        val extraInfoId = CommonUtils.getLongIntent(intent, EXTRA_INFO_ID, -1)
        if (extraData != null) {
            callBackIntentFromNotification.handle(extraData, extraInfoId > -1, extraInfoId)
            if (extraInfoId > -1) {
                Handler().postDelayed({ InfoUtils.scrollInfoList(extraInfoId) }, 200)
            }
        }
    }
}