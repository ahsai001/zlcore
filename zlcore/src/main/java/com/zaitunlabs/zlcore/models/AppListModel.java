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

@Table(name = "AppList", id = BaseColumns._ID)
public class AppListModel extends Model implements Serializable {

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
    private AppListPagingModel paging;




    @SerializedName("data")
    @Expose
    private List<AppListDataModel> data = null;

    /**
     * No args constructor for use in serialization
     *
     */
    public AppListModel() {
        super();
    }

    /**
     *
     * @param message
     * @param status
     * @param data
     * @param paging
     */
    public AppListModel(int status, String message, AppListPagingModel paging, List<AppListDataModel> data) {
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

    public AppListModel withStatus(int status) {
        this.status = status;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public AppListModel withMessage(String message) {
        this.message = message;
        return this;
    }

    public AppListPagingModel getPaging() {
        return paging;
    }

    public void setPaging(AppListPagingModel paging) {
        this.paging = paging;
    }

    public AppListModel withPaging(AppListPagingModel paging) {
        this.paging = paging;
        return this;
    }

    public List<AppListDataModel> getData() {
        return data;
    }

    public void setData(List<AppListDataModel> data) {
        this.data = data;
    }

    public AppListModel withData(List<AppListDataModel> data) {
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
        return "AppListModel{" +
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
        List<AppListDataModel> appListDataModelList = getData();
        for (AppListDataModel appListDataModel: appListDataModelList){
            appListDataModel.appListModel = this;
            appListDataModel.saveWithTimeStamp();
        }
    }

    public void addNewDataListToCache(List<AppListDataModel> newAppListDataModelList){
        for (AppListDataModel appListDataModel: newAppListDataModelList){
            appListDataModel.appListModel = this;
            appListDataModel.saveWithTimeStamp();
        }
    }

    private static void deleteCache(){
        new Delete().from(AppListDataModel.class).execute();
        new Delete().from(AppListModel.class).execute();
        new Delete().from(AppListPagingModel.class).execute();
    }

    public static AppListModel getLastCache(){
        int CACHED_TIME = APIConstant.CACHED_TIME;
        AppListModel appListModel = new Select()
                .from(AppListModel.class).executeSingle();
        if(appListModel != null) {
            int timelapseHour = (int) ((Calendar.getInstance().getTimeInMillis() - appListModel.timestamp.getTime()) / 1000) / 3600;
            if (timelapseHour > CACHED_TIME) {
                deleteCache();
                return null;
            }

            //load other table
            List<AppListDataModel> appListDataModelList = new Select()
                    .from(AppListDataModel.class).execute();
            appListModel.setData(appListDataModelList);

            AppListPagingModel appListPagingModel = new Select()
                    .from(AppListPagingModel.class).executeSingle();
            appListModel.setPaging(appListPagingModel);
        }

        return appListModel;
    }
}