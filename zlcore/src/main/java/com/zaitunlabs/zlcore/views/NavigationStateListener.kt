package com.zaitunlabs.zlcore.views

import android.view.View

interface NavigationStateListener {
    fun navigationStateIndex(outputView: View, navView: View, index: Int, counts: Int): Boolean
}
