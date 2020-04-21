package com.zaitunlabs.zlcore.adapters;

import androidx.core.content.ContextCompat;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.zaitunlabs.zlcore.R;
import com.zaitunlabs.zlcore.tables.InformationModel;
import com.zaitunlabs.zlcore.utils.DateStringUtil;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by ahsai on 6/15/2017.
 */

public class InfoAdapter extends RecyclerView.Adapter<InfoAdapter.InfoViewHolder>{
    private List<InformationModel> infoList;

    public class InfoViewHolder extends RecyclerView.ViewHolder {
        CardView rootView;
        TextView titleView;
        ExpandableTextView bodyView;
        TextView timeView;
        ImageView imageView;
        ImageButton optionView;

        public InfoViewHolder(View view) {
            super(view);
            rootView = view.findViewById(R.id.info_cardView);
            titleView = view.findViewById(R.id.info_title_row);
            bodyView = view.findViewById(R.id.info_body_row);
            timeView = view.findViewById(R.id.info_time_row);
            imageView = view.findViewById(R.id.info_image_row);
            optionView = view.findViewById(R.id.info_item_row_optionView);

            bodyView.setOnExpandStateChangeListener(mExpandListener);
        }
    }


    public InfoAdapter(List<InformationModel> infoList) {
        this.infoList = infoList;
    }

    @Override
    public InfoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_info_list_row, parent, false);
        return new InfoViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final InfoViewHolder holder, int position) {
        InformationModel info = infoList.get(position);
        //title
        holder.titleView.setText(info.getTitle());

        //body
        SparseBooleanArray collapseStatus = new SparseBooleanArray();

        String body = info.getBody();
        holder.bodyView.setText(body, collapseStatus, 0);

        //time
        holder.timeView.setText(DateStringUtil.getDateTimeInString(info._created_at, null, null));


        //image
        if(!TextUtils.isEmpty(info.getPhotoUrl()) && URLUtil.isValidUrl(info.getPhotoUrl())){
            holder.imageView.setVisibility(View.VISIBLE);
            Picasso.get().load(info.getPhotoUrl()).error(R.drawable.ic_error).into(holder.imageView);
        }else{
            holder.imageView.setVisibility(View.GONE);
            holder.imageView.setImageBitmap(null);
        }


        ((InfoViewHolder) holder).optionView
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(onMoreOptionClickListener != null) {
                            onMoreOptionClickListener.onClick(view, holder.getAdapterPosition());
                        }
                    }
                });

        ((InfoViewHolder) holder).rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onCardClickListener != null) {
                    onCardClickListener.onClick(view, holder.getAdapterPosition());
                }
            }
        });

        if(info.isRead()){
            holder.rootView.setCardBackgroundColor(ContextCompat.getColor(holder.rootView.getContext(),R.color.info_status_read));
        }else{
            holder.rootView.setCardBackgroundColor(ContextCompat.getColor(holder.rootView.getContext(),R.color.info_status_unread));
        }
    }

    @Override
    public int getItemCount() {
        return infoList.size();
    }



    public void markAllAsRead(){
        for (InformationModel item : infoList){
            item.setRead(true);
        }
        notifyDataSetChanged();
    }


    private ExpandableTextView.OnExpandStateChangeListener mExpandListener= new ExpandableTextView.OnExpandStateChangeListener(){
        @Override
        public void onExpandStateChanged(TextView textView, boolean isExpanded) {
            ((View)textView.getParent()).setTag(isExpanded);
        }
    };



    private OnMoreOptionClickListener onMoreOptionClickListener;
    public void setOnMoreOptionClickListener(OnMoreOptionClickListener onMoreOptionClickListener){
        this.onMoreOptionClickListener = onMoreOptionClickListener;
    }

    public static interface OnMoreOptionClickListener {
        public void onClick(View view, int position);
    }


    public void setOnCardClickListener(OnCardClickListener onCardClickListener){
        this.onCardClickListener = onCardClickListener;
    }

    private OnCardClickListener onCardClickListener;
    public static interface OnCardClickListener {
        public void onClick(View view, int position);
    }

}
