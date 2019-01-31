package com.zaitunlabs.zlcore.core;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zaitunlabs.zlcore.utils.DebugUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ahsai on 6/19/2017.
 */

public class BaseFragment extends Fragment{
    protected final String TAG = BaseFragment.this.getClass().getSimpleName();
    private Bundle bundle;
    public BaseFragment(){
    }

    public Bundle getBundle() {
        if(bundle == null){
            bundle = new Bundle();
        }
        return bundle;
    }

    public void saveAsArgument(){
        if(bundle != null) {
            setArguments(bundle);
        }
    }

    @Override
    public void onAttach(Context context) {
        DebugUtils.logV(TAG,"onAttach");
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        DebugUtils.logV(TAG,"onCreate");
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        DebugUtils.logV(TAG,"onCreateView");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        DebugUtils.logV(TAG,"onViewCreated");
        super.onViewCreated(view, savedInstanceState);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        DebugUtils.logV(TAG,"onActivityCreated");
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        DebugUtils.logV(TAG,"onDestroyView");
        for (AsyncTask asyncTask : asyncTaskList) {
            if (asyncTask.getStatus() == AsyncTask.Status.RUNNING){
                asyncTask.cancel(true);
            }
            asyncTaskList.remove(asyncTask);
        }
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        DebugUtils.logV(TAG,"onDestroy");
        super.onDestroy();
    }


    @Override
    public void onDetach() {
        DebugUtils.logV(TAG,"onDetach");
        super.onDetach();
    }

    private List<AsyncTask> asyncTaskList = new ArrayList<>();
    protected void addAsync(AsyncTask asyncTask){
        asyncTaskList.add(asyncTask);
    }

    protected void removeAsync(AsyncTask asyncTask){
        asyncTaskList.remove(asyncTask);
    }
}
