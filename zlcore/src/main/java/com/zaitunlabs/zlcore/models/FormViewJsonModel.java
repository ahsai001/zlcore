package com.zaitunlabs.zlcore.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by ahsai on 5/29/2018.
 */

public class FormViewJsonModel {
    @SerializedName("pageTitle")
    @Expose
    private String pageTitle;


    @SerializedName("pageType")
    @Expose
    private String pageType;


    @SerializedName("logo")
    @Expose
    private String logo;

    @SerializedName("formTitle")
    @Expose
    private String formTitle;

    @SerializedName("formDesc")
    @Expose
    private String formDesc;

    @SerializedName("formList")
    @Expose
    private ArrayList<FormWidgetModel> formList;

    public String getPageTitle() {
        return pageTitle;
    }

    public void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
    }

    public String getPageType() {
        return pageType;
    }

    public void setPageType(String pageType) {
        this.pageType = pageType;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getFormTitle() {
        return formTitle;
    }

    public void setFormTitle(String formTitle) {
        this.formTitle = formTitle;
    }

    public String getFormDesc() {
        return formDesc;
    }

    public void setFormDesc(String formDesc) {
        this.formDesc = formDesc;
    }

    public ArrayList<FormWidgetModel> getFormList() {
        return formList;
    }

    public void setFormList(ArrayList<FormWidgetModel> formList) {
        this.formList = formList;
    }

    public void addFormWidgetModel(){

    }


    @Override
    public String toString() {
        return "FormViewJsonModel{" +
                "pageTitle='" + pageTitle + '\'' +
                ", pageType='" + pageType + '\'' +
                ", logo='" + logo + '\'' +
                ", formTitle='" + formTitle + '\'' +
                ", formDesc='" + formDesc + '\'' +
                ", formList=" + formList +
                '}';
    }
}
