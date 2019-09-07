package com.zaitunlabs.zlcore.views

import android.content.Context
import android.graphics.Point
import android.os.CountDownTimer
import android.util.AttributeSet
import android.view.View
import android.view.ViewParent
import android.view.animation.Animation
import android.widget.RelativeLayout

import com.zaitunlabs.zlcore.utils.CommonUtils
import com.zaitunlabs.zlcore.utils.DebugUtils


/**
 * currently CanvasLayout only stick to Activity, not inside View or layout (Recommended)
 * @author ahmad
 */
class CanvasLayout : RelativeLayout {
    /*
	 * setContentView(canvas.getFillParentView()); perlu dipanggil di akhir dari method OnCreate setiap activity
	 */
    var widthRatio: Double = 0.toDouble()
    var heightRatio: Double = 0.toDouble()
    private var context: Context? = null
    var canvasWidth: Int = 0
        internal set
    var canvasHeight: Int = 0
        internal set

    val fillParentView: View
        get() {
            layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT)
            return this
        }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context) : super(context) {
        init(context)
    }

    private fun init(context: Context?) {
        this.context = context
        val isFullScreen = CommonUtils.isActivityFullScreen(context)
        DebugUtils.logW("SIZE", "fullscreen : $isFullScreen")
        widthRatio = CommonUtils.getScreenWidth(context).toDouble() / 100
        val navigationBar = CommonUtils.getNavigationBarSize(context)
        heightRatio = (CommonUtils.getScreenHeight(context) - navigationBar.y - if (isFullScreen) 0 else CommonUtils.getStatusBarHeight(context!!)).toDouble() / 100

        DebugUtils.logW("SIZE layout", "widthRatio : $widthRatio")
        DebugUtils.logW("SIZE layout", "heightRatio : $heightRatio")
    }

    /**
     * add View with frame relative to its parent
     * @param v View
     * @param left in percentage with parent width
     * @param top in percentage with parent height
     * @param width in percentage with parent width
     * @param height in percentage with parent height
     */
    fun addViewWithFrame(v: View, left: Int, top: Int, width: Int, height: Int) {
        //widthRatio * width = heightRatio * height ==> ???

        val viewWidth = Math.round(widthRatio * if (width == CanvasSection.SAME_AS_OTHER_SIDE) heightRatio * height / widthRatio else width).toInt()
        val viewHeight = Math.round(heightRatio * if (height == CanvasSection.SAME_AS_OTHER_SIDE) widthRatio * width / heightRatio else height).toInt()
        val leftX = Math.round(widthRatio * left).toInt()
        val topY = Math.round(heightRatio * top).toInt()

        DebugUtils.logW("LAYOUT", "height : $viewHeight")
        DebugUtils.logW("LAYOUT", "leftX : $leftX")

        val params = RelativeLayout.LayoutParams(viewWidth, viewHeight)
        params.leftMargin = leftX
        params.topMargin = topY
        addView(v, params)

        if (v is CanvasSection) {
            v.sectionWidthInPixel = viewWidth
            v.sectionHeightInPixel = viewHeight
        }
    }

    @JvmOverloads
    fun createNewSectionWithFrame(left: Int, top: Int, width: Int, height: Int, noScroll: Boolean = true): CanvasSection {
        DebugUtils.logW("SIZE", "h : " + heightRatio * 100)
        val section = CanvasSection("", this.context, left, top, width, height, (widthRatio * 100).toInt(), (heightRatio * 100).toInt(), noScroll)
        addViewWithFrame(section, left, top, width, height)
        return section
    }

    fun createNewSectionWithFrame(sectionName: String, left: Int, top: Int, width: Int, height: Int, noScroll: Boolean): CanvasSection {
        val section = CanvasSection(sectionName, this.context, left, top, width, height, (widthRatio * 100).toInt(), (heightRatio * 100).toInt(), noScroll)
        //section.setTag(CanvasSection.SECTION_NAME_TAG, sectionName);
        addViewWithFrame(section, left, top, width, height)
        return section
    }

    fun createNewSectionWithFrame(sectionName: String, left: Int, top: Int, width: Int, height: Int, noScroll: Boolean, useBackgroundView: Boolean): CanvasSection {
        val section = CanvasSection(sectionName, this.context, left, top, width, height, (widthRatio * 100).toInt(), (heightRatio * 100).toInt(), noScroll, useBackgroundView)
        //section.setTag(CanvasSection.SECTION_NAME_TAG, sectionName);
        addViewWithFrame(section, left, top, width, height)
        return section
    }

    fun ResizeSectionWithFrame(v: CanvasSection, left: Int, top: Int, width: Int, height: Int) {
        val section = getSectionInCanvas(v)
        if (section != null) {
            val viewWidth = (widthRatio * width).toInt()
            val viewHeight = (heightRatio * height).toInt()
            val leftX = (widthRatio * left).toInt()
            val topY = (heightRatio * top).toInt()

            //update section coordinate
            section.sectionX = left
            section.sectionY = top
            section.sectionWidth = width
            section.sectionHeight = height

            val anim = ResizeMoveAnimation(section, leftX.toFloat(), topY.toFloat(), viewWidth.toFloat(), viewHeight.toFloat())
            anim.start()
        }
    }

    private fun getSectionInCanvas(neeedleSection: CanvasSection): CanvasSection? {
        val sectionNumber = childCount
        var result: CanvasSection? = null
        for (i in 0 until sectionNumber) {
            val item = getChildAt(i) as CanvasSection
            if (item === neeedleSection) {
                result = item
            }
        }
        return result
    }

    fun calculateNewRatioWithParent() {
        val vp = parent
        if (vp != null && vp is View) {
            (vp as View).measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
            widthRatio = (vp as View).measuredWidth.toDouble() / 100
            heightRatio = (vp as View).measuredHeight.toDouble() / 100
        }
    }

    fun calculateNewRatioWith(v: View?) {
        if (v != null) {
            v.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
            widthRatio = v.measuredWidth.toDouble() / 100
            heightRatio = v.measuredHeight.toDouble() / 100
        }
    }

    fun calculateNewRatioWithContext() {
        init(this.context)
    }

    fun removeSection(section: CanvasSection) {
        val timer: CountDownTimer
        timer = object : CountDownTimer(500, 500) {
            override fun onFinish() {
                //section.setDisappearAnim(null); // gak dipake karna section jg akan diremove
                removeView(section)
                invalidate()
            }

            override fun onTick(millisUntilFinished: Long) {
                // TODO Auto-generated method stub
            }
        }
        CommonUtils.setViewAnim_disappearslideupfromBottom(section, 300).setAnimationListener(object : Animation.AnimationListener {

            override fun onAnimationStart(animation: Animation) {}

            override fun onAnimationRepeat(animation: Animation) {}

            override fun onAnimationEnd(animation: Animation) {
                // TODO Auto-generated method stub
                section.visibility = View.GONE
                timer.start()
            }

        })
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        // TODO Auto-generated method stub
        super.onSizeChanged(w, h, oldw, oldh)
        canvasWidth = w
        canvasHeight = h
    }

}
