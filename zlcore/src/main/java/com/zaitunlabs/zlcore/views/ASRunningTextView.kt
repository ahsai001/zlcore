package com.zaitunlabs.zlcore.views

import android.content.Context
import android.text.TextUtils.TruncateAt
import android.util.AttributeSet

class ASRunningTextView : ASTextView {

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context) : super(context) {
        init()
    }

    private fun init() {
        setSingleLine(true)
        ellipsize = TruncateAt.MARQUEE
        marqueeRepeatLimit = -1 //forever
        setHorizontallyScrolling(true)
        //setFreezesText(true);
        isSelected = true
    }

}
