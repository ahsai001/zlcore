package com.zaitunlabs.zlcore.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;

import com.zaitunlabs.zlcore.R;

import java.util.ArrayList;

/**
 * Created by ahsai on 12/7/2017.
 */

public class PermissionUtils {
    private int requestCode;
    private Runnable taskWillDo;
    private Runnable taskIfDenied;
    private Object activityOrFragment;

    public PermissionUtils(Object activityOrFragment, int requestCode, Runnable taskWillDo, Runnable taskIfDenied){
        this.activityOrFragment = activityOrFragment;
        this.requestCode = requestCode;
        this.taskWillDo = taskWillDo;
        this.taskIfDenied = taskIfDenied;
    }

    public static PermissionUtils checkPermissionAndGo(Object activityOrFragment, int requestCode, boolean showDialogInit, String initTitle, String initBody, Runnable taskWillDo, Runnable taskIfDenied,String... permissions){
        PermissionUtils permissionUtils = null;
        if(permissions != null && permissions.length > 0) {
            permissionUtils = new PermissionUtils(activityOrFragment, requestCode, taskWillDo, taskIfDenied);
            if (permissionUtils.arePermissionsGranted(showDialogInit, initTitle, initBody, permissions)) {
                taskWillDo.run();
            }
        } else {
            taskWillDo.run();
        }
        return  permissionUtils;
    }

    public static PermissionUtils checkPermissionAndGo(Object activityOrFragment, int requestCode, Runnable taskWillDo, Runnable taskIfDenied,String... permissions){
        return  checkPermissionAndGo(activityOrFragment, requestCode, true, null, null, taskWillDo, taskIfDenied, permissions);
    }


    public static PermissionUtils checkPermissionAndGo(Object activityOrFragment, int requestCode, Runnable taskWillDo,String... permissions){
        return checkPermissionAndGo(activityOrFragment, requestCode, taskWillDo, null, permissions);
    }


    private Context getContext(){
       Context context = null;
        if(activityOrFragment != null){
            if(activityOrFragment instanceof Activity){
                context = (Activity) activityOrFragment;
            } else if (activityOrFragment instanceof Fragment){
                context = ((Fragment)activityOrFragment).getActivity();
            }
        }
        return context;
    }

    private boolean arePermissionsGranted(boolean showDialogInit, String initTitle, String initBody, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activityOrFragment != null && permissions != null) {
            final ArrayList<String> needRequested = new ArrayList<>();
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(getContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                    needRequested.add(permission);
                }
            }

            if(needRequested.size() > 0){
                if(showDialogInit) {
                    CommonUtils.showDialog2Option(getContext(), initTitle==null?getContext().getString(R.string.zlcore_permission_utils_permission_title):initTitle, initBody==null?getContext().getString(R.string.zlcore_permission_utils_permission_message):initBody,
                            getContext().getString(R.string.zlcore_general_wording_ok), new Runnable() {
                                @Override
                                public void run() {
                                    //need request permission
                                    String[] needRequestedPermission = needRequested.toArray(new String[needRequested.size()]);
                                    requestPermisssion(activityOrFragment, needRequestedPermission, requestCode);
                                }
                            }, getContext().getString(R.string.zlcore_general_wording_cancel), new Runnable() {
                                @Override
                                public void run() {
                                    if (taskIfDenied != null) taskIfDenied.run();
                                }
                            });
                } else {
                    //need request permission
                    String[] needRequestedPermission = needRequested.toArray(new String[needRequested.size()]);
                    requestPermisssion(activityOrFragment, needRequestedPermission, requestCode);
                }
                return false;
            }
        }
        return true;
    }

    private void requestPermisssion(final @NonNull Object activityOrFragment,
                                    final @NonNull String[] needRequestedPermission, final @IntRange(from = 0) int requestCode){
        if(activityOrFragment instanceof Activity) {
            ActivityCompat.requestPermissions((Activity) activityOrFragment, needRequestedPermission, requestCode);
        } else if(activityOrFragment instanceof Fragment){
            ((Fragment)activityOrFragment).requestPermissions(needRequestedPermission, requestCode);
        }
    }


    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        if(requestCode == this.requestCode){
            // If request is cancelled, the result arrays are empty.
            ArrayList<String> deniedPermissions = new ArrayList<>();
            ArrayList<String> grantedPermissions = new ArrayList<>();
            if (grantResults.length > 0) {
                //partial cancelled
                int i = 0;
                for (int result : grantResults){
                    if(result == PackageManager.PERMISSION_DENIED){
                        deniedPermissions.add(permissions[i]);
                    }else if((result == PackageManager.PERMISSION_GRANTED)){
                        grantedPermissions.add(permissions[i]);
                    }
                    i++;
                }
            } else {
                //all cancelled
            }

            if(grantedPermissions.size() == permissions.length){
                //do task
                this.taskWillDo.run();
            }else{
                //there is some denied
                boolean isAnyNeverAskAgainChecked = false;
                if(activityOrFragment instanceof Activity) {
                    for (String deniedPermission : deniedPermissions) {
                        if (!ActivityCompat.shouldShowRequestPermissionRationale((Activity) activityOrFragment, deniedPermission)) {
                            isAnyNeverAskAgainChecked = true;
                        }
                    }
                } else if(activityOrFragment instanceof Fragment){
                    for (String deniedPermission : deniedPermissions) {
                        if (!((Fragment) activityOrFragment).shouldShowRequestPermissionRationale(deniedPermission)) {
                            isAnyNeverAskAgainChecked = true;
                        }
                    }
                }
                if(isAnyNeverAskAgainChecked){
                    CommonUtils.showDialog2Option(getContext(), getContext().getString(R.string.zlcore_permission_utils_permission_title), getContext().getString(R.string.zlcore_permission_utils_permission_message_enable_from_setting),
                            getContext().getString(R.string.zlcore_general_wording_cancel), new Runnable() {
                                @Override
                                public void run() {

                                }
                            }, getContext().getString(R.string.zlcore_permission_utils_permission_go_to_setting_title), new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", getContext().getPackageName(), null);
                                    intent.setData(uri);
                                    getContext().startActivity(intent);
                                }
                            });

                }
                if(taskIfDenied != null)taskIfDenied.run();
            }

        }
    }
}
