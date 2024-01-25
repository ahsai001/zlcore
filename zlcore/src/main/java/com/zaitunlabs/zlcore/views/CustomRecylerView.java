package com.zaitunlabs.zlcore.views;

import android.content.Context;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.util.AttributeSet;
import android.view.View;

import com.zaitunlabs.zlcore.listeners.RecyclerViewLoadMoreListener;
import com.zaitunlabs.zlcore.listeners.SwipeDragCallback;

/**
 * Created by ahsai on 7/10/2017.
 */


public class CustomRecylerView extends RecyclerView {

    private View emptyView;
    private RecyclerViewLoadMoreListener recyclerViewLoadMoreListener;

    final private AdapterDataObserver observer = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            checkIfEmpty();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            checkIfEmpty();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            checkIfEmpty();
        }
    };

    public CustomRecylerView(Context context) {
        super(context);
    }

    public CustomRecylerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomRecylerView(Context context, AttributeSet attrs,
                             int defStyle) {
        super(context, attrs, defStyle);
    }

    public void init(){
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        setLayoutManager(mLayoutManager);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        addItemDecoration(itemDecoration);
        setItemAnimator(new DefaultItemAnimator());
        setHasFixedSize(true);
    }

    void checkIfEmpty() {
        if (emptyView != null && getAdapter() != null) {
            final boolean emptyViewVisible = getAdapter().getItemCount() == 0;
            emptyView.setVisibility(emptyViewVisible ? VISIBLE : GONE);
            setVisibility(emptyViewVisible ? GONE : VISIBLE);
        }
    }

    @Override
    public void setAdapter(Adapter adapter) {
        final Adapter oldAdapter = getAdapter();
        if (oldAdapter != null) {
            oldAdapter.unregisterAdapterDataObserver(observer);
        }
        super.setAdapter(adapter);
        if (adapter != null) {
            adapter.registerAdapterDataObserver(observer);
        }
        checkIfEmpty();
        enableSwipeDrag(adapter);
    }

    private void enableSwipeDrag(Adapter adapter){
        if(adapter != null && adapter instanceof SwipeDragCallback.SwipeDragInterface) {
            SwipeDragCallback callback = new SwipeDragCallback((SwipeDragCallback.SwipeDragInterface)adapter);
            ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
            touchHelper.attachToRecyclerView(this);
        }
    }


    public void setEmptyView(View emptyView) {
        this.emptyView = emptyView;
        checkIfEmpty();
    }

    public void addOnLoadMoreListener(RecyclerViewLoadMoreListener listener) {
        if(listener != null) {
            recyclerViewLoadMoreListener = listener;
            super.addOnScrollListener(listener);
        }
    }

    public void initLoadMore(){
        if(recyclerViewLoadMoreListener != null) {
            recyclerViewLoadMoreListener.resetState();
        }
    }
}
