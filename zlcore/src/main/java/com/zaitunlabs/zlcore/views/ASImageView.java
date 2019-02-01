package com.zaitunlabs.zlcore.views;

import android.content.Context;
import androidx.appcompat.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class ASImageView  extends AppCompatImageView{
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
	public ASImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
		// TODO Auto-generated constructor stub
	}
	public ASImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
		// TODO Auto-generated constructor stub
	}
	public ASImageView(Context context) {
		super(context);
		init(context);
		// TODO Auto-generated constructor stub
	}
	
	private void init(Context context){
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
