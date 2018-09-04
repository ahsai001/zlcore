package com.zaitunlabs.zlcore.events;

/**
 * Created by ahsai on 7/25/2017.
 */

public class InfoPositionEvent {
    long infoId;
    public InfoPositionEvent(long infoId) {
        this.infoId = infoId;
    }

    public long getInfoId() {
        return infoId;
    }
}
