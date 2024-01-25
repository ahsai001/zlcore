package com.zaitunlabs.zlcore.events;

import com.androidnetworking.error.ANError;
import com.zaitunlabs.zlcore.services.DataIntentService;

import org.json.JSONObject;

/**
 * Created by ahsai on 6/27/2018.
 */

public class UploadCallbackEvent {
    private String tag;
    private JSONObject result;
    private ANError error;
    private DataIntentService.Extras extras;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public JSONObject getResult() {
        return result;
    }

    public void setResult(JSONObject result) {
        this.result = result;
    }

    public ANError getError() {
        return error;
    }

    public void setError(ANError error) {
        this.error = error;
    }


    public DataIntentService.Extras getExtras() {
        return extras;
    }

    public void setExtras(DataIntentService.Extras extras) {
        this.extras = extras;
    }

    public UploadCallbackEvent(String tag, JSONObject result, ANError error, DataIntentService.Extras extras) {
        this.tag = tag;
        this.result = result;
        this.error = error;
        this.extras = extras;
    }
}
