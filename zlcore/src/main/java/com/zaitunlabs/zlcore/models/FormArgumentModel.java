package com.zaitunlabs.zlcore.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by ahsai on 5/29/2018.
 */

public class FormArgumentModel {
    @SerializedName("argType")
    @Expose
    private String argType;


    @SerializedName("argValue")
    @Expose
    private Object argValue;


    public String getArgType() {
        return argType;
    }

    public void setArgType(String argType) {
        this.argType = argType;
    }

    public Object getArgValue() {
        return argValue;
    }

    public void setArgValue(Object argValue) {
        this.argValue = argValue;
    }

    @Override
    public String toString() {
        return "FormArgumentModel{" +
                "argType='" + argType + '\'' +
                ", argValue='" + argValue + '\'' +
                '}';
    }
}
