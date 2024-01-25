package com.zaitunlabs.zlcore.customs;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ahsai on 6/2/2018.
 */

public class DataList<T> {
    private List<T> arrayList;

    public DataList(){
        this.arrayList = new ArrayList<T>();
    }

    public DataList<T> add(T data){
        this.arrayList.add(data);
        return this;
    }

    public DataList<T> addAll(List<T> arrayList){
        this.arrayList.addAll(arrayList);
        return this;
    }

    public List<T> getArrayList(){
        return this.arrayList;
    }

}
