package com.zaitunlabs.zlcore.tables;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.zaitunlabs.zlcore.api.APIConstant;
import com.zaitunlabs.zlcore.core.BaseApplication;
import com.zaitunlabs.zlcore.utils.SQLiteWrapper;

import java.util.Calendar;
import java.util.List;

/**
 * Created by ahsai on 3/18/2018.
 */

public class AppListModel extends SQLiteWrapper.TableClass {
    @SerializedName("status")
    @Expose
    private int status;

    @SerializedName("message")
    @Expose
    private String message;







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


    public void cache(boolean deletePrev){
        if(deletePrev){
            deleteCache();
        }
        save();
        getPaging().appListModel = this;
        getPaging().save();
        List<AppListDataModel> appListDataModelList = getData();
        for (AppListDataModel appListDataModel: appListDataModelList){
            appListDataModel.appListModel = this;
            appListDataModel.save();
        }
    }

    public void addNewDataListToCache(List<AppListDataModel> newAppListDataModelList){
        for (AppListDataModel appListDataModel: newAppListDataModelList){
            appListDataModel.appListModel = this;
            appListDataModel.save();
        }
    }

    private static void deleteCache(){
        SQLiteWrapper.of(BaseApplication.DATABASE_NAME).deleteAll(null, AppListModel.class);

        /*new Delete().from(AppListDataModel.class).execute();
        new Delete().from(AppListModel.class).execute();
        new Delete().from(AppListPagingModel.class).execute();*/
    }

    public static AppListModel getLastCache(){
        int CACHED_TIME = APIConstant.CACHED_TIME;
        AppListModel appListModel = SQLiteWrapper.of(BaseApplication.DATABASE_NAME).findFirst(null, AppListModel.class);
        if(appListModel != null) {
            int timelapseHour = (int) ((Calendar.getInstance().getTimeInMillis() - appListModel._created_at.getTime()) / 1000) / 3600;
            if (timelapseHour > CACHED_TIME) {
                deleteCache();
                return null;
            }

            //load other table
            List<AppListDataModel> appListDataModelList = SQLiteWrapper.of(BaseApplication.DATABASE_NAME).findAllWithCriteria(null, AppListDataModel.class,
                    "applist_model_id=?", new String[]{Long.toString(appListModel._id)});
            appListModel.setData(appListDataModelList);

            AppListPagingModel appListPagingModel = SQLiteWrapper.of(BaseApplication.DATABASE_NAME).findFirstWithCriteria(null, AppListPagingModel.class,
                    "applist_model_id=?", new String[]{Long.toString(appListModel._id)});
            appListModel.setPaging(appListPagingModel);
        }

        return appListModel;
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



    @Override
    protected String getDatabaseName() {
        return BaseApplication.DATABASE_NAME;
    }

    @Override
    protected void getObjectData(List<Object> dataList) {
        dataList.add(status);
        dataList.add(message);
    }


    @Override
    protected void setObjectData(List<Object> dataList) {
        status = (int) dataList.get(0);
        message = (String) dataList.get(1);
    }
}