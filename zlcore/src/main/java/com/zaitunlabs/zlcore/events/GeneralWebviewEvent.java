package com.zaitunlabs.zlcore.events;

import java.util.HashMap;
import java.util.List;

public class GeneralWebviewEvent {
    public final static int LOAD_PAGE_STARTED = 0;
    public final static int LOAD_PAGE_FINISHED = 1;
    public final static int LOAD_PAGE_SUCCESS = 2;
    public final static int LOAD_PAGE_ERROR = 3;
    public final static int LOAD_RECEIVED_TITLE = 4;

    private int eventType;
    private List<Object> dataList;

    public GeneralWebviewEvent(int eventType) {
        this.eventType = eventType;
        this.dataList = null;
    }

    public GeneralWebviewEvent(int eventType, List<Object> dataList) {
        this.eventType = eventType;
        this.dataList = dataList;
    }

    public int getEventType() {
        return eventType;
    }

    public void setEventType(int eventType) {
        this.eventType = eventType;
    }

    public List<Object> getDataList() {
        return dataList;
    }

    public void setDataList(List<Object> dataList) {
        this.dataList = dataList;
    }
}
