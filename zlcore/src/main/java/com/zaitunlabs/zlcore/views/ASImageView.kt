package com.zaitunlabs.zlcore.views

import android.content.Context
import androidx.appcompat.widget.AppCompatImageView
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

open class ASImageView : AppCompatImageView {
    var iVtWidth = 0
        private set
    var ivHeight = 0
        private set
    private var gestureDetector: ASGestureDetector? = null

    fun setASGestureListener(dmListener: ASGestureListener) {
        this.gestureDetector!!.gestureListener = dmListener
    }

    private fun setIVWidth(width: Int) {
        this.iVtWidth = width
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
        this.setOnTouchListener { v, event -> gestureDetector!!.handleOnTouch(v, event) }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        // TODO Auto-generated method stub
        super.onSizeChanged(w, h, oldw, oldh)
        setIVWidth(w)
        ivHeight = h
    }

}
