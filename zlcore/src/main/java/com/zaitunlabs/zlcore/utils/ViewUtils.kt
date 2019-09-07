package com.zaitunlabs.zlcore.utils

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.StateListAnimator
import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.res.ColorStateList
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.StateListDrawable
import android.os.Build
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager

import android.text.InputType
import android.text.TextUtils
import android.util.TypedValue
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.HashSet
import java.util.Locale

/**
 * @author upshots.org 5/27/16.
 */

/**
 * Created by ahsai on 4/25/2018.
 */

object ViewUtils {

    /**
     * Finds the first child in #rootView that is an instance of #clazz
     *
     * @param rootView The View whose hierarchy should be examined for instances of #clazz.
     * @param clazz    The Class to search for within #rootView.
     * @param <T>      The type of View subclass to search for.
     * @return The first child in #rootView this is an instance of #clazz.
    </T> */
    fun <T : View> findViewByClassReference(rootView: View, clazz: Class<T>): T? {
        if (clazz.isInstance(rootView)) {
            return clazz.cast(rootView)
        }
        if (rootView is ViewGroup) {
            for (i in 0 until rootView.childCount) {
                val child = rootView.getChildAt(i)
                val match = findViewByClassReference(child, clazz)
                if (match != null) {
                    return match
                }
            }
        }
        return null
    }

    /**
     * Returns a Collection of View subclasses instances of type T found within #rootView.
     *
     * @param rootView The View whose hierarchy should be examined for instances of #clazz.
     * @param clazz    The Class to search for within #rootView.
     * @param out      A Collection of View subclasses of type T that will be populated with matches found in #rootView.
     * @param <T>      The type of View subclass to search for.
     * @return A Collection of View subclasses instances of type T found within #rootView.
    </T> */
    fun <T : View> findViewsByClassReference(rootView: View, clazz: Class<T>, out: MutableCollection<T>?): Collection<T> {
        var out = out
        if (out == null) {
            out = HashSet()
        }
        if (clazz.isInstance(rootView)) {
            out.add(clazz.cast(rootView))
        }
        if (rootView is ViewGroup) {
            for (i in 0 until rootView.childCount) {
                val child = rootView.getChildAt(i)
                findViewsByClassReference(child, clazz, out)
            }
        }
        return out
    }

    /**
     * Returns a Collection of View subclasses instances of type T found within #rootView.
     *
     * @param rootView The View whose hierarchy should be examined for instances of #clazz.
     * @param clazz    The Class to search for within #rootView.
     * @param <T>      The type of View subclass to search for.
     * @return A Collection of View subclasses instances of type T found within #rootView.
    </T> */
    fun <T : View> findViewsByClassReference(rootView: View, clazz: Class<T>): Collection<T> {
        return findViewsByClassReference(rootView, clazz, null)
    }

    fun getSelectableItemBackgroundResID(context: Context): Int {
        val outValue = TypedValue()
        context.theme.resolveAttribute(android.R.attr.selectableItemBackground, outValue, true)
        return outValue.resourceId
    }

    fun getSelectableItemBackgroundBorderLessResID(context: Context): Int {
        val outValue = TypedValue()
        context.theme.resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, outValue, true)
        return outValue.resourceId
    }

    fun getSelectableItemBackgroundDrawable(context: Context): Drawable? {
        return ContextCompat.getDrawable(context, getSelectableItemBackgroundResID(context))
    }

    fun getSelectableItemBackgroundBorderLessDrawable(context: Context): Drawable? {
        return ContextCompat.getDrawable(context, getSelectableItemBackgroundBorderLessResID(context))
    }

    fun getSelectableItemBackgroundWithDrawable(context: Context, drawable: Drawable?): Drawable {
        val layers = arrayOf<Drawable>(drawable, getSelectableItemBackgroundDrawable(context))
        return LayerDrawable(layers)
    }

    fun getSelectableItemBackgroundBorderLessWithDrawable(context: Context, drawable: Drawable): Drawable {
        val layers = arrayOf<Drawable>(drawable, getSelectableItemBackgroundBorderLessDrawable(context))
        return LayerDrawable(layers)
    }

    fun getSelectableItemBackgroundWithColor(context: Context, color: Int): Drawable {
        val colorDrawable = ColorDrawable(color)
        return getSelectableItemBackgroundWithDrawable(context, colorDrawable)
    }

    fun getSelectableItemBackgroundBorderLessWithColor(context: Context, color: Int): Drawable {
        val colorDrawable = ColorDrawable(color)
        return getSelectableItemBackgroundBorderLessWithDrawable(context, colorDrawable)
    }

    fun getLeftHeadTableBackground(context: Context, borderColor: Int, fillColor: Int, radiusInDp: Int): Drawable {
        val borderDrawable = GradientDrawable()
        borderDrawable.shape = GradientDrawable.RECTANGLE
        val radiusInPixel = CommonUtils.getPixelFromDip2(context, radiusInDp).toFloat()
        borderDrawable.cornerRadii = floatArrayOf(radiusInPixel, radiusInPixel, 0f, 0f, 0f, 0f, 0f, 0f)
        borderDrawable.setColor(borderColor)

        val fillDrawable = GradientDrawable()
        fillDrawable.shape = GradientDrawable.RECTANGLE
        fillDrawable.cornerRadii = floatArrayOf(radiusInPixel, radiusInPixel, 0f, 0f, 0f, 0f, 0f, 0f)
        fillDrawable.setColor(fillColor)

        val layers = arrayOf<Drawable>(borderDrawable, fillDrawable)
        val layerDrawable = LayerDrawable(layers)
        val oneDp = CommonUtils.getPixelFromDip2(context, 1).toFloat()
        val twoDp = CommonUtils.getPixelFromDip2(context, 2).toFloat()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            layerDrawable.setLayerInsetLeft(1, twoDp.toInt())
            layerDrawable.setLayerInsetStart(1, twoDp.toInt())
            layerDrawable.setLayerInsetTop(1, twoDp.toInt())
            layerDrawable.setLayerInsetBottom(1, twoDp.toInt())
            layerDrawable.setLayerInsetRight(1, oneDp.toInt())
            layerDrawable.setLayerInsetEnd(1, oneDp.toInt())
        } else {
            layerDrawable.setLayerInset(1, twoDp.toInt(), twoDp.toInt(), oneDp.toInt(), twoDp.toInt())
        }
        return layerDrawable
    }

    fun getCenterHeadTableBackground(context: Context, borderColor: Int, fillColor: Int): Drawable {
        val borderDrawable = GradientDrawable()
        borderDrawable.shape = GradientDrawable.RECTANGLE
        borderDrawable.setColor(borderColor)

        val fillDrawable = GradientDrawable()
        fillDrawable.shape = GradientDrawable.RECTANGLE
        fillDrawable.setColor(fillColor)

        val layers = arrayOf<Drawable>(borderDrawable, fillDrawable)
        val layerDrawable = LayerDrawable(layers)
        val oneDp = CommonUtils.getPixelFromDip2(context, 1).toFloat()
        val twoDp = CommonUtils.getPixelFromDip2(context, 2).toFloat()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            layerDrawable.setLayerInsetLeft(1, oneDp.toInt())
            layerDrawable.setLayerInsetStart(1, oneDp.toInt())
            layerDrawable.setLayerInsetTop(1, twoDp.toInt())
            layerDrawable.setLayerInsetBottom(1, twoDp.toInt())
            layerDrawable.setLayerInsetRight(1, oneDp.toInt())
            layerDrawable.setLayerInsetEnd(1, oneDp.toInt())
        } else {
            layerDrawable.setLayerInset(1, oneDp.toInt(), twoDp.toInt(), oneDp.toInt(), twoDp.toInt())
        }
        return layerDrawable
    }

    fun getRightHeadTableBackground(context: Context, borderColor: Int, fillColor: Int, radiusInDp: Int): Drawable {
        val borderDrawable = GradientDrawable()
        borderDrawable.shape = GradientDrawable.RECTANGLE
        val radiusInPixel = CommonUtils.getPixelFromDip2(context, radiusInDp).toFloat()
        borderDrawable.cornerRadii = floatArrayOf(0f, 0f, radiusInPixel, radiusInPixel, 0f, 0f, 0f, 0f)
        borderDrawable.setColor(borderColor)

        val fillDrawable = GradientDrawable()
        fillDrawable.shape = GradientDrawable.RECTANGLE
        fillDrawable.cornerRadii = floatArrayOf(0f, 0f, radiusInPixel, radiusInPixel, 0f, 0f, 0f, 0f)
        fillDrawable.setColor(fillColor)

        val layers = arrayOf<Drawable>(borderDrawable, fillDrawable)
        val layerDrawable = LayerDrawable(layers)
        val oneDp = CommonUtils.getPixelFromDip2(context, 1).toFloat()
        val twoDp = CommonUtils.getPixelFromDip2(context, 2).toFloat()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            layerDrawable.setLayerInsetLeft(1, oneDp.toInt())
            layerDrawable.setLayerInsetStart(1, oneDp.toInt())
            layerDrawable.setLayerInsetTop(1, twoDp.toInt())
            layerDrawable.setLayerInsetBottom(1, twoDp.toInt())
            layerDrawable.setLayerInsetRight(1, twoDp.toInt())
            layerDrawable.setLayerInsetEnd(1, twoDp.toInt())
        } else {
            layerDrawable.setLayerInset(1, oneDp.toInt(), twoDp.toInt(), twoDp.toInt(), twoDp.toInt())
        }
        return layerDrawable
    }

    fun getLeftBodyTableBackground(context: Context, borderColor: Int, fillColor: Int): Drawable {
        val borderDrawable = GradientDrawable()
        borderDrawable.shape = GradientDrawable.RECTANGLE
        borderDrawable.setColor(borderColor)

        val fillDrawable = GradientDrawable()
        fillDrawable.shape = GradientDrawable.RECTANGLE
        fillDrawable.setColor(fillColor)

        val layers = arrayOf<Drawable>(borderDrawable, fillDrawable)
        val layerDrawable = LayerDrawable(layers)
        val oneDp = CommonUtils.getPixelFromDip2(context, 1).toFloat()
        val twoDp = CommonUtils.getPixelFromDip2(context, 2).toFloat()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            layerDrawable.setLayerInsetLeft(1, twoDp.toInt())
            layerDrawable.setLayerInsetStart(1, twoDp.toInt())
            layerDrawable.setLayerInsetRight(1, oneDp.toInt())
            layerDrawable.setLayerInsetEnd(1, oneDp.toInt())
        } else {
            layerDrawable.setLayerInset(1, twoDp.toInt(), 0, oneDp.toInt(), 0)
        }
        return layerDrawable
    }

    fun getCenterBodyTableBackground(context: Context, borderColor: Int, fillColor: Int): Drawable {
        val borderDrawable = GradientDrawable()
        borderDrawable.shape = GradientDrawable.RECTANGLE
        borderDrawable.setColor(borderColor)

        val fillDrawable = GradientDrawable()
        fillDrawable.shape = GradientDrawable.RECTANGLE
        fillDrawable.setColor(fillColor)

        val layers = arrayOf<Drawable>(borderDrawable, fillDrawable)
        val layerDrawable = LayerDrawable(layers)
        val oneDp = CommonUtils.getPixelFromDip2(context, 1).toFloat()
        val twoDp = CommonUtils.getPixelFromDip2(context, 2).toFloat()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            layerDrawable.setLayerInsetLeft(1, oneDp.toInt())
            layerDrawable.setLayerInsetStart(1, oneDp.toInt())
            layerDrawable.setLayerInsetRight(1, oneDp.toInt())
            layerDrawable.setLayerInsetEnd(1, oneDp.toInt())
        } else {
            layerDrawable.setLayerInset(1, oneDp.toInt(), 0, oneDp.toInt(), 0)
        }
        return layerDrawable
    }

    fun getRightBodyTableBackground(context: Context, borderColor: Int, fillColor: Int): Drawable {
        val borderDrawable = GradientDrawable()
        borderDrawable.shape = GradientDrawable.RECTANGLE
        borderDrawable.setColor(borderColor)

        val fillDrawable = GradientDrawable()
        fillDrawable.shape = GradientDrawable.RECTANGLE
        fillDrawable.setColor(fillColor)

        val layers = arrayOf<Drawable>(borderDrawable, fillDrawable)
        val layerDrawable = LayerDrawable(layers)
        val oneDp = CommonUtils.getPixelFromDip2(context, 1).toFloat()
        val twoDp = CommonUtils.getPixelFromDip2(context, 2).toFloat()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            layerDrawable.setLayerInsetLeft(1, oneDp.toInt())
            layerDrawable.setLayerInsetStart(1, oneDp.toInt())
            layerDrawable.setLayerInsetRight(1, twoDp.toInt())
            layerDrawable.setLayerInsetEnd(1, twoDp.toInt())
        } else {
            layerDrawable.setLayerInset(1, oneDp.toInt(), 0, twoDp.toInt(), 0)
        }
        return layerDrawable
    }


    fun getLeftTailTableBackground(context: Context, borderColor: Int, fillColor: Int, radiusInDp: Int): Drawable {
        val borderDrawable = GradientDrawable()
        borderDrawable.shape = GradientDrawable.RECTANGLE
        val radiusInPixel = CommonUtils.getPixelFromDip2(context, radiusInDp).toFloat()
        borderDrawable.cornerRadii = floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, radiusInPixel, radiusInPixel)
        borderDrawable.setColor(borderColor)

        val fillDrawable = GradientDrawable()
        fillDrawable.shape = GradientDrawable.RECTANGLE
        fillDrawable.cornerRadii = floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, radiusInPixel, radiusInPixel)
        fillDrawable.setColor(fillColor)

        val layers = arrayOf<Drawable>(borderDrawable, fillDrawable)
        val layerDrawable = LayerDrawable(layers)
        val oneDp = CommonUtils.getPixelFromDip2(context, 1).toFloat()
        val twoDp = CommonUtils.getPixelFromDip2(context, 2).toFloat()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            layerDrawable.setLayerInsetLeft(1, twoDp.toInt())
            layerDrawable.setLayerInsetStart(1, twoDp.toInt())
            layerDrawable.setLayerInsetBottom(1, twoDp.toInt())
            layerDrawable.setLayerInsetRight(1, oneDp.toInt())
            layerDrawable.setLayerInsetEnd(1, oneDp.toInt())
        } else {
            layerDrawable.setLayerInset(1, twoDp.toInt(), 0, oneDp.toInt(), twoDp.toInt())
        }
        return layerDrawable
    }

    fun getCenterTailTableBackground(context: Context, borderColor: Int, fillColor: Int): Drawable {
        val borderDrawable = GradientDrawable()
        borderDrawable.shape = GradientDrawable.RECTANGLE
        borderDrawable.setColor(borderColor)

        val fillDrawable = GradientDrawable()
        fillDrawable.shape = GradientDrawable.RECTANGLE
        fillDrawable.setColor(fillColor)

        val layers = arrayOf<Drawable>(borderDrawable, fillDrawable)
        val layerDrawable = LayerDrawable(layers)
        val oneDp = CommonUtils.getPixelFromDip2(context, 1).toFloat()
        val twoDp = CommonUtils.getPixelFromDip2(context, 2).toFloat()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            layerDrawable.setLayerInsetLeft(1, oneDp.toInt())
            layerDrawable.setLayerInsetStart(1, oneDp.toInt())
            layerDrawable.setLayerInsetBottom(1, twoDp.toInt())
            layerDrawable.setLayerInsetRight(1, oneDp.toInt())
            layerDrawable.setLayerInsetEnd(1, oneDp.toInt())
        } else {
            layerDrawable.setLayerInset(1, oneDp.toInt(), 0, oneDp.toInt(), twoDp.toInt())
        }
        return layerDrawable
    }


    fun getRightTailTableBackground(context: Context, borderColor: Int, fillColor: Int, radiusInDp: Int): Drawable {
        val borderDrawable = GradientDrawable()
        borderDrawable.shape = GradientDrawable.RECTANGLE
        val radiusInPixel = CommonUtils.getPixelFromDip2(context, radiusInDp).toFloat()
        borderDrawable.cornerRadii = floatArrayOf(0f, 0f, 0f, 0f, radiusInPixel, radiusInPixel, 0f, 0f)
        borderDrawable.setColor(borderColor)

        val fillDrawable = GradientDrawable()
        fillDrawable.shape = GradientDrawable.RECTANGLE
        fillDrawable.cornerRadii = floatArrayOf(0f, 0f, 0f, 0f, radiusInPixel, radiusInPixel, 0f, 0f)
        fillDrawable.setColor(fillColor)

        val layers = arrayOf<Drawable>(borderDrawable, fillDrawable)
        val layerDrawable = LayerDrawable(layers)
        val oneDp = CommonUtils.getPixelFromDip2(context, 1).toFloat()
        val twoDp = CommonUtils.getPixelFromDip2(context, 2).toFloat()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            layerDrawable.setLayerInsetLeft(1, oneDp.toInt())
            layerDrawable.setLayerInsetStart(1, oneDp.toInt())
            layerDrawable.setLayerInsetBottom(1, twoDp.toInt())
            layerDrawable.setLayerInsetRight(1, twoDp.toInt())
            layerDrawable.setLayerInsetEnd(1, twoDp.toInt())
        } else {
            layerDrawable.setLayerInset(1, oneDp.toInt(), 0, twoDp.toInt(), twoDp.toInt())
        }
        return layerDrawable
    }


    object SelectorBuilder {
        val STATE_PRESSED = android.R.attr.state_pressed
        val STATE_FOCUSED = android.R.attr.state_focused
        val STATE_SELECTED = android.R.attr.state_selected
        val STATE_CHECKABLE = android.R.attr.state_checkable
        val STATE_CHECKED = android.R.attr.state_checked
        val STATE_ENABLED = android.R.attr.state_enabled
        val STATE_WINDOW_FOCUSED = android.R.attr.state_window_focused

        fun getColorDrawableSelector(@ColorInt disabledColor: Int, @ColorInt normalColor: Int, @ColorInt pressedColor: Int, @ColorInt selectedColor: Int): StateListDrawable {
            val stateListDrawable = StateListDrawable()
            stateListDrawable.addState(intArrayOf(STATE_SELECTED), ColorDrawable(selectedColor))
            stateListDrawable.addState(intArrayOf(STATE_PRESSED), ColorDrawable(pressedColor))
            stateListDrawable.addState(intArrayOf(STATE_ENABLED), ColorDrawable(normalColor))
            stateListDrawable.addState(intArrayOf(-STATE_ENABLED), ColorDrawable(disabledColor))
            return stateListDrawable
        }

        fun getColorDrawableSelector(@ColorInt disabledColor: Int, @ColorInt normalColor: Int): StateListDrawable {
            val stateListDrawable = StateListDrawable()
            stateListDrawable.addState(intArrayOf(STATE_ENABLED), ColorDrawable(normalColor))
            stateListDrawable.addState(intArrayOf(-STATE_ENABLED), ColorDrawable(disabledColor))
            return stateListDrawable
        }

        @JvmOverloads
        fun getCustomDrawableSelector(disabledDrawable: Drawable, normalDrawable: Drawable, pressedDrawable: Drawable? = null, selectedDrawable: Drawable? = null): StateListDrawable {
            val stateListDrawable = StateListDrawable()
            if (selectedDrawable != null) {
                stateListDrawable.addState(intArrayOf(STATE_SELECTED), selectedDrawable)
            }
            if (pressedDrawable != null) {
                stateListDrawable.addState(intArrayOf(STATE_PRESSED), pressedDrawable)
            }
            stateListDrawable.addState(intArrayOf(STATE_ENABLED), normalDrawable)
            stateListDrawable.addState(intArrayOf(-STATE_ENABLED), disabledDrawable)
            return stateListDrawable
        }

        fun getColorSelector(@ColorInt disabledColor: Int, @ColorInt normalColor: Int, @ColorInt pressedColor: Int, @ColorInt selectedColor: Int): ColorStateList {
            val states = arrayOf(intArrayOf(STATE_SELECTED), intArrayOf(STATE_PRESSED), intArrayOf(STATE_ENABLED), intArrayOf(-STATE_ENABLED))

            val colors = intArrayOf(selectedColor, pressedColor, normalColor, disabledColor)

            return ColorStateList(states, colors)
        }

        fun getColorSelector(@ColorInt disabledColor: Int, @ColorInt normalColor: Int): ColorStateList {
            val states = arrayOf(intArrayOf(STATE_ENABLED), intArrayOf(-STATE_ENABLED))

            val colors = intArrayOf(normalColor, disabledColor)

            return ColorStateList(states, colors)
        }

        fun getAnimationSelector(pressedAnimator: Animator, normalAnimator: Animator): StateListAnimator {
            val stateListAnimator = StateListAnimator()
            stateListAnimator.addState(intArrayOf(STATE_PRESSED), pressedAnimator)
            stateListAnimator.addState(intArrayOf(), normalAnimator)
            return stateListAnimator
        }


    }

    fun enablePushEffectAnim(targetView: View) {
        val pressAnim = AnimatorSet()
        val translateZPressAnim = ObjectAnimator()
        translateZPressAnim.propertyName = "translationZ"
        translateZPressAnim.setFloatValues(CommonUtils.getPixelFromDip2(targetView.context, 6))
        translateZPressAnim.duration = 100
        val scaleXPressAnim = ObjectAnimator()
        scaleXPressAnim.propertyName = "scaleX"
        scaleXPressAnim.setFloatValues(1.1f)
        scaleXPressAnim.duration = 100
        val scaleYPressAnim = ObjectAnimator()
        scaleYPressAnim.propertyName = "scaleY"
        scaleYPressAnim.setFloatValues(1.1f)
        scaleYPressAnim.duration = 100
        pressAnim.setTarget(targetView)
        pressAnim.playTogether(translateZPressAnim, scaleXPressAnim, scaleYPressAnim)


        val normalAnim = AnimatorSet()
        val translateZNormalAnim = ObjectAnimator()
        translateZNormalAnim.propertyName = "translationZ"
        translateZNormalAnim.setFloatValues(CommonUtils.getPixelFromDip2(targetView.context, 0))
        translateZNormalAnim.duration = 100
        val scaleXNormalAnim = ObjectAnimator()
        scaleXNormalAnim.propertyName = "scaleX"
        scaleXNormalAnim.setFloatValues(1f)
        scaleXNormalAnim.duration = 100
        val scaleYNormalAnim = ObjectAnimator()
        scaleYNormalAnim.propertyName = "scaleY"
        scaleYNormalAnim.setFloatValues(1f)
        scaleYNormalAnim.duration = 100
        normalAnim.setTarget(targetView)
        normalAnim.playTogether(translateZNormalAnim, scaleXNormalAnim, scaleYNormalAnim)

        val stateListAnimator = SelectorBuilder.getAnimationSelector(pressAnim, normalAnim)

        targetView.isClickable = true
        targetView.stateListAnimator = stateListAnimator
    }


    fun setButtonAsRounded(targetButton: Button, @DrawableRes roundedDrawableResId: Int, enablePushEffect: Boolean) {
        val buttonBg = ContextCompat.getDrawable(targetButton.context, roundedDrawableResId)
        setButtonAsRounded(targetButton, buttonBg, enablePushEffect)
    }

    fun setButtonAsRounded(targetButton: Button, roundedDrawable: Drawable?, enablePushEffect: Boolean) {
        val selectableItemBackgroundDrawable = getSelectableItemBackgroundWithDrawable(targetButton.context, roundedDrawable)
        targetButton.background = selectableItemBackgroundDrawable
        targetButton.clipToOutline = true
        if (enablePushEffect) {
            enablePushEffectAnim(targetButton)
        }
    }


    private fun showCustomDatePicker(editText: EditText, dateFormat: String, locale: Locale?,
                                     fragmentManager: FragmentManager, tag: String,
                                     isHideKeyboardForThis: Boolean, nextEditText: EditText?, isShowKeyboardForNext: Boolean) {


        var defaultDate: Date? = Calendar.getInstance().time
        val dateStringFromEditText = editText.text.toString()
        if (!TextUtils.isEmpty(dateStringFromEditText)) {
            defaultDate = DateStringUtils.getDateFromString(dateFormat, dateStringFromEditText, locale
                    ?: Locale.getDefault())
        }

        CommonUtils.showDatePicker(null, fragmentManager, tag, { view, year, month, dayOfMonth ->
            val sfd = SimpleDateFormat(dateFormat, locale ?: Locale.getDefault())
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            editText.setText(sfd.format(calendar.time))
            editText.error = null
            editText.tag = false

            if (isHideKeyboardForThis) {
                CommonUtils.hideKeyboard(editText.context, editText)
            }

            if (nextEditText != null) {
                nextEditText.requestFocus()
                if (isShowKeyboardForNext) {
                    CommonUtils.showKeyboard(nextEditText.context)
                }
            }
        }, { editText.tag = false }, defaultDate)
    }

    fun enableDatePicker(editText: EditText, dateFormat: String, locale: Locale,
                         fragmentManager: FragmentManager, tag: String,
                         isHideKeyboardForThis: Boolean, nextEditText: EditText, isShowKeyboardForNext: Boolean) {
        editText.setOnTouchListener { v, event ->
            val inType = editText.inputType // backup the input type
            editText.inputType = InputType.TYPE_NULL // disable soft input
            editText.onTouchEvent(event) // call native handler
            editText.inputType = inType // restore input type
            if (event.action == MotionEvent.ACTION_UP) {
                if (editText.hasFocus()) {
                    if (!(v.tag as Boolean)) {
                        showCustomDatePicker(editText, dateFormat, locale, fragmentManager, tag, isHideKeyboardForThis, nextEditText, isShowKeyboardForNext)
                    }
                }
            }
            true // consume touch even
        }

        editText.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                v.tag = true
                showCustomDatePicker(editText, dateFormat, locale, fragmentManager, tag, isHideKeyboardForThis, nextEditText, isShowKeyboardForNext)
            } else {
                v.tag = false
            }
        }

        editText.inputType = InputType.TYPE_CLASS_TEXT
        editText.isFocusable = true
    }


    fun enablePopupMenu(editText: EditText, menuResID: Int, isHideKeyboardThis: Boolean, nextEditText: EditText?, isShowKeyboardForNext: Boolean) {
        editText.setOnTouchListener { v, event ->
            val inType = editText.inputType // backup the input type
            editText.inputType = InputType.TYPE_NULL // disable soft input
            editText.onTouchEvent(event) // call native handler
            editText.inputType = inType // restore input type
            if (event.action == MotionEvent.ACTION_UP) {
                if (editText.hasFocus()) {
                    if (!(v.tag as Boolean)) {
                        CommonUtils.showPopupMenu(editText.context, menuResID, editText, { editText.tag = false },
                                { item ->
                                    editText.setText(item.title)
                                    editText.tag = false

                                    if (nextEditText != null) {
                                        nextEditText.requestFocus()
                                        if (isShowKeyboardForNext) {
                                            CommonUtils.showKeyboard(nextEditText.context)
                                        }
                                    }
                                    false
                                })
                        if (isHideKeyboardThis) {
                            CommonUtils.hideKeyboard(editText.context, editText)
                        }
                    }
                }
            }

            isHideKeyboardThis
        }

        editText.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                v.tag = true
                CommonUtils.showPopupMenu(editText.context, menuResID, editText, { editText.tag = false },
                        { item ->
                            editText.setText(item.title)
                            editText.tag = false

                            if (nextEditText != null) {
                                nextEditText.requestFocus()
                                if (isShowKeyboardForNext) {
                                    CommonUtils.showKeyboard(nextEditText.context)
                                }
                            }
                            false
                        })
                if (isHideKeyboardThis) {
                    CommonUtils.hideKeyboard(editText.context, editText)
                }
            } else {
                v.tag = false
            }
        }

        editText.inputType = InputType.TYPE_CLASS_TEXT
        editText.isFocusable = true
    }

}
