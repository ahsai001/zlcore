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

@Table(name = "StoreData", id = BaseColumns._ID)
public class StoreDataModel extends Model implements Serializable {

    @Column(name = "image")
    @SerializedName("image")
    @Expose
    private String image;

    @Column(name = "title")
    @SerializedName("title")
    @Expose
    private String title;


    @Column(name = "desc")
    @SerializedName("desc")
    @Expose
    private String desc;



    @Column(name = "unik")
    @SerializedName("unique")
    @Expose
    private String unique;


    @Column(name = "url")
    @SerializedName("url")
    @Expose
    private String url;


    @Column(name = "storeModel")
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

    @Column(name = "timestamp", index = true)
    public Date timestamp;

    public void saveWithTimeStamp(){
        timestamp = Calendar.getInstance().getTime();
        save();
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
}