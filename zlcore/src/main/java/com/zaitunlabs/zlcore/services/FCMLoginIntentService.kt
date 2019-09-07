package com.zaitunlabs.zlcore.services

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.os.Looper
import android.text.TextUtils

import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.google.gson.GsonBuilder
import com.zaitunlabs.zlcore.api.APIConstant
import com.zaitunlabs.zlcore.api.APIResponse
import com.zaitunlabs.zlcore.fragments.InfoFragment
import com.zaitunlabs.zlcore.models.GenericResponseModel
import com.zaitunlabs.zlcore.utils.CommonUtils
import com.zaitunlabs.zlcore.utils.HttpClientUtils
import com.zaitunlabs.zlcore.utils.PrefsData

import org.json.JSONObject

import java.lang.reflect.Modifier
import androidx.core.app.JobIntentService

class FCMLoginIntentService : JobIntentService() {
    private val TAG = FCMLoginIntentService::class.java!!.getSimpleName()

    private var isMeid: Boolean = false

    override fun onHandleWork(intent: Intent) {
        onHandleIntent(intent)
    }

    protected fun onHandleIntent(intent: Intent?) {
        if (intent != null) {
            val action = intent.action
            if (ACTION_SEND_TOKEN == action) {
                handleActionSendToken(intent)
            }
        }
    }

    private fun handleActionSendToken(intent: Intent) {
        val appid = CommonUtils.getStringIntent(intent, PARAM_APPID, "-1")

        if (isProcessing) return
        isProcessing = true


        isMeid = CommonUtils.getBooleanIntent(intent, PARAM_IS_MEID, false)

        if (TextUtils.isEmpty(PrefsData.pushyToken)) {
            //it means pushy.Me not yet generate token, please waiting and retry
            isProcessing = false
            FCMLoginIntentService.startSending(this, appid, isMeid, (2 * 1000).toLong())
        } else {
            if (!PrefsData.pushyTokenLoginSent && PrefsData.isAccountLogin) {
                AndroidNetworking.post(APIConstant.API_SEND_FCM_LOGIN)
                        .setOkHttpClient(HttpClientUtils.getHTTPClient(this, APIConstant.API_VERSION, isMeid))
                        .addUrlEncodeFormBodyParameter("fcmid", PrefsData.pushyToken)
                        .addUrlEncodeFormBodyParameter("appid", appid)
                        .setPriority(Priority.HIGH)
                        .setTag("updateloginfcm$this")
                        .build()
                        .getAsJSONObject(object : JSONObjectRequestListener {
                            override fun onResponse(response: JSONObject) {
                                val responseModel = GsonBuilder()
                                        .excludeFieldsWithoutExposeAnnotation()
                                        .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                                        .create()
                                        .fromJson<GenericResponseModel>(response.toString(), GenericResponseModel::class.java!!)

                                isProcessing = false
                                if (responseModel.status == APIResponse.GENERIC_RESPONSE.OK) {
                                    PrefsData.pushyTokenLoginSent = true
                                } else if (responseModel.status == APIResponse.GENERIC_RESPONSE.FAILED) {
                                    FCMLoginIntentService.startSending(this@FCMLoginIntentService, appid, isMeid, (2 * 1000).toLong())
                                }
                            }

                            override fun onError(anError: ANError) {
                                isProcessing = false
                                anError.printStackTrace()
                            }
                        })
            } else {
                isProcessing = false
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
    }

    companion object {
        val PARAM_IS_MEID = InfoFragment.PARAM_IS_MEID
        val JOB_ID = 11000013

        private val ACTION_SEND_TOKEN = "com.zaitunlabs.zlcore.services.action.SEND_TOKEN"
        private var isProcessing = false

        val PARAM_APPID = "param_appid"

        fun startSending(context: Context, appid: String, isMeid: Boolean) {
            val intent = Intent(context, FCMLoginIntentService::class.java)
            intent.action = ACTION_SEND_TOKEN
            intent.putExtra(PARAM_APPID, appid)
            intent.putExtra(PARAM_IS_MEID, isMeid)
            JobIntentService.enqueueWork(context, FCMLoginIntentService::class.java!!, JOB_ID, intent)
        }

        fun startSending(context: Context, appid: String, isMeid: Boolean, delayInMillis: Long) {
            android.os.Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(context, FCMLoginIntentService::class.java)
                intent.action = ACTION_SEND_TOKEN
                intent.putExtra(PARAM_APPID, appid)
                intent.putExtra(PARAM_IS_MEID, isMeid)
                JobIntentService.enqueueWork(context, FCMLoginIntentService::class.java!!, JOB_ID, intent)
            }, delayInMillis)
        }
    }
}
