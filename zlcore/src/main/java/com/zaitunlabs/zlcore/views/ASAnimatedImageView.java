package com.zaitunlabs.zlcore.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.TextView;

import com.zaitunlabs.zlcore.utils.CommonUtil;
import com.zaitunlabs.zlcore.utils.DebugUtil;

/**
 * sekarang masih gak support untuk di add ke CanvasSection dengan scroll aktif, (akan di riset)
 * @author ahmad
 *
 */
public class ASAnimatedImageView extends ASImageView {
	boolean isPressed = false;
	OnClickListener listener = null;
	Typeface currentTypeFace = Typeface.DEFAULT;
	int currentTextSize = 30;
	String currentActiveText = null;
	int leftImageID = -1;
	int rightImageID = -1;
	int currentColor = Color.BLACK;
	Handler animationHandler = null;
	int rotationAngle = 10;
	Paint p = null;
	Camera mCamera = null;
	
	
	Canvas offScreenCanvas = null;
	Bitmap offScreenImage = null;

	public ASAnimatedImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public ASAnimatedImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ASAnimatedImageView(Context context) {
		super(context);
		init();
	}
	
	public void setRightImage(int resourceId){
		rightImageID = resourceId;
	}
	
	public void setLeftImage(int resourceId){
		leftImageID = resourceId;
	}

	public void setTypeFace(Typeface newTypeFace) {
		currentTypeFace = newTypeFace;
	}

	public void setTextSize(int newTextSize) {
		currentTextSize = newTextSize;
	}

	public void setText(String newText) {
		currentActiveText = newText;
	}

	public void setTextColor(int color) {
		currentColor = color;
	}

	@Override
	public void setOnClickListener(OnClickListener l) {
		listener = l;
	}

	private void init() {
		p = new Paint();
		p.setAntiAlias(true);
	}

	private Matrix transformImageBitmap(Bitmap input, int rotationAngle) {
		if(mCamera == null)
			mCamera = new Camera();
		mCamera.save();
		final Matrix imageMatrix = new Matrix();
		final int imageHeight = input.getHeight();
		final int imageWidth = input.getWidth();
		final int rotation = Math.abs(rotationAngle);

		/*
		 * mCamera.translate(0.0f, 0.0f, 100.0f);
		 * 
		 * // As the angle of the view gets less, zoom in if (rotation < 60) {
		 * float zoomAmount = (float) (-120 + (rotation * 1.5));
		 * mCamera.translate(0.0f, 0.0f, zoomAmount); }
		 */

		mCamera.translate(0.0f, (rotationAngle * 2), 0.0f);
		mCamera.rotateY(rotationAngle);
		
		//mCamera.translate(-imageWidth, 0.0f, 0.0f);
		//mCamera.rotateY(-rotationAngle);
		
		
		mCamera.getMatrix(imageMatrix);
		// imageMatrix.preTranslate(-(imageWidth / 2), -(imageHeight / 2));
		// imageMatrix.postTranslate((imageWidth / 2), (imageHeight / 2));
		mCamera.restore();
		return imageMatrix;
	}

	private Bitmap drawMatrix(Bitmap input, int rotationAngle) {
		Matrix matrix = transformImageBitmap(input, rotationAngle);
		Bitmap resizedBitmap = Bitmap.createBitmap(input, 0, 0, input
				.getWidth(), input.getHeight(), matrix, true);
		return resizedBitmap;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (offScreenImage == null) {
			offScreenImage = Bitmap.createBitmap(this.getMeasuredWidth(), this.getMeasuredHeight(), Config.ARGB_8888);
			Canvas offScreenCanvas = new Canvas(offScreenImage);
			super.onDraw(offScreenCanvas);

			if(leftImageID != -1){
				ImageView iv = new ImageView(this.getContext());
				iv.setImageResource(leftImageID);
				iv.setScaleType(ScaleType.FIT_XY);
				iv.setAdjustViewBounds(true);
				Bitmap bmp = CommonUtil.getBitmapFromView(iv, offScreenCanvas.getWidth() * 1 / 7, offScreenCanvas.getHeight() * 1/2);
				if(bmp != null)
					offScreenCanvas.drawBitmap(bmp, 10, offScreenCanvas.getHeight()/4, null);
			}
			
			if (currentActiveText != null) {
				TextView tv = new TextView(this.getContext());
				tv.setWidth(offScreenCanvas.getWidth() * 3 / 5);
				tv.setHeight(offScreenCanvas.getHeight());
				tv.setTextSize(currentTextSize);
				tv.setGravity(Gravity.CENTER);
				tv.setTypeface(currentTypeFace);
				tv.setTextColor(currentColor);
				tv.setText(currentActiveText);
				Bitmap bmp = CommonUtil.getBitmapFromView(tv, offScreenCanvas.getWidth() * 3 / 5, offScreenCanvas.getHeight());
				if(bmp != null)
					offScreenCanvas.drawBitmap(bmp, offScreenCanvas.getWidth() * 1 / 5, 0, null);
			}
			if(rightImageID != -1){
				ImageView iv = new ImageView(this.getContext());
				iv.setImageResource(rightImageID);
				iv.setScaleType(ScaleType.FIT_XY);
				iv.setAdjustViewBounds(true);
				Bitmap bmp = CommonUtil.getBitmapFromView(iv, offScreenCanvas.getWidth() * 1 / 7, offScreenCanvas.getHeight()*1/2);
				if(bmp != null)
					offScreenCanvas.drawBitmap(bmp, offScreenCanvas.getWidth() * 6 / 7,offScreenCanvas.getHeight()/4, null);
			}
		}
		if (isPressed) {
			Bitmap newImage = drawMatrix(offScreenImage, rotationAngle);
			canvas.drawBitmap(newImage, 0, 0, p);

		} else {
			canvas.drawBitmap(offScreenImage, 0, 0, p);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			DebugUtil.logE("onTouchEvent", "ASAnimatedImageView MotionEvent.ACTION_DOWN");
			isPressed = true;
			postInvalidate();
			break;
		case MotionEvent.ACTION_OUTSIDE:
			DebugUtil.logE("onTouchEvent", "ASAnimatedImageView MotionEvent.ACTION_OUTSIDE");
			if (isPressed) {
				isPressed = false;
				postInvalidate();
			}
			break;
		case MotionEvent.ACTION_CANCEL:
			DebugUtil.logE("onTouchEvent", "ASAnimatedImageView MotionEvent.ACTION_CANCEL");
			if (isPressed) {
				isPressed = false;
				postInvalidate();
			}
			break;
		case MotionEvent.ACTION_MOVE:
			DebugUtil.logE("onTouchEvent", "ASAnimatedImageView MotionEvent.ACTION_MOVE");
			if ((event.getX() > this.getWidth() || event.getX() < 0)
					|| (event.getY() > this.getHeight() || event.getY() < 0)) {
				if (isPressed) {
					isPressed = false;
					postInvalidate();
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			DebugUtil.logE("onTouchEvent", "ASAnimatedImageView MotionEvent.ACTION_UP");
			if (isPressed) {
				if (listener != null)
					listener.onClick(this);
			}
			isPressed = false;
			postInvalidate();
			break;
		}
		return true;
	}
}
