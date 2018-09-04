package com.zaitunlabs.zlcore.utils;

import android.view.View;

import java.lang.ref.WeakReference;

/**
 * Created by ahsai on 7/28/2018.
 */

public class ViewEnablerUtils extends ReachLostUtils {
    private WeakReference<View> targetViewRef;

    public ViewEnablerUtils(View targetView, int targetReportTotal){
        this.targetViewRef = new WeakReference<View>(targetView);
        setTargetedDoneTotal(targetReportTotal);
        setActionWhenReachTarget(new Runnable() {
            @Override
            public void run() {
                if(ViewEnablerUtils.this.targetViewRef != null) {
                    View targetView = ViewEnablerUtils.this.targetViewRef.get();
                    if(targetView != null) {
                        targetView.setEnabled(true);
                    }
                }
            }
        });
        setActionWhenLostTarget(new Runnable() {
            @Override
            public void run() {
                if(ViewEnablerUtils.this.targetViewRef != null) {
                    View targetView = ViewEnablerUtils.this.targetViewRef.get();
                    if(targetView != null) {
                        targetView.setEnabled(false);
                    }
                }
            }
        });
    }

    @Override
    public void init() {
        if(targetedDoneTotal > 0){
            if(ViewEnablerUtils.this.targetViewRef != null) {
                View targetView = ViewEnablerUtils.this.targetViewRef.get();
                if(targetView != null) {
                    targetView.setEnabled(false);
                }
            }
        }
    }
}
