package com.zaitunlabs.zlcore.fragments;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.provider.Settings;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.TextUtils;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.CookieManager;
import android.webkit.GeolocationPermissions;
import android.webkit.PermissionRequest;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebBackForwardList;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.zaitunlabs.zlcore.R;
import com.zaitunlabs.zlcore.events.GeneralWebviewEvent;
import com.zaitunlabs.zlcore.models.BookmarkModel;
import com.zaitunlabs.zlcore.core.BaseActivity;
import com.zaitunlabs.zlcore.core.BaseFragment;
import com.zaitunlabs.zlcore.utils.CommonUtils;
import com.zaitunlabs.zlcore.utils.HttpClientUtils;
import com.zaitunlabs.zlcore.utils.PrefsData;
import com.zaitunlabs.zlcore.utils.SwipeRefreshLayoutUtils;
import com.zaitunlabs.zlcore.utils.ViewUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by ahmad s on 3/17/2016.
 */
public abstract class GeneralWebViewFragment extends BaseFragment {
    public static String FRAGMENT_TAG = "general_webview_fragment_tag";

    private static final String ARG_POSITION = "position";
    private static final String ARG_URL = "url";
    private static final String ARG_BG_COLOR = "bg_color";
    private static final String ARG_SHOW_BOOKMARK = "show_bookmark";
    private static final String ARG_DEFAULT_MESSAGE = "default_message";
    private static final String ARG_SHOW_SHARE = "show_share";
    private static final String ARG_SHARE_INSTRUCTION = "share_instruction";
    private static final String ARG_SHARE_TITLE = "shareTitleRes";
    private static final String ARG_SHARE_MESSAGE = "share_message";

    public static final String QUERY_PARAM_NO_HISTORY = "nohistory";
    public static final String QUERY_PARAM_CLEAR_HISTORY = "clearhistory";

    private View rootView;
    private FrameLayout webContainer;
    private TextView infoView;
    private ContentLoadingProgressBar progressBar;
    private String rootUrl;
    private String currentUrl;
    private String requestedUrl;
    private String currentPageTitle = "";

    private SwipeRefreshLayoutUtils swipeRefreshLayoutUtils;

    private int bgColor;

    private String defaultMessage = null;
    private String shareInstruction;
    private String shareTitle;
    private String shareMessage;

    private boolean isShowBookmark = false;
    private boolean isShowShare = false;
    private boolean isSuccess = true;
    private WebView webView;
    protected abstract View getCustomProgressBar();
    protected abstract View getCustomInfoView();
    protected abstract int getCustomInfoTextView();
    private FrameLayout customProgressPanel;
    private FrameLayout customInfoPanel;
    private ContentLoadingProgressBar customProgressBar;



    private ViewTreeObserver.OnScrollChangedListener onScrollChangedListener;

    public void setArg(int position, String url, String defaultMessage){
        setArg(position,url,defaultMessage,-1);
    }

    public void setArg(int position, String url, String defaultMessage, int bgColor){
        setArg(position,url,defaultMessage,-1, false);
    }

    public void setArg(int position, String url, String defaultMessage, int bgColor, boolean showBookmark){
        setArg(position,url,defaultMessage,-1, false, false, null, null, null);
    }

    public void setArg(int position, String url, String defaultMessage, int bgColor, boolean showBookmark,
                       boolean showShare, String shareInstruction, String shareTitle, String shareMessage){
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        b.putString(ARG_URL,url);
        b.putInt(ARG_BG_COLOR,bgColor);
        b.putString(ARG_DEFAULT_MESSAGE,defaultMessage);
        b.putBoolean(ARG_SHOW_BOOKMARK,showBookmark);
        b.putBoolean(ARG_SHOW_SHARE,showShare);
        b.putString(ARG_SHARE_INSTRUCTION,shareInstruction);
        b.putString(ARG_SHARE_TITLE,shareTitle);
        b.putString(ARG_SHARE_MESSAGE,shareMessage);
        this.setArguments(b);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isShowBookmark = CommonUtils.getBooleanFragmentArgument(getArguments(),ARG_SHOW_BOOKMARK, false);
        isShowShare = CommonUtils.getBooleanFragmentArgument(getArguments(),ARG_SHOW_SHARE, false);
        setHasOptionsMenu(true);
    }


    public void openNewLink(String link){
        if(webView != null) {
            webView.loadUrl(link);
        } else {
            requestedUrl =  link;
        }
    }

    public String getCurrentUrl(){
        return currentUrl;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.general_webview,container,false);
        webContainer = (FrameLayout) rootView.findViewById(R.id.general_webview_container_view);
        webView = (WebView)rootView.findViewById(R.id.general_webview_main_view);
        infoView = (TextView)rootView.findViewById(R.id.general_webview_info_view);
        progressBar = (ContentLoadingProgressBar) rootView.findViewById(R.id.general_webview_progress_bar);
        customProgressPanel = (FrameLayout) rootView.findViewById(R.id.general_webview_custom_progress_panel);
        View customProgress = getCustomProgressBar();
        if(customProgress != null){
            customProgressPanel.addView(customProgress);
            customProgressBar = ViewUtils.findViewByClassReference(customProgress,ContentLoadingProgressBar.class);
        }

        customInfoPanel = rootView.findViewById(R.id.general_webview_custom_info_panel);
        View customInfoView = getCustomInfoView();
        if(customInfoView != null){
            customInfoPanel.addView(customInfoView);
            infoView = customInfoView.findViewById(getCustomInfoTextView());
        }

        swipeRefreshLayoutUtils = SwipeRefreshLayoutUtils.init((SwipeRefreshLayout) rootView, new Runnable() {
            @Override
            public void run() {
                reloadLastValidLink();
                swipeRefreshLayoutUtils.refreshDone();
            }
        });
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    private void showContent(){
        if(webContainer != null) {
            webContainer.setVisibility(View.VISIBLE);
        }

        if(progressBar != null) {
            progressBar.setProgress(0);
            progressBar.setVisibility(View.GONE);
        }

        if(customProgressPanel != null){
            if(customProgressBar != null){
                customProgressBar.setProgress(0);
            }
            customProgressPanel.setVisibility(View.GONE);
        }

        if(infoView != null) {
            infoView.setVisibility(View.GONE);
        }

        if(customInfoPanel != null){
            customInfoPanel.setVisibility(View.GONE);
        }
    }

    private void showInfo(CharSequence info){
        if(info == null)
            return;

        if(webContainer != null) {
            webContainer.setVisibility(View.GONE);
        }
        if(progressBar != null) {
            progressBar.setProgress(0);
            progressBar.setVisibility(View.GONE);
        }

        if(customProgressPanel != null){
            if(customProgressBar != null){
                customProgressBar.setProgress(0);
            }
            customProgressPanel.setVisibility(View.GONE);
        }

        if(infoView != null) {
            infoView.setText(info);
            infoView.setVisibility(View.VISIBLE);
        }

        if(customInfoPanel != null){
            customInfoPanel.setVisibility(View.VISIBLE);
        }
    }

    private void showProgressBar(int progressValue){
        if(infoView != null) {
            infoView.setVisibility(View.GONE);
        }

        if(customInfoPanel != null){
            customInfoPanel.setVisibility(View.GONE);
        }

        if(webContainer != null) {
            webContainer.setVisibility(View.VISIBLE);
        }

        if(progressBar != null && getCustomProgressBar() == null) {
            progressBar.setProgress(progressValue);
            progressBar.setVisibility(View.VISIBLE);
        }

        if(customProgressPanel != null){
            if(customProgressBar != null){
                customProgressBar.setProgress(progressValue);
                customProgressBar.setVisibility(View.VISIBLE);
            }
            customProgressPanel.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rootUrl = CommonUtils.getStringFragmentArgument(getArguments(),ARG_URL,"");
        defaultMessage = CommonUtils.getStringFragmentArgument(getArguments(),ARG_DEFAULT_MESSAGE,null);
        bgColor = CommonUtils.getIntFragmentArgument(getArguments(),ARG_BG_COLOR, Color.TRANSPARENT);

        shareInstruction = CommonUtils.getStringFragmentArgument(getArguments(),ARG_SHARE_INSTRUCTION, null);

        shareTitle = CommonUtils.getStringFragmentArgument(getArguments(),ARG_SHARE_TITLE, null);
        if(TextUtils.isEmpty(shareTitle)){
            shareTitle = getString(R.string.zlcore_default_webview_share_title);
        }

        shareMessage = CommonUtils.getStringFragmentArgument(getArguments(),ARG_SHARE_MESSAGE, null);
        if(TextUtils.isEmpty(shareMessage)){
            shareMessage = getString(R.string.zlcore_default_webview_share_message);
        }

        if(!TextUtils.isEmpty(requestedUrl)){
            rootUrl = requestedUrl;
            requestedUrl = null;
        }

        currentUrl = rootUrl;

        if(!TextUtils.isEmpty(rootUrl)) {
            setupWebview(webView);
            CookieManager cookieManager = CookieManager.getInstance();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                cookieManager.removeAllCookies(new ValueCallback<Boolean>() {
                    @Override
                    public void onReceiveValue(Boolean aBoolean) {

                    }
                });
            }else{
                cookieManager.removeAllCookie();
            }
            if(isWebPageFromUrl()) {
                //url content
                //rootUrl = CommonUtils.prettifyUrl(rootUrl);

                HashMap<String, String> headers = new HashMap<>();

                if(TextUtils.isEmpty(HttpClientUtils.webview_user_agent)){
                    //webview_user_agent = new WebView(context).getSettings().getUserAgentString();
                }
                if(TextUtils.isEmpty(HttpClientUtils.androidId)){
                    HttpClientUtils.androidId = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
                }

                String osVersion = "";
                try {
                    osVersion = CommonUtils.urlEncode(Build.VERSION.RELEASE);
                } catch (UnsupportedEncodingException e) {
                    ////e.printStackTrace();
                }

                String userAgent = "";
                try {
                    userAgent = CommonUtils.urlEncode(System.getProperty("http.agent"));
                } catch (UnsupportedEncodingException e) {
                    ////e.printStackTrace();
                }

                headers.put("Authorization", HttpClientUtils.getAuthAPIKey());

                headers.put("x-screensize", CommonUtils.getDisplayMetricsDensityDPIInString(getContext()));
                headers.put("x-model", CommonUtils.getModelNumber());
                headers.put("x-meid", CommonUtils.getMeid(view.getContext()));
                headers.put("x-packagename", getContext().getPackageName());
                headers.put("x-versionname", CommonUtils.getVersionName(getContext()));
                headers.put("x-versioncode", ""+CommonUtils.getVersionCode(getContext())+"");
                headers.put("x-lang", CommonUtils.getCurrentDeviceLanguage(getContext()));
                headers.put("x-platform", "android");
                headers.put("x-os", osVersion);
                headers.put("x-token", PrefsData.getToken());
                headers.put("x-deviceid", HttpClientUtils.androidId);
                headers.put("x-useragent", userAgent);
                headers.put("User-Agent", userAgent);
                //headers.put("Accept-Encoding", "gzip");
                webView.loadUrl(rootUrl, headers);
            } else {
                //html content
                String encodedHtml = Base64.encodeToString(rootUrl.getBytes(), Base64.NO_PADDING);
                webView.loadData(encodedHtml,"text/html","base64");
            }
        }else{
            if(TextUtils.isEmpty(defaultMessage)) {
                showInfo(getResText(R.string.zlcore_info_not_available));
            }else{
                showInfo(defaultMessage);
            }
        }
    }

    public void setupWebview(WebView webView){
        webView.setBackgroundColor(bgColor);
        webView.setWebViewClient(new SmartWebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setGeolocationEnabled(true);
        webView.clearHistory();
        webView.clearCache(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.setWebChromeClient(new SmartWebChromeClient());

        if(isWebPageFromUrl()) {
            webView.getSettings().setLoadWithOverviewMode(true);
            webView.getSettings().setUseWideViewPort(true);
        }

        //enable multiwindows
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setSupportMultipleWindows(true);

        //enable file chooser
        webView.getSettings().setAllowFileAccess(true);
    }



    private CharSequence getResText(int id){
        if(isAdded() && getActivity() != null){
            return getText(id);
        }
        return null;
    }

    @Override
    public void onStart() {
        super.onStart();
        ((SwipeRefreshLayout)rootView).getViewTreeObserver().addOnScrollChangedListener(onScrollChangedListener = new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                if (webView.getScrollY() == 0)
                    ((SwipeRefreshLayout)rootView).setEnabled(true);
                else
                    ((SwipeRefreshLayout)rootView).setEnabled(false);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if(getActivity() != null){
            getActivity().invalidateOptionsMenu();
        }
    }

    public boolean navigateBack(){
        //navigate to previous second valid link, search first valid link first
        String url = null;
        if (webView != null) {
            try {
                WebBackForwardList history = webView.copyBackForwardList();
                int index = -1; //means back 1 history, about:blank is one history 0 1 2 3 4 5

                int urlExistCount = 0;

                if (!history.getItemAtIndex(history.getCurrentIndex()).getUrl().equals("about:blank")) {
                    urlExistCount = urlExistCount + 1;
                }
                while (webView.canGoBackOrForward(index)) {
                    if (!history.getItemAtIndex(history.getCurrentIndex() + index).getUrl().equals("about:blank")) {
                        urlExistCount = urlExistCount + 1;
                        if (urlExistCount == 2) {
                            url = history.getItemAtIndex(history.getCurrentIndex() + index).getUrl();
                            webView.goBackOrForward(index);
                            break;
                        }
                    }
                    index--;
                }


                // no history found that is not empty
                if (url == null) {
                    if (getActivity() != null) {
                        getActivity().finish();
                    }
                }
            } catch (Exception e){// nangkep get url pada null
                return false;
            }
        }
        return true;
    }


    public boolean reloadLastValidLink(){
        String url = null;
        if (webView != null) {
            WebBackForwardList history = webView.copyBackForwardList();

            if (!history.getItemAtIndex(history.getCurrentIndex()).getUrl().equals("about:blank")) {
                webView.reload();
            } else {
                int index = -1;

                while (webView.canGoBackOrForward(index)) {
                    if (!history.getItemAtIndex(history.getCurrentIndex() + index).getUrl().equals("about:blank")) {
                        url = history.getItemAtIndex(history.getCurrentIndex() + index).getUrl();
                        webView.goBackOrForward(index);
                        break;
                    }
                    index--;
                }

                // no history found that is not empty
                if (url == null) {
                    if (getActivity() != null) {
                        getActivity().finish();
                    }
                }
            }
        }

        return true;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(event.getAction() == KeyEvent.ACTION_DOWN){
            switch(keyCode) {
                case KeyEvent.KEYCODE_BACK: {
                    return navigateBack();
                }
            }

        }
        return false;
    }


    public void runJavascript(String jsScript){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            webView.evaluateJavascript(jsScript, null);
        } else {
            webView.loadUrl("javascript:"+jsScript);
        }
    }


    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        ((SwipeRefreshLayout)rootView).getViewTreeObserver().removeOnScrollChangedListener(onScrollChangedListener);
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        webView = null;
        infoView = null;
        progressBar = null;
        rootView = null;
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    private void clearHistoryIfQueryExist(WebView view, String url){
        if(url.startsWith("http://") || url.startsWith("https://")) {
            Set<String> keys = Uri.parse(url).getQueryParameterNames();
            if (keys != null && keys.size() > 0) {
                if (keys.contains(QUERY_PARAM_NO_HISTORY)) {
                    //ignore history
                } else if (keys.contains(QUERY_PARAM_CLEAR_HISTORY)) {
                    //clear all history
                    view.clearHistory();
                }
            }
        }
    }

    private class SmartWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            String rootUrlHost = Uri.parse(rootUrl).getHost();
            if (Uri.parse(url).getHost().endsWith(rootUrlHost)) {
                // This is my web site, so do not override; let my WebView load the page
                return false;
            }

            // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            view.getContext().startActivity(intent);
            return true;
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            String rootUrlHost = Uri.parse(rootUrl).getHost();
            if (Uri.parse(request.getUrl().toString()).getHost().endsWith(rootUrlHost)) {
                // This is my web site, so do not override; let my WebView load the page
                return false;
            }

            // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(request.getUrl().toString()));
            view.getContext().startActivity(intent);
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            isSuccess = true; //reset value isSuccess
            if(progressBar != null) {
                progressBar.setIndeterminate(true);
            }
            if(customProgressBar != null){
                customProgressBar.setIndeterminate(true);
            }
            showProgressBar(50);
            if(getActivity() != null) {
                getActivity().invalidateOptionsMenu();
            }

            EventBus.getDefault().post(new GeneralWebviewEvent(GeneralWebviewEvent.LOAD_PAGE_STARTED));
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            HttpClientUtils.handleWebviewSSLError(getActivity(),handler,error);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {;
            if(isPermittedWhenNotFound(failingUrl)){
                isSuccess = true;
                return;
            }
            showInfo(getResText(R.string.zlcore_something_wrong_for_webview));
            isSuccess = false;
            view.loadUrl("about:blank");
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if(isPermittedWhenNotFound(request.getUrl().getPath())){
                    isSuccess = true;
                    return;
                }
            }
            showInfo(getResText(R.string.zlcore_something_wrong_for_webview));
            isSuccess = false;
            view.loadUrl("about:blank");
        }

        @RequiresApi(21)
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
            return super.shouldInterceptRequest(view, request);
        }

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
            return super.shouldInterceptRequest(view, url);
        }


        @Override
        public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if(isPermittedWhenNotFound(request.getUrl().getPath())){
                    isSuccess = true;
                    return;
                }
            }
            showInfo(getResText(R.string.zlcore_something_wrong_for_webview));
            isSuccess = false;
            view.loadUrl("about:blank");
        }

        @Override
        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            EventBus.getDefault().post(new GeneralWebviewEvent(GeneralWebviewEvent.LOAD_PAGE_FINISHED));
            if(isSuccess) {
                currentUrl = url;
                clearHistoryIfQueryExist(view,url);
                showContent();
                EventBus.getDefault().post(new GeneralWebviewEvent(GeneralWebviewEvent.LOAD_PAGE_SUCCESS));
            }
        }

        private boolean isPermittedWhenNotFound(String url){
            if(url.toLowerCase().endsWith("favicon.ico")
                    || url.toLowerCase().endsWith(".gif")
                    || url.toLowerCase().endsWith(".png")
                    || url.toLowerCase().endsWith(".jpg")
                    || url.toLowerCase().endsWith(".wwf2")
                    || url.toLowerCase().endsWith(".ttf")
                    || url.toLowerCase().endsWith(".jpeg")){
                return true;
            }
            return false;
        }
    }

    private final int FILE_REQUEST_CODE = 1044;

    private ValueCallback<Uri> mUploadMessage;
    private ValueCallback<Uri[]> mFilePathCallback;

    private class SmartWebChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if(isSuccess) {
                currentUrl = view.getUrl();
                if (progressBar != null) {
                    progressBar.setIndeterminate(false);
                }
                if (customProgressBar != null) {
                    customProgressBar.setIndeterminate(false);
                }
                showProgressBar(newProgress);
                if(getActivity() != null) {
                    getActivity().invalidateOptionsMenu();
                }
            }
        }

        @Override
        public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
            callback.invoke(origin,true, true);
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            if(isSuccess) {
                if (!title.contains("http") && !title.contains("data:text/html")) {
                    if (getActivity() != null) {
                        ((BaseActivity) getActivity()).getSupportActionBar().setTitle(title);
                    }
                    currentPageTitle = title;
                }
            }
        }

        @Override
        public void onReceivedIcon(WebView view, Bitmap icon) {
            super.onReceivedIcon(view, icon);
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onPermissionRequest(PermissionRequest request) {
            request.grant(request.getResources()); //handle permission like camera
        }

        @Override
        public void onPermissionRequestCanceled(PermissionRequest request) {
            super.onPermissionRequestCanceled(request);
        }


        @Override
        public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
            WebView childView = new WebView(getContext());
            setupWebview(childView);
            childView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT));
            webContainer.addView(childView);
            WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
            transport.setWebView(childView);
            resultMsg.sendToTarget();
            return true;
        }

        @Override
        public void onCloseWindow(WebView window) {
            webContainer.removeView(window);
        }

        // For Android 5.0
        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
            if (mFilePathCallback != null) {
                mFilePathCallback.onReceiveValue(null);
            }
            mFilePathCallback = filePathCallback;
            CommonUtils.showFilePickerOpenDocument(GeneralWebViewFragment.this,"*/*", FILE_REQUEST_CODE);
            return true;
        }

        // openFileChooser for Android 3.0+
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
            if (mUploadMessage != null) {
                mUploadMessage.onReceiveValue(null);
            }
            mUploadMessage = uploadMsg;
            CommonUtils.showFilePickerOpenDocument(GeneralWebViewFragment.this,"*/*", FILE_REQUEST_CODE);
        }

        // openFileChooser for Android < 3.0
        public void openFileChooser(ValueCallback<Uri> uploadMsg) {
            openFileChooser(uploadMsg, "");
        }
        //openFileChooser for other Android versions
        public void openFileChooser(ValueCallback<Uri> uploadMsg,
                                    String acceptType,
                                    String capture) {
            openFileChooser(uploadMsg, acceptType);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Uri fileUri = CommonUtils.handleFilePicker(GeneralWebViewFragment.this,FILE_REQUEST_CODE,requestCode,resultCode, data);
        if(fileUri != null) {
            if (mFilePathCallback != null) {
                mFilePathCallback.onReceiveValue(new Uri[]{fileUri});
                mFilePathCallback = null;
            }

            if (mUploadMessage != null) {
                mUploadMessage.onReceiveValue(fileUri);
                mUploadMessage = null;
            }
        }
    }


    public boolean isWebPageFromUrl(){
        return (isWebPageFromNetwork() || isWebPageFromAsset());
    }

    public boolean isWebPageFromNetwork(){
        return (rootUrl.startsWith("https://")
                || rootUrl.startsWith("http://"));
    }

    public boolean isWebPageFromAsset(){
        return rootUrl.startsWith("file:///android_asset/");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if(isWebPageFromUrl()) {
            inflater.inflate(R.menu.menu_general_webview, menu);
            if(isWebPageFromNetwork()) {
                if (isShowBookmark) {
                    menu.findItem(R.id.action_page_bookmark).setVisible(true);
                } else {
                    menu.findItem(R.id.action_page_bookmark).setVisible(false);
                }

                if (isShowShare) {
                    menu.findItem(R.id.action_page_share).setVisible(true);
                } else {
                    menu.findItem(R.id.action_page_share).setVisible(false);
                }
            }

            if(isWebPageFromAsset()){
                menu.findItem(R.id.action_page_bookmark).setVisible(false);
                menu.findItem(R.id.action_page_share).setVisible(false);
                menu.findItem(R.id.action_page_browser).setVisible(false);
            }
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if(isWebPageFromUrl()) {
            if(isWebPageFromNetwork()) {
                if (isShowBookmark && !TextUtils.isEmpty(currentUrl)) {
                    if (BookmarkModel.findBookmark(currentUrl) == null) {
                        menu.findItem(R.id.action_page_bookmark).setTitle(getString(R.string.zlcore_menu_item_bookmark));
                        menu.findItem(R.id.action_page_bookmark).setIcon(R.drawable.baseline_bookmark_border_24);
                    } else {
                        menu.findItem(R.id.action_page_bookmark).setTitle(getString(R.string.zlcore_menu_item_unbookmark));
                        menu.findItem(R.id.action_page_bookmark).setIcon(R.drawable.baseline_bookmark_24);
                    }
                }

                if (isShowShare) {
                    if (TextUtils.isEmpty(currentUrl)) {
                        menu.findItem(R.id.action_page_share).setEnabled(false);
                    } else {
                        menu.findItem(R.id.action_page_share).setEnabled(true);
                    }
                }
            }

            if(isWebPageFromAsset()){
                menu.findItem(R.id.action_page_bookmark).setVisible(false);
                menu.findItem(R.id.action_page_share).setVisible(false);
                menu.findItem(R.id.action_page_browser).setVisible(false);
            }
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_reload) {
            reloadLastValidLink();
            return true;
        } else if (id == R.id.action_page_browser) {
            CommonUtils.openBrowser(webView.getContext(),currentUrl);
        } else if(id == R.id.action_page_close){
            if (getActivity() != null) {
                getActivity().finish();
            }
        } else if(id == R.id.action_page_bookmark){
            if(item.getTitle().toString().equalsIgnoreCase(getString(R.string.zlcore_menu_item_bookmark))){
                if(!TextUtils.isEmpty(currentUrl)) {
                    BookmarkModel.bookmark(currentPageTitle, "", currentUrl);
                }
                item.setTitle(getString(R.string.zlcore_menu_item_unbookmark));
                item.setIcon(R.drawable.baseline_bookmark_24);
            } else if(item.getTitle().toString().equalsIgnoreCase(getString(R.string.zlcore_menu_item_unbookmark))){
                if(!TextUtils.isEmpty(currentUrl)) {
                    BookmarkModel.unBookmark(currentPageTitle, "", currentUrl);
                }
                item.setTitle(getString(R.string.zlcore_menu_item_bookmark));
                item.setIcon(R.drawable.baseline_bookmark_border_24);
            }
        } else if(id == R.id.action_page_share){
            if(!TextUtils.isEmpty(currentUrl)) {
                CommonUtils.shareContent(webView.getContext(), shareInstruction, shareTitle, shareMessage +"\n\n"+ currentUrl);
            }
        }

        return super.onOptionsItemSelected(item);
    }
}
