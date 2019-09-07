package com.zaitunlabs.zlcore.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import ernestoyaquello.com.verticalstepperform.VerticalStepperFormLayout


/**
 * Created by ahsai on 7/17/2017.
 */

class CustomVerticalStepper : VerticalStepperFormLayout {

    internal var headClickListener: HeadClickListener? = null

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}


    fun makeTitleMultiLine() {
        if (stepsTitlesViews != null && stepsTitlesViews.size > 0) {
            for (textView in stepsTitlesViews) {
                val params = textView.layoutParams
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT
                textView.layoutParams = params
            }
        }
    }

    fun setActionOnTitleAndSubTitle(headClickListener: HeadClickListener?) {
        this.headClickListener = headClickListener
        if (stepsTitlesViews != null && stepsTitlesViews.size > 0) {
            var index = 0
            for (textView in stepsTitlesViews) {
                if (textView != null) {
                    textView.tag = index
                    textView.setOnClickListener { v ->
                        headClickListener?.onClick(v.tag as Int)
                    }
                }

                index++
            }
        }
        if (stepsSubtitlesViews != null && stepsSubtitlesViews.size > 0) {
            var index = 0
            for (textView in stepsSubtitlesViews) {
                if (textView != null) {
                    textView.tag = index
                    textView.setOnClickListener { v ->
                        headClickListener?.onClick(v.tag as Int)
                    }
                }

                index++
            }
        }
    }


    interface HeadClickListener {
        fun onClick(stepNumber: Int)
    }
}
