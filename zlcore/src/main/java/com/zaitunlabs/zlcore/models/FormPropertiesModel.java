package com.zaitunlabs.zlcore.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by ahsai on 5/29/2018.
 */

public class FormPropertiesModel {
    @SerializedName("propKey")
    @Expose
    private String propKey;

    @SerializedName("propArgs")
    @Expose
    private ArrayList<FormArgumentModel> propArgs;


    public String getPropKey() {
        return propKey;
    }

    public void setPropKey(String propKey) {
        this.propKey = propKey;
    }

    public ArrayList<FormArgumentModel> getPropArgs() {
        return propArgs;
    }

    public void setPropArg(ArrayList<FormArgumentModel> propArgs) {
        this.propArgs = propArgs;
    }

    @Override
    public String toString() {
        return "FormPropertiesModel{" +
                "propKey='" + propKey + '\'' +
                ", propArgs=" + propArgs +
                '}';
    }
}
