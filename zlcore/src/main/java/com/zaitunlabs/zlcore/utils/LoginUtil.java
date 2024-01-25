package com.zaitunlabs.zlcore.utils;

import android.app.Activity;

import androidx.appcompat.app.AlertDialog;

import com.zaitunlabs.zlcore.R;
import com.zaitunlabs.zlcore.activities.BaseLoginActivity;

/**
 * Created by ahsai on 6/19/2017.
 */

public class LoginUtil {
    public static AlertDialog logout(final Activity activity, final Class loginClass, final Class classAfterLogin, final Runnable runAfterLogout){
        return CommonUtil.showDialog2Option(activity, activity.getString(R.string.zlcore_login_utils_logout_confirmation_title),
                activity.getString(R.string.zlcore_login_utils_logout_confirmation_message),
                activity.getString(R.string.zlcore_login_utils_logout_button_title), new Runnable() {
                    @Override
                    public void run() {
                        PrefsData.setLogout();
                        if(runAfterLogout != null) {
                            runAfterLogout.run();
                        }
                        BaseLoginActivity.start(activity, PrefsData.getLoginType(), loginClass, classAfterLogin);
                        activity.finish();
                    }
                }, activity.getString(R.string.zlcore_general_wording_cancel), new Runnable() {
                    @Override
                    public void run() {

                    }
                });
    }

    public static AlertDialog logout(Activity activity, final Runnable runAfterLogout){
        return CommonUtil.showDialog2Option(activity, activity.getString(R.string.zlcore_login_utils_logout_confirmation_title),
                activity.getString(R.string.zlcore_login_utils_logout_confirmation_message),
                activity.getString(R.string.zlcore_login_utils_logout_button_title), new Runnable() {
                    @Override
                    public void run() {
                        PrefsData.setLogout();
                        if(runAfterLogout != null) {
                            runAfterLogout.run();
                        }
                    }
                }, activity.getString(R.string.zlcore_general_wording_cancel), new Runnable() {
                    @Override
                    public void run() {

                    }
                });
    }

    public static AlertDialog relogin(final Activity activity, final Class loginClass, final Runnable runBeforeShowingLogin, final Class classAfterLogin){
        return CommonUtil.showDialog1Option(activity, activity.getString(R.string.zlcore_login_utils_relogin_confirmation_title),
                activity.getString(R.string.zlcore_login_utils_relogin_confirmation_message),
                activity.getString(R.string.zlcore_general_wording_ok), new Runnable() {
                    @Override
                    public void run() {
                        PrefsData.setLogout();
                        if(runBeforeShowingLogin != null) {
                            runBeforeShowingLogin.run();
                        }
                        BaseLoginActivity.start(activity, PrefsData.getLoginType(), loginClass, classAfterLogin);
                        activity.finish();
                    }
                });
    }

    public static AlertDialog relogin(final Activity activity, final Class loginClass, final Runnable runBeforeShowingLogin, final int requestCode){
        return CommonUtil.showDialog1Option(activity, activity.getString(R.string.zlcore_login_utils_relogin_confirmation_title),
                activity.getString(R.string.zlcore_login_utils_relogin_confirmation_message),
                "OK", new Runnable() {
                    @Override
                    public void run() {
                        PrefsData.setLogout();
                        if(runBeforeShowingLogin != null) {
                            runBeforeShowingLogin.run();
                        }
                        BaseLoginActivity.startForResult(activity, PrefsData.getLoginType(), loginClass, requestCode);
                    }
                });
    }
}
