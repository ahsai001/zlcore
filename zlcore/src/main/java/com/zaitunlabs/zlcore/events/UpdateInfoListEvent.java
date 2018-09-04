package com.zaitunlabs.zlcore.events;

/**
 * Created by ahsai on 7/14/2017.
 */

public class UpdateInfoListEvent {
    int position;
    boolean read;
    public UpdateInfoListEvent(int position, boolean read) {
        this.position = position;
        this.read = read;
    }

    public int getPosition() {
        return position;
    }

    public boolean getReadStatus(){
        return read;
    }
}
