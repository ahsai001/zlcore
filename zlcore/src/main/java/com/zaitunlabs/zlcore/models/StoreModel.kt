package com.zaitunlabs.zlcore.models

import android.provider.BaseColumns

import com.activeandroid.Model
import com.activeandroid.annotation.Column
import com.activeandroid.annotation.Table
import com.activeandroid.query.Delete
import com.activeandroid.query.Select
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.zaitunlabs.zlcore.api.APIConstant

import java.io.Serializable
import java.util.Calendar
import java.util.Date

/**
 * Created by ahsai on 3/18/2018.
 */

@Table(name = "Store", id = BaseColumns._ID)
class StoreModel : Model, Serializable {

    @Column(name = "status", index = true)
    @SerializedName("status")
    @Expose
    var status: Int = 0

    @Column(name = "message")
    @SerializedName("message")
    @Expose
    var message: String? = null

    @Column(name = "paging")
    @SerializedName("paging")
    @Expose
    var paging: StorePagingModel? = null


    @SerializedName("data")
    @Expose
    var data: List<StoreDataModel>? = null

    @Column(name = "timestamp", index = true)
    var timestamp: Date

    /**
     * No args constructor for use in serialization
     *
     */
    constructor() : super() {}

    /**
     *
     * @param message
     * @param status
     * @param data
     * @param paging
     */
    constructor(status: Int, message: String, paging: StorePagingModel, data: List<StoreDataModel>) : super() {
        this.status = status
        this.message = message
        this.paging = paging
        this.data = data
    }

    fun withStatus(status: Int): StoreModel {
        this.status = status
        return this
    }

    fun withMessage(message: String): StoreModel {
        this.message = message
        return this
    }

    fun withPaging(paging: StorePagingModel): StoreModel {
        this.paging = paging
        return this
    }

    fun withData(data: List<StoreDataModel>): StoreModel {
        this.data = data
        return this
    }

    fun saveWithTimeStamp() {
        timestamp = Calendar.getInstance().time
        save()
    }

    override fun toString(): String {
        return "StoreModel{" +
                "status=" + status +
                ", message='" + message + '\''.toString() +
                ", paging=" + paging +
                ", data=" + data +
                '}'.toString()
    }


    fun cache(deletePrev: Boolean) {
        if (deletePrev) {
            deleteCache()
        }
        paging!!.saveWithTimeStamp()
        saveWithTimeStamp()
        val storeDataModelList = data
        for (storeDataModel in storeDataModelList!!) {
            storeDataModel.storeModel = this
            storeDataModel.saveWithTimeStamp()
        }
    }

    fun addNewDataListToCache(newStoreDataModelList: List<StoreDataModel>) {
        for (storeDataModel in newStoreDataModelList) {
            storeDataModel.storeModel = this
            storeDataModel.saveWithTimeStamp()
        }
    }

    companion object {

        private fun deleteCache() {
            Delete().from(StoreDataModel::class.java).execute<Model>()
            Delete().from(StoreModel::class.java).execute<Model>()
            Delete().from(StorePagingModel::class.java).execute<Model>()
        }

        //load other table
        val lastCache: StoreModel?
            get() {
                val CACHED_TIME = APIConstant.CACHED_TIME
                val storeModel = Select()
                        .from(StoreModel::class.java).executeSingle<StoreModel>()
                if (storeModel != null) {
                    val timelapseHour = ((Calendar.getInstance().timeInMillis - storeModel.timestamp.time) / 1000).toInt() / 3600
                    if (timelapseHour > CACHED_TIME) {
                        deleteCache()
                        return null
                    }
                    val storeDataModelList = Select()
                            .from(StoreDataModel::class.java).execute<StoreDataModel>()
                    storeModel.data = storeDataModelList

                    val storePagingModel = Select()
                            .from(StorePagingModel::class.java).executeSingle<StorePagingModel>()
                    storeModel.paging = storePagingModel
                }

                return storeModel
            }
    }
}