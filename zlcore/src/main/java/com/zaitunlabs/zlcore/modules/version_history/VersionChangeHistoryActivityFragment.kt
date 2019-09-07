package com.zaitunlabs.zlcore.modules.version_history

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.LayoutAnimationController
import android.view.animation.TranslateAnimation
import android.widget.ExpandableListView

import com.idunnololz.widgets.AnimatedExpandableListView
import com.zaitunlabs.zlcore.R
import com.zaitunlabs.zlcore.core.BaseFragment
import com.zaitunlabs.zlcore.modules.about.SimpleExpandableListAdapter

/**
 * A placeholder fragment containing a simple view.
 */
class VersionChangeHistoryActivityFragment : BaseFragment() {
    private var expandableListView: AnimatedExpandableListView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_version_change_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        expandableListView = view.findViewById(R.id.version_change_history_expandableListView)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        expandableListView!!.cacheColorHint = Color.TRANSPARENT
        expandableListView!!.dividerHeight = 3

        val adapter = SimpleExpandableListAdapter(activity, VersionChangeHistoryCanvas.createData(activity), false)

        if (Build.VERSION.SDK_INT > 19) {
            val set = AnimationSet(true)
            set.duration = 200

            var animation: Animation = AlphaAnimation(0.0f, 1.0f)
            set.addAnimation(animation)

            animation = TranslateAnimation(
                    Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f)
            set.addAnimation(animation)

            val controller = LayoutAnimationController(set, 1.0f)
            expandableListView!!.layoutAnimation = controller
        }
        expandableListView!!.setAdapter(adapter)
        expandableListView!!.setOnGroupExpandListener {
            /*
				for (int i = 0; i < listView.getCount(); i++) {
					if (i != arg0)
						listView.collapseGroup(i);
				}
				*/
        }

        expandableListView!!.setOnGroupClickListener { expandableListView, view, groupPosition, l ->
            // We call collapseGroupWithAnimation(int) and
            // expandGroupWithAnimation(int) to animate group
            // expansion/collapse.
            if (expandableListView.isGroupExpanded(groupPosition)) {
                this@VersionChangeHistoryActivityFragment.expandableListView!!.collapseGroupWithAnimation(groupPosition)
            } else {
                this@VersionChangeHistoryActivityFragment.expandableListView!!.expandGroupWithAnimation(groupPosition)
            }
            true
        }
    }
}
