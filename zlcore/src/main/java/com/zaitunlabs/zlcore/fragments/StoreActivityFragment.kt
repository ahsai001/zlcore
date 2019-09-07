package com.zaitunlabs.zlcore.fragments

import android.app.SearchManager
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.SearchView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import android.widget.EditText

import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.ANRequest
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.google.gson.GsonBuilder
import com.zaitunlabs.zlcore.R
import com.zaitunlabs.zlcore.adapters.StoreAdapter
import com.zaitunlabs.zlcore.api.APIConstant
import com.zaitunlabs.zlcore.models.StoreDataModel
import com.zaitunlabs.zlcore.models.StoreModel
import com.zaitunlabs.zlcore.models.StorePagingModel
import com.zaitunlabs.zlcore.core.BaseFragment
import com.zaitunlabs.zlcore.listeners.RecyclerViewLoadMoreListener
import com.zaitunlabs.zlcore.listeners.RecyclerViewTouchListener
import com.zaitunlabs.zlcore.utils.CommonUtils
import com.zaitunlabs.zlcore.utils.HttpClientUtils
import com.zaitunlabs.zlcore.views.CustomRecylerView

import org.json.JSONObject

import java.lang.reflect.Modifier
import java.util.ArrayList

/**
 * A placeholder fragment containing a simple view.
 */

class StoreActivityFragment : BaseFragment() {
    private var adapter: StoreAdapter? = null
    private val storeDataModels = ArrayList<StoreDataModel>()
    private var recyclerView: CustomRecylerView? = null
    private var swipeRefreshLayout: SwipeRefreshLayout? = null
    private var emptyView: View? = null

    private var searchTerm: String? = null
    private val countPerPage = 10
    private var nextPage = 1
    private var storeModel: StoreModel? = null

    fun setArg(isMeid: Boolean) {
        val b = Bundle()
        b.putBoolean(PARAM_IS_MEID, isMeid)
        arguments = b
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = StoreAdapter(storeDataModels)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_store, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById<View>(R.id.store_recylerView) as CustomRecylerView
        emptyView = view.findViewById(R.id.store_empty_view)
        swipeRefreshLayout = view.findViewById<View>(R.id.store_refreshLayout) as SwipeRefreshLayout
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //recyclerView.setHasFixedSize(true);
        val mLayoutManager = LinearLayoutManager(activity!!.applicationContext)
        recyclerView!!.layoutManager = mLayoutManager
        val itemDecoration = DividerItemDecoration(activity!!, DividerItemDecoration.VERTICAL)
        recyclerView!!.addItemDecoration(itemDecoration)
        recyclerView!!.itemAnimator = DefaultItemAnimator()

        recyclerView!!.setEmptyView(emptyView)
        recyclerView!!.adapter = adapter
        recyclerView!!.addOnLoadMoreListener(object : RecyclerViewLoadMoreListener(mLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
                loadMore(context, nextPage, storeModel)
            }
        })


        recyclerView!!.addOnItemTouchListener(RecyclerViewTouchListener(activity, recyclerView, object : RecyclerViewTouchListener.RecyclerViewItemClickListener {
            override fun onClick(view: View, position: Int) {
                val unique = storeDataModels[position].unique
                val url = storeDataModels[position].url

                if (!TextUtils.isEmpty(unique)) {
                    CommonUtils.openPlayStore(view.context, unique)
                } else if (!TextUtils.isEmpty(url) && URLUtil.isValidUrl(url)) {
                    CommonUtils.openBrowser(view.context, url)
                }
            }

            override fun onLongClick(view: View?, position: Int) {

            }
        }))

        swipeRefreshLayout!!.setColorSchemeResources(android.R.color.holo_blue_dark,
                android.R.color.holo_blue_light,
                android.R.color.holo_green_light,
                android.R.color.holo_green_light)
        swipeRefreshLayout!!.setOnRefreshListener { initStoreData(activity, true) }

        swipeRefreshLayout!!.isEnabled = true

        initStoreData(activity, false)
    }

    private fun initStoreData(context: Context?, forceRefresh: Boolean) {
        val storeModel = StoreModel.lastCache
        if (storeModel == null || forceRefresh) {
            fetchStoreData(context, 1, storeModel)
        } else {
            loadInitList(storeModel!!.data)
            nextPage = storeModel!!.paging!!.next
            this@StoreActivityFragment.storeModel = storeModel
            swipeRefreshLayout!!.isRefreshing = false
        }
    }

    private fun loadInitList(storeDataModelList: List<StoreDataModel>?) {
        recyclerView!!.initLoadMore()
        storeDataModels.clear()
        if (storeDataModelList != null) {
            storeDataModels.addAll(storeDataModelList)
        }
        adapter!!.notifyDataSetChanged()
    }

    private fun addList(storeDataModelList: List<StoreDataModel>?) {
        if (storeDataModelList != null) {
            storeDataModels.addAll(storeDataModelList)
        }
        adapter!!.notifyDataSetChanged()
    }


    private fun loadMore(context: Context?, loadingPage: Int, storeModel: StoreModel?) {
        if (loadingPage > -1) {
            adapter!!.showProgress()
            recyclerView!!.smoothScrollToPosition(adapter!!.itemCount)
            fetchStoreData(context, loadingPage, storeModel)
        }
    }


    private fun fetchStoreData(context: Context?, loadingPage: Int, storeModel: StoreModel?) {
        val isMeid = CommonUtils.getBooleanFragmentArgument(arguments, PARAM_IS_MEID, false)

        val builder = AndroidNetworking.post(APIConstant.API_STORE + "/" + countPerPage + "/" + loadingPage)
                .setOkHttpClient(HttpClientUtils.getHTTPClient(context, APIConstant.API_VERSION, isMeid))
                .setPriority(Priority.HIGH)
                .setTag("store")
        if (!TextUtils.isEmpty(searchTerm)) {
            builder.addUrlEncodeFormBodyParameter("searchterm", searchTerm)
        }
        builder.build()
                .getAsJSONObject(object : JSONObjectRequestListener {
                    override fun onResponse(response: JSONObject) {
                        val status = response.optInt("status")
                        if (status <= 0) {
                            swipeRefreshLayout!!.isRefreshing = false
                            adapter!!.hideProgress()
                            val message = response.optString("message")
                            CommonUtils.showToast(context, message)
                            return
                        }
                        val responseListModel = GsonBuilder()
                                .excludeFieldsWithoutExposeAnnotation()
                                .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                                .create()
                                .fromJson<StoreModel>(response.toString(), StoreModel::class.java!!)
                        if (loadingPage == 1) {
                            //delete and save new storemodel
                            responseListModel.cache(true)
                            loadInitList(responseListModel.data)
                            this@StoreActivityFragment.storeModel = responseListModel

                        } else {
                            //save new paging
                            val newStorePagingModel = responseListModel.paging
                            newStorePagingModel!!.saveWithTimeStamp()

                            val oldStorePagingModel = this@StoreActivityFragment.storeModel!!.paging
                            this@StoreActivityFragment.storeModel!!.paging = newStorePagingModel
                            this@StoreActivityFragment.storeModel!!.addNewDataListToCache(responseListModel.data)
                            this@StoreActivityFragment.storeModel!!.save()
                            //delete old paging
                            oldStorePagingModel!!.delete()

                            adapter!!.hideProgress()

                            addList(responseListModel.data)

                        }
                        nextPage = responseListModel.paging!!.next
                        swipeRefreshLayout!!.isRefreshing = false
                    }

                    override fun onError(anError: ANError) {
                        if (loadingPage == 1) {
                            var appListDataModels: List<StoreDataModel>? = null
                            if (storeModel != null) {
                                appListDataModels = storeModel.data
                            }
                            loadInitList(appListDataModels)
                        } else {
                            adapter!!.hideProgress()
                        }
                        CommonUtils.showSnackBar(context, anError.errorDetail)
                        swipeRefreshLayout!!.isRefreshing = false
                    }
                })
    }


    override fun onDestroyView() {
        AndroidNetworking.cancel("store")
        super.onDestroyView()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_store, menu)

        val searchMenuItem = menu.findItem(R.id.listing_search)
        val searchView = searchMenuItem.actionView as SearchView

        val searchManager = activity!!.getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView.setSearchableInfo(searchManager.getSearchableInfo(activity!!.componentName))

        val editText = searchView.findViewById<View>(androidx.appcompat.R.id.search_src_text) as EditText
        editText.setHintTextColor(Color.WHITE)
        editText.setTextColor(Color.WHITE)

        searchMenuItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(menuItem: MenuItem): Boolean {
                searchView.setQuery(searchTerm, false)
                return true
            }

            override fun onMenuItemActionCollapse(menuItem: MenuItem): Boolean {
                searchTerm = null
                try {
                    initStoreData(activity, true)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                return true
            }
        })
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

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                searchTerm = query
                try {
                    initStoreData(activity, true)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_refresh) {
            swipeRefreshLayout!!.isRefreshing = true
            initStoreData(activity, true)
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        val PARAM_IS_MEID = InfoFragment.PARAM_IS_MEID
    }
}
