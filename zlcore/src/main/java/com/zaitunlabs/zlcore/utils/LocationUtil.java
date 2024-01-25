package com.zaitunlabs.zlcore.utils;

import static android.content.Context.LOCATION_SERVICE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
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


    private LocationManager mLocationManager;

    private Context mContext;

    public LocationUtil(Context mContext) {
        this.mContext = mContext.getApplicationContext();
    }

    public void setUpdateLocationCallback(LocationHelperCallback updateLocationCallback) {
        this.updateLocationCallback = updateLocationCallback;
    }

    public void init(){
//        if (checkPlayServices()) {
//            // Building the GoogleApi client
//            //buildGoogleApiClient();
//        }
        mLocationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);

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

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10L, 10F, new LocationListener() {
                    @Override
                    public void onLocationChanged(@NonNull Location location) {
                        mLastLocation = location;
                        if (updateLocationCallback != null) {
                            updateLocationCallback.currentLocationUpdate(mLastLocation);
                        }
                        mLocationManager.removeUpdates(this);
                    }

                    @Override
                    public void onProviderDisabled(@NonNull String provider) {
                        Log.e("","onProviderDisabled : "+provider);
                    }

                    @Override
                    public void onProviderEnabled(@NonNull String provider) {
                        Log.e("","onProviderEnabled : "+provider);
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {
                        Log.e("","onStatusChanged : "+provider);
                    }
                });
            }
        });


//        FusedLocationProviderClient client =
//                LocationServices.getFusedLocationProviderClient(mContext);
//        client.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
//            @Override
//            public void onComplete(@NonNull Task<Location> task) {
//                if(task.isSuccessful()){
//                    mLastLocation = task.getResult();
//                    if (mLastLocation != null) {
//                        if(updateLocationCallback != null){
//                            updateLocationCallback.currentLocationUpdate(mLastLocation);
//                        }
//                    } else {
//                        if(updateLocationCallback != null){
//                            updateLocationCallback.failed("");
//                        }
//                        CommonUtil.showToast(mContext, mContext.getString(R.string.zlcore_warning_failed_get_location_with_gps));
//                    }
//                }
//            }
//        });

    }



    public static interface LocationHelperCallback{
        public void currentLocationUpdate(Location newLocation);
        public void failed(String reason);
    }
}
