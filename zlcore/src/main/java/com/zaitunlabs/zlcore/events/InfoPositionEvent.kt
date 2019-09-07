package com.zaitunlabs.zlcore.events

/**
 * Created by ahsai on 7/25/2017.
 */

class InfoPositionEvent(infoId: Long) {
    var infoId: Long = 0
        internal set

    init {
        this.infoId = infoId
    }
}
