package com.zaitunlabs.zlcore.core

import android.graphics.Color
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ItemTouchHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.zaitunlabs.zlcore.R
import com.zaitunlabs.zlcore.listeners.SwipeDragCallback
import com.zaitunlabs.zlcore.utils.CommonUtils

import java.util.Collections

/**
 * Created by ahsai on 5/30/2018.
 */

abstract class BaseRecyclerViewAdapter<DM, HV : RecyclerView.ViewHolder>(protected var modelList: MutableList<DM>) : RecyclerView.Adapter<*>(), SwipeDragCallback.SwipeDragInterface {
    private val VIEW_ITEM = 1
    private val VIEW_PROG = 0
    private var showProgress = false
    protected abstract val layout: Int

    private val allItemCount: Int
        get() = modelList.size + if (showProgress) 1 else 0


    override val isItemViewSwipeEnabled: Boolean
        get() = false

    override val isLongPressDragEnabled: Boolean
        get() = false

    private var onChildViewClickListener: OnChildViewClickListener<DM>? = null
    protected abstract fun getViewHolder(rootView: View): HV
    protected abstract fun doSettingViewWithModel(holder: HV, dataModel: DM, position: Int)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == VIEW_ITEM) {
            val rootView = LayoutInflater.from(parent.context).inflate(layout, parent, false)
            return getViewHolder(rootView)
        } else if (viewType == VIEW_PROG) {
            val rootView = LayoutInflater.from(parent.context).inflate(R.layout.progressbar_vertical_center, parent, false)
            return ProgressViewHolder(rootView)
        }
        return null
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder == null) return
        if (position < modelList.size) {
            doSettingViewWithModel(holder as HV, modelList[position], position)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < modelList.size) VIEW_ITEM else VIEW_PROG
    }

    override fun getItemCount(): Int {
        return allItemCount
    }

    fun showLoadMoreProgress() {
        showProgress = true
        notifyDataSetChanged()
    }

    fun hideLoadMoreProgress() {
        showProgress = false
        notifyDataSetChanged()
    }

    private inner class ProgressViewHolder private constructor(view: View) : RecyclerView.ViewHolder(view) {
        internal var descView: TextView

        init {
            descView = view.findViewById<View>(R.id.progress_custom_textview) as TextView
        }
    }

    protected fun setViewClickable(viewHolder: HV, view: View) {
        view.setOnClickListener { view ->
            if (onChildViewClickListener != null) {
                val position = viewHolder.adapterPosition
                onChildViewClickListener!!.onClick(view, modelList[position], position)
            }
        }
    }

    override fun onItemDrag(viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder) {
        val fromPosition = viewHolder.adapterPosition
        val toPosition = target.adapterPosition
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(modelList, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(modelList, i, i - 1)
            }
        }
        notifyItemMoved(fromPosition, toPosition)
    }

    override fun onItemSwipe(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition
        CommonUtils.showDialog2Option(viewHolder.itemView.context,
                viewHolder.itemView.context.getString(R.string.zlcore_base_recyclerview_adapter_delete_confirmation),
                viewHolder.itemView.context.getString(R.string.zlcore_base_recyclerview_adapter_delete_confirmation_message),
                viewHolder.itemView.context.getString(R.string.zlcore_general_wording_delete), {
            modelList.removeAt(position)
            notifyItemRemoved(position)
        }, viewHolder.itemView.context.getString(R.string.zlcore_general_wording_cancel), { notifyItemChanged(position) })
    }

    override fun swipeLeftColor(): Int {
        return Color.GREEN
    }

    override fun swipeLeftTextColor(): Int {
        return Color.WHITE
    }

    override fun swipeLeftTextString(): String? {
        return null
    }

    override fun swipeRightColor(): Int {
        return Color.RED
    }

    override fun swipeRightTextColor(): Int {
        return Color.WHITE
    }

    override fun swipeRightTextString(): String? {
        return null
    }

    override fun swipeFlags(): Int {
        return ItemTouchHelper.LEFT or ItemTouchHelper.START or ItemTouchHelper.RIGHT or ItemTouchHelper.END
    }

    fun setOnChildViewClickListener(onChildViewClickListener: OnChildViewClickListener<DM>) {
        this.onChildViewClickListener = onChildViewClickListener
    }

    interface OnChildViewClickListener<DM> {
        fun onClick(view: View, dataModel: DM, position: Int)
    }
}
