package com.zaitunlabs.zlcore.tables;

import com.ahsailabs.sqlitewrapper.SQLiteWrapper;
import com.zaitunlabs.zlcore.core.BaseApplication;

import java.util.List;

/**
 * Created by ahsai on 3/18/2018.
 */

public class BookmarkModel extends SQLiteWrapper.TableClass {
    private String title;
    private String desc;
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


    @Override
    protected String getDatabaseName() {
        return BaseApplication.DATABASE_NAME;
    }

    @Override
    protected void getObjectData(List<Object> dataList) {
        dataList.add(title);
        dataList.add(desc);
        dataList.add(link);
    }

    @Override
    protected void setObjectData(List<Object> dataList) {
        title = (String) dataList.get(0);
        desc = (String) dataList.get(1);
        link = (String) dataList.get(2);
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
        return SQLiteWrapper.of(BaseApplication.DATABASE_NAME).count(null, BookmarkModel.class,"link = '"+link+"'", null) > 0;
    }

    public static BookmarkModel findBookmark(String link){
        return SQLiteWrapper.of(BaseApplication.DATABASE_NAME).findFirstWithCriteria(null,BookmarkModel.class,
                "link = '"+link+"'", null);
    }

    public static boolean bookmark(String title, String desc, String link){
        BookmarkModel existingModel = findBookmark(link);
        if(existingModel == null){
            BookmarkModel newBoomark = new BookmarkModel(title,desc,link);
            newBoomark.save();
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
        return SQLiteWrapper.of(BaseApplication.DATABASE_NAME).findAll(null, BookmarkModel.class);
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