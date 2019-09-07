package com.zaitunlabs.zlcore.views

import android.R
import android.content.Context
import android.graphics.drawable.StateListDrawable
import androidx.core.content.ContextCompat
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View

class ASImageButtonView : AppCompatImageView {
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

    fun setImageDrawable(pressed: Int, focused: Int, normal: Int) {
        val states = StateListDrawable()
        states.addState(intArrayOf(R.attr.state_pressed), ContextCompat.getDrawable(context, pressed))
        states.addState(intArrayOf(R.attr.state_focused), ContextCompat.getDrawable(context, focused))
        states.addState(intArrayOf(), ContextCompat.getDrawable(context, normal))
        //this.setBackgroundDrawable(states);
        this.setImageDrawable(states)
    }

    fun setImageDrawable(pressed: Int, normal: Int) {
        setImageDrawable(pressed, pressed, normal)
    }

    fun setImageDrawable(normal: Int) {
        /*
		Resources r = getResources();

		//create a layer list and set them as background.
		int[] colors = {ContextCompat.getColor(getContext(),R.color.black), ContextCompat.getColor(getContext(),R.color.transparent)};
		GradientDrawable shadow = new GradientDrawable(Orientation.TOP_BOTTOM, colors);
		shadow.setBounds(0,98, 0, 0);

		int[] colors1 = {ContextCompat.getColor(getContext(),R.color.darker_gray), ContextCompat.getColor(getContext(),R.color.white)};
		GradientDrawable backColor = new GradientDrawable(Orientation.TOP_BOTTOM, colors1);
		backColor.setBounds(0, 0,0, 4);

		int[] colors2 = {ContextCompat.getColor(getContext(),R.color.black), ContextCompat.getColor(getContext(),R.color.transparent)};
		GradientDrawable fontColor = new GradientDrawable(Orientation.TOP_BOTTOM, colors2);
		fontColor.setBounds(0, 0,0, 4);

		Drawable[] layers = new Drawable[3];
		layers[0] = backColor;
		layers[1] = ContextCompat.getDrawable(getContext(),normal);
		layers[2] = fontColor;
		LayerDrawable layerList = new LayerDrawable(layers);

		StateListDrawable states = new StateListDrawable();
		states.addState(new int[] {R.attr.state_pressed},layerList);
		states.addState(new int[] {R.attr.state_focused},layerList);
		states.addState(new int[] {}, ContextCompat.getDrawable(getContext(),normal));
		//this.setBackgroundDrawable(states);
		this.setImageDrawable(states);
		*/

        val outValue = TypedValue()
        context.theme.resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, outValue, true)
        this.setBackgroundResource(outValue.resourceId)
        this.setImageResource(normal)
    }

    private fun init(context: Context) {
        this.isClickable = true
        this.scaleType = ImageView.ScaleType.FIT_XY
        this.adjustViewBounds = true
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
