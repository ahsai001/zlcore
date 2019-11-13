package com.zaitunlabs.zlcore.tables;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.zaitunlabs.zlcore.core.BaseApplication;
import com.zaitunlabs.zlcore.utils.SQLiteWrapper;

import java.util.List;

/**
 * Created by ahsai on 3/18/2018.
 */

public class StoreDataModel extends SQLiteWrapper.TableClass {

    @SerializedName("image")
    @Expose
    private String image;

    @SerializedName("title")
    @Expose
    private String title;


    @SerializedName("desc")
    @Expose
    private String desc;



    @SerializedName("unique")
    @Expose
    private String unique;


    @SerializedName("url")
    @Expose
    private String url;


    public StoreModel storeModel;

    /**
     * No args constructor for use in serialization
     *
     */
    public StoreDataModel() {
        super();
    }

    /**
     *
     * @param title
     * @param unique
     * @param desc
     * @param image
     * @param url
     */
    public StoreDataModel(String image, String title, String desc, String unique, String url) {
        super();
        this.image = image;
        this.title = title;
        this.desc = desc;
        this.unique = unique;
        this.url = url;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public StoreDataModel withImage(String image) {
        this.image = image;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public StoreDataModel withTitle(String title) {
        this.title = title;
        return this;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public StoreDataModel withDesc(String desc) {
        this.desc = desc;
        return this;
    }

    public String getUnique() {
        return unique;
    }

    public void setUnique(String unique) {
        this.unique = unique;
    }

    public StoreDataModel withUnique(String unique) {
        this.unique = unique;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public StoreDataModel withUrl(String url) {
        this.url = url;
        return this;
    }

    @Override
    public String toString() {
        return "StoreDataModel{" +
                "image='" + image + '\'' +
                ", title='" + title + '\'' +
                ", desc='" + desc + '\'' +
                ", unique='" + unique + '\'' +
                ", url='" + url + '\'' +
                '}';
    }


    @Override
    protected String getDatabaseName() {
        return BaseApplication.DATABASE_NAME;
    }


    @Override
    protected void getObjectData(List<Object> dataList) {
        dataList.add(image);
        dataList.add(title);
        dataList.add(desc);
        dataList.add(unique);
        dataList.add(url);
        dataList.add(storeModel);
    }

    @Override
    protected void setObjectData(List<Object> dataList) {
        image = (String) dataList.get(0);
        title = (String) dataList.get(1);
        desc = (String) dataList.get(2);
        unique = (String) dataList.get(3);
        url = (String) dataList.get(4);
        storeModel = (StoreModel) dataList.get(5);
    }
}