package com.zaitunlabs.zlcore.views

import android.graphics.Rect
import android.view.ViewGroup
import android.widget.ImageView.ScaleType

class ASShiftableImageView {
    internal var section: CanvasSection? = null
    var imageView: ASImageView? = null
        internal set
    private var isSticky = false
    private var isLocked = false

    fun setPositionLocked(isLocked: Boolean) {
        this.isLocked = isLocked
    }

    fun setPositionToOrigin(animate: Boolean) {
        section!!.shiftPositionHandler!!.changeStateToDimension(0, animate)
    }

    fun setPositionToNearest(animate: Boolean) {
        section!!.shiftPositionHandler!!.changeStateToNearestDimension(animate)
    }

    fun setPositionToRect(dimension: Rect, animate: Boolean) {
        section!!.shiftPositionHandler!!.changeStateToCustomDimension(dimension, 10, animate)
    }

    companion object {

        @JvmOverloads
        fun create(parent: ViewGroup, left: Int, top: Int, width: Int, height: Int, isSticky: Boolean = false, isLocked: Boolean = false): ASShiftableImageView {
            val shiftableImageView = ASShiftableImageView()
            if (parent is CanvasLayout)
                shiftableImageView.section = parent.createNewSectionWithFrame(left, top, width, height, true)
            else if (parent is CanvasSection)
                shiftableImageView.section = parent.addSubSectionWithFrame(left, top, width, height, true)
            shiftableImageView.imageView = ASImageView(parent.context)
            shiftableImageView.imageView!!.scaleType = ScaleType.FIT_XY
            shiftableImageView.imageView!!.adjustViewBounds = true
            shiftableImageView.section!!.addViewWithFrame(shiftableImageView.imageView, 0, 0, 100, 100)

            shiftableImageView.isSticky = isSticky
            shiftableImageView.isLocked = isLocked

            shiftableImageView.section!!.shiftPositionHandler!!.addRectToDimensionState(left, top, width, height)
            shiftableImageView.section!!.shiftPositionHandler!!.addRectToDimensionState(0, 0, width, height)
            shiftableImageView.section!!.shiftPositionHandler!!.addRectToDimensionState(0, 100 - height, width, height)
            shiftableImageView.section!!.shiftPositionHandler!!.addRectToDimensionState(100 - width, 100 - height, width, height)
            shiftableImageView.section!!.shiftPositionHandler!!.addRectToDimensionState(100 - width, 0, width, height)


            shiftableImageView.imageView!!.setASGestureListener(object : ASGestureListener {

                override// TODO Auto-generated method stub
                val isClickEnabled: Boolean
                    get() = false

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
                    if (shiftableImageView.isSticky) {
                        shiftableImageView.section!!.shiftPositionHandler!!.changeStateToNearestDimension(true)
                    }
                    return true
                }

                override fun downEventOccured(x: Float, y: Float): Boolean {
                    return true
                }

                override fun deltaMoveOutsideParameter(swipeType: Int, x: Float,
                                                       y: Float, dx: Float, dy: Float, fromDownDX: Float, fromDownDY: Float): Boolean {
                    if (!shiftableImageView.isLocked) {
                        shiftableImageView.section!!.shiftPositionHandler!!.shiftViewWithDelta(dx, dy, fromDownDX, fromDownDY)
                    }
                    return true
                }

                override fun deltaMoveInsideParameter(swipeType: Int, x: Float,
                                                      y: Float, dx: Float, dy: Float, fromDownDX: Float, fromDownDY: Float): Boolean {
                    if (!shiftableImageView.isLocked) {
                        shiftableImageView.section!!.shiftPositionHandler!!.shiftViewWithDelta(dx, dy, fromDownDX, fromDownDY)
                    }
                    return true
                }

                override fun movingSpeed(xSpeed: Float, ySpeed: Float): Boolean {
                    // TODO Auto-generated method stub
                    return false
                }

                override fun cancelEventOccured(x: Float, y: Float): Boolean {
                    // TODO Auto-generated method stub
                    return false
                }

                override fun clickEventOccured(): Boolean {
                    // TODO Auto-generated method stub
                    return false
                }

                override fun longClickEventOccured(): Boolean {
                    // TODO Auto-generated method stub
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
            return shiftableImageView
        }
    }
}
