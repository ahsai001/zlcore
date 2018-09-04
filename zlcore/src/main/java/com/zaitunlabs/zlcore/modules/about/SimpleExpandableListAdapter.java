package com.zaitunlabs.zlcore.modules.about;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.TextView;

import com.idunnololz.widgets.AnimatedExpandableListView;
import com.zaitunlabs.zlcore.utils.CommonUtils;
import com.zaitunlabs.zlcore.utils.DebugUtils;
import com.zaitunlabs.zlcore.utils.LinkUtils;

public class SimpleExpandableListAdapter extends AnimatedExpandableListView.AnimatedExpandableListAdapter {
	private final SparseArray<SimpleExpandableDataModel> data;
	public Activity activity;
	private int groupHeight = 0;
	private boolean isZL;

	public SimpleExpandableListAdapter(Activity act, SparseArray<SimpleExpandableDataModel> groups, boolean isZL) {
		activity = act;
		this.data = groups;

		int fontHeight = 0;
		int groupIndicatoHeight = 0;
		fontHeight = (int) CommonUtils.getFontHeight("T")+10;
		float scale = CommonUtils.getDisplayMetricsScaledDensity(act);
		groupIndicatoHeight = (int) (CommonUtils.getImageDimension(activity.getBaseContext(), android.R.attr.groupIndicator).y+(30*scale));
		groupHeight = Math.max(fontHeight, groupIndicatoHeight);
		DebugUtils.logW("HEIGHT", "groupIndicatoHeight : "+groupIndicatoHeight);
		DebugUtils.logW("HEIGHT", "fontHeight : "+fontHeight);

		this.isZL = isZL;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return data.get(groupPosition).children.get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getRealChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		final String children = (String) getChild(groupPosition, childPosition);
		TextView text = null;
		if (convertView == null) {
			convertView = new TextView(activity);
		}
		text = (TextView) convertView;
		text.setText(children);

		LinkUtils.autoLink(text, new LinkUtils.OnClickListener() {

			@Override
			public void onLinkClicked(String link) {
				DebugUtils.logI("", "link : " + link);
				CommonUtils.openBrowser(activity, link);
			}

			@Override
			public void onClicked() {
				DebugUtils.logI("", " link clicked");
			}
		});

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1){
			text.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
		}
		text.setGravity(Gravity.CENTER);
		text.setSingleLine(false);
		text.setPadding(0,20,0,20);
		if(isZL) {
			text.setTextColor(Color.WHITE);
			text.setBackgroundColor(Color.argb(150, 0, 0, 0));
		} else {
			text.setTextColor(Color.BLACK);
			text.setBackgroundColor(Color.argb(50,0,0,0));
		}
		return convertView;
	}

	@Override
	public int getRealChildrenCount(int groupPosition) {
		return data.get(groupPosition).children.size();
	}


	@Override
	public Object getGroup(int groupPosition) {
		return data.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return data.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return 0;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		TextView text = null;
		if (convertView == null) {
			convertView = new TextView(activity);
		}
		SimpleExpandableDataModel group = (SimpleExpandableDataModel) getGroup(groupPosition);
		text = (TextView) convertView;
		text.setText(group.string);

		if(isZL) {
			text.setTextColor(Color.WHITE);
		} else {
			text.setTextColor(Color.BLACK);
		}

		text.setHeight(groupHeight);

		int padding = CommonUtils.getIntAttrValue(activity, android.R.attr.paddingTop);
		text.setPadding(CommonUtils.getIntAttrValue(activity, android.R.attr.expandableListPreferredItemPaddingLeft), padding, padding, padding);

		text.setGravity(Gravity.CENTER_VERTICAL);

		if(isZL) {
			if (CommonUtils.isOdd(groupPosition)) {
				text.setBackgroundColor(Color.argb(200, 0, 0, 0));
			} else {
				text.setBackgroundColor(Color.argb(50, 0, 0, 0));
			}
		} else {
			text.setBackgroundColor(Color.WHITE);
		}
		
		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return false;
	}

}
