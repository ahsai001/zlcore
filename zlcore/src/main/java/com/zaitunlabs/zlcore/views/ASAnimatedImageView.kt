package com.zaitunlabs.zlcore.views

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.Config
import android.graphics.Camera
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Handler
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import android.widget.ImageView
import android.widget.TextView

import com.zaitunlabs.zlcore.utils.CommonUtils
import com.zaitunlabs.zlcore.utils.DebugUtils

/**
 * sekarang masih gak support untuk di add ke CanvasSection dengan scroll aktif, (akan di riset)
 * @author ahmad
 */
class ASAnimatedImageView : ASImageView {
    internal var isPressed = false
    internal var listener: View.OnClickListener? = null
    internal var currentTypeFace = Typeface.DEFAULT
    internal var currentTextSize = 30
    internal var currentActiveText: String? = null
    internal var leftImageID = -1
    internal var rightImageID = -1
    internal var currentColor = Color.BLACK
    internal var animationHandler: Handler? = null
    internal var rotationAngle = 10
    internal var p: Paint? = null
    internal var mCamera: Camera? = null


    internal var offScreenCanvas: Canvas? = null
    internal var offScreenImage: Bitmap? = null

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context) : super(context) {
        init()
    }

    fun setRightImage(resourceId: Int) {
        rightImageID = resourceId
    }

    fun setLeftImage(resourceId: Int) {
        leftImageID = resourceId
    }

    fun setTypeFace(newTypeFace: Typeface) {
        currentTypeFace = newTypeFace
    }

    fun setTextSize(newTextSize: Int) {
        currentTextSize = newTextSize
    }

    fun setText(newText: String) {
        currentActiveText = newText
    }

    fun setTextColor(color: Int) {
        currentColor = color
    }

    override fun setOnClickListener(l: View.OnClickListener?) {
        listener = l
    }

    private fun init() {
        p = Paint()
        p!!.isAntiAlias = true
    }

    private fun transformImageBitmap(input: Bitmap, rotationAngle: Int): Matrix {
        if (mCamera == null)
            mCamera = Camera()
        mCamera!!.save()
        val imageMatrix = Matrix()
        val imageHeight = input.height
        val imageWidth = input.width
        val rotation = Math.abs(rotationAngle)

        /*
		 * mCamera.translate(0.0f, 0.0f, 100.0f);
		 *
		 * // As the angle of the view gets less, zoom in if (rotation < 60) {
		 * float zoomAmount = (float) (-120 + (rotation * 1.5));
		 * mCamera.translate(0.0f, 0.0f, zoomAmount); }
		 */

        mCamera!!.translate(0.0f, (rotationAngle * 2).toFloat(), 0.0f)
        mCamera!!.rotateY(rotationAngle.toFloat())

        //mCamera.translate(-imageWidth, 0.0f, 0.0f);
        //mCamera.rotateY(-rotationAngle);


        mCamera!!.getMatrix(imageMatrix)
        // imageMatrix.preTranslate(-(imageWidth / 2), -(imageHeight / 2));
        // imageMatrix.postTranslate((imageWidth / 2), (imageHeight / 2));
        mCamera!!.restore()
        return imageMatrix
    }

    private fun drawMatrix(input: Bitmap, rotationAngle: Int): Bitmap {
        val matrix = transformImageBitmap(input, rotationAngle)
        return Bitmap.createBitmap(input, 0, 0, input
                .width, input.height, matrix, true)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onDraw(canvas: Canvas) {
        if (offScreenImage == null) {
            offScreenImage = Bitmap.createBitmap(this.measuredWidth, this.measuredHeight, Config.ARGB_8888)
            val offScreenCanvas = Canvas(offScreenImage!!)
            super.onDraw(offScreenCanvas)

            if (leftImageID != -1) {
                val iv = ImageView(this.context)
                iv.setImageResource(leftImageID)
                iv.scaleType = ImageView.ScaleType.FIT_XY
                iv.adjustViewBounds = true
                val bmp = CommonUtils.getBitmapFromView(iv, offScreenCanvas.width * 1 / 7, offScreenCanvas.height * 1 / 2)
                if (bmp != null)
                    offScreenCanvas.drawBitmap(bmp, 10f, (offScreenCanvas.height / 4).toFloat(), null)
            }

            if (currentActiveText != null) {
                val tv = TextView(this.context)
                tv.width = offScreenCanvas.width * 3 / 5
                tv.height = offScreenCanvas.height
                tv.textSize = currentTextSize.toFloat()
                tv.gravity = Gravity.CENTER
                tv.typeface = currentTypeFace
                tv.setTextColor(currentColor)
                tv.text = currentActiveText
                val bmp = CommonUtils.getBitmapFromView(tv, offScreenCanvas.width * 3 / 5, offScreenCanvas.height)
                if (bmp != null)
                    offScreenCanvas.drawBitmap(bmp, (offScreenCanvas.width * 1 / 5).toFloat(), 0f, null)
            }
            if (rightImageID != -1) {
                val iv = ImageView(this.context)
                iv.setImageResource(rightImageID)
                iv.scaleType = ImageView.ScaleType.FIT_XY
                iv.adjustViewBounds = true
                val bmp = CommonUtils.getBitmapFromView(iv, offScreenCanvas.width * 1 / 7, offScreenCanvas.height * 1 / 2)
                if (bmp != null)
                    offScreenCanvas.drawBitmap(bmp, (offScreenCanvas.width * 6 / 7).toFloat(), (offScreenCanvas.height / 4).toFloat(), null)
            }
        }
        if (isPressed) {
            val newImage = drawMatrix(offScreenImage, rotationAngle)
            canvas.drawBitmap(newImage, 0f, 0f, p)

        } else {
            canvas.drawBitmap(offScreenImage!!, 0f, 0f, p)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val action = event.action
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                DebugUtils.logE("onTouchEvent", "ASAnimatedImageView MotionEvent.ACTION_DOWN")
                isPressed = true
                postInvalidate()
            }
            MotionEvent.ACTION_OUTSIDE -> {
                DebugUtils.logE("onTouchEvent", "ASAnimatedImageView MotionEvent.ACTION_OUTSIDE")
                if (isPressed) {
                    isPressed = false
                    postInvalidate()
                }
            }
            MotionEvent.ACTION_CANCEL -> {
                DebugUtils.logE("onTouchEvent", "ASAnimatedImageView MotionEvent.ACTION_CANCEL")
                if (isPressed) {
                    isPressed = false
                    postInvalidate()
                }
            }
            MotionEvent.ACTION_MOVE -> {
                DebugUtils.logE("onTouchEvent", "ASAnimatedImageView MotionEvent.ACTION_MOVE")
                if (event.x > this.width || event.x < 0 || event.y > this.height || event.y < 0) {
                    if (isPressed) {
                        isPressed = false
                        postInvalidate()
                    }
                }
            }
            MotionEvent.ACTION_UP -> {
                DebugUtils.logE("onTouchEvent", "ASAnimatedImageView MotionEvent.ACTION_UP")
                if (isPressed) {
                    if (listener != null)
                        listener!!.onClick(this)
                }
                isPressed = false
                postInvalidate()
            }
        }
        return true
    }
}
