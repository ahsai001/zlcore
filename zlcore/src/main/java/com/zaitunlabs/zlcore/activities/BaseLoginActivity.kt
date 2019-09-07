package com.zaitunlabs.zlcore.activities

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import androidx.core.content.ContextCompat
import android.text.TextUtils
import android.view.KeyEvent
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.webkit.URLUtil
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Spinner
import android.widget.TextView

import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.squareup.picasso.Picasso
import com.zaitunlabs.zlcore.R
import com.zaitunlabs.zlcore.api.APIResponse
import com.zaitunlabs.zlcore.core.BaseActivity
import com.zaitunlabs.zlcore.interfaces.LoginCallbackResult
import com.zaitunlabs.zlcore.utils.CommonUtils
import com.zaitunlabs.zlcore.utils.HttpClientUtils
import com.zaitunlabs.zlcore.utils.PermissionUtils
import com.zaitunlabs.zlcore.utils.PrefsData
import com.zaitunlabs.zlcore.utils.ViewUtils

import org.json.JSONObject

import java.util.ArrayList
import java.util.HashMap

import de.hdodenhof.circleimageview.CircleImageView

abstract class BaseLoginActivity : BaseActivity(), LoginCallbackResult {

    // UI references.
    var userIDView: TextInputEditText? = null
        private set
    var passwordView: TextInputEditText? = null
        private set
    private var mUserIDTILView: TextInputLayout? = null
    private var mPasswordTILView: TextInputLayout? = null
    private var mProgressView: View? = null
    private var mLoginFormView: View? = null
    private var titleView: TextView? = null
    private var infoView: TextView? = null
    var loginButton: Button? = null
        private set
    private var iconView: CircleImageView? = null
    private var loginTypeSelectorPanel: LinearLayout? = null

    internal var permissionUtils: PermissionUtils? = null
    internal var isOverridePreviousLogin: Boolean = false

    internal var nextActivity: Class<*>
    internal var appType: String
    internal var requestCode: Int = 0
    internal var appInfo: String


    protected abstract val userIdHint: String
    protected abstract val userIdFieldName: String
    protected abstract val userIdInvalidMessage: String
    protected abstract val passwordHint: String
    protected abstract val passwordFieldName: String
    protected abstract val passwordInvalidMessage: String
    protected abstract val buttonLoginText: String

    protected abstract val loginTypeFieldName: String
    protected abstract val iconUrl: String
    protected abstract val iconResId: Int
    protected abstract val loginExplaination: String
    protected abstract val loginUrl: String
    protected abstract val apiVersion: String
    protected abstract val isMeidIncluded: Boolean
    protected abstract val loginTypeViewValueList: HashMap<String, String>?
    protected abstract val defaultValueLoginType: String?
    protected abstract val isHandleCustomSuccessResponse: Boolean
    protected abstract val isHandleCustomLogin: Boolean
    protected abstract fun isUserIDValid(userId: String): Boolean
    protected abstract fun isPasswordValid(password: String): Boolean
    protected abstract fun clearAllCache(): Boolean
    protected abstract fun getCookedPassword(rawPassword: String): String
    protected abstract fun handleCustomSuccessResponse(response: JSONObject, loginCallbackResult: LoginCallbackResult)
    protected abstract fun handleCustomData(data: JSONObject)
    protected abstract fun handleCustomLogin(appType: String, username: String, password: String, loginCallbackResult: LoginCallbackResult)

    fun setBackground(resId: Int) {
        val loginFormRoot = findViewById<View>(R.id.login_form_root) as RelativeLayout
        loginFormRoot.setBackgroundResource(resId)
    }

    fun setBackground(drawable: Drawable) {
        val loginFormRoot = findViewById<View>(R.id.login_form_root) as RelativeLayout
        loginFormRoot.background = drawable
    }

    fun setProgresBarTextColor(color: Int) {
        val progressBarTitleView = mProgressView!!.findViewById<View>(R.id.login_progress_title) as TextView
        progressBarTitleView.setTextColor(color)
    }

    fun setProgresBarBackgroundColor(color: Int) {
        mProgressView!!.setBackgroundColor(color)
    }

    fun setHeaderBackground(resId: Int) {
        val loginFormHeader = findViewById<View>(R.id.login_form_header) as LinearLayout
        loginFormHeader.setBackgroundResource(resId)
    }

    fun setHeaderBackground(drawable: Drawable) {
        val loginFormHeader = findViewById<View>(R.id.login_form_header) as LinearLayout
        loginFormHeader.background = drawable
    }

    fun setHeaderBackgroundColor(color: Int) {
        val loginFormHeader = findViewById<View>(R.id.login_form_header) as LinearLayout
        loginFormHeader.setBackgroundColor(color)
    }

    fun setBodyBackground(resId: Int) {
        val loginFormBody = findViewById<View>(R.id.login_form_body) as LinearLayout
        loginFormBody.setBackgroundResource(resId)
    }

    fun setBodyBackground(drawable: Drawable) {
        val loginFormBody = findViewById<View>(R.id.login_form_body) as LinearLayout
        loginFormBody.background = drawable
    }

    fun setBodyBackgroundColor(color: Int) {
        val loginFormBody = findViewById<View>(R.id.login_form_body) as LinearLayout
        loginFormBody.setBackgroundColor(color)
    }


    fun setFooterText(text: String) {
        val footerView = findViewById<View>(R.id.login_form_footerView) as TextView
        footerView.visibility = View.VISIBLE
        if (!TextUtils.isEmpty(text)) {
            footerView.text = text
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        nextActivity = CommonUtils.getSerializableIntent(intent, EXTRA_NEXT_ACTIVITY, null) as Class<*>
        appType = CommonUtils.getStringIntent(intent, EXTRA_APP_TYPE, null)
        requestCode = currentRequestCode
        appInfo = loginExplaination


        // Set up the login form.
        userIDView = findViewById<View>(R.id.login_form_userid) as TextInputEditText
        mUserIDTILView = findViewById<View>(R.id.login_form_userid_textinputlayout) as TextInputLayout
        mUserIDTILView!!.hint = userIdHint + if (TextUtils.isEmpty(appInfo)) "" else "*"


        passwordView = findViewById<View>(R.id.login_form_password) as TextInputEditText
        passwordView!!.setOnEditorActionListener(TextView.OnEditorActionListener { textView, id, keyEvent ->
            if (id == R.id.login || id == EditorInfo.IME_NULL) {
                attemptLogin()
                return@OnEditorActionListener true
            }
            false
        })

        mPasswordTILView = findViewById<View>(R.id.login_form_password_textinputlayout) as TextInputLayout
        mPasswordTILView!!.hint = passwordHint + if (TextUtils.isEmpty(appInfo)) "" else "*"

        loginButton = findViewById<View>(R.id.login_form_button) as Button
        loginButton!!.text = buttonLoginText
        loginButton!!.setOnClickListener { attemptLogin() }

        loginButton!!.background = ViewUtils.getSelectableItemBackgroundWithColor(this@BaseLoginActivity,
                ContextCompat.getColor(this@BaseLoginActivity, R.color.colorPrimary))

        titleView = findViewById<View>(R.id.login_form_title) as TextView
        titleView!!.setTextColor(ContextCompat.getColor(this@BaseLoginActivity, R.color.colorPrimary))

        mLoginFormView = findViewById(R.id.login_form)
        mProgressView = findViewById(R.id.login_progress)


        iconView = findViewById<View>(R.id.login_form_icon) as CircleImageView
        if (!TextUtils.isEmpty(iconUrl) && URLUtil.isValidUrl(iconUrl)) {
            Picasso.get().load(iconUrl).noPlaceholder().error(R.drawable.ic_error).into(iconView)
        } else if (iconResId > 0) {
            Picasso.get().load(iconResId).noPlaceholder().into(iconView)
        }

        loginTypeSelectorPanel = findViewById<View>(R.id.loginTypeSelectorPanel) as LinearLayout
        if (loginTypeViewValueList != null && loginTypeViewValueList!!.size > 0) {
            val loginTypeSpinner = loginTypeSelectorPanel!!.findViewById<Spinner>(R.id.loginTypeSpinner)

            val spinnerViewList = ArrayList<String>()
            val spinnerValueList = ArrayList<String>()

            for ((key, value) in loginTypeViewValueList!!) {
                spinnerViewList.add(key)
                spinnerValueList.add(value)
            }

            val spinnerArrayAdapter = ArrayAdapter(this,
                    android.R.layout.simple_spinner_item,
                    spinnerViewList)
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            loginTypeSpinner.adapter = spinnerArrayAdapter
            loginTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
                    appType = spinnerValueList[i]
                }

                override fun onNothingSelected(adapterView: AdapterView<*>) {

                }
            }

            if (defaultValueLoginType != null) {
                loginTypeSpinner.setSelection(spinnerValueList.indexOf(defaultValueLoginType))
            }

            loginTypeSelectorPanel!!.visibility = View.VISIBLE
        } else {
            loginTypeSelectorPanel!!.visibility = View.GONE
        }

        if (!TextUtils.isEmpty(appInfo)) {
            infoView = findViewById<View>(R.id.login_form_credential_infoView) as TextView
            infoView!!.visibility = View.VISIBLE
            infoView!!.text = "*$appInfo"
        }
    }


    protected fun setLoginButtonMatchParent() {
        val newLinearLayout = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        newLinearLayout.setMargins(0, CommonUtils.getPixelFromDip(this@BaseLoginActivity, 32).toInt(), 0, 0)
        loginButton!!.layoutParams = newLinearLayout
    }


    fun attemptLogin() {
        if (isMeidIncluded) {
            permissionUtils = PermissionUtils.checkPermissionAndGo(this, 1053, Runnable { loginProcess() }, Runnable {
                CommonUtils.showToast(this@BaseLoginActivity, getString(R.string.zlcore_warning_please_give_permission))
                finish()
            }, Manifest.permission.READ_PHONE_STATE)
        } else {
            loginProcess()
        }

    }

    private fun loginProcess() {
        CommonUtils.hideKeyboard(this@BaseLoginActivity, mLoginFormView!!)

        // Reset errors.
        userIDView!!.error = null
        passwordView!!.error = null

        // Store values at the time of the login attempt.
        val userId = userIDView!!.text!!.toString()
        val password = passwordView!!.text!!.toString()

        var cancel = false
        var focusView: View? = null

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            passwordView!!.error = getString(R.string.zlcore_error_field_required)
            focusView = passwordView
            cancel = true
        }

        if (!isPasswordValid(password)) {
            passwordView!!.error = if (TextUtils.isEmpty(passwordInvalidMessage))
                getString(R.string.zlcore_error_invalid_password)
            else
                passwordInvalidMessage
            focusView = passwordView
            cancel = true
        }

        if (TextUtils.isEmpty(userId)) {
            userIDView!!.error = getString(R.string.zlcore_error_field_required)
            focusView = userIDView
            cancel = true
        }

        if (!isUserIDValid(userId)) {
            userIDView!!.error = if (TextUtils.isEmpty(userIdInvalidMessage))
                getString(R.string.zlcore_error_invalid_userid)
            else
                userIdInvalidMessage
            focusView = userIDView
            cancel = true
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView!!.requestFocus()
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true)

            isOverridePreviousLogin = !TextUtils.isEmpty(PrefsData.userID) && userIDView!!.text!!.toString() != PrefsData.userID

            //set username - password to localdata
            val username = userIDView!!.text!!.toString()
            val cookedPassword = getCookedPassword(passwordView!!.text!!.toString())
            PrefsData.userID = username
            PrefsData.secret = cookedPassword


            if (isHandleCustomLogin) {
                handleCustomLogin(appType, userIDView!!.text!!.toString(), passwordView!!.text!!.toString(), this@BaseLoginActivity)
            } else {
                //do hit api
                AndroidNetworking.post(loginUrl)
                        .setOkHttpClient(HttpClientUtils.getHTTPClient(this@BaseLoginActivity, apiVersion, isMeidIncluded))
                        .addUrlEncodeFormBodyParameter(if (TextUtils.isEmpty(userIdFieldName)) "username" else userIdFieldName, username)
                        .addUrlEncodeFormBodyParameter(if (TextUtils.isEmpty(passwordFieldName)) "password" else passwordFieldName, cookedPassword)
                        .addUrlEncodeFormBodyParameter(if (TextUtils.isEmpty(loginTypeFieldName)) "loginType" else loginTypeFieldName, appType)
                        .setPriority(Priority.HIGH)
                        .setTag("login")
                        .build()
                        .getAsJSONObject(object : JSONObjectRequestListener {
                            override fun onResponse(response: JSONObject) {
                                if (isHandleCustomSuccessResponse) {
                                    handleCustomSuccessResponse(response, this@BaseLoginActivity)
                                } else {
                                    val status = response.optInt("status")
                                    val message = response.optString("message")
                                    if (status == APIResponse.GENERIC_RESPONSE.OK) {
                                        //success login
                                        val data = response.optJSONObject("data")
                                        val token = data.optString("token", null)
                                        val name = data.optString("name", null)
                                        val phone = data.optString("phone", null)
                                        val email = data.optString("email", null)
                                        val photo = data.optString("photo", null)
                                        setSuccess(token, name, phone, email, photo)
                                        handleCustomData(data)
                                        CommonUtils.showToast(this@BaseLoginActivity, message)
                                    } else {
                                        setFailed()
                                        CommonUtils.showSnackBar(this@BaseLoginActivity, message)
                                    }
                                }
                            }

                            override fun onError(anError: ANError) {
                                setFailed()
                                if (anError.errorCode != 0) {
                                    // received error from server
                                    // anError.getErrorCode() - the error code from server
                                    // anError.getErrorBody() - the error body from server
                                    // anError.getErrorDetail() - just an error detail

                                    CommonUtils.showSnackBar(this@BaseLoginActivity, anError.errorDetail)
                                } else {
                                    // error.getErrorDetail() : connectionError, parseError, requestCancelledError
                                    CommonUtils.showSnackBar(this@BaseLoginActivity, anError.errorDetail)
                                }
                            }
                        })
            }
        }
    }

    override fun setSuccess(token: String, name: String, phone: String, email: String, photoUrl: String) {
        PrefsData.isLogin = true
        PrefsData.loginType = appType
        PrefsData.token = token
        PrefsData.name = name
        PrefsData.phone = phone
        PrefsData.email = email
        PrefsData.photo = photoUrl

        if (isOverridePreviousLogin) {
            //clear old cached
            clearAllCache()
        }

        showProgress(false)

        if (requestCode > -1) {
            val dataIntent = Intent()
            dataIntent.putExtra("appType", appType)
            dataIntent.putExtra("token", token)
            dataIntent.putExtra("name", name)
            dataIntent.putExtra("phone", phone)
            dataIntent.putExtra("email", email)
            setResult(Activity.RESULT_OK, dataIntent)
        } else {
            val nextIntent = Intent(this@BaseLoginActivity, nextActivity)
            this@BaseLoginActivity.startActivity(nextIntent)
        }

        finish()
    }

    override fun setFailed() {
        showProgress(false)
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (permissionUtils != null) {
            permissionUtils!!.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }


    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private fun showProgress(show: Boolean) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            val shortAnimTime = resources.getInteger(android.R.integer.config_mediumAnimTime)

            mLoginFormView!!.visibility = if (show) View.GONE else View.VISIBLE
            mLoginFormView!!.animate().setDuration(shortAnimTime.toLong()).alpha(
                    (if (show) 0 else 1).toFloat()).setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    mLoginFormView!!.visibility = if (show) View.GONE else View.VISIBLE
                }
            })

            mProgressView!!.visibility = if (show) View.VISIBLE else View.GONE
            mProgressView!!.animate().setDuration(shortAnimTime.toLong()).alpha(
                    (if (show) 1 else 0).toFloat()).setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    mProgressView!!.visibility = if (show) View.VISIBLE else View.GONE
                }
            })
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView!!.visibility = if (show) View.VISIBLE else View.GONE
            mLoginFormView!!.visibility = if (show) View.GONE else View.VISIBLE
        }
    }


    override fun onDestroy() {
        AndroidNetworking.cancel("login")
        super.onDestroy()
    }

    companion object {
        private val EXTRA_NEXT_ACTIVITY = "nextActivity"
        private val EXTRA_APP_TYPE = "appType"

        fun start(context: Context, appType: String, loginClass: Class<*>, afterLoginActivity: Class<*>) {
            val intent = Intent(context, loginClass)
            intent.putExtra(EXTRA_NEXT_ACTIVITY, afterLoginActivity)
            intent.putExtra(EXTRA_APP_TYPE, appType)
            context.startActivity(intent)
        }

        fun startForResult(context: Activity, appType: String, loginClass: Class<*>, requestCode: Int) {
            val intent = Intent(context, loginClass)
            intent.putExtra(EXTRA_APP_TYPE, appType)
            context.startActivityForResult(intent, requestCode)
        }
    }

}

