package com.zaitunlabs.zlcore.events

/**
 * Created by ahsai on 7/14/2017.
 */

class UpdateInfoListEvent(position: Int, read: Boolean) {
    var position: Int = 0
        internal set
    var readStatus: Boolean = false
        internal set

    init {
        this.position = position
        this.readStatus = read
    }
}
