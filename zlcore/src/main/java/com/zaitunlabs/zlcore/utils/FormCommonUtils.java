package com.zaitunlabs.zlcore.utils;

import android.content.Context;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.zaitunlabs.zlcore.customs.DataList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ahsai on 5/30/2018.
 */

public class FormCommonUtils {
    public static void setSpinnerList(Context context, Spinner spinner, DataList<String> titleViewList,
                                      DataList<String> valueList,
                                      AdapterView.OnItemSelectedListener onItemSelectedListener){
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_dropdown_item_1line, titleViewList.getArrayList());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setTag(valueList.getArrayList());
        if(onItemSelectedListener != null) {
            spinner.setOnItemSelectedListener(onItemSelectedListener);
        }
    }

    public static void setSpinnerList(Context context, Spinner spinner, List<String> titleViewList,
                                      List<String> valueList,
                                      AdapterView.OnItemSelectedListener onItemSelectedListener){
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_dropdown_item_1line, titleViewList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setTag(valueList);
        if(onItemSelectedListener != null) {
            spinner.setOnItemSelectedListener(onItemSelectedListener);
        }
    }
}
