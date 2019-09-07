package com.zaitunlabs.zlcore.core

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle

import androidx.fragment.app.FragmentTransaction
import androidx.appcompat.widget.Toolbar
import android.text.TextUtils
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebView

import com.zaitunlabs.zlcore.R
import com.zaitunlabs.zlcore.fragments.GeneralWebViewFragment
import com.zaitunlabs.zlcore.fragments.InfoFragment
import com.zaitunlabs.zlcore.utils.CommonUtils
import com.zaitunlabs.zlcore.utils.HttpClientUtils

import java.util.ArrayList


/**
 * Created by ahmad s on 3/17/2016.
 */
class WebViewActivity : BaseActivity() {
    private var newFragment: WebViewFragment? = null
    private var url: String? = null
    private var title: String? = null
    private var bgColor: Int = 0
    private var defaultMessage: String? = null
    private var pageTag: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_layout_webview)

        url = CommonUtils.getStringIntent(intent, PARAM_URL, null)
        title = CommonUtils.getStringIntent(intent, PARAM_TITLE, null)
        bgColor = CommonUtils.getIntIntent(intent, PARAM_BG_COLOR, -1)
        defaultMessage = CommonUtils.getStringIntent(intent, PARAM_DEFAULT_MESSAGE, null)
        pageTag = CommonUtils.getStringIntent(intent, PARAM_PAGE_TAG, null)

        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        enableUpNavigation()
        supportActionBar!!.title = title

        val usedTag = if (TextUtils.isEmpty(pageTag)) GeneralWebViewFragment.FRAGMENT_TAG else pageTag

        val oldFragment = supportFragmentManager.findFragmentByTag(usedTag) as WebViewFragment?

        var transaction: FragmentTransaction? = supportFragmentManager.beginTransaction()
        if (oldFragment != null) {
            transaction!!.remove(oldFragment)
        }
        transaction!!.commit()
        transaction = null

        transaction = supportFragmentManager.beginTransaction()
        newFragment = WebViewFragment()

        val isMeid = CommonUtils.getBooleanIntent(intent, PARAM_IS_MEID, false)
        if (isMeid) {
            val headerList = HttpClientUtils.getHeaderList(true, true, true, true)
            newFragment!!.setArg(this, 1, url, defaultMessage, bgColor, false, headerList)
        } else {
            newFragment!!.setArg(this, 1, url, defaultMessage, bgColor)
        }
        transaction.replace(R.id.webview_main_fragment, newFragment!!, usedTag)
        transaction.commit()
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                if (newFragment != null && newFragment!!.navigateBack()) {
                    return true
                }
                onBackPressed()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return if (newFragment != null && newFragment!!.onKeyDown(keyCode, event)) {
            true
        } else super.onKeyDown(keyCode, event)
    }

    override fun onDestroy() {
        newFragment = null
        super.onDestroy()
    }


    class WebViewFragment : GeneralWebViewFragment() {

        protected override val customInfoView: View?
            get() = null

        protected override val customInfoTextView: Int
            get() = 0

        override fun getCustomProgressBar(): View? {
            return null
        }


        override fun setupWebview(webView: WebView?) {
            super.setupWebview(webView)
            webView!!.addJavascriptInterface(WebViewFragment.WebAppInterface(this.activity), getString(R.string.app_name).replace(" ", "").toLowerCase())
        }

        private inner class WebAppInterface
        /** Instantiate the interface and set the context  */
        internal constructor(internal var activity: Activity) {

            /** Show a toast from the web page  */
            @JavascriptInterface
            fun showToast(toast: String) {
                CommonUtils.showToast(activity, toast)
            }


            @JavascriptInterface
            fun showInfo(title: String, info: String) {
                CommonUtils.showInfo(activity, title, info)
            }

            @JavascriptInterface
            fun webDescription(desc: String) {
                //Toast.makeText(activity.getBaseContext(), desc, Toast.LENGTH_SHORT).show();
            }

            @JavascriptInterface
            fun showActionBar(title: String) {
                if (!TextUtils.isEmpty(title)) {
                    (activity as BaseActivity).supportActionBar!!.title = title
                }
                (activity as BaseActivity).supportActionBar!!.show()
            }

            @JavascriptInterface
            fun reload() {
            }


        }

    }

    companion object {
        val PARAM_URL = "param_url"
        val PARAM_TITLE = "param_title"
        val PARAM_BG_COLOR = "param_bg_color"
        val PARAM_DEFAULT_MESSAGE = "param_default_message"
        val PARAM_PAGE_TAG = "param_page_tag"
        val PARAM_IS_MEID = InfoFragment.PARAM_IS_MEID

        fun start(context: Context, urlOrHtmlContent: String, title: String, defaultMessage: String, bgColor: Int, pageTag: String, isMeid: Boolean) {
            val webviewIntent = Intent(context, WebViewActivity::class.java)
            webviewIntent.putExtra(PARAM_URL, urlOrHtmlContent)
            webviewIntent.putExtra(PARAM_TITLE, title)
            webviewIntent.putExtra(PARAM_BG_COLOR, bgColor)
            webviewIntent.putExtra(PARAM_DEFAULT_MESSAGE, defaultMessage)
            webviewIntent.putExtra(PARAM_PAGE_TAG, pageTag)
            webviewIntent.putExtra(PARAM_IS_MEID, isMeid)
            context.startActivity(webviewIntent)
        }
    }
}
