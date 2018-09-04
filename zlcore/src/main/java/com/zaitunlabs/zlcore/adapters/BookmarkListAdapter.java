package com.zaitunlabs.zlcore.adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.zaitunlabs.zlcore.R;
import com.zaitunlabs.zlcore.models.BookmarkModel;

import java.util.List;

/**
 * Created by ahsai on 3/18/2018.
 */

public class BookmarkListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<BookmarkModel> bookmarkList;
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;
    private boolean showProgress = false;

    private class BookmarkListViewHolder extends RecyclerView.ViewHolder {
        CardView rootView;
        TextView titleView;
        TextView bodyView;
        ImageButton optionView;

        private BookmarkListViewHolder(View view) {
            super(view);
            rootView = (CardView) view;
            titleView = (TextView) view.findViewById(R.id.bookmarklist_item_row_titleView);
            bodyView = (TextView) view.findViewById(R.id.bookmarklist_item_row_descView);
            optionView = (ImageButton) view.findViewById(R.id.bookmarklist_item_row_optionView);
        }
    }

    private class ProgressViewHolder extends RecyclerView.ViewHolder {
        TextView descView;
        private ProgressViewHolder(View view) {
            super(view);
            descView = (TextView) view.findViewById(R.id.progress_custom_textview);
        }
    }

    public BookmarkListAdapter(List<BookmarkModel> bookmarkList) {
        this.bookmarkList = bookmarkList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == VIEW_ITEM) {
            View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.bookmark_list_item_row, parent, false);
            return new BookmarkListViewHolder(rootView);
        } else if(viewType == VIEW_PROG){
            View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.progressbar_vertical_center, parent, false);
            return new ProgressViewHolder(rootView);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if(holder == null)return;
        if(position < bookmarkList.size()) {
            BookmarkModel appListDataModel = bookmarkList.get(position);
            ((BookmarkListViewHolder) holder).titleView.setText(appListDataModel.getTitle());
            ((BookmarkListViewHolder) holder).bodyView.setText(appListDataModel.getDesc());
            ((BookmarkListViewHolder) holder).bodyView.setVisibility(View.GONE);
            ((BookmarkListViewHolder) holder).optionView
                    .setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(onMoreOptionClickListener != null) {
                        onMoreOptionClickListener.onClick(view, holder.getAdapterPosition());
                    }
                }
            });

            ((BookmarkListViewHolder) holder).rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(onCardClickListener != null) {
                        onCardClickListener.onClick(view, holder.getAdapterPosition());
                    }
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        return (position < bookmarkList.size())?VIEW_ITEM:VIEW_PROG;
    }

    @Override
    public int getItemCount() {
        return getAllItemCount();
    }

    private int getAllItemCount(){
        return bookmarkList.size()+(showProgress?1:0);
    }

    public void showProgress(){
        showProgress = true;
        notifyDataSetChanged();
    }

    public void hideProgress(){
        showProgress = false;
        notifyDataSetChanged();
    }



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
