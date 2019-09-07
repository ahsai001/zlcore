package com.zaitunlabs.zlcore.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import java.util.ArrayList

/**
 * Created by ahsai on 5/29/2018.
 */

class FormViewJsonModel {
    @SerializedName("pageTitle")
    @Expose
    var pageTitle: String? = null


    @SerializedName("pageType")
    @Expose
    var pageType: String? = null


    @SerializedName("logo")
    @Expose
    var logo: String? = null

    @SerializedName("formTitle")
    @Expose
    var formTitle: String? = null

    @SerializedName("formDesc")
    @Expose
    var formDesc: String? = null

    @SerializedName("formList")
    @Expose
    var formList: ArrayList<FormWidgetModel>? = null

    fun addFormWidgetModel() {

    }


    override fun toString(): String {
        return "FormViewJsonModel{" +
                "pageTitle='" + pageTitle + '\''.toString() +
                ", pageType='" + pageType + '\''.toString() +
                ", logo='" + logo + '\''.toString() +
                ", formTitle='" + formTitle + '\''.toString() +
                ", formDesc='" + formDesc + '\''.toString() +
                ", formList=" + formList +
                '}'.toString()
    }
}
