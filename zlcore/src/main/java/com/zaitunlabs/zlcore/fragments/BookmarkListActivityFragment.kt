package com.zaitunlabs.zlcore.fragments

import android.content.Context
import android.os.Bundle
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup

import com.zaitunlabs.zlcore.R
import com.zaitunlabs.zlcore.adapters.BookmarkListAdapter
import com.zaitunlabs.zlcore.models.BookmarkModel
import com.zaitunlabs.zlcore.core.BaseFragment
import com.zaitunlabs.zlcore.events.ShowBookmarkInfoEvent
import com.zaitunlabs.zlcore.utils.CommonUtils
import com.zaitunlabs.zlcore.views.CustomRecylerView

import org.greenrobot.eventbus.EventBus

import java.util.ArrayList

/**
 * A placeholder fragment containing a simple view.
 */
class BookmarkListActivityFragment : BaseFragment() {
    internal var adapter: BookmarkListAdapter
    private val bookmarkModelList = ArrayList<BookmarkModel>()
    private var recyclerView: CustomRecylerView? = null
    private var swipeRefreshLayout: SwipeRefreshLayout? = null
    private var emptyView: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = BookmarkListAdapter(bookmarkModelList)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_bookmark_list, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById<View>(R.id.bookmarklist_recylerView) as CustomRecylerView
        emptyView = view.findViewById(R.id.bookmarklist_empty_view)
        swipeRefreshLayout = view.findViewById<View>(R.id.bookmarklist_refreshLayout) as SwipeRefreshLayout
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        recyclerView!!.setHasFixedSize(true)
        val mLayoutManager = LinearLayoutManager(activity!!.applicationContext)
        recyclerView!!.layoutManager = mLayoutManager
        val itemDecoration = DividerItemDecoration(activity!!, DividerItemDecoration.VERTICAL)
        recyclerView!!.addItemDecoration(itemDecoration)
        recyclerView!!.itemAnimator = DefaultItemAnimator()

        recyclerView!!.setEmptyView(emptyView)
        recyclerView!!.adapter = adapter

        /*
        recyclerView.addOnItemTouchListener(new RecyclerViewTouchListener(getActivity(), recyclerView, new RecyclerViewTouchListener.RecyclerViewItemClickListener() {
            @Override
            public void onClick(View view, final int position) {

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));*/

        adapter.setOnCardClickListener { view, position ->
            val title = bookmarkModelList[position].title
            val link = bookmarkModelList[position].link
            EventBus.getDefault().post(ShowBookmarkInfoEvent(title, link))
            activity!!.finish()
        }

        adapter.setOnMoreOptionClickListener { view, position ->
            CommonUtils.showPopupMenu(view.context, R.menu.menu_bookmark_list, view, null, PopupMenu.OnMenuItemClickListener { item ->
                val title = bookmarkModelList[position].title
                val link = bookmarkModelList[position].link
                if (item.itemId == R.id.action_open_remove_bookmark) {
                    EventBus.getDefault().post(ShowBookmarkInfoEvent(title, link))
                    val model = bookmarkModelList[position]
                    model.delete()
                    activity!!.finish()
                } else if (item.itemId == R.id.action_open_bookmark) {
                    EventBus.getDefault().post(ShowBookmarkInfoEvent(title, link))
                    activity!!.finish()
                } else if (item.itemId == R.id.action_remove_bookmark) {
                    val model = bookmarkModelList[position]
                    model.delete()
                    bookmarkModelList.removeAt(position)
                    adapter.notifyDataSetChanged()
                }
                true
            })
        }


        swipeRefreshLayout!!.setColorSchemeResources(android.R.color.holo_blue_dark,
                android.R.color.holo_blue_light,
                android.R.color.holo_green_light,
                android.R.color.holo_green_light)
        swipeRefreshLayout!!.setOnRefreshListener { initAppListData(activity, true) }

        swipeRefreshLayout!!.isEnabled = true

        initAppListData(activity, false)
    }

    private fun initAppListData(context: Context?, forceRefresh: Boolean) {
        if (forceRefresh) {
            bookmarkModelList.clear()
        }
        bookmarkModelList.addAll(BookmarkModel.allBookmarkList)
        adapter.notifyDataSetChanged()
        swipeRefreshLayout!!.isRefreshing = false
    }
}
