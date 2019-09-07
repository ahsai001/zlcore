package com.zaitunlabs.zlcore.models

import android.provider.BaseColumns

import com.activeandroid.Model
import com.activeandroid.annotation.Column
import com.activeandroid.annotation.Table
import com.activeandroid.query.Select

import java.io.Serializable
import java.util.Calendar
import java.util.Date

/**
 * Created by ahsai on 3/18/2018.
 */

@Table(name = "Bookmarks", id = BaseColumns._ID)
class BookmarkModel : Model, Serializable {

    @Column(name = "title")
    var title: String? = null

    @Column(name = "desc")
    var desc: String? = null

    @Column(name = "link")
    var link: String? = null

    @Column(name = "timestamp", index = true)
    var timestamp: Date


    constructor() : super() {}
    constructor(title: String, desc: String, link: String) : super() {
        this.title = title
        this.desc = desc
        this.link = link
    }

    fun saveWithTimeStamp() {
        timestamp = Calendar.getInstance().time
        save()
    }


    fun hasSaved(): Boolean {
        return Select().from(BookmarkModel::class.java).where("link = '$link'").count() > 0
    }

    override fun toString(): String {
        return "BookmarkModel{" +
                "title='" + title + '\''.toString() +
                ", desc='" + desc + '\''.toString() +
                ", link='" + link + '\''.toString() +
                '}'.toString()
    }

    companion object {

        fun findBookmark(link: String): BookmarkModel? {
            return Select().from(BookmarkModel::class.java).where("link = '$link'").executeSingle()
        }

        fun bookmark(title: String, desc: String, link: String): Boolean {
            val existingModel = findBookmark(link)
            if (existingModel == null) {
                val newBoomark = BookmarkModel(title, desc, link)
                newBoomark.saveWithTimeStamp()
                return true
            }
            return false
        }

        fun unBookmark(title: String, desc: String, link: String): Boolean {
            val existingModel = findBookmark(link)
            if (existingModel != null) {
                existingModel.delete()
                return true
            }
            return false
        }


        val allBookmarkList: List<BookmarkModel>
            get() = Select().from(BookmarkModel::class.java).execute()
    }
}