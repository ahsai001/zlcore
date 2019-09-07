package com.zaitunlabs.zlcore.views

import android.content.Context
import android.os.Build.VERSION
import android.os.Handler
import android.os.Message
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View

import com.zaitunlabs.zlcore.utils.DebugUtils


class ASGestureDetector {
    private val vTouchTracker: VelocityTracker? = null
    var gestureListener: ASGestureListener? = null

    private var ctx: Context? = null
    private var view: View? = null
    private var viewName: String? = null
    //this four is relative to last move point, updated when move
    private var touchDownX = -1f
    private var touchDownY = -1f
    private var touchDownRawX = -1f
    private var touchDownRawY = -1f

    //this two is real point from down event
    private var touchDownAbsoluteX = -1f
    private var touchDownAbsoluteY = -1f
    private var isMoving = false
    private var longClickHandler: Handler? = null
    private var isLongClickActive = false
    private var firstTapTime: Long = -1
    private var isDoubleTapActive = false
    private var oneTapHandler: Handler? = null

    private var globalSwipeType = SWIPE_NONE

    constructor(ctx: Context, gestureListener: ASGestureListener) {
        this.ctx = ctx
        this.gestureListener = gestureListener
        this.viewName = this.toString()
    }

    constructor(viewName: String, ctx: Context) {
        this.ctx = ctx
        this.viewName = viewName
    }

    constructor(ctx: Context) {
        this.ctx = ctx
        this.viewName = this.toString()
    }

    fun handleOnTouch(v: View, event: MotionEvent): Boolean {
        this.view = v
        val action = event.action
        /*
		 * int action = event.getActionMasked(); int pointerCount =
		 * event.getPointerCount(); int index = event.getActionIndex(); int
		 * pointer = event.getPointerId(index); DebugUtils.logE("onTouchEvent",
		 * getSectionName
		 * ()+"count:"+pointerCount+"---index:"+index+"---pointer:"
		 * +pointer+" View:"+v,isLogged);
		 */
        when (action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                if (VERSION.SDK_INT < 16) {
                    if (vTouchTracker == null) {
                        //vTouchTracker = VelocityTracker.obtain();
                    } else {
                        //vTouchTracker.clear();
                    }
                    //vTouchTracker.addMovement(event);
                }

                touchDownX = event.x
                touchDownY = event.y
                touchDownRawX = event.rawX
                touchDownRawY = event.rawY

                touchDownAbsoluteX = event.rawX
                touchDownAbsoluteY = event.rawY

                DebugUtils.logE("DETECTOR", "down x : " + Math.abs(event.rawX) + " for " + this.viewName)

                isMoving = false

                // DoubleTap Detection
                isDoubleTapActive = false
                if (gestureListener != null && gestureListener!!.isDoubleTapEnabled) {
                    if (firstTapTime > -1) {
                        val currentTime = System.currentTimeMillis()
                        val deltaDoubleTap = currentTime - firstTapTime
                        if (deltaDoubleTap < DELAY_BETWEEN_TAP_ON_DOUBLETAP) {
                            oneTapHandler!!.removeMessages(0)
                            gestureListener!!.doubleTapEventOccured()
                            isDoubleTapActive = true
                        }
                        firstTapTime = currentTime
                    }
                }
                // LongClick detection
                isLongClickActive = false
                if (gestureListener != null && gestureListener!!.isLongClickEnabled) {
                    longClickHandler = object : Handler() {
                        override fun handleMessage(msg: Message) {
                            // long click detected
                            isLongClickActive = true
                            gestureListener!!.longClickEventOccured()
                        }
                    }
                    longClickHandler!!.sendMessageDelayed(Message.obtain(),
                            MIN_LONGCLICK_DURATION.toLong())
                }

                if (gestureListener != null) {
                    return gestureListener!!.downEventOccured(event.x, event.y)
                }
            }
            MotionEvent.ACTION_MOVE -> {
                val xMove = event.x
                val yMove = event.y
                val xRawMove = event.rawX
                val yRawMove = event.rawY
                val deltaX = xMove - touchDownX
                val deltaY = yMove - touchDownY
                val deltaRawX = xRawMove - touchDownRawX
                val deltaRawY = yRawMove - touchDownRawY

                val deltaDownRawX = xRawMove - touchDownAbsoluteX
                val deltaDownRawY = yRawMove - touchDownAbsoluteY

                DebugUtils.logE("DETECTOR", "move a x : " + Math.abs(event.rawX) + " for " + this.viewName)
                //CommonUtils.LogV("DETECTOR", "yRawMove=" + yRawMove + "-" + "touchDownAbsoluteY=" + touchDownAbsoluteY + "-" + "deltaRawY=" + deltaDownRawY);
                var move_threshold = 0f

                // ViewConfiguration vc = ViewConfiguration.get(getContext());
                // int mSlop = vc.getScaledTouchSlop();
                // int mMinFlingVelocity = vc.getScaledMinimumFlingVelocity();
                // int mMaxFlingVelocity = vc.getScaledMaximumFlingVelocity();
                // CommonUtils.LogV("", "mSlop " + mSlop +" ",isLogged);
                // CommonUtils.LogV("", "mMinFlingVelocity " + mMinFlingVelocity
                // +" ",isLogged);
                // CommonUtils.LogV("", "mMaxFlingVelocity " + mMaxFlingVelocity
                // +" ",isLogged);

                if (isMoving == false && gestureListener != null && (gestureListener!!.isClickEnabled || gestureListener!!.isLongClickEnabled)) {
                    move_threshold = MAX_MOVE_THRESHOLD
                } else {
                    // on moving
                    move_threshold = 0f
                }

                val xSpeed = 0f
                val ySpeed = 0f

                if (VERSION.SDK_INT < 16) {
                    //vTouchTracker.addMovement(event);
                    //vTouchTracker.computeCurrentVelocity(1000);
                    //xSpeed = vTouchTracker.getXVelocity();
                    //ySpeed = vTouchTracker.getYVelocity();
                }

                if (Math.abs(deltaRawX) > move_threshold || Math.abs(deltaRawY) > move_threshold) {
                    // Movement detected !!!!!!!!!
                    DebugUtils.logE("DETECTOR", "move x : " + Math.abs(event.rawX) + " for " + this.viewName)
                    isMoving = true

                    // cancel longclick sending message
                    if (longClickHandler != null)
                        longClickHandler!!.removeMessages(WHAT_LONGCLICK_MESSAGE)

                    /*
				 * //swipe left right or swipe up down ==> experiment if((ySpeed
				 * == 0 && xSpeed > 0) || (ySpeed > 0 && xSpeed > 0 && xSpeed >
				 * ySpeed && (xSpeed / ySpeed) > SPEED_MOVE_THRESHOLD)){
				 * deltaRawY = 0; CommonUtils.LogV("","kanan kiri aktif"); }
				 * if((ySpeed > 0 && xSpeed == 0) || (ySpeed > 0 && xSpeed > 0
				 * && ySpeed > xSpeed && (ySpeed / xSpeed) >
				 * SPEED_MOVE_THRESHOLD)){ deltaRawX = 0;
				 * CommonUtils.LogV("","atas bawah aktif"); }
				 */

                    touchDownX = xMove
                    touchDownY = yMove

                    touchDownRawX = xRawMove
                    touchDownRawY = yRawMove

                    if (gestureListener != null) {
                        gestureListener!!.movingSpeed(xSpeed, ySpeed)


                        //detection of swipe
                        var degree = 0.0
                        var swypeType = SWIPE_NONE

                        //DebugUtils.logE("DETECTOR", "deltaDownRaw x : "+Math.abs(deltaDownRawX));
                        //DebugUtils.logE("DETECTOR", "deltaDownRaw y : "+Math.abs(deltaDownRawY));

                        if (gestureListener!!.isSwipeEnabled && (Math.abs(deltaDownRawX) > SWIPE_SENSITIVITY_THRESHOLD || Math.abs(deltaDownRawY) > SWIPE_SENSITIVITY_THRESHOLD)) {

                            if (deltaDownRawY > 0 && deltaDownRawX == 0f) {
                                degree = 90.0
                            } else {
                                degree = Math.toDegrees(Math.atan(Math.abs(deltaDownRawY / deltaDownRawX).toDouble()))
                            }


                            //CommonUtils.LogV("ahmad", "deegre:"+degree);
                            //CommonUtils.LogV("ahmad", "deltaRawY:"+deltaRawY);
                            //CommonUtils.LogV("ahmad", "deltaRawX:"+deltaRawX);
                            if (degree > 45) {
                                //swipe up-down
                                if (deltaDownRawY > 0) {
                                    //down
                                    swypeType = SWIPE_DOWN
                                } else if (deltaDownRawY < 0) {
                                    //up
                                    swypeType = SWIPE_UP
                                } else {
                                    //maybe no moving
                                }
                            } else {
                                //swipe left-right
                                if (deltaDownRawX > 0) {
                                    //right
                                    swypeType = SWIPE_RIGHT
                                } else if (deltaDownRawX < 0) {
                                    //left
                                    swypeType = SWIPE_LEFT
                                } else {
                                    //maybe no moving
                                }
                            }
                        }

                        globalSwipeType = swypeType
                        //CommonUtils.LogV("ahmad", "swypeType:"+swypeType);
                        //CommonUtils.LogV("ahmad", "globalSwipeType:"+globalSwipeType);

                        var returnValue = false
                        if (xMove > view!!.width || xMove < 0 || yMove > view!!.height || yMove < 0) {
                            returnValue = gestureListener!!.deltaMoveOutsideParameter(swypeType, xMove,
                                    yMove, deltaRawX, deltaRawY, deltaDownRawX, deltaDownRawY)
                        } else {
                            returnValue = gestureListener!!.deltaMoveInsideParameter(swypeType, xMove,
                                    yMove, deltaRawX, deltaRawY, deltaDownRawX, deltaDownRawY)
                        }
                        if (gestureListener!!.isSwipeEnabled && (Math.abs(deltaDownRawX) > SWIPE_SENSITIVITY_THRESHOLD || Math.abs(deltaDownRawY) > SWIPE_SENSITIVITY_THRESHOLD)) {
                            returnValue = gestureListener!!.swipeEventOccured(swypeType, xMove,
                                    yMove, deltaRawX, deltaRawY)
                        }
                        return returnValue
                    }

                    /*
				 * try { Thread.sleep(10); } catch (InterruptedException e) {
				 * e.printStackTrace(); }
				 */

                    //return true;
                } else {
                    //return true;
                }
            }
            MotionEvent.ACTION_UP -> {
                if (VERSION.SDK_INT < 16) {
                    //vTouchTracker.recycle();
                }

                touchDownY = -1f
                touchDownX = touchDownY
                touchDownRawY = -1f
                touchDownRawX = touchDownRawY

                touchDownAbsoluteY = -1f
                touchDownAbsoluteX = touchDownAbsoluteY

                val isSwipeType = globalSwipeType
                globalSwipeType = SWIPE_NONE

                // cancel longclick sending message
                if (longClickHandler != null)
                    longClickHandler!!.removeMessages(WHAT_LONGCLICK_MESSAGE)

                if (isLongClickActive || isDoubleTapActive)
                    return true

                if (gestureListener != null) {
                    if (gestureListener!!.isDoubleTapEnabled && !isMoving) {
                        oneTapHandler = object : Handler() {
                            override fun handleMessage(msg: Message) {
                                if (gestureListener!!.isClickEnabled && !isMoving) {
                                    if (gestureListener!!.clickEventOccured()) {
                                    }
                                }
                            }
                        }
                        firstTapTime = System.currentTimeMillis()
                        oneTapHandler!!.sendMessageDelayed(Message.obtain(), DELAY_BETWEEN_TAP_ON_DOUBLETAP.toLong())
                        return true
                    } else {
                        if (gestureListener!!.isClickEnabled && !isMoving) {
                            if (gestureListener!!.clickEventOccured()) {
                                return true
                            }
                        } else {
                            if (gestureListener!!.isSwipeEnabled) {
                                gestureListener!!.swipeTypeFinal(isSwipeType)
                            }
                        }
                    }
                    return gestureListener!!.upEventOccurred(event.x,
                            event.y)
                }
            }
            MotionEvent.ACTION_CANCEL -> {
                if (VERSION.SDK_INT < 16) {
                    //vTouchTracker.recycle();
                }
                touchDownY = -1f
                touchDownX = touchDownY
                touchDownRawY = -1f
                touchDownRawX = touchDownRawY
                touchDownAbsoluteY = -1f
                touchDownAbsoluteX = touchDownAbsoluteY

                globalSwipeType = SWIPE_NONE

                // cancel longclick sending message
                if (longClickHandler != null)
                    longClickHandler!!.removeMessages(WHAT_LONGCLICK_MESSAGE)

                if (isLongClickActive || isDoubleTapActive)
                    return true

                if (gestureListener != null) {
                    return gestureListener!!.cancelEventOccured(event.x,
                            event.y)
                }
            }

            // extra when use mask and pointer or multi touch
            MotionEvent.ACTION_OUTSIDE -> {
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
            }
            MotionEvent.ACTION_POINTER_UP -> {
            }

            else -> {
            }
        }
        // invalidate();
        return false
    }

    companion object {

        private val MAX_MOVE_THRESHOLD = 7f // in pixel
        private val SWIPE_SENSITIVITY_THRESHOLD = 40f // in pixel
        private val SPEED_MOVE_THRESHOLD = 5f // in pixel

        // paremeter to handle longClick
        private val MIN_LONGCLICK_DURATION = 1200
        private val WHAT_LONGCLICK_MESSAGE = 0

        // paremeter to handle double tap
        private val DELAY_BETWEEN_TAP_ON_DOUBLETAP = 300


        //parameter to handle swipe
        val SWIPE_LEFT = 0
        val SWIPE_RIGHT = 1
        val SWIPE_UP = 2
        val SWIPE_DOWN = 3
        val SWIPE_NONE = 4
    }

}
