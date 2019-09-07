package com.zaitunlabs.zlcore.adapters

import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import com.squareup.picasso.Picasso
import com.zaitunlabs.zlcore.R
import com.zaitunlabs.zlcore.models.StoreDataModel

/**
 * Created by ahsai on 3/18/2018.
 */

class StoreAdapter(private val appList: List<StoreDataModel>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val VIEW_ITEM = 1
    private val VIEW_PROG = 0
    private var showProgress = false

    private val allItemCount: Int
        get() = appList.size + if (showProgress) 1 else 0

    private inner class StoreViewHolder private constructor(view: View) : RecyclerView.ViewHolder(view) {
        internal var rootView: CardView
        internal var titleView: TextView
        internal var bodyView: TextView
        internal var imageView: ImageView

        init {
            rootView = view as CardView
            titleView = view.findViewById<View>(R.id.applist_item_row_titleView) as TextView
            bodyView = view.findViewById<View>(R.id.applist_item_row_descView) as TextView
            imageView = view.findViewById<View>(R.id.applist_item_row_imageView) as ImageView
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
            val rootView = LayoutInflater.from(parent.context).inflate(R.layout.store_item_row, parent, false)
            return StoreViewHolder(rootView)
        } else if (viewType == VIEW_PROG) {
            val rootView = LayoutInflater.from(parent.context).inflate(R.layout.progressbar_vertical_center, parent, false)
            return ProgressViewHolder(rootView)
        }
        return null
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder == null) return
        if (position < appList.size) {
            val appListDataModel = appList[position]
            (holder as StoreViewHolder).titleView.text = appListDataModel.title
            holder.bodyView.text = appListDataModel.desc
            Picasso.get().load(appListDataModel.image)
                    .placeholder(R.drawable.logo_zl).error(R.drawable.ic_error)
                    .into(holder.imageView)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < appList.size) VIEW_ITEM else VIEW_PROG
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
}
