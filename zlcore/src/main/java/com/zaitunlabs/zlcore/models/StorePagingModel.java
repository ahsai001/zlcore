package com.zaitunlabs.zlcore.models;

import android.provider.BaseColumns;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by ahsai on 3/18/2018.
 */

@Table(name = "StorePaging", id = BaseColumns._ID)
public class StorePagingModel extends Model implements Serializable {

    @Column(name = "countperpage")
    @SerializedName("countperpage")
    @Expose
    private int countperpage;

    @Column(name = "prev")
    @SerializedName("prev")
    @Expose
    private int prev;

    @Column(name = "next")
    @SerializedName("next")
    @Expose
    private int next;

    /**
     * No args constructor for use in serialization
     *
     */
    public StorePagingModel() {
        super();
    }

    /**
     *
     * @param countperpage
     * @param next
     * @param prev
     */
    public StorePagingModel(int countperpage, int prev, int next) {
        super();
        this.countperpage = countperpage;
        this.prev = prev;
        this.next = next;
    }

    public int getCountperpage() {
        return countperpage;
    }

    public void setCountperpage(int countperpage) {
        this.countperpage = countperpage;
    }

    public StorePagingModel withCountperpage(int countperpage) {
        this.countperpage = countperpage;
        return this;
    }

    public int getPrev() {
        return prev;
    }

    public void setPrev(int prev) {
        this.prev = prev;
    }

    public StorePagingModel withPrev(int prev) {
        this.prev = prev;
        return this;
    }

    public int getNext() {
        return next;
    }

    public void setNext(int next) {
        this.next = next;
    }

    public StorePagingModel withNext(int next) {
        this.next = next;
        return this;
    }


    @Column(name = "timestamp", index = true)
    public Date timestamp;

    public void saveWithTimeStamp(){
        timestamp = Calendar.getInstance().getTime();
        save();
    }

    @Override
    public String toString() {
        return "StorePagingModel{" +
                "countperpage=" + countperpage +
                ", prev=" + prev +
                ", next=" + next +
                '}';
    }
}
