package com.zaitunlabs.zlcore.activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.core.content.ContextCompat
import android.text.TextUtils
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView


import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.google.gson.Gson
import com.zaitunlabs.zlcore.R
import com.zaitunlabs.zlcore.api.APIConstant
import com.zaitunlabs.zlcore.api.APIResponse
import com.zaitunlabs.zlcore.models.CheckVersionModel
import com.zaitunlabs.zlcore.core.BaseActivity
import com.zaitunlabs.zlcore.utils.CommonUtils
import com.zaitunlabs.zlcore.utils.HttpClientUtils
import com.zaitunlabs.zlcore.utils.PermissionUtils

import org.json.JSONObject


/**
 * Created by ahmad s on 8/31/2015.
 */

abstract class BaseSplashActivity : BaseActivity() {
    private var backgroundPane: RelativeLayout? = null
    private var titleTextView: TextView? = null
    private var iconView: ImageView? = null
    private var bottomTextView: TextView? = null
    private var checkVersionUrl: String? = null
    private var isContinueNextPage = false
    private var permissionUtils: PermissionUtils? = null

    protected abstract val isMeidIncluded: Boolean

    protected abstract val minimumSplashTimeInMS: Int
    internal var checkVersionModel: CheckVersionModel? = null

    protected abstract fun getCheckVersionUrl(): String

    protected abstract fun doNextAction(): Boolean


    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        super.onCreate(savedInstanceState)

        checkVersionUrl = CommonUtils.getStringIntent(intent, PARAM_CHECK_VERSION_URL, null)
        if (TextUtils.isEmpty(checkVersionUrl)) {
            checkVersionUrl = getCheckVersionUrl()
        }

        setContentView(R.layout.activity_splash_screen)

        backgroundPane = findViewById(R.id.splashscreen_background)
        iconView = findViewById(R.id.splashscreen_icon)
        titleTextView = findViewById(R.id.splashscreen_title)
        bottomTextView = findViewById(R.id.splashscreen_bottom_text)

        if (!TextUtils.isEmpty(checkVersionUrl)) {
            if (isMeidIncluded) {
                permissionUtils = PermissionUtils.checkPermissionAndGo(this, 1041, Runnable { pushyMeInit() }, Runnable {
                    CommonUtils.showToast(this@BaseSplashActivity, "Please give permission to run this application")
                    finish()
                }, Manifest.permission.READ_PHONE_STATE)
            } else {
                pushyMeInit()
            }
        } else {
            pushyMeInit()
        }
    }

    private fun doLogicWithUpdater() {
        if (checkVersionModel == null) {
            readyDoNextAction()
        } else {
            if (checkVersionModel!!.status == APIResponse.GENERIC_RESPONSE.OK) {
                readyDoNextAction()
            } else if (checkVersionModel!!.status == APIResponse.GENERIC_RESPONSE.NEED_UPDATE) {
                CommonUtils.showDialog3Option(this@BaseSplashActivity,
                        checkVersionModel!!.title,
                        checkVersionModel!!.message,
                        getString(R.string.zlcore_download_option_dialog_init), {
                    CommonUtils.openBrowser(this@BaseSplashActivity, checkVersionModel!!.detail)
                    finish()
                }, getString(R.string.zlcore_close_option_dialog_init), { finish() }, getString(R.string.zlcore_use_existing_option_dialog_init), { readyDoNextAction() })
            } else if (checkVersionModel!!.status == APIResponse.GENERIC_RESPONSE.NEED_SHOW_MESSAGE) {
                CommonUtils.showInfo(this@BaseSplashActivity,
                        checkVersionModel!!.title, checkVersionModel!!.message
                ) { readyDoNextAction() }
            } else {
                readyDoNextAction()
            }
        }
    }

    fun pushyMeInit() {
        Handler().postDelayed({
            if (isContinueNextPage) {
                doLogicWithUpdater()
            } else {
                isContinueNextPage = true
            }
        }, minimumSplashTimeInMS.toLong())

        if (!TextUtils.isEmpty(checkVersionUrl)) {
            AndroidNetworking.post(checkVersionUrl)
                    .setOkHttpClient(HttpClientUtils.getHTTPClient(this@BaseSplashActivity, APIConstant.API_VERSION, isMeidIncluded))
                    .addBodyParameter("appid", APIConstant.API_APPID)
                    .setPriority(Priority.HIGH)
                    .setTag("checkversion")
                    .build()
                    .getAsJSONObject(object : JSONObjectRequestListener {
                        override fun onResponse(response: JSONObject) {
                            val gson = Gson()
                            checkVersionModel = gson.fromJson<CheckVersionModel>(response.toString(), CheckVersionModel::class.java!!)

                            if (isContinueNextPage) {
                                doLogicWithUpdater()
                            } else {
                                isContinueNextPage = true
                            }
                        }

                        override fun onError(error: ANError) {
                            if (isContinueNextPage) {
                                doLogicWithUpdater()
                            } else {
                                isContinueNextPage = true
                            }
                        }
                    })
        } else {
            isContinueNextPage = true
        }

    }

    private fun readyDoNextAction() {
        if (doNextAction()) {
            finish()
        }
    }


    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
    }


    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        AndroidNetworking.cancel("checkversion")
        super.onDestroy()
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (permissionUtils != null) {
            permissionUtils!!.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    protected fun setBackgroundPaneImage(resid: Int) {
        backgroundPane!!.setBackgroundResource(resid)
    }

    protected fun setBackgroundPaneColor(resIdColor: Int) {
        backgroundPane!!.setBackgroundColor(ContextCompat.getColor(this, resIdColor))
    }

    protected fun setImageIcon(resid: Int) {
        iconView!!.visibility = View.VISIBLE
        iconView!!.setImageResource(resid)
    }

    protected fun setTitleTextView(title: String, resIdColor: Int) {
        titleTextView!!.visibility = View.VISIBLE
        titleTextView!!.text = title
        titleTextView!!.setTextColor(ContextCompat.getColor(this, resIdColor))
    }

    protected fun setBottomTextView(bottomText: String, resIdColor: Int) {
        bottomTextView!!.visibility = View.VISIBLE
        bottomTextView!!.text = bottomText
        bottomTextView!!.setTextColor(ContextCompat.getColor(this, resIdColor))
    }

    companion object {
        val PARAM_CHECK_VERSION_URL = "param_check_version_url"

        fun showSplashScreen(context: Context, checkVersionUrl: String, splashClass: Class<*>) {
            val splashIntent = Intent(context, splashClass)
            //splashIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            //splashIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            splashIntent.putExtra(PARAM_CHECK_VERSION_URL, checkVersionUrl)
            context.startActivity(splashIntent)
        }
    }


}
