package com.zaitunlabs.zlcore.utils;

import android.os.Handler;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by ahmad s on 2019-07-04.
 */
public class MultipleClickHandler {
    private int delayTime;
    private MultipleClickListener multipleClickListener;
    private AtomicInteger mCounter = new AtomicInteger();
    private Handler handler = new Handler();
    private Runnable mRunnable = new Runnable(){
        @Override
        public void run(){
            mCounter = new AtomicInteger();
        }
    };

    public MultipleClickHandler(int delayTime, MultipleClickListener multipleClickListener){
        this.delayTime = delayTime;
        this.multipleClickListener = multipleClickListener;
    }

    public void handle(){
        handler.removeCallbacks(mRunnable);
        handler.postDelayed(mRunnable, delayTime);
        multipleClickListener.onHandle(mCounter.incrementAndGet());
    }

    public interface MultipleClickListener{
        void onHandle(int totalClick);
    }


}
