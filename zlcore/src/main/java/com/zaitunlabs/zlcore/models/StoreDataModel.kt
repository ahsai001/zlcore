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

@Table(name = "StoreData", id = BaseColumns._ID)
class StoreDataModel : Model, Serializable {

    @Column(name = "image")
    @SerializedName("image")
    @Expose
    var image: String? = null

    @Column(name = "title")
    @SerializedName("title")
    @Expose
    var title: String? = null


    @Column(name = "desc")
    @SerializedName("desc")
    @Expose
    var desc: String? = null


    @Column(name = "unik")
    @SerializedName("unique")
    @Expose
    var unique: String? = null


    @Column(name = "url")
    @SerializedName("url")
    @Expose
    var url: String? = null


    @Column(name = "storeModel")
    var storeModel: StoreModel? = null

    @Column(name = "timestamp", index = true)
    var timestamp: Date

    /**
     * No args constructor for use in serialization
     *
     */
    constructor() : super() {}

    /**
     *
     * @param title
     * @param unique
     * @param desc
     * @param image
     * @param url
     */
    constructor(image: String, title: String, desc: String, unique: String, url: String) : super() {
        this.image = image
        this.title = title
        this.desc = desc
        this.unique = unique
        this.url = url
    }

    fun withImage(image: String): StoreDataModel {
        this.image = image
        return this
    }

    fun withTitle(title: String): StoreDataModel {
        this.title = title
        return this
    }

    fun withDesc(desc: String): StoreDataModel {
        this.desc = desc
        return this
    }

    fun withUnique(unique: String): StoreDataModel {
        this.unique = unique
        return this
    }

    fun withUrl(url: String): StoreDataModel {
        this.url = url
        return this
    }

    fun saveWithTimeStamp() {
        timestamp = Calendar.getInstance().time
        save()
    }

    override fun toString(): String {
        return "StoreDataModel{" +
                "image='" + image + '\''.toString() +
                ", title='" + title + '\''.toString() +
                ", desc='" + desc + '\''.toString() +
                ", unique='" + unique + '\''.toString() +
                ", url='" + url + '\''.toString() +
                '}'.toString()
    }
}