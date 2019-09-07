package com.zaitunlabs.zlcore.views

import android.content.Context
import android.graphics.Rect
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.widget.FrameLayout
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.ScrollView

import com.zaitunlabs.zlcore.R
import com.zaitunlabs.zlcore.utils.CommonUtils
import com.zaitunlabs.zlcore.utils.DebugUtils


/**
 * CanvasSection view, direkomendasikan hanya untuk di add ke CanvasLayout atau
 * CanvasSection lainnya, karena view ini membutuhkan parentWidth dan
 * parentHeight dalam pixel
 *
 * @author ahmad
 */
class CanvasSection : FrameLayout {
    var sectionX: Int = 0
    var sectionY: Int = 0
    var sectionWidth: Int = 0
    var sectionHeight: Int = 0 // variable dengan value 0 - 100 % relatif ke Parent
    private var parentWidth: Int = 0 // in pixel
    private var parentHeight: Int = 0 // in pixel

    var sectionWidthInPixel: Int = 0
    var sectionHeightInPixel: Int = 0


    private var maintainRatio = true //default true, jangan ikut resize jika parent resize
    private var updateLayoutChildren = true //default true. selalu update child jika resize

    private var gestureDetector: ASGestureDetector? = null
    var shiftPositionHandler: ASShiftRepositionHandler? = null
        private set

    private var isSectionConsumeEvent = true // default nya consume event

    private var broadcastEventType = BROADCAST_EVENT_NONE

    var canvasWidth: Int = 0
        internal set
    var canvasHeight: Int = 0
        internal set

    private var context: Context? = null
    private var vScroll: ScrollView? = null // width-height boleh di tentukan sizenya
    private var hScroll: HorizontalScrollView? = null// width-height boleh di tentukan
    // sizenya
    var sectionLayout: ASRelativeLayout? = null// width-height harus wrap content
    var linearLayout: ASLinearLayout? = null
    private var backgroundView: View? = null
    private val useBackGroundView = false

    //String sectionName = (String) getTag(SECTION_NAME_TAG);
    var sectionName: String? = null


    var appearAnim: Animation? = null
    var disappearAnim: Animation? = null

    private var noScroll = false // defaultnya scroll aktif


    private var isAlwaysConsumeEvent = false //default false


    fun setMaintainRatio(maintainRatio: Boolean) {
        this.maintainRatio = maintainRatio
    }

    fun setUpdateLayoutChildren(updateLayoutChildren: Boolean) {
        this.updateLayoutChildren = updateLayoutChildren
    }

    fun setAlwaysConsumeEvent(isAlwaysConsumeEvent: Boolean) {
        this.isAlwaysConsumeEvent = isAlwaysConsumeEvent
    }

    fun setASGestureListener(dmListener: ASGestureListener) {
        gestureDetector!!.gestureListener = dmListener
    }

    fun getParentWidth(): Double {
        return parentWidth.toDouble()
    }

    fun setParentWidth(parentWidth: Int) {
        this.parentWidth = parentWidth
    }

    fun getParentHeight(): Double {
        return parentHeight.toDouble()
    }

    fun setParentHeight(parentHeight: Int) {
        this.parentHeight = parentHeight
    }

    fun consumeEvent() {
        this.isSectionConsumeEvent = true
    }

    fun unConsumeEvent() {
        this.isSectionConsumeEvent = false
    }

    fun setBroadCastEventType(broadCastEventType: Int) {
        this.broadcastEventType = broadCastEventType
    }


    override fun setBackgroundColor(color: Int) {
        if (backgroundView != null) {
            //by this hacked way, refresh happened in canvas parent
            backgroundView!!.visibility = View.VISIBLE
            backgroundView!!.setBackgroundColor(color)
        } else {
            //by this way, refresh not happened in canvas parent, tanya kenapa???
            super.setBackgroundColor(color)
        }
    }


    // (CanvasSection)linearlayout/FrameLayout -> scrollview -> horizontalscrollview ->
    // (sectionLayout)RelativeLayout
    // (CanvasSection)linearlayout/FrameLayout -> (sectionLayout)RelativeLayout
    private fun init(sectionName: String?, context: Context) {
        var sectionName = sectionName
        this.context = context

        if (sectionName == null || sectionName === "") {
            sectionName = this.toString()
        }
        sectionName = sectionName
        if (useBackGroundView) {
            backgroundView = View(context)
            addView(backgroundView, FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT))
            backgroundView!!.visibility = View.GONE
        }
        if (!noScroll) {
            vScroll = object : ScrollView(context) {
                /*
				GestureDetector mGestureDetector = new GestureDetector(this.getContext(), new GestureDetector.SimpleOnGestureListener(){
					public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
						if(Math.abs(distanceY) > Math.abs(distanceX)) {
							DebugUtils.logE("vScroll", getSectionName()+" onScroll: TRUE");
			                return true;
			            }
						DebugUtils.logE("vScroll", getSectionName()+" onScroll: FALSE");
			            return false;
					};
				});
				*/
                override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
                    val value = super.onInterceptTouchEvent(ev)
                    DebugUtils.logE("vScroll", "$sectionName onInterceptTouchEvent: $value")
                    return value
                }

                override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
                    val value = super.dispatchTouchEvent(ev)
                    DebugUtils.logE("vScroll", "$sectionName dispatchTouchEvent: $value")
                    return value
                }
            }

            vScroll!!.isFillViewport = false
            /*
			int width = (int) ((parentWidth * sectionWidth) / 100);
			int height = (int) ((parentHeight * sectionHeight) / 100);
			*/
            val vScrollParam = FrameLayout.LayoutParams(
/*width != 0 ? width : */FrameLayout.LayoutParams.MATCH_PARENT,
/*height != 0 ? height : */FrameLayout.LayoutParams.WRAP_CONTENT)
            vScrollParam.leftMargin = 0
            vScrollParam.topMargin = 0
            addView(vScroll, vScrollParam)

            hScroll = object : HorizontalScrollView(context) {
                /*
				GestureDetector mGestureDetector = new GestureDetector(this.getContext(), new GestureDetector.SimpleOnGestureListener(){
					public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
						if(Math.abs(distanceY) < Math.abs(distanceX)) {
			                return true;
			            }
			            return false;
					};
				});
				*/
                override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
                    val value = super.onInterceptTouchEvent(ev)
                    DebugUtils.logE("hScroll", "$sectionName onInterceptTouchEvent: $value")
                    //set return false, agar move event di asmovable menu bisa aktif
                    onTouchEvent(ev)
                    return false
                }

                override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
                    val value = super.dispatchTouchEvent(ev)
                    DebugUtils.logE("hScroll", "$sectionName dispatchTouchEvent: $value")
                    return value
                }
            }
            hScroll!!.isFillViewport = false
            val hScrollParam = FrameLayout.LayoutParams(
/*width != 0 ? width : */FrameLayout.LayoutParams.WRAP_CONTENT,
/*height != 0 ? height : */FrameLayout.LayoutParams.MATCH_PARENT)
            hScrollParam.leftMargin = 0
            hScrollParam.topMargin = 0
            vScroll!!.addView(hScroll, hScrollParam)

            /*
			 * LinearLayout.LayoutParams vScrollParam = new
			 * LinearLayout.LayoutParams((width != 0 ? width :
			 * HorizontalScrollView.LayoutParams.MATCH_PARENT),(height != 0 ?
			 * height : HorizontalScrollView.LayoutParams.MATCH_PARENT));
			 * TwoDScrollView hScroll = new TwoDScrollView(context);
			 * addView(hScroll, vScrollParam);
			 */

            sectionLayout = object : ASRelativeLayout(context) {
                override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
                    val value = super.onInterceptTouchEvent(ev)
                    DebugUtils.logE("sectionLayout", "$sectionName onInterceptTouchEvent: $value")
                    return value
                }

                override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
                    val value = super.dispatchTouchEvent(ev)
                    DebugUtils.logE("sectionLayout", "$sectionName dispatchTouchEvent: $value")
                    return value
                }
            }
            val sectionParam = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT)
            hScroll!!.addView(sectionLayout, sectionParam)
        } else {
            sectionLayout = object : ASRelativeLayout(context) {
                override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
                    val value = super.onInterceptTouchEvent(ev)
                    DebugUtils.logE("sectionLayout", "$sectionName onInterceptTouchEvent: $value")
                    return value
                }

                override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
                    val value = super.dispatchTouchEvent(ev)
                    DebugUtils.logE("sectionLayout", "$sectionName dispatchTouchEvent: $value")
                    return value
                }
            }
            val sectionParam = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT)
            addView(sectionLayout, sectionParam)
        }

        this.gestureDetector = ASGestureDetector(sectionName, this.getContext())
        this.shiftPositionHandler = object : ASShiftRepositionHandler(
                this.getContext(), this) {
            protected override val yNow: Int
                get() = sectionY

            protected override val xNow: Int
                get() = sectionX

            protected override val parentWidth: Int
                get() = parentWidth

            protected override val parentHeight: Int
                get() = parentHeight

            override fun postStepUpdate(view: View?, viewWidth: Int, viewHeight: Int, isInRepeatState: Boolean) {
                //do nothing now
                //DebugUtils.logW("postStepUpdate", ">>>>>>>>>>>>>>>>>>>>>>>");


                //if(!isInRepeatState){
                //DebugUtils.logW("postStepUpdate", "not in repeatstate");
                if (linearLayout != null) {

                    val param = linearLayout!!.layoutParams as RelativeLayout.LayoutParams
                    if (linearLayout!!.orientation == LinearLayout.VERTICAL) {
                        param.width = viewWidth
                    } else if (linearLayout!!.orientation == LinearLayout.HORIZONTAL) {
                        param.height = viewHeight
                    }
                    linearLayout!!.layoutParams = param

                    //DebugUtils.logW("postStepUpdate", "linearLayout");
                } else {
                    val childrenCount = this@CanvasSection.sectionLayout!!.childCount
                    //DebugUtils.logW("postStepUpdate", "sectionLayout");

                    for (x in 0 until childrenCount) {
                        val child = this@CanvasSection.sectionLayout!!.getChildAt(x)
                        if (child is CanvasSection) {
                            if (updateLayoutChildren) {
                                child.updateLayout(this@CanvasSection, viewWidth, viewHeight)
                            }
                        }
                    }

                }
                //}
            }

            override fun updateDimension(width: Int, height: Int) {
                sectionWidth = width
                sectionHeight = height
            }

            override fun updatePosition(x: Int, y: Int) {
                sectionX = x
                sectionY = y
            }

            override fun convertRectFromCommonToParamType(commonTypeValue: Rect): Rect {
                val paramTypeValue = Rect()
                paramTypeValue.left = parentWidth * commonTypeValue.left / 100
                paramTypeValue.top = parentHeight * commonTypeValue.top / 100
                paramTypeValue.right = paramTypeValue.left + parentWidth * commonTypeValue.width() / 100
                paramTypeValue.bottom = paramTypeValue.top + parentHeight * commonTypeValue.height() / 100
                return paramTypeValue
            }

            override fun convertRectFromParamToCommonType(paramTypeValue: Rect): Rect {
                val commonTypeValue = Rect()
                commonTypeValue.left = Math
                        .round(paramTypeValue.left.toFloat() * 100 / parentWidth.toFloat())
                commonTypeValue.top = Math
                        .round(paramTypeValue.top.toFloat() * 100 / parentHeight.toFloat())
                return commonTypeValue
            }
        }

        this.setOnTouchListener { arg0, arg1 ->
            //DebugUtils.logE("setOnTouchListener", getSectionName()+"",false);
            gestureDetector!!.handleOnTouch(arg0, arg1)
            //return true;
        }
    }


    fun updateLayout(parent: CanvasSection, newParentWidth: Int, newParentHeight: Int) {
        if (maintainRatio) {
            setParentWidth(newParentWidth)
            setParentHeight(newParentHeight)

            DebugUtils.logD("ahmad", "parent width:" + getParentWidth() + " - parent section widht:" + parent.sectionWidth + " - child section width:" + sectionWidth)

            DebugUtils.logD("ahmad", "before child $sectionName: w/h => $sectionWidthInPixel/$sectionHeightInPixel")
            val childWidth = Math.round(getParentWidth() * sectionWidth / 100).toInt()

            val childHeight = Math.round(getParentHeight() * sectionHeight / 100).toInt()


            val leftX = Math.round(getParentWidth() * sectionX / 100).toInt()
            val topY = Math.round(getParentHeight() * sectionY / 100).toInt()


            val params = RelativeLayout.LayoutParams(
                    childWidth, childHeight)
            params.leftMargin = leftX
            params.topMargin = topY

            layoutParams = params

            sectionWidthInPixel = childWidth
            sectionHeightInPixel = childHeight


            DebugUtils.logD("ahmad", "after child $sectionName: w/h => $sectionWidthInPixel/$sectionHeightInPixel")

            val childrenCount = this@CanvasSection.sectionLayout!!.childCount
            for (x in 0 until childrenCount) {
                val child = this@CanvasSection.sectionLayout!!.getChildAt(x)
                if (child is CanvasSection) {
                    child.updateLayout(this@CanvasSection, childWidth, childHeight)
                }
            }
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val isLogged = false
        val action = ev.action
        var actString = ""
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                DebugUtils.logD("dispatchTouchEvent",
                        sectionName!! + " MotionEvent.ACTION_DOWN")
                actString = "MotionEvent.ACTION_DOWN"
            }
            MotionEvent.ACTION_MOVE -> {
                DebugUtils.logD("dispatchTouchEvent",
                        sectionName!! + " MotionEvent.ACTION_MOVE")
                actString = "MotionEvent.ACTION_MOVE"
            }
            MotionEvent.ACTION_UP -> {
                DebugUtils.logD("dispatchTouchEvent",
                        sectionName!! + " MotionEvent.ACTION_UP")
                actString = "MotionEvent.ACTION_UP"
            }
            MotionEvent.ACTION_CANCEL -> {
                DebugUtils.logD("dispatchTouchEvent",
                        sectionName!! + " MotionEvent.ACTION_CANCEL")
                actString = "MotionEvent.ACTION_CANCEL"
            }
            MotionEvent.ACTION_OUTSIDE -> {
                DebugUtils.logD("dispatchTouchEvent",
                        sectionName!! + " MotionEvent.ACTION_OUTSIDE")
                actString = "MotionEvent.ACTION_OUTSIDE"
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                DebugUtils.logD("dispatchTouchEvent",
                        sectionName!! + " MotionEvent.ACTION_POINTER_DOWN")
                actString = "MotionEvent.ACTION_POINTER_DOWN"
            }
            MotionEvent.ACTION_POINTER_UP -> {
                DebugUtils.logD("dispatchTouchEvent",
                        sectionName!! + " MotionEvent.ACTION_POINTER_UP")
                actString = "MotionEvent.ACTION_POINTER_UP"
            }
            else -> {
                DebugUtils.logD("dispatchTouchEvent",
                        sectionName!! + " MotionEvent.DEFAULT")
                actString = "MotionEvent.DEFAULT"
            }
        }


        /*
		 * if(!isSectionConsumeEvent) return false;
		 *
		 * if(broadcastEventType == BROADCAST_EVENT_ALL_CHILDREN ||
		 * broadcastEventType == BROADCAST_EVENT_ALL_SECTION_CHILDREN){
		 * if(!onInterceptTouchEvent(ev)){ int childCount = getChildCount();
		 * for(int i = 0; i < childCount; i++){ View child = getChildAt(i);
		 * if(broadcastEventType == BROADCAST_EVENT_ALL_CHILDREN ||
		 * (broadcastEventType == BROADCAST_EVENT_ALL_SECTION_CHILDREN && child
		 * instanceof CanvasSection)){ child.dispatchTouchEvent(ev); } } }
		 * return true; }
		 */
        val returnValue = super.dispatchTouchEvent(ev)
        DebugUtils.logE("dispatchTouchEvent", "$sectionName $actString returnValue : $returnValue")
        return returnValue
        // super.dispatchTouchEvent(ev);
        // return true;
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        val isLogged = false
        val action = ev.action
        var actString = ""
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                DebugUtils.logD("onInterceptTouchEvent",
                        sectionName!! + " MotionEvent.ACTION_DOWN")
                actString = "MotionEvent.ACTION_DOWN"
            }
            MotionEvent.ACTION_MOVE -> {
                DebugUtils.logD("onInterceptTouchEvent",
                        sectionName!! + " MotionEvent.ACTION_MOVE")
                actString = "MotionEvent.ACTION_MOVE"
            }
            MotionEvent.ACTION_UP -> {
                DebugUtils.logD("onInterceptTouchEvent",
                        sectionName!! + " MotionEvent.ACTION_UP")
                actString = "MotionEvent.ACTION_UP"
            }
            MotionEvent.ACTION_CANCEL -> {
                DebugUtils.logD("onInterceptTouchEvent",
                        sectionName!! + " MotionEvent.ACTION_CANCEL")
                actString = "MotionEvent.ACTION_CANCEL"
            }
            MotionEvent.ACTION_OUTSIDE -> {
                DebugUtils.logD("onInterceptTouchEvent",
                        sectionName!! + " MotionEvent.ACTION_OUTSIDE")
                actString = "MotionEvent.ACTION_OUTSIDE"
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                DebugUtils.logD("onInterceptTouchEvent",
                        sectionName!! + " MotionEvent.ACTION_POINTER_DOWN")
                actString = "MotionEvent.ACTION_POINTER_DOWN"
            }
            MotionEvent.ACTION_POINTER_UP -> {
                DebugUtils.logD("onInterceptTouchEvent",
                        sectionName!! + " MotionEvent.ACTION_POINTER_UP")
                actString = "MotionEvent.ACTION_POINTER_UP"
            }
            else -> {
                DebugUtils.logD("onInterceptTouchEvent",
                        sectionName!! + " MotionEvent.DEFAULT")
                actString = "MotionEvent.DEFAULT"
            }
        }

        val returnValue = super.onInterceptTouchEvent(ev)
        DebugUtils.logE("onInterceptTouchEvent", "$sectionName $actString returnValue : $returnValue")
        return returnValue
        // handleTouchEvent(ev);
        // return false;
    }


    /*
	public CanvasSection(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
		// TODO Auto-generated constructor stub
	}

	public CanvasSection(Context context) {
		super(context);
		init(context);
		// TODO Auto-generated constructor stub
	}
	*/

    /**
     * ini merupakan constructor yg paling direkomendasikan
     *
     * @param context
     * @param left
     * @param top
     * @param width
     * @param height
     * @param parentWidth
     * @param parentHeight
     */
    constructor(sectionName: String, context: Context, left: Int, top: Int, width: Int,
                height: Int, parentWidth: Int, parentHeight: Int, noScroll: Boolean) : super(context) {
        sectionX = left
        sectionY = top
        sectionWidth = width
        sectionHeight = height
        setParentWidth(parentWidth)
        setParentHeight(parentHeight)
        this.noScroll = noScroll
        init(sectionName, context)
        // TODO Auto-generated constructor stub
    }

    constructor(sectionName: String, context: Context, left: Int, top: Int, width: Int,
                height: Int, parentWidth: Int, parentHeight: Int, noScroll: Boolean, useBackgroundView: Boolean) : super(context) {
        sectionX = left
        sectionY = top
        sectionWidth = width
        sectionHeight = height
        setParentWidth(parentWidth)
        setParentHeight(parentHeight)
        this.noScroll = noScroll
        this.useBackGroundView = useBackgroundView
        init(sectionName, context)
        // TODO Auto-generated constructor stub
    }

    fun getPixelRelativeToWidth(valueInPercentage: Int): Int {
        return Math.round((parentWidth * sectionWidth * valueInPercentage / (100 * 100)).toFloat())
    }


    fun getPixelRelativeToHeight(valueInPercentage: Int): Int {
        return Math.round((parentHeight * sectionHeight * valueInPercentage / (100 * 100)).toFloat())
    }

    fun getPercentageEqualHeightPixelWithPercentageFrom(otherCanvas: ViewGroup, valueInPercentage: Int): Int {
        var value = -1
        if (otherCanvas is CanvasSection) {
            value = Math.round(otherCanvas.getParentHeight() * otherCanvas.sectionHeight.toDouble() * valueInPercentage.toDouble() / (this.parentHeight * this.sectionHeight)).toInt()
        } else if (otherCanvas is CanvasLayout) {
            value = Math.round(otherCanvas.heightRatio * valueInPercentage.toDouble() * (100 * 100).toDouble() / (this.parentHeight * this.sectionHeight)).toInt()
        }
        return value
    }

    fun getPercentageEqualWidthPixelWithPercentageFrom(otherCanvas: ViewGroup, valueInPercentage: Int): Int {
        var value = -1
        if (otherCanvas is CanvasSection) {
            value = Math.round(otherCanvas.getParentWidth() * otherCanvas.sectionWidth.toDouble() * valueInPercentage.toDouble() / (this.parentWidth * this.sectionWidth)).toInt()
        } else if (otherCanvas is CanvasLayout) {
            value = Math.round(otherCanvas.widthRatio * valueInPercentage.toDouble() * (100 * 100).toDouble() / (this.parentWidth * this.sectionWidth)).toInt()
        }
        return value
    }


    fun getPercentageRelativeToWidth(valueInPixel: Int): Int {
        return Math.round((100 * 100 * valueInPixel / (parentWidth * sectionWidth)).toFloat())
    }


    fun getPercentageRelativeToHeight(valueInPixel: Int): Int {
        return Math.round((100 * 100 * valueInPixel / (parentHeight * sectionHeight)).toFloat())
    }


    fun addViewWithFrame(v: View, left: Int, top: Int, width: Int,
                         height: Int) {
        var viewWidth = 0
        if (width == RelativeLayout.LayoutParams.MATCH_PARENT || width == RelativeLayout.LayoutParams.WRAP_CONTENT) {
            viewWidth = width
        } else if (width == SAME_AS_OTHER_SIDE) {
            //parentWidth * width = parentHeight * height
            //width = (parentHeight * height) / parentWidth;
            //viewWidth = (int) Math.round((parentWidth * sectionWidth * width) / (100 * 100));
        } else {
            viewWidth = Math.round((parentWidth * sectionWidth * width / (100 * 100)).toFloat())
        }

        var viewHeight = 0
        if (height == RelativeLayout.LayoutParams.MATCH_PARENT || height == RelativeLayout.LayoutParams.WRAP_CONTENT) {
            viewHeight = height
        } else if (height == SAME_AS_OTHER_SIDE) {
            //parentWidth * width = parentHeight * height
            //height = (parentWidth * width) / parentHeight;
            //viewHeight = (int) Math.round((parentHeight * sectionHeight * height) / (100 * 100));
        } else {
            viewHeight = Math.round((parentHeight * sectionHeight * height / (100 * 100)).toFloat())
        }


        if (width == SAME_AS_OTHER_SIDE) {
            viewWidth = viewHeight
        } else if (height == SAME_AS_OTHER_SIDE) {
            viewHeight = viewWidth
        }

        val leftX = Math.round((parentWidth * sectionWidth * left / (100 * 100)).toFloat())
        val topY = Math.round((parentHeight * sectionHeight * top / (100 * 100)).toFloat())



        DebugUtils.logW("SECTION", "height $sectionName: $viewHeight")
        DebugUtils.logW("SECTION", "leftX $sectionName: $leftX")

        val params = RelativeLayout.LayoutParams(
                viewWidth, viewHeight)
        params.leftMargin = leftX
        params.topMargin = topY
        this.sectionLayout!!.addView(v, params)


        if (v is CanvasSection) {
            v.sectionWidthInPixel = viewWidth
            v.sectionHeightInPixel = viewHeight
        }
    }


    fun addViewInRelativeLayout(v: View,
                                param: RelativeLayout.LayoutParams) {
        this.sectionLayout!!.addView(v, param)
    }

    @JvmOverloads
    fun addSubSectionWithFrame(left: Int, top: Int, width: Int,
                               height: Int, noScroll: Boolean = true): CanvasSection {
        return addSubSectionWithFrame("", left, top, width, height, noScroll, false, false, false)
    }

    fun addSubSectionWithFrame(left: Int, top: Int, width: Int,
                               height: Int, noScroll: Boolean, isUseDefaultAppearAnimation: Boolean, isUseDefaultDisappearAnimation: Boolean): CanvasSection {
        return addSubSectionWithFrame("", left, top, width, height, noScroll, isUseDefaultAppearAnimation, isUseDefaultDisappearAnimation, false)
    }

    fun addSubSectionWithFrame(sectionName: String, left: Int,
                               top: Int, width: Int, height: Int, noScroll: Boolean, useBackgroundView: Boolean): CanvasSection {
        return addSubSectionWithFrame(sectionName, left, top, width, height, noScroll, false, false, false)
    }

    @JvmOverloads
    fun addSubSectionWithFrame(sectionName: String, left: Int,
                               top: Int, width: Int, height: Int, noScroll: Boolean, isUseDefaultAppearAnimation: Boolean = false, isUseDefaultDisappearAnimation: Boolean = false, useBackgroundView: Boolean = false): CanvasSection {
        val section = CanvasSection(sectionName, context, left, top, width,
                height, parentWidth * sectionWidth / 100,
                parentHeight * sectionHeight / 100, noScroll)

        //section.setTag(SECTION_NAME_TAG, sectionName);

        if (isUseDefaultAppearAnimation) {
            section.appearAnim = CommonUtils.setViewAnim_Appearslidedownfromtop(null, 800)
        }
        if (isUseDefaultDisappearAnimation) {
            section.disappearAnim = CommonUtils.setViewAnim_disappearslideupfromBottom(null, 800)
        }

        addViewWithFrame(section, left, top, width, height)
        return section
    }


    override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
        // TODO Auto-generated method stub
        super.onWindowFocusChanged(hasWindowFocus)
        var anim: Animation? = null
        if (hasWindowFocus) {
            if (appearAnim != null) {
                appearAnim!!
                        .setAnimationListener(object : Animation.AnimationListener {

                            override fun onAnimationStart(animation: Animation) {}

                            override fun onAnimationRepeat(animation: Animation) {}

                            override fun onAnimationEnd(animation: Animation) {
                                appearAnim = null
                            }
                        })
                anim = appearAnim
            }
        } else {
            if (disappearAnim != null) {
                disappearAnim!!
                        .setAnimationListener(object : Animation.AnimationListener {

                            override fun onAnimationStart(animation: Animation) {}

                            override fun onAnimationRepeat(animation: Animation) {}

                            override fun onAnimationEnd(animation: Animation) {
                                disappearAnim = null
                            }
                        })
                anim = disappearAnim
            }
        }
        val activeAnim = this.animation
        if (activeAnim != null) {
            activeAnim.cancel()
            activeAnim.reset()
        }
        this.clearAnimation()
        if (anim != null) {
            this.animation = anim
            anim.startNow()
        }
    }

    fun setSectionAsLinearLayout(linearOrientation: Int): CanvasSection {
        return setSectionAsLinearLayout(0, 0, linearOrientation)
    }

    fun setSectionAsLinearLayout(left: Int, top: Int, linearOrientation: Int): CanvasSection {
        var scrollView: ScrollView? = null
        if (noScroll) {
            scrollView = ScrollView(context)
            addViewWithFrame(scrollView, 0, 0, 100, 100)
        }
        linearLayout = object : ASLinearLayout(context) {
            override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
                val value = super.dispatchTouchEvent(ev)
                DebugUtils.logE("linearLayout", "$sectionName dispatchTouchEvent: $value")
                return value
            }

            override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
                val value = super.onInterceptTouchEvent(ev)
                DebugUtils.logE("linearLayout", "$sectionName onInterceptTouchEvent: $value")
                return value
            }
        }


        linearLayout!!.orientation = linearOrientation

        if (scrollView != null) {
            if (linearOrientation == LinearLayout.VERTICAL) {
                addViewWithFrame(linearLayout, left, top, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
            } else if (linearOrientation == LinearLayout.HORIZONTAL) {
                addViewWithFrame(linearLayout, left, top, RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT)
            }
        } else {
            if (linearOrientation == LinearLayout.VERTICAL) {
                addViewWithFrame(linearLayout, left, top, 100, RelativeLayout.LayoutParams.WRAP_CONTENT)
            } else if (linearOrientation == LinearLayout.HORIZONTAL) {
                addViewWithFrame(linearLayout, left, top, RelativeLayout.LayoutParams.WRAP_CONTENT, 100)
            }
        }
        return this
    }


    @JvmOverloads
    fun addViewInLinearLayout(v: View, marginRect: ASMovableMenu.LayoutViewMargin? = null) {
        var marginRect = marginRect
        if (linearLayout != null) {
            val orientation = linearLayout!!.orientation
            if (marginRect == null) {
                marginRect = ASMovableMenu.LayoutViewMargin(0, 0, 0, 0)
            }
            if (orientation == LinearLayout.VERTICAL) {
                val param = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT)
                param.setMargins(marginRect.leftMargin, marginRect.topMargin, marginRect.rightMargin, marginRect.bottomMargin)
                linearLayout!!.addView(v, param)
            } else if (orientation == LinearLayout.HORIZONTAL) {
                val param = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.MATCH_PARENT)
                param.setMargins(marginRect.leftMargin, marginRect.topMargin, marginRect.rightMargin, marginRect.bottomMargin)
                linearLayout!!.addView(v, param)
            }
        }
    }

    fun addViewInLinearLayout(v: View, param: LinearLayout.LayoutParams) {
        if (linearLayout != null) {
            linearLayout!!.addView(v, param)
        }
    }

    fun setVScrollOnTop() {
        // vScroll.post(new Runnable() {
        // public void run() {
        if (vScroll != null)
            vScroll!!.smoothScrollTo(0, 0)
        // }
        // });
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        // TODO Auto-generated method stub
        super.onSizeChanged(w, h, oldw, oldh)
        canvasWidth = w
        canvasHeight = h
    }

    override fun setDrawingCacheEnabled(enabled: Boolean) {
        super.setDrawingCacheEnabled(enabled)
        /*
        final int l = getChildCount();
        for (int i = 0; i < l; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                child.setDrawingCacheEnabled(enabled);
            }
        }
        */
    }

    override fun removeAllViews() {
        if (linearLayout != null) {
            linearLayout!!.removeAllViews()
        } else {
            sectionLayout!!.removeAllViews()
        }
    }


    fun shiftTarget(shiftedSection: CanvasSection,
                    isVertical: Boolean, isHorizontal: Boolean,
                    isJumpToNearest: Boolean, isClickable: Boolean) {
        this.setASGestureListener(object : ASGestureListener {

            override// TODO Auto-generated method stub
            val isClickEnabled: Boolean
                get() = true

            override// TODO Auto-generated method stub
            val isLongClickEnabled: Boolean
                get() = false

            override// TODO Auto-generated method stub
            val isDoubleTapEnabled: Boolean
                get() = false

            override// TODO Auto-generated method stub
            val isSwipeEnabled: Boolean
                get() = false

            override fun upEventOccurred(x: Float, y: Float): Boolean {
                if (isJumpToNearest) {
                    shiftedSection.shiftPositionHandler!!.changeStateToNearestDimension(true)
                    return true
                }
                return false
            }

            override fun downEventOccured(x: Float, y: Float): Boolean {
                return true
            }

            override fun deltaMoveOutsideParameter(swipeType: Int, x: Float,
                                                   y: Float, dx: Float, dy: Float, fromDownDX: Float, fromDownDY: Float): Boolean {
                shiftedSection.shiftPositionHandler!!.shiftViewWithDelta(
                        if (isHorizontal) dx else 0, if (isVertical) dy else 0, if (isHorizontal) fromDownDX else 0, if (isVertical) fromDownDY else 0)
                return true
            }

            override fun deltaMoveInsideParameter(swipeType: Int, x: Float,
                                                  y: Float, dx: Float, dy: Float, fromDownDX: Float, fromDownDY: Float): Boolean {
                shiftedSection.shiftPositionHandler!!.shiftViewWithDelta(
                        if (isHorizontal) dx else 0, if (isVertical) dy else 0, if (isHorizontal) fromDownDX else 0, if (isVertical) fromDownDY else 0)
                return true
            }

            override fun movingSpeed(xSpeed: Float, ySpeed: Float): Boolean {
                // TODO Auto-generated method stub
                return false
            }

            override fun cancelEventOccured(x: Float, y: Float): Boolean {
                return false
            }

            override fun clickEventOccured(): Boolean {
                if (isClickable) {
                    shiftedSection.shiftPositionHandler!!.changeStateToNextDimension(true)
                    return true
                }
                return false
            }

            override fun longClickEventOccured(): Boolean {
                return false
            }

            override fun doubleTapEventOccured(): Boolean {
                // TODO Auto-generated method stub
                return false
            }

            override fun swipeEventOccured(swipeType: Int, x: Float, y: Float,
                                           dx: Float, dy: Float): Boolean {
                // TODO Auto-generated method stub
                return false
            }

            override fun swipeTypeFinal(swipeType: Int): Boolean {
                return false
            }
        })
    }

    companion object {

        val SECTION_NAME_TAG = R.string.app_name

        private val BROADCAST_EVENT_NONE = 0
        private val BROADCAST_EVENT_ALL_CHILDREN = 1
        private val BROADCAST_EVENT_ALL_SECTION_CHILDREN = 2


        val SAME_AS_OTHER_SIDE = -5 //use to set height same as width or vice versa
    }
}
