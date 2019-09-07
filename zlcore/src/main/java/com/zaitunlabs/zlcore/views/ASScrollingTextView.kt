package com.zaitunlabs.zlcore.views

import android.content.Context
import android.graphics.Rect
import android.text.TextPaint
import android.util.AttributeSet
import android.view.Gravity
import android.view.animation.LinearInterpolator
import android.widget.Scroller

class ASScrollingTextView : ASTextView {

    // scrolling feature
    private var mSlr: Scroller? = null

    // milliseconds for a round of scrolling
    var rndDuration = 10000

    // the X offset when paused
    private var mXPaused = 0

    // whether it's being paused
    var isPaused = true
        private set

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
        setSingleLine()
        gravity = Gravity.CENTER_VERTICAL
        ellipsize = null
        visibility = View.INVISIBLE
    }

    /**
     * begin to scroll the text from the original position
     */
    fun startScroll() {
        // begin from the very right side
        mXPaused = -1 * width
        // assume it's paused
        isPaused = true
        resumeScroll()
    }

    /**
     * resume the scroll from the pausing point
     */
    fun resumeScroll() {

        if (!isPaused)
            return

        // Do not know why it would not scroll sometimes
        // if setHorizontallyScrolling is called in constructor.
        setHorizontallyScrolling(true)

        // use LinearInterpolator for steady scrolling
        mSlr = Scroller(this.context, LinearInterpolator())
        setScroller(mSlr)

        val scrollingLen = calculateScrollingLen()
        val distance = scrollingLen - (width + mXPaused)
        val duration = (rndDuration.toDouble() * distance.toDouble() * 1.00000 / scrollingLen).toInt()

        visibility = View.VISIBLE
        mSlr!!.startScroll(mXPaused, 0, distance, 0, duration)
        invalidate()
        isPaused = false
    }

    /**
     * calculate the scrolling length of the text in pixel
     *
     * @return the scrolling length in pixels
     */
    private fun calculateScrollingLen(): Int {
        val tp = paint
        var rect: Rect? = Rect()
        val strTxt = text.toString()
        tp.getTextBounds(strTxt, 0, strTxt.length, rect)
        val scrollingLen = rect!!.width() + width
        rect = null
        return scrollingLen
    }

    /**
     * pause scrolling the text
     */
    fun pauseScroll() {
        if (null == mSlr)
            return

        if (isPaused)
            return

        isPaused = true

        // abortAnimation sets the current X to be the final X,
        // and sets isFinished to be true
        // so current position shall be saved
        mXPaused = mSlr!!.currX
        mSlr!!.abortAnimation()
    }

    override/*
	 * override the computeScroll to restart scrolling when finished so as that
	 * the text is scrolled forever
	 */ fun computeScroll() {
        super.computeScroll()

        if (null == mSlr)
            return

        if (mSlr!!.isFinished && !isPaused) {
            this.startScroll()
        }
    }
}
