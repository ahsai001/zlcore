package com.zaitunlabs.zlcore.utils;

import android.content.Context;
import android.net.http.SslError;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Base64;
import android.webkit.SslErrorHandler;

import com.google.gson.GsonBuilder;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.zaitunlabs.zlcore.R;
import com.zaitunlabs.zlcore.api.APIConstant;
import com.zaitunlabs.zlcore.api.APIService;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Modifier;
import java.net.URLEncoder;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by ahmad s on 10/7/2015.
 * Edited by ahmad s on 12/2/2016.
 */


public class HttpClientUtils {
    public static final int DATA_DEFAULT_CONNECT_TIMEOUT_MILLIS = 15 * 1000; // 15s
    public static final int DATA_DEFAULT_READ_TIMEOUT_MILLIS = 30 * 1000; // 30s
    public static final int DATA_DEFAULT_WRITE_TIMEOUT_MILLIS = 30 * 1000; // 30s


    public static final int IMAGE_DEFAULT_CONNECT_TIMEOUT_MILLIS = 15 * 1000; // 15s
    public static final int IMAGE_DEFAULT_READ_TIMEOUT_MILLIS = 30 * 1000; // 30s
    public static final int IMAGE_DEFAULT_WRITE_TIMEOUT_MILLIS = 30 * 1000; // 30s


    public static final int UPLOAD_DEFAULT_CONNECT_TIMEOUT_MILLIS = 15 * 1000; // 15s
    public static final int UPLOAD_DEFAULT_READ_TIMEOUT_MILLIS = 60 * 1000; // 60s
    public static final int UPLOAD_DEFAULT_WRITE_TIMEOUT_MILLIS = 60 * 1000; // 60s

    private static Retrofit restAdapter = null;
    private static APIService apiService = null;

    public static String webview_user_agent = null;
    public static String androidId = null;

    public static synchronized Retrofit getRestAdapter(Context context,String apiVersion){
        synchronized (HttpClientUtils.class){
            if(restAdapter == null) {
                restAdapter = new Retrofit.Builder()
                        .baseUrl(APIConstant.BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create(
                                new GsonBuilder()
                                        //.excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                                        .excludeFieldsWithoutExposeAnnotation()
                                        .create()))
                        .client(HttpClientUtils.getHTTPClient(context,apiVersion))
                        .build();
            }
        }

        return restAdapter;
    }

    public static synchronized APIService getAPIService(Context context, String apiVersion){
        synchronized (HttpClientUtils.class){
            if(apiService == null){
                apiService = getRestAdapter(context.getApplicationContext(), apiVersion).create(APIService.class);
            }
        }
        return apiService;
    }

    public static OkHttpClient getHTTPClient(final Context context, String apiVersion){
        OkHttpClient client = null;
        if(context!= null) {
            Interceptor interceptor = getInterceptor(context, apiVersion);
            // Add the interceptor to OkHttpClient
            client = new OkHttpClient.Builder()
                    .connectTimeout(HttpClientUtils.DATA_DEFAULT_CONNECT_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
                    .readTimeout(HttpClientUtils.DATA_DEFAULT_READ_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
                    .writeTimeout(HttpClientUtils.DATA_DEFAULT_WRITE_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
                    .addInterceptor(interceptor)
                    .build();
        }
        return client;
    }

    public static OkHttpClient getHTTPClientForUpload(final Context context, String apiVersion){
        OkHttpClient client = null;
        if(context!= null) {
            Interceptor interceptor = getInterceptor(context, apiVersion);
            // Add the interceptor to OkHttpClient
            client = new OkHttpClient.Builder()
                    .connectTimeout(HttpClientUtils.UPLOAD_DEFAULT_CONNECT_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
                    .readTimeout(HttpClientUtils.UPLOAD_DEFAULT_READ_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
                    .writeTimeout(HttpClientUtils.UPLOAD_DEFAULT_WRITE_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
                    .addInterceptor(interceptor)
                    .build();
        }
        return client;
    }


    public static String getModelNumberInUrlEncode(){
        String model = "";
        try {
            model = CommonUtils.urlEncode(CommonUtils.getModelNumber());
        } catch (UnsupportedEncodingException e) {
            model = CommonUtils.getModelNumber();
        }
        return model;
    }

    private static Interceptor getInterceptor(final Context context, final String apiVersion){
        Interceptor interceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                if(TextUtils.isEmpty(webview_user_agent)){
                    //webview_user_agent = new WebView(context).getSettings().getUserAgentString();
                }
                if(TextUtils.isEmpty(androidId)){
                    androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
                }

                String osVersion = "";
                try {
                    osVersion = CommonUtils.urlEncode(Build.VERSION.RELEASE);
                } catch (UnsupportedEncodingException e) {
                    ////e.printStackTrace();
                }

                String userAgent = "";
                try {
                    userAgent = CommonUtils.urlEncode(System.getProperty("http.agent"));
                } catch (UnsupportedEncodingException e) {
                    ////e.printStackTrace();
                }

                Request.Builder builder = chain.request().newBuilder()
                        .addHeader("Authorization", HttpClientUtils.getAuthAPIKey())
                        .addHeader("x-screensize", CommonUtils.getDisplayMetricsDensityDPIInString(context))
                        .addHeader("x-model", getModelNumberInUrlEncode())
                        .addHeader("x-meid", CommonUtils.getMeid(context))
                        .addHeader("x-packagename", context.getPackageName())
                        .addHeader("x-versionname", CommonUtils.getVersionName(context))
                        .addHeader("x-versioncode", ""+ CommonUtils.getVersionCode(context)+"")
                        .addHeader("x-lang", CommonUtils.getCurrentDeviceLanguage(context))
                        .addHeader("x-platform", "android")//TODO : x-platform
                        .addHeader("x-os", osVersion)//TODO : x-os
                        .addHeader("x-token", PrefsData.getToken())
                        .addHeader("x-deviceid", androidId)//TODO : x-deviceid
                        .addHeader("x-useragent", userAgent)//TODO : x-useragent
                        .addHeader("User-Agent", userAgent);//TODO : User-Agent


                builder.addHeader("x-api", apiVersion);

                Request newRequest = builder.build();
                return chain.proceed(newRequest);
            }
        };
        return interceptor;
    }


    public static void setCustomPicassoSingletoneInstance(Context context, String apiVersion){
        Picasso.setSingletonInstance(getPicassoInstance(context,apiVersion));
    }


    private static Picasso getPicassoInstance(Context context, String apiVersion){
        Interceptor interceptor = getInterceptor(context, apiVersion);
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(HttpClientUtils.IMAGE_DEFAULT_CONNECT_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
                .readTimeout(HttpClientUtils.IMAGE_DEFAULT_READ_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
                .writeTimeout(HttpClientUtils.IMAGE_DEFAULT_WRITE_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
                .addInterceptor(interceptor)
                .build();
        Picasso picasso = new Picasso.Builder(context).downloader(new OkHttp3Downloader(client)).build();
        return picasso;
    }



    public static String getAuthBasic(){
        String credentials = PrefsData.getUserID()+":"+ PrefsData.getSecret();
        String result="";
        result = "Basic "+ Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
        return result;
    }

    public static String getAuthAPIKey(){
        String credentials = "APIKEY="+APIConstant.API_KEY;
        String result="";
        result = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
        return result;
    }

    public static void handleWebviewSSLError(Context context, final SslErrorHandler handler, SslError error){
        String sslMessage = "";
        switch(error.getPrimaryError()) {
            case SslError.SSL_DATE_INVALID:
                sslMessage = context.getText(R.string.notification_error_ssl_date_invalid).toString();
                break;
            case SslError.SSL_EXPIRED:
                sslMessage = context.getText(R.string.notification_error_ssl_expired).toString();
                break;
            case SslError.SSL_IDMISMATCH:
                sslMessage = context.getText(R.string.notification_error_ssl_idmismatch).toString();
                break;
            case SslError.SSL_INVALID:
                sslMessage = context.getText(R.string.notification_error_ssl_invalid).toString();
                break;
            case SslError.SSL_NOTYETVALID:
                sslMessage = context.getText(R.string.notification_error_ssl_not_yet_valid).toString();
                break;
            case SslError.SSL_UNTRUSTED:
                sslMessage = context.getText(R.string.notification_error_ssl_untrusted).toString();
                break;
            default:
                sslMessage = context.getText(R.string.notification_error_ssl_cert_invalid).toString();
        }

        CommonUtils.showDialog2Option(context, context.getText(R.string.notification_error_ssl_title).toString(), sslMessage,
                context.getText(R.string.notification_error_ssl_continue_text).toString(), new Runnable() {
                    @Override
                    public void run() {
                        handler.proceed();
                    }
                }, context.getText(R.string.notification_error_ssl_cancel_text).toString(), new Runnable() {
                    @Override
                    public void run() {
                        handler.cancel();
                    }
                });
    }
}
