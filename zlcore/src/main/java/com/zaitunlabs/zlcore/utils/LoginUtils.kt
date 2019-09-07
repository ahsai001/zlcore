package com.zaitunlabs.zlcore.utils

import android.app.Activity

import androidx.appcompat.app.AlertDialog

import com.zaitunlabs.zlcore.R
import com.zaitunlabs.zlcore.activities.BaseLoginActivity

/**
 * Created by ahsai on 6/19/2017.
 */

object LoginUtils {
    fun logout(activity: Activity, loginClass: Class<*>, classAfterLogin: Class<*>, runAfterLogout: Runnable?): AlertDialog {
        return CommonUtils.showDialog2Option(activity, activity.getString(R.string.zlcore_login_utils_logout_confirmation_title),
                activity.getString(R.string.zlcore_login_utils_logout_confirmation_message),
                activity.getString(R.string.zlcore_login_utils_logout_button_title), {
            PrefsData.setLogout()
            runAfterLogout?.run()
            BaseLoginActivity.start(activity, PrefsData.loginType, loginClass, classAfterLogin)
            activity.finish()
        }, activity.getString(R.string.zlcore_general_wording_cancel), { })
    }

    fun logout(activity: Activity, runAfterLogout: Runnable?): AlertDialog {
        return CommonUtils.showDialog2Option(activity, activity.getString(R.string.zlcore_login_utils_logout_confirmation_title),
                activity.getString(R.string.zlcore_login_utils_logout_confirmation_message),
                activity.getString(R.string.zlcore_login_utils_logout_button_title), {
            PrefsData.setLogout()
            runAfterLogout?.run()
        }, activity.getString(R.string.zlcore_general_wording_cancel), { })
    }

    fun relogin(activity: Activity, loginClass: Class<*>, runBeforeShowingLogin: Runnable?, classAfterLogin: Class<*>): AlertDialog {
        return CommonUtils.showDialog1Option(activity, activity.getString(R.string.zlcore_login_utils_relogin_confirmation_title),
                activity.getString(R.string.zlcore_login_utils_relogin_confirmation_message),
                activity.getString(R.string.zlcore_general_wording_ok)) {
            PrefsData.setLogout()
            runBeforeShowingLogin?.run()
            BaseLoginActivity.start(activity, PrefsData.loginType, loginClass, classAfterLogin)
            activity.finish()
        }
    }

    fun relogin(activity: Activity, loginClass: Class<*>, runBeforeShowingLogin: Runnable?, requestCode: Int): AlertDialog {
        return CommonUtils.showDialog1Option(activity, activity.getString(R.string.zlcore_login_utils_relogin_confirmation_title),
                activity.getString(R.string.zlcore_login_utils_relogin_confirmation_message),
                "OK") {
            PrefsData.setLogout()
            runBeforeShowingLogin?.run()
            BaseLoginActivity.startForResult(activity, PrefsData.loginType, loginClass, requestCode)
        }
    }
}
