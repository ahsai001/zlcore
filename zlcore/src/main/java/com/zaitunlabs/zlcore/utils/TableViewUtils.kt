package com.zaitunlabs.zlcore.utils

import android.content.Context
import androidx.core.widget.NestedScrollView
import androidx.appcompat.view.ContextThemeWrapper
import android.view.View
import android.widget.FrameLayout
import android.widget.HorizontalScrollView
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView

import com.zaitunlabs.zlcore.customs.DataList

import java.util.ArrayList

/**
 * Created by ahsai on 7/16/2018.
 */

class TableViewUtils(private val context: Context) {
    private var topRootView: NestedScrollView? = null
    private var rootView: HorizontalScrollView? = null
    private var tableLayout: TableLayout? = null
    private val headerColumName: MutableList<String>
    private val data: MutableList<List<String>>
    private var headerResStyle: Int = 0
    private var bodyResStyle: Int = 0
    private var borderColor: Int = 0
    private var fillColor: Int = 0
    private var isHeaderFill = false
    private var headerFillColor: Int = 0
    private var headerTextColor: Int = 0
    private var isBodyFill = false
    private var bodyFillColor: Int = 0
    private var bodyTextColor: Int = 0
    private var isTailFill = false
    private var tailFillColor: Int = 0
    private var tableRadiusInDp: Int = 0

    val tableView: View?
        get() = topRootView


    init {
        createNewParentView()
        headerColumName = ArrayList()
        data = ArrayList()
    }


    private fun createNewParentView() {
        topRootView = NestedScrollView(context)
        rootView = HorizontalScrollView(context)
        topRootView!!.addView(rootView, FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT))
        tableLayout = TableLayout(context)
        rootView!!.addView(tableLayout, FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT))

    }

    fun init(headRow: DataList<String>, borderColor: Int, fillColor: Int, tableRadiusInDp: Int): TableViewUtils {
        this.headerColumName.clear()
        this.headerColumName.addAll(headRow.arrayList)
        this.borderColor = borderColor
        this.fillColor = fillColor
        this.tableRadiusInDp = tableRadiusInDp
        return this
    }

    fun setBody(data: List<List<String>>): TableViewUtils {
        this.data.clear()
        this.data.addAll(data)
        return this
    }

    fun addBody(data: List<List<String>>): TableViewUtils {
        this.data.addAll(data)
        return this
    }

    fun setHeaderResStyle(headerResStyle: Int): TableViewUtils {
        this.headerResStyle = headerResStyle
        return this
    }

    fun setBodyResStyle(bodyResStyle: Int): TableViewUtils {
        this.bodyResStyle = bodyResStyle
        return this
    }

    fun setHeaderColor(headerFillColor: Int, headerTextColor: Int): TableViewUtils {
        this.headerFillColor = headerFillColor
        this.headerTextColor = headerTextColor
        this.isHeaderFill = true
        return this
    }

    fun setBodyColor(bodyFillColor: Int, bodyTextColor: Int): TableViewUtils {
        this.bodyFillColor = bodyFillColor
        this.bodyTextColor = bodyTextColor
        this.isBodyFill = true
        return this
    }

    fun setTailColor(tailFillColor: Int): TableViewUtils {
        this.tailFillColor = tailFillColor
        this.isTailFill = true
        return this
    }

    fun render(): TableViewUtils {
        render(true)
        return this
    }

    fun render(createNewParentView: Boolean): TableViewUtils {
        val headerFillColor = if (this.isHeaderFill) this.headerFillColor else this.fillColor
        val bodyFillColor = if (this.isBodyFill) this.bodyFillColor else this.fillColor
        val tailFillColor = if (this.isTailFill) this.tailFillColor else this.fillColor

        if (createNewParentView) {
            createNewParentView()
        } else {
            tableLayout!!.removeAllViews()
        }

        //header
        val headerRow = TableRow(context)
        for (i in headerColumName.indices) {
            var textView: TextView? = null
            if (headerResStyle > -1) {
                val wrappedContext = ContextThemeWrapper(context, headerResStyle)
                textView = TextView(wrappedContext, null, 0)
            } else {
                textView = TextView(context)
            }
            textView.text = headerColumName[i]

            if (i == 0) {
                textView.background = ViewUtils.getLeftHeadTableBackground(context, borderColor, headerFillColor, tableRadiusInDp)
            } else if (i == headerColumName.size - 1) {
                textView.background = ViewUtils.getRightHeadTableBackground(context, borderColor, headerFillColor, tableRadiusInDp)
            } else {
                textView.background = ViewUtils.getCenterHeadTableBackground(context, borderColor, headerFillColor)
            }

            if (isHeaderFill) {
                textView.setTextColor(headerTextColor)
            }

            headerRow.addView(textView, TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT))
        }
        tableLayout!!.addView(headerRow)

        //body
        for (rowData in data) {
            val bodyRow = TableRow(context)
            for (i in rowData.indices) {
                var textView: TextView? = null
                if (bodyResStyle > -1) {
                    val wrappedContext = ContextThemeWrapper(context, bodyResStyle)
                    textView = TextView(wrappedContext, null, 0)
                } else {
                    textView = TextView(context)
                }
                textView.text = rowData[i]
                if (i == 0) {
                    textView.background = ViewUtils.getLeftBodyTableBackground(context, borderColor, bodyFillColor)
                } else if (i == rowData.size - 1) {
                    textView.background = ViewUtils.getRightBodyTableBackground(context, borderColor, bodyFillColor)
                } else {
                    textView.background = ViewUtils.getCenterBodyTableBackground(context, borderColor, bodyFillColor)
                }

                if (isBodyFill) {
                    textView.setTextColor(bodyTextColor)
                }

                bodyRow.addView(textView, TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT))
            }
            tableLayout!!.addView(bodyRow)
        }

        //footer
        val footerRow = TableRow(context)
        for (i in headerColumName.indices) {
            var textView: TextView? = null
            if (headerResStyle > -1) {
                val wrappedContext = ContextThemeWrapper(context, headerResStyle)
                textView = TextView(wrappedContext, null, 0)
            } else {
                textView = TextView(context)
            }
            if (i == 0) {
                textView.background = ViewUtils.getLeftTailTableBackground(context, borderColor, tailFillColor, tableRadiusInDp)
            } else if (i == headerColumName.size - 1) {
                textView.background = ViewUtils.getRightTailTableBackground(context, borderColor, tailFillColor, tableRadiusInDp)
            } else {
                textView.background = ViewUtils.getCenterTailTableBackground(context, borderColor, tailFillColor)
            }
            footerRow.addView(textView, TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT))
        }
        tableLayout!!.addView(footerRow)
        return this
    }
}
