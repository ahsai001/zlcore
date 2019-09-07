package com.zaitunlabs.zlcore.utils

import android.app.Activity
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle


import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.Api
import com.google.android.gms.common.api.GoogleApiClient

/**
 * Created by ahmad s on 4/12/2016.
 */
class PlayServiceUtils(context: Context) : GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    var googleApiClient: GoogleApiClient? = null
        private set
    private val mContext: Context? = null

    init {
        this.mContext = context
    }

    fun init(vararg apis: Api<*>): PlayServiceUtils {
        if (isGooglePlayServicesAvailable(this.mContext)) {
            buildGoogleApiClient(*apis)
        }
        return this
    }

    fun start() {
        if (googleApiClient != null) {
            googleApiClient!!.connect()
        }
    }

    fun stop() {
        if (googleApiClient != null && googleApiClient!!.isConnected) {
            googleApiClient!!.disconnect()
        }
    }

    /**
     * Creating google api client object
     */
    @Synchronized
    protected fun buildGoogleApiClient(vararg apis: Api<*>) {
        val builder = GoogleApiClient.Builder(this.mContext!!).addConnectionCallbacks(this)
        for (api in apis) {
            builder.addApi(api)
        }
        googleApiClient = builder.build()
    }

    override fun onConnected(bundle: Bundle?) {

    }

    override fun onConnectionSuspended(i: Int) {
        if (googleApiClient != null) {
            googleApiClient!!.connect()
        }
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {

    }

    companion object {

        fun isGSFPackageAvailable(context: Context): Boolean {
            val pm = context.packageManager
            val list = pm.getInstalledPackages(0)

            for (pi in list) {
                if (pi.packageName == "com.google.android.gsf") return true
            }

            return false
        }

        fun isGooglePlayServicesAvailable(activity: Activity): Boolean {
            val googleApiAvailability = GoogleApiAvailability.getInstance()
            val status = googleApiAvailability.isGooglePlayServicesAvailable(activity)
            if (status != ConnectionResult.SUCCESS) {
                if (googleApiAvailability.isUserResolvableError(status)) {
                    googleApiAvailability.getErrorDialog(activity, status, 2404).show()
                }
                return false
            }
            return true
        }


        /**
         * Method to verify google play services on the device
         */
        fun isGooglePlayServicesAvailable(mContext: Context?): Boolean {
            val resultCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(mContext)
            if (resultCode != ConnectionResult.SUCCESS) {
                if (GoogleApiAvailability.getInstance().isUserResolvableError(resultCode)) {
                    //Toast.makeText(mContext.getApplicationContext(), GoogleApiAvailability.getInstance().getErrorString(resultCode), Toast.LENGTH_LONG).show();
                } else {
                    //Toast.makeText(mContext.getApplicationContext(), "This device is not supported.", Toast.LENGTH_LONG).show();
                }
                return false
            }
            return true
        }
    }
}
