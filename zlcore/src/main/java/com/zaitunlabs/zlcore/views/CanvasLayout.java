package com.zaitunlabs.zlcore.views;

import android.content.Context;
import android.graphics.Point;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewParent;
import android.view.animation.Animation;
import android.widget.RelativeLayout;

import com.zaitunlabs.zlcore.utils.CommonUtil;
import com.zaitunlabs.zlcore.utils.DebugUtil;


/**
 * currently CanvasLayout only stick to Activity, not inside View or layout (Recommended)
 * @author ahmad
 * 
 */
public class CanvasLayout extends RelativeLayout{
	/*
	 * setContentView(canvas.getFillParentView()); perlu dipanggil di akhir dari method OnCreate setiap activity
	 */
	double widthRatio;
	double heightRatio;
	private Context context;
	int canvasWidth;
	int canvasHeight;
	public CanvasLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}
	public CanvasLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	public CanvasLayout(Context context) {
		super(context);
		init(context);
	}
	
	public View getFillParentView(){
		setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
		return this;
	}
	
	public double getWidthRatio() {
		return widthRatio;
	}
	public void setWidthRatio(double widthRatio) {
		this.widthRatio = widthRatio;
	}
	public double getHeightRatio() {
		return heightRatio;
	}
	public void setHeightRatio(double heightRatio) {
		this.heightRatio = heightRatio;
	}
	private void init(Context context){
		this.context = context;
		widthRatio = (double) CommonUtil.getScreenWidth(context) / 100;
		heightRatio = (double) CommonUtil.getAppHeight(context) / 100;

		DebugUtil.logW("SIZE layout", "widthRatio : "+widthRatio);
		DebugUtil.logW("SIZE layout", "heightRatio : " + heightRatio);
	}
	
	/**
	 * add View with frame relative to its parent
	 * @param v View
	 * @param left in percentage with parent width
	 * @param top in percentage with parent height
	 * @param width in percentage with parent width
	 * @param height in percentage with parent height
	 */
	public void addViewWithFrame(View v,int left, int top, int width, int height){
		//widthRatio * width = heightRatio * height ==> ???
		
		int viewWidth = (int) Math.round((widthRatio * ((width == CanvasSection.SAME_AS_OTHER_SIDE)?((heightRatio * height)/widthRatio):width)));
		int viewHeight = (int) Math.round((heightRatio * ((height == CanvasSection.SAME_AS_OTHER_SIDE)?((widthRatio * width)/heightRatio):height)));
		int leftX = (int) Math.round(widthRatio * left);
		int topY = (int) Math.round(heightRatio * top);

		DebugUtil.logW("LAYOUT", "height : "+viewHeight);
		DebugUtil.logW("LAYOUT", "leftX : "+leftX);
		
		LayoutParams params = new LayoutParams(viewWidth, viewHeight);
		params.leftMargin = leftX;
		params.topMargin = topY;
		addView(v, params);

		if(v instanceof CanvasSection){
			((CanvasSection)v).setSectionWidthInPixel(viewWidth);
			((CanvasSection)v).setSectionHeightInPixel(viewHeight);
		}
	}
	
	public CanvasSection createNewSectionWithFrame(int left, int top, int width, int height, boolean noScroll){
		DebugUtil.logW("SIZE", "h : "+(heightRatio * 100));
		CanvasSection section = new CanvasSection("",this.context,left,top,width,height,(int)(widthRatio * 100),(int)(heightRatio * 100),noScroll);
		addViewWithFrame(section, left, top, width, height);
		return section;
	}
	
	public CanvasSection createNewSectionWithFrame(String sectionName, int left, int top, int width, int height, boolean noScroll){
		CanvasSection section = new CanvasSection(sectionName,this.context,left,top,width,height,(int)(widthRatio * 100),(int)(heightRatio * 100),noScroll);
		//section.setTag(CanvasSection.SECTION_NAME_TAG, sectionName);
		addViewWithFrame(section, left, top, width, height);
		return section;
	}
	
	public CanvasSection createNewSectionWithFrame(String sectionName, int left, int top, int width, int height, boolean noScroll, boolean useBackgroundView){
		CanvasSection section = new CanvasSection(sectionName,this.context,left,top,width,height,(int)(widthRatio * 100),(int)(heightRatio * 100),noScroll, useBackgroundView);
		//section.setTag(CanvasSection.SECTION_NAME_TAG, sectionName);
		addViewWithFrame(section, left, top, width, height);
		return section;
	}
	
	public CanvasSection createNewSectionWithFrame(int left, int top, int width, int height){
		return createNewSectionWithFrame(left, top, width, height, true);
	}

	public void ResizeSectionWithFrame(CanvasSection v,int left, int top, int width, int height){
		final CanvasSection section = getSectionInCanvas(v);
		if(section != null){
			final int viewWidth = (int) (widthRatio * width);
			final int viewHeight = (int) (heightRatio * height);
			final int leftX = (int) (widthRatio * left);
			final int topY = (int) (heightRatio * top);

			//update section coordinate
			section.setSectionX(left);
			section.setSectionY(top);
			section.setSectionWidth(width);
			section.setSectionHeight(height);

			ResizeMoveAnimation anim = new ResizeMoveAnimation(section, leftX, topY, viewWidth, viewHeight);
			anim.start();
		}
	}

	private CanvasSection getSectionInCanvas(CanvasSection neeedleSection){
		int sectionNumber = getChildCount();
		CanvasSection result = null;
		for(int i = 0; i < sectionNumber; i++){
			CanvasSection item = (CanvasSection)getChildAt(i);
			if(item == neeedleSection){
				result = item;
			}
		}
		return result;
	}
	
	public void calculateNewRatioWithParent(){
		ViewParent vp  = getParent();
		if(vp != null && vp instanceof View){
			((View)vp).measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
			widthRatio = (double)((View)vp).getMeasuredWidth() / 100;
			heightRatio = (double)((View)vp).getMeasuredHeight() / 100;
		}
	}
	 
	public void calculateNewRatioWith(View v){
		if(v != null){
			v.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
			widthRatio = (double)v.getMeasuredWidth() / 100;
			heightRatio = (double)v.getMeasuredHeight() / 100;
		}
	}
	
	public void calculateNewRatioWithContext(){
		init(this.context);
	}
	
	public void removeSection(final CanvasSection section){
		final CountDownTimer timer;
		timer = new CountDownTimer(500,500){
			@Override
			public void onFinish() {
				//section.setDisappearAnim(null); // gak dipake karna section jg akan diremove
				removeView(section);
				invalidate();
			}

			@Override
			public void onTick(long millisUntilFinished) {
				// TODO Auto-generated method stub
			}
        };
		CommonUtil.setViewAnim_disappearslideupfromBottom(section,300).setAnimationListener(new Animation.AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {}
			
			@Override
			public void onAnimationRepeat(Animation animation) {}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				section.setVisibility(View.GONE);
		        timer.start();
			}
			
		});
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// TODO Auto-generated method stub
		super.onSizeChanged(w, h, oldw, oldh);
		canvasWidth = w;
		canvasHeight = h;
	}
	
	public int getCanvasWidth() {
		return canvasWidth;
	}
	public int getCanvasHeight() {
		return canvasHeight;
	}
	
}
