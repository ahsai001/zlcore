package com.zaitunlabs.zlcore.services

import android.content.Context
import android.content.Intent
import androidx.core.app.JobIntentService

import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.ANRequest
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.DownloadProgressListener
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.androidnetworking.interfaces.UploadProgressListener
import com.zaitunlabs.zlcore.api.APIConstant
import com.zaitunlabs.zlcore.events.UploadCallbackEvent
import com.zaitunlabs.zlcore.fragments.InfoFragment
import com.zaitunlabs.zlcore.utils.CommonUtils
import com.zaitunlabs.zlcore.utils.HttpClientUtils
import com.zaitunlabs.zlcore.utils.NotificationProgressUtils

import org.greenrobot.eventbus.EventBus
import org.json.JSONObject

import java.io.File
import java.io.Serializable
import java.util.HashMap

/**
 * Created by ahsai on 2/19/2018.
 */

class DataIntentService : JobIntentService() {
    private val TAG = DataIntentService::class.java!!.getSimpleName()


    protected fun onHandleIntent(intent: Intent?) {
        if (intent != null) {
            val action = intent.action
            if (ACTION_UPLOAD == action) {
                handleActionUpload(intent)
            } else if (ACTION_POST == action) {
                handleActionPost(intent)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onHandleWork(intent: Intent) {
        onHandleIntent(intent)
    }

    private fun handleActionUpload(intent: Intent) {
        val url = CommonUtils.getStringIntent(intent, PARAM_URL, null)
        val icon = CommonUtils.getIntIntent(intent, PARAM_ICON, 1)
        val title = CommonUtils.getStringIntent(intent, PARAM_TITLE, null)
        val desc = CommonUtils.getStringIntent(intent, PARAM_DESC, null)
        val notifID = CommonUtils.getIntIntent(intent, PARAM_NOTIFID, -1)
        val files = CommonUtils.getSerializableIntent(intent, PARAM_FILES, null) as FileParts
        val headers = CommonUtils.getSerializableIntent(intent, PARAM_HEADERS, null) as HeaderParts
        val bodys = CommonUtils.getSerializableIntent(intent, PARAM_BODYS, null) as BodyParts
        val extras = CommonUtils.getSerializableIntent(intent, PARAM_EXTRAS, null) as Extras
        val isMeid = CommonUtils.getBooleanIntent(intent, PARAM_IS_MEID, false)
        val tag = CommonUtils.getStringIntent(intent, PARAM_TAG, null)

        val progressUtils = NotificationProgressUtils(this,
                title, desc, icon, notifID)

        val builder = AndroidNetworking.upload(url)
                .setOkHttpClient(HttpClientUtils.getHTTPClient(this, APIConstant.API_VERSION, isMeid, true))


        if (headers != null) {
            builder.addHeaders(headers.getHeaderList())
        }

        builder.addMultipartFile(files.getFilePartList())

        if (bodys != null) {
            builder.addMultipartParameter(bodys.getBodyList())
        }


        builder.setTag(ACTION_UPLOAD + this.toString())
                .setPriority(Priority.HIGH)
                //.setExecutor(Executors.newSingleThreadExecutor()) // setting an executor to get response or completion on that executor thread
                .build()
                .setUploadProgressListener { bytesUploaded, totalBytes -> progressUtils.setProgress(100, Math.floor((bytesUploaded * 100 / totalBytes).toDouble()).toInt()) }
                .getAsJSONObject(object : JSONObjectRequestListener {
                    override fun onResponse(response: JSONObject) {
                        progressUtils.setComplete(response.optString("message"))
                        EventBus.getDefault().post(UploadCallbackEvent(tag, response, null, extras))
                    }

                    override fun onError(error: ANError) {
                        progressUtils.setComplete(error.errorDetail)
                        EventBus.getDefault().post(UploadCallbackEvent(tag, null, error, extras))
                    }
                })
    }


    private fun handleActionPost(intent: Intent) {
        val url = CommonUtils.getStringIntent(intent, PARAM_URL, null)
        val icon = CommonUtils.getIntIntent(intent, PARAM_ICON, 1)
        val title = CommonUtils.getStringIntent(intent, PARAM_TITLE, null)
        val desc = CommonUtils.getStringIntent(intent, PARAM_DESC, null)
        val notifID = CommonUtils.getIntIntent(intent, PARAM_NOTIFID, -1)
        val headers = CommonUtils.getSerializableIntent(intent, PARAM_HEADERS, null) as HeaderParts
        val bodys = CommonUtils.getSerializableIntent(intent, PARAM_BODYS, null) as BodyParts
        val extras = CommonUtils.getSerializableIntent(intent, PARAM_EXTRAS, null) as Extras
        val isMeid = CommonUtils.getBooleanIntent(intent, PARAM_IS_MEID, false)
        val tag = CommonUtils.getStringIntent(intent, PARAM_TAG, null)

        val progressUtils = NotificationProgressUtils(this,
                title, desc, icon, notifID)

        val builder = AndroidNetworking.post(url)
                .setOkHttpClient(HttpClientUtils.getHTTPClient(this, APIConstant.API_VERSION, isMeid))

        if (headers != null) {
            builder.addHeaders(headers.getHeaderList())
        }

        if (bodys != null) {
            builder.addBodyParameter(bodys.getBodyList())
        }


        builder.setTag(ACTION_POST + this.toString())
                .setPriority(Priority.HIGH)
                //.setExecutor(Executors.newSingleThreadExecutor()) // setting an executor to get response or completion on that executor thread
                .build()
                .setDownloadProgressListener { bytesDownloaded, totalBytes -> progressUtils.setProgress(100, Math.floor((bytesDownloaded * 100 / totalBytes).toDouble()).toInt()) }
                .getAsJSONObject(object : JSONObjectRequestListener {
                    override fun onResponse(response: JSONObject) {
                        progressUtils.setComplete(response.optString("message"))
                        EventBus.getDefault().post(UploadCallbackEvent(tag, response, null, extras))
                    }

                    override fun onError(error: ANError) {
                        progressUtils.setComplete(error.errorDetail)
                        EventBus.getDefault().post(UploadCallbackEvent(tag, null, error, extras))
                    }
                })
    }

    class HeaderParts : Serializable {
        private val headerList: MutableMap<String, String>

        init {
            headerList = HashMap()
        }

        fun addItem(key: String, value: String): HeaderParts {
            headerList[key] = value
            return this
        }

        fun addItemCollection(headerList: Map<String, String>): HeaderParts {
            for ((key, value) in headerList) {
                addItem(key, value)
            }
            return this
        }

        fun getHeaderList(): Map<String, String> {
            return headerList
        }
    }

    class FileParts : Serializable {
        private val filePartList: MutableMap<String, File>

        init {
            filePartList = HashMap()
        }

        fun addItem(key: String, value: File): FileParts {
            filePartList[key] = value
            return this
        }

        fun addItemCollection(fileList: Map<String, File>): FileParts {
            for ((key, value) in fileList) {
                addItem(key, value)
            }
            return this
        }

        fun getFilePartList(): Map<String, File> {
            return filePartList
        }
    }

    class BodyParts : Serializable {
        private val bodyList: MutableMap<String, String>

        init {
            bodyList = HashMap()
        }

        fun addItem(key: String, value: String): BodyParts {
            bodyList[key] = value
            return this
        }

        fun addItemCollection(bodyList: Map<String, String>): BodyParts {
            for ((key, value) in bodyList) {
                addItem(key, value)
            }
            return this
        }

        fun getBodyList(): Map<String, String> {
            return bodyList
        }
    }

    class Extras : Serializable {
        private val extraList: MutableMap<String, Any>

        init {
            extraList = HashMap()
        }

        fun addItem(key: String, value: Any): Extras {
            extraList[key] = value
            return this
        }

        fun addItemCollection(extraList: Map<String, String>): Extras {
            for ((key, value) in extraList) {
                addItem(key, value)
            }
            return this
        }

        fun getExtraList(): Map<String, Any> {
            return extraList
        }
    }

    companion object {
        val JOB_ID = 11000012

        private val ACTION_UPLOAD = "com.zaitunlabs.zlcore.services.action.UPLOAD"
        private val ACTION_POST = "com.zaitunlabs.zlcore.services.action.POST"
        private val ACTION_DOWNLOAD = "com.zaitunlabs.zlcore.services.action.DOWNLOAD"
        private val PARAM_URL = "param_url"
        private val PARAM_ICON = "param_icon"
        private val PARAM_TITLE = "param_title"
        private val PARAM_DESC = "param_desc"
        private val PARAM_NOTIFID = "param_notifid"
        private val PARAM_FILES = "param_files"
        private val PARAM_HEADERS = "param_headers"
        private val PARAM_BODYS = "param_bodys"
        private val PARAM_EXTRAS = "param_extras"
        private val PARAM_IS_MEID = InfoFragment.PARAM_IS_MEID
        private val PARAM_TAG = "param_tag"


        fun startUpload(context: Context, url: String, icon: Int, title: String, desc: String,
                        notifID: Int, files: FileParts, headers: HeaderParts, bodys: BodyParts, extras: Extras, isMeid: Boolean, tag: String) {
            val intent = Intent(context, DataIntentService::class.java)
            intent.action = ACTION_UPLOAD
            intent.putExtra(PARAM_URL, url)
            intent.putExtra(PARAM_ICON, icon)
            intent.putExtra(PARAM_TITLE, title)
            intent.putExtra(PARAM_DESC, desc)
            intent.putExtra(PARAM_NOTIFID, notifID)
            intent.putExtra(PARAM_FILES, files)
            intent.putExtra(PARAM_HEADERS, headers)
            intent.putExtra(PARAM_BODYS, bodys)
            intent.putExtra(PARAM_EXTRAS, extras)
            intent.putExtra(PARAM_IS_MEID, isMeid)
            intent.putExtra(PARAM_TAG, tag)
            JobIntentService.enqueueWork(context, DataIntentService::class.java!!, JOB_ID, intent)
        }


        fun startPost(context: Context, url: String, icon: Int, title: String, desc: String,
                      notifID: Int, headers: HeaderParts, bodys: BodyParts, extras: Extras, isMeid: Boolean, tag: String) {
            val intent = Intent(context, DataIntentService::class.java)
            intent.action = ACTION_POST
            intent.putExtra(PARAM_URL, url)
            intent.putExtra(PARAM_ICON, icon)
            intent.putExtra(PARAM_TITLE, title)
            intent.putExtra(PARAM_DESC, desc)
            intent.putExtra(PARAM_NOTIFID, notifID)
            intent.putExtra(PARAM_HEADERS, headers)
            intent.putExtra(PARAM_BODYS, bodys)
            intent.putExtra(PARAM_EXTRAS, extras)
            intent.putExtra(PARAM_IS_MEID, isMeid)
            intent.putExtra(PARAM_TAG, tag)
            JobIntentService.enqueueWork(context, DataIntentService::class.java!!, JOB_ID, intent)
        }
    }
}
