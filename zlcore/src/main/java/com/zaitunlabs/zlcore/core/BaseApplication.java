package com.zaitunlabs.zlcore.core;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

import com.ahsailabs.sqlitewrapper.Lookup;
import com.ahsailabs.sqlitewrapper.SQLiteWrapper;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;
import com.zaitunlabs.zlcore.R;
import com.zaitunlabs.zlcore.constants.ZLCoreConstanta;
import com.zaitunlabs.zlcore.events.ReInitializeDatabaseEvent;
import com.zaitunlabs.zlcore.tables.AppListDataModel;
import com.zaitunlabs.zlcore.tables.AppListModel;
import com.zaitunlabs.zlcore.tables.AppListPagingModel;
import com.zaitunlabs.zlcore.tables.BookmarkModel;
import com.zaitunlabs.zlcore.tables.InformationModel;
import com.zaitunlabs.zlcore.tables.StoreDataModel;
import com.zaitunlabs.zlcore.tables.StoreModel;
import com.zaitunlabs.zlcore.tables.StorePagingModel;
import com.zaitunlabs.zlcore.utils.ApplicationWacther;
import com.zaitunlabs.zlcore.utils.CommonUtil;
import com.zaitunlabs.zlcore.utils.DebugUtil;
import com.zaitunlabs.zlcore.utils.EventsUtil;
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

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;

public class BaseApplication extends Application {
	public static final String DATABASE_NAME = "zlcore.db";
	public static final String TLSV_MIN = "TLSv1.2";
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
		} else {
			try {
				// Google Play will install latest OpenSSL
				ProviderInstaller.installIfNeeded(getApplicationContext());
				SSLContext sslContext;
				sslContext = SSLContext.getInstance(TLSV_MIN);
				sslContext.init(null, null, null);
				sslContext.createSSLEngine();
			} catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException
					| NoSuchAlgorithmException | KeyManagementException e) {
				e.printStackTrace();
			}
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

		Lookup.init(this, true);

		EventsUtil.register(this);


	}

	private void dbInitialize(){
		SQLiteWrapper.addDatabase(new SQLiteWrapper.Database() {
			@Override
			public Context getContext() {
				return BaseApplication.this;
			}

			@Override
			public String getDatabaseName() {
				return BaseApplication.DATABASE_NAME;
			}

			@Override
			public int getDatabaseVersion() {
				return 1;
			}

			@Override
			public void configure(SQLiteWrapper sqLiteWrapper) {
				//infomation
				sqLiteWrapper.addTable(new SQLiteWrapper.Table(InformationModel.class)
						.addStringField("title")
						.addStringField("body")
						.addBooleanField("read")
						.addIntField("type")
						.addStringField("photoUrl")
						.addStringField("infoUrl")
						.enableRecordLog()
						.addIndex("read"));

				//Bookmark
				sqLiteWrapper.addTable(new SQLiteWrapper.Table(BookmarkModel.class)
						.addStringField("title")
						.addStringField("desc")
						.addStringField("link")
						.enableRecordLog()
						.addIndex("link"));


				//Store
				sqLiteWrapper.addTable(new SQLiteWrapper.Table(StoreModel.class)
						.addIntField("status")
						.addStringField("message")
						.enableRecordLog());

				sqLiteWrapper.addTable(new SQLiteWrapper.Table(StorePagingModel.class)
						.addIntField("countperpage")
						.addIntField("prev")
						.addIntField("next")
						.addLongField("store_model_id")
						.enableRecordLog()
						.addForeignKey("store_model_id",
								null, StoreModel.class,SQLiteWrapper.ID,
								SQLiteWrapper.ForeignKey.CASCADE, SQLiteWrapper.ForeignKey.CASCADE));

				sqLiteWrapper.addTable(new SQLiteWrapper.Table(StoreDataModel.class)
						.addStringField("image")
						.addStringField("title")
						.addStringField("desc")
						.addStringField("uniq")
						.addStringField("url")
						.addLongField("store_model_id")
						.enableRecordLog()
						.addForeignKey("store_model_id",
								null, StoreModel.class, SQLiteWrapper.ID,
								SQLiteWrapper.ForeignKey.CASCADE, SQLiteWrapper.ForeignKey.CASCADE)
						.addIndex("store_model_id"));


				//AppList
				sqLiteWrapper.addTable(new SQLiteWrapper.Table(AppListModel.class)
						.addIntField("status")
						.addStringField("message")
						.enableRecordLog());

				sqLiteWrapper.addTable(new SQLiteWrapper.Table(AppListPagingModel.class)
						.addIntField("countperpage")
						.addIntField("prev")
						.addIntField("next")
						.addLongField("applist_model_id")
						.enableRecordLog()
						.addForeignKey("applist_model_id",
								null, AppListModel.class,SQLiteWrapper.ID,
								SQLiteWrapper.ForeignKey.CASCADE, SQLiteWrapper.ForeignKey.CASCADE)
						.addIndex("applist_model_id"));

				sqLiteWrapper.addTable(new SQLiteWrapper.Table(AppListDataModel.class)
						.addStringField("image")
						.addStringField("title")
						.addStringField("desc")
						.addStringField("uniq")
						.addStringField("url")
						.addLongField("applist_model_id")
						.enableRecordLog()
						.addForeignKey("applist_model_id",
								null, AppListModel.class, SQLiteWrapper.ID,
								SQLiteWrapper.ForeignKey.CASCADE, SQLiteWrapper.ForeignKey.CASCADE));

			}
		});
	}

	@Subscribe
	public void onEvent(ReInitializeDatabaseEvent event){
		SQLiteWrapper.removeAllDatabase();
		dbInitialize();
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

		EventsUtil.unregister(this);

		SQLiteWrapper.removeAllDatabase();
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
