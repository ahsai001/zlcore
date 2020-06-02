package com.zaitunlabs.zlcore.tables;

import com.ahsailabs.sqlitewrapper.SQLiteWrapper;
import com.zaitunlabs.zlcore.core.BaseApplication;

import java.util.List;

/**
 * Created by ahsai on 6/15/2017.
 */

public class InformationModel extends SQLiteWrapper.TableClass {
    private String title;
    private String body;
    private boolean read = false;
    private int type;
    private String photoUrl;
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


    @Override
    protected void getObjectData(List<Object> dataList) {
        dataList.add(title);
        dataList.add(body);
        dataList.add(read);
        dataList.add(type);
        dataList.add(photoUrl);
        dataList.add(infoUrl);
    }

    @Override
    protected void setObjectData(List<Object> dataList) {
        title = (String) dataList.get(0);
        body = (String) dataList.get(1);
        read = (boolean) dataList.get(2);
        type = (int) dataList.get(3);
        photoUrl = (String) dataList.get(4);
        infoUrl = (String) dataList.get(5);
    }

    @Override
    protected String getDatabaseName() {
        return BaseApplication.DATABASE_NAME;
    }

    public static List<InformationModel> getAllInfo(){
        return SQLiteWrapper.of(BaseApplication.DATABASE_NAME).findAll(null, InformationModel.class);
    }

    public static List<InformationModel> getAllUnreadInfo(){
        return SQLiteWrapper.of(BaseApplication.DATABASE_NAME).findAllWithCriteria(null, InformationModel.class,
                "read=0", null);
    }

    public static int allInfoCount(){
        return (int)SQLiteWrapper.of(BaseApplication.DATABASE_NAME).count(null, InformationModel.class,null, null);
    }

    public static int unreadInfoCount(){
        return (int)SQLiteWrapper.of(BaseApplication.DATABASE_NAME).count(null, InformationModel.class,"read=0", null);
    }

    public static void markAllAsRead(){
        SQLiteWrapper.of(BaseApplication.DATABASE_NAME).execSQL("update "+InformationModel.class.getSimpleName()+" set read=1", null);
    }

    public static InformationModel getInfo(long id){
        return SQLiteWrapper.of(BaseApplication.DATABASE_NAME).findById(id, null, InformationModel.class);
    }



    public static void deleteAllInfo(){
        SQLiteWrapper.of(BaseApplication.DATABASE_NAME).deleteAll(null, InformationModel.class);
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
                ", timestamp=" + super._created_at +
                ", photoUrl='" + photoUrl + '\'' +
                ", infoUrl='" + infoUrl + '\'' +
                '}';
    }
}
