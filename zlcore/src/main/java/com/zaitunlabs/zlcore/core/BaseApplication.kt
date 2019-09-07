package com.zaitunlabs.zlcore.core

import android.app.Application
import android.content.Context

import androidx.multidex.MultiDex

import com.activeandroid.ActiveAndroid
import com.activeandroid.Configuration
import com.activeandroid.Model
import com.zaitunlabs.zlcore.R
import com.zaitunlabs.zlcore.constants.ZLCoreConstanta
import com.zaitunlabs.zlcore.events.ReInitializeDatabaseEvent
import com.zaitunlabs.zlcore.utils.ApplicationWacther
import com.zaitunlabs.zlcore.utils.CommonUtils
import com.zaitunlabs.zlcore.utils.DebugUtils
import com.zaitunlabs.zlcore.utils.EventsUtils
import com.zaitunlabs.zlcore.utils.Hawk
import com.zaitunlabs.zlcore.utils.PlayServiceUtils
import com.zaitunlabs.zlcore.utils.audio.BackSoundService

import org.acra.ACRA
import org.acra.ReportingInteractionMode
import org.acra.collector.CrashReportData
import org.acra.config.ACRAConfiguration
import org.acra.config.ConfigurationBuilder
import org.acra.sender.ReportSender
import org.acra.sender.ReportSenderException
import org.acra.sender.ReportSenderFactory
import org.greenrobot.eventbus.Subscribe

class BaseApplication : Application() {
    internal var dbBuilder: Configuration.Builder? = null
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()

        DebugUtils.logD("Application", this.javaClass.getSimpleName() + ":onCreate")
        // inisialisasi untuk crash done engine
        if (!PlayServiceUtils.isGooglePlayServicesAvailable(this)) {
            val configurationBuilder = ConfigurationBuilder(this)
            configurationBuilder.setReportSenderFactoryClasses(CustomACRASenderFactory::class.java!!)
            configurationBuilder.setMailTo(ZLCoreConstanta.getCrashMailTo(this))
            configurationBuilder.setResToastText(R.string.zlcore_crash_toast_text)
            configurationBuilder.setResDialogText(R.string.zlcore_crash_dialog_text)
            configurationBuilder.setReportingInteractionMode(ReportingInteractionMode.NOTIFICATION)
            ACRA.init(this, configurationBuilder)
        }

        ApplicationWacther.initialize(this).registerAppWatcherListener(this,
                object : ApplicationWacther.AppWatcherListener {
                    override fun appVisible(visible: Boolean) {
                        if (visible) {
                            BackSoundService.resumeBackSound(this@BaseApplication)
                        } else {
                            BackSoundService.pauseBackSound(this@BaseApplication)
                        }
                    }

                    override fun noActivityExistInApp() {
                        BackSoundService.stopBackSound(this@BaseApplication)
                    }

                    override fun connectivityChanged(isOnline: Boolean) {}
                })

        dbInitialize()

        Hawk.init(this)

        EventsUtils.register(this)
    }

    private fun dbInitialize() {
        getDBBuilder()
        val dbConfiguration = dbBuilder!!.setDatabaseName(ZLCoreConstanta.getDatabaseName(this))
                .setDatabaseVersion(ZLCoreConstanta.databaseVersion)
                .create()
        ActiveAndroid.initialize(dbConfiguration)
    }

    @Subscribe
    fun onEvent(event: ReInitializeDatabaseEvent) {
        ActiveAndroid.dispose()
        dbInitialize()
    }

    protected fun addDBModelClasses(vararg modelClasses: Class<out Model>) {
        getDBBuilder()
        dbBuilder!!.addModelClasses(*modelClasses)
    }

    private fun getDBBuilder() {
        if (dbBuilder == null) {
            dbBuilder = Configuration.Builder(this)
        }
    }

    protected fun addDBModelClass(modelClass: Class<out Model>) {
        getDBBuilder()
        dbBuilder!!.addModelClass(modelClass)
    }

    override fun onLowMemory() {
        DebugUtils.logD("Application", this.javaClass.getSimpleName() + ":onLowMemory")
        DebugUtils.logE("LOW_MEMORY", "low memory occured")
        super.onLowMemory()
    }

    override fun onTerminate() {
        DebugUtils.logD("Application", this.javaClass.getSimpleName() + ":onTerminate")

        if (BackSoundService.isRunning) {
            BackSoundService.stopBackSound(this@BaseApplication)
        }
        ApplicationWacther.getInstance(this).unregisterAppWatcherListener(this)

        ActiveAndroid.dispose()
        EventsUtils.unregister(this)
        super.onTerminate()
    }


    private inner class CustomACRASender : ReportSender {
        @Throws(ReportSenderException::class)
        override fun send(context: Context, report: CrashReportData) {
            CommonUtils.sendEmail(this@BaseApplication, ZLCoreConstanta.getCrashMailTo(context), this@BaseApplication.packageName + " Crash Report", report.toString(), "An error has occurred! Send an error done?")
        }
    }

    private inner class CustomACRASenderFactory : ReportSenderFactory {

        override fun create(context: Context, config: ACRAConfiguration): ReportSender {
            return CustomACRASender()
        }
    }

}
