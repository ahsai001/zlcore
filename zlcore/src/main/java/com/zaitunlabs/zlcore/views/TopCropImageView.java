package com.zaitunlabs.zlcore.views;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class TopCropImageView extends ASImageView {

	public TopCropImageView(Context context) {
		super(context);
		setScaleType(ImageView.ScaleType.MATRIX);
	}
	
	public TopCropImageView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        setScaleType(ImageView.ScaleType.MATRIX);
    }

    public TopCropImageView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        setScaleType(ImageView.ScaleType.MATRIX);
    }

    
    @Override
    protected boolean setFrame(int l, int t, int r, int b)
    {
        Matrix matrix = getImageMatrix();
        Drawable drawAble = getDrawable();
		if(drawAble != null){
	        float scaleFactor = getWidth()/(float)drawAble.getIntrinsicWidth();
	        matrix.setScale(scaleFactor, scaleFactor, 0, 0);
	        setImageMatrix(matrix);
        }
        return super.setFrame(l, t, r, b);
    }
	
    /*
	@Override
	protected boolean setFrame(int l, int t, int r, int b) {
		final Matrix matrix = getImageMatrix();
		float scale;
		final int viewWidth = getWidth() - getPaddingLeft() - getPaddingRight();
		final int viewHeight = getHeight() - getPaddingTop() - getPaddingBottom();
		Drawable drawAble = getDrawable();
		if(drawAble != null){
			final int drawableWidth = drawAble.getIntrinsicWidth();
			final int drawableHeight = drawAble.getIntrinsicHeight();
			if (drawableWidth * viewHeight > drawableHeight * viewWidth) {
				scale = (float) viewHeight / (float) drawableHeight;
			} else {
				scale = (float) viewWidth / (float) drawableWidth;
			}
			matrix.setScale(scale, scale);
			setImageMatrix(matrix);
		}
		return super.setFrame(l, t, r, b);
	}
	*/
} 