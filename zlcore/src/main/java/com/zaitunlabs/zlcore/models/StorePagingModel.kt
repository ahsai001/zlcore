package com.zaitunlabs.zlcore.models

import android.provider.BaseColumns

import com.activeandroid.Model
import com.activeandroid.annotation.Column
import com.activeandroid.annotation.Table
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import java.io.Serializable
import java.util.Calendar
import java.util.Date

/**
 * Created by ahsai on 3/18/2018.
 */

@Table(name = "StorePaging", id = BaseColumns._ID)
class StorePagingModel : Model, Serializable {

    @Column(name = "countperpage")
    @SerializedName("countperpage")
    @Expose
    var countperpage: Int = 0

    @Column(name = "prev")
    @SerializedName("prev")
    @Expose
    var prev: Int = 0

    @Column(name = "next")
    @SerializedName("next")
    @Expose
    var next: Int = 0


    @Column(name = "timestamp", index = true)
    var timestamp: Date

    /**
     * No args constructor for use in serialization
     *
     */
    constructor() : super() {}

    /**
     *
     * @param countperpage
     * @param next
     * @param prev
     */
    constructor(countperpage: Int, prev: Int, next: Int) : super() {
        this.countperpage = countperpage
        this.prev = prev
        this.next = next
    }

    fun withCountperpage(countperpage: Int): StorePagingModel {
        this.countperpage = countperpage
        return this
    }

    fun withPrev(prev: Int): StorePagingModel {
        this.prev = prev
        return this
    }

    fun withNext(next: Int): StorePagingModel {
        this.next = next
        return this
    }

    fun saveWithTimeStamp() {
        timestamp = Calendar.getInstance().time
        save()
    }

    override fun toString(): String {
        return "StorePagingModel{" +
                "countperpage=" + countperpage +
                ", prev=" + prev +
                ", next=" + next +
                '}'.toString()
    }
}
