package com.zaitunlabs.zlcore.views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;


import com.zaitunlabs.zlcore.R;
import com.zaitunlabs.zlcore.core.CanvasActivity;
import com.zaitunlabs.zlcore.utils.CommonUtil;

import java.util.ArrayList;
import java.util.HashMap;

public class ASMovableMenu {
	CanvasSection menuSection = null;
	CanvasSection handleSection = null;
	ViewGroup canvasParent = null;
	private boolean isMenuOpened = false; 
	int handleWidth, handleHeight, menuWidth, menuHeight;

	
	private View blockingLayer = null;
	private Context context = null;
	
	private ASMovableMenuListener listener = null;
	
	private HashMap<Integer,ArrayList<Object>> itemHandlerList = null;
	private Integer currentSelectedItem = -1;
	
	private ASImageView settingImage = null;

	private ASTextView settingText = null;


	private static int lastDimensionState = 1;
	private static int closedSettingImage = 0;
	private static int openedSettingImage = 0;


	private int openedIndexState = 100;
	private Rect openedDefaultDimension = null;
	private boolean isMovable = true;
	
	private ArrayList<View> viewList = null;
	
	private ArrayList<Rect> rectList = null;
	private ArrayList<LayoutViewMargin> marginList = null;
	

	public ArrayList<View> getViewList() {
		return viewList;
	}
	public ArrayList<Rect> getRectList() {
		return rectList;
	}

	public static void setClosedSettingImage(int closedSettingImage) {
		ASMovableMenu.closedSettingImage = closedSettingImage;
	}
	public static void setOpenedSettingImage(int openedSettingImage) {
		ASMovableMenu.openedSettingImage = openedSettingImage;
	}
	public void setListener(ASMovableMenuListener listener) {
		this.listener = listener;
	}
	public CanvasSection getMenuSection() {
		return menuSection;
	}
	public CanvasSection getHandleSection() {
		return handleSection;
	}

	public boolean isMenuOpened() {
		return isMenuOpened;
	}
	
	
	public static ASMovableMenu create(ViewGroup parent, int handleWidth, int handleHeight, int menuWidth, int menuHeight){
		final ASMovableMenu movableMenu = new ASMovableMenu();

		movableMenu.canvasParent = parent;
		if(handleWidth == CanvasSection.SAME_AS_OTHER_SIDE){
			if(parent instanceof CanvasSection){
				//parentWidth * width = parentHeight * height
				handleWidth = (int) ((((CanvasSection)parent).getParentHeight() * handleHeight) / ((CanvasSection)parent).getParentWidth());
			}else if(parent instanceof CanvasLayout){
				//widthRatio * width = heightRatio * height
				handleWidth = (int) ((int) ((((CanvasLayout)parent).getHeightRatio()) * handleHeight) / ((CanvasLayout)parent).getWidthRatio());
			}
		}else if(handleHeight == CanvasSection.SAME_AS_OTHER_SIDE){
			if(parent instanceof CanvasSection){
				//parentWidth * width = parentHeight * height
				handleHeight = (int) ((((CanvasSection)parent).getParentWidth() * handleWidth) / ((CanvasSection)parent).getParentHeight());
			}else if(parent instanceof CanvasLayout){
				//widthRatio * width = heightRatio * height
				handleHeight = (int) ((int) ((((CanvasLayout)parent).getWidthRatio()) * handleWidth) / ((CanvasLayout)parent).getHeightRatio());
			}
		}
		
		if(menuWidth == CanvasSection.SAME_AS_OTHER_SIDE){
			if(parent instanceof CanvasSection){
				//parentWidth * width = parentHeight * height
				menuWidth = (int) ((((CanvasSection)parent).getParentHeight() * menuHeight) / ((CanvasSection)parent).getParentWidth());
			}else if(parent instanceof CanvasLayout){
				//widthRatio * width = heightRatio * height
				menuWidth = (int) ((int) ((((CanvasLayout)parent).getHeightRatio()) * menuHeight) / ((CanvasLayout)parent).getWidthRatio());
			}
		}else if(menuHeight == CanvasSection.SAME_AS_OTHER_SIDE){
			if(parent instanceof CanvasSection){
				//parentWidth * width = parentHeight * height
				menuHeight = (int) ((((CanvasSection)parent).getParentWidth() * menuWidth) / ((CanvasSection)parent).getParentHeight());
			}else if(parent instanceof CanvasLayout){
				//widthRatio * width = heightRatio * height
				menuHeight = (int) ((int) ((((CanvasLayout)parent).getWidthRatio()) * menuWidth) / ((CanvasLayout)parent).getHeightRatio());
			}
		}
		
		
		movableMenu.handleWidth = handleWidth;
		movableMenu.handleHeight = handleHeight;
		movableMenu.menuWidth = menuWidth;
		movableMenu.menuHeight  = menuHeight;
		
		movableMenu.context = parent.getContext();
		
		//create blockinglayer with initial state is invisible
		if(parent instanceof CanvasSection){
			movableMenu.blockingLayer = new View(((CanvasSection)parent).getContext());
			((CanvasSection)parent).addViewWithFrame(movableMenu.blockingLayer, 0, 0, 100, 100);
		}else if(parent instanceof CanvasLayout){
			movableMenu.blockingLayer = new View(((CanvasLayout)parent).getContext());
			((CanvasLayout)parent).addViewWithFrame(movableMenu.blockingLayer, 0, 0, 100, 100);
		}
		movableMenu.blockingLayer.setVisibility(View.GONE);
		movableMenu.blockingLayer.setBackgroundColor(Color.argb(200,10,10,10));
		movableMenu.blockingLayer.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				movableMenu.closeMenu(true);
			}
		});
				
		
		//create menusection first
		if(parent instanceof CanvasSection)
			movableMenu.menuSection = ((CanvasSection)parent).addSubSectionWithFrame("menuSection", 0, 0, handleWidth, handleHeight, false, true);
		else if(parent instanceof CanvasLayout)
			movableMenu.menuSection = ((CanvasLayout)parent).createNewSectionWithFrame("menuSection", 0, 0, handleWidth, handleHeight, false, true);
		movableMenu.menuSection.getShiftPositionHandler().addRectToDimensionState(0, 0, handleWidth, handleHeight);
		movableMenu.menuSection.getShiftPositionHandler().addRectToDimensionState((100-handleWidth)/2, 0, handleWidth, handleHeight);
		movableMenu.menuSection.getShiftPositionHandler().addRectToDimensionState((100-handleWidth), 0, handleWidth, handleHeight);
		movableMenu.menuSection.getShiftPositionHandler().addRectToDimensionState(0, (100-handleHeight)/2, handleWidth, handleHeight);
		movableMenu.menuSection.getShiftPositionHandler().addRectToDimensionState((100-handleWidth), (100-handleHeight)/2, handleWidth, handleHeight);
		movableMenu.menuSection.getShiftPositionHandler().addRectToDimensionState(0, (100-handleHeight), handleWidth, handleHeight);
		movableMenu.menuSection.getShiftPositionHandler().addRectToDimensionState((100-handleWidth)/2, (100-handleHeight), handleWidth, handleHeight);
		movableMenu.menuSection.getShiftPositionHandler().addRectToDimensionState((100-handleWidth), (100-handleHeight), handleWidth, handleHeight);


		movableMenu.setOpenedDefaultDimensionState((100-menuWidth)/2, (100-menuHeight)/2, menuWidth, menuHeight);
		
		movableMenu.menuSection.getShiftPositionHandler().setDimensionStateListener(new DimensionStateListener() {
			@Override
			public boolean rectForCurrentDimensionState(Rect currentRectState) {
				return false;
			}
			
			@Override
			public boolean indexForCurrentDimensionState(int currentIndexState) {
				if(currentIndexState == -1)
					return false;
				if(currentIndexState == movableMenu.openedIndexState){
					movableMenu.isMenuOpened = true;

					//change icon of menu handler
					if(ASMovableMenu.openedSettingImage>0) {
						movableMenu.settingImage.setImageResource(ASMovableMenu.openedSettingImage);
					}else{
						//movableMenu.settingImage.setBackgroundColor(Color.WHITE);
					}

					movableMenu.settingText.setVisibility(View.VISIBLE);

					if(movableMenu.listener != null)movableMenu.listener.menuHasBeenOpened();
					
				}else{
					lastDimensionState = currentIndexState;
					movableMenu.isMenuOpened = false;
					
					//movableMenu.menuSection.setVisibility(View.GONE);
					if(ASMovableMenu.closedSettingImage > 0) {
						movableMenu.settingImage.setImageResource(ASMovableMenu.closedSettingImage);
					}else{
						//movableMenu.settingImage.setBackgroundColor(Color.WHITE);
					}


					movableMenu.handleSection.setBackgroundResource(R.drawable.rounded_corner_1);
					movableMenu.menuSection.setBackgroundResource(R.drawable.rounded_corner_1);


					movableMenu.settingText.setVisibility(View.GONE);


					if(!movableMenu.isMovable){
						movableMenu.handleSection.setVisibility(View.GONE);
						movableMenu.menuSection.setVisibility(View.GONE);
					}

					if(movableMenu.listener != null)movableMenu.listener.menuHasBeenClosed();
					
					if(movableMenu.currentSelectedItem != -1){
						ArrayList<Object> handler = movableMenu.itemHandlerList.get(movableMenu.currentSelectedItem);
						movableMenu.itemHandlerList.clear();
						((View.OnClickListener)handler.get(1)).onClick((View)handler.get(0));
						movableMenu.currentSelectedItem = -1;
					}
					
				}
				return true;
			}
		});

		movableMenu.menuSection.setVisibility(View.GONE);
		
		
		
		//create handlesection second
		if(parent instanceof CanvasSection)
			movableMenu.handleSection = ((CanvasSection)parent).addSubSectionWithFrame("handleSection", 0, 0, handleWidth, handleHeight, true, true);
		else if(parent instanceof CanvasLayout)
			movableMenu.handleSection = ((CanvasLayout)parent).createNewSectionWithFrame("handleSection", 0, 0, handleWidth, handleHeight, true, true);
		
		movableMenu.handleSection.getShiftPositionHandler().addRectToDimensionState(0, 0, handleWidth, handleHeight);
		movableMenu.handleSection.getShiftPositionHandler().addRectToDimensionState((100-handleWidth)/2, 0, handleWidth, handleHeight);
		movableMenu.handleSection.getShiftPositionHandler().addRectToDimensionState((100-handleWidth), 0, handleWidth, handleHeight);
		movableMenu.handleSection.getShiftPositionHandler().addRectToDimensionState(0, (100-handleHeight)/2, handleWidth, handleHeight);
		movableMenu.handleSection.getShiftPositionHandler().addRectToDimensionState((100-handleWidth), (100-handleHeight)/2, handleWidth, handleHeight);
		movableMenu.handleSection.getShiftPositionHandler().addRectToDimensionState(0, (100-handleHeight), handleWidth, handleHeight);
		movableMenu.handleSection.getShiftPositionHandler().addRectToDimensionState((100 - handleWidth) / 2, (100 - handleHeight), handleWidth, handleHeight);
		movableMenu.handleSection.getShiftPositionHandler().addRectToDimensionState((100 - handleWidth), (100 - handleHeight), handleWidth, handleHeight);

		//setting text handle section
		int handleSectionWidth = movableMenu.handleSection.getSectionWidth();
		movableMenu.handleSection.setSectionWidth(menuWidth);
		movableMenu.settingText = new ASTextView(movableMenu.context);
		movableMenu.settingText.setText("Pengaturan");
		movableMenu.settingText.setTextColor(Color.BLACK);
		movableMenu.settingText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
		movableMenu.settingText.setGravity(Gravity.CENTER);
		movableMenu.handleSection.addViewWithFrame(movableMenu.settingText, 5, 5, 90, 90);
		movableMenu.handleSection.setSectionWidth(handleSectionWidth);

		movableMenu.settingText.setVisibility(View.GONE);

		//setting image handle section
		movableMenu.settingImage = new ASImageView(movableMenu.context);
		movableMenu.handleSection.addViewWithFrame(movableMenu.settingImage, 5, 5, 90, 90);
		
		movableMenu.handleSection.setASGestureListener(new ASGestureListener() {
			@Override
			public boolean upEventOccurred(float x, float y) {
				if(!movableMenu.isMenuOpened){
					movableMenu.handleSection.getShiftPositionHandler().changeStateToNearestDimension(true);
					movableMenu.menuSection.getShiftPositionHandler().changeStateToNearestDimension(true);
				}

				movableMenu.handleSection.getShiftPositionHandler().upEvent(x, y);
				movableMenu.menuSection.getShiftPositionHandler().upEvent(x, y);
				return true;
			}
			
			@Override
			public boolean swipeEventOccured(int swipeType, float x, float y, float dx,
					float dy) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean swipeTypeFinal(int swipeType) {
				return false;
			}

			@Override
			public boolean movingSpeed(float xSpeed, float ySpeed) {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public boolean longClickEventOccured() {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public boolean isSwipeEnabled() {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public boolean isLongClickEnabled() {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public boolean isDoubleTapEnabled() {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public boolean isClickEnabled() {
				return true;
			}
			
			@Override
			public boolean downEventOccured(float x, float y) {
				movableMenu.handleSection.getShiftPositionHandler().downEvent(x, y);
				movableMenu.menuSection.getShiftPositionHandler().downEvent(x, y);
				return true;
			}
			
			@Override
			public boolean doubleTapEventOccured() {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public boolean deltaMoveOutsideParameter(int swipeType, float x, float y,
					float dx, float dy, float fromDownDX, float fromDownDY) {
				if(!movableMenu.isMenuOpened){
					movableMenu.handleSection.getShiftPositionHandler().shiftViewWithDelta(dx, dy, fromDownDX, fromDownDY);
					movableMenu.menuSection.getShiftPositionHandler().shiftViewWithDelta(dx, dy, fromDownDX, fromDownDY);
				}
				return true;
			}
			
			@Override
			public boolean deltaMoveInsideParameter(int swipeType, float x, float y,
					float dx, float dy, float fromDownDX, float fromDownDY) {
				if(!movableMenu.isMenuOpened){
					movableMenu.handleSection.getShiftPositionHandler().shiftViewWithDelta(dx, dy, fromDownDX, fromDownDY);
					movableMenu.menuSection.getShiftPositionHandler().shiftViewWithDelta(dx, dy, fromDownDX, fromDownDY);
				}
				return true;
			}
			
			@Override
			public boolean clickEventOccured() {
				if(movableMenu.isMenuOpened){
					//close
					movableMenu.closeMenu(true);
				}else{
					//open
					movableMenu.openMenu(true);
				}
				
				return true;
			}
			
			@Override
			public boolean cancelEventOccured(float x, float y) {
				// TODO Auto-generated method stub
				return true;
			}
		});
		movableMenu.itemHandlerList = new HashMap<Integer, ArrayList<Object>>();

		//movableMenu.handleSection.getShiftPositionHandler().changeStateToDimension(lastDimensionState, false);
		//movableMenu.menuSection.getShiftPositionHandler().changeStateToDimension(lastDimensionState, false);
		return movableMenu;
	}


	public void setOpenedIndexState(int openedIndexState){
		this.openedIndexState = openedIndexState;
	}

	public void setOpenedDefaultDimensionState(int x, int y, int dx, int dy){
		if(dx == CanvasSection.SAME_AS_OTHER_SIDE){
			dx = (menuSection.getShiftPositionHandler().getParentHeight() * dy) / menuSection.getShiftPositionHandler().getParentWidth();
		}else if(dy == CanvasSection.SAME_AS_OTHER_SIDE){
			dy = (menuSection.getShiftPositionHandler().getParentWidth() * dx) / menuSection.getShiftPositionHandler().getParentHeight();
		}
		openedDefaultDimension = new Rect(x, y, x+dx, y+dy);
	}
	
	public void ChangeStateToLastDimension(){
		handleSection.getShiftPositionHandler().changeStateToDimension(lastDimensionState, false);
		menuSection.getShiftPositionHandler().changeStateToDimension(lastDimensionState, false);
	}
	
	public void ChangeStateToNextDimension(){
		handleSection.getShiftPositionHandler().changeStateToNextDimension(true);
		menuSection.getShiftPositionHandler().changeStateToNextDimension(true);
	}


	public void setAppearDisappearPoint(Point position){
		if(position == null){
			if(!isMovable) {
				handleSection.getShiftPositionHandler().clearRectInDimensionState(8);
				menuSection.getShiftPositionHandler().clearRectInDimensionState(8);
				lastDimensionState = 0;
				isMovable = true;
			}
		}else {
			if(isMovable) {
				handleSection.getShiftPositionHandler().addRectToDimensionState(position.x, position.y, 1, 1); //index 8
				menuSection.getShiftPositionHandler().addRectToDimensionState(position.x, position.y, 1, 1); //index 8
				lastDimensionState = 8;
				isMovable = false;
			}
		}
	}
	
	public void openMenu(boolean animation){
		if(listener != null)listener.menuWillBeOpened();

		if(!isMovable){
			handleSection.setVisibility(View.VISIBLE);
			menuSection.setVisibility(View.VISIBLE);
		}

		handleSection.setBackgroundResource(R.drawable.rounded_corner_4);
		menuSection.setBackgroundResource(R.drawable.rounded_corner_3);

		blockingLayer.setVisibility(View.VISIBLE);
		menuSection.setVisibility(View.VISIBLE);

		viewList = new ArrayList<View>();
		rectList = new ArrayList<Rect>();
		marginList = new ArrayList<LayoutViewMargin>();
		
		((CanvasActivity)context).onCreateMovableMenu(this);
		
		int height = 0;
		
		if(viewList != null && rectList != null){
			if(rectList.size() <= 0 || (viewList.size() > rectList.size())){
				//make menuSection as linear
				
				menuSection.setSectionWidth(menuHeight); // it's used for telling linearlayout the true width
				menuSection.setSectionAsLinearLayout(0, menuSection.getPercentageEqualHeightPixelWithPercentageFrom(canvasParent, handleHeight), LinearLayout.VERTICAL);

				//menuSection.linearLayout.setBackgroundColor(Color.GREEN);

				menuSection.setSectionWidth(handleHeight);
				for (int i = 0; i < viewList.size(); i++) {
					menuSection.addViewInLinearLayout(viewList.get(i), marginList.get(i));
				}
				height = CommonUtil.getViewDimension(menuSection.linearLayout).height()+10;
			}else{
				menuSection.setSectionWidth(menuHeight); // it's used for telling linearlayout the true width
				for (int i = 0; i < viewList.size(); i++) {
					menuSection.addViewWithFrame(viewList.get(i), rectList.get(i).left, rectList.get(i).top, rectList.get(i).width(), rectList.get(i).height());
				}
				menuSection.setSectionWidth(handleHeight);
				height = CommonUtil.getViewDimension(menuSection.sectionLayout).height()+10;
			}
			height = CommonUtil.getPercentHeightFromPixel(context, height);
			Rect x = new Rect(Math.round((100-menuWidth)/2), Math.round((100-menuHeight)/2), Math.round((100-menuWidth)/2 + menuWidth), Math.round((100-menuHeight)/2+height+handleHeight));
			if(height+handleHeight < menuHeight) {
				menuSection.getShiftPositionHandler().changeStateToCustomDimension(x, openedIndexState, animation);
			}else {
				menuSection.getShiftPositionHandler().changeStateToCustomDimension(openedDefaultDimension, openedIndexState, animation);
			}
		}else{
			menuSection.getShiftPositionHandler().changeStateToCustomDimension(openedDefaultDimension, openedIndexState, animation);
		}
		
		viewList = null;
		rectList = null;
		marginList = null;

		
		//int x = (100-menuWidth)/2+menuWidth-handleWidth;
		int x = Math.round((100-menuWidth)/2);
		int y = Math.round((100-menuHeight)/2);
		handleSection.getShiftPositionHandler().changeStateToCustomDimension(new Rect(x, y, x+menuWidth, y+handleHeight), -1,animation);
	}
	
	public void closeMenu(boolean animation){
		if(listener != null)listener.menuWillBeClosed();

		blockingLayer.setVisibility(View.GONE);
		settingText.setVisibility(View.GONE);


		menuSection.removeAllViews();
		handleSection.getShiftPositionHandler().changeStateToLastDimension(animation);
		menuSection.getShiftPositionHandler().changeStateToLastDimension(animation);
		
	}

	public void addItemMenu(View target){
		addItemMenu(target, null);
	}

	public void addItemMenu(View target, LayoutViewMargin marginRect){
		addItemMenu(target,null,marginRect);
	}

	public void addItemMenu(View target, View.OnClickListener listener , LayoutViewMargin marginRect){
		if(marginRect == null){
			marginRect = new LayoutViewMargin(0,0,0,0);
		}
		addItemMenu(target,listener,null,marginRect);
	}


	
	public void addItemMenu(View target, View.OnClickListener listener, Rect rect, LayoutViewMargin marginRect){
		viewList.add(target);
		if(marginRect == null){
			marginRect = new LayoutViewMargin(0,0,0,0);
		}
		marginList.add(marginRect);
		if(rect != null)
			rectList.add(rect);
		if(listener != null){
			ArrayList<Object> handler = new ArrayList<Object>();
			handler.add(target);
			handler.add(listener);
			itemHandlerList.put(target.hashCode(), handler);
			target.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					if(currentSelectedItem == -1){
						currentSelectedItem = arg0.hashCode();
					    closeMenu(true);
					}
				}
			});
		}
	}
	
	public int getTextHeight(String text){
		return getMenuSection().getPercentageRelativeToHeight((int) CommonUtil.getFontHeight(text));
	}
	
	public interface ASMovableMenuListener{
		public void menuWillBeOpened();
		public void menuHasBeenOpened();
		public void menuWillBeClosed();
		public void menuHasBeenClosed();
	}


	public static class LayoutViewMargin{
		private int leftMargin = 0;
		private int topMargin = 0;
		private int rightMargin = 0;
		private int bottomMargin = 0;

		public LayoutViewMargin(int left, int top, int right, int bottom){
			setLeftMargin(left);
			setTopMargin(top);
			setRightMargin(right);
			setBottomMargin(bottom);
		}

		public int getLeftMargin() {
			return leftMargin;
		}

		public void setLeftMargin(int leftMargin) {
			this.leftMargin = leftMargin;
		}

		public int getRightMargin() {
			return rightMargin;
		}

		public void setRightMargin(int rightMargin) {
			this.rightMargin = rightMargin;
		}

		public int getTopMargin() {
			return topMargin;
		}

		public void setTopMargin(int topMargin) {
			this.topMargin = topMargin;
		}

		public int getBottomMargin() {
			return bottomMargin;
		}

		public void setBottomMargin(int bottomMargin) {
			this.bottomMargin = bottomMargin;
		}

		public void setMargin(int left, int top, int right, int bottom){
			setLeftMargin(left);
			setTopMargin(top);
			setRightMargin(right);
			setBottomMargin(bottom);
		}
	}

}
