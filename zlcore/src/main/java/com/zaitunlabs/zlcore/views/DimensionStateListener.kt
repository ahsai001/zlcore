package com.zaitunlabs.zlcore.views

import android.graphics.Rect

interface DimensionStateListener {
    fun rectForCurrentDimensionState(currentRectState: Rect): Boolean
    fun indexForCurrentDimensionState(currentIndexState: Int): Boolean
}
