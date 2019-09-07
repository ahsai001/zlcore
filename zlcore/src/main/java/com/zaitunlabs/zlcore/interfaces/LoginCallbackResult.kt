package com.zaitunlabs.zlcore.interfaces

/**
 * Created by ahsai on 5/27/2018.
 */

interface LoginCallbackResult {
    fun setSuccess(token: String, name: String, phone: String, email: String, photoUrl: String)
    fun setFailed()
}
