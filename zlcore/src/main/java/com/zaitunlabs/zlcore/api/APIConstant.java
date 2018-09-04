package com.zaitunlabs.zlcore.api;

/**
 * Created by ahsai on 6/9/2017.
 */

public class APIConstant {
    public static final int CACHED_TIME = 24; //hours
    public static String BASE_URL = "https://api.zaitunlabs.com";
    public static String API_VERSION = "v1";
    public static String API_KEY = "";
    public static String API_APPID = "";
    public static String API_OTHER_APPS = "https://api.zaitunlabs.com/genpro/v1/applist";
    public static String API_STORE = "https://api.zaitunlabs.com/genpro/v1/storelist";
    public static String API_SEND_FCM = "https://api.zaitunlabs.com/genpro/v1/registerfcm";
    public static String API_SEND_FCM_LOGIN = "https://api.zaitunlabs.com/genpro/v1/registerfcm/updatelogin";
    public static String API_CHECK_VERSION = "https://api.zaitunlabs.com/genpro/v1/checkversion";

    public static void setBaseUrl(String baseUrl){
        BASE_URL = baseUrl;
    }

    public static void setApiVersion(String apiVersion) {
        API_VERSION = apiVersion;
    }

    public static void setApiKey(String apiKey) {
        API_KEY = apiKey;
    }

    public static void setApiAppid(String apiAppid) {
        API_APPID = apiAppid;
    }

    public static void setApiOtherApps(String apiOtherApps) {
        API_OTHER_APPS = apiOtherApps;
    }

    public static void setApiStore(String apiStore) {
        API_STORE = apiStore;
    }

    public static void setApiSendFcm(String apiSendFcm) {
        API_SEND_FCM = apiSendFcm;
    }

    public static void setApiSendFcmLogin(String apiSendFcmLogin) {
        API_SEND_FCM_LOGIN = apiSendFcmLogin;
    }



    public static void setApiCheckVersion(String apiCheckVersion) {
        API_CHECK_VERSION = apiCheckVersion;
    }
}
