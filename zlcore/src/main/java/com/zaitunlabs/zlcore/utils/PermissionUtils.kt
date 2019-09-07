package com.zaitunlabs.zlcore.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.IntRange
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat

import com.zaitunlabs.zlcore.R

import java.util.ArrayList

/**
 * Created by ahsai on 12/7/2017.
 */

class PermissionUtils(private val activityOrFragment: Any?, private val requestCode: Int, private val taskWillDo: Runnable, private val taskIfDenied: Runnable?) {


    private val context: Context?
        get() {
            var context: Context? = null
            if (activityOrFragment != null) {
                if (activityOrFragment is Activity) {
                    context = activityOrFragment
                } else if (activityOrFragment is Fragment) {
                    context = activityOrFragment.activity
                }
            }
            return context
        }

    private fun arePermissionsGranted(showDialogInit: Boolean, initTitle: String?, initBody: String?, vararg permissions: String): Boolean {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activityOrFragment != null && permissions != null) {
            val needRequested = ArrayList<String>()
            for (permission in permissions) {
                if (ContextCompat.checkSelfPermission(context!!, permission) != PackageManager.PERMISSION_GRANTED) {
                    needRequested.add(permission)
                }
            }

            if (needRequested.size > 0) {
                if (showDialogInit) {
                    CommonUtils.showDialog2Option(context, initTitle
                            ?: context!!.getString(R.string.zlcore_permission_utils_permission_title), initBody
                            ?: context!!.getString(R.string.zlcore_permission_utils_permission_message),
                            context!!.getString(R.string.zlcore_general_wording_ok), {
                        //need request permission
                        val needRequestedPermission = needRequested.toTypedArray<String>()
                        requestPermisssion(activityOrFragment, needRequestedPermission, requestCode)
                    }, context!!.getString(R.string.zlcore_general_wording_cancel), { taskIfDenied?.run() })
                } else {
                    //need request permission
                    val needRequestedPermission = needRequested.toTypedArray<String>()
                    requestPermisssion(activityOrFragment, needRequestedPermission, requestCode)
                }
                return false
            }
        }
        return true
    }

    private fun requestPermisssion(activityOrFragment: Any,
                                   needRequestedPermission: Array<String>, @IntRange(from = 0) requestCode: Int) {
        if (activityOrFragment is Activity) {
            ActivityCompat.requestPermissions(activityOrFragment, needRequestedPermission, requestCode)
        } else if (activityOrFragment is Fragment) {
            activityOrFragment.requestPermissions(needRequestedPermission, requestCode)
        }
    }


    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == this.requestCode) {
            // If request is cancelled, the result arrays are empty.
            val deniedPermissions = ArrayList<String>()
            val grantedPermissions = ArrayList<String>()
            if (grantResults.size > 0) {
                //partial cancelled
                var i = 0
                for (result in grantResults) {
                    if (result == PackageManager.PERMISSION_DENIED) {
                        deniedPermissions.add(permissions[i])
                    } else if (result == PackageManager.PERMISSION_GRANTED) {
                        grantedPermissions.add(permissions[i])
                    }
                    i++
                }
            } else {
                //all cancelled
            }

            if (grantedPermissions.size == permissions.size) {
                //do task
                this.taskWillDo.run()
            } else {
                //there is some denied
                var isAnyNeverAskAgainChecked = false
                if (activityOrFragment is Activity) {
                    for (deniedPermission in deniedPermissions) {
                        if (!ActivityCompat.shouldShowRequestPermissionRationale((activityOrFragment as Activity?)!!, deniedPermission)) {
                            isAnyNeverAskAgainChecked = true
                        }
                    }
                } else if (activityOrFragment is Fragment) {
                    for (deniedPermission in deniedPermissions) {
                        if (!activityOrFragment.shouldShowRequestPermissionRationale(deniedPermission)) {
                            isAnyNeverAskAgainChecked = true
                        }
                    }
                }
                if (isAnyNeverAskAgainChecked) {
                    CommonUtils.showDialog2Option(context, context!!.getString(R.string.zlcore_permission_utils_permission_title), context!!.getString(R.string.zlcore_permission_utils_permission_message_enable_from_setting),
                            context!!.getString(R.string.zlcore_general_wording_cancel), { }, context!!.getString(R.string.zlcore_permission_utils_permission_go_to_setting_title), {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri = Uri.fromParts("package", context!!.packageName, null)
                        intent.data = uri
                        context!!.startActivity(intent)
                    })

                }
                taskIfDenied?.run()
            }

        }
    }

    companion object {

        fun checkPermissionAndGo(activityOrFragment: Any, requestCode: Int, showDialogInit: Boolean, initTitle: String?, initBody: String?, taskWillDo: Runnable, taskIfDenied: Runnable?, vararg permissions: String): PermissionUtils? {
            var permissionUtils: PermissionUtils? = null
            if (permissions != null && permissions.size > 0) {
                permissionUtils = PermissionUtils(activityOrFragment, requestCode, taskWillDo, taskIfDenied)
                if (permissionUtils.arePermissionsGranted(showDialogInit, initTitle, initBody, *permissions)) {
                    taskWillDo.run()
                }
            } else {
                taskWillDo.run()
            }
            return permissionUtils
        }

        fun checkPermissionAndGo(activityOrFragment: Any, requestCode: Int, taskWillDo: Runnable, taskIfDenied: Runnable?, vararg permissions: String): PermissionUtils? {
            return checkPermissionAndGo(activityOrFragment, requestCode, true, null, null, taskWillDo, taskIfDenied, *permissions)
        }


        fun checkPermissionAndGo(activityOrFragment: Any, requestCode: Int, taskWillDo: Runnable, vararg permissions: String): PermissionUtils? {
            return checkPermissionAndGo(activityOrFragment, requestCode, taskWillDo, null, *permissions)
        }
    }
}
