package com.zaitunlabs.zlcore.modules.version_history

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build.VERSION
import android.os.Bundle
import android.util.SparseArray
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.LayoutAnimationController
import android.view.animation.TranslateAnimation
import android.widget.ExpandableListView
import android.widget.ExpandableListView.OnGroupExpandListener
import android.widget.LinearLayout

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.idunnololz.widgets.AnimatedExpandableListView
import com.zaitunlabs.zlcore.R
import com.zaitunlabs.zlcore.modules.about.AboutUs
import com.zaitunlabs.zlcore.modules.about.SimpleExpandableDataModel
import com.zaitunlabs.zlcore.modules.about.SimpleExpandableListAdapter
import com.zaitunlabs.zlcore.modules.about.SimpleItemDescriptionModel
import com.zaitunlabs.zlcore.modules.shaum_sholat.CountDownSholatReminderUtils
import com.zaitunlabs.zlcore.core.CanvasActivity
import com.zaitunlabs.zlcore.utils.FileUtils
import com.zaitunlabs.zlcore.views.ASTextView
import com.zaitunlabs.zlcore.views.CanvasLayout
import com.zaitunlabs.zlcore.views.CanvasSection
import com.zaitunlabs.zlcore.views.GoToTopView
import com.zaitunlabs.zlcore.views.GoToTopView.IGoToTopAction


import java.io.IOException

class VersionChangeHistoryCanvas : CanvasActivity() {
    internal var countDownTimerHeaderText: ASTextView
    internal var countDownSholatReminderUtils: CountDownSholatReminderUtils? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // create Canvas Page
        val canvas = CanvasLayout(this)

        // create page background
        canvas.setBackgroundResource(R.drawable.bg_riwayat)

        // create header
        val headerText = ASTextView(this)
        headerText.typeface = Typeface.createFromAsset(this.assets,
                "fonts/about/ArabDances.ttf")
        headerText.text = getString(R.string.app_name)
        headerText.setBackgroundResource(R.drawable.header_about)
        headerText.textSize = 30f
        headerText.setTextColor(Color.WHITE)
        headerText.gravity = Gravity.CENTER
        canvas.addViewWithFrame(headerText, 0, 0, 100, 12)

        // create subheader
        val subHeaderText = ASTextView(this)
        subHeaderText.typeface = Typeface.createFromAsset(this.assets,
                "fonts/about/albino.ttf")
        subHeaderText.text = getText(R.string.zlcore_title_activity_version_change_history)
        subHeaderText.setBackgroundResource(R.drawable.subheader_about)
        subHeaderText.textSize = 18f
        subHeaderText.setTextColor(Color.WHITE)
        subHeaderText.gravity = Gravity.CENTER_VERTICAL
        canvas.addViewWithFrame(subHeaderText, 0, 12, 100, 10)

        countDownTimerHeaderText = ASTextView(this)
        countDownTimerHeaderText.typeface = Typeface.createFromAsset(this.assets, "fonts/about/albino.ttf")
        countDownTimerHeaderText.text = ""
        countDownTimerHeaderText.textSize = 18f
        countDownTimerHeaderText.setTextColor(Color.WHITE)
        countDownTimerHeaderText.gravity = Gravity.END or Gravity.CENTER_VERTICAL
        canvas.addViewWithFrame(countDownTimerHeaderText, 51, 12, 50, 10)

        // create 78 % area with canvassection
        val mainSection = canvas.createNewSectionWithFrame(0, 22,
                100, 78, true).setSectionAsLinearLayout(LinearLayout.VERTICAL)

        //final AnimatedExpandableListView listView = new AnimatedExpandableListView(this);
        val customView = LayoutInflater.from(this).inflate(R.layout.fragment_version_change_history, null)
        val listView = customView.findViewById<AnimatedExpandableListView>(R.id.version_change_history_expandableListView)
        listView.cacheColorHint = Color.TRANSPARENT
        listView.dividerHeight = 3
        mainSection.addViewInLinearLayout(customView)

        val adapter = SimpleExpandableListAdapter(this, createData(this), true)

        if (VERSION.SDK_INT > 19) {
            val set = AnimationSet(true)
            set.duration = 200

            var animation: Animation = AlphaAnimation(0.0f, 1.0f)
            set.addAnimation(animation)

            animation = TranslateAnimation(
                    Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f)
            set.addAnimation(animation)

            val controller = LayoutAnimationController(set, 1.0f)
            listView.layoutAnimation = controller
        }
        listView.setAdapter(adapter)

        listView.setOnGroupExpandListener {
            /*
				for (int i = 0; i < listView.getCount(); i++) {
					if (i != arg0)
						listView.collapseGroup(i);
				}
				*/
        }

        listView.setOnGroupClickListener { expandableListView, view, groupPosition, l ->
            // We call collapseGroupWithAnimation(int) and
            // expandGroupWithAnimation(int) to animate group
            // expansion/collapse.
            if (expandableListView.isGroupExpanded(groupPosition)) {
                listView.collapseGroup(groupPosition)
            } else {
                listView.expandGroup(groupPosition)
            }
            true
        }

        val scrollToTop = GoToTopView(this, IGoToTopAction { listView.smoothScrollToPosition(0) })
        mainSection.addViewWithFrame(scrollToTop, 90, 90, 10, 10)


        if (AboutUs.isDisableBackSoundControl) {
            disableMovableMenu()
        }
        setContentView(canvas.fillParentView)

        countDownSholatReminderUtils = CountDownSholatReminderUtils()
    }

    override fun onResume() {
        super.onResume()
        if (countDownSholatReminderUtils != null) {
            countDownSholatReminderUtils!!.startCountDown(this, countDownTimerHeaderText)
        }
    }

    override fun onPause() {
        super.onPause()
        if (countDownSholatReminderUtils != null) {
            countDownSholatReminderUtils!!.stopCountDown()
        }
    }

    companion object {

        fun createData(context: Context): SparseArray<SimpleExpandableDataModel> {
            val groups = SparseArray<SimpleExpandableDataModel>()
            try {
                var data: List<SimpleItemDescriptionModel>? = null
                val gson = Gson()
                val listString = FileUtils.getStringFromRawFile(context, AboutUs.riwayatRawFile)
                data = gson.fromJson<List<SimpleItemDescriptionModel>>(listString, object : TypeToken<List<SimpleItemDescriptionModel>>() {

                }.type)

                val iterator = data!!.iterator()
                var i = 0
                while (iterator.hasNext()) {
                    val item = iterator.next()
                    val group = SimpleExpandableDataModel(item.item)
                    group.children.add(item.description)
                    groups.append(i++, group)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }


            return groups
        }

        fun start(context: Context) {
            val historyIntent = Intent(context, VersionChangeHistoryCanvas::class.java)
            context.startActivity(historyIntent)
        }
    }
}
