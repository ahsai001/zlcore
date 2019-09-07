package com.zaitunlabs.zlcore.views

import android.content.Context
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ItemTouchHelper
import android.util.AttributeSet
import android.view.View

import com.zaitunlabs.zlcore.listeners.RecyclerViewLoadMoreListener
import com.zaitunlabs.zlcore.listeners.SwipeDragCallback

/**
 * Created by ahsai on 7/10/2017.
 */


class CustomRecylerView : RecyclerView {

    private var emptyView: View? = null
    private var recyclerViewLoadMoreListener: RecyclerViewLoadMoreListener? = null

    private val observer = object : RecyclerView.AdapterDataObserver() {
        override fun onChanged() {
            checkIfEmpty()
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            checkIfEmpty()
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            checkIfEmpty()
        }
    }

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet,
                defStyle: Int) : super(context, attrs, defStyle) {
    }

    fun init() {
        val mLayoutManager = LinearLayoutManager(context)
        layoutManager = mLayoutManager
        val itemDecoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        addItemDecoration(itemDecoration)
        itemAnimator = DefaultItemAnimator()
        setHasFixedSize(true)
    }

    internal fun checkIfEmpty() {
        if (emptyView != null && adapter != null) {
            val emptyViewVisible = adapter!!.itemCount == 0
            emptyView!!.visibility = if (emptyViewVisible) View.VISIBLE else View.GONE
            visibility = if (emptyViewVisible) View.GONE else View.VISIBLE
        }
    }

    override fun setAdapter(adapter: RecyclerView.Adapter<*>?) {
        val oldAdapter = getAdapter()
        oldAdapter?.unregisterAdapterDataObserver(observer)
        super.setAdapter(adapter)
        adapter?.registerAdapterDataObserver(observer)
        checkIfEmpty()
        enableSwipeDrag(adapter)
    }

    private fun enableSwipeDrag(adapter: RecyclerView.Adapter<*>?) {
        if (adapter != null && adapter is SwipeDragCallback.SwipeDragInterface) {
            val callback = SwipeDragCallback(adapter as SwipeDragCallback.SwipeDragInterface?)
            val touchHelper = ItemTouchHelper(callback)
            touchHelper.attachToRecyclerView(this)
        }
    }


    fun setEmptyView(emptyView: View) {
        this.emptyView = emptyView
        checkIfEmpty()
    }

    fun addOnLoadMoreListener(listener: RecyclerViewLoadMoreListener?) {
        if (listener != null) {
            recyclerViewLoadMoreListener = listener
            super.addOnScrollListener(listener)
        }
    }

    fun initLoadMore() {
        if (recyclerViewLoadMoreListener != null) {
            recyclerViewLoadMoreListener!!.resetState()
        }
    }
}
