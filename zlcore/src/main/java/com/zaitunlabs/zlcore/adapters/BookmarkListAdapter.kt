package com.zaitunlabs.zlcore.adapters

import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView

import com.zaitunlabs.zlcore.R
import com.zaitunlabs.zlcore.models.BookmarkModel

/**
 * Created by ahsai on 3/18/2018.
 */

class BookmarkListAdapter(private val bookmarkList: List<BookmarkModel>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val VIEW_ITEM = 1
    private val VIEW_PROG = 0
    private var showProgress = false

    private val allItemCount: Int
        get() = bookmarkList.size + if (showProgress) 1 else 0


    private var onMoreOptionClickListener: OnMoreOptionClickListener? = null

    private var onCardClickListener: OnCardClickListener? = null

    private inner class BookmarkListViewHolder private constructor(view: View) : RecyclerView.ViewHolder(view) {
        internal var rootView: CardView
        internal var titleView: TextView
        internal var bodyView: TextView
        internal var optionView: ImageButton

        init {
            rootView = view as CardView
            titleView = view.findViewById<View>(R.id.bookmarklist_item_row_titleView) as TextView
            bodyView = view.findViewById<View>(R.id.bookmarklist_item_row_descView) as TextView
            optionView = view.findViewById<View>(R.id.bookmarklist_item_row_optionView) as ImageButton
        }
    }

    private inner class ProgressViewHolder private constructor(view: View) : RecyclerView.ViewHolder(view) {
        internal var descView: TextView

        init {
            descView = view.findViewById<View>(R.id.progress_custom_textview) as TextView
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == VIEW_ITEM) {
            val rootView = LayoutInflater.from(parent.context).inflate(R.layout.bookmark_list_item_row, parent, false)
            return BookmarkListViewHolder(rootView)
        } else if (viewType == VIEW_PROG) {
            val rootView = LayoutInflater.from(parent.context).inflate(R.layout.progressbar_vertical_center, parent, false)
            return ProgressViewHolder(rootView)
        }
        return null
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder == null) return
        if (position < bookmarkList.size) {
            val appListDataModel = bookmarkList[position]
            (holder as BookmarkListViewHolder).titleView.text = appListDataModel.title
            holder.bodyView.text = appListDataModel.desc
            holder.bodyView.visibility = View.GONE
            holder.optionView
                    .setOnClickListener { view ->
                        if (onMoreOptionClickListener != null) {
                            onMoreOptionClickListener!!.onClick(view, holder.getAdapterPosition())
                        }
                    }

            holder.rootView.setOnClickListener { view ->
                if (onCardClickListener != null) {
                    onCardClickListener!!.onClick(view, holder.getAdapterPosition())
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < bookmarkList.size) VIEW_ITEM else VIEW_PROG
    }

    override fun getItemCount(): Int {
        return allItemCount
    }

    fun showProgress() {
        showProgress = true
        notifyDataSetChanged()
    }

    fun hideProgress() {
        showProgress = false
        notifyDataSetChanged()
    }

    fun setOnMoreOptionClickListener(onMoreOptionClickListener: OnMoreOptionClickListener) {
        this.onMoreOptionClickListener = onMoreOptionClickListener
    }

    interface OnMoreOptionClickListener {
        fun onClick(view: View, position: Int)
    }


    fun setOnCardClickListener(onCardClickListener: OnCardClickListener) {
        this.onCardClickListener = onCardClickListener
    }

    interface OnCardClickListener {
        fun onClick(view: View, position: Int)
    }
}
