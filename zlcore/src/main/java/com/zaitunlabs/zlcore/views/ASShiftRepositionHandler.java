package com.zaitunlabs.zlcore.views;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout.LayoutParams;


import com.zaitunlabs.zlcore.utils.DebugUtil;

import java.util.ArrayList;

public abstract class ASShiftRepositionHandler {
	private Context context = null;
	private View view = null;
	private ArrayList<Rect> dimensionList = null;
	private ArrayList<Boolean> dimensionVisiblityInCircleList = null;
	private int dimensionState = 0;//pointer yg menunjuk ke dimensionList, default awal index 0
	
	private final int SDK_INT_LIMIT_USE_TRANSLATE_ANIMATION = 40;
	
	private DimensionStateListener dimensionStateListener = null;

	private ArrayList<DimensionStateListener> dimensionStateListeners = null;

	private int maxLocationX = 0, minLocationX = 0, maxLocationY = 0, minLocationY = 0;
	private boolean limitLocationSet = false;
	
	public DimensionStateListener getDimensionStateListener() {
		return dimensionStateListener;
	}
	public void setDimensionStateListener(DimensionStateListener dimensionStateListener) {
		this.dimensionStateListener = dimensionStateListener;
	}


	public void addDimensionStateListener(DimensionStateListener dimensionStateListener){
		if(this.dimensionStateListeners == null)this.dimensionStateListeners = new ArrayList<>();
		this.dimensionStateListeners.add(dimensionStateListener);
	}

	public void removeDimensionStateListener(DimensionStateListener dimensionStateListener){
		this.dimensionStateListeners.remove(dimensionStateListener);
	}
	
	public int getDimensionState() {
		return dimensionState;
	}
	
	public ASShiftRepositionHandler(Context context, View view) {
		this.context = context;
		this.view = view;
	}

	public Rect clearRectInDimensionState(int dimensionStateIndex){
		if(dimensionList!=null && dimensionList.size() > dimensionStateIndex && dimensionStateIndex >=0 ){
			return dimensionList.remove(dimensionStateIndex);
		}
		return null;
	}
	
	public Rect addRectToDimensionState(int x, int y, int dx, int dy){
		return addRectToDimensionState(x,y,dx,dy,true);
	}
	
	public Rect addRectToDimensionState(int x, int y, int dx, int dy, boolean isVisibleInCircle){
		//width = (parentHeight * height) / parentWidth;

		if(dx == CanvasSection.SAME_AS_OTHER_SIDE){
			dx = (getParentHeight() * dy) / getParentWidth();
		}else if(dy == CanvasSection.SAME_AS_OTHER_SIDE){
			dy = (getParentWidth() * dx) / getParentHeight();
		}
		
		Rect p = new Rect(x, y, x+dx, y+dy);
		if(dimensionList == null)dimensionList = new ArrayList<Rect>();
		dimensionList.add(p);
		if(dimensionVisiblityInCircleList == null)dimensionVisiblityInCircleList = new ArrayList<Boolean>();
		dimensionVisiblityInCircleList.add(isVisibleInCircle);
		return p;
	}


	public boolean changeVisibilityForDimensionState(int dimensionStateIndex, boolean isvisible){
		if(dimensionVisiblityInCircleList != null && dimensionVisiblityInCircleList.size() > dimensionStateIndex && dimensionStateIndex >= 0){
			dimensionVisiblityInCircleList.set(dimensionStateIndex, isvisible);
			return true;
		}
		return false;
	}

	public Rect changeStateToNextDimension(boolean animation){
		Rect p = null;
		if(dimensionList != null && dimensionList.size() > 0){	
			boolean searchNextState = true;
			do {
				dimensionState++;
				if(dimensionState >= dimensionList.size())dimensionState = 0;
				if(dimensionVisiblityInCircleList.get(dimensionState) == true){
					searchNextState = false;
				}
			} while (searchNextState);
			p = dimensionList.get(dimensionState);
			resizeMoveViewWithFrame(p.left, p.top, p.width(), p.height(), animation, dimensionState);
		}
		return p;
	}

	public int changeStateToNearestDimension(boolean animation){
		Rect p = null;
		if(dimensionList != null && dimensionList.size() > 0){
			int lastNearestIndex = -1;
			double lastNearestDistance = -1;
			for(int i=0;i<dimensionList.size();i++){
				if(dimensionVisiblityInCircleList.get(i) == false)
					continue;
				p = dimensionList.get(i);
				double distance = Math.sqrt(Math.pow(p.left - getXNow(), 2) + Math.pow(p.top - getYNow(), 2));
				if(distance <= lastNearestDistance || lastNearestIndex == -1){
					lastNearestIndex = i;
					lastNearestDistance = distance;
				}
			}
			this.dimensionState = lastNearestIndex;
			p = dimensionList.get(this.dimensionState);
			resizeMoveViewWithFrame(p.left, p.top, p.width(), p.height(), animation, this.dimensionState);
		}
		return this.dimensionState;
	}

	public int changeStateToNearestDimensionFromPoint(boolean animation, int x, int y){
		Rect p = null;
		if(dimensionList != null && dimensionList.size() > 0){
			int lastNearestIndex = -1;
			double lastNearestDistance = -1;
			for(int i=0;i<dimensionList.size();i++){
				if(dimensionVisiblityInCircleList.get(i) == false)
					continue;
				p = dimensionList.get(i);
				double distance = Math.sqrt(Math.pow(p.left - x, 2) + Math.pow(p.top - y, 2));
				if(distance <= lastNearestDistance || lastNearestIndex == -1){
					lastNearestIndex = i;
					lastNearestDistance = distance;
				}
			}
			this.dimensionState = lastNearestIndex;
			p = dimensionList.get(this.dimensionState);
			resizeMoveViewWithFrame(p.left, p.top, p.width(), p.height(), animation, this.dimensionState);
		}
		return this.dimensionState;
	}

	public Rect changeStateToDimension(int dimensionStateIndex, boolean animation){
		Rect p = null;
		if(dimensionList != null && dimensionList.size() > dimensionStateIndex && dimensionStateIndex >= 0){	
			if(dimensionVisiblityInCircleList.get(dimensionStateIndex) == true){
				this.dimensionState = dimensionStateIndex;
			}
			p = dimensionList.get(dimensionStateIndex);
			resizeMoveViewWithFrame(p.left, p.top, p.width(), p.height(), animation, dimensionStateIndex);
		}
		return p;
	}
	
	public Rect changeStateToCustomDimension(Rect customDimension, int dimensionStateIndex,boolean animation){
		Rect p = new Rect(customDimension);
		int dx = p.width(), dy = p.height();
		if(p.right == CanvasSection.SAME_AS_OTHER_SIDE){
			dy = p.height();
			dx = (getParentHeight() * dy) / getParentWidth();
		}else if(p.bottom == CanvasSection.SAME_AS_OTHER_SIDE){
			dx = p.width();
			dy = (getParentWidth() * dx) / getParentHeight();
		}
		
		resizeMoveViewWithFrame(p.left, p.top, dx, dy, animation, dimensionStateIndex);
		return p;
	}
	
	public Rect changeStateToLastDimension(boolean animation){
		Rect p = dimensionList.get(this.dimensionState);
		resizeMoveViewWithFrame(p.left, p.top, p.width(), p.height(), animation, this.dimensionState);
		return p;
	}
	
	public void setMinMaxLocationXY(int minLocationX, int maxLocationX, int minLocationY, int maxLocationY){
		if((maxLocationX != 0 || minLocationX != 0 || maxLocationY != 0 || minLocationY != 0)){
			limitLocationSet = true;
		}else{
			limitLocationSet = false;
		}
		this.minLocationX = minLocationX;
		this.maxLocationX = maxLocationX;
		this.minLocationY = minLocationY;
		this.maxLocationY = maxLocationY;
	}

	protected boolean isPositionPermitted(int X, int Y){
		return (!limitLocationSet || (limitLocationSet && (X <= maxLocationX && X >= minLocationX && Y <= maxLocationY && Y >= minLocationY)));
	}
	
	public void shiftViewWithDelta(float dx, float dy, float fromDownDX, float fromDownDY) {
		LayoutParams param = (LayoutParams)view.getLayoutParams();


		//DebugUtil.logW("JEJAK", ">>>>>>>>>>>cc 1 "+param.leftMargin+" "+param.topMargin);

		int x, y;
		if(Build.VERSION.SDK_INT >= SDK_INT_LIMIT_USE_TRANSLATE_ANIMATION){
			x = param.leftMargin + (int)fromDownDX;
			y = param.topMargin + (int)fromDownDY;
		}else{
			x = param.leftMargin + (int)dx;
			y = param.topMargin + (int)dy;
		}


		Rect commonTypeValue = convertRectFromParamToCommonType(new Rect(x, y, x+param.width, y+param.height));



		if(isPositionPermitted(commonTypeValue.left, commonTypeValue.top)){
			//update section coordinate
			updatePosition(commonTypeValue.left, commonTypeValue.top);

			DebugUtil.logD("JEJAK", ">>>>>>>>>>> "+((CanvasSection)view).getSectionName()+" new xy "+x+" "+y);
			DebugUtil.logD("JEJAK", ">>>>>>>>>>> "+((CanvasSection)view).getSectionName()+" new common xy "+commonTypeValue.left+" "+commonTypeValue.top);

			if(Build.VERSION.SDK_INT >= SDK_INT_LIMIT_USE_TRANSLATE_ANIMATION){
				view.setTranslationX(fromDownDX);
				view.setTranslationY(fromDownDY);
				DebugUtil.logD("JEJAK", ">>>>>>>>>>> "+((CanvasSection)view).getSectionName()+" setTranslation");
			}else{
				param.leftMargin = x;
				param.topMargin = y;
				view.setLayoutParams(param);
				DebugUtil.logD("JEJAK", ">>>>>>>>>>> "+((CanvasSection)view).getSectionName()+" setLayoutParams");
			}
			postStepUpdate(view, (int)param.width, (int)param.height, true);
		}
	}

	public void upEvent(float x, float y){
		//view.setDrawingCacheEnabled(false);
		if(Build.VERSION.SDK_INT >= SDK_INT_LIMIT_USE_TRANSLATE_ANIMATION){
			//RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams)view.getLayoutParams();

			//param.leftMargin = (int) x;
			//param.topMargin = (int) y;
			//view.setLayoutParams(param);
		}
	}

	public void downEvent(float x, float y){
		//view.setDrawingCacheEnabled(true);
	}

	protected void resizeMoveViewWithFrame(final int left, final int top, final int width,
										   final int height, boolean animation, final int destinationIndex) {
		final Rect paramTypeValue;
		Rect oldParamTypeValue = null;
		if(Build.VERSION.SDK_INT >= SDK_INT_LIMIT_USE_TRANSLATE_ANIMATION){
			int oldX = ((CanvasSection)view).getSectionX();
			int oldY = ((CanvasSection)view).getSectionY();
			int oldWidth = ((CanvasSection)view).getSectionWidth();
			int oldHeight = ((CanvasSection)view).getSectionHeight();

			oldParamTypeValue = convertRectFromCommonToParamType(new Rect(oldX, oldY, oldX+oldWidth, oldY+oldHeight));
			DebugUtil.logD("JEJAK", ">>>>>>>>>>> "+((CanvasSection)view).getSectionName()+" get oldParamTypeValue");
		}


		paramTypeValue = convertRectFromCommonToParamType(new Rect(left, top, left+width, top+height));


		DebugUtil.logD("JEJAK", ">>>>>>>>>>>cc "+((CanvasSection)view).getSectionName()+" resizemove common xy "+paramTypeValue.left+" "+paramTypeValue.top);
		//DebugUtil.logW("JEJAK", ">>>>>>>>>>>cc 3 old xy "+oldParamTypeValue.left+" "+oldParamTypeValue.top);

		DebugUtil.logD("JEJAK", ">>>>>>>>>>>cc "+((CanvasSection)view).getSectionName()+" resizemove update "+left+" "+top);

		//update section coordinate
		updatePosition(left, top);
		updateDimension(width, height);

		//view.setDrawingCacheEnabled(true);

		if(animation){
			Animation anim = null;
			if(Build.VERSION.SDK_INT >= SDK_INT_LIMIT_USE_TRANSLATE_ANIMATION){
				LayoutParams params = (LayoutParams) view.getLayoutParams();
				//DebugUtil.logW("JEJAK", ">>>>>>>>>>> 2 "+params.leftMargin+" "+params.topMargin);
				DebugUtil.logD("JEJAK", ">>>>>>>>>>> "+((CanvasSection)view).getSectionName()+" TranslateAnimation");
				anim = new TranslateAnimation(oldParamTypeValue.left - params.leftMargin, paramTypeValue.left - params.leftMargin, oldParamTypeValue.top - params.topMargin, paramTypeValue.top - params.topMargin);
			}else{
				DebugUtil.logD("JEJAK", ">>>>>>>>>>> "+((CanvasSection)view).getSectionName()+" ResizeMoveAnimation");
				anim = new ResizeMoveAnimation(view, paramTypeValue.left, paramTypeValue.top, paramTypeValue.width(), paramTypeValue.height());
			}

			anim.setAnimationListener(new AnimationListener() {
				@Override
				public void onAnimationStart(Animation animation) {
				}

				@Override
				public void onAnimationRepeat(Animation animation) {
				}

				@Override
				public void onAnimationEnd(Animation animation) {
					//view.setDrawingCacheEnabled(false);
					//if(Build.VERSION.SDK_INT >= SDK_INT_LIMIT_USE_TRANSLATE_ANIMATION){
						LayoutParams param = (LayoutParams)view.getLayoutParams();

						param.height = (int) paramTypeValue.height();
				        param.width = (int) paramTypeValue.width();
				        param.leftMargin = (int) paramTypeValue.left;
				        param.topMargin = (int) paramTypeValue.top;
				        view.setLayoutParams(param);
				        DebugUtil.logD("JEJAK", ">>>>>>>>>>> "+((CanvasSection)view).getSectionName()+" onAnimationEnd");
				        postStepUpdate(view, (int)param.width, (int)param.height, false);
					//}
					if(dimensionStateListener != null){
						dimensionStateListener.indexForCurrentDimensionState(destinationIndex);
						dimensionStateListener.rectForCurrentDimensionState(new Rect(left, top, left+width, top+height));
					}

					if(dimensionStateListeners != null){
						for (int i=0; i<dimensionStateListeners.size(); i++){
							dimensionStateListeners.get(i).indexForCurrentDimensionState(destinationIndex);
							dimensionStateListeners.get(i).rectForCurrentDimensionState(new Rect(left, top, left + width, top + height));
						}
					}
				}
			});

			if(Build.VERSION.SDK_INT >= SDK_INT_LIMIT_USE_TRANSLATE_ANIMATION){
				anim.setDuration(400);
			    view.startAnimation(anim);
			    DebugUtil.logD("JEJAK", ">>>>>>>>>>> "+((CanvasSection)view).getSectionName()+" TranslateAnimation 2");
			}else{
				((ResizeMoveAnimation)anim).setAnimListener(new ResizeMoveAnimation.ASAnimListener() {
					@Override
					public void animationRepeat(float width, float height) {
						DebugUtil.logW("JEJAK", ">>>>>>>>>>>");
						postStepUpdate(view, (int) width, (int) height, true);
					}
				});
				anim.startNow();
				DebugUtil.logD("JEJAK", ">>>>>>>>>>> "+((CanvasSection)view).getSectionName()+" ResizeMoveAnimation 2");
			}
		}else{
			//view.setDrawingCacheEnabled(false);
			LayoutParams param = (LayoutParams) view.getLayoutParams();
			param.height = (int) paramTypeValue.height();
			param.width = (int) paramTypeValue.width();
			param.leftMargin = (int) paramTypeValue.left;
			param.topMargin = (int) paramTypeValue.top;
			view.setLayoutParams(param);
			postStepUpdate(view, (int)param.width, (int)param.height, false);
			if(dimensionStateListener != null){
				dimensionStateListener.indexForCurrentDimensionState(destinationIndex);
				dimensionStateListener.rectForCurrentDimensionState(new Rect(left, top, left+width, top+height));
			}

			if(dimensionStateListeners != null){
				for (int i=0; i<dimensionStateListeners.size(); i++){
					dimensionStateListeners.get(i).indexForCurrentDimensionState(destinationIndex);
					dimensionStateListeners.get(i).rectForCurrentDimensionState(new Rect(left, top, left+width, top+height));
				}
			}
		}
	}
	
	/*
	 * input/common type format is same like input in addRect example : % in canvas section or pixel or any other format
	 * pixel/layoutparam type format like in deltamove
	 * */
	
	//must be implemented
	protected abstract int getXNow(); //common type
	protected abstract int getYNow(); //common type
	protected abstract int getParentWidth(); //param type
	protected abstract int getParentHeight(); //param type
	protected abstract void updateDimension(int width, int height); //common type
	protected abstract void updatePosition(int x, int y); //common type
	protected abstract void postStepUpdate(View view, int viewWidth, int viewHeight, boolean isInRepeatState); //layoutparam type
	protected abstract Rect convertRectFromCommonToParamType(Rect commonTypeValue);
	protected abstract Rect convertRectFromParamToCommonType(Rect paramTypeValue);
	
}
