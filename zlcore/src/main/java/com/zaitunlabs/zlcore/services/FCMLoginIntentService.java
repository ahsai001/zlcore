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

public class FCMLoginIntentService extends IntentService {
    private final String TAG = FCMLoginIntentService.class.getSimpleName();

    private static final String ACTION_SEND_TOKEN = "com.zaitunlabs.zlcore.services.action.SEND_TOKEN";
    private static boolean isProcessing = false;

    public static final String PARAM_APPID = "param_appid";

    public FCMLoginIntentService() {
        super("FCMLoginIntentService");
    }

    public static void startSending(Context context, String appid) {
        Intent intent = new Intent(context, FCMLoginIntentService.class);
        intent.setAction(ACTION_SEND_TOKEN);
        intent.putExtra(PARAM_APPID,appid);
        context.startService(intent);
    }

    public static void startSending(final Context context, final String appid, long delayInMillis) {
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(context, FCMLoginIntentService.class);
                intent.setAction(ACTION_SEND_TOKEN);
                intent.putExtra(PARAM_APPID,appid);
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

        if(isProcessing)return;
        isProcessing = true;

        if(TextUtils.isEmpty(PrefsData.getPushyToken())){
            //it means pushy.Me not yet generate token, please waiting and retry
            isProcessing = false;
            FCMLoginIntentService.startSending(this, appid, 2*1000);
        }else {
            if (!PrefsData.getPushyTokenLoginSent() && (PrefsData.isAccountLogin())) {
                AndroidNetworking.post(APIConstant.API_SEND_FCM_LOGIN)
                        .setOkHttpClient(HttpClientUtils.getHTTPClient(this, APIConstant.API_VERSION))
                        .addUrlEncodeFormBodyParameter("fcmid",PrefsData.getPushyToken())
                        .addUrlEncodeFormBodyParameter("appid",appid)
                        .setPriority(Priority.HIGH)
                        .setTag("update login fcm")
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
                                    FCMLoginIntentService.startSending(FCMLoginIntentService.this, appid, 2 * 1000);
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
}
