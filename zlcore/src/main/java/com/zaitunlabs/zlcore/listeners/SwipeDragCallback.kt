package com.zaitunlabs.zlcore.listeners

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ItemTouchHelper
import android.text.TextUtils
import android.view.View

/**
 * Created by ahsai on 6/10/2018.
 */

class SwipeDragCallback(private val listener: SwipeDragInterface) : ItemTouchHelper.Callback() {

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        val swipeFlags = listener.swipeFlags()
        return ItemTouchHelper.Callback.makeMovementFlags(dragFlags, swipeFlags)
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        listener.onItemDrag(viewHolder, target)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        listener.onItemSwipe(viewHolder, direction)
    }

    override fun isLongPressDragEnabled(): Boolean {
        return listener.isLongPressDragEnabled
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return listener.isItemViewSwipeEnabled
    }


    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            // Get RecyclerView item from the ViewHolder
            val itemView = viewHolder.itemView
            val buttonWidthWithoutPadding = (250 - 20).toFloat()
            val p = Paint()
            if (dX > 0) {
                p.color = listener.swipeRightColor()
                // Draw Rect with varying right side, equal to displacement dX
                c.drawRect(itemView.left.toFloat(), itemView.top.toFloat(), dX,
                        itemView.bottom.toFloat(), p)

                if (!TextUtils.isEmpty(listener.swipeRightTextString())) {
                    val leftButton = RectF(itemView.left.toFloat(), itemView.top.toFloat(), itemView.left + buttonWidthWithoutPadding, itemView.bottom.toFloat())
                    drawText(listener.swipeRightTextString(), c, leftButton, p, listener.swipeRightTextColor())
                }
            } else {
                p.color = listener.swipeLeftColor()
                // Draw Rect with varying left side, equal to the item's right side plus negative displacement dX
                c.drawRect(itemView.right.toFloat() + dX, itemView.top.toFloat(),
                        itemView.right.toFloat(), itemView.bottom.toFloat(), p)

                if (!TextUtils.isEmpty(listener.swipeLeftTextString())) {
                    val rightButton = RectF(itemView.right - buttonWidthWithoutPadding, itemView.top.toFloat(), itemView.right.toFloat(), itemView.bottom.toFloat())
                    drawText(listener.swipeLeftTextString(), c, rightButton, p, listener.swipeLeftTextColor())
                }
            }
        }

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    private fun drawText(text: String, c: Canvas, button: RectF, p: Paint, textColor: Int) {
        val textSize = 60f
        p.color = textColor
        p.isAntiAlias = true
        p.textSize = textSize

        val textWidth = p.measureText(text)
        c.drawText(text, button.centerX() - textWidth / 2, button.centerY() + textSize / 2, p)
    }

    interface SwipeDragInterface {
        val isLongPressDragEnabled: Boolean
        val isItemViewSwipeEnabled: Boolean
        fun onItemDrag(viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder)
        fun onItemSwipe(viewHolder: RecyclerView.ViewHolder, direction: Int)
        fun swipeLeftTextColor(): Int
        fun swipeRightTextColor(): Int
        fun swipeLeftColor(): Int
        fun swipeRightColor(): Int
        fun swipeLeftTextString(): String
        fun swipeRightTextString(): String
        fun swipeFlags(): Int
    }

}
