package com.zaitunlabs.zlcore.utils;


import android.os.Handler;

import androidx.viewpager.widget.ViewPager;


public abstract class ViewPageSliderUtil {
    private Handler handler;
    private Runnable runnable;
    private int delay = 2000;
    private int page = 0;
    private boolean isRunning = false;

    public abstract ViewPager getViewPager();

    public ViewPageSliderUtil(){
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                if (getViewPager().getAdapter().getCount() == page) {
                    page = 0;
                } else {
                    page++;
                }
                getViewPager().setCurrentItem(page, true);
                handler.postDelayed(this, delay);
            }
        };
    }

    public ViewPageSliderUtil setDelay(int delay){
        this.delay = delay;
        return this;
    }

    public synchronized void start(){
        if(!isRunning) {
            isRunning = true;
            handler.postDelayed(runnable,delay);
        }
    }


    public synchronized void stop(){
        isRunning = false;
        handler.removeCallbacks(runnable);
    }
}
