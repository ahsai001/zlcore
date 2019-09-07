package com.zaitunlabs.zlcore.core

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.zaitunlabs.zlcore.utils.DebugUtils

import java.util.ArrayList

/**
 * Created by ahsai on 6/19/2017.
 */

open class BaseFragment : Fragment() {
    protected val TAG = this@BaseFragment.javaClass.getSimpleName()
    private var bundle: Bundle? = null

    private val asyncTaskList = ArrayList<AsyncTask<*, *, *>>()

    fun getBundle(): Bundle {
        if (bundle == null) {
            bundle = Bundle()
        }
        return bundle
    }

    fun saveAsArgument() {
        if (bundle != null) {
            arguments = bundle
        }
    }

    override fun onAttach(context: Context) {
        DebugUtils.logV(TAG, "onAttach")
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        DebugUtils.logV(TAG, "onCreate")
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        DebugUtils.logV(TAG, "onCreateView")
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        DebugUtils.logV(TAG, "onViewCreated")
        super.onViewCreated(view, savedInstanceState)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        DebugUtils.logV(TAG, "onActivityCreated")
        super.onActivityCreated(savedInstanceState)
    }

    override fun onDestroyView() {
        DebugUtils.logV(TAG, "onDestroyView")
        for (asyncTask in asyncTaskList) {
            if (asyncTask.status == AsyncTask.Status.RUNNING) {
                asyncTask.cancel(true)
            }
            asyncTaskList.remove(asyncTask)
        }
        super.onDestroyView()
    }

    override fun onDestroy() {
        DebugUtils.logV(TAG, "onDestroy")
        super.onDestroy()
    }


    override fun onDetach() {
        DebugUtils.logV(TAG, "onDetach")
        super.onDetach()
    }

    protected fun addAsync(asyncTask: AsyncTask<*, *, *>) {
        asyncTaskList.add(asyncTask)
    }

    protected fun removeAsync(asyncTask: AsyncTask<*, *, *>) {
        asyncTaskList.remove(asyncTask)
    }
}
