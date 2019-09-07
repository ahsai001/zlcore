package com.zaitunlabs.zlcore.views

import android.content.Context
import android.util.AttributeSet
import android.widget.RelativeLayout

open class ASRelativeLayout : RelativeLayout {
    var iVtWidth = 0
        private set
    var ivHeight = 0
        private set

    private fun setIVWidth(width: Int) {
        this.iVtWidth = width
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        // TODO Auto-generated constructor stub
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        // TODO Auto-generated constructor stub
    }

    constructor(context: Context) : super(context) {
        // TODO Auto-generated constructor stub
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        // TODO Auto-generated method stub
        super.onSizeChanged(w, h, oldw, oldh)
        setIVWidth(w)
        ivHeight = h
    }

}
