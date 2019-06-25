package com.zaitunlabs.zlcore.utils;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import android.text.InputType;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;

/**
 * @author upshots.org 5/27/16.
 */

/**
 * Created by ahsai on 4/25/2018.
 */

public class ViewUtils {

    /**
     * Finds the first child in #rootView that is an instance of #clazz
     *
     * @param rootView The View whose hierarchy should be examined for instances of #clazz.
     * @param clazz    The Class to search for within #rootView.
     * @param <T>      The type of View subclass to search for.
     * @return The first child in #rootView this is an instance of #clazz.
     */
    public static <T extends View> T findViewByClassReference(View rootView, Class<T> clazz) {
        if(clazz.isInstance(rootView)) {
            return clazz.cast(rootView);
        }
        if(rootView instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) rootView;
            for(int i = 0; i < viewGroup.getChildCount(); i++) {
                View child = viewGroup.getChildAt(i);
                T match = findViewByClassReference(child, clazz);
                if(match != null) {
                    return match;
                }
            }
        }
        return null;
    }

    /**
     * Returns a Collection of View subclasses instances of type T found within #rootView.
     *
     * @param rootView The View whose hierarchy should be examined for instances of #clazz.
     * @param clazz    The Class to search for within #rootView.
     * @param out      A Collection of View subclasses of type T that will be populated with matches found in #rootView.
     * @param <T>      The type of View subclass to search for.
     * @return A Collection of View subclasses instances of type T found within #rootView.
     */
    public static <T extends View> Collection<T> findViewsByClassReference(View rootView, Class<T> clazz, Collection<T> out) {
        if(out == null) {
            out = new HashSet<>();
        }
        if(clazz.isInstance(rootView)) {
            out.add(clazz.cast(rootView));
        }
        if(rootView instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) rootView;
            for(int i = 0; i < viewGroup.getChildCount(); i++) {
                View child = viewGroup.getChildAt(i);
                findViewsByClassReference(child, clazz, out);
            }
        }
        return out;
    }

    /**
     * Returns a Collection of View subclasses instances of type T found within #rootView.
     *
     * @param rootView The View whose hierarchy should be examined for instances of #clazz.
     * @param clazz    The Class to search for within #rootView.
     * @param <T>      The type of View subclass to search for.
     * @return A Collection of View subclasses instances of type T found within #rootView.
     */
    public static <T extends View> Collection<T> findViewsByClassReference(View rootView, Class<T> clazz) {
        return findViewsByClassReference(rootView, clazz, null);
    }

    public static int getSelectableItemBackgroundResID(Context context){
        TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
        return outValue.resourceId;
    }

    public static int getSelectableItemBackgroundBorderLessResID(Context context){
        TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, outValue, true);
        return outValue.resourceId;
    }

    public static Drawable getSelectableItemBackgroundDrawable(Context context){
        return ContextCompat.getDrawable(context,getSelectableItemBackgroundResID(context));
    }

    public static Drawable getSelectableItemBackgroundBorderLessDrawable(Context context){
        return ContextCompat.getDrawable(context,getSelectableItemBackgroundBorderLessResID(context));
    }

    public static Drawable getSelectableItemBackgroundWithDrawable(Context context, Drawable drawable){
        Drawable[] layers = {drawable, getSelectableItemBackgroundDrawable(context)};
        LayerDrawable layerDrawable = new LayerDrawable(layers);
        return layerDrawable;
    }

    public static Drawable getSelectableItemBackgroundBorderLessWithDrawable(Context context, Drawable drawable){
        Drawable[] layers = {drawable, getSelectableItemBackgroundBorderLessDrawable(context)};
        LayerDrawable layerDrawable = new LayerDrawable(layers);
        return layerDrawable;
    }

    public static Drawable getSelectableItemBackgroundWithColor(Context context, int color){
        ColorDrawable colorDrawable = new ColorDrawable(color);
        return getSelectableItemBackgroundWithDrawable(context,colorDrawable);
    }

    public static Drawable getSelectableItemBackgroundBorderLessWithColor(Context context, int color){
        ColorDrawable colorDrawable = new ColorDrawable(color);
        return getSelectableItemBackgroundBorderLessWithDrawable(context,colorDrawable);
    }

    public static Drawable getLeftHeadTableBackground(Context context, int borderColor, int fillColor, int radiusInDp){
        GradientDrawable borderDrawable = new GradientDrawable();
        borderDrawable.setShape(GradientDrawable.RECTANGLE);
        float radiusInPixel = CommonUtils.getPixelFromDip2(context, radiusInDp);
        borderDrawable.setCornerRadii(new float[]{radiusInPixel, radiusInPixel, 0, 0, 0, 0, 0, 0});
        borderDrawable.setColor(borderColor);

        GradientDrawable fillDrawable = new GradientDrawable();
        fillDrawable.setShape(GradientDrawable.RECTANGLE);
        fillDrawable.setCornerRadii(new float[]{radiusInPixel, radiusInPixel, 0, 0, 0, 0, 0, 0});
        fillDrawable.setColor(fillColor);

        Drawable[] layers = {borderDrawable, fillDrawable};
        LayerDrawable layerDrawable = new LayerDrawable(layers);
        float oneDp = CommonUtils.getPixelFromDip2(context, 1);
        float twoDp = CommonUtils.getPixelFromDip2(context, 2);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            layerDrawable.setLayerInsetLeft(1, (int) twoDp);
            layerDrawable.setLayerInsetStart(1, (int) twoDp);
            layerDrawable.setLayerInsetTop(1, (int) twoDp);
            layerDrawable.setLayerInsetBottom(1, (int) twoDp);
            layerDrawable.setLayerInsetRight(1, (int) oneDp);
            layerDrawable.setLayerInsetEnd(1, (int) oneDp);
        } else {
            layerDrawable.setLayerInset(1,(int)twoDp,(int)twoDp,(int)oneDp,(int)twoDp);
        }
        return layerDrawable;
    }

    public static Drawable getCenterHeadTableBackground(Context context, int borderColor, int fillColor){
        GradientDrawable borderDrawable = new GradientDrawable();
        borderDrawable.setShape(GradientDrawable.RECTANGLE);
        borderDrawable.setColor(borderColor);

        GradientDrawable fillDrawable = new GradientDrawable();
        fillDrawable.setShape(GradientDrawable.RECTANGLE);
        fillDrawable.setColor(fillColor);

        Drawable[] layers = {borderDrawable, fillDrawable};
        LayerDrawable layerDrawable = new LayerDrawable(layers);
        float oneDp = CommonUtils.getPixelFromDip2(context, 1);
        float twoDp = CommonUtils.getPixelFromDip2(context, 2);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            layerDrawable.setLayerInsetLeft(1, (int) oneDp);
            layerDrawable.setLayerInsetStart(1, (int) oneDp);
            layerDrawable.setLayerInsetTop(1, (int) twoDp);
            layerDrawable.setLayerInsetBottom(1, (int) twoDp);
            layerDrawable.setLayerInsetRight(1, (int) oneDp);
            layerDrawable.setLayerInsetEnd(1, (int) oneDp);
        } else {
            layerDrawable.setLayerInset(1,(int)oneDp,(int)twoDp,(int)oneDp,(int)twoDp);
        }
        return layerDrawable;
    }

    public static Drawable getRightHeadTableBackground(Context context, int borderColor, int fillColor, int radiusInDp){
        GradientDrawable borderDrawable = new GradientDrawable();
        borderDrawable.setShape(GradientDrawable.RECTANGLE);
        float radiusInPixel = CommonUtils.getPixelFromDip2(context, radiusInDp);
        borderDrawable.setCornerRadii(new float[]{0,0, radiusInPixel, radiusInPixel, 0, 0, 0, 0});
        borderDrawable.setColor(borderColor);

        GradientDrawable fillDrawable = new GradientDrawable();
        fillDrawable.setShape(GradientDrawable.RECTANGLE);
        fillDrawable.setCornerRadii(new float[]{0,0, radiusInPixel, radiusInPixel, 0, 0, 0, 0});
        fillDrawable.setColor(fillColor);

        Drawable[] layers = {borderDrawable, fillDrawable};
        LayerDrawable layerDrawable = new LayerDrawable(layers);
        float oneDp = CommonUtils.getPixelFromDip2(context, 1);
        float twoDp = CommonUtils.getPixelFromDip2(context, 2);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            layerDrawable.setLayerInsetLeft(1, (int) oneDp);
            layerDrawable.setLayerInsetStart(1, (int) oneDp);
            layerDrawable.setLayerInsetTop(1, (int) twoDp);
            layerDrawable.setLayerInsetBottom(1, (int) twoDp);
            layerDrawable.setLayerInsetRight(1, (int) twoDp);
            layerDrawable.setLayerInsetEnd(1, (int) twoDp);
        } else {
            layerDrawable.setLayerInset(1,(int)oneDp,(int)twoDp,(int)twoDp,(int)twoDp);
        }
        return layerDrawable;
    }

    public static Drawable getLeftBodyTableBackground(Context context, int borderColor, int fillColor){
        GradientDrawable borderDrawable = new GradientDrawable();
        borderDrawable.setShape(GradientDrawable.RECTANGLE);
        borderDrawable.setColor(borderColor);

        GradientDrawable fillDrawable = new GradientDrawable();
        fillDrawable.setShape(GradientDrawable.RECTANGLE);
        fillDrawable.setColor(fillColor);

        Drawable[] layers = {borderDrawable, fillDrawable};
        LayerDrawable layerDrawable = new LayerDrawable(layers);
        float oneDp = CommonUtils.getPixelFromDip2(context, 1);
        float twoDp = CommonUtils.getPixelFromDip2(context, 2);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            layerDrawable.setLayerInsetLeft(1, (int) twoDp);
            layerDrawable.setLayerInsetStart(1, (int) twoDp);
            layerDrawable.setLayerInsetRight(1, (int) oneDp);
            layerDrawable.setLayerInsetEnd(1, (int) oneDp);
        } else {
            layerDrawable.setLayerInset(1,(int)twoDp,0,(int)oneDp,0);
        }
        return layerDrawable;
    }

    public static Drawable getCenterBodyTableBackground(Context context, int borderColor, int fillColor){
        GradientDrawable borderDrawable = new GradientDrawable();
        borderDrawable.setShape(GradientDrawable.RECTANGLE);
        borderDrawable.setColor(borderColor);

        GradientDrawable fillDrawable = new GradientDrawable();
        fillDrawable.setShape(GradientDrawable.RECTANGLE);
        fillDrawable.setColor(fillColor);

        Drawable[] layers = {borderDrawable, fillDrawable};
        LayerDrawable layerDrawable = new LayerDrawable(layers);
        float oneDp = CommonUtils.getPixelFromDip2(context, 1);
        float twoDp = CommonUtils.getPixelFromDip2(context, 2);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            layerDrawable.setLayerInsetLeft(1, (int) oneDp);
            layerDrawable.setLayerInsetStart(1, (int) oneDp);
            layerDrawable.setLayerInsetRight(1, (int) oneDp);
            layerDrawable.setLayerInsetEnd(1, (int) oneDp);
        } else {
            layerDrawable.setLayerInset(1, (int) oneDp, 0, (int) oneDp, 0);
        }
        return layerDrawable;
    }

    public static Drawable getRightBodyTableBackground(Context context, int borderColor, int fillColor){
        GradientDrawable borderDrawable = new GradientDrawable();
        borderDrawable.setShape(GradientDrawable.RECTANGLE);
        borderDrawable.setColor(borderColor);

        GradientDrawable fillDrawable = new GradientDrawable();
        fillDrawable.setShape(GradientDrawable.RECTANGLE);
        fillDrawable.setColor(fillColor);

        Drawable[] layers = {borderDrawable, fillDrawable};
        LayerDrawable layerDrawable = new LayerDrawable(layers);
        float oneDp = CommonUtils.getPixelFromDip2(context, 1);
        float twoDp = CommonUtils.getPixelFromDip2(context, 2);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            layerDrawable.setLayerInsetLeft(1, (int) oneDp);
            layerDrawable.setLayerInsetStart(1, (int) oneDp);
            layerDrawable.setLayerInsetRight(1, (int) twoDp);
            layerDrawable.setLayerInsetEnd(1, (int) twoDp);
        } else {
            layerDrawable.setLayerInset(1,(int)oneDp,0,(int)twoDp,0);
        }
        return layerDrawable;
    }


    public static Drawable getLeftTailTableBackground(Context context, int borderColor, int fillColor, int radiusInDp){
        GradientDrawable borderDrawable = new GradientDrawable();
        borderDrawable.setShape(GradientDrawable.RECTANGLE);
        float radiusInPixel = CommonUtils.getPixelFromDip2(context, radiusInDp);
        borderDrawable.setCornerRadii(new float[]{0,0, 0, 0, 0, 0, radiusInPixel, radiusInPixel});
        borderDrawable.setColor(borderColor);

        GradientDrawable fillDrawable = new GradientDrawable();
        fillDrawable.setShape(GradientDrawable.RECTANGLE);
        fillDrawable.setCornerRadii(new float[]{0,0, 0, 0, 0, 0, radiusInPixel, radiusInPixel});
        fillDrawable.setColor(fillColor);

        Drawable[] layers = {borderDrawable, fillDrawable};
        LayerDrawable layerDrawable = new LayerDrawable(layers);
        float oneDp = CommonUtils.getPixelFromDip2(context, 1);
        float twoDp = CommonUtils.getPixelFromDip2(context, 2);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            layerDrawable.setLayerInsetLeft(1, (int) twoDp);
            layerDrawable.setLayerInsetStart(1, (int) twoDp);
            layerDrawable.setLayerInsetBottom(1, (int) twoDp);
            layerDrawable.setLayerInsetRight(1, (int) oneDp);
            layerDrawable.setLayerInsetEnd(1, (int) oneDp);
        } else {
            layerDrawable.setLayerInset(1,(int)twoDp,0,(int)oneDp,(int)twoDp);
        }
        return layerDrawable;
    }

    public static Drawable getCenterTailTableBackground(Context context, int borderColor, int fillColor){
        GradientDrawable borderDrawable = new GradientDrawable();
        borderDrawable.setShape(GradientDrawable.RECTANGLE);
        borderDrawable.setColor(borderColor);

        GradientDrawable fillDrawable = new GradientDrawable();
        fillDrawable.setShape(GradientDrawable.RECTANGLE);
        fillDrawable.setColor(fillColor);

        Drawable[] layers = {borderDrawable, fillDrawable};
        LayerDrawable layerDrawable = new LayerDrawable(layers);
        float oneDp = CommonUtils.getPixelFromDip2(context, 1);
        float twoDp = CommonUtils.getPixelFromDip2(context, 2);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            layerDrawable.setLayerInsetLeft(1, (int) oneDp);
            layerDrawable.setLayerInsetStart(1, (int) oneDp);
            layerDrawable.setLayerInsetBottom(1, (int) twoDp);
            layerDrawable.setLayerInsetRight(1, (int) oneDp);
            layerDrawable.setLayerInsetEnd(1, (int) oneDp);
        } else {
            layerDrawable.setLayerInset(1,(int)oneDp,0,(int)oneDp,(int)twoDp);
        }
        return layerDrawable;
    }


    public static Drawable getRightTailTableBackground(Context context, int borderColor, int fillColor, int radiusInDp){
        GradientDrawable borderDrawable = new GradientDrawable();
        borderDrawable.setShape(GradientDrawable.RECTANGLE);
        float radiusInPixel = CommonUtils.getPixelFromDip2(context, radiusInDp);
        borderDrawable.setCornerRadii(new float[]{0,0, 0, 0, radiusInPixel, radiusInPixel, 0,0});
        borderDrawable.setColor(borderColor);

        GradientDrawable fillDrawable = new GradientDrawable();
        fillDrawable.setShape(GradientDrawable.RECTANGLE);
        fillDrawable.setCornerRadii(new float[]{0,0, 0, 0, radiusInPixel, radiusInPixel, 0,0});
        fillDrawable.setColor(fillColor);

        Drawable[] layers = {borderDrawable, fillDrawable};
        LayerDrawable layerDrawable = new LayerDrawable(layers);
        float oneDp = CommonUtils.getPixelFromDip2(context, 1);
        float twoDp = CommonUtils.getPixelFromDip2(context, 2);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            layerDrawable.setLayerInsetLeft(1, (int) oneDp);
            layerDrawable.setLayerInsetStart(1, (int) oneDp);
            layerDrawable.setLayerInsetBottom(1, (int) twoDp);
            layerDrawable.setLayerInsetRight(1, (int) twoDp);
            layerDrawable.setLayerInsetEnd(1, (int) twoDp);
        } else {
            layerDrawable.setLayerInset(1,(int)oneDp,0,(int)twoDp,(int)twoDp);
        }
        return layerDrawable;
    }



    public static class SelectorBuilder {
        public static final int STATE_PRESSED = android.R.attr.state_pressed;
        public static final int STATE_FOCUSED = android.R.attr.state_focused;
        public static final int STATE_SELECTED = android.R.attr.state_selected;
        public static final int STATE_CHECKABLE = android.R.attr.state_checkable;
        public static final int STATE_CHECKED = android.R.attr.state_checked;
        public static final int STATE_ENABLED = android.R.attr.state_enabled;
        public static final int STATE_WINDOW_FOCUSED = android.R.attr.state_window_focused;

        public static StateListDrawable getColorDrawableSelector(@ColorInt int disabledColor, @ColorInt int normalColor, @ColorInt int pressedColor, @ColorInt int selectedColor) {
            StateListDrawable stateListDrawable = new StateListDrawable();
            stateListDrawable.addState(new int[]{STATE_SELECTED}, new ColorDrawable(selectedColor));
            stateListDrawable.addState(new int[]{STATE_PRESSED}, new ColorDrawable(pressedColor));
            stateListDrawable.addState(new int[]{STATE_ENABLED}, new ColorDrawable(normalColor));
            stateListDrawable.addState(new int[]{-STATE_ENABLED}, new ColorDrawable(disabledColor));
            return stateListDrawable;
        }

        public static StateListDrawable getColorDrawableSelector(@ColorInt int disabledColor, @ColorInt int normalColor) {
            StateListDrawable stateListDrawable = new StateListDrawable();
            stateListDrawable.addState(new int[]{STATE_ENABLED}, new ColorDrawable(normalColor));
            stateListDrawable.addState(new int[]{-STATE_ENABLED}, new ColorDrawable(disabledColor));
            return stateListDrawable;
        }

        public static StateListDrawable getCustomDrawableSelector(Drawable disabledDrawable, Drawable normalDrawable, Drawable pressedDrawable, Drawable selectedDrawable) {
            StateListDrawable stateListDrawable = new StateListDrawable();
            if(selectedDrawable != null) {
                stateListDrawable.addState(new int[]{STATE_SELECTED}, selectedDrawable);
            }
            if(pressedDrawable != null) {
                stateListDrawable.addState(new int[]{STATE_PRESSED}, pressedDrawable);
            }
            stateListDrawable.addState(new int[]{STATE_ENABLED}, normalDrawable);
            stateListDrawable.addState(new int[]{-STATE_ENABLED}, disabledDrawable);
            return stateListDrawable;
        }

        public static StateListDrawable getCustomDrawableSelector(Drawable disabledDrawable, Drawable normalDrawable){
            return getCustomDrawableSelector(disabledDrawable,normalDrawable, null, null);
        }

        public static ColorStateList getColorSelector(@ColorInt int disabledColor, @ColorInt int normalColor, @ColorInt int pressedColor, @ColorInt int selectedColor){
            int[][] states = new int[][] {
                    new int[] {STATE_SELECTED},
                    new int[] {STATE_PRESSED},
                    new int[] {STATE_ENABLED},
                    new int[] {-STATE_ENABLED}
            };

            int[] colors = new int[] {
                    selectedColor,
                    pressedColor,
                    normalColor,
                    disabledColor
            };

            ColorStateList colorStateList = new ColorStateList(states, colors);
            return colorStateList;
        }

        public static ColorStateList getColorSelector(@ColorInt int disabledColor, @ColorInt int normalColor){
            int[][] states = new int[][] {
                    new int[] {STATE_ENABLED},
                    new int[] {-STATE_ENABLED}
            };

            int[] colors = new int[] {
                    normalColor,
                    disabledColor
            };

            ColorStateList colorStateList = new ColorStateList(states, colors);
            return colorStateList;
        }

        public static StateListAnimator getAnimationSelector(Animator pressedAnimator, Animator normalAnimator){
            StateListAnimator stateListAnimator = new StateListAnimator();
            stateListAnimator.addState(new int[]{STATE_PRESSED}, pressedAnimator);
            stateListAnimator.addState(new int[]{}, normalAnimator);
            return stateListAnimator;
        }


    }

    public static void enablePushEffectAnim(View targetView){
        AnimatorSet pressAnim = new AnimatorSet();
        ObjectAnimator translateZPressAnim = new ObjectAnimator();
        translateZPressAnim.setPropertyName("translationZ");
        translateZPressAnim.setFloatValues(CommonUtils.getPixelFromDip2(targetView.getContext(), 6));
        translateZPressAnim.setDuration(100);
        ObjectAnimator scaleXPressAnim = new ObjectAnimator();
        scaleXPressAnim.setPropertyName("scaleX");
        scaleXPressAnim.setFloatValues(1.1F);
        scaleXPressAnim.setDuration(100);
        ObjectAnimator scaleYPressAnim = new ObjectAnimator();
        scaleYPressAnim.setPropertyName("scaleY");
        scaleYPressAnim.setFloatValues(1.1F);
        scaleYPressAnim.setDuration(100);
        pressAnim.setTarget(targetView);
        pressAnim.playTogether(translateZPressAnim, scaleXPressAnim, scaleYPressAnim);


        AnimatorSet normalAnim = new AnimatorSet();
        ObjectAnimator translateZNormalAnim = new ObjectAnimator();
        translateZNormalAnim.setPropertyName("translationZ");
        translateZNormalAnim.setFloatValues(CommonUtils.getPixelFromDip2(targetView.getContext(), 0));
        translateZNormalAnim.setDuration(100);
        ObjectAnimator scaleXNormalAnim = new ObjectAnimator();
        scaleXNormalAnim.setPropertyName("scaleX");
        scaleXNormalAnim.setFloatValues(1F);
        scaleXNormalAnim.setDuration(100);
        ObjectAnimator scaleYNormalAnim = new ObjectAnimator();
        scaleYNormalAnim.setPropertyName("scaleY");
        scaleYNormalAnim.setFloatValues(1F);
        scaleYNormalAnim.setDuration(100);
        normalAnim.setTarget(targetView);
        normalAnim.playTogether(translateZNormalAnim, scaleXNormalAnim, scaleYNormalAnim);

        StateListAnimator stateListAnimator = SelectorBuilder.getAnimationSelector(pressAnim, normalAnim);

        targetView.setClickable(true);
        targetView.setStateListAnimator(stateListAnimator);
    }


    public static void setButtonAsRounded(Button targetButton, @DrawableRes int roundedDrawableResId, boolean enablePushEffect){
        Drawable buttonBg = ContextCompat.getDrawable(targetButton.getContext(),roundedDrawableResId);
        setButtonAsRounded(targetButton, buttonBg, enablePushEffect);
    }

    public static void setButtonAsRounded(Button targetButton, Drawable roundedDrawable, boolean enablePushEffect){
        Drawable selectableItemBackgroundDrawable = getSelectableItemBackgroundWithDrawable(targetButton.getContext(), roundedDrawable);
        targetButton.setBackground(selectableItemBackgroundDrawable);
        targetButton.setClipToOutline(true);
        if(enablePushEffect) {
            enablePushEffectAnim(targetButton);
        }
    }



    private static void showCustomDatePicker(@NonNull final EditText editText, @NonNull final String dateFormat, final Date defaultDate, final Locale locale,
                                             @NonNull final FragmentManager fragmentManager, @NonNull final String tag,
                                             final boolean isHideKeyboardForThis, final EditText nextEditText, final boolean isShowKeyboardForNext){
        CommonUtils.showDatePicker(null, fragmentManager, tag, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                SimpleDateFormat sfd = new SimpleDateFormat(dateFormat, locale==null?CommonUtils.getIndonesianLocale():locale);
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);
                editText.setText(sfd.format(defaultDate==null?calendar.getTime():defaultDate));
                editText.setTag(false);

                if(isHideKeyboardForThis){
                    CommonUtils.hideKeyboard(editText.getContext(),editText);
                }

                if(nextEditText != null) {
                    nextEditText.requestFocus();
                    if (isShowKeyboardForNext) {
                        CommonUtils.showKeyboard(nextEditText.getContext());
                    }
                }
            }
        }, Calendar.getInstance().getTime());
    }

    public static void enableDatePicker(@NonNull final EditText editText, @NonNull final String dateFormat, final Date defaultDate, final Locale locale,
                                        @NonNull final FragmentManager fragmentManager, @NonNull final String tag,
                                        final boolean isHideKeyboardForThis, final EditText nextEditText, final boolean isShowKeyboardForNext){
        editText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int inType = editText.getInputType(); // backup the input type
                editText.setInputType(InputType.TYPE_NULL); // disable soft input
                editText.onTouchEvent(event); // call native handler
                editText.setInputType(inType); // restore input type
                if(event.getAction() == MotionEvent.ACTION_UP){
                    if(editText.hasFocus()){
                        if(!((Boolean) v.getTag())) {
                            showCustomDatePicker(editText, dateFormat, defaultDate, locale, fragmentManager, tag, isHideKeyboardForThis, nextEditText, isShowKeyboardForNext);
                        }
                    }
                }
                return true; // consume touch even
            }
        });

        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    v.setTag(true);
                    showCustomDatePicker(editText, dateFormat, defaultDate, locale, fragmentManager, tag, isHideKeyboardForThis, nextEditText, isShowKeyboardForNext);
                } else {
                    v.setTag(false);
                }
            }
        });

        editText.setInputType(InputType.TYPE_CLASS_TEXT);
        editText.setFocusable(true);
    }


    public static void enablePopupMenu(@NonNull final EditText editText, final int menuResID, final boolean isHideKeyboard, final EditText nextEditText, final boolean isShowKeyboardForNext){
        editText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int inType = editText.getInputType(); // backup the input type
                editText.setInputType(InputType.TYPE_NULL); // disable soft input
                editText.onTouchEvent(event); // call native handler
                editText.setInputType(inType); // restore input type
                if(event.getAction() == MotionEvent.ACTION_UP){
                    if(editText.hasFocus()){
                        if(!((Boolean) v.getTag())) {
                            CommonUtils.showPopupMenu(editText.getContext(), menuResID, editText, null,
                                    new PopupMenu.OnMenuItemClickListener() {
                                        @Override
                                        public boolean onMenuItemClick(MenuItem item) {
                                            editText.setText(item.getTitle());
                                            editText.setTag(false);

                                            if(nextEditText != null) {
                                                nextEditText.requestFocus();
                                                if (isShowKeyboardForNext) {
                                                    CommonUtils.showKeyboard(nextEditText.getContext());
                                                }
                                            }
                                            return false;
                                        }
                                    });
                            if(isHideKeyboard){
                                CommonUtils.hideKeyboard(editText.getContext(),editText);
                            }
                        }
                    }
                }

                if(isHideKeyboard){
                    return true;
                }

                return false;
            }
        });

        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    v.setTag(true);
                    CommonUtils.showPopupMenu(editText.getContext(), menuResID, editText, null,
                            new PopupMenu.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem item) {
                                    editText.setText(item.getTitle());
                                    editText.setTag(false);

                                    if(nextEditText != null) {
                                        nextEditText.requestFocus();
                                        if (isShowKeyboardForNext) {
                                            CommonUtils.showKeyboard(nextEditText.getContext());
                                        }
                                    }
                                    return false;
                                }
                            });
                    if(isHideKeyboard){
                        CommonUtils.hideKeyboard(editText.getContext(),editText);
                    }
                } else {
                    v.setTag(false);
                }
            }
        });

        editText.setInputType(InputType.TYPE_CLASS_TEXT);
        editText.setFocusable(true);
    }

}
