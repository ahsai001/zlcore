package com.zaitunlabs.zlcore.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by ahmad s on 13/09/20.
 */
public class SimpleListItem extends ViewGroup {
    ImageView icon;
    TextView titleView;
    TextView subtitleView;

    public SimpleListItem(Context context) {
        super(context);
    }

    public SimpleListItem(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Measure icon
        measureChildWithMargins(icon, widthMeasureSpec, 0, heightMeasureSpec, 0);

        // Figure out how much width the icon used.
        MarginLayoutParams lp = (MarginLayoutParams) icon.getLayoutParams();
        int widthUsed = icon.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;

        int heightUsed = 0;

        // Measure title
        measureChildWithMargins(
                titleView,
                // Pass width constraints and width already used.
                widthMeasureSpec, widthUsed,
                // Pass height constraints and height already used.
                heightMeasureSpec, heightUsed
        );

        MarginLayoutParams titleLP = (MarginLayoutParams) titleView.getLayoutParams();

        // Measure the Subtitle.
        measureChildWithMargins(
                subtitleView,
                // Pass width constraints and width already used.
                widthMeasureSpec, widthUsed,
                // Pass height constraints and height already used.
                heightMeasureSpec, titleView.getMeasuredHeight());

        // Calculate this view's measured width and height.
        //???
        MarginLayoutParams subTitleLP = (MarginLayoutParams) titleView.getLayoutParams();

        int measuredWidth = widthUsed + subtitleView.getMeasuredWidth() + titleLP.leftMargin + titleLP.rightMargin;
        int measuredHeight = subtitleView.getMeasuredHeight() + subTitleLP.topMargin + subTitleLP.bottomMargin;

        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    @Override
    protected void onLayout(boolean b, int i, int i1, int i2, int i3) {
        MarginLayoutParams layoutParams = (MarginLayoutParams) icon.getLayoutParams();

        // Figure out the x-coordinate and y-coordinate of the icon.
        int x = getPaddingLeft() + layoutParams.leftMargin;
        int y = getPaddingTop() + layoutParams.topMargin;

        // Layout the icon.
        icon.layout(x, y, x + icon.getMeasuredWidth(), y + icon.getMeasuredHeight());

        // Calculate the x-coordinate of the title: icon's right coordinate +
        // the icon's right margin.
        x += icon.getMeasuredWidth() + layoutParams.rightMargin;

        // Add in the title's left margin.
        layoutParams = (MarginLayoutParams) titleView.getLayoutParams();
        x += layoutParams.leftMargin;

        // Calculate the y-coordinate of the title: this ViewGroup's top padding +
        // the title's top margin
        y = getPaddingTop() + layoutParams.topMargin;

        // Layout the title.
        titleView.layout(x, y, x + titleView.getMeasuredWidth(), y + titleView.getMeasuredHeight());

        // The subtitle has the same x-coordinate as the title. So no more calculating there.

        // Calculate the y-coordinate of the subtitle: the title's bottom coordinate +
        // the title's bottom margin.

        y += titleView.getMeasuredHeight() + layoutParams.bottomMargin;

        layoutParams = (MarginLayoutParams) subtitleView.getLayoutParams();

        y += layoutParams.topMargin;

        subtitleView.layout(x, y, x + subtitleView.getMeasuredWidth(), y + subtitleView.getMeasuredHeight());
    }



    /**
     * Validates if a set of layout parameters is valid for a child this ViewGroup.
     */
    @Override
    protected boolean checkLayoutParams(LayoutParams p) {
        return p instanceof MarginLayoutParams;
    }

    /**
     * @return A set of default layout parameters when given a child with no layout parameters.
     */
    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    /**
     * @return A set of layout parameters created from attributes passed in XML.
     */
    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    /**
     * Called when {@link #checkLayoutParams(LayoutParams)} fails.
     *
     * @return A set of valid layout parameters for this ViewGroup that copies appropriate/valid
     * attributes from the supplied, not-so-good-parameters.
     */
    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        return generateDefaultLayoutParams();
    }
}
