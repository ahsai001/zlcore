package com.zaitunlabs.zlcore.tables;

import com.ahsailabs.sqlitewrapper.SQLiteWrapper;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.zaitunlabs.zlcore.core.BaseApplication;

import java.util.List;

/**
 * Created by ahsai on 3/18/2018.
 */

public class StorePagingModel extends SQLiteWrapper.TableClass {

    @SerializedName("countperpage")
    @Expose
    private int countperpage;

    @SerializedName("prev")
    @Expose
    private int prev;

    @SerializedName("next")
    @Expose
    private int next;

    public StoreModel storeModel;

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

    @Override
    public String toString() {
        return "StorePagingModel{" +
                "countperpage=" + countperpage +
                ", prev=" + prev +
                ", next=" + next +
                '}';
    }

    @Override
    protected String getDatabaseName() {
        return BaseApplication.DATABASE_NAME;
    }


    @Override
    protected void getObjectData(List<Object> dataList) {
        dataList.add(countperpage);
        dataList.add(prev);
        dataList.add(next);
        dataList.add(storeModel);
    }

    @Override
    protected void setObjectData(List<Object> dataList) {
        countperpage = (int) dataList.get(0);
        prev = (int) dataList.get(1);
        next = (int) dataList.get(2);
        storeModel = (StoreModel) dataList.get(3);
    }
}
