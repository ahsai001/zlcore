package com.zaitunlabs.zlcore.utils.audio;

import java.io.IOException;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.zaitunlabs.zlcore.utils.CommonUtil;
import com.zaitunlabs.zlcore.utils.DebugUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class BackSoundService extends Service {
	private NotificationManager mNotificationManager;
	private static boolean isRunning = false;
	private MediaPlayer mp = null;
	private Messenger mMessenger = new Messenger(new IncomingMessageHandler());
	private Messenger UImessenger = null;

	public static final int MSG_REGISTER_CLIENT = 1;
	public static final int MSG_UNREGISTER_CLIENT = 2;
	public static final int MSG_SET_DATA_VALUE = 3;
	public static final int MSG_SET_STRING_VALUE = 4;

	public static final int ACTION_PAUSE = 1;
	public static final int ACTION_RESUME = 2;
	public static final int ACTION_START = 3;
	public static final int ACTION_STOP = 4;
	public static final int ACTION_NONE = 5;

	public static final String STRING_ACTION = "action";
	public static final String STRING_POSITION = "position";
	public static final String STRING_SOURCES = "songlist";
	
	private static int last_action = ACTION_NONE;

	private static float volume = (float) 0.0;
	
	int currentposplay = -1;
	private static boolean isPlaying = false;
	private static String[] sources = null;
	String[] titles = null;
	String[] images = null;
	private boolean isPlaylistRepeat = true; //for backsound default is true
	
	public static int getLastAction() {
		return last_action;
	}

	public static float getVolume() {
		return volume;
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
		DebugUtil.logD("BackSoundService", this.getClass().getSimpleName()
				+ ":onConfigurationChanged");
	}

	@Override
	public void onCreate() {
		DebugUtil.logD("BackSoundService", this.getClass().getSimpleName()
				+ ":onCreate");
		super.onCreate();
		if (mp == null) {
			mp = new MediaPlayer();
		}
		mp.setVolume(volume, volume);
		// showNotification();
		isRunning = true;
		isPlaying = false;
		last_action = ACTION_NONE;
		
		
		EventBus.getDefault().register(this);
	}

	@Subscribe
	public void onEvent(BackSoundVolumeEvent event){
		if (mp != null && mp.isPlaying()) {
			volume = event.getVolume();
			mp.setVolume(volume, volume);
		}
	}

	@Override
	public void onDestroy() {
		DebugUtil.logD("BackSoundService", this.getClass().getSimpleName()
				+ ":onDestroy");
		// TODO Auto-generated method stub
		super.onDestroy();
		if (mp != null) {
			if (mp.isPlaying()) {
				mp.stop();
			}
			mp.reset();
			mp.release();
			mp = null;
		}
		// mNotificationManager.cancel(GennieUtils.getIDResource(this, "string",
		// "audioplaying"));
		isRunning = false;
		isPlaying = false;
		last_action = ACTION_NONE;
		
		EventBus.getDefault().post(new BackSoundCallBack(-1,-1,null, last_action));
		EventBus.getDefault().unregister(this);
	}

	@Override
	public void onLowMemory() {
		DebugUtil.logD("BackSoundService", this.getClass().getSimpleName()
				+ ":onLowMemory");
		// TODO Auto-generated method stub
		super.onLowMemory();
	}

	@Override
	public void onRebind(Intent intent) {
		DebugUtil.logD("BackSoundService", this.getClass().getSimpleName()
				+ ":onRebind");
		// TODO Auto-generated method stub
		super.onRebind(intent);
	}

	@Override
	public void onStart(Intent intent, int startId) {
		DebugUtil.logD("BackSoundService", this.getClass().getSimpleName()
				+ ":onStart");
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		int action = CommonUtil.getIntIntent(intent, STRING_ACTION,
				ACTION_NONE);
		DebugUtil.logD("BackSoundService", this.getClass().getSimpleName()
				+ ":onStartCommand");



			switch (action) {
				case ACTION_START:
					isPlaying = true;
					sources = CommonUtil.getArrayStringIntent(intent, STRING_SOURCES, null);
					int position = CommonUtil.getIntIntent(intent, STRING_POSITION, 0);
					currentposplay = position;
					playSong(sources[currentposplay]);
					// sendMessageToUI(titles[currentposplay], images[currentposplay]);
					last_action = ACTION_START;
					break;
				case ACTION_PAUSE:
					isPlaying = false;
					if (mp != null && mp.isPlaying())
						mp.pause();
					last_action = ACTION_PAUSE;
					if(sources != null) {
						EventBus.getDefault().post(new BackSoundCallBack(currentposplay, sources.length, sources[currentposplay], last_action));
					}
					break;
				case ACTION_RESUME:
					isPlaying = true;
					if (last_action == ACTION_NONE && sources != null) {
						currentposplay = 0;
						playSong(sources[currentposplay]);
						last_action = ACTION_START;
					} else if (mp != null && !mp.isPlaying() && sources != null) {
						mp.start();
						last_action = ACTION_RESUME;
						EventBus.getDefault().post(new BackSoundCallBack(currentposplay, sources.length, sources[currentposplay], last_action));
					}
					break;
				case ACTION_STOP:
					isPlaying = false;
					if (mp != null && mp.isPlaying()) {
						// stop
						mp.stop();
					}
					last_action = ACTION_STOP;
					if(sources != null) {
						EventBus.getDefault().post(new BackSoundCallBack(currentposplay, sources.length, sources[currentposplay], last_action));
					}
					mp.reset();
					mp.release();
					stopSelf();
					break;
				default:
					break;
			}

		if (mp != null && mp.isPlaying()) {
			mp.setVolume(volume, volume);
		}
		return START_STICKY;

	}

	@Override
	public boolean onUnbind(Intent intent) {
		DebugUtil.logD("BackSoundService", this.getClass().getSimpleName()
				+ ":onUnbind");
		return super.onUnbind(intent);
	}

	@Override
	public IBinder onBind(Intent intent) {
		DebugUtil.logD("BackSoundService", this.getClass().getSimpleName()
				+ ":onBind");
		return mMessenger.getBinder();
	}

	public static boolean isRunning() {
		return isRunning;
	}

	public static boolean isPlaying() {
		return isPlaying;
	}

	private void sendMessageToUI(String title, String image) {
		Message msg = Message.obtain();
		Bundle bdl = new Bundle();
		bdl.putString("title", title);
		bdl.putString("image", image);
		msg.what = MSG_SET_DATA_VALUE;
		msg.setData(bdl);
		try {
			if (UImessenger != null)
				UImessenger.send(msg);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	private class IncomingMessageHandler extends Handler { // Handler of
															// incoming messages
															// from clients.
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_REGISTER_CLIENT:
				UImessenger = msg.replyTo;
				break;
			case MSG_UNREGISTER_CLIENT:
				UImessenger = null;
				break;
			case MSG_SET_DATA_VALUE:
				break;
			default:
				super.handleMessage(msg);
			}
		}
	}

	private void showNotification() {
		mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		CharSequence text = getText(CommonUtil.getIDResource(this, "string",
				"audioplaying"));
		Notification notification = new Notification(CommonUtil.getIDResource(
				this, "drawable", "icon"), text, System.currentTimeMillis());
		PendingIntent contentIntent = PendingIntent.getBroadcast(this, 0,
				new Intent(this, StopPlayerReceiver.class), 0);
		String title = (String) getText(getResources().getIdentifier(
				"app_name", "string", getPackageName()));
		//notification.setLatestEventInfo(this, title, text, contentIntent);
		notification.flags = Notification.FLAG_ONGOING_EVENT;
		mNotificationManager.notify(
				CommonUtil.getIDResource(this, "string", "audioplaying"),
				notification);
	}

	private void playSong(final String songPath) {
		mp.reset();
		if (songPath.startsWith("http://")) {
			mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
			try {
				mp.setDataSource(songPath);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
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
			Uri uri = Uri.parse("android.resource://" + getPackageName()
					+ "/raw/"
					+ songPath.substring(0, songPath.lastIndexOf('.')));
			try {
				// configdata.mp.setDataSource("android.resource://"+ctx.getPackageName()+"/raw/"+
				// songPath.substring(0, songPath.lastIndexOf('.'));
				mp.setDataSource(this, uri);
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// Utils.Toast(ctx, "PLAY LOCAL bawah");
		}
		mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer mp) {
				mp.start();
				EventBus.getDefault().post(new BackSoundCallBack(currentposplay,sources.length,songPath, ACTION_START));
			}
		});
		mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				nextSong();
			}
		});
		mp.prepareAsync();
	}

	private void nextSong() {
		if (++currentposplay >= sources.length) {
			// Last song, just reset currentPosition
			currentposplay = 0;
			if(isPlaylistRepeat){
				// repeat from beginning
				playSong(sources[currentposplay]);
			}else{
				stopSelf();
			}
		} else {
			// Play next song
			playSong(sources[currentposplay]);
		}
	}

	// play,pause,resume and stop
	public static void startBackSound(Context ctx, String[] sources) {
		// start service to play app backsound
		Intent in = new Intent(ctx, BackSoundService.class);
		in.putExtra(BackSoundService.STRING_ACTION,
				BackSoundService.ACTION_START);
		in.putExtra(BackSoundService.STRING_POSITION, 0);
		in.putExtra(BackSoundService.STRING_SOURCES, sources);
		ctx.startService(in);
		DebugUtil.logD("BackSoundService", ctx.getClass().getSimpleName()
				+ ":startBackSound");
	}

	public static void pauseBackSound(Context ctx) {
		if (BackSoundService.isRunning()) {
			Intent in = new Intent(ctx, BackSoundService.class);
			in.putExtra(BackSoundService.STRING_ACTION,
					BackSoundService.ACTION_PAUSE);
			ctx.startService(in);
			DebugUtil.logD("BackSoundService", ctx.getClass().getSimpleName()
					+ ":pauseBackSound");
		}
	}

	public static void resumeBackSound(Context ctx) {
		if (BackSoundService.isRunning()) {
			Intent in = new Intent(ctx, BackSoundService.class);
			in.putExtra(BackSoundService.STRING_ACTION,
					BackSoundService.ACTION_RESUME);
			ctx.startService(in);
			DebugUtil.logD("BackSoundService", ctx.getClass().getSimpleName()
					+ ":resumeBackSound");
		}
	}

	public static void stopBackSound(Context ctx) {
		// stop service that playing app backsound
		if (BackSoundService.isRunning()) {
			Intent in = new Intent(ctx, BackSoundService.class);
			ctx.stopService(in);
			DebugUtil.logD("BackSoundService", ctx.getClass().getSimpleName()
					+ ":stopBackSound stopService");
		}
	}
}
