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
import android.view.View;
import android.widget.TextView;

/**
 * sekarang masih gak support untuk di add ke CanvasSection dengan scroll aktif, (akan di riset)
 * @author ahmad
 *
 */
public class AnimatedImageView extends ASImageView {
	boolean isPressed = false;
	View.OnClickListener listener = null;
	Typeface currentTypeFace = Typeface.DEFAULT;
	int currentTextSize = 30;
	String currentActiveText = null;
	int currentColor = Color.BLACK;
	Handler animationHandler = null;
	int rotationAngle = 10;
	Paint p = null;
	Bitmap offScreenImage = null;
	Camera mCamera = null;

	public AnimatedImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public AnimatedImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public AnimatedImageView(Context context) {
		super(context);
		init();
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
	public void setOnClickListener(View.OnClickListener l) {
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
		//if (offScreenImage == null) {
			Bitmap offScreenImage = Bitmap.createBitmap(this.getMeasuredWidth(), this
					.getMeasuredHeight(), Config.ARGB_8888);
			Canvas offScreenCanvas = new Canvas(offScreenImage);
			super.onDraw(offScreenCanvas);

			if (currentActiveText != null) {
				TextView tv = new TextView(this.getContext());
				tv.setWidth(offScreenCanvas.getWidth() * 3 / 5);
				tv.setHeight(offScreenCanvas.getHeight());
				tv.setTextSize(currentTextSize);
				tv.setGravity(Gravity.CENTER);
				tv.setTypeface(currentTypeFace);
				tv.setTextColor(currentColor);
				tv.setDrawingCacheEnabled(true);
				tv.setText(currentActiveText);
				tv.measure(View.MeasureSpec.makeMeasureSpec(offScreenCanvas
						.getWidth() * 3 / 5, View.MeasureSpec.EXACTLY), View.MeasureSpec
						.makeMeasureSpec(offScreenCanvas.getHeight(),
								View.MeasureSpec.EXACTLY));
				tv.layout(0, 0, tv.getMeasuredWidth(), tv.getMeasuredHeight());
				offScreenCanvas.drawBitmap(tv.getDrawingCache(),
						offScreenCanvas.getWidth() * 1 / 5, 0, null);
				tv.setDrawingCacheEnabled(false);
			}
		//}
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
			isPressed = true;
			postInvalidate();
			break;
		case MotionEvent.ACTION_MOVE:
			if ((event.getX() > this.getWidth() || event.getX() < 0)
					|| (event.getY() > this.getHeight() || event.getY() < 0)) {
				if (isPressed) {
					isPressed = false;
					postInvalidate();
				}
			}
			break;
		case MotionEvent.ACTION_UP:
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
