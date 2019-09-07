package com.zaitunlabs.zlcore.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by ahsai on 5/29/2018.
 */

class FormWidgetModel {
    @SerializedName("id")
    @Expose
    var id: String? = null

    @SerializedName("fieldName")
    @Expose
    var fieldName: String? = null

    @SerializedName("label")
    @Expose
    var label: String? = null

    @SerializedName("subLabel")
    @Expose
    var subLabel: String? = null

    @SerializedName("widgetName")
    @Expose
    var widgetName: String? = null


    @SerializedName("data")
    @Expose
    var data: List<FormArgumentModel>? = null


    @SerializedName("validation")
    @Expose
    var validation: List<FormValidationRuleModel>? = null

    @SerializedName("properties")
    @Expose
    var properties: List<FormPropertiesModel>? = null

    override fun toString(): String {
        return "FormWidgetModel{" +
                "id='" + id + '\''.toString() +
                ", fieldName='" + fieldName + '\''.toString() +
                ", label='" + label + '\''.toString() +
                ", subLabel='" + subLabel + '\''.toString() +
                ", widgetName='" + widgetName + '\''.toString() +
                ", data=" + data +
                ", validation=" + validation +
                ", properties=" + properties +
                '}'.toString()
    }
}
