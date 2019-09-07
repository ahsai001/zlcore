package com.zaitunlabs.zlcore.adapters

import androidx.core.content.ContextCompat
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView

import com.ms.square.android.expandabletextview.ExpandableTextView
import com.zaitunlabs.zlcore.R
import com.zaitunlabs.zlcore.models.InformationModel
import com.zaitunlabs.zlcore.utils.DateStringUtils
import com.squareup.picasso.Picasso

/**
 * Created by ahsai on 6/15/2017.
 */

class InfoAdapter(private val infoList: List<InformationModel>) : RecyclerView.Adapter<InfoAdapter.InfoViewHolder>() {


    private val mExpandListener = ExpandableTextView.OnExpandStateChangeListener { textView, isExpanded -> (textView.parent as View).tag = isExpanded }


    private var onMoreOptionClickListener: OnMoreOptionClickListener? = null

    private var onCardClickListener: OnCardClickListener? = null

    inner class InfoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        internal var rootView: CardView
        internal var titleView: TextView
        internal var bodyView: ExpandableTextView
        internal var timeView: TextView
        internal var imageView: ImageView
        internal var optionView: ImageButton

        init {
            rootView = view.findViewById(R.id.info_cardView)
            titleView = view.findViewById(R.id.info_title_row)
            bodyView = view.findViewById(R.id.info_body_row)
            timeView = view.findViewById(R.id.info_time_row)
            imageView = view.findViewById(R.id.info_image_row)
            optionView = view.findViewById(R.id.info_item_row_optionView)

            bodyView.setOnExpandStateChangeListener(mExpandListener)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InfoViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.fragment_info_list_row, parent, false)
        return InfoViewHolder(itemView)
    }


    override fun onBindViewHolder(holder: InfoViewHolder, position: Int) {
        val info = infoList[position]
        //title
        holder.titleView.text = info.title

        //body
        val collapseStatus = SparseBooleanArray()

        val body = info.body
        holder.bodyView.setText(body, collapseStatus, 0)

        //time
        holder.timeView.text = DateStringUtils.getDateTimeInString(info.timestamp, null)


        //image
        if (!TextUtils.isEmpty(info.photoUrl) && URLUtil.isValidUrl(info.photoUrl)) {
            holder.imageView.visibility = View.VISIBLE
            Picasso.get().load(info.photoUrl).error(R.drawable.ic_error).into(holder.imageView)
        } else {
            holder.imageView.visibility = View.GONE
            holder.imageView.setImageBitmap(null)
        }


        holder.optionView
                .setOnClickListener { view ->
                    if (onMoreOptionClickListener != null) {
                        onMoreOptionClickListener!!.onClick(view, holder.adapterPosition)
                    }
                }

        holder.rootView.setOnClickListener { view ->
            if (onCardClickListener != null) {
                onCardClickListener!!.onClick(view, holder.adapterPosition)
            }
        }

        if (info.isRead) {
            holder.rootView.setCardBackgroundColor(ContextCompat.getColor(holder.rootView.context, R.color.info_status_read))
        } else {
            holder.rootView.setCardBackgroundColor(ContextCompat.getColor(holder.rootView.context, R.color.info_status_unread))
        }
    }

    override fun getItemCount(): Int {
        return infoList.size
    }


    fun markAllAsRead() {
        for (item in infoList) {
            item.isRead = true
        }
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
