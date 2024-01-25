package com.zaitunlabs.zlcore.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ernestoyaquello.com.verticalstepperform.VerticalStepperFormLayout;


/**
 * Created by ahsai on 7/17/2017.
 */

public class CustomVerticalStepper extends VerticalStepperFormLayout {
    public CustomVerticalStepper(Context context) {
        super(context);
    }

    public CustomVerticalStepper(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomVerticalStepper(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public void makeTitleMultiLine(){
        if(stepsTitlesViews != null && stepsTitlesViews.size() > 0) {
            for (TextView textView : stepsTitlesViews) {
                ViewGroup.LayoutParams params = textView.getLayoutParams();
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                textView.setLayoutParams(params);
            }
        }
    }

    HeadClickListener headClickListener = null;

    public void setActionOnTitleAndSubTitle(final HeadClickListener headClickListener){
        this.headClickListener = headClickListener;
        if(stepsTitlesViews != null && stepsTitlesViews.size() > 0) {
            int index = 0;
            for (TextView textView : stepsTitlesViews) {
                if(textView != null) {
                    textView.setTag(index);
                    textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (headClickListener != null) {
                                headClickListener.onClick((int) v.getTag());
                            }
                        }
                    });
                }

                index++;
            }
        }
        if(stepsSubtitlesViews != null && stepsSubtitlesViews.size() > 0) {
            int index = 0;
            for (TextView textView : stepsSubtitlesViews) {
                if(textView != null) {
                    textView.setTag(index);
                    textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (headClickListener != null) {
                                headClickListener.onClick((int) v.getTag());
                            }
                        }
                    });
                }

                index++;
            }
        }
    }


    public static interface HeadClickListener{
        public void onClick(int stepNumber);
    }
}
