package com.zaitunlabs.zlcore.views;

import android.graphics.Rect;
import android.view.ViewGroup;
import android.widget.ImageView.ScaleType;

public class ASShiftableImageView {
	CanvasSection section = null;
	ASImageView iv = null;
	private boolean isSticky = false;
	private boolean isLocked = false;
	public ASShiftableImageView() {
	}
	
	public ASImageView getImageView(){
		return iv;
	}

	public static ASShiftableImageView create(ViewGroup parent, int left, int top, int width, int height){
		return create(parent,left,top, width, height,false, false);
	}

	public void setPositionLocked(boolean isLocked){
		this.isLocked = isLocked;
	}

	public void setPositionToOrigin(boolean animate){
		section.getShiftPositionHandler().changeStateToDimension(0,animate);
	}

	public void setPositionToNearest(boolean animate){
		section.getShiftPositionHandler().changeStateToNearestDimension(animate);
	}

	public void setPositionToRect(Rect dimension, boolean animate){
		section.getShiftPositionHandler().changeStateToCustomDimension(dimension,10,animate);
	}

	public static ASShiftableImageView create(ViewGroup parent, int left, int top, int width, int height, boolean isSticky, boolean isLocked){
		final ASShiftableImageView shiftableImageView = new ASShiftableImageView();
		if(parent instanceof CanvasLayout)
			shiftableImageView.section = ((CanvasLayout)parent).createNewSectionWithFrame(left, top, width, height, true);
		else if(parent instanceof CanvasSection)
			shiftableImageView.section = ((CanvasSection)parent).addSubSectionWithFrame(left, top, width, height, true);
		shiftableImageView.iv = new ASImageView(parent.getContext());
		shiftableImageView.iv.setScaleType(ScaleType.FIT_XY);
		shiftableImageView.iv.setAdjustViewBounds(true);
		shiftableImageView.section.addViewWithFrame(shiftableImageView.iv, 0, 0, 100, 100);

		shiftableImageView.isSticky = isSticky;
		shiftableImageView.isLocked = isLocked;

		shiftableImageView.section.getShiftPositionHandler().addRectToDimensionState(left, top, width, height);
		shiftableImageView.section.getShiftPositionHandler().addRectToDimensionState(0, 0, width, height);
		shiftableImageView.section.getShiftPositionHandler().addRectToDimensionState(0, 100-height, width, height);
		shiftableImageView.section.getShiftPositionHandler().addRectToDimensionState(100-width, 100-height, width, height);
		shiftableImageView.section.getShiftPositionHandler().addRectToDimensionState(100-width, 0, width, height);
		
		
		shiftableImageView.iv.setASGestureListener(new ASGestureListener() {
			@Override
			public boolean upEventOccurred(float x, float y) {
				if (shiftableImageView.isSticky) {
					shiftableImageView.section.getShiftPositionHandler().changeStateToNearestDimension(true);
				}
				return true;
			}

			@Override
			public boolean downEventOccured(float x, float y) {
				return true;
			}

			@Override
			public boolean deltaMoveOutsideParameter(int swipeType, float x,
													 float y, float dx, float dy, float fromDownDX, float fromDownDY) {
				if (!shiftableImageView.isLocked) {
					shiftableImageView.section.getShiftPositionHandler().shiftViewWithDelta(dx, dy, fromDownDX, fromDownDY);
				}
				return true;
			}

			@Override
			public boolean deltaMoveInsideParameter(int swipeType, float x,
													float y, float dx, float dy, float fromDownDX, float fromDownDY) {
				if (!shiftableImageView.isLocked) {
					shiftableImageView.section.getShiftPositionHandler().shiftViewWithDelta(dx, dy, fromDownDX, fromDownDY);
				}
				return true;
			}

			@Override
			public boolean movingSpeed(float xSpeed, float ySpeed) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean cancelEventOccured(float x, float y) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean clickEventOccured() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean longClickEventOccured() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean isClickEnabled() {
				// TODO Auto-generated method stub
				return false;
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
		return shiftableImageView;
	}
}
