package com.zaitunlabs.zlcore.fragments;

import android.content.Context;
import androidx.annotation.Nullable;
import android.os.Bundle;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.zaitunlabs.zlcore.R;
import com.zaitunlabs.zlcore.adapters.BookmarkListAdapter;
import com.zaitunlabs.zlcore.models.BookmarkModel;
import com.zaitunlabs.zlcore.core.BaseFragment;
import com.zaitunlabs.zlcore.events.ShowBookmarkInfoEvent;
import com.zaitunlabs.zlcore.utils.CommonUtils;
import com.zaitunlabs.zlcore.views.CustomRecylerView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class BookmarkListActivityFragment extends BaseFragment {
    BookmarkListAdapter adapter;
    private List<BookmarkModel> bookmarkModelList = new ArrayList<>();
    private CustomRecylerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private View emptyView;
    public BookmarkListActivityFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new BookmarkListAdapter(bookmarkModelList);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bookmark_list, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (CustomRecylerView) view.findViewById(R.id.bookmarklist_recylerView);
        emptyView = view.findViewById(R.id.bookmarklist_empty_view);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.bookmarklist_refreshLayout);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setEmptyView(emptyView);
        recyclerView.setAdapter(adapter);

        /*
        recyclerView.addOnItemTouchListener(new RecyclerViewTouchListener(getActivity(), recyclerView, new RecyclerViewTouchListener.RecyclerViewItemClickListener() {
            @Override
            public void onClick(View view, final int position) {

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));*/

        adapter.setOnCardClickListener(new BookmarkListAdapter.OnCardClickListener() {
            @Override
            public void onClick(View view, int position) {
                String title = bookmarkModelList.get(position).getTitle();
                String link = bookmarkModelList.get(position).getLink();
                EventBus.getDefault().post(new ShowBookmarkInfoEvent(title, link));
                getActivity().finish();
            }
        });

        adapter.setOnMoreOptionClickListener(new BookmarkListAdapter.OnMoreOptionClickListener() {
            @Override
            public void onClick(View view, final int position) {
                CommonUtils.showPopup(view.getContext(), R.menu.menu_bookmark_list, view, null, new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        String title = bookmarkModelList.get(position).getTitle();
                        String link = bookmarkModelList.get(position).getLink();
                        if(item.getItemId() == R.id.action_open_remove_bookmark) {
                            EventBus.getDefault().post(new ShowBookmarkInfoEvent(title, link));
                            BookmarkModel model = bookmarkModelList.get(position);
                            model.delete();
                            getActivity().finish();
                        } else if(item.getItemId() == R.id.action_open_bookmark) {
                            EventBus.getDefault().post(new ShowBookmarkInfoEvent(title, link));
                            getActivity().finish();
                        } else if(item.getItemId() == R.id.action_remove_bookmark) {
                            BookmarkModel model = bookmarkModelList.get(position);
                            model.delete();
                            bookmarkModelList.remove(position);
                            adapter.notifyDataSetChanged();
                        }
                        return true;
                    }
                });
            }
        });


        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_dark,
                android.R.color.holo_blue_light,
                android.R.color.holo_green_light,
                android.R.color.holo_green_light);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initAppListData(getActivity(), true);
            }
        });

        swipeRefreshLayout.setEnabled(true);

        initAppListData(getActivity(), false);
    }

    private void initAppListData(Context context, boolean forceRefresh) {
        if(forceRefresh){
            bookmarkModelList.clear();
        }
        bookmarkModelList.addAll(BookmarkModel.getAllBookmarkList());
        adapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
    }
}
