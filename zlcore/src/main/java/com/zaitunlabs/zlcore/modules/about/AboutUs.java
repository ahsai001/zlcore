package com.zaitunlabs.zlcore.modules.about;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.RawRes;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView.ScaleType;

import com.squareup.picasso.Picasso;
import com.zaitunlabs.zlcore.R;
import com.zaitunlabs.zlcore.modules.version_history.VersionChangeHistoryActivity;
import com.zaitunlabs.zlcore.core.CanvasActivity;
import com.zaitunlabs.zlcore.core.WebViewActivity;
import com.zaitunlabs.zlcore.modules.version_history.VersionChangeHistoryCanvas;
import com.zaitunlabs.zlcore.utils.CommonUtil;
import com.zaitunlabs.zlcore.utils.ViewUtil;
import com.zaitunlabs.zlcore.views.ASImageView;
import com.zaitunlabs.zlcore.views.ASMovableMenu;
import com.zaitunlabs.zlcore.views.ASTextView;
import com.zaitunlabs.zlcore.views.CanvasLayout;
import com.zaitunlabs.zlcore.views.CanvasSection;

import static android.os.Build.VERSION.SDK_INT;


public class AboutUs extends CanvasActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setPortrait();

		//create Canvas Page
		CanvasLayout canvas = new CanvasLayout(this);

		//create page background 
		canvas.setBackgroundResource(AboutUs.backgroundDrawableRes);

		//create header
		ASTextView headerText = new ASTextView(this);
		if(isZL) {
			headerText.setTypeface(Typeface.createFromAsset(this.getAssets(), "fonts/about/ArabDances.ttf"));
		} else {
			headerText.setTypeface(Typeface.createFromAsset(this.getAssets(), "fonts/about/GeosansLight.ttf"));
		}
		headerText.setText(getString(R.string.app_name));
		headerText.setTextSize(30);
		headerText.setTextColor(AboutUs.textColorInt);
		headerText.setGravity(Gravity.CENTER);
		canvas.addViewWithFrame(headerText, 0, 0, 100, 12);



		ASImageView logoX = new ASImageView(this);
		//RoundedImageView logo = new RoundedImageView(this);
		logoX.setScaleType(ScaleType.FIT_XY);
		logoX.setAdjustViewBounds(true);
		Picasso.get().load(logoDrawableRes).into(logoX);
		canvas.addViewWithFrame(logoX, 40, 12, 20, 20);


		logoX.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(AboutUs.appLandingPageLink.startsWith("file:///android_asset/")){
					WebViewActivity.start(AboutUs.this,AboutUs.appLandingPageLink, getString(R.string.zlcore_aboutus_about_app),
							getString(R.string.zlcore_warning_sorry_there_is_problem),
							0,"tentang aplikasi", isMeid);
				} else {
					CommonUtil.openBrowser(AboutUs.this, AboutUs.appLandingPageLink);
				}
			}
		});




		//create Developer : ZaitunLabs.com
		ASTextView Text1 = new ASTextView(this);
		Text1.setTypeface(Typeface.createFromAsset(this.getAssets(), "fonts/about/GeosansLight.ttf"));
		Text1.setText(String.format(getString(R.string.zlcore_aboutus_developer_name),AboutUs.developerName));
		Text1.setTextSize(20);
		Text1.setTextColor(AboutUs.textColorInt);
		Text1.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
		canvas.addViewWithFrame(Text1, 10, 32, 80, 5);



		Text1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				CommonUtil.openBrowser(AboutUs.this, AboutUs.developerHomePage);
			}
		});

		//create Developer : Email : team@zaitunlabs.com
		ASTextView  Text3 = new ASTextView(this);
		Text3.setTypeface(Typeface.createFromAsset(this.getAssets(), "fonts/about/GeosansLight.ttf"));
		Text3.setText(String.format(getString(R.string.zlcore_aboutus_developer_email),AboutUs.developerEmail));
		Text3.setTextSize(20);
		Text3.setTextColor(AboutUs.textColorInt);
		Text3.setGravity(Gravity.CENTER_VERTICAL);
		canvas.addViewWithFrame(Text3, 10, 37, 80, 5);

		Text3.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				CommonUtil.sendEmail(AboutUs.this,AboutUs.developerEmail,"","",getString(R.string.zlcore_aboutus_kirim_email));
			}
		});


		//create Developer : Email : team@zaitunlabs.com
		ASTextView  Text4 = new ASTextView(this);
		Text4.setTypeface(Typeface.createFromAsset(this.getAssets(), "fonts/about/GeosansLight.ttf"));
		Text4.setText(String.format(getString(R.string.zlcore_aboutus_version), CommonUtil.getVersionName(this)));
		Text4.setTextSize(18);
		Text4.setTextColor(AboutUs.textColorInt);
		Text4.setGravity(Gravity.CENTER_VERTICAL);
		canvas.addViewWithFrame(Text4, 10, 42, 80, 5);


		CanvasSection versionAndAboutAppCanvas = canvas.createNewSectionWithFrame(50, 47, 50,10);
		versionAndAboutAppCanvas.setBackgroundColor(Color.argb(80,0,0,0));

		if(!TextUtils.isEmpty(AboutUs.aboutThisAppUrlOrHtmlContent)){
			ASTextView  aboutThisApp = new ASTextView(this);
			aboutThisApp.setTypeface(Typeface.createFromAsset(this.getAssets(), "fonts/about/GeosansLight.ttf"));

			SpannableString aboutThisAppSpanString = new SpannableString(getString(R.string.zlcore_aboutus_title_link_about_app));
			aboutThisAppSpanString.setSpan(new UnderlineSpan(), 0, aboutThisAppSpanString.length(), 0);
			aboutThisAppSpanString.setSpan(new StyleSpan(Typeface.BOLD), 0, aboutThisAppSpanString.length(), 0);
			aboutThisAppSpanString.setSpan(new StyleSpan(Typeface.ITALIC), 0, aboutThisAppSpanString.length(), 0);

			aboutThisApp.setText(aboutThisAppSpanString);
			aboutThisApp.setTextSize(14);
			aboutThisApp.setTextColor(AboutUs.textColorInt);
			aboutThisApp.setGravity(Gravity.CENTER_VERTICAL|Gravity.RIGHT | Gravity.END);
			versionAndAboutAppCanvas.addViewWithFrame(aboutThisApp, 0, 0, 100,50);

			aboutThisApp.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(AboutUs.aboutThisAppUrlOrHtmlContent.startsWith("file:///android_asset/")){
						WebViewActivity.start(AboutUs.this,AboutUs.aboutThisAppUrlOrHtmlContent, getString(R.string.zlcore_aboutus_about_app),
								getString(R.string.zlcore_warning_sorry_there_is_problem),
								0,"tentang aplikasi", isMeid);
					} else {
						CommonUtil.openBrowser(AboutUs.this, AboutUs.appLandingPageLink);
					}
				}
			});
		}

		//
		ASTextView  history = new ASTextView(this);
		history.setTypeface(Typeface.createFromAsset(this.getAssets(), "fonts/about/GeosansLight.ttf"));

		SpannableString historySpanString = new SpannableString(getString(R.string.zlcore_aboutus_app_history));
		historySpanString.setSpan(new UnderlineSpan(), 0, historySpanString.length(), 0);
		historySpanString.setSpan(new StyleSpan(Typeface.BOLD), 0, historySpanString.length(), 0);
		historySpanString.setSpan(new StyleSpan(Typeface.ITALIC), 0, historySpanString.length(), 0);

		history.setText(historySpanString);
		history.setTextSize(14);
		history.setTextColor(AboutUs.textColorInt);
		history.setGravity(Gravity.CENTER_VERTICAL|Gravity.RIGHT | Gravity.END);
		if(TextUtils.isEmpty(AboutUs.aboutThisAppUrlOrHtmlContent)) {
			versionAndAboutAppCanvas.addViewWithFrame(history, 0, 26 , 100, 50);
		} else {
			versionAndAboutAppCanvas.addViewWithFrame(history, 0, 50, 100, 50);
		}

		history.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(isZL) {
					VersionChangeHistoryCanvas.start(AboutUs.this);
				} else {
					VersionChangeHistoryActivity.start(AboutUs.this);
				}
			}
		});




		CanvasSection logoTradeMarkCanvas = canvas.createNewSectionWithFrame(0, 64, 100, 18);

		logoTradeMarkCanvas.setBackgroundColor(Color.argb(50,255,255,255));

		ASImageView logo = new ASImageView(this);
		//RoundedImageView logo = new RoundedImageView(this);
		logo.setScaleType(ScaleType.FIT_XY);
		logo.setAdjustViewBounds(true);
		Picasso.get().load(developerResLogo).into(logo);
		logoTradeMarkCanvas.addViewWithFrame(logo, 15, 0, 20, 100);

		logo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				CommonUtil.openBrowser(AboutUs.this, AboutUs.developerHomePage);
			}
		});

		//create 2014 Muslim Indonesia <br> All Rights Reseved
		ASTextView  Text2 = new ASTextView(this);
		Text2.setTypeface(Typeface.createFromAsset(this.getAssets(), "fonts/about/GeosansLight.ttf"));
		Text2.setText(AboutUs.developerTM);
		Text2.setTextSize(20);
		Text2.setPadding(0,0,0,0);
		Text2.setTextColor(AboutUs.textColorInt);

		Text2.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
		logoTradeMarkCanvas.addViewWithFrame(Text2, 39, 0, 60, 100);


		CanvasSection leftBottomMenu = canvas.createNewSectionWithFrame(0, 85, 34, 15);
		leftBottomMenu.setClickable(true);
		if(isZL) {
			leftBottomMenu.setBackgroundResource(R.drawable.menubottom_selector);
		} else {
			if(SDK_INT >= 16) {
				leftBottomMenu.setBackground(ViewUtil.getSelectableItemBackgroundWithColor(this,
						ContextCompat.getColor(this, R.color.colorAccent)));
			} else {
				leftBottomMenu.setBackgroundDrawable(ViewUtil.getSelectableItemBackgroundWithColor(this,
						ContextCompat.getColor(this, R.color.colorAccent)));
			}
		}
		
		CanvasSection midleBottomMenu = canvas.createNewSectionWithFrame(33, 85, 34, 15);
		midleBottomMenu.setClickable(true);
		if(isZL) {
			midleBottomMenu.setBackgroundResource(R.drawable.menubottom_selector);
		} else {
            if(SDK_INT >= 16) {
				midleBottomMenu.setBackground(ViewUtil.getSelectableItemBackgroundWithColor(this,
						ContextCompat.getColor(this, R.color.colorAccent)));
			} else {
				midleBottomMenu.setBackgroundDrawable(ViewUtil.getSelectableItemBackgroundWithColor(this,
						ContextCompat.getColor(this, R.color.colorAccent)));
			}
		}
		
		CanvasSection rightBottomMenu = canvas.createNewSectionWithFrame(66, 85, 34, 15);
		rightBottomMenu.setClickable(true);
		if(isZL) {
			rightBottomMenu.setBackgroundResource(R.drawable.menubottom_selector);
		} else {
			if(SDK_INT >= 16) {
				rightBottomMenu.setBackground(ViewUtil.getSelectableItemBackgroundWithColor(this,
						ContextCompat.getColor(this, R.color.colorAccent)));
			} else {
				rightBottomMenu.setBackgroundDrawable(ViewUtil.getSelectableItemBackgroundWithColor(this,
						ContextCompat.getColor(this, R.color.colorAccent)));
			}

		}

		ASImageView shareButton = new ASImageView(this);
		shareButton.setImageResource(shareDrawableRes);
		leftBottomMenu.addViewWithFrame(shareButton, 25, 5, 50, 70);

		leftBottomMenu.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				CommonUtil.shareContent(AboutUs.this, getString(R.string.zlcore_aboutus_share_title), getString(shareTitleRes), getString(shareBodyRes));
			}
		});

		ASImageView feedBackButton = new ASImageView(this);
		feedBackButton.setImageResource(feedBackDrawableRes);
		midleBottomMenu.addViewWithFrame(feedBackButton, 25, 5, 50, 70);

		midleBottomMenu.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				CommonUtil.sendEmail(AboutUs.this, getString(feedbackMailToRes), getString(feedbackTitleRes), getString(feedbackBodyRes), "kirim email feedback :");
			}
		});

		ASImageView rateButton = new ASImageView(this);
		rateButton.setImageResource(rateDrawableRes);
		rightBottomMenu.addViewWithFrame(rateButton, 25, 5, 50, 70);


		rightBottomMenu.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				CommonUtil.openPlayStore(v.getContext(),AboutUs.this.getPackageName());
			}
		});


		ASTextView  shareText = new ASTextView(this);
		shareText.setTypeface(Typeface.createFromAsset(this.getAssets(), "fonts/about/GeosansLight.ttf"));
		shareText.setText(getString(R.string.zlcore_aboutus_menu_share_title));
		shareText.setTextSize(13);
		shareText.setTextColor(AboutUs.menuTextColorInt);
		shareText.setGravity(Gravity.CENTER);
		leftBottomMenu.addViewWithFrame(shareText, 0, 75, 100, 25);
		
		ASTextView  feedbackText = new ASTextView(this);
		feedbackText.setTypeface(Typeface.createFromAsset(this.getAssets(), "fonts/about/GeosansLight.ttf"));
		feedbackText.setText(getString(R.string.zlcore_aboutus_menu_feedback_title));
		feedbackText.setTextSize(13);
		feedbackText.setTextColor(AboutUs.menuTextColorInt);
		feedbackText.setGravity(Gravity.CENTER);
		midleBottomMenu.addViewWithFrame(feedbackText, 0, 75, 100, 25);

		ASTextView  rateText = new ASTextView(this);
		rateText.setTypeface(Typeface.createFromAsset(this.getAssets(), "fonts/about/GeosansLight.ttf"));
		rateText.setText(getString(R.string.zlcore_aboutus_menu_rate_title));
		rateText.setTextSize(13);
		rateText.setTextColor(AboutUs.menuTextColorInt);
		rateText.setGravity(Gravity.CENTER);
		rightBottomMenu.addViewWithFrame(rateText, 0, 75, 100, 25);

		if(isDisableBackSoundControl) {
			disableMovableMenu();
		}
		setContentView(canvas.getFillParentView());
	}

	@Override
	public void onCreateMovableMenu(ASMovableMenu menu) {
		super.onCreateMovableMenu(menu);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	public static int logoDrawableRes;
	public static String appLandingPageLink;
	public static int shareDrawableRes;
	public static int shareTitleRes;
	public static int shareBodyRes;
	public static int feedBackDrawableRes;
	public static int feedbackMailToRes;
	public static int feedbackTitleRes;
	public static int feedbackBodyRes;
	public static int rateDrawableRes;
	public static int riwayatRawFile;
	public static boolean isDisableBackSoundControl = false;
	public static boolean isZL = false;
	public static String developerName;
	public static String developerHomePage;
	public static String developerEmail;
	public static int developerResLogo;
	public static String developerTM;
	public static int backgroundDrawableRes;
	public static int textColorInt;
	public static int menuTextColorInt;
	public static String aboutThisAppUrlOrHtmlContent;
	public static boolean isMeid = false;

	public static void start(Context context,@DrawableRes int logoDrawableRes,
							 @DrawableRes int shareDrawableRes, @StringRes int shareTitleRes, @StringRes int shareBodyRes,
							 @DrawableRes int feedBackDrawableRes,@StringRes int feedbackMailToRes,@StringRes int feedbackTitleRes ,@StringRes int feedbackBodyRes,
							 @DrawableRes int rateDrawableRes,
							 @RawRes int riwayatRawFile, boolean isDisableBackSoundControl,
							 String appLandingPageLink, boolean isMeid){
		start(context, logoDrawableRes, shareDrawableRes, shareTitleRes, shareBodyRes,
				feedBackDrawableRes, feedbackMailToRes, feedbackTitleRes, feedbackBodyRes,
				rateDrawableRes,
				riwayatRawFile, isDisableBackSoundControl,
				appLandingPageLink, true,
				null, null, null,
				0, null, 0, 0, 0, appLandingPageLink, isMeid);
	}

	public static void start(Context context,@DrawableRes int logoDrawableRes,
							 @DrawableRes int shareDrawableRes,@StringRes int shareTitleRes,@StringRes int shareBodyRes,
							 @DrawableRes int feedBackDrawableRes,@StringRes int feedbackMailToRes,@StringRes int feedbackTitleRes ,@StringRes int feedbackBodyRes,
							 @DrawableRes int rateDrawableRes,
							 @RawRes int riwayatRawFile, boolean isDisableBackSoundControl,
							 String appLandingPageLink, boolean isZL,
							 String developerName, String developerHomePage, String developerEmail,
							 @DrawableRes int developerLogoDrawableRes, String developerTM, @DrawableRes int backgroundDrawableRes, @ColorInt int textColorInt, @ColorInt int menuTextColorInt, String aboutThisAppUrlOrHtmlContent, boolean isMeid){
		Intent aboutIntent = new Intent(context, AboutUs.class);
		AboutUs.logoDrawableRes = logoDrawableRes;

		AboutUs.shareDrawableRes = shareDrawableRes == 0?R.drawable.share:shareDrawableRes;
		AboutUs.shareTitleRes = shareTitleRes;
		AboutUs.shareBodyRes = shareBodyRes;

		AboutUs.feedBackDrawableRes = feedBackDrawableRes == 0?R.drawable.feedback:feedBackDrawableRes;
		AboutUs.feedbackMailToRes = feedbackMailToRes;
		AboutUs.feedbackTitleRes = feedbackTitleRes;
		AboutUs.feedbackBodyRes = feedbackBodyRes;

		AboutUs.rateDrawableRes = rateDrawableRes == 0?R.drawable.rate:rateDrawableRes;
		AboutUs.riwayatRawFile = riwayatRawFile;
		AboutUs.isDisableBackSoundControl = isDisableBackSoundControl;
		AboutUs.appLandingPageLink = appLandingPageLink;
		AboutUs.isZL = isZL;
		AboutUs.developerName = TextUtils.isEmpty(developerName)?"ZaitunLabs.com":developerName;
		AboutUs.developerHomePage = TextUtils.isEmpty(developerHomePage)?"https://www.zaitunlabs.com/":developerHomePage;
		AboutUs.developerEmail = TextUtils.isEmpty(developerEmail)?"team@zaitunlabs.com":developerEmail;
		AboutUs.developerResLogo = developerLogoDrawableRes == 0?R.drawable.logo_zl:developerLogoDrawableRes;
		AboutUs.developerTM = TextUtils.isEmpty(developerTM)?"2014 \nAll Rights Reserved":developerTM;
		AboutUs.backgroundDrawableRes = backgroundDrawableRes == 0?R.drawable.bg_about:backgroundDrawableRes;
		AboutUs.textColorInt = textColorInt == 0?Color.WHITE:textColorInt;
		AboutUs.menuTextColorInt = menuTextColorInt == 0?Color.WHITE:menuTextColorInt;
		AboutUs.aboutThisAppUrlOrHtmlContent = aboutThisAppUrlOrHtmlContent;
		AboutUs.isMeid = isMeid;
		context.startActivity(aboutIntent);
	}
}
