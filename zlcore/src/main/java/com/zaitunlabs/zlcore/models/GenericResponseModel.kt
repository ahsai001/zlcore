package com.zaitunlabs.zlcore.models

import com.google.gson.annotations.Expose

/**
 * Created by ahmad s on 8/31/2015.
 */

class GenericResponseModel {


    /**
     *
     * @return
     * The status
     */
    /**
     *
     * @param status
     * The status
     */
    @Expose
    var status: Int = 0


    /**
     *
     * @return
     * The message
     */
    /**
     *
     * @param message
     * The message
     */
    @Expose
    var message: String? = null

    fun withStatus(status: Int): GenericResponseModel {
        this.status = status
        return this
    }

    fun withMessage(message: String): GenericResponseModel {
        this.message = message
        return this
    }


    override fun toString(): String {
        return "GenericResponseModel{" +
                "status=" + status +
                ", message='" + message + '\''.toString() +
                '}'.toString()
    }
}


