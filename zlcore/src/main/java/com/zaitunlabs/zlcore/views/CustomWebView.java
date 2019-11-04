package com.zaitunlabs.zlcore.views;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.util.AttributeSet;
import android.webkit.WebView;

import androidx.annotation.RequiresApi;

/**
 * Created by ahmad s on 2019-11-04.
 */
public class CustomWebView extends WebView {
    public CustomWebView(Context context) {
        super(getFixedContext(context));
    }

    public CustomWebView(Context context, AttributeSet attrs) {
        super(getFixedContext(context), attrs);
    }

    public CustomWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(getFixedContext(context), attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CustomWebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(getFixedContext(context), attrs, defStyleAttr, defStyleRes);
    }

    // To fix Android Lollipop WebView problem create a new configuration on that Android version only
    private static Context getFixedContext(Context context) {
        if (Build.VERSION.SDK_INT == 21 || Build.VERSION.SDK_INT == 22) // Android Lollipop 5.0 & 5.1
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                return context.createConfigurationContext(new Configuration());
            }
        return context;
    }
}
