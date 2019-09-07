package com.zaitunlabs.zlcore.utils.audio

class BackSoundCallBack(index: Int, total: Int, url: String, state: Int) {
    var index: Int = 0
    var total: Int = 0
    var url: String
    var state: Int = 0

    init {
        index = index
        total = total
        url = url
        state = state
    }
}
