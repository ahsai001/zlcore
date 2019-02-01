package com.zaitunlabs.zlcore.listeners;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.text.TextUtils;
import android.view.View;

/**
 * Created by ahsai on 6/10/2018.
 */

public class SwipeDragCallback extends ItemTouchHelper.Callback {
    private final SwipeDragInterface listener;

    public SwipeDragCallback(SwipeDragInterface listener) {
        this.listener = listener;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlags = listener.swipeFlags();
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        listener.onItemDrag(viewHolder, target);
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        listener.onItemSwipe(viewHolder, direction);
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return listener.isLongPressDragEnabled();
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return listener.isItemViewSwipeEnabled();
    }


    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            // Get RecyclerView item from the ViewHolder
            View itemView = viewHolder.itemView;
            float buttonWidthWithoutPadding = 250 - 20;
            Paint p = new Paint();
            if (dX > 0) {
                p.setColor(listener.swipeRightColor());
                // Draw Rect with varying right side, equal to displacement dX
                c.drawRect((float) itemView.getLeft(), (float) itemView.getTop(), dX,
                        (float) itemView.getBottom(), p);

                if(!TextUtils.isEmpty(listener.swipeRightTextString())) {
                    RectF leftButton = new RectF(itemView.getLeft(), itemView.getTop(), itemView.getLeft() + buttonWidthWithoutPadding, itemView.getBottom());
                    drawText(listener.swipeRightTextString(), c, leftButton, p, listener.swipeRightTextColor());
                }
            } else {
                p.setColor(listener.swipeLeftColor());
                // Draw Rect with varying left side, equal to the item's right side plus negative displacement dX
                c.drawRect((float) itemView.getRight() + dX, (float) itemView.getTop(),
                        (float) itemView.getRight(), (float) itemView.getBottom(), p);

                if(!TextUtils.isEmpty(listener.swipeLeftTextString())) {
                    RectF rightButton = new RectF(itemView.getRight() - buttonWidthWithoutPadding, itemView.getTop(), itemView.getRight(), itemView.getBottom());
                    drawText(listener.swipeLeftTextString(), c, rightButton, p, listener.swipeLeftTextColor());
                }
            }
        }

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    private void drawText(String text, Canvas c, RectF button, Paint p, int textColor) {
        float textSize = 60;
        p.setColor(textColor);
        p.setAntiAlias(true);
        p.setTextSize(textSize);

        float textWidth = p.measureText(text);
        c.drawText(text, button.centerX()-(textWidth/2), button.centerY()+(textSize/2), p);
    }

    public interface SwipeDragInterface {
        void onItemDrag(RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target);
        void onItemSwipe(RecyclerView.ViewHolder viewHolder, int direction);
        boolean isLongPressDragEnabled();
        boolean isItemViewSwipeEnabled();
        int swipeLeftTextColor();
        int swipeRightTextColor();
        int swipeLeftColor();
        int swipeRightColor();
        String swipeLeftTextString();
        String swipeRightTextString();
        int swipeFlags();
    }

}
