package com.zaitunlabs.zlcore.adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.zaitunlabs.zlcore.R;
import com.zaitunlabs.zlcore.models.AppListDataModel;

import java.util.List;

/**
 * Created by ahsai on 3/18/2018.
 */

public class AppListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<AppListDataModel> appList;
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;
    private boolean showProgress = false;

    private class AppListViewHolder extends RecyclerView.ViewHolder {
        CardView rootView;
        TextView titleView;
        TextView bodyView;
        ImageView imageView;

        private AppListViewHolder(View view) {
            super(view);
            rootView = (CardView) view;
            titleView = (TextView) view.findViewById(R.id.applist_item_row_titleView);
            bodyView = (TextView) view.findViewById(R.id.applist_item_row_descView);
            imageView = (ImageView) view.findViewById(R.id.applist_item_row_imageView);
        }
    }

    private class ProgressViewHolder extends RecyclerView.ViewHolder {
        TextView descView;

        private ProgressViewHolder(View view) {
            super(view);
            descView = (TextView) view.findViewById(R.id.progress_custom_textview);
        }
    }

    public AppListAdapter(List<AppListDataModel> appList) {
        this.appList = appList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == VIEW_ITEM) {
            View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.app_list_item_row, parent, false);
            return new AppListViewHolder(rootView);
        } else if(viewType == VIEW_PROG){
            View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.progressbar_vertical_center, parent, false);
            return new ProgressViewHolder(rootView);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder == null)return;
        if(position < appList.size()) {
            AppListDataModel appListDataModel = appList.get(position);
            ((AppListViewHolder) holder).titleView.setText(appListDataModel.getTitle());
            ((AppListViewHolder) holder).bodyView.setText(appListDataModel.getDesc());
            Picasso.with(((AppListViewHolder) holder).imageView.getContext()).load(appListDataModel.getImage())
                    .placeholder(R.drawable.logo_zl).error(R.drawable.ic_error)
                    .into(((AppListViewHolder) holder).imageView);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return (position < appList.size())?VIEW_ITEM:VIEW_PROG;
    }

    @Override
    public int getItemCount() {
        return getAllItemCount();
    }

    private int getAllItemCount(){
        return appList.size()+(showProgress?1:0);
    }

    public void showProgress(){
        showProgress = true;
        notifyDataSetChanged();
    }

    public void hideProgress(){
        showProgress = false;
        notifyDataSetChanged();
    }
}
