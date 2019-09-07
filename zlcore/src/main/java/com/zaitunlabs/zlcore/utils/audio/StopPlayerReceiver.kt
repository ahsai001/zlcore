package com.zaitunlabs.zlcore.utils.audio

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

import com.zaitunlabs.zlcore.utils.audio.BackSoundService


class StopPlayerReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val `in` = Intent(context, BackSoundService::class.java)
        /* if(audioservice.isRunning()){
		   if (((GennieApp)context.getApplicationContext()).mServiceMessenger != null) {
				try {
					Message msg = Message.obtain(null, audioservice.MSG_UNREGISTER_CLIENT);
					((GennieApp)context.getApplicationContext()).mServiceMessenger.send(msg);
				} catch (RemoteException e) {
					// There is nothing special we need to do if the service has crashed.
				}
			}
			// Detach our existing connection.
		   if(((GennieApp)context.getApplicationContext()).mConnection != null)
			   context.unbindService(((GennieApp)context.getApplicationContext()).mConnection);
	   }*/
        context.stopService(`in`)
    }

}
