package com.zaitunlabs.zlcore.fragments;

import android.app.SearchManager;
import android.content.Context;
import android.graphics.Color;
import androidx.annotation.Nullable;
import android.os.Bundle;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.EditText;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.gson.GsonBuilder;
import com.zaitunlabs.zlcore.R;
import com.zaitunlabs.zlcore.adapters.StoreAdapter;
import com.zaitunlabs.zlcore.api.APIConstant;
import com.zaitunlabs.zlcore.tables.StoreDataModel;
import com.zaitunlabs.zlcore.tables.StoreModel;
import com.zaitunlabs.zlcore.tables.StorePagingModel;
import com.zaitunlabs.zlcore.core.BaseFragment;
import com.zaitunlabs.zlcore.listeners.RecyclerViewLoadMoreListener;
import com.zaitunlabs.zlcore.listeners.RecyclerViewTouchListener;
import com.zaitunlabs.zlcore.utils.CommonUtil;
import com.zaitunlabs.zlcore.utils.HttpClientUtil;
import com.zaitunlabs.zlcore.views.CustomRecylerView;

import org.json.JSONObject;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */

public class StoreActivityFragment extends BaseFragment {
    public static final String PARAM_IS_MEID = InfoFragment.PARAM_IS_MEID;
    private StoreAdapter adapter;
    private List<StoreDataModel> storeDataModels = new ArrayList<>();
    private CustomRecylerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private View emptyView;

    private String searchTerm;
    private int countPerPage = 10;
    private int nextPage = 1;
    private StoreModel storeModel;

    public StoreActivityFragment() {
    }

    public void setArg(boolean isMeid){
        Bundle b = new Bundle();
        b.putBoolean(PARAM_IS_MEID, isMeid);
        setArguments(b);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new StoreAdapter(storeDataModels);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_store, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (CustomRecylerView) view.findViewById(R.id.store_recylerView);
        emptyView = view.findViewById(R.id.store_empty_view);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.store_refreshLayout);
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
                loadMore(getContext(),nextPage, storeModel);
            }
        });


        recyclerView.addOnItemTouchListener(new RecyclerViewTouchListener(getActivity(), recyclerView, new RecyclerViewTouchListener.RecyclerViewItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                String unique = storeDataModels.get(position).getUnique();
                String url = storeDataModels.get(position).getUrl();

                if (!TextUtils.isEmpty(unique)){
                    CommonUtil.openPlayStore(view.getContext(),unique);
                } else if (!TextUtils.isEmpty(url) && URLUtil.isValidUrl(url)){
                    CommonUtil.openBrowser(view.getContext(),url);
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
                initStoreData(getActivity(), true);
            }
        });

        swipeRefreshLayout.setEnabled(true);

        initStoreData(getActivity(), false);
    }

    private void initStoreData(Context context, boolean forceRefresh) {
        StoreModel storeModel = StoreModel.getLastCache();
        if(storeModel == null || forceRefresh) {
            fetchStoreData(context,1,storeModel);
        } else {
            loadInitList(storeModel.getData());
            nextPage = storeModel.getPaging().getNext();
            StoreActivityFragment.this.storeModel = storeModel;
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void loadInitList(List<StoreDataModel> storeDataModelList){
        recyclerView.initLoadMore();
        storeDataModels.clear();
        if(storeDataModelList != null) {
            storeDataModels.addAll(storeDataModelList);
        }
        adapter.notifyDataSetChanged();
    }

    private void addList(List<StoreDataModel> storeDataModelList){
        if(storeDataModelList != null) {
            storeDataModels.addAll(storeDataModelList);
        }
        adapter.notifyDataSetChanged();
    }


    private void loadMore(Context context,int loadingPage, StoreModel storeModel){
        if(loadingPage > -1) {
            adapter.showProgress();
            recyclerView.smoothScrollToPosition(adapter.getItemCount());
            fetchStoreData(context, loadingPage, storeModel);
        }
    }


    private void fetchStoreData(final Context context, final int loadingPage, final StoreModel storeModel){
        boolean isMeid = CommonUtil.getBooleanFragmentArgument(getArguments(), PARAM_IS_MEID, false);

        ANRequest.PostRequestBuilder builder = AndroidNetworking.post(APIConstant.API_STORE +"/"+countPerPage+"/"+loadingPage)
                .setOkHttpClient(HttpClientUtil.getHTTPClient(context, APIConstant.API_VERSION, isMeid))
                .setPriority(Priority.HIGH)
                .setTag("store");
        if(!TextUtils.isEmpty(searchTerm)){
            builder.addUrlEncodeFormBodyParameter("searchterm",searchTerm);
        }
        builder.build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        int status = response.optInt("status");
                        if(status <= 0){
                            swipeRefreshLayout.setRefreshing(false);
                            adapter.hideProgress();
                            String message = response.optString("message");
                            CommonUtil.showToast(context,message);
                            return;
                        }
                        StoreModel responseListModel = new GsonBuilder()
                                .excludeFieldsWithoutExposeAnnotation()
                                .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                                .create()
                                .fromJson(response.toString(), StoreModel.class);
                        if(loadingPage == 1) {
                            //delete and save new storemodel
                            responseListModel.cache(true);
                            loadInitList(responseListModel.getData());
                            StoreActivityFragment.this.storeModel = responseListModel;

                        } else {
                            StorePagingModel newStorePagingModel = responseListModel.getPaging();

                            StorePagingModel oldStorePagingModel = StoreActivityFragment.this.storeModel.getPaging();
                            StoreActivityFragment.this.storeModel.setPaging(newStorePagingModel);
                            //StoreActivityFragment.this.storeModel.update();

                            StoreActivityFragment.this.storeModel.addNewDataListToCache(responseListModel.getData());

                            //save new paging
                            newStorePagingModel.storeModel = StoreActivityFragment.this.storeModel;
                            newStorePagingModel.save();

                            //delete old paging
                            oldStorePagingModel.delete();

                            adapter.hideProgress();

                            addList(responseListModel.getData());

                        }
                        nextPage = responseListModel.getPaging().getNext();
                        swipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onError(ANError anError) {
                        if(loadingPage == 1) {
                            List<StoreDataModel> appListDataModels = null;
                            if(storeModel != null){
                                appListDataModels = storeModel.getData();
                            }
                            loadInitList(appListDataModels);
                        } else {
                            adapter.hideProgress();
                        }
                        CommonUtil.showSnackBar(context,anError.getErrorDetail());
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
    }


    @Override
    public void onDestroyView() {
        AndroidNetworking.cancel("store");
        super.onDestroyView();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_store,menu);

        MenuItem searchMenuItem = menu.findItem(R.id.listing_search);
        final SearchView searchView = (SearchView) searchMenuItem.getActionView();

        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));

        EditText editText = (EditText) searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        editText.setHintTextColor(Color.WHITE);
        editText.setTextColor(Color.WHITE);

        searchMenuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                searchView.setQuery(searchTerm, false);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                searchTerm = null;
                try {
                    initStoreData(getActivity(), true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }
        });
        /*
        MenuItemCompat.setOnActionExpandListener(searchMenuItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                searchView.setQuery(searchTerm, false);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                searchTerm = null;
                try {
                    initEventData(getActivity(), true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }
        });*/

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchTerm = query;
                try {
                    initStoreData(getActivity(), true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_refresh){
            swipeRefreshLayout.setRefreshing(true);
            initStoreData(getActivity(), true);
        }
        return super.onOptionsItemSelected(item);
    }
}
