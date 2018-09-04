package com.zaitunlabs.zlcore.models;

import android.provider.BaseColumns;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.activeandroid.query.Update;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by ahsai on 6/15/2017.
 */

@Table(name = "Information", id = BaseColumns._ID)
public class InformationModel extends Model implements Serializable{

    @Column(name = "title", index = true)
    private String title;

    @Column(name = "body", index = true)
    private String body;

    @Column(name = "read", index = true)
    private boolean read = false;


    @Column(name = "type", index = true)
    private int type;

    @Column(name = "timestamp", index = true)
    public Date timestamp;

    public void saveWithTimeStamp(){
        timestamp = Calendar.getInstance().getTime();
        save();
    }


    @Column(name = "photo_url")
    private String photoUrl;

    @Column(name = "info_url")
    private String infoUrl;

    public InformationModel(){
        super();
    }

    public InformationModel(String title, String body, String photoUrl, String infoUrl, int type) {
        super();
        this.title = title;
        this.body = body;
        this.photoUrl = photoUrl;
        this.infoUrl = infoUrl;
        this.type = type;
    }


    public static List<InformationModel> getAllInfo(){
        return new Select().from(InformationModel.class).orderBy("timestamp desc").execute();
    }

    public static List<InformationModel> getAllUnreadInfo(){
        return new Select().from(InformationModel.class).where("read=0").orderBy("timestamp desc").execute();
    }

    public static int allInfoCount(){
        return new Select().from(InformationModel.class).count();
    }

    public static int unreadInfoCount(){
        return new Select().from(InformationModel.class).where("read=0").count();
    }

    public static void markAllAsRead(){
        new Update(InformationModel.class).set("read=1").execute();
    }

    public static InformationModel getInfo(long id){
        return new Select().from(InformationModel.class).where(BaseColumns._ID+"="+id).executeSingle();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getInfoUrl() {
        return infoUrl;
    }

    public void setInfoUrl(String infoUrl) {
        this.infoUrl = infoUrl;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "InformationModel{" +
                "title='" + title + '\'' +
                ", body='" + body + '\'' +
                ", read=" + read +
                ", type=" + type +
                ", timestamp=" + timestamp +
                ", photoUrl='" + photoUrl + '\'' +
                ", infoUrl='" + infoUrl + '\'' +
                '}';
    }

    public static void deleteAllInfo(){
        new Delete().from(InformationModel.class).execute();
    }
}
