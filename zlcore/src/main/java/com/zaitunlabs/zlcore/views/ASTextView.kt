package com.zaitunlabs.zlcore.views

import android.content.Context
import androidx.appcompat.widget.AppCompatTextView
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View

open class ASTextView : AppCompatTextView {
    var tVtWidth = 0
        private set
    var tvHeight = 0
        private set


    private var gestureDetector: ASGestureDetector? = null

    fun setASGestureListener(dmListener: ASGestureListener) {
        this.gestureDetector!!.gestureListener = dmListener
    }

    private fun setTVWidth(width: Int) {
        this.tVtWidth = width
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(context)
        // TODO Auto-generated constructor stub
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
        // TODO Auto-generated constructor stub
    }

    constructor(context: Context) : super(context) {
        init(context)
        // TODO Auto-generated constructor stub
    }

    private fun init(context: Context) {
        this.gestureDetector = ASGestureDetector(context)
        val outValue = TypedValue()
        getContext().theme.resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, outValue, true)
        this.setBackgroundResource(outValue.resourceId)
        this.setOnTouchListener { v, event -> gestureDetector!!.handleOnTouch(v, event) }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        // TODO Auto-generated method stub
        super.onSizeChanged(w, h, oldw, oldh)
        setTVWidth(w)
        tvHeight = h
    }
}
