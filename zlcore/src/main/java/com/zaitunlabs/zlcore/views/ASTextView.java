package com.zaitunlabs.zlcore.views;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class ASTextView extends AppCompatTextView{
	private int width = 0;
	private int height = 0;
	

	private ASGestureDetector gestureDetector = null;
	
	public void setASGestureListener(ASGestureListener dmListener) {
		this.gestureDetector.setGestureListener(dmListener);
	}
	
	public int getTVtWidth() {
		return width;
	}
	private void setTVWidth(int width) {
		this.width = width;
	}
	public int getTVHeight() {
		return height;
	}
	private void setTVHeight(int height) {
		this.height = height;
	}
	public ASTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
		// TODO Auto-generated constructor stub
	}
	public ASTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
		// TODO Auto-generated constructor stub
	}
	public ASTextView(Context context) {
		super(context);
		init(context);
		// TODO Auto-generated constructor stub
	}
	
	private void init(Context context){
		this.gestureDetector = new ASGestureDetector(context);
		TypedValue outValue = new TypedValue();
		getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, outValue, true);
		this.setBackgroundResource(outValue.resourceId);
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
		setTVWidth(w);
		setTVHeight(h);
	}
}
