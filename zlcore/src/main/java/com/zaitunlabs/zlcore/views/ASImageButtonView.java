package com.zaitunlabs.zlcore.views;

import android.R;
import android.content.Context;
import android.graphics.drawable.StateListDrawable;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

public class ASImageButtonView extends AppCompatImageView{
	private int width = 0;
	private int height = 0;
	private ASGestureDetector gestureDetector = null;
	
	public void setASGestureListener(ASGestureListener dmListener) {
		this.gestureDetector.setGestureListener(dmListener);
	}
	public int getIVtWidth() {
		return width;
	}
	private void setIVWidth(int width) {
		this.width = width;
	}
	public int getIVHeight() {
		return height;
	}
	private void setIVHeight(int height) {
		this.height = height;
	}
	public ASImageButtonView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
		// TODO Auto-generated constructor stub
	}
	public ASImageButtonView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
		// TODO Auto-generated constructor stub
	}
	public ASImageButtonView(Context context) {
		super(context);
		init(context);
		// TODO Auto-generated constructor stub
	}

	public void setImageDrawable(int pressed, int focused, int normal) {
		StateListDrawable states = new StateListDrawable();
		states.addState(new int[] {R.attr.state_pressed}, ContextCompat.getDrawable(getContext(),pressed));
		states.addState(new int[] {R.attr.state_focused}, ContextCompat.getDrawable(getContext(),focused));
		states.addState(new int[] {}, ContextCompat.getDrawable(getContext(),normal));
		//this.setBackground(states);
		this.setImageDrawable(states);
	}

	public void setImageDrawable(int pressed, int normal) {
		setImageDrawable(pressed, pressed, normal);
	}

	public void setImageDrawable(int normal) {
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
		//this.setBackground(states);
		this.setImageDrawable(states);
		*/

		TypedValue outValue = new TypedValue();
		getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, outValue, true);
		this.setBackgroundResource(outValue.resourceId);
		this.setImageResource(normal);
	}

	private void init(Context context){
		this.setClickable(true);
		this.setScaleType(ScaleType.FIT_XY);
		this.setAdjustViewBounds(true);
		this.gestureDetector = new ASGestureDetector(context);
		this.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return gestureDetector.handleOnTouch(v, event);
			}
		});
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// TODO Auto-generated method stub
		super.onSizeChanged(w, h, oldw, oldh);
		setIVWidth(w);
		setIVHeight(h);
	}

}
