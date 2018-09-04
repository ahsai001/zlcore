package com.zaitunlabs.zlcore.utils;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.ColorRes;
import android.support.v4.widget.SwipeRefreshLayout;

import java.lang.ref.WeakReference;

/**
 * Created by ahsai on 7/18/2018.
 */

public class SwipeRefreshLayoutUtils {
    private WeakReference<SwipeRefreshLayout> swipeRefreshLayoutRef;
    private Runnable refreshAction;
    public static SwipeRefreshLayoutUtils init(final SwipeRefreshLayout swipeRefreshLayout, Runnable refreshAction) {
        final SwipeRefreshLayoutUtils swipeRefreshLayoutUtils = new SwipeRefreshLayoutUtils();
        if(swipeRefreshLayout != null) {
            swipeRefreshLayoutUtils.swipeRefreshLayoutRef = new WeakReference<SwipeRefreshLayout>(swipeRefreshLayout);
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    if(swipeRefreshLayoutUtils.refreshAction != null){
                        swipeRefreshLayoutUtils.doRefresh(swipeRefreshLayoutUtils.refreshAction);
                    }
                }
            });
            swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                    android.R.color.holo_green_light,
                    android.R.color.holo_orange_light,
                    android.R.color.holo_red_light);

        }
        swipeRefreshLayoutUtils.refreshAction = refreshAction;
        return swipeRefreshLayoutUtils;
    }

    private void doRefresh(Runnable refreshAction){
        refreshAction.run();
        //new Handler(Looper.getMainLooper()).post(refreshAction);
    }

    public void setColorSchemeResources(@ColorRes int... colorResIds){
        if(swipeRefreshLayoutRef != null) {
            SwipeRefreshLayout swipeRefreshLayout = swipeRefreshLayoutRef.get();
            if(swipeRefreshLayout != null){
                swipeRefreshLayout.setColorSchemeResources(colorResIds);
            }
        }
    }

    public void setColorSchemeColors(int... colors){
        if(swipeRefreshLayoutRef != null) {
            SwipeRefreshLayout swipeRefreshLayout = swipeRefreshLayoutRef.get();
            if(swipeRefreshLayout != null){
                swipeRefreshLayout.setColorSchemeColors(colors);
            }
        }
    }

    public void setEnabled(boolean enabled){
        if(swipeRefreshLayoutRef != null) {
            SwipeRefreshLayout swipeRefreshLayout = swipeRefreshLayoutRef.get();
            if(swipeRefreshLayout != null){
                swipeRefreshLayout.setEnabled(enabled);
            }
        }
    }

    public void setProgressBackgroundColorSchemeColor(int color){
        if(swipeRefreshLayoutRef != null) {
            SwipeRefreshLayout swipeRefreshLayout = swipeRefreshLayoutRef.get();
            if(swipeRefreshLayout != null){
                swipeRefreshLayout.setProgressBackgroundColorSchemeColor(color);
            }
        }
    }

    public void setProgressBackgroundColorSchemeResource(int colorRes){
        if(swipeRefreshLayoutRef != null) {
            SwipeRefreshLayout swipeRefreshLayout = swipeRefreshLayoutRef.get();
            if(swipeRefreshLayout != null){
                swipeRefreshLayout.setProgressBackgroundColorSchemeResource(colorRes);
            }
        }
    }

    public boolean refreshNow(){
        if(swipeRefreshLayoutRef != null && refreshAction != null) {
            SwipeRefreshLayout swipeRefreshLayout = swipeRefreshLayoutRef.get();
            if(swipeRefreshLayout != null){
                swipeRefreshLayout.setRefreshing(true);
                doRefresh(refreshAction);
                return true;
            }
        }
        return false;
    }

    public void refreshDone(){
        if(swipeRefreshLayoutRef != null) {
            SwipeRefreshLayout swipeRefreshLayout = swipeRefreshLayoutRef.get();
            if(swipeRefreshLayout != null){
                swipeRefreshLayout.setRefreshing(false);
            }
        }
    }
}
