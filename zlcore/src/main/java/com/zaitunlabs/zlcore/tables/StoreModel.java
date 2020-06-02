package com.zaitunlabs.zlcore.tables;

import com.ahsailabs.sqlitewrapper.SQLiteWrapper;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.zaitunlabs.zlcore.api.APIConstant;
import com.zaitunlabs.zlcore.core.BaseApplication;

import java.util.Calendar;
import java.util.List;

/**
 * Created by ahsai on 3/18/2018.
 */

public class StoreModel extends SQLiteWrapper.TableClass {
    @SerializedName("status")
    @Expose
    private int status;

    @SerializedName("message")
    @Expose
    private String message;







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


    public void cache(boolean deletePrev){
        if(deletePrev){
            deleteCache();
        }
        save();
        getPaging().storeModel = this;
        getPaging().save();
        List<StoreDataModel> storeDataModelList = getData();
        for (StoreDataModel storeDataModel: storeDataModelList){
            storeDataModel.storeModel = this;
            storeDataModel.save();
        }
    }

    public void addNewDataListToCache(List<StoreDataModel> newStoreDataModelList){
        for (StoreDataModel storeDataModel: newStoreDataModelList){
            storeDataModel.storeModel = this;
            storeDataModel.save();
        }
    }

    private static void deleteCache(){
        SQLiteWrapper.of(BaseApplication.DATABASE_NAME).deleteAll(null, StoreModel.class);

        /*new Delete().from(StoreDataModel.class).execute();
        new Delete().from(StoreModel.class).execute();
        new Delete().from(StorePagingModel.class).execute();*/
    }

    public static StoreModel getLastCache(){
        int CACHED_TIME = APIConstant.CACHED_TIME;
        StoreModel storeModel = SQLiteWrapper.of(BaseApplication.DATABASE_NAME).findFirst(null, StoreModel.class);
        if(storeModel != null) {
            int timelapseHour = (int) ((Calendar.getInstance().getTimeInMillis() - storeModel._created_at.getTime()) / 1000) / 3600;
            if (timelapseHour > CACHED_TIME) {
                deleteCache();
                return null;
            }

            //load other table
            List<StoreDataModel> storeDataModelList = SQLiteWrapper.of(BaseApplication.DATABASE_NAME).findAllWithCriteria(null, StoreDataModel.class,
                    "store_model_id=?", new String[]{Long.toString(storeModel._id)});
            storeModel.setData(storeDataModelList);

            StorePagingModel storePagingModel = SQLiteWrapper.of(BaseApplication.DATABASE_NAME).findFirstWithCriteria(null, StorePagingModel.class,
                    "store_model_id=?", new String[]{Long.toString(storeModel._id)});
            storeModel.setPaging(storePagingModel);
        }

        return storeModel;
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