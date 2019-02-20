package com.zaitunlabs.zlcore.views;

import android.content.Context;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.zaitunlabs.zlcore.R;
import com.zaitunlabs.zlcore.utils.CommonUtils;
import com.zaitunlabs.zlcore.utils.DebugUtils;


/**
 * CanvasSection view, direkomendasikan hanya untuk di add ke CanvasLayout atau
 * CanvasSection lainnya, karena view ini membutuhkan parentWidth dan
 * parentHeight dalam pixel
 * 
 * @author ahmad
 * 
 */
public class CanvasSection extends FrameLayout {
	private int sectionX, sectionY, sectionWidth, sectionHeight; // variable dengan value 0 - 100 % relatif ke Parent
	private int parentWidth; // in pixel
	private int parentHeight; // in pixel

	private int sectionWidthInPixel;
	private int sectionHeightInPixel;



	private boolean maintainRatio = true; //default true, jangan ikut resize jika parent resize
	private boolean updateLayoutChildren = true; //default true. selalu update child jika resize

	private ASGestureDetector gestureDetector = null;
	private ASShiftRepositionHandler shiftPositionHandler = null;

	public static final int SECTION_NAME_TAG = R.string.app_name;

	private boolean isSectionConsumeEvent = true; // default nya consume event

	private static final int BROADCAST_EVENT_NONE = 0;
	private static final int BROADCAST_EVENT_ALL_CHILDREN = 1;
	private static final int BROADCAST_EVENT_ALL_SECTION_CHILDREN = 2;

	private int broadcastEventType = BROADCAST_EVENT_NONE;
	
	
	public static final int SAME_AS_OTHER_SIDE = -5; //use to set height same as width or vice versa

	int canvasWidth;
	int canvasHeight;

	private Context context = null;
	private ScrollView vScroll = null; // width-height boleh di tentukan sizenya
	private HorizontalScrollView hScroll = null;// width-height boleh di tentukan
											// sizenya
	public ASRelativeLayout sectionLayout = null;// width-height harus wrap content
	public ASLinearLayout linearLayout = null;
	private View backgroundView = null;
	private boolean useBackGroundView = false;
	
	private String sectionName = null;


	private Animation appearAnim = null;
	private Animation disappearAnim = null;

	private boolean noScroll = false; // defaultnya scroll aktif
	
	
	private boolean isAlwaysConsumeEvent = false; //default false


	public void setMaintainRatio(boolean maintainRatio) {
		this.maintainRatio = maintainRatio;
	}

	public void setUpdateLayoutChildren(boolean updateLayoutChildren) {
		this.updateLayoutChildren = updateLayoutChildren;
	}

	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;
	}

	
	public String getSectionName() {
		//String sectionName = (String) getTag(SECTION_NAME_TAG);
		return this.sectionName;
	}

	public void setAlwaysConsumeEvent(boolean isAlwaysConsumeEvent) {
		this.isAlwaysConsumeEvent = isAlwaysConsumeEvent;
	}

	public ASShiftRepositionHandler getShiftPositionHandler() {
		return shiftPositionHandler;
	}

	public void setASGestureListener(ASGestureListener dmListener) {
		gestureDetector.setGestureListener(dmListener);
	}

	public Animation getAppearAnim() {
		return appearAnim;
	}

	public void setAppearAnim(Animation appearAnim) {
		this.appearAnim = appearAnim;
	}

	public Animation getDisappearAnim() {
		return disappearAnim;
	}

	public void setDisappearAnim(Animation disappearAnim) {
		this.disappearAnim = disappearAnim;
	}

	public double getParentWidth() {
		return parentWidth;
	}

	public void setParentWidth(int parentWidth) {
		this.parentWidth = parentWidth;
	}

	public double getParentHeight() {
		return parentHeight;
	}

	public void setParentHeight(int parentHeight) {
		this.parentHeight = parentHeight;
	}


	public int getSectionWidthInPixel() {
		return sectionWidthInPixel;
	}

	public void setSectionWidthInPixel(int sectionWidthInPixel) {
		this.sectionWidthInPixel = sectionWidthInPixel;
	}

	public int getSectionHeightInPixel() {
		return sectionHeightInPixel;
	}

	public void setSectionHeightInPixel(int sectionHeightInPixel) {
		this.sectionHeightInPixel = sectionHeightInPixel;
	}

	public int getSectionX() {
		return sectionX;
	}

	public void setSectionX(int sectionX) {
		this.sectionX = sectionX;
	}

	public int getSectionY() {
		return sectionY;
	}

	public void setSectionY(int sectionY) {
		this.sectionY = sectionY;
	}

	public int getSectionWidth() {
		return sectionWidth;
	}

	public void setSectionWidth(int sectionWidth) {
		this.sectionWidth = sectionWidth;
	}

	public int getSectionHeight() {
		return sectionHeight;
	}

	public void setSectionHeight(int sectionHeight) {
		this.sectionHeight = sectionHeight;
	}

	public void consumeEvent() {
		this.isSectionConsumeEvent = true;
	}

	public void unConsumeEvent() {
		this.isSectionConsumeEvent = false;
	}

	public void setBroadCastEventType(int broadCastEventType) {
		this.broadcastEventType = broadCastEventType;
	}

	
	@Override
	public void setBackgroundColor(int color) {
		if(backgroundView != null){
			//by this hacked way, refresh happened in canvas parent
			backgroundView.setVisibility(View.VISIBLE);
			backgroundView.setBackgroundColor(color);
		}else{
			//by this way, refresh not happened in canvas parent, tanya kenapa???
			super.setBackgroundColor(color);
		}
	}
	
	
	// (CanvasSection)linearlayout/FrameLayout -> scrollview -> horizontalscrollview ->
	// (sectionLayout)RelativeLayout
	// (CanvasSection)linearlayout/FrameLayout -> (sectionLayout)RelativeLayout
	private void init(String sectionName, Context context) {
		this.context = context;

		if(sectionName == null || sectionName == ""){
			sectionName = this.toString();
		}
		setSectionName(sectionName);
		if(useBackGroundView){
			backgroundView = new View(context);
			addView(backgroundView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			backgroundView.setVisibility(View.GONE);
		}
		if (!noScroll) {
			vScroll = new ScrollView(context) {
				/*
				GestureDetector mGestureDetector = new GestureDetector(this.getContext(), new GestureDetector.SimpleOnGestureListener(){
					public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
						if(Math.abs(distanceY) > Math.abs(distanceX)) {
							DebugUtils.logE("vScroll", getSectionName()+" onScroll: TRUE");
			                return true;
			            }
						DebugUtils.logE("vScroll", getSectionName()+" onScroll: FALSE");
			            return false;
					};
				});
				*/
				@Override
				public boolean onInterceptTouchEvent(MotionEvent ev) {
					boolean value = super.onInterceptTouchEvent(ev);
					DebugUtils.logE("vScroll", getSectionName()+" onInterceptTouchEvent: " + value);
					return value;
				}
				@Override
				public boolean dispatchTouchEvent(MotionEvent ev) {
					boolean value = super.dispatchTouchEvent(ev);
					DebugUtils.logE("vScroll", getSectionName()+" dispatchTouchEvent: " + value);
					return value;
				}
			};

			vScroll.setFillViewport(false);
			/*
			int width = (int) ((parentWidth * sectionWidth) / 100);
			int height = (int) ((parentHeight * sectionHeight) / 100);
			*/
			LayoutParams vScrollParam = new LayoutParams(
					(/*width != 0 ? width : */LayoutParams.MATCH_PARENT),
					(/*height != 0 ? height : */LayoutParams.WRAP_CONTENT));
			vScrollParam.leftMargin = 0;
			vScrollParam.topMargin = 0;
			addView(vScroll, vScrollParam);

			hScroll = new HorizontalScrollView(context) {
				/*
				GestureDetector mGestureDetector = new GestureDetector(this.getContext(), new GestureDetector.SimpleOnGestureListener(){
					public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
						if(Math.abs(distanceY) < Math.abs(distanceX)) {
			                return true;
			            }
			            return false;
					};
				});
				*/
				@Override
				public boolean onInterceptTouchEvent(MotionEvent ev) {
					boolean value = super.onInterceptTouchEvent(ev);
					DebugUtils.logE("hScroll", getSectionName()+" onInterceptTouchEvent: " + value);
					//set return false, agar move event di asmovable menu bisa aktif
					onTouchEvent(ev);
					return false;
				}
				@Override
				public boolean dispatchTouchEvent(MotionEvent ev) {
					boolean value = super.dispatchTouchEvent(ev);
					DebugUtils.logE("hScroll", getSectionName()+" dispatchTouchEvent: " + value);
					return value;
				}
			};
			hScroll.setFillViewport(false);
			LayoutParams hScrollParam = new LayoutParams(
					(/*width != 0 ? width : */LayoutParams.WRAP_CONTENT),
					(/*height != 0 ? height : */LayoutParams.MATCH_PARENT));
			hScrollParam.leftMargin = 0;
			hScrollParam.topMargin = 0;
			vScroll.addView(hScroll, hScrollParam);

			/*
			 * LinearLayout.LayoutParams vScrollParam = new
			 * LinearLayout.LayoutParams((width != 0 ? width :
			 * HorizontalScrollView.LayoutParams.MATCH_PARENT),(height != 0 ?
			 * height : HorizontalScrollView.LayoutParams.MATCH_PARENT));
			 * TwoDScrollView hScroll = new TwoDScrollView(context);
			 * addView(hScroll, vScrollParam);
			 */

			sectionLayout = new ASRelativeLayout(context){
				@Override
				public boolean onInterceptTouchEvent(MotionEvent ev) {
					boolean value = super.onInterceptTouchEvent(ev);
					DebugUtils.logE("sectionLayout", getSectionName()+" onInterceptTouchEvent: " + value);
					return value;
				}
				@Override
				public boolean dispatchTouchEvent(MotionEvent ev) {
					boolean value = super.dispatchTouchEvent(ev);
					DebugUtils.logE("sectionLayout", getSectionName()+" dispatchTouchEvent: " + value);
					return value;
				}
			};
			LayoutParams sectionParam = new LayoutParams(
					LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			hScroll.addView(sectionLayout, sectionParam);
		} else {
			sectionLayout = new ASRelativeLayout(context){
				@Override
				public boolean onInterceptTouchEvent(MotionEvent ev) {
					boolean value = super.onInterceptTouchEvent(ev);
					DebugUtils.logE("sectionLayout", getSectionName()+" onInterceptTouchEvent: " + value);
					return value;
				}
				@Override
				public boolean dispatchTouchEvent(MotionEvent ev) {
					boolean value = super.dispatchTouchEvent(ev);
					DebugUtils.logE("sectionLayout", getSectionName()+" dispatchTouchEvent: " + value);
					return value;
				}
			};
			LayoutParams sectionParam = new LayoutParams(
					LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			addView(sectionLayout, sectionParam);
		}

		this.gestureDetector = new ASGestureDetector(sectionName, this.getContext());
		this.shiftPositionHandler = new ASShiftRepositionHandler(
				this.getContext(), this) {
			@Override
			protected int getYNow() {
				return getSectionY();
			}

			@Override
			protected int getXNow() {
				return getSectionX();
			}

			@Override
			protected void postStepUpdate(View view, int viewWidth, int viewHeight, boolean isInRepeatState) {
				//do nothing now
				//DebugUtils.logW("postStepUpdate", ">>>>>>>>>>>>>>>>>>>>>>>");


				//if(!isInRepeatState){
					//DebugUtils.logW("postStepUpdate", "not in repeatstate");
					if(linearLayout != null){

						RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams) linearLayout.getLayoutParams();
						if (linearLayout.getOrientation() == LinearLayout.VERTICAL) {
							param.width = viewWidth;
						} else if (linearLayout.getOrientation() == LinearLayout.HORIZONTAL) {
							param.height = viewHeight;
						}
						linearLayout.setLayoutParams(param);

						//DebugUtils.logW("postStepUpdate", "linearLayout");
					}else{
						int childrenCount = CanvasSection.this.sectionLayout.getChildCount();
						//DebugUtils.logW("postStepUpdate", "sectionLayout");

						int parentWidth = viewWidth;
						int parentHeight = viewHeight;

						for(int x = 0; x < childrenCount; x++){
							View child = CanvasSection.this.sectionLayout.getChildAt(x);
							if(child instanceof CanvasSection){
								if(updateLayoutChildren){
									((CanvasSection)child).updateLayout(CanvasSection.this,parentWidth,parentHeight);
								}
							}
						}

					}
				//}
			}

			@Override
			protected void updateDimension(int width, int height) {
				setSectionWidth(width);
				setSectionHeight(height);
			}

			@Override
			protected void updatePosition(int x, int y) {
				setSectionX(x);
				setSectionY(y);
			}

			@Override
			protected Rect convertRectFromCommonToParamType(Rect commonTypeValue) {
				Rect paramTypeValue = new Rect();
				paramTypeValue.left = (int) (parentWidth * commonTypeValue.left) / (100);
				paramTypeValue.top = (int) (parentHeight * commonTypeValue.top) / (100);
				paramTypeValue.right = paramTypeValue.left
						+ (int) (parentWidth * commonTypeValue.width()) / (100);
				paramTypeValue.bottom = paramTypeValue.top
						+ (int) (parentHeight * commonTypeValue.height())
						/ (100);
				return paramTypeValue;
			}

			@Override
			protected Rect convertRectFromParamToCommonType(Rect paramTypeValue) {
				Rect commonTypeValue = new Rect();
				commonTypeValue.left = Math
						.round(((float) paramTypeValue.left * 100)
								/ (float) parentWidth);
				commonTypeValue.top = Math
						.round(((float) paramTypeValue.top * 100)
								/ (float) parentHeight);
				return commonTypeValue;
			}

			@Override
			protected int getParentWidth() {
				return parentWidth;
			}

			@Override
			protected int getParentHeight() {
				return parentHeight;
			}
		};

		this.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				//DebugUtils.logE("setOnTouchListener", getSectionName()+"",false);
				return gestureDetector.handleOnTouch(arg0, arg1);
				//return true;
			}
		});
	}


	public void updateLayout(CanvasSection parent, int newParentWidth, int newParentHeight){
		if(maintainRatio){
			setParentWidth(newParentWidth);
			setParentHeight(newParentHeight);

			DebugUtils.logD("ahmad", "parent width:"+getParentWidth()+" - parent section widht:"+parent.getSectionWidth()+" - child section width:"+getSectionWidth());

			DebugUtils.logD("ahmad", "before child "+getSectionName()+": w/h => "+getSectionWidthInPixel()+"/"+getSectionHeightInPixel());
			int childWidth = (int) Math.round((getParentWidth() * getSectionWidth()) / (100));

			int childHeight = (int) Math.round((getParentHeight() * getSectionHeight()) / (100));


			int leftX = (int) Math.round((getParentWidth() * getSectionX()) / (100));
			int topY = (int) Math.round((getParentHeight() * getSectionY()) / (100));


			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
					childWidth, childHeight);
			params.leftMargin = leftX;
			params.topMargin = topY;

			setLayoutParams(params);

			setSectionWidthInPixel(childWidth);
			setSectionHeightInPixel(childHeight);


			DebugUtils.logD("ahmad", "after child " + getSectionName() + ": w/h => " + getSectionWidthInPixel() + "/" + getSectionHeightInPixel());

			int childrenCount = CanvasSection.this.sectionLayout.getChildCount();
			for(int x = 0; x < childrenCount; x++){
				View child = CanvasSection.this.sectionLayout.getChildAt(x);
				if(child instanceof CanvasSection){
					((CanvasSection)child).updateLayout(CanvasSection.this,childWidth,childHeight);
				}
			}
		}
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		boolean isLogged = false;
		int action = ev.getAction();
		String actString = "";
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			DebugUtils.logD("dispatchTouchEvent",
			getSectionName()+" MotionEvent.ACTION_DOWN");
			actString = "MotionEvent.ACTION_DOWN";
			break;
		case MotionEvent.ACTION_MOVE:
			DebugUtils.logD("dispatchTouchEvent",
			getSectionName()+" MotionEvent.ACTION_MOVE");
			actString = "MotionEvent.ACTION_MOVE";
			break;
		case MotionEvent.ACTION_UP:
			DebugUtils.logD("dispatchTouchEvent",
			getSectionName()+" MotionEvent.ACTION_UP");
			actString = "MotionEvent.ACTION_UP";
			break;
		case MotionEvent.ACTION_CANCEL:
			DebugUtils.logD("dispatchTouchEvent",
			getSectionName()+" MotionEvent.ACTION_CANCEL");
			actString = "MotionEvent.ACTION_CANCEL";
			break;
		case MotionEvent.ACTION_OUTSIDE:
			DebugUtils.logD("dispatchTouchEvent",
			getSectionName()+" MotionEvent.ACTION_OUTSIDE");
			actString = "MotionEvent.ACTION_OUTSIDE";
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			DebugUtils.logD("dispatchTouchEvent",
			getSectionName()+" MotionEvent.ACTION_POINTER_DOWN");
			actString = "MotionEvent.ACTION_POINTER_DOWN";
			break;
		case MotionEvent.ACTION_POINTER_UP:
			DebugUtils.logD("dispatchTouchEvent",
			getSectionName()+" MotionEvent.ACTION_POINTER_UP");
			actString = "MotionEvent.ACTION_POINTER_UP";
			break;
		default:
			DebugUtils.logD("dispatchTouchEvent",
			getSectionName()+" MotionEvent.DEFAULT");
			actString = "MotionEvent.DEFAULT";
			break;
		}
		
		

		/*
		 * if(!isSectionConsumeEvent) return false;
		 * 
		 * if(broadcastEventType == BROADCAST_EVENT_ALL_CHILDREN ||
		 * broadcastEventType == BROADCAST_EVENT_ALL_SECTION_CHILDREN){
		 * if(!onInterceptTouchEvent(ev)){ int childCount = getChildCount();
		 * for(int i = 0; i < childCount; i++){ View child = getChildAt(i);
		 * if(broadcastEventType == BROADCAST_EVENT_ALL_CHILDREN ||
		 * (broadcastEventType == BROADCAST_EVENT_ALL_SECTION_CHILDREN && child
		 * instanceof CanvasSection)){ child.dispatchTouchEvent(ev); } } }
		 * return true; }
		 */
		boolean returnValue = super.dispatchTouchEvent(ev);
		DebugUtils.logE("dispatchTouchEvent", getSectionName()+" "+actString+" "+"returnValue : "+returnValue);
		return returnValue;
		// super.dispatchTouchEvent(ev);
		// return true;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		boolean isLogged = false;
		int action = ev.getAction();
		String actString = "";
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			DebugUtils.logD("onInterceptTouchEvent",
			getSectionName()+" MotionEvent.ACTION_DOWN");
			actString = "MotionEvent.ACTION_DOWN";
			break;
		case MotionEvent.ACTION_MOVE:
			DebugUtils.logD("onInterceptTouchEvent",
			getSectionName()+" MotionEvent.ACTION_MOVE");
			actString = "MotionEvent.ACTION_MOVE";
			break;
		case MotionEvent.ACTION_UP:
			DebugUtils.logD("onInterceptTouchEvent",
			getSectionName()+" MotionEvent.ACTION_UP");
			actString = "MotionEvent.ACTION_UP";
			break;
		case MotionEvent.ACTION_CANCEL:
			DebugUtils.logD("onInterceptTouchEvent",
			getSectionName()+" MotionEvent.ACTION_CANCEL");
			actString = "MotionEvent.ACTION_CANCEL";
			break;
		case MotionEvent.ACTION_OUTSIDE:
			DebugUtils.logD("onInterceptTouchEvent",
			getSectionName()+" MotionEvent.ACTION_OUTSIDE");
			actString = "MotionEvent.ACTION_OUTSIDE";
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			DebugUtils.logD("onInterceptTouchEvent",
			getSectionName()+" MotionEvent.ACTION_POINTER_DOWN");
			actString = "MotionEvent.ACTION_POINTER_DOWN";
			break;
		case MotionEvent.ACTION_POINTER_UP:
			DebugUtils.logD("onInterceptTouchEvent",
			getSectionName()+" MotionEvent.ACTION_POINTER_UP");
			actString = "MotionEvent.ACTION_POINTER_UP";
			break;
		default:
			DebugUtils.logD("onInterceptTouchEvent",
			getSectionName()+" MotionEvent.DEFAULT");
			actString = "MotionEvent.DEFAULT";
			break;
		}
		
		boolean returnValue = super.onInterceptTouchEvent(ev);
		DebugUtils.logE("onInterceptTouchEvent", getSectionName()+" "+actString+" "+"returnValue : "+returnValue);
		return returnValue;
		// handleTouchEvent(ev);
		// return false;
	}


	/*
	public CanvasSection(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
		// TODO Auto-generated constructor stub
	}

	public CanvasSection(Context context) {
		super(context);
		init(context);
		// TODO Auto-generated constructor stub
	}
	*/

	/**
	 * ini merupakan constructor yg paling direkomendasikan
	 * 
	 * @param context
	 * @param left
	 * @param top
	 * @param width
	 * @param height
	 * @param parentWidth
	 * @param parentHeight
	 */
	public CanvasSection(String sectionName,Context context, int left, int top, int width,
			int height, int parentWidth, int parentHeight, boolean noScroll) {
		super(context);
		setSectionX(left);
		setSectionY(top);
		setSectionWidth(width);
		setSectionHeight(height);
		setParentWidth(parentWidth);
		setParentHeight(parentHeight);
		this.noScroll = noScroll;
		init(sectionName, context);
		// TODO Auto-generated constructor stub
	}
	
	public CanvasSection(String sectionName,Context context, int left, int top, int width,
			int height, int parentWidth, int parentHeight, boolean noScroll, boolean useBackgroundView) {
		super(context);
		setSectionX(left);
		setSectionY(top);
		setSectionWidth(width);
		setSectionHeight(height);
		setParentWidth(parentWidth);
		setParentHeight(parentHeight);
		this.noScroll = noScroll;
		this.useBackGroundView = useBackgroundView;
		init(sectionName,context);
		// TODO Auto-generated constructor stub
	}
	
	public int getPixelRelativeToWidth(int valueInPercentage){
		return (int) Math.round((parentWidth * sectionWidth * valueInPercentage) / (100 * 100));
	}
	

	
	public int getPixelRelativeToHeight(int valueInPercentage){
		return (int) Math.round((parentHeight * sectionHeight * valueInPercentage) / (100 * 100));
	}
	
	public int getPercentageEqualHeightPixelWithPercentageFrom(ViewGroup otherCanvas, int valueInPercentage){
		int value = -1;
		if(otherCanvas instanceof CanvasSection){
			value = (int) Math.round((((CanvasSection)otherCanvas).getParentHeight() * ((CanvasSection)otherCanvas).getSectionHeight() * valueInPercentage) / (this.parentHeight * this.sectionHeight));
		}else if(otherCanvas instanceof CanvasLayout){
			value = (int) Math.round(((CanvasLayout)otherCanvas).getHeightRatio() * valueInPercentage * (100 * 100) / (this.parentHeight * this.sectionHeight));
		}
		return value;
	}
	
	public int getPercentageEqualWidthPixelWithPercentageFrom(ViewGroup otherCanvas, int valueInPercentage){
		int value = -1;
		if(otherCanvas instanceof CanvasSection){
			value = (int) Math.round((((CanvasSection)otherCanvas).getParentWidth() * ((CanvasSection)otherCanvas).getSectionWidth() * valueInPercentage) / (this.parentWidth * this.sectionWidth));
		}else if(otherCanvas instanceof CanvasLayout){
			value = (int) Math.round(((CanvasLayout)otherCanvas).getWidthRatio() * valueInPercentage * (100 * 100) / (this.parentWidth * this.sectionWidth));
		}
		return value;
	}

	
	public int getPercentageRelativeToWidth(int valueInPixel){
		return (int) Math.round((100 * 100* valueInPixel) / (parentWidth * sectionWidth));
	}
	

	
	public int getPercentageRelativeToHeight(int valueInPixel){
		return (int) Math.round((100 * 100 * valueInPixel) / (parentHeight * sectionHeight));
	}
	
	
	public void addViewWithFrame(View v, int left, int top, int width,
			int height) {
		int viewWidth = 0;
		if (width == RelativeLayout.LayoutParams.MATCH_PARENT
				|| width == RelativeLayout.LayoutParams.WRAP_CONTENT) {
			viewWidth = width;
		}else if(width == SAME_AS_OTHER_SIDE){
			//parentWidth * width = parentHeight * height
			//width = (parentHeight * height) / parentWidth;
			//viewWidth = (int) Math.round((parentWidth * sectionWidth * width) / (100 * 100));
		} else {
			viewWidth = (int) Math.round((parentWidth * sectionWidth * width) / (100 * 100));
		}
		
		int viewHeight = 0;
		if (height == RelativeLayout.LayoutParams.MATCH_PARENT
				|| height == RelativeLayout.LayoutParams.WRAP_CONTENT) {
			viewHeight = height;
		}else if(height == SAME_AS_OTHER_SIDE){
			//parentWidth * width = parentHeight * height
			//height = (parentWidth * width) / parentHeight;
			//viewHeight = (int) Math.round((parentHeight * sectionHeight * height) / (100 * 100));
		} else {
			viewHeight = (int) Math.round((parentHeight * sectionHeight * height) / (100 * 100));
		}


		if(width == SAME_AS_OTHER_SIDE){
			viewWidth = viewHeight;
		}else if(height == SAME_AS_OTHER_SIDE){
			viewHeight = viewWidth;
		}

		int leftX = (int) Math.round((parentWidth * sectionWidth * left) / (100 * 100));
		int topY = (int) Math.round((parentHeight * sectionHeight * top) / (100 * 100));

		

		DebugUtils.logW("SECTION", "height "+getSectionName()+": "+viewHeight);
		DebugUtils.logW("SECTION", "leftX "+getSectionName()+": "+leftX);
		
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				viewWidth, viewHeight);
		params.leftMargin = leftX;
		params.topMargin = topY;
		this.sectionLayout.addView(v, params);


		if(v instanceof CanvasSection){
			((CanvasSection)v).setSectionWidthInPixel(viewWidth);
			((CanvasSection) v).setSectionHeightInPixel(viewHeight);
		}
	}
	
	

	public void addViewInRelativeLayout(View v,
			RelativeLayout.LayoutParams param) {
		this.sectionLayout.addView(v, param);
	}
	


	public CanvasSection addSubSectionWithFrame(int left, int top, int width,
			int height) {
		return addSubSectionWithFrame(left, top, width, height, true);
	}

	public CanvasSection addSubSectionWithFrame(int left, int top, int width,
			int height, boolean noScroll) {
		return addSubSectionWithFrame("",left,top,width,height,noScroll, false, false, false);
	}
	
	public CanvasSection addSubSectionWithFrame(int left, int top, int width,
			int height, boolean noScroll, boolean isUseDefaultAppearAnimation, boolean isUseDefaultDisappearAnimation) {
		return addSubSectionWithFrame("",left,top,width,height,noScroll, isUseDefaultAppearAnimation, isUseDefaultDisappearAnimation, false);
	}

	public CanvasSection addSubSectionWithFrame(String sectionName, int left,
			int top, int width, int height, boolean noScroll) {
		return addSubSectionWithFrame(sectionName, left, top, width, height, noScroll, false, false, false);
	}
	
	public CanvasSection addSubSectionWithFrame(String sectionName, int left,
			int top, int width, int height, boolean noScroll, boolean useBackgroundView) {
		return addSubSectionWithFrame(sectionName, left, top, width, height, noScroll, false, false, false);
	}
	
	public CanvasSection addSubSectionWithFrame(String sectionName, int left,
			int top, int width, int height, boolean noScroll, boolean isUseDefaultAppearAnimation, boolean isUseDefaultDisappearAnimation, boolean useBackgroundView) {
		CanvasSection section = new CanvasSection(sectionName, context, left, top, width,
				height, (int) ((parentWidth * sectionWidth) / 100),
				(int) ((parentHeight * sectionHeight) / 100), noScroll);
		
		//section.setTag(SECTION_NAME_TAG, sectionName);
		
		if(isUseDefaultAppearAnimation){
			section.setAppearAnim(CommonUtils.setViewAnim_Appearslidedownfromtop(null, 800));
		}
		if(isUseDefaultDisappearAnimation){
			section.setDisappearAnim(CommonUtils.setViewAnim_disappearslideupfromBottom(null, 800));
		}

		addViewWithFrame(section, left, top, width, height);
		return section;
	}

	

	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus) {
		// TODO Auto-generated method stub
		super.onWindowFocusChanged(hasWindowFocus);
		Animation anim = null;
		if (hasWindowFocus) {
			if (appearAnim != null) {
				appearAnim
						.setAnimationListener(new Animation.AnimationListener() {

							@Override
							public void onAnimationStart(Animation animation) {
							}

							@Override
							public void onAnimationRepeat(Animation animation) {
							}

							@Override
							public void onAnimationEnd(Animation animation) {
								setAppearAnim(null);
							}
						});
				anim = appearAnim;
			}
		} else {
			if (disappearAnim != null) {
				disappearAnim
						.setAnimationListener(new Animation.AnimationListener() {

							@Override
							public void onAnimationStart(Animation animation) {
							}

							@Override
							public void onAnimationRepeat(Animation animation) {
							}

							@Override
							public void onAnimationEnd(Animation animation) {
								setDisappearAnim(null);
							}
						});
				anim = disappearAnim;
			}
		}
		Animation activeAnim = this.getAnimation();
		if (activeAnim != null) {
			activeAnim.cancel();
			activeAnim.reset();
		}
		this.clearAnimation();
		if (anim != null) {
			this.setAnimation(anim);
			anim.startNow();
		}
	}

	public CanvasSection setSectionAsLinearLayout(int linearOrientation) {
		return setSectionAsLinearLayout(0, 0, linearOrientation);
	}
	
	public CanvasSection setSectionAsLinearLayout(int left, int top, int linearOrientation) {
		ScrollView scrollView = null;
		if(noScroll){
			scrollView = new ScrollView(context);
			addViewWithFrame(scrollView, 0, 0, 100, 100);
		}
		linearLayout = new ASLinearLayout(context){
			@Override
			public boolean dispatchTouchEvent(MotionEvent ev) {
				boolean value = super.dispatchTouchEvent(ev);
				DebugUtils.logE("linearLayout", getSectionName()+" dispatchTouchEvent: " + value);
				return value;
			}
			@Override
			public boolean onInterceptTouchEvent(MotionEvent ev) {
				boolean value = super.onInterceptTouchEvent(ev);
				DebugUtils.logE("linearLayout", getSectionName()+" onInterceptTouchEvent: " + value);
				return value;
			}
		};
		

		linearLayout.setOrientation(linearOrientation);
		
		if(scrollView != null){
			if (linearOrientation == LinearLayout.VERTICAL) {
				addViewWithFrame(linearLayout, left, top, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
			} else if (linearOrientation == LinearLayout.HORIZONTAL) {
				addViewWithFrame(linearLayout, left, top, RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
			}
		}else{
			if (linearOrientation == LinearLayout.VERTICAL) {
				addViewWithFrame(linearLayout, left, top, 100, RelativeLayout.LayoutParams.WRAP_CONTENT);
			} else if (linearOrientation == LinearLayout.HORIZONTAL) {
				addViewWithFrame(linearLayout, left, top, RelativeLayout.LayoutParams.WRAP_CONTENT, 100);
			}
		}
		return this;
	}


	public void addViewInLinearLayout(View v, ASMovableMenu.LayoutViewMargin marginRect) {
		if (linearLayout != null) {
			int orientation = linearLayout.getOrientation();
			if(marginRect==null){
				marginRect = new ASMovableMenu.LayoutViewMargin(0,0,0,0);
			}
			if (orientation == LinearLayout.VERTICAL) {
				LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
						LinearLayout.LayoutParams.WRAP_CONTENT);
				param.setMargins(marginRect.getLeftMargin(),marginRect.getTopMargin(),marginRect.getRightMargin(),marginRect.getBottomMargin());
				linearLayout.addView(v, param);
			} else if (orientation == LinearLayout.HORIZONTAL) {
				LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
						LinearLayout.LayoutParams.MATCH_PARENT);
				param.setMargins(marginRect.getLeftMargin(),marginRect.getTopMargin(),marginRect.getRightMargin(),marginRect.getBottomMargin());
				linearLayout.addView(v, param);
			}
		}
	}

	public void addViewInLinearLayout(View v) {
		addViewInLinearLayout(v, (ASMovableMenu.LayoutViewMargin)null);
	}

	public void addViewInLinearLayout(View v, LinearLayout.LayoutParams param) {
		if (linearLayout != null) {
			linearLayout.addView(v, param);
		}
	}

	public void setVScrollOnTop() {
		// vScroll.post(new Runnable() {
		// public void run() {
		if(vScroll != null)
			vScroll.smoothScrollTo(0, 0);
		// }
		// });
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
	
	@Override
	public void setDrawingCacheEnabled(boolean enabled) {
        super.setDrawingCacheEnabled(enabled);
        /*
        final int l = getChildCount();
        for (int i = 0; i < l; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                child.setDrawingCacheEnabled(enabled);
            }
        }
        */
	}
	
	@Override
	public void removeAllViews() {
		if(linearLayout != null){
			linearLayout.removeAllViews();
		}else{
			sectionLayout.removeAllViews();
		}
	}
	

	public void shiftTarget(final CanvasSection shiftedSection,
							final boolean isVertical, final boolean isHorizontal,
							final boolean isJumpToNearest, final boolean isClickable) {
		this.setASGestureListener(new ASGestureListener() {
			@Override
			public boolean upEventOccurred(float x, float y) {
				if(isJumpToNearest){
					shiftedSection.getShiftPositionHandler().changeStateToNearestDimension(true);
					return true;
				}
				return false;
			}

			@Override
			public boolean downEventOccured(float x, float y) {
				return true;
			}

			@Override
			public boolean deltaMoveOutsideParameter(int swipeType, float x,
					float y, float dx, float dy, float fromDownDX, float fromDownDY) {
				shiftedSection.getShiftPositionHandler().shiftViewWithDelta(
						(isHorizontal ? dx : 0), (isVertical ? dy : 0), (isHorizontal ? fromDownDX : 0), (isVertical ? fromDownDY : 0));
				return true;
			}

			@Override
			public boolean deltaMoveInsideParameter(int swipeType, float x,
					float y, float dx, float dy, float fromDownDX, float fromDownDY) {
				shiftedSection.getShiftPositionHandler().shiftViewWithDelta(
						(isHorizontal ? dx : 0), (isVertical ? dy : 0), (isHorizontal ? fromDownDX : 0), (isVertical ? fromDownDY : 0));
				return true;
			}

			@Override
			public boolean movingSpeed(float xSpeed, float ySpeed) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean cancelEventOccured(float x, float y) {
				return false;
			}

			@Override
			public boolean clickEventOccured() {
				if(isClickable){
					shiftedSection.getShiftPositionHandler().changeStateToNextDimension(true);
					return true;
				}
				return false;
			}

			@Override
			public boolean longClickEventOccured() {
				return false;
			}

			@Override
			public boolean isClickEnabled() {
				// TODO Auto-generated method stub
				return true;
			}

			@Override
			public boolean isLongClickEnabled() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean doubleTapEventOccured() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean isDoubleTapEnabled() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean swipeEventOccured(int swipeType, float x, float y,
					float dx, float dy) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean swipeTypeFinal(int swipeType) {
				return false;
			}

			@Override
			public boolean isSwipeEnabled() {
				// TODO Auto-generated method stub
				return false;
			}
		});
	}
}
