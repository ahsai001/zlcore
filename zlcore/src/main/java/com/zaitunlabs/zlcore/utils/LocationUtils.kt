package com.zaitunlabs.zlcore.utils

import android.content.Context
import android.location.Location
import android.os.Bundle
import android.widget.Toast

import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationServices
import com.zaitunlabs.zlcore.R
import com.zaitunlabs.zlcore.utils.CommonUtils

/**
 * Created by ahmad s on 3/14/2016.
 */
class LocationUtils(private val mContext: Context) : GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private var mLastLocation: android.location.Location? = null

    // Google client to interact with Google API
    private var mGoogleApiClient: GoogleApiClient? = null

    var updateLocationCallback: LocationHelperCallback? = null

    fun init() {
        if (checkPlayServices()) {
            // Building the GoogleApi client
            buildGoogleApiClient()
        }
    }

    fun start() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient!!.connect()
        }
    }

    fun stop() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient!!.disconnect()
        }
    }

    /**
     * Creating google api client object
     */
    @Synchronized
    protected fun buildGoogleApiClient() {
        mGoogleApiClient = GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build()
    }

    /**
     * Method to verify google play services on the device
     */
    private fun checkPlayServices(): Boolean {
        val resultCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(mContext)
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GoogleApiAvailability.getInstance().isUserResolvableError(resultCode)) {
                Toast.makeText(mContext.applicationContext, GoogleApiAvailability.getInstance().getErrorString(resultCode), Toast.LENGTH_LONG)
                        .show()
            } else {
                Toast.makeText(mContext.applicationContext, "This device is not supported.", Toast.LENGTH_LONG)
                        .show()
            }
            return false
        }
        return true
    }

    /**
     * Method to display the location on UI
     */
    fun getLastLocation() {
        if (mGoogleApiClient != null && mGoogleApiClient!!.isConnected) {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient)

            if (mLastLocation != null) {
                if (updateLocationCallback != null) {
                    updateLocationCallback!!.currentLocationUpdate(mLastLocation)
                }
            } else {
                if (updateLocationCallback != null) {
                    updateLocationCallback!!.failed("")
                }
                CommonUtils.showToast(mContext, mContext.getString(R.string.zlcore_warning_failed_get_location_with_gps))
            }
            stop()
        }
    }

    override fun onConnected(bundle: Bundle?) {
        getLastLocation()
    }

    override fun onConnectionSuspended(i: Int) {
        mGoogleApiClient!!.connect()
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {

    }


    interface LocationHelperCallback {
        fun currentLocationUpdate(newLocation: Location)
        fun failed(reason: String)
    }

    companion object {
        private val PLAY_SERVICES_RESOLUTION_REQUEST = 1000
    }
}
