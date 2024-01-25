package com.zaitunlabs.zlcore.services;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;


/**
 * Created by ahmad s on 2019-11-07.
 */

public class ForceDeleteFCMTokenService extends JobIntentService
{
    public static final String TAG = ForceDeleteFCMTokenService.class.getSimpleName();
    public static final int JOB_ID = 11000053;

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        onHandleIntent(intent);
    }

    protected void onHandleIntent(Intent intent)
    {
        /*try
        {
            FirebaseInstanceId.getInstance().deleteInstanceId();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }*/
    }

    public static void start(Context context){
        Intent intent = new Intent(context, ForceDeleteFCMTokenService.class);
        JobIntentService.enqueueWork(context,ForceDeleteFCMTokenService.class,JOB_ID,intent);
    }

}
