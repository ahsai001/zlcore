package com.zaitunlabs.zlcore.views

interface ASGestureListener {

    //property
    val isClickEnabled: Boolean
    val isLongClickEnabled: Boolean
    val isDoubleTapEnabled: Boolean
    val isSwipeEnabled: Boolean
    fun deltaMoveInsideParameter(swipeType: Int, x: Float, y: Float, dx: Float, dy: Float, fromDownDX: Float, fromDownDY: Float): Boolean
    fun deltaMoveOutsideParameter(swipeType: Int, x: Float, y: Float, dx: Float, dy: Float, fromDownDX: Float, fromDownDY: Float): Boolean
    fun movingSpeed(xSpeed: Float, ySpeed: Float): Boolean
    fun upEventOccurred(x: Float, y: Float): Boolean
    fun downEventOccured(x: Float, y: Float): Boolean
    fun cancelEventOccured(x: Float, y: Float): Boolean
    fun clickEventOccured(): Boolean
    fun longClickEventOccured(): Boolean
    fun doubleTapEventOccured(): Boolean
    fun swipeEventOccured(swipeType: Int, x: Float, y: Float, dx: Float, dy: Float): Boolean
    fun swipeTypeFinal(swipeType: Int): Boolean
}
