package com.zaitunlabs.zlcore.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.transition.TransitionManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;

import com.zaitunlabs.zlcore.R;
import com.zaitunlabs.zlcore.core.BaseActivity;
import com.zaitunlabs.zlcore.utils.CommonUtils;
import com.zaitunlabs.zlcore.utils.Prefs;

import java.util.List;

/**
 * Created by ahsai on 4/6/2018.
 */

public abstract class BaseOnBoardingActivity extends BaseActivity {
    public final String ONBOARDING_COMPLETED_STATE = TAG+"_onboarding_complete";
    public final static String ONBOARDING_ONLY_SHOWN = "onboarding_only_shown";
    private ViewPager pager;
    private Button skip;
    private Button next;
    private boolean onBoardingOnlyShown;

    protected abstract List<Fragment> getFragmentList();
    protected abstract void doGetStarted();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getFragmentList() == null){
            finishOnboarding();
            return;
        }

        if(getFragmentList().size() <= 0){
            finishOnboarding();
            return;
        }

        onBoardingOnlyShown = CommonUtils.getBooleanIntent(getIntent(),ONBOARDING_ONLY_SHOWN,false);
        if(!onBoardingOnlyShown){
            if(Prefs.with(this).getBoolean(ONBOARDING_COMPLETED_STATE,false)){
                finishOnboarding();
                return;
            }
        }



        setContentView(R.layout.activity_onboarding);

        pager = (ViewPager)findViewById(R.id.onboarding_pager);
        skip = (Button)findViewById(R.id.onboarding_skip);
        next = (Button)findViewById(R.id.onboarding_next);

        FragmentStatePagerAdapter adapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return getFragmentList().get(position);
            }

            @Override
            public int getCount() {
                return getFragmentList().size();
            }
        };

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position == getFragmentList().size() - 1){
                    if(onBoardingOnlyShown){
                        next.setText(R.string.zlcore_onboarding_close);
                    } else {
                        next.setText(R.string.zlcore_onboarding_get_started);
                    }
                } else {
                    next.setText(R.string.zlcore_onboarding_next);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        pager.setAdapter(adapter);

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishOnboarding();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pager.getCurrentItem() == getFragmentList().size()-1) { // The last screen
                    finishOnboarding();
                } else {
                    pager.setCurrentItem(
                            pager.getCurrentItem() + 1,
                            true
                    );
                }
            }
        });
    }

    private void finishOnboarding() {
        Prefs.with(this).save(ONBOARDING_COMPLETED_STATE,true);
        if(!onBoardingOnlyShown) {
            doGetStarted();
        }
        finish();
    }

    public static void start(Context context, Class OnBoardingClass){
        Intent i = new Intent(context, OnBoardingClass);
        i.putExtra(ONBOARDING_ONLY_SHOWN, false);
        context.startActivity(i);
    }

    public static void showOnly(Context context, Class OnBoardingClass){
        Intent i = new Intent(context, OnBoardingClass);
        i.putExtra(ONBOARDING_ONLY_SHOWN, true);
        context.startActivity(i);
    }

}
