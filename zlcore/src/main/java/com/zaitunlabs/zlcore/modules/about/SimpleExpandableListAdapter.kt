package com.zaitunlabs.zlcore.modules.about

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.util.SparseArray
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.idunnololz.widgets.AnimatedExpandableListView
import com.zaitunlabs.zlcore.utils.CommonUtils
import com.zaitunlabs.zlcore.utils.DebugUtils
import com.zaitunlabs.zlcore.utils.LinkUtils

class SimpleExpandableListAdapter(var activity: Activity, private val data: SparseArray<SimpleExpandableDataModel>, private val isZL: Boolean) : AnimatedExpandableListView.AnimatedExpandableListAdapter() {
    private var groupHeight = 0

    init {

        var fontHeight = 0
        var groupIndicatoHeight = 0
        fontHeight = CommonUtils.getFontHeight("T").toInt() + 10
        val scale = CommonUtils.getDisplayMetricsScaledDensity(activity)
        groupIndicatoHeight = (CommonUtils.getImageDimension(activity.baseContext, android.R.attr.groupIndicator).y + 30 * scale).toInt()
        groupHeight = Math.max(fontHeight, groupIndicatoHeight)
        DebugUtils.logW("HEIGHT", "groupIndicatoHeight : $groupIndicatoHeight")
        DebugUtils.logW("HEIGHT", "fontHeight : $fontHeight")
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        return data.get(groupPosition).children[childPosition]
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        // TODO Auto-generated method stub
        return 0
    }

    override fun getRealChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val children = getChild(groupPosition, childPosition) as String
        var textView: TextView? = null
        if (convertView == null) {
            convertView = TextView(activity)
        }
        textView = convertView as TextView?
        textView!!.text = children

        LinkUtils.autoLink(textView, object : LinkUtils.OnClickListener {

            override fun onLinkClicked(link: String?) {
                DebugUtils.logI("", "link : " + link!!)
                CommonUtils.openBrowser(activity, link)
            }

            override fun onClicked() {
                DebugUtils.logI("", " link clicked")
            }
        })

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            textView.textAlignment = View.TEXT_ALIGNMENT_CENTER
        }
        textView.gravity = Gravity.CENTER
        textView.setSingleLine(false)
        textView.setPadding(0, 20, 0, 20)
        if (isZL) {
            textView.setTextColor(Color.WHITE)
            textView.setBackgroundColor(Color.argb(150, 0, 0, 0))
        } else {
            textView.setTextColor(Color.BLACK)
            textView.setBackgroundColor(Color.argb(50, 0, 0, 0))
        }
        return convertView
    }

    override fun getRealChildrenCount(groupPosition: Int): Int {
        return data.get(groupPosition).children.size
    }


    override fun getGroup(groupPosition: Int): Any {
        return data.get(groupPosition)
    }

    override fun getGroupCount(): Int {
        return data.size()
    }

    override fun getGroupId(groupPosition: Int): Long {
        return 0
    }

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean,
                              convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        var text: TextView? = null
        if (convertView == null) {
            convertView = TextView(activity)
        }
        val group = getGroup(groupPosition) as SimpleExpandableDataModel
        text = convertView as TextView?
        text!!.text = group.string

        if (isZL) {
            text.setTextColor(Color.WHITE)
        } else {
            text.setTextColor(Color.BLACK)
        }

        text.height = groupHeight

        val padding = CommonUtils.getIntAttrValue(activity, android.R.attr.paddingTop)
        text.setPadding(CommonUtils.getIntAttrValue(activity, android.R.attr.expandableListPreferredItemPaddingLeft), padding, padding, padding)

        text.gravity = Gravity.CENTER_VERTICAL

        if (isZL) {
            if (CommonUtils.isOdd(groupPosition)) {
                text.setBackgroundColor(Color.argb(200, 0, 0, 0))
            } else {
                text.setBackgroundColor(Color.argb(50, 0, 0, 0))
            }
        } else {
            text.setBackgroundColor(Color.WHITE)
        }

        return convertView
    }

    override fun hasStableIds(): Boolean {
        // TODO Auto-generated method stub
        return true
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        // TODO Auto-generated method stub
        return false
    }

}
