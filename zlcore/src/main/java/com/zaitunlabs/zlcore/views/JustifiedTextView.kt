package com.zaitunlabs.zlcore.views

import android.content.Context
import android.webkit.WebView
import android.webkit.WebViewClient

class JustifiedTextView(context: Context) : WebView(context) {
    init {
        setBackgroundColor(0x00000000)
        //if (Build.VERSION.SDK_INT >= 11) setLayerType(LAYER_TYPE_SOFTWARE, null);//WebView.LAYER_TYPE_SOFTWARE = 1

        webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                view.setBackgroundColor(0x00000000)
                //if (Build.VERSION.SDK_INT >= 11) setLayerType(LAYER_TYPE_SOFTWARE, null);
            }
        }
    }

    fun setText(text: String) {
        loadData(String.format("<html><body style=\"text-align:justify ;color:#80BFFF\">%s</body></Html>", text), "text/html", "utf-8")
    }

    companion object {
        val LAYER_TYPE_SOFTWARE = 1
    }

}
