package com.zaitunlabs.zlcore.core;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Configuration;
import com.activeandroid.Model;
import com.zaitunlabs.zlcore.R;
import com.zaitunlabs.zlcore.constants.ZLCoreConstanta;
import com.zaitunlabs.zlcore.events.ReInitializeDatabaseEvent;
import com.zaitunlabs.zlcore.utils.ApplicationWacther;
import com.zaitunlabs.zlcore.utils.CommonUtil;
import com.zaitunlabs.zlcore.utils.DebugUtil;
import com.zaitunlabs.zlcore.utils.EventsUtil;
import com.zaitunlabs.zlcore.utils.Hawk;
import com.zaitunlabs.zlcore.utils.PlayServiceUtil;
import com.zaitunlabs.zlcore.utils.audio.BackSoundService;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.collector.CrashReportData;
import org.acra.config.ACRAConfiguration;
import org.acra.config.ConfigurationBuilder;
import org.acra.sender.ReportSender;
import org.acra.sender.ReportSenderException;
import org.acra.sender.ReportSenderFactory;
import org.greenrobot.eventbus.Subscribe;

public class BaseApplication extends Application {
	Configuration.Builder dbBuilder = null;
	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		MultiDex.install(this);
	}

	@Override
	public void onCreate() {
		super.onCreate();

		DebugUtil.logD("Application", this.getClass().getSimpleName() + ":onCreate");
		// inisialisasi untuk crash done engine
		if(!PlayServiceUtil.isGooglePlayServicesAvailable(this)) {
			ConfigurationBuilder configurationBuilder = new ConfigurationBuilder(this);
			configurationBuilder.setReportSenderFactoryClasses(CustomACRASenderFactory.class);
			configurationBuilder.setMailTo(ZLCoreConstanta.getCrashMailTo(this));
			configurationBuilder.setResToastText(R.string.zlcore_crash_toast_text);
			configurationBuilder.setResDialogText(R.string.zlcore_crash_dialog_text);
			configurationBuilder.setReportingInteractionMode(ReportingInteractionMode.NOTIFICATION);
			ACRA.init(this, configurationBuilder);
		}

		ApplicationWacther.initialize(this).registerAppWatcherListener(this,
				new ApplicationWacther.AppWatcherListener() {
					@Override
					public void appVisible(boolean visible) {
						if (visible) {
							BackSoundService.resumeBackSound(BaseApplication.this);
						} else {
							BackSoundService.pauseBackSound(BaseApplication.this);
						}
					}

					@Override
					public void noActivityExistInApp() {
						BackSoundService.stopBackSound(BaseApplication.this);
					}

					@Override
					public void connectivityChanged(boolean isOnline) {
					}
				});

		dbInitialize();

		Hawk.init(this);

		EventsUtil.register(this);
	}

	private void dbInitialize(){
		getDBBuilder();
		Configuration dbConfiguration = dbBuilder.setDatabaseName(ZLCoreConstanta.getDatabaseName(this))
				.setDatabaseVersion(ZLCoreConstanta.getDatabaseVersion())
				.create();
		ActiveAndroid.initialize(dbConfiguration);
	}

	@Subscribe
	public void onEvent(ReInitializeDatabaseEvent event){
		ActiveAndroid.dispose();
		dbInitialize();
	}

	protected void addDBModelClasses(Class<? extends Model>... modelClasses){
		getDBBuilder();
		dbBuilder.addModelClasses(modelClasses);
	}

	private void getDBBuilder(){
		if(dbBuilder == null){
			dbBuilder = new Configuration.Builder(this);
		}
	}

	protected void addDBModelClass(Class<? extends Model> modelClass){
		getDBBuilder();
		dbBuilder.addModelClass(modelClass);
	}

	@Override
	public void onLowMemory() {
		DebugUtil.logD("Application", this.getClass().getSimpleName()
				+ ":onLowMemory");
		DebugUtil.logE("LOW_MEMORY", "low memory occured");
		super.onLowMemory();
	}

	@Override
	public void onTerminate() {
		DebugUtil.logD("Application", this.getClass().getSimpleName()
				+ ":onTerminate");
		
		if (BackSoundService.isRunning()) {
			BackSoundService.stopBackSound(BaseApplication.this);
		}
		ApplicationWacther.getInstance(this).unregisterAppWatcherListener(this);

		ActiveAndroid.dispose();
		EventsUtil.unregister(this);
		super.onTerminate();
	}



	private class CustomACRASender implements ReportSender {
		@Override
		public void send(Context context, CrashReportData report) throws ReportSenderException {
			CommonUtil.sendEmail(BaseApplication.this, ZLCoreConstanta.getCrashMailTo(context), BaseApplication.this.getPackageName()+" Crash Report", report.toString(), "An error has occurred! Send an error done?");
		}
	}

	private class CustomACRASenderFactory implements ReportSenderFactory {

		public CustomACRASenderFactory(){

		}

		@Override
		public ReportSender create(Context context, ACRAConfiguration config) {
			return new CustomACRASender();
		}
	}

}
