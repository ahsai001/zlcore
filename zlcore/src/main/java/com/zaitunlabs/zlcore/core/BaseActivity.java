package com.zaitunlabs.zlcore.core;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.transition.Transition;

import com.zaitunlabs.zlcore.R;
import com.zaitunlabs.zlcore.utils.ApplicationWacther;
import com.zaitunlabs.zlcore.utils.CommonUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ahsai on 7/14/2017.
 */

public class BaseActivity extends AppCompatActivity {
    protected final String TAG = BaseActivity.this.getClass().getSimpleName();
    protected static final String EXTRA_REQUEST_CODE =  "extra_requestCode";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ApplicationWacther.getInstance(this).registerActivity(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        ApplicationWacther.getInstance(this).reportActivityStartEvent(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ApplicationWacther.getInstance(this).setLastActivityResumed(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        ApplicationWacther.getInstance(this).reportActivityStopEvent(this);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        //unregister this new activity from watcher
        ApplicationWacther.getInstance(this).unregisterActivity(this);
        if(isTaskRoot()){

        }

        for (AsyncTask asyncTask : asyncTaskList) {
            if (asyncTask.getStatus() == AsyncTask.Status.RUNNING){
                asyncTask.cancel(true);
            }
            asyncTaskList.remove(asyncTask);
        }

        super.onDestroy();
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        ApplicationWacther.getInstance(this).setConfigurationChanged(true);
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        ApplicationWacther.getInstance(this).setConfigurationChanged(false);
        super.onRestoreInstanceState(savedInstanceState);
    }


    protected void enableUpNavigation(){
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    protected <T extends BaseFragment> T showFragment(int frameLayoutID, Class<T> clazz, PostFragmentInstantiation<T> postFragmentInstantiation, Bundle savedInstanceState, String TAG){
        return showFragment(frameLayoutID, clazz, postFragmentInstantiation, savedInstanceState, false, TAG);
    }
    protected <T extends BaseFragment> T showFragment(int frameLayoutID, Class<T> clazz, PostFragmentInstantiation<T> postFragmentInstantiation, Bundle savedInstanceState, boolean ignoreStateOrAlwaysCreateNew, String TAG){
        T fragment = null;
        if(savedInstanceState == null || ignoreStateOrAlwaysCreateNew){
            try {
                fragment = clazz.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            if(fragment == null) return null;

            if(postFragmentInstantiation != null) {
                postFragmentInstantiation.postInstantiation(fragment);
            }

            getSupportFragmentManager().beginTransaction()
                    .replace(frameLayoutID, fragment, TAG).commit();
        } else {
            fragment = (T) getSupportFragmentManager().findFragmentByTag(TAG);
            if(fragment == null){
                return showFragment(frameLayoutID, clazz, postFragmentInstantiation, null, true, TAG);
            }
        }
        return fragment;
    }

    public abstract class PostFragmentInstantiation<T extends BaseFragment>{
        public abstract void postInstantiation(T fragment);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        intent.putExtra(EXTRA_REQUEST_CODE, requestCode);
        super.startActivityForResult(intent, requestCode);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode, @Nullable Bundle options) {
        intent.putExtra(EXTRA_REQUEST_CODE, requestCode);
        super.startActivityForResult(intent, requestCode, options);
    }

    protected int getRequestCode(Intent intent){
        int requestCode = CommonUtil.getIntIntent(intent, EXTRA_REQUEST_CODE,-1);
        return requestCode;
    }

    protected int getCurrentRequestCode(){
        return getRequestCode(getIntent());
    }


    public void setupEnterWindowAnimations(Transition enterTransition){
        getWindow().setEnterTransition(enterTransition);
        getWindow().setAllowEnterTransitionOverlap(false);
    }

    public void setupEnterWindowAnimations(Transition enterTransition, Transition returnTransition){
        getWindow().setEnterTransition(enterTransition);
        getWindow().setReturnTransition(returnTransition);
        getWindow().setAllowEnterTransitionOverlap(false);
        getWindow().setAllowReturnTransitionOverlap(false);
    }

    public void setupExitWindowAnimations(Transition exitTransition){
        getWindow().setExitTransition(exitTransition);
    }

    public void setupExitWindowAnimations(Transition exitTransition, Transition reEnterTransition){
        getWindow().setExitTransition(exitTransition);
        getWindow().setReenterTransition(reEnterTransition);
    }

    private boolean canExit = false;
    private int defaultMaxIntervalBackToExit = 3000;
    protected void onBackPressedTwiceToExit(){
        onBackPressedTwiceToExit(defaultMaxIntervalBackToExit, null);
    }
    protected void onBackPressedTwiceToExit(int maxInterval){
        onBackPressedTwiceToExit(maxInterval, null);
    }
    protected void onBackPressedTwiceToExit(String message){
        onBackPressedTwiceToExit(defaultMaxIntervalBackToExit, message);
    }
    protected void onBackPressedTwiceToExit(int maxInterval, String message){
        if(!canExit){
            canExit = true;
            if(TextUtils.isEmpty(message)) {
                CommonUtil.showSnackBar(this, getString(R.string.zlcore_warning_press_once_again_to_close_app));
            } else {
                CommonUtil.showSnackBar(this, message);
            }
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    canExit = false;
                }
            }, maxInterval);
        } else {
            super.onBackPressed();
        }
    }


    private List<AsyncTask> asyncTaskList = new ArrayList<>();
    protected void addAsync(AsyncTask asyncTask){
        asyncTaskList.add(asyncTask);
    }

    protected void removeAsync(AsyncTask asyncTask){
        asyncTaskList.remove(asyncTask);
    }
}
