package com.zaitunlabs.zlcore.utils

import android.graphics.Color
import com.google.android.material.tabs.TabLayout
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView

import com.amulyakhare.textdrawable.TextDrawable
import com.zaitunlabs.zlcore.R

/**
 * Created by ahsai on 6/16/2017.
 */

object TabCounterHelper {
    fun prepareInfoCounter(title: String, tab: TabLayout.Tab) {
        tab.setCustomView(R.layout.info_custom_tab_view)
        if (!TextUtils.isEmpty(title)) {
            (tab.customView!!.findViewById<View>(R.id.info_custom_tab_title_view) as TextView).text = title
        }
        updateInfoCounter(tab, 0)
    }

    fun updateInfoCounter(tab: TabLayout.Tab, count: Int) {
        val countView = tab.customView!!.findViewById<View>(R.id.info_custom_tab_image_view) as ImageView
        if (count == 0) {
            countView.visibility = View.GONE
        } else {
            countView.visibility = View.VISIBLE
            val drawable1 = TextDrawable.builder().buildRoundRect("" + count, Color.RED, 10) // radius in px
            countView.setImageDrawable(drawable1)
        }
    }
}
