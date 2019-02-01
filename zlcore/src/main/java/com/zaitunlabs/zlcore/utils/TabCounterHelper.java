package com.zaitunlabs.zlcore.utils;

import android.graphics.Color;
import com.google.android.material.tabs.TabLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.zaitunlabs.zlcore.R;

/**
 * Created by ahsai on 6/16/2017.
 */

public class TabCounterHelper {
    public static void prepareInfoCounter(String title, TabLayout.Tab tab){
        tab.setCustomView(R.layout.info_custom_tab_view);
        if(!TextUtils.isEmpty(title)){
            ((TextView)tab.getCustomView().findViewById(R.id.info_custom_tab_title_view)).setText(title);
        }
        updateInfoCounter(tab, 0);
    }

    public static void updateInfoCounter(TabLayout.Tab tab, int count){
        ImageView countView = (ImageView) tab.getCustomView().findViewById(R.id.info_custom_tab_image_view);
        if(count == 0){
            countView.setVisibility(View.GONE);
        }else{
            countView.setVisibility(View.VISIBLE);
            TextDrawable drawable1 = TextDrawable.builder().buildRoundRect(""+count, Color.RED, 10); // radius in px
            countView.setImageDrawable(drawable1);
        }
    }
}
