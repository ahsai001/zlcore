package com.zaitunlabs.zlcore.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import java.util.ArrayList

/**
 * Created by ahsai on 5/29/2018.
 */

class FormValidationRuleModel {
    @SerializedName("ruleName")
    @Expose
    var ruleName: String? = null


    @SerializedName("errorMessage")
    @Expose
    var errorMessage: String? = null

    @SerializedName("ruleArgs")
    @Expose
    var ruleArgs: ArrayList<FormArgumentModel>? = null

    override fun toString(): String {
        return "FormValidationRuleModel{" +
                "ruleName='" + ruleName + '\''.toString() +
                ", errorMessage='" + errorMessage + '\''.toString() +
                ", ruleArgs=" + ruleArgs +
                '}'.toString()
    }
}
