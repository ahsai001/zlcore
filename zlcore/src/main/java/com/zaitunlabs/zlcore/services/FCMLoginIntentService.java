package com.zaitunlabs.zlcore.services;

import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.text.TextUtils;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.gson.GsonBuilder;
import com.zaitunlabs.zlcore.api.APIConstant;
import com.zaitunlabs.zlcore.api.APIResponse;
import com.zaitunlabs.zlcore.fragments.InfoFragment;
import com.zaitunlabs.zlcore.models.GenericResponseModel;
import com.zaitunlabs.zlcore.utils.CommonUtil;
import com.zaitunlabs.zlcore.utils.HttpClientUtil;
import com.zaitunlabs.zlcore.utils.PrefsData;

import org.json.JSONObject;

import java.lang.reflect.Modifier;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

public class FCMLoginIntentService extends JobIntentService {
    public static final String PARAM_IS_MEID = InfoFragment.PARAM_IS_MEID;
    private final String TAG = FCMLoginIntentService.class.getSimpleName();
    public static final int JOB_ID = 11000013;

    private static final String ACTION_SEND_TOKEN = "com.zaitunlabs.zlcore.services.action.SEND_TOKEN";
    private static boolean isProcessing = false;

    public static final String PARAM_APPID = "param_appid";

    private boolean isMeid;

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        onHandleIntent(intent);
    }

    public static void startSending(Context context, String appid, boolean isMeid) {
        Intent intent = new Intent(context, FCMLoginIntentService.class);
        intent.setAction(ACTION_SEND_TOKEN);
        intent.putExtra(PARAM_APPID,appid);
        intent.putExtra(PARAM_IS_MEID, isMeid);
        JobIntentService.enqueueWork(context,FCMLoginIntentService.class,JOB_ID,intent);
    }

    public static void startSending(final Context context, final String appid, final boolean isMeid, long delayInMillis) {
        new android.os.Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(context, FCMLoginIntentService.class);
                intent.setAction(ACTION_SEND_TOKEN);
                intent.putExtra(PARAM_APPID,appid);
                intent.putExtra(PARAM_IS_MEID, isMeid);
                JobIntentService.enqueueWork(context,FCMLoginIntentService.class,JOB_ID,intent);
            }
        }, delayInMillis);
    }

    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_SEND_TOKEN.equals(action)) {
                handleActionSendToken(intent);
            }
        }
    }

    private void handleActionSendToken(Intent intent) {
        final String appid = CommonUtil.getStringIntent(intent,PARAM_APPID, "-1");

        if(isProcessing)return;
        isProcessing = true;


        isMeid = CommonUtil.getBooleanIntent(intent, PARAM_IS_MEID, false);

        if(TextUtils.isEmpty(PrefsData.getPushyToken())){
            //it means pushy.Me not yet generate token, please waiting and retry
            isProcessing = false;
            FCMLoginIntentService.startSending(this, appid, isMeid, 2*1000);
        }else {
            if (!PrefsData.getPushyTokenLoginSent() && (PrefsData.isAccountLoggedIn())) {
                AndroidNetworking.post(APIConstant.API_SEND_FCM_LOGIN)
                        .setOkHttpClient(HttpClientUtil.getHTTPClient(this, APIConstant.API_VERSION, isMeid))
                        .addUrlEncodeFormBodyParameter("fcmid",PrefsData.getPushyToken())
                        .addUrlEncodeFormBodyParameter("appid",appid)
                        .setPriority(Priority.HIGH)
                        .setTag("updateloginfcm"+this.toString())
                        .build()
                        .getAsJSONObject(new JSONObjectRequestListener() {
                            @Override
                            public void onResponse(JSONObject response) {
                                GenericResponseModel responseModel = new GsonBuilder()
                                        .excludeFieldsWithoutExposeAnnotation()
                                        .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                                        .create()
                                        .fromJson(response.toString(), GenericResponseModel.class);

                                isProcessing = false;
                                if(responseModel.getStatus() == APIResponse.GENERIC_RESPONSE.OK) {
                                    PrefsData.setPushyTokenLoginSent(true);
                                } else if(responseModel.getStatus() == APIResponse.GENERIC_RESPONSE.FAILED) {
                                    FCMLoginIntentService.startSending(FCMLoginIntentService.this, appid, isMeid, 2 * 1000);
                                }
                            }

                            @Override
                            public void onError(ANError anError) {
                                isProcessing = false;
                                anError.printStackTrace();
                            }
                        });
            } else {
                isProcessing = false;
            }
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
