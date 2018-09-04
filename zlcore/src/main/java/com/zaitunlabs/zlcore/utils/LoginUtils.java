package com.zaitunlabs.zlcore.utils;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

import com.zaitunlabs.zlcore.activities.BaseLoginActivity;

/**
 * Created by ahsai on 6/19/2017.
 */

public class LoginUtils {
    public static AlertDialog logout(final Activity activity, final Class loginClass, final Class classAfterLogin, final Runnable runAfterLogout){
        return CommonUtils.showDialog2Option(activity, "Logout Confirmation",
                "Are you sure want to logout?",
                "logout", new Runnable() {
                    @Override
                    public void run() {
                        PrefsData.setLogout();
                        if(runAfterLogout != null) {
                            runAfterLogout.run();
                        }
                        BaseLoginActivity.start(activity, PrefsData.getLoginType(), loginClass, classAfterLogin);
                        activity.finish();
                    }
                }, "cancel", new Runnable() {
                    @Override
                    public void run() {

                    }
                });
    }

    public static AlertDialog logout(Activity activity, final Runnable runAfterLogout){
        return CommonUtils.showDialog2Option(activity, "Logout Confirmation",
                "Are you sure want to logout?",
                "logout", new Runnable() {
                    @Override
                    public void run() {
                        PrefsData.setLogout();
                        if(runAfterLogout != null) {
                            runAfterLogout.run();
                        }
                    }
                }, "cancel", new Runnable() {
                    @Override
                    public void run() {

                    }
                });
    }

    public static AlertDialog relogin(final Activity activity, final Class loginClass, final Runnable runBeforeShowingLogin, final Class classAfterLogin){
        return CommonUtils.showDialog1Option(activity, "Re-login Confirmation",
                "You need to re-login to continue?",
                "OK", new Runnable() {
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
        return CommonUtils.showDialog1Option(activity, "Re-login Confirmation",
                "You need to re-login to continue?",
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
