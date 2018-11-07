package com.zaitunlabs.zlcore.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;

/**
 * Created by ahsai on 12/29/2017.
 */

public class SmsListener extends BroadcastReceiver {
    public static final String SMS_RECEIVED_INTENT = "android.provider.Telephony.SMS_RECEIVED";
    private ReceivedSMSListener receivedSMSListener;

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(SMS_RECEIVED_INTENT)){
            Bundle bundle = intent.getExtras();
            SmsMessage[] msgs = null;
            String msg_from;
            if (bundle != null){
                try{
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    msgs = new SmsMessage[pdus.length];
                    for(int i=0; i<msgs.length; i++){
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            String format = bundle.getString("format");
                            msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i], format);
                        } else {
                            msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                        }

                        msg_from = msgs[i].getOriginatingAddress();
                        String msgBody = msgs[i].getMessageBody();

                        if (receivedSMSListener != null)
                            receivedSMSListener.onReceived(msg_from, "", msgBody);
                    }
                }catch(Exception e){
                    if(receivedSMSListener != null)receivedSMSListener.onFailed(e.getMessage());
                }
            }
        }
    }

    public static interface ReceivedSMSListener{
        public void onReceived(String from, String to, String message);
        public void onFailed(String failedMessage);
    }

    public static SmsListener listenForSMS(Context context, ReceivedSMSListener receivedSMSListener){
        SmsListener smsBroadcastListener = new SmsListener();
        smsBroadcastListener.receivedSMSListener = receivedSMSListener;
        context.registerReceiver(smsBroadcastListener,new IntentFilter(SMS_RECEIVED_INTENT));
        return smsBroadcastListener;
    }

    public void stopListenForSMS(Context context){
        context.unregisterReceiver(this);
    }
}