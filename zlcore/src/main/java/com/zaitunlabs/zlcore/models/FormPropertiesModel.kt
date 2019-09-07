package com.zaitunlabs.zlcore.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import java.util.ArrayList

/**
 * Created by ahsai on 5/29/2018.
 */

class FormPropertiesModel {
    @SerializedName("propKey")
    @Expose
    var propKey: String? = null

    @SerializedName("propArgs")
    @Expose
    var propArgs: ArrayList<FormArgumentModel>? = null
        private set

    fun setPropArg(propArgs: ArrayList<FormArgumentModel>) {
        this.propArgs = propArgs
    }

    override fun toString(): String {
        return "FormPropertiesModel{" +
                "propKey='" + propKey + '\''.toString() +
                ", propArgs=" + propArgs +
                '}'.toString()
    }
}
