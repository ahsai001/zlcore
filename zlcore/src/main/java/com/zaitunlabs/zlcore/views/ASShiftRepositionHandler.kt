package com.zaitunlabs.zlcore.views

import android.content.Context
import android.graphics.Rect
import android.os.Build
import android.view.View
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.TranslateAnimation
import android.widget.RelativeLayout.LayoutParams


import com.zaitunlabs.zlcore.utils.DebugUtils

import java.util.ArrayList

abstract class ASShiftRepositionHandler(context: Context, view: View) {
    private val context: Context? = null
    private val view: View? = null
    private var dimensionList: ArrayList<Rect>? = null
    private var dimensionVisiblityInCircleList: ArrayList<Boolean>? = null
    var dimensionState = 0
        private set//pointer yg menunjuk ke dimensionList, default awal index 0

    private val SDK_INT_LIMIT_USE_TRANSLATE_ANIMATION = 40

    var dimensionStateListener: DimensionStateListener? = null

    private var dimensionStateListeners: ArrayList<DimensionStateListener>? = null

    private var maxLocationX = 0
    private var minLocationX = 0
    private var maxLocationY = 0
    private var minLocationY = 0
    private var limitLocationSet = false

    /*
	 * input/common type format is same like input in addRect example : % in canvas section or pixel or any other format
	 * pixel/layoutparam type format like in deltamove
	 * */

    //must be implemented
    protected abstract val xNow: Int //common type
    protected abstract val yNow: Int //common type
    abstract val parentWidth: Int //param type
    abstract val parentHeight: Int //param type


    fun addDimensionStateListener(dimensionStateListener: DimensionStateListener) {
        if (this.dimensionStateListeners == null) this.dimensionStateListeners = ArrayList()
        this.dimensionStateListeners!!.add(dimensionStateListener)
    }

    fun removeDimensionStateListener(dimensionStateListener: DimensionStateListener) {
        this.dimensionStateListeners!!.remove(dimensionStateListener)
    }

    init {
        this.context = context
        this.view = view
    }

    fun clearRectInDimensionState(dimensionStateIndex: Int): Rect? {
        return if (dimensionList != null && dimensionList!!.size > dimensionStateIndex && dimensionStateIndex >= 0) {
            dimensionList!!.removeAt(dimensionStateIndex)
        } else null
    }

    @JvmOverloads
    fun addRectToDimensionState(x: Int, y: Int, dx: Int, dy: Int, isVisibleInCircle: Boolean = true): Rect {
        var dx = dx
        var dy = dy
        //width = (parentHeight * height) / parentWidth;

        if (dx == CanvasSection.SAME_AS_OTHER_SIDE) {
            dx = parentHeight * dy / parentWidth
        } else if (dy == CanvasSection.SAME_AS_OTHER_SIDE) {
            dy = parentWidth * dx / parentHeight
        }

        val p = Rect(x, y, x + dx, y + dy)
        if (dimensionList == null) dimensionList = ArrayList()
        dimensionList!!.add(p)
        if (dimensionVisiblityInCircleList == null) dimensionVisiblityInCircleList = ArrayList()
        dimensionVisiblityInCircleList!!.add(isVisibleInCircle)
        return p
    }


    fun changeVisibilityForDimensionState(dimensionStateIndex: Int, isvisible: Boolean): Boolean {
        if (dimensionVisiblityInCircleList != null && dimensionVisiblityInCircleList!!.size > dimensionStateIndex && dimensionStateIndex >= 0) {
            dimensionVisiblityInCircleList!![dimensionStateIndex] = isvisible
            return true
        }
        return false
    }

    fun changeStateToNextDimension(animation: Boolean): Rect? {
        var p: Rect? = null
        if (dimensionList != null && dimensionList!!.size > 0) {
            var searchNextState = true
            do {
                dimensionState++
                if (dimensionState >= dimensionList!!.size) dimensionState = 0
                if (dimensionVisiblityInCircleList!![dimensionState] == true) {
                    searchNextState = false
                }
            } while (searchNextState)
            p = dimensionList!![dimensionState]
            resizeMoveViewWithFrame(p.left, p.top, p.width(), p.height(), animation, dimensionState)
        }
        return p
    }

    fun changeStateToNearestDimension(animation: Boolean): Int {
        var p: Rect? = null
        if (dimensionList != null && dimensionList!!.size > 0) {
            var lastNearestIndex = -1
            var lastNearestDistance = -1.0
            for (i in dimensionList!!.indices) {
                if (dimensionVisiblityInCircleList!![i] == false)
                    continue
                p = dimensionList!![i]
                val distance = Math.sqrt(Math.pow((p.left - xNow).toDouble(), 2.0) + Math.pow((p.top - yNow).toDouble(), 2.0))
                if (distance <= lastNearestDistance || lastNearestIndex == -1) {
                    lastNearestIndex = i
                    lastNearestDistance = distance
                }
            }
            this.dimensionState = lastNearestIndex
            p = dimensionList!![this.dimensionState]
            resizeMoveViewWithFrame(p.left, p.top, p.width(), p.height(), animation, this.dimensionState)
        }
        return this.dimensionState
    }

    fun changeStateToNearestDimensionFromPoint(animation: Boolean, x: Int, y: Int): Int {
        var p: Rect? = null
        if (dimensionList != null && dimensionList!!.size > 0) {
            var lastNearestIndex = -1
            var lastNearestDistance = -1.0
            for (i in dimensionList!!.indices) {
                if (dimensionVisiblityInCircleList!![i] == false)
                    continue
                p = dimensionList!![i]
                val distance = Math.sqrt(Math.pow((p.left - x).toDouble(), 2.0) + Math.pow((p.top - y).toDouble(), 2.0))
                if (distance <= lastNearestDistance || lastNearestIndex == -1) {
                    lastNearestIndex = i
                    lastNearestDistance = distance
                }
            }
            this.dimensionState = lastNearestIndex
            p = dimensionList!![this.dimensionState]
            resizeMoveViewWithFrame(p.left, p.top, p.width(), p.height(), animation, this.dimensionState)
        }
        return this.dimensionState
    }

    fun changeStateToDimension(dimensionStateIndex: Int, animation: Boolean): Rect? {
        var p: Rect? = null
        if (dimensionList != null && dimensionList!!.size > dimensionStateIndex && dimensionStateIndex >= 0) {
            if (dimensionVisiblityInCircleList!![dimensionStateIndex] == true) {
                this.dimensionState = dimensionStateIndex
            }
            p = dimensionList!![dimensionStateIndex]
            resizeMoveViewWithFrame(p.left, p.top, p.width(), p.height(), animation, dimensionStateIndex)
        }
        return p
    }

    fun changeStateToCustomDimension(customDimension: Rect, dimensionStateIndex: Int, animation: Boolean): Rect {
        val p = Rect(customDimension)
        var dx = p.width()
        var dy = p.height()
        if (p.right == CanvasSection.SAME_AS_OTHER_SIDE) {
            dy = p.height()
            dx = parentHeight * dy / parentWidth
        } else if (p.bottom == CanvasSection.SAME_AS_OTHER_SIDE) {
            dx = p.width()
            dy = parentWidth * dx / parentHeight
        }

        resizeMoveViewWithFrame(p.left, p.top, dx, dy, animation, dimensionStateIndex)
        return p
    }

    fun changeStateToLastDimension(animation: Boolean): Rect {
        val p = dimensionList!![this.dimensionState]
        resizeMoveViewWithFrame(p.left, p.top, p.width(), p.height(), animation, this.dimensionState)
        return p
    }

    fun setMinMaxLocationXY(minLocationX: Int, maxLocationX: Int, minLocationY: Int, maxLocationY: Int) {
        if (maxLocationX != 0 || minLocationX != 0 || maxLocationY != 0 || minLocationY != 0) {
            limitLocationSet = true
        } else {
            limitLocationSet = false
        }
        this.minLocationX = minLocationX
        this.maxLocationX = maxLocationX
        this.minLocationY = minLocationY
        this.maxLocationY = maxLocationY
    }

    protected fun isPositionPermitted(X: Int, Y: Int): Boolean {
        return !limitLocationSet || limitLocationSet && X <= maxLocationX && X >= minLocationX && Y <= maxLocationY && Y >= minLocationY
    }

    fun shiftViewWithDelta(dx: Float, dy: Float, fromDownDX: Float, fromDownDY: Float) {
        val param = view!!.layoutParams as LayoutParams


        //DebugUtils.logW("JEJAK", ">>>>>>>>>>>cc 1 "+param.leftMargin+" "+param.topMargin);

        val x: Int
        val y: Int
        if (Build.VERSION.SDK_INT >= SDK_INT_LIMIT_USE_TRANSLATE_ANIMATION) {
            x = param.leftMargin + fromDownDX.toInt()
            y = param.topMargin + fromDownDY.toInt()
        } else {
            x = param.leftMargin + dx.toInt()
            y = param.topMargin + dy.toInt()
        }


        val commonTypeValue = convertRectFromParamToCommonType(Rect(x, y, x + param.width, y + param.height))



        if (isPositionPermitted(commonTypeValue.left, commonTypeValue.top)) {
            //update section coordinate
            updatePosition(commonTypeValue.left, commonTypeValue.top)

            DebugUtils.logD("JEJAK", ">>>>>>>>>>> " + (view as CanvasSection).sectionName + " new xy " + x + " " + y)
            DebugUtils.logD("JEJAK", ">>>>>>>>>>> " + view.sectionName + " new common xy " + commonTypeValue.left + " " + commonTypeValue.top)

            if (Build.VERSION.SDK_INT >= SDK_INT_LIMIT_USE_TRANSLATE_ANIMATION) {
                view.translationX = fromDownDX
                view.translationY = fromDownDY
                DebugUtils.logD("JEJAK", ">>>>>>>>>>> " + view.sectionName + " setTranslation")
            } else {
                param.leftMargin = x
                param.topMargin = y
                view.layoutParams = param
                DebugUtils.logD("JEJAK", ">>>>>>>>>>> " + view.sectionName + " setLayoutParams")
            }
            postStepUpdate(view, param.width, param.height, true)
        }
    }

    fun upEvent(x: Float, y: Float) {
        //view.setDrawingCacheEnabled(false);
        if (Build.VERSION.SDK_INT >= SDK_INT_LIMIT_USE_TRANSLATE_ANIMATION) {
            //RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams)view.getLayoutParams();

            //param.leftMargin = (int) x;
            //param.topMargin = (int) y;
            //view.setLayoutParams(param);
        }
    }

    fun downEvent(x: Float, y: Float) {
        //view.setDrawingCacheEnabled(true);
    }

    protected fun resizeMoveViewWithFrame(left: Int, top: Int, width: Int,
                                          height: Int, animation: Boolean, destinationIndex: Int) {
        val paramTypeValue: Rect
        var oldParamTypeValue: Rect? = null
        if (Build.VERSION.SDK_INT >= SDK_INT_LIMIT_USE_TRANSLATE_ANIMATION) {
            val oldX = (view as CanvasSection).sectionX
            val oldY = view.sectionY
            val oldWidth = view.sectionWidth
            val oldHeight = view.sectionHeight

            oldParamTypeValue = convertRectFromCommonToParamType(Rect(oldX, oldY, oldX + oldWidth, oldY + oldHeight))
            DebugUtils.logD("JEJAK", ">>>>>>>>>>> " + view.sectionName + " get oldParamTypeValue")
        }


        paramTypeValue = convertRectFromCommonToParamType(Rect(left, top, left + width, top + height))


        DebugUtils.logD("JEJAK", ">>>>>>>>>>>cc " + (view as CanvasSection).sectionName + " resizemove common xy " + paramTypeValue.left + " " + paramTypeValue.top)
        //DebugUtils.logW("JEJAK", ">>>>>>>>>>>cc 3 old xy "+oldParamTypeValue.left+" "+oldParamTypeValue.top);

        DebugUtils.logD("JEJAK", ">>>>>>>>>>>cc " + view.sectionName + " resizemove update " + left + " " + top)

        //update section coordinate
        updatePosition(left, top)
        updateDimension(width, height)

        //view.setDrawingCacheEnabled(true);

        if (animation) {
            var anim: Animation? = null
            if (Build.VERSION.SDK_INT >= SDK_INT_LIMIT_USE_TRANSLATE_ANIMATION) {
                val params = view.layoutParams as LayoutParams
                //DebugUtils.logW("JEJAK", ">>>>>>>>>>> 2 "+params.leftMargin+" "+params.topMargin);
                DebugUtils.logD("JEJAK", ">>>>>>>>>>> " + view.sectionName + " TranslateAnimation")
                anim = TranslateAnimation((oldParamTypeValue!!.left - params.leftMargin).toFloat(), (paramTypeValue.left - params.leftMargin).toFloat(), (oldParamTypeValue.top - params.topMargin).toFloat(), (paramTypeValue.top - params.topMargin).toFloat())
            } else {
                DebugUtils.logD("JEJAK", ">>>>>>>>>>> " + view.sectionName + " ResizeMoveAnimation")
                anim = ResizeMoveAnimation(view, paramTypeValue.left.toFloat(), paramTypeValue.top.toFloat(), paramTypeValue.width().toFloat(), paramTypeValue.height().toFloat())
            }

            anim.setAnimationListener(object : AnimationListener {
                override fun onAnimationStart(animation: Animation) {}

                override fun onAnimationRepeat(animation: Animation) {}

                override fun onAnimationEnd(animation: Animation) {
                    //view.setDrawingCacheEnabled(false);
                    //if(Build.VERSION.SDK_INT >= SDK_INT_LIMIT_USE_TRANSLATE_ANIMATION){
                    val param = view.layoutParams as LayoutParams

                    param.height = paramTypeValue.height()
                    param.width = paramTypeValue.width()
                    param.leftMargin = paramTypeValue.left
                    param.topMargin = paramTypeValue.top
                    view.layoutParams = param
                    DebugUtils.logD("JEJAK", ">>>>>>>>>>> " + view.sectionName + " onAnimationEnd")
                    postStepUpdate(view, param.width, param.height, false)
                    //}
                    if (dimensionStateListener != null) {
                        dimensionStateListener!!.indexForCurrentDimensionState(destinationIndex)
                        dimensionStateListener!!.rectForCurrentDimensionState(Rect(left, top, left + width, top + height))
                    }

                    if (dimensionStateListeners != null) {
                        for (i in dimensionStateListeners!!.indices) {
                            dimensionStateListeners!![i].indexForCurrentDimensionState(destinationIndex)
                            dimensionStateListeners!![i].rectForCurrentDimensionState(Rect(left, top, left + width, top + height))
                        }
                    }
                }
            })

            if (Build.VERSION.SDK_INT >= SDK_INT_LIMIT_USE_TRANSLATE_ANIMATION) {
                anim.duration = 400
                view.startAnimation(anim)
                DebugUtils.logD("JEJAK", ">>>>>>>>>>> " + view.sectionName + " TranslateAnimation 2")
            } else {
                (anim as ResizeMoveAnimation).animListener = ResizeMoveAnimation.ASAnimListener { width, height ->
                    DebugUtils.logW("JEJAK", ">>>>>>>>>>>")
                    postStepUpdate(view, width.toInt(), height.toInt(), true)
                }
                anim.startNow()
                DebugUtils.logD("JEJAK", ">>>>>>>>>>> " + view.sectionName + " ResizeMoveAnimation 2")
            }
        } else {
            //view.setDrawingCacheEnabled(false);
            val param = view.layoutParams as LayoutParams
            param.height = paramTypeValue.height()
            param.width = paramTypeValue.width()
            param.leftMargin = paramTypeValue.left
            param.topMargin = paramTypeValue.top
            view.layoutParams = param
            postStepUpdate(view, param.width, param.height, false)
            if (dimensionStateListener != null) {
                dimensionStateListener!!.indexForCurrentDimensionState(destinationIndex)
                dimensionStateListener!!.rectForCurrentDimensionState(Rect(left, top, left + width, top + height))
            }

            if (dimensionStateListeners != null) {
                for (i in dimensionStateListeners!!.indices) {
                    dimensionStateListeners!![i].indexForCurrentDimensionState(destinationIndex)
                    dimensionStateListeners!![i].rectForCurrentDimensionState(Rect(left, top, left + width, top + height))
                }
            }
        }
    }

    protected abstract fun updateDimension(width: Int, height: Int)  //common type
    protected abstract fun updatePosition(x: Int, y: Int)  //common type
    protected abstract fun postStepUpdate(view: View?, viewWidth: Int, viewHeight: Int, isInRepeatState: Boolean)  //layoutparam type
    protected abstract fun convertRectFromCommonToParamType(commonTypeValue: Rect): Rect
    protected abstract fun convertRectFromParamToCommonType(paramTypeValue: Rect): Rect

}
