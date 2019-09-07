package com.zaitunlabs.zlcore.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by ahsai on 5/29/2018.
 */

class FormArgumentModel {
    @SerializedName("argType")
    @Expose
    var argType: String? = null


    @SerializedName("argValue")
    @Expose
    var argValue: Any? = null

    override fun toString(): String {
        return "FormArgumentModel{" +
                "argType='" + argType + '\''.toString() +
                ", argValue='" + argValue + '\''.toString() +
                '}'.toString()
    }
}
