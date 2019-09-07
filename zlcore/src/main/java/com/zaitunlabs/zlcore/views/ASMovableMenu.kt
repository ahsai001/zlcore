package com.zaitunlabs.zlcore.views

import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.Rect
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout


import com.zaitunlabs.zlcore.R
import com.zaitunlabs.zlcore.core.CanvasActivity
import com.zaitunlabs.zlcore.utils.CommonUtils

import java.util.ArrayList
import java.util.HashMap

class ASMovableMenu {
    var menuSection: CanvasSection? = null
        internal set
    var handleSection: CanvasSection? = null
        internal set
    internal var canvasParent: ViewGroup? = null
    var isMenuOpened = false
        private set
    internal var handleWidth: Int = 0
    internal var handleHeight: Int = 0
    internal var menuWidth: Int = 0
    internal var menuHeight: Int = 0


    private var blockingLayer: View? = null
    private var context: Context? = null

    private var listener: ASMovableMenuListener? = null

    private var itemHandlerList: HashMap<Int, ArrayList<Any>>? = null
    private var currentSelectedItem: Int? = -1

    private var settingImage: ASImageView? = null

    private var settingText: ASTextView? = null


    private var openedIndexState = 100
    private var openedDefaultDimension: Rect? = null
    private var isMovable = true

    var viewList: ArrayList<View>? = null
        private set

    var rectList: ArrayList<Rect>? = null
        private set
    private var marginList: ArrayList<LayoutViewMargin>? = null
    fun setListener(listener: ASMovableMenuListener) {
        this.listener = listener
    }


    fun setOpenedIndexState(openedIndexState: Int) {
        this.openedIndexState = openedIndexState
    }

    fun setOpenedDefaultDimensionState(x: Int, y: Int, dx: Int, dy: Int) {
        var dx = dx
        var dy = dy
        if (dx == CanvasSection.SAME_AS_OTHER_SIDE) {
            dx = menuSection!!.shiftPositionHandler!!.parentHeight * dy / menuSection!!.shiftPositionHandler!!.parentWidth
        } else if (dy == CanvasSection.SAME_AS_OTHER_SIDE) {
            dy = menuSection!!.shiftPositionHandler!!.parentWidth * dx / menuSection!!.shiftPositionHandler!!.parentHeight
        }
        openedDefaultDimension = Rect(x, y, x + dx, y + dy)
    }

    fun ChangeStateToLastDimension() {
        handleSection!!.shiftPositionHandler!!.changeStateToDimension(lastDimensionState, false)
        menuSection!!.shiftPositionHandler!!.changeStateToDimension(lastDimensionState, false)
    }

    fun ChangeStateToNextDimension() {
        handleSection!!.shiftPositionHandler!!.changeStateToNextDimension(true)
        menuSection!!.shiftPositionHandler!!.changeStateToNextDimension(true)
    }


    fun setAppearDisappearPoint(position: Point?) {
        if (position == null) {
            if (!isMovable) {
                handleSection!!.shiftPositionHandler!!.clearRectInDimensionState(8)
                menuSection!!.shiftPositionHandler!!.clearRectInDimensionState(8)
                lastDimensionState = 0
                isMovable = true
            }
        } else {
            if (isMovable) {
                handleSection!!.shiftPositionHandler!!.addRectToDimensionState(position.x, position.y, 1, 1) //index 8
                menuSection!!.shiftPositionHandler!!.addRectToDimensionState(position.x, position.y, 1, 1) //index 8
                lastDimensionState = 8
                isMovable = false
            }
        }
    }

    fun openMenu(animation: Boolean) {
        if (listener != null) listener!!.menuWillBeOpened()

        if (!isMovable) {
            handleSection!!.visibility = View.VISIBLE
            menuSection!!.visibility = View.VISIBLE
        }

        handleSection!!.setBackgroundResource(R.drawable.rounded_corner_4)
        menuSection!!.setBackgroundResource(R.drawable.rounded_corner_3)

        blockingLayer!!.visibility = View.VISIBLE
        menuSection!!.visibility = View.VISIBLE

        viewList = ArrayList()
        rectList = ArrayList()
        marginList = ArrayList()

        (context as CanvasActivity).onCreateMovableMenu(this)

        var height = 0

        if (viewList != null && rectList != null) {
            if (rectList!!.size <= 0 || viewList!!.size > rectList!!.size) {
                //make menuSection as linear

                menuSection!!.sectionWidth = menuHeight // it's used for telling linearlayout the true width
                menuSection!!.setSectionAsLinearLayout(0, menuSection!!.getPercentageEqualHeightPixelWithPercentageFrom(canvasParent, handleHeight), LinearLayout.VERTICAL)

                //menuSection.linearLayout.setBackgroundColor(Color.GREEN);

                menuSection!!.sectionWidth = handleHeight
                for (i in viewList!!.indices) {
                    menuSection!!.addViewInLinearLayout(viewList!![i], marginList!![i])
                }
                height = CommonUtils.getViewDimension(menuSection!!.linearLayout!!).height() + 10
            } else {
                menuSection!!.sectionWidth = menuHeight // it's used for telling linearlayout the true width
                for (i in viewList!!.indices) {
                    menuSection!!.addViewWithFrame(viewList!![i], rectList!![i].left, rectList!![i].top, rectList!![i].width(), rectList!![i].height())
                }
                menuSection!!.sectionWidth = handleHeight
                height = CommonUtils.getViewDimension(menuSection!!.sectionLayout!!).height() + 10
            }
            height = CommonUtils.getPercentHeightFromPixel(context, height.toFloat())
            val x = Rect(Math.round(((100 - menuWidth) / 2).toFloat()), Math.round(((100 - menuHeight) / 2).toFloat()), Math.round(((100 - menuWidth) / 2 + menuWidth).toFloat()), Math.round(((100 - menuHeight) / 2 + height + handleHeight).toFloat()))
            if (height + handleHeight < menuHeight) {
                menuSection!!.shiftPositionHandler!!.changeStateToCustomDimension(x, openedIndexState, animation)
            } else {
                menuSection!!.shiftPositionHandler!!.changeStateToCustomDimension(openedDefaultDimension, openedIndexState, animation)
            }
        } else {
            menuSection!!.shiftPositionHandler!!.changeStateToCustomDimension(openedDefaultDimension, openedIndexState, animation)
        }

        viewList = null
        rectList = null
        marginList = null


        //int x = (100-menuWidth)/2+menuWidth-handleWidth;
        val x = Math.round(((100 - menuWidth) / 2).toFloat())
        val y = Math.round(((100 - menuHeight) / 2).toFloat())
        handleSection!!.shiftPositionHandler!!.changeStateToCustomDimension(Rect(x, y, x + menuWidth, y + handleHeight), -1, animation)
    }

    fun closeMenu(animation: Boolean) {
        if (listener != null) listener!!.menuWillBeClosed()

        blockingLayer!!.visibility = View.GONE
        settingText!!.visibility = View.GONE


        menuSection!!.removeAllViews()
        handleSection!!.shiftPositionHandler!!.changeStateToLastDimension(animation)
        menuSection!!.shiftPositionHandler!!.changeStateToLastDimension(animation)

    }

    @JvmOverloads
    fun addItemMenu(target: View, marginRect: LayoutViewMargin? = null) {
        addItemMenu(target, null, marginRect)
    }

    fun addItemMenu(target: View, listener: View.OnClickListener?, marginRect: LayoutViewMargin?) {
        var marginRect = marginRect
        if (marginRect == null) {
            marginRect = LayoutViewMargin(0, 0, 0, 0)
        }
        addItemMenu(target, listener, null, marginRect)
    }


    fun addItemMenu(target: View, listener: View.OnClickListener?, rect: Rect?, marginRect: LayoutViewMargin?) {
        var marginRect = marginRect
        viewList!!.add(target)
        if (marginRect == null) {
            marginRect = LayoutViewMargin(0, 0, 0, 0)
        }
        marginList!!.add(marginRect)
        if (rect != null)
            rectList!!.add(rect)
        if (listener != null) {
            val handler = ArrayList<Any>()
            handler.add(target)
            handler.add(listener)
            itemHandlerList!![target.hashCode()] = handler
            target.setOnClickListener { arg0 ->
                if (currentSelectedItem == -1) {
                    currentSelectedItem = arg0.hashCode()
                    closeMenu(true)
                }
            }
        }
    }

    fun getTextHeight(text: String): Int {
        return menuSection!!.getPercentageRelativeToHeight(CommonUtils.getFontHeight(text).toInt())
    }

    interface ASMovableMenuListener {
        fun menuWillBeOpened()
        fun menuHasBeenOpened()
        fun menuWillBeClosed()
        fun menuHasBeenClosed()
    }


    class LayoutViewMargin(left: Int, top: Int, right: Int, bottom: Int) {
        var leftMargin = 0
        var topMargin = 0
        var rightMargin = 0
        var bottomMargin = 0

        init {
            leftMargin = left
            topMargin = top
            rightMargin = right
            bottomMargin = bottom
        }

        fun setMargin(left: Int, top: Int, right: Int, bottom: Int) {
            leftMargin = left
            topMargin = top
            rightMargin = right
            bottomMargin = bottom
        }
    }

    companion object {


        private var lastDimensionState = 1
        private var closedSettingImage = 0
        private var openedSettingImage = 0

        fun setClosedSettingImage(closedSettingImage: Int) {
            ASMovableMenu.closedSettingImage = closedSettingImage
        }

        fun setOpenedSettingImage(openedSettingImage: Int) {
            ASMovableMenu.openedSettingImage = openedSettingImage
        }


        fun create(parent: ViewGroup, handleWidth: Int, handleHeight: Int, menuWidth: Int, menuHeight: Int): ASMovableMenu {
            var handleWidth = handleWidth
            var handleHeight = handleHeight
            var menuWidth = menuWidth
            var menuHeight = menuHeight
            val movableMenu = ASMovableMenu()

            movableMenu.canvasParent = parent
            if (handleWidth == CanvasSection.SAME_AS_OTHER_SIDE) {
                if (parent is CanvasSection) {
                    //parentWidth * width = parentHeight * height
                    handleWidth = (parent.parentHeight * handleHeight / parent.parentWidth).toInt()
                } else if (parent is CanvasLayout) {
                    //widthRatio * width = heightRatio * height
                    handleWidth = ((parent.heightRatio * handleHeight).toInt() / parent.widthRatio).toInt()
                }
            } else if (handleHeight == CanvasSection.SAME_AS_OTHER_SIDE) {
                if (parent is CanvasSection) {
                    //parentWidth * width = parentHeight * height
                    handleHeight = (parent.parentWidth * handleWidth / parent.parentHeight).toInt()
                } else if (parent is CanvasLayout) {
                    //widthRatio * width = heightRatio * height
                    handleHeight = ((parent.widthRatio * handleWidth).toInt() / parent.heightRatio).toInt()
                }
            }

            if (menuWidth == CanvasSection.SAME_AS_OTHER_SIDE) {
                if (parent is CanvasSection) {
                    //parentWidth * width = parentHeight * height
                    menuWidth = (parent.parentHeight * menuHeight / parent.parentWidth).toInt()
                } else if (parent is CanvasLayout) {
                    //widthRatio * width = heightRatio * height
                    menuWidth = ((parent.heightRatio * menuHeight).toInt() / parent.widthRatio).toInt()
                }
            } else if (menuHeight == CanvasSection.SAME_AS_OTHER_SIDE) {
                if (parent is CanvasSection) {
                    //parentWidth * width = parentHeight * height
                    menuHeight = (parent.parentWidth * menuWidth / parent.parentHeight).toInt()
                } else if (parent is CanvasLayout) {
                    //widthRatio * width = heightRatio * height
                    menuHeight = ((parent.widthRatio * menuWidth).toInt() / parent.heightRatio).toInt()
                }
            }


            movableMenu.handleWidth = handleWidth
            movableMenu.handleHeight = handleHeight
            movableMenu.menuWidth = menuWidth
            movableMenu.menuHeight = menuHeight

            movableMenu.context = parent.context

            //create blockinglayer with initial state is invisible
            if (parent is CanvasSection) {
                movableMenu.blockingLayer = View(parent.context)
                parent.addViewWithFrame(movableMenu.blockingLayer, 0, 0, 100, 100)
            } else if (parent is CanvasLayout) {
                movableMenu.blockingLayer = View(parent.context)
                parent.addViewWithFrame(movableMenu.blockingLayer, 0, 0, 100, 100)
            }
            movableMenu.blockingLayer!!.visibility = View.GONE
            movableMenu.blockingLayer!!.setBackgroundColor(Color.argb(200, 10, 10, 10))
            movableMenu.blockingLayer!!.setOnClickListener { movableMenu.closeMenu(true) }


            //create menusection first
            if (parent is CanvasSection)
                movableMenu.menuSection = parent.addSubSectionWithFrame("menuSection", 0, 0, handleWidth, handleHeight, false, true)
            else if (parent is CanvasLayout)
                movableMenu.menuSection = parent.createNewSectionWithFrame("menuSection", 0, 0, handleWidth, handleHeight, false, true)
            movableMenu.menuSection!!.shiftPositionHandler!!.addRectToDimensionState(0, 0, handleWidth, handleHeight)
            movableMenu.menuSection!!.shiftPositionHandler!!.addRectToDimensionState((100 - handleWidth) / 2, 0, handleWidth, handleHeight)
            movableMenu.menuSection!!.shiftPositionHandler!!.addRectToDimensionState(100 - handleWidth, 0, handleWidth, handleHeight)
            movableMenu.menuSection!!.shiftPositionHandler!!.addRectToDimensionState(0, (100 - handleHeight) / 2, handleWidth, handleHeight)
            movableMenu.menuSection!!.shiftPositionHandler!!.addRectToDimensionState(100 - handleWidth, (100 - handleHeight) / 2, handleWidth, handleHeight)
            movableMenu.menuSection!!.shiftPositionHandler!!.addRectToDimensionState(0, 100 - handleHeight, handleWidth, handleHeight)
            movableMenu.menuSection!!.shiftPositionHandler!!.addRectToDimensionState((100 - handleWidth) / 2, 100 - handleHeight, handleWidth, handleHeight)
            movableMenu.menuSection!!.shiftPositionHandler!!.addRectToDimensionState(100 - handleWidth, 100 - handleHeight, handleWidth, handleHeight)


            movableMenu.setOpenedDefaultDimensionState((100 - menuWidth) / 2, (100 - menuHeight) / 2, menuWidth, menuHeight)

            movableMenu.menuSection!!.shiftPositionHandler!!.dimensionStateListener = object : DimensionStateListener {
                override fun rectForCurrentDimensionState(currentRectState: Rect): Boolean {
                    return false
                }

                override fun indexForCurrentDimensionState(currentIndexState: Int): Boolean {
                    if (currentIndexState == -1)
                        return false
                    if (currentIndexState == movableMenu.openedIndexState) {
                        movableMenu.isMenuOpened = true

                        //change icon of menu handler
                        if (ASMovableMenu.openedSettingImage > 0) {
                            movableMenu.settingImage!!.setImageResource(ASMovableMenu.openedSettingImage)
                        } else {
                            //movableMenu.settingImage.setBackgroundColor(Color.WHITE);
                        }

                        movableMenu.settingText!!.visibility = View.VISIBLE

                        if (movableMenu.listener != null) movableMenu.listener!!.menuHasBeenOpened()

                    } else {
                        lastDimensionState = currentIndexState
                        movableMenu.isMenuOpened = false

                        //movableMenu.menuSection.setVisibility(View.GONE);
                        if (ASMovableMenu.closedSettingImage > 0) {
                            movableMenu.settingImage!!.setImageResource(ASMovableMenu.closedSettingImage)
                        } else {
                            //movableMenu.settingImage.setBackgroundColor(Color.WHITE);
                        }


                        movableMenu.handleSection!!.setBackgroundResource(R.drawable.rounded_corner_1)
                        movableMenu.menuSection!!.setBackgroundResource(R.drawable.rounded_corner_1)


                        movableMenu.settingText!!.visibility = View.GONE


                        if (!movableMenu.isMovable) {
                            movableMenu.handleSection!!.visibility = View.GONE
                            movableMenu.menuSection!!.visibility = View.GONE
                        }

                        if (movableMenu.listener != null) movableMenu.listener!!.menuHasBeenClosed()

                        if (movableMenu.currentSelectedItem != -1) {
                            val handler = movableMenu.itemHandlerList!!.get(movableMenu.currentSelectedItem)
                            movableMenu.itemHandlerList!!.clear()
                            (handler!![1] as View.OnClickListener).onClick(handler[0] as View)
                            movableMenu.currentSelectedItem = -1
                        }

                    }
                    return true
                }
            }

            movableMenu.menuSection!!.visibility = View.GONE


            //create handlesection second
            if (parent is CanvasSection)
                movableMenu.handleSection = parent.addSubSectionWithFrame("handleSection", 0, 0, handleWidth, handleHeight, true, true)
            else if (parent is CanvasLayout)
                movableMenu.handleSection = parent.createNewSectionWithFrame("handleSection", 0, 0, handleWidth, handleHeight, true, true)

            movableMenu.handleSection!!.shiftPositionHandler!!.addRectToDimensionState(0, 0, handleWidth, handleHeight)
            movableMenu.handleSection!!.shiftPositionHandler!!.addRectToDimensionState((100 - handleWidth) / 2, 0, handleWidth, handleHeight)
            movableMenu.handleSection!!.shiftPositionHandler!!.addRectToDimensionState(100 - handleWidth, 0, handleWidth, handleHeight)
            movableMenu.handleSection!!.shiftPositionHandler!!.addRectToDimensionState(0, (100 - handleHeight) / 2, handleWidth, handleHeight)
            movableMenu.handleSection!!.shiftPositionHandler!!.addRectToDimensionState(100 - handleWidth, (100 - handleHeight) / 2, handleWidth, handleHeight)
            movableMenu.handleSection!!.shiftPositionHandler!!.addRectToDimensionState(0, 100 - handleHeight, handleWidth, handleHeight)
            movableMenu.handleSection!!.shiftPositionHandler!!.addRectToDimensionState((100 - handleWidth) / 2, 100 - handleHeight, handleWidth, handleHeight)
            movableMenu.handleSection!!.shiftPositionHandler!!.addRectToDimensionState(100 - handleWidth, 100 - handleHeight, handleWidth, handleHeight)

            //setting text handle section
            val handleSectionWidth = movableMenu.handleSection!!.sectionWidth
            movableMenu.handleSection!!.sectionWidth = menuWidth
            movableMenu.settingText = ASTextView(movableMenu.context)
            movableMenu.settingText!!.text = "Pengaturan"
            movableMenu.settingText!!.setTextColor(Color.BLACK)
            movableMenu.settingText!!.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18f)
            movableMenu.settingText!!.gravity = Gravity.CENTER
            movableMenu.handleSection!!.addViewWithFrame(movableMenu.settingText, 5, 5, 90, 90)
            movableMenu.handleSection!!.sectionWidth = handleSectionWidth

            movableMenu.settingText!!.visibility = View.GONE

            //setting image handle section
            movableMenu.settingImage = ASImageView(movableMenu.context)
            movableMenu.handleSection!!.addViewWithFrame(movableMenu.settingImage, 5, 5, 90, 90)

            movableMenu.handleSection!!.setASGestureListener(object : ASGestureListener {

                override// TODO Auto-generated method stub
                val isSwipeEnabled: Boolean
                    get() = false

                override// TODO Auto-generated method stub
                val isLongClickEnabled: Boolean
                    get() = false

                override// TODO Auto-generated method stub
                val isDoubleTapEnabled: Boolean
                    get() = false

                override val isClickEnabled: Boolean
                    get() = true

                override fun upEventOccurred(x: Float, y: Float): Boolean {
                    if (!movableMenu.isMenuOpened) {
                        movableMenu.handleSection!!.shiftPositionHandler!!.changeStateToNearestDimension(true)
                        movableMenu.menuSection!!.shiftPositionHandler!!.changeStateToNearestDimension(true)
                    }

                    movableMenu.handleSection!!.shiftPositionHandler!!.upEvent(x, y)
                    movableMenu.menuSection!!.shiftPositionHandler!!.upEvent(x, y)
                    return true
                }

                override fun swipeEventOccured(swipeType: Int, x: Float, y: Float, dx: Float,
                                               dy: Float): Boolean {
                    // TODO Auto-generated method stub
                    return false
                }

                override fun swipeTypeFinal(swipeType: Int): Boolean {
                    return false
                }

                override fun movingSpeed(xSpeed: Float, ySpeed: Float): Boolean {
                    // TODO Auto-generated method stub
                    return false
                }

                override fun longClickEventOccured(): Boolean {
                    // TODO Auto-generated method stub
                    return false
                }

                override fun downEventOccured(x: Float, y: Float): Boolean {
                    movableMenu.handleSection!!.shiftPositionHandler!!.downEvent(x, y)
                    movableMenu.menuSection!!.shiftPositionHandler!!.downEvent(x, y)
                    return true
                }

                override fun doubleTapEventOccured(): Boolean {
                    // TODO Auto-generated method stub
                    return false
                }

                override fun deltaMoveOutsideParameter(swipeType: Int, x: Float, y: Float,
                                                       dx: Float, dy: Float, fromDownDX: Float, fromDownDY: Float): Boolean {
                    if (!movableMenu.isMenuOpened) {
                        movableMenu.handleSection!!.shiftPositionHandler!!.shiftViewWithDelta(dx, dy, fromDownDX, fromDownDY)
                        movableMenu.menuSection!!.shiftPositionHandler!!.shiftViewWithDelta(dx, dy, fromDownDX, fromDownDY)
                    }
                    return true
                }

                override fun deltaMoveInsideParameter(swipeType: Int, x: Float, y: Float,
                                                      dx: Float, dy: Float, fromDownDX: Float, fromDownDY: Float): Boolean {
                    if (!movableMenu.isMenuOpened) {
                        movableMenu.handleSection!!.shiftPositionHandler!!.shiftViewWithDelta(dx, dy, fromDownDX, fromDownDY)
                        movableMenu.menuSection!!.shiftPositionHandler!!.shiftViewWithDelta(dx, dy, fromDownDX, fromDownDY)
                    }
                    return true
                }

                override fun clickEventOccured(): Boolean {
                    if (movableMenu.isMenuOpened) {
                        //close
                        movableMenu.closeMenu(true)
                    } else {
                        //open
                        movableMenu.openMenu(true)
                    }

                    return true
                }

                override fun cancelEventOccured(x: Float, y: Float): Boolean {
                    // TODO Auto-generated method stub
                    return true
                }
            })
            movableMenu.itemHandlerList = HashMap()

            //movableMenu.handleSection.getShiftPositionHandler().changeStateToDimension(lastDimensionState, false);
            //movableMenu.menuSection.getShiftPositionHandler().changeStateToDimension(lastDimensionState, false);
            return movableMenu
        }
    }

}
