package com.zaitunlabs.zlcore.models

import android.provider.BaseColumns

import com.activeandroid.Model
import com.activeandroid.annotation.Column
import com.activeandroid.annotation.Table
import com.activeandroid.query.Delete
import com.activeandroid.query.Select
import com.activeandroid.query.Update

import java.io.Serializable
import java.util.Calendar
import java.util.Date

/**
 * Created by ahsai on 6/15/2017.
 */

@Table(name = "Information", id = BaseColumns._ID)
class InformationModel : Model, Serializable {

    @Column(name = "title", index = true)
    var title: String? = null

    @Column(name = "body", index = true)
    var body: String? = null

    @Column(name = "read", index = true)
    var isRead = false


    @Column(name = "type", index = true)
    var type: Int = 0

    @Column(name = "timestamp", index = true)
    var timestamp: Date


    @Column(name = "photo_url")
    var photoUrl: String? = null

    @Column(name = "info_url")
    var infoUrl: String? = null

    fun saveWithTimeStamp() {
        timestamp = Calendar.getInstance().time
        save()
    }

    constructor() : super() {}

    constructor(title: String, body: String, photoUrl: String, infoUrl: String, type: Int) : super() {
        this.title = title
        this.body = body
        this.photoUrl = photoUrl
        this.infoUrl = infoUrl
        this.type = type
    }

    override fun toString(): String {
        return "InformationModel{" +
                "title='" + title + '\''.toString() +
                ", body='" + body + '\''.toString() +
                ", read=" + isRead +
                ", type=" + type +
                ", timestamp=" + timestamp +
                ", photoUrl='" + photoUrl + '\''.toString() +
                ", infoUrl='" + infoUrl + '\''.toString() +
                '}'.toString()
    }

    companion object {


        val allInfo: List<InformationModel>
            get() = Select().from(InformationModel::class.java).orderBy("timestamp desc").execute()

        val allUnreadInfo: List<InformationModel>
            get() = Select().from(InformationModel::class.java).where("read=0").orderBy("timestamp desc").execute()

        fun allInfoCount(): Int {
            return Select().from(InformationModel::class.java).count()
        }

        fun unreadInfoCount(): Int {
            return Select().from(InformationModel::class.java).where("read=0").count()
        }

        fun markAllAsRead() {
            Update(InformationModel::class.java).set("read=1").execute()
        }

        fun getInfo(id: Long): InformationModel {
            return Select().from(InformationModel::class.java).where(BaseColumns._ID + "=" + id).executeSingle()
        }

        fun deleteAllInfo() {
            Delete().from(InformationModel::class.java).execute<Model>()
        }
    }
}
