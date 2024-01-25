package com.zaitunlabs.zlcore.utils;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.annotation.NonNull;

/**
 * Created by ahmad s on 2020-06-02.
 */
public class ConnectivityUtil {
    private NetworkReceiver receiver;
    public void registerListener(@NonNull Activity activity, @NonNull NetworkListener networkListener){
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkReceiver(networkListener);
        activity.registerReceiver(receiver, filter);
    }

    public void unRegisterListener(Activity activity){
        if(receiver != null)activity.unregisterReceiver(receiver);
    }


    //need permission android.permission.ACCESS_NETWORK_STATE
    public static class NetworkReceiver extends BroadcastReceiver {
        private NetworkListener networkListener;

        public NetworkReceiver(NetworkListener networkListener){
            this.networkListener = networkListener;
        }
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager conn =  (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = conn.getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                //WIFI
                if(networkListener!=null)networkListener.wifiActive(networkInfo);
            } else if (networkInfo != null) {
                //Non WIFI
                if(networkListener!=null)networkListener.nonWifiActive(networkInfo);
            } else {
                //No Connectivity
                if(networkListener!=null)networkListener.noConnectivity();
            }
        }
    }

    public interface NetworkListener{
        void wifiActive(NetworkInfo networkInfo);
        void nonWifiActive(NetworkInfo networkInfo);
        void noConnectivity();
    }
}
