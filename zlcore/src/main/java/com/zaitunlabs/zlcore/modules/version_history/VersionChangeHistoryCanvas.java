package com.zaitunlabs.zlcore.modules.version_history;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.idunnololz.widgets.AnimatedExpandableListView;
import com.zaitunlabs.zlcore.R;
import com.zaitunlabs.zlcore.modules.about.AboutUs;
import com.zaitunlabs.zlcore.modules.about.SimpleExpandableDataModel;
import com.zaitunlabs.zlcore.modules.about.SimpleExpandableListAdapter;
import com.zaitunlabs.zlcore.modules.about.SimpleItemDescriptionModel;
import com.zaitunlabs.zlcore.modules.shaum_sholat.CountDownSholatReminderUtils;
import com.zaitunlabs.zlcore.core.CanvasActivity;
import com.zaitunlabs.zlcore.utils.FileUtils;
import com.zaitunlabs.zlcore.views.ASTextView;
import com.zaitunlabs.zlcore.views.CanvasLayout;
import com.zaitunlabs.zlcore.views.CanvasSection;
import com.zaitunlabs.zlcore.views.GoToTopView;
import com.zaitunlabs.zlcore.views.GoToTopView.IGoToTopAction;


import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class VersionChangeHistoryCanvas extends CanvasActivity {
	ASTextView countDownTimerHeaderText;
	CountDownSholatReminderUtils countDownSholatReminderUtils;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// create Canvas Page
		CanvasLayout canvas = new CanvasLayout(this);

		// create page background
		canvas.setBackgroundResource(R.drawable.bg_riwayat);

		// create header
		ASTextView headerText = new ASTextView(this);
		headerText.setTypeface(Typeface.createFromAsset(this.getAssets(),
				"fonts/about/ArabDances.ttf"));
		headerText.setText(getString(R.string.app_name));
		headerText.setBackgroundResource(R.drawable.header_about);
		headerText.setTextSize(30);
		headerText.setTextColor(Color.WHITE);
		headerText.setGravity(Gravity.CENTER);
		canvas.addViewWithFrame(headerText, 0, 0, 100, 12);

		// create subheader
		ASTextView subHeaderText = new ASTextView(this);
		subHeaderText.setTypeface(Typeface.createFromAsset(this.getAssets(),
				"fonts/about/albino.ttf"));
		subHeaderText.setText(getText(R.string.zlcore_title_activity_version_change_history));
		subHeaderText.setBackgroundResource(R.drawable.subheader_about);
		subHeaderText.setTextSize(18);
		subHeaderText.setTextColor(Color.WHITE);
		subHeaderText.setGravity(Gravity.CENTER_VERTICAL);
		canvas.addViewWithFrame(subHeaderText, 0, 12, 100, 10);

		countDownTimerHeaderText = new ASTextView(this);
		countDownTimerHeaderText.setTypeface(Typeface.createFromAsset(this.getAssets(), "fonts/about/albino.ttf"));
		countDownTimerHeaderText.setText("");
		countDownTimerHeaderText.setTextSize(18);
		countDownTimerHeaderText.setTextColor(Color.WHITE);
		countDownTimerHeaderText.setGravity(Gravity.END|Gravity.CENTER_VERTICAL);
		canvas.addViewWithFrame(countDownTimerHeaderText, 51, 12, 50, 10);

		// create 78 % area with canvassection
		CanvasSection mainSection = canvas.createNewSectionWithFrame(0, 22,
				100, 78, true).setSectionAsLinearLayout(LinearLayout.VERTICAL);

		final AnimatedExpandableListView listView = new AnimatedExpandableListView(this);
		listView.setCacheColorHint(Color.TRANSPARENT);
		listView.setDividerHeight(3);
		mainSection.addViewInLinearLayout(listView);

		SimpleExpandableListAdapter adapter = new SimpleExpandableListAdapter(this,createData(this), true);
		
		if (VERSION.SDK_INT > 19) {
			AnimationSet set = new AnimationSet(true);
			set.setDuration(200);
			
			Animation animation = new AlphaAnimation(0.0f, 1.0f);
			set.addAnimation(animation);
			
			animation = new TranslateAnimation(
			    Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f,
			    Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f);
			set.addAnimation(animation);
			
			LayoutAnimationController controller = new LayoutAnimationController(set, 1.0f);
			listView.setLayoutAnimation(controller);
		}
		listView.setAdapter(adapter);

		listView.setOnGroupExpandListener(new OnGroupExpandListener() {

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

		listView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
			@Override
			public boolean onGroupClick(ExpandableListView expandableListView, View view, int groupPosition, long l) {
				// We call collapseGroupWithAnimation(int) and
				// expandGroupWithAnimation(int) to animate group
				// expansion/collapse.
				if (expandableListView.isGroupExpanded(groupPosition)) {
					listView.collapseGroupWithAnimation(groupPosition);
				} else {
					listView.expandGroupWithAnimation(groupPosition);
				}
				return true;
			}
		});

		GoToTopView scrollToTop = new GoToTopView(this, new IGoToTopAction() {
			@Override
			public void goToTopAction() {
				listView.smoothScrollToPosition(0);
			}
		});
		mainSection.addViewWithFrame(scrollToTop, 90, 90, 10, 10);


		if(AboutUs.isDisableBackSoundControl) {
			disableMovableMenu();
		}
		setContentView(canvas.getFillParentView());

		countDownSholatReminderUtils = new CountDownSholatReminderUtils();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(countDownSholatReminderUtils != null) {
			countDownSholatReminderUtils.startCountDown(this, countDownTimerHeaderText);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if(countDownSholatReminderUtils != null) {
			countDownSholatReminderUtils.stopCountDown();
		}
	}

	public static SparseArray<SimpleExpandableDataModel> createData(Context context) {
		SparseArray<SimpleExpandableDataModel> groups = new SparseArray<>();
		try {
			List<SimpleItemDescriptionModel> data = null;
			Gson gson = new Gson();
			String listString = FileUtils.getStringFromRawFile(context, AboutUs.riwayatRawFile);
			data = gson.fromJson(listString, new TypeToken<List<SimpleItemDescriptionModel>>(){}.getType());

			Iterator<SimpleItemDescriptionModel> iterator = data.iterator();
			int i = 0;
			while(iterator.hasNext()){
				SimpleItemDescriptionModel item = iterator.next();
				SimpleExpandableDataModel group = new SimpleExpandableDataModel(item.getItem());
				group.children.add(item.getDescription());
				groups.append(i++, group);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}


		return groups;
	}

	public static void start(Context context){
		Intent historyIntent = new Intent(context, VersionChangeHistoryCanvas.class);
		context.startActivity(historyIntent);
	}
}
