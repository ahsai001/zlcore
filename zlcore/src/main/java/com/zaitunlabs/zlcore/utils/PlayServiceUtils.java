package com.zaitunlabs.zlcore.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.List;

/**
 * Created by ahmad s on 4/12/2016.
 */
public class PlayServiceUtils implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient = null;
    private Context mContext = null;

    public PlayServiceUtils(Context context){
        this.mContext = context;
    }


    public GoogleApiClient getGoogleApiClient(){
        return mGoogleApiClient;
    }

    public PlayServiceUtils init(Api... apis) {
        if (isGooglePlayServicesAvailable(this.mContext)) {
            buildGoogleApiClient(apis);
        }
        return this;
    }

    public void start() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    public void stop() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    /**
     * Creating google api client object
     * */
    protected synchronized void buildGoogleApiClient(Api... apis) {
        GoogleApiClient.Builder builder = new GoogleApiClient.Builder(this.mContext).addConnectionCallbacks(this);
        for (Api api : apis){
            builder.addApi(api);
        }
        mGoogleApiClient = builder.build();
    }

    public static boolean isGSFPackageAvailable(Context context) {
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> list = pm.getInstalledPackages(0);

        for (PackageInfo pi : list) {
            if(pi.packageName.equals("com.google.android.gsf")) return true;
        }

        return false;
    }

    public static boolean isGooglePlayServicesAvailable(Activity activity) {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(activity);
        if(status != ConnectionResult.SUCCESS) {
            if(googleApiAvailability.isUserResolvableError(status)) {
                googleApiAvailability.getErrorDialog(activity, status, 2404).show();
            }
            return false;
        }
        return true;
    }


    /**
     * Method to verify google play services on the device
     * */
    public static boolean isGooglePlayServicesAvailable(Context mContext) {
        int resultCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(mContext);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GoogleApiAvailability.getInstance().isUserResolvableError(resultCode)) {
                //Toast.makeText(mContext.getApplicationContext(), GoogleApiAvailability.getInstance().getErrorString(resultCode), Toast.LENGTH_LONG).show();
            } else {
                //Toast.makeText(mContext.getApplicationContext(), "This device is not supported.", Toast.LENGTH_LONG).show();
            }
            return false;
        }
        return true;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {
        if(mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
