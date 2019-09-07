package com.zaitunlabs.zlcore.api

/**
 * Created by ahsai on 6/9/2017.
 */

object APIConstant {
    val CACHED_TIME = 24 //hours
    var BASE_URL = "https://api.zaitunlabs.com"
    var API_VERSION = "v1"
    var API_KEY = ""
    var API_APPID = ""
    var API_OTHER_APPS = "https://api.zaitunlabs.com/genpro/v1/applist"
    var API_STORE = "https://api.zaitunlabs.com/genpro/v1/storelist"
    var API_SEND_FCM = "https://api.zaitunlabs.com/genpro/v1/registerfcm"
    var API_SEND_FCM_LOGIN = "https://api.zaitunlabs.com/genpro/v1/registerfcm/updatelogin"
    var API_CHECK_VERSION = "https://api.zaitunlabs.com/genpro/v1/checkversion"

    fun setBaseUrl(baseUrl: String) {
        BASE_URL = baseUrl
    }

    fun setApiVersion(apiVersion: String) {
        API_VERSION = apiVersion
    }

    fun setApiKey(apiKey: String) {
        API_KEY = apiKey
    }

    fun setApiAppid(apiAppid: String) {
        API_APPID = apiAppid
    }

    fun setApiOtherApps(apiOtherApps: String) {
        API_OTHER_APPS = apiOtherApps
    }

    fun setApiStore(apiStore: String) {
        API_STORE = apiStore
    }

    fun setApiSendFcm(apiSendFcm: String) {
        API_SEND_FCM = apiSendFcm
    }

    fun setApiSendFcmLogin(apiSendFcmLogin: String) {
        API_SEND_FCM_LOGIN = apiSendFcmLogin
    }


    fun setApiCheckVersion(apiCheckVersion: String) {
        API_CHECK_VERSION = apiCheckVersion
    }
}
