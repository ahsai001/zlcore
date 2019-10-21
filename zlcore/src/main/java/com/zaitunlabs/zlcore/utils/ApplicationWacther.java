package com.zaitunlabs.zlcore.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;

public class ApplicationWacther {
	Stack<Activity> activityStack = null;
	Context appContext = null;
	Activity lastActivityResumed = null;
	boolean isApplicationVisible = true; // initial value is true
	HashMap<Object, AppWatcherListener> listeners = null;
	private boolean isConfigurationChanged = false;
	
	public synchronized boolean isApplicationVisible() {
		return isApplicationVisible;
	}

	public synchronized Activity getLastActivityStarted() {
		return lastActivityResumed;
	}

	public synchronized void setLastActivityResumed(Activity lastActivityResumed) {
		this.lastActivityResumed = lastActivityResumed;
	}

	private static ApplicationWacther instance = null;

	private ApplicationWacther() {
		activityStack = new Stack<Activity>();
		listeners = new HashMap<Object, ApplicationWacther.AppWatcherListener>();
	}

	public static synchronized ApplicationWacther getInstance(Context appContext) {
		if (instance == null) {
			instance = new ApplicationWacther();
			instance.appContext = appContext.getApplicationContext();
		}
		return instance;
	}

	public static synchronized ApplicationWacther initialize(Context appContext) {
		return getInstance(appContext);
	}

	private synchronized int pushActivity(Activity act) {
		activityStack.push(act);
		return activityStack.size();
	}

	private synchronized Activity popActivity() {
		return (Activity) activityStack.pop();
	}

	private synchronized int activityCounts() {
		return activityStack.size();
	}

	private synchronized int removeActivity(Activity act) {
		activityStack.remove(act);
		return activityStack.size();
	}
	
	public synchronized void setConfigurationChanged(boolean changed){
		this.isConfigurationChanged = changed;
	}

	public synchronized int registerActivity(Activity act) {
		synchronized (this) {
			
		}
		return pushActivity(act);
	}

	public synchronized int unregisterActivity(Activity act) {
		synchronized (this) {
			if (activityCounts() <= 1 && !isConfigurationChanged) {
				// notify that no activity exist in application now
				Iterator<AppWatcherListener> iterator = listeners.values().iterator();
				while (iterator.hasNext()) {
					AppWatcherListener listener = iterator.next();
					listener.noActivityExistInApp();
				}
			}
		}
		return removeActivity(act);
	}

	public synchronized void reportActivityStopEvent(Activity stopAct) {
		synchronized (this) {
			if (lastActivityResumed == stopAct) {
				isApplicationVisible = false;
				// notify that application invisible now
				Iterator<AppWatcherListener> iterator = listeners.values()
						.iterator();
				while (iterator.hasNext()) {
					AppWatcherListener listener = iterator.next();
					listener.appVisible(isApplicationVisible);
				}
			}
		}
	}

	public synchronized void reportActivityStartEvent(Activity resumeAct) {
		synchronized (this) {
			if (!isApplicationVisible) {
				isApplicationVisible = true;
				// notify that application visible now
				Iterator<AppWatcherListener> iterator = listeners.values()
						.iterator();
				while (iterator.hasNext()) {
					AppWatcherListener listener = iterator.next();
					listener.appVisible(isApplicationVisible);
				}
			}
		}
	}

	public synchronized void registerAppWatcherListener(Object key,
			AppWatcherListener listener) {
		listeners.put(key, listener);
	}

	
	public void connectivityChanged(){
		new Handler().post(new Runnable() {
			@Override
			public void run() {
				boolean isOnline = CommonUtil.isOnline(appContext);
				Iterator<AppWatcherListener> iterator = listeners.values().iterator();
				while (iterator.hasNext()) {
					AppWatcherListener listener = iterator.next();
					listener.connectivityChanged(isOnline);
				}
			}
		});
	}

	public synchronized void unregisterAppWatcherListener(Object key) {
		listeners.remove(key);
	}

	public static interface AppWatcherListener {
		public void appVisible(boolean visible);
		public void noActivityExistInApp();
		public void connectivityChanged(boolean isOnline);
	}
	
	
	//need to create function to show memory usage and storage
}
