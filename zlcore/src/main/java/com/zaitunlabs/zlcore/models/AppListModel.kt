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

@Table(name = "AppList", id = BaseColumns._ID)
class AppListModel : Model, Serializable {

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
    var paging: AppListPagingModel? = null


    @SerializedName("data")
    @Expose
    var data: List<AppListDataModel>? = null

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
    constructor(status: Int, message: String, paging: AppListPagingModel, data: List<AppListDataModel>) : super() {
        this.status = status
        this.message = message
        this.paging = paging
        this.data = data
    }

    fun withStatus(status: Int): AppListModel {
        this.status = status
        return this
    }

    fun withMessage(message: String): AppListModel {
        this.message = message
        return this
    }

    fun withPaging(paging: AppListPagingModel): AppListModel {
        this.paging = paging
        return this
    }

    fun withData(data: List<AppListDataModel>): AppListModel {
        this.data = data
        return this
    }

    fun saveWithTimeStamp() {
        timestamp = Calendar.getInstance().time
        save()
    }

    override fun toString(): String {
        return "AppListModel{" +
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
        val appListDataModelList = data
        for (appListDataModel in appListDataModelList!!) {
            appListDataModel.appListModel = this
            appListDataModel.saveWithTimeStamp()
        }
    }

    fun addNewDataListToCache(newAppListDataModelList: List<AppListDataModel>) {
        for (appListDataModel in newAppListDataModelList) {
            appListDataModel.appListModel = this
            appListDataModel.saveWithTimeStamp()
        }
    }

    companion object {

        private fun deleteCache() {
            Delete().from(AppListDataModel::class.java).execute<Model>()
            Delete().from(AppListModel::class.java).execute<Model>()
            Delete().from(AppListPagingModel::class.java).execute<Model>()
        }

        //load other table
        val lastCache: AppListModel?
            get() {
                val CACHED_TIME = APIConstant.CACHED_TIME
                val appListModel = Select()
                        .from(AppListModel::class.java).executeSingle<AppListModel>()
                if (appListModel != null) {
                    val timelapseHour = ((Calendar.getInstance().timeInMillis - appListModel.timestamp.time) / 1000).toInt() / 3600
                    if (timelapseHour > CACHED_TIME) {
                        deleteCache()
                        return null
                    }
                    val appListDataModelList = Select()
                            .from(AppListDataModel::class.java).execute<AppListDataModel>()
                    appListModel.data = appListDataModelList

                    val appListPagingModel = Select()
                            .from(AppListPagingModel::class.java).executeSingle<AppListPagingModel>()
                    appListModel.paging = appListPagingModel
                }

                return appListModel
            }
    }
}