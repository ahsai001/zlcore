package com.zaitunlabs.zlcore.models;

import android.provider.BaseColumns;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by ahsai on 3/18/2018.
 */

@Table(name = "Bookmarks", id = BaseColumns._ID)
public class BookmarkModel extends Model implements Serializable {

    @Column(name = "title")
    private String title;

    @Column(name = "desc")
    private String desc;

    @Column(name = "link")
    private String link;


    public BookmarkModel(){
        super();
    }
    public BookmarkModel(String title, String desc, String link) {
        super();
        this.title = title;
        this.desc = desc;
        this.link = link;
    }

    @Column(name = "timestamp", index = true)
    public Date timestamp;

    public void saveWithTimeStamp(){
        timestamp = Calendar.getInstance().getTime();
        save();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }


    public boolean hasSaved(){
        return new Select().from(BookmarkModel.class).where("link = '"+link+"'").count() > 0;
    }

    public static BookmarkModel findBookmark(String link){
        return new Select().from(BookmarkModel.class).where("link = '"+link+"'").executeSingle();
    }

    public static boolean bookmark(String title, String desc, String link){
        BookmarkModel existingModel = findBookmark(link);
        if(existingModel == null){
            BookmarkModel newBoomark = new BookmarkModel(title,desc,link);
            newBoomark.saveWithTimeStamp();
            return true;
        }
        return false;
    }

    public static boolean unBookmark(String title, String desc, String link){
        BookmarkModel existingModel = findBookmark(link);
        if(existingModel != null){
            existingModel.delete();
            return true;
        }
        return false;
    }


    public static List<BookmarkModel> getAllBookmarkList(){
        return new Select().from(BookmarkModel.class).execute();
    }

    @Override
    public String toString() {
        return "BookmarkModel{" +
                "title='" + title + '\'' +
                ", desc='" + desc + '\'' +
                ", link='" + link + '\'' +
                '}';
    }
}