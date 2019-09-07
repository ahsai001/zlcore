package com.zaitunlabs.zlcore.views

import android.view.View
import android.view.animation.Animation
import android.view.animation.Interpolator
import android.view.animation.Transformation
import android.widget.RelativeLayout.LayoutParams

import com.zaitunlabs.zlcore.utils.DebugUtils


class ResizeMoveAnimation(private val mView: View, private val mToX: Float, private val mToY: Float, private val mToWidth: Float, private val mToHeight: Float) : Animation() {

    private val mFromX: Float

    private val mFromY: Float
    private val mFromHeight: Float
    private val mFromWidth: Float

    var currentWidth: Float = 0.toFloat()
        private set


    var currentHeight: Float = 0.toFloat()
        private set

    var animListener: ASAnimListener? = null

    private val param: LayoutParams

    private var x = 0

    init {

        //before
        param = mView.layoutParams as LayoutParams
        mFromX = param.leftMargin.toFloat()
        mFromY = param.topMargin.toFloat()
        mFromHeight = param.height.toFloat()
        mFromWidth = param.width.toFloat()

        DebugUtils.logW("JEJAK", ">>>>>>>>>>> " + (mView as CanvasSection).sectionName + " ResizeMoveAnimation from w=" + mFromWidth + " h=" + mFromHeight)
        DebugUtils.logW("JEJAK", ">>>>>>>>>>> " + mView.sectionName + " ResizeMoveAnimation to w=" + mToWidth + " h=" + mToHeight)

        currentWidth = param.width.toFloat()
        currentHeight = param.height.toFloat()
        mView.clearAnimation()
        mView.setAnimation(null)
        mView.setAnimation(this)
        //param = (LayoutParams)mView.getLayoutParams();

        //set parameter basic animation
        duration = 400
        interpolator = sMenuInterpolator
    }//after
    //set view

    override fun startNow() {
        super.startNow()
        mView.rootView.invalidate()
    }

    override fun start() {
        super.start()
        mView.rootView.invalidate()
    }

    override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
        val height = (mToHeight - mFromHeight) * interpolatedTime + mFromHeight
        val width = (mToWidth - mFromWidth) * interpolatedTime + mFromWidth

        val left = (mToX - mFromX) * interpolatedTime + mFromX
        val top = (mToY - mFromY) * interpolatedTime + mFromY

        DebugUtils.logD("COUNTER", "ke-" + x++)

        param.height = height.toInt()
        param.width = width.toInt()
        param.leftMargin = left.toInt()
        param.topMargin = top.toInt()
        mView.layoutParams = param

        DebugUtils.logW("JEJAK", ">>>>>>>>>>> " + (mView as CanvasSection).sectionName + " ResizeMoveAnimation current x=" + param.leftMargin + " y=" + param.topMargin)


        currentWidth = width
        currentHeight = height

        if (animListener != null) {
            /*
        	new Handler().post(new Runnable() {
				@Override
				public void run() {
		        	animListener.animationRepeat(currentWidth, currentHeight);
				}
			});
			*/
            DebugUtils.logW("JEJAK", "invoke listener")
            animListener!!.animationRepeat(currentWidth, currentHeight)
        }
    }

    interface ASAnimListener {
        fun animationRepeat(width: Float, height: Float)
    }

    companion object {


        private val sMenuInterpolator = Interpolator { t ->
            var t = t
            t -= 1.0f
            Math.pow(t.toDouble(), 5.0).toFloat() + 1.0f
        }
    }

}