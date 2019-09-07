package com.zaitunlabs.zlcore.fragments

import android.annotation.TargetApi
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Message
import androidx.annotation.RequiresApi
import androidx.core.widget.ContentLoadingProgressBar
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

import android.text.TextUtils
import android.util.Base64
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.webkit.CookieManager
import android.webkit.DownloadListener
import android.webkit.GeolocationPermissions
import android.webkit.PermissionRequest
import android.webkit.SslErrorHandler
import android.webkit.URLUtil
import android.webkit.ValueCallback
import android.webkit.WebBackForwardList
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.TextView

import com.zaitunlabs.zlcore.R
import com.zaitunlabs.zlcore.events.GeneralWebviewEvent
import com.zaitunlabs.zlcore.models.BookmarkModel
import com.zaitunlabs.zlcore.core.BaseActivity
import com.zaitunlabs.zlcore.core.BaseFragment
import com.zaitunlabs.zlcore.utils.CommonUtils
import com.zaitunlabs.zlcore.utils.HttpClientUtils
import com.zaitunlabs.zlcore.utils.SwipeRefreshLayoutUtils
import com.zaitunlabs.zlcore.utils.ViewUtils

import org.greenrobot.eventbus.EventBus

import java.util.ArrayList
import java.util.HashMap

import android.content.Context.DOWNLOAD_SERVICE

/**
 * Created by ahmad s on 3/17/2016.
 */
abstract class GeneralWebViewFragment : BaseFragment() {

    private var rootView: View? = null
    private var webContainer: FrameLayout? = null
    private var infoView: TextView? = null
    private var progressBar: ContentLoadingProgressBar? = null
    private var rootUrl: String? = null
    var currentUrl: String? = null
        private set
    private var requestedUrl: String? = null
    private var currentPageTitle = ""

    private var swipeRefreshLayoutUtils: SwipeRefreshLayoutUtils? = null

    private var bgColor: Int = 0

    private var defaultMessage: String? = null
    private var shareInstruction: String? = null
    private var shareTitle: String? = null
    private var shareMessage: String? = null
    private var headerMap: HashMap<String, String>? = null

    private var isShowBookmark = false
    private var isShowShare = false
    private var isSuccess = true
    private var webView: WebView? = null
    protected abstract val customInfoView: View?
    protected abstract val customInfoTextView: Int
    private var customProgressPanel: FrameLayout? = null
    private var customInfoPanel: FrameLayout? = null
    private var customProgressBar: ContentLoadingProgressBar? = null


    private var onScrollChangedListener: ViewTreeObserver.OnScrollChangedListener? = null

    private val FILE_REQUEST_CODE = 1044

    private var mUploadMessage: ValueCallback<Uri>? = null
    private var mFilePathCallback: ValueCallback<Array<Uri>>? = null


    val isWebPageFromUrl: Boolean
        get() = isWebPageFromNetwork || isWebPageFromAsset

    val isWebPageFromNetwork: Boolean
        get() = rootUrl!!.startsWith("https://") || rootUrl!!.startsWith("http://")

    val isWebPageFromAsset: Boolean
        get() = rootUrl!!.startsWith("file:///android_asset/")

    protected abstract fun getCustomProgressBar(): View?

    @JvmOverloads
    fun setArg(context: Context, position: Int, url: String, defaultMessage: String, bgColor: Int = -1) {
        val headerList = HttpClientUtils.getHeaderList(false, false, false, false)
        setArg(context, position, url, defaultMessage, -1, false, headerList)
    }

    fun setArg(context: Context, position: Int, url: String, defaultMessage: String, bgColor: Int, showBookmark: Boolean, headerList: ArrayList<String>) {
        val headerMap = HttpClientUtils.getHeaderMap(context, headerList)
        setArg(position, url, defaultMessage, -1, false, false, null, null, null, headerMap)
    }

    fun setArg(position: Int, url: String, defaultMessage: String, bgColor: Int, showBookmark: Boolean,
               showShare: Boolean, shareInstruction: String?, shareTitle: String?, shareMessage: String?, headerMap: HashMap<String, String>) {
        val b = Bundle()
        b.putInt(ARG_POSITION, position)
        b.putString(ARG_URL, url)
        b.putInt(ARG_BG_COLOR, bgColor)
        b.putString(ARG_DEFAULT_MESSAGE, defaultMessage)
        b.putBoolean(ARG_SHOW_BOOKMARK, showBookmark)
        b.putBoolean(ARG_SHOW_SHARE, showShare)
        b.putString(ARG_SHARE_INSTRUCTION, shareInstruction)
        b.putString(ARG_SHARE_TITLE, shareTitle)
        b.putString(ARG_SHARE_MESSAGE, shareMessage)
        b.putSerializable(ARG_HEADER_MAP, headerMap)
        this.arguments = b
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isShowBookmark = CommonUtils.getBooleanFragmentArgument(arguments, ARG_SHOW_BOOKMARK, false)
        isShowShare = CommonUtils.getBooleanFragmentArgument(arguments, ARG_SHOW_SHARE, false)
        headerMap = CommonUtils.getSerializableFragmentArgument(arguments, ARG_HEADER_MAP, null) as HashMap<String, String>
        setHasOptionsMenu(true)
    }


    fun openNewLink(link: String) {
        if (webView != null) {
            webView!!.loadUrl(link)
        } else {
            requestedUrl = link
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.general_webview, container, false)
        webContainer = rootView!!.findViewById<View>(R.id.general_webview_container_view) as FrameLayout
        webView = rootView!!.findViewById<View>(R.id.general_webview_main_view) as WebView
        infoView = rootView!!.findViewById<View>(R.id.general_webview_info_view) as TextView
        progressBar = rootView!!.findViewById<View>(R.id.general_webview_progress_bar) as ContentLoadingProgressBar
        customProgressPanel = rootView!!.findViewById<View>(R.id.general_webview_custom_progress_panel) as FrameLayout
        val customProgress = getCustomProgressBar()
        if (customProgress != null) {
            customProgressPanel!!.addView(customProgress)
            customProgressBar = ViewUtils.findViewByClassReference(customProgress, ContentLoadingProgressBar::class.java!!)
        }

        customInfoPanel = rootView!!.findViewById(R.id.general_webview_custom_info_panel)
        val customInfoView = customInfoView
        if (customInfoView != null) {
            customInfoPanel!!.addView(customInfoView)
            infoView = customInfoView.findViewById(customInfoTextView)
        }

        swipeRefreshLayoutUtils = SwipeRefreshLayoutUtils.init(rootView as SwipeRefreshLayout?) {
            reloadLastValidLink()
            swipeRefreshLayoutUtils!!.refreshDone()
        }
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }


    private fun showContent() {
        if (webContainer != null) {
            webContainer!!.visibility = View.VISIBLE
        }

        if (progressBar != null) {
            progressBar!!.progress = 0
            progressBar!!.visibility = View.GONE
        }

        if (customProgressPanel != null) {
            if (customProgressBar != null) {
                customProgressBar!!.progress = 0
            }
            customProgressPanel!!.visibility = View.GONE
        }

        if (infoView != null) {
            infoView!!.visibility = View.GONE
        }

        if (customInfoPanel != null) {
            customInfoPanel!!.visibility = View.GONE
        }
    }

    private fun showInfo(info: CharSequence?) {
        if (info == null)
            return

        if (webContainer != null) {
            webContainer!!.visibility = View.GONE
        }
        if (progressBar != null) {
            progressBar!!.progress = 0
            progressBar!!.visibility = View.GONE
        }

        if (customProgressPanel != null) {
            if (customProgressBar != null) {
                customProgressBar!!.progress = 0
            }
            customProgressPanel!!.visibility = View.GONE
        }

        if (infoView != null) {
            infoView!!.text = info
            infoView!!.visibility = View.VISIBLE
        }

        if (customInfoPanel != null) {
            customInfoPanel!!.visibility = View.VISIBLE
        }
    }

    private fun showProgressBar(progressValue: Int) {
        if (infoView != null) {
            infoView!!.visibility = View.GONE
        }

        if (customInfoPanel != null) {
            customInfoPanel!!.visibility = View.GONE
        }

        if (webContainer != null) {
            webContainer!!.visibility = View.VISIBLE
        }

        if (progressBar != null && getCustomProgressBar() == null) {
            progressBar!!.progress = progressValue
            progressBar!!.visibility = View.VISIBLE
        }

        if (customProgressPanel != null) {
            if (customProgressBar != null) {
                customProgressBar!!.progress = progressValue
                customProgressBar!!.visibility = View.VISIBLE
            }
            customProgressPanel!!.visibility = View.VISIBLE
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rootUrl = CommonUtils.getStringFragmentArgument(arguments, ARG_URL, "")
        defaultMessage = CommonUtils.getStringFragmentArgument(arguments, ARG_DEFAULT_MESSAGE, null)
        bgColor = CommonUtils.getIntFragmentArgument(arguments, ARG_BG_COLOR, Color.TRANSPARENT)

        shareInstruction = CommonUtils.getStringFragmentArgument(arguments, ARG_SHARE_INSTRUCTION, null)

        shareTitle = CommonUtils.getStringFragmentArgument(arguments, ARG_SHARE_TITLE, null)
        if (TextUtils.isEmpty(shareTitle)) {
            shareTitle = getString(R.string.zlcore_default_webview_share_title)
        }

        shareMessage = CommonUtils.getStringFragmentArgument(arguments, ARG_SHARE_MESSAGE, null)
        if (TextUtils.isEmpty(shareMessage)) {
            shareMessage = getString(R.string.zlcore_default_webview_share_message)
        }

        if (!TextUtils.isEmpty(requestedUrl)) {
            rootUrl = requestedUrl
            requestedUrl = null
        }

        currentUrl = rootUrl

        if (!TextUtils.isEmpty(rootUrl)) {
            setupWebview(webView)
            val cookieManager = CookieManager.getInstance()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                cookieManager.removeAllCookies { }
            } else {
                cookieManager.removeAllCookie()
            }
            if (isWebPageFromUrl) {
                //url content
                //rootUrl = CommonUtils.prettifyUrl(rootUrl);

                //headers.put("Accept-Encoding", "gzip");
                webView!!.loadUrl(rootUrl, headerMap)
            } else {
                //html content
                val encodedHtml = Base64.encodeToString(rootUrl!!.toByteArray(), Base64.NO_PADDING)
                webView!!.loadData(encodedHtml, "text/html", "base64")
            }
        } else {
            if (TextUtils.isEmpty(defaultMessage)) {
                showInfo(getResText(R.string.zlcore_info_not_available))
            } else {
                showInfo(defaultMessage)
            }
        }
    }

    open fun setupWebview(webView: WebView?) {
        webView!!.setBackgroundColor(bgColor)
        webView.webViewClient = SmartWebViewClient()
        webView.settings.javaScriptEnabled = true
        webView.settings.setGeolocationEnabled(true)
        webView.clearHistory()
        webView.clearCache(true)
        webView.settings.cacheMode = WebSettings.LOAD_NO_CACHE
        webView.webChromeClient = SmartWebChromeClient()

        if (isWebPageFromUrl) {
            webView.settings.loadWithOverviewMode = true
            webView.settings.useWideViewPort = true
        }

        //enable zoom
        /*
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        */


        //enable multiwindows
        webView.settings.javaScriptCanOpenWindowsAutomatically = true
        webView.settings.setSupportMultipleWindows(true)

        //enable file chooser
        webView.settings.allowFileAccess = true

        //enable downloading file
        webView.setDownloadListener { url, userAgent, contentDisposition, mimeType, contentLength ->
            CommonUtils.showDialog3Option(webView.context, getString(R.string.zlcore_generalwebview_download_confirmation_title),
                    getString(R.string.zlcore_generalwebview_download_confirmation_message),
                    getString(R.string.zlcore_generalwebview_download_dialog_download_option), {
                val request = DownloadManager.Request(Uri.parse(url))
                request.setMimeType(mimeType)
                //------------------------COOKIE!!------------------------
                val cookies = CookieManager.getInstance().getCookie(url)
                request.addRequestHeader("cookie", cookies)
                //------------------------COOKIE!!------------------------
                request.addRequestHeader("User-Agent", userAgent)
                request.setDescription(getString(R.string.zlcore_generalwebview_download_wording_description))
                request.setTitle(URLUtil.guessFileName(url, contentDisposition, mimeType))
                request.allowScanningByMediaScanner()
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(url, contentDisposition, mimeType))
                val dm = webView.context.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
                dm.enqueue(request)
                CommonUtils.showToast(webView.context, getString(R.string.zlcore_generalwebview_download_wording_toast_message))
            }, getString(R.string.zlcore_general_wording_cancel), { }, getString(R.string.zlcore_generalwebview_download_dialog_open_other_app_option), {
                //download file using web browser
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(url)
                startActivity(i)
            })
        }
    }


    private fun getResText(id: Int): CharSequence? {
        return if (isAdded && activity != null) {
            getText(id)
        } else null
    }

    override fun onStart() {
        super.onStart()
        (rootView as SwipeRefreshLayout).viewTreeObserver.addOnScrollChangedListener(onScrollChangedListener = ViewTreeObserver.OnScrollChangedListener {
            if (webView!!.scrollY == 0)
                (rootView as SwipeRefreshLayout).isEnabled = true
            else
                (rootView as SwipeRefreshLayout).isEnabled = false
        })
    }

    override fun onResume() {
        super.onResume()
        if (activity != null) {
            activity!!.invalidateOptionsMenu()
        }
    }

    fun navigateBack(): Boolean {
        //navigate to previous second valid link, search first valid link first
        var url: String? = null
        if (webView != null) {
            try {
                val history = webView!!.copyBackForwardList()
                var index = -1 //means back 1 history, about:blank is one history 0 1 2 3 4 5

                var urlExistCount = 0

                if (history.getItemAtIndex(history.currentIndex).url != "about:blank") {
                    urlExistCount = urlExistCount + 1
                }
                while (webView!!.canGoBackOrForward(index)) {
                    if (history.getItemAtIndex(history.currentIndex + index).url != "about:blank") {
                        urlExistCount = urlExistCount + 1
                        if (urlExistCount == 2) {
                            url = history.getItemAtIndex(history.currentIndex + index).url
                            webView!!.goBackOrForward(index)
                            break
                        }
                    }
                    index--
                }


                // no history found that is not empty
                if (url == null) {
                    if (activity != null) {
                        activity!!.finish()
                    }
                }
            } catch (e: Exception) {// nangkep get url pada null
                return false
            }

        }
        return true
    }


    fun reloadLastValidLink(): Boolean {
        var url: String? = null
        if (webView != null) {
            val history = webView!!.copyBackForwardList()

            if (history.getItemAtIndex(history.currentIndex).url != "about:blank") {
                webView!!.reload()
            } else {
                var index = -1

                while (webView!!.canGoBackOrForward(index)) {
                    if (history.getItemAtIndex(history.currentIndex + index).url != "about:blank") {
                        url = history.getItemAtIndex(history.currentIndex + index).url
                        webView!!.goBackOrForward(index)
                        break
                    }
                    index--
                }

                // no history found that is not empty
                if (url == null) {
                    if (activity != null) {
                        activity!!.finish()
                    }
                }
            }
        }

        return true
    }

    fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_DOWN) {
            when (keyCode) {
                KeyEvent.KEYCODE_BACK -> {
                    return navigateBack()
                }
            }

        }
        return false
    }


    fun runJavascript(jsScript: String) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            webView!!.evaluateJavascript(jsScript, null)
        } else {
            webView!!.loadUrl("javascript:$jsScript")
        }
    }


    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        (rootView as SwipeRefreshLayout).viewTreeObserver.removeOnScrollChangedListener(onScrollChangedListener)
        super.onStop()
    }

    override fun onDestroyView() {
        webView = null
        infoView = null
        progressBar = null
        rootView = null
        super.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
    }


    private fun clearHistoryIfQueryExist(view: WebView, url: String) {
        if (url.startsWith("http://") || url.startsWith("https://")) {
            val keys = Uri.parse(url).queryParameterNames
            if (keys != null && keys.size > 0) {
                if (keys.contains(QUERY_PARAM_NO_HISTORY)) {
                    //ignore history
                } else if (keys.contains(QUERY_PARAM_CLEAR_HISTORY)) {
                    //clear all history
                    view.clearHistory()
                }
            }
        }
    }

    private inner class SmartWebViewClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            val rootUrlHost = Uri.parse(rootUrl).host
            val requestUrlHost = Uri.parse(url).host
            if (!TextUtils.isEmpty(requestUrlHost) && requestUrlHost!!.endsWith(rootUrlHost!!)) {
                // This is my web site, so do not override; let my WebView load the page
                return false
            }

            // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            view.context.startActivity(intent)
            return true
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
            val rootUrlHost = Uri.parse(rootUrl).host
            val requestUrlHost = Uri.parse(request.url.toString()).host
            if (!TextUtils.isEmpty(requestUrlHost) && requestUrlHost!!.endsWith(rootUrlHost!!)) {
                // This is my web site, so do not override; let my WebView load the page
                return false
            }

            // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(request.url.toString()))
            view.context.startActivity(intent)
            return true
        }

        override fun onPageStarted(view: WebView, url: String, favicon: Bitmap) {
            isSuccess = true //reset value isSuccess
            if (progressBar != null) {
                progressBar!!.isIndeterminate = true
            }
            if (customProgressBar != null) {
                customProgressBar!!.isIndeterminate = true
            }
            showProgressBar(50)
            if (activity != null) {
                activity!!.invalidateOptionsMenu()
            }

            EventBus.getDefault().post(GeneralWebviewEvent(GeneralWebviewEvent.LOAD_PAGE_STARTED))
        }

        override fun onReceivedSslError(view: WebView, handler: SslErrorHandler, error: SslError) {
            HttpClientUtils.handleWebviewSSLError(activity, handler, error)
        }

        override fun onReceivedError(view: WebView, errorCode: Int, description: String, failingUrl: String) {
            if (isPermittedWhenNotFound(failingUrl)) {
                isSuccess = true
                return
            }
            showInfo(getResText(R.string.zlcore_something_wrong_for_webview))
            isSuccess = false
            view.loadUrl("about:blank")
        }

        override fun onReceivedError(view: WebView, request: WebResourceRequest, error: WebResourceError) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (isPermittedWhenNotFound(request.url.path!!)) {
                    isSuccess = true
                    return
                }
            }
            showInfo(getResText(R.string.zlcore_something_wrong_for_webview))
            isSuccess = false
            view.loadUrl("about:blank")
        }

        @RequiresApi(21)
        override fun shouldInterceptRequest(view: WebView, request: WebResourceRequest): WebResourceResponse? {
            return super.shouldInterceptRequest(view, request)
        }

        override fun shouldInterceptRequest(view: WebView, url: String): WebResourceResponse? {
            return super.shouldInterceptRequest(view, url)
        }


        override fun onReceivedHttpError(view: WebView, request: WebResourceRequest, errorResponse: WebResourceResponse) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (isPermittedWhenNotFound(request.url.path!!)) {
                    isSuccess = true
                    return
                }
            }
            showInfo(getResText(R.string.zlcore_something_wrong_for_webview))
            isSuccess = false
            view.loadUrl("about:blank")
        }

        override fun onLoadResource(view: WebView, url: String) {
            super.onLoadResource(view, url)
        }

        override fun onPageFinished(view: WebView, url: String) {
            EventBus.getDefault().post(GeneralWebviewEvent(GeneralWebviewEvent.LOAD_PAGE_FINISHED))
            if (isSuccess) {
                currentUrl = url
                clearHistoryIfQueryExist(view, url)
                showContent()
                EventBus.getDefault().post(GeneralWebviewEvent(GeneralWebviewEvent.LOAD_PAGE_SUCCESS))
            }
        }

        private fun isPermittedWhenNotFound(url: String): Boolean {
            return if (url.toLowerCase().endsWith("favicon.ico")
                    || url.toLowerCase().endsWith(".gif")
                    || url.toLowerCase().endsWith(".png")
                    || url.toLowerCase().endsWith(".jpg")
                    || url.toLowerCase().endsWith(".wwf2")
                    || url.toLowerCase().endsWith(".ttf")
                    || url.toLowerCase().endsWith(".jpeg")) {
                true
            } else false
        }
    }

    private inner class SmartWebChromeClient : WebChromeClient() {
        override fun onProgressChanged(view: WebView, newProgress: Int) {
            if (isSuccess) {
                currentUrl = view.url
                if (progressBar != null) {
                    progressBar!!.isIndeterminate = false
                }
                if (customProgressBar != null) {
                    customProgressBar!!.isIndeterminate = false
                }
                showProgressBar(newProgress)
                if (activity != null) {
                    activity!!.invalidateOptionsMenu()
                }
            }
        }

        override fun onGeolocationPermissionsShowPrompt(origin: String, callback: GeolocationPermissions.Callback) {
            callback.invoke(origin, true, true)
        }

        override fun onReceivedTitle(view: WebView, title: String) {
            if (isSuccess) {
                if (!title.contains("http") && !title.contains("data:text/html")) {
                    if (activity != null) {
                        (activity as BaseActivity).supportActionBar!!.setTitle(title)
                    }
                    currentPageTitle = title
                }
            }
        }

        override fun onReceivedIcon(view: WebView, icon: Bitmap) {
            super.onReceivedIcon(view, icon)
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        override fun onPermissionRequest(request: PermissionRequest) {
            request.grant(request.resources) //handle permission like camera
        }

        override fun onPermissionRequestCanceled(request: PermissionRequest) {
            super.onPermissionRequestCanceled(request)
        }


        override fun onCreateWindow(view: WebView, isDialog: Boolean, isUserGesture: Boolean, resultMsg: Message): Boolean {
            val childView = WebView(context)
            setupWebview(childView)
            childView.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
            webContainer!!.addView(childView)
            val transport = resultMsg.obj as WebView.WebViewTransport
            transport.webView = childView
            resultMsg.sendToTarget()
            return true
        }

        override fun onCloseWindow(window: WebView) {
            webContainer!!.removeView(window)
        }

        // For Android 5.0
        override fun onShowFileChooser(webView: WebView, filePathCallback: ValueCallback<Array<Uri>>, fileChooserParams: WebChromeClient.FileChooserParams): Boolean {
            if (mFilePathCallback != null) {
                mFilePathCallback!!.onReceiveValue(null)
            }
            mFilePathCallback = filePathCallback
            CommonUtils.showFilePickerOpenDocument(this@GeneralWebViewFragment, "*/*", FILE_REQUEST_CODE)
            return true
        }

        // openFileChooser for Android 3.0+
        @JvmOverloads
        fun openFileChooser(uploadMsg: ValueCallback<Uri>, acceptType: String = "") {
            if (mUploadMessage != null) {
                mUploadMessage!!.onReceiveValue(null)
            }
            mUploadMessage = uploadMsg
            CommonUtils.showFilePickerOpenDocument(this@GeneralWebViewFragment, "*/*", FILE_REQUEST_CODE)
        }

        //openFileChooser for other Android versions
        fun openFileChooser(uploadMsg: ValueCallback<Uri>,
                            acceptType: String,
                            capture: String) {
            openFileChooser(uploadMsg, acceptType)
        }
    }// openFileChooser for Android < 3.0

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val fileUri = CommonUtils.handleFilePickerData(this@GeneralWebViewFragment, FILE_REQUEST_CODE, requestCode, resultCode, data)
        if (fileUri != null) {
            if (mFilePathCallback != null) {
                mFilePathCallback!!.onReceiveValue(arrayOf(fileUri))
                mFilePathCallback = null
            }

            if (mUploadMessage != null) {
                mUploadMessage!!.onReceiveValue(fileUri)
                mUploadMessage = null
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if (isWebPageFromUrl) {
            inflater.inflate(R.menu.menu_general_webview, menu)
            if (isWebPageFromNetwork) {
                if (isShowBookmark) {
                    menu.findItem(R.id.action_page_bookmark).isVisible = true
                } else {
                    menu.findItem(R.id.action_page_bookmark).isVisible = false
                }

                if (isShowShare) {
                    menu.findItem(R.id.action_page_share).isVisible = true
                } else {
                    menu.findItem(R.id.action_page_share).isVisible = false
                }
            }

            if (isWebPageFromAsset) {
                menu.findItem(R.id.action_page_bookmark).isVisible = false
                menu.findItem(R.id.action_page_share).isVisible = false
                menu.findItem(R.id.action_page_browser).isVisible = false
            }
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        if (isWebPageFromUrl) {
            if (isWebPageFromNetwork) {
                if (isShowBookmark && !TextUtils.isEmpty(currentUrl)) {
                    if (BookmarkModel.findBookmark(currentUrl) == null) {
                        menu.findItem(R.id.action_page_bookmark).title = getString(R.string.zlcore_menu_item_bookmark)
                        menu.findItem(R.id.action_page_bookmark).setIcon(R.drawable.baseline_bookmark_border_24)
                    } else {
                        menu.findItem(R.id.action_page_bookmark).title = getString(R.string.zlcore_menu_item_unbookmark)
                        menu.findItem(R.id.action_page_bookmark).setIcon(R.drawable.baseline_bookmark_24)
                    }
                }

                if (isShowShare) {
                    if (TextUtils.isEmpty(currentUrl)) {
                        menu.findItem(R.id.action_page_share).isEnabled = false
                    } else {
                        menu.findItem(R.id.action_page_share).isEnabled = true
                    }
                }
            }

            if (isWebPageFromAsset) {
                menu.findItem(R.id.action_page_bookmark).isVisible = false
                menu.findItem(R.id.action_page_share).isVisible = false
                menu.findItem(R.id.action_page_browser).isVisible = false
            }
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.action_reload) {
            reloadLastValidLink()
            return true
        } else if (id == R.id.action_page_browser) {
            CommonUtils.openBrowser(webView!!.context, currentUrl)
        } else if (id == R.id.action_page_close) {
            if (activity != null) {
                activity!!.finish()
            }
        } else if (id == R.id.action_page_bookmark) {
            if (item.title.toString().equals(getString(R.string.zlcore_menu_item_bookmark), ignoreCase = true)) {
                if (!TextUtils.isEmpty(currentUrl)) {
                    BookmarkModel.bookmark(currentPageTitle, "", currentUrl)
                }
                item.title = getString(R.string.zlcore_menu_item_unbookmark)
                item.setIcon(R.drawable.baseline_bookmark_24)
            } else if (item.title.toString().equals(getString(R.string.zlcore_menu_item_unbookmark), ignoreCase = true)) {
                if (!TextUtils.isEmpty(currentUrl)) {
                    BookmarkModel.unBookmark(currentPageTitle, "", currentUrl)
                }
                item.title = getString(R.string.zlcore_menu_item_bookmark)
                item.setIcon(R.drawable.baseline_bookmark_border_24)
            }
        } else if (id == R.id.action_page_share) {
            if (!TextUtils.isEmpty(currentUrl)) {
                CommonUtils.shareContent(webView!!.context, shareInstruction, shareTitle, shareMessage + "\n\n" + currentUrl)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    companion object {
        var FRAGMENT_TAG = "general_webview_fragment_tag"

        private val ARG_POSITION = "position"
        private val ARG_URL = "url"
        private val ARG_BG_COLOR = "bg_color"
        private val ARG_SHOW_BOOKMARK = "show_bookmark"
        private val ARG_DEFAULT_MESSAGE = "default_message"
        private val ARG_SHOW_SHARE = "show_share"
        private val ARG_SHARE_INSTRUCTION = "share_instruction"
        private val ARG_SHARE_TITLE = "shareTitleRes"
        private val ARG_SHARE_MESSAGE = "share_message"
        private val ARG_HEADER_MAP = "header_map"

        val QUERY_PARAM_NO_HISTORY = "nohistory"
        val QUERY_PARAM_CLEAR_HISTORY = "clearhistory"
    }
}
