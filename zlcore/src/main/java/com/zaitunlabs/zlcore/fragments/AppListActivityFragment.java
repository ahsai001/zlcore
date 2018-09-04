package com.zaitunlabs.zlcore.fragments;

import android.content.Context;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.gson.GsonBuilder;
import com.zaitunlabs.zlcore.R;
import com.zaitunlabs.zlcore.adapters.AppListAdapter;
import com.zaitunlabs.zlcore.api.APIConstant;
import com.zaitunlabs.zlcore.models.AppListDataModel;
import com.zaitunlabs.zlcore.models.AppListModel;
import com.zaitunlabs.zlcore.models.AppListPagingModel;
import com.zaitunlabs.zlcore.core.BaseFragment;
import com.zaitunlabs.zlcore.listeners.RecyclerViewLoadMoreListener;
import com.zaitunlabs.zlcore.listeners.RecyclerViewTouchListener;
import com.zaitunlabs.zlcore.utils.CommonUtils;
import com.zaitunlabs.zlcore.utils.HttpClientUtils;
import com.zaitunlabs.zlcore.views.CustomRecylerView;

import org.json.JSONObject;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class AppListActivityFragment extends BaseFragment {
    private AppListAdapter adapter;
    private List<AppListDataModel> appListDataModels = new ArrayList<>();
    private CustomRecylerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private View emptyView;

    private int countPerPage = 10;
    private int nextPage = 1;
    private AppListModel appListModel;

    public AppListActivityFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new AppListAdapter(appListDataModels);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_app_list, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (CustomRecylerView) view.findViewById(R.id.applist_recylerView);
        emptyView = view.findViewById(R.id.applist_empty_view);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.applist_refreshLayout);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setEmptyView(emptyView);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnLoadMoreListener(new RecyclerViewLoadMoreListener(mLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadMore(getContext(),nextPage,appListModel);
            }
        });

        recyclerView.addOnItemTouchListener(new RecyclerViewTouchListener(getActivity(), recyclerView, new RecyclerViewTouchListener.RecyclerViewItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                String unique = appListDataModels.get(position).getUnique();
                String url = appListDataModels.get(position).getUrl();

                if (!TextUtils.isEmpty(unique)){
                    CommonUtils.openPlayStore(view.getContext(),unique);
                } else if (!TextUtils.isEmpty(url) && URLUtil.isValidUrl(url)){
                    CommonUtils.openBrowser(view.getContext(),url);
                }
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));


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
        AppListModel appListModel = AppListModel.getLastCache();
        if(appListModel == null || forceRefresh) {
            fetchAppListData(context,1,appListModel);
        } else {
            loadInitList(appListModel.getData());
            nextPage = appListModel.getPaging().getNext();
            AppListActivityFragment.this.appListModel = appListModel;
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void loadInitList(List<AppListDataModel> appListDataModelList){
        recyclerView.initLoadMore();
        appListDataModels.clear();
        if(appListDataModelList != null) {
            appListDataModels.addAll(appListDataModelList);
        }
        adapter.notifyDataSetChanged();
    }

    private void addList(List<AppListDataModel> appListDataModelList){
        if(appListDataModelList != null) {
            appListDataModels.addAll(appListDataModelList);
        }
        adapter.notifyDataSetChanged();
    }


    private void loadMore(Context context,int loadingPage, AppListModel appListModel){
        if(loadingPage > -1) {
            adapter.showProgress();
            recyclerView.smoothScrollToPosition(adapter.getItemCount());
            fetchAppListData(context, loadingPage, appListModel);
        }
    }


    private void fetchAppListData(final Context context, final int loadingPage, final AppListModel appListModel){
        AndroidNetworking.get(APIConstant.API_OTHER_APPS +"/"+countPerPage+"/"+loadingPage)
                .setOkHttpClient(HttpClientUtils.getHTTPClient(context, APIConstant.API_VERSION))
                .setPriority(Priority.HIGH)
                .setTag("others app")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        int status = response.optInt("status");
                        if(status <= 0){
                            swipeRefreshLayout.setRefreshing(false);
                            adapter.hideProgress();
                            String message = response.optString("message");
                            CommonUtils.showSnackBar(context,message);
                            return;
                        }
                        AppListModel responseListModel = new GsonBuilder()
                                .excludeFieldsWithoutExposeAnnotation()
                                .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                                .create()
                                .fromJson(response.toString(), AppListModel.class);
                        if(loadingPage == 1) {
                            //delete and save new applistmodel
                            responseListModel.cache(true);
                            loadInitList(responseListModel.getData());
                            AppListActivityFragment.this.appListModel = responseListModel;

                        } else {
                            //save new paging
                            AppListPagingModel newAppListPagingModel = responseListModel.getPaging();
                            newAppListPagingModel.saveWithTimeStamp();

                            AppListPagingModel oldAppListPagingModel = AppListActivityFragment.this.appListModel.getPaging();
                            AppListActivityFragment.this.appListModel.setPaging(newAppListPagingModel);
                            AppListActivityFragment.this.appListModel.addNewDataListToCache(responseListModel.getData());
                            AppListActivityFragment.this.appListModel.save();
                            //delete old paging
                            oldAppListPagingModel.delete();

                            adapter.hideProgress();

                            addList(responseListModel.getData());

                        }
                        nextPage = responseListModel.getPaging().getNext();
                        swipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onError(ANError anError) {
                        if(loadingPage == 1) {
                            List<AppListDataModel> appListDataModels = null;
                            if(appListModel != null){
                                appListDataModels = appListModel.getData();
                            }
                            loadInitList(appListDataModels);
                        } else {
                            adapter.hideProgress();
                        }
                        CommonUtils.showSnackBar(context,anError.getErrorDetail());
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_store,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_refresh){
            swipeRefreshLayout.setRefreshing(true);
            initAppListData(getActivity(), true);
        }
        return super.onOptionsItemSelected(item);
    }
}
