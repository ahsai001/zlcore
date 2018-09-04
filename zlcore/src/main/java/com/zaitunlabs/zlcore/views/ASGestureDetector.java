package com.zaitunlabs.zlcore.views;

import android.content.Context;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;

import com.zaitunlabs.zlcore.utils.DebugUtils;


public class ASGestureDetector {
	private VelocityTracker vTouchTracker = null;
	private ASGestureListener gestureListener = null;

	private Context ctx = null;
	private View view = null;
	private String viewName = null;
	//this four is relative to last move point, updated when move
	private float touchDownX = -1;
	private float touchDownY = -1;
	private float touchDownRawX = -1;
	private float touchDownRawY = -1;
	
	//this two is real point from down event
	private float touchDownAbsoluteX = -1;
	private float touchDownAbsoluteY = -1;

	private static final float MAX_MOVE_THRESHOLD = 7; // in pixel
	private static final float SWIPE_SENSITIVITY_THRESHOLD = 40; // in pixel
	private boolean isMoving = false;
	private static final float SPEED_MOVE_THRESHOLD = 5; // in pixel

	// paremeter to handle longClick
	private static final int MIN_LONGCLICK_DURATION = 1200;
	private static final int WHAT_LONGCLICK_MESSAGE = 0;
	private Handler longClickHandler = null;
	private boolean isLongClickActive = false;

	// paremeter to handle double tap
	private static final int DELAY_BETWEEN_TAP_ON_DOUBLETAP = 300;
	private long firstTapTime = -1;
	private boolean isDoubleTapActive = false;
	private Handler oneTapHandler = null;
	
	
	//parameter to handle swipe
	public static final int SWIPE_LEFT = 0;
	public static final int SWIPE_RIGHT = 1;
	public static final int SWIPE_UP = 2;
	public static final int SWIPE_DOWN = 3;
	public static final int SWIPE_NONE = 4;

	private int globalSwipeType = SWIPE_NONE;

	public ASGestureListener getGestureListener() {
		return gestureListener;
	}

	public void setGestureListener(ASGestureListener gestureListener) {
		this.gestureListener = gestureListener;
	}

	public ASGestureDetector(Context ctx, ASGestureListener gestureListener) {
		this.ctx = ctx;
		this.gestureListener = gestureListener;
		this.viewName = this.toString();
	}

	public ASGestureDetector(String viewName, Context ctx) {
		this.ctx = ctx;
		this.viewName = viewName;
	}
	public ASGestureDetector(Context ctx) {
		this.ctx = ctx;
		this.viewName = this.toString();
	}

	public boolean handleOnTouch(View v, MotionEvent event) {
		this.view = v;
		int action = event.getAction();
		/*
		 * int action = event.getActionMasked(); int pointerCount =
		 * event.getPointerCount(); int index = event.getActionIndex(); int
		 * pointer = event.getPointerId(index); DebugUtils.logE("onTouchEvent",
		 * getSectionName
		 * ()+"count:"+pointerCount+"---index:"+index+"---pointer:"
		 * +pointer+" View:"+v,isLogged);
		 */
		switch (action & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			if (VERSION.SDK_INT < 16) {
				if (vTouchTracker == null) {
					//vTouchTracker = VelocityTracker.obtain();
				} else {
					//vTouchTracker.clear();
				}
				//vTouchTracker.addMovement(event);
			}

			touchDownX = event.getX();
			touchDownY = event.getY();
			touchDownRawX = event.getRawX();
			touchDownRawY = event.getRawY();
			
			touchDownAbsoluteX = event.getRawX();
			touchDownAbsoluteY = event.getRawY();

			DebugUtils.logE("DETECTOR", "down x : "+Math.abs(event.getRawX()) + " for "+this.viewName);
			
			isMoving = false;

			// DoubleTap Detection
			isDoubleTapActive = false;
			if (gestureListener != null && gestureListener.isDoubleTapEnabled()) {
				if (firstTapTime > -1) {
					long currentTime = System.currentTimeMillis();
					long deltaDoubleTap = currentTime - firstTapTime;
					if (deltaDoubleTap < DELAY_BETWEEN_TAP_ON_DOUBLETAP) {
						oneTapHandler.removeMessages(0);
						gestureListener.doubleTapEventOccured();
						isDoubleTapActive = true;
					}
					firstTapTime = currentTime;
				}
			}
			// LongClick detection
			isLongClickActive = false;
			if (gestureListener != null && gestureListener.isLongClickEnabled()) {
				longClickHandler = new Handler() {
					@Override
					public void handleMessage(Message msg) {
						// long click detected
						isLongClickActive = true;
						gestureListener.longClickEventOccured();
					}
				};
				longClickHandler.sendMessageDelayed(Message.obtain(),
						MIN_LONGCLICK_DURATION);
			}

			if (gestureListener != null) {
				return gestureListener.downEventOccured(event.getX(), event.getY());
			}
			break;
		case MotionEvent.ACTION_MOVE:
			float xMove = event.getX();
			float yMove = event.getY();
			float xRawMove = event.getRawX();
			float yRawMove = event.getRawY();
			float deltaX = xMove - touchDownX;
			float deltaY = yMove - touchDownY;
			float deltaRawX = xRawMove - touchDownRawX;
			float deltaRawY = yRawMove - touchDownRawY;

			float deltaDownRawX = xRawMove - touchDownAbsoluteX;
			float deltaDownRawY = yRawMove - touchDownAbsoluteY;
			
			DebugUtils.logE("DETECTOR", "move a x : " + Math.abs(event.getRawX()) + " for " + this.viewName);
			//CommonUtils.LogV("DETECTOR", "yRawMove=" + yRawMove + "-" + "touchDownAbsoluteY=" + touchDownAbsoluteY + "-" + "deltaRawY=" + deltaDownRawY);
			float move_threshold = 0;

			// ViewConfiguration vc = ViewConfiguration.get(getContext());
			// int mSlop = vc.getScaledTouchSlop();
			// int mMinFlingVelocity = vc.getScaledMinimumFlingVelocity();
			// int mMaxFlingVelocity = vc.getScaledMaximumFlingVelocity();
			// CommonUtils.LogV("", "mSlop " + mSlop +" ",isLogged);
			// CommonUtils.LogV("", "mMinFlingVelocity " + mMinFlingVelocity
			// +" ",isLogged);
			// CommonUtils.LogV("", "mMaxFlingVelocity " + mMaxFlingVelocity
			// +" ",isLogged);

			if (isMoving == false && gestureListener != null && ( gestureListener.isClickEnabled() || gestureListener.isLongClickEnabled())) {
				move_threshold = MAX_MOVE_THRESHOLD;
			} else {
				// on moving
				move_threshold = 0;
			}

			float xSpeed = 0;
			float ySpeed = 0;

			if (VERSION.SDK_INT < 16) {
				//vTouchTracker.addMovement(event);
				//vTouchTracker.computeCurrentVelocity(1000);
				//xSpeed = vTouchTracker.getXVelocity();
				//ySpeed = vTouchTracker.getYVelocity();
			}

			if (Math.abs(deltaRawX) > move_threshold || Math.abs(deltaRawY) > move_threshold) {
				// Movement detected !!!!!!!!!
				DebugUtils.logE("DETECTOR", "move x : "+Math.abs(event.getRawX()) + " for "+this.viewName);
				isMoving = true;

				// cancel longclick sending message
				if (longClickHandler != null)
					longClickHandler.removeMessages(WHAT_LONGCLICK_MESSAGE);

				/*
				 * //swipe left right or swipe up down ==> experiment if((ySpeed
				 * == 0 && xSpeed > 0) || (ySpeed > 0 && xSpeed > 0 && xSpeed >
				 * ySpeed && (xSpeed / ySpeed) > SPEED_MOVE_THRESHOLD)){
				 * deltaRawY = 0; CommonUtils.LogV("","kanan kiri aktif"); }
				 * if((ySpeed > 0 && xSpeed == 0) || (ySpeed > 0 && xSpeed > 0
				 * && ySpeed > xSpeed && (ySpeed / xSpeed) >
				 * SPEED_MOVE_THRESHOLD)){ deltaRawX = 0;
				 * CommonUtils.LogV("","atas bawah aktif"); }
				 */

				touchDownX = xMove;
				touchDownY = yMove;

				touchDownRawX = xRawMove;
				touchDownRawY = yRawMove;

				if (gestureListener != null) {
					gestureListener.movingSpeed(xSpeed, ySpeed);
					
					
					//detection of swipe
					double degree = 0;
					int swypeType = SWIPE_NONE;
					
					//DebugUtils.logE("DETECTOR", "deltaDownRaw x : "+Math.abs(deltaDownRawX));
					//DebugUtils.logE("DETECTOR", "deltaDownRaw y : "+Math.abs(deltaDownRawY));
					
					if(gestureListener.isSwipeEnabled() && (Math.abs(deltaDownRawX) > SWIPE_SENSITIVITY_THRESHOLD || Math.abs(deltaDownRawY) > SWIPE_SENSITIVITY_THRESHOLD)){

						if(deltaDownRawY > 0 && deltaDownRawX == 0){
							degree = 90; 
						}else{
							degree = Math.toDegrees(Math.atan(Math.abs(deltaDownRawY / deltaDownRawX)));
						}


						//CommonUtils.LogV("ahmad", "deegre:"+degree);
						//CommonUtils.LogV("ahmad", "deltaRawY:"+deltaRawY);
						//CommonUtils.LogV("ahmad", "deltaRawX:"+deltaRawX);
						if(degree > 45){
							//swipe up-down
							if(deltaDownRawY > 0){
								//down
								swypeType = SWIPE_DOWN;
							}else if(deltaDownRawY < 0){
								//up
								swypeType = SWIPE_UP;
							}else{
								//maybe no moving
							}
						}else{
							//swipe left-right
							if(deltaDownRawX > 0){
								//right
								swypeType = SWIPE_RIGHT;
							}else if(deltaDownRawX < 0){
								//left
								swypeType = SWIPE_LEFT;
							}else{
								//maybe no moving
							}
						}
					}

					globalSwipeType = swypeType;
					//CommonUtils.LogV("ahmad", "swypeType:"+swypeType);
					//CommonUtils.LogV("ahmad", "globalSwipeType:"+globalSwipeType);

					boolean returnValue = false;
					if ((xMove > view.getWidth() || xMove < 0)
							|| (yMove > view.getHeight() || yMove < 0)) {
						returnValue = gestureListener.deltaMoveOutsideParameter(swypeType, xMove,
								yMove, deltaRawX, deltaRawY, deltaDownRawX, deltaDownRawY);
					} else {
						returnValue = gestureListener.deltaMoveInsideParameter(swypeType, xMove,
								yMove, deltaRawX, deltaRawY, deltaDownRawX, deltaDownRawY);
					}
					if(gestureListener.isSwipeEnabled() && (Math.abs(deltaDownRawX) > SWIPE_SENSITIVITY_THRESHOLD || Math.abs(deltaDownRawY) > SWIPE_SENSITIVITY_THRESHOLD)){
						returnValue = gestureListener.swipeEventOccured(swypeType, xMove,
								yMove, deltaRawX, deltaRawY);
					}
					return returnValue;
				}

				/*
				 * try { Thread.sleep(10); } catch (InterruptedException e) {
				 * e.printStackTrace(); }
				 */
				
				//return true;
			} else {
				//return true;
			}
			break;
		case MotionEvent.ACTION_UP:
			if (VERSION.SDK_INT < 16) {
				//vTouchTracker.recycle();
			}

			touchDownX = touchDownY = -1;
			touchDownRawX = touchDownRawY = -1;

			touchDownAbsoluteX = touchDownAbsoluteY = -1;

			int isSwipeType = globalSwipeType;
			globalSwipeType = SWIPE_NONE;

			// cancel longclick sending message
			if (longClickHandler != null)
				longClickHandler.removeMessages(WHAT_LONGCLICK_MESSAGE);

			if (isLongClickActive || isDoubleTapActive)
				return true;

			if (gestureListener != null) {
				if (gestureListener.isDoubleTapEnabled() && !isMoving) {
					oneTapHandler = new Handler(){
						@Override
						public void handleMessage(Message msg) {
							if (gestureListener.isClickEnabled() && !isMoving) {
								if (gestureListener.clickEventOccured()) {
								}
							}
						}
					};
					firstTapTime = System.currentTimeMillis();
					oneTapHandler.sendMessageDelayed(Message.obtain(), DELAY_BETWEEN_TAP_ON_DOUBLETAP);
					return true;
				} else {
					if (gestureListener.isClickEnabled() && !isMoving) {
						if (gestureListener.clickEventOccured()) {
							return true;
						}
					}else{
						if(gestureListener.isSwipeEnabled()){
							gestureListener.swipeTypeFinal(isSwipeType);
						}
					}
				}
				return gestureListener.upEventOccurred(event.getX(),
						event.getY());
			}
			break;
		case MotionEvent.ACTION_CANCEL:
			if (VERSION.SDK_INT < 16) {
				//vTouchTracker.recycle();
			}
			touchDownX = touchDownY = -1;
			touchDownRawX = touchDownRawY = -1;
			touchDownAbsoluteX = touchDownAbsoluteY = -1;

			globalSwipeType = SWIPE_NONE;

			// cancel longclick sending message
			if (longClickHandler != null)
				longClickHandler.removeMessages(WHAT_LONGCLICK_MESSAGE);

			if (isLongClickActive || isDoubleTapActive)
				return true;

			if (gestureListener != null) {
				return gestureListener.cancelEventOccured(event.getX(),
						event.getY());
			}
			break;

		// extra when use mask and pointer or multi touch
		case MotionEvent.ACTION_OUTSIDE:
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			break;
		case MotionEvent.ACTION_POINTER_UP:
			break;

		default:
			break;
		}
		// invalidate();
		return false;
	}

}
