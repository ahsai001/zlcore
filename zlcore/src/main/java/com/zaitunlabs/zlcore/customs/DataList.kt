package com.zaitunlabs.zlcore.customs

import java.util.ArrayList

/**
 * Created by ahsai on 6/2/2018.
 */

class DataList<T> {
    private val arrayList: MutableList<T>

    init {
        this.arrayList = ArrayList()
    }

    fun add(data: T): DataList<T> {
        this.arrayList.add(data)
        return this
    }

    fun addAll(arrayList: List<T>): DataList<T> {
        this.arrayList.addAll(arrayList)
        return this
    }

    fun getArrayList(): List<T> {
        return this.arrayList
    }

}
