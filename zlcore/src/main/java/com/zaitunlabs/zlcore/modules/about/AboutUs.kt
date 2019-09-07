package com.zaitunlabs.zlcore.modules.about


import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.view.Gravity
import android.view.View
import android.widget.ImageView.ScaleType

import com.squareup.picasso.Picasso
import com.zaitunlabs.zlcore.R
import com.zaitunlabs.zlcore.modules.version_history.VersionChangeHistoryActivity
import com.zaitunlabs.zlcore.core.CanvasActivity
import com.zaitunlabs.zlcore.core.WebViewActivity
import com.zaitunlabs.zlcore.modules.version_history.VersionChangeHistoryCanvas
import com.zaitunlabs.zlcore.utils.CommonUtils
import com.zaitunlabs.zlcore.utils.ViewUtils
import com.zaitunlabs.zlcore.views.ASImageView
import com.zaitunlabs.zlcore.views.ASMovableMenu
import com.zaitunlabs.zlcore.views.ASTextView
import com.zaitunlabs.zlcore.views.CanvasLayout
import com.zaitunlabs.zlcore.views.CanvasSection

import android.os.Build.VERSION.SDK_INT


class AboutUs : CanvasActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setPortrait();

        //create Canvas Page
        val canvas = CanvasLayout(this)

        //create page background
        canvas.setBackgroundResource(AboutUs.backgroundDrawableRes)

        //create header
        val headerText = ASTextView(this)
        if (isZL) {
            headerText.typeface = Typeface.createFromAsset(this.assets, "fonts/about/ArabDances.ttf")
        } else {
            headerText.typeface = Typeface.createFromAsset(this.assets, "fonts/about/GeosansLight.ttf")
        }
        headerText.text = getString(R.string.app_name)
        headerText.textSize = 30f
        headerText.setTextColor(AboutUs.textColorInt)
        headerText.gravity = Gravity.CENTER
        canvas.addViewWithFrame(headerText, 0, 0, 100, 12)


        val logoX = ASImageView(this)
        //RoundedImageView logo = new RoundedImageView(this);
        logoX.scaleType = ScaleType.FIT_XY
        logoX.adjustViewBounds = true
        Picasso.get().load(logoDrawableRes).into(logoX)
        canvas.addViewWithFrame(logoX, 40, 12, 20, 20)


        logoX.setOnClickListener {
            if (AboutUs.appLandingPageLink.startsWith("file:///android_asset/")) {
                WebViewActivity.start(this@AboutUs, AboutUs.appLandingPageLink, getString(R.string.zlcore_aboutus_about_app),
                        getString(R.string.zlcore_warning_sorry_there_is_problem),
                        0, "tentang aplikasi", isMeid)
            } else {
                CommonUtils.openBrowser(this@AboutUs, AboutUs.appLandingPageLink)
            }
        }


        //create Developer : ZaitunLabs.com
        val Text1 = ASTextView(this)
        Text1.typeface = Typeface.createFromAsset(this.assets, "fonts/about/GeosansLight.ttf")
        Text1.setText(String.format(getString(R.string.zlcore_aboutus_developer_name), AboutUs.developerName))
        Text1.textSize = 20f
        Text1.setTextColor(AboutUs.textColorInt)
        Text1.gravity = Gravity.CENTER_VERTICAL or Gravity.LEFT
        canvas.addViewWithFrame(Text1, 10, 32, 80, 5)



        Text1.setOnClickListener { CommonUtils.openBrowser(this@AboutUs, AboutUs.developerHomePage) }

        //create Developer : Email : team@zaitunlabs.com
        val Text3 = ASTextView(this)
        Text3.typeface = Typeface.createFromAsset(this.assets, "fonts/about/GeosansLight.ttf")
        Text3.setText(String.format(getString(R.string.zlcore_aboutus_developer_email), AboutUs.developerEmail))
        Text3.textSize = 20f
        Text3.setTextColor(AboutUs.textColorInt)
        Text3.gravity = Gravity.CENTER_VERTICAL
        canvas.addViewWithFrame(Text3, 10, 37, 80, 5)

        Text3.setOnClickListener { CommonUtils.sendEmail(this@AboutUs, AboutUs.developerEmail, "", "", getString(R.string.zlcore_aboutus_kirim_email)) }


        //create Developer : Email : team@zaitunlabs.com
        val Text4 = ASTextView(this)
        Text4.typeface = Typeface.createFromAsset(this.assets, "fonts/about/GeosansLight.ttf")
        Text4.setText(String.format(getString(R.string.zlcore_aboutus_version), CommonUtils.getVersionName(this)))
        Text4.textSize = 18f
        Text4.setTextColor(AboutUs.textColorInt)
        Text4.gravity = Gravity.CENTER_VERTICAL
        canvas.addViewWithFrame(Text4, 10, 42, 80, 5)


        val versionAndAboutAppCanvas = canvas.createNewSectionWithFrame(50, 47, 50, 10)
        versionAndAboutAppCanvas.setBackgroundColor(Color.argb(80, 0, 0, 0))

        if (!TextUtils.isEmpty(AboutUs.aboutThisAppUrlOrHtmlContent)) {
            val aboutThisApp = ASTextView(this)
            aboutThisApp.typeface = Typeface.createFromAsset(this.assets, "fonts/about/GeosansLight.ttf")

            val aboutThisAppSpanString = SpannableString(getString(R.string.zlcore_aboutus_title_link_about_app))
            aboutThisAppSpanString.setSpan(UnderlineSpan(), 0, aboutThisAppSpanString.length, 0)
            aboutThisAppSpanString.setSpan(StyleSpan(Typeface.BOLD), 0, aboutThisAppSpanString.length, 0)
            aboutThisAppSpanString.setSpan(StyleSpan(Typeface.ITALIC), 0, aboutThisAppSpanString.length, 0)

            aboutThisApp.text = aboutThisAppSpanString
            aboutThisApp.textSize = 14f
            aboutThisApp.setTextColor(AboutUs.textColorInt)
            aboutThisApp.gravity = Gravity.CENTER_VERTICAL or Gravity.RIGHT or Gravity.END
            versionAndAboutAppCanvas.addViewWithFrame(aboutThisApp, 0, 0, 100, 50)

            aboutThisApp.setOnClickListener {
                if (AboutUs.aboutThisAppUrlOrHtmlContent.startsWith("file:///android_asset/")) {
                    WebViewActivity.start(this@AboutUs, AboutUs.aboutThisAppUrlOrHtmlContent, getString(R.string.zlcore_aboutus_about_app),
                            getString(R.string.zlcore_warning_sorry_there_is_problem),
                            0, "tentang aplikasi", isMeid)
                } else {
                    CommonUtils.openBrowser(this@AboutUs, AboutUs.appLandingPageLink)
                }
            }
        }

        //
        val history = ASTextView(this)
        history.typeface = Typeface.createFromAsset(this.assets, "fonts/about/GeosansLight.ttf")

        val historySpanString = SpannableString(getString(R.string.zlcore_aboutus_app_history))
        historySpanString.setSpan(UnderlineSpan(), 0, historySpanString.length, 0)
        historySpanString.setSpan(StyleSpan(Typeface.BOLD), 0, historySpanString.length, 0)
        historySpanString.setSpan(StyleSpan(Typeface.ITALIC), 0, historySpanString.length, 0)

        history.text = historySpanString
        history.textSize = 14f
        history.setTextColor(AboutUs.textColorInt)
        history.gravity = Gravity.CENTER_VERTICAL or Gravity.RIGHT or Gravity.END
        if (TextUtils.isEmpty(AboutUs.aboutThisAppUrlOrHtmlContent)) {
            versionAndAboutAppCanvas.addViewWithFrame(history, 0, 26, 100, 50)
        } else {
            versionAndAboutAppCanvas.addViewWithFrame(history, 0, 50, 100, 50)
        }

        history.setOnClickListener {
            if (isZL) {
                VersionChangeHistoryCanvas.start(this@AboutUs)
            } else {
                VersionChangeHistoryActivity.start(this@AboutUs)
            }
        }


        val logoTradeMarkCanvas = canvas.createNewSectionWithFrame(0, 64, 100, 18)

        logoTradeMarkCanvas.setBackgroundColor(Color.argb(50, 255, 255, 255))

        val logo = ASImageView(this)
        //RoundedImageView logo = new RoundedImageView(this);
        logo.scaleType = ScaleType.FIT_XY
        logo.adjustViewBounds = true
        Picasso.get().load(developerResLogo).into(logo)
        logoTradeMarkCanvas.addViewWithFrame(logo, 15, 0, 20, 100)

        logo.setOnClickListener { CommonUtils.openBrowser(this@AboutUs, AboutUs.developerHomePage) }

        //create 2014 Muslim Indonesia <br> All Rights Reseved
        val Text2 = ASTextView(this)
        Text2.typeface = Typeface.createFromAsset(this.assets, "fonts/about/GeosansLight.ttf")
        Text2.text = AboutUs.developerTM
        Text2.textSize = 20f
        Text2.setPadding(0, 0, 0, 0)
        Text2.setTextColor(AboutUs.textColorInt)

        Text2.gravity = Gravity.LEFT or Gravity.CENTER_VERTICAL
        logoTradeMarkCanvas.addViewWithFrame(Text2, 39, 0, 60, 100)


        val leftBottomMenu = canvas.createNewSectionWithFrame(0, 85, 34, 15)
        leftBottomMenu.isClickable = true
        if (isZL) {
            leftBottomMenu.setBackgroundResource(R.drawable.menubottom_selector)
        } else {
            if (SDK_INT >= 16) {
                leftBottomMenu.background = ViewUtils.getSelectableItemBackgroundWithColor(this,
                        ContextCompat.getColor(this, R.color.colorAccent))
            } else {
                leftBottomMenu.setBackgroundDrawable(ViewUtils.getSelectableItemBackgroundWithColor(this,
                        ContextCompat.getColor(this, R.color.colorAccent)))
            }
        }

        val midleBottomMenu = canvas.createNewSectionWithFrame(33, 85, 34, 15)
        midleBottomMenu.isClickable = true
        if (isZL) {
            midleBottomMenu.setBackgroundResource(R.drawable.menubottom_selector)
        } else {
            if (SDK_INT >= 16) {
                midleBottomMenu.background = ViewUtils.getSelectableItemBackgroundWithColor(this,
                        ContextCompat.getColor(this, R.color.colorAccent))
            } else {
                midleBottomMenu.setBackgroundDrawable(ViewUtils.getSelectableItemBackgroundWithColor(this,
                        ContextCompat.getColor(this, R.color.colorAccent)))
            }
        }

        val rightBottomMenu = canvas.createNewSectionWithFrame(66, 85, 34, 15)
        rightBottomMenu.isClickable = true
        if (isZL) {
            rightBottomMenu.setBackgroundResource(R.drawable.menubottom_selector)
        } else {
            if (SDK_INT >= 16) {
                rightBottomMenu.background = ViewUtils.getSelectableItemBackgroundWithColor(this,
                        ContextCompat.getColor(this, R.color.colorAccent))
            } else {
                rightBottomMenu.setBackgroundDrawable(ViewUtils.getSelectableItemBackgroundWithColor(this,
                        ContextCompat.getColor(this, R.color.colorAccent)))
            }

        }

        val shareButton = ASImageView(this)
        shareButton.setImageResource(shareDrawableRes)
        leftBottomMenu.addViewWithFrame(shareButton, 25, 5, 50, 70)

        leftBottomMenu.setOnClickListener { CommonUtils.shareContent(this@AboutUs, getString(R.string.zlcore_aboutus_share_title), getString(shareTitleRes), getString(shareBodyRes)) }

        val feedBackButton = ASImageView(this)
        feedBackButton.setImageResource(feedBackDrawableRes)
        midleBottomMenu.addViewWithFrame(feedBackButton, 25, 5, 50, 70)

        midleBottomMenu.setOnClickListener { CommonUtils.sendEmail(this@AboutUs, getString(feedbackMailToRes), getString(feedbackTitleRes), getString(feedbackBodyRes), "kirim email feedback :") }

        val rateButton = ASImageView(this)
        rateButton.setImageResource(rateDrawableRes)
        rightBottomMenu.addViewWithFrame(rateButton, 25, 5, 50, 70)


        rightBottomMenu.setOnClickListener { v -> CommonUtils.openPlayStore(v.context, this@AboutUs.packageName) }


        val shareText = ASTextView(this)
        shareText.typeface = Typeface.createFromAsset(this.assets, "fonts/about/GeosansLight.ttf")
        shareText.text = getString(R.string.zlcore_aboutus_menu_share_title)
        shareText.textSize = 13f
        shareText.setTextColor(AboutUs.menuTextColorInt)
        shareText.gravity = Gravity.CENTER
        leftBottomMenu.addViewWithFrame(shareText, 0, 75, 100, 25)

        val feedbackText = ASTextView(this)
        feedbackText.typeface = Typeface.createFromAsset(this.assets, "fonts/about/GeosansLight.ttf")
        feedbackText.text = getString(R.string.zlcore_aboutus_menu_feedback_title)
        feedbackText.textSize = 13f
        feedbackText.setTextColor(AboutUs.menuTextColorInt)
        feedbackText.gravity = Gravity.CENTER
        midleBottomMenu.addViewWithFrame(feedbackText, 0, 75, 100, 25)

        val rateText = ASTextView(this)
        rateText.typeface = Typeface.createFromAsset(this.assets, "fonts/about/GeosansLight.ttf")
        rateText.text = getString(R.string.zlcore_aboutus_menu_rate_title)
        rateText.textSize = 13f
        rateText.setTextColor(AboutUs.menuTextColorInt)
        rateText.gravity = Gravity.CENTER
        rightBottomMenu.addViewWithFrame(rateText, 0, 75, 100, 25)

        if (isDisableBackSoundControl) {
            disableMovableMenu()
        }
        setContentView(canvas.fillParentView)
    }

    override fun onCreateMovableMenu(menu: ASMovableMenu) {
        super.onCreateMovableMenu(menu)
    }

    override fun onResume() {
        super.onResume()
    }

    companion object {

        var logoDrawableRes: Int = 0
        var appLandingPageLink: String
        var shareDrawableRes: Int = 0
        var shareTitleRes: Int = 0
        var shareBodyRes: Int = 0
        var feedBackDrawableRes: Int = 0
        var feedbackMailToRes: Int = 0
        var feedbackTitleRes: Int = 0
        var feedbackBodyRes: Int = 0
        var rateDrawableRes: Int = 0
        var riwayatRawFile: Int = 0
        var isDisableBackSoundControl = false
        var isZL = false
        var developerName: String? = null
        var developerHomePage: String? = null
        var developerEmail: String? = null
        var developerResLogo: Int = 0
        var developerTM: String? = null
        var backgroundDrawableRes: Int = 0
        var textColorInt: Int = 0
        var menuTextColorInt: Int = 0
        var aboutThisAppUrlOrHtmlContent: String
        var isMeid = false

        fun start(context: Context, @DrawableRes logoDrawableRes: Int,
                  @DrawableRes shareDrawableRes: Int, @StringRes shareTitleRes: Int, @StringRes shareBodyRes: Int,
                  @DrawableRes feedBackDrawableRes: Int, @StringRes feedbackMailToRes: Int, @StringRes feedbackTitleRes: Int, @StringRes feedbackBodyRes: Int,
                  @DrawableRes rateDrawableRes: Int,
                  @RawRes riwayatRawFile: Int, isDisableBackSoundControl: Boolean,
                  appLandingPageLink: String, isMeid: Boolean) {
            start(context, logoDrawableRes, shareDrawableRes, shareTitleRes, shareBodyRes,
                    feedBackDrawableRes, feedbackMailToRes, feedbackTitleRes, feedbackBodyRes,
                    rateDrawableRes,
                    riwayatRawFile, isDisableBackSoundControl,
                    appLandingPageLink, true, null, null, null,
                    0, null, 0, 0, 0, appLandingPageLink, isMeid)
        }

        fun start(context: Context, @DrawableRes logoDrawableRes: Int,
                  @DrawableRes shareDrawableRes: Int, @StringRes shareTitleRes: Int, @StringRes shareBodyRes: Int,
                  @DrawableRes feedBackDrawableRes: Int, @StringRes feedbackMailToRes: Int, @StringRes feedbackTitleRes: Int, @StringRes feedbackBodyRes: Int,
                  @DrawableRes rateDrawableRes: Int,
                  @RawRes riwayatRawFile: Int, isDisableBackSoundControl: Boolean,
                  appLandingPageLink: String, isZL: Boolean,
                  developerName: String?, developerHomePage: String?, developerEmail: String?,
                  @DrawableRes developerLogoDrawableRes: Int, developerTM: String?, @DrawableRes backgroundDrawableRes: Int, @ColorInt textColorInt: Int, @ColorInt menuTextColorInt: Int, aboutThisAppUrlOrHtmlContent: String, isMeid: Boolean) {
            val aboutIntent = Intent(context, AboutUs::class.java)
            AboutUs.logoDrawableRes = logoDrawableRes

            AboutUs.shareDrawableRes = if (shareDrawableRes == 0) R.drawable.share else shareDrawableRes
            AboutUs.shareTitleRes = shareTitleRes
            AboutUs.shareBodyRes = shareBodyRes

            AboutUs.feedBackDrawableRes = if (feedBackDrawableRes == 0) R.drawable.feedback else feedBackDrawableRes
            AboutUs.feedbackMailToRes = feedbackMailToRes
            AboutUs.feedbackTitleRes = feedbackTitleRes
            AboutUs.feedbackBodyRes = feedbackBodyRes

            AboutUs.rateDrawableRes = if (rateDrawableRes == 0) R.drawable.rate else rateDrawableRes
            AboutUs.riwayatRawFile = riwayatRawFile
            AboutUs.isDisableBackSoundControl = isDisableBackSoundControl
            AboutUs.appLandingPageLink = appLandingPageLink
            AboutUs.isZL = isZL
            AboutUs.developerName = if (TextUtils.isEmpty(developerName)) "ZaitunLabs.com" else developerName
            AboutUs.developerHomePage = if (TextUtils.isEmpty(developerHomePage)) "https://www.zaitunlabs.com/" else developerHomePage
            AboutUs.developerEmail = if (TextUtils.isEmpty(developerEmail)) "team@zaitunlabs.com" else developerEmail
            AboutUs.developerResLogo = if (developerLogoDrawableRes == 0) R.drawable.logo_zl else developerLogoDrawableRes
            AboutUs.developerTM = if (TextUtils.isEmpty(developerTM)) "2014 \nAll Rights Reserved" else developerTM
            AboutUs.backgroundDrawableRes = if (backgroundDrawableRes == 0) R.drawable.bg_about else backgroundDrawableRes
            AboutUs.textColorInt = if (textColorInt == 0) Color.WHITE else textColorInt
            AboutUs.menuTextColorInt = if (menuTextColorInt == 0) Color.WHITE else menuTextColorInt
            AboutUs.aboutThisAppUrlOrHtmlContent = aboutThisAppUrlOrHtmlContent
            AboutUs.isMeid = isMeid
            context.startActivity(aboutIntent)
        }
    }
}
