package com.zaitunlabs.zlcore.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;

import com.zaitunlabs.zlcore.R;
import com.zaitunlabs.zlcore.adapters.InfoAdapter;
import com.zaitunlabs.zlcore.core.WebViewActivity;
import com.zaitunlabs.zlcore.tables.InformationModel;
import com.zaitunlabs.zlcore.core.BaseFragment;
import com.zaitunlabs.zlcore.events.InfoPositionEvent;
import com.zaitunlabs.zlcore.events.UpdateInfoListEvent;
import com.zaitunlabs.zlcore.utils.CommonUtil;
import com.zaitunlabs.zlcore.utils.EventsUtil;
import com.zaitunlabs.zlcore.utils.InfoUtil;
import com.zaitunlabs.zlcore.views.CustomRecylerView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by ahsai on 6/13/2017.
 */

public class InfoFragment extends BaseFragment {
    public static final String PARAM_IS_MEID = "param_is_meid";
    CustomRecylerView recyclerView;
    View emptyView;
    InfoAdapter mAdapter;
    private List<InformationModel> infoList = new ArrayList<>();

    public InfoFragment() {
    }

    public void setArg(boolean isMeid){
        Bundle b = new Bundle();
        b.putBoolean(PARAM_IS_MEID, isMeid);
        setArguments(b);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        EventsUtil.register(this);
        initInfoList();
    }

    private void initInfoList(){
        mAdapter = new InfoAdapter(infoList);

        List<InformationModel> list = InformationModel.getAllInfo();

        if(list.size() > 0) {
            infoList.addAll(list);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_info,parent,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.info_recylerView);
        emptyView = view.findViewById(R.id.info_list_empty_view);

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        EventsUtil.unregister(this);
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setEmptyView(emptyView);

        recyclerView.setAdapter(mAdapter);

        /*
        recyclerView.addOnItemTouchListener(new RecyclerViewTouchListener(getActivity(), recyclerView, new RecyclerViewTouchListener.RecyclerViewItemClickListener() {
            @Override
            public void onClick(View view, int position) {
            }

            @Override
            public void onLongClick(View view, int position) {
            }
        }));*/


        final boolean isMeid = CommonUtil.getBooleanFragmentArgument(getArguments(), PARAM_IS_MEID, false);

        mAdapter.setOnCardClickListener(new InfoAdapter.OnCardClickListener() {
            @Override
            public void onClick(View view, int position) {
                InformationModel info = infoList.get(position);

                switch (info.getType()){
                    case 2:
                    case 3:
                        //text/photo
                        if(!TextUtils.isEmpty(info.getInfoUrl())) {
                            if (URLUtil.isValidUrl(info.getInfoUrl())){
                                CommonUtil.openBrowser(view.getContext(), info.getInfoUrl());
                            } else if(info.getInfoUrl().startsWith("webview://")){
                                String htmlContent = info.getInfoUrl().replace("webview://","");
                                if(htmlContent.startsWith("base64/")) {
                                    htmlContent = htmlContent.replace("base64/", "");
                                    htmlContent = CommonUtil.decodeBase64(htmlContent);
                                }
                                WebViewActivity.start(view.getContext(),htmlContent,info.getTitle(), "",
                                        ContextCompat.getColor(view.getContext(),android.R.color.white),info.getTitle()+info._id, isMeid);
                            } else if(info.getInfoUrl().startsWith("activity://")){
                                Uri uri = Uri.parse(info.getInfoUrl());
                                if(uri != null) {
                                    try {
                                        Class nextClass = Class.forName(uri.getHost());
                                        Intent targetIntent = new Intent(view.getContext(), nextClass);
                                        Set<String> keys = uri.getQueryParameterNames();
                                        for (String key : keys){
                                            String value = uri.getQueryParameter(key);
                                            targetIntent.putExtra(key,value);
                                        }
                                        view.getContext().startActivity(targetIntent);
                                    } catch (ClassNotFoundException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }


                        break;
                }

                //update read status
                if(!info.isRead()) {
                    info.setRead(true);

                    //save to DB
                    info.save();

                    //notify list
                    InfoUtil.notifyUpdateInfoList(position, true);

                    //notify infoCounter
                    InfoUtil.notifyInfoCounter();
                }
            }
        });

        mAdapter.setOnMoreOptionClickListener(new InfoAdapter.OnMoreOptionClickListener() {
            @Override
            public void onClick(View view, final int position) {
                final InformationModel info = infoList.get(position);
                CommonUtil.showPopupMenu(view.getContext(), info.isRead()?R.menu.menu_info_item_unread:R.menu.menu_info_item, view, null,
                        new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                if(item.getItemId() == R.id.action_mark_as_read) {
                                    info.setRead(true);
                                    info.save();
                                    InfoUtil.notifyInfoCounter();
                                    InfoUtil.notifyUpdateInfoList(position, true);
                                } else if(item.getItemId() == R.id.action_mark_as_unread) {
                                    info.setRead(false);
                                    info.save();
                                    InfoUtil.notifyInfoCounter();
                                    InfoUtil.notifyUpdateInfoList(position, false);
                                } else if(item.getItemId() == R.id.action_delete) {
                                    info.delete();
                                    infoList.remove(position);
                                    mAdapter.notifyDataSetChanged();
                                    InfoUtil.notifyInfoCounter();
                                }
                                return true;
                            }
                        });
            }
        });

        loadInfo();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(InformationModel event){
        //update list
        infoList.add(0,event); //new info place at the top
        mAdapter.notifyItemInserted(0);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(InfoPositionEvent event){
        long infoId = event.getInfoId();
        if(infoId > -1){
            for(int i=0; i<infoList.size(); i++){
                if(infoId == infoList.get(i)._id){
                    recyclerView.smoothScrollToPosition(i);
                    break;
                }
            }
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(UpdateInfoListEvent event){
        infoList.get(event.getPosition()).setRead(event.getReadStatus());
        mAdapter.notifyItemChanged(event.getPosition());
    }


    private void loadInfo() {
        mAdapter.notifyDataSetChanged();
        InfoUtil.notifyInfoCounter();
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_message_list,menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_mark_all_as_read).setEnabled(InformationModel.unreadInfoCount() > 0);
        menu.findItem(R.id.action_delete_all).setEnabled(InformationModel.allInfoCount() > 0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_mark_all_as_read){
            InformationModel.markAllAsRead();
            mAdapter.markAllAsRead();
            InfoUtil.notifyInfoCounter();
            getActivity().invalidateOptionsMenu();
            CommonUtil.showSnackBar(getActivity(),getString(R.string.zlcore_infofragment_mark_all_as_read_success));
            return true;
        } else if (item.getItemId() == R.id.action_delete_all){
            InformationModel.deleteAllInfo();
            infoList.clear();
            mAdapter.notifyDataSetChanged();
            InfoUtil.notifyInfoCounter();
            getActivity().invalidateOptionsMenu();
            CommonUtil.showSnackBar(getActivity(),getString(R.string.zlcore_infofragment_delete_all_messages_success));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
