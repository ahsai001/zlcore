package com.zaitunlabs.zlcore.utils.audio;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.zaitunlabs.zlcore.utils.audio.BackSoundService;


public class StopPlayerReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
	   Intent in = new Intent(context,BackSoundService.class);
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
	   context.stopService(in);
	}

}
