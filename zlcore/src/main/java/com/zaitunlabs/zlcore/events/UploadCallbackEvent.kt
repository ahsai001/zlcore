package com.zaitunlabs.zlcore.events

import com.androidnetworking.error.ANError
import com.zaitunlabs.zlcore.services.DataIntentService

import org.json.JSONObject

/**
 * Created by ahsai on 6/27/2018.
 */

class UploadCallbackEvent(var tag: String?, var result: JSONObject?, var error: ANError?, var extras: DataIntentService.Extras?)
