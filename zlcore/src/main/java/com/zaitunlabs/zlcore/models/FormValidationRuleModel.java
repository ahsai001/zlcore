package com.zaitunlabs.zlcore.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by ahsai on 5/29/2018.
 */

public class FormValidationRuleModel {
    @SerializedName("ruleName")
    @Expose
    private String ruleName;


    @SerializedName("errorMessage")
    @Expose
    private String errorMessage;

    @SerializedName("ruleArgs")
    @Expose
    private ArrayList<FormArgumentModel> ruleArgs;

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public ArrayList<FormArgumentModel> getRuleArgs() {
        return ruleArgs;
    }

    public void setRuleArgs(ArrayList<FormArgumentModel> ruleArgs) {
        this.ruleArgs = ruleArgs;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        return "FormValidationRuleModel{" +
                "ruleName='" + ruleName + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                ", ruleArgs=" + ruleArgs +
                '}';
    }
}
