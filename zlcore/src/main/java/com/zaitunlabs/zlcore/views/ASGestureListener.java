package com.zaitunlabs.zlcore.views;

public interface ASGestureListener {
	boolean deltaMoveInsideParameter(int swipeType, float x, float y, float dx, float dy, float fromDownDX, float fromDownDY);
	boolean deltaMoveOutsideParameter(int swipeType, float x, float y, float dx, float dy, float fromDownDX, float fromDownDY);
	boolean movingSpeed(float xSpeed, float ySpeed);
	boolean upEventOccurred(float x, float y);
	boolean downEventOccured(float x, float y);
	boolean cancelEventOccured(float x, float y);
	boolean clickEventOccured();
	boolean longClickEventOccured();
	boolean doubleTapEventOccured();
	boolean swipeEventOccured(int swipeType, float x, float y, float dx, float dy);
	boolean swipeTypeFinal(int swipeType);
	
	//property
	boolean isClickEnabled();
	boolean isLongClickEnabled();
	boolean isDoubleTapEnabled();
	boolean isSwipeEnabled();
}
