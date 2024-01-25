package com.zaitunlabs.zlcore.modules.version_history;

import android.graphics.Color;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.ExpandableListView;

import com.idunnololz.widgets.AnimatedExpandableListView;
import com.zaitunlabs.zlcore.R;
import com.zaitunlabs.zlcore.core.BaseFragment;
import com.zaitunlabs.zlcore.modules.about.SimpleExpandableListAdapter;

/**
 * A placeholder fragment containing a simple view.
 */
public class VersionChangeHistoryActivityFragment extends BaseFragment {
    private AnimatedExpandableListView expandableListView;
    public VersionChangeHistoryActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_version_change_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        expandableListView = view.findViewById(R.id.version_change_history_expandableListView);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        expandableListView.setCacheColorHint(Color.TRANSPARENT);
        expandableListView.setDividerHeight(3);

        SimpleExpandableListAdapter adapter = new SimpleExpandableListAdapter(getActivity(), VersionChangeHistoryCanvas.createData(getActivity()), false);

        if (Build.VERSION.SDK_INT > 19) {
            AnimationSet set = new AnimationSet(true);
            set.setDuration(200);

            Animation animation = new AlphaAnimation(0.0f, 1.0f);
            set.addAnimation(animation);

            animation = new TranslateAnimation(
                    Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f);
            set.addAnimation(animation);

            LayoutAnimationController controller = new LayoutAnimationController(set, 1.0f);
            expandableListView.setLayoutAnimation(controller);
        }
        expandableListView.setAdapter(adapter);
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int arg0) {
				/*
				for (int i = 0; i < listView.getCount(); i++) {
					if (i != arg0)
						listView.collapseGroup(i);
				}
				*/
            }
        });

        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int groupPosition, long l) {
                // We call collapseGroupWithAnimation(int) and
                // expandGroupWithAnimation(int) to animate group
                // expansion/collapse.
                if (expandableListView.isGroupExpanded(groupPosition)) {
                    VersionChangeHistoryActivityFragment.this.expandableListView.collapseGroupWithAnimation(groupPosition);
                } else {
                    VersionChangeHistoryActivityFragment.this.expandableListView.expandGroupWithAnimation(groupPosition);
                }
                return true;
            }
        });
    }
}
