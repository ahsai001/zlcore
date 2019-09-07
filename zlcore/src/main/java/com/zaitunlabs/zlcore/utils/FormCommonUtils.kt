package com.zaitunlabs.zlcore.utils

import android.content.Context
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner

import com.zaitunlabs.zlcore.customs.DataList

import java.util.ArrayList

/**
 * Created by ahsai on 5/30/2018.
 */

object FormCommonUtils {
    fun setSpinnerList(context: Context, spinner: Spinner, titleViewList: DataList<String>,
                       valueList: DataList<String>,
                       onItemSelectedListener: AdapterView.OnItemSelectedListener?) {
        val adapter = ArrayAdapter(context,
                android.R.layout.simple_dropdown_item_1line, titleViewList.arrayList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.tag = valueList.arrayList
        if (onItemSelectedListener != null) {
            spinner.onItemSelectedListener = onItemSelectedListener
        }
    }

    fun setSpinnerList(context: Context, spinner: Spinner, titleViewList: List<String>,
                       valueList: List<String>,
                       onItemSelectedListener: AdapterView.OnItemSelectedListener?) {
        val adapter = ArrayAdapter(context,
                android.R.layout.simple_dropdown_item_1line, titleViewList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.tag = valueList
        if (onItemSelectedListener != null) {
            spinner.onItemSelectedListener = onItemSelectedListener
        }
    }
}
