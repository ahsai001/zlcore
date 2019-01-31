package com.zaitunlabs.zlcore.utils;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.zaitunlabs.zlcore.R;
import com.zaitunlabs.zlcore.utils.CommonUtils;

/**
 * Created by ahmad s on 3/14/2016.
 */
public class LocationUtils implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private android.location.Location mLastLocation;

    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

    private LocationHelperCallback updateLocationCallback;

    private Context mContext;

    public LocationUtils(Context mContext) {
        this.mContext = mContext;
    }

    public LocationHelperCallback getUpdateLocationCallback() {
        return updateLocationCallback;
    }

    public void setUpdateLocationCallback(LocationHelperCallback updateLocationCallback) {
        this.updateLocationCallback = updateLocationCallback;
    }

    public void init(){
        if (checkPlayServices()) {
            // Building the GoogleApi client
            buildGoogleApiClient();
        }
    }

    public void start(){
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    public void stop(){
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
    }

    /**
     * Creating google api client object
     * */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    /**
     * Method to verify google play services on the device
     * */
    private boolean checkPlayServices() {
        int resultCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(mContext);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GoogleApiAvailability.getInstance().isUserResolvableError(resultCode)) {
                Toast.makeText(mContext.getApplicationContext(), GoogleApiAvailability.getInstance().getErrorString(resultCode), Toast.LENGTH_LONG)
                        .show();
            } else {
                Toast.makeText(mContext.getApplicationContext(),"This device is not supported.", Toast.LENGTH_LONG)
                        .show();
            }
            return false;
        }
        return true;
    }

    /**
     * Method to display the location on UI
     * */
    public void getLastLocation() {
        if(mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

            if (mLastLocation != null) {
                if(updateLocationCallback != null){
                    updateLocationCallback.currentLocationUpdate(mLastLocation);
                }
            } else {
                if(updateLocationCallback != null){
                    updateLocationCallback.failed("");
                }
                CommonUtils.showToast(mContext, mContext.getString(R.string.zlcore_warning_failed_get_location_with_gps));
            }
            stop();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        getLastLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


    public static interface LocationHelperCallback{
        public void currentLocationUpdate(Location newLocation);
        public void failed(String reason);
    }
}
