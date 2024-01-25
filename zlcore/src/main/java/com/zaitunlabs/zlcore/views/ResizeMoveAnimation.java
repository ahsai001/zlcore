package com.zaitunlabs.zlcore.views;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.Transformation;
import android.widget.RelativeLayout.LayoutParams;

import com.zaitunlabs.zlcore.utils.DebugUtil;


public class ResizeMoveAnimation extends Animation {
    private View mView;
    
    private float mFromX;
    private float mToX;
    
    private float mFromY;
    private float mToY;
    
    private float mToHeight;
    private float mFromHeight;

    private float mToWidth;
    private float mFromWidth;
    
    private float currentWidth;
    
   
	private float currentHeight;
	
	private ASAnimListener animListener = null;
    
	private LayoutParams param;
	
	private int x = 0;
    
    public float getCurrentWidth() {
		return currentWidth;
	}
	public float getCurrentHeight() {
		return currentHeight;
	}

	public ASAnimListener getAnimListener() {
		return animListener;
	}
	public void setAnimListener(ASAnimListener animListener) {
		this.animListener = animListener;
	}
	
    public ResizeMoveAnimation(View v, float toX, float toY, float toWidth, float toHeight) {
        //after
    	mToX = toX;
        mToY = toY;
    	mToHeight = toHeight;
        mToWidth = toWidth;
        
        //before
        param = (LayoutParams) v.getLayoutParams();
        mFromX = param.leftMargin;
        mFromY = param.topMargin;
        mFromHeight = param.height;
        mFromWidth = param.width;
        
        DebugUtil.logW("JEJAK", ">>>>>>>>>>> "+((CanvasSection)v).getSectionName()+" ResizeMoveAnimation from w="+mFromWidth+" h="+mFromHeight);
        DebugUtil.logW("JEJAK", ">>>>>>>>>>> "+((CanvasSection)v).getSectionName()+" ResizeMoveAnimation to w="+mToWidth+" h="+mToHeight);

        currentWidth = param.width;
        currentHeight = param.height;
        
        //set view
        mView = v;
        mView.clearAnimation();
        mView.setAnimation(null);
        mView.setAnimation(this);
        //param = (LayoutParams)mView.getLayoutParams();
        
        //set parameter basic animation
        setDuration(400);
        setInterpolator(sMenuInterpolator);
    }
    
    
    private static final Interpolator sMenuInterpolator = new Interpolator() {
        @Override
        public float getInterpolation(float t) {
            t -= 1.0f;
            return (float) Math.pow(t, 5) + 1.0f;
        }
    };
    
    @Override
    public void startNow() {
    	super.startNow();
    	mView.getRootView().invalidate();
    }
    @Override
    public void start() {
    	super.start();
    	mView.getRootView().invalidate();
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        float height = (mToHeight - mFromHeight) * interpolatedTime + mFromHeight;
        float width = (mToWidth - mFromWidth) * interpolatedTime + mFromWidth;
        
        float left = (mToX - mFromX) * interpolatedTime + mFromX;
        float top = (mToY - mFromY) * interpolatedTime + mFromY;
        
        DebugUtil.logD("COUNTER", "ke-" + x++);
        
        param.height = (int) height;
        param.width = (int) width;
        param.leftMargin = (int) left;
        param.topMargin = (int) top;
        mView.setLayoutParams(param);
        
        DebugUtil.logW("JEJAK", ">>>>>>>>>>> "+((CanvasSection)mView).getSectionName()+" ResizeMoveAnimation current x="+param.leftMargin+" y="+param.topMargin);

        
        currentWidth = width;
        currentHeight = height;
        
        if(animListener != null){
        	/*
        	new Handler().post(new Runnable() {
				@Override
				public void run() {
		        	animListener.animationRepeat(currentWidth, currentHeight);
				}
			});
			*/
            DebugUtil.logW("JEJAK", "invoke listener");
			animListener.animationRepeat(currentWidth, currentHeight);
        }
    }
    
    public interface ASAnimListener{
    	public void animationRepeat(float width, float height);
    }
    
}