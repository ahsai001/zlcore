package com.zaitunlabs.zlcore.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
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
import com.zaitunlabs.zlcore.models.InformationModel;
import com.zaitunlabs.zlcore.core.BaseFragment;
import com.zaitunlabs.zlcore.events.InfoPositionEvent;
import com.zaitunlabs.zlcore.events.UpdateInfoListEvent;
import com.zaitunlabs.zlcore.utils.CommonUtils;
import com.zaitunlabs.zlcore.utils.EventsUtils;
import com.zaitunlabs.zlcore.utils.InfoUtils;
import com.zaitunlabs.zlcore.views.CustomRecylerView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ahsai on 6/13/2017.
 */

public class InfoFragment extends BaseFragment {

    CustomRecylerView recyclerView;
    View emptyView;
    InfoAdapter mAdapter;
    private List<InformationModel> infoList = new ArrayList<>();

    public InfoFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        EventsUtils.register(this);
        initInfoList();
    }

    private void initInfoList(){
        mAdapter = new InfoAdapter(infoList);

        List<InformationModel> list = InformationModel.getAllInfo();

        /*
        if(list.size() == 0){
            prepareMovieData();
            list = InformationModel.getAllInfo();
        }*/

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
        EventsUtils.unregister(this);
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
                                CommonUtils.openBrowser(view.getContext(), info.getInfoUrl());
                            } else {
                                //may be this is activity name
                                try {
                                    Class nextClass = Class.forName(info.getInfoUrl());
                                    Intent targetIntent = new Intent(view.getContext().getApplicationContext(), nextClass);
                                    view.getContext().startActivity(targetIntent);
                                } catch (ClassNotFoundException e) {
                                    e.printStackTrace();
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
                    InfoUtils.notifyUpdateInfoList(position, true);

                    //notify infoCounter
                    InfoUtils.notifyInfoCounter();
                }
            }
        });

        mAdapter.setOnMoreOptionClickListener(new InfoAdapter.OnMoreOptionClickListener() {
            @Override
            public void onClick(View view, final int position) {
                final InformationModel info = infoList.get(position);
                CommonUtils.showPopup(view.getContext(), info.isRead()?R.menu.menu_info_item_unread:R.menu.menu_info_item, view, null,
                        new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                if(item.getItemId() == R.id.action_mark_as_read) {
                                    info.setRead(true);
                                    info.save();
                                    InfoUtils.notifyInfoCounter();
                                    InfoUtils.notifyUpdateInfoList(position, true);
                                } else if(item.getItemId() == R.id.action_mark_as_unread) {
                                    info.setRead(false);
                                    info.save();
                                    InfoUtils.notifyInfoCounter();
                                    InfoUtils.notifyUpdateInfoList(position, false);
                                } else if(item.getItemId() == R.id.action_delete) {
                                    info.delete();
                                    infoList.remove(position);
                                    mAdapter.notifyDataSetChanged();
                                    InfoUtils.notifyInfoCounter();
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
                if(infoId == infoList.get(i).getId()){
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
        InfoUtils.notifyInfoCounter();
    }

    private void prepareMovieData() {
        InformationModel movie = new InformationModel("Mad Max: Fury Road", "Action & Adventure", "http://tempatwisatadibandung.info/wp-content/uploads/2015/08/gunung-tangkuban-perahu.jpg", "https://www.google.com",2);
        movie.saveWithTimeStamp();

        movie = new InformationModel("Inside Out", "Animation, Kids & Family fsdf sdfasdf asdfasd fasdf asdfa sdfasdfasdf asdfasdf asdfasdfa sdfadsf asdfasdfa sdfasdfa sdfadfa sdfasdfa sdfasdfa sdfasdf ", "2015", "https://www.facebook.com",1);
        movie.saveWithTimeStamp();

        movie = new InformationModel("Star Wars: Episode VII - The Force Awakens", "Action", "2015", "",1);
        movie.saveWithTimeStamp();

        movie = new InformationModel("Shaun the Sheep", "Animation", "http://image-serve.hipwee.com/wp-content/uploads/2016/08/hipwee-pendaki-perempuan-tewas-saat-berendam-di-air-panas-gunung-rinjani.jpg", "",1);
        movie.saveWithTimeStamp();

        movie = new InformationModel("The Martian", "Science Fiction & Fantasy sd fasd asdf asdf asdfadsfasdfasdfasdfasdf ads fadsfasdfsadfasdfasdfadsff adsfadsfasdfasdfas daf sdf adsf adsf adfadf", "2015", "",1);
        movie.saveWithTimeStamp();

        movie = new InformationModel("Mission: Impossible Rogue Nation", "Action", "2015", "",1);
        movie.saveWithTimeStamp();

        movie = new InformationModel("Up", "Animation", "2009", "",1);
        movie.saveWithTimeStamp();

        movie = new InformationModel("Star Trek", "Science Fiction", "2009", "",1);
        movie.saveWithTimeStamp();

        movie = new InformationModel("The LEGO Movie", "Animation", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTRRkXzYclfsunkRxpD3ZVw_10oB1kRk1sfozwSTBZJjkZWSPlCdA", "",1);
        movie.saveWithTimeStamp();

        movie = new InformationModel("Iron Man", "Action & Adventure", "2008", "",1);
        movie.saveWithTimeStamp();

        movie = new InformationModel("Aliens", "Science Fiction", "1986", "",1);
        movie.saveWithTimeStamp();

        movie = new InformationModel("Chicken Run", "Animation", "2000", "",1);
        movie.saveWithTimeStamp();

        movie = new InformationModel("Back to the Future", "Science Fiction", "1985", "",1);
        movie.saveWithTimeStamp();
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
            InfoUtils.notifyInfoCounter();
            getActivity().invalidateOptionsMenu();
            CommonUtils.showSnackBar(getActivity(),"mark all as read success");
            return true;
        } else if (item.getItemId() == R.id.action_delete_all){
            InformationModel.deleteAllInfo();
            infoList.clear();
            mAdapter.notifyDataSetChanged();
            InfoUtils.notifyInfoCounter();
            getActivity().invalidateOptionsMenu();
            CommonUtils.showSnackBar(getActivity(),"delete all messages success");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
