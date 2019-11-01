package com.zaitunlabs.zlcore.utils;

import android.view.View;

import java.lang.ref.WeakReference;

/**
 * Created by ahsai on 7/28/2018.
 */

public class ViewEnablerUtil extends ReachLostUtil {
    private WeakReference<View> targetViewRef;

    public ViewEnablerUtil(View targetView, int targetReportTotal){
        this.targetViewRef = new WeakReference<View>(targetView);
        setTargetedDoneTotal(targetReportTotal);
        setActionWhenReachTarget(new Runnable() {
            @Override
            public void run() {
                if(ViewEnablerUtil.this.targetViewRef != null) {
                    View targetView = ViewEnablerUtil.this.targetViewRef.get();
                    if(targetView != null) {
                        targetView.setEnabled(true);
                    }
                }
            }
        });
        setActionWhenLostTarget(new Runnable() {
            @Override
            public void run() {
                if(ViewEnablerUtil.this.targetViewRef != null) {
                    View targetView = ViewEnablerUtil.this.targetViewRef.get();
                    if(targetView != null) {
                        targetView.setEnabled(false);
                    }
                }
            }
        });
    }

    @Override
    public void init() {
        super.init();
        if(targetedDoneTotal > 0){
            if(ViewEnablerUtil.this.targetViewRef != null) {
                View targetView = ViewEnablerUtil.this.targetViewRef.get();
                if(targetView != null) {
                    targetView.setEnabled(false);
                }
            }
        }
    }
}
