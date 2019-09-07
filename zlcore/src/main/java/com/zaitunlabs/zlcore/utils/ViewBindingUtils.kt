package com.zaitunlabs.zlcore.utils

import androidx.core.widget.NestedScrollView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.HorizontalScrollView
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TextView

import com.zaitunlabs.zlcore.views.CustomRecylerView

import java.util.HashMap

/**
 * Created by ahsai on 7/13/2018.
 */

class ViewBindingUtils<T : View> {
    private val idViewClassMaps: MutableMap<Int, Class<T>>
    private val idViewMaps: MutableMap<Int, View>
    private var parentView: View? = null

    init {
        idViewMaps = HashMap()
        idViewClassMaps = HashMap()
    }

    fun setParentView(parentView: View): ViewBindingUtils<T> {
        this.parentView = parentView
        return this
    }

    fun init(): ViewBindingUtils<T> {
        findViews(parentView!!)
        return this
    }

    private fun findViews(view: View) {
        if (view.id > -1) {
            idViewMaps[view.id] = view
            idViewClassMaps[view.id] = view.javaClass as Class<T>
        }
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val child = view.getChildAt(i)
                findViews(child)
            }
        }
    }

    fun getViewWithId(id: Int): T? {
        return if (idViewMaps.containsKey(id)) {
            idViewClassMaps[id]!!.cast(idViewMaps[id])
        } else null
    }

    fun <T : View> getViewWithId(id: Int, clazz: Class<T>): T? {
        return if (idViewMaps.containsKey(id)) {
            clazz.cast(idViewMaps[id])
        } else null
    }

    fun getTextView(id: Int): TextView? {
        return getViewWithId(id, TextView::class.java)
    }

    fun getButton(id: Int): Button? {
        return getViewWithId(id, Button::class.java)
    }

    fun getImageButton(id: Int): ImageButton? {
        return getViewWithId(id, ImageButton::class.java)
    }

    fun getCardView(id: Int): CardView? {
        return getViewWithId(id, CardView::class.java)
    }

    fun getLinearLayout(id: Int): LinearLayout? {
        return getViewWithId(id, LinearLayout::class.java)
    }

    fun getTableLayout(id: Int): TableLayout? {
        return getViewWithId(id, TableLayout::class.java)
    }

    fun getSwipeRefreshLayout(id: Int): SwipeRefreshLayout? {
        return getViewWithId(id, SwipeRefreshLayout::class.java)
    }

    fun getNestedScrollView(id: Int): NestedScrollView? {
        return getViewWithId(id, NestedScrollView::class.java)
    }

    fun getHorizontalScrollView(id: Int): HorizontalScrollView? {
        return getViewWithId(id, HorizontalScrollView::class.java)
    }

    fun getCustomRecylerView(id: Int): CustomRecylerView? {
        return getViewWithId(id, CustomRecylerView::class.java)
    }

    fun getRecyclerView(id: Int): RecyclerView? {
        return getViewWithId(id, RecyclerView::class.java)
    }

    fun getEditText(id: Int): EditText? {
        return getViewWithId(id, EditText::class.java)
    }

    companion object {

        fun <T : View> initWithParentView(parentView: View): ViewBindingUtils<T> {
            val viewBindingUtils = ViewBindingUtils<T>()
            viewBindingUtils.setParentView(parentView)
            viewBindingUtils.findViews(viewBindingUtils.parentView!!)
            return viewBindingUtils
        }
    }
}
