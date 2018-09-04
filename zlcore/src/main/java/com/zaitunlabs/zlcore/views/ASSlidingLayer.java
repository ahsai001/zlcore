package com.zaitunlabs.zlcore.views;

import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

import com.zaitunlabs.zlcore.R;
import com.zaitunlabs.zlcore.utils.DebugUtils;


public class ASSlidingLayer {
	CanvasSection sliderPanel = null;
	CanvasSection sliderHandle = null;
	CanvasSection sliderContentPanel = null;
	TextView handleText = null;
	ASImageView handleInfoImage = null;
	private String closedHandleStatement = null;
	private String openedHandleStatement = null;

	int sliderHandlePercentage = 10; // default 10

	private ASSlidingLayerListener slidingListener = null;

	public static final int STICK_TO_BOTTOM = 1;
	public static final int STICK_TO_UP = 2;
	public static final int STICK_TO_LEFT = 3;
	public static final int STICK_TO_RIGHT = 4;
	public static final int STICK_TO_NONE = 5;
	public static final int STICK_TO_CUSTOM = 6;


	public int OPEN_STATE = 1;
	public int CLOSE_STATE = 0;
	
	int stickType = STICK_TO_NONE;

	public CanvasSection getSliderPanel() {
		return sliderPanel;
	}

	public CanvasSection getSliderHandle() {
		return sliderHandle;
	}

	public CanvasSection getSliderContentPanel() {
		return sliderContentPanel;
	}

	public TextView getHandleText() {
		return handleText;
	}


	public void setSlidingListener(ASSlidingLayerListener slidingListener){
		this.slidingListener = slidingListener;
	}

	public void setClosedHandleStatement(String closedHandleStatement) {
		this.closedHandleStatement = closedHandleStatement;
		if(sliderPanel.getShiftPositionHandler().getDimensionState() == 0 && handleText != null)
			handleText.setText(closedHandleStatement);
	}

	public void setOpenedHandleStatement(String openedHandleStatement) {
		this.openedHandleStatement = openedHandleStatement;
		if(sliderPanel.getShiftPositionHandler().getDimensionState() == 1 && handleText != null)
			handleText.setText(openedHandleStatement);
	}

	
	public static ASSlidingLayer create(CanvasSection parentCanvas, Rect firstLocation, Rect secondLocation, int sliderHandlePercentage, int stickType, boolean noScroll) {
		int minSectionX, maxSectionX, minSectionY, maxSectionY;
		int handleLeft = 0, handleTop = 0, handleWidth = 0, handleHeight = 0;
		int contentLeft = 0, contentTop = 0, contentWidth = 0, contentHeight = 0;
		
		final ASSlidingLayer slidingLayer = new ASSlidingLayer();
		slidingLayer.sliderHandlePercentage = sliderHandlePercentage;
		slidingLayer.sliderPanel = parentCanvas.addSubSectionWithFrame("sliderPanel",
				firstLocation.left, firstLocation.top, firstLocation.width(),
				firstLocation.height(), true);
		slidingLayer.sliderPanel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//to isolate touch to not propagate behind this sliding
			}
		});
		slidingLayer.sliderPanel.getShiftPositionHandler().addRectToDimensionState(firstLocation.left,
				firstLocation.top, firstLocation.width(),
				firstLocation.height());
		slidingLayer.sliderPanel.getShiftPositionHandler().addRectToDimensionState(secondLocation.left,
				secondLocation.top, secondLocation.width(),
				secondLocation.height());

		if (firstLocation.left <= secondLocation.left) {
			minSectionX = firstLocation.left;
			maxSectionX = secondLocation.left;
		} else {
			minSectionX = secondLocation.left;
			maxSectionX = firstLocation.left;
		}

		if (firstLocation.top <= secondLocation.top) {
			minSectionY = firstLocation.top;
			maxSectionY = secondLocation.top;
		} else {
			minSectionY = secondLocation.top;
			maxSectionY = firstLocation.top;
		}
		
		if(stickType != STICK_TO_CUSTOM){
			slidingLayer.sliderPanel.getShiftPositionHandler().setMinMaxLocationXY(minSectionX, maxSectionX,minSectionY, maxSectionY);
		}
		slidingLayer.sliderPanel.setBackgroundColor(Color.argb(200, 0, 0, 0));
		//slidingLayer.sliderPanel.unConsumeEvent();
		slidingLayer.sliderPanel.getShiftPositionHandler().setDimensionStateListener(new DimensionStateListener() {
			@Override
			public boolean rectForCurrentDimensionState(
					Rect currentRectState) {
				return false;
			}

			@Override
			public boolean indexForCurrentDimensionState(
					int currentIndexState) {
				if (currentIndexState == slidingLayer.OPEN_STATE) {
					if(slidingLayer.openedHandleStatement != null)
						slidingLayer.handleText.setText(slidingLayer.openedHandleStatement);
					slidingLayer.sliderPanel.consumeEvent();
					slidingLayer.handleInfoImage.setImageResource(R.drawable.arrow_down_left);
				} else if (currentIndexState == slidingLayer.CLOSE_STATE) {
					if(slidingLayer.closedHandleStatement != null)
						slidingLayer.handleText.setText(slidingLayer.closedHandleStatement);
					slidingLayer.sliderPanel.unConsumeEvent();
					slidingLayer.handleInfoImage.setImageResource(R.drawable.arrow_up_right);
				}
				if(slidingLayer.slidingListener != null){
					slidingLayer.slidingListener.slidingState(currentIndexState);
				}
				return false;
			}
		});

		switch (stickType) {
		case STICK_TO_BOTTOM:
		case STICK_TO_CUSTOM:
			handleLeft = 0;handleTop = 0;handleWidth = 100;handleHeight = slidingLayer.sliderHandlePercentage;
			contentLeft = 0;contentTop = slidingLayer.sliderHandlePercentage;contentWidth = 100;contentHeight = 100 - slidingLayer.sliderHandlePercentage;
			break;
		case STICK_TO_UP:
			handleLeft = 0;handleTop = 100 - slidingLayer.sliderHandlePercentage;handleWidth = 100;handleHeight = slidingLayer.sliderHandlePercentage;
			contentLeft = 0;contentTop = 0;contentWidth = 100;contentHeight = 100 - slidingLayer.sliderHandlePercentage;
			break;
		case STICK_TO_LEFT:
			handleLeft = 100 - slidingLayer.sliderHandlePercentage;handleTop = 0;handleWidth = slidingLayer.sliderHandlePercentage;handleHeight = 100;
			contentLeft = 0;contentTop = 0;contentWidth = 100 - slidingLayer.sliderHandlePercentage;contentHeight = 100;
			break;
		case STICK_TO_RIGHT:
			handleLeft = 0;handleTop = 0;handleWidth = slidingLayer.sliderHandlePercentage;handleHeight = 100;
			contentLeft = slidingLayer.sliderHandlePercentage;contentTop = 0;contentWidth = 100 - slidingLayer.sliderHandlePercentage;contentHeight = 100;
			break;

		default:
			break;
		}
		slidingLayer.sliderHandle = slidingLayer.sliderPanel.addSubSectionWithFrame("sliderHandle",handleLeft, handleTop, handleWidth, handleHeight, true);
		slidingLayer.sliderContentPanel = slidingLayer.sliderPanel.addSubSectionWithFrame("sliderContentPanel",contentLeft, contentTop, contentWidth, contentHeight, noScroll);

		slidingLayer.sliderContentPanel.getShiftPositionHandler().addRectToDimensionState(contentLeft, contentTop, contentWidth, contentHeight);

		slidingLayer.sliderHandle.setBackgroundColor(Color.argb(200, 0, 255, 0));
		/*
		slidingLayer.sliderHandle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// need implement this to activate click and sliding
				// together, any other ways?
			}
		});
		*/
		switch (stickType) {
		case STICK_TO_CUSTOM:
		case STICK_TO_BOTTOM:
		case STICK_TO_UP:
			slidingLayer.handleText = new TextView(parentCanvas.getContext());
			slidingLayer.handleText.setGravity(Gravity.CENTER);
			break;
		case STICK_TO_LEFT:
			slidingLayer.handleText = new VerticalTextView(parentCanvas.getContext());
			slidingLayer.handleText.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL | Gravity.TOP);
			break;
		case STICK_TO_RIGHT:
			slidingLayer.handleText = new VerticalTextView(parentCanvas.getContext());
			slidingLayer.handleText.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
			break;
		default:
			break;
		}
		
		if(slidingLayer.closedHandleStatement != null)
			slidingLayer.handleText.setText(slidingLayer.closedHandleStatement);
		slidingLayer.handleText.setTextColor(Color.BLACK);
		slidingLayer.handleText.setTypeface(null, Typeface.BOLD);
		slidingLayer.sliderHandle.addViewWithFrame(slidingLayer.handleText, 0, 0, 100, 100);
		
		//set information image
		slidingLayer.handleInfoImage = new ASImageView(parentCanvas.getContext());
		slidingLayer.handleInfoImage.setScaleType(ScaleType.FIT_END);
		slidingLayer.handleInfoImage.setImageResource(R.drawable.arrow_up_right);
		slidingLayer.sliderHandle.addViewWithFrame(slidingLayer.handleInfoImage, 0, 0, 100, 100);
		
		slidingLayer.stickType = stickType;
		slidingLayer.sliderHandle.setASGestureListener(new ASGestureListener() {
			@Override
			public boolean upEventOccurred(float x, float y) {
				slidingLayer.sliderPanel.getShiftPositionHandler().changeStateToNearestDimension(true);
				return true;
			}

			@Override
			public boolean downEventOccured(float x, float y) {
				return true;
			}

			@Override
			public boolean deltaMoveInsideParameter(int swipeType, float x,
					float y, float dx, float dy, float fromDownDX, float fromDownDY) {
				switch (slidingLayer.stickType) {
				case STICK_TO_BOTTOM:
				case STICK_TO_UP:
					slidingLayer.sliderPanel.getShiftPositionHandler().shiftViewWithDelta(0, dy, 0, fromDownDY);
					break;
				case STICK_TO_LEFT:
				case STICK_TO_RIGHT:
					slidingLayer.sliderPanel.getShiftPositionHandler().shiftViewWithDelta(dx, 0, fromDownDX, 0);
					break;
				case STICK_TO_CUSTOM:
					slidingLayer.sliderPanel.getShiftPositionHandler().shiftViewWithDelta(dx, dy, fromDownDX, fromDownDY);
					break;
				default:
					break;
				}
				return true;
			}

			@Override
			public boolean deltaMoveOutsideParameter(int swipeType, float x,
					float y, float dx, float dy, float fromDownDX, float fromDownDY) {
				switch (slidingLayer.stickType) {
				case STICK_TO_BOTTOM:
				case STICK_TO_UP:
					slidingLayer.sliderPanel.getShiftPositionHandler().shiftViewWithDelta(0, dy, 0, fromDownDY);
					break;
				case STICK_TO_LEFT:
				case STICK_TO_RIGHT:
					slidingLayer.sliderPanel.getShiftPositionHandler().shiftViewWithDelta(dx, 0, fromDownDX, 0);
					break;
				case STICK_TO_CUSTOM:
					slidingLayer.sliderPanel.getShiftPositionHandler().shiftViewWithDelta(dx, dy, fromDownDX, fromDownDY);
					break;
				default:
					break;
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
				return true;
			}

			@Override
			public boolean clickEventOccured() {
				slidingLayer.sliderPanel.getShiftPositionHandler().changeStateToNextDimension(true);
				return true;
			}

			@Override
			public boolean longClickEventOccured() {
				DebugUtils.logE("long Click", "OK");
				return true;
			}

			@Override
			public boolean isClickEnabled() {
				return true;
			}

			@Override
			public boolean isLongClickEnabled() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean doubleTapEventOccured() {
				DebugUtils.logE("DoubleTap", "OK");
				return true;
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


		slidingLayer.sliderPanel.setMaintainRatio(false);
		slidingLayer.sliderHandle.setMaintainRatio(false);
		slidingLayer.sliderContentPanel.setMaintainRatio(false);
		return slidingLayer;

	}

	public static ASSlidingLayer create(CanvasSection parentCanvas,
			int stickType, int width, int height, int sliderHandlePercentage, boolean noScroll) {
		Rect firstLocation = null;
		Rect secondLocation = null;
		int xCenter = (100-width)/2;
		int yCenter = (100-height)/2;
		switch (stickType) {
		case STICK_TO_BOTTOM:
			firstLocation = new Rect(xCenter, 100 - sliderHandlePercentage, xCenter + width, 100 - sliderHandlePercentage + height);
			secondLocation = new Rect(0, 0, 0 + width, 0 + height);
			break;
		case STICK_TO_UP:
			firstLocation = new Rect(xCenter, sliderHandlePercentage - height, xCenter + width, sliderHandlePercentage);
			secondLocation = new Rect(xCenter, 0, xCenter + width, 0 + height);
			break;
		case STICK_TO_LEFT:
			firstLocation = new Rect(sliderHandlePercentage - width, yCenter, sliderHandlePercentage, yCenter+height);
			secondLocation = new Rect(0, yCenter, 0+width, yCenter+height);
			break;
		case STICK_TO_RIGHT:
			firstLocation = new Rect(100-sliderHandlePercentage, yCenter, 100-sliderHandlePercentage+width, yCenter+height);
			secondLocation = new Rect(0, yCenter, 0+width, yCenter+height);
			break;
		default:
			break;
		}
		ASSlidingLayer slidingLayer = ASSlidingLayer.create(parentCanvas, firstLocation, secondLocation, sliderHandlePercentage, stickType, noScroll);
		return slidingLayer;

	}
	
	public void openLayer(boolean animation){
		if(sliderPanel.getShiftPositionHandler().getDimensionState() == CLOSE_STATE)
			sliderPanel.getShiftPositionHandler().changeStateToDimension(OPEN_STATE, animation);
	}
	
	public void closeLayer(boolean animation){
		if(sliderPanel.getShiftPositionHandler().getDimensionState() == OPEN_STATE)
			sliderPanel.getShiftPositionHandler().changeStateToDimension(CLOSE_STATE, animation);
	}



	public interface ASSlidingLayerListener{
		public void slidingState(int state);
	}
}
