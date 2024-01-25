package com.zaitunlabs.zlcore.views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;

import com.zaitunlabs.zlcore.R;

public class GoToTopView extends ASTextView{
	private int alpha = 200;
	private IGoToTopAction action = null;
	public GoToTopView(Context context, String title, IGoToTopAction action) {
		super(context);
		this.action = action;
		init(title);
	}
	public GoToTopView(Context context, IGoToTopAction action) {
		super(context);
		this.action = action;
		init(context.getString(R.string.zlcore_gototopview_up_wording));
	}
	
	private void init(String title){
		this.setBackgroundColor(Color.argb(alpha, 255, 255, 255));
		this.setText(title);
		this.setTypeface(null, Typeface.BOLD);
		this.setTextColor(Color.BLACK);
		this.setTextSize(12);
		this.setGravity(Gravity.CENTER);
		this.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(action != null){
					action.goToTopAction();
				}
			}
		});
		this.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int action = event.getAction();
				switch (action) {
				case MotionEvent.ACTION_DOWN:
					GoToTopView.this.setBackgroundColor(Color.argb(alpha, 0, 0, 0));
					GoToTopView.this.setTextColor(Color.WHITE);
					GoToTopView.this.setTextSize(15);
					break;
				case MotionEvent.ACTION_MOVE:
					break;
				case MotionEvent.ACTION_UP:
					GoToTopView.this.setBackgroundColor(Color.argb(alpha, 255, 255, 255));
					GoToTopView.this.setTextColor(Color.BLACK);
					GoToTopView.this.setTextSize(12);
					break;
				}
				return false;
			}
		});
	}
	
	public static interface IGoToTopAction{
		public void goToTopAction();
	}

}
