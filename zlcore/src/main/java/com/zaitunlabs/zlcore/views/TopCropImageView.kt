package com.zaitunlabs.zlcore.views

import android.content.Context
import android.graphics.Matrix
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.ImageView

class TopCropImageView : ASImageView {

    constructor(context: Context) : super(context) {
        scaleType = ImageView.ScaleType.MATRIX
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        scaleType = ImageView.ScaleType.MATRIX
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        scaleType = ImageView.ScaleType.MATRIX
    }


    override fun setFrame(l: Int, t: Int, r: Int, b: Int): Boolean {
        val matrix = imageMatrix
        val drawAble = drawable
        if (drawAble != null) {
            val scaleFactor = width / drawAble.intrinsicWidth.toFloat()
            matrix.setScale(scaleFactor, scaleFactor, 0f, 0f)
            imageMatrix = matrix
        }
        return super.setFrame(l, t, r, b)
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