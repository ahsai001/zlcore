package com.zaitunlabs.zlcore.models;

import com.google.gson.annotations.Expose;

/**
 * Created by ahmad s on 8/31/2015.
 */

public class GenericResponseModel {


    @Expose
    private int status;


    @Expose
    private String message;

    /**
     *
     * @return
     * The status
     */
    public int getStatus() {
        return status;
    }

    /**
     *
     * @param status
     * The status
     */
    public void setStatus(int status) {
        this.status = status;
    }

    public GenericResponseModel withStatus(int status) {
        this.status = status;
        return this;
    }

    /**
     *
     * @return
     * The message
     */
    public String getMessage() {
        return message;
    }

    /**
     *
     * @param message
     * The message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    public GenericResponseModel withMessage(String message) {
        this.message = message;
        return this;
    }


    @Override
    public String toString() {
        return "GenericResponseModel{" +
                "status=" + status +
                ", message='" + message + '\'' +
                '}';
    }
}


