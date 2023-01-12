package com.zaitunlabs.zlcore.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.zaitunlabs.zlcore.R;

/**
 * Created by ahmad s on 3/14/2016.
 */
public class LocationUtil {

    private android.location.Location mLastLocation;


    private LocationHelperCallback updateLocationCallback;

    private Context mContext;

    public LocationUtil(Context mContext) {
        this.mContext = mContext;
    }

    public void setUpdateLocationCallback(LocationHelperCallback updateLocationCallback) {
        this.updateLocationCallback = updateLocationCallback;
    }

    public void init(){
        if (checkPlayServices()) {
            // Building the GoogleApi client
            //buildGoogleApiClient();
        }
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
    @SuppressLint("MissingPermission")
    public void getLastLocation() {
        FusedLocationProviderClient client =
                LocationServices.getFusedLocationProviderClient(mContext);
        client.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if(task.isSuccessful()){
                    mLastLocation = task.getResult();
                    if (mLastLocation != null) {
                        if(updateLocationCallback != null){
                            updateLocationCallback.currentLocationUpdate(mLastLocation);
                        }
                    } else {
                        if(updateLocationCallback != null){
                            updateLocationCallback.failed("");
                        }
                        CommonUtil.showToast(mContext, mContext.getString(R.string.zlcore_warning_failed_get_location_with_gps));
                    }
                }
            }
        });

    }



    public static interface LocationHelperCallback{
        public void currentLocationUpdate(Location newLocation);
        public void failed(String reason);
    }
}
