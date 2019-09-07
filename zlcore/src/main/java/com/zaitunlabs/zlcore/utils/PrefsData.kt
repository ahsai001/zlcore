package com.zaitunlabs.zlcore.utils


import android.text.TextUtils

/**
 * Created by ahmad s on 9/5/2015.
 */

object PrefsData {
    private val NAME = "name"
    private val USERID = "userid"
    private val SECRET = "secret"
    private val TOKEN = "token"
    private val EMAIL = "email"
    private val PHONE = "phone"
    private val PHOTO = "photo"
    private val ISLOGIN = "islogin"
    private val LOGINTYPE = "logintype"
    private val PUSHY_TOKEN = "pushy_token"
    private val PUSHY_TOKEN_SENT = "pushy_token_sent"
    private val PUSHY_TOKEN_LOGIN_SENT = "pushy_token_login_sent"


    var name: String?
        get() = Hawk.get(NAME, "")
        set(value) = Hawk.put(NAME, value)

    val isAccountLogin: Boolean
        get() = Hawk.get(ISLOGIN, false) && !TextUtils.isEmpty(secret) && !TextUtils.isEmpty(token)

    var isLogin: Boolean
        get() = Hawk.get(ISLOGIN, false)
        set(value) = Hawk.put(ISLOGIN, value)

    var photo: String?
        get() = Hawk.get(PHOTO, null)
        set(value) = Hawk.put(PHOTO, value)

    var userID: String?
        get() = Hawk.get(USERID, "")
        set(value) = Hawk.put(USERID, value)

    var email: String?
        get() = Hawk.get(EMAIL, "")
        set(value) = Hawk.put(EMAIL, value)

    var phone: String?
        get() = Hawk.get(PHONE, "")
        set(value) = Hawk.put(PHONE, value)

    var token: String?
        get() = Hawk.get(TOKEN, "")
        set(value) = Hawk.put(TOKEN, value)


    var secret: String?
        get() = Hawk.get(SECRET, "")
        set(value) = Hawk.put(SECRET, value)

    var pushyToken: String?
        get() = Hawk.get(PUSHY_TOKEN, "")
        set(value) = Hawk.put(PUSHY_TOKEN, value)

    var pushyTokenSent: Boolean
        get() = Hawk.get(PUSHY_TOKEN_SENT, true)
        set(value) = Hawk.put(PUSHY_TOKEN_SENT, value)

    var pushyTokenLoginSent: Boolean
        get() = Hawk.get(PUSHY_TOKEN_LOGIN_SENT, true)
        set(value) = Hawk.put(PUSHY_TOKEN_LOGIN_SENT, value)


    var loginType: String?
        get() = Hawk.get(LOGINTYPE, "")
        set(loginType) = Hawk.put(LOGINTYPE, loginType)

    fun setLogout() {
        val userId = userID
        var loginType = loginType
        val pushToken = pushyToken
        val pushTokenSent = pushyTokenSent
        clearAllData()
        userID = userId
        loginType = loginType
        pushyToken = pushToken
        pushyTokenSent = pushTokenSent
    }

    fun getPhoto(defaultValue: String): String? {
        return Hawk.get(PHOTO, defaultValue)
    }

    fun getName(defaultValue: String): String? {
        return Hawk.get(NAME, defaultValue)
    }

    fun getUserID(defaultValue: String): String? {
        return Hawk.get(USERID, defaultValue)
    }

    fun getEmail(defaultValue: String): String? {
        return Hawk.get(EMAIL, defaultValue)
    }

    fun getPhone(defaultValue: String): String? {
        return Hawk.get(PHONE, defaultValue)
    }

    fun getToken(defaultValue: String): String? {
        return Hawk.get(TOKEN, defaultValue)
    }

    fun getSecret(defaultValue: String): String? {
        return Hawk.get(SECRET, defaultValue)
    }


    fun clearAllData() {
        Hawk.remove(NAME)
        Hawk.remove(USERID)
        Hawk.remove(EMAIL)
        Hawk.remove(PHONE)
        Hawk.remove(PHOTO)
        Hawk.remove(SECRET)
        Hawk.remove(TOKEN)
        Hawk.remove(ISLOGIN)
        Hawk.remove(LOGINTYPE)
        Hawk.remove(PUSHY_TOKEN)
        Hawk.remove(PUSHY_TOKEN_SENT)
        Hawk.remove(PUSHY_TOKEN_LOGIN_SENT)
    }


}
