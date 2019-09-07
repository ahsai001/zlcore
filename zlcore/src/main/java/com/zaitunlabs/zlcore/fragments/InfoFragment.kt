package com.zaitunlabs.zlcore.fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil

import com.zaitunlabs.zlcore.R
import com.zaitunlabs.zlcore.adapters.InfoAdapter
import com.zaitunlabs.zlcore.core.WebViewActivity
import com.zaitunlabs.zlcore.models.InformationModel
import com.zaitunlabs.zlcore.core.BaseFragment
import com.zaitunlabs.zlcore.events.InfoPositionEvent
import com.zaitunlabs.zlcore.events.UpdateInfoListEvent
import com.zaitunlabs.zlcore.utils.CommonUtils
import com.zaitunlabs.zlcore.utils.EventsUtils
import com.zaitunlabs.zlcore.utils.InfoUtils
import com.zaitunlabs.zlcore.views.CustomRecylerView

import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

import java.net.URI
import java.util.ArrayList

/**
 * Created by ahsai on 6/13/2017.
 */

class InfoFragment : BaseFragment() {
    internal var recyclerView: CustomRecylerView
    internal var emptyView: View
    internal var mAdapter: InfoAdapter
    private val infoList = ArrayList<InformationModel>()

    fun setArg(isMeid: Boolean) {
        val b = Bundle()
        b.putBoolean(PARAM_IS_MEID, isMeid)
        arguments = b
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        EventsUtils.register(this)
        initInfoList()
    }

    private fun initInfoList() {
        mAdapter = InfoAdapter(infoList)

        val list = InformationModel.allInfo

        if (list.size > 0) {
            infoList.addAll(list)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_info, parent, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.info_recylerView)
        emptyView = view.findViewById(R.id.info_list_empty_view)

    }


    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onDestroy() {
        EventsUtils.unregister(this)
        super.onDestroy()
    }

    override fun onDetach() {
        super.onDetach()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val mLayoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = mLayoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()

        recyclerView.setEmptyView(emptyView)

        recyclerView.adapter = mAdapter

        /*
        recyclerView.addOnItemTouchListener(new RecyclerViewTouchListener(getActivity(), recyclerView, new RecyclerViewTouchListener.RecyclerViewItemClickListener() {
            @Override
            public void onClick(View view, int position) {
            }

            @Override
            public void onLongClick(View view, int position) {
            }
        }));*/


        val isMeid = CommonUtils.getBooleanFragmentArgument(arguments, PARAM_IS_MEID, false)

        mAdapter.setOnCardClickListener { view, position ->
            val info = infoList[position]

            when (info.type) {
                2, 3 ->
                    //text/photo
                    if (!TextUtils.isEmpty(info.infoUrl)) {
                        if (URLUtil.isValidUrl(info.infoUrl)) {
                            CommonUtils.openBrowser(view.context, info.infoUrl)
                        } else if (info.infoUrl!!.startsWith("webview://")) {
                            var htmlContent = info.infoUrl!!.replace("webview://", "")
                            if (htmlContent.startsWith("base64/")) {
                                htmlContent = htmlContent.replace("base64/", "")
                                htmlContent = CommonUtils.decodeBase64(htmlContent)
                            }
                            WebViewActivity.start(view.context, htmlContent, info.title, "",
                                    ContextCompat.getColor(view.context, android.R.color.white), info.title!! + info.id!!, isMeid)
                        } else if (info.infoUrl!!.startsWith("activity://")) {
                            val uri = Uri.parse(info.infoUrl)
                            if (uri != null) {
                                try {
                                    val nextClass = Class.forName(uri.host!!)
                                    val targetIntent = Intent(view.context, nextClass)
                                    val keys = uri.queryParameterNames
                                    for (key in keys) {
                                        val value = uri.getQueryParameter(key)
                                        targetIntent.putExtra(key, value)
                                    }
                                    view.context.startActivity(targetIntent)
                                } catch (e: ClassNotFoundException) {
                                    e.printStackTrace()
                                }

                            }
                        }
                    }
            }

            //update read status
            if (!info.isRead) {
                info.isRead = true

                //save to DB
                info.save()

                //notify list
                InfoUtils.notifyUpdateInfoList(position, true)

                //notify infoCounter
                InfoUtils.notifyInfoCounter()
            }
        }

        mAdapter.setOnMoreOptionClickListener { view, position ->
            val info = infoList[position]
            CommonUtils.showPopupMenu(view.context, if (info.isRead) R.menu.menu_info_item_unread else R.menu.menu_info_item, view, null,
                    PopupMenu.OnMenuItemClickListener { item ->
                        if (item.itemId == R.id.action_mark_as_read) {
                            info.isRead = true
                            info.save()
                            InfoUtils.notifyInfoCounter()
                            InfoUtils.notifyUpdateInfoList(position, true)
                        } else if (item.itemId == R.id.action_mark_as_unread) {
                            info.isRead = false
                            info.save()
                            InfoUtils.notifyInfoCounter()
                            InfoUtils.notifyUpdateInfoList(position, false)
                        } else if (item.itemId == R.id.action_delete) {
                            info.delete()
                            infoList.removeAt(position)
                            mAdapter.notifyDataSetChanged()
                            InfoUtils.notifyInfoCounter()
                        }
                        true
                    })
        }

        loadInfo()
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: InformationModel) {
        //update list
        infoList.add(0, event) //new info place at the top
        mAdapter.notifyItemInserted(0)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: InfoPositionEvent) {
        val infoId = event.infoId
        if (infoId > -1) {
            for (i in infoList.indices) {
                if (infoId == infoList[i].id) {
                    recyclerView.smoothScrollToPosition(i)
                    break
                }
            }
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: UpdateInfoListEvent) {
        infoList[event.position].isRead = event.readStatus
        mAdapter.notifyItemChanged(event.position)
    }


    private fun loadInfo() {
        mAdapter.notifyDataSetChanged()
        InfoUtils.notifyInfoCounter()
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_message_list, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.action_mark_all_as_read).isEnabled = InformationModel.unreadInfoCount() > 0
        menu.findItem(R.id.action_delete_all).isEnabled = InformationModel.allInfoCount() > 0
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_mark_all_as_read) {
            InformationModel.markAllAsRead()
            mAdapter.markAllAsRead()
            InfoUtils.notifyInfoCounter()
            activity!!.invalidateOptionsMenu()
            CommonUtils.showSnackBar(activity, getString(R.string.zlcore_infofragment_mark_all_as_read_success))
            return true
        } else if (item.itemId == R.id.action_delete_all) {
            InformationModel.deleteAllInfo()
            infoList.clear()
            mAdapter.notifyDataSetChanged()
            InfoUtils.notifyInfoCounter()
            activity!!.invalidateOptionsMenu()
            CommonUtils.showSnackBar(activity, getString(R.string.zlcore_infofragment_delete_all_messages_success))
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        val PARAM_IS_MEID = "param_is_meid"
    }
}
