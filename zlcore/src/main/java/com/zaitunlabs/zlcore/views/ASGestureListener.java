package com.zaitunlabs.zlcore.views;

public interface ASGestureListener {
	public boolean deltaMoveInsideParameter(int swipeType, float x, float y, float dx, float dy, float fromDownDX, float fromDownDY);
	public boolean deltaMoveOutsideParameter(int swipeType, float x, float y, float dx, float dy, float fromDownDX, float fromDownDY);
	public boolean movingSpeed(float xSpeed, float ySpeed);
	public boolean upEventOccurred(float x, float y);
	public boolean downEventOccured(float x, float y);
	public boolean cancelEventOccured(float x, float y);
	public boolean clickEventOccured();
	public boolean longClickEventOccured();
	public boolean doubleTapEventOccured();
	public boolean swipeEventOccured(int swipeType, float x, float y, float dx, float dy);
	public boolean swipeTypeFinal(int swipeType);
	
	//property
	public boolean isClickEnabled();
	public boolean isLongClickEnabled();
	public boolean isDoubleTapEnabled();
	public boolean isSwipeEnabled();
}
