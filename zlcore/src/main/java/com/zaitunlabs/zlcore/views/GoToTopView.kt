package com.zaitunlabs.zlcore.views

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.MotionEvent
import android.view.View

import com.zaitunlabs.zlcore.R

class GoToTopView : ASTextView {
    private val alpha = 200
    private var action: IGoToTopAction? = null

    constructor(context: Context, title: String, action: IGoToTopAction) : super(context) {
        this.action = action
        init(title)
    }

    constructor(context: Context, action: IGoToTopAction) : super(context) {
        this.action = action
        init(context.getString(R.string.zlcore_gototopview_up_wording))
    }

    private fun init(title: String) {
        this.setBackgroundColor(Color.argb(alpha, 255, 255, 255))
        this.text = title
        this.setTypeface(null, Typeface.BOLD)
        this.setTextColor(Color.BLACK)
        this.textSize = 12f
        this.gravity = Gravity.CENTER
        this.setOnClickListener {
            if (action != null) {
                action!!.goToTopAction()
            }
        }
        this.setOnTouchListener { v, event ->
            val action = event.action
            when (action) {
                MotionEvent.ACTION_DOWN -> {
                    this@GoToTopView.setBackgroundColor(Color.argb(alpha, 0, 0, 0))
                    this@GoToTopView.setTextColor(Color.WHITE)
                    this@GoToTopView.textSize = 15f
                }
                MotionEvent.ACTION_MOVE -> {
                }
                MotionEvent.ACTION_UP -> {
                    this@GoToTopView.setBackgroundColor(Color.argb(alpha, 255, 255, 255))
                    this@GoToTopView.setTextColor(Color.BLACK)
                    this@GoToTopView.textSize = 12f
                }
            }
            false
        }
    }

    interface IGoToTopAction {
        fun goToTopAction()
    }

}
