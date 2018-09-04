package com.zaitunlabs.zlcore.views;

import android.content.Context;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;

public class ASRunningTextView extends ASTextView{

	public ASRunningTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	public ASRunningTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	public ASRunningTextView(Context context) {
		super(context);
		init();
	}
	
	private void init(){
		setSingleLine(true);
		setEllipsize(TruncateAt.MARQUEE);
		setMarqueeRepeatLimit(-1); //forever
		setHorizontallyScrolling(true);
		//setFreezesText(true);
		setSelected(true);
	}

}
