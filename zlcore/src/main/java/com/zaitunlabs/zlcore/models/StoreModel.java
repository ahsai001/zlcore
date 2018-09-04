package com.zaitunlabs.zlcore.models;

import android.provider.BaseColumns;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.zaitunlabs.zlcore.api.APIConstant;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by ahsai on 3/18/2018.
 */

@Table(name = "Store", id = BaseColumns._ID)
public class StoreModel extends Model implements Serializable {

    @Column(name = "status", index = true)
    @SerializedName("status")
    @Expose
    private int status;

    @Column(name = "message")
    @SerializedName("message")
    @Expose
    private String message;

    @Column(name = "paging")
    @SerializedName("paging")
    @Expose
    private StorePagingModel paging;




    @SerializedName("data")
    @Expose
    private List<StoreDataModel> data = null;

    /**
     * No args constructor for use in serialization
     *
     */
    public StoreModel() {
        super();
    }

    /**
     *
     * @param message
     * @param status
     * @param data
     * @param paging
     */
    public StoreModel(int status, String message, StorePagingModel paging, List<StoreDataModel> data) {
        super();
        this.status = status;
        this.message = message;
        this.paging = paging;
        this.data = data;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public StoreModel withStatus(int status) {
        this.status = status;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public StoreModel withMessage(String message) {
        this.message = message;
        return this;
    }

    public StorePagingModel getPaging() {
        return paging;
    }

    public void setPaging(StorePagingModel paging) {
        this.paging = paging;
    }

    public StoreModel withPaging(StorePagingModel paging) {
        this.paging = paging;
        return this;
    }

    public List<StoreDataModel> getData() {
        return data;
    }

    public void setData(List<StoreDataModel> data) {
        this.data = data;
    }

    public StoreModel withData(List<StoreDataModel> data) {
        this.data = data;
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
        return "StoreModel{" +
                "status=" + status +
                ", message='" + message + '\'' +
                ", paging=" + paging +
                ", data=" + data +
                '}';
    }


    public void cache(boolean deletePrev){
        if(deletePrev){
            deleteCache();
        }
        getPaging().saveWithTimeStamp();
        saveWithTimeStamp();
        List<StoreDataModel> storeDataModelList = getData();
        for (StoreDataModel storeDataModel: storeDataModelList){
            storeDataModel.storeModel = this;
            storeDataModel.saveWithTimeStamp();
        }
    }

    public void addNewDataListToCache(List<StoreDataModel> newStoreDataModelList){
        for (StoreDataModel storeDataModel: newStoreDataModelList){
            storeDataModel.storeModel = this;
            storeDataModel.saveWithTimeStamp();
        }
    }

    private static void deleteCache(){
        new Delete().from(StoreDataModel.class).execute();
        new Delete().from(StoreModel.class).execute();
        new Delete().from(StorePagingModel.class).execute();
    }

    public static StoreModel getLastCache(){
        int CACHED_TIME = APIConstant.CACHED_TIME;
        StoreModel storeModel = new Select()
                .from(StoreModel.class).executeSingle();
        if(storeModel != null) {
            int timelapseHour = (int) ((Calendar.getInstance().getTimeInMillis() - storeModel.timestamp.getTime()) / 1000) / 3600;
            if (timelapseHour > CACHED_TIME) {
                deleteCache();
                return null;
            }

            //load other table
            List<StoreDataModel> storeDataModelList = new Select()
                    .from(StoreDataModel.class).execute();
            storeModel.setData(storeDataModelList);

            StorePagingModel storePagingModel = new Select()
                    .from(StorePagingModel.class).executeSingle();
            storeModel.setPaging(storePagingModel);
        }

        return storeModel;
    }
}