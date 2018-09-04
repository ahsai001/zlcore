package com.zaitunlabs.zlcore.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.gson.GsonBuilder;
import com.zaitunlabs.zlcore.api.APIConstant;
import com.zaitunlabs.zlcore.api.APIResponse;
import com.zaitunlabs.zlcore.models.GenericResponseModel;
import com.zaitunlabs.zlcore.utils.CommonUtils;
import com.zaitunlabs.zlcore.utils.HttpClientUtils;
import com.zaitunlabs.zlcore.utils.PrefsData;

import org.json.JSONObject;

import java.lang.reflect.Modifier;

public class FCMIntentService extends IntentService {
    private final String TAG = FCMIntentService.class.getSimpleName();

    private static final String ACTION_SEND_TOKEN = "com.zaitunlabs.zlcore.services.action.SEND_TOKEN";
    private static boolean isProcessing = false;

    public static final String PARAM_APPID = "param_appid";
    public static final String PARAM_NEED_LOGIN = "param_need_login";

    public FCMIntentService() {
        super("FCMIntentService");
    }

    public static void startSending(Context context, String appid, boolean needLogin) {
        Intent intent = new Intent(context, FCMIntentService.class);
        intent.setAction(ACTION_SEND_TOKEN);
        intent.putExtra(PARAM_APPID,appid);
        intent.putExtra(PARAM_NEED_LOGIN,needLogin);
        context.startService(intent);
    }

    public static void startSending(final Context context, final String appid, final boolean needLogin, long delayInMillis) {
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(context, FCMIntentService.class);
                intent.setAction(ACTION_SEND_TOKEN);
                intent.putExtra(PARAM_APPID,appid);
                intent.putExtra(PARAM_NEED_LOGIN,needLogin);
                context.startService(intent);
            }
        }, delayInMillis);
    }

    @Override
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

        if(TextUtils.isEmpty(PrefsData.getPushyToken())){
            //it means pushy.Me not yet generate token, please waiting and retry
            isProcessing = false;
            FCMIntentService.startSending(this, appid, needLogin, 2*1000);
        }else {
            if (!PrefsData.getPushyTokenSent() && (PrefsData.isAccountLogin() || !needLogin)) {
                AndroidNetworking.post(APIConstant.API_SEND_FCM)
                        .setOkHttpClient(HttpClientUtils.getHTTPClient(this, APIConstant.API_VERSION))
                        .addUrlEncodeFormBodyParameter("fcmid",PrefsData.getPushyToken())
                        .addUrlEncodeFormBodyParameter("appid",appid)
                        .setPriority(Priority.HIGH)
                        .setTag("register fcm")
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
                                    FCMIntentService.startSending(FCMIntentService.this, appid, needLogin, 2 * 1000);
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
}
