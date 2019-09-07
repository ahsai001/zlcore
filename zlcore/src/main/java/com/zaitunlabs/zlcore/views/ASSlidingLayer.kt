package com.zaitunlabs.zlcore.views

import android.graphics.Color
import android.graphics.Rect
import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.widget.ImageView.ScaleType
import android.widget.TextView

import com.zaitunlabs.zlcore.R
import com.zaitunlabs.zlcore.utils.DebugUtils
import com.zaitunlabs.zlcore.views.ASSlidingLayer.Companion


class ASSlidingLayer {
    var sliderPanel: CanvasSection? = null
        internal set
    var sliderHandle: CanvasSection? = null
        internal set
    var sliderContentPanel: CanvasSection? = null
        internal set
    var handleText: TextView? = null
        internal set
    internal var handleInfoImage: ASImageView? = null
    private var closedHandleStatement: String? = null
    private var openedHandleStatement: String? = null

    internal var sliderHandlePercentage = 10 // default 10

    private var slidingListener: ASSlidingLayerListener? = null


    var OPEN_STATE = 1
    var CLOSE_STATE = 0

    internal var stickType = STICK_TO_NONE


    fun setSlidingListener(slidingListener: ASSlidingLayerListener) {
        this.slidingListener = slidingListener
    }

    fun setClosedHandleStatement(closedHandleStatement: String) {
        this.closedHandleStatement = closedHandleStatement
        if (sliderPanel!!.shiftPositionHandler!!.dimensionState == 0 && handleText != null)
            handleText!!.text = closedHandleStatement
    }

    fun setOpenedHandleStatement(openedHandleStatement: String) {
        this.openedHandleStatement = openedHandleStatement
        if (sliderPanel!!.shiftPositionHandler!!.dimensionState == 1 && handleText != null)
            handleText!!.text = openedHandleStatement
    }

    fun openLayer(animation: Boolean) {
        if (sliderPanel!!.shiftPositionHandler!!.dimensionState == CLOSE_STATE)
            sliderPanel!!.shiftPositionHandler!!.changeStateToDimension(OPEN_STATE, animation)
    }

    fun closeLayer(animation: Boolean) {
        if (sliderPanel!!.shiftPositionHandler!!.dimensionState == OPEN_STATE)
            sliderPanel!!.shiftPositionHandler!!.changeStateToDimension(CLOSE_STATE, animation)
    }


    interface ASSlidingLayerListener {
        fun slidingState(state: Int)
    }

    companion object {

        val STICK_TO_BOTTOM = 1
        val STICK_TO_UP = 2
        val STICK_TO_LEFT = 3
        val STICK_TO_RIGHT = 4
        val STICK_TO_NONE = 5
        val STICK_TO_CUSTOM = 6


        fun create(parentCanvas: CanvasSection, firstLocation: Rect, secondLocation: Rect, sliderHandlePercentage: Int, stickType: Int, noScroll: Boolean): ASSlidingLayer {
            val minSectionX: Int
            val maxSectionX: Int
            val minSectionY: Int
            val maxSectionY: Int
            var handleLeft = 0
            var handleTop = 0
            var handleWidth = 0
            var handleHeight = 0
            var contentLeft = 0
            var contentTop = 0
            var contentWidth = 0
            var contentHeight = 0

            val slidingLayer = ASSlidingLayer()
            slidingLayer.sliderHandlePercentage = sliderHandlePercentage
            slidingLayer.sliderPanel = parentCanvas.addSubSectionWithFrame("sliderPanel",
                    firstLocation.left, firstLocation.top, firstLocation.width(),
                    firstLocation.height(), true)
            slidingLayer.sliderPanel!!.setOnClickListener {
                //to isolate touch to not propagate behind this sliding
            }
            slidingLayer.sliderPanel!!.shiftPositionHandler!!.addRectToDimensionState(firstLocation.left,
                    firstLocation.top, firstLocation.width(),
                    firstLocation.height())
            slidingLayer.sliderPanel!!.shiftPositionHandler!!.addRectToDimensionState(secondLocation.left,
                    secondLocation.top, secondLocation.width(),
                    secondLocation.height())

            if (firstLocation.left <= secondLocation.left) {
                minSectionX = firstLocation.left
                maxSectionX = secondLocation.left
            } else {
                minSectionX = secondLocation.left
                maxSectionX = firstLocation.left
            }

            if (firstLocation.top <= secondLocation.top) {
                minSectionY = firstLocation.top
                maxSectionY = secondLocation.top
            } else {
                minSectionY = secondLocation.top
                maxSectionY = firstLocation.top
            }

            if (stickType != STICK_TO_CUSTOM) {
                slidingLayer.sliderPanel!!.shiftPositionHandler!!.setMinMaxLocationXY(minSectionX, maxSectionX, minSectionY, maxSectionY)
            }
            slidingLayer.sliderPanel!!.setBackgroundColor(Color.argb(200, 0, 0, 0))
            //slidingLayer.sliderPanel.unConsumeEvent();
            slidingLayer.sliderPanel!!.shiftPositionHandler!!.dimensionStateListener = object : DimensionStateListener {
                override fun rectForCurrentDimensionState(
                        currentRectState: Rect): Boolean {
                    return false
                }

                override fun indexForCurrentDimensionState(
                        currentIndexState: Int): Boolean {
                    if (currentIndexState == slidingLayer.OPEN_STATE) {
                        if (slidingLayer.openedHandleStatement != null)
                            slidingLayer.handleText!!.text = slidingLayer.openedHandleStatement
                        slidingLayer.sliderPanel!!.consumeEvent()
                        slidingLayer.handleInfoImage!!.setImageResource(R.drawable.arrow_down_left)
                    } else if (currentIndexState == slidingLayer.CLOSE_STATE) {
                        if (slidingLayer.closedHandleStatement != null)
                            slidingLayer.handleText!!.text = slidingLayer.closedHandleStatement
                        slidingLayer.sliderPanel!!.unConsumeEvent()
                        slidingLayer.handleInfoImage!!.setImageResource(R.drawable.arrow_up_right)
                    }
                    if (slidingLayer.slidingListener != null) {
                        slidingLayer.slidingListener!!.slidingState(currentIndexState)
                    }
                    return false
                }
            }

            when (stickType) {
                STICK_TO_BOTTOM, STICK_TO_CUSTOM -> {
                    handleLeft = 0
                    handleTop = 0
                    handleWidth = 100
                    handleHeight = slidingLayer.sliderHandlePercentage
                    contentLeft = 0
                    contentTop = slidingLayer.sliderHandlePercentage
                    contentWidth = 100
                    contentHeight = 100 - slidingLayer.sliderHandlePercentage
                }
                STICK_TO_UP -> {
                    handleLeft = 0
                    handleTop = 100 - slidingLayer.sliderHandlePercentage
                    handleWidth = 100
                    handleHeight = slidingLayer.sliderHandlePercentage
                    contentLeft = 0
                    contentTop = 0
                    contentWidth = 100
                    contentHeight = 100 - slidingLayer.sliderHandlePercentage
                }
                STICK_TO_LEFT -> {
                    handleLeft = 100 - slidingLayer.sliderHandlePercentage
                    handleTop = 0
                    handleWidth = slidingLayer.sliderHandlePercentage
                    handleHeight = 100
                    contentLeft = 0
                    contentTop = 0
                    contentWidth = 100 - slidingLayer.sliderHandlePercentage
                    contentHeight = 100
                }
                STICK_TO_RIGHT -> {
                    handleLeft = 0
                    handleTop = 0
                    handleWidth = slidingLayer.sliderHandlePercentage
                    handleHeight = 100
                    contentLeft = slidingLayer.sliderHandlePercentage
                    contentTop = 0
                    contentWidth = 100 - slidingLayer.sliderHandlePercentage
                    contentHeight = 100
                }

                else -> {
                }
            }
            slidingLayer.sliderHandle = slidingLayer.sliderPanel!!.addSubSectionWithFrame("sliderHandle", handleLeft, handleTop, handleWidth, handleHeight, true)
            slidingLayer.sliderContentPanel = slidingLayer.sliderPanel!!.addSubSectionWithFrame("sliderContentPanel", contentLeft, contentTop, contentWidth, contentHeight, noScroll)

            slidingLayer.sliderContentPanel!!.shiftPositionHandler!!.addRectToDimensionState(contentLeft, contentTop, contentWidth, contentHeight)

            slidingLayer.sliderHandle!!.setBackgroundColor(Color.argb(200, 0, 255, 0))
            /*
		slidingLayer.sliderHandle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// need implement this to activate click and sliding
				// together, any other ways?
			}
		});
		*/
            when (stickType) {
                STICK_TO_CUSTOM, STICK_TO_BOTTOM, STICK_TO_UP -> {
                    slidingLayer.handleText = TextView(parentCanvas.context)
                    slidingLayer.handleText!!.gravity = Gravity.CENTER
                }
                STICK_TO_LEFT -> {
                    slidingLayer.handleText = VerticalTextView(parentCanvas.context)
                    slidingLayer.handleText!!.gravity = Gravity.CENTER_VERTICAL or Gravity.CENTER_HORIZONTAL or Gravity.TOP
                }
                STICK_TO_RIGHT -> {
                    slidingLayer.handleText = VerticalTextView(parentCanvas.context)
                    slidingLayer.handleText!!.gravity = Gravity.CENTER_VERTICAL or Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM
                }
                else -> {
                }
            }

            if (slidingLayer.closedHandleStatement != null)
                slidingLayer.handleText!!.text = slidingLayer.closedHandleStatement
            slidingLayer.handleText!!.setTextColor(Color.BLACK)
            slidingLayer.handleText!!.setTypeface(null, Typeface.BOLD)
            slidingLayer.sliderHandle!!.addViewWithFrame(slidingLayer.handleText, 0, 0, 100, 100)

            //set information image
            slidingLayer.handleInfoImage = ASImageView(parentCanvas.context)
            slidingLayer.handleInfoImage!!.scaleType = ScaleType.FIT_END
            slidingLayer.handleInfoImage!!.setImageResource(R.drawable.arrow_up_right)
            slidingLayer.sliderHandle!!.addViewWithFrame(slidingLayer.handleInfoImage, 0, 0, 100, 100)

            slidingLayer.stickType = stickType
            slidingLayer.sliderHandle!!.setASGestureListener(object : ASGestureListener {

                override val isClickEnabled: Boolean
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
                    slidingLayer.sliderPanel!!.shiftPositionHandler!!.changeStateToNearestDimension(true)
                    return true
                }

                override fun downEventOccured(x: Float, y: Float): Boolean {
                    return true
                }

                override fun deltaMoveInsideParameter(swipeType: Int, x: Float,
                                                      y: Float, dx: Float, dy: Float, fromDownDX: Float, fromDownDY: Float): Boolean {
                    when (slidingLayer.stickType) {
                        STICK_TO_BOTTOM, STICK_TO_UP -> slidingLayer.sliderPanel!!.shiftPositionHandler!!.shiftViewWithDelta(0f, dy, 0f, fromDownDY)
                        STICK_TO_LEFT, STICK_TO_RIGHT -> slidingLayer.sliderPanel!!.shiftPositionHandler!!.shiftViewWithDelta(dx, 0f, fromDownDX, 0f)
                        STICK_TO_CUSTOM -> slidingLayer.sliderPanel!!.shiftPositionHandler!!.shiftViewWithDelta(dx, dy, fromDownDX, fromDownDY)
                        else -> {
                        }
                    }
                    return true
                }

                override fun deltaMoveOutsideParameter(swipeType: Int, x: Float,
                                                       y: Float, dx: Float, dy: Float, fromDownDX: Float, fromDownDY: Float): Boolean {
                    when (slidingLayer.stickType) {
                        STICK_TO_BOTTOM, STICK_TO_UP -> slidingLayer.sliderPanel!!.shiftPositionHandler!!.shiftViewWithDelta(0f, dy, 0f, fromDownDY)
                        STICK_TO_LEFT, STICK_TO_RIGHT -> slidingLayer.sliderPanel!!.shiftPositionHandler!!.shiftViewWithDelta(dx, 0f, fromDownDX, 0f)
                        STICK_TO_CUSTOM -> slidingLayer.sliderPanel!!.shiftPositionHandler!!.shiftViewWithDelta(dx, dy, fromDownDX, fromDownDY)
                        else -> {
                        }
                    }
                    return true
                }

                override fun movingSpeed(xSpeed: Float, ySpeed: Float): Boolean {
                    // TODO Auto-generated method stub
                    return false
                }

                override fun cancelEventOccured(x: Float, y: Float): Boolean {
                    // TODO Auto-generated method stub
                    return true
                }

                override fun clickEventOccured(): Boolean {
                    slidingLayer.sliderPanel!!.shiftPositionHandler!!.changeStateToNextDimension(true)
                    return true
                }

                override fun longClickEventOccured(): Boolean {
                    DebugUtils.logE("long Click", "OK")
                    return true
                }

                override fun doubleTapEventOccured(): Boolean {
                    DebugUtils.logE("DoubleTap", "OK")
                    return true
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


            slidingLayer.sliderPanel!!.setMaintainRatio(false)
            slidingLayer.sliderHandle!!.setMaintainRatio(false)
            slidingLayer.sliderContentPanel!!.setMaintainRatio(false)
            return slidingLayer

        }

        fun create(parentCanvas: CanvasSection,
                   stickType: Int, width: Int, height: Int, sliderHandlePercentage: Int, noScroll: Boolean): ASSlidingLayer {
            var firstLocation: Rect? = null
            var secondLocation: Rect? = null
            val xCenter = (100 - width) / 2
            val yCenter = (100 - height) / 2
            when (stickType) {
                STICK_TO_BOTTOM -> {
                    firstLocation = Rect(xCenter, 100 - sliderHandlePercentage, xCenter + width, 100 - sliderHandlePercentage + height)
                    secondLocation = Rect(0, 0, 0 + width, 0 + height)
                }
                STICK_TO_UP -> {
                    firstLocation = Rect(xCenter, sliderHandlePercentage - height, xCenter + width, sliderHandlePercentage)
                    secondLocation = Rect(xCenter, 0, xCenter + width, 0 + height)
                }
                STICK_TO_LEFT -> {
                    firstLocation = Rect(sliderHandlePercentage - width, yCenter, sliderHandlePercentage, yCenter + height)
                    secondLocation = Rect(0, yCenter, 0 + width, yCenter + height)
                }
                STICK_TO_RIGHT -> {
                    firstLocation = Rect(100 - sliderHandlePercentage, yCenter, 100 - sliderHandlePercentage + width, yCenter + height)
                    secondLocation = Rect(0, yCenter, 0 + width, yCenter + height)
                }
                else -> {
                }
            }
            return create(parentCanvas, firstLocation!!, secondLocation!!, sliderHandlePercentage, stickType, noScroll)

        }
    }
}
