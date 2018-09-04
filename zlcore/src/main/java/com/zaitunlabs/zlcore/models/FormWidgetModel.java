package com.zaitunlabs.zlcore.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by ahsai on 5/29/2018.
 */

public class FormWidgetModel {
    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("fieldName")
    @Expose
    private String fieldName;

    @SerializedName("label")
    @Expose
    private String label;

    @SerializedName("subLabel")
    @Expose
    private String subLabel;

    @SerializedName("widgetName")
    @Expose
    private String widgetName;


    @SerializedName("data")
    @Expose
    private List<FormArgumentModel> data;


    @SerializedName("validation")
    @Expose
    private List<FormValidationRuleModel> validation;

    @SerializedName("properties")
    @Expose
    private List<FormPropertiesModel> properties;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getSubLabel() {
        return subLabel;
    }

    public void setSubLabel(String subLabel) {
        this.subLabel = subLabel;
    }

    public String getWidgetName() {
        return widgetName;
    }

    public void setWidgetName(String widgetName) {
        this.widgetName = widgetName;
    }

    public List<FormPropertiesModel> getProperties() {
        return properties;
    }

    public void setProperties(List<FormPropertiesModel> properties) {
        this.properties = properties;
    }

    public List<FormValidationRuleModel> getValidation() {
        return validation;
    }

    public void setValidation(List<FormValidationRuleModel> validation) {
        this.validation = validation;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public List<FormArgumentModel> getData() {
        return data;
    }

    public void setData(List<FormArgumentModel> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "FormWidgetModel{" +
                "id='" + id + '\'' +
                ", fieldName='" + fieldName + '\'' +
                ", label='" + label + '\'' +
                ", subLabel='" + subLabel + '\'' +
                ", widgetName='" + widgetName + '\'' +
                ", data=" + data +
                ", validation=" + validation +
                ", properties=" + properties +
                '}';
    }
}
