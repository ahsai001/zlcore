package com.zaitunlabs.zlcore.core;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zaitunlabs.zlcore.R;
import com.zaitunlabs.zlcore.listeners.SwipeDragCallback;
import com.zaitunlabs.zlcore.utils.CommonUtils;

import java.util.Collections;
import java.util.List;

/**
 * Created by ahsai on 5/30/2018.
 */

public abstract class BaseRecyclerViewAdapter<DM, HV extends RecyclerView.ViewHolder> extends RecyclerView.Adapter
implements SwipeDragCallback.SwipeDragInterface {
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;
    private boolean showProgress = false;

    protected List<DM> modelList;
    protected abstract int getLayout();
    protected abstract HV getViewHolder(View rootView);
    protected abstract void doSettingViewWithModel(HV holder, DM dataModel, int position);

    public BaseRecyclerViewAdapter(List<DM> modelList) {
        this.modelList = modelList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == VIEW_ITEM) {
            View rootView = LayoutInflater.from(parent.getContext()).inflate(getLayout(), parent, false);
            return getViewHolder(rootView);
        } else if(viewType == VIEW_PROG){
            View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.progressbar_vertical_center, parent, false);
            return new ProgressViewHolder(rootView);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder == null)return;
        if(position < modelList.size()) {
            doSettingViewWithModel((HV)holder, modelList.get(position), position);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return (position < modelList.size())?VIEW_ITEM:VIEW_PROG;
    }

    @Override
    public int getItemCount() {
        return getAllItemCount();
    }

    private int getAllItemCount(){
        return modelList.size()+(showProgress?1:0);
    }

    public void showLoadMoreProgress(){
        showProgress = true;
        notifyDataSetChanged();
    }

    public void hideLoadMoreProgress(){
        showProgress = false;
        notifyDataSetChanged();
    }

    private class ProgressViewHolder extends RecyclerView.ViewHolder {
        TextView descView;
        private ProgressViewHolder(View view) {
            super(view);
            descView = (TextView) view.findViewById(R.id.progress_custom_textview);
        }
    }

    protected void setViewClickable(final HV viewHolder, View view){
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onChildViewClickListener != null) {
                    int position = viewHolder.getAdapterPosition();
                    onChildViewClickListener.onClick(view, (DM)modelList.get(position), position);
                }
            }
        });
    }

    @Override
    public void onItemDrag(RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        int fromPosition = viewHolder.getAdapterPosition();
        int toPosition = target.getAdapterPosition();
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(modelList, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(modelList, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onItemSwipe(RecyclerView.ViewHolder viewHolder, int direction) {
        final int position = viewHolder.getAdapterPosition();
        CommonUtils.showDialog2Option(viewHolder.itemView.getContext(),
                viewHolder.itemView.getContext().getString(R.string.base_recyclerview_adapter_delete_confirmation),
                viewHolder.itemView.getContext().getString(R.string.base_recyclerview_adapter_delete_confirmation_message),
                viewHolder.itemView.getContext().getString(R.string.general_wording_delete), new Runnable() {
                    @Override
                    public void run() {
                        modelList.remove(position);
                        notifyItemRemoved(position);
                    }
                }, viewHolder.itemView.getContext().getString(R.string.general_wording_cancel), new Runnable() {
                    @Override
                    public void run() {
                        notifyItemChanged(position);
                    }
                });
    }



    @Override
    public boolean isItemViewSwipeEnabled() {
        return false;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return false;
    }

    @Override
    public int swipeLeftColor() {
        return Color.GREEN;
    }

    @Override
    public int swipeLeftTextColor() {
        return Color.WHITE;
    }

    @Override
    public String swipeLeftTextString() {
        return null;
    }

    @Override
    public int swipeRightColor() {
        return Color.RED;
    }

    @Override
    public int swipeRightTextColor() {
        return Color.WHITE;
    }

    @Override
    public String swipeRightTextString() {
        return null;
    }

    @Override
    public int swipeFlags() {
        return ItemTouchHelper.LEFT | ItemTouchHelper.START | ItemTouchHelper.RIGHT | ItemTouchHelper.END;
    }

    private OnChildViewClickListener onChildViewClickListener;
    public void setOnChildViewClickListener(OnChildViewClickListener onChildViewClickListener){
        this.onChildViewClickListener = onChildViewClickListener;
    }

    public interface OnChildViewClickListener<DM> {
        public void onClick(View view, DM dataModel, int position);
    }
}
