package com.zaitunlabs.zlcore.events

import java.util.HashMap

class GeneralWebviewEvent {

    var eventType: Int = 0
    var dataList: List<Any>? = null

    constructor(eventType: Int) {
        this.eventType = eventType
        this.dataList = null
    }

    constructor(eventType: Int, dataList: List<Any>) {
        this.eventType = eventType
        this.dataList = dataList
    }

    companion object {
        val LOAD_PAGE_STARTED = 0
        val LOAD_PAGE_FINISHED = 1
        val LOAD_PAGE_SUCCESS = 2
        val LOAD_PAGE_ERROR = 3
    }
}
