package com.zaitunlabs.zlcore.events;

/**
 * Created by ahsai on 5/10/2018.
 */

public class ShowBookmarkInfoEvent {
    private String title;
    private String link;

    public ShowBookmarkInfoEvent(String title, String link) {
        this.title = title;
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
