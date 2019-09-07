package com.zaitunlabs.zlcore.views

import android.view.View

class NavigationHandler {
    var counts = 0
    private var index = 0

    var nextView: View? = null
        set(nextView) {
            if (this.nextView != null) this.nextView!!.setOnClickListener(null)
            field = nextView
            this.nextView!!.setOnClickListener(nextListener)
        }
    var prevView: View? = null
        set(prevView) {
            if (this.prevView != null) this.prevView!!.setOnClickListener(null)
            field = prevView
            this.prevView!!.setOnClickListener(prevListener)
        }
    var outputView: View? = null


    private var nextListener: View.OnClickListener? = null
    private var prevListener: View.OnClickListener? = null

    var otputListener: NavigationStateListener? = null
        private set

    fun showNavigationView() {
        if (this.nextView != null) this.nextView!!.visibility = View.VISIBLE
        if (this.prevView != null) this.prevView!!.visibility = View.VISIBLE
    }

    fun showNavigationViewWithState() {
        if (this.prevView != null) {
            if (index <= 0) {
                this.prevView!!.visibility = View.GONE
            } else {
                this.prevView!!.visibility = View.VISIBLE
            }
        }
        if (this.nextView != null) {
            if (index >= counts - 1) {
                this.nextView!!.visibility = View.GONE
            } else {
                this.nextView!!.visibility = View.VISIBLE
            }
        }
    }

    fun hideNavigationView() {
        if (this.nextView != null) this.nextView!!.visibility = View.GONE
        if (this.prevView != null) this.prevView!!.visibility = View.GONE
    }

    fun setOutputListener(outputListener: NavigationStateListener) {
        this.otputListener = outputListener
    }

    fun getIndex(): Int {
        return index
    }

    fun setIndex(index: Int) {
        this.index = index
        showNavigationViewWithState()
        if (otputListener != null) otputListener!!.navigationStateIndex(outputView, this.prevView, index, counts)
    }

    constructor() {
        init()
    }

    constructor(counts: Int) {
        this.counts = counts
        init()
    }

    private fun init() {
        nextListener = View.OnClickListener {
            if (index < counts - 1) {
                index++
                setIndex(index)
            }
        }
        prevListener = View.OnClickListener {
            if (index > 0) {
                index--
                setIndex(index)
            }
        }
    }

    fun setNextPrevView(prevView: View, nextView: View) {
        prevView = prevView
        nextView = nextView
    }

    operator fun next() {
        if (index < counts - 1) {
            index++
            setIndex(index)
        }
    }

    fun prev() {
        if (index > 0) {
            index--
            setIndex(index)
        }
    }

}
