package com.zaitunlabs.zlcore.services;

import android.app.IntentService;
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
import com.zaitunlabs.zlcore.utils.CommonUtils;
import com.zaitunlabs.zlcore.utils.HttpClientUtils;
import com.zaitunlabs.zlcore.utils.PrefsData;

import org.json.JSONObject;

import java.lang.reflect.Modifier;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

public class FCMIntentService extends JobIntentService {
    public static final String PARAM_IS_MEID = InfoFragment.PARAM_IS_MEID;
    private final String TAG = FCMIntentService.class.getSimpleName();
    public static final int JOB_ID = 11000011;

    private static final String ACTION_SEND_TOKEN = "com.zaitunlabs.zlcore.services.action.SEND_TOKEN";
    private static boolean isProcessing = false;

    public static final String PARAM_APPID = "param_appid";
    public static final String PARAM_NEED_LOGIN = "param_need_login";


    private boolean isMeid;


    public static void startSending(Context context, String appid, boolean needLogin, boolean isMeid) {
        Intent intent = new Intent(context, FCMIntentService.class);
        intent.setAction(ACTION_SEND_TOKEN);
        intent.putExtra(PARAM_APPID,appid);
        intent.putExtra(PARAM_NEED_LOGIN,needLogin);
        intent.putExtra(PARAM_IS_MEID, isMeid);
        JobIntentService.enqueueWork(context,FCMIntentService.class,JOB_ID,intent);
    }

    public static void startSending(final Context context, final String appid, final boolean needLogin, final boolean isMeid, long delayInMillis) {
        new android.os.Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(context, FCMIntentService.class);
                intent.setAction(ACTION_SEND_TOKEN);
                intent.putExtra(PARAM_APPID,appid);
                intent.putExtra(PARAM_NEED_LOGIN,needLogin);
                intent.putExtra(PARAM_IS_MEID, isMeid);
                JobIntentService.enqueueWork(context,FCMIntentService.class,JOB_ID,intent);
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
        final String appid = CommonUtils.getStringIntent(intent,PARAM_APPID, "-1");
        final boolean needLogin = CommonUtils.getBooleanIntent(intent,PARAM_NEED_LOGIN, true);

        if(isProcessing)return;
        isProcessing = true;


        isMeid = CommonUtils.getBooleanIntent(intent, PARAM_IS_MEID, false);

        if(TextUtils.isEmpty(PrefsData.getPushyToken())){
            //it means pushy.Me not yet generate token, please waiting and retry
            isProcessing = false;
            FCMIntentService.startSending(this, appid, needLogin, isMeid, 2*1000);
        }else {
            if (!PrefsData.getPushyTokenSent() && (PrefsData.isAccountLogin() || !needLogin)) {

                AndroidNetworking.post(APIConstant.API_SEND_FCM)
                        .setOkHttpClient(HttpClientUtils.getHTTPClient(this, APIConstant.API_VERSION, isMeid))
                        .addUrlEncodeFormBodyParameter("fcmid",PrefsData.getPushyToken())
                        .addUrlEncodeFormBodyParameter("appid",appid)
                        .setPriority(Priority.HIGH)
                        .setTag("registerfcm"+this.toString())
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
                                    PrefsData.setPushyTokenSent(true);
                                } else if(responseModel.getStatus() == APIResponse.GENERIC_RESPONSE.FAILED) {
                                    FCMIntentService.startSending(FCMIntentService.this, appid, needLogin, isMeid, 2 * 1000);
                                }
                            }

                            @Override
                            public void onError(ANError anError) {
                                isProcessing = false;
                                anError.printStackTrace();
                            }
                        });


                /*
                APIService apiService = HttpClientUtils.getAPIService(this, APIConstant.API_VERSION);
                Call<GenericResponseModel> sendTokenResObj = apiService.sendToken(HttpClientUtils.getAuthAPIKey(), PrefsData.getLoginType(), PrefsData.getPushyToken());
                try {
                    Response<GenericResponseModel> response = sendTokenResObj.execute();
                    if (response.isSuccessful() && response.body() != null) {
                        if (response.body().getStatus() >= APIResponse.GENERIC_RESPONSE.OK) {
                            PrefsData.setPushyTokenSent(true);
                        } else if (response.body().getStatus() <= APIResponse.GENERIC_RESPONSE.FAILED) {
                            FCMIntentService.startSending(this, 2 * 1000);
                        } else {
                            FCMIntentService.startSending(this, 2 * 1000);
                        }
                    } else if (response.code() == APIResponse.HTTPCode.INVALID_METHOD) {
                        DebugUtils.logW(TAG, "why invalid method???");
                    } else {
                        FCMIntentService.startSending(this, 2 * 1000);
                    }
                } catch (IOException e) {
                    if (e instanceof UnknownHostException
                            || e instanceof ConnectException
                            || e instanceof SocketTimeoutException) {
                        FCMIntentService.startSending(this, 2 * 1000);
                    } else {
                        DebugUtils.logE(TAG, e.getMessage());
                    }
                }*/
            } else {
                isProcessing = false;
            }
        }
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        onHandleIntent(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
