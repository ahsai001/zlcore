package com.zaitunlabs.zlcore.utils.audio

import java.io.IOException


import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.os.Messenger
import android.os.RemoteException

import com.zaitunlabs.zlcore.utils.CommonUtils
import com.zaitunlabs.zlcore.utils.DebugUtils

import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class AudioService : Service() {
    private var mNotificationManager: NotificationManager? = null
    private var mp: MediaPlayer? = null
    private val mMessenger = Messenger(IncomingMessageHandler())
    private var UImessenger: Messenger? = null


    internal var currentposplay = -1
    internal var sources: Array<String>
    internal var titles: Array<String>? = null
    internal var images: Array<String>? = null

    override fun onConfigurationChanged(newConfig: Configuration) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig)
        DebugUtils.logD("Service", this.javaClass.getSimpleName() + ":onConfigurationChanged")
    }

    override fun onCreate() {
        DebugUtils.logD("Service", this.javaClass.getSimpleName() + ":onCreate")
        super.onCreate()
        if (mp == null) {
            mp = MediaPlayer()
        }
        mp!!.setVolume(volume, volume)
        // showNotification();
        isRunning = true

        EventBus.getDefault().register(this)
    }

    @Subscribe
    fun onEvent(event: AudioVolumeEvent) {
        if (mp != null && mp!!.isPlaying) {
            volume = event.volume
            mp!!.setVolume(volume, volume)
        }
    }

    override fun onDestroy() {
        DebugUtils.logD("Service", this.javaClass.getSimpleName() + ":onDestroy")
        // TODO Auto-generated method stub
        super.onDestroy()
        if (mp != null) {
            if (mp!!.isPlaying) {
                mp!!.stop()
            }
            mp!!.reset()
            mp!!.release()
            mp = null
        }
        // mNotificationManager.cancel(GennieUtils.getIDResource(this, "string",
        // "audioplaying"));
        isRunning = false
        isPlaying = false
        lastAction = ACTION_NONE

        EventBus.getDefault().post(AudioServiceCallBack(-1, -1, null, lastAction))
        EventBus.getDefault().unregister(this)
    }

    override fun onLowMemory() {
        DebugUtils.logD("Service", this.javaClass.getSimpleName() + ":onLowMemory")
        // TODO Auto-generated method stub
        super.onLowMemory()
    }

    override fun onRebind(intent: Intent) {
        DebugUtils.logD("Service", this.javaClass.getSimpleName() + ":onRebind")
        // TODO Auto-generated method stub
        super.onRebind(intent)
    }

    override fun onStart(intent: Intent, startId: Int) {
        DebugUtils.logD("Service", this.javaClass.getSimpleName() + ":onStart")
        // TODO Auto-generated method stub
        super.onStart(intent, startId)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val action = CommonUtils.getIntIntent(intent, STRING_ACTION,
                ACTION_NONE)
        DebugUtils.logD("Service", this.javaClass.getSimpleName() + ":onStartCommand")
        when (action) {
            ACTION_START -> {
                isPlaying = true
                sources = CommonUtils.getArrayStringIntent(intent, STRING_SOURCES, null)
                val position = CommonUtils.getIntIntent(intent, STRING_POSITION, 0)
                currentposplay = position
                playSong(sources[currentposplay])
                lastAction = ACTION_START
            }
            ACTION_PAUSE -> {
                isPlaying = false
                if (mp != null && mp!!.isPlaying)
                    mp!!.pause()
                lastAction = ACTION_PAUSE
                EventBus.getDefault().post(AudioServiceCallBack(currentposplay, sources.size, sources[currentposplay], lastAction))
            }
            ACTION_RESUME -> {
                isPlaying = true
                if (mp != null && !mp!!.isPlaying)
                    mp!!.start()
                lastAction = ACTION_PAUSE
                EventBus.getDefault().post(AudioServiceCallBack(currentposplay, sources.size, sources[currentposplay], lastAction))
            }
            ACTION_STOP -> {
                isPlaying = false
                if (mp != null && mp!!.isPlaying) {
                    // stop
                    mp!!.stop()
                }
                lastAction = ACTION_STOP
                EventBus.getDefault().post(AudioServiceCallBack(currentposplay, sources.size, sources[currentposplay], lastAction))
                mp!!.reset()
                mp!!.release()
                stopSelf()
            }
            else -> {
            }
        }// sendMessageToUI(titles[currentposplay], images[currentposplay]);
        if (mp != null && mp!!.isPlaying) {
            mp!!.setVolume(volume, volume)
        }
        return Service.START_STICKY

    }

    override fun onUnbind(intent: Intent): Boolean {
        DebugUtils.logD("Service", this.javaClass.getSimpleName() + ":onUnbind")
        return super.onUnbind(intent)
    }

    override fun onBind(intent: Intent): IBinder? {
        DebugUtils.logD("Service", this.javaClass.getSimpleName() + ":onBind")
        return mMessenger.binder
    }

    private fun sendMessageToUI(title: String, image: String) {
        val msg = Message.obtain()
        val bdl = Bundle()
        bdl.putString("title", title)
        bdl.putString("image", image)
        msg.what = MSG_SET_DATA_VALUE
        msg.data = bdl
        try {
            if (UImessenger != null)
                UImessenger!!.send(msg)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }

    }

    private inner class IncomingMessageHandler : Handler() { // Handler of
        // incoming messages
        // from clients.
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MSG_REGISTER_CLIENT -> UImessenger = msg.replyTo
                MSG_UNREGISTER_CLIENT -> UImessenger = null
                MSG_SET_DATA_VALUE -> {
                }
                else -> super.handleMessage(msg)
            }
        }
    }

    private fun showNotification() {
        mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val text = getText(CommonUtils.getIDResource(this, "string",
                "audioplaying"))
        val notification = Notification(CommonUtils.getIDResource(
                this, "drawable", "icon"), text, System.currentTimeMillis())
        val contentIntent = PendingIntent.getBroadcast(this, 0,
                Intent(this, StopPlayerReceiver::class.java), 0)
        val title = getText(resources.getIdentifier(
                "app_name", "string", packageName)) as String
        //notification.setLatestEventInfo(this, title, text, contentIntent);
        notification.flags = Notification.FLAG_ONGOING_EVENT
        mNotificationManager!!.notify(
                CommonUtils.getIDResource(this, "string", "audioplaying"),
                notification)
    }

    private fun playSong(songPath: String) {
        mp!!.reset()
        if (songPath.startsWith("http://")) {
            mp!!.setAudioStreamType(AudioManager.STREAM_MUSIC)
            try {
                mp!!.setDataSource(songPath)
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            } catch (e: IllegalStateException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            // Utils.Toast(ctx, "PLAY STREAM bawah");
        } else {
            // Utils.Toast(ctx, "PLAY LOCAL atas");
            // cara 1:play audio from assets
            /*
			 * AssetFileDescriptor descriptor = null; try { descriptor =
			 * getAssets().openFd("audios/"+songPath); } catch (IOException e) {
			 * // TODO Auto-generated catch block e.printStackTrace(); } try {
			 * mp.setDataSource(descriptor.getFileDescriptor(),
			 * descriptor.getStartOffset(), descriptor.getLength() ); } catch
			 * (IllegalArgumentException e) { // TODO Auto-generated catch block
			 * e.printStackTrace(); } catch (IllegalStateException e) { // TODO
			 * Auto-generated catch block e.printStackTrace(); } catch
			 * (IOException e) { // TODO Auto-generated catch block
			 * e.printStackTrace(); } try { descriptor.close(); } catch
			 * (IOException e) { // TODO Auto-generated catch block
			 * e.printStackTrace(); }
			 */

            // cara 2:audio from res/raw
            val uri = Uri.parse("android.resource://" + packageName
                    + "/raw/"
                    + songPath.substring(0, songPath.lastIndexOf('.')))
            try {
                // configdata.mp.setDataSource("android.resource://"+ctx.getPackageName()+"/raw/"+
                // songPath.substring(0, songPath.lastIndexOf('.'));
                mp!!.setDataSource(this, uri)
            } catch (e: IllegalArgumentException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            } catch (e: IllegalStateException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            } catch (e: IOException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }

            // Utils.Toast(ctx, "PLAY LOCAL bawah");
        }
        mp!!.setOnPreparedListener { mp ->
            mp.start()
            EventBus.getDefault().post(AudioServiceCallBack(currentposplay, sources.size, songPath, ACTION_START))
        }
        mp!!.setOnCompletionListener { nextSong() }
        mp!!.prepareAsync()
    }

    private fun nextSong() {
        if (++currentposplay >= sources.size) {
            // Last song, just reset currentPosition
            currentposplay = 0

            stopSelf()
        } else {
            // Play next song
            playSong(sources[currentposplay])
        }
    }

    companion object {
        var isRunning = false
            private set

        val MSG_REGISTER_CLIENT = 1
        val MSG_UNREGISTER_CLIENT = 2
        val MSG_SET_DATA_VALUE = 3
        val MSG_SET_STRING_VALUE = 4

        val ACTION_PAUSE = 1
        val ACTION_RESUME = 2
        val ACTION_START = 3
        val ACTION_STOP = 4
        val ACTION_NONE = 5

        val STRING_ACTION = "action"
        val STRING_POSITION = "position"
        val STRING_SOURCES = "songlist"

        var lastAction = ACTION_NONE
            private set

        var volume = 0.5.toFloat()
            private set
        var isPlaying = false
            private set

        // play,pause,resume and stop
        fun startAudioSound(ctx: Context, sources: Array<String>) {
            // start service to play app backsound
            val `in` = Intent(ctx, AudioService::class.java)
            `in`.putExtra(AudioService.STRING_ACTION, AudioService.ACTION_START)
            `in`.putExtra(AudioService.STRING_POSITION, 0)
            `in`.putExtra(AudioService.STRING_SOURCES, sources)
            ctx.startService(`in`)
            DebugUtils.logD("AudioService", ctx.javaClass.getSimpleName() + ":startAudioSound")
        }

        fun pauseAudioSound(ctx: Context) {
            if (AudioService.isRunning) {
                val `in` = Intent(ctx, AudioService::class.java)
                `in`.putExtra(AudioService.STRING_ACTION, AudioService.ACTION_PAUSE)
                ctx.startService(`in`)
                DebugUtils.logD("AudioService", ctx.javaClass.getSimpleName() + ":pauseAudioSound")
            }
        }

        fun resumeAudioSound(ctx: Context) {
            if (AudioService.isRunning) {
                val `in` = Intent(ctx, AudioService::class.java)
                `in`.putExtra(AudioService.STRING_ACTION, AudioService.ACTION_RESUME)
                ctx.startService(`in`)
                DebugUtils.logD("AudioService", ctx.javaClass.getSimpleName() + ":resumeAudioSound")
            }
        }

        fun stopAudioSound(ctx: Context) {
            // stop service that playing app backsound
            if (AudioService.isRunning) {
                val `in` = Intent(ctx, AudioService::class.java)
                ctx.stopService(`in`)
                DebugUtils.logD("AudioService", ctx.javaClass.getSimpleName() + ":stopAudioSound stopService")
            }
        }
    }
}
