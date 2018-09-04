package com.zaitunlabs.zlcore.views;

import android.content.Context;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class JustifiedTextView extends WebView{
	public static final int LAYER_TYPE_SOFTWARE  = 1;
	public JustifiedTextView(Context context) {
		super(context);
		setBackgroundColor(0x00000000);
		//if (Build.VERSION.SDK_INT >= 11) setLayerType(LAYER_TYPE_SOFTWARE, null);//WebView.LAYER_TYPE_SOFTWARE = 1

		setWebViewClient(new WebViewClient()
		{
		    @Override
		    public void onPageFinished(WebView view, String url)
		    {
		        view.setBackgroundColor(0x00000000);
		        //if (Build.VERSION.SDK_INT >= 11) setLayerType(LAYER_TYPE_SOFTWARE, null);
		    }
		});
	}
	
	public void setText(String text){
		loadData(String.format("<html><body style=\"text-align:justify ;color:#80BFFF\">%s</body></Html>", text), "text/html", "utf-8");
	}

}
