package com.zaitunlabs.zlcore.api

/**
 * Created by ahsai on 6/9/2017.
 */

class APIResponse {
    object HTTPCode {
        val OK = 200
        val INVALID_METHOD = 405
    }

    object GENERIC_RESPONSE {
        var OK = 1
        var NEED_UPDATE = 2
        var NEED_SHOW_MESSAGE = 3
        var FAILED = -1
        var NEED_LOGIN = -10
    }
}
