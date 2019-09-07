package com.zaitunlabs.zlcore.fragments

import android.content.Context
import android.os.Bundle
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil

import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.google.gson.GsonBuilder
import com.zaitunlabs.zlcore.R
import com.zaitunlabs.zlcore.adapters.AppListAdapter
import com.zaitunlabs.zlcore.api.APIConstant
import com.zaitunlabs.zlcore.models.AppListDataModel
import com.zaitunlabs.zlcore.models.AppListModel
import com.zaitunlabs.zlcore.models.AppListPagingModel
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
class AppListActivityFragment : BaseFragment() {
    private var adapter: AppListAdapter? = null
    private val appListDataModels = ArrayList<AppListDataModel>()
    private var recyclerView: CustomRecylerView? = null
    private var swipeRefreshLayout: SwipeRefreshLayout? = null
    private var emptyView: View? = null

    private val countPerPage = 10
    private var nextPage = 1
    private var appListModel: AppListModel? = null

    fun setArg(isMeid: Boolean) {
        val b = Bundle()
        b.putBoolean(PARAM_IS_MEID, isMeid)
        arguments = b
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = AppListAdapter(appListDataModels)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_app_list, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById<View>(R.id.applist_recylerView) as CustomRecylerView
        emptyView = view.findViewById(R.id.applist_empty_view)
        swipeRefreshLayout = view.findViewById<View>(R.id.applist_refreshLayout) as SwipeRefreshLayout
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
                loadMore(context, nextPage, appListModel)
            }
        })

        recyclerView!!.addOnItemTouchListener(RecyclerViewTouchListener(activity, recyclerView, object : RecyclerViewTouchListener.RecyclerViewItemClickListener {
            override fun onClick(view: View, position: Int) {
                val unique = appListDataModels[position].unique
                val url = appListDataModels[position].url

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
        swipeRefreshLayout!!.setOnRefreshListener { initAppListData(activity, true) }

        swipeRefreshLayout!!.isEnabled = true

        initAppListData(activity, false)
    }


    private fun initAppListData(context: Context?, forceRefresh: Boolean) {
        val appListModel = AppListModel.lastCache
        if (appListModel == null || forceRefresh) {
            fetchAppListData(context, 1, appListModel)
        } else {
            loadInitList(appListModel!!.data)
            nextPage = appListModel!!.paging!!.next
            this@AppListActivityFragment.appListModel = appListModel
            swipeRefreshLayout!!.isRefreshing = false
        }
    }

    private fun loadInitList(appListDataModelList: List<AppListDataModel>?) {
        recyclerView!!.initLoadMore()
        appListDataModels.clear()
        if (appListDataModelList != null) {
            appListDataModels.addAll(appListDataModelList)
        }
        adapter!!.notifyDataSetChanged()
    }

    private fun addList(appListDataModelList: List<AppListDataModel>?) {
        if (appListDataModelList != null) {
            appListDataModels.addAll(appListDataModelList)
        }
        adapter!!.notifyDataSetChanged()
    }


    private fun loadMore(context: Context?, loadingPage: Int, appListModel: AppListModel?) {
        if (loadingPage > -1) {
            adapter!!.showProgress()
            recyclerView!!.smoothScrollToPosition(adapter!!.itemCount)
            fetchAppListData(context, loadingPage, appListModel)
        }
    }


    private fun fetchAppListData(context: Context?, loadingPage: Int, appListModel: AppListModel?) {
        val isMeid = CommonUtils.getBooleanFragmentArgument(arguments, PARAM_IS_MEID, false)

        AndroidNetworking.get(APIConstant.API_OTHER_APPS + "/" + countPerPage + "/" + loadingPage)
                .setOkHttpClient(HttpClientUtils.getHTTPClient(context, APIConstant.API_VERSION, isMeid))
                .setPriority(Priority.HIGH)
                .setTag("othersapp")
                .build()
                .getAsJSONObject(object : JSONObjectRequestListener {
                    override fun onResponse(response: JSONObject) {
                        val status = response.optInt("status")
                        if (status <= 0) {
                            swipeRefreshLayout!!.isRefreshing = false
                            adapter!!.hideProgress()
                            val message = response.optString("message")
                            CommonUtils.showSnackBar(context, message)
                            return
                        }
                        val responseListModel = GsonBuilder()
                                .excludeFieldsWithoutExposeAnnotation()
                                .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                                .create()
                                .fromJson<AppListModel>(response.toString(), AppListModel::class.java!!)
                        if (loadingPage == 1) {
                            //delete and save new applistmodel
                            responseListModel.cache(true)
                            loadInitList(responseListModel.data)
                            this@AppListActivityFragment.appListModel = responseListModel

                        } else {
                            //save new paging
                            val newAppListPagingModel = responseListModel.paging
                            newAppListPagingModel!!.saveWithTimeStamp()

                            val oldAppListPagingModel = this@AppListActivityFragment.appListModel!!.paging
                            this@AppListActivityFragment.appListModel!!.paging = newAppListPagingModel
                            this@AppListActivityFragment.appListModel!!.addNewDataListToCache(responseListModel.data)
                            this@AppListActivityFragment.appListModel!!.save()
                            //delete old paging
                            oldAppListPagingModel!!.delete()

                            adapter!!.hideProgress()

                            addList(responseListModel.data)

                        }
                        nextPage = responseListModel.paging!!.next
                        swipeRefreshLayout!!.isRefreshing = false
                    }

                    override fun onError(anError: ANError) {
                        if (loadingPage == 1) {
                            var appListDataModels: List<AppListDataModel>? = null
                            if (appListModel != null) {
                                appListDataModels = appListModel.data
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
        AndroidNetworking.cancel("othersapp")
        super.onDestroyView()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_store, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_refresh) {
            swipeRefreshLayout!!.isRefreshing = true
            initAppListData(activity, true)
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        val PARAM_IS_MEID = InfoFragment.PARAM_IS_MEID
    }
}
